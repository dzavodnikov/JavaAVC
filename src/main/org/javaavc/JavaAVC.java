/*
 * Copyright (c) 2013-2015 JavaAVC Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * This class is part of Java Audio/Video Codec (JavaAVC) Library.
 */
package org.javaavc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.javaavc.ffmpeg.avcodec.Libavcodec;
import org.javaavc.ffmpeg.avdevice.Libavdevice;
import org.javaavc.ffmpeg.avfilter.Libavfilter;
import org.javaavc.ffmpeg.avformat.Libavformat;
import org.javaavc.ffmpeg.avutil.Libavutil;
import org.javaavc.ffmpeg.swresample.Libswresample;
import org.javaavc.ffmpeg.swscale.Libswscale;
import org.javaavc.platform.Platform;

import com.sun.jna.Native;

/**
 * The main class of wrapper.
 *
 * <p>
 * <h6>Links:</h6>
 * <ol>
 * <li><a href="http://ffmpeg.org/">FFmpeg</a>.</li>
 * <li><a href="http://www.ffmpeg.org/faq.html">FFmpeg FAQ</a>.</li>
 * <li><a href="http://www.ffmpeg.org/general.html#Supported-File-Formats_002c-Codecs-or-Features">Supported File
 * Formats, Codecs or Features</a> -- FFmpeg Documentation.</li>
 * <li><a href="http://ffmpeg.gusari.org/">FFmpeg Support Forum</a>.</li>
 * <li><a href="http://ffmpeg.zeranoe.com/">Zeranoe FFmpeg</a>.</li>
 * <li><a href="http://trac.ffmpeg.org/wiki">Official FFmpeg Wiki</a>.</li>
 * <li><a href="http://www.ffmpeg.org/doxygen/2.0/index.html">FFmpeg 2.0 API Doxygen</a>.</li>
 * <li><a href="http://www.ffmpeg.org/doxygen/2.0/examples.html">FFmpeg 2.0 Examples</a>.</li>
 * <li><a href="http://dranger.com/ffmpeg/">An FFmpeg and SDL Tutorial</a> by Stephen Dranger, explains how to write a
 * video player based on FFmpeg (<a href="https://github.com/chelyaev/ffmpeg-tutorial">updated source code</a>).</li>
 * <li><a href="http://ffmpeg.tv/">FFmpeg Basics</a> by Frantisek Korbel, describes various FFmpeg features and common
 * tasks.</li>
 * <li><a href="http://wiki.multimedia.cx/index.php?title=FFmpeg_codec_HOWTO">FFmpeg Codec HOWTO</a> -- MultimediaWiki.
 * </li>
 * <li><a href="http://wiki.multimedia.cx/index.php?title=FFmpeg_filter_HOWTO">FFmpeg Filter HOWTO</a> --
 * MultimediaWiki.</li>
 * <li><a href="http://wiki.multimedia.cx/index.php?title=FFmpeg_technical">FFmpeg Technical</a> -- MultimediaWiki.</li>
 * </ol>
 * </p>
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class JavaAVC {

    public static final String        LIB_NAME    = "JavaAVC";
    public static final String        LIB_VER     = "2.0.0";

    public static final String        NATIVE_NAME = "javaavc";
    public static final String        NATIVE_VER  = "2.2.8";

    protected static volatile JavaAVC INSTANCE    = null;

    public final Libavutil            avutil;
    public final Libavcodec           avcodec;
    public final Libavformat          avformat;
    public final Libswresample        swresample;
    public final Libswscale           swscale;
    public final Libavfilter          avfilter;
    public final Libavdevice          avdevice;

    protected final Platform          platform;

    protected final File              nativeDir;

    /**
     * ffmpeg is a very fast video and audio converter that can also grab from a live audio/video source. It can also
     * convert between arbitrary sample rates and resize video on the fly with a high quality polyphase filter.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://ffmpeg.org/ffmpeg.html">ffmpeg -- FFmpeg Documentation</a>.</li>
     * </ol>
     * </p>
     */
    public static final String        BIN_FFMPEG  = "ffmpeg";

    /**
     * ffserver is a streaming server for both audio and video. It supports several live feeds, streaming from files and
     * time shifting on live feeds.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://ffmpeg.org/ffserver.html">ffserver -- FFmpeg Documentation</a>.</li>
     * </ol>
     * </p>
     */
    public static final String        BIN_SERVER  = "ffserver";

    /**
     * ffprobe gathers information from multimedia streams and prints it in human- and machine-readable fashion.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://ffmpeg.org/ffprobe.html">ffprobe -- FFmpeg Documentation</a>.</li>
     * </ol>
     * </p>
     */
    public static final String        BIN_PROBE   = "ffprobe";

    /**
     * Unpack native library and load wrapper.
     */
    public static JavaAVC getInstance() throws IOException {
        JavaAVC localInstance = INSTANCE;

        if (localInstance == null) {
            synchronized (JavaAVC.class) {
                localInstance = INSTANCE;
                if (localInstance == null) {
                    INSTANCE = localInstance = new JavaAVC();
                }
            }
        }

        return localInstance;
    }

    protected String findLib(final String name) throws IOException {
        final List<File> files = this.platform.findSharedLibs(name);
        return files.get(files.size() - 1).getPath();
    }

    protected void checkLib(final Object library, final String name) throws IOException {
        if (library == null) {
            throw new IOException("Library '" + name + "' was not loaded!");
        }
    }

    /**
     * Unpack native libraries and load wrappers.
     */
    protected JavaAVC() throws IOException {
        // Unpack library.
        this.platform = Platform.getPlatform();

        this.nativeDir = platform.unpackNativeLibrary(NATIVE_NAME, NATIVE_VER);

        // Load "avutil". Require: nothing.
        final String avutilName = "avutil";
        this.avutil = (Libavutil) Native.loadLibrary(findLib(avutilName), Libavutil.class);
        checkLib(this.avutil, avutilName);

        // Load "avcodec". Require: "avutil".
        final String avcodecName = "avcodec";
        this.avcodec = (Libavcodec) Native.loadLibrary(findLib(avcodecName), Libavcodec.class);
        checkLib(this.avcodec, avcodecName);

        // Load "avformat". Require: "avcodec".
        final String avformatName = "avformat";
        this.avformat = (Libavformat) Native.loadLibrary(findLib(avformatName), Libavformat.class);
        checkLib(this.avformat, avformatName);

        // Load "swresample". Require: "avutil".
        final String swresampleName = "swresample";
        this.swresample = (Libswresample) Native.loadLibrary(findLib(swresampleName), Libswresample.class);
        checkLib(this.swresample, swresampleName);

        // Load "swscale". Require: "avutil".
        final String swscaleName = "swscale";
        this.swscale = (Libswscale) Native.loadLibrary(findLib(swscaleName), Libswscale.class);
        checkLib(this.swscale, swscaleName);

        // Load "avfilter". Require: "swresample", "swscale", "avformat", "avcodec", "avutil".
        final String avfilterName = "avfilter";
        this.avfilter = (Libavfilter) Native.loadLibrary(findLib(avfilterName), Libavfilter.class);
        checkLib(this.avfilter, avfilterName);

        // Load "avdevice". Require: "avfilter", "avformat".
        final String avdeviceName = "avdevice";
        this.avdevice = (Libavdevice) Native.loadLibrary(findLib(avdeviceName), Libavdevice.class);
        checkLib(this.avdevice, avdeviceName);
    }

    /**
     * Execute command line of FFmpeg utils. Use <code>BIN_*</code> constants.
     *
     * @param binName
     *            Name of binary file.
     * @param command
     *            Command to execute.
     */
    public void commandLineExecute(final String binName, final String command, final boolean outputError) {
        /*
         * See:
         * * http://www.rgagnon.com/javadetails/java-0014.html
         */
        try {
            String line;

            // Run FFmpeg.
            final File binFile = new File(this.nativeDir.getCanonicalPath() + File.separatorChar + binName);
            final Process run = this.platform.getNativeProcess(binFile, command);

            // Print standard output.
            final BufferedReader input = new BufferedReader(new InputStreamReader(run.getInputStream()));
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            input.close();

            // Print error output.
            if (outputError) {
                final BufferedReader error = new BufferedReader(new InputStreamReader(run.getErrorStream()));
                while ((line = error.readLine()) != null) {
                    System.err.println(line);
                }
                error.close();
            }

            run.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Same as {@link #commandLineExecute(String, String, boolean)}, but use <code>true</code> as output error values.
     */
    public void commandLineExecute(final String binName, final String command) {
        commandLineExecute(binName, command, true);
    }
}

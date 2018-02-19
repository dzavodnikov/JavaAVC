/*
 * Copyright 2012-2018 JavaAVC Team
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;

/**
 * Test class for {@link JavaAVC}.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class JavaAvcTest {

    /**
     * Test method for {@link JavaAVC}.
     */
    @Test
    public void testInfo() throws IOException {
        final JavaAVC avc = JavaAVC.getInstance();
        assertNotNull(avc);

        // AVUtil.
        assertNotNull(avc.avutil);
        assertNotNull(avc.avutil.avutil_version());
        assertNotNull(avc.avutil.avutil_license());
        assertNotNull(avc.avutil.avutil_configuration());

        // AVCodec.
        assertNotNull(avc.avcodec);
        assertNotNull(avc.avcodec.avcodec_version());
        assertNotNull(avc.avcodec.avcodec_license());
        assertNotNull(avc.avcodec.avcodec_configuration());

        // AVFormat.
        assertNotNull(avc.avformat);
        assertNotNull(avc.avformat.avformat_version());
        assertNotNull(avc.avformat.avformat_license());
        assertNotNull(avc.avformat.avformat_configuration());

        // SWResample.
        assertNotNull(avc.swresample);
        assertNotNull(avc.swresample.swresample_version());
        assertNotNull(avc.swresample.swresample_license());
        assertNotNull(avc.swresample.swresample_configuration());

        // SWScale.
        assertNotNull(avc.swscale);
        assertNotNull(avc.swscale.swscale_version());
        assertNotNull(avc.swscale.swscale_license());
        assertNotNull(avc.swscale.swscale_configuration());

        // AVFilter.
        assertNotNull(avc.avfilter);
        assertNotNull(avc.avfilter.avfilter_version());
        assertNotNull(avc.avfilter.avfilter_license());
        assertNotNull(avc.avfilter.avfilter_configuration());

        // AVDevice.
        assertNotNull(avc.avdevice);
        assertNotNull(avc.avdevice.avdevice_version());
        assertNotNull(avc.avdevice.avdevice_license());
        assertNotNull(avc.avdevice.avdevice_configuration());
    }

    @Test
    public void testCmdVersion() {
        try {
            final JavaAVC avc = JavaAVC.getInstance();
            assertNotNull(avc);

            final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
            final ByteArrayOutputStream baosErr = new ByteArrayOutputStream();

            System.setOut(new PrintStream(baosOut));
            System.setErr(new PrintStream(baosErr));

            avc.commandLineExecute(JavaAVC.BIN_FFMPEG, "-version");

            assertTrue(baosOut.toString().length() > "ffmpeg version".length());
            assertTrue(baosErr.toString().length() == 0);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCmdFormatsAndCodecs() throws IOException {
        final JavaAVC avc = JavaAVC.getInstance();
        avc.commandLineExecute(JavaAVC.BIN_FFMPEG, "-formats");
        avc.commandLineExecute(JavaAVC.BIN_FFMPEG, "-codecs");
    }
}

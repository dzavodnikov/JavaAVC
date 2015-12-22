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
package org.javaavc.platform;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.sun.jna.Native;

/**
 * Base class for platform-specific code. Sub-classes should implement code specific for some OS.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public abstract class Platform {

    public static final String SEPARATOR        = "-";

    public static final String OS_NAME          = "os.name";

    public static final String JAVA_TEMP_DIR    = "java.io.tmpdir";

    public static final String JNA_LIBRARY_PATH = "jna.library.path";

    private final String       id;

    private final Arch         arch;

    public final StdIOLibrary  STD_IO_LIB;

    private static void checkString(final String name, final String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(String.format("Value '%s' should not be empty or null!", name));
        }
    }

    protected Platform(final String id) {
        this.id = id;
        this.arch = Arch.getArch();

        this.STD_IO_LIB = (StdIOLibrary) Native.loadLibrary(getStdIOLibraryName(), StdIOLibrary.class);
    }

    /**
     * Return platform-specified directory for given library name in format <code>LibName-LibVer-OsName-Arch</code> (for
     * example, <code>MyLib-1.2.3-linux-x86_64</code>).
     */
    private String getLibraryNativeName(final String libName, final String libVer) {
        return libName + SEPARATOR + libVer + SEPARATOR + this.id + SEPARATOR + this.arch.getId();
    }

    /**
     * Run executable file in separate process. Supported Linux and Windows.
     */
    public abstract Process getNativeProcess(final File binFile, final String command) throws IOException;

    /**
     * Return extension of platform-specific file extension of shared library.
     */
    protected abstract String getSharedLibExtension();

    protected abstract String getStdIOLibraryName();

    public List<File> findSharedLibs(final String libName) throws IOException {
        /*
         * Check values.
         */
        checkString("library name", libName);

        /*
         * Find results.
         */
        final List<File> result = new ArrayList<File>();

        for (File dir : getJnaPathFiles()) {
            for (File f : dir.listFiles()) {
                final String fileName = f.getName();

                final int nameIdx = fileName.indexOf(libName);
                final int extIdx = fileName.indexOf(getSharedLibExtension());

                if (nameIdx >= 0 && extIdx >= 0 && nameIdx + fileName.length() > extIdx) {
                    result.add(f.getCanonicalFile());
                }
            }
        }

        Collections.sort(result, new Comparator<File>() {

            @Override
            public int compare(final File f1, final File f2) {
                return f1.getPath().compareTo(f2.getPath());
            }
        });

        return result;
    }

    /**
     * Extracts a JAR-file with some resource to defined directory.
     */
    public void unpackJarToDir(final URL jarUrl, final File outputDir) throws IOException {
        /*
         * Check values.
         */
        if (jarUrl == null) {
            throw new IllegalArgumentException("JAR-file URL can not be null!");
        }
        if (outputDir == null || !outputDir.exists() || outputDir.isFile()) {
            throw new IllegalArgumentException(String.format("Incorrect output directory '%s'!", outputDir));
        }

        /*
         * Unpacking.
         */
        final JarURLConnection conn = (JarURLConnection) jarUrl.openConnection();
        final JarInputStream jis = new JarInputStream(new FileInputStream(new File(conn.getJarFile().getName())));
        final String basePath = outputDir.getAbsolutePath() + File.separator;

        // Unpack files and directories from JAR-file.
        final byte[] buffer = new byte[4096];
        for (JarEntry entry = jis.getNextJarEntry(); entry != null; entry = jis.getNextJarEntry()) {
            final File fout = new File(basePath + entry.getName());

            if (!entry.isDirectory()) {
                final OutputStream os = new BufferedOutputStream(new FileOutputStream(fout));
                for (int read = 0; read != -1; read = jis.read(buffer)) {
                    os.write(buffer, 0, read);
                }
                os.close();
            } else {
                fout.mkdir();
            }
            jis.closeEntry();
        }
        jis.close();
    }

    /**
     * Unpack native library from JAR-file to temporary directory and return {@link File} to it.
     *
     * <p>
     * Will be found resource in format <code>LibName_OsName_Arch</code> (for example, <code>mylib_linux_64</code>),
     * copied into temporary directory and returned {@link File} of this copy (for example,
     * <code>/tmp/mylib_linux_64</code>).
     * </p>
     */
    public File unpackNativeLibrary(final String libName, final String libVer) throws IOException {
        /*
         * Checking values.
         */
        checkString("library name", libName);
        checkString("library version", libVer);

        /*
         * Unpacking.
         */
        final String nativeLibDirName = getLibraryNativeName(libName, libVer);
        final File tempDirFile = getJavaTempDirectoryFile();

        /*
         * Remember that:
         *  * If the name begins with a '/', then the absolute name of the resource is the portion of the name following the '/'.
         *  * Otherwise, the absolute name is of the following form: "package_name/name".
         */
        final String resName = "/" + nativeLibDirName;

        final URL url = Class.class.getResource(resName);
        if (url != null) {
            unpackJarToDir(url, tempDirFile);
        } else {
            throw new IOException(String.format("Can not find JAR-file with resource '%s'!", resName));
        }

        final File newPathFile = new File(tempDirFile.getCanonicalPath() + File.separatorChar + nativeLibDirName);
        addJnaPathFile(newPathFile);

        return newPathFile;
    }

    /**
     * Return property value. If value is not exists (<code>null</code>) return empty string.
     */
    private static String getSystemProperty(final String propertyName) {
        /*
         * Checking values.
         */
        checkString("property name", propertyName);

        /*
         * Check property value.
         */
        String value = System.getProperty(propertyName);
        if (value == null) {
            value = "";
        }

        return value;
    }

    public static Platform getPlatform() {
        /*
         * See:
         * * http://lopica.sourceforge.net/os.html
         */
        final String name = getSystemProperty(OS_NAME);

        Platform platform = null;

        if (name.matches(Linux.NAME_PATTERN)) {
            platform = new Linux();
        }

        if (name.matches(Windows.NAME_PATTERN)) {
            platform = new Windows();
        }

        if (platform == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unsupported OS: ");
            sb.append(name);
            sb.append("!");
            throw new RuntimeException(sb.toString());
        } else {
            return platform;
        }
    }

    /**
     * Return all system properties as a string.
     */
    public static String getSystemProperties() {
        final StringBuilder sb = new StringBuilder();
        final Properties props = System.getProperties();
        for (Object obj : props.keySet()) {
            final String key = (String) obj;
            sb.append(key);
            sb.append("=");
            sb.append(props.getProperty(key));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Return list of files and directories of some path from system property.
     */
    private static List<File> getPathFiles(final String propertyName) {
        final Map<String, File> result = new HashMap<String, File>();

        for (String s : getSystemProperty(propertyName).split(File.pathSeparator)) {
            final File f = new File(s);
            try {
                result.put(f.getCanonicalPath(), f.getCanonicalFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return new ArrayList<File>(result.values());
    }

    /**
     * Add new path to some path from system property.
     */
    private static void addPathFile(final String propertyName, final File newPath) {
        if (newPath == null) {
            return;
        }

        try {
            final StringBuilder sb = new StringBuilder();
            final List<File> pathFiles = getPathFiles(propertyName);
            pathFiles.add(newPath);
            for (File f : pathFiles) {
                sb.append(f.getCanonicalPath());
                sb.append(File.pathSeparator);
            }
            System.setProperty(propertyName, sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return list of directories for search JNA native code (based on "jna.library.path" system property).
     */
    public static List<File> getJnaPathFiles() {
        return getPathFiles(JNA_LIBRARY_PATH);
    }

    /**
     * Add new path to JNA native code search (based on "jna.library.path" system property).
     */
    public static void addJnaPathFile(final File newClassPath) {
        addPathFile(JNA_LIBRARY_PATH, newClassPath);
    }

    private static File getFileProperty(final String propertyName) {
        final File f = new File(getSystemProperty(propertyName));
        try {
            return f.getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return file object for system temporary directory.
     */
    public static File getJavaTempDirectoryFile() {
        return getFileProperty(JAVA_TEMP_DIR);
    }

    public enum Arch {
        x86("x86", ".{1,2}86"), x86_64("x86_64", "amd64");

        public static final String NAME_SYSTEM_PROPPERTY = "os.arch";

        private final String       id;

        private final String       namePattern;

        private String             name;

        private Arch(final String id, final String namePattern) {
            this.id = id;
            this.namePattern = namePattern;
        }

        public String getId() {
            return this.id;
        }

        public String getNamePattern() {
            return this.namePattern;
        }

        public String getName() {
            return this.name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        /**
         * JRE architecture.
         */
        public static Arch getArch() {
            /*
             * See:
             * * http://lopica.sourceforge.net/os.html
             */
            final String name = getSystemProperty(Arch.NAME_SYSTEM_PROPPERTY);

            Arch arch = null;

            if (name.matches(Arch.x86.getNamePattern())) {
                arch = Arch.x86;
            }

            if (name.matches(Arch.x86_64.getNamePattern())) {
                arch = Arch.x86_64;
            }

            if (arch == null) {
                throw new RuntimeException(String.format("Unsupported arch: $s!", name));
            }

            arch.setName(name);

            return arch;
        }
    }
}

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

import java.io.File;
import java.io.IOException;

/**
 * Contains code specific for Windows.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public final class Windows extends Platform {

    public static final String ID           = "windows";

    public static final String NAME_PATTERN = "Windows.*";

    protected Windows() {
        super(ID);
    }

    /**
     * Run executable file in separate process. Supported Linux and Windows.
     *
     * <p>
     * For windows -- DLLs search paths:
     * <ol>
     * <li>The current directory.</li>
     * <li><code>%WINDOWS%\System32</code>.</li>
     * <li><code>%WINDOWS%</code>.</li>
     * <li>The directories listed in the <code>PATH<code> environment variable.</li>
     * </ol>
     * <strong>Note: The LIBPATH environment variable is not used.</strong>
     * </p>
     */
    @Override
    public Process getNativeProcess(final File binFile, final String command) throws IOException {
        //@formatter:off
        return Runtime.getRuntime().exec(new String[]{ 
                    "cmd.exe", 
                    "/c", 
                    String.format("%s %s", binFile.getAbsolutePath(), command)
                }
            );
        //@formatter:on
    }

    @Override
    public String getSharedLibExtension() {
        return "dll";
    }

    @Override
    protected String getStdIOLibraryName() {
        return "msvcrt";
    }
}

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
package org.javaavc.platform;

import java.io.File;
import java.io.IOException;

/**
 * Contains code specific for Linux.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public final class Linux extends Platform {

    public static final String ID = "linux";

    public static final String NAME_PATTERN = "Linux";

    protected Linux() {
        super(ID);
    }

    /**
     * Run executable file in separate process. Supported Linux and Windows.
     *
     * <p> For Linux -- see shared library search paths: <ol> <li><a
     * href="http://tldp.org/HOWTO/Program-Library-HOWTO/shared-libraries.html">Shared Libraries -- tldp.org</a>. </li>
     * </ol> </p>
     */
    @Override
    public Process getNativeProcess(final File binFile, final String command) throws IOException {
        //@formatter:off
        return Runtime.getRuntime().exec(
                new String[]{ 
                    "/bin/sh", 
                    "-c",
                    String.format(
                            "chmod u+x %s; export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:%s; exec %s %s",
                            binFile.getAbsolutePath(), 
                            binFile.getParent(), 
                            binFile.getAbsolutePath(), 
                            command
                        )
                }
            );
        //@formatter:on
    }

    @Override
    public String getSharedLibExtension() {
        return "so";
    }

    @Override
    protected String getStdIOLibraryName() {
        return "c";
    }
}

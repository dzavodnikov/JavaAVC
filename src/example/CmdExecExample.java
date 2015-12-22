
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
import java.io.IOException;

import org.javaavc.JavaAVC;

/**
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public class CmdExecExample {

    public static void main(final String[] args) throws IOException {
        final JavaAVC avc = JavaAVC.getInstance();
        avc.commandLineExecute(JavaAVC.BIN_FFMPEG, "-version");
    }
}

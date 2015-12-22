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

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

/**
 * Some functions and structures, defined into <code>stdio.h</code>.
 *
 * <p>
 * <h6>Links:</h6>
 * <ol>
 * <li><a href="http://en.wikipedia.org/wiki/C_file_input/output">C file input/output -- Wikipedia</a>.</li>
 * </ol>
 * </p>
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
public interface StdIOLibrary extends Library {

    /**
     * Opens a file indicated by filename and returns a file stream associated with that file. <code>mode</code> is used
     * to determine the file access mode.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.cppreference.com/w/c/io/fopen">fopen -- cppreference.com</a>.</li>
     * </ol>
     * </p>
     *
     * <p>
     * Original signature: <code>FILE *fopen(const char *path, const char *mode)</code>.
     * </p>
     *
     * @param path
     *            File name to associate the file stream.
     * @param mode
     *            NULL-terminated character string determining file access mode.
     * @return Opened file stream on success, <code>NULL</code> on failure.
     */
    public FILE fopen(String path, String mode);

    /**
     * Reassigns an existing file stream stream to a different file identified by filename using specified
     * <code>mode</code>. <code>mode</code> is used to determine the new file access mode.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.cppreference.com/w/c/io/freopen">freopen -- cppreference.com</a>.</li>
     * </ol>
     * </p>
     *
     * <p>
     * Original signature: <code>FILE *freopen(const char *path, const char *mode, FILE *stream)</code>.
     * </p>
     *
     * @param path
     *            File name to associate the file stream.
     * @param mode
     *            NULL-terminated character string determining file access mode.
     * @param stream
     *            The file stream to modify.
     * @return Opened file stream on success, <code>NULL</code> on failure.
     */
    public FILE freopen(String path, String mode, FILE stream);

    /**
     * Causes the output file stream to be synchronized with the actual contents of the file. If the given stream is of
     * the input type, then the behavior of the function is undefined.
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.cppreference.com/w/c/io/fflush">fflush -- cppreference.com</a>.</li>
     * </ol>
     * </p>
     *
     * <p>
     * Original signature: <code>int *fflush(FILE *stream)</code>.
     * </p>
     *
     * @param stream
     *            The file stream to synchronize.
     * @return <code>0​</code> on success, EOF otherwise.
     */
    public FILE fflush(FILE stream);

    /**
     * Closes the given file stream. Any unwritten buffered data are flushed to the OS. Any unread buffered data are
     * discarded.
     *
     * <p>
     * Whether or not the operation succeeds, the stream is no longer associated with a file, and the buffer allocated
     * by <code>setbuf</code> or <code>setvbuf</code>, if any, is also disassociated and deallocated if automatic
     * allocation was used.
     * </p>
     *
     * <p>
     * <h6>Links:</h6>
     * <ol>
     * <li><a href="http://en.cppreference.com/w/c/io/fclose">fclose -- cppreference.com</a>.</li>
     * </ol>
     * </p>
     *
     * <p>
     * Original signature: <code>int *fclose(FILE *stream)</code>.
     * </p>
     *
     * @param stream
     *            The file stream to close.
     * @return <code>0​</code> on success, EOF otherwise.
     */
    public FILE fclose(FILE stream);

    public static class FILE extends PointerType {

        public FILE(Pointer address) {
            super(address);
        }

        public FILE() {
            super();
        }
    };
}

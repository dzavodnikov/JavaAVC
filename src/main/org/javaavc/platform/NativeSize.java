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

import com.sun.jna.IntegerType;
import com.sun.jna.Native;

/**
 * This class implement <code>size_t</code> C-type, that can be 32-bit or 64-bit integer.
 *
 * @author Dmitry Zavodnikov (d.zavodnikov@gmail.com)
 */
/*
 * Based on:
 *      https://github.com/dzavodnikov/JavaAVC/blob/master/src/com/ochafik/lang/jnaerator/runtime/NativeSize.java
 *      https://github.com/ochafik/nativelibs4java/blob/master/libraries/jnaerator/jnaerator-runtime/src/main/java/com/ochafik/lang/jnaerator/runtime/NativeSize.java
 */
public class NativeSize extends IntegerType {

    private static final long serialVersionUID = 1L;

    /**
     * Create a zero-valued signed IntegerType.
     */
    public NativeSize() {
        this(0, false);
    }

    /**
     * Create a zero-valued optionally unsigned IntegerType.
     */
    public NativeSize(boolean unsigned) {
        this(0, unsigned);
    }

    /**
     * Create a signed IntegerType with the given value.
     */
    public NativeSize(long value) {
        this(value, false);
    }

    /**
     * Create an optionally signed IntegerType with the given value.
     */
    public NativeSize(long value, boolean unsigned) {
        super(Native.SIZE_T_SIZE, value, unsigned);
    }
}

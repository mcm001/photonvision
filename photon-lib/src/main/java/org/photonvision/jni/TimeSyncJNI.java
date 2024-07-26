/*
 * MIT License
 *
 * Copyright (c) PhotonVision
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.photonvision.jni;

import java.util.concurrent.atomic.AtomicBoolean;

/** Demo class for loading the driver via JNI. */
public class TimeSyncJNI {
    static boolean libraryLoaded = false;

    /** Helper class for determining whether or not to load the driver on static initialization. */
    public static class Helper {
        private static AtomicBoolean extractOnStaticLoad = new AtomicBoolean(true);

        /**
         * Get whether to load the driver on static init.
         *
         * @return true if the driver will load on static init
         */
        public static boolean getExtractOnStaticLoad() {
            return extractOnStaticLoad.get();
        }

        /**
         * Set whether to load the driver on static init.
         *
         * @param load the new value
         */
        public static void setExtractOnStaticLoad(boolean load) {
            extractOnStaticLoad.set(load);
        }
    }

    static {
        if (Helper.getExtractOnStaticLoad()) {
            System.loadLibrary("photonlibJni");
            libraryLoaded = true;
        }
    }

    /** Force load the library. */
    public static synchronized void forceLoad() {
        if (libraryLoaded) {
            return;
        }
        System.loadLibrary("photonlibJni");
        libraryLoaded = true;
    }

    public static native int start();
    public static native int stop();
}

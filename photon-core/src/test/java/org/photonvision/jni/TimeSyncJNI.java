/*
 * Copyright (C) Photon Vision.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

    /**
     * Tells the driver to initialize. This is a demo of a native JNI method from the driver.
     *
     * @return the int returned by the driver
     * @see "VendorJNI.cpp"
     */
    public static native int start();
}

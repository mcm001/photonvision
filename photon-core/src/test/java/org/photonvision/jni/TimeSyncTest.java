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

import edu.wpi.first.hal.HAL;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.photonvision.common.util.TestUtils;

public class TimeSyncTest {
    @Test
    public void meme() throws IOException {
        TimeSyncJNI.Helper.setExtractOnStaticLoad(false);

        TestUtils.loadLibraries();
        System.load(
                "/home/matt/github/photonvision/photon-lib/build/libs/photonlibJni/shared/linuxx86-64/release/libphotonlibJni.so");

        HAL.initialize(500, 0);

        // TimeSyncJNI.forceLoad();

        System.err.println(TimeSyncJNI.start());
    }
}

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

import edu.wpi.first.hal.HAL;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class TimeSyncTest {
    @Test
    public void meme() throws IOException, InterruptedException {
        TimeSyncJNI.Helper.setExtractOnStaticLoad(false);

        // TestUtils.loadLibraries();
        System.load(
                "/home/matt/github/photonvision/photon-lib/build/libs/photonlibJni/shared/linuxx86-64/release/libphotonlibJni.so");

        HAL.initialize(500, 0);

        System.err.println(TimeSyncJNI.start());

        Thread.sleep(30000);
    }
}

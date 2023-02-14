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

package org.photonvision.vision.camera;

import edu.wpi.first.cscore.VideoMode;
import edu.wpi.first.cscore.VideoMode.PixelFormat;
import edu.wpi.first.math.MathUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.util.MathUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.photonvision.common.configuration.CameraConfiguration;
import org.photonvision.common.dataflow.DataChangeDestination;
import org.photonvision.common.dataflow.DataChangeService;
import org.photonvision.common.dataflow.DataChangeSource;
import org.photonvision.common.dataflow.DataChangeSubscriber;
import org.photonvision.common.dataflow.events.DataChangeEvent;
import org.photonvision.vision.frame.Frame;
import org.photonvision.vision.frame.FrameProvider;
import org.photonvision.vision.opencv.CVMat;
import org.photonvision.vision.processes.VisionSource;
import org.photonvision.vision.processes.VisionSourceSettables;
import org.photonvision.vision.frame.provider.CpuImageProcessor;

public class WebsocketVisionSource extends VisionSource {
    private final WebsocketVisionSourceSettables settables;
    private final WebsocketFrameProvider frameProvider;

    private final CVMat mostRecentFrame = new CVMat(Mat.zeros(new Size(640, 480), CvType.CV_8UC3));
    private int m_lastRxSequence = -1;
    private boolean hasNewFrame;
    private final Object frameLock = new Object();

    private WebsocketFrameSubscriber subscriber;

    public WebsocketVisionSource(CameraConfiguration cameraConfiguration) {
        super(cameraConfiguration);

        this.settables = new WebsocketVisionSourceSettables(cameraConfiguration);
        this.frameProvider = new WebsocketFrameProvider();

        this.subscriber = new WebsocketFrameSubscriber();
        DataChangeService.getInstance().addSubscriber(subscriber);
    }

    class WebsocketFrameSubscriber extends DataChangeSubscriber {

        public WebsocketFrameSubscriber() {
            super(
                    Collections.singletonList(DataChangeSource.DCS_WEBSOCKET),
                    Collections.singletonList(DataChangeDestination.DCD_OTHER));
        }

        @Override
        public void onDataChangeEvent(DataChangeEvent<?> event) {
            if (event.propertyName.equals("simCameraFrame")) {

                if (event.data instanceof Pair<?, ?>) {
                    var payload = (Pair<CVMat, Integer>) event.data;
                    // System.out.println("Yoinked camera frame with mat " + payload.getLeft().getMat().nativeObj + " " + payload.getLeft().toString());

                    synchronized (frameLock) {
                        var size = new Size(640, 480);
                        Imgproc.resize(payload.getLeft().getMat(), mostRecentFrame.getMat(), size);

                        // Imgcodecs.imwrite("yoinked.jpg", payload.getLeft().getMat());
                        payload.getLeft().release();

                        m_lastRxSequence = payload.getRight();
                        hasNewFrame = true;

                        // System.out.println("Resized to " + mostRecentFrame.toString());
                    }

                    payload.getLeft().release();
                }
            }
        }

    }

    @Override
    public FrameProvider getFrameProvider() {
        return frameProvider;
    }

    @Override
    public VisionSourceSettables getSettables() {
        return settables;
    }

    @Override
    public boolean isVendorCamera() {
        return false;
    }

    private class WebsocketFrameProvider extends CpuImageProcessor {
        private long lastGetMillis = System.currentTimeMillis();
        @Override
        protected CapturedFrame getInputMat() {
            boolean hasNew;
            do {
                synchronized (frameLock) {
                    hasNew = hasNewFrame;
                }
            } while (!hasNew);

            // block to keep FPS at a defined rate
            long millisDelay = 1000 / 10;
            if (System.currentTimeMillis() - lastGetMillis < millisDelay) {
                try {
                    Thread.sleep(millisDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            synchronized (frameLock) {
                CapturedFrame outputFrame = new CapturedFrame(new CVMat(), settables.getFrameStaticProperties(), org.photonvision.common.util.math.MathUtils.wpiNanoTime());
                // if (!mostRecentFrame.getMat().empty()) Imgcodecs.imwrite("resized.jpg", mostRecentFrame.getMat());
                outputFrame.colorImage.copyTo(mostRecentFrame);
                // if (!outputFrame.image.getMat().empty()) Imgcodecs.imwrite("output.jpg", outputFrame.image.getMat());
                lastGetMillis = System.currentTimeMillis();
                return outputFrame;
            }
        }

        @Override
        public String getName() {
            return "WebsocketFrameProvider";
        }

    }

    private class WebsocketVisionSourceSettables extends VisionSourceSettables {
        private final VideoMode videoMode;

        WebsocketVisionSourceSettables(
                CameraConfiguration cameraConfiguration) {
            super(cameraConfiguration);
            videoModes = new HashMap<>();
            videoMode = new VideoMode(
                    PixelFormat.kMJPEG,
                    640, 480,
                    30);
            videoModes.put(0, videoMode);
        }

        @Override
        public void setExposure(double exposure) {
        }

        public void setAutoExposure(boolean cameraAutoExposure) {
        }

        @Override
        public void setBrightness(int brightness) {
        }

        @Override
        public void setGain(int gain) {
        }

        @Override
        public VideoMode getCurrentVideoMode() {
            return videoMode;
        }

        @Override
        protected void setVideoModeInternal(VideoMode videoMode) {
            // Do nothing
        }

        @Override
        public HashMap<Integer, VideoMode> getAllVideoModes() {
            return videoModes;
        }
    }
}

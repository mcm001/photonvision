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

package org.photonvision.vision.frame.consumer;

import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.photonvision.common.logging.LogGroup;
import org.photonvision.common.logging.Logger;
import org.photonvision.vision.opencv.CVMat;

public class GStreamerFrameConsumer {
    private static final Logger logger = new Logger(GStreamerFrameConsumer.class, LogGroup.Camera);

    public VideoWriter writer = null;
    int width = -1;
    int height = -1;
    int fps = 60;
    String uriname;

    public GStreamerFrameConsumer(int width, int height, int fps, int port, String uriname) {
        this.width = width;
        this.height = height;
        this.fps = fps;
        this.uriname = uriname;
        // try_create_writer();
    }

    private void try_create_writer() {
        if (writer != null) {
            writer.release();
        }

        try {
            writer =
                    new VideoWriter(
                            "appsrc ! videoconvert"
                                    + " ! video/x-raw,format=I420"
                                    + " ! x264enc speed-preset=ultrafast tune=zerolatency bitrate=1000 key-int-max="
                                    + Integer.toString(fps * 2)
                                    + " ! video/x-h264,profile=baseline"
                                    + " ! rtspclientsink location=rtsp://localhost:8554/"
                                    + uriname,
                            Videoio.CAP_GSTREAMER,
                            0,
                            fps,
                            new Size(width, height),
                            true);

            if (!writer.isOpened()) {
                logger.warn("Writer not open!");

                writer.release();
                writer = null;
            }
        } catch (Exception e) {
            logger.error("While creating gstreamer pipe", e);
            writer.release();
            writer = null;
        }
    }

    public void accept(CVMat image) {

        if (image != null && !image.getMat().empty() && image.getMat().channels() == 3) {

            if (image.getMat().rows() != height || image.getMat().cols() != width || writer == null) {
                // resolution change required
                this.height = image.getMat().rows();
                this.width = image.getMat().cols();

                try_create_writer();
            }

            if (writer == null) {
                logger.error("writer still null?");
                return;
            }

            writer.write(image.getMat());
        }
    }

    public void close() {
        writer.release();
    }
}

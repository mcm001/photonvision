package org.photonvision.vision.frame.consumer;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoMode.PixelFormat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.photonvision.common.util.TestUtils;
import org.photonvision.vision.opencv.CVMat;

class GStreamerFrameConsumerTest {
    @BeforeAll
    public static void loadLibraries() {
        TestUtils.loadLibraries();
    }

    @Test
    public void testRun() throws InterruptedException {
        var camera = new UsbCamera("source_0", "/dev/video2");

        System.out.println(Core.getBuildInformation());

        camera.setVideoMode(PixelFormat.kMJPEG, 640, 480, 30);

        var cvSink = CameraServer.getVideo(camera);
        var consumer = new GStreamerFrameConsumer(640, 480, 30, 8224, "test_input");
        var consumer2 = new GStreamerFrameConsumer(640, 480, 30, 8224, "test_output");

        var frame = new Mat();
        var cvmat = new CVMat(frame);
        for (int i = 0; i < 30000; i++) {

            cvSink.grabFrame(frame);

            if (!frame.empty()) {
                consumer.accept(cvmat);
                consumer2.accept(cvmat);
            }

            System.out.println("hi iter " + i);
        }

        consumer.close();
    }
}
;

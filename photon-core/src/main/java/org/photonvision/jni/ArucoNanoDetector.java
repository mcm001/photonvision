package org.photonvision.jni;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.photonvision.ArucoNanoV5Detector;
import org.photonvision.ArucoNanoV5Detector.DetectionResult;
import org.photonvision.common.util.TestUtils;
import org.photonvision.vision.aruco.ArucoDetectionResult;
import org.photonvision.vision.opencv.CVMat;

public class ArucoNanoDetector extends PhotonJniCommon {
    public static synchronized void forceLoad() throws IOException {
        forceLoad(ArucoNanoDetector.class, List.of("photonaruconanojni"));
    }

    public static List<ArucoDetectionResult> detect(CVMat in) {
        DetectionResult[] ret = ArucoNanoV5Detector.detect(in.getMat().getNativeObjAddr(), 0);

        return List.of(ret).stream()
                .map(it -> new ArucoDetectionResult(it.xCorners, it.yCorners, it.id))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException {
        TestUtils.loadLibraries();
        forceLoad();
    }
}

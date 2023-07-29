package org.photonvision.vision.pipeline;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.mrgingham.MrginghamJNI;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.ArucoDetector;
import org.opencv.aruco.Board;
import org.opencv.aruco.CharucoBoard;
import org.opencv.aruco.DetectorParameters;
import org.opencv.aruco.Dictionary;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.photonvision.common.logging.LogGroup;
import org.photonvision.common.logging.Logger;
import org.photonvision.vision.frame.FrameDivisor;
import org.photonvision.vision.pipe.CVPipe;
import org.photonvision.vision.pipeline.UICalibrationData;
import org.photonvision.vision.pipeline.UICalibrationData.BoardType;

import edu.wpi.first.util.RuntimeLoader;

public class CalibrateCharucoTest {

    @BeforeAll
    public static void setUp() {
        try {
            var loader = new RuntimeLoader<>(
                    Core.NATIVE_LIBRARY_NAME, RuntimeLoader.getDefaultExtractionRoot(), Core.class);
            loader.loadLibrary();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void drawDict() {
        int squaresX = 9;
        int squaresY = 6;
        float squareLength = 280.0f;
        float markerLength = 182f;

        Dictionary dictionary = Aruco.getPredefinedDictionary(0);
        CharucoBoard charucoBoard = CharucoBoard.create(squaresX, squaresY, squareLength, markerLength, dictionary);

        Mat mat = new Mat();
        charucoBoard.draw(new Size(1080, 1080 * 5.0 / 7.0), mat, 0, 1);

        Imgcodecs.imwrite("test.png", mat);
        HighGui.imshow(mat.toString(), mat);
        HighGui.waitKey();
        HighGui.destroyAllWindows();
    }

    @Test
    protected void captureImagesCharuco() {
        int squaresX = 9;
        int squaresY = 6;
        float squareLength = 280.0f;
        float markerLength = 182f;
        int calibrationFlags = 0;

        DetectorParameters detectParams = DetectorParameters.create();
        {
            detectParams.set_adaptiveThreshWinSizeMin(3);
            detectParams.set_adaptiveThreshWinSizeMax(23);
            detectParams.set_adaptiveThreshWinSizeStep(10);
            detectParams.set_adaptiveThreshConstant(7);
            detectParams.set_minMarkerPerimeterRate(0.03);
            detectParams.set_maxMarkerPerimeterRate(4.0);
            detectParams.set_polygonalApproxAccuracyRate(0.05);
            detectParams.set_minCornerDistanceRate(10);
            detectParams.set_minDistanceToBorder(3);
            detectParams.set_minMarkerDistanceRate(0.05);
            detectParams.set_cornerRefinementWinSize(5);
            detectParams.set_cornerRefinementMaxIterations(30);
            detectParams.set_cornerRefinementMinAccuracy(0.1);
            detectParams.set_markerBorderBits(1);
            detectParams.set_perspectiveRemovePixelPerCell(8);
            detectParams.set_perspectiveRemoveIgnoredMarginPerCell(0.13);
            detectParams.set_maxErroneousBitsInBorderRate(0.04);
            detectParams.set_minOtsuStdDev(5.0);
            detectParams.set_errorCorrectionRate(0.6);
        }

        Dictionary dictionary = Aruco.getPredefinedDictionary(0);
        CharucoBoard charucoBoard = CharucoBoard.create(squaresX, squaresY, squareLength, markerLength, dictionary);

        List<List<Mat>> charucoCorners = new ArrayList<>();
        List<Mat> charucoIds = new ArrayList<>();
        List<Mat> validImgs = new ArrayList<>();
        Size imgSize = new Size();
        int nFrame = 0;

        var detector = ArucoDetector.create(dictionary, detectParams);

        var imageDirectory = Path.of("D:\\Pictures\\Camera Roll\\calib").toFile();
        File[] listImages = imageDirectory.listFiles();
        for (File file : listImages) {
            String src = file.getAbsolutePath();
            Mat imgRead = Imgcodecs.imread(src, Imgcodecs.IMREAD_GRAYSCALE);
            imgSize = imgRead.size();
            Mat imgCopy = new Mat();
            Mat ids = new Mat();
            List<Mat> rejectedCorners = new ArrayList<>();
            List<Mat> corners = new ArrayList<>();
            if (!imgRead.empty()) {
                detector.detectMarkers(imgRead, corners, ids);
                detector.refineDetectedMarkers(imgRead, charucoBoard, corners, ids, rejectedCorners);
                Mat currentCharucoCorners = new Mat();
                Mat currentCharucoIds = new Mat();
                int idsSize = ids.rows() * ids.cols();
                if (idsSize > 0) {
                    Aruco.interpolateCornersCharuco(corners, ids, imgRead, charucoBoard, currentCharucoCorners,
                            currentCharucoIds);
                }
                imgRead.copyTo(imgCopy);
                if (idsSize < 0) {
                    Aruco.drawDetectedCornersCharuco(imgCopy, currentCharucoCorners);
                }
                if (currentCharucoCorners.total() > 0) {
                    Aruco.drawDetectedCornersCharuco(imgCopy, currentCharucoCorners, currentCharucoIds,
                            new Scalar(255, 0, 0));
                }

                HighGui.imshow(imgCopy.toString(), imgCopy);
                HighGui.waitKey();
                HighGui.destroyAllWindows();

                charucoCorners.add(corners);
                charucoIds.add(currentCharucoIds);
                validImgs.add(imgRead);
                nFrame++;
            }
        }

        Mat intrinsic = new Mat();
        Mat distCoeffs = new Mat();

        intrinsic.put(0, 0, 1);
        intrinsic.put(1, 1, 1);
        List<Mat> allCharucoCorners = new ArrayList<>();
        List<Mat> allCharucoIds = new ArrayList<>();
        for (int i = 0; i < nFrame; i++) {
            Mat currentCharucoCorners = new Mat();
            Mat currentCharucoIds = new Mat();
            Aruco.interpolateCornersCharuco(charucoCorners.get(i), charucoIds.get(i), validImgs.get(i), charucoBoard,
                    currentCharucoCorners, currentCharucoIds, intrinsic, distCoeffs, 4);
            allCharucoCorners.add(currentCharucoCorners);
            allCharucoIds.add(currentCharucoIds);
        }

        List<Mat> rvecs = new ArrayList<>();
        List<Mat> tvecs = new ArrayList<>();

        double repError = Aruco.calibrateCameraCharuco(allCharucoCorners, charucoIds, charucoBoard, imgSize, intrinsic,
                distCoeffs, rvecs, tvecs, calibrationFlags);
        System.out.println("reprojection error : " + repError);
    }
}

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

package org.photonvision.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.photonvision.jni.WpilibLoader;
import org.photonvision.vision.calibration.CameraCalibrationCoefficients;

public class TestUtils {
    public static boolean loadLibraries() {
        return WpilibLoader.loadLibraries();
    }

    public static Path getResourcesFolderPath() {
        System.out.println("CWD: " + Path.of("").toAbsolutePath());

        // VSCode likes to make this path relative to the wrong root directory, so a fun hack to tell
        // if it's wrong
        Path ret = Path.of("test-resources");

        if (Path.of("test-resources")
                .toAbsolutePath()
                .toString()
                .replace("/", "")
                .replace("\\", "")
                .toLowerCase()
                .matches(".*photon-[a-z]*test-resources")) {
            ret = Path.of("../test-resources");

            if (ret.toFile().exists()) {
                return ret;
            } else {
                throw new RuntimeException("Seems like we're running from gradle, but test-resources still doesnt exist");
            }
        }

        return ret;
    }

    /**
     * Load a Path, relative to the test-resources folder, or extract from our JAR if it isn't present.
     */
    public static Path loadResourceFromJar(Path path) {
        // First check if the given path exists already
        if (path.toFile().exists()) {
            return path;
        }

        try {
            var in = TestUtils.class.getClassLoader().getResource(path.toString());

            if (in == null) {
                System.err.println("Could not get resource at path " + path);
                return null;
            }

            // Resource folder is a literal folder - easy enoguh
            if (in.getProtocol().equals("file")) {
                return Path.of(in.toURI());
            }

            // Otherwise, extract from jar

            // get filename
            var fname = path.getFileName().toString();

            // Somewhere to shove the file
            File temp = Files.createTempFile(fname, null).toFile();

            FileUtils.copyInputStreamToFile(in.openStream(), temp);

            return temp.toPath();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    public enum WPI2019Image {
        kCargoAngledDark48in(1.2192),
        kCargoSideStraightDark36in(0.9144),
        kCargoSideStraightDark60in(1.524),
        kCargoSideStraightDark72in(1.8288),
        kCargoSideStraightPanelDark36in(0.9144),
        kCargoStraightDark19in(0.4826),
        kCargoStraightDark24in(0.6096),
        kCargoStraightDark48in(1.2192),
        kCargoStraightDark72in(1.8288),
        kCargoStraightDark72in_HighRes(1.8288),
        kCargoStraightDark90in(2.286),
        kRocketPanelAngleDark48in(1.2192),
        kRocketPanelAngleDark60in(1.524);

        public static double FOV = 68.5;

        public final double distanceMeters;
        public final Path path;

        Path getPath() {
            var filename = this.toString().substring(1);
            return Path.of("2019", "WPI", filename + ".jpg");
        }

        WPI2019Image(double distanceMeters) {
            this.distanceMeters = distanceMeters;
            this.path = getPath();
        }
    }

    @SuppressWarnings("unused")
    public enum WPI2020Image {
        kBlueGoal_060in_Center(1.524),
        kBlueGoal_084in_Center(2.1336),
        kBlueGoal_084in_Center_720p(2.1336),
        kBlueGoal_108in_Center(2.7432),
        kBlueGoal_132in_Center(3.3528),
        kBlueGoal_156in_Center(3.9624),
        kBlueGoal_180in_Center(4.572),
        kBlueGoal_156in_Left(3.9624),
        kBlueGoal_224in_Left(5.6896),
        kBlueGoal_228in_ProtectedZone(5.7912),
        kBlueGoal_330in_ProtectedZone(8.382),
        kBlueGoal_Far_ProtectedZone(10.668), // TODO: find a more accurate distance
        kRedLoading_016in_Down(0.4064),
        kRedLoading_030in_Down(0.762),
        kRedLoading_048in_Down(1.2192),
        kRedLoading_048in(1.2192),
        kRedLoading_060in(1.524),
        kRedLoading_084in(2.1336),
        kRedLoading_108in(2.7432);

        public static double FOV = 68.5;

        public final double distanceMeters;
        public final Path path;

        Path getPath() {
            var filename = this.toString().substring(1).replace('_', '-');
            return Path.of("2020", "WPI", filename + ".jpg");
        }

        WPI2020Image(double distanceMeters) {
            this.distanceMeters = distanceMeters;
            this.path = getPath();
        }
    }

    public enum WPI2024Images {
        kBackAmpZone_117in,
        kSpeakerCenter_143in;

        public static double FOV = 68.5;

        public final Path path;

        Path getPath() {
            var filename = this.toString().substring(1);
            return Path.of("2024", filename + ".jpg");
        }

        WPI2024Images() {
            this.path = getPath();
        }
    }

    public enum PolygonTestImages {
        kPolygons;

        public final Path path;

        Path getPath() {
            var filename = this.toString().substring(1).toLowerCase();
            return Path.of("polygons", filename + ".png");
        }

        PolygonTestImages() {
            this.path = getPath();
        }
    }

    public enum PowercellTestImages {
        kPowercell_test_1,
        kPowercell_test_2,
        kPowercell_test_3,
        kPowercell_test_4,
        kPowercell_test_5,
        kPowercell_test_6;

        public final Path path;

        Path getPath() {
            var filename = this.toString().substring(1).toLowerCase();
            return Path.of(filename + ".png");
        }

        PowercellTestImages() {
            this.path = getPath();
        }
    }

    public enum ApriltagTestImages {
        kRobots,
        kTag1_640_480,
        kTag1_16h5_1280,
        kTag_corner_1280;

        public final Path path;

        Path getPath() {
            // Strip leading k
            var filename = this.toString().substring(1).toLowerCase();
            var extension = ".jpg";
            if (filename.equals("tag1_16h5_1280")) extension = ".png";
            return Path.of("apriltag", filename + extension);
        }

        ApriltagTestImages() {
            this.path = getPath();
        }
    }


    public static Path getTestMode2019ImagePath() {
        return getResourcesFolderPath()
                .resolve("testimages")
                .resolve(WPI2019Image.kRocketPanelAngleDark60in.path);
    }

    public static Path getTestMode2020ImagePath() {
        return getResourcesFolderPath()
                .resolve("testimages")
                .resolve(WPI2020Image.kBlueGoal_156in_Left.path);
    }

    public static Path getTestModeApriltagPath() {
        return getResourcesFolderPath()
                .resolve("testimages")
                .resolve(ApriltagTestImages.kRobots.path);
    }

    public static Path getTestImagesPath() {
        return Path.of("testimages");
    }

    public static Path getCalibrationPath() {
        return getResourcesFolderPath().resolve("calibration");
    }

    public static Path getPowercellPath() {
        return getTestImagesPath().resolve("polygons").resolve("powercells");
    }

    public static Path getWPIImagePath(WPI2024Images image) {
        return loadResourceFromJar(getTestImagesPath().resolve(image.path));
    }

    public static Path getWPIImagePath(WPI2020Image image) {
        return getTestImagesPath().resolve(image.path);
    }

    public static Path getWPIImagePath(WPI2019Image image) {
        return getTestImagesPath().resolve(image.path);
    }

    public static Path getPolygonImagePath(PolygonTestImages image) {
        return getTestImagesPath().resolve(image.path);
    }

    public static Path getApriltagImagePath(ApriltagTestImages image) {
        return getTestImagesPath().resolve(image.path);
    }

    public static Path getPowercellImagePath(PowercellTestImages image) {
        return getPowercellPath().resolve(image.path);
    }

    public static Path getSquaresBoardImagesPath() {
        return getResourcesFolderPath().resolve("calibrationSquaresImg");
    }

    public static Path getCharucoBoardImagesPath() {
        return getResourcesFolderPath().resolve("calibrationCharucoImg");
    }

    public static File getHardwareConfigJson() {
        return getResourcesFolderPath()
                .resolve("hardware")
                .resolve("HardwareConfig.json")
                .toFile();
    }

    private static final String LIFECAM_240P_CAL_FILE = "lifecam240p.json";
    private static final String LIFECAM_480P_CAL_FILE = "lifecam480p.json";
    public static final String LIFECAM_1280P_CAL_FILE = "lifecam_1280.json";
    public static final String LIMELIGHT_480P_CAL_FILE = "limelight_1280_720.json";

    public static CameraCalibrationCoefficients getCoeffs(String filename) {
        try {
            return new ObjectMapper()
                    .readValue(
                            (Path.of(getCalibrationPath().toString(), filename).toFile()),
                            CameraCalibrationCoefficients.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CameraCalibrationCoefficients get2019LifeCamCoeffs() {
        return getCoeffs(LIFECAM_240P_CAL_FILE);
    }

    public static CameraCalibrationCoefficients get2020LifeCamCoeffs() {
        return getCoeffs(LIFECAM_480P_CAL_FILE);
    }

    public static CameraCalibrationCoefficients get2023LifeCamCoeffs() {
        return getCoeffs(LIFECAM_1280P_CAL_FILE);
    }

    private static final int DefaultTimeoutMillis = 5000;

    public static void showImage(Mat frame, String title, int timeoutMs) {
        if (frame.empty()) return;
        try {
            HighGui.imshow(title, frame);
            HighGui.waitKey(timeoutMs);
            HighGui.destroyAllWindows();
        } catch (HeadlessException ignored) {
        }
    }

    public static void showImage(Mat frame, String title) {
        showImage(frame, title, DefaultTimeoutMillis);
    }

    public static Path getConfigDirectoriesPath() {
        return getResourcesFolderPath().resolve("old_configs");
    }
}

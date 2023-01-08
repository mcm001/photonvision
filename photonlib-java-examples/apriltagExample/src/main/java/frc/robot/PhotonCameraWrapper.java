/*
 * MIT License
 *
 * Copyright (c) 2022 PhotonVision
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

package frc.robot;

import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.Pair;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.FieldConstants;
import frc.robot.Constants.VisionConstants;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.photonvision.PhotonCamera;
import org.photonvision.RobotPoseEstimator;
import org.photonvision.RobotPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;

public class PhotonCameraWrapper {
    public PhotonCamera photonCamera;
    public RobotPoseEstimator robotPoseEstimator;
    public AprilTagFieldLayout atfl = null;

    private PhotonPipelineResult lastResult = new PhotonPipelineResult();

    public PhotonCameraWrapper() {

        // TODO until wpilib merges the new layout, load from resource
        try {
            if (Robot.isSimulation()) {
                atfl = new AprilTagFieldLayout(Path.of("src/main/deploy/2023-chargedup.json"));
            } else {
                atfl = new AprilTagFieldLayout(
                        Path.of(Filesystem.getDeployDirectory().getAbsoluteFile().toString(), "2023-chargedup.json"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Post tags to AdvantageScope viewer
        {
            var tags = atfl.getTags();
            var poses = new double[tags.size() * 7];
            var ids = new double[tags.size() * 1];
            for (int i = 0; i < tags.size(); i++) {
                var fieldToCamera = tags.get(i).pose;
                ids[i] = tags.get(i).ID;
                poses[i * 7] = fieldToCamera.getTranslation().getX();
                poses[i * 7 + 1] = fieldToCamera.getTranslation().getY();
                poses[i * 7 + 2] = fieldToCamera.getTranslation().getZ();
                poses[i * 7 + 3] = fieldToCamera.getRotation().getQuaternion().getW();
                poses[i * 7 + 4] = fieldToCamera.getRotation().getQuaternion().getX();
                poses[i * 7 + 5] = fieldToCamera.getRotation().getQuaternion().getY();
                poses[i * 7 + 6] = fieldToCamera.getRotation().getQuaternion().getZ();
            }
            SmartDashboard.putNumberArray("tagPose", poses);
            SmartDashboard.putNumberArray("tagID", ids);
        }

        // Forward Camera
        photonCamera = new PhotonCamera(
                VisionConstants.cameraName); // Change the name of your camera here to whatever it is in the
        // PhotonVision UI.

        // ... Add other cameras here

        // Assemble the list of cameras & mount locations
        var camList = new ArrayList<Pair<PhotonCamera, Transform3d>>();
        camList.add(new Pair<PhotonCamera, Transform3d>(photonCamera, VisionConstants.robotToCam));

        robotPoseEstimator = new RobotPoseEstimator(atfl, PoseStrategy.CLOSEST_TO_REFERENCE_POSE, camList);
    }

    public static class ObservedTags {
        public List<Pose2d> robotInFieldPoses = new ArrayList<>();
        public double fpgaTimestamp;
    }

    public ObservedTags getObservedTags() {
        var ret = new ObservedTags();

        // Grab our camera's latest result
        var result = photonCamera.getLatestResult();

        // Make sure we don't push duplicate observations
        if (Math.abs(result.getTimestampSeconds() - lastResult.getTimestampSeconds()) < 1e-6) {
            return new ObservedTags();
        }

        lastResult = result;

        for (var target : result.getTargets()) {
            var fieldToTag = atfl.getTagPose(target.getFiducialId());
            if (fieldToTag.isPresent()) {
                var j = target.getFiducialId();

                // This is the pose of our _camera_ in field space
                Pose3d fieldToCamera = fieldToTag.get()
                        .plus(target.getBestCameraToTarget().inverse());

                // Offset the camera pose to get the robot's pose in the field
                var fieldToRobot = fieldToCamera.plus(Constants.VisionConstants.robotToCam.inverse());

                // Squish down to a pose2d (ignore everything but XY location and Z angle)
                ret.robotInFieldPoses.add(fieldToRobot.toPose2d());
            }
        }

        // Tell the pose estimator the time this observation was made at
        ret.fpgaTimestamp = result.getTimestampSeconds();

        return ret;
    }

    /**
     * @param estimatedRobotPose The current best guess at robot pose
     * @return A pair of the fused camera observations to a single Pose2d on the
     *         field, and the time
     *         of the observation. Assumes a planar field and the robot is always
     *         firmly on the ground
     */
    public Pair<Pose2d, Double> getEstimatedGlobalPose(Pose2d prevEstimatedRobotPose) {
        robotPoseEstimator.setReferencePose(prevEstimatedRobotPose);

        double currentTime = Timer.getFPGATimestamp();
        Optional<Pair<Pose3d, Double>> result = robotPoseEstimator.update();
        if (result.isPresent() && result.get() != null && result.get().getFirst() != null) {
            return new Pair<Pose2d, Double>(
                    result.get().getFirst().toPose2d(), currentTime - result.get().getSecond());
        } else {
            return new Pair<Pose2d, Double>(null, 0.0);
        }
    }
}

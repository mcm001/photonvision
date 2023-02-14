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
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.IOException;

/** Reports our expected, desired, and actual poses to dashboards */
public class PoseTelemetry {
    Field2d field = new Field2d();

    Pose2d actPose = new Pose2d();
    Pose2d desPose = new Pose2d();
    Pose2d estPose = new Pose2d();

    public void recordOutput(AprilTag[] layout) {
        double[] data = new double[layout.length * 7];
        for (int i = 0; i < layout.length; i++) {
            data[i * 7] = layout[i].pose.getX();
            data[i * 7 + 1] = layout[i].pose.getY();
            data[i * 7 + 2] = layout[i].pose.getZ();
            data[i * 7 + 3] = layout[i].pose.getRotation().getQuaternion().getW();
            data[i * 7 + 4] = layout[i].pose.getRotation().getQuaternion().getX();
            data[i * 7 + 5] = layout[i].pose.getRotation().getQuaternion().getY();
            data[i * 7 + 6] = layout[i].pose.getRotation().getQuaternion().getZ();
        }
        SmartDashboard.putNumberArray("tagposes", data);
    }

    public PoseTelemetry() {
        SmartDashboard.putData("Field", field);
        update();

        try {
            AprilTag[] layout =
                    AprilTagFieldLayout.loadFromResource(AprilTagFields.k2022RapidReact.m_resourceFile)
                            .getTags()
                            .toArray(new AprilTag[] {});

            recordOutput(layout);

            var list = new double[layout.length];
            for (int i = 0; i < layout.length; i++) list[i] = (double) i;
            SmartDashboard.putNumberArray("tag IDs", list);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void update() {
        field.getObject("DesPose").setPose(desPose);
        field.getObject("ActPose").setPose(actPose);
        field.getObject("Robot").setPose(estPose);
    }

    public void setActualPose(Pose2d in) {
        actPose = in;
    }

    public void setDesiredPose(Pose2d in) {
        desPose = in;
    }

    public void setEstimatedPose(Pose2d in) {
        estPose = in;
    }
}

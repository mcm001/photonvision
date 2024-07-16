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

// THIS FILE WAS AUTO-GENERATED BY ./photon-targeting/generate_messages.py. DO NOT MODIFY

package org.photonvision.struct;

import org.photonvision.common.dataflow.structures.Packet;
import org.photonvision.common.dataflow.structures.PacketSerde;
import org.photonvision.utils.PacketUtils;

// Assume that the base class lives here and we can import it
import org.photonvision.targeting.*;


/**
 * Auto-generated serialization/deserialization helper for PhotonTrackedTarget
 */
public class PhotonTrackedTargetSerde implements PacketSerde<PhotonTrackedTarget> {

    // Message definition md5sum. See photon_packet.adoc for details
    public static final String MESSAGE_VERSION = "bc26b76ea1520b42d776972512362b11";
    public static final String MESSAGE_FORMAT = "{\"fields\": [{\"name\": \"yaw\", \"type\": \"float64\"}, {\"name\": \"pitch\", \"type\": \"float64\"}, {\"name\": \"area\", \"type\": \"float64\"}, {\"name\": \"skew\", \"type\": \"float64\"}, {\"name\": \"fiducialId\", \"type\": \"int32\"}, {\"name\": \"objDetectConf\", \"type\": \"float32\"}, {\"name\": \"bestCameraToTarget\", \"type\": \"Transform3d\"}, {\"name\": \"altCameraToTarget\", \"type\": \"Transform3d\"}, {\"name\": \"poseAmbiguity\", \"type\": \"float64\"}, {\"name\": \"minAreaRectCorners\", \"type\": \"TargetCorner\", \"vla\": true}, {\"name\": \"detectedCorners\", \"type\": \"TargetCorner\", \"vla\": true}], \"name\": \"PhotonTrackedTarget\"}";

    @Override
    public int getMaxByteSize() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMaxByteSize'");
    }

    @Override
    public void pack(Packet packet, PhotonTrackedTarget value) {
        // field yaw is of intrinsic type float64
        packet.encode((double) value.yaw);
    
        // field pitch is of intrinsic type float64
        packet.encode((double) value.pitch);
    
        // field area is of intrinsic type float64
        packet.encode((double) value.area);
    
        // field skew is of intrinsic type float64
        packet.encode((double) value.skew);
    
        // field fiducialId is of intrinsic type int32
        packet.encode((int) value.fiducialId);
    
        // field objDetectConf is of intrinsic type float32
        packet.encode((float) value.objDetectConf);
    
        // field is shimmed!
        PacketUtils.packTransform3d(packet, value.bestCameraToTarget);
    
        // field is shimmed!
        PacketUtils.packTransform3d(packet, value.altCameraToTarget);
    
        // field poseAmbiguity is of intrinsic type float64
        packet.encode((double) value.poseAmbiguity);
    
        // minAreaRectCorners is a custom VLA!
        packet.encodeList(value.minAreaRectCorners);
    
        // detectedCorners is a custom VLA!
        packet.encodeList(value.detectedCorners);
    }

    @Override
    public PhotonTrackedTarget unpack(Packet packet) {
        var ret = new PhotonTrackedTarget();

        // yaw is of intrinsic type float64
        ret.yaw = packet.decodeDouble();
    
        // pitch is of intrinsic type float64
        ret.pitch = packet.decodeDouble();
    
        // area is of intrinsic type float64
        ret.area = packet.decodeDouble();
    
        // skew is of intrinsic type float64
        ret.skew = packet.decodeDouble();
    
        // fiducialId is of intrinsic type int32
        ret.fiducialId = packet.decodeInt();
    
        // objDetectConf is of intrinsic type float32
        ret.objDetectConf = packet.decodeFloat();
    
        // field is shimmed!
        ret.bestCameraToTarget = PacketUtils.unpackTransform3d(packet);
    
        // field is shimmed!
        ret.altCameraToTarget = PacketUtils.unpackTransform3d(packet);
    
        // poseAmbiguity is of intrinsic type float64
        ret.poseAmbiguity = packet.decodeDouble();
    
        // minAreaRectCorners is a custom VLA!
        ret.minAreaRectCorners = packet.decodeList(TargetCorner.photonStruct);
    
        // detectedCorners is a custom VLA!
        ret.detectedCorners = packet.decodeList(TargetCorner.photonStruct);

        return ret;
    }
}
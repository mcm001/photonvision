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

#include "photon/struct/PhotonTrackedTargetSerde.h"

// For namespacing dependant structserializable types
using namespace photon;

using StructType = ::photon::Struct<PhotonTrackedTarget>;

void StructType::Pack(Packet& packet, const PhotonTrackedTarget& value) {packet.Pack<double>(value.yaw);
    packet.Pack<double>(value.pitch);
    packet.Pack<double>(value.area);
    packet.Pack<double>(value.skew);
    packet.Pack<int32_t>(value.fiducialId);
    packet.Pack<float>(value.objDetectConf);
    packet.Pack<frc::Transform3d>(value.bestCameraToTarget);
    packet.Pack<frc::Transform3d>(value.altCameraToTarget);
    packet.Pack<double>(value.poseAmbiguity);
    packet.Pack<TargetCorner>(value.minAreaRectCorners);
    packet.Pack<TargetCorner>(value.detectedCorners);
}

PhotonTrackedTarget StructType::Unpack(Packet& packet) {
    PhotonTrackedTarget ret;

    ret.yaw = packet.Unpack<double>();
    ret.pitch = packet.Unpack<double>();
    ret.area = packet.Unpack<double>();
    ret.skew = packet.Unpack<double>();
    ret.fiducialId = packet.Unpack<int32_t>();
    ret.objDetectConf = packet.Unpack<float>();
    ret.bestCameraToTarget = packet.Unpack<frc::Transform3d>();
    ret.altCameraToTarget = packet.Unpack<frc::Transform3d>();
    ret.poseAmbiguity = packet.Unpack<double>();
    ret.minAreaRectCorners = packet.Unpack<TargetCorner>();
    ret.detectedCorners = packet.Unpack<TargetCorner>();

    return ret;
}
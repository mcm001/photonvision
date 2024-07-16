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

#include "photon/struct/PhotonPipelineResultSerde.h"

// For namespacing dependant structserializable types
using namespace photon;

using StructType = ::photon::Struct<PhotonPipelineResult>;

void StructType::Pack(Packet& packet, const PhotonPipelineResult& value) {packet.Pack<PhotonPipelineMetadata>(value.metadata);
    packet.Pack<PhotonTrackedTarget>(value.targets);
    packet.Pack<MultiTargetPNPResult>(value.multiTagResult);
}

PhotonPipelineResult StructType::Unpack(Packet& packet) {
    PhotonPipelineResult ret;

    ret.metadata = packet.Unpack<PhotonPipelineMetadata>();
    ret.targets = packet.Unpack<PhotonTrackedTarget>();
    ret.multiTagResult = packet.Unpack<MultiTargetPNPResult>();

    return ret;
}
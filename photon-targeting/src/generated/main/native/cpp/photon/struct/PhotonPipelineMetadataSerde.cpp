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

#include "photon/struct/PhotonPipelineMetadataSerde.h"

// For namespacing dependant structserializable types
using namespace photon;

using StructType = ::photon::Struct<PhotonPipelineMetadata>;

void StructType::Pack(Packet& packet, const PhotonPipelineMetadata& value) {packet.Pack<int64_t>(value.sequenceID);
    packet.Pack<int64_t>(value.captureTimestampMicros);
    packet.Pack<int64_t>(value.publishTimestampMicros);
}

PhotonPipelineMetadata StructType::Unpack(Packet& packet) {
    PhotonPipelineMetadata ret;

    ret.sequenceID = packet.Unpack<int64_t>();
    ret.captureTimestampMicros = packet.Unpack<int64_t>();
    ret.publishTimestampMicros = packet.Unpack<int64_t>();

    return ret;
}
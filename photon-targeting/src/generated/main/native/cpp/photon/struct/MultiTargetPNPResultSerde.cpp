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

#include "photon/struct/MultiTargetPNPResultSerde.h"

// For namespacing dependant structserializable types
using namespace photon;

using StructType = ::photon::Struct<MultiTargetPNPResult>;

void StructType::Pack(Packet& packet, const MultiTargetPNPResult& value) {packet.Pack<PnpResult>(value.estimatedPose);
    packet.Pack<int16_t>(value.fiducialIDsUsed);
}

MultiTargetPNPResult StructType::Unpack(Packet& packet) {
    MultiTargetPNPResult ret;

    ret.estimatedPose = packet.Unpack<PnpResult>();
    ret.fiducialIDsUsed = packet.Unpack<int16_t>();

    return ret;
}
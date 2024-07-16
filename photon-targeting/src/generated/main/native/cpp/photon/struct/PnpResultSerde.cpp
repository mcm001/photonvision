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

#include "photon/struct/PnpResultSerde.h"

// For namespacing dependant structserializable types
using namespace photon;

using StructType = ::photon::Struct<PnpResult>;

void StructType::Pack(Packet& packet, const PnpResult& value) {packet.Pack<frc::Transform3d>(value.best);
    packet.Pack<frc::Transform3d>(value.alt);
    packet.Pack<double>(value.bestReprojErr);
    packet.Pack<double>(value.altReprojErr);
    packet.Pack<double>(value.ambiguity);
}

PnpResult StructType::Unpack(Packet& packet) {
    PnpResult ret;

    ret.best = packet.Unpack<frc::Transform3d>();
    ret.alt = packet.Unpack<frc::Transform3d>();
    ret.bestReprojErr = packet.Unpack<double>();
    ret.altReprojErr = packet.Unpack<double>();
    ret.ambiguity = packet.Unpack<double>();

    return ret;
}
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

#pragma once

// THIS FILE WAS AUTO-GENERATED BY ./photon-targeting/generate_messages.py. DO NOT MODIFY

#include "photon/dataflow/structures/Packet.h"
#include "photon/targeting/PhotonPipelineMetadata.h"

template <>
struct WPILIB_DLLEXPORT photon::Struct<photon::PhotonPipelineMetadata> {
  static constexpr std::string_view GetSchemaHash() {
    return "2a7039527bda14d13028a1b9282d40a2";
  }

  static constexpr std::string_view GetSchema() {
    return "{\"fields\": [{\"name\": \"sequenceID\", \"type\": \"int64\"}, {\"name\": \"captureTimestampMicros\", \"type\": \"int64\"}, {\"name\": \"publishTimestampMicros\", \"type\": \"int64\"}], \"name\": \"PhotonPipelineMetadata\"}";
  }

  static photon::PhotonPipelineMetadata Unpack(photon::Packet& packet);
  static void Pack(photon::Packet& packet, const photon::PhotonPipelineMetadata& value);
};

static_assert(photon::PhotonStructSerializable<photon::PnpResult>);
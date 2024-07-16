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
#include "photon/targeting/MultiTargetPNPResult.h"

template <>
struct WPILIB_DLLEXPORT photon::Struct<photon::MultiTargetPNPResult> {
  static constexpr std::string_view GetSchemaHash() {
    return "66304efd9a29e2ed932b80e4cd7f2652";
  }

  static constexpr std::string_view GetSchema() {
    return "{\"fields\": [{\"name\": \"estimatedPose\", \"type\": \"PnpResult\"}, {\"name\": \"fiducialIDsUsed\", \"type\": \"int16\", \"vla\": true}], \"name\": \"MultiTargetPNPResult\"}";
  }

  static photon::MultiTargetPNPResult Unpack(photon::Packet& packet);
  static void Pack(photon::Packet& packet, const photon::MultiTargetPNPResult& value);
};

static_assert(photon::PhotonStructSerializable<photon::PnpResult>);
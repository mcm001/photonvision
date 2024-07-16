#pragma once

#include "photon/dataflow/structures/Packet.h"
#include "photon/targeting/PnpResult.h"

template <>
struct WPILIB_DLLEXPORT photon::Struct<photon::PnpResult> {
  static constexpr std::string_view GetSchemaHash() {
    return "todo lol md5";
  }

  static constexpr std::string_view GetSchema() {
    return "Translation3d translation;Rotation3d rotation; todo other things";
  }

  static photon::PnpResult Unpack(photon::Packet& packet);
  static void Pack(photon::Packet& packet, const photon::PnpResult& value);
};

static_assert(photon::PhotonStructSerializable<photon::PnpResult>);

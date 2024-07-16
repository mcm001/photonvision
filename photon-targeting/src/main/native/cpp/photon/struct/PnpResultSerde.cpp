#include "photon/struct/PnpResultSerde.h"

using StructType = photon::Struct<photon::PnpResult>;

void StructType::Pack(photon::Packet& packet, const photon::PnpResult& value) {
    packet.Pack<frc::Transform3d>(value.best);
    packet.Pack<frc::Transform3d>(value.alt);
    packet.Pack<double>(value.bestReprojErr);
    packet.Pack<double>(value.altReprojErr);
    packet.Pack<double>(value.ambiguity);
}

photon::PnpResult StructType::Unpack(photon::Packet& packet) {
    photon::PnpResult ret;

    ret.best = packet.Unpack<frc::Transform3d>();
    ret.alt = packet.Unpack<frc::Transform3d>();
    ret.bestReprojErr = packet.Unpack<double>();
    ret.altReprojErr = packet.Unpack<double>();
    ret.ambiguity = packet.Unpack<double>();

    return ret;
}

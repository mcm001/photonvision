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

#include <algorithm>
#include <bit>
#include <cstring>
#include <string>
#include <vector>
#include <optional>
#include <span>
#include <wpi/struct/Struct.h>

namespace photon {

class Packet;

// Struct is where all our actual ser/de methods are implemented
template <typename T>
struct Struct{};

template <typename T>
concept PhotonStructSerializable = requires(Packet& packet, const T& value) {
    typename Struct<typename std::remove_cvref_t<T>>;

  // MD6sum of the message definition
  { Struct<typename std::remove_cvref_t<T>>::GetSchemaHash() } -> std::convertible_to<std::string_view>;
  // JSON-encoded message chema
  { Struct<typename std::remove_cvref_t<T>>::GetSchema() } -> std::convertible_to<std::string_view>;
  // Unpack myself from a packet
  { Struct<typename std::remove_cvref_t<T>>::Unpack(packet) } -> std::same_as<typename std::remove_cvref_t<T>>;
  // Pack myself into a packet
  { Struct<typename std::remove_cvref_t<T>>::Pack(packet, value) } -> std::same_as<void>;
};

// template <typename T>
// photon::Struct<std::optional<T>> OptionalPhotonStruct {
//   static std::string_view GetSchemaHash();
//   static std::string_view GetSchema();

//   void Pack(photon::Packet& packet, const T& value) {
//     packet.Pack<bool>(src.has_value());
//     if (src) {
//       packet.Pack<T>(*src);
//     }
//   }
//   T Unpack(photon::Packet& packet) {
//     bool present = packet.Unpack<bool>();
//     if (present) {
//       return packet.Unpack<T>();
//     }
//     return std::nullopt;
//   }
// };


/**
 * A packet that holds byte-packed data to be sent over NetworkTables.
 */
class Packet {
 public:
  /**
   * Constructs an empty packet.
   */
  Packet() = default;

  /**
   * Constructs a packet with the given data.
   * @param data The packet data.
   */
  explicit Packet(std::vector<uint8_t> data);

  /**
   * Clears the packet and resets the read and write positions.
   */
  void Clear();

  /**
   * Returns the packet data.
   * @return The packet data.
   */
  inline const std::vector<uint8_t>& GetData() { return packetData; }

  /**
   * Returns the number of bytes in the data.
   * @return The number of bytes in the data.
   */
  inline size_t GetDataSize() const { return packetData.size(); }

  template <typename T, typename... I>
  requires wpi::StructSerializable<T, I...>
  inline void Pack(const T& value) {
    wpi::PackStruct(packetData, value);
    writePos += wpi::GetStructSize<T, I...>();
  }

  template <typename T>
  void PackList(const std::span<T> src) {
    if (src.size() > 127) {
      // bad stuff lol; we need to give up
    }
    Pack<uint8_t>(src.size());
    for (const auto& t : src) {
      Pack(src);
    }
  }

  template <typename T, typename... I>
  requires wpi::StructSerializable<T, I...>
  inline T Unpack() {
    T ret = wpi::UnpackStruct<T, I...>(packetData);
    readPos += wpi::GetStructSize<T, I...>();
    return ret;
  }

  template <typename T>
  std::vector<T> UnpackList() {
    auto len = Unpack<uint8_t>();

    std::vector<T> ret {};
    ret.resize(len);
    for (int i = 0; i < len; i++) {
      Unpack<T>(ret[i]);
    }
    return ret;
  }

  bool operator==(const Packet& right) const;
  bool operator!=(const Packet& right) const;

 private:
  // Data stored in the packet
  std::vector<uint8_t> packetData;

  size_t readPos = 0;
  size_t writePos = 0;
};
}  // namespace photon

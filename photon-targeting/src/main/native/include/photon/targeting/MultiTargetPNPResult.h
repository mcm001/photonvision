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

#include <frc/geometry/Transform3d.h>
#include <wpi/SmallVector.h>

#include "PnpResult.h"
#include "photon/dataflow/structures/Packet.h"
#include "photon/struct/MultiTargetPNPResultStruct.h"

namespace photon {
class MultiTargetPNPResult : public MultiTargetPNPResult_PhotonStruct {
 public:
  explicit MultiTargetPNPResult(MultiTargetPNPResult_PhotonStruct&& data)
      : MultiTargetPNPResult_PhotonStruct(data) {}

  friend bool operator==(MultiTargetPNPResult const&,
                         MultiTargetPNPResult const&) = default;
};
}  // namespace photon

#include "photon/serde/MultiTargetPNPResultSerde.h"

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

#include "photonlib/PhotonPipelineResult.h"

namespace photonlib {
PhotonPipelineResult::PhotonPipelineResult(
    units::second_t latency, wpi::ArrayRef<PhotonTrackedTarget> targets)
    : latency(latency),
      targets(targets.data(), targets.data() + targets.size()) {
  hasTargets = targets.size() != 0;
}

bool PhotonPipelineResult::operator==(const PhotonPipelineResult& other) const {
  return latency == other.latency && hasTargets == other.hasTargets &&
         targets == other.targets;
}

bool PhotonPipelineResult::operator!=(const PhotonPipelineResult& other) const {
  return !operator==(other);
}

Packet& operator<<(Packet& packet, const PhotonPipelineResult& result) {
  // Encode latency, existence of targets, and number of targets.
  packet << result.latency.to<double>() * 1000 << result.hasTargets
         << static_cast<int8_t>(result.targets.size());

  // Encode the information of each target.
  for (auto& target : result.targets) packet << target;

  // Return the packet
  return packet;
}

Packet& operator>>(Packet& packet, PhotonPipelineResult& result) {
  // Decode latency, existence of targets, and number of targets.
  int8_t targetCount = 0;
  double latencyMillis = 0;
  packet >> latencyMillis >> result.hasTargets >> targetCount;
  result.latency = units::second_t(latencyMillis / 1000.0);

  result.targets.clear();

  // Decode the information of each target.
  for (int i = 0; i < targetCount; ++i) {
    PhotonTrackedTarget target;
    packet >> target;
    result.targets.push_back(target);
  }
  return packet;
}

}  // namespace photonlib

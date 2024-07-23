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

#include <units/angle.h>

#include "gtest/gtest.h"
#include "photon/dataflow/structures/Packet.h"
#include "photon/targeting/MultiTargetPNPResult.h"
#include "photon/targeting/PhotonPipelineResult.h"
#include "photon/targeting/PhotonTrackedTarget.h"
#include "photon/targeting/PnpResult.h"

using namespace photon;

TEST(PacketTest, PnpResult) {
  PnpResult result{PnpResult_PhotonStruct{}};
  Packet p {1000};
  p.Pack<PnpResult>(result);

  PnpResult b = p.Unpack<PnpResult>();

  EXPECT_EQ(result, b);
}

// TEST(PacketTest, MultiTargetPNPResult) {
//   MultiTargetPNPResult result;
//   Packet p;
//   p << result;

//   MultiTargetPNPResult b;
//   p >> b;

//   EXPECT_EQ(result, b);
// }

// TEST(PacketTest, PhotonTrackedTarget) {
//   PhotonTrackedTarget target{
//       3.0,
//       4.0,
//       9.0,
//       -5.0,
//       -1,
//       -1,
//       -1.0,
//       frc::Transform3d(frc::Translation3d(1_m, 2_m, 3_m),
//                        frc::Rotation3d(1_rad, 2_rad, 3_rad)),
//       frc::Transform3d(frc::Translation3d(1_m, 2_m, 3_m),
//                        frc::Rotation3d(1_rad, 2_rad, 3_rad)),
//       -1,
//       {std::pair{1, 2}, std::pair{3, 4}, std::pair{5, 6}, std::pair{7, 8}},
//       {std::pair{1, 2}, std::pair{3, 4}, std::pair{5, 6}, std::pair{7, 8}}};

//   Packet p;
//   p << target;

//   PhotonTrackedTarget b;
//   p >> b;

//   EXPECT_EQ(target, b);
// }

// TEST(PacketTest, PhotonPipelineResult) {
//   PhotonPipelineResult result{0, 0_s, 1_s, {}};
//   Packet p;
//   p << result;

//   PhotonPipelineResult b;
//   p >> b;

//   EXPECT_EQ(result, b);

//   wpi::SmallVector<PhotonTrackedTarget, 2> targets{
//       PhotonTrackedTarget{
//           3.0,
//           -4.0,
//           9.0,
//           4.0,
//           1,
//           -1,
//           -1.0,
//           frc::Transform3d(frc::Translation3d(1_m, 2_m, 3_m),
//                            frc::Rotation3d(1_rad, 2_rad, 3_rad)),
//           frc::Transform3d(frc::Translation3d(1_m, 2_m, 3_m),
//                            frc::Rotation3d(1_rad, 2_rad, 3_rad)),
//           -1,
//           {std::pair{1, 2}, std::pair{3, 4}, std::pair{5, 6}, std::pair{7,
//           8}}, {std::pair{1, 2}, std::pair{3, 4}, std::pair{5, 6},
//           std::pair{7, 8}}},
//       PhotonTrackedTarget{
//           3.0,
//           -4.0,
//           9.1,
//           6.7,
//           -1,
//           -1,
//           -1.0,
//           frc::Transform3d(frc::Translation3d(1_m, 2_m, 3_m),
//                            frc::Rotation3d(1_rad, 2_rad, 3_rad)),
//           frc::Transform3d(frc::Translation3d(1_m, 2_m, 3_m),
//                            frc::Rotation3d(1_rad, 2_rad, 3_rad)),
//           -1,
//           {std::pair{1, 2}, std::pair{3, 4}, std::pair{5, 6}, std::pair{7,
//           8}}, {std::pair{1, 2}, std::pair{3, 4}, std::pair{5, 6},
//            std::pair{7, 8}}}};

//   PhotonPipelineResult result2{0, 0_s, 1_s, targets};
//   Packet p2;
//   p2 << result2;

//   PhotonPipelineResult b2;
//   p2 >> b2;

//   EXPECT_EQ(result2, b2);
// }

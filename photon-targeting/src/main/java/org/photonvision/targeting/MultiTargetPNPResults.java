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

package org.photonvision.targeting;

import java.util.ArrayList;
import java.util.List;
import org.photonvision.common.dataflow.structures.Packet;

public class MultiTargetPNPResults {
    // pnpresult + 32 possible targets (arbitrary upper limit that should never be hit, ideally)
    private static final int MAX_IDS = 32;
    public static final int PACK_SIZE_BYTES = PNPResults.PACK_SIZE_BYTES + Byte.BYTES * MAX_IDS;

    public PNPResults estimatedPose = new PNPResults();
    public List<Integer> fiducialIDsUsed = List.of();

    public MultiTargetPNPResults() {}

    public MultiTargetPNPResults(PNPResults results, ArrayList<Integer> ids) {
        estimatedPose = results;
        fiducialIDsUsed = ids;
    }

    public static MultiTargetPNPResults createFromPacket(Packet packet) {
        var results = PNPResults.createFromPacket(packet);
        var ids = new ArrayList<Integer>(MAX_IDS);
        for (int i = 0; i < MAX_IDS; i++) {
            ids.add((int) packet.decodeByte());
        }
        return new MultiTargetPNPResults(results, ids);
    }

    public void populatePacket(Packet packet) {
        estimatedPose.populatePacket(packet);
        for (int i = 0; i < MAX_IDS; i++) {
            if (i < fiducialIDsUsed.size()) {
                packet.encode(fiducialIDsUsed.get(i).byteValue());
            } else {
                packet.encode(Byte.MIN_VALUE);
            }
        }
    }
}
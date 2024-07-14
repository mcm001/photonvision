package org.photonvision.targeting.serde;

import org.photonvision.common.dataflow.structures.PacketSerde;

public interface PhotonStructSerializable<T> {
    PacketSerde<T> getSerde();
}

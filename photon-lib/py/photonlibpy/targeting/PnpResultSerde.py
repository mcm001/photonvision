from photonlibpy.packet import Packet

class PnpResultSerde:
    
    # Message definition md5sum. See photon_packet.adoc for details
    MESSAGE_VERSION = "78f12b7e27611118ac1f416c4433bd86"
    MESSAGE_FORMAT = "Transform3d best;Transform3d alt;float64 bestReprojErr;float64 altReprojErr;float64 ambiguity;"

    def populateFromPacket(self, packet: Packet) -> Packet:
        self.best = packet.Unpack<frc::Transform3d>(),
        self.alt = packet.Unpack<frc::Transform3d>(),
        self.bestReprojErr = packet.Unpack<double>(),
        self.altReprojErr = packet.Unpack<double>(),
        self.ambiguity = packet.Unpack<double>(),

        # self.sequenceID = packet.decodei64()
        # self.captureTimestampMicros = packet.decodei64()
        # self.publishTimestampMicros = packet.decodei64()

        # targetCount = packet.decode8()

        # self.targets = []
        # for _ in range(targetCount):
        #     target = PhotonTrackedTarget()
        #     target.createFromPacket(packet)
        #     self.targets.append(target)

        # self.multiTagResult = MultiTargetPNPResult()
        # self.multiTagResult.createFromPacket(packet)

        # return packet

from photonlibpy.packet import Packet

class MultiTargetPNPResultSerde:
    
    # Message definition md5sum. See photon_packet.adoc for details
    MESSAGE_VERSION = "03960a77c0071f70aa848e7a11c6dd74"
    MESSAGE_FORMAT = "PnpResult estimatedPose;int16[?] fiducialIDsUsed;"

    def populateFromPacket(self, packet: Packet) -> Packet:
        self.estimatedPose = packet.Unpack<photon::PnpResult>(),
        self.fiducialIDsUsed = packet.Unpack<std::vector<int16_t>>(),

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

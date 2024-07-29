from photonlibpy.packet import Packet

class PhotonPipelineMetadataSerde:
    
    # Message definition md5sum. See photon_packet.adoc for details
    MESSAGE_VERSION = "2a7039527bda14d13028a1b9282d40a2"
    MESSAGE_FORMAT = "int64 sequenceID;int64 captureTimestampMicros;int64 publishTimestampMicros;"

    def populateFromPacket(self, packet: Packet) -> Packet:
        self.sequenceID = packet.Unpack<int64_t>(),
        self.captureTimestampMicros = packet.Unpack<int64_t>(),
        self.publishTimestampMicros = packet.Unpack<int64_t>(),

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

from photonlibpy.packet import Packet

class PhotonPipelineResultSerde:
    
    # Message definition md5sum. See photon_packet.adoc for details
    MESSAGE_VERSION = "eefe85cf831c55de6f95e367c3f8784b"
    MESSAGE_FORMAT = "PhotonPipelineMetadata metadata;PhotonTrackedTarget[?] targets;MultiTargetPNPResult? multitagResult;"

    def populateFromPacket(self, packet: Packet) -> Packet:
        self.metadata = packet.Unpack<photon::PhotonPipelineMetadata>(),
        self.targets = packet.Unpack<std::vector<photon::PhotonTrackedTarget>>(),
        self.multitagResult = packet.Unpack<std::optional<photon::MultiTargetPNPResult>>(),

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

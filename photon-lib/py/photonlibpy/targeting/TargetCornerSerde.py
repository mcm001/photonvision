from photonlibpy.packet import Packet

class TargetCornerSerde:
    
    # Message definition md5sum. See photon_packet.adoc for details
    MESSAGE_VERSION = "22b1ff7551d10215af6fb3672fe4eda8"
    MESSAGE_FORMAT = "float64 x;float64 y;"

    def populateFromPacket(self, packet: Packet) -> Packet:
        self.x = packet.Unpack<double>(),
        self.y = packet.Unpack<double>(),

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

from photonlibpy.packet import Packet

from photonlibpy.multiTargetPNPResult import MultiTargetPNPResult
from photonlibpy.photonTrackedTarget import PhotonTrackedTarget


class PhotonPipelineResult:
    def populateFromPacket(self, packet: Packet) -> Packet:

        self.sequenceID = packet.decodei64()
        self.captureTimestampMicros = packet.decodei64()
        self.publishTimestampMicros = packet.decodei64()

        targetCount = packet.decode8()

        self.targets = []
        for _ in range(targetCount):
            target = PhotonTrackedTarget()
            target.createFromPacket(packet)
            self.targets.append(target)

        self.multiTagResult = MultiTargetPNPResult()
        self.multiTagResult.createFromPacket(packet)

        return packet

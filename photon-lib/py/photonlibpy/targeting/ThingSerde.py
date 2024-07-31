from photonlibpy.packet import Packet

from photonlibpy.multiTargetPNPResult import MultiTargetPNPResult
from photonlibpy.photonTrackedTarget import PhotonTrackedTarget


class PhotonPipelineResult:
    def populateFromPacket(self, packet: Packet) -> Packet:
        self.sequenceID = packet.decodeLong()
        self.captureTimestampMicros = packet.decodeLong()
        self.publishTimestampMicros = packet.decodeLong()

        targetCount = packet.decode8()

        self.targets = []
        for _ in range(targetCount):
            target = PhotonTrackedTarget()
            target.createFromPacket(packet)
            self.targets.append(target)

        self.multiTagResult = MultiTargetPNPResult()
        self.multiTagResult.createFromPacket(packet)

        return packet

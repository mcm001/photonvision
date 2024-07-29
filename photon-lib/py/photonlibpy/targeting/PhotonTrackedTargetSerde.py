from photonlibpy.packet import Packet

class PhotonTrackedTargetSerde:
    
    # Message definition md5sum. See photon_packet.adoc for details
    MESSAGE_VERSION = "4608fa07779c423723ef2d8f2ae01e53"
    MESSAGE_FORMAT = "float64 yaw;float64 pitch;float64 area;float64 skew;int32 fiducialId;int32 objDetectId;float32 objDetectConf;Transform3d bestCameraToTarget;Transform3d altCameraToTarget;float64 poseAmbiguity;TargetCorner[?] minAreaRectCorners;TargetCorner[?] detectedCorners;"

    def populateFromPacket(self, packet: Packet) -> Packet:
        self.yaw = packet.Unpack<double>(),
        self.pitch = packet.Unpack<double>(),
        self.area = packet.Unpack<double>(),
        self.skew = packet.Unpack<double>(),
        self.fiducialId = packet.Unpack<int32_t>(),
        self.objDetectId = packet.Unpack<int32_t>(),
        self.objDetectConf = packet.Unpack<float>(),
        self.bestCameraToTarget = packet.Unpack<frc::Transform3d>(),
        self.altCameraToTarget = packet.Unpack<frc::Transform3d>(),
        self.poseAmbiguity = packet.Unpack<double>(),
        self.minAreaRectCorners = packet.Unpack<std::vector<photon::TargetCorner>>(),
        self.detectedCorners = packet.Unpack<std::vector<photon::TargetCorner>>(),

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

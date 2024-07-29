from photonlibpy.packet import Packet

from photonlibpy import *

class PhotonTrackedTargetSerde:

    # Message definition md5sum. See photon_packet.adoc for details
    MESSAGE_VERSION = "8fdada56b9162f2e32bd24f0055d7b60"
    MESSAGE_FORMAT = "float64 yaw;float64 pitch;float64 area;float64 skew;int32 fiducialId;int32 objDetectId;float32 objDetectConf;Transform3d bestCameraToTarget;Transform3d altCameraToTarget;float64 poseAmbiguity;TargetCorner[?] minAreaRectCorners;TargetCorner[?] detectedCorners;"

    @staticmethod
    def unpack(packet: Packet) -> Packet:
        ret = PhotonTrackedTarget()

        # yaw is of intrinsic type float64
        ret.yaw = packet.decodeDouble()
    
        # pitch is of intrinsic type float64
        ret.pitch = packet.decodeDouble()
    
        # area is of intrinsic type float64
        ret.area = packet.decodeDouble()
    
        # skew is of intrinsic type float64
        ret.skew = packet.decodeDouble()
    
        # fiducialId is of intrinsic type int32
        ret.fiducialId = packet.decodeInt()
    
        # objDetectId is of intrinsic type int32
        ret.objDetectId = packet.decodeInt()
    
        # objDetectConf is of intrinsic type float32
        ret.objDetectConf = packet.decodeFloat()
    
        # field is shimmed!
        ret.bestCameraToTarget = packet.decodeTransform()
    
        # field is shimmed!
        ret.altCameraToTarget = packet.decodeTransform()
    
        # poseAmbiguity is of intrinsic type float64
        ret.poseAmbiguity = packet.decodeDouble()
    
        # minAreaRectCorners is a custom VLA!
        ret.minAreaRectCorners = packet.decodeList(TargetCorner.photonStruct)
    
        # detectedCorners is a custom VLA!
        ret.detectedCorners = packet.decodeList(TargetCorner.photonStruct)

        return ret
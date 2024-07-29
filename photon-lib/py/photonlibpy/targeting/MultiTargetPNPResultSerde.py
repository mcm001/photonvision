from photonlibpy.packet import Packet

from photonlibpy import *

class MultiTargetPNPResultSerde:

    # Message definition md5sum. See photon_packet.adoc for details
    MESSAGE_VERSION = "ffc1cb847deb6e796a583a5b1885496b"
    MESSAGE_FORMAT = "PnpResult estimatedPose;int16[?] fiducialIDsUsed;"

    @staticmethod
    def unpack(packet: Packet) -> Packet:
        ret = MultiTargetPNPResult()

        # estimatedPose is of non-intrinsic type PnpResult
        ret.estimatedPose = PnpResult.photonStruct.unpack(packet)
    
        # fiducialIDsUsed is a custom VLA!
        ret.fiducialIDsUsed = packet.decodeShortList()

        return ret
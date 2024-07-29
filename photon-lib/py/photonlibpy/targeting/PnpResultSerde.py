from photonlibpy.packet import Packet

from photonlibpy import *

class PnpResultSerde:

    # Message definition md5sum. See photon_packet.adoc for details
    MESSAGE_VERSION = "0d1f2546b00f24718e30f38d206d4491"
    MESSAGE_FORMAT = "Transform3d best;Transform3d alt;float64 bestReprojErr;float64 altReprojErr;float64 ambiguity;"

    @staticmethod
    def unpack(packet: Packet) -> Packet:
        ret = PnpResult()

        # field is shimmed!
        ret.best = packet.decodeTransform()
    
        # field is shimmed!
        ret.alt = packet.decodeTransform()
    
        # bestReprojErr is of intrinsic type float64
        ret.bestReprojErr = packet.decodeDouble()
    
        # altReprojErr is of intrinsic type float64
        ret.altReprojErr = packet.decodeDouble()
    
        # ambiguity is of intrinsic type float64
        ret.ambiguity = packet.decodeDouble()

        return ret
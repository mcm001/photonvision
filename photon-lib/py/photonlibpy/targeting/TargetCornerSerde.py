from photonlibpy.packet import Packet

from photonlibpy import *

class TargetCornerSerde:

    # Message definition md5sum. See photon_packet.adoc for details
    MESSAGE_VERSION = "22b1ff7551d10215af6fb3672fe4eda8"
    MESSAGE_FORMAT = "float64 x;float64 y;"

    @staticmethod
    def unpack(packet: Packet) -> Packet:
        ret = TargetCorner()

        # x is of intrinsic type float64
        ret.x = packet.decodeDouble()
    
        # y is of intrinsic type float64
        ret.y = packet.decodeDouble()

        return ret
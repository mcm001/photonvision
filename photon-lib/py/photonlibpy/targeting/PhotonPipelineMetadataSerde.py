from photonlibpy.packet import Packet

from photonlibpy import *


class PhotonPipelineMetadataSerde:

    # Message definition md5sum. See photon_packet.adoc for details
    MESSAGE_VERSION = "2a7039527bda14d13028a1b9282d40a2"
    MESSAGE_FORMAT = (
        "int64 sequenceID;int64 captureTimestampMicros;int64 publishTimestampMicros;"
    )

    @staticmethod
    def unpack(packet: Packet) -> Packet:
        ret = PhotonPipelineMetadata()

        # sequenceID is of intrinsic type int64
        ret.sequenceID = packet.decodeLong()

        # captureTimestampMicros is of intrinsic type int64
        ret.captureTimestampMicros = packet.decodeLong()

        # publishTimestampMicros is of intrinsic type int64
        ret.publishTimestampMicros = packet.decodeLong()

        return ret

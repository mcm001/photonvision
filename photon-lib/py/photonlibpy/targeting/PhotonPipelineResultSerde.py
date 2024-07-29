from photonlibpy.packet import Packet

from photonlibpy import *


class PhotonPipelineResultSerde:

    # Message definition md5sum. See photon_packet.adoc for details
    MESSAGE_VERSION = "cb3e1605048ba49325888eb797399fe2"
    MESSAGE_FORMAT = "PhotonPipelineMetadata metadata;PhotonTrackedTarget[?] targets;MultiTargetPNPResult? multitagResult;"

    @staticmethod
    def unpack(packet: Packet) -> Packet:
        ret = PhotonPipelineResult()

        # metadata is of non-intrinsic type PhotonPipelineMetadata
        ret.metadata = PhotonPipelineMetadata.photonStruct.unpack(packet)

        # targets is a custom VLA!
        ret.targets = packet.decodeList(PhotonTrackedTarget.photonStruct)

        # multitagResult is optional! it better not be a VLA too
        ret.multitagResult = packet.decodeOptional(MultiTargetPNPResult.photonStruct)

        return ret

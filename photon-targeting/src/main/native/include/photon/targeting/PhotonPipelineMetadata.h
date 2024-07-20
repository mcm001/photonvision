// #include "photon/struct/PhotonPipelineMetadataSerde.h"

class PhotonPipelineMetadata_PhotonStruct;

namespace photon {
class PhotonPipelineMetadata : public PhotonPipelineMetadata_PhotonStruct {
public:
    PhotonPipelineMetadata(PhotonPipelineMetadata_PhotonStruct data) : PhotonPipelineMetadata_PhotonStruct(data) {}
};
} // namespace photon

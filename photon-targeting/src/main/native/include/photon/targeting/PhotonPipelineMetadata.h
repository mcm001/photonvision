#pragma once

#include "photon/struct/PhotonPipelineMetadataStruct.h"

namespace photon {
class PhotonPipelineMetadata : public PhotonPipelineMetadata_PhotonStruct {
public:
    PhotonPipelineMetadata(PhotonPipelineMetadata_PhotonStruct data) : PhotonPipelineMetadata_PhotonStruct(data) {}
};
} // namespace photon

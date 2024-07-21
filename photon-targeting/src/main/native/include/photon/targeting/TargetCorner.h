#include "photon/struct/TargetCornerStruct.h"

namespace photon {
class TargetCorner : public TargetCorner_PhotonStruct {
public:
    TargetCorner(TargetCorner_PhotonStruct data) : TargetCorner_PhotonStruct(data) {}
};
} // namespace photon

#include "photon/serde/TargetCornerSerde.h"

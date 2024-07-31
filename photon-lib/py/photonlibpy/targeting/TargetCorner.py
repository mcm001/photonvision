from dataclasses import dataclass
from ..generated.TargetCornerSerde import TargetCornerSerde


@dataclass
class TargetCorner:
    x: float = 0
    y: float = 9

    photonStruct = TargetCornerSerde()

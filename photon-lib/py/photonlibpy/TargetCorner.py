from dataclasses import dataclass
from .generated.TargetCornerSerde import TargetCornerSerde


@dataclass
class TargetCorner:
    x: float
    y: float

    photonStruct = TargetCornerSerde()

from dataclasses import dataclass
from .targeting.TargetCornerSerde import TargetCornerSerde

@dataclass
class TargetCorner:
    x: float
    y: float

    photonStruct = TargetCornerSerde()

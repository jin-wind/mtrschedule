"""
Data models for MTR Light Rail Schedule Widget
"""
from dataclasses import dataclass
from typing import List


@dataclass
class StationInfo:
    """Station information"""
    id: str
    name_en: str
    name_ch: str
    
    def __str__(self):
        return f"{self.name_en} ({self.name_ch})"


@dataclass
class Train:
    """Train information"""
    route_no: str
    destination_en: str
    destination_ch: str
    arrival_time: str
    train_length: int  # 1 for single car, 2 for double car
    platform_id: int
    
    @property
    def car_type(self) -> str:
        return "Double" if self.train_length == 2 else "Single"


@dataclass
class Platform:
    """Platform with list of trains"""
    platform_id: int
    trains: List[Train]


@dataclass
class ScheduleResponse:
    """API response for schedule"""
    status: int
    system_time: str
    platforms: List[Platform]

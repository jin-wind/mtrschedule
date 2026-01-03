"""
ViewModel for MTR Schedule Widget (MVVM Pattern)
"""
import asyncio
from typing import Optional, Callable, List
from datetime import datetime
import pytz
from api_client import MTRApiClient
from models import ScheduleResponse, Train
from stations import get_station_by_id, get_all_stations


class ScheduleViewModel:
    """ViewModel for managing train schedule data and business logic"""
    
    def __init__(self):
        self.api_client = MTRApiClient()
        self.current_station_id: Optional[str] = None
        self.schedule_data: Optional[ScheduleResponse] = None
        self.last_updated: Optional[datetime] = None
        self.is_loading: bool = False
        
        # Callbacks for UI updates
        self.on_data_changed: Optional[Callable] = None
        self.on_loading_changed: Optional[Callable] = None
        self.on_error: Optional[Callable] = None
    
    async def load_schedule(self, station_id: str):
        """Load schedule for a given station"""
        self.current_station_id = station_id
        self._set_loading(True)
        
        try:
            schedule = await self.api_client.fetch_schedule(station_id)
            
            if schedule:
                self.schedule_data = schedule
                self.last_updated = datetime.now(pytz.timezone('Asia/Hong_Kong'))
                if self.on_data_changed:
                    self.on_data_changed()
            else:
                if self.on_error:
                    self.on_error("Failed to fetch schedule data")
        except Exception as e:
            if self.on_error:
                self.on_error(f"Error: {str(e)}")
        finally:
            self._set_loading(False)
    
    def _set_loading(self, loading: bool):
        """Set loading state and notify UI"""
        self.is_loading = loading
        if self.on_loading_changed:
            self.on_loading_changed(loading)
    
    def get_platform_trains(self, platform_id: int) -> List[Train]:
        """Get trains for a specific platform"""
        if not self.schedule_data:
            return []
        
        for platform in self.schedule_data.platforms:
            if platform.platform_id == platform_id:
                return platform.trains
        
        return []
    
    def get_platform_1_trains(self) -> List[Train]:
        """Get trains for Platform 1"""
        return self.get_platform_trains(1)
    
    def get_platform_2_trains(self) -> List[Train]:
        """Get trains for Platform 2"""
        return self.get_platform_trains(2)
    
    def get_last_updated_string(self) -> str:
        """Get formatted last updated time in HK time"""
        if not self.last_updated:
            return "Never"
        
        return self.last_updated.strftime("%Y-%m-%d %H:%M:%S HKT")
    
    def get_current_station(self):
        """Get current station info"""
        if self.current_station_id:
            return get_station_by_id(self.current_station_id)
        return None
    
    @staticmethod
    def get_all_stations():
        """Get all available stations"""
        return get_all_stations()

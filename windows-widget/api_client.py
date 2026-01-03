"""
MTR API Client for fetching train schedules
"""
import aiohttp
import asyncio
from typing import Optional
from models import ScheduleResponse, Platform, Train


class MTRApiClient:
    """Client for MTR Light Rail API"""
    
    BASE_URL = "https://rt.data.gov.hk/v1/transport/mtr/lrt/getSchedule"
    
    async def fetch_schedule(self, station_id: str) -> Optional[ScheduleResponse]:
        """
        Fetch train schedule for a given station
        
        Args:
            station_id: Station ID to fetch schedule for
            
        Returns:
            ScheduleResponse object or None if error
        """
        try:
            async with aiohttp.ClientSession() as session:
                url = f"{self.BASE_URL}?station_id={station_id}"
                async with session.get(url, timeout=aiohttp.ClientTimeout(total=30)) as response:
                    if response.status == 200:
                        data = await response.json()
                        return self._parse_response(data)
                    else:
                        print(f"Error fetching schedule: HTTP {response.status}")
                        return None
        except asyncio.TimeoutError:
            print("Request timeout")
            return None
        except Exception as e:
            print(f"Error fetching schedule: {e}")
            return None
    
    def _parse_response(self, data: dict) -> ScheduleResponse:
        """Parse API response into ScheduleResponse object"""
        platforms = []
        
        for platform_data in data.get("platform_list", []):
            platform_id = platform_data.get("platform_id")
            trains = []
            
            for route_data in platform_data.get("route_list", []):
                train = Train(
                    route_no=route_data.get("route_no", ""),
                    destination_en=route_data.get("dest_en", ""),
                    destination_ch=route_data.get("dest_ch", ""),
                    arrival_time=route_data.get("time_en", ""),
                    train_length=route_data.get("train_length", 1),
                    platform_id=platform_id
                )
                trains.append(train)
            
            platform = Platform(platform_id=platform_id, trains=trains)
            platforms.append(platform)
        
        return ScheduleResponse(
            status=data.get("status", 0),
            system_time=data.get("system_time", ""),
            platforms=platforms
        )

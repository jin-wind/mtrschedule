"""
Simple test script to verify the MTR API client works
"""
import asyncio
from api_client import MTRApiClient


async def test_api():
    """Test the API client with a known station"""
    print("Testing MTR API Client...")
    print("-" * 50)
    
    client = MTRApiClient()
    
    # Test with Siu Hong station (ID: 100)
    station_id = "100"
    print(f"\nFetching schedule for station ID: {station_id} (Siu Hong)")
    
    schedule = await client.fetch_schedule(station_id)
    
    if schedule:
        print(f"✓ Successfully fetched data!")
        print(f"  Status: {schedule.status}")
        print(f"  System Time: {schedule.system_time}")
        print(f"  Number of platforms: {len(schedule.platforms)}")
        
        for platform in schedule.platforms:
            print(f"\n  Platform {platform.platform_id}:")
            print(f"    Number of trains: {len(platform.trains)}")
            
            for i, train in enumerate(platform.trains[:3], 1):  # Show first 3 trains
                print(f"    Train {i}:")
                print(f"      Route: {train.route_no}")
                print(f"      Destination: {train.destination_en} ({train.destination_ch})")
                print(f"      Arrival: {train.arrival_time}")
                print(f"      Car Type: {train.car_type}")
        
        print("\n✓ API test completed successfully!")
        return True
    else:
        print("✗ Failed to fetch schedule")
        return False


if __name__ == "__main__":
    success = asyncio.run(test_api())
    exit(0 if success else 1)

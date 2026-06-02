"""
Mock test for demonstration purposes (when API is not accessible)
"""
from models import Train, Platform, ScheduleResponse
from stations import STATIONS, get_station_by_id
from datetime import datetime
import pytz


def test_data_models():
    """Test data models"""
    print("Testing Data Models...")
    print("-" * 50)
    
    # Test Station
    station = get_station_by_id("100")
    print(f"\n✓ Station model works:")
    print(f"  {station}")
    
    # Test Train
    train = Train(
        route_no="610",
        destination_en="Tuen Mun Ferry Pier",
        destination_ch="屯門碼頭",
        arrival_time="2 min",
        train_length=2,
        platform_id=1
    )
    print(f"\n✓ Train model works:")
    print(f"  Route: {train.route_no}")
    print(f"  Destination: {train.destination_en} ({train.destination_ch})")
    print(f"  Arrival: {train.arrival_time}")
    print(f"  Car Type: {train.car_type}")
    
    # Test Platform
    platform = Platform(platform_id=1, trains=[train])
    print(f"\n✓ Platform model works:")
    print(f"  Platform {platform.platform_id} has {len(platform.trains)} train(s)")
    
    # Test ScheduleResponse
    schedule = ScheduleResponse(
        status=1,
        system_time="2024-01-15 14:30:00",
        platforms=[platform]
    )
    print(f"\n✓ ScheduleResponse model works:")
    print(f"  Status: {schedule.status}")
    print(f"  System Time: {schedule.system_time}")
    print(f"  Platforms: {len(schedule.platforms)}")
    
    return True


def test_stations_list():
    """Test stations list"""
    print("\n\nTesting Stations List...")
    print("-" * 50)
    
    print(f"\n✓ Total stations: {len(STATIONS)}")
    print(f"\n  First 5 stations:")
    for station in STATIONS[:5]:
        print(f"    - {station}")
    
    print(f"\n  Last 5 stations:")
    for station in STATIONS[-5:]:
        print(f"    - {station}")
    
    return True


def test_viewmodel():
    """Test ViewModel functionality"""
    print("\n\nTesting ViewModel...")
    print("-" * 50)
    
    from viewmodel import ScheduleViewModel
    
    vm = ScheduleViewModel()
    
    # Test initial state
    print(f"\n✓ ViewModel initialized")
    print(f"  Current station: {vm.current_station_id}")
    print(f"  Is loading: {vm.is_loading}")
    print(f"  Last updated: {vm.get_last_updated_string()}")
    
    # Test station retrieval
    stations = vm.get_all_stations()
    print(f"\n✓ Can retrieve all stations: {len(stations)} stations")
    
    # Create mock data
    vm.schedule_data = ScheduleResponse(
        status=1,
        system_time="2024-01-15 14:30:00",
        platforms=[
            Platform(platform_id=1, trains=[
                Train("610", "Tuen Mun Ferry Pier", "屯門碼頭", "2 min", 2, 1),
                Train("614", "Yuen Long", "元朗", "5 min", 1, 1),
            ]),
            Platform(platform_id=2, trains=[
                Train("615", "Tin Shui Wai", "天水圍", "3 min", 2, 2),
            ])
        ]
    )
    vm.last_updated = datetime.now(pytz.timezone('Asia/Hong_Kong'))
    
    platform1_trains = vm.get_platform_1_trains()
    platform2_trains = vm.get_platform_2_trains()
    
    print(f"\n✓ Platform data retrieval works:")
    print(f"  Platform 1: {len(platform1_trains)} train(s)")
    print(f"  Platform 2: {len(platform2_trains)} train(s)")
    print(f"  Last updated: {vm.get_last_updated_string()}")
    
    return True


def run_all_tests():
    """Run all tests"""
    print("=" * 50)
    print("MTR Schedule Widget - Mock Tests")
    print("=" * 50)
    
    tests = [
        ("Data Models", test_data_models),
        ("Stations List", test_stations_list),
        ("ViewModel", test_viewmodel),
    ]
    
    results = []
    for name, test_func in tests:
        try:
            success = test_func()
            results.append((name, success))
        except Exception as e:
            print(f"\n✗ {name} test failed: {e}")
            results.append((name, False))
    
    print("\n\n" + "=" * 50)
    print("Test Results Summary")
    print("=" * 50)
    for name, success in results:
        status = "✓ PASS" if success else "✗ FAIL"
        print(f"{status}: {name}")
    
    all_passed = all(success for _, success in results)
    print("\n" + ("All tests passed! ✓" if all_passed else "Some tests failed! ✗"))
    print("=" * 50)
    
    return all_passed


if __name__ == "__main__":
    success = run_all_tests()
    exit(0 if success else 1)

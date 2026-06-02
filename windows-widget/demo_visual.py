"""
Demo script to show what the widget displays
This creates a visual representation in text of the widget UI
"""
from models import Train, Platform, ScheduleResponse
from stations import get_station_by_id
from datetime import datetime
import pytz


def create_demo_data():
    """Create demo schedule data"""
    platform1_trains = [
        Train("610", "Tuen Mun Ferry Pier", "屯門碼頭", "2 min", 2, 1),
        Train("614", "Yuen Long", "元朗", "5 min", 1, 1),
        Train("761P", "Tin Shui Wai", "天水圍", "8 min", 2, 1),
        Train("507", "Tin Shui Wai", "天水圍", "12 min", 1, 1),
        Train("610", "Tuen Mun Ferry Pier", "屯門碼頭", "15 min", 2, 1),
    ]
    
    platform2_trains = [
        Train("615", "Tin Shui Wai", "天水圍", "3 min", 2, 2),
        Train("751", "Yuen Long", "元朗", "7 min", 1, 2),
        Train("706", "Yau Oi South", "友愛南", "10 min", 2, 2),
        Train("614", "Tin King", "田景", "13 min", 1, 2),
    ]
    
    return ScheduleResponse(
        status=1,
        system_time="2025-11-12 14:30:25",
        platforms=[
            Platform(platform_id=1, trains=platform1_trains),
            Platform(platform_id=2, trains=platform2_trains)
        ]
    )


def print_widget_demo():
    """Print a text representation of the widget"""
    station = get_station_by_id("100")
    schedule = create_demo_data()
    last_updated = datetime.now(pytz.timezone('Asia/Hong_Kong')).strftime("%Y-%m-%d %H:%M:%S HKT")
    
    width = 90
    
    # Top bar
    print("=" * width)
    print("║  MTR Light Rail Schedule Widget" + " " * (width - 35) + "║")
    print("=" * width)
    print(f"║  Station: {station}" + " " * (width - 12 - len(str(station))) + "║")
    print(f"║  Last Updated: {last_updated}" + " " * (width - 18 - len(last_updated)) + "║")
    print("=" * width)
    
    # Platform headers
    left_header = "         Platform 1 (Red)         "
    right_header = "         Platform 2 (Blue)        "
    separator = "│"
    print(f"{left_header}{separator}{right_header}")
    print("-" * (len(left_header)) + "┼" + "-" * len(right_header))
    
    # Get trains
    platform1_trains = schedule.platforms[0].trains
    platform2_trains = schedule.platforms[1].trains
    
    # Print trains side by side
    max_trains = max(len(platform1_trains), len(platform2_trains))
    
    for i in range(max_trains):
        # Platform 1 train
        if i < len(platform1_trains):
            train = platform1_trains[i]
            print(f"┌────────────────────────────────┐{separator}┌────────────────────────────────┐")
            print(f"│ Route {train.route_no:<23}│{separator}│", end="")
        else:
            print(f"                                 {separator}┌────────────────────────────────┐")
            print(f"                                 {separator}│", end="")
        
        # Platform 2 train
        if i < len(platform2_trains):
            train2 = platform2_trains[i]
            print(f" Route {train2.route_no:<23}│")
        else:
            print("                                │")
        
        # Destination line 1
        if i < len(platform1_trains):
            train = platform1_trains[i]
            dest_line = f"→ {train.destination_en}"
            print(f"│ {dest_line:<30}│{separator}│", end="")
        else:
            print(f"                                 {separator}│", end="")
        
        if i < len(platform2_trains):
            train2 = platform2_trains[i]
            dest_line2 = f"→ {train2.destination_en}"
            print(f" {dest_line2:<30}│")
        else:
            print("                                │")
        
        # Destination line 2 (Chinese)
        if i < len(platform1_trains):
            train = platform1_trains[i]
            print(f"│    {train.destination_ch:<26}│{separator}│", end="")
        else:
            print(f"                                 {separator}│", end="")
        
        if i < len(platform2_trains):
            train2 = platform2_trains[i]
            print(f"    {train2.destination_ch:<26}│")
        else:
            print("                                │")
        
        # Time and car type
        if i < len(platform1_trains):
            train = platform1_trains[i]
            info_line = f"⏰ {train.arrival_time:<8} 🚋 {train.car_type} Car"
            print(f"│ {info_line:<30}│{separator}│", end="")
        else:
            print(f"                                 {separator}│", end="")
        
        if i < len(platform2_trains):
            train2 = platform2_trains[i]
            info_line2 = f"⏰ {train2.arrival_time:<8} 🚋 {train2.car_type} Car"
            print(f" {info_line2:<30}│")
        else:
            print("                                │")
        
        # Bottom of card
        print(f"└────────────────────────────────┘{separator}└────────────────────────────────┘")
        if i < max_trains - 1:
            print(f"                                 {separator}")
    
    print("=" * width)
    print("\n✓ Widget displays real-time MTR Light Rail schedules")
    print("✓ Auto-refreshes every 30 seconds")
    print("✓ 68 stations available in dropdown")
    print("✓ Full MVVM architecture with async API calls")


if __name__ == "__main__":
    print("\n" + "=" * 90)
    print("MTR Light Rail Schedule Widget - Visual Demo")
    print("=" * 90 + "\n")
    print_widget_demo()
    print("\n" + "=" * 90)

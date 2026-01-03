# MTR Light Rail Schedule Widget - Feature Documentation

## Overview
A Python-based Windows Desktop Widget that displays real-time MTR Light Rail train schedules with a clean, modern interface.

## Visual Layout

```
┌─────────────────────────────────────────────────────────────────────────┐
│  MTR Light Rail Schedule Widget                                    ╳ ⬜ ▬ │
├─────────────────────────────────────────────────────────────────────────┤
│  Station: [Siu Hong (兆康)                        ▼]  [↻ Refresh]      │
│  Last Updated: 2025-11-12 14:30:25 HKT                                 │
├──────────────────────────────────┬──────────────────────────────────────┤
│         Platform 1               │          Platform 2                  │
│  (Red Theme)                     │   (Blue Theme)                       │
├──────────────────────────────────┼──────────────────────────────────────┤
│ ┌────────────────────────────┐  │  ┌────────────────────────────────┐  │
│ │ ▌Route 610                 │  │  │ ▌Route 615                     │  │
│ │ ▌→ Tuen Mun Ferry Pier     │  │  │ ▌→ Tin Shui Wai               │  │
│ │ ▌   屯門碼頭                 │  │  │ ▌   天水圍                      │  │
│ │ ▌⏰ 2 min    🚋 Double Car  │  │  │ ▌⏰ 3 min    🚋 Double Car     │  │
│ └────────────────────────────┘  │  └────────────────────────────────┘  │
│                                  │                                       │
│ ┌────────────────────────────┐  │  ┌────────────────────────────────┐  │
│ │ ▌Route 614                 │  │  │ ▌Route 751                     │  │
│ │ ▌→ Yuen Long               │  │  │ ▌→ Yuen Long                   │  │
│ │ ▌   元朗                     │  │  │ ▌   元朗                        │  │
│ │ ▌⏰ 5 min    🚋 Single Car  │  │  │ ▌⏰ 7 min    🚋 Single Car     │  │
│ └────────────────────────────┘  │  └────────────────────────────────┘  │
│                                  │                                       │
│ ┌────────────────────────────┐  │  ┌────────────────────────────────┐  │
│ │ ▌Route 761P                │  │  │ ▌Route 706                     │  │
│ │ ▌→ Tin Shui Wai            │  │  │ ▌→ Yau Oi South               │  │
│ │ ▌   天水圍                   │  │  │ ▌   友愛南                      │  │
│ │ ▌⏰ 8 min    🚋 Double Car  │  │  │ ▌⏰ 10 min   🚋 Double Car     │  │
│ └────────────────────────────┘  │  └────────────────────────────────┘  │
│                                  │                                       │
│          [scroll bar]            │           [scroll bar]                │
└──────────────────────────────────┴──────────────────────────────────────┘
```

## Key Features

### 1. Station Selector
- **Dropdown menu** with all 68 MTR Light Rail stations
- **Bilingual display**: Shows both English and Chinese names
- **Instant update**: Selecting a new station immediately fetches schedules

### 2. Dual Platform Display
- **Side-by-side layout**: Platform 1 (left, red) and Platform 2 (right, blue)
- **Color-coded**: Easy visual distinction between platforms
- **Independent scrolling**: Each platform has its own scroll area

### 3. Train Information Cards
Each train is displayed in a card showing:
- **Route Number**: Large, bold display (e.g., "Route 610")
- **Destination**: Both English and Chinese names
- **Arrival Time**: Real-time countdown or scheduled time
- **Car Type**: Single or Double car indication with emoji 🚋

### 4. Auto-Refresh System
- **30-second interval**: Automatically updates schedules
- **Manual refresh**: Button available for immediate updates
- **Non-blocking**: Uses async/await for smooth operation

### 5. Loading States
- **Loading overlay**: Semi-transparent overlay with "Loading..." message
- **Skeleton screens**: Animated placeholder cards while fetching data
- **Smooth transitions**: No jarring UI changes

### 6. Time Display
- **Hong Kong Time (UTC+8)**: All timestamps shown in HKT
- **Last Updated**: Shows exact time of last successful data fetch
- **Format**: YYYY-MM-DD HH:MM:SS HKT

## Technical Implementation

### Architecture: MVVM Pattern

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐
│    View     │────▶│  ViewModel   │────▶│   Model     │
│  (main.py)  │     │(viewmodel.py)│     │(models.py)  │
│             │◀────│              │◀────│             │
└─────────────┘     └──────────────┘     └─────────────┘
      │                    │                     │
      │                    │                     │
      │                    ▼                     │
      │            ┌──────────────┐              │
      │            │ API Client   │              │
      │            │(api_client.py)│             │
      │            └──────────────┘              │
      │                    │                     │
      │                    ▼                     │
      │            ┌──────────────┐              │
      └───────────▶│  Stations    │◀─────────────┘
                   │(stations.py) │
                   └──────────────┘
```

### Components

1. **main.py** (View Layer)
   - tkinter-based UI
   - Event handlers for user interactions
   - UI rendering and updates

2. **viewmodel.py** (ViewModel Layer)
   - Business logic
   - State management
   - Callbacks for UI updates
   - Data transformation

3. **api_client.py** (Data Layer)
   - Async HTTP client using aiohttp
   - API communication with MTR servers
   - Error handling and timeouts

4. **models.py** (Data Models)
   - Train, Platform, ScheduleResponse classes
   - Data structures with type hints
   - Business entity definitions

5. **stations.py** (Static Data)
   - Complete list of 68 Light Rail stations
   - Station lookup functions
   - Bilingual station information

## API Integration

### Endpoint
```
https://rt.data.gov.hk/v1/transport/mtr/lrt/getSchedule?station_id={station_id}
```

### Response Structure
```json
{
  "status": 1,
  "system_time": "2025-11-12 14:30:00",
  "platform_list": [
    {
      "platform_id": 1,
      "route_list": [
        {
          "route_no": "610",
          "dest_en": "Tuen Mun Ferry Pier",
          "dest_ch": "屯門碼頭",
          "time_en": "2 min",
          "time_ch": "2分鐘",
          "train_length": 2,
          "arrival_departure": "A",
          "stop": 1
        }
      ]
    }
  ]
}
```

## User Experience Features

### 1. Responsive Design
- Window size: 900x600 pixels (adjustable)
- Automatic layout adjustment
- Scrollable platform areas for many trains

### 2. Visual Hierarchy
- **Primary**: Route numbers (large, bold)
- **Secondary**: Destinations (medium)
- **Tertiary**: Time and car type (smaller)

### 3. Color Scheme
- **Top Bar**: Dark theme (#2c3e50) for header
- **Platform 1**: Red accent (#e74c3c)
- **Platform 2**: Blue accent (#3498db)
- **Background**: Light gray (#ecf0f1)
- **Cards**: White with subtle shadows

### 4. Icons and Emojis
- ⏰ for arrival times
- 🚋 for train car types
- ↻ for refresh button
- → for direction indication

### 5. Error Handling
- **Network errors**: Graceful error messages
- **No trains**: "No trains scheduled" message
- **API failures**: Retry with exponential backoff

## Performance Considerations

### 1. Async Operations
- Non-blocking API calls
- Responsive UI during data fetch
- Proper thread management

### 2. Memory Management
- Efficient data structures
- Cleanup of old widgets
- No memory leaks

### 3. Network Optimization
- 30-second refresh to avoid API spam
- Request timeout: 30 seconds
- Proper connection pooling

## Installation Steps

1. **Install Python 3.8+**
2. **Install dependencies**: `pip install -r requirements.txt`
3. **Run application**: `python main.py` or use `run.bat` (Windows) / `run.sh` (Linux/Mac)

## Platform Support

- **Primary**: Windows 10/11
- **Secondary**: Linux with X11
- **Secondary**: macOS
- **Requirement**: tkinter support (usually built-in)

## Future Enhancements (Potential)

1. **Favorites**: Save favorite stations
2. **Notifications**: Alert for specific trains
3. **Dark Mode**: System theme support
4. **Multi-language**: Full language switching
5. **Widget Mode**: Always-on-top compact view
6. **Route Map**: Visual route display
7. **History**: View past schedules
8. **Offline Mode**: Cached data when offline

## Testing

### Mock Tests Available
- Data model validation
- Station list verification
- ViewModel functionality
- All pass successfully

### API Tests
- Real API connectivity test (requires network access to rt.data.gov.hk)
- Timeout handling
- Error scenarios

## Conclusion

This Windows Desktop Widget provides a clean, efficient way to view MTR Light Rail schedules with:
- **Real-time data** from official MTR API
- **Modern UI** with tkinter
- **Robust architecture** using MVVM
- **Smooth user experience** with async operations
- **Complete coverage** of all 68 Light Rail stations

Perfect for commuters, travelers, and anyone needing quick access to MTR Light Rail schedules!

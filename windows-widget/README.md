# MTR Light Rail Schedule Widget - Windows Desktop Application

A Python-based Windows desktop widget that displays real-time MTR Light Rail train schedules for Hong Kong stations.

## Features

- 📱 **Dual Platform Display**: Shows trains arriving at Platform 1 and Platform 2 side-by-side
- 🚉 **68+ Stations**: Support for all MTR Light Rail stations
- 🔄 **Auto-Refresh**: Automatically updates every 30 seconds
- ⏰ **Hong Kong Time**: Displays last updated timestamp in HKT (UTC+8)
- 🎨 **Modern UI**: Clean, intuitive interface with loading skeletons
- 📊 **Detailed Info**: Shows route number, destination, arrival time, and train car type (single/double)
- 🏗️ **MVVM Pattern**: Clean architecture with proper separation of concerns
- ⚡ **Async/Await**: Non-blocking API calls for smooth user experience

## Requirements

- Python 3.8 or higher
- Windows OS (tested on Windows 10/11)
- Internet connection for API access

## Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/jin-wind/mtrschedule.git
   cd mtrschedule/windows-widget
   ```

2. **Install dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

## Usage

Run the application:

```bash
python main.py
```

### How to Use

1. **Select a Station**: Use the dropdown menu at the top to select an MTR Light Rail station
2. **View Schedules**: The widget will display upcoming trains for Platform 1 (left) and Platform 2 (right)
3. **Manual Refresh**: Click the "↻ Refresh" button to manually update the schedule
4. **Auto-Refresh**: The widget automatically refreshes every 30 seconds

## Application Structure

```
windows-widget/
├── main.py              # Main application entry point and UI
├── viewmodel.py         # ViewModel (MVVM pattern) - business logic
├── api_client.py        # MTR API client with async/await
├── models.py            # Data models (Train, Platform, Station)
├── stations.py          # List of all 68 MTR Light Rail stations
├── requirements.txt     # Python dependencies
└── README.md           # This file
```

## Architecture

This application follows the **MVVM (Model-View-ViewModel)** pattern:

- **Model** (`models.py`): Data structures for trains, platforms, and stations
- **View** (`main.py`): UI layer using tkinter
- **ViewModel** (`viewmodel.py`): Business logic and state management

### Key Components

- **MTRApiClient**: Handles async HTTP requests to the MTR API
- **ScheduleViewModel**: Manages application state and coordinates data flow
- **MTRScheduleWidget**: Main UI window with dual platform display

## Data Source

Train schedules are fetched from the official Hong Kong Open Data API:
- **API Endpoint**: `https://rt.data.gov.hk/v1/transport/mtr/lrt/getSchedule`
- **Documentation**: [Hong Kong Transport Data Portal](https://data.gov.hk/en-data/dataset/mtr-data2-light-rail-train-service-data)

## Screenshots

The widget displays:
- Station selector dropdown at the top
- Last updated timestamp in Hong Kong Time
- Platform 1 trains on the left (red theme)
- Platform 2 trains on the right (blue theme)
- Each train card shows:
  - Route number
  - Destination (English and Chinese)
  - Arrival time
  - Car type (Single/Double)

## Troubleshooting

**Issue**: Application doesn't start
- Ensure Python 3.8+ is installed: `python --version`
- Verify all dependencies are installed: `pip install -r requirements.txt`

**Issue**: No train data displayed
- Check your internet connection
- Verify the MTR API is accessible: Visit `https://rt.data.gov.hk/v1/transport/mtr/lrt/getSchedule?station_id=1` in browser
- Some stations may have limited or no service at certain times

**Issue**: Loading takes too long
- The API request timeout is set to 30 seconds
- Network latency may affect loading times

## Development

### Adding New Features

1. **Modify UI**: Edit `main.py` - the `MTRScheduleWidget` class
2. **Change Business Logic**: Edit `viewmodel.py` - the `ScheduleViewModel` class
3. **Update API Client**: Edit `api_client.py` - the `MTRApiClient` class
4. **Add/Modify Data Models**: Edit `models.py`

### Testing

To test with a specific station:
```python
python -c "from main import *; asyncio.run(MTRApiClient().fetch_schedule('100'))"
```

## Compatibility

- **Windows**: Primary platform (tested on Windows 10/11)
- **Linux**: Should work with tkinter installed
- **macOS**: Should work with tkinter installed

## License

This project follows the original repository's license terms. Please refer to the main repository for licensing information.

## Credits

- Based on the [mtrschedule Android app](https://github.com/jin-wind/mtrschedule) by jin-wind
- MTR data provided by Hong Kong Transport Department
- Station list sourced from the original Android application

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## Support

For issues or questions:
- Open an issue on GitHub
- Refer to the original Android app repository for general MTR schedule questions

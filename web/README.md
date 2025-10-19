# MTR Light Rail Schedule - Web Version

A web-based application for viewing real-time train schedules for Hong Kong's MTR Light Rail system. This is a complete rewrite of the Android app as a Progressive Web App (PWA).

## Features

### ✅ All Features from Android App Included

1. **Station List View**
   - Display all Light Rail stations
   - Search functionality (by station name, ID)
   - Pin/favorite stations for quick access
   - Swipe/long-press to pin stations
   - Automatic sorting by pinned status
   - Real-time train schedules

2. **Station Detail View**
   - View real-time train arrivals
   - Grouped by platform
   - Shows route number, destination, ETA
   - Train type indicator (single/double car)
   - Pull-to-refresh support
   - Auto-refresh every 30 seconds

3. **Route Mode**
   - View all Light Rail routes (505, 507, 610, 614, 614P, 615, 615P, 705, 706, 751, 761P)
   - Toggle route direction (forward/reverse)
   - View all stations along a route
   - See real-time train info for each station
   - Click station to view full details

4. **Settings**
   - Language selection (繁體中文 / English)
   - Set default station
   - Reset pinned stations
   - Clear cache

5. **Additional Features**
   - Progressive Web App (PWA) - can be installed on mobile/desktop
   - Offline support via Service Worker
   - Responsive design (works on all screen sizes)
   - Material Design inspired UI
   - Local storage for preferences
   - Navigation drawer menu

## Technology Stack

- **Pure HTML5/CSS3/JavaScript** (No frameworks required)
- **Progressive Web App** (PWA)
- **Service Worker** for offline support
- **LocalStorage** for data persistence
- **Fetch API** for real-time data from MTR API

## File Structure

```
web/
├── index.html              # Main HTML file
├── styles.css              # All styling
├── manifest.json           # PWA manifest
├── sw.js                   # Service Worker
├── js/
│   ├── app.js             # Main application logic
│   ├── ui.js              # UI management
│   ├── api.js             # MTR API service
│   └── storage.js         # Local storage manager
└── data/
    ├── stations.js        # Station data
    └── routes.js          # Route data
```

## Installation & Usage

### Local Development

1. Clone the repository
2. Navigate to the web directory:
   ```bash
   cd web
   ```

3. Serve the files using any static web server:
   
   **Using Python:**
   ```bash
   python -m http.server 8000
   ```
   
   **Using Node.js (http-server):**
   ```bash
   npx http-server -p 8000
   ```
   
   **Using PHP:**
   ```bash
   php -S localhost:8000
   ```

4. Open browser and navigate to `http://localhost:8000`

### Deploy to Production

Deploy the `web` directory to any static hosting service:
- GitHub Pages
- Netlify
- Vercel
- Firebase Hosting
- Any web server (Apache, Nginx, etc.)

## API Usage

The app uses the official MTR real-time API:
```
https://rt.data.gov.hk/v2/transport/mtr/lrt/getSchedule?station_id={stationId}
```

## Browser Support

- Chrome/Edge (latest)
- Firefox (latest)
- Safari (latest)
- Mobile browsers (iOS Safari, Chrome Mobile)

## Features Comparison

| Feature | Android App | Web App |
|---------|-------------|---------|
| Station List | ✅ | ✅ |
| Search Stations | ✅ | ✅ |
| Pin/Favorite Stations | ✅ | ✅ |
| Station Details | ✅ | ✅ |
| Real-time Train Data | ✅ | ✅ |
| Route Mode | ✅ | ✅ |
| Route Direction Toggle | ✅ | ✅ |
| Settings | ✅ | ✅ |
| Language Switch | ✅ | ✅ |
| Pull to Refresh | ✅ | ✅ |
| Auto Refresh | ✅ | ✅ |
| Offline Support | Limited | ✅ |
| Install as App | N/A | ✅ (PWA) |
| Cross-platform | ❌ | ✅ |

## Future Enhancements

- Push notifications for train alerts
- Favorite routes
- Journey planner
- Nearby stations (geolocation)
- Historical data and analytics
- Dark mode theme
- More languages

## License

Same as the original Android app.

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## Credits

Based on the original Android app by jin-wind.
Data source: MTR Corporation Limited & Hong Kong Government Open Data Portal.

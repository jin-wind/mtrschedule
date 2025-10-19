# MTR Light Rail Schedule - Web Version

A web-based application for viewing MTR Light Rail train schedules in Hong Kong.

## Features

- **Real-time Schedule**: View real-time train arrivals for all MTR Light Rail stations
- **Station Selection**: Browse and select from all 68 Light Rail stations
- **Default Station**: Set a default station for quick access
- **Auto-refresh**: Manually refresh schedules to get the latest information
- **Responsive Design**: Works on desktop and mobile devices
- **Bilingual Display**: Shows station names in both Chinese and English

## Usage

### Running Locally

#### Option 1: Using a Local HTTP Server (Recommended)

```bash
# Using Python 3
cd web
python3 -m http.server 8000

# Or using Node.js
npx http-server -p 8000

# Or using PHP
php -S localhost:8000

# Then open http://localhost:8000 in your browser
```

**Note about CORS**: When running locally, you may encounter CORS (Cross-Origin Resource Sharing) errors when the app tries to fetch data from the Hong Kong government API. This is a browser security feature. The app will work properly when:
- Deployed to a web server with HTTPS
- Using a CORS proxy
- The API responses include proper CORS headers

#### Option 2: Demo Mode

To see the UI without API calls, open `demo.html` which shows the interface with sample data:

```bash
cd web
python3 -m http.server 8000
# Then open http://localhost:8000/demo.html
```

### Deploying to a Web Server

Upload the entire `web` directory to your web server. The application is completely static and requires no server-side processing.

**Deployment Options:**
- **GitHub Pages**: Push to a repository and enable GitHub Pages
- **Netlify/Vercel**: Drag and drop the `web` folder
- **Traditional Web Hosting**: Upload via FTP/SFTP to any web host
- **Firebase Hosting**: Deploy with Firebase CLI

The app works best when served over HTTPS to avoid CORS issues with the API.

## File Structure

```
web/
├── index.html          # Main HTML page
├── css/
│   └── styles.css     # Application styles
└── js/
    ├── app.js         # Main application logic
    ├── api.js         # API service for fetching schedules
    └── stations.js    # Station data
```

## API

This application uses the Hong Kong Government's open data API:
- **Endpoint**: `https://rt.data.gov.hk/v1/transport/mtr/lrt/getSchedule`
- **Documentation**: [DATA.GOV.HK](https://data.gov.hk/en-data/dataset/mtr-data2-light-rail-real-time-arriving-data)

## Browser Compatibility

- Chrome/Edge (latest)
- Firefox (latest)
- Safari (latest)
- Mobile browsers (iOS Safari, Chrome Mobile)

## Development

The web version is built with vanilla JavaScript, HTML5, and CSS3. No build process or frameworks are required.

### Key Components

1. **app.js**: Main application class that handles UI interactions and state management
2. **api.js**: API service for fetching train schedules from the MTR API
3. **stations.js**: Static data containing all Light Rail station information

## Features Comparison

| Feature | Android App | Web Version |
|---------|-------------|-------------|
| Real-time Schedules | ✅ | ✅ |
| Station Selection | ✅ | ✅ |
| Default Station | ✅ | ✅ |
| Offline Support | ✅ | ❌ |
| Push Notifications | ❌ | ❌ |
| Works on Desktop | ❌ | ✅ |

## License

Same as the main project.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

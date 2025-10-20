# MTR Schedule - Web Version

A modern web application for Hong Kong MTR Light Rail train schedules, built with Vue 3, TypeScript, and Vuetify.

## Features

- **Station Cards**: View all stations with real-time train schedules
- **Station Details**: See detailed train information including platform, route number, destination, and ETA
- **Dark Theme**: Beautiful dark theme by default with light theme option
- **Search**: Quickly find stations by name or ID
- **Pin Stations**: Pin your favorite stations to the top of the list
- **Responsive**: Works on desktop, tablet, and mobile devices
- **Real-time Data**: Fetches live train schedules from MTR API

## Technology Stack

- **Vue 3**: Progressive JavaScript framework
- **TypeScript**: Type-safe JavaScript
- **Vuetify 3**: Material Design component library with beautiful dark theme
- **Pinia**: State management
- **Vue Router**: Client-side routing
- **Axios**: HTTP client for API calls
- **Vite**: Fast build tool

## Getting Started

### Prerequisites

- Node.js 18+ and npm

### Installation

1. Navigate to the web directory:
   ```bash
   cd web
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Run development server:
   ```bash
   npm run dev
   ```

4. Open your browser and visit: `http://localhost:5173`

### Build for Production

```bash
npm run build
```

The built files will be in the `dist` directory.

### Preview Production Build

```bash
npm run preview
```

## Project Structure

```
web/
├── src/
│   ├── components/       # Vue components
│   │   ├── StationCard.vue
│   │   └── StationDetail.vue
│   ├── views/           # Page views
│   │   ├── HomeView.vue
│   │   └── SettingsView.vue
│   ├── stores/          # Pinia stores
│   │   └── stationStore.ts
│   ├── services/        # API services
│   │   └── mtrApiService.ts
│   ├── models/          # TypeScript interfaces
│   │   ├── Station.ts
│   │   ├── Train.ts
│   │   └── LightRailRoute.ts
│   ├── data/            # Static data
│   │   └── stationList.ts
│   ├── router/          # Vue Router configuration
│   │   └── index.ts
│   ├── App.vue          # Root component
│   └── main.ts          # Application entry point
├── public/              # Static assets
├── index.html           # HTML template
└── package.json         # Dependencies and scripts
```

## Features in Detail

### Station Cards
- Display station name and ID
- Show pinned status
- Click to view detailed train schedule
- Click pin icon to pin to top

### Station Details
- Real-time train information
- Grouped by platform
- Color-coded by arrival time
- Shows train car type (single/double)
- Refresh button to update data

### Dark Theme
- Beautiful dark theme by default
- Glassmorphism design effects
- Smooth animations and transitions
- Can be toggled in settings

### Settings
- Toggle between dark and light themes
- Reset pinned stations
- View app information

## API

This app uses the public Hong Kong MTR API:
- Base URL: `https://rt.data.gov.hk/v1/transport/mtr/`
- Endpoint: `lrt/getSchedule?station_id={stationId}`

## License

This project is based on the original Android app by jin-wind, rewritten as a web version.

## Credits

- Original Android app: [jin-wind/mtrschedule](https://github.com/jin-wind/mtrschedule)
- Data source: Hong Kong Transport Department Open Data

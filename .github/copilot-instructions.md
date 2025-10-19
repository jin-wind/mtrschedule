# Copilot Instructions for mtrschedule

## Overview
This repository contains two main versions of the MTR Light Rail Schedule application:

1. **Android App**: A native Android application located in the `/app` directory.
2. **Web Version**: A browser-based web application located in the `/web` directory.

Each version has its own structure, dependencies, and workflows. This document provides guidance for AI coding agents to navigate and contribute effectively to this codebase.

---

## Key Components and Architecture

### Android App (`/app`)
- **Purpose**: Provides a native mobile experience for viewing MTR Light Rail schedules.
- **Structure**:
  - `src/main/java/`: Contains the main application logic.
  - `res/`: Includes XML resources for layouts, strings, and other assets.
  - `build.gradle`: Defines dependencies and build configurations.
- **Build Workflow**:
  - Use Gradle to build the app: `./gradlew assembleDebug`.
  - Debug APKs are located in `app/build/outputs/apk/debug/`.

### Web Version (`/web`)
- **Purpose**: A static web application for desktop and mobile browsers.
- **Structure**:
  - `index.html`: Entry point for the web app.
  - `css/styles.css`: Contains styles for the application.
  - `js/app.js`: Handles UI interactions and state management.
  - `js/api.js`: Fetches real-time schedules from the MTR API.
  - `js/stations.js`: Contains static station data.
- **Development Workflow**:
  - Run a local server to test the app:
    ```bash
    cd web
    python3 -m http.server 8000
    ```
  - Open `http://localhost:8000` in your browser.
  - For demo mode, open `http://localhost:8000/demo.html`.

---

## External Dependencies

### Web Version
- **API**: The web app fetches real-time schedules from the Hong Kong Government's open data API:
  - Endpoint: `https://rt.data.gov.hk/v1/transport/mtr/lrt/getSchedule`
  - Documentation: [DATA.GOV.HK](https://data.gov.hk/en-data/dataset/mtr-data2-light-rail-real-time-arriving-data)
- **CORS Issues**: When running locally, use a CORS proxy or deploy to a server with HTTPS to avoid browser restrictions.

### Android App
- Standard Android dependencies are managed via Gradle.

---

## Project-Specific Conventions

1. **Bilingual Support**:
   - Both the Android and Web versions support Chinese and English.
   - Ensure all user-facing text is localized.

2. **Station Data**:
   - The station list is static and shared between the Android and Web versions.
   - For the web version, this data is stored in `js/stations.js`.

3. **Real-Time Updates**:
   - Both versions fetch real-time schedules from the MTR API.
   - Handle API errors gracefully and provide fallback messages to users.

---

## Contribution Guidelines

- Follow the existing structure and conventions for each version.
- Test changes thoroughly before submitting a pull request.
- For the web version, ensure compatibility with modern browsers (Chrome, Firefox, Safari).
- For the Android app, test on both emulators and physical devices.

---

## Examples

### Adding a New Feature to the Web Version
1. Update `index.html` to include any new UI elements.
2. Add logic to `js/app.js` for handling the new feature.
3. If the feature requires API calls, update `js/api.js`.
4. Test locally using a Python HTTP server.

### Debugging the Android App
1. Use Android Studio to open the `/app` directory.
2. Set breakpoints in the Java code under `src/main/java/`.
3. Run the app on an emulator or connected device.

---

For more details, refer to the main [README.md](../README.md) or the [Web README](../web/README.md).

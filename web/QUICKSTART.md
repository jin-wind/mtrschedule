# Quick Start Guide

Get the MTR Light Rail Schedule web app running in under 2 minutes!

## 🚀 Fastest Way to Run

### Option 1: Python (Recommended)
```bash
cd web
python3 -m http.server 8080
```
Open `http://localhost:8080` in your browser.

### Option 2: Node.js
```bash
cd web
npx http-server -p 8080
```
Open `http://localhost:8080` in your browser.

### Option 3: PHP
```bash
cd web
php -S localhost:8080
```
Open `http://localhost:8080` in your browser.

## 📱 Using the App

### View Stations
1. The home page shows all 65 Light Rail stations
2. Use the search box to find stations by name or ID
3. Click any station to see real-time train schedules

### Pin Favorite Stations
- **Mobile**: Long press on a station card
- **Desktop**: Right-click on a station card
- Pinned stations appear at the top of the list

### View Route Mode
1. Click the **☰** menu button (top left)
2. Select **Routes**
3. Click any route number (505, 507, 610, etc.)
4. Click the **⇄** button to reverse direction
5. Click any station to view details

### Change Settings
1. Click the **☰** menu button or **⚙️** settings icon
2. Select **Settings**
3. Choose your language (繁體中文/English)
4. Set your default station
5. Reset pinned stations or clear cache if needed

### Install as App (PWA)
#### On Mobile (Chrome/Safari):
1. Tap the browser menu (⋮ or Share icon)
2. Select "Add to Home Screen" or "Install App"
3. Confirm installation
4. App icon appears on home screen

#### On Desktop (Chrome/Edge):
1. Look for the install icon (⊕) in the address bar
2. Click "Install"
3. App opens in its own window

## 🌐 Deploy to the Web

### Fastest: Netlify Drag & Drop
1. Go to [app.netlify.com/drop](https://app.netlify.com/drop)
2. Drag the `web` folder to the upload area
3. Your site is live! 🎉

### GitHub Pages (Free)
1. Push to GitHub
2. Settings → Pages
3. Source: `main` branch, `/web` folder
4. Save
5. Site available at `https://username.github.io/mtrschedule/`

### More Options
See [DEPLOYMENT.md](DEPLOYMENT.md) for Vercel, Firebase, and traditional server setup.

## 🎯 Key Features

- ✅ **65 Stations** - Complete Light Rail network
- ✅ **11 Routes** - All major routes with direction toggle
- ✅ **Real-time Data** - Live train schedules from MTR API
- ✅ **Search** - Find stations quickly
- ✅ **Pin Favorites** - Keep important stations at top
- ✅ **Bilingual** - 繁體中文 / English
- ✅ **Offline Mode** - Works without internet (PWA)
- ✅ **Auto-refresh** - Updates every 30 seconds
- ✅ **Pull-to-refresh** - Manual refresh anytime
- ✅ **Responsive** - Works on phone, tablet, desktop

## 🆘 Troubleshooting

### Port 8080 already in use?
Change to a different port:
```bash
python3 -m http.server 3000
```

### Can't access from other devices?
Use your local IP address instead of localhost:
```bash
# Find your IP
ifconfig  # Mac/Linux
ipconfig  # Windows

# Access from other devices
http://192.168.x.x:8080
```

### API not working?
The app uses the official MTR API at `https://rt.data.gov.hk`. If it's down, train data won't load, but the app will still function for viewing station information.

### Service Worker issues?
Clear it in browser console:
```javascript
navigator.serviceWorker.getRegistrations().then(r => r.forEach(x => x.unregister()))
```

## 💡 Tips

1. **Pin Your Regular Stations** - Right-click (or long press) stations you use often
2. **Install as App** - For faster access and offline capability
3. **Bookmark Routes** - Save frequently used routes for quick access
4. **Use Search** - Type station name or ID to find it instantly
5. **Auto-refresh** - Train times update automatically every 30 seconds

## 📖 More Information

- Full documentation: [README.md](README.md)
- Deployment guide: [DEPLOYMENT.md](DEPLOYMENT.md)
- Feature comparison: [FEATURES.md](FEATURES.md)

## 🤝 Support

Having issues? Check:
1. Browser console for errors (F12)
2. Ensure you're using a modern browser
3. Check internet connection for real-time data
4. Try clearing browser cache

## 🎉 That's It!

You now have a fully functional MTR Light Rail schedule app running locally. Enjoy! 🚊

---

Made with ❤️ for Hong Kong Light Rail commuters

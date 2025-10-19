# Deployment Guide for MTR Schedule Web App

This guide covers various deployment options for the MTR Light Rail Schedule web application.

## Table of Contents
- [Local Development](#local-development)
- [GitHub Pages](#github-pages)
- [Netlify](#netlify)
- [Vercel](#vercel)
- [Firebase Hosting](#firebase-hosting)
- [Traditional Web Server](#traditional-web-server)

## Local Development

### Using Python
```bash
cd web
python3 -m http.server 8080
```
Then open `http://localhost:8080` in your browser.

### Using Node.js
```bash
cd web
npx http-server -p 8080
```

### Using PHP
```bash
cd web
php -S localhost:8080
```

## GitHub Pages

1. Push the `web` directory to your GitHub repository
2. Go to repository Settings > Pages
3. Select the branch and set the folder to `/web`
4. Click Save
5. Your app will be available at `https://[username].github.io/[repository]/`

### Using GitHub Actions (Recommended)

Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy to GitHub Pages

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Deploy to GitHub Pages
        uses: peaceiris/actions-gh-pages@v3
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: ./web
```

## Netlify

### Method 1: Drag and Drop
1. Go to [Netlify](https://www.netlify.com/)
2. Drag the `web` folder to the deploy area
3. Your site is live!

### Method 2: Git Integration
1. Connect your GitHub repository
2. Set build settings:
   - Base directory: `web`
   - Build command: (leave empty)
   - Publish directory: `.`
3. Deploy!

### Method 3: Netlify CLI
```bash
npm install -g netlify-cli
cd web
netlify deploy --prod
```

## Vercel

### Using Vercel CLI
```bash
npm install -g vercel
cd web
vercel --prod
```

### Using Git Integration
1. Import your repository at [vercel.com](https://vercel.com)
2. Set the root directory to `web`
3. Deploy!

Create `vercel.json` in the web directory:
```json
{
  "routes": [
    { "src": "/(.*)", "dest": "/$1" }
  ]
}
```

## Firebase Hosting

1. Install Firebase CLI:
```bash
npm install -g firebase-tools
```

2. Initialize Firebase in your project:
```bash
firebase login
firebase init hosting
```

3. Configure `firebase.json`:
```json
{
  "hosting": {
    "public": "web",
    "ignore": [
      "firebase.json",
      "**/.*",
      "**/node_modules/**"
    ],
    "rewrites": [
      {
        "source": "**",
        "destination": "/index.html"
      }
    ]
  }
}
```

4. Deploy:
```bash
firebase deploy --only hosting
```

## Traditional Web Server (Apache/Nginx)

### Apache

1. Copy the `web` directory to your web root (e.g., `/var/www/html/mtr-schedule`)

2. Create `.htaccess` in the web directory:
```apache
# Enable CORS for API requests
<IfModule mod_headers.c>
    Header set Access-Control-Allow-Origin "*"
</IfModule>

# Enable compression
<IfModule mod_deflate.c>
    AddOutputFilterByType DEFLATE text/html text/plain text/xml text/css text/javascript application/javascript application/json
</IfModule>

# Cache static assets
<IfModule mod_expires.c>
    ExpiresActive On
    ExpiresByType text/css "access plus 1 month"
    ExpiresByType application/javascript "access plus 1 month"
    ExpiresByType image/png "access plus 1 year"
    ExpiresByType image/jpeg "access plus 1 year"
</IfModule>
```

3. Restart Apache:
```bash
sudo systemctl restart apache2
```

### Nginx

1. Copy the `web` directory to `/var/www/mtr-schedule`

2. Create Nginx configuration `/etc/nginx/sites-available/mtr-schedule`:
```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /var/www/mtr-schedule;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Service Worker should not be cached
    location /sw.js {
        add_header Cache-Control "no-cache";
    }

    # Handle SPA routing
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Enable CORS for API
    add_header Access-Control-Allow-Origin *;
}
```

3. Enable site and restart Nginx:
```bash
sudo ln -s /etc/nginx/sites-available/mtr-schedule /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

## SSL/HTTPS Setup

For production deployments, always use HTTPS. Most hosting providers (Netlify, Vercel, Firebase) provide free SSL certificates automatically.

### Let's Encrypt (for self-hosted)
```bash
sudo apt-get update
sudo apt-get install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
```

## Environment-Specific Configuration

If you need different API endpoints for different environments, you can modify the API base URL in `js/api.js`:

```javascript
const baseUrl = window.location.hostname === 'localhost' 
    ? 'https://rt.data.gov.hk/v2/transport/mtr'
    : 'https://rt.data.gov.hk/v2/transport/mtr';
```

## Performance Optimization

1. **Enable Compression**: All hosting providers should have gzip/brotli compression enabled
2. **CDN**: Use a CDN for static assets (Cloudflare, etc.)
3. **Caching**: Configure proper cache headers for static assets
4. **Service Worker**: Already included for offline support
5. **Minification**: Consider minifying CSS/JS for production

## Monitoring

Consider adding analytics and error tracking:

- Google Analytics
- Plausible Analytics
- Sentry for error tracking
- Lighthouse CI for performance monitoring

## Post-Deployment Checklist

- [ ] Test all features in production
- [ ] Verify PWA installation works
- [ ] Check mobile responsiveness
- [ ] Test offline functionality
- [ ] Verify API calls work correctly
- [ ] Check HTTPS is enabled
- [ ] Test on multiple browsers
- [ ] Verify search functionality
- [ ] Test route mode with all routes
- [ ] Check settings persistence

## Troubleshooting

### API CORS Issues
If you encounter CORS errors, the MTR API might be blocking requests. Consider:
- Using a CORS proxy for development
- Implementing server-side API proxy for production

### Service Worker Not Updating
Clear the service worker cache:
```javascript
// In browser console
navigator.serviceWorker.getRegistrations().then(registrations => {
    registrations.forEach(registration => registration.unregister());
});
```

### PWA Not Installing
- Ensure HTTPS is enabled
- Check manifest.json is accessible
- Verify service worker registers successfully
- Check browser console for errors

## Support

For issues or questions:
- Create an issue on GitHub
- Check the main README.md for feature documentation
- Review browser console for error messages

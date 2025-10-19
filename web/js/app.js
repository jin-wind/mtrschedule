// Main Application
class MtrScheduleApp {
    constructor() {
        this.initialized = false;
        this.serviceWorkerRegistration = null;
    }

    async init() {
        if (this.initialized) return;

        console.log('Initializing MTR Schedule App...');

        // Check for service worker support
        if ('serviceWorker' in navigator) {
            try {
                this.serviceWorkerRegistration = await navigator.serviceWorker.register('sw.js');
                console.log('Service Worker registered successfully');
            } catch (error) {
                console.warn('Service Worker registration failed:', error);
            }
        }

        // Load default station on startup
        const defaultStationId = storageManager.getDefaultStation();
        if (defaultStationId) {
            // Show station list first, then load default station
            uiManager.showStationList();
            
            // Optional: auto-open default station after a short delay
            // setTimeout(() => {
            //     uiManager.showStationDetail(defaultStationId);
            // }, 500);
        } else {
            uiManager.showStationList();
        }

        // Set up auto-refresh for station details (every 30 seconds)
        setInterval(() => {
            if (uiManager.currentView === 'stationDetail' && uiManager.currentStation) {
                uiManager.refreshStationDetail();
            }
        }, 30000);

        // Check API status
        this.checkApiStatus();

        this.initialized = true;
        console.log('MTR Schedule App initialized successfully');
    }

    async checkApiStatus() {
        try {
            const isAvailable = await mtrApi.checkApiStatus();
            if (!isAvailable) {
                console.warn('MTR API may be unavailable');
            }
        } catch (error) {
            console.error('Error checking API status:', error);
        }
    }

    // Install prompt for PWA
    setupInstallPrompt() {
        let deferredPrompt;

        window.addEventListener('beforeinstallprompt', (e) => {
            e.preventDefault();
            deferredPrompt = e;
            
            // Show install button if needed
            const installBtn = document.getElementById('install-btn');
            if (installBtn) {
                installBtn.style.display = 'block';
                installBtn.addEventListener('click', async () => {
                    if (deferredPrompt) {
                        deferredPrompt.prompt();
                        const { outcome } = await deferredPrompt.userChoice;
                        console.log(`User response to install prompt: ${outcome}`);
                        deferredPrompt = null;
                        installBtn.style.display = 'none';
                    }
                });
            }
        });

        window.addEventListener('appinstalled', () => {
            console.log('PWA installed successfully');
            deferredPrompt = null;
        });
    }
}

// Initialize app when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    const app = new MtrScheduleApp();
    app.init();
    app.setupInstallPrompt();
});

// Handle online/offline events
window.addEventListener('online', () => {
    console.log('App is online');
    uiManager.showError('');
});

window.addEventListener('offline', () => {
    console.log('App is offline');
    uiManager.showError('You are offline. Some features may not be available.');
});

// UI Manager
class UIManager {
    constructor() {
        this.currentView = 'station-list';
        this.currentStation = null;
        this.currentRoute = null;
        this.isRouteReversed = false;
        this.allStations = [];
        this.filteredStations = [];
        
        this.initializeElements();
        this.attachEventListeners();
    }

    initializeElements() {
        // Views
        this.views = {
            stationList: document.getElementById('station-list-view'),
            stationDetail: document.getElementById('station-detail-view'),
            routeMode: document.getElementById('route-mode-view'),
            settings: document.getElementById('settings-view')
        };

        // Drawer
        this.drawer = document.getElementById('drawer');
        this.drawerOverlay = document.getElementById('drawer-overlay');

        // Toolbar
        this.toolbarTitle = document.getElementById('toolbar-title');
        this.menuBtn = document.getElementById('menu-btn');
        this.settingsBtn = document.getElementById('settings-btn');

        // Station List View
        this.searchInput = document.getElementById('search-input');
        this.stationList = document.getElementById('station-list');
        this.timestamp = document.getElementById('timestamp');
        this.loadingIndicator = document.getElementById('loading-indicator');
        this.errorMessage = document.getElementById('error-message');

        // Station Detail View
        this.backBtn = document.getElementById('back-btn');
        this.stationName = document.getElementById('station-name');
        this.stationId = document.getElementById('station-id');
        this.trainList = document.getElementById('train-list');
        this.noTrainsMessage = document.getElementById('no-trains-message');
        this.pullToRefresh = document.getElementById('pull-to-refresh');

        // Route Mode View
        this.routeList = document.getElementById('route-list');
        this.routeDetailPlaceholder = document.getElementById('route-detail-placeholder');
        this.routeStationsContainer = document.getElementById('route-stations-container');
        this.routeTitle = document.getElementById('route-title');
        this.routeStationsList = document.getElementById('route-stations-list');
        this.routeLoading = document.getElementById('route-loading');

        // Settings View
        this.languageSelect = document.getElementById('language-select');
        this.defaultStationSelect = document.getElementById('default-station-select');
        this.resetPinnedBtn = document.getElementById('reset-pinned-btn');
        this.clearCacheBtn = document.getElementById('clear-cache-btn');
    }

    attachEventListeners() {
        // Menu and drawer
        this.menuBtn.addEventListener('click', () => this.toggleDrawer());
        this.drawerOverlay.addEventListener('click', () => this.closeDrawer());
        
        // Drawer navigation
        document.querySelectorAll('.drawer-menu li').forEach(item => {
            item.addEventListener('click', (e) => {
                const nav = e.currentTarget.dataset.nav;
                this.handleNavigation(nav);
            });
        });

        // Settings button
        this.settingsBtn.addEventListener('click', () => this.showSettings());

        // Search
        this.searchInput.addEventListener('input', (e) => {
            this.filterStations(e.target.value);
        });

        // Back button
        this.backBtn.addEventListener('click', () => this.showStationList());

        // Settings
        this.languageSelect.addEventListener('change', (e) => {
            storageManager.setLanguage(e.target.value);
            this.updateLanguage();
        });

        this.defaultStationSelect.addEventListener('change', (e) => {
            storageManager.setDefaultStation(e.target.value);
        });

        this.resetPinnedBtn.addEventListener('click', () => {
            if (confirm('Reset all pinned stations?')) {
                storageManager.resetPinnedStations();
                this.loadStations();
            }
        });

        this.clearCacheBtn.addEventListener('click', () => {
            if (confirm('Clear all cached data?')) {
                storageManager.clearCache();
                alert('Cache cleared successfully');
            }
        });

        // Pull to refresh (simple implementation)
        let touchStartY = 0;
        this.views.stationDetail.addEventListener('touchstart', (e) => {
            touchStartY = e.touches[0].clientY;
        });

        this.views.stationDetail.addEventListener('touchmove', (e) => {
            const touchY = e.touches[0].clientY;
            const touchDiff = touchY - touchStartY;
            
            if (touchDiff > 100 && this.views.stationDetail.scrollTop === 0) {
                this.refreshStationDetail();
            }
        });
    }

    toggleDrawer() {
        this.drawer.classList.toggle('active');
        this.drawerOverlay.classList.toggle('active');
    }

    closeDrawer() {
        this.drawer.classList.remove('active');
        this.drawerOverlay.classList.remove('active');
    }

    handleNavigation(nav) {
        this.closeDrawer();
        
        switch(nav) {
            case 'home':
                this.showStationList();
                break;
            case 'routes':
                this.showRouteMode();
                break;
            case 'settings':
                this.showSettings();
                break;
        }
    }

    showView(viewName) {
        Object.values(this.views).forEach(view => view.classList.remove('active'));
        
        if (this.views[viewName]) {
            this.views[viewName].classList.add('active');
            this.currentView = viewName;
        }
    }

    showLoading(show = true) {
        if (show) {
            this.loadingIndicator.classList.remove('hidden');
        } else {
            this.loadingIndicator.classList.add('hidden');
        }
    }

    showError(message) {
        if (message) {
            this.errorMessage.textContent = message;
            this.errorMessage.classList.remove('hidden');
        } else {
            this.errorMessage.classList.add('hidden');
        }
    }

    updateTimestamp() {
        const now = new Date();
        const formatted = now.toISOString().replace('T', ' ').substring(0, 19);
        this.timestamp.textContent = `Last updated: ${formatted} UTC`;
    }

    // Station List View
    showStationList() {
        this.showView('stationList');
        this.toolbarTitle.textContent = 'MTR Light Rail';
        this.loadStations();
    }

    async loadStations() {
        this.showLoading(true);
        this.showError('');
        
        try {
            // Get all stations
            let stations = getAllStations();
            
            // Mark pinned stations
            const pinnedIds = storageManager.getPinnedStations();
            stations = stations.map(s => ({
                ...s,
                isPinned: pinnedIds.includes(s.stationId)
            }));

            // Sort by saved order
            stations = storageManager.sortStationsByOrder(stations);

            this.allStations = stations;
            this.filteredStations = stations;
            
            this.renderStationList(stations);
            this.updateTimestamp();
        } catch (error) {
            this.showError('Failed to load stations: ' + error.message);
        } finally {
            this.showLoading(false);
        }
    }

    renderStationList(stations) {
        this.stationList.innerHTML = '';
        
        stations.forEach(station => {
            const card = this.createStationCard(station);
            this.stationList.appendChild(card);
        });
    }

    createStationCard(station) {
        const card = document.createElement('div');
        card.className = 'station-card';
        if (station.isPinned) {
            card.classList.add('pinned');
        }

        const lang = storageManager.getLanguage();
        const stationName = lang === 'en' ? station.stationNameEn : station.stationName;

        card.innerHTML = `
            <div class="station-card-header">
                <div class="station-name">${stationName}</div>
                ${station.isPinned ? '<div class="pin-indicator">📌</div>' : ''}
            </div>
            <div class="station-code">Station ID: ${station.stationId}</div>
            <div class="swipe-hint">Tap to view • Long press to pin</div>
        `;

        card.addEventListener('click', () => this.showStationDetail(station.stationId));
        
        // Long press to pin
        let pressTimer;
        card.addEventListener('touchstart', (e) => {
            pressTimer = setTimeout(() => {
                e.preventDefault();
                this.togglePinStation(station.stationId);
            }, 500);
        });
        
        card.addEventListener('touchend', () => {
            clearTimeout(pressTimer);
        });

        card.addEventListener('touchmove', () => {
            clearTimeout(pressTimer);
        });

        // Desktop right-click to pin
        card.addEventListener('contextmenu', (e) => {
            e.preventDefault();
            this.togglePinStation(station.stationId);
        });

        return card;
    }

    togglePinStation(stationId) {
        const isPinned = storageManager.togglePinnedStation(stationId);
        
        // Move to top if pinned
        if (isPinned) {
            this.allStations = storageManager.topStationAndSaveOrder(this.allStations, stationId);
        }
        
        // Update display
        this.allStations = this.allStations.map(s => ({
            ...s,
            isPinned: storageManager.isPinned(s.stationId)
        }));
        
        this.filterStations(this.searchInput.value);
    }

    filterStations(query) {
        if (!query) {
            this.filteredStations = this.allStations;
        } else {
            const lowerQuery = query.toLowerCase();
            this.filteredStations = this.allStations.filter(station => 
                station.stationName.toLowerCase().includes(lowerQuery) ||
                station.stationNameEn.toLowerCase().includes(lowerQuery) ||
                station.stationId.includes(lowerQuery)
            );
        }
        
        this.renderStationList(this.filteredStations);
    }

    // Station Detail View
    async showStationDetail(stationId) {
        this.currentStation = stationId;
        this.showView('stationDetail');
        this.toolbarTitle.textContent = 'Station Details';
        
        const station = getStationById(stationId);
        if (!station) return;

        const lang = storageManager.getLanguage();
        const stationName = lang === 'en' ? station.stationNameEn : station.stationName;
        
        this.stationName.textContent = stationName;
        this.stationId.textContent = `Station ID: ${stationId}`;
        
        await this.loadStationSchedule(stationId);
    }

    async loadStationSchedule(stationId) {
        this.showLoading(true);
        this.trainList.innerHTML = '';
        this.noTrainsMessage.classList.add('hidden');
        
        try {
            const stationData = await mtrApi.getNextTrains(stationId);
            this.renderTrainList(stationData.nextTrains);
            this.updateTimestamp();
        } catch (error) {
            this.showError('Failed to load train schedule: ' + error.message);
        } finally {
            this.showLoading(false);
        }
    }

    renderTrainList(trains) {
        if (!trains || trains.length === 0) {
            this.noTrainsMessage.classList.remove('hidden');
            return;
        }

        // Group trains by platform
        const platformGroups = {};
        trains.forEach(train => {
            if (!platformGroups[train.platform]) {
                platformGroups[train.platform] = [];
            }
            platformGroups[train.platform].push(train);
        });

        // Render each platform group
        Object.keys(platformGroups).sort().forEach(platform => {
            const group = this.createPlatformGroup(platform, platformGroups[platform]);
            this.trainList.appendChild(group);
        });
    }

    createPlatformGroup(platform, trains) {
        const group = document.createElement('div');
        group.className = 'platform-group';

        const header = document.createElement('div');
        header.className = 'platform-header';
        header.textContent = `Platform ${platform}`;
        group.appendChild(header);

        trains.forEach(train => {
            const item = this.createTrainItem(train);
            group.appendChild(item);
        });

        return group;
    }

    createTrainItem(train) {
        const item = document.createElement('div');
        item.className = 'train-item';

        const carType = train.isDoubleCar ? '雙卡' : '單卡';
        
        item.innerHTML = `
            <div class="train-info">
                <div class="train-route">Route ${train.routeNumber}</div>
                <div class="train-destination">To ${train.destination}</div>
                <div class="train-car-type">${carType}</div>
            </div>
            <div class="train-eta">
                <div class="eta-time">${train.eta}</div>
                <div class="eta-label">ETA</div>
            </div>
        `;

        return item;
    }

    async refreshStationDetail() {
        if (this.currentStation) {
            await this.loadStationSchedule(this.currentStation);
        }
    }

    // Route Mode View
    showRouteMode() {
        this.showView('routeMode');
        this.toolbarTitle.textContent = 'Route Mode';
        this.renderRouteList();
    }

    renderRouteList() {
        this.routeList.innerHTML = '';
        const routes = getAllRouteNumbers();

        routes.forEach(routeNumber => {
            const item = this.createRouteItem(routeNumber);
            this.routeList.appendChild(item);
        });
    }

    createRouteItem(routeNumber) {
        const item = document.createElement('div');
        item.className = 'route-item';
        item.dataset.route = routeNumber;

        item.innerHTML = `
            <div class="route-number">${routeNumber}</div>
            <button class="route-direction-toggle" title="Toggle direction">⇄</button>
        `;

        const toggleBtn = item.querySelector('.route-direction-toggle');
        toggleBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            this.isRouteReversed = !this.isRouteReversed;
            this.loadRouteStations(routeNumber);
        });

        item.addEventListener('click', () => {
            document.querySelectorAll('.route-item').forEach(i => i.classList.remove('active'));
            item.classList.add('active');
            this.isRouteReversed = false;
            this.loadRouteStations(routeNumber);
        });

        return item;
    }

    async loadRouteStations(routeNumber) {
        this.currentRoute = routeNumber;
        this.routeDetailPlaceholder.classList.add('hidden');
        this.routeStationsContainer.classList.remove('hidden');
        this.routeLoading.classList.remove('hidden');

        const route = getRouteByNumber(routeNumber);
        if (!route) return;

        // Set title based on direction
        const lang = storageManager.getLanguage();
        let title;
        if (this.isRouteReversed) {
            title = lang === 'en' 
                ? `${route.endStationEn} → ${route.startStationEn}`
                : `${route.endStation} → ${route.startStation}`;
        } else {
            title = lang === 'en'
                ? `${route.startStationEn} → ${route.endStationEn}`
                : `${route.startStation} → ${route.endStation}`;
        }
        this.routeTitle.textContent = title;

        // Get stations for direction
        const stationIds = getRouteStationsForDirection(routeNumber, this.isRouteReversed);

        try {
            // Load station data
            const stations = await mtrApi.getMultipleStationSchedules(stationIds);
            
            // Sort stations in route order
            const orderedStations = stationIds
                .map(id => stations.find(s => s.stationId === id))
                .filter(s => s !== undefined);

            this.renderRouteStations(orderedStations);
        } catch (error) {
            this.showError('Failed to load route stations: ' + error.message);
        } finally {
            this.routeLoading.classList.add('hidden');
        }
    }

    renderRouteStations(stations) {
        this.routeStationsList.innerHTML = '';

        stations.forEach(station => {
            const item = this.createRouteStationItem(station);
            this.routeStationsList.appendChild(item);
        });
    }

    createRouteStationItem(station) {
        const item = document.createElement('div');
        item.className = 'route-station-item';

        const lang = storageManager.getLanguage();
        const stationName = lang === 'en' ? station.stationNameEn : station.stationName;

        const trainInfo = station.nextTrains.slice(0, 3).map(train => 
            `<span class="route-train-badge">${train.routeNumber} - ${train.eta}</span>`
        ).join('');

        item.innerHTML = `
            <div class="route-station-name">${stationName}</div>
            <div class="route-train-info">${trainInfo || 'No trains'}</div>
        `;

        item.addEventListener('click', () => {
            this.showStationDetail(station.stationId);
        });

        return item;
    }

    // Settings View
    showSettings() {
        this.showView('settings');
        this.toolbarTitle.textContent = 'Settings';
        this.loadSettings();
    }

    loadSettings() {
        // Load language
        this.languageSelect.value = storageManager.getLanguage();

        // Load default station
        this.defaultStationSelect.innerHTML = '<option value="">None</option>';
        const stations = getAllStations();
        stations.forEach(station => {
            const option = document.createElement('option');
            option.value = station.stationId;
            option.textContent = `${station.stationName} (${station.stationId})`;
            this.defaultStationSelect.appendChild(option);
        });
        this.defaultStationSelect.value = storageManager.getDefaultStation();
    }

    updateLanguage() {
        // Reload current view to update language
        if (this.currentView === 'stationList') {
            this.loadStations();
        } else if (this.currentView === 'stationDetail' && this.currentStation) {
            this.showStationDetail(this.currentStation);
        } else if (this.currentView === 'routeMode' && this.currentRoute) {
            this.loadRouteStations(this.currentRoute);
        }
    }
}

// Export UI manager instance
const uiManager = new UIManager();

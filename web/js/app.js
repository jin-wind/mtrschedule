// Main Application
class MtrScheduleApp {
    constructor() {
    this.currentView = 'list'; // 'list' or 'detail'
    this.currentStation = null;
    this.defaultStationId = localStorage.getItem('defaultStation') || '240';
    const savedMode = localStorage.getItem('displayMode');
    this.detailMode = savedMode === 'train' ? 'train' : 'route';
        
        this.initElements();
        this.initEventListeners();
        this.loadInitialView();
    }
    
    initElements() {
        // Views
        this.stationListView = document.getElementById('stationListView');
        this.stationDetailView = document.getElementById('stationDetailView');
        
        // Station list
        this.stationsList = document.getElementById('stationsList');
        
        // Station detail
        this.stationName = document.getElementById('stationName');
        this.stationCode = document.getElementById('stationCode');
        this.trainsList = document.getElementById('trainsList');
        this.noTrainsText = document.getElementById('noTrainsText');
        this.trainModeContainer = document.getElementById('trainModeContainer');
        this.routeModeContainer = document.getElementById('routeModeContainer');
    this.routeGroupsList = document.getElementById('routeGroupsList');
    this.noRoutesText = document.getElementById('noRoutesText');
    this.displayModeSelect = document.getElementById('displayMode');
        
        // UI elements
        this.timestampText = document.getElementById('timestampText');
        this.loadingIndicator = document.getElementById('loadingIndicator');
        this.errorText = document.getElementById('errorText');
        
        // Buttons
    this.refreshBtn = document.getElementById('refreshBtn');
    this.settingsBtn = document.getElementById('settingsBtn');
    this.backBtn = document.getElementById('backBtn');
        
        // Settings modal
        this.settingsModal = document.getElementById('settingsModal');
        this.closeModalBtn = document.getElementById('closeModalBtn');
        this.defaultStationSelect = document.getElementById('defaultStation');
        this.saveSettingsBtn = document.getElementById('saveSettingsBtn');
    }
    
    initEventListeners() {
        this.refreshBtn.addEventListener('click', () => this.handleRefresh());
        this.settingsBtn.addEventListener('click', () => this.openSettings());
        this.backBtn.addEventListener('click', () => this.showStationList());
        this.closeModalBtn.addEventListener('click', () => this.closeSettings());
    this.saveSettingsBtn.addEventListener('click', () => this.saveSettings());
        
        // Close modal on outside click
        this.settingsModal.addEventListener('click', (e) => {
            if (e.target === this.settingsModal) {
                this.closeSettings();
            }
        });
    }
    
    async loadInitialView() {
        this.updateTimestamp();
        
        // Load the default station directly
        if (this.defaultStationId) {
            await this.loadStationDetail(this.defaultStationId);
        } else {
            this.renderStationsList();
        }
    }
    
    renderStationsList() {
        this.stationsList.innerHTML = '';
        const stations = getAllStations();
        
        stations.forEach(station => {
            const stationItem = document.createElement('div');
            stationItem.className = 'station-item';
            stationItem.innerHTML = `
                <div>
                    <div class="station-item-name">${station.nameCh}</div>
                    <div class="station-item-code">${station.nameEn}</div>
                </div>
                <div class="station-item-code">ID: ${station.id}</div>
            `;
            stationItem.addEventListener('click', () => this.loadStationDetail(station.id));
            this.stationsList.appendChild(stationItem);
        });
        
        this.showStationList();
    }
    
    async loadStationDetail(stationId) {
        this.showLoading(true);
        this.hideError();
        
        try {
            const data = await fetchStationSchedule(stationId);
            this.currentStation = data;
            this.renderStationDetail(data);
            this.showStationDetail();
            this.updateTimestamp();
        } catch (error) {
            this.showError(`Failed to load station data: ${error.message}`);
        } finally {
            this.showLoading(false);
        }
    }
    
    renderStationDetail(data) {
        this.stationName.textContent = data.stationName;
        this.stationCode.textContent = `Station Code: ${data.stationCode}`;
        this.renderCurrentDetailMode(data);
    }
    
    formatTimeToArrival(minutes) {
        if (minutes === 0) {
            return 'Arriving';
        } else if (minutes === 1) {
            return '1 minute';
        } else {
            return `${minutes} minutes`;
        }
    }

    formatRouteMinutesLabel(minutes) {
        if (typeof minutes !== 'number' || Number.isNaN(minutes) || minutes < 0) {
            return '--';
        }
        if (minutes === 0) {
            return '即將到站';
        }
        return `${minutes} 分鐘`;
    }
    
    showStationList() {
        this.currentView = 'list';
        this.stationListView.classList.remove('hidden');
        this.stationDetailView.classList.add('hidden');
        
        if (this.stationsList.children.length === 0) {
            this.renderStationsList();
        }
    }
    
    showStationDetail() {
        this.currentView = 'detail';
        this.stationListView.classList.add('hidden');
        this.stationDetailView.classList.remove('hidden');
    }
    
    async handleRefresh() {
        if (this.currentView === 'detail' && this.currentStation) {
            await this.loadStationDetail(this.currentStation.stationId);
        } else {
            this.updateTimestamp();
        }
    }
    
    updateTimestamp() {
        this.timestampText.textContent = `Last updated: ${getCurrentTimestamp()}`;
    }
    
    showLoading(show) {
        if (show) {
            this.loadingIndicator.classList.remove('hidden');
        } else {
            this.loadingIndicator.classList.add('hidden');
        }
    }
    
    showError(message) {
        this.errorText.textContent = message;
        this.errorText.classList.remove('hidden');
    }
    
    hideError() {
        this.errorText.classList.add('hidden');
    }
    
    openSettings() {
        // Populate station select
        this.defaultStationSelect.innerHTML = '<option value="">Select a station...</option>';
        const stations = getAllStations();
        
        stations.forEach(station => {
            const option = document.createElement('option');
            option.value = station.id;
            option.textContent = `${station.nameCh} - ${station.nameEn}`;
            if (station.id === this.defaultStationId) {
                option.selected = true;
            }
            this.defaultStationSelect.appendChild(option);
        });
        
        if (this.displayModeSelect) {
            this.displayModeSelect.value = this.detailMode;
        }

        this.settingsModal.classList.remove('hidden');
    }
    
    closeSettings() {
        this.settingsModal.classList.add('hidden');
    }
    
    saveSettings() {
        const selectedStation = this.defaultStationSelect.value;
        const selectedMode = this.displayModeSelect ? this.displayModeSelect.value : this.detailMode;
        
        if (selectedStation) {
            this.defaultStationId = selectedStation;
            localStorage.setItem('defaultStation', selectedStation);
            // Reload with new default station
            this.loadStationDetail(selectedStation);
        }

        if (selectedMode === 'train' || selectedMode === 'route') {
            this.detailMode = selectedMode;
            localStorage.setItem('displayMode', selectedMode);
            if (this.currentStation) {
                this.renderCurrentDetailMode(this.currentStation);
            }
        }

        this.closeSettings();
    }

    renderCurrentDetailMode(data) {
        if (this.detailMode === 'route') {
            this.renderRouteGroups(data.trains);
        } else {
            this.renderTrainList(data.trains);
        }
    }

    renderTrainList(trains) {
        this.trainModeContainer.classList.remove('hidden');
        this.routeModeContainer.classList.add('hidden');
        this.trainsList.innerHTML = '';
        this.routeGroupsList.innerHTML = '';
        this.noRoutesText.classList.add('hidden');

        if (trains.length === 0) {
            this.noTrainsText.classList.remove('hidden');
            this.trainsList.classList.add('hidden');
            return;
        }

        this.noTrainsText.classList.add('hidden');
        this.trainsList.classList.remove('hidden');

        trains.forEach(train => {
            const trainItem = document.createElement('div');
            trainItem.className = 'train-item';
            trainItem.innerHTML = `
                <div class="train-route">${train.routeNumber}</div>
                <div class="train-info">
                    <div class="train-destination">${train.destination}</div>
                    <div class="train-platform">Platform ${train.platform}</div>
                </div>
                <div class="train-eta">
                    <div class="train-time">${train.eta}</div>
                    <div class="train-minutes">${this.formatTimeToArrival(train.timeToArrival)}</div>
                </div>
            `;
            this.trainsList.appendChild(trainItem);
        });
    }

    renderRouteGroups(trains) {
        this.routeModeContainer.classList.remove('hidden');
        this.trainModeContainer.classList.add('hidden');
        this.routeGroupsList.innerHTML = '';
        this.noTrainsText.classList.add('hidden');

        if (trains.length === 0) {
            this.noRoutesText.classList.remove('hidden');
            this.routeGroupsList.classList.add('hidden');
            return;
        }

        this.noRoutesText.classList.add('hidden');
        this.routeGroupsList.classList.remove('hidden');

        const platformMap = new Map();
        trains.forEach(train => {
            if (!platformMap.has(train.platform)) {
                platformMap.set(train.platform, []);
            }
            platformMap.get(train.platform).push(train);
        });

        const sortedPlatforms = Array.from(platformMap.keys()).sort((a, b) => (
            String(a).localeCompare(String(b), undefined, { numeric: true, sensitivity: 'base' })
        ));

        sortedPlatforms.forEach(platformId => {
            const platformElement = document.createElement('div');
            platformElement.className = 'platform-group';

            const platformTrains = platformMap.get(platformId).sort((a, b) => a.timeToArrival - b.timeToArrival);
            const directionLabel = this.buildPlatformDirection(platformTrains);

            const platformHeader = document.createElement('div');
            platformHeader.className = 'platform-header';
            platformHeader.innerHTML = `
                <div class="platform-title">月臺 ${platformId}</div>
                <div class="platform-direction">${directionLabel}</div>
            `;
            platformElement.appendChild(platformHeader);

            const trainsGrid = document.createElement('div');
            trainsGrid.className = 'trains-grid';

            platformTrains.forEach(train => {
                const minutesLabel = this.formatRouteMinutesLabel(train.timeToArrival);
                const trainCard = document.createElement('div');
                trainCard.className = 'train-card';
                trainCard.innerHTML = `
                    <div class="train-card-route">${train.routeNumber || '--'}</div>
                    <div class="train-card-minutes">${minutesLabel}</div>
                `;
                trainsGrid.appendChild(trainCard);
            });

            platformElement.appendChild(trainsGrid);
            this.routeGroupsList.appendChild(platformElement);
        });
    }

    buildPlatformDirection(trains) {
        if (!Array.isArray(trains) || trains.length === 0) {
            return '暫無班次資料';
        }

        const seen = new Set();
        const destinations = [];

        trains.forEach(train => {
            const destination = (train.destination || '').trim();
            if (!destination) {
                return;
            }
            if (!seen.has(destination)) {
                seen.add(destination);
                destinations.push(destination);
            }
        });

        if (destinations.length === 0) {
            return '目的地資料暫缺';
        }

        const maxDestinations = 3;
        const primaryList = destinations.slice(0, maxDestinations).join(' / ');
        const suffix = destinations.length > maxDestinations ? ' …' : '';
        return `往 ${primaryList}${suffix}`;
    }
}

// Initialize app when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    new MtrScheduleApp();
});

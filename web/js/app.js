// Main Application
class MtrScheduleApp {
    constructor() {
        this.currentView = 'list'; // 'list' or 'detail'
        this.currentStation = null;
        this.defaultStationId = localStorage.getItem('defaultStation') || '240';
        
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
        
        this.trainsList.innerHTML = '';
        
        if (data.trains.length === 0) {
            this.noTrainsText.classList.remove('hidden');
            this.trainsList.classList.add('hidden');
        } else {
            this.noTrainsText.classList.add('hidden');
            this.trainsList.classList.remove('hidden');
            
            data.trains.forEach(train => {
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
        
        this.settingsModal.classList.remove('hidden');
    }
    
    closeSettings() {
        this.settingsModal.classList.add('hidden');
    }
    
    saveSettings() {
        const selectedStation = this.defaultStationSelect.value;
        
        if (selectedStation) {
            this.defaultStationId = selectedStation;
            localStorage.setItem('defaultStation', selectedStation);
            this.closeSettings();
            
            // Reload with new default station
            this.loadStationDetail(selectedStation);
        }
    }
}

// Initialize app when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    new MtrScheduleApp();
});

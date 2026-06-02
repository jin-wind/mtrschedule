// Configuration
const API_URL = 'https://rt.data.gov.hk/v1/transport/mtr/lrt/getSchedule';
const REFRESH_INTERVAL = 30000; // 30 seconds

// State
let currentStationId = '100'; // Default: Siu Hong
let refreshTimer = null;
let isPinned = false;

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    initStationSelector();
    initEventListeners();
    loadSchedule();
    startAutoRefresh();
});

// Initialize station selector dropdown
function initStationSelector() {
    const select = document.getElementById('stationSelect');
    
    STATIONS.forEach(station => {
        const option = document.createElement('option');
        option.value = station.id;
        option.textContent = `${station.nameCh} (${station.nameEn})`;
        select.appendChild(option);
    });
    
    select.value = currentStationId;
}

// Initialize event listeners
function initEventListeners() {
    document.getElementById('stationSelect').addEventListener('change', (e) => {
        currentStationId = e.target.value;
        loadSchedule();
    });
    
    document.getElementById('refreshBtn').addEventListener('click', () => {
        loadSchedule();
    });
    
    document.getElementById('pinBtn').addEventListener('click', togglePin);
    
    // Electron window controls
    if (window.electronAPI) {
        const minimizeBtn = document.getElementById('minimizeBtn');
        const closeBtn = document.getElementById('closeBtn');
        
        if (minimizeBtn) {
            minimizeBtn.addEventListener('click', () => {
                window.electronAPI.minimizeWindow();
            });
        }
        
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                window.electronAPI.closeWindow();
            });
        }
        
        // Load initial pin state
        window.electronAPI.getAlwaysOnTop().then(isAlwaysOnTop => {
            isPinned = isAlwaysOnTop;
            const pinBtn = document.getElementById('pinBtn');
            if (isPinned) {
                pinBtn.classList.add('pinned');
                pinBtn.title = '取消固定';
            }
        });
    }
}

// Toggle always-on-top
async function togglePin() {
    if (window.electronAPI) {
        // Electron environment
        const isNowPinned = await window.electronAPI.toggleAlwaysOnTop();
        isPinned = isNowPinned;
        const pinBtn = document.getElementById('pinBtn');
        
        if (isPinned) {
            pinBtn.classList.add('pinned');
            pinBtn.title = '取消固定';
        } else {
            pinBtn.classList.remove('pinned');
            pinBtn.title = '固定置頂';
        }
    } else {
        // Browser fallback
        isPinned = !isPinned;
        const pinBtn = document.getElementById('pinBtn');
        
        if (isPinned) {
            pinBtn.classList.add('pinned');
            pinBtn.title = '取消固定';
        } else {
            pinBtn.classList.remove('pinned');
            pinBtn.title = '固定置頂';
        }
    }
}

// Load schedule data from API
async function loadSchedule() {
    try {
        const response = await fetch(`${API_URL}?station_id=${currentStationId}`);
        
        if (!response.ok) {
            throw new Error('Failed to fetch schedule');
        }
        
        const data = await response.json();
        displaySchedule(data);
        updateLastUpdateTime();
    } catch (error) {
        console.error('Error loading schedule:', error);
        showError();
    }
}

// Display schedule data
function displaySchedule(data) {
    const platform1Container = document.getElementById('platform1Trains');
    const platform2Container = document.getElementById('platform2Trains');
    
    // Clear existing content
    platform1Container.innerHTML = '';
    platform2Container.innerHTML = '';
    
    if (!data.platform_list || data.platform_list.length === 0) {
        platform1Container.innerHTML = '<div class="no-trains">暫無列車資訊</div>';
        platform2Container.innerHTML = '<div class="no-trains">暫無列車資訊</div>';
        return;
    }
    
    // Find platforms
    const platform1 = data.platform_list.find(p => p.platform_id === 1);
    const platform2 = data.platform_list.find(p => p.platform_id === 2);
    
    // Display Platform 1 trains
    if (platform1 && platform1.route_list && platform1.route_list.length > 0) {
        platform1.route_list.slice(0, 5).forEach(train => {
            platform1Container.appendChild(createTrainCard(train));
        });
    } else {
        platform1Container.innerHTML = '<div class="no-trains">暫無列車</div>';
    }
    
    // Display Platform 2 trains
    if (platform2 && platform2.route_list && platform2.route_list.length > 0) {
        platform2.route_list.slice(0, 5).forEach(train => {
            platform2Container.appendChild(createTrainCard(train));
        });
    } else {
        platform2Container.innerHTML = '<div class="no-trains">暫無列車</div>';
    }
}

// Create train card element
function createTrainCard(train) {
    const card = document.createElement('div');
    card.className = 'train-card';
    
    const carType = train.train_length === 2 ? '雙卡' : '單卡';
    
    card.innerHTML = `
        <div class="train-route">${train.route_no}</div>
        <div class="train-destination">${train.dest_ch || train.dest_en}</div>
        <div class="train-info">
            <div class="train-time">
                <span>⏰</span>
                <span>${train.time_ch || train.time_en}</span>
            </div>
            <div class="train-car-type">
                <span>🚋</span>
                <span>${carType}</span>
            </div>
        </div>
    `;
    
    return card;
}

// Show error message
function showError() {
    const platform1Container = document.getElementById('platform1Trains');
    const platform2Container = document.getElementById('platform2Trains');
    
    platform1Container.innerHTML = '<div class="no-trains">載入失敗，請重試</div>';
    platform2Container.innerHTML = '<div class="no-trains">載入失敗，請重試</div>';
}

// Update last update time display
function updateLastUpdateTime() {
    const now = new Date();
    const timeString = now.toLocaleTimeString('zh-HK', { 
        hour: '2-digit', 
        minute: '2-digit',
        second: '2-digit',
        hour12: false 
    });
    
    document.getElementById('lastUpdate').textContent = timeString;
}

// Start auto-refresh
function startAutoRefresh() {
    if (refreshTimer) {
        clearInterval(refreshTimer);
    }
    
    refreshTimer = setInterval(() => {
        loadSchedule();
    }, REFRESH_INTERVAL);
}

// Stop auto-refresh (for cleanup)
function stopAutoRefresh() {
    if (refreshTimer) {
        clearInterval(refreshTimer);
        refreshTimer = null;
    }
}

// Cleanup on page unload
window.addEventListener('beforeunload', () => {
    stopAutoRefresh();
});

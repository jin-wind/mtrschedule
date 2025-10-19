// MTR API Service
const API_BASE_URL = 'https://rt.data.gov.hk/v1/transport/mtr/lrt/getSchedule';
const CORS_PROXIES = [
    'https://corsproxy.io/?',
    'https://api.codetabs.com/v1/proxy?quest='
];

// Fetch train schedule for a station
async function fetchStationSchedule(stationId) {
    const apiUrl = `${API_BASE_URL}?station_id=${stationId}`;
    
    // Try direct API first
    try {
        const response = await fetch(apiUrl);
        if (response.ok) {
            const data = await response.json();
            if (data.status === 1) {
                return parseScheduleData(stationId, data);
            }
        }
    } catch (error) {
        console.log('Direct API failed, trying CORS proxy...', error.message);
    }
    
    // Try CORS proxies
    for (const proxy of CORS_PROXIES) {
        try {
            const url = `${proxy}${encodeURIComponent(apiUrl)}`;
            const response = await fetch(url);
            
            if (response.ok) {
                const data = await response.json();
                if (data.status === 1) {
                    return parseScheduleData(stationId, data);
                }
            }
        } catch (error) {
            console.log(`CORS proxy ${proxy} failed:`, error.message);
        }
    }
    
    // If all methods fail, return demo data
    console.log('All API methods failed, using demo data');
    return getDemoData(stationId);
}

// Generate demo data when API is unavailable
function getDemoData(stationId) {
    const station = getStationById(stationId);
    
    if (!station) {
        throw new Error(`Station not found: ${stationId}`);
    }
    
    // Generate realistic demo train data
    const routes = ['610', '614', '615', '751', '761', '505', '507'];
    const destinations = ['屯門碼頭', '元朗', '屯門', '天逸', '友愛', '三聖'];
    const trains = [];
    
    // Generate 6-8 trains
    const trainCount = 6 + Math.floor(Math.random() * 3);
    for (let i = 0; i < trainCount; i++) {
        const timeToArrival = i * 3 + Math.floor(Math.random() * 2);
        const route = routes[Math.floor(Math.random() * routes.length)];
        const destination = destinations[Math.floor(Math.random() * destinations.length)];
        const platform = Math.floor(Math.random() * 2) + 1;
        
        trains.push({
            trainId: `demo_${i}`,
            routeNumber: route,
            destination: destination,
            platform: platform,
            eta: timeToArrival === 0 ? '即將抵達' : `${timeToArrival} 分鐘`,
            timeToArrival: timeToArrival
        });
    }
    
    // Sort trains by time to arrival
    trains.sort((a, b) => a.timeToArrival - b.timeToArrival);
    
    return {
        stationId: stationId,
        stationCode: stationId,
        stationName: station.nameCh,
        stationNameEn: station.nameEn,
        trains: trains,
        timestamp: new Date().toISOString(),
        isDemo: true
    };
}

// Parse API response into a structured format
function parseScheduleData(stationId, apiResponse) {
    const station = getStationById(stationId);
    
    if (!station) {
        throw new Error(`Station not found: ${stationId}`);
    }
    
    const trains = [];
    
    // Process each platform
    if (apiResponse.platform_list) {
        apiResponse.platform_list.forEach(platform => {
            // Process each route on the platform
            if (platform.route_list) {
                platform.route_list.forEach(route => {
                    // Parse time to arrival in minutes
                    let timeToArrival = 0;
                    if (route.time_en) {
                        const timeStr = route.time_en.toLowerCase();
                        if (timeStr.includes('arriving') || timeStr === '-') {
                            timeToArrival = 0;
                        } else {
                            const match = timeStr.match(/(\d+)/);
                            if (match) {
                                timeToArrival = parseInt(match[1]);
                            }
                        }
                    }
                    
                    trains.push({
                        trainId: `${platform.platform_id}_${route.route_no}`,
                        routeNumber: route.route_no,
                        destination: route.dest_ch || route.dest_en || 'Unknown',
                        platform: platform.platform_id,
                        eta: route.time_ch || route.time_en || '-',
                        timeToArrival: timeToArrival
                    });
                });
            }
        });
    }
    
    // Sort trains by time to arrival
    trains.sort((a, b) => a.timeToArrival - b.timeToArrival);
    
    return {
        stationId: stationId,
        stationCode: stationId,
        stationName: station.nameCh,
        stationNameEn: station.nameEn,
        trains: trains,
        timestamp: new Date().toISOString()
    };
}

// Get current timestamp formatted
function getCurrentTimestamp() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

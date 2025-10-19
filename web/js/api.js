// MTR API Service
const API_BASE_URL = 'https://rt.data.gov.hk/v1/transport/mtr/lrt/getSchedule';

// Fetch train schedule for a station
async function fetchStationSchedule(stationId) {
    try {
        const response = await fetch(`${API_BASE_URL}?station_id=${stationId}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        
        if (data.status !== 1) {
            throw new Error('Invalid API response');
        }
        
        return parseScheduleData(stationId, data);
    } catch (error) {
        console.error('Error fetching station schedule:', error);
        throw error;
    }
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

// API Service for MTR Data
class MtrApiService {
    constructor() {
        this.baseUrl = 'https://rt.data.gov.hk/v2/transport/mtr';
        this.endpoints = {
            schedule: '/lrt/getSchedule'
        };
    }

    // Get next trains for a station
    async getNextTrains(stationId) {
        try {
            const url = `${this.baseUrl}${this.endpoints.schedule}?station_id=${stationId}`;
            const response = await fetch(url);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const data = await response.json();
            return this.parseApiResponse(data, stationId);
        } catch (error) {
            console.error('Error fetching train schedule:', error);
            throw error;
        }
    }

    // Parse API response to match our data model
    parseApiResponse(data, stationId) {
        const station = getStationById(stationId);
        if (!station) {
            throw new Error(`Station ${stationId} not found`);
        }

        const trains = [];

        // Parse platform data
        if (data && data.platform_list) {
            data.platform_list.forEach(platform => {
                if (platform.route_list) {
                    platform.route_list.forEach(route => {
                        if (route.train_list) {
                            route.train_list.forEach(train => {
                                trains.push({
                                    trainId: train.train_id || '',
                                    routeNumber: train.route_no || route.route_no || '',
                                    destination: this.getDestinationName(train.dest_ch, train.dest_en),
                                    platform: platform.platform_id || '',
                                    eta: train.time_ch || train.time_en || '',
                                    timeToArrival: this.parseTimeToArrival(train.time_en),
                                    timestamp: data.curr_time || new Date().toISOString(),
                                    isDoubleCar: train.car_length === '2' || train.car_length === 2
                                });
                            });
                        }
                    });
                }
            });
        }

        return {
            ...station,
            nextTrains: trains,
            timestamp: data.curr_time || new Date().toISOString()
        };
    }

    // Get destination name (prefer Chinese)
    getDestinationName(destCh, destEn) {
        const lang = storageManager.getLanguage();
        if (lang === 'en') {
            return destEn || destCh || 'Unknown';
        }
        return destCh || destEn || 'Unknown';
    }

    // Parse time to arrival from string like "1 min", "5 min", "- -"
    parseTimeToArrival(timeStr) {
        if (!timeStr || timeStr === '- -' || timeStr === '--') {
            return 999; // Unknown time
        }
        
        const match = timeStr.match(/(\d+)/);
        if (match) {
            return parseInt(match[1]);
        }
        
        return 999;
    }

    // Get multiple station schedules (parallel requests)
    async getMultipleStationSchedules(stationIds) {
        try {
            const promises = stationIds.map(stationId => 
                this.getNextTrains(stationId).catch(err => {
                    console.error(`Error fetching station ${stationId}:`, err);
                    return null;
                })
            );
            
            const results = await Promise.all(promises);
            return results.filter(result => result !== null);
        } catch (error) {
            console.error('Error fetching multiple station schedules:', error);
            throw error;
        }
    }

    // Check if API is available
    async checkApiStatus() {
        try {
            // Try to fetch a common station
            await this.getNextTrains('100');
            return true;
        } catch (error) {
            return false;
        }
    }
}

// Export singleton instance
const mtrApi = new MtrApiService();

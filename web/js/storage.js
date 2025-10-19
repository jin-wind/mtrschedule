// Local Storage Manager
class StorageManager {
    constructor() {
        this.STORAGE_KEYS = {
            PINNED_STATIONS: 'pinned_stations',
            STATION_ORDER: 'station_order',
            DEFAULT_STATION: 'default_station',
            LANGUAGE: 'language',
            CACHE_TIMESTAMP: 'cache_timestamp',
            STATION_CACHE: 'station_cache'
        };
    }

    // Get pinned stations
    getPinnedStations() {
        const pinnedStr = localStorage.getItem(this.STORAGE_KEYS.PINNED_STATIONS);
        if (!pinnedStr) return [];
        try {
            return JSON.parse(pinnedStr);
        } catch (e) {
            return [];
        }
    }

    // Set pinned stations
    setPinnedStations(stationIds) {
        localStorage.setItem(this.STORAGE_KEYS.PINNED_STATIONS, JSON.stringify(stationIds));
    }

    // Add pinned station
    addPinnedStation(stationId) {
        const pinned = this.getPinnedStations();
        if (!pinned.includes(stationId)) {
            pinned.push(stationId);
            this.setPinnedStations(pinned);
        }
    }

    // Remove pinned station
    removePinnedStation(stationId) {
        const pinned = this.getPinnedStations();
        const filtered = pinned.filter(id => id !== stationId);
        this.setPinnedStations(filtered);
    }

    // Toggle pinned station
    togglePinnedStation(stationId) {
        const pinned = this.getPinnedStations();
        if (pinned.includes(stationId)) {
            this.removePinnedStation(stationId);
            return false;
        } else {
            this.addPinnedStation(stationId);
            return true;
        }
    }

    // Check if station is pinned
    isPinned(stationId) {
        return this.getPinnedStations().includes(stationId);
    }

    // Reset all pinned stations
    resetPinnedStations() {
        this.setPinnedStations([]);
    }

    // Get station order
    getStationOrder() {
        const orderStr = localStorage.getItem(this.STORAGE_KEYS.STATION_ORDER);
        if (!orderStr) return [];
        try {
            return JSON.parse(orderStr);
        } catch (e) {
            return [];
        }
    }

    // Set station order
    setStationOrder(stationIds) {
        localStorage.setItem(this.STORAGE_KEYS.STATION_ORDER, JSON.stringify(stationIds));
    }

    // Move station to top and save order
    topStationAndSaveOrder(stations, stationId) {
        const stationIndex = stations.findIndex(s => s.stationId === stationId);
        if (stationIndex === -1) return stations;

        const newStations = [...stations];
        const [station] = newStations.splice(stationIndex, 1);
        newStations.unshift(station);

        // Save the new order
        const order = newStations.map(s => s.stationId);
        this.setStationOrder(order);

        return newStations;
    }

    // Sort stations by saved order
    sortStationsByOrder(stations) {
        const order = this.getStationOrder();
        if (order.length === 0) return stations;

        const ordered = [];
        const remaining = [...stations];

        // First add stations in saved order
        order.forEach(stationId => {
            const index = remaining.findIndex(s => s.stationId === stationId);
            if (index !== -1) {
                ordered.push(remaining[index]);
                remaining.splice(index, 1);
            }
        });

        // Then add any remaining stations
        return [...ordered, ...remaining];
    }

    // Get default station
    getDefaultStation() {
        return localStorage.getItem(this.STORAGE_KEYS.DEFAULT_STATION) || '100'; // Default to Siu Hong
    }

    // Set default station
    setDefaultStation(stationId) {
        localStorage.setItem(this.STORAGE_KEYS.DEFAULT_STATION, stationId);
    }

    // Get language
    getLanguage() {
        return localStorage.getItem(this.STORAGE_KEYS.LANGUAGE) || 'zh-HK';
    }

    // Set language
    setLanguage(language) {
        localStorage.setItem(this.STORAGE_KEYS.LANGUAGE, language);
    }

    // Cache management
    getCacheTimestamp() {
        const timestamp = localStorage.getItem(this.STORAGE_KEYS.CACHE_TIMESTAMP);
        return timestamp ? parseInt(timestamp) : 0;
    }

    setCacheTimestamp(timestamp) {
        localStorage.setItem(this.STORAGE_KEYS.CACHE_TIMESTAMP, timestamp.toString());
    }

    isCacheValid(maxAgeMinutes = 30) {
        const timestamp = this.getCacheTimestamp();
        const now = Date.now();
        return (now - timestamp) < (maxAgeMinutes * 60 * 1000);
    }

    // Station data cache
    getStationCache() {
        const cacheStr = localStorage.getItem(this.STORAGE_KEYS.STATION_CACHE);
        if (!cacheStr) return {};
        try {
            return JSON.parse(cacheStr);
        } catch (e) {
            return {};
        }
    }

    setStationCache(cache) {
        localStorage.setItem(this.STORAGE_KEYS.STATION_CACHE, JSON.stringify(cache));
    }

    getCachedStation(stationId) {
        const cache = this.getStationCache();
        return cache[stationId];
    }

    cacheStation(stationId, stationData) {
        const cache = this.getStationCache();
        cache[stationId] = stationData;
        this.setStationCache(cache);
    }

    clearCache() {
        localStorage.removeItem(this.STORAGE_KEYS.STATION_CACHE);
        localStorage.removeItem(this.STORAGE_KEYS.CACHE_TIMESTAMP);
    }

    clearAll() {
        Object.values(this.STORAGE_KEYS).forEach(key => {
            localStorage.removeItem(key);
        });
    }
}

// Export singleton instance
const storageManager = new StorageManager();

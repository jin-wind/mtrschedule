// Light Rail Route Data
const LIGHT_RAIL_ROUTES = {
    "505": {
        routeNumber: "505",
        stations: ["920", "265", "270", "280", "295", "60", "190", "180", "200", "170", "160", "150", "140", "130", "120", "110", "100"],
        startStation: "三聖",
        endStation: "兆康",
        startStationEn: "Sam Shing",
        endStationEn: "Siu Hong",
        isCircular: false
    },
    "507": {
        routeNumber: "507",
        stations: ["1", "240", "250", "260", "270", "280", "295", "70", "75", "230", "220", "212", "160", "150", "140"],
        startStation: "屯門碼頭",
        endStation: "田景",
        startStationEn: "Tuen Mun Ferry Pier",
        endStationEn: "Tin King",
        isCircular: false
    },
    "610": {
        routeNumber: "610",
        stations: ["1", "10", "15", "20", "30", "40", "50", "200", "170", "212", "220", "230", "80", "90", "100", "350", "360", "370", "380", "390", "400", "560", "570", "580", "590", "600"],
        startStation: "屯門碼頭",
        endStation: "元朗",
        startStationEn: "Tuen Mun Ferry Pier",
        endStationEn: "Yuen Long",
        isCircular: false
    },
    "614": {
        routeNumber: "614",
        stations: ["1", "240", "250", "260", "270", "280", "300", "310", "320", "330", "340", "100", "350", "360", "370", "380", "390", "400", "560", "570", "580", "590", "600"],
        startStation: "屯門碼頭",
        endStation: "元朗",
        startStationEn: "Tuen Mun Ferry Pier",
        endStationEn: "Yuen Long",
        isCircular: false
    },
    "614P": {
        routeNumber: "614P",
        stations: ["1", "240", "250", "260", "270", "280", "300", "310", "320", "330", "340", "100"],
        startStation: "屯門碼頭",
        endStation: "兆康",
        startStationEn: "Tuen Mun Ferry Pier",
        endStationEn: "Siu Hong",
        isCircular: false
    },
    "615": {
        routeNumber: "615",
        stations: ["1", "10", "15", "20", "30", "40", "50", "200", "170", "160", "150", "140", "130", "120", "110", "100", "350", "360", "370", "380", "390", "400", "560", "570", "580", "590", "600"],
        startStation: "屯門碼頭",
        endStation: "元朗",
        startStationEn: "Tuen Mun Ferry Pier",
        endStationEn: "Yuen Long",
        isCircular: false
    },
    "615P": {
        routeNumber: "615P",
        stations: ["1", "10", "15", "20", "30", "40", "50", "200", "170", "160", "150", "140", "130", "120", "110", "100"],
        startStation: "屯門碼頭",
        endStation: "兆康",
        startStationEn: "Tuen Mun Ferry Pier",
        endStationEn: "Siu Hong",
        isCircular: false
    },
    "705": {
        routeNumber: "705",
        stations: ["430", "435", "450", "455", "500", "490", "468", "480", "550", "540", "530", "520", "510", "460", "448", "445", "430"],
        startStation: "天水圍 (逆時針)",
        endStation: "天水圍 (逆時針)",
        startStationEn: "Tin Shui Wai (Counter-Clockwise)",
        endStationEn: "Tin Shui Wai (Counter-Clockwise)",
        isCircular: true
    },
    "706": {
        routeNumber: "706",
        stations: ["430", "445", "448", "460", "510", "520", "530", "540", "550", "480", "468", "490", "500", "455", "450", "435", "430"],
        startStation: "天水圍 (順時針)",
        endStation: "天水圍 (順時針)",
        startStationEn: "Tin Shui Wai (Clockwise)",
        endStationEn: "Tin Shui Wai (Clockwise)",
        isCircular: true
    },
    "751": {
        routeNumber: "751",
        stations: ["275", "270", "280", "295", "70", "75", "80", "90", "100", "350", "360", "370", "380", "425", "430", "435", "450", "455", "500", "490", "468", "480", "550"],
        startStation: "友愛",
        endStation: "天逸",
        startStationEn: "Yau Oi",
        endStationEn: "Tin Yat",
        isCircular: false
    },
    "761P": {
        routeNumber: "761P",
        stations: ["600", "590", "580", "570", "560", "400", "390", "425", "430", "445", "448", "460", "490", "468", "480", "550"],
        startStation: "元朗",
        endStation: "天逸",
        startStationEn: "Yuen Long",
        endStationEn: "Tin Yat",
        isCircular: false
    }
};

// Get all routes
function getAllRoutes() {
    return Object.values(LIGHT_RAIL_ROUTES);
}

// Get route by number
function getRouteByNumber(routeNumber) {
    return LIGHT_RAIL_ROUTES[routeNumber];
}

// Get route stations for a specific direction
function getRouteStationsForDirection(routeNumber, isReverse) {
    const route = LIGHT_RAIL_ROUTES[routeNumber];
    if (!route) return [];
    
    // Special handling for route 505: when reversed, exclude stations 190 and 180
    if (routeNumber === "505" && isReverse) {
        return route.stations
            .filter(stationId => stationId !== "190" && stationId !== "180")
            .reverse();
    }
    
    // For other routes or 505 in forward direction
    return isReverse ? [...route.stations].reverse() : route.stations;
}

// Get all route numbers
function getAllRouteNumbers() {
    return Object.keys(LIGHT_RAIL_ROUTES).sort();
}

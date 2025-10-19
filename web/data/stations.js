// Station data for MTR Light Rail
const STATIONS = {
    "1": { id: "1", code: "001", name: "屯門碼頭", nameEn: "Tuen Mun Ferry Pier" },
    "10": { id: "10", code: "010", name: "美樂", nameEn: "Mei Lok" },
    "15": { id: "15", code: "015", name: "蝴蝶", nameEn: "Butterfly" },
    "20": { id: "20", code: "020", name: "輕鐵車廠", nameEn: "Light Rail Depot" },
    "30": { id: "30", code: "030", name: "龍門", nameEn: "Lung Mun" },
    "40": { id: "40", code: "040", name: "青山村", nameEn: "Tsing Shan Tsuen" },
    "50": { id: "50", code: "050", name: "青雲", nameEn: "Tsing Wun" },
    "60": { id: "60", code: "060", name: "建安", nameEn: "Kin On" },
    "70": { id: "70", code: "070", name: "河田", nameEn: "Ho Tin" },
    "75": { id: "75", code: "075", name: "蔡意橋", nameEn: "Tsoi Yuen Bridge" },
    "80": { id: "80", code: "080", name: "澤豐", nameEn: "Chak Fung" },
    "90": { id: "90", code: "090", name: "屯門醫院", nameEn: "Tuen Mun Hospital" },
    "100": { id: "100", code: "100", name: "兆康", nameEn: "Siu Hong" },
    "110": { id: "110", code: "110", name: "麒麟", nameEn: "Kei Lun" },
    "120": { id: "120", code: "120", name: "青松", nameEn: "Ching Chung" },
    "130": { id: "130", code: "130", name: "建生", nameEn: "Kin Sang" },
    "140": { id: "140", code: "140", name: "田景", nameEn: "Tin King" },
    "150": { id: "150", code: "150", name: "良景", nameEn: "Leung King" },
    "160": { id: "160", code: "160", name: "新圍", nameEn: "San Wai" },
    "170": { id: "170", code: "170", name: "石排", nameEn: "Shek Pai" },
    "180": { id: "180", code: "180", name: "山景 (北)", nameEn: "Shan King (North)" },
    "190": { id: "190", code: "190", name: "山景 (南)", nameEn: "Shan King (South)" },
    "200": { id: "200", code: "200", name: "鳴琴", nameEn: "Ming Kum" },
    "212": { id: "212", code: "212", name: "大興 (北)", nameEn: "Tai Hing (North)" },
    "220": { id: "220", code: "220", name: "大興 (南)", nameEn: "Tai Hing (South)" },
    "230": { id: "230", code: "230", name: "銀圍", nameEn: "Ngan Wai" },
    "240": { id: "240", code: "240", name: "兆禧", nameEn: "Siu Hei" },
    "250": { id: "250", code: "250", name: "屯門泳池", nameEn: "Tuen Mun Swimming Pool" },
    "260": { id: "260", code: "260", name: "豐景園", nameEn: "Fung King Garden" },
    "265": { id: "265", code: "265", name: "兆麟", nameEn: "Siu Lun" },
    "270": { id: "270", code: "270", name: "安定", nameEn: "On Ting" },
    "275": { id: "275", code: "275", name: "友愛", nameEn: "Yau Oi" },
    "280": { id: "280", code: "280", name: "市中心", nameEn: "Town Centre" },
    "295": { id: "295", code: "295", name: "屯門", nameEn: "Tuen Mun" },
    "300": { id: "300", code: "300", name: "杯渡", nameEn: "Pui To" },
    "310": { id: "310", code: "310", name: "何福堂", nameEn: "Ho Fuk Tong" },
    "320": { id: "320", code: "320", name: "新墟", nameEn: "San Hui" },
    "330": { id: "330", code: "330", name: "景峰", nameEn: "Prime View" },
    "340": { id: "340", code: "340", name: "鳳地", nameEn: "Fung Tei" },
    "350": { id: "350", code: "350", name: "藍地", nameEn: "Lam Tei" },
    "360": { id: "360", code: "360", name: "泥圍", nameEn: "Nai Wai" },
    "370": { id: "370", code: "370", name: "鍾屋村", nameEn: "Chung Uk Tsuen" },
    "380": { id: "380", code: "380", name: "洪水橋", nameEn: "Hung Shui Kiu" },
    "390": { id: "390", code: "390", name: "塘坊村", nameEn: "Tong Fong Tsuen" },
    "400": { id: "400", code: "400", name: "屏山", nameEn: "Ping Shan" },
    "425": { id: "425", code: "425", name: "坑尾村", nameEn: "Hang Mei Tsuen" },
    "430": { id: "430", code: "430", name: "天水圍", nameEn: "Tin Shui Wai" },
    "435": { id: "435", code: "435", name: "天慈", nameEn: "Tin Tsz" },
    "445": { id: "445", code: "445", name: "天耀", nameEn: "Tin Yiu" },
    "448": { id: "448", code: "448", name: "樂湖", nameEn: "Locwood" },
    "450": { id: "450", code: "450", name: "天湖", nameEn: "Tin Wu" },
    "455": { id: "455", code: "455", name: "銀座", nameEn: "Ginza" },
    "460": { id: "460", code: "460", name: "天晴", nameEn: "Tin Tsing" },
    "468": { id: "468", code: "468", name: "頌富", nameEn: "Chung Fu" },
    "480": { id: "480", code: "480", name: "天富", nameEn: "Tin Fu" },
    "490": { id: "490", code: "490", name: "翠湖", nameEn: "Chestwood" },
    "500": { id: "500", code: "500", name: "天榮", nameEn: "Tin Wing" },
    "510": { id: "510", code: "510", name: "天悅", nameEn: "Tin Yuet" },
    "520": { id: "520", code: "520", name: "天秀", nameEn: "Tin Sau" },
    "530": { id: "530", code: "530", name: "濕地公園", nameEn: "Wetland Park" },
    "540": { id: "540", code: "540", name: "天恆", nameEn: "Tin Heng" },
    "550": { id: "550", code: "550", name: "天逸", nameEn: "Tin Yat" },
    "560": { id: "560", code: "560", name: "水邊圍", nameEn: "Shui Pin Wai" },
    "570": { id: "570", code: "570", name: "豐年路", nameEn: "Fung Nin Road" },
    "580": { id: "580", code: "580", name: "康樂路", nameEn: "Hong Lok Road" },
    "590": { id: "590", code: "590", name: "大棠路", nameEn: "Tai Tong Road" },
    "600": { id: "600", code: "600", name: "元朗", nameEn: "Yuen Long" },
    "920": { id: "920", code: "920", name: "三聖", nameEn: "Sam Shing" }
};

// Get all stations as array
function getAllStations() {
    return Object.values(STATIONS).map(station => ({
        stationId: station.id,
        stationCode: station.code,
        stationName: station.name,
        stationNameEn: station.nameEn,
        nextTrains: [],
        isPinned: false
    }));
}

// Get station by ID
function getStationById(id) {
    const station = STATIONS[id];
    if (!station) return null;
    
    return {
        stationId: station.id,
        stationCode: station.code,
        stationName: station.name,
        stationNameEn: station.nameEn,
        nextTrains: [],
        isPinned: false
    };
}

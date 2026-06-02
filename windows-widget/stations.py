"""
MTR Light Rail Station List (68 stations)
"""
from models import StationInfo


STATIONS = [
    StationInfo("1", "Tuen Mun Ferry Pier", "屯門碼頭"),
    StationInfo("10", "Melody Garden", "美樂"),
    StationInfo("15", "Butterfly", "蝴蝶"),
    StationInfo("20", "Light Rail Depot", "輕鐵車廠"),
    StationInfo("30", "Lung Mun", "龍門"),
    StationInfo("40", "Tsing Shan Tsuen", "青山村"),
    StationInfo("50", "Tsing Wun", "青雲"),
    StationInfo("60", "Kin On", "建安"),
    StationInfo("70", "Ho Tin", "河田"),
    StationInfo("75", "Choy Yee Bridge", "蔡意橋"),
    StationInfo("80", "Affluence", "澤豐"),
    StationInfo("90", "Tuen Mun Hospital", "屯門醫院"),
    StationInfo("100", "Siu Hong", "兆康"),
    StationInfo("110", "Kei Lun", "麒麟"),
    StationInfo("120", "Ching Chung", "青松"),
    StationInfo("130", "Kin Sang", "建生"),
    StationInfo("140", "Tin King", "田景"),
    StationInfo("150", "Leung King", "良景"),
    StationInfo("160", "San Wai", "新圍"),
    StationInfo("170", "Shek Pai", "石排"),
    StationInfo("180", "Shan King(North)", "山景(北)"),
    StationInfo("190", "Shan King(South)", "山景(南)"),
    StationInfo("200", "Ming Kum", "鳴琴"),
    StationInfo("212", "Tai Hing(North)", "大興(北)"),
    StationInfo("220", "Tai Hing(South)", "大興(南)"),
    StationInfo("230", "Ngan Wai", "銀圍"),
    StationInfo("240", "Siu Hei", "兆禧"),
    StationInfo("250", "Tuen Mun Swimming Pool", "屯門泳池"),
    StationInfo("260", "Goodview Garden", "豐景園"),
    StationInfo("265", "Siu Lun", "兆麟"),
    StationInfo("270", "On Ting", "安定"),
    StationInfo("275", "Yau Oi", "友愛"),
    StationInfo("280", "Town Centre", "市中心"),
    StationInfo("295", "Tuen Mun", "屯門"),
    StationInfo("300", "Pui To", "杯渡"),
    StationInfo("310", "Hoh Fuk Tong", "何福堂"),
    StationInfo("320", "San Hui", "新墟"),
    StationInfo("330", "Prime View", "景峰"),
    StationInfo("340", "Fung Tei", "鳳地"),
    StationInfo("350", "Lam Tei", "藍地"),
    StationInfo("360", "Nai Wai", "泥圍"),
    StationInfo("370", "Chung Uk Tsuen", "鍾屋村"),
    StationInfo("380", "Hung Shui Kiu", "洪水橋"),
    StationInfo("390", "Tong Fong Tsuen", "塘坊村"),
    StationInfo("400", "Ping Shan", "屏山"),
    StationInfo("425", "Hang Mei Tsuen", "坑尾村"),
    StationInfo("430", "Tin Shui Wai", "天水圍"),
    StationInfo("435", "Tin Tsz", "天慈"),
    StationInfo("445", "Tin Yiu", "天耀"),
    StationInfo("448", "Locwood", "樂湖"),
    StationInfo("450", "Tin Wu", "天湖"),
    StationInfo("455", "Ginza", "銀座"),
    StationInfo("460", "Tin Shui", "天瑞"),
    StationInfo("468", "Chung Fu", "頌富"),
    StationInfo("480", "Tin Fu", "天富"),
    StationInfo("490", "Chestwood", "翠湖"),
    StationInfo("500", "Tin Wing", "天榮"),
    StationInfo("510", "Tin Yuet", "天悅"),
    StationInfo("520", "Tin Sau", "天秀"),
    StationInfo("530", "Wetland Park", "濕地公園"),
    StationInfo("540", "Tin Heng", "天恒"),
    StationInfo("550", "Tin Yat", "天逸"),
    StationInfo("560", "Shui Pin Wai", "水邊圍"),
    StationInfo("570", "Fung Nin Road", "豐年路"),
    StationInfo("580", "Hong Lok Road", "康樂路"),
    StationInfo("590", "Tai Tong Road", "大棠路"),
    StationInfo("600", "Yuen Long", "元朗"),
    StationInfo("920", "Sam Shing", "三聖"),
]


def get_station_by_id(station_id: str) -> StationInfo:
    """Get station by ID"""
    for station in STATIONS:
        if station.id == station_id:
            return station
    return None


def get_all_stations() -> list:
    """Get all stations"""
    return STATIONS

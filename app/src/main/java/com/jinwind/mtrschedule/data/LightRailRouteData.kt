package com.jinwind.mtrschedule.data

import com.jinwind.mtrschedule.model.LightRailRoute

/**
 * 轻铁路线数据，包含所有预定义的轻铁路线信息
 */
object LightRailRouteData {

    // 获取所有轻铁路线
    fun getAllRoutes(): Map<String, LightRailRoute> {
        return mapOf(
            "505" to LightRailRoute(
                routeNumber = "505",
                stations = listOf(
                    "920", // 三聖
                    "265", // 兆麟
                    "270", // 安定
                    "280", // 市中心
                    "295", // 屯門
                    "60",  // 建安
                    "190", // 山景 (南)
                    "180", // 山景 (北)
                    "200", // 鳴琴
                    "170", // 石排
                    "160", // 新圍
                    "150", // 良景
                    "140", // 田景
                    "130", // 建生
                    "120", // 青松
                    "110", // 麒麟
                    "100"  // 兆康
                ),
                startStation = "三聖",
                endStation = "兆康"
            ),
            "507" to LightRailRoute(
                routeNumber = "507",
                stations = listOf(
                    "1",   // 屯門碼頭
                    "240", // 兆禧
                    "250", // 屯門泳池
                    "260", // 豐景園
                    "270", // 安定
                    "280", // 市中心
                    "295", // 屯門
                    "70",  // 河田
                    "75",  // 蔡意橋
                    "230", // 銀圍
                    "220", // 大興 (南)
                    "212", // 大興 (北)
                    "160", // 新圍
                    "150", // 良景
                    "140"  // 田景
                ),
                startStation = "屯門碼頭",
                endStation = "田景"
            ),
            "610" to LightRailRoute(
                routeNumber = "610",
                stations = listOf(
                    "1",   // 屯門碼頭
                    "10",  // 美樂
                    "15",  // 蝴蝶
                    "20",  // 輕鐵車廠
                    "30",  // 龍門
                    "40",  // 青山村
                    "50",  // 青雲
                    "200", // 鳴琴
                    "170", // 石排
                    "212", // 大興 (北)
                    "220", // 大興 (南)
                    "230", // 銀圍
                    "80",  // 澤豐
                    "90",  // 屯門醫院
                    "100", // 兆康
                    "350", // 藍地
                    "360", // 泥圍
                    "370", // 鍾屋村
                    "380", // 洪水橋
                    "390", // 塘坊村
                    "400", // 屏山
                    "560", // 水邊圍
                    "570", // 豐年路
                    "580", // 康樂路
                    "590", // 大棠路
                    "600"  // 元朗
                ),
                startStation = "屯門碼頭",
                endStation = "元朗"
            ),
            "614" to LightRailRoute(
                routeNumber = "614",
                stations = listOf(
                    "1",   // 屯門碼頭
                    "240", // 兆禧
                    "250", // 屯門泳池
                    "260", // 豐景園
                    "270", // 安定
                    "280", // 市中心
                    "300", // 杯渡
                    "310", // 何福堂
                    "320", // 新墟
                    "330", // 景峰
                    "340", // 鳳地
                    "100", // 兆康
                    "350", // 藍地
                    "360", // 泥圍
                    "370", // 鍾屋村
                    "380", // 洪水橋
                    "390", // 塘坊村
                    "400", // 屏山
                    "560", // 水邊圍
                    "570", // 豐年路
                    "580", // 康樂路
                    "590", // 大棠路
                    "600"  // 元朗
                ),
                startStation = "屯門碼頭",
                endStation = "元朗"
            ),
            "614P" to LightRailRoute(
                routeNumber = "614P",
                stations = listOf(
                    "1",   // 屯門碼頭
                    "240", // 兆禧
                    "250", // 屯門泳池
                    "260", // 豐景園
                    "270", // 安定
                    "280", // 市中心
                    "300", // 杯渡
                    "310", // 何福堂
                    "320", // 新墟
                    "330", // 景峰
                    "340", // 鳳地
                    "100"  // 兆康
                ),
                startStation = "屯門碼頭",
                endStation = "兆康"
            ),
            "615" to LightRailRoute(
                routeNumber = "615",
                stations = listOf(
                    "1",   // 屯門碼頭
                    "10",  // 美樂
                    "15",  // 蝴蝶
                    "20",  // 輕鐵車廠
                    "30",  // 龍門
                    "40",  // 青山村
                    "50",  // 青雲
                    "200", // 鳴琴
                    "170", // 石排
                    "160", // 新圍
                    "150", // 良景
                    "140", // 田景
                    "130", // 建生
                    "120", // 青松
                    "110", // 麒麟
                    "100", // 兆康
                    "350", // 藍地
                    "360", // 泥圍
                    "370", // 鍾屋村
                    "380", // 洪水橋
                    "390", // 塘坊村
                    "400", // 屏山
                    "560", // 水邊圍
                    "570", // 豐年路
                    "580", // 康樂路
                    "590", // 大棠路
                    "600"  // 元朗
                ),
                startStation = "屯門碼頭",
                endStation = "元朗"
            ),
            "615P" to LightRailRoute(
                routeNumber = "615P",
                stations = listOf(
                    "1",   // 屯門碼頭
                    "10",  // 美樂
                    "15",  // 蝴蝶
                    "20",  // 輕鐵車廠
                    "30",  // 龍門
                    "40",  // 青山村
                    "50",  // 青雲
                    "200", // 鳴琴
                    "170", // 石排
                    "160", // 新圍
                    "150", // 良景
                    "140", // 田景
                    "130", // 建生
                    "120", // 青松
                    "110", // 麒麟
                    "100"  // 兆康
                ),
                startStation = "屯門碼頭",
                endStation = "兆康"
            ),
            "705" to LightRailRoute(
                routeNumber = "705",
                stations = listOf(
                    "430", // 天水圍
                    "435", // 天慈
                    "450", // 天湖
                    "455", // 銀座
                    "500", // 天榮
                    "490", // 翠湖
                    "468", // 頌富
                    "480", // 天富
                    "550", // 天逸
                    "540", // 天恆
                    "530", // 濕地公園
                    "520", // 天秀
                    "510", // 天悅
                    "460", // 天晴
                    "448", // 樂湖
                    "445", // 天耀
                    "430"  // 天水圍
                ),
                startStation = "天水圍 (逆時針)",
                endStation = "天水圍 (逆時針)",
                isCircular = true
            ),
            "706" to LightRailRoute(
                routeNumber = "706",
                stations = listOf(
                    "430", // 天水圍
                    "445", // 天耀
                    "448", // 樂湖
                    "460", // 天晴
                    "510", // 天悅
                    "520", // 天秀
                    "530", // 濕地公園
                    "540", // 天恆
                    "550", // 天逸
                    "480", // 天富
                    "468", // 頌富
                    "490", // 翠湖
                    "500", // 天榮
                    "455", // 銀座
                    "450", // 天湖
                    "435", // 天慈
                    "430"  // 天水圍
                ),
                startStation = "天水圍 (順時針)",
                endStation = "天水圍 (順時針)",
                isCircular = true
            ),
            "751" to LightRailRoute(
                routeNumber = "751",
                stations = listOf(
                    "275", // 友愛
                    "270", // 安定
                    "280", // 市中心
                    "295", // 屯門
                    "70",  // 河田
                    "75",  // 蔡意橋
                    "80",  // 澤豐
                    "90",  // 屯門醫院
                    "100", // 兆康
                    "350", // 藍地
                    "360", // 泥圍
                    "370", // 鍾屋村
                    "380", // 洪水橋
                    "425", // 坑尾村
                    "430", // 天水圍
                    "435", // 天慈
                    "450", // 天湖
                    "455", // 銀座
                    "500", // 天榮
                    "490", // 翠湖
                    "468", // 頌富
                    "480", // 天富
                    "550"  // 天逸
                ),
                startStation = "友愛",
                endStation = "天逸"
            ),
            "761P" to LightRailRoute(
                routeNumber = "761P",
                stations = listOf(
                    "600", // 元朗
                    "590", // 大棠路
                    "580", // 康樂路
                    "570", // 豐年路
                    "560", // 水邊圍
                    "400", // 屏山
                    "390", // 塘坊村
                    "425", // 坑尾村
                    "430", // 天水圍
                    "445", // 天耀
                    "448", // 樂湖
                    "460", // 天瑞
                    "490", // 翠湖
                    "468", // 頌富
                    "480", // 天富
                    "550"  // 天逸
                ),
                startStation = "元朗",
                endStation = "天逸"
            )
        )
    }

    // 获取单个路线信息
    fun getRoute(routeNumber: String): LightRailRoute? {
        return getAllRoutes()[routeNumber]
    }

    // 获取特定方向的路线站点列表
    fun getRouteStationsForDirection(routeNumber: String, isReverse: Boolean): List<String> {
        val route = getRoute(routeNumber) ?: return emptyList()

        // 505路线特殊处理：反向（从兆康到三聖）时不包含山景站
        if (routeNumber == "505" && isReverse) {
            return route.stations.filter { stationId ->
                stationId != "190" && stationId != "180" // 排除山景(南)和山景(北)
            }.reversed() // 反向时需要反转站点顺序
        }

        // 其他路线或505正向：返回完整站点列表
        return if (isReverse) route.stations.reversed() else route.stations
    }
}

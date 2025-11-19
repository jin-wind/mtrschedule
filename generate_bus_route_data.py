import json

target_routes = ["K52", "K52A", "K52S", "K53", "K58", "506", "K51", "K51A"]

def generate_bus_route_data():
    with open('routeFareList.json', 'r', encoding='utf-8') as f:
        data = json.load(f)

    route_list = data.get('routeList', {})
    
    output_path = 'app/src/main/java/com/jinwind/mtrschedule/data/BusRouteData.kt'
    with open(output_path, 'w', encoding='utf-8') as out:
        def write(line):
            out.write(line + '\n')

        write("package com.jinwind.mtrschedule.data")
        write("")
        write("object BusRouteData {")
        write("    data class BusRoute(")
        write("        val routeNumber: String,")
        write("        val startStation: String,")
        write("        val endStation: String,")
        write("        val stops: List<String>")
        write("    )")
        write("")
        write("    // Map of RouteNumber to List of BusRoutes (usually 2 directions)")
        write("    val routes = mapOf(")
        
        for route_num in target_routes:
            write(f'        "{route_num}" to listOf(')
            
            # Find keys for this route
            # Keys format: Route+Bound+Origin+Dest
            # We need to be careful to match exact route number (e.g. K52 should not match K52A)
            
            relevant_keys = []
            for key in route_list.keys():
                parts = key.split('+')
                if parts[0] == route_num:
                    relevant_keys.append(key)
            
            # Sort keys to ensure consistent order (usually forward/backward)
            relevant_keys.sort()
            
            for key in relevant_keys:
                route_data = route_list[key]
                origin = route_data['orig']['zh'].replace('"', '\\"')
                dest = route_data['dest']['zh'].replace('"', '\\"')
                
                # Extract stops
                # Stops are in route_data['stops']['lrtfeeder']
                stops = route_data.get('stops', {}).get('lrtfeeder', [])
                
                if not stops:
                    continue

                write(f'            BusRoute(')
                write(f'                routeNumber = "{route_num}",')
                write(f'                startStation = "{origin}",')
                write(f'                endStation = "{dest}",')
                write(f'                stops = listOf(')
                for stop in stops:
                    write(f'                    "{stop}",')
                write(f'                )')
                write(f'            ),')
                
            write("        ),")

        write("    )")
        write("")
        write("    fun getRoute(routeNumber: String, isReverse: Boolean): BusRoute? {")
        write("        val routeList = routes[routeNumber]")
        write("        if (routeList.isNullOrEmpty()) return null")
        write("        ")
        write("        // If isReverse is true, try to return the second route (index 1)")
        write("        // If isReverse is false, return the first route (index 0)")
        write("        val index = if (isReverse) 1 else 0")
        write("        if (index < routeList.size) {")
        write("            return routeList[index]")
        write("        }")
        write("        return routeList[0]")
        write("    }")
        write("}")

if __name__ == "__main__":
    generate_bus_route_data()

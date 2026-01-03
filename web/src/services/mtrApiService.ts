import axios from 'axios'
import type { Station } from '@/models/Station'
import type { Train } from '@/models/Train'
import { getStationInfo } from '@/data/stationList'

const BASE_URL = 'https://rt.data.gov.hk/v1/transport/mtr/'

interface MtrApiResponse {
  status: number
  system_time: string
  platform_list?: Platform[]
}

interface Platform {
  platform_id: number
  route_list?: Route[]
}

interface Route {
  train_length: number
  arrival_departure: string
  dest_en: string
  dest_ch: string
  time_en: string
  time_ch: string
  route_no: string
  stop: number
}

const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 30000
})

function getCurrentTimestamp(): string {
  return new Date().toLocaleString('en-US', {
    timeZone: 'Asia/Hong_Kong',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

export async function getStationSchedule(stationId: string): Promise<Station> {
  try {
    const response = await apiClient.get<MtrApiResponse>('lrt/getSchedule', {
      params: { station_id: stationId }
    })

    const apiResponse = response.data
    const stationInfo = getStationInfo(stationId)

    if (!stationInfo) {
      throw new Error(`Station info not found for ID: ${stationId}`)
    }

    // Status 0 means no trains available
    if (apiResponse.status === 0 || !apiResponse.platform_list) {
      return {
        stationId,
        stationCode: stationId,
        stationName: stationInfo.nameChi,
        nextTrains: [],
        isPinned: false
      }
    }

    // Parse trains from platforms
    const trains: Train[] = []
    const currentTimestamp = getCurrentTimestamp()

    apiResponse.platform_list.forEach((platform) => {
      if (platform.route_list) {
        platform.route_list.forEach((route) => {
          // Parse minutes from time_en (e.g., "8 mins" -> 8, "Arriving" -> 0)
          const timeEnLower = route.time_en?.toLowerCase() || ''
          const minutes =
            timeEnLower === 'arriving'
              ? 0
              : parseInt(route.time_en?.split(' ')[0] || '0') || 0

          // Double car when train_length is 2
          const isDoubleCar = route.train_length === 2

          trains.push({
            trainId: `${platform.platform_id}_${route.route_no}`,
            routeNumber: route.route_no,
            destination: route.dest_ch,
            platform: `站台 ${platform.platform_id}`,
            eta: route.time_ch,
            timeToArrival: minutes,
            timestamp: currentTimestamp,
            isDoubleCar
          })
        })
      }
    })

    // Sort by arrival time
    trains.sort((a, b) => a.timeToArrival - b.timeToArrival)

    return {
      stationId,
      stationCode: stationId,
      stationName: stationInfo.nameChi,
      nextTrains: trains,
      isPinned: false
    }
  } catch (error) {
    console.error('Error fetching station schedule:', error)
    throw error
  }
}

export async function getMultipleStationSchedules(stationIds: string[]): Promise<Station[]> {
  const promises = stationIds.map((id) =>
    getStationSchedule(id).catch((err) => {
      console.error(`Failed to fetch station ${id}:`, err)
      return null
    })
  )

  const results = await Promise.all(promises)
  return results.filter((station): station is Station => station !== null)
}

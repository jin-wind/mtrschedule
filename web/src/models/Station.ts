import type { Train } from './Train'

export interface Station {
  stationId: string
  stationCode: string
  stationName: string
  nextTrains: Train[]
  isPinned?: boolean
}

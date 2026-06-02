import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Station } from '@/models/Station'
import { getStationSchedule, getMultipleStationSchedules } from '@/services/mtrApiService'
import { stations } from '@/data/stationList'

export const useStationStore = defineStore('station', () => {
  const allStations = ref<Station[]>([])
  const selectedStation = ref<Station | null>(null)
  const isLoading = ref(false)
  const error = ref<string>('')
  const pinnedStationIds = ref<Set<string>>(new Set())
  const stationOrder = ref<string[]>([])

  // Load pinned stations from localStorage
  function loadPinnedStations() {
    const saved = localStorage.getItem('pinnedStations')
    if (saved) {
      pinnedStationIds.value = new Set(JSON.parse(saved))
    }
  }

  // Save pinned stations to localStorage
  function savePinnedStations() {
    localStorage.setItem('pinnedStations', JSON.stringify(Array.from(pinnedStationIds.value)))
  }

  // Load station order from localStorage
  function loadStationOrder() {
    const saved = localStorage.getItem('stationOrder')
    if (saved) {
      stationOrder.value = JSON.parse(saved)
    }
  }

  // Save station order to localStorage
  function saveStationOrder() {
    localStorage.setItem('stationOrder', JSON.stringify(stationOrder.value))
  }

  // Initialize basic station info
  function initializeStations() {
    loadPinnedStations()
    loadStationOrder()

    allStations.value = stations.map((s) => ({
      stationId: s.id,
      stationCode: s.id,
      stationName: s.nameChi,
      nextTrains: [],
      isPinned: pinnedStationIds.value.has(s.id)
    }))

    // Apply saved order if available
    if (stationOrder.value.length > 0) {
      allStations.value = sortStationsByOrder(allStations.value)
    }
  }

  // Sort stations by saved order
  function sortStationsByOrder(stationList: Station[]): Station[] {
    if (stationOrder.value.length === 0) return stationList

    const orderMap = new Map(stationOrder.value.map((id, index) => [id, index]))
    return [...stationList].sort((a, b) => {
      const orderA = orderMap.get(a.stationId) ?? Number.MAX_SAFE_INTEGER
      const orderB = orderMap.get(b.stationId) ?? Number.MAX_SAFE_INTEGER
      return orderA - orderB
    })
  }

  // Fetch station schedule
  async function fetchStationSchedule(stationId: string) {
    isLoading.value = true
    error.value = ''

    try {
      const station = await getStationSchedule(stationId)
      station.isPinned = pinnedStationIds.value.has(stationId)
      selectedStation.value = station

      // Update in allStations list
      const index = allStations.value.findIndex((s) => s.stationId === stationId)
      if (index !== -1) {
        allStations.value[index] = station
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to load station schedule'
      console.error(err)
    } finally {
      isLoading.value = false
    }
  }

  // Toggle pin station
  function togglePinStation(stationId: string) {
    if (pinnedStationIds.value.has(stationId)) {
      pinnedStationIds.value.delete(stationId)
    } else {
      pinnedStationIds.value.add(stationId)
    }
    savePinnedStations()

    // Update station objects
    allStations.value = allStations.value.map((s) => ({
      ...s,
      isPinned: pinnedStationIds.value.has(s.stationId)
    }))

    if (selectedStation.value && selectedStation.value.stationId === stationId) {
      selectedStation.value.isPinned = pinnedStationIds.value.has(stationId)
    }
  }

  // Pin station to top
  function pinStationToTop(stationId: string) {
    // Remove from current position
    stationOrder.value = stationOrder.value.filter((id) => id !== stationId)
    // Add to top
    stationOrder.value.unshift(stationId)
    saveStationOrder()

    // Re-sort stations
    allStations.value = sortStationsByOrder(allStations.value)
  }

  // Reset all pinned stations
  function resetPinnedStations() {
    pinnedStationIds.value.clear()
    savePinnedStations()

    allStations.value = allStations.value.map((s) => ({
      ...s,
      isPinned: false
    }))
  }

  // Computed filtered stations
  const filteredStations = computed(() => {
    return allStations.value
  })

  return {
    allStations,
    selectedStation,
    isLoading,
    error,
    pinnedStationIds,
    filteredStations,
    initializeStations,
    fetchStationSchedule,
    togglePinStation,
    pinStationToTop,
    resetPinnedStations
  }
})

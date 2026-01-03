<script setup lang="ts">
import { ref, computed } from 'vue'
import { useStationStore } from '@/stores/stationStore'
import StationCard from '@/components/StationCard.vue'
import StationDetail from '@/components/StationDetail.vue'

const stationStore = useStationStore()
const drawer = ref(false)
const searchQuery = ref('')
const showDetail = ref(false)

const filteredStations = computed(() => {
  if (!searchQuery.value) return stationStore.allStations

  const query = searchQuery.value.toLowerCase()
  return stationStore.allStations.filter(
    (station) =>
      station.stationName.toLowerCase().includes(query) ||
      station.stationId.includes(query) ||
      station.stationCode.toLowerCase().includes(query)
  )
})

function handleStationClick(stationId: string) {
  stationStore.fetchStationSchedule(stationId)
  showDetail.value = true
}

function handleBack() {
  showDetail.value = false
}

function handlePinToTop(stationId: string) {
  stationStore.pinStationToTop(stationId)
}
</script>

<template>
  <v-app>
    <v-app-bar color="primary" prominent>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-toolbar-title>輕鐵時刻表</v-toolbar-title>
      <v-spacer></v-spacer>
    </v-app-bar>

    <v-navigation-drawer v-model="drawer" temporary>
      <v-list>
        <v-list-item prepend-icon="mdi-home" title="主頁" @click="showDetail = false"></v-list-item>
        <v-list-item prepend-icon="mdi-routes" title="路線模式" to="/routes"></v-list-item>
        <v-list-item prepend-icon="mdi-cog" title="設定" to="/settings"></v-list-item>
      </v-list>
    </v-navigation-drawer>

    <v-main>
      <v-container fluid>
        <!-- Station List View -->
        <div v-if="!showDetail">
          <v-text-field
            v-model="searchQuery"
            prepend-inner-icon="mdi-magnify"
            label="搜尋車站"
            variant="outlined"
            density="comfortable"
            clearable
            class="mb-4"
          ></v-text-field>

          <v-row>
            <v-col
              v-for="station in filteredStations"
              :key="station.stationId"
              cols="12"
              sm="6"
              md="4"
              lg="3"
            >
              <StationCard
                :station="station"
                @click="handleStationClick(station.stationId)"
                @pin-to-top="handlePinToTop(station.stationId)"
              />
            </v-col>
          </v-row>

          <div v-if="filteredStations.length === 0" class="text-center py-8">
            <v-icon size="64" color="grey">mdi-train-variant-off</v-icon>
            <p class="text-h6 text-grey mt-4">未找到車站</p>
          </div>
        </div>

        <!-- Station Detail View -->
        <StationDetail v-else :station="stationStore.selectedStation" @back="handleBack" />
      </v-container>
    </v-main>
  </v-app>
</template>

<style scoped>
.v-container {
  max-width: 1400px;
}
</style>

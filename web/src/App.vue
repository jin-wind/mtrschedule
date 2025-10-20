<script setup lang="ts">
import { RouterView } from 'vue-router'
import { onMounted } from 'vue'
import { useStationStore } from './stores/stationStore'

const stationStore = useStationStore()

onMounted(() => {
  stationStore.initializeStations()
})
</script>

<template>
  <v-app>
    <RouterView />
  </v-app>
</template>

<style>
html {
  overflow-y: auto;
}
</style>

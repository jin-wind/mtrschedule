<script setup lang="ts">
import type { Station } from '@/models/Station'

interface Props {
  station: Station
}

defineProps<Props>()
const emit = defineEmits<{
  click: []
  pinToTop: []
}>()
</script>

<template>
  <v-card
    class="station-card"
    :class="{ 'pinned-card': station.isPinned }"
    elevation="4"
    hover
    @click="emit('click')"
  >
    <v-card-title class="d-flex justify-space-between align-center">
      <span>{{ station.stationName }}</span>
      <v-icon v-if="station.isPinned" color="pink" size="small">mdi-pin</v-icon>
    </v-card-title>
    <v-card-subtitle>站台 ID: {{ station.stationId }}</v-card-subtitle>

    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn
        icon="mdi-chevron-double-up"
        size="small"
        variant="text"
        @click.stop="emit('pinToTop')"
      ></v-btn>
    </v-card-actions>
  </v-card>
</template>

<style scoped>
.station-card {
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.station-card:hover {
  transform: translateY(-4px);
  border-color: rgba(98, 0, 234, 0.5);
  box-shadow: 0 8px 24px rgba(98, 0, 234, 0.3) !important;
}

.pinned-card {
  border-color: rgba(255, 64, 129, 0.5);
}
</style>

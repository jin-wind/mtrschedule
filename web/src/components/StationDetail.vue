<script setup lang="ts">
import { computed } from 'vue'
import type { Station } from '@/models/Station'
import type { Train } from '@/models/Train'
import { useStationStore } from '@/stores/stationStore'

interface Props {
  station: Station | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  back: []
}>()

const stationStore = useStationStore()

// Group trains by platform
const trainsByPlatform = computed(() => {
  if (!props.station) return new Map()

  const grouped = new Map<string, Train[]>()
  props.station.nextTrains.forEach((train) => {
    if (!grouped.has(train.platform)) {
      grouped.set(train.platform, [])
    }
    grouped.get(train.platform)!.push(train)
  })
  return grouped
})

function refresh() {
  if (props.station) {
    stationStore.fetchStationSchedule(props.station.stationId)
  }
}

function togglePin() {
  if (props.station) {
    stationStore.togglePinStation(props.station.stationId)
  }
}
</script>

<template>
  <div v-if="station">
    <v-btn icon="mdi-arrow-left" variant="text" @click="emit('back')" class="mb-4"></v-btn>

    <v-card class="mb-4 detail-card" elevation="8">
      <v-card-title class="text-h4 d-flex justify-space-between align-center">
        <span>{{ station.stationName }}</span>
        <v-btn
          :icon="station.isPinned ? 'mdi-pin' : 'mdi-pin-outline'"
          :color="station.isPinned ? 'pink' : 'default'"
          variant="text"
          @click="togglePin"
        ></v-btn>
      </v-card-title>
      <v-card-subtitle>站台 ID: {{ station.stationId }}</v-card-subtitle>

      <v-card-actions>
        <v-btn prepend-icon="mdi-refresh" :loading="stationStore.isLoading" @click="refresh">
          刷新
        </v-btn>
        <v-spacer></v-spacer>
        <v-chip v-if="station.nextTrains.length > 0" color="success">
          {{ station.nextTrains.length }} 班列車
        </v-chip>
      </v-card-actions>
    </v-card>

    <!-- Error Message -->
    <v-alert v-if="stationStore.error" type="error" class="mb-4" closable>
      {{ stationStore.error }}
    </v-alert>

    <!-- Loading Skeleton -->
    <v-card v-if="stationStore.isLoading" class="mb-4">
      <v-skeleton-loader type="list-item-avatar-three-line"></v-skeleton-loader>
      <v-skeleton-loader type="list-item-avatar-three-line"></v-skeleton-loader>
    </v-card>

    <!-- No Trains Message -->
    <v-card v-else-if="station.nextTrains.length === 0" class="text-center pa-8">
      <v-icon size="64" color="grey">mdi-train-variant-off</v-icon>
      <p class="text-h6 text-grey mt-4">暫無列車資料</p>
    </v-card>

    <!-- Trains by Platform -->
    <div v-else>
      <v-card
        v-for="[platform, trains] in trainsByPlatform"
        :key="platform"
        class="mb-4 platform-card"
        elevation="6"
      >
        <v-card-title class="platform-header">
          <v-icon>mdi-subway-variant</v-icon>
          {{ platform }}
        </v-card-title>

        <v-list lines="three">
          <v-list-item
            v-for="train in trains"
            :key="train.trainId"
            class="train-item"
            :class="{ 'arriving-train': train.timeToArrival === 0 }"
          >
            <template v-slot:prepend>
              <v-avatar :color="train.timeToArrival === 0 ? 'success' : 'primary'" size="56">
                <v-icon>{{
                  train.isDoubleCar ? 'mdi-train-car-passenger' : 'mdi-train-car'
                }}</v-icon>
              </v-avatar>
            </template>

            <v-list-item-title class="text-h6">
              <v-chip color="secondary" size="small" class="mr-2">{{ train.routeNumber }}</v-chip>
              {{ train.destination }}
            </v-list-item-title>

            <v-list-item-subtitle>
              <v-icon size="small">mdi-clock-outline</v-icon>
              {{ train.eta }}
              <v-chip
                v-if="train.timeToArrival === 0"
                color="success"
                size="x-small"
                class="ml-2 pulse-animation"
              >
                正在到站
              </v-chip>
              <v-chip v-else color="info" size="x-small" class="ml-2">
                {{ train.timeToArrival }} 分鐘
              </v-chip>
            </v-list-item-subtitle>

            <v-list-item-subtitle>
              <v-chip size="x-small" variant="outlined">
                {{ train.isDoubleCar ? '雙車廂' : '單車廂' }}
              </v-chip>
            </v-list-item-subtitle>
          </v-list-item>
        </v-list>
      </v-card>
    </div>
  </div>
</template>

<style scoped>
.detail-card {
  background: linear-gradient(135deg, rgba(98, 0, 234, 0.1) 0%, rgba(3, 218, 198, 0.1) 100%);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.platform-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.platform-card:hover {
  border-color: rgba(98, 0, 234, 0.3);
  box-shadow: 0 4px 16px rgba(98, 0, 234, 0.2) !important;
}

.platform-header {
  background: linear-gradient(90deg, rgba(98, 0, 234, 0.2) 0%, rgba(3, 218, 198, 0.2) 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.train-item {
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  transition: background-color 0.2s ease;
}

.train-item:hover {
  background-color: rgba(98, 0, 234, 0.1);
}

.train-item:last-child {
  border-bottom: none;
}

.arriving-train {
  background: linear-gradient(90deg, rgba(76, 175, 80, 0.1) 0%, transparent 100%);
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.pulse-animation {
  animation: pulse 2s ease-in-out infinite;
}
</style>

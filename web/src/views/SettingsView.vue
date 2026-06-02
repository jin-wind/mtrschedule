<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStationStore } from '@/stores/stationStore'
import { useTheme } from 'vuetify'

const router = useRouter()
const stationStore = useStationStore()
const theme = useTheme()

const isDarkMode = ref(true)

onMounted(() => {
  isDarkMode.value = theme.global.name.value === 'dark'
})

function toggleTheme() {
  theme.global.name.value = isDarkMode.value ? 'dark' : 'light'
  localStorage.setItem('theme', theme.global.name.value)
}

function resetPinnedStations() {
  stationStore.resetPinnedStations()
}

function goBack() {
  router.push('/')
}
</script>

<template>
  <v-app>
    <v-app-bar color="primary">
      <v-btn icon="mdi-arrow-left" @click="goBack"></v-btn>
      <v-toolbar-title>設定</v-toolbar-title>
    </v-app-bar>

    <v-main>
      <v-container>
        <v-card class="settings-card" elevation="8">
          <v-card-title class="text-h5">外觀設定</v-card-title>

          <v-list>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon>mdi-theme-light-dark</v-icon>
              </template>
              <v-list-item-title>深色模式</v-list-item-title>
              <template v-slot:append>
                <v-switch v-model="isDarkMode" @change="toggleTheme" hide-details></v-switch>
              </template>
            </v-list-item>
          </v-list>
        </v-card>

        <v-card class="settings-card mt-4" elevation="8">
          <v-card-title class="text-h5">資料設定</v-card-title>

          <v-list>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon>mdi-pin-off</v-icon>
              </template>
              <v-list-item-title>重置置頂站點</v-list-item-title>
              <v-list-item-subtitle>清除所有置頂及排序設定</v-list-item-subtitle>
              <template v-slot:append>
                <v-btn color="error" variant="outlined" @click="resetPinnedStations">
                  重置
                </v-btn>
              </template>
            </v-list-item>
          </v-list>
        </v-card>

        <v-card class="settings-card mt-4" elevation="8">
          <v-card-title class="text-h5">關於</v-card-title>

          <v-card-text>
            <p class="text-body-1 mb-2">
              <strong>輕鐵時刻表</strong>
            </p>
            <p class="text-body-2 text-grey">版本: 3.0.0 (Web 版)</p>
            <p class="text-body-2 text-grey">
              一款輕鐵時刻表 Web 應用，提供即時班次、站點收藏等功能。
            </p>
            <p class="text-body-2 text-grey mt-4">使用 Vue 3 + Vuetify 構建，支援深色主題。</p>
          </v-card-text>
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>

<style scoped>
.settings-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}
</style>

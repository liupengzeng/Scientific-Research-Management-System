<template>
  <div class="layout-container">
    <Sidebar />
    <div class="main-container" :class="{ 'sidebar-collapsed': appStore.sidebarCollapsed }">
      <Header />
      <AppMain />
    </div>
  </div>
</template>

<script setup>
import { useAppStore } from '@/stores/app'
import Sidebar from './components/Sidebar.vue'
import Header from './components/Header.vue'
import AppMain from './components/AppMain.vue'

const appStore = useAppStore()
</script>

<style lang="scss" scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 200px;
  transition: margin-left 0.3s;

  &.sidebar-collapsed {
    margin-left: 64px;
  }
}
</style>


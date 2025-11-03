import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    // 侧边栏是否收起
    sidebarCollapsed: false,
    // 设备类型（mobile/desktop）
    device: 'desktop',
    // 页面加载状态
    loading: false,
    // 全屏状态
    fullscreen: false,
    // 主题颜色
    theme: '#409EFF'
  }),

  getters: {
    // 侧边栏是否展开
    sidebarOpened: (state) => {
      return !state.sidebarCollapsed
    }
  },

  actions: {
    // 切换侧边栏
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },

    // 设置侧边栏状态
    setSidebarCollapsed(collapsed) {
      this.sidebarCollapsed = collapsed
    },

    // 设置设备类型
    setDevice(device) {
      this.device = device
    },

    // 设置加载状态
    setLoading(loading) {
      this.loading = loading
    },

    // 切换全屏
    toggleFullscreen() {
      this.fullscreen = !this.fullscreen
    },

    // 设置主题
    setTheme(theme) {
      this.theme = theme
    }
  }
})


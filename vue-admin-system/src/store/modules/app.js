import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebar: {
      collapsed: false
    },
    device: 'desktop',
    theme: 'light',
    language: 'zh-CN',
    size: 'default'
  }),

  getters: {
    isCollapse: (state) => state.sidebar.collapsed,
    isMobile: (state) => state.device === 'mobile'
  },

  actions: {
    toggleSidebar() {
      this.sidebar.collapsed = !this.sidebar.collapsed
    },

    closeSidebar() {
      this.sidebar.collapsed = true
    },

    toggleDevice(device) {
      this.device = device
    },

    setTheme(theme) {
      this.theme = theme
      document.documentElement.setAttribute('data-theme', theme)
    },

    setLanguage(language) {
      this.language = language
    },

    setSize(size) {
      this.size = size
    }
  },

  persist: {
    key: 'app-store',
    paths: ['sidebar', 'theme', 'language', 'size']
  }
})

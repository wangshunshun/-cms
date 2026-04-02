import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import 'nprogress/nprogress.css'

import App from './App.vue'
import router from './router'
import { setupI18n } from './lang'
import '@/assets/styles/index.scss'
import * as directives from './directives'

const app = createApp(App)

// Pinia
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

// 国际化
setupI18n(app)

// Element Plus
app.use(ElementPlus)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册自定义指令
Object.keys(directives).forEach(key => {
  app.directive(key, directives[key])
})

const bootstrap = async () => {
  // 仅在开启开关时注入 Mock，默认走真实后端接口
  if (import.meta.env.DEV && import.meta.env.VITE_USE_MOCK === 'true') {
    const { setupMock } = await import('./mock')
    setupMock()
  }

  app.use(router)
  app.mount('#app')
}

bootstrap()

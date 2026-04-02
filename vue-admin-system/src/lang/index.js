import { createI18n } from 'vue-i18n'
import zhCN from './zh-CN'
import enUS from './en-US'
import elZhCn from 'element-plus/dist/locale/zh-cn.mjs'
import elEnUs from 'element-plus/dist/locale/en.mjs'

const messages = {
  'zh-CN': zhCN,
  'en-US': enUS
}

// Element Plus 语言映射
const elementLocaleMap = {
  'zh-CN': elZhCn,
  'en-US': elEnUs
}

const i18n = createI18n({
  legacy: false,
  locale: localStorage.getItem('app-store')
    ? JSON.parse(localStorage.getItem('app-store'))?.language || 'zh-CN'
    : 'zh-CN',
  fallbackLocale: 'zh-CN',
  messages
})

export function setupI18n(app) {
  app.use(i18n)
}

export function getElementLocale(locale) {
  return elementLocaleMap[locale] || elZhCn
}

export default i18n

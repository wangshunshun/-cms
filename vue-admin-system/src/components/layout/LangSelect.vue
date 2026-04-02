<template>
  <el-dropdown @command="handleCommand" trigger="click">
    <span class="lang-trigger">
      <span class="lang-text">{{ language === 'zh-CN' ? '中' : 'EN' }}</span>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="zh-CN" :disabled="language === 'zh-CN'">
          简体中文
        </el-dropdown-item>
        <el-dropdown-item command="en-US" :disabled="language === 'en-US'">
          English
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/store/modules/app'

const { locale } = useI18n()
const appStore = useAppStore()

const language = computed(() => appStore.language)

const handleCommand = (lang) => {
  locale.value = lang
  appStore.setLanguage(lang)
}
</script>

<style lang="scss" scoped>
.lang-trigger {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.lang-text {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  padding: 4px 10px;
  border-radius: var(--radius-sm);
  background: var(--bg-page);
  transition: all var(--transition-fast);

  &:hover {
    color: var(--primary-color);
    background: var(--primary-bg);
  }
}
</style>

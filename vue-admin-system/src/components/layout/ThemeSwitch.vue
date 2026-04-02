<template>
  <el-tooltip :content="theme === 'dark' ? $t('navbar.lightMode') : $t('navbar.darkMode')" placement="bottom">
    <el-icon class="theme-icon" @click="toggleTheme">
      <Sunny v-if="theme === 'dark'" />
      <Moon v-else />
    </el-icon>
  </el-tooltip>
</template>

<script setup>
import { computed } from 'vue'
import { useAppStore } from '@/store/modules/app'

const appStore = useAppStore()

const theme = computed(() => appStore.theme)

const toggleTheme = () => {
  const newTheme = theme.value === 'dark' ? 'light' : 'dark'
  appStore.setTheme(newTheme)
}
</script>

<style lang="scss" scoped>
.theme-icon {
  font-size: 18px;
  cursor: pointer;

  &:hover {
    color: #409eff;
  }
}
</style>

<template>
  <el-tooltip :content="isFullscreen ? $t('navbar.exitFullscreen') : $t('navbar.fullscreen')" placement="bottom">
    <el-icon class="screenfull-icon" @click="toggle">
      <FullScreen v-if="!isFullscreen" />
      <Minus v-else />
    </el-icon>
  </el-tooltip>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const isFullscreen = ref(false)

const toggle = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}

onMounted(() => {
  document.addEventListener('fullscreenchange', handleFullscreenChange)
})

onUnmounted(() => {
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
})
</script>

<style lang="scss" scoped>
.screenfull-icon {
  font-size: 18px;
  cursor: pointer;

  &:hover {
    color: #409eff;
  }
}
</style>

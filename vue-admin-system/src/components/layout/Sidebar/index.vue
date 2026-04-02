<template>
  <div class="sidebar-container" :class="{ 'is-collapse': isCollapse }">
    <div class="logo">
      <el-icon class="logo-icon"><Promotion /></el-icon>
      <span v-show="!isCollapse" class="logo-title">Vue Admin</span>
    </div>
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        :unique-opened="true"
        mode="vertical"
        router
      >
        <SidebarItem
          v-for="route in routes"
          :key="route.path"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/store/modules/app'
import { useUserStore } from '@/store/modules/user'
import SidebarItem from './SidebarItem.vue'

const route = useRoute()
const appStore = useAppStore()
const userStore = useUserStore()

const isCollapse = computed(() => appStore.isCollapse)
const routes = computed(() => userStore.routes)
const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})
</script>

<style lang="scss" scoped>
.sidebar-container {
  width: 220px;
  height: 100%;
  background: linear-gradient(180deg, #0f172a 0%, #1e293b 100%);
  transition: width var(--transition-normal);
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1001;
  overflow: hidden;

  &.is-collapse {
    width: 64px;

    .logo-title {
      display: none;
    }
  }

  .logo {
    height: 56px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 16px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);

    .logo-icon {
      font-size: 28px;
      color: var(--primary-color);
      flex-shrink: 0;
    }

    .logo-title {
      margin-left: 12px;
      color: #fff;
      font-size: 18px;
      font-weight: 600;
      white-space: nowrap;
      letter-spacing: 0.5px;
    }
  }

  :deep(.el-scrollbar) {
    height: calc(100% - 56px);
  }

  :deep(.el-menu) {
    border-right: none;
    background-color: transparent;
    padding: 8px;
  }

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    height: 44px;
    line-height: 44px;
    border-radius: var(--radius-md);
    margin: 2px 0;
    color: rgba(255, 255, 255, 0.65);
    transition: all var(--transition-fast);

    &:hover {
      background-color: rgba(255, 255, 255, 0.08);
      color: #fff;
    }

    .el-icon {
      font-size: 18px;
      margin-right: 12px;
    }
  }

  :deep(.el-menu-item.is-active) {
    background: linear-gradient(135deg, var(--primary-color) 0%, #6b93fc 100%);
    color: #fff;
    box-shadow: 0 4px 12px rgba(74, 124, 250, 0.3);
  }

  :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
    color: #fff;
  }

  :deep(.el-sub-menu .el-menu-item) {
    padding-left: 52px !important;
    min-width: auto;
  }

  :deep(.scrollbar-wrapper) {
    overflow-x: hidden !important;
  }
}
</style>

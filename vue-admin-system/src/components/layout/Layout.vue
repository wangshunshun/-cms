<template>
  <el-container class="layout-container">
    <Sidebar />
    <el-container class="main-container" :class="{ 'sidebar-collapsed': isCollapse }">
      <Navbar />
      <TagsView />
      <el-main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <keep-alive :include="cachedViews">
              <component :is="Component" :key="key" />
            </keep-alive>
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/store/modules/app'
import { useTagsViewStore } from '@/store/modules/tagsView'
import Sidebar from './Sidebar/index.vue'
import Navbar from './Navbar.vue'
import TagsView from './TagsView.vue'

const route = useRoute()
const appStore = useAppStore()
const tagsViewStore = useTagsViewStore()

const isCollapse = computed(() => appStore.isCollapse)
const cachedViews = computed(() => tagsViewStore.cachedViews)
const key = computed(() => route.path)
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  width: 100%;
}

.main-container {
  margin-left: 220px;
  transition: margin-left var(--transition-normal);
  display: flex;
  flex-direction: column;
  min-height: 100vh;

  &.sidebar-collapsed {
    margin-left: 64px;
  }
}

.app-main {
  background-color: var(--bg-page);
  padding: 20px;
  overflow: auto;
  flex: 1;
  min-height: calc(100vh - 56px - 40px);
}
</style>

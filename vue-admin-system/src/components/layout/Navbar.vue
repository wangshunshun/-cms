<template>
  <div class="navbar">
    <div class="left-menu">
      <el-icon class="hamburger" @click="toggleSidebar">
        <Fold v-if="!isCollapse" />
        <Expand v-else />
      </el-icon>
      <Breadcrumb class="breadcrumb" />
    </div>

    <div class="right-menu">
      <ScreenFull class="right-menu-item" />
      <LangSelect class="right-menu-item" />
      <ThemeSwitch class="right-menu-item" />

      <el-dropdown class="avatar-container right-menu-item" trigger="click">
        <div class="avatar-wrapper">
          <el-avatar :size="30" :src="avatar">
            <el-icon><UserFilled /></el-icon>
          </el-avatar>
          <span class="username">{{ username }}</span>
          <el-icon class="caret"><CaretBottom /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <router-link to="/profile">
              <el-dropdown-item>{{ $t('navbar.profile') }}</el-dropdown-item>
            </router-link>
            <el-dropdown-item divided @click="handleLogout">
              {{ $t('navbar.logout') }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/store/modules/app'
import { useUserStore } from '@/store/modules/user'
import Breadcrumb from './Breadcrumb.vue'
import ScreenFull from './ScreenFull.vue'
import LangSelect from './LangSelect.vue'
import ThemeSwitch from './ThemeSwitch.vue'

const { t } = useI18n()
const appStore = useAppStore()
const userStore = useUserStore()

const isCollapse = computed(() => appStore.isCollapse)
const username = computed(() => userStore.username)
const avatar = computed(() => userStore.avatar)

const toggleSidebar = () => {
  appStore.toggleSidebar()
}

const handleLogout = async () => {
  await userStore.logout()
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 56px;
  background-color: var(--bg-card);
  border-bottom: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;

  .left-menu {
    display: flex;
    align-items: center;

    .hamburger {
      font-size: 20px;
      cursor: pointer;
      padding: 8px;
      border-radius: var(--radius-md);
      color: var(--text-secondary);
      transition: all var(--transition-fast);

      &:hover {
        background: var(--bg-page);
        color: var(--primary-color);
      }
    }

    .breadcrumb {
      margin-left: 8px;
    }
  }

  .right-menu {
    display: flex;
    align-items: center;
    gap: 4px;

    .right-menu-item {
      padding: 8px 12px;
      cursor: pointer;
      border-radius: var(--radius-md);
      color: var(--text-secondary);
      transition: all var(--transition-fast);

      &:hover {
        background: var(--bg-page);
        color: var(--primary-color);
      }
    }

    .avatar-container {
      margin-left: 8px;

      .avatar-wrapper {
        display: flex;
        align-items: center;
        cursor: pointer;
        padding: 6px 12px;
        border-radius: var(--radius-lg);
        transition: background var(--transition-fast);

        &:hover {
          background: var(--bg-page);
        }

        .username {
          margin: 0 8px;
          font-size: 14px;
          color: var(--text-primary);
          font-weight: 500;
        }

        .caret {
          font-size: 12px;
          color: var(--text-tertiary);
        }
      }
    }
  }
}
</style>

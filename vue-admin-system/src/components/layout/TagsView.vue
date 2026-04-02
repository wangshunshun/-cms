<template>
  <div class="tags-view-container">
    <el-scrollbar class="tags-view-wrapper">
      <router-link
        v-for="tag in visitedViews"
        :key="tag.path"
        :to="{ path: tag.path, query: tag.query }"
        class="tags-view-item"
        :class="isActive(tag) ? 'active' : ''"
        @contextmenu.prevent="openMenu(tag, $event)"
      >
        {{ tag.title }}
        <el-icon
          v-if="!tag.affix"
          class="close-icon"
          @click.prevent.stop="closeSelectedTag(tag)"
        >
          <Close />
        </el-icon>
      </router-link>
    </el-scrollbar>

    <ul v-show="visible" class="context-menu" :style="{ left: left + 'px', top: top + 'px' }">
      <li @click="refreshSelectedTag(selectedTag)">{{ $t('tagsView.refresh') }}</li>
      <li v-if="!selectedTag?.affix" @click="closeSelectedTag(selectedTag)">
        {{ $t('tagsView.close') }}
      </li>
      <li @click="closeOthersTags">{{ $t('tagsView.closeOthers') }}</li>
      <li @click="closeAllTags">{{ $t('tagsView.closeAll') }}</li>
    </ul>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTagsViewStore } from '@/store/modules/tagsView'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const tagsViewStore = useTagsViewStore()

const visitedViews = computed(() => tagsViewStore.visitedViews)

const visible = ref(false)
const top = ref(0)
const left = ref(0)
const selectedTag = ref(null)

const isActive = (tag) => {
  return tag.path === route.path
}

const addViewTags = () => {
  if (route.name) {
    tagsViewStore.addView(route)
  }
}

const closeSelectedTag = (view) => {
  tagsViewStore.delView(view).then(({ visitedViews }) => {
    if (isActive(view)) {
      toLastView(visitedViews, view)
    }
  })
}

const closeOthersTags = () => {
  tagsViewStore.delOthersViews(selectedTag.value)
  router.push(selectedTag.value.path)
}

const closeAllTags = () => {
  tagsViewStore.delAllViews()
  router.push('/')
}

const refreshSelectedTag = (view) => {
  tagsViewStore.delCachedView(view).then(() => {
    router.replace({ path: '/redirect' + view.path })
  })
}

const toLastView = (visitedViews, view) => {
  const latestView = visitedViews.slice(-1)[0]
  if (latestView) {
    router.push(latestView.path)
  } else {
    router.push('/')
  }
}

const openMenu = (tag, e) => {
  const menuMinWidth = 105
  const offsetLeft = document.body.getBoundingClientRect().left
  const offsetWidth = document.body.offsetWidth
  const maxLeft = offsetWidth - menuMinWidth
  const l = e.clientX - offsetLeft + 15

  left.value = l > maxLeft ? maxLeft : l
  top.value = e.clientY
  visible.value = true
  selectedTag.value = tag
}

const closeMenu = () => {
  visible.value = false
}

watch(route, () => {
  addViewTags()
})

watch(visible, (value) => {
  if (value) {
    document.body.addEventListener('click', closeMenu)
  } else {
    document.body.removeEventListener('click', closeMenu)
  }
})

onMounted(() => {
  addViewTags()
})
</script>

<style lang="scss" scoped>
.tags-view-container {
  height: 40px;
  background: var(--bg-card);
  border-bottom: 1px solid var(--border-light);

  .tags-view-wrapper {
    display: flex;
    align-items: center;
    height: 100%;
    padding: 0 16px;

    .tags-view-item {
      display: inline-flex;
      align-items: center;
      position: relative;
      cursor: pointer;
      height: 28px;
      line-height: 28px;
      border: 1px solid var(--border-color);
      color: var(--text-secondary);
      background: var(--bg-page);
      padding: 0 12px;
      font-size: 13px;
      margin-left: 8px;
      border-radius: var(--radius-md);
      transition: all var(--transition-fast);

      &:first-of-type {
        margin-left: 0;
      }

      &.active {
        background: var(--primary-color);
        color: #fff;
        border-color: var(--primary-color);
        box-shadow: 0 2px 8px rgba(74, 124, 250, 0.3);
      }

      &:hover:not(.active) {
        color: var(--primary-color);
        border-color: var(--primary-light);
      }

      .close-icon {
        margin-left: 6px;
        font-size: 12px;
        border-radius: 50%;
        width: 16px;
        height: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all var(--transition-fast);

        &:hover {
          background: rgba(0, 0, 0, 0.1);
          color: #f56c6c;
        }
      }
    }
  }

  .context-menu {
    margin: 0;
    background: var(--bg-card);
    z-index: 3000;
    position: fixed;
    list-style-type: none;
    padding: 6px;
    border-radius: var(--radius-md);
    font-size: 13px;
    color: var(--text-primary);
    box-shadow: var(--shadow-lg);
    border: 1px solid var(--border-light);
    min-width: 120px;

    li {
      margin: 0;
      padding: 8px 12px;
      cursor: pointer;
      border-radius: var(--radius-sm);
      transition: background var(--transition-fast);

      &:hover {
        background: var(--bg-page);
      }
    }
  }
}
</style>

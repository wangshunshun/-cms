<template>
  <template v-if="!item.meta?.hidden">
    <!-- 只有一个可显示的子菜单，直接显示子菜单项 -->
    <template v-if="hasOneShowingChild">
      <el-menu-item :index="resolvePath(onlyOneChild.path)">
        <el-icon v-if="onlyOneChild.meta?.icon || item.meta?.icon">
          <component :is="onlyOneChild.meta?.icon || item.meta.icon" />
        </el-icon>
        <template #title>
          <span>{{ onlyOneChild.meta?.title || onlyOneChild.name }}</span>
        </template>
      </el-menu-item>
    </template>

    <!-- 无子菜单 -->
    <template v-else-if="!hasChildren">
      <el-menu-item :index="resolvePath(item.path)">
        <el-icon v-if="item.meta?.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <template #title>
          <span>{{ item.meta?.title || item.name }}</span>
        </template>
      </el-menu-item>
    </template>

    <!-- 有多个子菜单 -->
    <template v-else>
      <el-sub-menu :index="resolvePath(item.path)">
        <template #title>
          <el-icon v-if="item.meta?.icon">
            <component :is="item.meta.icon" />
          </el-icon>
          <span>{{ item.meta?.title || item.name }}</span>
        </template>

        <SidebarItem
          v-for="child in visibleChildren"
          :key="child.path"
          :item="child"
          :base-path="resolvePath(item.path)"
        />
      </el-sub-menu>
    </template>
  </template>
</template>

<script setup>
import { computed } from 'vue'
import path from 'path-browserify'

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  basePath: {
    type: String,
    default: ''
  }
})

// 过滤隐藏的子菜单
const visibleChildren = computed(() => {
  return (props.item.children || []).filter(child => !child.meta?.hidden)
})

// 是否有子菜单
const hasChildren = computed(() => {
  return visibleChildren.value.length > 0
})

// 只有一个可显示的子菜单
const hasOneShowingChild = computed(() => {
  if (visibleChildren.value.length === 1) {
    return true
  }
  // 如果没有子菜单但当前项有 redirect，也显示为单个菜单
  if (visibleChildren.value.length === 0 && !props.item.redirect) {
    return true
  }
  return false
})

// 唯一的子菜单
const onlyOneChild = computed(() => {
  if (visibleChildren.value.length === 1) {
    const child = visibleChildren.value[0]
    // 如果子路由路径为空，使用父路由的 meta
    if (child.path === '' || child.path === undefined) {
      return {
        ...child,
        path: props.item.path,
        meta: {
          ...child.meta,
          icon: child.meta?.icon || props.item.meta?.icon,
          title: child.meta?.title || props.item.meta?.title
        }
      }
    }
    return child
  }
  return props.item
})

// 解析路径
const resolvePath = (routePath) => {
  if (!routePath) return props.basePath
  // 如果已经是完整路径
  if (routePath.startsWith('/')) {
    return routePath
  }
  return path.resolve(props.basePath, routePath)
}
</script>

<template>
  <el-breadcrumb class="breadcrumb" separator="/">
    <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="item.path">
      <span v-if="index === breadcrumbs.length - 1" class="no-redirect">
        {{ item.meta?.title || item.name }}
      </span>
      <a v-else @click.prevent="handleLink(item)">{{ item.meta?.title || item.name }}</a>
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const breadcrumbs = ref([])

const getBreadcrumbs = () => {
  const matched = route.matched.filter(item => item.meta?.title)
  breadcrumbs.value = matched
}

const handleLink = (item) => {
  router.push(item.path)
}

watch(
  () => route.path,
  () => {
    getBreadcrumbs()
  },
  { immediate: true }
)
</script>

<style lang="scss" scoped>
.breadcrumb {
  .no-redirect {
    color: #97a8be;
    cursor: text;
  }

  a {
    color: #304156;
    cursor: pointer;

    &:hover {
      color: #409eff;
    }
  }
}
</style>

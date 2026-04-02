<template>
  <el-config-provider :locale="elementLocale">
    <router-view />
  </el-config-provider>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { getElementLocale } from './lang'

const { locale } = useI18n()

const elementLocale = computed(() => getElementLocale(locale.value))
</script>

<style>
#app {
  width: 100%;
  height: 100%;
}
</style>

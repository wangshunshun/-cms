<template>
  <div class="error-container">
    <div class="error-content">
      <h1 class="error-code">404</h1>
      <h2 class="error-message">{{ $t('error.notFound') }}</h2>
      <div class="error-actions">
        <el-button type="primary" @click="goHome">{{ $t('error.backHome') }}</el-button>
        <el-button @click="goBack">{{ $t('error.goBack') }}</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style lang="scss" scoped>
.error-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}

.error-content {
  text-align: center;
}

.error-code {
  font-size: 120px;
  font-weight: 600;
  color: #409eff;
  margin: 0;
  line-height: 1;
}

.error-message {
  font-size: 24px;
  color: #333;
  margin: 20px 0;
}

.error-actions {
  .el-button + .el-button {
    margin-left: 15px;
  }
}
</style>

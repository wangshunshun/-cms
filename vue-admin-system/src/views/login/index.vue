<template>
  <div class="login-container">
    <div class="login-background">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>

    <div class="login-content">
      <div class="login-card">
        <div class="login-header">
          <div class="logo">
            <el-icon :size="40"><Monitor /></el-icon>
          </div>
          <h1>{{ $t('login.title') }}</h1>
          <p>企业级后台管理系统</p>
        </div>

        <el-form ref="formRef" :model="loginForm" :rules="rules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              :placeholder="$t('login.usernamePlaceholder')"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              :placeholder="$t('login.passwordPlaceholder')"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-checkbox v-model="rememberMe">{{ $t('login.rememberMe') }}</el-checkbox>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="login-btn"
              @click="handleLogin"
            >
              {{ loading ? $t('login.login') + '...' : $t('login.login') }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <span>测试账号: admin / password</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { User, Lock, Monitor } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'

const { t } = useI18n()
const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)
const rememberMe = ref(true)

const loginForm = reactive({
  username: 'admin',
  password: 'password'
})

const rules = {
  username: [{ required: true, message: t('login.usernamePlaceholder'), trigger: 'blur' }],
  password: [{ required: true, message: t('login.passwordPlaceholder'), trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true

  try {
    await userStore.login(loginForm)
    await userStore.getUserInfo()
    ElMessage.success(t('login.loginSuccess'))
    router.push('/dashboard')
  } catch (error) {
    ElMessage.error(error.message || t('login.loginFailed'))
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  z-index: 0;

  .bg-shape {
    position: absolute;
    border-radius: 50%;
    background: linear-gradient(135deg, rgba(74, 124, 250, 0.08) 0%, rgba(74, 124, 250, 0.02) 100%);
    animation: float 20s infinite ease-in-out;
  }

  .shape-1 {
    width: 600px;
    height: 600px;
    top: -200px;
    right: -200px;
  }

  .shape-2 {
    width: 400px;
    height: 400px;
    bottom: -100px;
    left: -100px;
  }

  .shape-3 {
    width: 300px;
    height: 300px;
    top: 40%;
    left: 30%;
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(-30px, 30px);
  }
}

.login-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 400px;
  padding: 20px;
}

.login-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: 48px 36px;
  box-shadow: var(--shadow-lg), 0 0 0 1px rgba(0, 0, 0, 0.02);
  animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 36px;

  .logo {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 60px;
    height: 60px;
    background: linear-gradient(135deg, var(--primary-color) 0%, #6b93fc 100%);
    border-radius: var(--radius-lg);
    color: white;
    margin-bottom: 20px;
    box-shadow: 0 8px 24px rgba(74, 124, 250, 0.25);
  }

  h1 {
    font-size: 24px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 8px 0;
    letter-spacing: 0.5px;
  }

  p {
    font-size: 14px;
    color: var(--text-tertiary);
    margin: 0;
  }
}

.login-form {
  :deep(.el-input__wrapper) {
    border-radius: var(--radius-md);
    box-shadow: none;
    border: 1px solid var(--border-color);
    transition: all var(--transition-fast);

    &:hover {
      border-color: #c0c4cc;
    }

    &.is-focus {
      border-color: var(--primary-color);
      box-shadow: 0 0 0 3px var(--primary-bg);
    }
  }

  :deep(.el-form-item) {
    margin-bottom: 20px;
  }

  :deep(.el-checkbox__label) {
    color: var(--text-secondary);
    font-size: 13px;
  }
}

.login-btn {
  width: 100%;
  height: 46px;
  border-radius: var(--radius-md);
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 1px;
  background: linear-gradient(135deg, var(--primary-color) 0%, #6b93fc 100%);
  border: none;
  transition: all var(--transition-fast);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(74, 124, 250, 0.35);
  }

  &:active {
    transform: translateY(0);
  }
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--border-light);

  span {
    font-size: 12px;
    color: var(--text-tertiary);
    background: var(--bg-page);
    padding: 4px 12px;
    border-radius: var(--radius-sm);
  }
}

// 响应式适配
@media (max-width: 480px) {
  .login-card {
    padding: 36px 24px;
  }

  .login-header {
    h1 {
      font-size: 22px;
    }
  }
}
</style>

<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>个人信息</span>
          </template>
          <div class="avatar-section">
            <el-avatar :size="100" :src="userInfo.avatar">
              <el-icon :size="50"><UserFilled /></el-icon>
            </el-avatar>
            <div class="user-info">
              <h3>{{ userInfo.nickname }}</h3>
              <p>{{ userInfo.roles?.join(', ') }}</p>
            </div>
          </div>
          <el-descriptions :column="1" border class="user-descriptions">
            <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userInfo.phone }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ userInfo.createTime }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>{{ $t('profile.basicInfo') }}</span>
          </template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item :label="$t('system.user.nickname')" prop="nickname">
              <el-input v-model="form.nickname" />
            </el-form-item>
            <el-form-item :label="$t('system.user.email')" prop="email">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item :label="$t('system.user.phone')" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave">{{ $t('profile.save') }}</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="hover" class="password-card">
          <template #header>
            <span>{{ $t('profile.changePassword') }}</span>
          </template>
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
            <el-form-item :label="$t('profile.oldPassword')" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item :label="$t('profile.newPassword')" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item :label="$t('profile.confirmPassword')" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePwd">{{ $t('profile.save') }}</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { updateUser, changePassword } from '@/api/user'

const { t } = useI18n()
const userStore = useUserStore()
const formRef = ref(null)
const pwdFormRef = ref(null)

const userInfo = computed(() => userStore.userInfo)

const form = reactive({
  nickname: userInfo.value.nickname || '',
  email: userInfo.value.email || '',
  phone: userInfo.value.phone || ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  nickname: [{ required: true, message: t('system.user.nickname'), trigger: 'blur' }],
  email: [{ type: 'email', message: 'Email格式不正确', trigger: 'blur' }]
}

const pwdRules = {
  oldPassword: [{ required: true, message: t('profile.oldPassword'), trigger: 'blur' }],
  newPassword: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: t('profile.confirmPassword'), trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleSave = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      await updateUser({ ...form, id: userInfo.value.id })
      ElMessage.success(t('common.success'))
    }
  })
}

const handleChangePwd = async () => {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      await changePassword(pwdForm)
      ElMessage.success(t('common.success'))
      pwdFormRef.value.resetFields()
    }
  })
}
</script>

<style lang="scss" scoped>
.profile-container {
  .avatar-section {
    text-align: center;
    padding: 20px 0;

    .user-info {
      margin-top: 15px;

      h3 {
        margin: 0;
        color: #333;
      }

      p {
        margin: 5px 0 0;
        color: #909399;
        font-size: 14px;
      }
    }
  }

  .user-descriptions {
    margin-top: 20px;
  }

  .password-card {
    margin-top: 20px;
  }
}
</style>

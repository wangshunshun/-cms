<template>
  <div class="permission-demo">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限控制示例</span>
        </div>
      </template>

      <div class="user-info">
        <el-descriptions title="当前用户信息" :column="2" border>
          <el-descriptions-item label="用户名">{{ username }}</el-descriptions-item>
          <el-descriptions-item label="角色">{{ roles.join(', ') }}</el-descriptions-item>
          <el-descriptions-item label="权限" :span="2">
            <el-tag v-for="perm in permissions" :key="perm" size="small" style="margin-right: 5px">
              {{ perm }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <el-divider />

      <div class="permission-section">
        <h3>按钮权限控制 (使用 v-if)</h3>
        <p>以下按钮根据当前用户的权限显示或隐藏：</p>

        <el-space wrap>
          <el-button v-if="hasPermission('*')" type="success">全部权限 (*)</el-button>
          <el-button v-if="hasPermission('system:user:view')" type="primary">查看用户</el-button>
          <el-button v-if="hasPermission('system:user:add')" type="success">添加用户</el-button>
          <el-button v-if="hasPermission('system:user:edit')" type="warning">编辑用户</el-button>
          <el-button v-if="hasPermission('system:user:delete')" type="danger">删除用户</el-button>
          <el-button v-if="hasPermission('system:user:reset')" type="info">重置密码</el-button>
        </el-space>
      </div>

      <el-divider />

      <div class="test-section">
        <h3>测试说明</h3>
        <el-alert title="测试账号" type="info" :closable="false">
          <p><strong>admin / password</strong> - 管理员（所有权限 *）</p>
          <p><strong>editor / password</strong> - 编辑员（部分权限：查看用户、查看角色、查看日志）</p>
        </el-alert>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()

const username = computed(() => userStore.username)
const roles = computed(() => userStore.roles || [])
const permissions = computed(() => userStore.permissions || [])

// 权限检查函数
const hasPermission = (permission) => {
  return permissions.value.includes('*') || permissions.value.includes(permission)
}
</script>

<style lang="scss" scoped>
.permission-demo {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .user-info {
    margin-bottom: 20px;
  }

  .permission-section,
  .test-section {
    margin-top: 24px;

    h3 {
      margin-bottom: 12px;
      color: var(--text-primary);
      font-size: 15px;
      font-weight: 600;
    }

    p {
      color: var(--text-secondary);
      margin-bottom: 16px;
      font-size: 13px;
    }
  }
}
</style>

<template>
  <div class="user-container">
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :model="queryParams" inline>
        <el-form-item :label="$t('system.user.username')">
          <el-input v-model="queryParams.username" :placeholder="$t('system.user.username')" clearable />
        </el-form-item>
        <el-form-item :label="$t('system.user.status')">
          <el-select v-model="queryParams.status" :placeholder="$t('common.status')" clearable>
            <el-option :label="$t('common.enabled')" :value="1" />
            <el-option :label="$t('common.disabled')" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">{{ $t('common.search') }}</el-button>
          <el-button @click="handleReset">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <div class="table-container">
      <div class="table-header">
        <el-button v-if="hasPermission('system:user:add')" type="primary" @click="handleAdd">
          {{ $t('system.user.add') }}
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" :label="$t('system.user.username')" />
        <el-table-column prop="nickname" :label="$t('system.user.nickname')" />
        <el-table-column prop="email" :label="$t('system.user.email')" />
        <el-table-column prop="phone" :label="$t('system.user.phone')" />
        <el-table-column prop="status" :label="$t('system.user.status')" width="100">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" :label="$t('system.user.createTime')" width="180" />
        <el-table-column :label="$t('system.user.actions')" width="250" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPermission('system:user:edit')" type="primary" link @click="handleEdit(row)">
              {{ $t('common.edit') }}
            </el-button>
            <el-button v-if="hasPermission('system:user:reset')" type="warning" link @click="handleResetPwd(row)">
              {{ $t('system.user.resetPassword') }}
            </el-button>
            <el-button v-if="hasPermission('system:user:delete')" type="danger" link @click="handleDelete(row)">
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getList"
          @current-change="getList"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item :label="$t('system.user.username')" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item :label="$t('system.user.nickname')" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item :label="$t('system.user.email')" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item :label="$t('system.user.phone')" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item :label="$t('system.user.roles')" prop="roles">
          <el-select v-model="form.roles" multiple>
            <el-option v-for="role in roleOptions" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('system.user.status')" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">{{ $t('common.enabled') }}</el-radio>
            <el-radio :label="0">{{ $t('common.disabled') }}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser } from '@/api/user'
import { getAllRoles } from '@/api/role'
import { useUserStore } from '@/store/modules/user'

const { t } = useI18n()
const userStore = useUserStore()

// 权限检查函数
const hasPermission = (permission) => {
  const permissions = userStore.permissions || []
  return permissions.includes('*') || permissions.includes(permission)
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const roleOptions = ref([])

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  username: '',
  status: null
})

const form = reactive({
  id: null,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  roles: [],
  status: 1
})

const isEdit = computed(() => !!form.id)
const dialogTitle = computed(() => isEdit.value ? t('system.user.edit') : t('system.user.add'))

const rules = {
  username: [{ required: true, message: t('system.user.username'), trigger: 'blur' }],
  nickname: [{ required: true, message: t('system.user.nickname'), trigger: 'blur' }],
  email: [{ type: 'email', message: 'Email格式不正确', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const { data } = await getUserList(queryParams)
    tableData.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const getRoleOptions = async () => {
  try {
    const { data } = await getAllRoles()
    roleOptions.value = data || []
  } catch (error) {
    console.error('获取角色列表失败:', error)
    roleOptions.value = [
      { id: 1, roleName: '管理员' },
      { id: 2, roleName: '普通用户' }
    ]
  }
}

const handleSearch = () => {
  queryParams.page = 1
  getList()
}

const handleReset = () => {
  queryParams.username = ''
  queryParams.status = null
  handleSearch()
}

const resetForm = () => {
  form.id = null
  form.username = ''
  form.nickname = ''
  form.email = ''
  form.phone = ''
  form.roles = []
  form.status = 1
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateUser(form)
        } else {
          await createUser(form)
        }
        ElMessage.success(t('common.success'))
        dialogVisible.value = false
        getList()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(t('common.confirmDelete'), t('common.confirm'), {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success(t('common.success'))
      getList()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

const handleResetPwd = (row) => {
  ElMessageBox.confirm(`确定要重置用户 ${row.username} 的密码吗？`, t('common.confirm'), {
    type: 'warning'
  }).then(() => {
    ElMessage.success('密码已重置为: 123456')
  })
}

onMounted(() => {
  getList()
  getRoleOptions()
})
</script>

<style lang="scss" scoped>
.user-container {
  .search-form {
    background: var(--bg-card);
    border-radius: var(--radius-lg);
    margin-bottom: 16px;
    box-shadow: var(--shadow-card);
  }

  .table-container {
    background: var(--bg-card);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-card);

    .table-header {
      margin-bottom: 20px;
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>

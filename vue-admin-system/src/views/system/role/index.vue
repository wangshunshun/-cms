<template>
  <div class="role-container">
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :model="queryParams" inline>
        <el-form-item :label="$t('system.role.roleName')">
          <el-input v-model="queryParams.roleName" :placeholder="$t('system.role.roleName')" clearable />
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
        <el-button v-if="hasPermission('system:role:add')" type="primary" @click="handleAdd">{{ $t('system.role.add') }}</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" :label="$t('system.role.roleName')" />
        <el-table-column prop="roleKey" :label="$t('system.role.roleKey')" />
        <el-table-column prop="description" :label="$t('system.role.description')" />
        <el-table-column prop="status" :label="$t('system.role.status')" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? $t('common.enabled') : $t('common.disabled') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" :label="$t('system.role.createTime')" width="180" />
        <el-table-column :label="$t('system.role.actions')" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPermission('system:role:edit')" type="primary" link @click="handleEdit(row)">{{ $t('common.edit') }}</el-button>
            <el-button v-if="hasPermission('system:role:permission')" type="warning" link @click="handlePermission(row)">{{ $t('system.role.assignPermissions') }}</el-button>
            <el-button v-if="hasPermission('system:role:delete')" type="danger" link @click="handleDelete(row)">{{ $t('common.delete') }}</el-button>
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
        <el-form-item :label="$t('system.role.roleName')" prop="roleName">
          <el-input v-model="form.roleName" />
        </el-form-item>
        <el-form-item :label="$t('system.role.roleKey')" prop="roleKey">
          <el-input v-model="form.roleKey" />
        </el-form-item>
        <el-form-item :label="$t('system.role.description')">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item :label="$t('system.role.status')" prop="status">
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

    <!-- 权限分配对话框 -->
    <el-dialog v-model="permDialogVisible" :title="$t('system.role.assignPermissions')" width="500px">
      <el-tree
        ref="treeRef"
        :data="menuTree"
        :props="{ label: 'label', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedKeys"
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handlePermSubmit">{{ $t('common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole, getRolePermissions, updateRolePermissions } from '@/api/role'
import { getMenuTree } from '@/api/menu'
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
const permDialogVisible = ref(false)
const formRef = ref(null)
const treeRef = ref(null)
const menuTree = ref([])
const checkedKeys = ref([])
const currentRoleId = ref(null)

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  roleName: ''
})

const form = reactive({
  id: null,
  roleName: '',
  roleKey: '',
  description: '',
  status: 1
})

const isEdit = computed(() => !!form.id)
const dialogTitle = computed(() => isEdit.value ? t('system.role.edit') : t('system.role.add'))

const rules = {
  roleName: [{ required: true, message: t('system.role.roleName'), trigger: 'blur' }],
  roleKey: [{ required: true, message: t('system.role.roleKey'), trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const { data } = await getRoleList(queryParams)
    tableData.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const getMenuTreeData = async () => {
  const { data } = await getMenuTree()
  menuTree.value = data
}

const handleSearch = () => {
  queryParams.page = 1
  getList()
}

const handleReset = () => {
  queryParams.roleName = ''
  handleSearch()
}

const resetForm = () => {
  form.id = null
  form.roleName = ''
  form.roleKey = ''
  form.description = ''
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
      if (isEdit.value) {
        await updateRole(form)
      } else {
        await createRole(form)
      }
      ElMessage.success(t('common.success'))
      dialogVisible.value = false
      getList()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(t('common.confirmDelete'), t('common.confirm'), {
    type: 'warning'
  }).then(async () => {
    await deleteRole(row.id)
    ElMessage.success(t('common.success'))
    getList()
  })
}

const handlePermission = async (row) => {
  currentRoleId.value = row.id
  const { data } = await getRolePermissions(row.id)
  checkedKeys.value = data
  permDialogVisible.value = true
}

const handlePermSubmit = async () => {
  const checkedNodes = treeRef.value.getCheckedKeys()
  await updateRolePermissions(currentRoleId.value, checkedNodes)
  ElMessage.success(t('common.success'))
  permDialogVisible.value = false
}

getList()
getMenuTreeData()
</script>

<style lang="scss" scoped>
.role-container {
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

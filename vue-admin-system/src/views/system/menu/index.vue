<template>
  <div class="menu-container">
    <div class="table-container">
      <div class="table-header">
        <el-button v-if="hasPermission('system:menu:add')" type="primary" @click="handleAdd(null)">{{ $t('system.menu.add') }}</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" row-key="id" border default-expand-all>
        <el-table-column prop="menuName" :label="$t('system.menu.menuName')" />
        <el-table-column prop="path" :label="$t('system.menu.path')" />
        <el-table-column prop="icon" :label="$t('system.menu.icon')">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="sort" :label="$t('system.menu.sort')" width="80" />
        <el-table-column prop="status" :label="$t('system.menu.status')" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? $t('common.enabled') : $t('common.disabled') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('system.menu.actions')" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPermission('system:menu:add')" type="primary" link @click="handleAdd(row)">{{ $t('common.add') }}</el-button>
            <el-button v-if="hasPermission('system:menu:edit')" type="primary" link @click="handleEdit(row)">{{ $t('common.edit') }}</el-button>
            <el-button v-if="hasPermission('system:menu:delete')" type="danger" link @click="handleDelete(row)">{{ $t('common.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTree"
            :props="{ label: 'menuName', value: 'id' }"
            check-strictly
            clearable
            placeholder="选择上级菜单"
          />
        </el-form-item>
        <el-form-item :label="$t('system.menu.menuName')" prop="menuName">
          <el-input v-model="form.menuName" />
        </el-form-item>
        <el-form-item :label="$t('system.menu.path')" prop="path">
          <el-input v-model="form.path" />
        </el-form-item>
        <el-form-item :label="$t('system.menu.icon')">
          <el-input v-model="form.icon" />
        </el-form-item>
        <el-form-item :label="$t('system.menu.sort')">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item :label="$t('system.menu.status')">
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
import { ref, reactive, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenuList, createMenu, updateMenu, deleteMenu } from '@/api/menu'
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
const menuTree = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  parentId: null,
  menuName: '',
  path: '',
  icon: '',
  sort: 0,
  status: 1
})

const isEdit = computed(() => !!form.id)
const dialogTitle = computed(() => isEdit.value ? t('system.menu.edit') : t('system.menu.add'))

const rules = {
  menuName: [{ required: true, message: t('system.menu.menuName'), trigger: 'blur' }],
  path: [{ required: true, message: t('system.menu.path'), trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const { data } = await getMenuList()
    tableData.value = data
    menuTree.value = [{ id: 0, menuName: '根目录', children: data }]
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = null
  form.parentId = null
  form.menuName = ''
  form.path = ''
  form.icon = ''
  form.sort = 0
  form.status = 1
}

const handleAdd = (row) => {
  resetForm()
  if (row) {
    form.parentId = row.id
  }
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
        await updateMenu(form)
      } else {
        await createMenu(form)
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
    await deleteMenu(row.id)
    ElMessage.success(t('common.success'))
    getList()
  })
}

getList()
</script>

<style lang="scss" scoped>
.menu-container {
  .table-container {
    background: var(--bg-card);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-card);

    .table-header {
      margin-bottom: 20px;
    }
  }
}
</style>

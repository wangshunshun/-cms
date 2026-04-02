<template>
  <div class="log-container">
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :model="queryParams" inline>
        <el-form-item :label="$t('system.log.operator')">
          <el-input v-model="queryParams.operator" :placeholder="$t('system.log.operator')" clearable />
        </el-form-item>
        <el-form-item :label="$t('system.log.operation')">
          <el-input v-model="queryParams.operation" :placeholder="$t('system.log.operation')" clearable />
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
        <el-button v-if="hasPermission('system:log:clear')" type="danger" @click="handleClear">{{ $t('system.log.clearAll') }}</el-button>
        <el-button v-if="hasPermission('system:log:export')" type="primary" @click="handleExport">{{ $t('system.log.export') }}</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="operator" :label="$t('system.log.operator')" width="100" />
        <el-table-column prop="operation" :label="$t('system.log.operation')" width="100" />
        <el-table-column prop="ip" :label="$t('system.log.ip')" width="140" />
        <el-table-column prop="description" :label="$t('system.log.description')" show-overflow-tooltip />
        <el-table-column prop="createTime" :label="$t('system.log.createTime')" width="180" />
        <el-table-column :label="$t('system.log.actions')" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">{{ $t('system.log.detail') }}</el-button>
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

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" :title="$t('system.log.detail')" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item :label="$t('system.log.operator')">{{ detail.operator }}</el-descriptions-item>
        <el-descriptions-item :label="$t('system.log.operation')">{{ detail.operation }}</el-descriptions-item>
        <el-descriptions-item :label="$t('system.log.ip')">{{ detail.ip }}</el-descriptions-item>
        <el-descriptions-item :label="$t('system.log.description')">{{ detail.description }}</el-descriptions-item>
        <el-descriptions-item :label="$t('system.log.createTime')">{{ detail.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getLogList, getLogDetail, clearLogs, exportLogs } from '@/api/log'
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
const detailVisible = ref(false)
const detail = ref({})

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  operator: '',
  operation: ''
})

const getList = async () => {
  loading.value = true
  try {
    const { data } = await getLogList(queryParams)
    tableData.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  getList()
}

const handleReset = () => {
  queryParams.operator = ''
  queryParams.operation = ''
  handleSearch()
}

const handleDetail = async (row) => {
  const { data } = await getLogDetail(row.id)
  detail.value = data
  detailVisible.value = true
}

const handleClear = () => {
  ElMessageBox.confirm('确定要清空所有日志吗？', t('common.confirm'), {
    type: 'warning'
  }).then(async () => {
    await clearLogs()
    ElMessage.success(t('common.success'))
    getList()
  })
}

const handleExport = async () => {
  await exportLogs(queryParams)
  ElMessage.success(t('common.success'))
}

getList()
</script>

<style lang="scss" scoped>
.log-container {
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
      display: flex;
      gap: 12px;
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>

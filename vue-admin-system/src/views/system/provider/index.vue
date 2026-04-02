<template>
  <div class="provider-container">
    <div class="search-form">
      <el-form :model="queryParams" inline>
        <el-form-item label="Provider Name">
          <el-input v-model="queryParams.providerName" placeholder="请输入供应商名称" clearable />
        </el-form-item>
        <el-form-item label="Country">
          <el-input v-model="queryParams.country" placeholder="请输入国家" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <div class="table-header">
        <el-button type="primary" @click="openProviderDialog()">新增供应商</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="providerName" label="Provider Name" min-width="180" />
        <el-table-column prop="country" label="Country" width="160" />
        <el-table-column prop="notes" label="Notes" min-width="220" show-overflow-tooltip />
        <el-table-column prop="contractCount" label="Contracts" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openProviderDialog(row)">编辑</el-button>
            <el-button type="success" link @click="openContractDialog(row)">
              <el-icon><Plus /></el-icon>添加合同
            </el-button>
            <el-button type="info" link @click="openDetail(row)">详情</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="providerDialogVisible" :title="providerForm.id ? '编辑供应商' : '新增供应商'" width="520px">
      <el-form ref="providerFormRef" :model="providerForm" :rules="providerRules" label-width="120px">
        <el-form-item label="Provider Name" prop="providerName">
          <el-input v-model="providerForm.providerName" />
        </el-form-item>
        <el-form-item label="Country" prop="country">
          <el-input v-model="providerForm.country" />
        </el-form-item>
        <el-form-item label="Notes">
          <el-input v-model="providerForm.notes" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="providerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProvider">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="contractDialogVisible" title="添加合同" width="720px">
      <el-form ref="contractFormRef" :model="contractForm" :rules="contractRules" label-width="140px">
        <el-form-item label="Contract Name" prop="contractName">
          <el-input v-model="contractForm.contractName" />
        </el-form-item>
        <el-form-item label="Provider">
          <el-input :model-value="currentProviderName" disabled />
        </el-form-item>
        <el-form-item label="Start Date" prop="startDate">
          <el-date-picker v-model="contractForm.startDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="End Date" prop="endDate">
          <el-date-picker v-model="contractForm.endDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="Commercial Rights">
          <el-checkbox v-for="platform in platforms" :key="platform" v-model="contractForm.commercialRights[platform]">
            {{ platform }}
          </el-checkbox>
        </el-form-item>
        <el-form-item label="Notes">
          <el-input v-model="contractForm.notes" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="contractDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitContract">确定</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="供应商详情" size="65%">
      <el-descriptions title="基本信息" :column="2" border>
        <el-descriptions-item label="Provider Name">{{ detail.providerName }}</el-descriptions-item>
        <el-descriptions-item label="Country">{{ detail.country }}</el-descriptions-item>
        <el-descriptions-item label="Notes" :span="2">{{ detail.notes || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div class="drawer-block">
        <div class="block-title">关联合同</div>
        <el-table :data="detail.contracts || []" border stripe>
          <el-table-column prop="contractName" label="Contract Name" min-width="220" />
          <el-table-column prop="startDate" label="Start Date" width="120" />
          <el-table-column prop="endDate" label="End Date" width="120" />
          <el-table-column prop="providerName" label="Provider" min-width="180" />
        </el-table>
      </div>

      <div class="drawer-block">
        <div class="block-title">供应商信息修改记录</div>
        <el-timeline>
          <el-timeline-item v-for="item in detailHistory" :key="item.id" :timestamp="item.time" placement="top">
            {{ item.action }} - {{ item.content }}（{{ item.operator }}）
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  createProvider,
  deleteProvider,
  getProviderDetail,
  getProviderHistory,
  getProviderList,
  updateProvider
} from '@/api/provider'
import { createContract } from '@/api/contract'

const platforms = ['IPTV', 'Mobile', 'SmartTV', 'Web']

const loading = ref(false)
const total = ref(0)
const tableData = ref([])

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  providerName: '',
  country: ''
})

const providerDialogVisible = ref(false)
const providerFormRef = ref(null)
const providerForm = reactive({
  id: null,
  providerName: '',
  country: '',
  notes: ''
})
const providerRules = {
  providerName: [{ required: true, message: '请输入 Provider Name', trigger: 'blur' }],
  country: [{ required: true, message: '请输入 Country', trigger: 'blur' }]
}

const contractDialogVisible = ref(false)
const contractFormRef = ref(null)
const contractForm = reactive({
  contractName: '',
  providerId: null,
  startDate: '',
  endDate: '',
  commercialRights: { IPTV: false, Mobile: false, SmartTV: false, Web: false },
  notes: ''
})
const contractRules = {
  contractName: [{ required: true, message: '请输入 Contract Name', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}
const currentProviderName = computed(() => {
  const row = tableData.value.find(item => item.id === contractForm.providerId)
  return row?.providerName || ''
})

const detailVisible = ref(false)
const detail = ref({})
const detailHistory = ref([])

const getList = async () => {
  loading.value = true
  try {
    const { data } = await getProviderList(queryParams)
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
  queryParams.providerName = ''
  queryParams.country = ''
  handleSearch()
}

const openProviderDialog = (row) => {
  providerDialogVisible.value = true
  providerForm.id = row?.id || null
  providerForm.providerName = row?.providerName || ''
  providerForm.country = row?.country || ''
  providerForm.notes = row?.notes || ''
}

const submitProvider = async () => {
  if (!providerFormRef.value) return
  await providerFormRef.value.validate(async (valid) => {
    if (!valid) return
    if (providerForm.id) {
      await updateProvider(providerForm.id, providerForm)
    } else {
      await createProvider(providerForm)
    }
    ElMessage.success('操作成功')
    providerDialogVisible.value = false
    getList()
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该供应商？', '提示', { type: 'warning' }).then(async () => {
    await deleteProvider(row.id)
    ElMessage.success('删除成功')
    getList()
  })
}

const openContractDialog = (providerRow) => {
  contractDialogVisible.value = true
  contractForm.contractName = ''
  contractForm.providerId = providerRow.id
  contractForm.startDate = ''
  contractForm.endDate = ''
  contractForm.notes = ''
  contractForm.commercialRights = { IPTV: false, Mobile: false, SmartTV: false, Web: false }
}

const submitContract = async () => {
  if (!contractFormRef.value) return
  await contractFormRef.value.validate(async (valid) => {
    if (!valid) return
    await createContract(contractForm)
    ElMessage.success('合同已创建')
    contractDialogVisible.value = false
  })
}

const openDetail = async (row) => {
  const [{ data: providerDetail }, { data: history }] = await Promise.all([
    getProviderDetail(row.id),
    getProviderHistory(row.id)
  ])
  detail.value = providerDetail
  detailHistory.value = history || []
  detailVisible.value = true
}

getList()
</script>

<style lang="scss" scoped>
.provider-container {
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

  .drawer-block {
    margin-top: 24px;
  }

  .block-title {
    font-size: 15px;
    font-weight: 600;
    margin-bottom: 12px;
  }
}
</style>

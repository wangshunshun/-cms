<template>
  <div class="contract-container">
    <div class="search-form">
      <el-form :model="queryParams" inline>
        <el-form-item label="Contract Name">
          <el-input v-model="queryParams.contractName" clearable />
        </el-form-item>
        <el-form-item label="Provider">
          <el-select v-model="queryParams.providerId" clearable style="width: 220px">
            <el-option v-for="item in providerOptions" :key="item.id" :label="item.providerName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <div class="table-header">
        <el-button type="primary" @click="openDialog()">新增合同</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="contractName" label="Contract Name" min-width="220" />
        <el-table-column prop="providerName" label="Provider" min-width="180" />
        <el-table-column prop="startDate" label="Start Date" width="120" />
        <el-table-column prop="endDate" label="End Date" width="120" />
        <el-table-column prop="notes" label="Notes" min-width="200" show-overflow-tooltip />
        <el-table-column label="附件" width="120">
          <template #default="{ row }">
            <el-button v-if="row.attachmentName" link type="primary" @click="downloadFile(row)">下载</el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑合同' : '新增合同'" width="760px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="Contract Name" prop="contractName">
          <el-input v-model="form.contractName" />
        </el-form-item>
        <el-form-item label="Provider" prop="providerId">
          <el-select v-model="form.providerId" :disabled="!!form.id" style="width: 100%">
            <el-option v-for="item in providerOptions" :key="item.id" :label="item.providerName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="Start Date" prop="startDate">
          <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="End Date" prop="endDate">
          <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="Commercial Rights">
          <div class="rights-grid">
            <el-checkbox v-for="platform in platforms" :key="platform" v-model="form.commercialRights[platform]">
              {{ platform }}
            </el-checkbox>
          </div>
        </el-form-item>
        <el-form-item label="合同附件">
          <el-upload :show-file-list="false" :auto-upload="false" :on-change="onSelectFile">
            <el-button type="primary" plain>上传附件</el-button>
          </el-upload>
          <span class="file-name">{{ form.attachmentName || '未上传文件' }}</span>
        </el-form-item>
        <el-form-item label="Notes">
          <el-input v-model="form.notes" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="合同详情" size="65%">
      <el-descriptions title="基本信息" border :column="2">
        <el-descriptions-item label="Contract Name">{{ detail.contractName }}</el-descriptions-item>
        <el-descriptions-item label="Provider">{{ detail.providerName }}</el-descriptions-item>
        <el-descriptions-item label="Start Date">{{ detail.startDate }}</el-descriptions-item>
        <el-descriptions-item label="End Date">{{ detail.endDate }}</el-descriptions-item>
        <el-descriptions-item label="Notes" :span="2">{{ detail.notes || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div class="drawer-block">
        <div class="block-title">关联系统许可证</div>
        <el-table :data="detail.licenses || []" border stripe>
          <el-table-column prop="licenseName" label="License Name" min-width="220" />
          <el-table-column prop="serviceType" label="Service Type" width="140" />
          <el-table-column prop="startDate" label="Start Date" width="120" />
          <el-table-column prop="endDate" label="End Date" width="120" />
        </el-table>
      </div>

      <div class="drawer-block">
        <div class="block-title">合同信息修改记录</div>
        <el-timeline>
          <el-timeline-item v-for="item in detailHistory" :key="item.id" :timestamp="item.time">
            {{ item.action }} - {{ item.content }}（{{ item.operator }}）
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createContract,
  deleteContract,
  getContractDetail,
  getContractHistory,
  getContractList,
  uploadContractAttachment,
  updateContract
} from '@/api/contract'
import { getAllProviders } from '@/api/provider'

const platforms = ['IPTV', 'Mobile', 'SmartTV', 'Web']

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const providerOptions = ref([])

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  contractName: '',
  providerId: null
})

const dialogVisible = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  contractName: '',
  providerId: null,
  startDate: '',
  endDate: '',
  commercialRights: { IPTV: false, Mobile: false, SmartTV: false, Web: false },
  notes: '',
  attachmentName: '',
  attachmentUrl: ''
})
const rules = {
  contractName: [{ required: true, message: '请输入 Contract Name', trigger: 'blur' }],
  providerId: [{ required: true, message: '请选择 Provider', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

const detailVisible = ref(false)
const detail = ref({})
const detailHistory = ref([])

const getProviderOptions = async () => {
  const { data } = await getAllProviders()
  providerOptions.value = data || []
}

const getList = async () => {
  loading.value = true
  try {
    const { data } = await getContractList(queryParams)
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
  queryParams.contractName = ''
  queryParams.providerId = null
  handleSearch()
}

const openDialog = (row) => {
  dialogVisible.value = true
  form.id = row?.id || null
  form.contractName = row?.contractName || ''
  form.providerId = row?.providerId || null
  form.startDate = row?.startDate || ''
  form.endDate = row?.endDate || ''
  form.commercialRights = row?.commercialRights
    ? { ...row.commercialRights }
    : { IPTV: false, Mobile: false, SmartTV: false, Web: false }
  form.notes = row?.notes || ''
  form.attachmentName = row?.attachmentName || ''
  form.attachmentUrl = row?.attachmentUrl || ''
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    if (form.id) {
      await updateContract(form.id, form)
    } else {
      await createContract(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    getList()
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该合同？', '提示', { type: 'warning' }).then(async () => {
    await deleteContract(row.id)
    ElMessage.success('删除成功')
    getList()
  })
}

const openDetail = async (row) => {
  const [{ data: contractDetail }, { data: history }] = await Promise.all([
    getContractDetail(row.id),
    getContractHistory(row.id)
  ])
  detail.value = contractDetail
  detailHistory.value = history || []
  detailVisible.value = true
}

const onSelectFile = async (file) => {
  if (!file.raw) return
  try {
    const formData = new FormData()
    formData.append('file', file.raw)
    const { data } = await uploadContractAttachment(formData)
    form.attachmentName = data.attachmentName
    form.attachmentUrl = data.attachmentUrl
    ElMessage.success('附件上传成功')
  } catch (error) {
    ElMessage.error('附件上传失败')
  }
}

const downloadFile = (row) => {
  if (!row.attachmentUrl) {
    ElMessage.warning('附件地址不存在')
    return
  }
  const link = document.createElement('a')
  link.href = row.attachmentUrl
  link.download = row.attachmentName || ''
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

getProviderOptions()
getList()
</script>

<style lang="scss" scoped>
.contract-container {
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

  .rights-grid {
    display: grid;
    grid-template-columns: repeat(4, minmax(100px, 1fr));
    gap: 8px;
  }

  .file-name {
    margin-left: 12px;
    color: var(--el-text-color-secondary);
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

<template>
  <div class="license-container">
    <div class="search-form">
      <el-form :model="queryParams" inline>
        <el-form-item label="License Name">
          <el-input v-model="queryParams.licenseName" clearable />
        </el-form-item>
        <el-form-item label="Contract">
          <el-select v-model="queryParams.contractId" clearable style="width: 220px">
            <el-option v-for="item in contractOptions" :key="item.id" :label="item.contractName" :value="item.id" />
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
        <el-button type="primary" @click="openDialog()">新增许可证</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="licenseName" label="License Name" min-width="220" />
        <el-table-column prop="contractName" label="Contract" min-width="180" />
        <el-table-column prop="serviceType" label="Service Type" width="120" />
        <el-table-column prop="startDate" label="Start Date" width="120" />
        <el-table-column prop="endDate" label="End Date" width="120" />
        <el-table-column prop="platforms" label="Platform" min-width="180">
          <template #default="{ row }">
            {{ row.platforms?.join(', ') || '-' }}
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑许可证' : '新增许可证'" width="780px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="180px">
        <el-form-item label="License Name" prop="licenseName">
          <el-input v-model="form.licenseName" />
        </el-form-item>
        <el-form-item label="Contract" prop="contractId">
          <el-select v-model="form.contractId" :disabled="!!form.id" style="width: 100%">
            <el-option v-for="item in contractOptions" :key="item.id" :label="item.contractName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="Start Date" prop="startDate">
          <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="End Date" prop="endDate">
          <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="Service Type" prop="serviceType">
          <el-select v-model="form.serviceType">
            <el-option label="SVOD" value="SVOD" />
            <el-option label="TVOD" value="TVOD" />
            <el-option label="AVOD" value="AVOD" />
          </el-select>
        </el-form-item>
        <el-form-item label="Regions" prop="regions">
          <el-select v-model="form.regions" multiple filterable allow-create default-first-option style="width: 100%" />
        </el-form-item>
        <el-form-item label="Platform" prop="platforms">
          <el-checkbox-group v-model="form.platforms">
            <el-checkbox v-for="platform in platforms" :key="platform" :label="platform">{{ platform }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item v-if="form.platforms.length" label="Advertorial Rights">
          <div class="rights-grid">
            <div v-for="platform in form.platforms" :key="platform" class="rights-item">
              <span>{{ platform }}</span>
              <el-switch v-model="form.advertorialRights[platform]" />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="Downloadable for Mobile">
          <el-switch v-model="form.downloadableForMobile" />
        </el-form-item>
        <el-form-item v-if="form.downloadableForMobile" label="Download Duration (Days)" prop="downloadDuration">
          <el-input-number v-model="form.downloadDuration" :min="1" :max="365" />
        </el-form-item>
        <el-form-item label="Preview for Mobile">
          <el-switch v-model="form.previewForMobile" />
        </el-form-item>
        <el-form-item v-if="form.previewForMobile" label="Preview Begin Time" prop="previewBeginTime">
          <el-time-picker v-model="form.previewBeginTime" value-format="HH:mm" format="HH:mm" />
        </el-form-item>
        <el-form-item v-if="form.previewForMobile" label="Preview End Time" prop="previewEndTime">
          <el-time-picker v-model="form.previewEndTime" value-format="HH:mm" format="HH:mm" />
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

    <el-drawer v-model="detailVisible" title="许可证详情" size="65%">
      <el-descriptions title="基本信息" border :column="2">
        <el-descriptions-item label="License Name">{{ detail.licenseName }}</el-descriptions-item>
        <el-descriptions-item label="Contract">{{ detail.contractName }}</el-descriptions-item>
        <el-descriptions-item label="Service Type">{{ detail.serviceType }}</el-descriptions-item>
        <el-descriptions-item label="Regions">{{ detail.regions?.join(', ') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Platform" :span="2">{{ detail.platforms?.join(', ') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Notes" :span="2">{{ detail.notes || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div class="drawer-block">
        <div class="block-title">许可证信息修改记录</div>
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
  createLicense,
  deleteLicense,
  getLicenseDetail,
  getLicenseHistory,
  getLicenseList,
  updateLicense
} from '@/api/license'
import { getContractList } from '@/api/contract'

const platforms = ['IPTV', 'Mobile', 'SmartTV', 'Web']

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const contractOptions = ref([])

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  licenseName: '',
  contractId: null
})

const dialogVisible = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  licenseName: '',
  contractId: null,
  startDate: '',
  endDate: '',
  serviceType: 'SVOD',
  regions: [],
  platforms: [],
  advertorialRights: {},
  downloadableForMobile: false,
  downloadDuration: null,
  previewForMobile: false,
  previewBeginTime: '',
  previewEndTime: '',
  notes: ''
})

const rules = {
  licenseName: [{ required: true, message: '请输入 License Name', trigger: 'blur' }],
  contractId: [{ required: true, message: '请选择 Contract', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  serviceType: [{ required: true, message: '请选择 Service Type', trigger: 'change' }],
  platforms: [{ required: true, message: '请选择 Platform', trigger: 'change' }],
  downloadDuration: [{ required: true, message: '请输入下载时长', trigger: 'blur' }],
  previewBeginTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  previewEndTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const detailVisible = ref(false)
const detail = ref({})
const detailHistory = ref([])

const resetForm = () => {
  form.id = null
  form.licenseName = ''
  form.contractId = null
  form.startDate = ''
  form.endDate = ''
  form.serviceType = 'SVOD'
  form.regions = []
  form.platforms = []
  form.advertorialRights = {}
  form.downloadableForMobile = false
  form.downloadDuration = null
  form.previewForMobile = false
  form.previewBeginTime = ''
  form.previewEndTime = ''
  form.notes = ''
}

const getContractOptions = async () => {
  const { data } = await getContractList({ page: 1, pageSize: 9999 })
  contractOptions.value = data.list || []
}

const getList = async () => {
  loading.value = true
  try {
    const { data } = await getLicenseList(queryParams)
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
  queryParams.licenseName = ''
  queryParams.contractId = null
  handleSearch()
}

const openDialog = (row) => {
  dialogVisible.value = true
  if (!row) {
    resetForm()
    return
  }
  form.id = row.id
  form.licenseName = row.licenseName
  form.contractId = row.contractId
  form.startDate = row.startDate
  form.endDate = row.endDate
  form.serviceType = row.serviceType
  form.regions = row.regions || []
  form.platforms = row.platforms || []
  form.advertorialRights = { ...(row.advertorialRights || {}) }
  form.downloadableForMobile = !!row.downloadableForMobile
  form.downloadDuration = row.downloadDuration
  form.previewForMobile = !!row.previewForMobile
  form.previewBeginTime = row.previewBeginTime || ''
  form.previewEndTime = row.previewEndTime || ''
  form.notes = row.notes || ''
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    if (!form.downloadableForMobile) {
      form.downloadDuration = null
    }
    if (!form.previewForMobile) {
      form.previewBeginTime = ''
      form.previewEndTime = ''
    }
    if (form.id) {
      await updateLicense(form.id, form)
    } else {
      await createLicense(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    getList()
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该许可证？', '提示', { type: 'warning' }).then(async () => {
    await deleteLicense(row.id)
    ElMessage.success('删除成功')
    getList()
  })
}

const openDetail = async (row) => {
  const [{ data: licenseDetail }, { data: history }] = await Promise.all([
    getLicenseDetail(row.id),
    getLicenseHistory(row.id)
  ])
  detail.value = licenseDetail
  detailHistory.value = history || []
  detailVisible.value = true
}

getContractOptions()
getList()
</script>

<style lang="scss" scoped>
.license-container {
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
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }

  .rights-item {
    border: 1px solid var(--el-border-color);
    border-radius: 6px;
    padding: 6px 10px;
    display: flex;
    align-items: center;
    gap: 8px;
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

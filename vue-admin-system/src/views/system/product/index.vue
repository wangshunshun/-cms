<template>
  <div class="product-container">
    <div class="search-form">
      <el-form :model="queryParams" inline>
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="queryParams.name" placeholder="请输入商品名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="商品状态" clearable style="width: 150px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">新增商品</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="productList"
        border
        stripe
        @sort-change="handleSortChange"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="商品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="description" label="商品描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="120" align="right" sortable="custom">
          <template #default="{ row }">
            ¥{{ Number(row.price || 0).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" align="center" sortable="custom" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" align="center" sortable="custom">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt || row.created_at) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增商品' : '编辑商品'"
      width="600px"
      @close="handleClose"
    >
      <el-form
        ref="productFormRef"
        :model="productForm"
        :rules="rules"
        label-width="100px"
        style="max-height: 500px; overflow-y: auto"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
          />
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input-number
            v-model="productForm.price"
            :min="0"
            :precision="2"
            :step="0.01"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <el-input-number
            v-model="productForm.stock"
            :min="0"
            :precision="0"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="商品状态" prop="status">
          <el-radio-group v-model="productForm.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="商品图片" prop="image">
          <el-upload
            class="avatar-uploader"
            :action="'/api/upload'"
            :headers="{ Authorization: 'Bearer ' + getToken() }"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="productForm.image" :src="productForm.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getProductList,
  createProduct,
  updateProduct,
  deleteProduct,
  updateProductStatus
} from '@/api/product'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const productList = ref([])
const total = ref(0)
const productFormRef = ref(null)

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10,
  name: '',
  status: null,
  sort_field: '',
  sort_order: ''
})

// 表单数据
const productForm = reactive({
  id: null,
  name: '',
  description: '',
  price: 0,
  stock: 0,
  status: 1,
  image: ''
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入商品价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }],
  status: [{ required: true, message: '请选择商品状态', trigger: 'change' }]
}

// 获取token
const getToken = () => {
  return localStorage.getItem('token')
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取商品列表
const getList = async () => {
  loading.value = true
  try {
    const response = await getProductList(queryParams)
    productList.value = response.data.items || response.data.list || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取商品列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.page = 1
  getList()
}

// 重置搜索
const resetQuery = () => {
  queryParams.name = ''
  queryParams.status = null
  queryParams.sort_field = ''
  queryParams.sort_order = ''
  handleQuery()
}

// 排序变化
const handleSortChange = ({ prop, order }) => {
  queryParams.sort_field = prop || ''
  queryParams.sort_order = order || ''
  getList()
}

// 页码变化
const handleCurrentChange = (val) => {
  queryParams.page = val
  getList()
}

// 每页数量变化
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

// 添加商品
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  resetForm()
}

// 编辑商品
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(productForm, {
    id: row.id,
    name: row.name || '',
    description: row.description || '',
    price: Number(row.price) || 0,
    stock: Number(row.stock) || 0,
    status: row.status ?? 1,
    image: row.image || ''
  })
}

// 删除商品
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认要删除该商品吗？', '提示', {
      type: 'warning'
    })
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }
}

// 更新商品状态
const handleStatusChange = async (row) => {
  try {
    await updateProductStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1 // 恢复原状态
    ElMessage.error('状态更新失败')
    console.error(error)
  }
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
  resetForm()
}

// 重置表单
const resetForm = () => {
  productForm.id = null
  productForm.name = ''
  productForm.description = ''
  productForm.price = 0
  productForm.stock = 0
  productForm.status = 1
  productForm.image = ''
}

// 图片上传前的验证
const beforeUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJpgOrPng) {
    ElMessage.error('上传图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
  }
  return isJpgOrPng && isLt2M
}

// 图片上传成功
const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    productForm.image = response.data.url
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!productFormRef.value) return

  try {
    await productFormRef.value.validate()

    const payload = {
      ...productForm,
      price: Number(productForm.price),
      stock: Number(productForm.stock)
    }

    if (dialogType.value === 'add') {
      await createProduct(payload)
      ElMessage.success('添加成功')
    } else {
      await updateProduct(productForm.id, payload)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
      console.error(error)
    }
  }
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.product-container {
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

  .avatar-uploader {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
  }

  .avatar-uploader:hover {
    border-color: var(--el-color-primary);
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
    line-height: 178px;
  }

  .avatar {
    width: 178px;
    height: 178px;
    display: block;
    object-fit: cover;
  }
}
</style>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">分类管理</span>
        <el-button type="primary" @click="openDialog()">新增分类</el-button>
      </div>

      <el-form :model="query" inline class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="分类名称" clearable @clear="loadData" />
        </el-form-item>
        <el-form-item label="上级分类">
          <el-select v-model="query.parentId" placeholder="全部" clearable @change="loadData">
            <el-option v-for="c in firstLevels" :key="c.id" :label="c.categoryName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable @change="loadData">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="categoryName" label="分类名称" width="180" />
        <el-table-column prop="categoryIcon" label="图标" width="80" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="商品数" width="80">
          <template #default="{ row }">
            <span>{{ getProductCount(row.id) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="320">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button link type="success" size="small" @click="$router.push('/admin/products?categoryId='+row.id)">查看商品</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="上级分类">
          <el-select v-model="form.parentId" clearable placeholder="无（一级分类）" style="width:100%">
            <el-option v-for="c in firstLevels" :key="c.id" :label="c.categoryName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.categoryIcon" placeholder="Element Plus 图标名，如 Goods" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.categoryImage" placeholder="分类图片URL（可选）" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { getCategoryList, getFirstLevelCategories, addCategory, updateCategory, deleteCategory, changeCategoryStatus } from '../../api/admin'

const query = reactive({ pageNum: 1, pageSize: 10, keyword: '', parentId: null, status: null })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const firstLevels = ref([])
const productCounts = ref({})
function getProductCount(catId) { return productCounts.value[catId] ?? '-' }
async function loadProductCounts() {
  try { const r = await axios.get('/api/admin/category/product-counts'); const map={}; (r.data.data||[]).forEach(c=>{map[c.id]=c.productCount}); productCounts.value=map } catch {}
}
const form = reactive({ id: null, parentId: null, categoryName: '', categoryIcon: '', categoryImage: '', sort: 0 })
const formRef = ref(null)

const dialogTitle = computed(() => form.id ? '编辑分类' : '新增分类')

const rules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
}

async function loadData() {
  loading.value = true
  try {
    const params = {}
    if (query.keyword) params.keyword = query.keyword
    if (query.parentId) params.parentId = query.parentId
    if (query.status !== null && query.status !== '') params.status = query.status
    params.pageNum = query.pageNum
    params.pageSize = query.pageSize
    const res = await getCategoryList(params)
    tableData.value = (res.data && res.data.records) ? res.data.records : []
    total.value = (res.data && res.data.total) ? res.data.total : 0
  } catch { tableData.value = []; total.value = 0 }
  finally { loading.value = false }
}

async function loadFirstLevels() {
  try {
    const res = await getFirstLevelCategories()
    firstLevels.value = res.data || []
  } catch { firstLevels.value = [] }
}

function resetQuery() {
  query.keyword = ''
  query.parentId = null
  query.status = null
  query.pageNum = 1
  loadData()
}

function openDialog(row) {
  if (row && row.id) {
    form.id = row.id
    form.parentId = row.parentId
    form.categoryName = row.categoryName
    form.categoryIcon = row.categoryIcon || ''
    form.categoryImage = row.categoryImage || ''
    form.sort = row.sort || 0
  } else {
    form.id = null
    form.parentId = null
    form.categoryName = ''
    form.categoryIcon = ''
    form.categoryImage = ''
    form.sort = 0
  }
  dialogVisible.value = true
}

function resetForm() {
  formRef.value?.resetFields()
  form.id = null
  form.parentId = null
  form.categoryName = ''
  form.categoryIcon = ''
  form.categoryImage = ''
  form.sort = 0
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (form.id) {
      await updateCategory({ id: form.id, parentId: form.parentId, categoryName: form.categoryName, categoryIcon: form.categoryIcon, categoryImage: form.categoryImage, sort: form.sort })
      ElMessage.success('更新成功')
    } else {
      await addCategory({ parentId: form.parentId, categoryName: form.categoryName, categoryIcon: form.categoryIcon, categoryImage: form.categoryImage, sort: form.sort })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
    loadFirstLevels()
  } catch { /* interceptor handles */ }
  finally { submitLoading.value = false }
}

async function toggleStatus(row) {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await changeCategoryStatus(row.id, newStatus)
    ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
    loadData()
  } catch { /* interceptor handles */ }
}

async function handleDelete(id) {
  try {
    await deleteCategory(id)
    ElMessage.success('删除成功')
    loadData()
    loadFirstLevels()
  } catch { /* interceptor handles */ }
}

onMounted(() => { loadData(); loadFirstLevels(); loadProductCounts() })
</script>

<style scoped>
.page-container { animation: fadeIn .3s; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { font-size: 16px; font-weight: 600; }
.search-form { margin: 16px 0; }
.pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
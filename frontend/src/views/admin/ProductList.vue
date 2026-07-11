<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">商品管理</span>
        <div>
          <el-button v-if="selectedIds.length" type="success" @click="batchOnline">批量上架</el-button>
          <el-button v-if="selectedIds.length" type="danger" @click="batchOffline">批量下架</el-button>
          <el-button @click="exportData">导出Excel</el-button>
          <el-button type="primary" :icon="Plus" @click="$router.push('/admin/product/edit')">新增商品</el-button>
        </div>
      </div>

      <el-form :model="query" inline class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="商品名称/编号" clearable @clear="loadData" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" clearable placeholder="全部分类" @change="loadData">
            <el-option v-for="c in flatCategories" :key="c.id" :label="c.categoryName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable @change="loadData">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading" style="width:100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column label="主图" width="80">
          <template #default="{ row }">
            <el-image v-if="row.mainImage" :src="row.mainImage" style="width:50px;height:50px;border-radius:6px" fit="cover" />
            <el-icon v-else :size="40" color="#ccc"><Picture /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品名称" min-width="160" />
        <el-table-column prop="productNo" label="编号" width="120" />
        <el-table-column label="价格" width="100">
          <template #default="{ row }">
            <span style="color:#f56c6c;font-weight:600">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column label="推荐" width="70">
          <template #default="{ row }">
            <el-switch :model-value="row.isRecommend === 1" @change="toggleRecommend(row)" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="toggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push(`/admin/product/edit/${row.id}`)">编辑</el-button>
            <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
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

    <el-dialog title="商品详情" v-model="detailVisible" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="编号">{{ detail.productNo }}</el-descriptions-item>
        <el-descriptions-item label="名称">{{ detail.productName }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ detail.categoryId }}</el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ detail.price }}</el-descriptions-item>
        <el-descriptions-item label="原价">¥{{ detail.originalPrice }}</el-descriptions-item>
        <el-descriptions-item label="库存">{{ detail.stock }}</el-descriptions-item>
        <el-descriptions-item label="销量">{{ detail.sales }}</el-descriptions-item>
        <el-descriptions-item label="单位">{{ detail.unit }}</el-descriptions-item>
        <el-descriptions-item label="重量">{{ detail.weight }}</el-descriptions-item>
        <el-descriptions-item label="产地">{{ detail.originPlace }}</el-descriptions-item>
        <el-descriptions-item label="好评率">{{ detail.goodRate }}%</el-descriptions-item>
        <el-descriptions-item label="推荐">{{ detail.isRecommend === 1 ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="新品">{{ detail.isNew === 1 ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="热销">{{ detail.isHot === 1 ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ detail.description }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Picture } from '@element-plus/icons-vue'
import { getProductList, getProductDetail, deleteProduct, changeProductStatus, changeProductRecommend, batchProducts, exportTable } from '../../api/admin'
import { getCategoryTree } from '../../api/admin'

const route = useRoute()
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '', categoryId: null, status: null })
const selectedIds = ref([])

function handleSelectionChange(rows) { selectedIds.value = rows.map(r => r.id) }
async function batchOnline() { try { await batchProducts(selectedIds.value, 'online'); ElMessage.success('批量上架成功'); selectedIds.value = []; loadData() } catch {} }
async function batchOffline() { try { await batchProducts(selectedIds.value, 'offline'); ElMessage.success('批量下架成功'); selectedIds.value = []; loadData() } catch {} }
async function exportData() {
  try { const res = await exportTable('products'); const url = window.URL.createObjectURL(new Blob([res.data])); const a = document.createElement('a'); a.href = url; a.download = `products_${Date.now()}.csv`; a.click(); window.URL.revokeObjectURL(url) } catch { ElMessage.error('导出失败') }
}
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const detail = ref({})
const categories = ref([])
const flatCategories = computed(() => {
  const result = []
  function flatten(list, prefix = '') {
    if (!list || !list.length) return
    list.forEach(c => {
      result.push({ id: c.id, categoryName: prefix + c.categoryName })
      if (c.children && c.children.length) flatten(c.children, prefix + '--')
    })
  }
  flatten(categories.value)
  return result
})

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    Object.keys(params).forEach(k => (params[k] === '' || params[k] === null) && delete params[k])
    const res = await getProductList(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

async function loadCategories() {
  const res = await getCategoryTree()
  categories.value = res.data || []
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, keyword: '', categoryId: null, status: null })
  loadData()
}

async function viewDetail(row) {
  const res = await getProductDetail(row.id)
  detail.value = res.data
  detailVisible.value = true
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await changeProductStatus(row.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
  loadData()
}

async function toggleRecommend(row) {
  const newVal = row.isRecommend === 1 ? 0 : 1
  await changeProductRecommend(row.id, newVal)
  ElMessage.success(newVal === 1 ? '已推荐' : '已取消推荐')
  loadData()
}

async function handleDelete(id) {
  await deleteProduct(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => { if (route.query.categoryId) { query.categoryId = parseInt(route.query.categoryId) } loadData(); loadCategories() })
</script>

<style scoped>
.page-container { animation: fadeIn .3s; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { font-size: 16px; font-weight: 600; }
.search-form { margin: 16px 0; }
.pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>

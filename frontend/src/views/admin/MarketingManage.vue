<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">营销活动管理</span>
        <el-button type="primary" @click="openDialog()">新增活动</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="160" />
        <el-table-column prop="name" label="活动名称" width="180" />
        <el-table-column label="类型" width="100">
          <template #default="{row}">{{ typeText(row.type) }}</template>
        </el-table-column>
        <el-table-column label="规则" width="150" show-overflow-tooltip>
          <template #default="{row}">{{ formatRule(row) }}</template>
        </el-table-column>
        <el-table-column label="时间" width="300">
          <template #default="{row}">{{ row.startTime }} ~ {{ row.endTime }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{row}">
            <el-tag :type="statusTag(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="240">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button link type="success" size="small" @click="openProducts(row)">关联商品</el-button>
            <el-popconfirm title="确定删除？" @confirm="del(row.id)">
              <template #reference><el-button link type="danger" size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="form.id?'编辑活动':'新增活动'" v-model="dialogVisible" width="550px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="活动名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" @change="onTypeChange">
            <el-option label="折扣活动" value="discount" />
            <el-option label="满减活动" value="full_reduce" />
            <el-option label="新人专享" value="new_user" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type==='discount'" label="折扣">
          <el-input-number v-model="discountRate" :min="0.1" :max="0.99" :step="0.1" :precision="2" />
          <span style="margin-left:8px">折（如0.8=8折）</span>
        </el-form-item>
        <el-form-item v-if="form.type==='full_reduce'" label="满减规则">
          <div style="display:flex;gap:8px;align-items:center">
            <span>满</span>
            <el-input-number v-model="minAmount" :min="0" :precision="2" style="width:120px" />
            <span>减</span>
            <el-input-number v-model="reduceAmount" :min="0" :precision="2" style="width:120px" />
          </div>
        </el-form-item>
        <el-form-item label="活动时间">
          <el-date-picker v-model="dateRange" type="datetimerange" range-separator="至"
            value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0"
            active-text="进行中" inactive-text="未开始" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="save" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 关联商品对话框 -->
    <el-dialog title="关联商品" v-model="productsVisible" width="600px">
      <div style="margin-bottom:12px">
        <el-select v-model="selectedProductId" filterable placeholder="选择商品" style="width:300px">
          <el-option v-for="p in allProducts" :key="p.id" :label="p.productName" :value="p.id" />
        </el-select>
        <el-button type="primary" style="margin-left:8px" @click="addProduct" :disabled="!selectedProductId">添加</el-button>
      </div>
      <el-table :data="linkedProducts" border size="small">
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column label="价格" width="100">
          <template #default="{row}">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{row}">
            <el-button link type="danger" size="small" @click="removeProduct(row.id)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="linkedProducts.length===0" description="暂未关联商品" :image-size="40" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const form = reactive({ id: null, name: '', type: 'discount', rule: '', status: 0 })
const dateRange = ref([])
const discountRate = ref(0.8)
const minAmount = ref(100)
const reduceAmount = ref(20)

const productsVisible = ref(false)
const currentActivityId = ref(null)
const linkedProducts = ref([])
const allProducts = ref([])
const selectedProductId = ref(null)

function typeText(t) { return { discount: '折扣活动', full_reduce: '满减活动', new_user: '新人专享' }[t] || t }
function statusTag(s) { return [, 'success', 'info'][s] || 'warning' }
function statusText(s) { return [, '进行中', '已结束'][s] || '未开始' }
function formatRule(row) {
  try {
    const r = JSON.parse(row.rule || '{}')
    if (row.type === 'discount') return (r.discount * 10) + '折'
    if (row.type === 'full_reduce') return '满' + r.minAmount + '减' + r.reduceAmount
    return row.rule
  } catch { return row.rule }
}

function onTypeChange() {
  if (form.type === 'discount') form.rule = JSON.stringify({ discount: discountRate.value })
  else if (form.type === 'full_reduce') form.rule = JSON.stringify({ minAmount: minAmount.value, reduceAmount: reduceAmount.value })
  else form.rule = ''
}

async function load() {
  loading.value = true
  try {
    const r = await axios.get('/api/marketing')
    tableData.value = r.data.data || []
  } finally { loading.value = false }
}

function openDialog(row) {
  if (row) {
    form.id = row.id; form.name = row.name; form.type = row.type; form.rule = row.rule || ''; form.status = row.status
    dateRange.value = row.startTime && row.endTime ? [row.startTime, row.endTime] : []
    try {
      const r = JSON.parse(row.rule || '{}')
      if (row.type === 'discount') discountRate.value = r.discount || 0.8
      if (row.type === 'full_reduce') { minAmount.value = r.minAmount || 100; reduceAmount.value = r.reduceAmount || 20 }
    } catch {}
  } else {
    form.id = null; form.name = ''; form.type = 'discount'; form.rule = ''; form.status = 0
    dateRange.value = []; discountRate.value = 0.8
  }
  dialogVisible.value = true
}

async function save() {
  saving.value = true
  try {
    if (form.type === 'discount') form.rule = JSON.stringify({ discount: discountRate.value })
    else if (form.type === 'full_reduce') form.rule = JSON.stringify({ minAmount: minAmount.value, reduceAmount: reduceAmount.value })
    const d = { name: form.name, type: form.type, rule: form.rule, status: form.status }
    if (form.id) d.id = form.id
    if (dateRange.value && dateRange.value.length === 2) { d.startTime = dateRange.value[0]; d.endTime = dateRange.value[1] }
    if (form.id) await axios.put('/api/marketing', d)
    else await axios.post('/api/marketing', d)
    ElMessage.success(form.id ? '更新成功' : '创建成功')
    dialogVisible.value = false; load()
  } catch { ElMessage.error('操作失败') } finally { saving.value = false }
}

async function del(id) {
  await axios.delete('/api/marketing/' + id)
  ElMessage.success('已删除'); load()
}

async function openProducts(row) {
  currentActivityId.value = row.id
  productsVisible.value = true
  selectedProductId.value = null
  try {
    const r = await axios.get('/api/marketing/' + row.id + '/products')
    linkedProducts.value = r.data.data || []
  } catch { linkedProducts.value = [] }
  try {
    const r = await axios.get('/admin/product/list?pageNum=1&pageSize=100')
    allProducts.value = r.data?.records || []
  } catch { allProducts.value = [] }
}

async function addProduct() {
  if (!selectedProductId.value) return
  await axios.post('/api/marketing/' + currentActivityId.value + '/products', { productIds: [selectedProductId.value] })
  ElMessage.success('已添加')
  const r = await axios.get('/api/marketing/' + currentActivityId.value + '/products')
  linkedProducts.value = r.data.data || []
  selectedProductId.value = null
}

async function removeProduct(goodsId) {
  await axios.delete('/api/marketing/goods/' + goodsId)
  ElMessage.success('已移除')
  const r = await axios.get('/api/marketing/' + currentActivityId.value + '/products')
  linkedProducts.value = r.data.data || []
}

onMounted(load)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}
@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}
.page-title{font-size:16px;font-weight:600}
</style>

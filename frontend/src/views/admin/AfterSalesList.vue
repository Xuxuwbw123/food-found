<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">售后工单</span>
      </div>

      <el-form :model="query" inline class="search-form">
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable @change="loadData">
            <el-option label="待审核" :value="0" />
            <el-option label="审核通过" :value="1" />
            <el-option label="退货中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已关闭" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="afterSalesNo" label="售后单号" width="180" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ afterSalesTypeText(row.afterSalesType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="refundAmount" label="退款金额" width="100">
          <template #default="{ row }">¥{{ row.refundAmount || 0 }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="afterSalesStatusTag(row.status)" size="small">{{ afterSalesStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="170" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push(`/admin/after-sales/detail/${row.id}`)">处理</el-button>
            <el-popconfirm v-if="row.status === 3 || row.status === 4" title="确定删除？" @confirm="handleDelete(row.id)">
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAfterSalesList, deleteAfterSales } from '../../api/admin'

function afterSalesTypeText(t) {
  return t === 1 ? '仅退款' : t === 2 ? '退货退款' : t === 3 ? '换货' : t === 4 ? '补发' : '未知'
}
function afterSalesStatusText(s) {
  const map = { 0: '待审核', 1: '审核通过', 2: '退货中', 3: '已完成', 4: '已关闭' }
  return map[s] || '未知'
}
function afterSalesStatusTag(s) {
  const map = { 0: 'warning', 1: 'primary', 2: '', 3: 'success', 4: 'info' }
  return map[s] || ''
}

const query = reactive({ pageNum: 1, pageSize: 10, status: null })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    Object.keys(params).forEach(k => (params[k] === '' || params[k] === null) && delete params[k])
    const res = await getAfterSalesList(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, status: null })
  loadData()
}

async function handleDelete(id) {
  await deleteAfterSales(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page-container { animation: fadeIn .3s; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { font-size: 16px; font-weight: 600; }
.search-form { margin: 16px 0; }
.pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>

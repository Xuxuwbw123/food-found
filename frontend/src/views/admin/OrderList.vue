<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">订单管理</span>
      </div>

      <el-form :model="query" inline class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="订单号/收货人" clearable @clear="loadData" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="query.status" placeholder="全部" clearable @change="loadData">
            <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;padding:8px 12px;background:#f5f7fa;border-radius:6px">
        <span style="font-size:13px;color:#666">共 {{ total }} 条记录，第 {{ query.pageNum }} 页</span>
        <div>
          <el-button size="small" :disabled="query.pageNum<=1" @click="query.pageNum--;loadData()">上一页</el-button>
          <el-button size="small" :disabled="query.pageNum*query.pageSize>=total" @click="query.pageNum++;loadData()">下一页</el-button>
          <el-select v-model="query.pageSize" size="small" style="width:90px;margin-left:8px" @change="query.pageNum=1;loadData()">
            <el-option :value="10" label="10条/页" />
            <el-option :value="20" label="20条/页" />
            <el-option :value="50" label="50条/页" />
          </el-select>
        </div>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="width:100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column label="购买者" width="120">
          <template #default="{ row }">
            <div style="font-weight:600">{{ row.buyerNickname || row.buyerUsername || '-' }}</div>
            <div style="font-size:12px;color:#999">{{ row.buyerPhone || '' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="商品" min-width="200">
          <template #default="{ row }">
            <div v-for="item in (row.items || []).slice(0, 2)" :key="item.productName" style="display:flex;align-items:center;gap:6px;margin-bottom:4px">
              <el-image v-if="item.productImage" :src="item.productImage" style="width:30px;height:30px;border-radius:4px" fit="cover" />
              <span>{{ item.productName }} × {{ item.quantity }}</span>
            </div>
            <span v-if="(row.items || []).length > 2" style="color:#999;font-size:12px">共{{ row.items.length }}件</span>
          </template>
        </el-table-column>
        <el-table-column label="收货地址" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            <div style="font-weight:500">{{ row.receiverName }} {{ row.receiverPhone }}</div>
            <div style="font-size:12px;color:#666;margin-top:2px">{{ row.receiverProvince }}{{ row.receiverCity }}{{ row.receiverDistrict }} {{ row.receiverAddress }}</div>
          </template>
        </el-table-column>
        <el-table-column label="金额" width="120">
          <template #default="{ row }">
            <span style="color:#f56c6c;font-weight:600">¥{{ row.actualAmount || row.payAmount || row.totalAmount }}</span>
            <div v-if="row.planAmount && row.actualAmount && row.planAmount !== row.actualAmount" style="font-size:12px;color:#999;text-decoration:line-through">¥{{ row.planAmount }}</div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="orderStatusTag(row.orderStatus)" size="small">{{ orderStatusText(row.orderStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下单时间" width="160">
          <template #default="{ row }">{{ row.createTime || row.createTime }}</template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="240">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push(`/admin/order/detail/${row.id}`)">详情</el-button>
            <el-button v-if="row.orderStatus === 1" link type="success" size="small" @click="openDelivery(row)">发货</el-button>
            <el-popconfirm v-if="row.orderStatus === 4 || row.orderStatus === 3" title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>

      </el-table>

    </el-card>

    <el-dialog title="发货" v-model="deliveryVisible" width="400px">
      <el-form :model="deliveryForm" label-width="80px">
        <el-form-item label="物流单号">
          <el-input v-model="deliveryForm.logisticsNo" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deliveryVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDelivery" :loading="deliveryLoading">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderList, deliveryOrder, deleteOrder } from '../../api/admin'

const statusOptions = [
  { value: 0, label: '待付款' }, { value: 1, label: '待发货' },
  { value: 2, label: '已发货' }, { value: 3, label: '已完成' },
  { value: 4, label: '已取消' }, { value: 5, label: '售后中' }
]

function orderStatusTag(s) {
  const map = { 0: 'warning', 1: 'primary', 2: '', 3: 'success', 4: 'info', 5: 'danger' }
  return map[s] || ''
}
function orderStatusText(s) {
  const map = { 0: '待付款', 1: '待发货', 2: '已发货', 3: '已完成', 4: '已取消', 5: '售后中' }
  return map[s] || '未知'
}

const query = reactive({ pageNum: 1, pageSize: 10, keyword: '', status: null })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const deliveryVisible = ref(false)
const deliveryLoading = ref(false)
const deliveryForm = reactive({ id: null, logisticsNo: '' })

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    Object.keys(params).forEach(k => (params[k] === '' || params[k] === null) && delete params[k])
    const res = await getOrderList(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, keyword: '', status: null })
  loadData()
}

function openDelivery(row) {
  deliveryForm.id = row.id
  deliveryForm.logisticsNo = ''
  deliveryVisible.value = true
}

async function handleDelivery() {
  if (!deliveryForm.logisticsNo) { ElMessage.warning('请输入物流单号'); return }
  deliveryLoading.value = true
  try {
    await deliveryOrder(deliveryForm.id, deliveryForm.logisticsNo)
    ElMessage.success('发货成功')
    deliveryVisible.value = false
    loadData()
  } finally { deliveryLoading.value = false }
}

async function handleDelete(id) {
  await deleteOrder(id)
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

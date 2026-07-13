<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">支付记录</span>
      </div>

      <el-form :model="query" inline class="search-form">
        <el-form-item label="订单ID">
          <el-input v-model="query.orderId" placeholder="订单ID" clearable @clear="loadData" />
        </el-form-item>
        <el-form-item label="支付状态">
          <el-select v-model="query.payStatus" placeholder="全部" clearable @change="loadData">
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已退款" :value="2" />
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
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="paymentNo" label="支付单号" width="180" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="payType" label="支付方式" width="100" />
        <el-table-column prop="payAmount" label="支付金额" width="110">
          <template #default="{ row }">¥{{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column label="支付状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.payStatus === 1 ? 'success' : row.payStatus === 2 ? 'danger' : 'warning'" size="small">
              {{ row.payStatus === 0 ? '待支付' : row.payStatus === 1 ? '已支付' : '已退款' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payTime" label="支付时间" width="170" />
        <el-table-column prop="thirdPartyNo" label="第三方单号" width="180" />
        <el-table-column prop="refundAmount" label="退款金额" width="100" />
        <el-table-column prop="refundTime" label="退款时间" width="170" />
        <el-table-column prop="refundReason" label="退款原因" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" width="80">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>

      </el-table>

    </el-card>

    <el-dialog title="支付详情" v-model="detailVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ payDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="支付单号">{{ payDetail.paymentNo }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ payDetail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ payDetail.userId }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ payDetail.payType }}</el-descriptions-item>
        <el-descriptions-item label="支付金额">¥{{ payDetail.payAmount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="payDetail.payStatus === 1 ? 'success' : payDetail.payStatus === 2 ? 'danger' : 'warning'" size="small">
            {{ payDetail.payStatus === 0 ? '待支付' : payDetail.payStatus === 1 ? '已支付' : '已退款' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ payDetail.payTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="第三方单号">{{ payDetail.thirdPartyNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退款金额">¥{{ payDetail.refundAmount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="退款时间">{{ payDetail.refundTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退款原因" :span="2">{{ payDetail.refundReason || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getPaymentList, getPaymentDetail } from '../../api/admin'

const query = reactive({ pageNum: 1, pageSize: 10, orderId: null, payStatus: null })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const payDetail = ref({})

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    Object.keys(params).forEach(k => (params[k] === '' || params[k] === null) && delete params[k])
    const res = await getPaymentList(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, orderId: null, payStatus: null })
  loadData()
}

async function viewDetail(row) {
  const res = await getPaymentDetail(row.id)
  payDetail.value = res.data
  detailVisible.value = true
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

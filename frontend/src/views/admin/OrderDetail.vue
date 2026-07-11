<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">订单详情</span>
        <el-button @click="$router.back()">返回</el-button>
      </div>

      <div v-if="order.id" v-loading="loading">
        <el-divider content-position="left">订单信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单ID">{{ order.id }}</el-descriptions-item>
          <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="orderStatusTag(order.orderStatus)">{{ orderStatusText(order.orderStatus) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="计划金额">¥{{ order.planAmount || order.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">¥{{ order.actualAmount || order.payAmount }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ order.createTime }}</el-descriptions-item>
          <el-descriptions-item label="物流单号">{{ order.logisticsNo || '未发货' }}</el-descriptions-item>
          <el-descriptions-item label="物流公司">{{ order.logisticsCompany || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">收货信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="收货人">{{ order.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ order.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">
            {{ order.receiverProvince }}{{ order.receiverCity }}{{ order.receiverDistrict }} {{ order.receiverAddress }}
          </el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">商品明细</el-divider>
        <el-table :data="orderItems" border stripe>
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="price" label="单价" width="100"><template #default="{row}">¥{{row.price}}</template></el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="小计" width="120"><template #default="{row}">¥{{(row.price*row.quantity).toFixed(2)}}</template></el-table-column>
        </el-table>

        <el-divider content-position="left">操作日志</el-divider>
        <el-timeline v-if="orderLogs.length">
          <el-timeline-item v-for="log in orderLogs" :key="log.id" :timestamp="log.createTime">
            <el-tag size="small">{{orderStatusText(log.orderStatus)}}</el-tag>
            <span v-if="log.remark" style="margin-left:8px;color:#666">{{log.remark}}</span>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无操作日志" :image-size="60" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '../../utils/request'

const route = useRoute()
const order = ref({})
const orderItems = ref([])
const orderLogs = ref([])
const loading = ref(false)

function orderStatusTag(s) {
  const map = {0:'warning',1:'primary',2:'',3:'success',4:'info',5:'danger'}
  return map[s]||''
}
function orderStatusText(s) {
  const map = {0:'待付款',1:'待发货',2:'已发货',3:'已完成',4:'已取消',5:'售后中'}
  return map[s]||'未知'
}

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/order/' + route.params.id)
    const d = res.data || res || {}
    order.value = d.order || {}
    orderItems.value = d.orderItems || []
    orderLogs.value = d.orderLogs || []
  } finally { loading.value = false }
}

onMounted(loadData)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:20px}.page-title{font-size:16px;font-weight:600}
</style>
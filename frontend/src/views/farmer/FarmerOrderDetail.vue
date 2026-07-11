<template>
  <div class="page-container">
    <el-card shadow="never" v-loading="loading">
      <div class="page-header">
        <span class="page-title">订单详情 #{{ order.id }}</span>
        <el-button @click="$router.back()">返回</el-button>
      </div>
      <el-descriptions :column="2" border v-if="order.id">
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="statusTag(order.orderStatus)">{{ statusText(order.orderStatus) }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="收货人">{{ order.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ order.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ order.receiverProvince }}{{ order.receiverCity }}{{ order.receiverDistrict }} {{ order.receiverAddress }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ order.payAmount || order.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ order.createTime }}</el-descriptions-item>
        <el-descriptions-item label="物流公司">{{ order.logisticsCompany || '-' }}</el-descriptions-item>
        <el-descriptions-item label="物流单号">{{ order.logisticsNo || '-' }}</el-descriptions-item>
      </el-descriptions>

      <h3 style="margin-top:20px">商品明细</h3>
      <el-table :data="order.items || []" border stripe>
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="price" label="单价" width="100"><template #default="{row}">¥{{row.price}}</template></el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="小计" width="120"><template #default="{row}">¥{{row.price * row.quantity}}</template></el-table-column>
      </el-table>

      <h3 style="margin-top:20px" v-if="(order.logs||[]).length">状态日志</h3>
      <el-timeline v-if="(order.logs||[]).length">
        <el-timeline-item v-for="l in order.logs" :key="l.id" :timestamp="l.createTime">{{ l.remark || statusText(l.orderStatus) }}</el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const order = ref({})
const loading = ref(false)

function statusTag(s) { return {0:'warning',1:'primary',2:'',3:'success',4:'info',5:'danger'}[s]||'' }
function statusText(s) { return {0:'待付款',1:'待发货',2:'已发货',3:'已完成',4:'已取消',5:'售后中'}[s]||'未知' }

onMounted(async () => {
  loading.value = true
  try { const r = await axios.get(`/api/farmer/order/detail/${route.params.id}`); order.value = r.data.data || {} }
  finally { loading.value = false }
})
</script>

<style scoped>
.page-container { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-title { font-size: 18px; font-weight: 600; }
h3 { font-size: 16px; font-weight: 600; margin-bottom: 12px; }
</style>
<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6" v-for="card in statCards" :key="card.key">
        <el-card shadow="hover" class="stat-card" :style="{ borderTop: '3px solid ' + card.color }">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats[card.key] ?? 0 }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
            <el-icon :size="40" :color="card.color">{{ card.icon }}</el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><div class="card-header"><span>数据概览</span></div></template>
          <div class="chart-container" ref="pieChartRef" style="height:320px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><div class="card-header"><span>待处理事项</span></div></template>
          <div class="pending-list">
            <div class="pending-item" @click="$router.push('/admin/comments')">
              <el-icon color="#e6a23c"><ChatLineSquare /></el-icon>
              <span>待审核评论</span>
              <el-tag type="warning">{{ stats.pendingCommentCount ?? 0 }}</el-tag>
            </div>
            <div class="pending-item" @click="$router.push('/admin/users')">
              <el-icon color="#f56c6c"><User /></el-icon>
              <span>待审核农户</span>
              <el-tag type="danger">{{ stats.pendingFarmerCount ?? 0 }}</el-tag>
            </div>
            <div class="pending-item" @click="$router.push('/admin/orders')">
              <el-icon color="#409eff"><Document /></el-icon>
              <span>待发货订单</span>
              <el-tag type="primary">查看</el-tag>
            </div>
            <div class="pending-item" @click="$router.push('/admin/after-sales')">
              <el-icon color="#67c23a"><Service /></el-icon>
              <span>售后工单</span>
              <el-tag type="success">处理</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { getHomeStats } from '../../api/admin'
import * as echarts from 'echarts'

const stats = reactive({})
const pieChartRef = ref(null)
let chartInstance = null

const statCards = [
  { key: 'userCount', label: '用户总数', color: '#409eff', icon: 'User' },
  { key: 'productCount', label: '商品总数', color: '#67c23a', icon: 'Goods' },
  { key: 'orderCount', label: '订单总数', color: '#e6a23c', icon: 'Document' },
  { key: 'totalSales', label: '总销售额', color: '#f56c6c', icon: 'Money' },
]

async function loadStats() {
  try {
    const res = await getHomeStats()
    Object.assign(stats, res.data)
  } catch { /* handled by interceptor */ }
}

function initChart() {
  if (!pieChartRef.value) return
  if (chartInstance) chartInstance.dispose()
  chartInstance = echarts.init(pieChartRef.value)
  chartInstance.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%' },
    series: [{
      name: '数据概览',
      type: 'pie',
      radius: ['45%', '75%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{d}%' },
      data: [
        { value: stats.onlineProductCount ?? 0, name: '在售商品' },
        { value: (stats.productCount ?? 0) - (stats.onlineProductCount ?? 0), name: '下架商品' },
        { value: stats.orderCount ?? 0, name: '订单数' },
        { value: stats.userCount ?? 0, name: '用户数' }
      ]
    }]
  })
}

onMounted(async () => {
  await loadStats()
  initChart()
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.stat-content { display: flex; justify-content: space-between; align-items: center; }
.stat-value { font-size: 30px; font-weight: bold; color: #303133; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }
.stat-card { cursor: pointer; transition: transform .2s; }
.stat-card:hover { transform: translateY(-3px); }
.card-header { font-weight: 600; font-size: 15px; }
.pending-list { display: flex; flex-direction: column; gap: 12px; }
.pending-item { display: flex; align-items: center; gap: 12px; padding: 12px; background: #f5f7fa; border-radius: 8px; cursor: pointer; transition: background .2s; }
.pending-item:hover { background: #ecf5ff; }
.pending-item span { flex: 1; font-size: 14px; }
</style>

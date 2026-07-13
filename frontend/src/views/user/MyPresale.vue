<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">我的预售</span>
      </div>

      <div v-if="orders.length">
        <el-card v-for="order in orders" :key="order.id" shadow="hover" style="margin-bottom:12px">
          <div class="presale-order">
            <div class="order-header">
              <span>订单号：{{ order.orderNo }}</span>
              <el-tag :type="presaleStatusType(order.presaleStatus)" size="small">{{ presaleStatusText(order.presaleStatus) }}</el-tag>
            </div>
            <div class="order-body" v-for="item in order.items" :key="item.id">
              <el-image :src="item.productImage" style="width:80px;height:60px;border-radius:6px" fit="cover" />
              <div class="item-info">
                <h4>{{ item.productName }}</h4>
                <p>¥{{ item.price }} × {{ item.quantity }}</p>
              </div>
            </div>

            <!-- 生长动态 -->
            <div v-if="order.growthList && order.growthList.length" class="growth-section">
              <h4>生长动态</h4>
              <el-timeline>
                <el-timeline-item v-for="g in order.growthList" :key="g.id" :timestamp="g.createTime" :color="stageColor(g.stage)">
                  <h5>{{ g.title }} <el-tag size="small" :color="stageColor(g.stage)" style="color:#fff">{{ stageText(g.stage) }}</el-tag></h5>
                  <p style="color:#666;font-size:13px">{{ g.content }}</p>
                  <el-image v-if="g.imageUrls" :src="g.imageUrls" style="width:100px;height:70px;margin-top:4px;border-radius:4px" fit="cover" />
                </el-timeline-item>
              </el-timeline>
            </div>
            <el-empty v-else-if="order.presaleStatus >= 1" description="暂无生长动态" :image-size="40" />

            <div class="order-footer">
              <span class="order-time">{{ order.createTime }}</span>
              <span class="order-amount">合计：<b>¥{{ order.payAmount || order.totalAmount }}</b></span>
            </div>
          </div>
        </el-card>
      </div>
      <el-empty v-else description="暂无预售订单" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const orders = ref([])

function presaleStatusText(s) { return {0:'普通订单',1:'等待种植',2:'生长中',3:'已成熟',4:'已发货'}[s]||'未知' }
function presaleStatusType(s) { return {0:'info',1:'warning',2:'',3:'success',4:'primary'}[s]||'info' }
function stageText(s) { return {sow:'播种',seedling:'发芽',bloom:'开花',fruit:'结果',ripe:'成熟'}[s]||s }
function stageColor(s) { return {sow:'#52c41a',seedling:'#73d13d',bloom:'#faad14',fruit:'#fa8c16',ripe:'#f56c6c'}[s]||'#999' }

async function loadData() {
  try {
    const r = await axios.get('/api/order/list?pageNum=1&pageSize=50')
    const allOrders = r.data.data?.records || []
    // 只显示预售订单
    orders.value = allOrders.filter(o => o.presaleStatus > 0)
    // 加载每个订单的生长动态
    for (const order of orders.value) {
      try {
        const gr = await axios.get(`/api/presale/growth/${order.id}`)
        order.growthList = gr.data.data?.growthList || []
      } catch { order.growthList = [] }
    }
  } catch {}
}

onMounted(loadData)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}
.presale-order{display:flex;flex-direction:column;gap:12px}
.order-header{display:flex;justify-content:space-between;align-items:center;font-size:13px;color:#666}
.order-body{display:flex;gap:12px;align-items:center}
.item-info h4{font-size:14px;margin-bottom:4px}.item-info p{color:#f56c6c;font-size:13px}
.growth-section{background:#f9f9f9;padding:12px;border-radius:8px}
.growth-section h4{margin-bottom:8px;font-size:14px}
.order-footer{display:flex;justify-content:space-between;align-items:center;border-top:1px solid #eee;padding-top:8px}
.order-time{font-size:12px;color:#999}.order-amount b{color:#f56c6c;font-size:16px}
</style>

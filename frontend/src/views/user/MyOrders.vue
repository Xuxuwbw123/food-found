<template>
  <div class="orders-page">
    <div class="section-content">
      <h2 v-if="auth.isLoggedIn">我的订单</h2>
      <el-empty v-if="!auth.isLoggedIn" description="请先登录"><el-button type="primary" @click="$router.push('/login')">去登录</el-button></el-empty>
      <div v-else v-loading="loading">
        <div v-for="o in orders" :key="o.id" class="order-card">
          <div class="order-header">
            <span>订单号：{{ o.orderNo }}</span>
            <el-tag :type="statusTag(o.orderStatus)" size="small">{{ statusText(o.orderStatus) }}</el-tag>
            <el-button v-if="o.orderStatus===0" type="danger" size="small" @click="payOrder(o.id)">去付款 ¥{{ o.payAmount || o.totalAmount }}</el-button>
            <el-button v-if="o.orderStatus===0||o.orderStatus===1" link type="warning" size="small" @click="cancelOrder(o.id)">取消订单</el-button>
            <el-button v-if="o.orderStatus===2||o.orderStatus===3" size="small" @click="goLogistics(o)">📦 物流</el-button>
            <el-button v-if="o.orderStatus===2" link type="danger" size="small" @click="applyAfterSales(o, 'intercept')">🚫 拦截退货</el-button>
            <el-button v-if="o.orderStatus===3" link type="danger" size="small" @click="applyAfterSales(o, 'return')">申请售后</el-button>
            <el-button v-if="o.orderStatus===3 && !o.isCommented" type="warning" size="small" @click="openReview(o)">去评价</el-button>
            <el-tag v-if="o.orderStatus===3 && o.isCommented" type="success" size="small">已评价</el-tag>
            <span style="margin-left:auto;color:#999;font-size:13px">{{ o.createTime }}</span>
          </div>
          <div v-for="item in o.items" :key="item.id" class="order-item">
            <el-image :src="item.productImage" style="width:60px;height:60px;border-radius:6px" fit="cover" />
            <div class="item-info">
              <div class="item-name">{{ item.productName }}</div>
              <div class="item-meta">¥{{ item.price }} × {{ item.quantity }} {{ item.unit }}</div>
            </div>
            <div class="item-actions">
              <el-button v-if="item.traceId" size="small" type="success" @click="goTrace(item.productId)">🔍 查看溯源</el-button>
            </div>
          </div>
          <div class="order-footer">
            <span>共 {{ o.items?.length || 0 }} 件商品，合计：<b style="color:#f56c6c">¥{{ o.payAmount || o.totalAmount }}</b></span>
          </div>
        </div>
        <el-empty v-if="orders.length===0" description="暂无订单" />
      </div>

      <!-- 评价弹窗 -->
      <el-dialog v-model="reviewVisible" title="评价晒单" width="500px">
        <div style="text-align:center;margin-bottom:16px">
          <span style="font-size:14px;margin-right:8px">评分：</span>
          <el-rate v-model="reviewForm.rating" :max="5" show-text />
        </div>
        <el-input v-model="reviewForm.content" type="textarea" :rows="4" placeholder="分享你的购买体验..." />
        <template #footer>
          <el-button @click="reviewVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReview" :loading="reviewLoading">提交评价</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import axios from 'axios'

const router = useRouter()

const auth = useAuthStore(); auth.restoreSession()
const orders = ref([]); const loading = ref(false)

function statusTag(s) { return {0:'warning',1:'primary',2:'',3:'success',4:'info',5:'danger'}[s]||'' }
function statusText(s) { return {0:'待付款',1:'待发货',2:'已发货',3:'已完成',4:'已取消',5:'售后中'}[s]||'未知' }

async function goTrace(productId) {
  try {
    const res = await axios.get(`/api/trace/by-product/${productId}`)
    if (res.data.data?.batchNo) router.push(`/trace/${res.data.data.batchNo}`)
    else router.push('/trace')
  } catch { router.push('/trace') }
}
function goLogistics(o) {
  router.push(`/logistics/${o.id}`)
}
async function payOrder(id) {
  try { await axios.put(`/api/order/pay/${id}`); ElMessage.success('支付成功'); load() } catch { ElMessage.error('支付失败') }
}
async function cancelOrder(id) {
  try {
    await axios.put(`/api/order/cancel/${id}`)
    ElMessage.success('订单已取消')
    // 直接从列表移除，不依赖页面刷新
    orders.value = orders.value.filter(o => String(o.id) !== String(id))
  } catch { ElMessage.error('取消失败') }
}
// ===== 评价 =====
const reviewVisible = ref(false)
const reviewForm = reactive({ orderId: null, productId: null, rating: 5, content: '', images: [] })
const reviewLoading = ref(false)
function openReview(o) {
  const item = o.items?.[0]
  if (!item) return
  reviewForm.orderId = o.id
  reviewForm.productId = item.productId
  reviewForm.rating = 5
  reviewForm.content = ''
  reviewForm.images = []
  reviewVisible.value = true
}
async function submitReview() {
  if (!reviewForm.content.trim()) { ElMessage.warning('请输入评价内容'); return }
  reviewLoading.value = true
  try {
    await axios.post('/api/comment/add', { ...reviewForm })
    ElMessage.success('评价成功')
    reviewVisible.value = false
    load()
  } catch (e) { ElMessage.error(e.response?.data?.message || '评价失败') }
  finally { reviewLoading.value = false }
}

function applyAfterSales(o, mode) {
  const item = o.items?.[0]
  if (!item) return
  router.push({ path: '/after-sales', query: { orderId: o.id, orderNo: o.orderNo, productId: item.productId, productName: item.productName, refundAmount: item.price * item.quantity, mode } })
}

async function load() {
  loading.value = true
  try {
    const r = await axios.get('/api/order/mylist', { params: { userId: auth.user.id, _t: Date.now() } })
    console.log('[MyOrders] API响应:', JSON.stringify(r.data).substring(0, 500))
    const allOrders = r.data.data?.records || r.data.data || []
    console.log('[MyOrders] 订单数量:', allOrders.length, '状态分布:', allOrders.map(o => o.orderStatus))
    orders.value = allOrders.filter(o => o.orderStatus !== 4)
    console.log('[MyOrders] 过滤后:', orders.value.length)
  } finally { loading.value = false }
}
onMounted(() => { if (auth.isLoggedIn) load() })
</script>

<style scoped>
.orders-page { min-height: 70vh; background: #f5f6f7; padding-bottom: 40px; }
.section-content { max-width: 900px; margin: 0 auto; padding: 20px; }
h2 { font-size: 20px; padding: 10px 0; }
.order-card { background: #fff; border-radius: 8px; margin-bottom: 12px; overflow: hidden; }
.order-header { display: flex; align-items: center; gap: 12px; padding: 12px 16px; background: #fafafa; border-bottom: 1px solid #eee; font-size: 14px; }
.order-item { display: flex; align-items: center; gap: 12px; padding: 12px 16px; border-bottom: 1px solid #f5f5f5; }
.item-info { flex: 1; }
.item-name { font-size: 15px; font-weight: 600; }
.item-meta { font-size: 13px; color: #999; margin-top: 4px; }
.item-actions { display: flex; gap: 8px; }
.order-footer { padding: 10px 16px; text-align: right; font-size: 14px; }
</style>

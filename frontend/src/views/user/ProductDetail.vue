<template>
  <div class="product-detail">
    <div class="section-content">
      <el-breadcrumb separator="/" style="padding:20px 0">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>{{ product.productName }}</el-breadcrumb-item>
      </el-breadcrumb>

      <div v-loading="loading" class="detail-main">
        <div class="detail-left">
          <el-image :src="product.mainImage" fit="cover" style="width:100%;height:420px;border-radius:12px">
            <template #error><el-icon :size="80" color="#dcdfe6"><Picture /></el-icon></template>
          </el-image>
        </div>
        <div class="detail-right">
          <h1 class="product-title">{{ product.productName }}</h1>
          <div class="product-subtitle">
            <span>{{ product.productNo }}</span>
            <span class="divider">|</span>
            <span>产地：{{ product.originPlace }}</span>
            <span class="divider">|</span>
            <span>规格：{{ product.weight }} / {{ product.unit }}</span>
          </div>
          <div class="price-box">
            <div class="price-main">
              <span class="price-label">售价</span>
              <span class="price-now">¥{{ product.price }}</span>
              <span v-if="product.originalPrice > product.price" class="price-old">¥{{ product.originalPrice }}</span>
            </div>
            <div class="price-meta">
              <span>库存 {{ product.stock }}{{ product.unit }}</span>
              <span class="divider">|</span>
              <span>已售 {{ product.sales || 0 }}{{ product.unit }}</span>
              <span class="divider">|</span>
              <span>好评率 {{ product.goodRate || 0 }}%</span>
            </div>
          </div>
          <div class="tags-row">
            <el-tag v-if="product.isRecommend === 1" type="danger">推荐</el-tag>
            <el-tag v-if="product.isNew === 1" type="warning">新品</el-tag>
            <el-tag v-if="product.isHot === 1" type="success">热销</el-tag>
            <el-tag v-if="product.traceId" type="success" effect="dark" @click="goTrace" style="cursor:pointer">
              <el-icon><Search /></el-icon> 可溯源
            </el-tag>
          </div>
          <div class="action-row">
            <el-input-number v-model="quantity" :min="1" :max="product.stock" size="large" />
            <el-button type="danger" size="large" style="width:180px" @click="buyNow">立即购买</el-button>
            <el-button size="large" style="width:140px" @click="addCart">加入购物车</el-button>
            <el-button size="large" :type="isFaved ? 'warning' : 'default'" @click="toggleFavorite">
              {{ isFaved ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </div>
      </div>

      <div class="detail-description" style="margin-top:30px">
        <el-card shadow="never">
          <template #header>商品描述</template>
          <div class="desc-content">{{ product.description || '暂无详细描述' }}</div>
        </el-card>
      </div>

      <div style="margin-top:30px">
        <el-card shadow="never">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span>用户评价（{{comments.length}}条）</span>
              <el-button v-if="auth.isLoggedIn" type="primary" size="small" @click="showCommentDialog=true">写评价</el-button>
            </div>
          </template>
          <div v-if="comments.length===0" style="text-align:center;color:#999;padding:20px">暂无评价</div>
          <div v-for="c in comments" :key="c.id" style="border-bottom:1px solid #f0f0f0;padding:12px 0">
            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:6px">
              <div>
                <span style="font-weight:600">{{ c.nickname || c.username || '匿名用户' }}</span>
                <el-rate :model-value="c.rating" disabled size="small" style="display:inline-block;vertical-align:middle;margin-left:8px" />
              </div>
              <span style="font-size:12px;color:#999">{{ c.createTime }}</span>
            </div>
            <div style="color:#333;line-height:1.6">{{ c.content }}</div>
          </div>
        </el-card>
      </div>
    </div>
  </div>

  <!-- 评论弹窗 -->
  <el-dialog v-model="showCommentDialog" title="写评价" width="500px">
    <div style="text-align:center;margin-bottom:16px">
      <span style="font-size:14px;margin-right:8px">评分：</span>
      <el-rate v-model="commentForm.rating" :max="5" show-text />
    </div>
    <el-input v-model="commentForm.content" type="textarea" :rows="4" placeholder="分享您的购买体验..." />
    <template #footer>
      <el-button @click="showCommentDialog=false">取消</el-button>
      <el-button type="primary" @click="submitComment" :loading="commentLoading">提交评价</el-button>
    </template>
  </el-dialog>

  <!-- 单品结算弹窗（地址+优惠券） -->
  <el-dialog title="确认订单" v-model="showPay" width="500px">
    <h4 style="margin-bottom:8px">收货地址</h4>
    <div v-if="addresses.length===0">
      <el-empty description="暂无收货地址"><el-button type="primary" @click="$router.push('/address')">去添加</el-button></el-empty>
    </div>
    <div v-else>
      <div v-for="a in addresses" :key="a.id" class="addr-opt" :class="{sel:selAddr===a}" @click="selAddr=a">
        <div style="font-weight:600">{{a.receiverName}} {{a.receiverPhone}}</div>
        <div style="color:#666;margin-top:2px">{{a.province}}{{a.city}}{{a.district}} {{a.detailAddress}}</div>
        <el-tag v-if="a.isDefault===1" type="success" size="small">默认</el-tag>
      </div>
    </div>
    <h4 style="margin:16px 0 8px">优惠券</h4>
    <el-select v-model="selCouponId" placeholder="选择优惠券（可选）" clearable style="width:100%" @change="calcDiscount">
      <el-option v-for="c in coupons" :key="c.id" :label="c.name+' ¥'+c.faceValue+(c.minAmount>0?' (满'+c.minAmount+')':'')" :value="c.couponId" />
    </el-select>
    <div v-if="discount>0" style="text-align:right;color:#f56c6c;margin-top:8px">优惠：-¥{{discount}}</div>
    <div style="text-align:right;margin-top:12px;font-size:20px;font-weight:700;color:#f56c6c">实付：¥{{ actualTotal }}</div>
    <template #footer>
      <el-button @click="showPay=false">取消</el-button>
      <el-button type="danger" @click="doPay" :loading="paying">立即支付 ¥{{ actualTotal }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useAuthStore } from '../../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
auth.restoreSession()
const product = ref({})
const loading = ref(false)
const quantity = ref(1)

function requireLogin() {
  if (!auth.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return false
  }
  return true
}

const showPay = ref(false); const paying = ref(false)
const addresses = ref([]); const selAddr = ref(null)
const coupons = ref([]); const selCouponId = ref(null); const discount = ref(0)
const actualTotal = computed(() => Math.max(0, (product.value.price || 0) * quantity.value - discount.value).toFixed(2))

async function buyNow() {
  if (!requireLogin()) return
  if (!product.value.id) { ElMessage.warning('商品加载中'); return }
  try {
    const [a, c] = await Promise.all([axios.get('/api/address/list',{params:{userId:auth.user.id}}), axios.get('/api/coupon/my',{params:{status:'unused'}})])
    addresses.value = a.data.data || []
    coupons.value = c.data.data || []
    discount.value = 0; selCouponId.value = null
    if (addresses.value.length === 0) { ElMessage.warning('请先添加收货地址'); router.push('/address'); return }
    selAddr.value = addresses.value.find(x => x.isDefault === 1) || addresses.value[0]
    showPay.value = true
  } catch { ElMessage.error('获取信息失败') }
}

function calcDiscount() {
  const c = coupons.value.find(x => x.id === selCouponId.value)
  if (!c) { discount.value = 0; return }
  const subtotal = (product.value.price || 0) * quantity.value
  if (c.minAmount > 0 && subtotal < c.minAmount) { ElMessage.warning('未满足使用门槛'); selCouponId.value = null; discount.value = 0; return }
  discount.value = parseFloat(c.faceValue)
}

async function doPay() {
  if (!selAddr.value) { ElMessage.warning('请选择地址'); return }
  paying.value = true
  try {
    const body = { userId: auth.user.id, items: [{ productId: product.value.id, productName: product.value.productName, productImage: product.value.mainImage, price: product.value.price, quantity: quantity.value, unit: product.value.unit, traceId: product.value.traceId }], address: selAddr.value }
    if (selCouponId.value) body.couponId = selCouponId.value
    console.log('[ProductDetail] 创建订单:', JSON.stringify(body))
    const res = await axios.post('/api/order/create', body)
    console.log('[ProductDetail] 订单响应:', JSON.stringify(res.data))
    showPay.value = false
    const orderId = res.data?.data?.orderId
    const total = res.data?.data?.total
    console.log('[ProductDetail] 跳转支付, orderId:', orderId, 'total:', total)
    router.push({ path: '/pay', query: { orderId, total } })
  } catch(e) { console.error('[ProductDetail] 下单失败:', e); ElMessage.error('下单失败') }
  finally { paying.value = false }
}

async function goTrace() {
  try {
    const res = await axios.get(`/api/trace/by-product/${product.value.id}`)
    if (res.data.data?.batchNo) {
      router.push(`/trace/${res.data.data.batchNo}`)
    } else {
      router.push('/trace')
    }
  } catch { router.push('/trace') }
}

async function addCart() {
  if (!requireLogin()) return
  try {
    await axios.post('/api/cart/add', {
      userId: auth.user.id, productId: product.value.id,
      productName: product.value.productName, productImage: product.value.mainImage,
      price: product.value.price, quantity: quantity.value
    })
    ElMessage.success('已加入购物车')
  } catch { ElMessage.error('操作失败') }
}

// ===== 收藏 =====
const isFaved = ref(false)
async function checkFav() {
  if (!auth.isLoggedIn) return
  try { const r = await axios.get(`/api/favorite/check/${route.params.id}`); isFaved.value = r.data.data?.faved || false } catch {}
}
async function toggleFavorite() {
  if (!requireLogin()) return
  try {
    if (isFaved.value) {
      await axios.delete(`/api/favorite/remove/${route.params.id}`)
      isFaved.value = false
      ElMessage.success('已取消收藏')
    } else {
      await axios.post('/api/favorite/add', { productId: route.params.id })
      isFaved.value = true
      ElMessage.success('已收藏')
    }
  } catch { ElMessage.error('操作失败') }
}

async function loadData() {
  loading.value = true
  try {
    const res = await axios.get(`/api/public/products/detail/${route.params.id}`)
    const d = res.data?.data || res.data || {}
    product.value = d
    // 记录浏览足迹
    if (product.value.id) {
      try { await axios.post('/api/footprint/add', { productId: product.value.id }) } catch {}
    }
  } finally { loading.value = false }
}

// ===== 评论 =====
const comments = ref([])
const showCommentDialog = ref(false)
const commentLoading = ref(false)
const commentForm = reactive({ rating: 5, content: '' })

async function loadComments() {
  try { const r = await axios.get(`/api/comment/product/${route.params.id}`); comments.value = r.data.data || [] } catch {}
}

async function submitComment() {
  if (!commentForm.content.trim()) { ElMessage.warning('请输入评价内容'); return }
  commentLoading.value = true
  try {
    await axios.post('/api/comment/direct', { productId: parseInt(route.params.id), rating: commentForm.rating, content: commentForm.content })
    ElMessage.success('评论成功')
    showCommentDialog.value = false
    commentForm.rating = 5; commentForm.content = ''
    loadComments()
  } catch (e) { ElMessage.error(e.response?.data?.message || '评论失败') }
  finally { commentLoading.value = false }
}

onMounted(() => { loadData(); checkFav(); loadComments() })
</script>

<style scoped>
.product-detail { background: #f5f6f7; min-height: 80vh; }
.section-content { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.detail-main { display: flex; gap: 30px; background: #fff; border-radius: 12px; padding: 24px; }
.detail-left { flex: 0 0 480px; }
.thumb-list { display: flex; gap: 8px; margin-top: 12px; }
.thumb-item:hover { border-color: #1a8c3a !important; }
.detail-right { flex: 1; display: flex; flex-direction: column; gap: 16px; }
.product-title { font-size: 22px; font-weight: 700; color: #333; }
.product-subtitle { font-size: 13px; color: #999; display: flex; gap: 10px; align-items: center; }
.divider { color: #e4e7ed; }
.price-box { background: #fef0f0; border-radius: 8px; padding: 16px; }
.price-main { display: flex; align-items: baseline; gap: 8px; }
.price-label { font-size: 13px; color: #999; }
.price-now { font-size: 30px; font-weight: 700; color: #f56c6c; }
.price-old { font-size: 14px; color: #ccc; text-decoration: line-through; }
.price-meta { font-size: 13px; color: #999; margin-top: 8px; display: flex; gap: 10px; }
.tags-row { display: flex; gap: 8px; }
.action-row { display: flex; gap: 12px; align-items: center; margin-top: 10px; }
.desc-content { font-size: 14px; color: #666; line-height: 1.8; white-space: pre-wrap; }
.addr-opt { padding: 10px; border: 2px solid #eee; border-radius: 8px; cursor: pointer; margin-bottom: 8px; position: relative; }
.addr-opt.sel { border-color: #1a8c3a; background: #f1f8e9; }
h4 { margin: 8px 0; font-size: 14px; color: #666; }
</style>

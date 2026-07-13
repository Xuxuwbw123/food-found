<template>
  <div class="cart-page"><div class="section-content">
    <h2 v-if="auth.isLoggedIn">我的购物车</h2>
    <el-empty v-if="!auth.isLoggedIn" description="请先登录"><el-button type="primary" @click="$router.push('/login')">去登录</el-button></el-empty>
    <div v-else v-loading="loading">
      <el-table v-if="list.length" :data="list" border stripe style="width:100%">
        <el-table-column label="商品" width="60"><template #default="{row}"><el-image :src="row.productImage||row.mainImage" style="width:50px;height:50px;border-radius:6px" fit="cover"/></template></el-table-column>
        <el-table-column prop="productName" label="名称" min-width="160"/>
        <el-table-column prop="price" label="单价" width="100"><template #default="{row}">¥{{row.price}}</template></el-table-column>
        <el-table-column label="数量" width="160"><template #default="{row}"><el-input-number v-model="row.quantity" :min="1" size="small" @change="updateQty(row)"/></template></el-table-column>
        <el-table-column label="小计" width="100"><template #default="{row}">¥{{(row.price*row.quantity).toFixed(2)}}</template></el-table-column>
        <el-table-column label="操作" width="80"><template #default="{row}"><el-button link type="danger" size="small" @click="delItem(row.id)">删除</el-button></template></el-table-column>
      </el-table>
      <el-empty v-else description="购物车是空的"/>
      <div v-if="list.length" style="text-align:right;margin-top:20px">
        <div v-if="memberLevel>0" style="font-size:13px;color:#52c41a;margin-bottom:4px">
          会员{{(discountRate*10).toFixed(1)}}折，省 ¥{{memberSavings}}
        </div>
        <div v-if="discount>0" style="font-size:13px;color:#f56c6c;margin-bottom:4px">
          优惠券 -¥{{discount}}
        </div>
        <span style="font-size:20px;font-weight:700;color:#f56c6c">合计：¥{{ actualTotal }}</span>
        <el-button type="danger" size="large" style="margin-left:16px" @click="openCheckout">结算</el-button>
      </div>
    </div>
    <!-- 选择地址对话框 -->
    <el-dialog title="确认订单" v-model="showAddr" width="550px">
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
      <div style="text-align:right;margin-top:8px;font-size:13px">
        <div v-if="memberLevel>0" style="color:#52c41a">会员{{(discountRate*10).toFixed(1)}}折：-¥{{memberSavings}}</div>
        <div v-if="discount>0" style="color:#f56c6c">优惠券：-¥{{discount}}</div>
      </div>
      <div style="text-align:right;margin-top:8px;font-size:20px;font-weight:700;color:#f56c6c">合计：¥{{ actualTotal }}</div>
      <template #footer>
        <el-button @click="showAddr=false">取消</el-button>
        <el-button type="danger" @click="doCheckout" :loading="paying">确认下单</el-button>
      </template>
    </el-dialog>
  </div></div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useAuthStore } from '../../stores/auth'

const auth = useAuthStore(); auth.restoreSession()
const router = useRouter()
const list = ref([]); const loading = ref(false); const paying = ref(false)
const showAddr = ref(false); const addresses = ref([]); const selAddr = ref(null)
const total = computed(()=>list.value.reduce((s,i)=>s+i.price*i.quantity,0).toFixed(2))
const coupons = ref([]); const selCouponId = ref(null); const discount = ref(0)
const memberDiscount = ref(0); const memberLevel = ref(0); const discountRate = ref(1)
const memberSavings = computed(()=>(parseFloat(total.value)*(1-discountRate.value)).toFixed(2))
const actualTotal = computed(()=>Math.max(0,parseFloat(total.value)-parseFloat(memberSavings.value)-discount.value).toFixed(2))

async function loadCoupons() {
  try { const r = await axios.get('/api/coupon/my',{params:{status:'unused'}}); coupons.value = r.data.data || [] } catch {}
}
async function loadMemberInfo() {
  try {
    const r = await axios.get('/api/member/info')
    const d = r.data.data
    memberLevel.value = d.memberLevel || 0
    discountRate.value = d.discountRate || 1
  } catch {}
}
function calcDiscount() {
  const c = coupons.value.find(c=>c.id===selCouponId.value)
  if (!c) { discount.value = 0; return }
  if (c.minAmount > 0 && parseFloat(total.value) < c.minAmount) { ElMessage.warning('未满足使用门槛'); selCouponId.value=null; discount.value=0; return }
  discount.value = parseFloat(c.faceValue)
}

async function load() { loading.value=true; try{const r=await axios.get('/api/cart/list',{params:{userId:auth.user.id}});list.value=r.data.data?.records||r.data.data||[]}finally{loading.value=false} }
async function updateQty(row) { await axios.put('/api/cart/update',{id:row.id,quantity:row.quantity});load() }
async function delItem(id) { await axios.delete(`/api/cart/delete/${id}`); ElMessage.success('已移除'); load() }

async function openCheckout() {
  try {
    const[addrRes,couponRes]=await Promise.all([axios.get('/api/address/list',{params:{userId:auth.user.id}}),axios.get('/api/coupon/my',{params:{status:'unused'}})])
    addresses.value = addrRes.data.data || []; coupons.value = couponRes.data.data || []; discount.value = 0; selCouponId.value = null
    if (addresses.value.length===0) { ElMessage.warning('请先添加收货地址'); router.push('/address'); return }
    selAddr.value = addresses.value.find(a=>a.isDefault===1) || addresses.value[0]
    showAddr.value = true
  } catch { ElMessage.error('获取信息失败') }
}

async function doCheckout() {
  if (!selAddr.value) { ElMessage.warning('请选择收货地址'); return }
  paying.value = true
  try {
    const items = list.value.map(i=>({productId:i.productId,productName:i.productName,productImage:i.productImage||i.mainImage,price:i.price,quantity:i.quantity,unit:i.unit,traceId:i.traceId}))
    const body = {userId:auth.user.id,items,address:selAddr.value}
    if (selCouponId.value) body.couponId = selCouponId.value
    console.log('[Cart] 下单请求:', JSON.stringify(body))
    const res = await axios.post('/api/order/create', body)
    console.log('[Cart] 下单响应:', JSON.stringify(res.data))
    showAddr.value = false
    const orderId = res.data?.data?.orderId
    const total = res.data?.data?.total
    console.log('[Cart] 跳转支付页, orderId:', orderId, 'total:', total)
    router.push({path:'/pay',query:{orderId,total}})
  } catch(e) { console.error('[Cart] 下单失败:', e); ElMessage.error('下单失败') }
  finally { paying.value = false }
}

onMounted(()=>{if(auth.isLoggedIn){load();loadMemberInfo()}})
</script>

<style scoped>
.cart-page { min-height:70vh; background:#f5f6f7; padding-bottom:40px }
.section-content { max-width:960px; margin:0 auto; padding:20px }
h2 { font-size:20px; padding:10px 0 }
.addr-opt { border:2px solid #eee; border-radius:8px; padding:14px; margin-bottom:8px; cursor:pointer; position:relative }
.addr-opt.sel { border-color:#1a8c3a; background:#f1f8e9 }
.addr-opt .el-tag { position:absolute; right:12px; top:14px }
</style>

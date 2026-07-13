<template>
  <div class="pay-page">
    <div class="pay-card">
      <h2>确认支付</h2>

      <!-- 优惠信息 -->
      <div v-if="payOptions.totalDiscount > 0" class="member-info">
        <el-tag v-if="payOptions.memberLevel > 0" :type="['','success','warning','danger'][payOptions.memberLevel]" effect="dark">
          {{ payOptions.levelName }} {{ (payOptions.discountRate * 10).toFixed(1) }}折
        </el-tag>
        <span class="savings-text">共省 ¥{{ payOptions.totalDiscount }}</span>
      </div>

      <div class="pay-amount">
        <span v-if="payOptions.totalDiscount > 0" class="original-amount">¥{{ payOptions.originalAmount }}</span>
        ¥{{ payOptions.payAmount || total }}
      </div>

      <div class="pay-methods">
        <div class="pay-method" :class="{active: method==='wechat'}" @click="method='wechat'">
          <span style="color:#07c160;font-size:24px">💚</span>
          <span>微信支付</span>
          <el-icon v-if="method==='wechat'" color="#07c160"><Check /></el-icon>
        </div>
        <div class="pay-method" :class="{active: method==='alipay'}" @click="method='alipay'">
          <span style="color:#1677ff;font-size:24px">💙</span>
          <span>支付宝</span>
          <el-icon v-if="method==='alipay'" color="#1677ff"><Check /></el-icon>
        </div>
        <div class="pay-method" :class="{active: method==='balance', disabled: !payOptions.canPayByBalance}" @click="payOptions.canPayByBalance && (method='balance')">
          <span style="color:#faad14;font-size:24px">💳</span>
          <div style="flex:1">
            <span>会员卡支付</span>
            <div style="font-size:12px;color:#999;margin-top:2px">
              余额 ¥{{ payOptions.balance }}
              <span v-if="!payOptions.canPayByBalance" style="color:#f56c6c">（余额不足）</span>
            </div>
          </div>
          <el-icon v-if="method==='balance'" color="#faad14"><Check /></el-icon>
        </div>
      </div>

      <el-button type="danger" size="large" style="width:100%;margin-top:24px" @click="doPay" :loading="paying">
        立即支付 ¥{{ payOptions.payAmount || total }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const route = useRoute(); const router = useRouter()
const total = ref(route.query.total || '0.00')
const orderId = ref(route.query.orderId || '')
const method = ref('wechat')
const paying = ref(false)
const payOptions = reactive({
  originalAmount: 0, payAmount: 0, totalDiscount: 0, balance: 0,
  memberLevel: 0, levelName: '普通用户', discountRate: 1,
  canPayByBalance: false, savings: 0
})

async function loadPayOptions() {
  if (!orderId.value) return
  try {
    const r = await axios.get(`/api/order/pay-options/${orderId.value}`)
    Object.assign(payOptions, r.data.data)
    if (payOptions.canPayByBalance) method.value = 'balance'
  } catch {}
}

async function doPay() {
  if (!orderId.value) { ElMessage.error('订单ID为空'); return }
  paying.value = true
  try {
    if (method.value === 'balance') {
      // 会员卡余额支付（orderId作为字符串传递，避免JS精度丢失）
      const r = await axios.post('/api/order/pay-by-balance', { orderId: orderId.value })
      ElMessage.success('支付成功！余额: ¥' + r.data.data.balance)
    } else {
      // 模拟支付宝/微信支付
      await axios.put(`/api/order/pay/${orderId.value}`)
      ElMessage.success('支付成功！')
    }
    // 强制刷新订单页面
    window.location.href = '/orders'
  } catch(e) { ElMessage.error(e.response?.data?.msg || '支付失败') }
  finally { paying.value = false }
}

onMounted(loadPayOptions)
</script>

<style scoped>
.pay-page { min-height: 70vh; display: flex; align-items: center; justify-content: center; background: #f5f6f7; }
.pay-card { background: #fff; border-radius: 12px; padding: 40px; width: 420px; box-shadow: 0 4px 20px rgba(0,0,0,.08); }
h2 { text-align: center; margin-bottom: 16px; }
.member-info { text-align: center; margin-bottom: 12px; display: flex; align-items: center; justify-content: center; gap: 8px; }
.discount-text { font-size: 14px; color: #f56c6c; font-weight: 600; }
.savings-text { font-size: 12px; color: #52c41a; background: #f6ffed; padding: 2px 8px; border-radius: 4px; }
.pay-amount { text-align: center; font-size: 36px; font-weight: 700; color: #f56c6c; margin-bottom: 24px; }
.original-amount { font-size: 18px; color: #ccc; text-decoration: line-through; margin-right: 8px; }
.pay-methods { display: flex; flex-direction: column; gap: 8px; }
.pay-method { display: flex; align-items: center; gap: 12px; padding: 14px; border: 2px solid #eee; border-radius: 8px; cursor: pointer; }
.pay-method.active { border-color: #1a8c3a; background: #f1f8e9; }
.pay-method.disabled { opacity: 0.5; cursor: not-allowed; }
.pay-method span { flex: 1; font-size: 15px; }
</style>

<template>
  <div class="pay-page">
    <div class="pay-card">
      <h2>确认支付</h2>
      <div class="pay-amount">¥{{ total }}</div>
      <div class="pay-methods">
        <div class="pay-method active" @click="method='wechat'">
          <span style="color:#07c160;font-size:24px">💚</span>
          <span>微信支付</span>
          <el-icon v-if="method==='wechat'" color="#07c160"><Check /></el-icon>
        </div>
        <div class="pay-method" @click="method='alipay'">
          <span style="color:#1677ff;font-size:24px">💙</span>
          <span>支付宝</span>
          <el-icon v-if="method==='alipay'" color="#1677ff"><Check /></el-icon>
        </div>
      </div>
      <el-button type="danger" size="large" style="width:100%;margin-top:24px" @click="doPay" :loading="paying">立即支付 ¥{{ total }}</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const route = useRoute(); const router = useRouter()
const total = ref(route.query.total || '0.00')
const orderId = ref(route.query.orderId || '')
const method = ref('wechat')
const paying = ref(false)

console.log('[Pay] 页面加载, orderId:', orderId.value, 'total:', total.value)

async function doPay() {
  console.log('[Pay] 点击支付, orderId:', orderId.value)
  if (!orderId.value) { ElMessage.error('订单ID为空，无法支付'); return }
  paying.value = true
  try {
    const res = await axios.put(`/api/order/pay/${orderId.value}`)
    console.log('[Pay] 支付响应:', JSON.stringify(res.data))
    ElMessage.success('支付成功！')
    window.location.href = '/orders'
  } catch(e) { console.error('[Pay] 支付失败:', e); ElMessage.error('支付失败') }
  finally { paying.value = false }
}
</script>

<style scoped>
.pay-page { min-height: 70vh; display: flex; align-items: center; justify-content: center; background: #f5f6f7; }
.pay-card { background: #fff; border-radius: 12px; padding: 40px; width: 420px; box-shadow: 0 4px 20px rgba(0,0,0,.08); }
h2 { text-align: center; margin-bottom: 16px; }
.pay-amount { text-align: center; font-size: 36px; font-weight: 700; color: #f56c6c; margin-bottom: 24px; }
.pay-methods { display: flex; flex-direction: column; gap: 8px; }
.pay-method { display: flex; align-items: center; gap: 12px; padding: 14px; border: 2px solid #eee; border-radius: 8px; cursor: pointer; }
.pay-method.active { border-color: #1a8c3a; background: #f1f8e9; }
.pay-method span { flex: 1; font-size: 15px; }
</style>

<template>
  <div class="as-page"><div class="section-content">
    <h2>{{ isIntercept ? '运输中拦截退货' : '申请售后' }}</h2>
    <el-alert v-if="isIntercept" title="商品正在运输途中，提交后将通知快递拦截退回，无需您自行寄回" type="warning" :closable="false" style="margin-bottom:16px"/>
    <el-alert v-else title="请确保已与商家沟通，退货退款需要您自行寄回商品" type="info" :closable="false" style="margin-bottom:16px"/>
    <el-card shadow="never" style="max-width:600px">
      <el-form :model="form" ref="formRef" label-width="90px">
        <el-form-item label="订单号"><el-input :model-value="orderNo" disabled /></el-form-item>
        <el-form-item label="商品"><el-input :model-value="productName" disabled /></el-form-item>
        <el-form-item label="售后类型" prop="afterSalesType">
          <el-radio-group v-model="form.afterSalesType">
            <el-radio :value="1">{{ isIntercept ? '仅退款（拦截退回）' : '仅退款' }}</el-radio>
            <el-radio :value="2" v-if="!isIntercept">退货退款</el-radio>
            <el-radio :value="3" v-if="!isIntercept">换货</el-radio>
            <el-radio :value="4" v-if="!isIntercept">补发</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="原因" prop="reason"><el-input v-model="form.reason" placeholder="请描述遇到的问题" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="详细描述"><el-input v-model="form.description" placeholder="补充说明" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="退款金额"><el-input-number v-model="form.refundAmount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item><el-button type="danger" @click="submit" :loading="loading">提交申请</el-button></el-form-item>
      </el-form>
    </el-card>
  </div></div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import axios from 'axios'

const route = useRoute(); const router = useRouter()
const auth = useAuthStore(); auth.restoreSession()
const isIntercept = route.query.mode === 'intercept'
const orderId = route.query.orderId || ''
const orderNo = route.query.orderNo || ''
const productId = route.query.productId || ''
const productName = route.query.productName || ''
const loading = ref(false)

const form = reactive({
  afterSalesType: 2,
  reason: '',
  description: '',
  refundAmount: Number(route.query.refundAmount || 0)
})

async function submit() {
  if (!form.reason) { ElMessage.warning('请填写原因'); return }
  loading.value = true
  try {
    await axios.post('/api/after-sales/apply', {
      userId: auth.user.id, orderId, orderNo, productId, productName,
      afterSalesType: form.afterSalesType, reason: form.reason,
      description: form.description, refundAmount: form.refundAmount
    })
    ElMessage.success('售后申请已提交，等待审核')
    router.push('/orders')
  } catch { ElMessage.error('提交失败') }
  finally { loading.value = false }
}
</script>

<style scoped>
.as-page { min-height: 70vh; background: #f5f6f7; padding-bottom: 40px; }
.section-content { max-width: 700px; margin: 0 auto; padding: 20px; }
h2 { font-size: 20px; padding: 10px 0; }
</style>

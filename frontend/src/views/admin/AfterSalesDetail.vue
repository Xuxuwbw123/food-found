<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">售后处理</span>
        <el-button @click="$router.back()">返回</el-button>
      </div>

      <div v-loading="loading">
        <el-divider content-position="left">售后信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="售后单号">{{ detail.afterSalesNo }}</el-descriptions-item>
          <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="商品名称">{{ detail.productName }}</el-descriptions-item>
          <el-descriptions-item label="售后类型">{{ afterSalesTypeText(detail.afterSalesType) }}</el-descriptions-item>
          <el-descriptions-item label="退款金额">¥{{ detail.refundAmount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="afterSalesStatusTag(detail.status)">{{ afterSalesStatusText(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请原因" :span="2">{{ detail.reason }}</el-descriptions-item>
          <el-descriptions-item label="详细描述" :span="2">{{ detail.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="管理员备注" :span="2">{{ detail.adminRemark || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ detail.applyTime }}</el-descriptions-item>
          <el-descriptions-item label="审核时间">{{ detail.auditTime || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="detail.status === 0" style="margin-top:20px">
          <el-divider content-position="left">审核操作</el-divider>
          <el-form inline>
            <el-form-item label="备注">
              <el-input v-model="auditRemark" placeholder="审核备注" style="width:300px" />
            </el-form-item>
            <el-form-item>
              <el-button type="success" @click="audit(1)" :loading="auditLoading">审核通过</el-button>
              <el-button type="danger" @click="audit(4)" :loading="auditLoading">关闭工单</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-divider content-position="left">客服沟通记录</el-divider>
        <div class="chat-area">
          <div v-for="msg in chatLogs" :key="msg.id" class="chat-msg" :class="msg.operatorType === 2 ? 'msg-admin' : 'msg-user'">
            <div class="msg-header">
              <el-tag size="small" :type="msg.operatorType === 2 ? 'warning' : 'primary'">{{ msg.operatorName || (msg.operatorType === 2 ? '客服' : '用户') }}</el-tag>
              <span class="msg-time">{{ msg.createTime }}</span>
            </div>
            <div class="msg-body" v-if="msg.msgType === 1">{{ msg.content }}</div>
            <div class="msg-body" v-else-if="msg.msgType === 2">
              <el-image v-if="msg.imageUrls" :src="msg.imageUrls" style="max-width:200px;border-radius:6px" fit="cover" />
            </div>
          </div>
          <el-empty v-if="!chatLogs.length" description="暂无沟通记录" :image-size="40" />
        </div>

        <div style="margin-top:20px">
          <el-input v-model="newMsg" type="textarea" :rows="3" placeholder="输入回复内容..." />
          <el-button type="primary" style="margin-top:10px" @click="sendMsg" :loading="sendLoading">发送</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAfterSalesDetail, auditAfterSales, closeAfterSales } from '../../api/admin'
import { getCsLogs, addCsLog } from '../../api/admin'

const route = useRoute()
const detail = ref({})
const chatLogs = ref([])
const loading = ref(false)
const auditLoading = ref(false)
const sendLoading = ref(false)
const auditRemark = ref('')
const newMsg = ref('')

function afterSalesTypeText(t) {
  return t === 1 ? '仅退款' : t === 2 ? '退货退款' : t === 3 ? '换货' : t === 4 ? '补发' : '未知'
}
function afterSalesStatusText(s) {
  const map = { 0: '待审核', 1: '审核通过', 2: '退货中', 3: '已完成', 4: '已关闭' }
  return map[s] || '未知'
}
function afterSalesStatusTag(s) {
  const map = { 0: 'warning', 1: 'primary', 2: '', 3: 'success', 4: 'info' }
  return map[s] || ''
}

async function loadData() {
  loading.value = true
  try {
    const id = route.params.id
    const [detailRes, logRes] = await Promise.all([getAfterSalesDetail(id), getCsLogs(id)])
    detail.value = detailRes.data
    chatLogs.value = logRes.data || []
  } finally { loading.value = false }
}

async function audit(status) {
  auditLoading.value = true
  try {
    if (status === 4) {
      await closeAfterSales(detail.value.id, auditRemark.value || '已关闭')
      ElMessage.success('已关闭')
    } else {
      await auditAfterSales(detail.value.id, status, auditRemark.value)
      ElMessage.success('审核通过')
    }
    loadData()
  } finally { auditLoading.value = false }
}

async function sendMsg() {
  if (!newMsg.value.trim()) { ElMessage.warning('请输入内容'); return }
  sendLoading.value = true
  try {
    await addCsLog({
      afterSalesId: detail.value.id,
      orderId: detail.value.orderId,
      userId: detail.value.userId,
      operatorType: 2,
      msgType: 1,
      content: newMsg.value
    })
    ElMessage.success('发送成功')
    newMsg.value = ''
    const logRes = await getCsLogs(detail.value.id)
    chatLogs.value = logRes.data || []
  } finally { sendLoading.value = false }
}

onMounted(loadData)
</script>

<style scoped>
.page-container { animation: fadeIn .3s; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-title { font-size: 16px; font-weight: 600; }
.chat-area { max-height: 400px; overflow-y: auto; background: #f5f7fa; border-radius: 8px; padding: 16px; }
.chat-msg { margin-bottom: 12px; }
.chat-msg.msg-admin { text-align: right; }
.msg-header { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; justify-content: flex-start; }
.msg-admin .msg-header { justify-content: flex-end; }
.msg-time { font-size: 12px; color: #909399; }
.msg-body { background: #fff; padding: 8px 12px; border-radius: 8px; display: inline-block; max-width: 80%; text-align: left; font-size: 14px; }
.msg-admin .msg-body { background: #ecf5ff; }
</style>

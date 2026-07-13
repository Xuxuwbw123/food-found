<template>
  <div class="chat-admin-page">
    <div class="chat-layout">
      <!-- 左侧：会话列表 -->
      <div class="conv-list">
        <div class="conv-header">客服会话</div>
        <div v-for="conv in conversations" :key="conv.userId"
             class="conv-item" :class="{ active: selectedUserId === conv.userId }"
             @click="selectUser(conv.userId)">
          <div class="conv-avatar">{{ conv.nickname?.charAt(0) || '?' }}</div>
          <div class="conv-info">
            <div class="conv-name">
              {{ conv.nickname }}
              <el-tag v-if="conv.online" type="success" size="small" style="margin-left:4px">在线</el-tag>
            </div>
            <div class="conv-last">{{ conv.lastMessage }}</div>
          </div>
          <div class="conv-meta">
            <div class="conv-time">{{ formatTime(conv.lastTime) }}</div>
            <el-badge v-if="conv.unreadCount > 0" :value="conv.unreadCount" :max="99" />
          </div>
        </div>
        <el-empty v-if="conversations.length === 0" description="暂无会话" :image-size="40" />
      </div>

      <!-- 右侧：聊天窗口 -->
      <div class="chat-window">
        <template v-if="selectedUserId">
          <div class="chat-header">
            <span>{{ selectedNickname }}</span>
            <div style="display:flex;gap:8px">
              <el-button size="small" type="danger" @click="showRefund=true">处理退款</el-button>
              <el-button size="small" type="warning" @click="openExchange">处理换货</el-button>
            </div>
          </div>
          <div class="chat-messages" ref="messagesContainer">
            <div v-for="msg in messages" :key="msg.id"
                 :class="['msg-item', (msg.isMine || msg.isAI) ? 'msg-right' : 'msg-left']">
              <div class="msg-content">
                <div v-if="msg.isAI" class="msg-ai-badge">AI</div>
                <div v-if="msg.msgType==='refund'" class="msg-system refund">{{ msg.content }}</div>
                <div v-else-if="msg.msgType==='exchange'" class="msg-system exchange">{{ msg.content }}</div>
                <div v-else-if="msg.msgType==='system'" class="msg-system">{{ msg.content }}</div>
                <div v-else class="msg-text">{{ msg.content }}</div>
                <div class="msg-time">{{ formatTime(msg.createTime) }}</div>
              </div>
            </div>
          </div>
          <div class="chat-input">
            <el-input v-model="inputMsg" placeholder="输入消息..." @keyup.enter="sendMsg">
              <template #append>
                <el-button type="primary" @click="sendMsg" :disabled="!inputMsg.trim()">发送</el-button>
              </template>
            </el-input>
          </div>
        </template>
        <div v-else style="display:flex;align-items:center;justify-content:center;height:100%;color:#999">
          选择一个会话开始回复
        </div>
      </div>
    </div>

    <!-- 退款弹窗 -->
    <el-dialog title="处理退款" v-model="showRefund" width="400px">
      <el-form label-width="80px">
        <el-form-item label="订单ID"><el-input v-model="refundForm.orderId" placeholder="输入订单ID" /></el-form-item>
        <el-form-item label="退款原因"><el-input v-model="refundForm.reason" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRefund=false">取消</el-button>
        <el-button type="danger" @click="processRefund" :loading="refundLoading">确认退款</el-button>
      </template>
    </el-dialog>

    <!-- 换货弹窗 -->
    <el-dialog title="处理换货" v-model="showExchange" width="500px">
      <el-form label-width="80px">
        <el-form-item label="订单ID"><el-input v-model="exchangeForm.orderId" placeholder="输入订单ID" /></el-form-item>
        <el-form-item label="换货原因"><el-input v-model="exchangeForm.reason" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="退货地址">
          <el-input v-model="exchangeForm.returnAddress" type="textarea" :rows="2" placeholder="点击下方按钮自动填入发货地址" />
          <el-button size="small" type="primary" style="margin-top:4px" @click="fillReturnAddress" :loading="loadingAddr">一键填入发货地址</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showExchange=false">取消</el-button>
        <el-button type="warning" @click="processExchange" :loading="exchangeLoading">确认换货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import axios from 'axios'

const auth = useAuthStore()
const conversations = ref([])
const messages = ref([])
const selectedUserId = ref(null)
const selectedNickname = ref('')
const inputMsg = ref('')
const messagesContainer = ref(null)

const showRefund = ref(false)
const refundLoading = ref(false)
const refundForm = ref({ orderId: '', reason: '' })

const showExchange = ref(false)
const exchangeLoading = ref(false)
const loadingAddr = ref(false)
const exchangeForm = ref({ orderId: '', reason: '', returnAddress: '' })

let ws = null

function formatTime(t) { return t ? t.substring(11, 16) : '' }
function scrollToBottom() {
  nextTick(() => { if (messagesContainer.value) messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight })
}

async function loadConversations() {
  try { const r = await axios.get('/admin/chat/conversations'); conversations.value = r.data.data || [] } catch {}
}

async function selectUser(userId) {
  selectedUserId.value = userId
  const conv = conversations.value.find(c => c.userId === userId)
  selectedNickname.value = conv?.nickname || ''
  try {
    const r = await axios.get(`/admin/chat/messages/${userId}`)
    messages.value = r.data.data || []
    scrollToBottom()
    if (conv) conv.unreadCount = 0
  } catch {}
}

async function sendMsg() {
  if (!inputMsg.value.trim() || !selectedUserId.value) return
  const content = inputMsg.value.trim()
  inputMsg.value = ''
  try {
    const r = await axios.post('/admin/chat/send', { toUserId: selectedUserId.value, content })
    messages.value.push(r.data.data)
    scrollToBottom()
    loadConversations()
  } catch {}
}

async function processRefund() {
  if (!refundForm.value.orderId) { ElMessage.warning('请输入订单ID'); return }
  if (!selectedUserId.value) { ElMessage.warning('请先选择用户'); return }
  refundLoading.value = true
  try {
    const r = await axios.post('/admin/chat/refund', {
      orderId: refundForm.value.orderId,
      userId: selectedUserId.value,
      reason: refundForm.value.reason || '客服处理退款'
    })
    if (r.data.code === 200) {
      ElMessage.success('退款成功 ¥' + r.data.data.refundAmount)
      showRefund.value = false
      refundForm.value = { orderId: '', reason: '' }
      selectUser(selectedUserId.value)
    } else {
      ElMessage.error(r.data.message || '退款失败')
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || e.response?.data?.msg || '退款失败，请检查订单ID')
  }
  finally { refundLoading.value = false }
}

async function openExchange() {
  exchangeForm.value = { orderId: '', reason: '', returnAddress: '' }
  showExchange.value = true
}

async function fillReturnAddress() {
  if (!exchangeForm.value.orderId) { ElMessage.warning('请先输入订单号或订单ID'); return }
  loadingAddr.value = true
  try {
    // 获取订单详情（支持订单号FD...或订单ID）
    const orderIdStr = exchangeForm.value.orderId.trim()
    let orderData = null

    // 先尝试用订单号查询（通过订单列表搜索）
    const listRes = await axios.get('/api/admin/order/list', { params: { pageNum: 1, pageSize: 50 } })
    const orders = listRes.data.data?.records || []
    const found = orders.find(o => o.orderNo === orderIdStr || String(o.id) === orderIdStr)

    if (!found) { ElMessage.error('订单不存在，请检查订单号'); return }

    // 获取订单详情
    const orderRes = await axios.get(`/api/order/${found.id}`)
    orderData = orderRes.data.data?.order || orderRes.data.data
    if (!orderData) { ElMessage.error('订单不存在'); return }

    // 获取农户地址
    let farmerAddr = ''
    const farmerId = orderData.farmerId || found.farmerId
    if (farmerId) {
      try {
        const farmerRes = await axios.get(`/api/farmer/detail/${farmerId}`)
        const farmer = farmerRes.data.data
        if (farmer) {
          farmerAddr = `${farmer.farmerName || ''}\n${farmer.province || ''}${farmer.city || ''}${farmer.district || ''} ${farmer.address || ''}\n联系人：${farmer.contactPerson || ''} ${farmer.contactPhone || ''}`
        }
      } catch {}
    }

    exchangeForm.value.returnAddress = farmerAddr
    // 保存数字ID用于后续操作
    exchangeForm.value._numericId = found.id
    ElMessage.success('已填入发货地址')
  } catch { ElMessage.error('获取订单信息失败') }
  finally { loadingAddr.value = false }
}

async function processExchange() {
  if (!exchangeForm.value.orderId) { ElMessage.warning('请输入订单ID'); return }
  if (!exchangeForm.value.returnAddress) { ElMessage.warning('请填写退货地址'); return }
  exchangeLoading.value = true
  try {
    // 发送换货通知消息给用户
    const msg = `【换货申请已受理】\n订单号：${exchangeForm.value.orderId}\n原因：${exchangeForm.value.reason || '商品问题'}\n\n请将商品寄回以下地址：\n${exchangeForm.value.returnAddress}\n\n寄出后请告知快递单号，我们会尽快为您处理。`
    await axios.post('/admin/chat/send', { toUserId: selectedUserId.value, content: msg, msgType: 'exchange' })
    ElMessage.success('换货通知已发送')
    showExchange.value = false
    selectUser(selectedUserId.value)
  } catch { ElMessage.error('发送失败') }
  finally { exchangeLoading.value = false }
}

function connectWs() {
  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
  ws = new WebSocket(`${protocol}//${location.host}/ws/chat?userId=${auth.user.id}`)
  ws.onopen = () => {}
  ws.onclose = () => setTimeout(connectWs, 3000)
  ws.onerror = () => {}
  ws.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      if (data.type === 'new_message') {
        if (data.message.fromUserId === selectedUserId.value) {
          messages.value.push(data.message)
          scrollToBottom()
        }
        loadConversations()
      } else if (data.type === 'user_transfer') {
        ElMessage.info(data.message)
        loadConversations()
      }
    } catch {}
  }
}

onMounted(() => { loadConversations(); connectWs() })
onUnmounted(() => { if (ws) ws.close() })
</script>

<style scoped>
.chat-admin-page { height: calc(100vh - 120px); }
.chat-layout { display: flex; height: 100%; border: 1px solid #e4e7ed; border-radius: 8px; overflow: hidden; }
.conv-list { width: 280px; border-right: 1px solid #e4e7ed; overflow-y: auto; background: #fafafa; }
.conv-header { padding: 14px 16px; font-weight: 600; font-size: 15px; border-bottom: 1px solid #e4e7ed; background: #fff; }
.conv-item { display: flex; align-items: center; gap: 10px; padding: 12px 16px; cursor: pointer; border-bottom: 1px solid #f0f0f0; }
.conv-item:hover { background: #f0f8ff; }
.conv-item.active { background: #e8f5e9; }
.conv-avatar { width: 36px; height: 36px; border-radius: 50%; background: #1a8c3a; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 600; flex-shrink: 0; }
.conv-info { flex: 1; min-width: 0; }
.conv-name { font-size: 14px; font-weight: 500; display: flex; align-items: center; }
.conv-last { font-size: 12px; color: #999; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-top: 2px; }
.conv-meta { text-align: right; flex-shrink: 0; }
.conv-time { font-size: 11px; color: #999; }
.chat-window { flex: 1; display: flex; flex-direction: column; }
.chat-header { padding: 14px 20px; border-bottom: 1px solid #eee; display: flex; align-items: center; justify-content: space-between; font-weight: 600; font-size: 15px; }
.chat-messages { flex: 1; overflow-y: auto; padding: 16px; }
.msg-item { display: flex; margin-bottom: 12px; }
.msg-right { justify-content: flex-end; }
.msg-left { justify-content: flex-start; }
.msg-content { max-width: 60%; position: relative; }
.msg-ai-badge { position: absolute; top: -8px; left: -8px; background: #67c23a; color: #fff; font-size: 10px; padding: 1px 6px; border-radius: 8px; }
.msg-text { padding: 10px 14px; border-radius: 12px; font-size: 14px; line-height: 1.5; word-break: break-word; }
.msg-right .msg-text { background: #1a8c3a; color: #fff; border-bottom-right-radius: 4px; }
.msg-left .msg-text { background: #f0f0f0; color: #333; border-bottom-left-radius: 4px; }
.msg-system { padding: 8px 14px; border-radius: 8px; font-size: 13px; }
.msg-system.refund { background: #fff3e0; color: #e65100; border: 1px solid #ffcc80; }
.msg-system.exchange { background: #e3f2fd; color: #1565c0; border: 1px solid #90caf9; }
.msg-time { font-size: 11px; color: #999; margin-top: 4px; }
.msg-right .msg-time { text-align: right; }
.chat-input { padding: 12px 16px; border-top: 1px solid #eee; }
</style>

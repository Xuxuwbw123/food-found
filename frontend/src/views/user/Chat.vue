<template>
  <div class="chat-page">
    <div class="chat-container">
      <div class="chat-header">
        <span>在线客服</span>
        <div style="display:flex;gap:8px;align-items:center">
          <el-tag :type="chatMode==='ai'?'success':'primary'" size="small" style="cursor:pointer" @click="chatMode='ai'">AI客服</el-tag>
          <el-tag :type="chatMode==='human'?'warning':'info'" size="small" style="cursor:pointer" @click="switchToHuman">人工客服</el-tag>
        </div>
      </div>

      <!-- AI轮数提示 -->
      <div v-if="chatMode==='ai'" class="ai-info">
        <span>AI智能客服 · 每次对话最多10轮 · 超出自动转人工</span>
      </div>

      <div class="chat-messages" ref="messagesContainer">
        <div v-for="msg in messages" :key="msg.id" :class="['msg-item', msg.isMine ? 'msg-mine' : 'msg-other']">
          <div class="msg-content">
            <div v-if="msg.isAI" class="msg-ai-badge">AI</div>
            <div class="msg-text" v-html="formatContent(msg.content)"></div>
            <div class="msg-time">{{ formatTime(msg.createTime) }}</div>
          </div>
        </div>
        <div v-if="messages.length === 0" style="text-align:center;color:#999;padding:40px">
          <p>您好！我是AI智能客服，请描述您的问题</p>
          <p style="font-size:12px;margin-top:8px">支持：订单查询 · 退款申请 · 商品咨询 · 账户问题</p>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="quick-actions">
        <el-button size="small" @click="sendQuick('我的订单状态是什么')">查订单</el-button>
        <el-button size="small" @click="sendQuick('如何申请退款')">申请退款</el-button>
        <el-button size="small" @click="sendQuick('商品如何溯源查真伪')">溯源查询</el-button>
        <el-button size="small" @click="showOrders=!showOrders">发送订单</el-button>
      </div>

      <!-- 订单选择 -->
      <div v-if="showOrders" class="order-select">
        <div class="order-header">选择订单 <el-button link @click="showOrders=false">关闭</el-button></div>
        <div v-for="o in myOrders" :key="o.id" class="order-item" @click="sendOrderMsg(o)">
          <span>{{ o.orderNo }}</span>
          <span>{{ o.statusText }}</span>
          <span style="color:#f56c6c">¥{{ o.payAmount }}</span>
        </div>
      </div>

      <div class="chat-input">
        <el-input v-model="inputMsg" placeholder="输入消息..." @keyup.enter="sendMsg">
          <template #append>
            <el-button type="primary" @click="sendMsg" :disabled="!inputMsg.trim()">发送</el-button>
          </template>
        </el-input>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import axios from 'axios'

const auth = useAuthStore()
const messages = ref([])
const inputMsg = ref('')
const chatMode = ref('ai')
const showOrders = ref(false)
const myOrders = ref([])
const messagesContainer = ref(null)
let ws = null

function formatTime(t) { return t ? t.substring(11, 16) : '' }
function formatContent(c) { return c ? c.replace(/\n/g, '<br>') : '' }
function scrollToBottom() {
  nextTick(() => { if (messagesContainer.value) messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight })
}

async function loadMessages() {
  try { const r = await axios.get('/api/chat/messages'); messages.value = r.data.data || []; scrollToBottom() } catch {}
}

async function loadOrders() {
  try { const r = await axios.get('/api/chat/orders'); myOrders.value = r.data.data || [] } catch {}
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
      if (data.type === 'ai_reply' || data.type === 'new_message') {
        messages.value.push(data.message)
        scrollToBottom()
        if (data.transferToHuman) chatMode.value = 'human'
      }
    } catch {}
  }
}

async function sendMsg() {
  if (!inputMsg.value.trim()) return
  const content = inputMsg.value.trim()
  inputMsg.value = ''

  try {
    const r = await axios.post('/api/chat/send', { content, useAI: chatMode.value === 'ai' })
    if (r.data.data.message) messages.value.push(r.data.data.message)
    if (r.data.data.aiReply) messages.value.push(r.data.data.aiReply)
    if (r.data.data.transferToHuman) chatMode.value = 'human'
    scrollToBottom()
  } catch { ElMessage.error('发送失败') }
}

function sendQuick(text) {
  inputMsg.value = text
  sendMsg()
}

async function sendOrderMsg(order) {
  showOrders.value = false
  const content = `我有一个订单需要帮助：\n订单号：${order.orderNo}\n状态：${order.statusText}\n金额：¥${order.payAmount}`
  inputMsg.value = content
  sendMsg()
}

async function switchToHuman() {
  chatMode.value = 'human'
  try { await axios.post('/api/chat/transfer') } catch {}
  ElMessage.success('已转接人工客服')
}

onMounted(() => {
  if (auth.isLoggedIn) { loadMessages(); loadOrders(); connectWs() }
})
onUnmounted(() => { if (ws) ws.close() })
</script>

<style scoped>
.chat-page { min-height: 70vh; background: #f5f6f7; display: flex; justify-content: center; padding: 20px; }
.chat-container { width: 100%; max-width: 600px; background: #fff; border-radius: 12px; display: flex; flex-direction: column; height: 80vh; box-shadow: 0 4px 20px rgba(0,0,0,.08); }
.chat-header { padding: 14px 20px; border-bottom: 1px solid #eee; display: flex; align-items: center; justify-content: space-between; font-weight: 600; font-size: 16px; }
.ai-info { background: #f0f9eb; padding: 6px 16px; font-size: 12px; color: #67c23a; text-align: center; }
.chat-messages { flex: 1; overflow-y: auto; padding: 16px; }
.msg-item { display: flex; margin-bottom: 12px; }
.msg-mine { justify-content: flex-end; }
.msg-other { justify-content: flex-start; }
.msg-content { max-width: 75%; position: relative; }
.msg-ai-badge { position: absolute; top: -8px; left: -8px; background: #67c23a; color: #fff; font-size: 10px; padding: 1px 6px; border-radius: 8px; }
.msg-text { padding: 10px 14px; border-radius: 12px; font-size: 14px; line-height: 1.5; word-break: break-word; }
.msg-mine .msg-text { background: #1a8c3a; color: #fff; border-bottom-right-radius: 4px; }
.msg-other .msg-text { background: #f0f0f0; color: #333; border-bottom-left-radius: 4px; }
.msg-time { font-size: 11px; color: #999; margin-top: 4px; }
.msg-mine .msg-time { text-align: right; }
.quick-actions { padding: 8px 16px; display: flex; gap: 8px; flex-wrap: wrap; border-top: 1px solid #f0f0f0; }
.order-select { max-height: 200px; overflow-y: auto; border-top: 1px solid #eee; }
.order-header { padding: 8px 16px; display: flex; justify-content: space-between; align-items: center; background: #fafafa; font-size: 13px; font-weight: 600; }
.order-item { display: flex; justify-content: space-between; padding: 8px 16px; font-size: 13px; cursor: pointer; border-bottom: 1px solid #f5f5f5; }
.order-item:hover { background: #f0f9eb; }
.chat-input { padding: 12px 16px; border-top: 1px solid #eee; }
</style>

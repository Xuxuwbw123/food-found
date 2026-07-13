<template>
  <div class="notice-page">
    <div class="section-content">
      <h2>消息中心</h2>
      <div class="notice-toolbar">
        <el-radio-group v-model="filterType" @change="loadData">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="order">订单通知</el-radio-button>
          <el-radio-button value="afterSales">售后通知</el-radio-button>
          <el-radio-button value="system">系统通知</el-radio-button>
        </el-radio-group>
        <el-button @click="readAll" :disabled="auth.unreadCount===0">全部已读</el-button>
      </div>
      <el-empty v-if="!loading && list.length===0" description="暂无消息" />
      <div v-else v-loading="loading" class="notice-list">
        <div v-for="n in list" :key="n.id" :class="['notice-item', { unread: n.isRead===0 }]" @click="clickNotice(n)">
          <div class="notice-left">
            <el-tag :type="typeTag(n.noticeType)" size="small">{{ typeText(n.noticeType) }}</el-tag>
            <span class="notice-title">{{ n.title }}</span>
          </div>
          <div class="notice-right">
            <span class="notice-time">{{ n.createTime }}</span>
            <el-badge v-if="n.isRead===0" is-dot />
          </div>
        </div>
      </div>
      <el-pagination v-if="total>list.length" v-model:current-page="pageNum" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" style="margin-top:16px;justify-content:center" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useAuthStore } from '../../stores/auth'

const auth = useAuthStore(); auth.restoreSession()
const router = useRouter()
const list = ref([]); const loading = ref(false); const total = ref(0)
const pageNum = ref(1); const filterType = ref('')

function typeTag(t) { return { order: 'warning', after_sales: 'danger', system: '' }[t] || '' }
function typeText(t) { return { order: '订单', after_sales: '售后', system: '系统' }[t] || '系统' }

async function loadData() {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: 20 }
    if (filterType.value) params.type = filterType.value
    const r = await axios.get('/api/notice/list', { params })
    list.value = r.data.data?.records || []
    total.value = r.data.data?.total || 0
    // 同步全局未读数(顶部铃铛和这里的"全部已读"按钮共用)
    await auth.loadUnreadCount()
  } finally { loading.value = false }
}

async function readAll() {
  await axios.put('/api/notice/read-all')
  // 不依赖后端返回,本地先把全局未读数置 0;列表走 loadData 重新拉(用 isRead=1 渲染)
  auth.resetUnreadCount()
  loadData()
}

async function clickNotice(n) {
  if (n.isRead === 0) {
    await axios.put(`/api/notice/read/${n.id}`)
    n.isRead = 1
    auth.decrementUnreadCount()
  }
  if (n.relationId) {
    if (n.noticeType === 'order') router.push(`/orders`)
    else if (n.noticeType === 'after_sales') router.push('/after-sales')
  }
}

onMounted(loadData)
</script>

<style scoped>
.notice-page { min-height: 70vh; background: #f5f6f7; padding-bottom: 40px; }
.section-content { max-width: 800px; margin: 0 auto; padding: 20px; }
h2 { font-size: 20px; padding: 10px 0; }
.notice-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.notice-list { background: #fff; border-radius: 8px; overflow: hidden; }
.notice-item { display: flex; justify-content: space-between; align-items: center; padding: 14px 20px; border-bottom: 1px solid #f0f0f0; cursor: pointer; transition: background .2s; }
.notice-item:hover { background: #fafafa; }
.notice-item.unread { background: #f6f8ff; }
.notice-left { display: flex; align-items: center; gap: 12px; }
.notice-title { font-size: 14px; }
.notice-right { display: flex; align-items: center; gap: 12px; }
.notice-time { font-size: 13px; color: #999; }
</style>
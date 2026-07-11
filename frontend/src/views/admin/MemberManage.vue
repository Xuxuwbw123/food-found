<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header"><span class="page-title">会员管理</span></div>
      <el-form :model="query" inline class="search-form">
        <el-form-item label="关键词"><el-input v-model="query.keyword" placeholder="手机号/昵称" clearable @clear="loadData" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="query.status" placeholder="全部" clearable @change="loadData"><el-option label="启用" :value="1" /><el-option label="禁用" :value="0" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">搜索</el-button></el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="160" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="会员等级" width="120">
          <template #default="{row}">
            <el-tag :type="levelTag(row.memberLevel)" size="small">{{ levelText(row.memberLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="累计消费" width="100"><template #default="{row}">¥{{row.totalSpent||0}}</template></el-table-column>
        <el-table-column prop="orderCount" label="订单数" width="80" />
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'" size="small">{{row.status===1?'启用':'禁用'}}</el-tag></template></el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="openAdjust(row)">调整等级</el-button>
            <el-button link type="warning" size="small" @click="toggle(row)">{{row.status===1?'禁用':'启用'}}</el-button>
            <el-button link size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination"><el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" :total="total" @size-change="loadData" @current-change="loadData" /></div>
    </el-card>

    <!-- 调整等级对话框 -->
    <el-dialog title="调整会员等级" v-model="adjustVisible" width="400px">
      <el-form label-width="80px">
        <el-form-item label="用户">{{ adjustUser.nickname }} ({{ adjustUser.phone }})</el-form-item>
        <el-form-item label="当前等级"><el-tag :type="levelTag(adjustUser.memberLevel)">{{ levelText(adjustUser.memberLevel) }}</el-tag></el-form-item>
        <el-form-item label="新等级">
          <el-select v-model="newLevel" style="width:100%">
            <el-option label="普通用户" :value="0" />
            <el-option label="普通会员" :value="1" />
            <el-option label="银卡会员" :value="2" />
            <el-option label="金卡会员" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustVisible=false">取消</el-button>
        <el-button type="primary" @click="doAdjust" :loading="adjustLoading">确认调整</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="会员详情" width="500px">
      <el-descriptions :column="1" border v-if="detail.id">
        <el-descriptions-item label="ID">{{detail.id}}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{detail.username}}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{detail.nickname}}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{detail.phone}}</el-descriptions-item>
        <el-descriptions-item label="会员等级"><el-tag :type="levelTag(detail.memberLevel)">{{ levelText(detail.memberLevel) }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="累计消费">¥{{detail.totalSpent||0}}</el-descriptions-item>
        <el-descriptions-item label="订单数">{{detail.orderCount||0}}</el-descriptions-item>
        <el-descriptions-item label="状态">{{detail.status===1?'启用':'禁用'}}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{detail.createTime}}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const query = reactive({ pageNum:1, pageSize:10, keyword:'', status:null })
const tableData = ref([]); const total = ref(0); const loading = ref(false)
const detailVisible = ref(false); const detail = ref({})
const adjustVisible = ref(false); const adjustLoading = ref(false)
const adjustUser = ref({}); const newLevel = ref(0)

function levelText(l) { return {0:'普通用户',1:'普通会员',2:'银卡会员',3:'金卡会员'}[l]||'普通用户' }
function levelTag(l) { return ['', 'success', 'warning', 'danger'][l] || 'info' }

async function loadData() {
  loading.value = true
  try { const params={...query}; Object.keys(params).forEach(k=>(params[k]===''||params[k]===null)&&delete params[k]); const r=await axios.get('/api/admin/members',{params}); tableData.value=r.data.data?.records||[]; total.value=r.data.data?.total||0 } finally { loading.value = false }
}

async function toggle(row) {
  const ns = row.status===1?0:1
  try { await axios.put(`/api/admin/members/${row.id}/status`,{status:ns}); ElMessage.success(ns===1?'已启用':'已禁用'); loadData() } catch {}
}

async function viewDetail(row) {
  try { const r=await axios.get(`/api/admin/members/${row.id}`); detail.value=r.data.data||{}; detailVisible.value=true } catch {}
}

function openAdjust(row) {
  adjustUser.value = row
  newLevel.value = row.memberLevel || 0
  adjustVisible.value = true
}

async function doAdjust() {
  adjustLoading.value = true
  try {
    await axios.put(`/api/admin/member-level/adjust/${adjustUser.value.id}`, { memberLevel: newLevel.value })
    ElMessage.success('等级调整成功')
    adjustVisible.value = false; loadData()
  } catch { ElMessage.error('调整失败') } finally { adjustLoading.value = false }
}

onMounted(loadData)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}
.search-form{margin:16px 0}.pagination{display:flex;justify-content:flex-end;margin-top:16px}
</style>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header"><span class="page-title">操作日志</span></div>
      <el-form :model="query" inline class="search-form">
        <el-form-item label="模块"><el-input v-model="query.module" placeholder="模块名" clearable /></el-form-item>
        <el-form-item label="操作人"><el-input v-model="query.username" placeholder="用户名" clearable /></el-form-item>
        <el-form-item label="时间范围"><el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">搜索</el-button><el-button @click="resetQuery">重置</el-button></el-form-item>
      </el-form>
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;padding:8px 12px;background:#f5f7fa;border-radius:6px">
        <span style="font-size:13px;color:#666">共 {{ total }} 条记录，第 {{ query.pageNum }} 页</span>
        <div>
          <el-button size="small" :disabled="query.pageNum<=1" @click="query.pageNum--;loadData()">上一页</el-button>
          <el-button size="small" :disabled="query.pageNum*query.pageSize>=total" @click="query.pageNum++;loadData()">下一页</el-button>
          <el-select v-model="query.pageSize" size="small" style="width:90px;margin-left:8px" @change="query.pageNum=1;loadData()">
            <el-option :value="10" label="10条/页" />
            <el-option :value="20" label="20条/页" />
            <el-option :value="50" label="50条/页" />
          </el-select>
        </div>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="160" />
        <el-table-column prop="username" label="操作人" width="100" />
        <el-table-column prop="module" label="模块" width="100" />
        <el-table-column prop="action" label="操作" width="100" />
        <el-table-column prop="target" label="目标" min-width="150" show-overflow-tooltip />
        <el-table-column prop="reqMethod" label="方式" width="70" />
        <el-table-column prop="reqPath" label="路径" min-width="200" show-overflow-tooltip />
        <el-table-column prop="reqIp" label="IP" width="140" />
        <el-table-column prop="result" label="结果" width="80"><template #default="{row}"><el-tag :type="row.result==='SUCCESS'?'success':'danger'" size="small">{{row.result}}</el-tag></template></el-table-column>
        <el-table-column prop="duration" label="耗时ms" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="60" fixed="right"><template #default="{row}"><el-button link type="primary" size="small" @click="showDetail(row)">详情</el-button></template></el-table-column>

      </el-table>
    </el-card>
    <el-dialog v-model="detailVisible" title="日志详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="模块">{{ detail.module }}</el-descriptions-item>
        <el-descriptions-item label="操作">{{ detail.action }}</el-descriptions-item>
        <el-descriptions-item label="目标">{{ detail.target }}</el-descriptions-item>
        <el-descriptions-item label="结果">{{ detail.result }}</el-descriptions-item>
        <el-descriptions-item label="请求方式">{{ detail.reqMethod }}</el-descriptions-item>
        <el-descriptions-item label="请求路径" :span="1">{{ detail.reqPath }}</el-descriptions-item>
        <el-descriptions-item label="IP">{{ detail.reqIp }}</el-descriptions-item>
        <el-descriptions-item label="耗时">{{ detail.duration }}ms</el-descriptions-item>
        <el-descriptions-item label="时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2"><pre style="max-height:300px;overflow:auto;font-size:12px;background:#f5f5f5;padding:8px;border-radius:4px">{{ detail.reqParams || '无' }}</pre></el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'

const query = reactive({ pageNum: 1, pageSize: 20, module: '', username: '' })
const dateRange = ref([])
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const detail = ref({})

async function loadData() {
  loading.value = true
  try {
    const params = { pageNum: query.pageNum, pageSize: query.pageSize }
    if (query.module) params.module = query.module
    if (query.username) params.username = query.username
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res = await axios.get('/api/admin/operationLog/page', { params })
    tableData.value = res.data?.data?.records || []
    total.value = res.data?.data?.total || 0
  } finally { loading.value = false }
}

function resetQuery() {
  query.module = ''
  query.username = ''
  query.pageNum = 1
  dateRange.value = []
  loadData()
}

function showDetail(row) { detail.value = row; detailVisible.value = true }

onMounted(loadData)
</script>

<style scoped>
.page-container { animation: fadeIn .3s; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { font-size: 16px; font-weight: 600; }
.search-form { margin: 16px 0; }
.pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
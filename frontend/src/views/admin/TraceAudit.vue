<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">溯源批次审核</span>
        <el-radio-group v-model="filterStatus" @change="load">
          <el-radio-button :value="0">待审核</el-radio-button>
          <el-radio-button :value="null">全部</el-radio-button>
        </el-radio-group>
      </div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="160" />
        <el-table-column prop="traceCode" label="溯源码" width="160" />
        <el-table-column prop="batchNo" label="批次号" width="140" />
        <el-table-column prop="productName" label="产品名" width="150" />
        <el-table-column prop="farmName" label="农场" width="150" />
        <el-table-column prop="responsiblePerson" label="负责人" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{row}">
            <el-tag :type="row.auditStatus===1?'success':row.auditStatus===2?'danger':'warning'" size="small">
              {{ row.auditStatus===1?'已通过':row.auditStatus===2?'已拒绝':'待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.auditStatus===0" type="success" size="small" @click="approve(row.id)">通过</el-button>
            <el-button v-if="row.auditStatus===0" type="danger" size="small" @click="reject(row.id)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading&&list.length===0" description="暂无数据" />
    </el-card>

    <el-dialog title="溯源批次详情" v-model="detailVisible" width="700px">
      <el-descriptions :column="2" border v-if="detail.id">
        <el-descriptions-item label="溯源码">{{ detail.traceCode }}</el-descriptions-item>
        <el-descriptions-item label="批次号">{{ detail.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="产品名">{{ detail.productName }}</el-descriptions-item>
        <el-descriptions-item label="产地">{{ detail.originPlace }}</el-descriptions-item>
        <el-descriptions-item label="农场">{{ detail.farmName }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ detail.responsiblePerson }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="detail.images?.length" style="margin-top:16px">
        <h4>溯源图片</h4>
        <div style="display:flex;flex-wrap:wrap;gap:8px">
          <el-image v-for="img in detail.images" :key="img.id" :src="img.imageUrl"
            style="width:120px;height:100px;border-radius:6px" fit="cover"
            :preview-src-list="detail.images.map(i=>i.imageUrl)" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const list = ref([]); const loading = ref(false); const filterStatus = ref(0)
const detailVisible = ref(false); const detail = ref({})

async function load() {
  loading.value = true
  try {
    const params = {}
    if (filterStatus.value !== null) params.status = filterStatus.value
    const r = await axios.get('/admin/trace/audit/list', { params })
    list.value = Array.isArray(r.data.data) ? r.data.data : (r.data.data?.records || [])
  } finally { loading.value = false }
}

async function viewDetail(row) {
  try {
    const r = await axios.get(`/admin/trace/audit/detail/${row.id}`)
    detail.value = r.data.data || {}
    detailVisible.value = true
  } catch {}
}

async function approve(id) {
  await axios.put(`/admin/trace/audit/approve/${id}`)
  ElMessage.success('审核通过'); load()
}

async function reject(id) {
  await axios.put(`/admin/trace/audit/reject/${id}`)
  ElMessage.success('已拒绝'); load()
}

onMounted(load)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}
@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}
.page-title{font-size:16px;font-weight:600}
</style>

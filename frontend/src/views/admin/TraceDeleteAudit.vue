<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header"><span class="page-title">溯源删除审核</span></div>
      <el-table :data="list" border stripe v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="ID" width="160" />
        <el-table-column prop="productName" label="产品名" width="150" />
        <el-table-column prop="batchNo" label="批次号" width="140" />
        <el-table-column prop="farmName" label="农场" width="180" />
        <el-table-column prop="responsiblePerson" label="申请人" width="100" />
        <el-table-column prop="createTime" label="申请时间" width="170" />
        <el-table-column label="操作" width="200">
          <template #default="{row}">
            <el-button type="danger" size="small" @click="approve(row.id)">确认删除</el-button>
            <el-button size="small" @click="reject(row.id)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="list.length===0" description="暂无待审核的删除申请" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
const list = ref([]); const loading = ref(false)

async function load() { loading.value=true; try{const r=await axios.get('/api/trace/pending-deletes');list.value=r.data.data||[]}finally{loading.value=false} }
async function approve(id) { try{await axios.put(`/api/trace/approve-delete/${id}`);ElMessage.success('已删除');load()}catch{} }
async function reject(id) { try{await axios.put(`/api/trace/reject-delete/${id}`);ElMessage.success('已拒绝');load()}catch{} }
onMounted(load)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}
@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}
.page-title{font-size:16px;font-weight:600}
</style>

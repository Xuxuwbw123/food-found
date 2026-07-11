<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">商品发布审核</span>
        <el-radio-group v-model="filterStatus" @change="load">
          <el-radio-button :value="0">待审核</el-radio-button>
          <el-radio-button :value="null">全部</el-radio-button>
        </el-radio-group>
      </div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="160" />
        <el-table-column label="主图" width="80">
          <template #default="{row}">
            <el-image v-if="row.mainImage" :src="row.mainImage" style="width:50px;height:50px;border-radius:6px" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品名称" min-width="160" />
        <el-table-column prop="productNo" label="编号" width="120" />
        <el-table-column label="价格" width="100">
          <template #default="{row}"><span style="color:#f56c6c;font-weight:600">¥{{ row.price }}</span></template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="unit" label="单位" width="60" />
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
            <el-button v-if="row.auditStatus===0" type="success" size="small" @click="approve(row.id)">通过</el-button>
            <el-button v-if="row.auditStatus===0" type="danger" size="small" @click="reject(row.id)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading&&list.length===0" description="暂无待审核商品" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const list = ref([]); const loading = ref(false); const filterStatus = ref(0)

async function load() {
  loading.value = true
  try {
    const params = {}
    if (filterStatus.value !== null) params.status = filterStatus.value
    const r = await axios.get('/admin/product/audit/list', { params })
    list.value = r.data.data?.records || r.data.data || []
  } finally { loading.value = false }
}

async function approve(id) {
  await axios.put(`/admin/product/audit/approve/${id}`)
  ElMessage.success('审核通过'); load()
}

async function reject(id) {
  await axios.put(`/admin/product/audit/reject/${id}`)
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

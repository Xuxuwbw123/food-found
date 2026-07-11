<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header"><span class="page-title">用户地址管理</span></div>
      <el-table :data="list" border stripe v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="电话" width="130" />
        <el-table-column label="地址" min-width="250">
          <template #default="{row}">{{ row.province }}{{ row.city }}{{ row.district }} {{ row.detailAddress }}</template>
        </el-table-column>
        <el-table-column prop="isDefault" label="默认" width="70">
          <template #default="{row}"><el-tag v-if="row.isDefault===1" type="success" size="small">默认</el-tag></template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
const list = ref([]); const loading = ref(false)
onMounted(async () => { loading.value = true; try { const r = await axios.get('/api/admin/addresses'); list.value = r.data.data?.records || r.data.data || [] } finally { loading.value = false } })
</script>

<style scoped>
.page-container { animation: fadeIn .3s; }
@keyframes fadeIn { from{opacity:0;transform:translateY(10px)} to{opacity:1;transform:translateY(0)} }
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px }
.page-title { font-size:16px; font-weight:600 }
</style>

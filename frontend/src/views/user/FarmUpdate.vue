<template>
  <div class="page-container">
    <el-card shadow="never">
      <h2 style="margin-bottom:16px">农场动态</h2>
      <el-select v-model="farmerId" placeholder="选择农户" @change="loadUpdates" style="width:240px;margin-bottom:16px">
        <el-option v-for="f in farmers" :key="f.id" :label="f.farmName||f.name" :value="f.id" />
      </el-select>

      <el-timeline v-if="updates.length">
        <el-timeline-item v-for="u in updates" :key="u.id" :timestamp="u.createTime" placement="top">
          <el-card>
            <h3>{{ u.title }} <el-tag v-if="u.updateType" size="small">{{ u.updateType }}</el-tag></h3>
            <p style="color:#666;margin-top:8px">{{ u.content }}</p>
            <el-image v-if="u.imageUrl" :src="u.imageUrl" style="width:200px;height:120px;margin-top:8px;border-radius:4px" fit="cover" />
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-if="farmerId && !updates.length" description="暂无动态" />
      <el-empty v-if="!farmerId" description="请选择农户" />
    </el-card>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const farmers = ref([]); const farmerId = ref(null); const updates = ref([])

async function loadFarmers() {
  try { const r = await axios.get('/api/farmer/list'); farmers.value = r.data.data || [] } catch {}
}

async function loadUpdates() {
  if (!farmerId.value) return
  try { const r = await axios.get(`/api/trace/farm-update/${farmerId.value}`); updates.value = r.data.data || [] } catch {}
}

onMounted(loadFarmers)
</script>
<style scoped>.page-container{max-width:900px;margin:0 auto;padding:20px}</style>

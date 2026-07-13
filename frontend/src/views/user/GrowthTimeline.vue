<template>
  <div class="page-container">
    <el-card shadow="never">
      <h2 style="margin-bottom:16px">生长周期</h2>
      <el-select v-model="traceId" placeholder="选择溯源批次" @change="loadTimeline" style="width:240px;margin-bottom:16px">
        <el-option v-for="t in traces" :key="t.id" :label="t.productName+' ('+t.batchNo+')'" :value="t.id" />
      </el-select>

      <el-timeline v-if="records.length">
        <el-timeline-item v-for="r in records" :key="r.id" :timestamp="r.createTime" placement="top">
          <el-card>
            <h3>{{ r.title }} <el-tag size="small" type="success">{{ stageText(r.stage) }}</el-tag></h3>
            <p style="color:#666;margin-top:8px">{{ r.content }}</p>
            <el-image v-if="r.imageUrl" :src="r.imageUrl" style="width:200px;height:120px;margin-top:8px;border-radius:4px" fit="cover" />
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-if="traceId && !records.length" description="暂无生长记录" />
      <el-empty v-if="!traceId" description="请选择溯源批次" />
    </el-card>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const traces = ref([]); const traceId = ref(null); const records = ref([])
function stageText(s) { return { sow:'播种', bloom:'开花', fruit:'结果', ripe:'成熟', seedling:'育苗', grow:'生长' }[s] || s || '其他' }

async function loadTraces() {
  try { const r = await axios.get('/api/trace/list?pageNum=1&pageSize=100'); traces.value = r.data.data?.records || [] } catch {}
}

async function loadTimeline() {
  if (!traceId.value) return
  try { const r = await axios.get(`/api/trace/growth/${traceId.value}`); records.value = r.data.data || [] } catch {}
}

onMounted(loadTraces)
</script>
<style scoped>.page-container{max-width:900px;margin:0 auto;padding:20px}</style>

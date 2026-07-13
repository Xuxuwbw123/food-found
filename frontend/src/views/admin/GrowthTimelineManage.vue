<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">生长周期管理</span>
        <el-select v-model="traceId" placeholder="选择溯源批次" @change="load" style="width:200px">
          <el-option v-for="t in traces" :key="t.id" :label="t.productName" :value="t.id" />
        </el-select>
      </div>
      <el-timeline v-if="records.length">
        <el-timeline-item v-for="r in records" :key="r.id" :timestamp="r.createTime" placement="top">
          <el-card><h4>{{ r.title }} ({{ stageText(r.stage) }})</h4><p>{{ r.content }}</p></el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-if="!records.length" description="暂无记录" />
    </el-card>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
const traces = ref([]); const traceId = ref(null); const records = ref([])
function stageText(s){return{sow:'播种',bloom:'开花',fruit:'结果',ripe:'成熟'}[s]||s}
async function loadTraces(){const r=await axios.get('/api/trace/list?pageNum=1&pageSize=100');traces.value=r.data.data?.records||[]}
async function load(){if(!traceId.value)return;const r=await axios.get('/api/trace/growth/'+traceId.value);records.value=r.data.data||[]}
onMounted(loadTraces)
</script>
<style scoped>.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}</style>

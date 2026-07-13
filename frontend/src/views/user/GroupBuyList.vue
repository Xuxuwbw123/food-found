<template>
  <div class="page-container">
    <h2 style="margin-bottom:16px">拼团活动</h2>
    <el-row :gutter="16">
      <el-col :span="8" v-for="gb in list" :key="gb.id" style="margin-bottom:16px">
        <el-card shadow="hover">
          <h3>{{ gb.title }}</h3>
          <p style="color:#f56c6c;font-size:20px;font-weight:bold;margin:8px 0">¥{{ gb.groupPrice }} <span style="color:#999;font-size:14px;text-decoration:line-through">¥{{ gb.originalPrice }}</span></p>
          <p style="color:#666;font-size:13px">成团人数: {{ gb.groupSize }}人 | 已参团: {{ gb.currentCount }}人</p>
          <p style="color:#999;font-size:12px">{{ gb.startTime }} ~ {{ gb.endTime }}</p>
          <el-progress :percentage="Math.min(100, (gb.currentCount / gb.groupSize) * 100)" :stroke-width="8" style="margin:8px 0" />
          <el-button type="primary" style="width:100%" @click="joinGroupBuy(gb.id)">参与拼团</el-button>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!list.length" description="暂无拼团活动" />
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const list = ref([])

async function loadData() {
  try { const r = await axios.get('/api/group-buy/list'); list.value = r.data.data || [] } catch {}
}

async function joinGroupBuy(id) {
  try { await axios.post(`/api/group-buy/join/${id}`); ElMessage.success('参与成功'); loadData() } catch (e) { ElMessage.error(e.response?.data?.msg || '参与失败') }
}

onMounted(loadData)
</script>
<style scoped>.page-container{max-width:1200px;margin:0 auto;padding:20px}</style>

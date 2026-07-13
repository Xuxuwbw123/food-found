<template>
  <div class="page-container">
    <el-card shadow="never">
      <h2 style="margin-bottom:16px">绿色积分</h2>

      <el-row :gutter="16" style="margin-bottom:20px">
        <el-col :span="12">
          <el-card shadow="hover" style="background:linear-gradient(135deg,#43e97b,#38f9d7);color:#fff;text-align:center">
            <h2 style="font-size:32px;margin:0">{{ myPoints }}</h2>
            <p>可用积分</p>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover" style="background:linear-gradient(135deg,#f093fb,#f5576c);color:#fff;text-align:center">
            <h2 style="font-size:32px;margin:0">{{ certs.length }}</h2>
            <p>助农证书</p>
          </el-card>
        </el-col>
      </el-row>

      <h3>积分规则</h3>
      <el-table :data="rules" border style="margin:12px 0">
        <el-table-column prop="ruleName" label="规则名称" min-width="150" />
        <el-table-column prop="actionType" label="行为类型" width="120" />
        <el-table-column prop="points" label="积分" width="80" />
        <el-table-column prop="description" label="描述" min-width="200" />
      </el-table>

      <el-divider />
      <h3>我的助农证书</h3>
      <el-row :gutter="16" v-if="certs.length">
        <el-col :span="8" v-for="c in certs" :key="c.id" style="margin-bottom:12px">
          <el-card shadow="hover">
            <h4>{{ c.certName || '助农证书' }}</h4>
            <p style="color:#666;font-size:13px">{{ c.createTime }}</p>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else description="暂无证书" />
    </el-card>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const rules = ref([]); const certs = ref([]); const myPoints = ref(0)

async function loadData() {
  try {
    const [r1, r2] = await Promise.all([axios.get('/api/green-points/rules'), axios.get('/api/green-points/certs')])
    rules.value = r1.data.data || []
    certs.value = r2.data.data || []
  } catch {}
  try { const r = await axios.get('/api/member/info'); myPoints.value = r.data.data?.points || 0 } catch {}
}

onMounted(loadData)
</script>
<style scoped>.page-container{max-width:1200px;margin:0 auto;padding:20px}</style>

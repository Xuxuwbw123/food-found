<template>
  <div class="points-page">
    <div class="section-content">
      <h2>我的积分</h2>
      <el-card shadow="never" style="margin-bottom:20px">
        <div style="display:flex;justify-content:space-around;text-align:center;padding:20px 0">
          <div><div style="font-size:36px;color:#f56c6c;font-weight:bold">{{ info.availablePoint || 0 }}</div><div style="color:#999;margin-top:8px">可用积分</div></div>
          <div><div style="font-size:36px;color:#409eff;font-weight:bold">{{ info.totalPoint || 0 }}</div><div style="color:#999;margin-top:8px">累计获得</div></div>
          <div><div style="font-size:16px;color:#999">100积分 = 1元</div><div style="color:#999;margin-top:4px">消费1元 = 1积分</div></div>
        </div>
      </el-card>
      <el-card shadow="never">
        <template #header><span>积分流水</span></template>
        <el-table :data="logs" stripe v-loading="loading">
          <el-table-column prop="createTime" label="时间" width="160" />
          <el-table-column label="类型" width="80"><template #default="{row}"><el-tag :type="logType(row.type)" size="small">{{logText(row.type)}}</el-tag></template></el-table-column>
          <el-table-column label="变动" width="100"><template #default="{row}"><span :style="{color:row.point>0?'#67c23a':'#f56c6c',fontWeight:'bold'}">{{row.point>0?'+':''}}{{row.point}}</span></template></el-table-column>
          <el-table-column prop="balance" label="余额" width="80" />
          <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        </el-table>
        <el-pagination v-if="total>logs.length" v-model:current-page="pageNum" :page-size="20" layout="total,prev,pager,next" :total="total" @current-change="loadLogs" style="margin-top:16px;justify-content:flex-end" />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useAuthStore } from '../../stores/auth'
const auth = useAuthStore(); auth.restoreSession()
const info = ref({}); const logs = ref([]); const loading = ref(false); const total = ref(0); const pageNum = ref(1)
function logType(t) { return {earn:'success',use:'warning',refund:'info',freeze:'danger'}[t]||'' }
function logText(t) { return {earn:'获得',use:'使用',refund:'退回',freeze:'冻结'}[t]||t }
async function loadInfo() { try { const r = await axios.get('/api/point/info'); info.value = r.data.data || {} } catch {} }
async function loadLogs() {
  loading.value = true
  try { const r = await axios.get('/api/point/log', { params: { pageNum: pageNum.value, pageSize: 20 } }); logs.value = r.data.data?.records || []; total.value = r.data.data?.total || 0 } finally { loading.value = false }
}
onMounted(() => { if (auth.isLoggedIn) { loadInfo(); loadLogs() } })
</script>

<style scoped>
.points-page { min-height: 70vh; background: #f5f6f7; padding-bottom: 40px; }
.section-content { max-width: 800px; margin: 0 auto; padding: 20px; }
h2 { font-size: 20px; padding: 10px 0; }
</style>
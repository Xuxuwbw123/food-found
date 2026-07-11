<template>
  <div class="coupon-page"><div class="section-content">
    <h2>领券中心</h2>
    <div v-loading="loading" class="coupon-grid">
      <div v-for="c in list" :key="c.id" class="coupon-card" :class="{taken:c.hasTaken>0}">
        <div class="coupon-amount"><span style="font-size:16px">¥</span><span style="font-size:36px;font-weight:bold">{{c.faceValue}}</span></div>
        <div class="coupon-info">
          <div class="coupon-name">{{c.name}}</div>
          <div class="coupon-cond">{{c.minAmount>0?'满'+c.minAmount+'元可用':'无门槛'}}</div>
          <div class="coupon-time">{{c.startTime?.slice(0,10)}} ~ {{c.endTime?.slice(0,10)}}</div>
        </div>
        <el-button :type="c.hasTaken>0?'info':'danger'" :disabled="c.hasTaken>0" @click="take(c.id)">{{c.hasTaken>0?'已领取':'立即领取'}}</el-button>
      </div>
    </div>
    <el-empty v-if="!loading&&list.length===0" description="暂无可用优惠券" />
  </div></div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
const auth = useAuthStore(); auth.restoreSession()
const list = ref([]); const loading = ref(false)
async function load() { loading.value = true; try { const r = await axios.get('/api/coupon/available'); list.value = r.data.data || [] } finally { loading.value = false } }
async function take(cid) { try { await axios.post(`/api/coupon/take/${cid}`); ElMessage.success('领取成功'); load() } catch(e) { ElMessage.error(e.response?.data?.message||'领取失败') } }
onMounted(() => { if (auth.isLoggedIn) load() })
</script>

<style scoped>
.coupon-page{min-height:70vh;background:#f5f6f7;padding-bottom:40px}.section-content{max-width:900px;margin:0 auto;padding:20px}h2{font-size:20px;padding:10px 0}
.coupon-grid{display:grid;grid-template-columns:repeat(2,1fr);gap:16px}
.coupon-card{background:linear-gradient(135deg,#f56c6c,#e64242);color:#fff;border-radius:12px;padding:20px;display:flex;align-items:center;gap:16px;transition:.3s}
.coupon-card.taken{background:linear-gradient(135deg,#bbb,#999)}
.coupon-amount{flex-shrink:0;text-align:center;min-width:80px;border-right:1px dashed rgba(255,255,255,.4);padding-right:16px}
.coupon-info{flex:1}.coupon-name{font-size:16px;font-weight:600}.coupon-cond{font-size:13px;margin-top:4px;opacity:.8}.coupon-time{font-size:12px;margin-top:4px;opacity:.6}
</style>
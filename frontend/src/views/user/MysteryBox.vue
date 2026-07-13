<template>
  <div class="page-container">
    <h2 style="margin-bottom:16px">惊喜盲盒</h2>
    <el-row :gutter="16">
      <el-col :span="6" v-for="b in boxes" :key="b.id" style="margin-bottom:16px">
        <el-card shadow="hover" class="box-card">
          <el-image v-if="b.imageUrl" :src="b.imageUrl" style="width:100%;height:160px;border-radius:8px" fit="cover" />
          <div v-else class="box-placeholder">?</div>
          <h3 style="margin:10px 0 4px">{{ b.name }}</h3>
          <p style="color:#666;font-size:13px;height:40px;overflow:hidden">{{ b.description }}</p>
          <p style="color:#f56c6c;font-size:20px;font-weight:bold;margin:8px 0">¥{{ b.price }}</p>
          <p style="color:#999;font-size:12px">剩余 {{ (b.totalCount||0) - (b.soldCount||0) }} 个</p>
          <el-button type="warning" style="width:100%;margin-top:8px" @click="buyBox(b)">立即开盒</el-button>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!boxes.length" description="暂无盲盒" />
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const boxes = ref([])

async function loadData() {
  try { const r = await axios.get('/api/mystery-boxes'); boxes.value = r.data.data || [] } catch {}
}

function buyBox(b) {
  ElMessageBox.confirm(`确定花费 ¥${b.price} 购买【${b.name}】盲盒？`, '购买盲盒', { type: 'info' })
    .then(() => { ElMessage.success('购买成功！请到订单查看') })
    .catch(() => {})
}

onMounted(loadData)
</script>
<style scoped>.page-container{max-width:1200px;margin:0 auto;padding:20px}.box-card{text-align:center}.box-placeholder{width:100%;height:160px;background:linear-gradient(135deg,#667eea,#764ba2);border-radius:8px;display:flex;align-items:center;justify-content:center;font-size:48px;color:#fff;font-weight:bold}</style>

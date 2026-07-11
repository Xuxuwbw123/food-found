<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header"><span class="page-title">会员等级配置</span></div>
      <el-table :data="configs" border stripe v-loading="loading">
        <el-table-column prop="level" label="等级" width="80" />
        <el-table-column prop="levelName" label="等级名称" width="120">
          <template #default="{row}"><el-input v-model="row.levelName" size="small" /></template>
        </el-table-column>
        <el-table-column label="折扣率" width="150">
          <template #default="{row}"><el-input-number v-model="row.discountRate" :min="0.1" :max="1" :step="0.01" :precision="2" size="small" style="width:100px" /></template>
        </el-table-column>
        <el-table-column label="消费满额自动升级(元)" width="180">
          <template #default="{row}"><el-input-number v-model="row.upgradeAmount" :min="0" :step="100" size="small" style="width:120px" /></template>
        </el-table-column>
        <el-table-column label="充值最低金额(元)" width="180">
          <template #default="{row}"><el-input-number v-model="row.rechargeMin" :min="0" :step="10" size="small" style="width:120px" /></template>
        </el-table-column>
        <el-table-column label="积分倍率" width="120">
          <template #default="{row}"><el-input-number v-model="row.pointsRate" :min="1" :max="10" size="small" style="width:80px" /></template>
        </el-table-column>
        <el-table-column label="说明" min-width="200">
          <template #default="{row}">
            <span v-if="row.level===0">所有用户默认等级</span>
            <span v-else-if="row.level===1">消费满{{row.upgradeAmount}}元或充值{{row.rechargeMin}}元</span>
            <span v-else-if="row.level===2">消费满{{row.upgradeAmount}}元或充值{{row.rechargeMin}}元</span>
            <span v-else-if="row.level===3">消费满{{row.upgradeAmount}}元或充值{{row.rechargeMin}}元</span>
          </template>
        </el-table-column>
      </el-table>
      <el-button type="primary" style="margin-top:16px" @click="save" :loading="saving">保存配置</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const configs = ref([])
const loading = ref(false)
const saving = ref(false)

async function load() {
  loading.value = true
  try {
    const r = await axios.get('/api/admin/member-level/config')
    configs.value = r.data.data || []
  } finally { loading.value = false }
}

async function save() {
  saving.value = true
  try {
    await axios.put('/api/admin/member-level/config', configs.value)
    ElMessage.success('保存成功')
  } catch { ElMessage.error('保存失败') } finally { saving.value = false }
}

onMounted(load)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}
@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}
.page-title{font-size:16px;font-weight:600}
</style>

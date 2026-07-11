<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header"><span class="page-title">系统配置</span></div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="configKey" label="配置键" width="200" />
        <el-table-column prop="configName" label="配置名称" width="180" />
        <el-table-column prop="configValue" label="配置值" width="250" />
        <el-table-column prop="configGroup" label="分组" width="100"><template #default="{row}"><el-tag size="small">{{groupText(row.configGroup)}}</el-tag></template></el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right"><template #default="{row}"><el-button link type="primary" size="small" @click="edit(row)">编辑</el-button></template></el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="editVisible" title="修改配置" width="450px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="配置名称"><el-input :model-value="form.configName" disabled /></el-form-item>
        <el-form-item label="配置键"><el-input :model-value="form.configKey" disabled /></el-form-item>
        <el-form-item label="配置值"><el-input v-model="form.configValue" /></el-form-item>
        <el-form-item label="备注"><span style="color:#999;font-size:13px">{{form.remark}}</span></el-form-item>
      </el-form>
      <template #footer><el-button @click="editVisible=false">取消</el-button><el-button type="primary" @click="save" :loading="saving">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
const tableData = ref([]); const loading = ref(false); const editVisible = ref(false); const saving = ref(false)
const form = reactive({ configKey:'', configName:'', configValue:'', remark:'' })
function groupText(g) { return {base:'基础配置',trade:'交易配置',point:'积分配置',security:'安全配置'}[g]||g }

async function loadData() { loading.value = true; try { const r = await axios.get('/api/admin/config/list'); tableData.value = r.data.data?.records || r.data.data || [] } finally { loading.value = false } }
function edit(row) { form.configKey = row.configKey; form.configName = row.configName; form.configValue = row.configValue; form.remark = row.remark; editVisible.value = true }
async function save() { saving.value = true; try { await axios.put('/api/admin/config/update',{configKey:form.configKey,configValue:form.configValue}); ElMessage.success('配置已更新'); editVisible.value=false; loadData() } catch {} finally { saving.value = false } }
onMounted(loadData)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}
</style>
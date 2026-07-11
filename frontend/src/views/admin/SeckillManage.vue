<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header"><span class="page-title">秒杀管理</span><el-button type="primary" @click="openDialog()">新增秒杀</el-button></div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="160" />
        <el-table-column prop="productName" label="商品名称" width="150" />
        <el-table-column label="原价" width="80"><template #default="{row}">¥{{row.price}}</template></el-table-column>
        <el-table-column label="秒杀价" width="80"><template #default="{row}"><span style="color:#f56c6c;font-weight:bold">¥{{row.seckillPrice}}</span></template></el-table-column>
        <el-table-column label="库存" width="100"><template #default="{row}">{{row.sold}}/{{row.stock}}</template></el-table-column>
        <el-table-column label="时间" width="300"><template #default="{row}">{{row.startTime}} ~ {{row.endTime}}</template></el-table-column>
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'" size="small">{{row.status===1?'启用':'停用'}}</el-tag></template></el-table-column>
        <el-table-column label="操作" fixed="right" width="160"><template #default="{row}"><el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button><el-popconfirm title="确定删除？" @confirm="del(row.id)"><template #ref><el-button link type="danger" size="small">删除</el-button></template></el-popconfirm></template></el-table-column>
      </el-table>
    </el-card>
    <el-dialog :title="form.id?'编辑秒杀':'新增秒杀'" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品ID"><el-input v-model="form.productId" placeholder="输入商品ID" /></el-form-item>
        <el-form-item label="秒杀价"><el-input-number v-model="form.seckillPrice" :min="0.01" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="活动时间"><el-date-picker v-model="dateRange" type="datetimerange" range-separator="至" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="save" :loading="saving">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
const tableData = ref([]); const loading = ref(false); const dialogVisible = ref(false); const saving = ref(false)
const form = reactive({ id:null, productId:'', seckillPrice:0, stock:100, status:1 }); const dateRange = ref([])
async function load() { loading.value = true; try { const r = await axios.get('/api/admin/seckill'); tableData.value = r.data.data?.records || r.data.data || [] } finally { loading.value = false } }
function openDialog(row) {
  if (row) { form.id=row.id; form.productId=String(row.productId); form.seckillPrice=row.seckillPrice; form.stock=row.stock; form.status=row.status; dateRange.value=[row.startTime,row.endTime] }
  else { form.id=null; form.productId=''; form.seckillPrice=0; form.stock=100; form.status=1; dateRange.value=[] }
  dialogVisible.value = true
}
async function save() {
  saving.value = true
  try {
    const data = { id:form.id, productId:parseInt(form.productId), seckillPrice:form.seckillPrice, stock:form.stock, status:form.status }
    if (dateRange.value.length===2) { data.startTime=dateRange.value[0]; data.endTime=dateRange.value[1] }
    if (form.id) await axios.put('/api/admin/seckill', data); else await axios.post('/api/admin/seckill', data)
    ElMessage.success(form.id?'更新成功':'创建成功'); dialogVisible.value=false; load()
  } catch(e) { ElMessage.error(e.response?.data?.message||'操作失败') } finally { saving.value = false }
}
async function del(id) { await axios.delete(`/api/admin/seckill/${id}`); ElMessage.success('已删除'); load() }
onMounted(load)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}
</style>
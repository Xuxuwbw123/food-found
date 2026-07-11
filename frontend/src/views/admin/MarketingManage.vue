<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header"><span class="page-title">营销活动管理</span><el-button type="primary" @click="openDialog()">新增活动</el-button></div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="160" />
        <el-table-column prop="name" label="活动名称" width="180" />
        <el-table-column label="类型" width="100"><template #default="{row}">{{typeText(row.type)}}</template></el-table-column>
        <el-table-column prop="rule" label="规则" width="120" show-overflow-tooltip />
        <el-table-column label="时间" width="300"><template #default="{row}">{{row.startTime}} ~ {{row.endTime}}</template></el-table-column>
        <el-table-column label="状态" width="100"><template #default="{row}"><el-tag :type="statusTag(row.status)" size="small">{{statusText(row.status)}}</el-tag></template></el-table-column>
        <el-table-column label="操作" fixed="right" width="160"><template #default="{row}"><el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button><el-popconfirm title="确定删除？" @confirm="del(row.id)"><template #ref><el-button link type="danger" size="small">删除</el-button></template></el-popconfirm></template></el-table-column>
      </el-table>
    </el-card>
    <el-dialog :title="form.id?'编辑活动':'新增活动'" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="活动名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="form.type"><el-option label="折扣活动" value="discount" /><el-option label="满减活动" value="full_reduce" /><el-option label="新人专享" value="new_user" /></el-select></el-form-item>
        <el-form-item label="规则(JSON)"><el-input v-model="form.rule" placeholder='如 {"discount":0.8}' /></el-form-item>
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
const tableData=ref([]);const loading=ref(false);const dialogVisible=ref(false);const saving=ref(false)
const form=reactive({id:null,name:'',type:'discount',rule:'',status:0});const dateRange=ref([])
function typeText(t){return{discount:'折扣活动',full_reduce:'满减活动',new_user:'新人专享'}[t]||t}
function statusTag(s){return[,'success','info'][s]||'warning'}
function statusText(s){return[,'进行中','已结束'][s]||'未开始'}

async function load(){loading.value=true;try{const r=await axios.get('/api/admin/marketing');tableData.value=r.data.data||[]}finally{loading.value=false}}
function openDialog(row){
  if(row){form.id=row.id;form.name=row.name;form.type=row.type;form.rule=row.rule||'';form.status=row.status;dateRange.value=[row.startTime,row.endTime]}
  else{form.id=null;form.name='';form.type='discount';form.rule='';form.status=0;dateRange.value=[]}
  dialogVisible.value=true
}
async function save(){
  saving.value=true
  try{
    const d={id:form.id,name:form.name,type:form.type,rule:form.rule,status:form.status}
    if(dateRange.value.length===2){d.startTime=dateRange.value[0];d.endTime=dateRange.value[1]}
    if(form.id) await axios.put('/api/admin/marketing',d); else await axios.post('/api/admin/marketing',d)
    ElMessage.success(form.id?'更新成功':'创建成功');dialogVisible.value=false;load()
  }catch{}finally{saving.value=false}
}
async function del(id){await axios.delete('/api/admin/marketing/'+id);ElMessage.success('已删除');load()}
onMounted(load)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}
</style>
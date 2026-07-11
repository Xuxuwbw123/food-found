<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header"><span class="page-title">优惠券管理</span><el-button type="primary" @click="openDialog()">新增优惠券</el-button></div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="160" />
        <el-table-column prop="name" label="名称" width="150" />
        <el-table-column label="类型" width="100"><template #default="{row}">{{typeText(row.type)}}</template></el-table-column>
        <el-table-column label="面额" width="80"><template #default="{row}">¥{{row.faceValue}}</template></el-table-column>
        <el-table-column label="门槛" width="80"><template #default="{row}">¥{{row.minAmount||0}}</template></el-table-column>
        <el-table-column label="发放" width="120"><template #default="{row}">{{row.takenCount}}/{{row.totalCount}}</template></el-table-column>
        <el-table-column label="有效期" width="200"><template #default="{row}">{{row.startTime}} ~ {{row.endTime}}</template></el-table-column>
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'" size="small">{{row.status===1?'启用':'停用'}}</el-tag></template></el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{row}"><el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button><el-button link size="small" @click="viewRecords(row)">记录</el-button><el-popconfirm title="确定删除？" @confirm="del(row.id)"><template #ref><el-button link type="danger" size="small">删除</el-button></template></el-popconfirm></template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog :title="form.id?'编辑优惠券':'新增优惠券'" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="form.type"><el-option label="满减券" value="full_reduce" /><el-option label="新人券" value="new_user" /><el-option label="通用券" value="general" /></el-select></el-form-item>
        <el-form-item label="面额"><el-input-number v-model="form.faceValue" :min="1" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="使用门槛"><el-input-number v-model="form.minAmount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="发放总量"><el-input-number v-model="form.totalCount" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="startTime" type="datetime" placeholder="选择开始时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="endTime" type="datetime" placeholder="选择结束时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="save" :loading="saving">保存</el-button></template>
    </el-dialog>
    <el-dialog v-model="recordVisible" title="领取记录" width="700px">
      <el-table :data="records" border stripe v-loading="recLoading">
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column label="状态" width="100"><template #default="{r}"><el-tag :type="r.status==='unused'?'':r.status==='used'?'success':'info'" size="small">{{r.status==='unused'?'未使用':r.status==='used'?'已使用':'已过期'}}</el-tag></template></el-table-column>
        <el-table-column prop="takeTime" label="领取时间" width="160" />
        <el-table-column prop="useTime" label="使用时间" width="160" />
      </el-table>
      <el-pagination v-model:current-page="recPage" :page-size="20" layout="total,prev,pager,next" :total="recTotal" @current-change="loadRecords" style="margin-top:16px;justify-content:flex-end" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
const tableData = ref([]); const loading = ref(false); const dialogVisible = ref(false); const saving = ref(false)
const form = reactive({ id: null, name: '', type: 'full_reduce', faceValue: 10, minAmount: 0, totalCount: 100, status: 1 })
const startTime = ref(null); const endTime = ref(null)
const recordVisible = ref(false); const records = ref([]); const recLoading = ref(false); const recTotal = ref(0); const recPage = ref(1); const recCouponId = ref(null)
function typeText(t) { return {full_reduce:'满减券',new_user:'新人券',general:'通用券'}[t]||t }

async function load() { loading.value = true; try { const r = await axios.get('/api/admin/coupons'); tableData.value = r.data.data?.records || r.data.data || [] } finally { loading.value = false } }
function openDialog(row) {
  if (row) { form.id = row.id; form.name = row.name; form.type = row.type; form.faceValue = row.faceValue; form.minAmount = row.minAmount; form.totalCount = row.totalCount; form.status = row.status; startTime.value = row.startTime; endTime.value = row.endTime }
  else { form.id = null; form.name = ''; form.type = 'full_reduce'; form.faceValue = 10; form.minAmount = 0; form.totalCount = 100; form.status = 1; startTime.value = null; endTime.value = null }
  dialogVisible.value = true
}
async function save() {
  saving.value = true
  try {
    const data = { id: form.id, name: form.name, type: form.type, faceValue: form.faceValue, minAmount: form.minAmount, totalCount: form.totalCount, status: form.status }
    if (startTime.value) data.startTime = startTime.value
    if (endTime.value) data.endTime = endTime.value
    if (form.id) await axios.put('/api/admin/coupons', data); else await axios.post('/api/admin/coupons', data)
    ElMessage.success(form.id ? '更新成功' : '创建成功'); dialogVisible.value = false; load()
  } catch {} finally { saving.value = false }
}
async function del(id) { await axios.delete(`/api/admin/coupons/${id}`); ElMessage.success('已删除'); load() }
async function viewRecords(row) { recCouponId.value = row.id; recPage.value = 1; loadRecords(); recordVisible.value = true }
async function loadRecords() { recLoading.value = true; try { const r = await axios.get(`/api/admin/coupons/${recCouponId.value}/records`,{params:{pageNum:recPage.value,pageSize:20}}); records.value = r.data.data?.records||[]; recTotal.value = r.data.data?.total||0 } finally { recLoading.value = false } }
onMounted(load)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}
</style>
<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">拼团管理</span>
        <el-button type="primary" @click="openDialog()">新增拼团</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="title" label="拼团标题" min-width="150" />
        <el-table-column prop="productId" label="商品ID" width="100" />
        <el-table-column label="拼团价" width="100"><template #default="{row}">¥{{row.groupPrice}}</template></el-table-column>
        <el-table-column label="原价" width="100"><template #default="{row}">¥{{row.originalPrice}}</template></el-table-column>
        <el-table-column prop="groupSize" label="成团人数" width="90" />
        <el-table-column prop="currentCount" label="已参团" width="80" />
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'" size="small">{{row.status===1?'进行中':'已结束'}}</el-tag></template></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="doDelete(row.id)"><template #reference><el-button link type="danger" size="small">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="form.id?'编辑拼团':'新增拼团'" v-model="visible" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="拼团标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="商品ID"><el-input-number v-model="form.productId" :min="1" /></el-form-item>
        <el-form-item label="拼团价"><el-input-number v-model="form.groupPrice" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="原价"><el-input-number v-model="form.originalPrice" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="成团人数"><el-input-number v-model="form.groupSize" :min="2" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="进行中" :value="1" /><el-option label="已结束" :value="0" /></el-select></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="doSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const tableData = ref([]); const loading = ref(false); const visible = ref(false); const saving = ref(false)
const form = reactive({ id:null, title:'', productId:null, groupPrice:null, originalPrice:null, groupSize:2, status:1, startTime:'', endTime:'' })

async function loadData() { loading.value = true; try { const r = await axios.get('/admin/group-buy/list'); tableData.value = r.data.data || [] } finally { loading.value = false } }

function openDialog(row) {
  if (row) { Object.assign(form, row) } else { Object.assign(form, { id:null, title:'', productId:null, groupPrice:null, originalPrice:null, groupSize:2, status:1, startTime:'', endTime:'' }) }
  visible.value = true
}

async function doSave() {
  saving.value = true
  try {
    if (form.id) { await axios.put('/admin/group-buy', form) } else { await axios.post('/admin/group-buy', form) }
    ElMessage.success('保存成功'); visible.value = false; loadData()
  } catch { ElMessage.error('保存失败') } finally { saving.value = false }
}

async function doDelete(id) { try { await axios.delete(`/admin/group-buy/${id}`); ElMessage.success('删除成功'); loadData() } catch { ElMessage.error('删除失败') } }

onMounted(loadData)
</script>
<style scoped>.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}</style>

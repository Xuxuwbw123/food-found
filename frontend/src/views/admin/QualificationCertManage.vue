<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">资质证书管理</span>
        <el-button type="primary" @click="openDialog()">新增证书</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="farmerId" label="农户ID" width="100" />
        <el-table-column prop="certName" label="证书名称" min-width="150" />
        <el-table-column prop="certNo" label="证书编号" width="150" />
        <el-table-column prop="certType" label="证书类型" width="120" />
        <el-table-column prop="issueOrg" label="发证机构" min-width="150" show-overflow-tooltip />
        <el-table-column prop="issueDate" label="发证日期" width="120" />
        <el-table-column prop="expireDate" label="过期日期" width="120" />
        <el-table-column prop="imageUrl" label="证书图片" width="100"><template #default="{row}"><el-image v-if="row.imageUrl" :src="row.imageUrl" style="width:60px;height:40px" fit="cover" /></template></el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="doDelete(row.id)"><template #reference><el-button link type="danger" size="small">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="form.id?'编辑证书':'新增证书'" v-model="visible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="农户ID"><el-input-number v-model="form.farmerId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="证书名称"><el-input v-model="form.certName" /></el-form-item>
        <el-form-item label="证书编号"><el-input v-model="form.certNo" /></el-form-item>
        <el-form-item label="证书类型"><el-input v-model="form.certType" placeholder="如有机认证、绿色食品" /></el-form-item>
        <el-form-item label="发证机构"><el-input v-model="form.issueOrg" /></el-form-item>
        <el-form-item label="发证日期"><el-date-picker v-model="form.issueDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="过期日期"><el-date-picker v-model="form.expireDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="证书图片"><el-input v-model="form.imageUrl" placeholder="图片URL" /></el-form-item>
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
const form = reactive({ id:null, farmerId:null, certName:'', certNo:'', certType:'', issueOrg:'', issueDate:'', expireDate:'', imageUrl:'' })

async function loadData() { loading.value = true; try { const r = await axios.get('/admin/qualification-certs'); tableData.value = r.data.data || [] } finally { loading.value = false } }

function openDialog(row) {
  if (row) { Object.assign(form, row) } else { Object.assign(form, { id:null, farmerId:null, certName:'', certNo:'', certType:'', issueOrg:'', issueDate:'', expireDate:'', imageUrl:'' }) }
  visible.value = true
}

async function doSave() {
  saving.value = true
  try {
    if (form.id) { await axios.put('/admin/qualification-certs', form) } else { await axios.post('/admin/qualification-certs', form) }
    ElMessage.success('保存成功'); visible.value = false; loadData()
  } catch { ElMessage.error('保存失败') } finally { saving.value = false }
}

async function doDelete(id) { try { await axios.delete(`/admin/qualification-certs/${id}`); ElMessage.success('删除成功'); loadData() } catch { ElMessage.error('删除失败') } }

onMounted(loadData)
</script>
<style scoped>.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}</style>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">农场动态管理</span>
        <el-button type="primary" @click="openDialog()">新增动态</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="farmerId" label="农户ID" width="100" />
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="imageUrl" label="图片" width="100"><template #default="{row}"><el-image v-if="row.imageUrl" :src="row.imageUrl" style="width:60px;height:40px" fit="cover" /></template></el-table-column>
        <el-table-column prop="updateType" label="类型" width="100"><template #default="{row}"><el-tag size="small">{{row.updateType||'日常'}}</el-tag></template></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="doDelete(row.id)"><template #reference><el-button link type="danger" size="small">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="form.id?'编辑动态':'新增动态'" v-model="visible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="农户ID"><el-input-number v-model="form.farmerId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="图片"><el-input v-model="form.imageUrl" placeholder="图片URL" /></el-form-item>
        <el-form-item label="类型"><el-input v-model="form.updateType" placeholder="如 采摘、施肥、灌溉" /></el-form-item>
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
const form = reactive({ id:null, farmerId:null, title:'', content:'', imageUrl:'', updateType:'' })

async function loadData() { loading.value = true; try { const r = await axios.get('/admin/farm-updates'); tableData.value = r.data.data || [] } finally { loading.value = false } }

function openDialog(row) {
  if (row) { Object.assign(form, row) } else { Object.assign(form, { id:null, farmerId:null, title:'', content:'', imageUrl:'', updateType:'' }) }
  visible.value = true
}

async function doSave() {
  saving.value = true
  try {
    if (form.id) { await axios.put('/admin/farm-updates', form) } else { await axios.post('/admin/farm-updates', form) }
    ElMessage.success('保存成功'); visible.value = false; loadData()
  } catch { ElMessage.error('保存失败') } finally { saving.value = false }
}

async function doDelete(id) { try { await axios.delete(`/admin/farm-updates/${id}`); ElMessage.success('删除成功'); loadData() } catch { ElMessage.error('删除失败') } }

onMounted(loadData)
</script>
<style scoped>.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}</style>

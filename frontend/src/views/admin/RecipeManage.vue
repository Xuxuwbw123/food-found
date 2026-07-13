<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">菜谱管理</span>
        <el-button type="primary" @click="openDialog()">新增菜谱</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="菜谱名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="cookTime" label="烹饪时间" width="100"><template #default="{row}">{{row.cookTime ? row.cookTime+'分钟' : '-'}}</template></el-table-column>
        <el-table-column prop="difficulty" label="难度" width="80"><template #default="{row}"><el-tag :type="['','success','warning','danger'][row.difficulty]||'info'" size="small">{{['','简单','中等','困难'][row.difficulty]||'-'}}</el-tag></template></el-table-column>
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'" size="small">{{row.status===1?'启用':'禁用'}}</el-tag></template></el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="doDelete(row.id)"><template #reference><el-button link type="danger" size="small">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="form.id?'编辑菜谱':'新增菜谱'" v-model="visible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="菜谱名称"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="封面图"><el-input v-model="form.imageUrl" placeholder="图片URL" /></el-form-item>
        <el-form-item label="烹饪时间"><el-input-number v-model="form.cookTime" :min="1" /> 分钟</el-form-item>
        <el-form-item label="难度">
          <el-select v-model="form.difficulty">
            <el-option label="简单" :value="1" />
            <el-option label="中等" :value="2" />
            <el-option label="困难" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="步骤"><el-input v-model="form.steps" type="textarea" :rows="5" placeholder="详细烹饪步骤" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="启用" :value="1" /><el-option label="禁用" :value="0" /></el-select></el-form-item>
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
const form = reactive({ id:null, title:'', description:'', imageUrl:'', cookTime:null, difficulty:1, steps:'', status:1 })

async function loadData() { loading.value = true; try { const r = await axios.get('/admin/recipes'); tableData.value = r.data.data || [] } finally { loading.value = false } }

function openDialog(row) {
  if (row) { Object.assign(form, row) } else { Object.assign(form, { id:null, title:'', description:'', imageUrl:'', cookTime:null, difficulty:1, steps:'', status:1 }) }
  visible.value = true
}

async function doSave() {
  saving.value = true
  try {
    if (form.id) { await axios.put('/admin/recipes', form) } else { await axios.post('/admin/recipes', form) }
    ElMessage.success('保存成功'); visible.value = false; loadData()
  } catch { ElMessage.error('保存失败') } finally { saving.value = false }
}

async function doDelete(id) { try { await axios.delete(`/admin/recipes/${id}`); ElMessage.success('删除成功'); loadData() } catch { ElMessage.error('删除失败') } }

onMounted(loadData)
</script>
<style scoped>.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}</style>

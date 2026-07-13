<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">主题专区管理</span>
        <el-button type="primary" @click="openDialog()">新增主题</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="主题名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="imageUrl" label="封面图" width="100"><template #default="{row}"><el-image v-if="row.imageUrl" :src="row.imageUrl" style="width:60px;height:40px" fit="cover" /></template></el-table-column>
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'" size="small">{{row.status===1?'启用':'禁用'}}</el-tag></template></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button link type="success" size="small" @click="openProductDialog(row)">关联商品</el-button>
            <el-popconfirm title="确定删除?" @confirm="doDelete(row.id)"><template #reference><el-button link type="danger" size="small">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="form.id?'编辑主题':'新增主题'" v-model="visible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="主题名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="封面图"><el-input v-model="form.imageUrl" placeholder="图片URL" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="启用" :value="1" /><el-option label="禁用" :value="0" /></el-select></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="doSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog title="关联商品" v-model="productVisible" width="400px">
      <el-form label-width="60px">
        <el-form-item label="商品ID"><el-input v-model="productIds" placeholder="多个用逗号分隔" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="productVisible=false">取消</el-button>
        <el-button type="primary" @click="doAddProducts">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const tableData = ref([]); const loading = ref(false); const visible = ref(false); const saving = ref(false)
const productVisible = ref(false); const productIds = ref(''); const currentThemeId = ref(null)
const form = reactive({ id:null, name:'', description:'', imageUrl:'', status:1, startTime:'', endTime:'' })

async function loadData() { loading.value = true; try { const r = await axios.get('/admin/themes'); tableData.value = r.data.data || [] } finally { loading.value = false } }

function openDialog(row) {
  if (row) { Object.assign(form, row) } else { Object.assign(form, { id:null, name:'', description:'', imageUrl:'', status:1, startTime:'', endTime:'' }) }
  visible.value = true
}

function openProductDialog(row) { currentThemeId.value = row.id; productIds.value = ''; productVisible.value = true }

async function doSave() {
  saving.value = true
  try {
    if (form.id) { await axios.put('/admin/themes', form) } else { await axios.post('/admin/themes', form) }
    ElMessage.success('保存成功'); visible.value = false; loadData()
  } catch { ElMessage.error('保存失败') } finally { saving.value = false }
}

async function doAddProducts() {
  const ids = productIds.value.split(',').map(s => Number(s.trim())).filter(n => n > 0)
  if (!ids.length) return ElMessage.warning('请输入商品ID')
  try { await axios.post(`/admin/themes/${currentThemeId.value}/products`, { productIds: ids }); ElMessage.success('添加成功'); productVisible.value = false } catch { ElMessage.error('添加失败') }
}

async function doDelete(id) { try { await axios.delete(`/admin/themes/${id}`); ElMessage.success('删除成功'); loadData() } catch { ElMessage.error('删除失败') } }

onMounted(loadData)
</script>
<style scoped>.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}</style>

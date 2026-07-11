<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header"><span class="page-title">管理员账号</span><el-button type="primary" @click="openDialog()">新增管理员</el-button></div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="160" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'" size="small">{{row.status===1?'启用':'禁用'}}</el-tag></template></el-table-column>
        <el-table-column label="操作" fixed="right" width="280">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button link type="warning" size="small" @click="toggleStatus(row)">{{row.status===1?'禁用':'启用'}}</el-button>
            <el-popconfirm title="确定重置为 Abc123!@？" @confirm="resetPwd(row.id)"><template #reference><el-button link type="info" size="small">重置密码</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="450px" @close="resetForm">
      <el-form :model="form" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username"><el-input v-model="form.username" placeholder="请输入用户名" :disabled="!!form.id" /></el-form-item>
        <el-form-item v-if="!form.id" label="密码" prop="password"><el-input v-model="form.password" type="password" show-password placeholder="请输入密码" /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" placeholder="请输入昵称" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" placeholder="请输入手机号" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const form = reactive({ id: null, username: '', password: '', nickname: '', phone: '' })
const formRef = ref(null)
const dialogTitle = computed(() => form.id ? '编辑管理员' : '新增管理员')

async function loadData() {
  loading.value = true
  try { const res = await axios.get('/api/admin/admins'); tableData.value = res.data || [] } finally { loading.value = false }
}

function openDialog(row) {
  if (row) { form.id = row.id; form.username = row.username; form.nickname = row.nickname||''; form.phone = row.phone||''; form.password = '' }
  else { form.id = null; form.username = ''; form.password = ''; form.nickname = ''; form.phone = '' }
  dialogVisible.value = true
}

function resetForm() { formRef.value?.resetFields() }

async function handleSubmit() {
  if (!form.id && !form.password) { ElMessage.warning('请输入密码'); return }
  submitLoading.value = true
  try {
    if (form.id) await axios.put('/api/admin/admins', { id: form.id, nickname: form.nickname, phone: form.phone, status: 1 })
    else await axios.post('/api/admin/admins', { username: form.username, password: form.password, nickname: form.nickname, phone: form.phone })
    ElMessage.success(form.id ? '更新成功' : '新增成功')
    dialogVisible.value = false; loadData()
  } catch (e) { ElMessage.error(e?.response?.data?.message || e.message || '操作失败') } finally { submitLoading.value = false }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  try { await axios.put('/api/admin/admins', { id: row.id, status: newStatus }); ElMessage.success(newStatus===1?'已启用':'已禁用'); loadData() } catch {}
}

async function resetPwd(id) {
  try { await axios.put(`/api/admin/admins/resetPassword/${id}`); ElMessage.success('密码已重置为 Abc123!@') } catch {}
}

onMounted(loadData)
</script>

<style scoped>
.page-container { animation: fadeIn .3s; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { font-size: 16px; font-weight: 600; }
</style>
<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">用户管理</span>
        <div>
          <el-button v-if="selectedIds.length" type="success" @click="batchEnable">批量启用</el-button>
          <el-button v-if="selectedIds.length" type="danger" @click="batchDisable">批量禁用</el-button>
          <el-button @click="exportData">导出Excel</el-button>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增用户</el-button>
        </div>
      </div>

      <el-form :model="query" inline class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="用户名/手机号" clearable @clear="loadData" />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="query.userType" placeholder="全部" clearable @change="loadData">
            <el-option label="普通用户" :value="1" />
            <el-option label="农户" :value="2" />
            <el-option label="管理员" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable @change="loadData">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading" style="width:100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column label="手机号" width="130"><template #default="{row}">{{maskPhone(row.phone)}}</template></el-table-column>
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column label="用户类型" width="100">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.userType)" size="small">{{ typeText(row.userType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="toggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="170" />
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
            <el-popconfirm title="确定删除该用户？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :page-sizes="[10,20,50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="550px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-radio-group v-model="form.userType">
            <el-radio :value="1">普通用户</el-radio>
            <el-radio :value="2">农户</el-radio>
            <el-radio :value="3">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog title="用户详情" v-model="detailVisible" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detail.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ detail.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detail.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detail.email }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">{{ typeText(detail.userType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status === 1 ? '启用' : '禁用' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ detail.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { maskPhone } from '../../utils/mask'
import { getUserList, getUserDetail, addUser, updateUser, deleteUser, changeUserStatus, batchUsers, exportTable } from '../../api/admin'

const query = reactive({ pageNum: 1, pageSize: 10, keyword: '', userType: null, status: null })
const selectedIds = ref([])

function handleSelectionChange(rows) { selectedIds.value = rows.map(r => r.id) }

async function batchEnable() {
  try { await batchUsers(selectedIds.value, 'enable'); ElMessage.success('批量启用成功'); selectedIds.value = []; loadData() } catch {}
}
async function batchDisable() {
  try { await batchUsers(selectedIds.value, 'disable'); ElMessage.success('批量禁用成功'); selectedIds.value = []; loadData() } catch {}
}
async function exportData() {
  try {
    const res = await exportTable('users')
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const a = document.createElement('a'); a.href = url; a.download = `users_${Date.now()}.csv`; a.click()
    window.URL.revokeObjectURL(url)
  } catch { ElMessage.error('导出失败') }
}
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const submitLoading = ref(false)
const form = reactive({ id: null, username: '', nickname: '', phone: '', email: '', userType: 1, password: '' })
const detail = ref({})
const formRef = ref(null)

const dialogTitle = computed(() => form.id ? '编辑用户' : '新增用户')

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
}

function typeTag(type) {
  return type === 1 ? '' : type === 2 ? 'success' : 'warning'
}
function typeText(type) {
  return type === 1 ? '普通用户' : type === 2 ? '农户' : '管理员'
}

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    Object.keys(params).forEach(k => (params[k] === '' || params[k] === null) && delete params[k])
    const res = await getUserList(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, keyword: '', userType: null, status: null })
  loadData()
}

function openDialog(row) {
  if (row) {
    Object.assign(form, { id: row.id, username: row.username, nickname: row.nickname || '', phone: row.phone || '', email: row.email || '', userType: row.userType, password: '' })
  }
  dialogVisible.value = true
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, username: '', nickname: '', phone: '', email: '', userType: 1, password: '' })
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (form.id) {
      await updateUser({ id: form.id, username: form.username, nickname: form.nickname, phone: form.phone, email: form.email, userType: form.userType })
      ElMessage.success('更新成功')
    } else {
      await addUser({ username: form.username, nickname: form.nickname, phone: form.phone, email: form.email, userType: form.userType, password: form.password })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await changeUserStatus(row.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
  loadData()
}

async function viewDetail(row) {
  const res = await getUserDetail(row.id)
  detail.value = res.data
  detailVisible.value = true
}

async function handleDelete(id) {
  await deleteUser(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page-container { animation: fadeIn .3s; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { font-size: 16px; font-weight: 600; }
.search-form { margin: 16px 0; }
.pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>

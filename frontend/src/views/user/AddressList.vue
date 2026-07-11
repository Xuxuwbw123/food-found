<template>
  <div class="address-page">
    <div class="section-content">
      <div class="page-header">
        <h2>收货地址</h2>
        <el-button type="primary" @click="openDialog()">新增地址</el-button>
      </div>

      <div v-if="!auth.isLoggedIn" class="login-tip">
        <el-empty description="请先登录后管理收货地址">
          <el-button type="primary" @click="$router.push('/login')">去登录</el-button>
        </el-empty>
      </div>

      <div v-else v-loading="loading" class="address-list">
        <div v-for="addr in list" :key="addr.id" class="address-card" :class="{ default: addr.isDefault === 1 }">
          <div class="addr-info">
            <div class="addr-header">
              <span class="addr-name">{{ addr.receiverName }}</span>
              <span class="addr-phone">{{ addr.receiverPhone }}</span>
              <el-tag v-if="addr.isDefault === 1" type="success" size="small">默认</el-tag>
            </div>
            <div class="addr-detail">
              {{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detailAddress }}
            </div>
          </div>
          <div class="addr-actions">
            <el-button link type="primary" size="small" @click="openDialog(addr)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(addr.id)">删除</el-button>
            <el-button v-if="addr.isDefault !== 1" link type="primary" size="small" @click="setDefault(addr.id)">设为默认</el-button>
          </div>
        </div>
        <el-empty v-if="list.length === 0" description="暂无收货地址" />
      </div>
    </div>

    <el-dialog :title="form.id ? '编辑地址' : '新增地址'" v-model="dialogVisible" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="收货人" prop="receiverName">
              <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="receiverPhone">
              <el-input v-model="form.receiverPhone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="省" prop="province">
              <el-input v-model="form.province" placeholder="省" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="市" prop="city">
              <el-input v-model="form.city" placeholder="市" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区" prop="district">
              <el-input v-model="form.district" placeholder="区" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="form.detailAddress" type="textarea" :rows="2" placeholder="街道/小区/门牌号" />
        </el-form-item>
        <el-form-item label="邮编">
          <el-input v-model="form.postalCode" placeholder="邮编（选填）" style="width:160px" />
        </el-form-item>
        <el-form-item label=" ">
          <el-checkbox v-model="form.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saveLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import axios from 'axios'

const auth = useAuthStore()
auth.restoreSession()
const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saveLoading = ref(false)
const formRef = ref(null)

const emptyForm = () => ({ id: null, receiverName: '', receiverPhone: '', province: '', city: '', district: '', detailAddress: '', postalCode: '', isDefault: false })
const form = reactive(emptyForm())

const rules = {
  receiverName: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省', trigger: 'blur' }],
  city: [{ required: true, message: '请输入市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
}

async function loadData() {
  if (!auth.isLoggedIn) return
  loading.value = true
  try {
    const res = await axios.get('/api/address/list', { params: { userId: auth.user.id } })
    list.value = res.data.data?.records || res.data.data || []
  } finally { loading.value = false }
}

function openDialog(row) {
  Object.assign(form, row ? {
    id: row.id, receiverName: row.receiverName, receiverPhone: row.receiverPhone,
    province: row.province, city: row.city, district: row.district,
    detailAddress: row.detailAddress, postalCode: row.postalCode || '',
    isDefault: row.isDefault === 1
  } : emptyForm())
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saveLoading.value = true
  try {
    const data = {
      id: form.id, userId: auth.user.id,
      receiverName: form.receiverName, receiverPhone: form.receiverPhone,
      province: form.province, city: form.city, district: form.district,
      detailAddress: form.detailAddress, postalCode: form.postalCode,
      isDefault: form.isDefault
    }
    if (form.id) {
      await axios.put('/api/address/update', data)
    } else {
      await axios.post('/api/address/add', data)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally { saveLoading.value = false }
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该地址？', '提示', { type: 'warning' })
  await axios.delete(`/api/address/delete/${id}`)
  ElMessage.success('已删除')
  loadData()
}

async function setDefault(id) {
  await axios.put(`/api/address/default/${id}`)
  ElMessage.success('已设为默认')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.address-page { min-height: 70vh; background: #f5f6f7; padding-bottom: 40px; }
.section-content { max-width: 900px; margin: 0 auto; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 0; }
.page-header h2 { font-size: 20px; color: #333; }
.login-tip { padding: 60px 0; }
.address-list { display: flex; flex-direction: column; gap: 12px; }
.address-card { background: #fff; border-radius: 8px; padding: 20px; display: flex; justify-content: space-between; align-items: center; border: 2px solid transparent; }
.address-card.default { border-color: #1a8c3a; }
.addr-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.addr-name { font-size: 16px; font-weight: 600; color: #333; }
.addr-phone { font-size: 14px; color: #666; }
.addr-detail { font-size: 14px; color: #666; }
.addr-actions { display: flex; gap: 8px; white-space: nowrap; }
</style>

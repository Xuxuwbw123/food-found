<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">积分兑换规则</span>
        <el-button type="primary" @click="openDialog()">新增规则</el-button>
      </div>
      <el-table :data="rules" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="160" />
        <el-table-column prop="ruleName" label="规则名称" width="200" />
        <el-table-column prop="pointsCost" label="消耗积分" width="100" />
        <el-table-column label="兑换优惠券" width="180">
          <template #default="{row}">{{ getCouponName(row.couponId) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{row}">
            <el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?'启用':'停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="160">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除？" @confirm="del(row.id)">
              <template #reference><el-button link type="danger" size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="form.id?'编辑规则':'新增规则'" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="规则名称"><el-input v-model="form.ruleName" /></el-form-item>
        <el-form-item label="消耗积分"><el-input-number v-model="form.pointsCost" :min="1" /></el-form-item>
        <el-form-item label="兑换优惠券">
          <el-select v-model="form.couponId" placeholder="选择优惠券" style="width:100%">
            <el-option v-for="c in coupons" :key="c.id" :label="c.name + ' (¥' + c.faceValue + (c.minAmount > 0 ? ' 满' + c.minAmount : '') + ')'" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="save" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const rules = ref([])
const coupons = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const form = reactive({ id: null, ruleName: '', pointsCost: 100, couponId: null, status: 1 })

function getCouponName(couponId) {
  const c = coupons.value.find(x => x.id === couponId)
  return c ? c.name + ' ¥' + c.faceValue : '未关联'
}

async function load() {
  loading.value = true
  try {
    const r = await axios.get('/api/admin/points-exchange/rules')
    rules.value = r.data.data || []
  } finally { loading.value = false }
}

async function loadCoupons() {
  try {
    const r = await axios.get('/admin/coupons')
    coupons.value = r.data.data || []
  } catch {}
}

function openDialog(row) {
  if (row) {
    Object.assign(form, { id: row.id, ruleName: row.ruleName, pointsCost: row.pointsCost, couponId: row.couponId, status: row.status })
  } else {
    Object.assign(form, { id: null, ruleName: '', pointsCost: 100, couponId: null, status: 1 })
  }
  dialogVisible.value = true
}

async function save() {
  if (!form.couponId) { ElMessage.warning('请选择关联的优惠券'); return }
  saving.value = true
  try {
    if (form.id) await axios.put('/api/admin/points-exchange/rules', form)
    else await axios.post('/api/admin/points-exchange/rules', form)
    ElMessage.success(form.id ? '更新成功' : '创建成功')
    dialogVisible.value = false; load()
  } catch { ElMessage.error('操作失败') } finally { saving.value = false }
}

async function del(id) {
  await axios.delete('/api/admin/points-exchange/rules/' + id)
  ElMessage.success('已删除'); load()
}

onMounted(() => { load(); loadCoupons() })
</script>

<style scoped>
.page-container{animation:fadeIn .3s}
@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}
.page-title{font-size:16px;font-weight:600}
</style>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">库存预警管理</span>
        <div>
          <el-button type="warning" @click="checkStock">检查库存</el-button>
          <el-button type="primary" @click="openDialog()">新增预警</el-button>
        </div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="productId" label="商品ID" width="100" />
        <el-table-column prop="alertQuantity" label="预警数量" width="100" />
        <el-table-column prop="lastNotified" label="上次通知" width="160" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" fixed="right" width="100">
          <template #default="{row}">
            <el-popconfirm title="确定删除?" @confirm="doDelete(row.id)"><template #reference><el-button link type="danger" size="small">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card v-if="warnings.length" shadow="never" style="margin-top:16px">
      <div class="page-header"><span class="page-title" style="color:#e6a23c">库存不足商品</span></div>
      <el-table :data="warnings" border stripe>
        <el-table-column prop="productId" label="商品ID" width="100" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="currentStock" label="当前库存" width="100" />
        <el-table-column prop="alertQuantity" label="预警数量" width="100" />
      </el-table>
    </el-card>

    <el-dialog title="新增库存预警" v-model="visible" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="商品ID"><el-input-number v-model="form.productId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="预警数量"><el-input-number v-model="form.alertQuantity" :min="1" style="width:100%" /></el-form-item>
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
const warnings = ref([])
const form = reactive({ productId:null, alertQuantity:10 })

async function loadData() { loading.value = true; try { const r = await axios.get('/admin/stock/alerts'); tableData.value = r.data.data || [] } finally { loading.value = false } }

function openDialog() { Object.assign(form, { productId:null, alertQuantity:10 }); visible.value = true }

async function doSave() {
  saving.value = true
  try { await axios.post('/admin/stock/alerts', form); ElMessage.success('保存成功'); visible.value = false; loadData() } catch { ElMessage.error('保存失败') } finally { saving.value = false }
}

async function doDelete(id) { try { await axios.delete(`/admin/stock/alerts/${id}`); ElMessage.success('删除成功'); loadData() } catch { ElMessage.error('删除失败') } }

async function checkStock() {
  try { const r = await axios.post('/admin/stock/check'); warnings.value = r.data.data || []; if (!warnings.value.length) ElMessage.success('库存充足，无预警') } catch { ElMessage.error('检查失败') }
}

onMounted(loadData)
</script>
<style scoped>.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}</style>

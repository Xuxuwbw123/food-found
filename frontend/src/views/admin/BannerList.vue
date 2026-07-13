<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">轮播图管理</span>
        <el-button type="primary" :icon="Plus" @click="openDialog()">新增轮播图</el-button>
      </div>

      <el-form :model="query" inline class="search-form">
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
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;padding:8px 12px;background:#f5f7fa;border-radius:6px">
        <span style="font-size:13px;color:#666">共 {{ total }} 条记录，第 {{ query.pageNum }} 页</span>
        <div>
          <el-button size="small" :disabled="query.pageNum<=1" @click="query.pageNum--;loadData()">上一页</el-button>
          <el-button size="small" :disabled="query.pageNum*query.pageSize>=total" @click="query.pageNum++;loadData()">下一页</el-button>
          <el-select v-model="query.pageSize" size="small" style="width:90px;margin-left:8px" @change="query.pageNum=1;loadData()">
            <el-option :value="10" label="10条/页" />
            <el-option :value="20" label="20条/页" />
            <el-option :value="50" label="50条/页" />
          </el-select>
        </div>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column label="图片" width="200">
          <template #default="{ row }">
            <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width:180px;height:80px;border-radius:6px" fit="cover" />
            <span v-else style="color:#ccc">无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" width="180" />
        <el-table-column label="链接类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ linkTypeText(row.linkType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="linkId" label="关联ID" width="100" />
        <el-table-column prop="linkUrl" label="自定义URL" width="200" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="70" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="toggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>

      </el-table>

    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="550px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="轮播图标题" />
        </el-form-item>
        <el-form-item label="图片URL" prop="imageUrl">
          <el-input v-model="form.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="链接类型">
          <el-radio-group v-model="form.linkType">
            <el-radio :value="0">无链接</el-radio>
            <el-radio :value="1">商品</el-radio>
            <el-radio :value="2">分类</el-radio>
            <el-radio :value="3">自定义URL</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="关联ID" v-if="form.linkType === 1 || form.linkType === 2">
          <el-input-number v-model="form.linkId" :min="0" placeholder="关联的商品/分类ID" style="width:100%" />
        </el-form-item>
        <el-form-item label="链接URL" v-if="form.linkType === 3">
          <el-input v-model="form.linkUrl" placeholder="请输入链接URL" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getBannerList, addBanner, updateBanner, deleteBanner, changeBannerStatus } from '../../api/admin'

const query = reactive({ pageNum: 1, pageSize: 10, status: null })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const form = reactive({ id: null, title: '', imageUrl: '', linkType: 0, linkId: null, linkUrl: '', sort: 0, remark: '' })
const formRef = ref(null)

const dialogTitle = computed(() => form.id ? '编辑轮播图' : '新增轮播图')

function linkTypeText(t) {
  return t === 0 ? '无链接' : t === 1 ? '商品' : t === 2 ? '分类' : '自定义URL'
}

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请输入图片URL', trigger: 'blur' }],
}

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    Object.keys(params).forEach(k => (params[k] === '' || params[k] === null) && delete params[k])
    const res = await getBannerList(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, status: null })
  loadData()
}

function openDialog(row) {
  if (row) {
    Object.assign(form, { id: row.id, title: row.title, imageUrl: row.imageUrl, linkType: row.linkType, linkId: row.linkId, linkUrl: row.linkUrl || '', sort: row.sort || 0, remark: row.remark || '' })
  }
  dialogVisible.value = true
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, title: '', imageUrl: '', linkType: 0, linkId: null, linkUrl: '', sort: 0, remark: '' })
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    const data = { title: form.title, imageUrl: form.imageUrl, linkType: form.linkType, linkId: form.linkId, linkUrl: form.linkUrl, sort: form.sort, remark: form.remark }
    if (form.id) {
      await updateBanner({ id: form.id, ...data })
      ElMessage.success('更新成功')
    } else {
      await addBanner(data)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await changeBannerStatus(row.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
  loadData()
}

async function handleDelete(id) {
  await deleteBanner(id)
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

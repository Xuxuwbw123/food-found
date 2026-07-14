<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">评论审核</span>
        <div>
          <el-radio-group v-model="query.status" @change="loadData">
            <el-radio-button :value="null">全部</el-radio-button>
            <el-radio-button :value="0">待审核</el-radio-button>
            <el-radio-button :value="1">已通过</el-radio-button>
            <el-radio-button :value="2">已拒绝</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <div style="display:flex;gap:12px;margin-bottom:16px;flex-wrap:wrap;align-items:center">
        <el-input v-model="query.keyword" placeholder="搜索商品名/评论内容/用户名" clearable style="width:300px" @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-tag v-if="query.productId" closable @close="query.productId=null;loadData()" type="success">
          商品筛选：{{ query.productName || query.productId }}
        </el-tag>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="ID" width="100" show-overflow-tooltip />
        <el-table-column label="商品" min-width="180">
          <template #default="{ row }">
            <div style="display:flex;align-items:center;gap:8px">
              <el-image v-if="row.mainImage" :src="row.mainImage" style="width:40px;height:40px;border-radius:4px;flex-shrink:0" fit="cover" />
              <div>
                <div style="font-weight:600;cursor:pointer;color:#1a8c3a" @click="filterByProduct(row.productId, row.productName)">
                  {{ row.productName || '未知商品' }}
                </div>
                <div style="font-size:12px;color:#999">{{ row.categoryName || '未分类' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="用户" width="100">
          <template #default="{ row }">
            <span>{{ row.nickname || row.username || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" min-width="220" show-overflow-tooltip />
        <el-table-column label="图片" width="120">
          <template #default="{ row }">
            <div v-if="row.images" style="display:flex;gap:4px;flex-wrap:wrap">
              <el-image
                v-for="(img, idx) in row.images.split(',').filter(x => x).slice(0, 3)"
                :key="idx"
                :src="img"
                :preview-src-list="row.images.split(',').filter(x => x)"
                :initial-index="idx"
                fit="cover"
                style="width:40px;height:40px;border-radius:4px"
              >
                <template #error><div style="width:40px;height:40px;background:#f5f5f5;display:flex;align-items:center;justify-content:center;color:#999;font-size:10px">?</div></template>
              </el-image>
              <span v-if="row.images.split(',').filter(x => x).length > 3" style="font-size:12px;color:#999;align-self:center">
                +{{ row.images.split(',').filter(x => x).length - 3 }}
              </span>
            </div>
            <span v-else style="color:#ccc">-</span>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="120">
          <template #default="{ row }">
            <el-rate :model-value="row.rating" disabled show-score size="small" />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'" size="small">
              {{ row.status === 0 ? '待审核' : row.status === 1 ? '已通过' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评论时间" width="170" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" link type="success" size="small" @click="approve(row.id)">通过</el-button>
            <el-button v-if="row.status === 0" link type="warning" size="small" @click="reject(row.id)">拒绝</el-button>
            <el-popconfirm title="确定删除此评论？删除后用户端也将不可见" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>

      </el-table>

    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCommentList, approveComment, rejectComment, deleteComment } from '../../api/admin'

const query = reactive({ pageNum: 1, pageSize: 10, status: 0, keyword: '', productId: null, productName: '' })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const params = { pageNum: query.pageNum, pageSize: query.pageSize }
    if (query.status !== null && query.status !== '' && query.status !== undefined) params.status = query.status
    if (query.productId) params.productId = query.productId
    if (query.keyword) params.keyword = query.keyword
    const res = await getCommentList(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function filterByProduct(productId, productName) {
  query.productId = productId
  query.productName = productName
  query.pageNum = 1
  loadData()
}

async function approve(id) {
  await approveComment(id)
  ElMessage.success('已通过')
  loadData()
}

async function reject(id) {
  await rejectComment(id)
  ElMessage.success('已拒绝')
  loadData()
}

async function handleDelete(id) {
  await deleteComment(id)
  ElMessage.success('已删除')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page-container { animation: fadeIn .3s; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; flex-wrap: wrap; gap: 12px; }
.page-title { font-size: 16px; font-weight: 600; }
.pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>

<template>
  <div class="farmer-detail">
    <div class="section-content">
      <el-breadcrumb separator="/" style="padding:20px 0">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>农户详情</el-breadcrumb-item>
      </el-breadcrumb>

      <div v-loading="loading">
        <div v-if="farmer.id" class="farmer-main">
          <div class="farmer-header">
            <el-avatar :size="80" icon="UserFilled" />
            <div class="farmer-header-info">
              <h1>{{ farmer.farmerName }}</h1>
              <div class="farmer-meta">
                <el-tag :type="auditTag(farmer.auditStatus)">{{ auditText(farmer.auditStatus) }}</el-tag>
                <span>联系人：{{ farmer.contactPerson }}</span>
                <span>电话：{{ farmer.contactPhone }}</span>
              </div>
            </div>
            <div class="farmer-stats">
              <div class="stat-item">
                <div class="stat-num">{{ farmer.level || 1 }}</div>
                <div class="stat-label">等级</div>
              </div>
              <div class="stat-item">
                <div class="stat-num">{{ farmer.score || 0 }}</div>
                <div class="stat-label">评分</div>
              </div>
              <div class="stat-item">
                <div class="stat-num">{{ farmer.salesCount || 0 }}</div>
                <div class="stat-label">销量</div>
              </div>
            </div>
          </div>

          <el-card shadow="never" style="margin-top:20px">
            <template #header>农场信息</template>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="农场名称">{{ farmer.farmerName }}</el-descriptions-item>
              <el-descriptions-item label="联系人">{{ farmer.contactPerson }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ farmer.contactPhone }}</el-descriptions-item>
              <el-descriptions-item label="所在地区">
                {{ farmer.province }}{{ farmer.city }}{{ farmer.district }}
              </el-descriptions-item>
              <el-descriptions-item label="详细地址" :span="2">{{ farmer.address }}</el-descriptions-item>
              <el-descriptions-item label="农场面积">{{ farmer.farmArea }} 亩</el-descriptions-item>
              <el-descriptions-item label="主营产品">{{ farmer.mainProducts }}</el-descriptions-item>
            </el-descriptions>
          </el-card>
        </div>

        <el-empty v-else description="未找到农户信息" :image-size="120" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getFarmerDetail } from '../../api/trace'

const route = useRoute()
const farmer = ref({})
const loading = ref(false)

function auditTag(s) {
  return s === 1 ? 'success' : s === 2 ? 'danger' : 'warning'
}
function auditText(s) {
  return s === 0 ? '待审核' : s === 1 ? '已认证' : '已拒绝'
}

async function loadData() {
  loading.value = true
  try {
    const res = await getFarmerDetail(route.params.id)
    farmer.value = res.data
  } finally { loading.value = false }
}

onMounted(loadData)
</script>

<style scoped>
.farmer-detail { background: #f5f6f7; min-height: 80vh; }
.section-content { max-width: 960px; margin: 0 auto; padding: 0 20px 40px; }
.farmer-header { background: #fff; border-radius: 12px; padding: 30px; display: flex; align-items: center; gap: 24px; }
.farmer-header-info h1 { font-size: 24px; color: #333; margin-bottom: 8px; }
.farmer-meta { display: flex; gap: 16px; align-items: center; font-size: 14px; color: #666; }
.farmer-stats { margin-left: auto; display: flex; gap: 30px; }
.stat-item { text-align: center; }
.stat-num { font-size: 24px; font-weight: 700; color: #1a8c3a; }
.stat-label { font-size: 12px; color: #999; margin-top: 4px; }
</style>

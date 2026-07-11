<template>
  <div class="trace-query">
    <div class="query-hero">
      <div class="hero-content">
        <el-icon :size="60" color="#1a8c3a"><Search /></el-icon>
        <h1>农产品溯源查询</h1>
        <p>扫描二维码或输入批次号，追溯农产品从田间到餐桌的全程信息</p>
        <div class="query-input">
          <el-input v-model="batchNo" placeholder="请输入商品批次号，如 B20250401" size="large" clearable @keyup.enter="onQuery" />
          <el-button type="primary" size="large" @click="onQuery" :loading="queryLoading">
            <el-icon><Search /></el-icon> 立即溯源
          </el-button>
        </div>
        <p style="color:#a5d6a7;font-size:13px;margin-top:12px">
          💡 试试输入 <b style="color:#fff;cursor:pointer;text-decoration:underline" @click="batchNo='B20250401';onQuery()">B20250401</b> 查看有机番茄的完整溯源信息
        </p>
      </div>
    </div>

    <div class="section-content">
      <div class="feature-grid">
        <div class="feature-card">
          <el-icon :size="40" color="#1a8c3a"><Stamp /></el-icon>
          <h4>全程追溯</h4>
          <p>种植、施肥、用药、灌溉、收获全链路记录</p>
        </div>
        <div class="feature-card">
          <el-icon :size="40" color="#1a8c3a"><CircleCheck /></el-icon>
          <h4>权威检测</h4>
          <p>每批次产品均有第三方检测报告，质量安全有保障</p>
        </div>
        <div class="feature-card">
          <el-icon :size="40" color="#1a8c3a"><User /></el-icon>
          <h4>农户直连</h4>
          <p>直接对接认证农户，了解产地环境与种植过程</p>
        </div>
        <div class="feature-card">
          <el-icon :size="40" color="#1a8c3a"><Picture /></el-icon>
          <h4>实景图片</h4>
          <p>田间实拍、检测报告照片，所见即所得</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const batchNo = ref('')
const queryLoading = ref(false)

function onQuery() {
  if (!batchNo.value.trim()) { ElMessage.warning('请输入批次号'); return }
  queryLoading.value = true
  router.push(`/trace/${batchNo.value}`)
}
</script>

<style scoped>
.trace-query { background: #f5f6f7; min-height: 80vh; }
.query-hero { background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 50%, #a5d6a7 100%); padding: 80px 20px; text-align: center; }
.hero-content { max-width: 600px; margin: 0 auto; }
.hero-content h1 { font-size: 32px; color: #2e7d32; margin: 16px 0 8px; }
.hero-content p { font-size: 15px; color: #558b2f; margin-bottom: 30px; }
.query-input { display: flex; gap: 12px; }
.query-input .el-input { flex: 1; }

.section-content { max-width: 1200px; margin: 40px auto; padding: 0 20px; }
.feature-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.feature-card { background: #fff; border-radius: 12px; padding: 30px 20px; text-align: center; box-shadow: 0 2px 8px rgba(0,0,0,.04); transition: transform .3s; }
.feature-card:hover { transform: translateY(-4px); }
.feature-card h4 { margin: 12px 0 8px; font-size: 16px; color: #333; }
.feature-card p { font-size: 13px; color: #999; line-height: 1.6; }

@media (max-width: 768px) { .feature-grid { grid-template-columns: repeat(2, 1fr); } .query-input { flex-direction: column; } }
</style>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">预售动态管理</span>
        <el-button @click="$router.back()">返回</el-button>
      </div>

      <!-- 批次信息 -->
      <el-descriptions :column="3" border v-if="trace" style="margin-bottom:16px">
        <el-descriptions-item label="产品名称">{{ trace.productName }}</el-descriptions-item>
        <el-descriptions-item label="批次号">{{ trace.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="预售订单数">{{ presaleOrderCount }}</el-descriptions-item>
      </el-descriptions>

      <!-- 发送动态 -->
      <el-card shadow="hover" style="margin-bottom:16px">
        <h3 style="margin-bottom:12px">发送生长动态</h3>
        <el-form :model="form" label-width="80px">
          <el-form-item label="生长阶段">
            <el-select v-model="form.stage" style="width:100%">
              <el-option label="播种" value="sow" />
              <el-option label="发芽" value="seedling" />
              <el-option label="开花" value="bloom" />
              <el-option label="结果" value="fruit" />
              <el-option label="成熟" value="ripe" />
            </el-select>
          </el-form-item>
          <el-form-item label="标题"><el-input v-model="form.title" placeholder="如：番茄已开花" /></el-form-item>
          <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="3" placeholder="描述当前生长状态..." /></el-form-item>
          <el-form-item label="图片">
            <div style="display:flex;gap:8px;align-items:center">
              <input type="file" accept="image/*" @change="onFileChange" ref="fileInput" style="display:none" />
              <el-button @click="$refs.fileInput.click()">选择图片</el-button>
              <span v-if="imageFile" style="font-size:13px;color:#52c41a">{{ imageFile.name }}</span>
              <el-button v-if="imageFile" type="success" size="small" @click="uploadImage" :loading="uploading">上传</el-button>
            </div>
            <el-image v-if="form.imageUrl" :src="form.imageUrl" style="width:120px;height:80px;margin-top:8px;border-radius:6px" fit="cover" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="sendGrowth" :loading="sending">发送动态</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 历史动态 -->
      <h3 style="margin-bottom:12px">历史动态</h3>
      <el-timeline v-if="growthList.length">
        <el-timeline-item v-for="g in growthList" :key="g.id" :timestamp="g.createTime" placement="top" :color="stageColor(g.stage)">
          <el-card shadow="hover">
            <h4>{{ g.title }} <el-tag size="small" :color="stageColor(g.stage)" style="color:#fff">{{ stageText(g.stage) }}</el-tag></h4>
            <p style="color:#666;margin-top:6px">{{ g.content }}</p>
            <el-image v-if="g.imageUrls" :src="g.imageUrls" style="width:120px;height:80px;margin-top:8px;border-radius:6px" fit="cover" />
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无动态" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const traceId = route.params.id
const trace = ref(null)
const presaleOrderCount = ref(0)
const growthList = ref([])
const imageFile = ref(null)
const uploading = ref(false)
const sending = ref(false)
const form = reactive({ stage: 'sow', title: '', content: '', imageUrl: '' })

function stageText(s) { return { sow: '播种', seedling: '发芽', bloom: '开花', fruit: '结果', ripe: '成熟' }[s] || s }
function stageColor(s) { return { sow: '#52c41a', seedling: '#73d13d', bloom: '#faad14', fruit: '#fa8c16', ripe: '#f56c6c' }[s] || '#999' }

function onFileChange(e) { if (e.target.files.length) imageFile.value = e.target.files[0] }

async function uploadImage() {
  if (!imageFile.value) return
  uploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', imageFile.value)
    const r = await axios.post('/api/upload/trace-image', fd)
    form.imageUrl = r.data.data?.url || r.data.data || ''
    ElMessage.success('上传成功')
  } catch { ElMessage.error('上传失败') }
  finally { uploading.value = false }
}

async function loadGrowth() {
  try {
    const r = await axios.get(`/api/trace/growth/${traceId}`)
    growthList.value = r.data.data || []
  } catch {}
}

async function sendGrowth() {
  if (!form.title) return ElMessage.warning('请填写标题')
  sending.value = true
  try {
    await axios.post('/api/farmer/presale/growth', { traceId: Number(traceId), ...form })
    ElMessage.success('动态发送成功')
    form.title = ''; form.content = ''; form.imageUrl = ''
    loadGrowth()
  } catch (e) { ElMessage.error(e.response?.data?.msg || '发送失败') }
  finally { sending.value = false }
}

async function loadData() {
  try {
    // 获取溯源批次信息
    const r = await axios.get(`/api/trace/detail/${traceId}`)
    trace.value = r.data.data?.traceability || r.data.data
    // 获取预售订单数
    try {
      const tr = await axios.get('/api/farmer/presale/traces')
      const traces = tr.data.data || []
      const found = traces.find(t => String(t.trace?.id) === String(traceId))
      if (found) presaleOrderCount.value = found.presaleOrderCount || 0
    } catch {}
  } catch {}
  loadGrowth()
}

onMounted(loadData)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}
</style>

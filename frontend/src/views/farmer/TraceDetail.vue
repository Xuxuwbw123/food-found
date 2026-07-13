<template>
  <div class="trace-detail-page">
    <div class="section-content">
      <div class="page-header">
        <h2>溯源管理 — {{ trace.productName || '批次详情' }}</h2>
        <div style="display:flex;gap:8px">
          <el-button type="warning" @click="showQrcode=true">🔐 生成溯源码</el-button>
          <el-button v-if="!hasProduct" type="success" @click="showPublish=true">📦 发布商品</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </div>

      <el-card shadow="never" v-for="section in sections" :key="section.key" style="margin-bottom:16px">
        <template #header>
          <div style="display:flex;justify-content:space-between;align-items:center">
            <div style="display:flex;align-items:center;gap:10px">
              <span>{{ section.title }}</span>
              <el-tag :type="section.roleTag" size="small">{{ section.roleLabel }}</el-tag>
            </div>
            <div style="display:flex;gap:8px">
              <el-button v-if="section.canEdit" type="primary" size="small" @click="openForm(section.key)">添加记录</el-button>
              <el-button v-if="section.canEdit" size="small" @click="openSectionImage(section.key)">📷 上传图片</el-button>
            </div>
          </div>
        </template>
        <div v-if="section.images?.length" style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:10px">
          <el-image v-for="img in section.images" :key="img.id" :src="img.imageUrl" style="width:80px;height:60px;border-radius:4px" fit="cover" :preview-src-list="[img.imageUrl]" />
        </div>
        <el-table :data="section.data" border stripe size="small" style="width:100%" v-if="section.data.length">
          <el-table-column v-for="col in section.cols" :key="col.prop" :prop="col.prop" :label="col.label" :width="col.width" />
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-popconfirm title="删除？" @confirm="delRecord(section.key, row.id)">
                <template #ref><el-button link type="danger" size="small">删除</el-button></template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无记录" :image-size="40" />
      </el-card>
    </div>

    <el-dialog :title="'上传图片 - ' + (sectionLabel)" v-model="showImageUpload" width="480px">
      <div style="display:flex;gap:8px;align-items:center;margin-bottom:12px;flex-wrap:wrap">
        <input type="file" accept="image/*" multiple @change="onSectionFilesSelected" ref="sectionFileInput" style="display:none" />
        <el-button type="primary" @click="$refs.sectionFileInput.click()">选择图片（可多选）</el-button>
        <span v-if="sectionFiles.length" style="font-size:13px;color:#666">{{ sectionFiles.length }} 张已选择</span>
        <el-button type="success" v-if="sectionFiles.length" @click="uploadSectionImages" :loading="imgUploading">上传全部</el-button>
      </div>
      <div style="display:flex;flex-wrap:wrap;gap:8px;margin-top:12px">
        <div v-for="img in sectionImages" :key="img.id" style="position:relative">
          <el-image :src="img.imageUrl" style="width:120px;height:100px;border-radius:6px" fit="cover" :preview-src-list="[img.imageUrl]" />
          <el-button type="danger" :icon="Delete" circle size="small" style="position:absolute;top:-6px;right:-6px" @click="delSectionImage(img.id)" />
        </div>
      </div>
      <el-empty v-if="sectionImages.length===0" description="暂无图片" :image-size="40" />
    </el-dialog>

    <el-dialog :title="formTitle" v-model="showForm" width="550px">
      <el-form :model="form" ref="formRef" label-width="100px">
        <template v-for="f in currentFields" :key="f.key">
          <el-form-item :label="f.label">
            <el-input v-if="f.type==='text'" v-model="form[f.key]" :placeholder="f.placeholder" />
            <el-input v-else-if="f.type==='number'" v-model.number="form[f.key]" :placeholder="f.placeholder" type="number" />
            <el-input v-else-if="f.type==='textarea'" v-model="form[f.key]" type="textarea" :rows="2" :placeholder="f.placeholder" />
            <el-date-picker v-else-if="f.type==='date'" v-model="form[f.key]" type="date" value-format="YYYY-MM-DD" style="width:100%" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" @click="saveRecord" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 发布商品对话框 -->
    <el-dialog title="发布商品到商城" v-model="showPublish" width="550px">
      <el-form :model="pubForm" ref="pubFormRef" label-width="90px">
        <el-form-item label="商品名称"><el-input :model-value="trace.productName" disabled /></el-form-item>
        <el-form-item label="商品主图" prop="mainImage"><el-input v-model="pubForm.mainImage" placeholder="图片URL" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="售价"><el-input-number v-model="pubForm.price" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="原价"><el-input-number v-model="pubForm.originalPrice" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="库存"><el-input-number v-model="pubForm.stock" :min="1" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="单位"><el-input v-model="pubForm.unit" placeholder="kg/箱/袋" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="商品描述"><el-input v-model="pubForm.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="规格"><el-input v-model="pubForm.weight" placeholder="如：2.5kg" /></el-form-item>
        <el-form-item label="产地"><el-input v-model="pubForm.originPlace" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPublish=false">取消</el-button>
        <el-button type="success" @click="publishProduct" :loading="publishing">提交审核</el-button>
      </template>
    </el-dialog>

    <!-- 生成溯源码对话框 -->
    <el-dialog title="批量生成溯源码" v-model="showQrcode" width="500px">
      <el-form label-width="80px">
        <el-form-item label="溯源批次">
          <el-input :model-value="trace.productName + ' (' + trace.batchNo + ')'" disabled />
        </el-form-item>
        <el-form-item label="生成数量">
          <el-input-number v-model="qrcodeCount" :min="1" :max="500" style="width:100%" />
        </el-form-item>
      </el-form>
      <div v-if="qrcodes.length" style="margin-top:16px">
        <el-alert :title="'已生成 ' + qrcodes.length + ' 个溯源码'" type="success" show-icon :closable="false" style="margin-bottom:12px" />
        <el-button type="primary" @click="downloadPdf" :loading="downloading">下载 PDF（含二维码）</el-button>
        <el-button @click="downloadCsv">下载 CSV</el-button>
      </div>
      <template #footer>
        <el-button @click="showQrcode=false">关闭</el-button>
        <el-button type="warning" @click="generateQrcodes" :loading="generating" v-if="!qrcodes.length">生成</el-button>
      </template>
    </el-dialog>

    <!-- 发布预售商品对话框 -->
    <el-dialog title="发布预售商品" v-model="showPresalePublish" width="550px">
      <el-form :model="presaleForm" label-width="100px">
        <el-form-item label="商品名称"><el-input :model-value="trace.productName" disabled /></el-form-item>
        <el-form-item label="商品主图"><el-input v-model="presaleForm.mainImage" placeholder="图片URL" /></el-form-item>
        <el-form-item label="预售价格"><el-input-number v-model="presaleForm.price" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="原价"><el-input-number v-model="presaleForm.originalPrice" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="预计种植日期"><el-date-picker v-model="presaleForm.presaleStart" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="预计成熟日期"><el-date-picker v-model="presaleForm.presaleEnd" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="预售库存"><el-input-number v-model="presaleForm.stock" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="presaleForm.unit" placeholder="箱/份" /></el-form-item>
        <el-form-item label="商品描述"><el-input v-model="presaleForm.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPresalePublish=false">取消</el-button>
        <el-button type="success" @click="publishPresale" :loading="presalePublishing">提交预售审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const traceId = route.params.id
const trace = ref({})
const hasProduct = ref(false)
const saving = ref(false)
const showForm = ref(false)
const showImageUpload = ref(false)
const currentSection = ref('')
const sectionLabel = ref('')
const sectionImages = ref([])
const sectionFiles = ref([])
const imgUploading = ref(false)
const form = reactive({})
const formRef = ref(null)

// 溯源码
const showQrcode = ref(false)
const qrcodeCount = ref(10)
const qrcodes = ref([])
const generating = ref(false)
const downloading = ref(false)

async function generateQrcodes() {
  generating.value = true
  try {
    // 获取管理员配置的服务器地址
    let baseUrl = window.location.origin
    try {
      const cfg = await axios.get('/api/trace/qrcode/config')
      baseUrl = cfg.data.data.baseUrl
    } catch {}

    const r = await axios.post('/api/trace/qrcode/batch-generate', {
      traceId: Number(traceId),
      count: qrcodeCount.value,
      baseUrl: baseUrl
    })
    qrcodes.value = r.data.data.codes
    ElMessage.success(`成功生成 ${qrcodes.value.length} 个溯源码`)
  } catch (e) { ElMessage.error(e.response?.data?.msg || '生成失败') }
  finally { generating.value = false }
}

function downloadCsv() {
  const header = '溯源码,验证链接\n'
  const rows = qrcodes.value.map(c => `${c.content},http://localhost:8088/qrcode-scan/${c.content}`).join('\n')
  const blob = new Blob(['﻿' + header + rows], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a'); a.href = url; a.download = `溯源码_${trace.value.batchNo}.csv`; a.click()
  URL.revokeObjectURL(url)
}

async function downloadPdf() {
  downloading.value = true
  try {
    // Dynamically load QRCode and jsPDF libraries
    await loadScript('https://cdn.jsdelivr.net/npm/qrcode/build/qrcode.min.js')
    await loadScript('https://cdn.jsdelivr.net/npm/jspdf@2.5.1/dist/jspdf.umd.min.js')

    const { jsPDF } = window.jspdf
    const doc = new jsPDF('p', 'mm', 'a4')
    const pageWidth = 210, pageHeight = 297
    const cols = 3, rows = 8
    const cellW = 60, cellH = 32, marginX = 15, marginY = 20

    doc.setFontSize(16)
    doc.text(`溯源码 - ${trace.value.productName} (${trace.value.batchNo})`, pageWidth / 2, 12, { align: 'center' })

    for (let i = 0; i < qrcodes.value.length; i++) {
      const pageIdx = Math.floor(i / (cols * rows))
      const idx = i % (cols * rows)
      const col = idx % cols
      const row = Math.floor(idx / cols)

      if (idx === 0 && i > 0) doc.addPage()

      const x = marginX + col * cellW
      const y = marginY + row * cellH

      // Generate QR code as data URL - encode the full URL so scanning opens browser
      const qrDataUrl = await new Promise(resolve => {
        window.QRCode.toDataURL(qrcodes.value[i].url, { width: 200, margin: 1 }, (err, url) => resolve(url))
      })

      doc.addImage(qrDataUrl, 'PNG', x + 2, y, 22, 22)
      doc.setFontSize(7)
      doc.text(qrcodes.value[i].content.substring(0, 28), x + 25, y + 6)
      doc.setFontSize(6)
      doc.text(`扫码验证溯源`, x + 25, y + 12)
      doc.setFontSize(5)
      doc.text(`#${i + 1}`, x + 25, y + 18)
    }

    doc.save(`溯源码_${trace.value.batchNo}.pdf`)
    ElMessage.success('PDF下载成功')
  } catch (e) { ElMessage.error('PDF生成失败: ' + e.message) }
  finally { downloading.value = false }
}

function loadScript(src) {
  return new Promise((resolve, reject) => {
    if (document.querySelector(`script[src="${src}"]`)) { resolve(); return }
    const s = document.createElement('script'); s.src = src; s.onload = resolve; s.onerror = reject
    document.head.appendChild(s)
  })
}

// 发布商品
const showPublish = ref(false)
const publishing = ref(false)

// 发布预售
const showPresalePublish = ref(false)
const presalePublishing = ref(false)
const presaleForm = reactive({
  mainImage: '', price: 0, originalPrice: 0,
  presaleStart: '', presaleEnd: '',
  stock: 100, unit: '箱', description: ''
})

async function publishPresale() {
  if (!presaleForm.price || !presaleForm.presaleStart || !presaleForm.presaleEnd) {
    ElMessage.warning('请填写价格和预售时间'); return
  }
  presalePublishing.value = true
  try {
    await axios.post('/api/admin/product/publish', {
      productName: trace.value.productName,
      farmerId: trace.value.farmerId,
      traceId: traceId,
      categoryId: trace.value.categoryId || 12,
      productNo: 'PP' + Date.now(),
      isPresale: 1,
      ...presaleForm
    })
    ElMessage.success('预售商品已提交审核')
    showPresalePublish.value = false
  } catch { ElMessage.error('提交失败') }
  finally { presalePublishing.value = false }
}
const pubForm = reactive({ mainImage: '', price: 0, originalPrice: 0, stock: 100, unit: 'kg', description: '', weight: '', originPlace: '' })
const pubFormRef = ref(null)

async function publishProduct() {
  publishing.value = true
  try {
    await axios.post('/api/admin/product/publish', {
      productName: trace.value.productName,
      farmerId: trace.value.farmerId || trace.value.farmerId,
      traceId: traceId,
      categoryId: trace.value.categoryId || 12,
      productNo: 'P' + Date.now(),
      ...pubForm
    })
    ElMessage.success('商品已提交审核，等待管理员审核通过后上架')
    showPublish.value = false
  } catch { ElMessage.error('提交失败') }
  finally { publishing.value = false }
}

function openSectionImage(key) {
  currentSection.value = key
  const cfg = sectionConfig[key]
  sectionLabel.value = cfg ? cfg.title : ''
  loadSectionImages()
  showImageUpload.value = true
}
async function loadSectionImages() {
  try {
    const res = await axios.get(`/api/trace/image/list-by-trace/${traceId}`)
    const all = res.data.data || []
    const target = currentSection.value
    sectionImages.value = all.filter(i => (i.imageType || '').toLowerCase() === target)
  } catch { sectionImages.value = [] }
}
function onSectionFilesSelected(e) { sectionFiles.value = Array.from(e.target.files || []) }
async function uploadSectionImages() {
  if (!sectionFiles.value.length) return
  imgUploading.value = true
  try {
    for (const f of sectionFiles.value) {
      const fd = new FormData(); fd.append('file', f); fd.append('traceId', traceId); fd.append('imageType', currentSection.value)
      await axios.post('/api/upload/trace-image', fd)
    }
    ElMessage.success(`上传 ${sectionFiles.value.length} 张成功`)
    sectionFiles.value = []
    const inp = document.querySelector('#sectionFileInput'); if (inp) inp.value = ''
    loadSectionImages()
  } catch { ElMessage.error('上传失败') }
  finally { imgUploading.value = false }
}
async function delSectionImage(id) {
  await axios.delete(`/api/trace/image/delete/${id}`)
  loadSectionImages()
}

const sectionConfig = {
  planting:    { title: '种植记录', api: 'planting', type: 'single', roleLabel: '👨‍🌾 农户', roleTag: 'success', canEdit: true,  cols: [{ prop: 'plantingDate', label: '种植日期', width: 120 }, { prop: 'seedVariety', label: '品种', width: 100 }, { prop: 'plantingMethod', label: '种植方式', width: 120 }, { prop: 'soilType', label: '土壤', width: 80 }, { prop: 'operator', label: '操作人', width: 80 }, { prop: 'description', label: '说明' }] },
  fertilizer:  { title: '施肥记录', api: 'fertilizer', type: 'list', roleLabel: '👨‍🌾 农户', roleTag: 'success', canEdit: true,  cols: [{ prop: 'fertilizeDate', label: '日期', width: 120 }, { prop: 'fertilizerName', label: '肥料名称', width: 120 }, { prop: 'dosage', label: '用量(kg)', width: 80 }, { prop: 'fertilizeMethod', label: '方式', width: 80 }, { prop: 'operator', label: '操作人', width: 80 }, { prop: 'description', label: '备注' }] },
  pesticide:   { title: '农药使用', api: 'pesticide', type: 'list', roleLabel: '👨‍🌾 农户', roleTag: 'success', canEdit: true,  cols: [{ prop: 'useDate', label: '日期', width: 120 }, { prop: 'pesticideName', label: '农药名称', width: 140 }, { prop: 'dosage', label: '用量(g)', width: 80 }, { prop: 'safetyInterval', label: '安全间隔(天)', width: 100 }, { prop: 'operator', label: '操作人', width: 80 }, { prop: 'description', label: '备注' }] },
  irrigation:  { title: '灌溉记录', api: 'irrigation', type: 'list', roleLabel: '👨‍🌾 农户', roleTag: 'success', canEdit: true,  cols: [{ prop: 'irrigationDate', label: '日期', width: 120 }, { prop: 'waterSource', label: '水源', width: 100 }, { prop: 'waterVolume', label: '用量(m³)', width: 80 }, { prop: 'irrigationType', label: '方式', width: 80 }, { prop: 'duration', label: '时长(分)', width: 80 }, { prop: 'operator', label: '操作人', width: 80 }, { prop: 'description', label: '备注' }] },
  harvest:     { title: '收获记录', api: 'harvest', type: 'single', roleLabel: '👨‍🌾 农户', roleTag: 'success', canEdit: true,  cols: [{ prop: 'harvestDate', label: '收获日期', width: 160 }, { prop: 'harvestQuantity', label: '数量', width: 80 }, { prop: 'harvestUnit', label: '单位', width: 60 }, { prop: 'harvestMethod', label: '方式', width: 100 }, { prop: 'operator', label: '操作人', width: 80 }, { prop: 'description', label: '备注' }] },
}

const sections = reactive(Object.entries(sectionConfig).map(([key, cfg]) => ({ key, title: cfg.title, cols: cfg.cols, roleLabel: cfg.roleLabel, roleTag: cfg.roleTag, canEdit: cfg.canEdit, data: [], images: [] })))

const fieldDefs = {
  planting: [
    { key: 'plantingDate', label: '种植日期', type: 'date' },
    { key: 'seedVariety', label: '品种', type: 'text', placeholder: '如：浙粉208' },
    { key: 'plantingMethod', label: '种植方式', type: 'text', placeholder: '如：大棚育苗移栽' },
    { key: 'soilType', label: '土壤类型', type: 'text', placeholder: '如：沙壤土' },
    { key: 'description', label: '说明', type: 'textarea' },
    { key: 'operator', label: '操作人', type: 'text' },
  ],
  fertilizer: [
    { key: 'fertilizeDate', label: '施肥日期', type: 'date' },
    { key: 'fertilizerName', label: '肥料名称', type: 'text', placeholder: '如：有机堆肥' },
    { key: 'dosage', label: '用量(kg)', type: 'number' },
    { key: 'fertilizeMethod', label: '施肥方式', type: 'text', placeholder: '如：沟施、滴灌' },
    { key: 'description', label: '备注', type: 'textarea' },
    { key: 'operator', label: '操作人', type: 'text' },
  ],
  pesticide: [
    { key: 'useDate', label: '施用日期', type: 'date' },
    { key: 'pesticideName', label: '农药名称', type: 'text', placeholder: '如：苏云金杆菌' },
    { key: 'dosage', label: '用量(g)', type: 'number' },
    { key: 'safetyInterval', label: '安全间隔(天)', type: 'number' },
    { key: 'description', label: '备注', type: 'textarea' },
    { key: 'operator', label: '操作人', type: 'text' },
  ],
  irrigation: [
    { key: 'irrigationDate', label: '灌溉日期', type: 'date' },
    { key: 'waterSource', label: '水源', type: 'text', placeholder: '如：地下水' },
    { key: 'waterVolume', label: '用量(m³)', type: 'number' },
    { key: 'irrigationType', label: '灌溉方式', type: 'text', placeholder: '如：滴灌' },
    { key: 'duration', label: '时长(分钟)', type: 'number' },
    { key: 'description', label: '备注', type: 'textarea' },
    { key: 'operator', label: '操作人', type: 'text' },
  ],
  harvest: [
    { key: 'harvestDate', label: '收获日期', type: 'date' },
    { key: 'harvestQuantity', label: '数量', type: 'number' },
    { key: 'harvestUnit', label: '单位', type: 'text', placeholder: 'kg' },
    { key: 'harvestMethod', label: '收获方式', type: 'text', placeholder: '如：人工采摘' },
    { key: 'description', label: '备注', type: 'textarea' },
    { key: 'operator', label: '操作人', type: 'text' },
  ],
}

const currentFields = ref([])
const formTitle = ref('')
let currentKey = ''

function openForm(key) {
  currentKey = key
  formTitle.value = `添加${sectionConfig[key].title}`
  currentFields.value = fieldDefs[key] || []
  Object.keys(form).forEach(k => delete form[k])
  if (key === 'planting') form.plantingDate = new Date().toISOString().slice(0, 10)
  showForm.value = true
}

async function saveRecord() {
  saving.value = true
  try {
    const cfg = sectionConfig[currentKey]
    const api = cfg.api
    const data = { ...form, traceId }
    if (cfg.type === 'single') {
      await axios.post(`/api/trace/${api}/save`, data)
    } else {
      await axios.post(`/api/trace/${api}/add`, data)
    }
    ElMessage.success('保存成功')
    showForm.value = false
    loadSection(currentKey)
  } finally { saving.value = false }
}

async function delRecord(key, id) {
  await axios.delete(`/api/trace/${key}/delete/${id}`)
  ElMessage.success('已删除')
  loadSection(key)
}

async function loadSection(key) {
  const cfg = sectionConfig[key]
  try {
    const isPlanting = key === 'planting'
    const url = cfg.type === 'single'
      ? (isPlanting ? `/api/trace/${cfg.api}/${traceId}` : `/api/trace/${cfg.api}/get-by-trace/${traceId}`)
      : `/api/trace/${cfg.api}/list/${traceId}`
    const res = await axios.get(url)
    const data = cfg.type === 'single' ? (res.data.data ? [res.data.data] : []) : (res.data.data || [])
    const sec = sections.find(s => s.key === key)
    if (sec) { sec.data = data; loadSectionImagesForKey(key, sec) }
  } catch { /* ok */ }
}

async function loadSectionImagesForKey(key, sec) {
  try {
    const res = await axios.get(`/api/trace/image/list-by-trace/${traceId}`)
    const all = res.data.data || []
    if (sec) sec.images = all.filter(i => (i.imageType || '').toLowerCase() === key)
  } catch {}
}

async function loadAll() {
  for (const key of Object.keys(sectionConfig)) { await loadSection(key) }
}

onMounted(async () => {
  try {
    const res = await axios.get(`/api/trace/detail/${traceId}`)
    trace.value = res.data.data?.traceability || {}
  } catch {}
  // 检查是否已有商品
  try {
    const pr = await axios.get(`/api/trace/by-product/${trace.value.productId || 0}`)
    hasProduct.value = !!(pr.data.data?.batchNo)
  } catch { hasProduct.value = false }
  loadAll()
})
</script>

<style scoped>
.trace-detail-page { min-height: 70vh; background: #f5f6f7; padding-bottom: 40px; }
.section-content { max-width: 1100px; margin: 0 auto; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; }
.page-header h2 { font-size: 20px; }
</style>

<template>
  <div class="farmer-dashboard">
    <div class="section-content">
      <div class="page-header">
        <h2>农户工作台</h2>
        <div>
          <el-button @click="$router.push('/')">返回商城</el-button>
          <el-button type="danger" @click="handleLogout">退出</el-button>
        </div>
      </div>

      <div v-if="!farmer.id" v-loading="loading">
        <el-empty v-if="!loading && !farmer.id" description="您还未完成农户认证，请先申请认证">
          <el-button type="primary" @click="showApply = true">申请农户认证</el-button>
        </el-empty>
      </div>

      <div v-else-if="farmer.auditStatus === 0 || farmer.auditStatus === 0" v-loading="loading">
        <el-result icon="warning" title="认证审核中" sub-title="您的农户认证申请已提交，请耐心等待管理员审核">
          <template #extra>
            <el-tag type="warning">预计1-3个工作日</el-tag>
          </template>
        </el-result>
      </div>

      <div v-else-if="farmer.auditStatus === 2 || farmer.auditStatus === 2">
        <el-result icon="error" title="认证申请被驳回" :sub-title="farmer.auditRemark || farmer.auditRemark || '请联系管理员了解详情'">
          <template #extra>
            <el-button type="primary" @click="showApply=true">重新申请</el-button>
          </template>
        </el-result>
      </div>

      <div v-else>
        <el-row :gutter="20" class="stat-row">
          <el-col :span="8">
            <el-card shadow="hover"><div class="stat-num">{{ traceList.length }}</div><div class="stat-label">溯源批次</div></el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover"><div class="stat-num">{{ farmer.score || 0 }}</div><div class="stat-label">信用评分</div></el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover"><div class="stat-num">{{ farmer.salesCount || 0 }}</div><div class="stat-label">累计销量</div></el-card>
          </el-col>
        </el-row>

        <el-card shadow="never" style="margin-top:20px">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span>我的溯源批次</span>
              <el-button type="primary" size="small" @click="showAddTrace = true">新增批次</el-button>
            </div>
          </template>
          <el-table :data="traceList" border stripe v-loading="loading" style="width:100%">
            <el-table-column prop="traceCode" label="溯源码" width="160" />
            <el-table-column prop="batchNo" label="批次号" width="140" />
            <el-table-column prop="productName" label="产品名称" width="140" />
            <el-table-column prop="originPlace" label="产地" width="160" />
            <el-table-column prop="scanCount" label="扫码次数" width="100" />
            <el-table-column prop="currentStage" label="当前阶段" width="100" />
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="openTrace(row)">管理</el-button>
                <el-button link type="primary" size="small" @click="openImages(row)">图片</el-button>
                <el-popconfirm title="申请删除？需管理员审核" @confirm="reqDelTrace(row.id)">
                  <template #ref><el-button link type="danger" size="small">申请删除</el-button></template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 订单管理 -->
        <el-card shadow="never" style="margin-top:20px">
          <template #header><div style="display:flex;justify-content:space-between;align-items:center"><span>我的订单</span><el-select v-model="orderStatus" placeholder="全部状态" clearable size="small" style="width:120px" @change="loadOrders"><el-option label="待付款" :value="0" /><el-option label="待发货" :value="1" /><el-option label="已发货" :value="2" /><el-option label="已完成" :value="3" /></el-select></div></template>
          <el-table :data="orderList" border stripe v-loading="orderLoading" style="width:100%">
            <el-table-column prop="orderNo" label="订单号" width="160" />
            <el-table-column label="金额" width="100"><template #default="{row}">¥{{row.payAmount||row.totalAmount}}</template></el-table-column>
            <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="orderStatusTag(row.orderStatus)" size="small">{{orderStatusText(row.orderStatus)}}</el-tag></template></el-table-column>
            <el-table-column prop="createTime" label="下单时间" width="160" />
            <el-table-column label="操作" width="200">
              <template #default="{row}">
                <el-button link type="primary" size="small" @click="$router.push(`/farmer/order/${row.id}`)">详情</el-button>
                <el-button v-if="row.orderStatus===1" link type="success" size="small" @click="openDeliver(row)">发货</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="orderList.length===0&&!orderLoading" description="暂无订单" :image-size="40" />
        </el-card>
      </div>
    </div>

    <!-- 发货弹窗 -->
    <el-dialog v-model="deliverVisible" title="录入物流信息" width="400px">
      <el-form :model="deliverForm" label-width="80px">
        <el-form-item label="物流公司"><el-input v-model="deliverForm.logisticsCompany" placeholder="如：顺丰速运" /></el-form-item>
        <el-form-item label="物流单号"><el-input v-model="deliverForm.logisticsNo" placeholder="请输入物流单号" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="deliverVisible=false">取消</el-button><el-button type="primary" @click="doDeliver" :loading="deliverLoading">确认发货</el-button></template>
    </el-dialog>

    <!-- 新增批次 + 发布商品（一步到位） -->
    <el-dialog title="新增溯源批次" v-model="showAddTrace" width="600px">
      <el-tabs v-model="traceTab">
        <el-tab-pane label="溯源信息" name="trace">
          <el-form :model="traceForm" label-width="80px">
            <el-form-item label="产品名称" prop="productName"><el-input v-model="traceForm.productName" /></el-form-item>
            <el-form-item label="批次号" prop="batchNo"><el-input v-model="traceForm.batchNo" placeholder="如 B20250701" /></el-form-item>
            <el-form-item label="产地" prop="originPlace"><el-input v-model="traceForm.originPlace" /></el-form-item>
            <el-form-item label="农场名称"><el-input v-model="traceForm.farmName" /></el-form-item>
            <el-form-item label="负责人"><el-input v-model="traceForm.responsiblePerson" /></el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="产品信息" name="product">
          <el-form :model="productForm" label-width="80px">
            <el-form-item label="商品主图">
              <div style="display:flex;gap:8px;align-items:center;flex-wrap:wrap">
                <input type="file" accept="image/*" @change="onProductImageSelected" ref="productImgInput" style="display:none" />
                <el-button @click="$refs.productImgInput.click()">选择图片</el-button>
                <span v-if="productImgFile" style="font-size:13px;color:#1a8c3a">{{ productImgFile.name }}</span>
                <el-button v-if="productImgFile" type="success" size="small" @click="uploadProductImage" :loading="imgUploading2">上传</el-button>
              </div>
              <el-image v-if="productForm.mainImage" :src="productForm.mainImage" style="width:120px;height:100px;border-radius:6px;margin-top:8px" fit="cover" />
            </el-form-item>
            <el-row :gutter="16">
              <el-col :span="12"><el-form-item label="售价"><el-input-number v-model="productForm.price" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="原价"><el-input-number v-model="productForm.originalPrice" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12"><el-form-item label="库存"><el-input-number v-model="productForm.stock" :min="1" style="width:100%" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="单位"><el-input v-model="productForm.unit" placeholder="kg/箱" /></el-form-item></el-col>
            </el-row>
            <el-form-item label="规格"><el-input v-model="productForm.weight" placeholder="如：2.5kg" /></el-form-item>
            <el-form-item label="描述"><el-input v-model="productForm.description" type="textarea" :rows="2" /></el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="showAddTrace = false">取消</el-button>
        <el-button type="primary" @click="addTrace" :loading="saving">创建并提交审核</el-button>
      </template>
    </el-dialog>

    <!-- 图片管理对话框 -->
    <el-dialog title="溯源图片管理" v-model="showImages" width="600px">
      <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center">
        <input type="file" accept="image/*" @change="onFileSelected" ref="fileInput" style="display:none" />
        <el-button type="primary" @click="$refs.fileInput.click()">选择图片</el-button>
        <span v-if="selectedFile" style="font-size:13px;color:#666">{{ selectedFile.name }}</span>
        <el-button type="success" v-if="selectedFile" @click="uploadImage" :loading="uploading">开始上传</el-button>
      </div>
      <div style="display:flex;flex-wrap:wrap;gap:8px">
        <div v-for="img in imageList" :key="img.id" style="position:relative">
          <el-image :src="img.imageUrl" style="width:120px;height:100px;border-radius:6px" fit="cover" />
          <el-button type="danger" :icon="Delete" circle size="small" style="position:absolute;top:-6px;right:-6px" @click="delImage(img.id)" />
        </div>
      </div>
      <el-empty v-if="imageList.length === 0" description="暂无图片" :image-size="40" />
    </el-dialog>

    <!-- 认证申请 -->
    <el-dialog title="农户认证申请" v-model="showApply" width="500px">
      <el-form :model="applyForm" ref="applyFormRef" label-width="80px">
        <el-form-item label="农场名称" prop="farmerName"><el-input v-model="applyForm.farmerName" /></el-form-item>
        <el-form-item label="联系人" prop="contactPerson"><el-input v-model="applyForm.contactPerson" /></el-form-item>
        <el-form-item label="联系电话" prop="contactPhone"><el-input v-model="applyForm.contactPhone" /></el-form-item>
        <el-form-item label="省"><el-input v-model="applyForm.province" /></el-form-item>
        <el-form-item label="市"><el-input v-model="applyForm.city" /></el-form-item>
        <el-form-item label="区"><el-input v-model="applyForm.district" /></el-form-item>
        <el-form-item label="详细地址"><el-input v-model="applyForm.address" /></el-form-item>
        <el-form-item label="农场面积(亩)"><el-input-number v-model="applyForm.farmArea" :min="1" /></el-form-item>
        <el-form-item label="主营产品"><el-input v-model="applyForm.mainProducts" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showApply = false">取消</el-button>
        <el-button type="primary" @click="applyFarmer" :loading="saving">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'
import axios from 'axios'

const router = useRouter()
const auth = useAuthStore()
auth.restoreSession()

const farmer = ref({})
const traceList = ref([])
const loading = ref(false)
const saving = ref(false)
const showAddTrace = ref(false)
const showImages = ref(false)
const showApply = ref(false)
const currentTraceId = ref(null)
const imageList = ref([])
const selectedFile = ref(null)
const uploading = ref(false)
const fileInput = ref(null)

const traceForm = reactive({ productName: '', batchNo: '', originPlace: '', farmName: '', responsiblePerson: '' })
const productForm = reactive({ mainImage: '', price: 0, originalPrice: 0, stock: 100, unit: 'kg', weight: '', description: '' })
const productImgFile = ref(null)
const imgUploading2 = ref(false)
const traceTab = ref('trace')

function onProductImageSelected(e) { if (e.target.files.length > 0) productImgFile.value = e.target.files[0] }
async function uploadProductImage() {
  if (!productImgFile.value) return
  imgUploading2.value = true
  try {
    const fd = new FormData(); fd.append('file', productImgFile.value)
    const res = await axios.post('/api/upload/product-image', fd)
    productForm.mainImage = res.data.data.imageUrl
    ElMessage.success('主图上传成功')
    productImgFile.value = null
  } catch { ElMessage.error('上传失败') }
  finally { imgUploading2.value = false }
}
const applyForm = reactive({ farmerName: '', contactPerson: '', contactPhone: '', province: '', city: '', district: '', address: '', farmArea: 50, mainProducts: '' })

async function loadFarmer() {
  if (!auth.isLoggedIn) return router.push('/login')
  loading.value = true
  try {
    const res = await axios.get('/api/farmer/list')
    const allFarmers = res.data.data?.records || res.data.data || []
    farmer.value = allFarmers.find(f => String(f.userId) === String(auth.user.id)) || {}
    console.log('[farmer] userId:', auth.user?.id, 'farmer:', farmer.value)
  } catch (e) { console.error('[farmer] load error:', e) }
  finally { loading.value = false }
}

async function loadTraces() {
  if (!farmer.value.id) return
  try {
    const res = await axios.get('/api/trace/list', { params: { pageNum: 1, pageSize: 100 } })
    traceList.value = (res.data.data?.records || res.data.data || []).filter(t => String(t.farmerId) === String(farmer.value.id))
  } catch {}
}

async function addTrace() {
  saving.value = true
  try {
    // 1. 创建溯源批次
    // Bug #5 fix: 用户填写的 responsiblePerson 优先生效
    const traceRes = await axios.post('/api/trace/create', {
      farmerId: farmer.value.id,
      farmName: farmer.value.farmName || farmer.value.farmerName,
      responsiblePerson: farmer.value.contactPerson,
      ...traceForm,
      responsiblePerson: (traceForm.responsiblePerson && traceForm.responsiblePerson.trim())
        ? traceForm.responsiblePerson.trim()
        : farmer.value.contactPerson
    })
    const traceId = traceRes.data.data?.id || traceRes.data
    // 2. 同时创建产品（待审核）
    if (productForm.price > 0) await axios.post('/api/admin/product/publish', {
      productName: traceForm.productName,
      farmerId: farmer.value.id,
      traceId: traceId,
      categoryId: productForm.categoryId || 8000000000000001,  // Bug #6 fix
      productNo: 'P' + Date.now(),
      originPlace: traceForm.originPlace,
      ...productForm
    })
    ElMessage.success('批次+产品已创建，等待管理员审核')
    showAddTrace.value = false
    loadTraces()
  } catch (e) { ElMessage.error('创建失败: ' + (e.response?.data?.message || '')) }
  finally { saving.value = false }
}

async function reqDelTrace(id) {
  try {
    await axios.put(`/api/trace/request-delete/${id}`)
    ElMessage.success('删除申请已提交，等待管理员审核')
    loadTraces()
  } catch { ElMessage.error('操作失败') }
}

function openTrace(row) { currentTraceId.value = row.id; router.push(`/farmer/trace/${row.id}`) }
function openImages(row) { currentTraceId.value = row.id; loadImages(); showImages.value = true }

async function loadImages() {
  try { const res = await axios.get(`/api/trace/image/list-by-trace/${currentTraceId.value}`); imageList.value = res.data.data || [] } catch {}
}
function onFileSelected(e) {
  if (e.target.files.length > 0) selectedFile.value = e.target.files[0]
}
async function uploadImage() {
  if (!selectedFile.value) return
  uploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', selectedFile.value)
    fd.append('traceId', currentTraceId.value)
    fd.append('imageType', 'general')
    await axios.post('/api/upload/trace-image', fd)
    ElMessage.success('上传成功')
    selectedFile.value = null
    if (fileInput.value) fileInput.value.value = ''
    loadImages()
  } catch { ElMessage.error('上传失败') }
  finally { uploading.value = false }
}
async function delImage(id) {
  await axios.delete(`/api/trace/image/delete/${id}`)
  loadImages()
}

async function applyFarmer() {
  if (!applyForm.farmerName || !applyForm.contactPerson || !applyForm.contactPhone) {
    ElMessage.warning('请填写农场名称、联系人和联系电话')
    return
  }
  saving.value = true
  try {
    await axios.post('/api/farmer/apply', { userId: auth.user.id, ...applyForm })
    ElMessage.success('认证申请已提交，等待审核')
    showApply.value = false
    loadFarmer()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '提交失败，请重试')
  } finally { saving.value = false }
}

function handleLogout() { auth.logout(); router.push('/') }

// ===== 订单管理 =====
const orderList = ref([])
const orderLoading = ref(false)
const orderStatus = ref(null)
const deliverVisible = ref(false)
const deliverLoading = ref(false)
const deliverForm = reactive({ orderId: null, logisticsCompany: '', logisticsNo: '' })

function orderStatusTag(s) { return {0:'warning',1:'primary',2:'',3:'success',4:'info',5:'danger'}[s]||'' }
function orderStatusText(s) { return {0:'待付款',1:'待发货',2:'已发货',3:'已完成',4:'已取消',5:'售后中'}[s]||'未知' }

async function loadOrders() {
  orderLoading.value = true
  try {
    const params = {}
    if (orderStatus.value !== null && orderStatus.value !== '') params.status = orderStatus.value
    const res = await axios.get('/api/farmer/order/list', { params })
    orderList.value = res.data.data?.records || res.data.data || []
  } finally { orderLoading.value = false }
}

function openDeliver(row) {
  deliverForm.orderId = row.id
  deliverForm.logisticsCompany = ''
  deliverForm.logisticsNo = ''
  deliverVisible.value = true
}

async function doDeliver() {
  if (!deliverForm.logisticsNo) { ElMessage.warning('请输入物流单号'); return }
  deliverLoading.value = true
  try {
    await axios.put(`/api/farmer/order/deliver/${deliverForm.orderId}`, { logisticsNo: deliverForm.logisticsNo, logisticsCompany: deliverForm.logisticsCompany })
    ElMessage.success('发货成功')
    deliverVisible.value = false
    loadOrders()
  } catch { ElMessage.error('发货失败') }
  finally { deliverLoading.value = false }
}

watch(() => farmer.value.id, () => { loadTraces(); loadOrders() })
onMounted(() => { loadFarmer(); if (farmer.value.id) loadOrders() })
</script>

<style scoped>
.farmer-dashboard { min-height: 70vh; background: #f5f6f7; padding-bottom: 40px; }
.section-content { max-width: 1100px; margin: 0 auto; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; }
.page-header h2 { font-size: 20px; }
.stat-row { margin-bottom: 20px; }
.stat-num { font-size: 28px; font-weight: 700; color: #1a8c3a; text-align: center; }
.stat-label { font-size: 14px; color: #999; text-align: center; margin-top: 4px; }
</style>

<template>
  <div class="presale-confirm-page">
    <div class="section-content">
      <el-card shadow="never">
        <h2 style="margin-bottom:16px">预售订单确认</h2>

        <div v-if="product" class="product-info">
          <el-image :src="product.mainImage" style="width:120px;height:100px;border-radius:8px" fit="cover" />
          <div class="info-right">
            <h3>{{ product.productName }}</h3>
            <p class="origin">{{ product.originPlace }}</p>
            <div class="price">¥{{ product.price }} <span v-if="product.originalPrice > product.price" class="original">¥{{ product.originalPrice }}</span></div>
          </div>
        </div>

        <el-divider />

        <div class="presale-detail">
          <h3>预售信息</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <label>预计种植时间</label>
              <span>{{ product?.presaleStart || '待定' }}</span>
            </div>
            <div class="detail-item">
              <label>预计成熟时间</label>
              <span>{{ product?.presaleEnd || '待定' }}</span>
            </div>
            <div class="detail-item">
              <label>生长周期</label>
              <span>{{ growthDays }}天</span>
            </div>
          </div>
        </div>

        <el-divider />

        <div class="growth-preview">
          <h3>生长周期预览</h3>
          <p style="color:#666;font-size:13px;margin-bottom:12px">下单后您将实时收到以下阶段的动态更新：</p>
          <el-steps :active="0" direction="vertical" style="max-width:400px">
            <el-step title="播种" description="种子入土，生命开始" />
            <el-step title="发芽" description="破土而出，嫩芽初现" />
            <el-step title="开花" description="花开满枝，授粉结果" />
            <el-step title="结果" description="果实逐渐长大" />
            <el-step title="成熟" description="果实成熟，采摘发货" />
          </el-steps>
        </div>

        <el-divider />

        <div class="order-form">
          <h3>订单信息</h3>
          <el-form label-width="80px" style="max-width:500px">
            <el-form-item label="购买数量">
              <el-input-number v-model="quantity" :min="1" :max="10" />
              <span style="margin-left:8px;color:#666">箱</span>
            </el-form-item>
            <el-form-item label="收货地址">
              <el-select v-model="addressId" placeholder="选择收货地址" style="width:100%">
                <el-option v-for="a in addresses" :key="a.id" :label="a.receiverName + ' ' + a.receiverPhone + ' ' + a.province + a.city + a.district + a.detailAddress" :value="a.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="备注"><el-input v-model="remark" placeholder="可选" /></el-form-item>
          </el-form>
        </div>

        <div class="submit-bar">
          <div class="total">
            <span>合计：</span>
            <span class="total-price">¥{{ (product?.price * quantity).toFixed(2) }}</span>
          </div>
          <el-button type="success" size="large" @click="submitOrder" :loading="submitting">确认预订</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const addresses = ref([])
const quantity = ref(1)
const addressId = ref(null)
const remark = ref('')
const submitting = ref(false)

const growthDays = computed(() => {
  if (!product.value?.presaleStart || !product.value?.presaleEnd) return '—'
  const start = new Date(product.value.presaleStart)
  const end = new Date(product.value.presaleEnd)
  return Math.ceil((end - start) / (1000 * 60 * 60 * 24))
})

async function loadData() {
  const productId = route.params.productId
  try {
    const r = await axios.get(`/api/public/products/detail/${productId}`)
    product.value = r.data.data
  } catch {}
  try {
    const r = await axios.get('/api/address/list')
    addresses.value = r.data.data || []
    if (addresses.value.length) {
      const def = addresses.value.find(a => a.isDefault === 1)
      addressId.value = def ? def.id : addresses.value[0].id
    }
  } catch {}
}

async function submitOrder() {
  if (!addressId.value) return ElMessage.warning('请选择收货地址')
  submitting.value = true
  try {
    const r = await axios.post('/api/order/create', {
      addressId: addressId.value,
      items: [{ productId: product.value.id, quantity: quantity.value }],
      remark: remark.value,
      isPresale: true
    })
    const orderId = r.data.data?.orderId || r.data.data?.id || r.data.data
    ElMessage.success('订单创建成功，请支付')
    router.push({ path: '/pay', query: { orderId, total: (product.value.price * quantity.value).toFixed(2) } })
  } catch (e) { ElMessage.error(e.response?.data?.message || '下单失败') }
  finally { submitting.value = false }
}

onMounted(loadData)
</script>

<style scoped>
.presale-confirm-page { background: #f5f6f7; min-height: 80vh; padding: 20px 0; }
.section-content { max-width: 800px; margin: 0 auto; padding: 0 20px; }
.product-info { display: flex; gap: 16px; }
.info-right h3 { font-size: 18px; margin-bottom: 4px; }
.origin { color: #666; font-size: 13px; margin-bottom: 8px; }
.price { color: #f56c6c; font-size: 24px; font-weight: 700; }
.original { color: #ccc; font-size: 14px; text-decoration: line-through; margin-left: 8px; }
.detail-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-top: 12px; }
.detail-item { display: flex; flex-direction: column; gap: 4px; }
.detail-item label { font-size: 12px; color: #999; }
.detail-item span { font-size: 15px; color: #333; font-weight: 500; }
.submit-bar { display: flex; justify-content: space-between; align-items: center; margin-top: 20px; padding: 16px; background: #f9f9f9; border-radius: 8px; }
.total-price { color: #f56c6c; font-size: 28px; font-weight: 700; }
</style>

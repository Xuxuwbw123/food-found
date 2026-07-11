<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">{{ isEdit ? '编辑商品' : '新增商品' }}</span>
        <el-button @click="$router.back()">返回</el-button>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width:800px">
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="productName">
              <el-input v-model="form.productName" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品编号" prop="productNo">
              <el-input v-model="form.productNo" placeholder="自动生成或手动输入" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width:100%">
                <el-option v-for="c in flatCategories" :key="c.id" :label="c.categoryName" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产地" prop="originPlace">
              <el-input v-model="form.originPlace" placeholder="如：湖北武汉黄陂" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">价格库存</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="售价" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" :step="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="原价">
              <el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" :step="1" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="单位">
              <el-input v-model="form.unit" placeholder="如：kg、箱" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="重量">
              <el-input v-model="form.weight" placeholder="如：5kg" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">标签设置</el-divider>
        <el-form-item label="标签">
          <el-checkbox v-model="isRecommend" :true-value="1" :false-value="0">推荐</el-checkbox>
          <el-checkbox v-model="isNew" :true-value="1" :false-value="0">新品</el-checkbox>
          <el-checkbox v-model="isHot" :true-value="1" :false-value="0">热销</el-checkbox>
        </el-form-item>

        <el-divider content-position="left">商品描述</el-divider>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入商品描述" />
        </el-form-item>

        <el-divider content-position="left">主图</el-divider>
        <el-form-item label="主图">
          <div style="display:flex;gap:12px;align-items:flex-start">
            <div v-if="form.mainImage">
              <el-image :src="form.mainImage" style="width:120px;height:120px;border-radius:8px" fit="cover" />
              <el-button link type="danger" size="small" @click="form.mainImage=''">移除</el-button>
            </div>
            <div>
              <input type="file" accept="image/*" ref="mainImgRef" style="display:none" @change="uploadMainImage" />
              <el-button @click="$refs.mainImgRef.click()" :loading="mainImgUploading">{{ form.mainImage ? '更换图片' : '选择图片' }}</el-button>
              <div style="font-size:12px;color:#999;margin-top:4px">支持 JPG/PNG/GIF/WebP，最大 5MB</div>
            </div>
          </div>
        </el-form-item>

        <el-divider content-position="left">商品图片管理</el-divider>
        <el-form-item label="商品图片">
          <div style="width:100%">
            <div class="image-list">
              <div v-for="(img, idx) in images" :key="img.id || idx" class="image-item">
                <el-image :src="img.imageUrl" style="width:100px;height:100px;border-radius:6px" fit="cover" />
                <div class="image-info">
                  <el-select v-model="img.imageType" size="small" style="width:100px">
                    <el-option label="详情图" :value="2" />
                    <el-option label="轮播图" :value="3" />
                  </el-select>
                  <el-input-number v-model="img.sort" :min="0" size="small" style="width:80px" placeholder="排序" />
                </div>
                <el-button type="danger" :icon="Delete" circle size="small" @click="removeImage(idx)" />
              </div>
            </div>
            <div style="margin-top:10px">
              <input type="file" accept="image/*" ref="imgRef" style="display:none" @change="uploadImage" />
              <el-button type="primary" @click="$refs.imgRef.click()" :loading="imgUploading">添加图片</el-button>
            </div>
          </div>
        </el-form-item>

        <el-form-item style="margin-top:30px">
          <el-button type="primary" size="large" @click="handleSubmit" :loading="submitLoading">保存商品</el-button>
          <el-button size="large" @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import axios from 'axios'
import { getProductDetail, addProduct, updateProduct, getProductImages, addProductImage, deleteProductImage } from '../../api/admin'
import { getCategoryTree } from '../../api/admin'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const formRef = ref(null)
const submitLoading = ref(false)
const categories = ref([])
const flatCategories = computed(() => {
  const result = []
  function flatten(list, prefix = '') {
    if (!list || !list.length) return
    list.forEach(c => {
      result.push({ id: c.id, categoryName: prefix + c.categoryName })
      if (c.children && c.children.length) flatten(c.children, prefix + '--')
    })
  }
  flatten(categories.value)
  return result
})
const images = ref([])
const mainImgUploading = ref(false)
const imgUploading = ref(false)

const form = reactive({
  id: null, productName: '', productNo: '', categoryId: null, mainImage: '',
  price: 0, originalPrice: 0, stock: 0, unit: 'kg', weight: '', originPlace: '',
  description: '', isRecommend: 0, isNew: 0, isHot: 0
})

const isRecommend = computed({
  get: () => form.isRecommend,
  set: (v) => form.isRecommend = v
})
const isNew = computed({
  get: () => form.isNew,
  set: (v) => form.isNew = v
})
const isHot = computed({
  get: () => form.isHot,
  set: (v) => form.isHot = v
})

const rules = {
  productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
}

async function loadCategories() {
  const res = await getCategoryTree()
  categories.value = res.data || []
}

async function loadProduct() {
  if (!isEdit.value) return
  const res = await getProductDetail(route.params.id)
  const p = res.data
  Object.assign(form, {
    id: p.id, productName: p.productName, productNo: p.productNo || '',
    categoryId: p.categoryId, mainImage: p.mainImage || '',
    price: p.price || 0, originalPrice: p.originalPrice || 0,
    stock: p.stock || 0, unit: p.unit || 'kg', weight: p.weight || '',
    originPlace: p.originPlace || '', description: p.description || '',
    isRecommend: p.isRecommend || 0, isNew: p.isNew || 0, isHot: p.isHot || 0
  })
  const imgRes = await getProductImages(p.id)
  images.value = imgRes.data || []
}

function addImage() {
  // 现在通过上传按钮添加图片，此函数保留兼容
}

function removeImage(idx) {
  images.value.splice(idx, 1)
}

// 主图上传
async function uploadMainImage(e) {
  const file = e.target.files[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) { ElMessage.warning('图片不能超过5MB'); return }
  mainImgUploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', file)
    const res = await axios.post('/api/upload/product-image', fd)
    form.mainImage = res.data.data.imageUrl
    ElMessage.success('主图上传成功')
  } catch (e) { ElMessage.error('上传失败') }
  finally { mainImgUploading.value = false; e.target.value = '' }
}

// 商品图片上传
async function uploadImage(e) {
  const file = e.target.files[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) { ElMessage.warning('图片不能超过5MB'); return }
  imgUploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', file)
    const res = await axios.post('/api/upload/product-image', fd)
    images.value.push({ imageUrl: res.data.data.imageUrl, imageType: 2, sort: images.value.length })
    ElMessage.success('图片上传成功')
  } catch (e) { ElMessage.error('上传失败') }
  finally { imgUploading.value = false; e.target.value = '' }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    const data = { ...form }
    delete data.id
    let productId = form.id
    if (isEdit.value) {
      await updateProduct({ id: form.id, ...data })
    } else {
      const res = await addProduct(data)
      productId = res.data?.id || res.data
    }
    if (productId && images.value.length) {
      for (const img of images.value) {
        if (!img.id) {
          await addProductImage({ productId, imageUrl: img.imageUrl, imageType: img.imageType || 1, sort: img.sort || 0 })
        }
      }
    }
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    router.push('/admin/products')
  } finally { submitLoading.value = false }
}

onMounted(() => { loadCategories(); loadProduct() })
</script>

<style scoped>
.page-container { animation: fadeIn .3s; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-title { font-size: 16px; font-weight: 600; }
.image-list { display: flex; flex-wrap: wrap; gap: 12px; }
.image-item { position: relative; border: 1px solid #e4e7ed; border-radius: 8px; padding: 8px; background: #fafafa; }
.image-info { display: flex; gap: 6px; margin-top: 6px; }
</style>

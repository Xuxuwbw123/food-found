<template>
  <div class="home">
    <!-- Banner轮播 -->
    <section class="banner-section">
      <el-carousel height="420px" indicator-position="outside" arrow="always" :interval="4000" autoplay @change="onBannerChange">
        <el-carousel-item v-for="b in banners" :key="b.id">
          <div class="banner-slide" @click="onBannerClick(b)" style="cursor:pointer">
            <el-image :src="b.imageUrl" fit="contain" style="width:100%;height:100%;background:#f5f5f5" lazy alt="" />
          </div>
        </el-carousel-item>
      </el-carousel>
      <div class="banner-title-bar">
        <h2>{{ currentTitle }}</h2>
      </div>
    </section>

    <!-- 分类导航 -->
    <section class="category-section">
      <div class="section-content">
        <h3 class="section-title">
          <span>{{ selectedCategory ? selectedCategory.categoryName : '商品分类' }}</span>
          <el-button v-if="selectedCategory" link type="primary" size="small" @click="clearFilter">查看全部 →</el-button>
        </h3>
        <div class="category-grid">
          <div v-for="c in categories" :key="c.id"
               class="category-card"
               :class="{ active: selectedCategory?.id === c.id }"
               @click="selectCategory(c)">
            <div class="category-icon">
              <el-image :src="getCatImage(c.categoryName)" style="width:48px;height:48px" fit="contain" lazy :alt="c.categoryName">
                <template #error><el-icon :size="32"><Goods /></el-icon></template>
              </el-image>
            </div>
            <div class="category-name">{{ c.categoryName }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 推荐商品 -->
    <section class="product-section">
      <div class="section-content">
        <h3 class="section-title">
          <span>{{ selectedCategory ? selectedCategory.categoryName : '精选推荐' }}</span>
        </h3>
        <div class="product-grid">
          <div v-for="p in products" :key="p.id" class="product-card" @click="$router.push(`/product/${p.id}`)">
            <div class="product-image">
              <el-image :src="p.mainImage" fit="cover" style="width:100%;height:100%" lazy :alt="p.productName">
                <template #error><el-icon :size="60" color="#dcdfe6"><Picture /></el-icon></template>
              </el-image>
              <div class="product-tags">
                <el-tag v-if="p.isNew === 1" type="danger" size="small" effect="dark">新品</el-tag>
                <el-tag v-if="p.isHot === 1" type="warning" size="small" effect="dark">热销</el-tag>
              </div>
            </div>
            <div class="product-info">
              <div class="product-name">{{ p.productName }}</div>
              <div class="product-origin">{{ p.originPlace }}</div>
              <div class="product-bottom">
                <div class="product-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ p.price }}</span>
                  <span v-if="p.originalPrice > p.price" class="price-original">¥{{ p.originalPrice }}</span>
                </div>
                <div class="product-sales">已售{{ p.sales || 0 }}{{ p.unit }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPublicBanners, getPublicProducts, getPublicFirstLevelCategories, getPublicCategoryTree } from '../../api/admin'

const router = useRouter()
const banners = ref([])
const categories = ref([])
const products = ref([])
const selectedCategory = ref(null)
const currentBanner = ref(0)
const currentTitle = computed(() => banners.value[currentBanner.value]?.title || '')

// ===== 分类图片映射（按你指定的对应关系，改这里即可） =====
const catImageMap = {
  '新鲜蔬菜': '/images/categories/蔬菜.png',
  '时令水果': '/images/categories/水果.png',
  '肉禽蛋奶': '/images/categories/肉禽蛋奶.png',
  '海鲜水产': '/images/categories/海鲜.png',
  '粮油干货': '/images/categories/粮食.png',
}
function getCatImage(name) {
  return catImageMap[name] || ''
}

function onBannerChange(index) {
  currentBanner.value = index
}
function onBannerClick(b) {
  if (b.linkType === 1 && b.linkId) router.push(`/product/${b.linkId}`)
  else if (b.linkType === 2) loadProducts(b.linkId)
  else if (b.linkType === 3 && b.linkUrl) window.open(b.linkUrl)
}

async function selectCategory(c) {
  if (selectedCategory.value?.id === c.id) {
    clearFilter()
    return
  }
  selectedCategory.value = c
  await loadProducts(c.id)
}

function clearFilter() {
  selectedCategory.value = null
  loadProducts(null)
}

async function loadProducts(categoryId) {
  try {
    const params = { pageNum: 1, pageSize: 8, status: 1 }
    if (categoryId) params.categoryId = categoryId
    const res = await getPublicProducts(params)
    products.value = res.data?.records || []
  } catch { /* handled by interceptor */ }
}

async function loadData() {
  try {
    const [bannerRes, catRes, prodRes] = await Promise.all([
      getPublicBanners({ pageNum: 1, pageSize: 10, status: 1 }),
      getPublicFirstLevelCategories(),
      getPublicProducts({ pageNum: 1, pageSize: 8, status: 1 })
    ])
    banners.value = bannerRes.data?.records || []
    categories.value = catRes.data || []
    products.value = prodRes.data?.records || []
  } catch { /* handled by interceptor */ }
}

onMounted(loadData)
</script>

<style scoped>
.home { background: #f5f6f7; }
.section-content { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.section-title { display: flex; justify-content: space-between; align-items: center; font-size: 22px; font-weight: 600; color: #333; padding: 30px 0 16px; }
.banner-section { background: #fff; }
.banner-slide { width: 100%; height: 100%; position: relative; }
.banner-title-bar { text-align: center; padding: 16px 0 8px; }
.banner-title-bar h2 { font-size: 22px; font-weight: 600; color: #333; }

.category-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(120px, 1fr)); gap: 16px; }
.category-card { background: #fff; border-radius: 12px; padding: 20px 10px; text-align: center; cursor: pointer; transition: all .3s; box-shadow: 0 2px 8px rgba(0,0,0,.04); border: 2px solid transparent; }
.category-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0,0,0,.1); color: #1a8c3a; }
.category-card.active { border-color: #1a8c3a; background: #e8f5e9; }
.category-icon { margin-bottom: 8px; color: #1a8c3a; }
.category-name { font-size: 14px; font-weight: 500; }

.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.product-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all .3s; box-shadow: 0 2px 8px rgba(0,0,0,.04); }
.product-card:hover { transform: translateY(-6px); box-shadow: 0 12px 32px rgba(0,0,0,.12); }
.product-image { position: relative; width: 100%; height: 220px; overflow: hidden; background: #f5f5f5; display: flex; align-items: center; justify-content: center; }
.product-tags { position: absolute; top: 8px; left: 8px; display: flex; gap: 4px; }
.product-info { padding: 14px; }
.product-name { font-size: 15px; font-weight: 600; color: #333; margin-bottom: 6px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.product-origin { font-size: 12px; color: #999; margin-bottom: 10px; }
.product-bottom { display: flex; justify-content: space-between; align-items: center; }
.price-symbol { color: #f56c6c; font-size: 14px; font-weight: 600; }
.price-value { color: #f56c6c; font-size: 22px; font-weight: 700; }
.price-original { color: #ccc; font-size: 13px; text-decoration: line-through; margin-left: 6px; }
.product-sales { font-size: 12px; color: #999; }

@media (max-width: 1024px) { .product-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) { .product-grid { grid-template-columns: repeat(2, 1fr); } }
</style>

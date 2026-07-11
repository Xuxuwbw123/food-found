<template>
  <div class="search-page"><div class="section-content">
    <h2 v-if="keyword">搜索结果：{{ keyword }}</h2>
    <el-row :gutter="16">
      <el-col :span="5" class="filter-col">
        <el-card shadow="never"><template #header>分类筛选</template>
          <div v-for="cat in categories" :key="cat.id" :class="{active:selCat===cat.id}" style="cursor:pointer;padding:6px 0" @click="selCat=selCat===cat.id?null:cat.id;load()">{{ cat.categoryName }}</div>
        </el-card>
      </el-col>
      <el-col :span="19">
        <div class="sort-bar">
          <el-radio-group v-model="sort" @change="load" size="small">
            <el-radio-button value="default">综合</el-radio-button>
            <el-radio-button value="sales">销量</el-radio-button>
            <el-radio-button value="price_asc">价格↑</el-radio-button>
            <el-radio-button value="price_desc">价格↓</el-radio-button>
          </el-radio-group>
        </div>
        <div v-loading="loading">
          <div class="product-grid" v-if="list.length">
            <div v-for="p in list" :key="p.id" class="product-card" @click="$router.push('/product/'+p.id)">
              <div class="product-img"><el-image :src="p.mainImage" fit="cover" style="width:100%;height:100%"><template #error><el-icon :size="60" color="#dcdfe6"><Picture /></el-icon></template></el-image></div>
              <div class="product-info"><div class="product-name">{{ p.productName }}</div><div class="product-price">¥{{ p.price }} <span v-if="p.originalPrice>p.price" class="price-old">¥{{ p.originalPrice }}</span></div><div class="product-sales">已售{{ p.sales }}</div></div>
            </div>
          </div>
          <el-empty v-else description="未找到相关商品" />
        </div>
      </el-col>
    </el-row>
  </div></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
const route = useRoute()
const keyword = ref(route.query.keyword || '')
const list = ref([]); const loading = ref(false)
const categories = ref([]); const selCat = ref(null); const sort = ref('default')

async function load() {
  loading.value = true
  try {
    const [catRes, searchRes] = await Promise.all([
      axios.get('/admin/category/first-level'),
      axios.get('/api/product/search', { params: { keyword: keyword.value, pageSize: 20, categoryId: selCat.value, sort: sort.value } })
    ])
    categories.value = catRes.data.data || []
    list.value = searchRes.data.data?.records || []
  } finally { loading.value = false }
}
watch(() => route.query.keyword, (v) => { keyword.value = v; load() })
onMounted(load)
</script>

<style scoped>
.search-page { min-height:70vh; background:#f5f6f7; padding-bottom:40px }
.section-content { max-width:1200px; margin:0 auto; padding:20px }
.filter-col { } .filter-col .active { color:#1a8c3a; font-weight:bold }
.sort-bar { margin-bottom:16px }
.product-grid { display:grid; grid-template-columns:repeat(4,1fr); gap:12px }
.product-card { background:#fff; border-radius:8px; overflow:hidden; cursor:pointer; transition:.3s }
.product-card:hover { transform:translateY(-4px); box-shadow:0 8px 24px rgba(0,0,0,.1) }
.product-img { height:180px; background:#f5f5f5; display:flex; align-items:center; justify-content:center }
.product-info { padding:12px }
.product-name { font-size:14px; font-weight:600; overflow:hidden; text-overflow:ellipsis; white-space:nowrap }
.product-price { color:#f56c6c; font-size:18px; font-weight:700; margin-top:6px }
.price-old { color:#ccc; font-size:12px; text-decoration:line-through; margin-left:4px }
.product-sales { font-size:12px; color:#999; margin-top:4px }
h2 { padding:10px 0 }
</style>

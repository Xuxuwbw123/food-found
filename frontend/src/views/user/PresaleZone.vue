<template>
  <div class="presale-page">
    <div class="section-content">
      <div class="page-header">
        <h1>预售专区</h1>
        <p class="subtitle">提前预订，见证农产品从种子到餐桌的全过程</p>
      </div>

      <div class="presale-grid" v-loading="loading">
        <div v-for="p in products" :key="p.id" class="presale-card" @click="$router.push(`/presale-confirm/${p.id}`)">
          <div class="card-image">
            <el-image :src="p.mainImage" fit="cover" style="width:100%;height:200px" lazy>
              <template #error><el-icon :size="60" color="#dcdfe6"><Picture /></el-icon></template>
            </el-image>
            <div class="presale-tag">预售</div>
          </div>
          <div class="card-body">
            <h3>{{ p.productName }}</h3>
            <p class="origin">{{ p.originPlace }}</p>
            <div class="presale-info">
              <span v-if="p.presaleStart">预计种植：{{ p.presaleStart }}</span>
              <span v-if="p.presaleEnd">预计成熟：{{ p.presaleEnd }}</span>
            </div>
            <div class="card-bottom">
              <div class="price">
                <span class="symbol">¥</span>
                <span class="value">{{ p.price }}</span>
                <span v-if="p.originalPrice > p.price" class="original">¥{{ p.originalPrice }}</span>
              </div>
              <el-button type="success" size="small">立即预订</el-button>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && !products.length" description="暂无预售商品" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import axios from 'axios'

const products = ref([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const r = await axios.get('/api/presale/list?pageNum=1&pageSize=50')
    products.value = r.data.data?.records || []
  } catch {} finally { loading.value = false }
}

onMounted(loadData)
</script>

<style scoped>
.presale-page { background: #f5f6f7; min-height: 80vh; }
.section-content { max-width: 1200px; margin: 0 auto; padding: 0 20px 40px; }
.page-header { text-align: center; padding: 30px 0 20px; }
.page-header h1 { font-size: 28px; color: #333; margin-bottom: 8px; }
.subtitle { color: #666; font-size: 14px; }

.presale-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.presale-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all .3s; box-shadow: 0 2px 8px rgba(0,0,0,.04); }
.presale-card:hover { transform: translateY(-6px); box-shadow: 0 12px 32px rgba(0,0,0,.12); }
.card-image { position: relative; }
.presale-tag { position: absolute; top: 10px; left: 10px; background: linear-gradient(135deg, #f093fb, #f5576c); color: #fff; padding: 2px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; }
.card-body { padding: 14px; }
.card-body h3 { font-size: 15px; color: #333; margin-bottom: 4px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.origin { font-size: 12px; color: #999; margin-bottom: 8px; }
.presale-info { display: flex; flex-direction: column; gap: 2px; font-size: 12px; color: #1a8c3a; margin-bottom: 10px; }
.card-bottom { display: flex; justify-content: space-between; align-items: center; }
.price .symbol { color: #f56c6c; font-size: 14px; }
.price .value { color: #f56c6c; font-size: 22px; font-weight: 700; }
.price .original { color: #ccc; font-size: 13px; text-decoration: line-through; margin-left: 6px; }

@media (max-width: 1024px) { .presale-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) { .presale-grid { grid-template-columns: repeat(2, 1fr); } }
</style>

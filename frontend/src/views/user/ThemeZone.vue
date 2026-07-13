<template>
  <div class="page-container">
    <h2 style="margin-bottom:16px">主题专区</h2>
    <el-row :gutter="16">
      <el-col :span="8" v-for="t in themes" :key="t.id" style="margin-bottom:16px">
        <el-card shadow="hover" @click="openTheme(t)" style="cursor:pointer">
          <el-image v-if="t.imageUrl" :src="t.imageUrl" style="width:100%;height:160px;border-radius:8px" fit="cover" />
          <h3 style="margin:10px 0 4px">{{ t.name }}</h3>
          <p style="color:#666;font-size:13px">{{ t.description }}</p>
          <p style="color:#999;font-size:12px">{{ t.startTime }} ~ {{ t.endTime }}</p>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!themes.length" description="暂无主题活动" />

    <el-dialog :title="currentTheme?.name" v-model="dialogVisible" width="800px">
      <el-row :gutter="16">
        <el-col :span="8" v-for="p in themeProducts" :key="p.id" style="margin-bottom:12px">
          <el-card shadow="hover" @click="$router.push('/product/'+p.id)" style="cursor:pointer">
            <el-image :src="p.mainImage" style="width:100%;height:120px" fit="cover" />
            <h4 style="margin:6px 0;font-size:13px">{{ p.productName }}</h4>
            <p style="color:#f56c6c;font-weight:bold">¥{{ p.price }}</p>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!themeProducts.length" description="暂无商品" />
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const themes = ref([]); const themeProducts = ref([]); const currentTheme = ref(null); const dialogVisible = ref(false)

async function loadData() {
  try { const r = await axios.get('/api/themes/active'); themes.value = r.data.data || [] } catch {}
}

async function openTheme(t) {
  currentTheme.value = t; dialogVisible.value = true
  try { const r = await axios.get(`/api/themes/${t.id}/products`); themeProducts.value = r.data.data || [] } catch { themeProducts.value = [] }
}

onMounted(loadData)
</script>
<style scoped>.page-container{max-width:1200px;margin:0 auto;padding:20px}</style>

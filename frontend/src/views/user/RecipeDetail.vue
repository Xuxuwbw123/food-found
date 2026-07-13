<template>
  <div class="page-container">
    <el-card v-if="recipe" shadow="never">
      <el-image v-if="recipe.imageUrl" :src="recipe.imageUrl" style="width:100%;height:300px;border-radius:8px" fit="cover" />
      <h1 style="margin:16px 0 8px">{{ recipe.title }}</h1>
      <div style="display:flex;gap:16px;color:#666;margin-bottom:16px">
        <span>难度: <el-tag size="small" :type="['','success','warning','danger'][recipe.difficulty]||'info'">{{ ['','简单','中等','困难'][recipe.difficulty]||'-' }}</el-tag></span>
        <span v-if="recipe.cookTime">烹饪时间: {{ recipe.cookTime }}分钟</span>
      </div>
      <p style="color:#666;line-height:1.8">{{ recipe.description }}</p>

      <el-divider />
      <h3>所需食材</h3>
      <el-table :data="ingredients" border style="margin:12px 0">
        <el-table-column prop="ingredientName" label="食材" min-width="150" />
        <el-table-column prop="amount" label="用量" width="120" />
      </el-table>

      <el-divider />
      <h3>烹饪步骤</h3>
      <div style="white-space:pre-line;line-height:2;color:#333;padding:12px;background:#fafafa;border-radius:8px">{{ recipe.steps }}</div>
    </el-card>
    <el-empty v-else description="加载中..." />
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const recipe = ref(null); const ingredients = ref([])

async function loadData() {
  try {
    const r = await axios.get(`/api/recipes/${route.params.id}`)
    recipe.value = r.data.data?.recipe
    ingredients.value = r.data.data?.ingredients || []
  } catch {}
}

onMounted(loadData)
</script>
<style scoped>.page-container{max-width:900px;margin:0 auto;padding:20px}</style>

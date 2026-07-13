<template>
  <div class="page-container">
    <h2 style="margin-bottom:16px">精选菜谱</h2>
    <el-row :gutter="16">
      <el-col :span="6" v-for="r in recipes" :key="r.id" style="margin-bottom:16px">
        <el-card shadow="hover" @click="$router.push('/recipe/'+r.id)" style="cursor:pointer">
          <el-image v-if="r.imageUrl" :src="r.imageUrl" style="width:100%;height:150px;border-radius:8px" fit="cover" />
          <h3 style="margin:10px 0 4px;font-size:15px">{{ r.title }}</h3>
          <p style="color:#666;font-size:13px;height:36px;overflow:hidden">{{ r.description }}</p>
          <div style="display:flex;justify-content:space-between;align-items:center;margin-top:8px">
            <el-tag size="small" :type="['','success','warning','danger'][r.difficulty]||'info'">{{ ['','简单','中等','困难'][r.difficulty]||'-' }}</el-tag>
            <span style="color:#999;font-size:12px">{{ r.cookTime ? r.cookTime+'分钟' : '' }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!recipes.length" description="暂无菜谱" />
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const recipes = ref([])

async function loadData() {
  try { const r = await axios.get('/api/recipes'); recipes.value = r.data.data || [] } catch {}
}

onMounted(loadData)
</script>
<style scoped>.page-container{max-width:1200px;margin:0 auto;padding:20px}</style>

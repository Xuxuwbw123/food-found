<template>
  <div class="footprint-page">
    <div class="section-content">
      <h2>浏览足迹</h2>
      <div v-loading="loading" class="grid">
        <div v-for="f in list" :key="f.id" class="card" @click="$router.push('/product/'+f.productId)">
          <div class="img"><el-image :src="f.mainImage" fit="cover" style="width:100%;height:100%"><template #error><el-icon :size="50" color="#dcdfe6"><Picture/></el-icon></template></el-image></div>
          <div class="info">
            <div class="name">{{f.productName}}</div>
            <div class="price">¥{{f.price}}</div>
            <div class="time">{{f.browseTime}}</div>
          </div>
          <el-button class="del-btn" :icon="Delete" circle size="small" @click.stop="remove(f.id)" />
        </div>
      </div>
      <el-empty v-if="!loading&&list.length===0" description="暂无浏览记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { Delete } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'
const auth = useAuthStore(); auth.restoreSession()
const list = ref([]); const loading = ref(false)
async function load() { loading.value = true; try { const r = await axios.get('/api/footprint/list'); list.value = r.data.data?.records || r.data.data || [] } finally { loading.value = false } }
async function remove(id) { await axios.delete(`/api/footprint/delete/${id}`); load() }
onMounted(() => { if (auth.isLoggedIn) load() })
</script>

<style scoped>
.footprint-page { min-height: 70vh; background: #f5f6f7; padding-bottom: 40px; }
.section-content { max-width: 1100px; margin: 0 auto; padding: 20px; }
h2 { font-size: 20px; padding: 10px 0; }
.grid { display: grid; grid-template-columns: repeat(3,1fr); gap: 12px; }
.card { background: #fff; border-radius: 8px; overflow: hidden; cursor: pointer; display: flex; position: relative; transition: .3s; }
.card:hover { box-shadow: 0 4px 16px rgba(0,0,0,.1); }
.img { width: 120px; height: 120px; background: #f5f5f5; flex-shrink: 0; }
.info { padding: 12px; flex: 1; }
.name { font-size: 14px; font-weight: 600; }
.price { color: #f56c6c; font-size: 18px; font-weight: 700; margin-top: 4px; }
.time { font-size: 12px; color: #999; margin-top: 4px; }
.del-btn { position: absolute; top: 8px; right: 8px; }
</style>
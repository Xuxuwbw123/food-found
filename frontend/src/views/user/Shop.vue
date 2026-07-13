<template>
  <div class="page-container">
    <el-card v-if="shop" shadow="never">
      <div class="shop-header">
        <div>
          <h2>{{ shop.farmer?.farmName || shop.farmer?.name || '农户店铺' }}</h2>
          <p style="color:#666">{{ shop.farmer?.address }}</p>
          <p style="color:#999;font-size:13px">粉丝: {{ shop.followerCount || 0 }}</p>
        </div>
        <el-button :type="shop.followed ? 'info' : 'primary'" @click="toggleFollow">
          {{ shop.followed ? '已关注' : '关注' }}
        </el-button>
      </div>

      <el-divider />
      <h3>在售商品</h3>
      <el-row :gutter="16">
        <el-col :span="6" v-for="p in shop.products" :key="p.id" style="margin-bottom:16px">
          <el-card shadow="hover" @click="$router.push('/product/'+p.id)" style="cursor:pointer">
            <el-image :src="p.mainImage" style="width:100%;height:150px" fit="cover" />
            <h4 style="margin:8px 0 4px;font-size:14px">{{ p.productName }}</h4>
            <p style="color:#f56c6c;font-weight:bold">¥{{ p.price }}</p>
          </el-card>
        </el-col>
      </el-row>

      <el-divider />
      <h3>农场动态</h3>
      <el-timeline v-if="shop.updates?.length">
        <el-timeline-item v-for="u in shop.updates" :key="u.id" :timestamp="u.createTime" placement="top">
          <el-card><h4>{{ u.title }}</h4><p>{{ u.content }}</p></el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无动态" />

      <el-divider />
      <h3>资质证书</h3>
      <el-row :gutter="16" v-if="shop.certs?.length">
        <el-col :span="8" v-for="c in shop.certs" :key="c.id" style="margin-bottom:12px">
          <el-card shadow="hover">
            <h4>{{ c.certName }}</h4>
            <p style="color:#666;font-size:13px">编号: {{ c.certNo }}</p>
            <p style="color:#666;font-size:13px">类型: {{ c.certType }}</p>
            <p style="color:#999;font-size:12px">发证: {{ c.issueOrg }}</p>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else description="暂无证书" />
    </el-card>
    <el-empty v-else description="加载中..." />
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const shop = ref(null)

async function loadShop() {
  try { const r = await axios.get(`/api/shop/${route.params.id}`); shop.value = r.data.data } catch {}
}

async function toggleFollow() {
  if (!shop.value) return
  try {
    if (shop.value.followed) {
      await axios.delete(`/api/shop/unfollow/${route.params.id}`)
      shop.value.followed = false; shop.value.followerCount--
    } else {
      await axios.post(`/api/shop/follow/${route.params.id}`)
      shop.value.followed = true; shop.value.followerCount++
    }
  } catch { ElMessage.error('操作失败') }
}

onMounted(loadShop)
</script>
<style scoped>.page-container{max-width:1200px;margin:0 auto;padding:20px}.shop-header{display:flex;justify-content:space-between;align-items:flex-start}</style>

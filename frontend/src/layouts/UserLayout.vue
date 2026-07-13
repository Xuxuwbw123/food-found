<template>
  <div class="user-layout">
    <header class="user-header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/')">
          <img src="/images/logo/logo-color.png" alt="农臻溯源" style="height:52px" />
        </div>
        <div class="nav-links">
          <el-input v-model="searchKeyword" placeholder="搜索商品..." size="large" class="search-input" @keyup.enter="onSearch">
            <template #append>
              <el-button :icon="Search" @click="onSearch" />
            </template>
          </el-input>
          <el-button type="primary" size="large" @click="$router.push('/trace')">溯源查询</el-button>
          <template v-if="auth.isLoggedIn && !isFarmerPage">
            <el-button size="large" @click="$router.push('/cart')">购物车</el-button>
            <el-button size="large" @click="$router.push('/orders')">我的订单</el-button>
          </template>
        </div>
        <div class="header-actions">
          <template v-if="auth.isLoggedIn">
            <el-button v-if="auth.isFarmer" size="large" type="success" @click="$router.push('/farmer')">农户工作台</el-button>
            <el-button v-if="auth.isAdmin" size="large" @click="$router.push('/admin/dashboard')">管理后台</el-button>
            <el-button v-if="!auth.isFarmer && !auth.isAdmin" size="large" type="warning" @click="$router.push('/farmer')">申请成为农户</el-button>
            <el-badge :value="auth.unreadCount" :hidden="auth.unreadCount===0" :max="5" :is-dot="auth.unreadCount>5" style="margin-right:8px"><el-button size="large" @click="$router.push('/notices')">🔔</el-button></el-badge>
            <el-dropdown trigger="click">
              <span style="cursor:pointer;font-size:14px;color:#333;display:flex;align-items:center;gap:4px">
                <el-avatar :size="28" icon="UserFilled" />
                <span>{{ auth.user?.nickname || auth.user?.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="!isFarmerPage" @click="$router.push('/address')">收货地址</el-dropdown-item>
                  <el-dropdown-item v-if="!isFarmerPage" @click="$router.push('/favorites')">我的收藏</el-dropdown-item>
                  <el-dropdown-item v-if="!isFarmerPage" @click="$router.push('/coupons')">领券中心</el-dropdown-item>
                  <el-dropdown-item v-if="!isFarmerPage" @click="$router.push('/points')">我的积分</el-dropdown-item>
                  <el-dropdown-item v-if="!isFarmerPage" @click="$router.push('/footprints')">浏览足迹</el-dropdown-item>
                  <el-dropdown-item v-if="!isFarmerPage" @click="$router.push('/member')">会员中心</el-dropdown-item>
                  <el-dropdown-item v-if="!isFarmerPage" @click="$router.push('/chat')">在线客服</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/profile')">账户设置</el-dropdown-item>
                  <el-dropdown-item @click="switchAccount" divided>切换账号</el-dropdown-item>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button size="large" @click="$router.push('/register')">注册</el-button>
            <el-button size="large" type="primary" @click="$router.push('/login')">登录</el-button>
          </template>
        </div>
      </div>
    </header>
    <main class="user-main">
      <router-view />
    </main>
    <footer class="user-footer">
      <div class="footer-content">
        <div class="footer-section">
          <h4>农臻溯源</h4>
          <p>从田间到餐桌，全程可追溯</p>
          <p>让每一份农产品都有迹可循</p>
        </div>
        <div class="footer-section">
          <h4>快速链接</h4>
          <p><a href="/">首页</a></p>
          <p><a href="/trace">溯源查询</a></p>
        </div>
        <div class="footer-section">
          <h4>联系方式</h4>
          <p>客服热线：400-100-1001</p>
          <p>邮箱：support@nongzhen.com</p>
        </div>
      </div>
      <div class="copyright">© 2025 农臻溯源 版权所有 | 农产品质量安全追溯平台</div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, ArrowDown, UserFilled } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const isFarmerPage = computed(() => route.path.startsWith('/farmer'))
auth.restoreSession()
const searchKeyword = ref('')

function handleLogout() {
  auth.logout()
  router.push('/')
}

function switchAccount() {
  auth.logout()
  router.push('/login')
}

function onSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { keyword: searchKeyword.value } })
  }
}

let pollTimer = null
// 任何路由变化都重新拉一次(原来在 /notices 时跳过会导致铃铛停留在登录时的旧值)
watch(() => route.path, () => auth.loadUnreadCount())
onMounted(() => {
  auth.loadUnreadCount()
  pollTimer = setInterval(() => auth.loadUnreadCount(), 30000)
})
onUnmounted(() => { if (pollTimer) clearInterval(pollTimer) })
</script>

<style scoped>
.user-layout { min-height: 100vh; display: flex; flex-direction: column; }
.user-header { background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,.06); position: sticky; top: 0; z-index: 100; }
.header-content { max-width: 1200px; margin: 0 auto; display: flex; align-items: center; gap: 16px; padding: 0 20px; height: 64px; }
.logo { display: flex; align-items: center; gap: 8px; font-size: 22px; font-weight: bold; color: #1a8c3a; cursor: pointer; white-space: nowrap; flex-shrink: 0; }
.nav-links { display: flex; align-items: center; gap: 10px; }
.search-input { width: 280px; }
.header-actions { margin-left: auto; display: flex; align-items: center; gap: 10px; flex-shrink: 0; }
.user-main { flex: 1; }
.user-footer { background: #2c3e50; color: #ecf0f1; padding: 40px 0 0; margin-top: 60px; }
.footer-content { max-width: 1200px; margin: 0 auto; display: grid; grid-template-columns: repeat(3, 1fr); gap: 40px; padding: 0 20px 30px; }
.footer-section h4 { font-size: 16px; margin-bottom: 12px; color: #fff; }
.footer-section p { font-size: 13px; line-height: 2; color: #bdc3c7; }
.footer-section a { color: #bdc3c7; text-decoration: none; }
.footer-section a:hover { color: #1a8c3a; }
.copyright { text-align: center; padding: 16px; border-top: 1px solid #3d566e; font-size: 13px; color: #95a5a6; }
</style>

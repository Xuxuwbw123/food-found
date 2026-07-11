<template>
  <el-container class="admin-container">
    <el-aside width="220px" class="admin-aside">
      <div class="logo">
        <img src="/images/logo/logo-white.png" alt="logo" style="height:36px" onerror="this.style.display='none'" />
        <span v-if="false">溯源管理平台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#001529"
        text-color="#ffffffb3"
        active-text-color="#fff"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <el-icon><Grid /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/products">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/banners">
          <el-icon><PictureFilled /></el-icon>
          <span>轮播图管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/after-sales">
          <el-icon><Service /></el-icon>
          <span>售后工单</span>
        </el-menu-item>
        <el-menu-item index="/admin/comments">
          <el-icon><ChatLineSquare /></el-icon>
          <span>评论审核</span>
        </el-menu-item>
        <el-menu-item index="/admin/payments">
          <el-icon><Money /></el-icon>
          <span>支付记录</span>
        </el-menu-item>
        <el-menu-item index="/admin/addresses">
          <el-icon><Location /></el-icon>
          <span>用户地址</span>
        </el-menu-item>
        <el-menu-item index="/admin/trace-audit">
          <el-icon><Document /></el-icon>
          <span>溯源批次审核</span>
        </el-menu-item>
        <el-menu-item index="/admin/product-audit">
          <el-icon><Goods /></el-icon>
          <span>商品发布审核</span>
        </el-menu-item>
        <el-menu-item index="/admin/trace-delete-audit">
          <el-icon><Delete /></el-icon>
          <span>溯源删除审核</span>
        </el-menu-item>
        <el-sub-menu index="system">
          <template #title><el-icon><Setting /></el-icon><span>系统管理</span></template>
          <el-menu-item index="/admin/operation-logs"><span>操作日志</span></el-menu-item>
          <el-menu-item index="/admin/admins" v-if="false"><span>管理员账号</span></el-menu-item>
          <el-menu-item index="/admin/farmer-audit"><span>农户审核</span></el-menu-item>
          <el-menu-item index="/admin/config"><span>系统配置</span></el-menu-item>
          <el-menu-item index="/admin/member-level"><span>会员等级配置</span></el-menu-item>
          <el-menu-item index="/admin/points-exchange"><span>积分兑换规则</span></el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="marketing">
          <template #title><el-icon><Present /></el-icon><span>营销管理</span></template>
          <el-menu-item index="/admin/coupons"><span>优惠券管理</span></el-menu-item>
          <el-menu-item index="/admin/seckill"><span>秒杀管理</span></el-menu-item>
          <el-menu-item index="/admin/marketing"><span>营销活动</span></el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/admin/members"><el-icon><UserFilled /></el-icon><span>会员管理</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="pageTitle">{{ pageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-button size="small" @click="$router.push('/')">用户端</el-button>
          <el-dropdown>
            <span class="admin-avatar">
              <el-avatar :size="32" icon="UserFilled" />
              <span>{{ auth.user?.nickname || auth.user?.username || '管理员' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="switchAccount">切换账号</el-dropdown-item>
                <el-dropdown-item @click="handleLogout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
auth.restoreSession()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta?.title || '')

function handleLogout() {
  auth.logout()
  router.push('/login')
}

function switchAccount() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-container { height: 100vh; }
.admin-aside { background: #001529; overflow-y: auto; }
.logo { height: 60px; display: flex; align-items: center; justify-content: center; gap: 10px; color: #fff; font-size: 18px; font-weight: bold; border-bottom: 1px solid #ffffff1a; }
.admin-header { background: #fff; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 1px 4px rgba(0,0,0,.08); z-index: 1; padding: 0 20px; }
.header-left { display: flex; align-items: center; }
.header-right { display: flex; align-items: center; gap: 16px; }
.admin-avatar { display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; }
.admin-main { background: #f0f2f5; padding: 20px; overflow-y: auto; }
</style>

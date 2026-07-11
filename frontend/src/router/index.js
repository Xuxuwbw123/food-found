import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAdmin: true },
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '仪表盘' } },
      { path: 'users', name: 'UserList', component: () => import('../views/admin/UserList.vue'), meta: { title: '用户管理' } },
      { path: 'categories', name: 'CategoryList', component: () => import('../views/admin/CategoryList.vue'), meta: { title: '分类管理' } },
      { path: 'products', name: 'ProductList', component: () => import('../views/admin/ProductList.vue'), meta: { title: '商品管理' } },
      { path: 'product/edit/:id?', name: 'ProductEdit', component: () => import('../views/admin/ProductEdit.vue'), meta: { title: '商品编辑' } },
      { path: 'banners', name: 'BannerList', component: () => import('../views/admin/BannerList.vue'), meta: { title: '轮播图管理' } },
      { path: 'orders', name: 'OrderList', component: () => import('../views/admin/OrderList.vue'), meta: { title: '订单管理' } },
      { path: 'order/detail/:id', name: 'OrderDetail', component: () => import('../views/admin/OrderDetail.vue'), meta: { title: '订单详情' } },
      { path: 'after-sales', name: 'AfterSalesList', component: () => import('../views/admin/AfterSalesList.vue'), meta: { title: '售后工单' } },
      { path: 'after-sales/detail/:id', name: 'AfterSalesDetail', component: () => import('../views/admin/AfterSalesDetail.vue'), meta: { title: '售后处理' } },
      { path: 'comments', name: 'CommentList', component: () => import('../views/admin/CommentList.vue'), meta: { title: '评论审核' } },
      { path: 'payments', name: 'PaymentList', component: () => import('../views/admin/PaymentList.vue'), meta: { title: '支付记录' } },
      { path: 'addresses', name: 'AddressManage', component: () => import('../views/admin/AddressManage.vue'), meta: { title: '用户地址' } },
      { path: 'trace-audit', name: 'TraceAudit', component: () => import('../views/admin/TraceAudit.vue'), meta: { title: '溯源批次审核' } },
      { path: 'product-audit', name: 'ProductAudit', component: () => import('../views/admin/ProductAudit.vue'), meta: { title: '商品发布审核' } },
      { path: 'trace-delete-audit', name: 'TraceDeleteAudit', component: () => import('../views/admin/TraceDeleteAudit.vue'), meta: { title: '溯源删除审核' } },
      { path: 'operation-logs', name: 'OperationLog', component: () => import('../views/admin/OperationLog.vue'), meta: { title: '操作日志' } },
      { path: 'admins', name: 'AdminManage', component: () => import('../views/admin/AdminManage.vue'), meta: { title: '管理员账号' } },
      { path: 'farmer-audit', name: 'FarmerAudit', component: () => import('../views/admin/FarmerAudit.vue'), meta: { title: '农户审核' } },
      { path: 'config', name: 'ConfigCenter', component: () => import('../views/admin/ConfigCenter.vue'), meta: { title: '系统配置' } },
      { path: 'member-level', name: 'MemberLevelConfig', component: () => import('../views/admin/MemberLevelConfig.vue'), meta: { title: '会员等级配置' } },
      { path: 'points-exchange', name: 'PointsExchangeRule', component: () => import('../views/admin/PointsExchangeRule.vue'), meta: { title: '积分兑换规则' } },
      { path: 'coupons', name: 'CouponManage', component: () => import('../views/admin/CouponManage.vue'), meta: { title: '优惠券管理' } },
      { path: 'seckill', name: 'SeckillManage', component: () => import('../views/admin/SeckillManage.vue'), meta: { title: '秒杀管理' } },
      { path: 'members', name: 'MemberManage', component: () => import('../views/admin/MemberManage.vue'), meta: { title: '会员管理' } },
      { path: 'marketing', name: 'MarketingManage', component: () => import('../views/admin/MarketingManage.vue'), meta: { title: '营销管理' } }
    ]
  },
  {
    path: '/',
    component: () => import('../layouts/UserLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/user/Home.vue'), meta: { title: '首页' } },
      { path: 'product/:id', name: 'ProductDetail', component: () => import('../views/user/ProductDetail.vue'), meta: { title: '商品详情' } },
      { path: 'trace', name: 'TraceQuery', component: () => import('../views/user/TraceQuery.vue'), meta: { title: '溯源查询' } },
      { path: 'trace/:batchNo', name: 'TraceResult', component: () => import('../views/user/TraceResult.vue'), meta: { title: '溯源结果' } },
      { path: 'farmer/:id', name: 'FarmerDetail', component: () => import('../views/user/FarmerDetail.vue'), meta: { title: '农户详情' } },
      { path: 'address', name: 'AddressList', component: () => import('../views/user/AddressList.vue'), meta: { title: '收货地址' } },
      { path: 'cart', name: 'Cart', component: () => import('../views/user/Cart.vue'), meta: { title: '购物车' } },
      { path: 'pay', name: 'Pay', component: () => import('../views/user/Pay.vue'), meta: { title: '确认支付' } },
      { path: 'orders', name: 'MyOrders', component: () => import('../views/user/MyOrders.vue'), meta: { title: '我的订单' } },
      { path: 'after-sales', name: 'AfterSales', component: () => import('../views/user/AfterSales.vue'), meta: { title: '申请售后' } },
      { path: 'search', name: 'Search', component: () => import('../views/user/SearchResult.vue'), meta: { title: '搜索' } },
      { path: 'favorites', name: 'Favorites', component: () => import('../views/user/Favorites.vue'), meta: { title: '我的收藏' } },
      { path: 'profile', name: 'Profile', component: () => import('../views/user/Profile.vue'), meta: { title: '账户设置' } },
      { path: 'logistics/:orderId', name: 'Logistics', component: () => import('../views/user/Logistics.vue'), meta: { title: '物流详情' } },
      { path: 'notices', name: 'NoticeCenter', component: () => import('../views/user/NoticeCenter.vue'), meta: { title: '消息中心' } },
      { path: 'points', name: 'MyPoints', component: () => import('../views/user/MyPoints.vue'), meta: { title: '我的积分' } },
      { path: 'footprints', name: 'MyFootprint', component: () => import('../views/user/MyFootprint.vue'), meta: { title: '浏览足迹' } },
      { path: 'coupons', name: 'CouponCenter', component: () => import('../views/user/CouponCenter.vue'), meta: { title: '领券中心' } }
    ]
  },
  {
    path: '/farmer',
    component: () => import('../layouts/UserLayout.vue'),
    meta: { requiresFarmer: true },
    children: [
      { path: '', name: 'FarmerDashboard', component: () => import('../views/farmer/Dashboard.vue'), meta: { title: '农户工作台' } },
      { path: 'trace/:id', name: 'FarmerTraceDetail', component: () => import('../views/farmer/TraceDetail.vue'), meta: { title: '溯源管理' } },
      { path: 'order/:id', name: 'FarmerOrderDetail', component: () => import('../views/farmer/FarmerOrderDetail.vue'), meta: { title: '订单详情' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：校验 Token + 用户角色
router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  auth.restoreSession()

  if (to.matched.some(r => r.meta.requiresAdmin)) {
    if (!auth.accessToken) return next({ path: '/login', query: { redirect: to.fullPath } })
    // 校验用户是否为管理员（user_type === 3）
    if (!auth.user || auth.user.user_type !== 3) {
      ElMessage.error('无权访问管理后台')
      return next({ path: '/' })
    }
    next()
    return
  }
  if (to.matched.some(r => r.meta.requiresFarmer)) {
    if (!auth.accessToken) return next({ path: '/login', query: { redirect: to.fullPath } })
    // 农户工作台允许普通用户进入（用于申请认证），仅拦截管理员
    if (!auth.user || auth.user.user_type === 3) {
      ElMessage.error('无权访问农户工作台')
      return next({ path: '/' })
    }
    next()
    return
  }
  next()
})

export default router

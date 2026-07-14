import request from '../utils/request'

// 首页统计
export const getHomeStats = () => request.get('/statistics/home')

// 用户管理
export const getUserList = (params) => request.get('/user/list', { params })
export const getUserDetail = (id) => request.get(`/user/${id}`)
export const addUser = (data) => request.post('/user/add', data)
export const updateUser = (data) => request.put('/user/update', data)
export const deleteUser = (id) => request.delete(`/user/delete/${id}`)
export const changeUserStatus = (id, status) => request.put(`/user/change-status/${id}?status=${status}`)

// 分类管理
export const getCategoryList = (params) => request.get('/category/list', { params })
export const getCategoryTree = () => request.get('/category/tree')
export const getFirstLevelCategories = () => request.get('/category/first-level')
export const getCategoryDetail = (id) => request.get(`/category/${id}`)
export const addCategory = (data) => request.post('/category/add', data)
export const updateCategory = (data) => request.put('/category/update', data)
export const deleteCategory = (id) => request.delete(`/category/delete/${id}`)
export const changeCategoryStatus = (id, status) => request.put(`/category/change-status/${id}?status=${status}`)

// 商品管理
export const getProductList = (params) => request.get('/product/list', { params })
// 后端 /product/{id} 返回 {product, images} 包装结构,前端要的是 product 对象,这里解包
export const getProductDetail = async (id) => {
  const res = await request.get(`/product/${id}`)
  return { ...res, data: res.data?.product || res.data }
}
export const addProduct = (data) => request.post('/product/add', data)
export const updateProduct = (data) => request.put('/product/update', data)
export const deleteProduct = (id) => request.delete(`/product/delete/${id}`)
export const changeProductStatus = (id, status) => request.put(`/product/change-status/${id}?status=${status}`)
export const changeProductRecommend = (id, recommend) => request.put(`/product/change-recommend/${id}?recommend=${recommend}`)

// 商品图片
export const getProductImages = (productId) => request.get(`/product-image/list/${productId}`)
export const addProductImage = (data) => request.post('/product-image/add', data)
export const deleteProductImage = (id) => request.delete(`/product-image/delete/${id}`)
export const deleteProductAllImages = (productId) => request.delete(`/product-image/delete-by-product/${productId}`)

// 轮播图
export const getBannerList = (params) => request.get('/banner/list', { params })
export const getBannerDetail = (id) => request.get(`/banner/${id}`)
export const addBanner = (data) => request.post('/banner/add', data)
export const updateBanner = (data) => request.put('/banner/update', data)
export const deleteBanner = (id) => request.delete(`/banner/delete/${id}`)
export const changeBannerStatus = (id, status) => request.put(`/banner/change-status/${id}?status=${status}`)

// 订单管理
export const getOrderList = (params) => request.get('/order/list', { params })
export const getOrderDetail = (id) => request.get(`/order/${id}`)
export const deliveryOrder = (id, logisticsNo) => request.put(`/order/delivery/${id}?logisticsNo=${logisticsNo}`)
export const changeOrderStatus = (id, status) => request.put(`/order/change-status/${id}?status=${status}`)
export const deleteOrder = (id) => request.delete(`/order/delete/${id}`)

// 订单明细
export const getOrderItems = (orderId) => request.get(`/order-item/list/${orderId}`)
export const getOrderItemDetail = (id) => request.get(`/order-item/${id}`)

// 订单日志
export const getOrderLogs = (orderId) => request.get(`/order-log/list/${orderId}`)
export const addOrderLog = (data) => request.post('/order-log/add', data)

// 支付记录
export const getPaymentList = (params) => request.get('/payment/list', { params })
export const getPaymentDetail = (id) => request.get(`/payment/${id}`)
export const getPaymentByOrder = (orderId) => request.get(`/payment/by-order/${orderId}`)

// 售后工单
export const getAfterSalesList = (params) => request.get('/after-sales/list', { params })
export const getAfterSalesDetail = (id) => request.get(`/after-sales/${id}`)
export const auditAfterSales = (id, status, adminRemark) => request.put(`/after-sales/audit/${id}?status=${status}&adminRemark=${adminRemark}`)
export const closeAfterSales = (id, closeReason) => request.put(`/after-sales/close/${id}?closeReason=${closeReason}`)
export const deleteAfterSales = (id) => request.delete(`/after-sales/delete/${id}`)

// 客服记录
export const getCsLogs = (afterSalesId) => request.get(`/cs-log/list/${afterSalesId}`)
export const addCsLog = (data) => request.post('/cs-log/add', data)

// 评论审核
export const getCommentList = (params) => request.get('/comment/list', { params })
export const getCommentDetail = (id) => request.get(`/comment/${id}`)
export const approveComment = (id) => request.put(`/comment/approve/${id}`)
export const rejectComment = (id) => request.put(`/comment/reject/${id}`)
export const deleteComment = (id) => request.delete(`/comment/delete/${id}`)

// ===== P0 新增：操作日志 =====
import axios from 'axios'
export const getOperationLogPage = (params) => axios.get('/api/admin/operationLog/page', { params })

// ===== P0 新增：管理员账号 =====
export const getAdmins = () => axios.get('/api/admin/admins')
export const addAdmin = (data) => axios.post('/api/admin/admins', data)
export const updateAdmin = (data) => axios.put('/api/admin/admins', data)
export const resetAdminPassword = (id) => axios.put(`/api/admin/admins/resetPassword/${id}`)

// ===== P0 新增：批量操作 + 导出 =====
export const batchUsers = (ids, action) => axios.put('/api/admin/batch/users', { ids, action })
export const batchProducts = (ids, action) => axios.put('/api/admin/batch/products', { ids, action })
export const exportTable = (table) => axios.get(`/api/admin/export/${table}`, {
  responseType: 'blob',
  headers: { Authorization: 'Bearer ' + (localStorage.getItem('accessToken') || '') }
})

// ===== P2: 公开API（无需登录） =====
const publicApi = axios.create({ baseURL: '/api/public', timeout: 15000 })
publicApi.interceptors.response.use(res => { const d = res.data; return d.code === 200 ? d : Promise.reject(d) })
export const getPublicBanners = (params) => publicApi.get('/banner/list', { params })
export const getPublicCategories = () => publicApi.get('/category/list')
export const getPublicCategoryTree = () => publicApi.get('/category/tree')
export const getPublicFirstLevelCategories = () => publicApi.get('/category/first-level')
export const getPublicProducts = (params) => publicApi.get('/product/list', { params })

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios'
import App from './App.vue'
import router from './router'

// 全局 Axios 拦截器：自动附带 Token + 过期刷新
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken')
  if (token) config.headers.Authorization = 'Bearer ' + token
  return config
})

// 全局业务层拦截器:HTTP 2xx 但 body code != 200 的"假成功"自动 reject
// 根因:后端 @RestControllerAdvice 把所有异常包成 Result.error() 但 HTTP 状态仍是 200
// 直接 await axios.post() 不写 catch 的话会当成功 —— 这是 tmdd233 add 地址 bug 的根因
axios.interceptors.response.use(
  res => {
    const data = res.data
    // 只对 Result<T> 包装结构(code + message + data)生效
    if (data && typeof data === 'object' && 'code' in data && data.code !== 200) {
      // /auth/* 留给 auth store 自己处理(login.vue 有详细错误分支)
      if (res.config?.url?.includes('/auth/')) return res
      return Promise.reject(new Error(data.message || `业务异常 code=${data.code}`))
    }
    return res
  },
  err => Promise.reject(err)  // HTTP 错误继续往下传
)

let isRefreshing = false
let refreshQueue = []

axios.interceptors.response.use(
  res => res,
  async err => {
    const originalRequest = err.config
    // 跳过 auth 端点自身的 401，避免死循环
    if (originalRequest.url?.includes('/auth/')) {
      return Promise.reject(err)
    }
    if (err.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          refreshQueue.push({ resolve, reject, config: originalRequest })
        })
      }
      originalRequest._retry = true
      isRefreshing = true
      try {
        const refreshToken = localStorage.getItem('refreshToken')
        if (!refreshToken) throw new Error('no refresh token')
        const res = await axios.post('/auth/refresh', { refreshToken })
        const newToken = res.data.data.accessToken
        localStorage.setItem('accessToken', newToken)
        axios.defaults.headers.common['Authorization'] = 'Bearer ' + newToken
        refreshQueue.forEach(({ resolve, config }) => {
          config.headers.Authorization = 'Bearer ' + newToken
          config._retry = true
          resolve(axios(config))
        })
        refreshQueue = []
        originalRequest.headers.Authorization = 'Bearer ' + newToken
        return axios(originalRequest)
      } catch {
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('user')
        refreshQueue.forEach(({ reject }) => reject(new Error('refresh failed')))
        refreshQueue = []
        return Promise.reject(err)
      } finally { isRefreshing = false }
    }
    return Promise.reject(err)
  }
)

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.mount('#app')

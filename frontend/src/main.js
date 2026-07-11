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

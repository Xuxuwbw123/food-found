import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/admin',
  timeout: 15000
})

// 自动附带 JWT Token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken')
  if (token) config.headers.Authorization = 'Bearer ' + token
  return config
})

// Token 刷新队列（防止并发刷新）
let isRefreshing = false
let refreshQueue = []

// 执行 token 刷新
async function tryRefreshToken() {
  const refreshToken = localStorage.getItem('refreshToken')
  if (!refreshToken) return null
  const res = await axios.post('/auth/refresh', { refreshToken })
  if (res.data?.code === 200) {
    const newToken = res.data.data.accessToken
    localStorage.setItem('accessToken', newToken)
    return newToken
  }
  return null
}

request.interceptors.response.use(
  (res) => {
    const data = res.data
    if (data.code === 200) return data
    ElMessage.error(data.message || '请求失败')
    return Promise.reject(new Error(data.message))
  },
  async (err) => {
    const originalRequest = err.config

    if (err.response?.status === 401 && !originalRequest._retry) {
      // 如果正在刷新 token，排队等待
      if (isRefreshing) {
        return new Promise(resolve => {
          refreshQueue.push(token => {
            originalRequest.headers.Authorization = 'Bearer ' + token
            originalRequest._retry = true
            resolve(request(originalRequest))
          })
        })
      }

      originalRequest._retry = true
      isRefreshing = true

      try {
        const newToken = await tryRefreshToken()
        if (newToken) {
          // 通知排队请求
          refreshQueue.forEach(cb => cb(newToken))
          refreshQueue = []
          // 重试原请求
          originalRequest.headers.Authorization = 'Bearer ' + newToken
          return request(originalRequest)
        }
        // 刷新失败，清除登录态
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('user')
        ElMessage.error('登录已过期，请重新登录')
        window.location.href = '/login'
        return Promise.reject(err)
      } catch {
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('user')
        ElMessage.error('登录已过期，请重新登录')
        window.location.href = '/login'
        return Promise.reject(err)
      } finally {
        isRefreshing = false
      }
    }

    // 区分不同错误类型
    if (!err.response) {
      ElMessage.error('网络连接失败，请检查网络')
    } else if (err.response.status === 403) {
      ElMessage.error('无权访问此资源')
    } else if (err.response.status >= 500) {
      ElMessage.error('服务器错误，请稍后重试')
    } else {
      ElMessage.error('请求失败，请稍后重试')
    }
    return Promise.reject(err)
  }
)

export default request

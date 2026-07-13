import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useAuthStore = defineStore('auth', () => {
  // 从 localStorage 恢复 session（JWT token 24h 自动过期）
  const savedToken = localStorage.getItem('accessToken') || ''
  const savedUser = localStorage.getItem('user')

  const user = ref(null)
  const accessToken = ref(savedToken)
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')

  // 检查 token 是否过期（解析 JWT payload 中的 exp）
  function isTokenExpired(token) {
    if (!token) return true
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return Date.now() >= payload.exp * 1000
    } catch { return true }
  }

  // 如果 token 已过期，清除登录态
  if (savedToken && isTokenExpired(savedToken)) {
    user.value = null
    accessToken.value = ''
    refreshToken.value = ''
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
  } else if (savedUser && savedToken) {
    try { user.value = JSON.parse(savedUser) } catch { localStorage.removeItem('user') }
  }

  const isLoggedIn = computed(() => !!accessToken.value && !!user.value)
  const isAdmin = computed(() => user.value?.user_type === 3)
  const isFarmer = computed(() => user.value?.user_type === 2)

  // 顶部铃铛 + 消息中心 共享的未读消息数(全局 state,避免两处 ref 各自维护不同步)
  const unreadCount = ref(0)

  async function loadUnreadCount() {
    if (!isLoggedIn.value) { unreadCount.value = 0; return }
    try {
      const r = await axios.get('/api/notice/unread-count')
      unreadCount.value = r.data?.data?.count || 0
    } catch { /* 接口失败时保留上次值,避免抖动 */ }
  }

  function decrementUnreadCount() {
    if (unreadCount.value > 0) unreadCount.value--
  }

  function resetUnreadCount() {
    unreadCount.value = 0
  }

  async function login(username, password) {
    const res = await axios.post('/auth/login', { username, password })
    if (res.data.code !== 200) throw new Error(res.data.message || '登录失败')
    const data = res.data.data
    accessToken.value = data.accessToken
    refreshToken.value = data.refreshToken
    localStorage.setItem('accessToken', accessToken.value)
    localStorage.setItem('refreshToken', refreshToken.value)
    user.value = data.user
    localStorage.setItem('user', JSON.stringify(data.user))
    // 全局设置 axios 默认 header
    axios.defaults.headers.common['Authorization'] = 'Bearer ' + accessToken.value
    return data.user
  }

  async function tryRefresh() {
    if (!refreshToken.value) return false
    try {
      const res = await axios.post('/auth/refresh', { refreshToken: refreshToken.value })
      if (res.data.code === 200) {
        accessToken.value = res.data.data.accessToken
        refreshToken.value = res.data.data.refreshToken
        localStorage.setItem('accessToken', accessToken.value)
        localStorage.setItem('refreshToken', refreshToken.value)
        axios.defaults.headers.common['Authorization'] = 'Bearer ' + accessToken.value
        return true
      }
    } catch { /* refresh failed */ }
    logout()
    return false
  }

  function logout() {
    user.value = null
    accessToken.value = ''
    refreshToken.value = ''
    unreadCount.value = 0
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
    delete axios.defaults.headers.common['Authorization']
  }

  function restoreSession() {
    const saved = localStorage.getItem('user')
    if (saved && accessToken.value) {
      try { user.value = JSON.parse(saved) } catch { logout() }
      axios.defaults.headers.common['Authorization'] = 'Bearer ' + accessToken.value
    }
  }

  return { user, accessToken, refreshToken, unreadCount, isLoggedIn, isAdmin, isFarmer, login, logout, restoreSession, tryRefresh, loadUnreadCount, decrementUnreadCount, resetUnreadCount }
})

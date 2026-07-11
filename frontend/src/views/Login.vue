<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <img src="/images/logo/logo-color.png" alt="logo" style="height:48px" @error="e=>e.target.style.display='none'" />
        <h2>农产品溯源电商平台</h2>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%" @click="handleLogin" :loading="loading">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <span>没有账号？</span>
        <el-button link type="primary" @click="$router.push('/register')">立即注册</el-button>
        <span style="margin:0 8px;color:#ddd">|</span>
        <el-button link type="primary" @click="$router.push('/')">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const isSubmitting = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  if (isSubmitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  isSubmitting.value = true
  loading.value = true
  try {
    const user = await auth.login(form.username, form.password)
    ElMessage.success('登录成功')
    let redirect = router.currentRoute.value.query.redirect
    if (!redirect) {
      if (user.user_type === 3) redirect = '/admin/dashboard'
      else if (user.user_type === 2) redirect = '/farmer'
      else redirect = '/'
    }
    router.push(redirect)
  } catch (e) {
    console.error('[login]', e)
    const status = e.response?.status
    const data = e.response?.data
    if (status === 401) {
      ElMessage.error(data?.message || '账号或密码错误')
    } else if (status === 400) {
      ElMessage.error(data?.message || '请填写用户名和密码')
    } else if (status === 429) {
      ElMessage.error('操作太频繁，请稍后再试')
    } else if (status >= 500) {
      ElMessage.error('服务异常，请稍后重试')
    } else if (e.message?.includes('Network Error')) {
      ElMessage.error('网络错误，请检查连接')
    } else {
      ElMessage.error('登录失败，请重试')
    }
  } finally {
    loading.value = false
    isSubmitting.value = false
    // DOM 兜底：下一帧强制移除 el-button loading 状态
    setTimeout(() => {
      const btn = document.querySelector('.el-button--primary.is-loading')
      if (btn) { btn.classList.remove('is-loading'); btn.removeAttribute('disabled') }
    }, 0)
  }
}
</script>

<style scoped>
.login-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%); }
.login-card { background: #fff; border-radius: 16px; padding: 40px; width: 400px; box-shadow: 0 8px 40px rgba(0,0,0,.1); }
.login-header { text-align: center; margin-bottom: 30px; }
.login-header h2 { font-size: 20px; color: #333; margin-top: 12px; }
.login-footer { text-align: center; margin-top: 16px; font-size: 13px; color: #999; display: flex; justify-content: center; align-items: center; gap: 8px; }
</style>

<template>
  <div class="register-page">
    <div class="register-card">
      <div class="reg-header">
        <img src="/images/logo/logo-color.png" alt="logo" style="height:40px" @error="e=>e.target.style.display='none'" />
        <h2>注册账号</h2>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" @keyup.enter="handleRegister">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="4-20位字母或数字" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="6-20位密码" size="large" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="password2">
          <el-input v-model="form.password2" type="password" placeholder="再次输入密码" size="large" show-password />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" size="large" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="选填" size="large" />
        </el-form-item>
        <el-alert title="注册即为普通用户，农户卖货功能请在登录后申请开通" type="info" :closable="false" style="margin-bottom:12px" />
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%" @click="handleRegister" :loading="loading">注 册</el-button>
        </el-form-item>
      </el-form>
      <div class="reg-footer">
        <span>已有账号？</span>
        <el-button link type="primary" @click="$router.push('/login')">去登录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', password: '', password2: '', phone: '', nickname: '', userType: 1 })

const validatePass2 = (rule, value, callback) => {
  if (value !== form.password) callback(new Error('两次密码不一致'))
  else callback()
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度4-20位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码长度8-20位', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])/, message: '需包含大小写字母+数字+特殊字符', trigger: 'blur' }
  ],
  password2: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    // 生成唯一ID（使用时间戳+随机数）
    const id = Date.now() * 10000 + Math.floor(Math.random() * 10000)
    await axios.post('/auth/register', {
      id,
      username: form.username,
      password: form.password,
      nickname: form.nickname || form.username,
      phone: form.phone,
      userType: form.userType,
      status: 1
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '注册失败，用户名或手机号可能已存在')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%); }
.register-card { background: #fff; border-radius: 16px; padding: 36px 40px; width: 440px; box-shadow: 0 8px 40px rgba(0,0,0,.1); }
.reg-header { text-align: center; margin-bottom: 24px; }
.reg-header h2 { font-size: 20px; color: #333; margin-top: 8px; }
.reg-footer { text-align: center; margin-top: 8px; font-size: 13px; color: #999; }
</style>

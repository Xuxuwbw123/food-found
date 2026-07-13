<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">AI客服设置</span>
      </div>

      <el-form label-width="100px" style="max-width:600px">
        <el-form-item label="API地址">
          <el-input v-model="form.apiUrl" placeholder="如 https://api.xiaomimimo.com/v1/chat/completions" />
          <div style="color:#999;font-size:12px;margin-top:4px">兼容 OpenAI 格式的接口地址</div>
        </el-form-item>
        <el-form-item label="API Key">
          <el-input v-model="form.apiKey" type="password" show-password placeholder="sk-xxxxx" />
        </el-form-item>
        <el-form-item label="模型名称">
          <el-input v-model="form.model" placeholder="如 mimo-v2.5-pro" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save" :loading="saving">保存配置</el-button>
          <el-button @click="testApi" :loading="testing">测试连接</el-button>
        </el-form-item>
      </el-form>

      <el-divider />

      <h3 style="margin-bottom:12px">使用说明</h3>
      <el-steps direction="vertical" :active="4">
        <el-step title="获取API Key" description="从AI服务商获取API Key（如小米MiMo、OpenAI、硅基流动等）" />
        <el-step title="填写配置" description="输入API地址、Key和模型名称" />
        <el-step title="测试连接" description="点击测试按钮验证配置是否正确" />
        <el-step title="自动生效" description="保存后用户端在线客服将使用新的AI配置" />
      </el-steps>

      <el-divider />

      <h3 style="margin-bottom:12px">支持的AI服务商</h3>
      <el-table :data="providers" border size="small">
        <el-table-column prop="name" label="服务商" width="120" />
        <el-table-column prop="url" label="API地址" />
        <el-table-column prop="model" label="模型名称" width="150" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const saving = ref(false)
const testing = ref(false)
const form = reactive({
  apiUrl: 'https://api.xiaomimimo.com/v1/chat/completions',
  apiKey: '',
  model: 'mimo-v2.5-pro'
})

const providers = [
  { name: '小米MiMo', url: 'https://api.xiaomimimo.com/v1/chat/completions', model: 'mimo-v2.5-pro' },
  { name: 'OpenAI', url: 'https://api.openai.com/v1/chat/completions', model: 'gpt-4o-mini' },
  { name: '硅基流动', url: 'https://api.siliconflow.cn/v1/chat/completions', model: 'Qwen/Qwen2.5-7B-Instruct' },
  { name: '通义千问', url: 'https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions', model: 'qwen-turbo' },
  { name: '智谱AI', url: 'https://open.bigmodel.cn/api/paas/v4/chat/completions', model: 'glm-4-flash' }
]

async function loadConfig() {
  try {
    const r = await axios.get('/admin/config/list')
    const configs = r.data.data || []
    configs.forEach(c => {
      if (c.configKey === 'ai_api_url') form.apiUrl = c.configValue
      if (c.configKey === 'ai_api_key') form.apiKey = c.configValue
      if (c.configKey === 'ai_model') form.model = c.configValue
    })
  } catch {}
}

async function save() {
  saving.value = true
  try {
    await axios.put('/admin/config/update', { configKey: 'ai_api_url', configValue: form.apiUrl })
    await axios.put('/admin/config/update', { configKey: 'ai_api_key', configValue: form.apiKey })
    await axios.put('/admin/config/update', { configKey: 'ai_model', configValue: form.model })
    ElMessage.success('保存成功')
  } catch { ElMessage.error('保存失败') }
  finally { saving.value = false }
}

async function testApi() {
  if (!form.apiKey) { ElMessage.warning('请先填写API Key'); return }
  testing.value = true
  try {
    const r = await axios.post('/admin/chat/test-ai', { apiUrl: form.apiUrl, apiKey: form.apiKey, model: form.model })
    if (r.data.code === 200) {
      ElMessage.success('连接成功：' + r.data.data.reply)
    } else {
      ElMessage.error(r.data.message || '连接失败')
    }
  } catch (e) { ElMessage.error(e.response?.data?.message || '连接失败') }
  finally { testing.value = false }
}

onMounted(loadConfig)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}
</style>

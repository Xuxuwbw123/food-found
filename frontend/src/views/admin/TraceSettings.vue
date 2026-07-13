<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">溯源设置</span>
      </div>

      <el-form label-width="140px" style="max-width:600px">
        <el-form-item label="扫码服务器地址">
          <el-input v-model="baseUrl" placeholder="如 http://192.168.31.79:8088" />
          <div style="color:#999;font-size:12px;margin-top:6px">
            手机扫描溯源码后会访问此地址查看溯源信息。<br />
            请填写手机能访问的 IP 地址（如 http://192.168.31.79:8088）。
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save" :loading="saving">保存</el-button>
        </el-form-item>
      </el-form>

      <el-divider />

      <h3 style="margin-bottom:12px">使用说明</h3>
      <el-steps direction="vertical" :active="4" style="max-width:500px">
        <el-step title="设置服务器地址" description="在此页面设置手机可访问的服务器 IP 地址" />
        <el-step title="农户生成溯源码" description="农户在溯源管理页面批量生成溯源码并下载 PDF" />
        <el-step title="打印贴标" description="将 PDF 中的二维码打印出来，贴在商品包装上" />
        <el-step title="用户扫码验证" description="用户手机扫码即可查看溯源信息和正品认证" />
      </el-steps>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const baseUrl = ref('http://192.168.31.79:8088')
const saving = ref(false)

async function loadConfig() {
  try {
    const r = await axios.get('/admin/config/list')
    const configs = r.data.data || []
    const cfg = configs.find(c => c.configKey === 'qrcode_base_url')
    if (cfg) baseUrl.value = cfg.configValue
  } catch {}
}

async function save() {
  saving.value = true
  try {
    await axios.put('/admin/config/update', { configKey: 'qrcode_base_url', configValue: baseUrl.value })
    ElMessage.success('保存成功')
  } catch { ElMessage.error('保存失败') }
  finally { saving.value = false }
}

onMounted(loadConfig)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}
</style>

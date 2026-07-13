<template>
  <div class="page-container">
    <el-card shadow="never">
      <h2 style="margin-bottom:20px">一物一码 · 防伪溯源</h2>
      <p style="color:#666;margin-bottom:20px">扫描商品包装上的溯源二维码，或手动输入溯源码，即可验证商品真伪并查看溯源信息。</p>

      <el-row :gutter="24">
        <!-- 左侧：输入溯源码 -->
        <el-col :span="12">
          <el-card shadow="hover">
            <h3>验证溯源码</h3>
            <el-input v-model="scanCode" placeholder="请输入溯源码，如 TRACE-1-1689123456789" size="large" clearable style="margin:16px 0">
              <template #append>
                <el-button type="primary" @click="doScan" :loading="scanning">验证</el-button>
              </template>
            </el-input>

            <!-- 验证结果 -->
            <div v-if="scanResult" class="scan-result" :class="scanResult.firstScan ? 'result-first' : 'result-repeat'">
              <div class="result-icon">{{ scanResult.firstScan ? '✓' : '⚠' }}</div>
              <h3>{{ scanResult.firstScan ? '首次验证 · 正品保障' : '重复验证 · 请注意' }}</h3>
              <p v-if="scanResult.firstScan" style="color:#52c41a">该商品为首次验证，是正品溯源商品。</p>
              <p v-else style="color:#faad14">该溯源码已被验证过 {{ scanResult.scanCount }} 次。如非本人操作，请注意商品来源。</p>
              <el-descriptions :column="1" border size="small" style="margin-top:12px">
                <el-descriptions-item label="溯源码">{{ scanCode }}</el-descriptions-item>
                <el-descriptions-item label="商品ID">{{ scanResult.productId }}</el-descriptions-item>
                <el-descriptions-item label="验证次数">第 {{ scanResult.scanCount }} 次</el-descriptions-item>
              </el-descriptions>
              <el-button type="primary" style="margin-top:12px" @click="$router.push('/trace')">查看完整溯源链</el-button>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧：说明 -->
        <el-col :span="12">
          <el-card shadow="hover">
            <h3>防伪说明</h3>
            <el-steps direction="vertical" :active="3" style="margin:16px 0">
              <el-step title="生产环节" description="农户为每个商品生成唯一溯源码，印在包装上" />
              <el-step title="流通环节" description="溯源码记录商品从种植到配送的完整链路" />
              <el-step title="消费环节" description="消费者扫码验证，首次扫码=正品保障" />
            </el-steps>

            <el-divider />
            <h3>如何获取溯源码？</h3>
            <ul style="color:#666;line-height:2;padding-left:20px">
              <li>在商品包装上找到溯源二维码</li>
              <li>使用手机扫描二维码获取溯源码</li>
              <li>或在左侧输入框手动输入溯源码</li>
            </ul>
          </el-card>

          <el-card shadow="hover" style="margin-top:16px">
            <h3>管理员：生成溯源码</h3>
            <p style="color:#999;font-size:13px;margin:8px 0">为商品生成防伪溯源二维码</p>
            <el-form inline>
              <el-form-item label="商品ID">
                <el-input-number v-model="genProductId" :min="1" />
              </el-form-item>
              <el-form-item>
                <el-button type="success" @click="doGenerate" :loading="generating">生成溯源码</el-button>
              </el-form-item>
            </el-form>
            <div v-if="genResult" style="margin-top:12px;padding:12px;background:#f6ffed;border-radius:6px;border:1px solid #b7eb8f">
              <p style="color:#52c41a;font-weight:bold">生成成功！</p>
              <p style="font-size:13px">溯源码: <code style="background:#fff;padding:2px 6px;border-radius:3px">{{ genResult.content }}</code></p>
              <p style="font-size:12px;color:#999;margin-top:4px">请将此溯源码印制在商品包装上</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const scanCode = ref('')
const scanResult = ref(null)
const scanning = ref(false)

const genProductId = ref(1)
const genResult = ref(null)
const generating = ref(false)

async function doScan() {
  if (!scanCode.value.trim()) return ElMessage.warning('请输入溯源码')
  scanning.value = true; scanResult.value = null
  try {
    const r = await axios.post('/api/trace/qrcode/scan', { content: scanCode.value.trim() })
    scanResult.value = r.data.data
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '溯源码不存在')
  } finally { scanning.value = false }
}

async function doGenerate() {
  generating.value = true; genResult.value = null
  try {
    const r = await axios.post('/api/trace/qrcode/generate', { productId: genProductId.value })
    genResult.value = r.data.data
    ElMessage.success('生成成功')
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '生成失败')
  } finally { generating.value = false }
}
</script>

<style scoped>
.page-container { max-width: 1000px; margin: 0 auto; padding: 20px; }
.scan-result { margin-top: 16px; padding: 20px; border-radius: 8px; text-align: center; }
.result-first { background: #f6ffed; border: 1px solid #b7eb8f; }
.result-repeat { background: #fffbe6; border: 1px solid #ffe58f; }
.result-icon { font-size: 48px; margin-bottom: 8px; }
.result-first .result-icon { color: #52c41a; }
.result-repeat .result-icon { color: #faad14; }
</style>

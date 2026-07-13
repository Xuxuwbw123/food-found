<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">溯源地图轨迹</span>
        <div style="display:flex;gap:12px">
          <el-select v-model="traceId" placeholder="选择溯源批次" @change="load" style="width:200px">
            <el-option v-for="t in traces" :key="t.id" :label="t.productName+' - '+t.batchNo" :value="t.id" />
          </el-select>
          <el-button type="primary" @click="showAdd = true" :disabled="!traceId">添加位置</el-button>
        </div>
      </div>

      <!-- 地图区域 -->
      <div ref="mapContainer" class="map-container" v-show="traceId"></div>

      <!-- 位置列表 -->
      <el-table :data="locations" border stripe v-loading="loading" v-if="traceId" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="160" />
        <el-table-column prop="locationType" label="类型" width="120">
          <template #default="{row}">
            <el-tag size="small">{{ {farm:'种植基地',warehouse:'仓储中心',delivery:'配送节点',processing:'加工厂'}[row.locationType]||row.locationType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="locationName" label="名称" width="180" />
        <el-table-column prop="latitude" label="纬度" width="120" />
        <el-table-column prop="longitude" label="经度" width="120" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column label="操作" width="100">
          <template #default="{row}">
            <el-popconfirm title="确定删除?" @confirm="del(row.id)"><template #reference><el-button link type="danger" size="small">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!traceId" description="请先选择溯源批次" />
    </el-card>

    <!-- 添加位置对话框 -->
    <el-dialog title="添加溯源位置" v-model="showAdd" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="位置类型">
          <el-select v-model="form.locationType" style="width:100%">
            <el-option label="种植基地" value="farm" />
            <el-option label="仓储中心" value="warehouse" />
            <el-option label="配送节点" value="delivery" />
            <el-option label="加工厂" value="processing" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称"><el-input v-model="form.locationName" /></el-form-item>
        <el-form-item label="纬度"><el-input-number v-model="form.latitude" :precision="6" :step="0.001" style="width:100%" /></el-form-item>
        <el-form-item label="经度"><el-input-number v-model="form.longitude" :precision="6" :step="0.001" style="width:100%" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item>
          <p style="color:#999;font-size:12px">提示: 可在地图上点击选取坐标</p>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdd=false">取消</el-button>
        <el-button type="primary" @click="doAdd">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const traces = ref([]); const traceId = ref(null); const locations = ref([]); const loading = ref(false)
const showAdd = ref(false); const mapContainer = ref(null)
let map = null; let markers = []

const form = reactive({ locationType: 'farm', locationName: '', latitude: 30.5, longitude: 114.3, description: '' })

async function loadTraces() {
  try { const r = await axios.get('/api/trace/list?pageNum=1&pageSize=100'); traces.value = r.data.data?.records || [] } catch {}
}

async function load() {
  if (!traceId.value) return
  loading.value = true
  try {
    const r = await axios.get('/api/trace/locations/' + traceId.value)
    locations.value = r.data.data || []
    await nextTick()
    renderMap()
  } finally { loading.value = false }
}

function renderMap() {
  if (!mapContainer.value) return
  if (!window.AMap) {
    // 动态加载高德地图 JS API
    const script = document.createElement('script')
    script.src = 'https://webapi.amap.com/maps?v=2.0&key=202efb721c5ede9efc4f1b7343cd0cdd'
    script.onload = () => initMap()
    document.head.appendChild(script)
  } else {
    initMap()
  }
}

function initMap() {
  if (map) { map.destroy(); map = null }
  markers = []
  map = new window.AMap.Map(mapContainer.value, {
    zoom: 12,
    center: locations.value.length ? [locations.value[0].longitude, locations.value[0].latitude] : [114.3, 30.5],
    mapStyle: 'amap://styles/normal'
  })

  // 添加标记
  const typeColors = { farm: '#52c41a', warehouse: '#1890ff', delivery: '#fa8c16', processing: '#722ed1' }
  const typeLabels = { farm: '种植基地', warehouse: '仓储中心', delivery: '配送节点', processing: '加工厂' }

  locations.value.forEach(loc => {
    const marker = new window.AMap.Marker({
      position: [loc.longitude, loc.latitude],
      title: loc.locationName,
      label: {
        content: `<div style="background:${typeColors[loc.locationType]||'#666'};color:#fff;padding:2px 6px;border-radius:4px;font-size:12px;white-space:nowrap">${loc.locationName}</div>`,
        offset: new window.AMap.Pixel(0, -30)
      }
    })
    markers.push(marker)
  })
  if (markers.length) map.add(markers)

  // 点击地图获取坐标
  map.on('click', (e) => {
    form.latitude = parseFloat(e.lnglat.getLat().toFixed(6))
    form.longitude = parseFloat(e.lnglat.getLng().toFixed(6))
    ElMessage.info(`已选取坐标: ${form.latitude}, ${form.longitude}`)
  })

  // 自适应显示所有标记
  if (markers.length > 1) map.setFitView(markers)
}

async function doAdd() {
  if (!form.locationName) return ElMessage.warning('请输入名称')
  try {
    await axios.post('/api/trace/locations', { ...form, traceId: traceId.value })
    ElMessage.success('添加成功'); showAdd.value = false; load()
  } catch { ElMessage.error('添加失败') }
}

async function del(id) {
  try { await axios.delete('/api/trace/locations/' + id); ElMessage.success('已删除'); load() } catch { ElMessage.error('删除失败') }
}

onMounted(loadTraces)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}
.map-container{width:100%;height:450px;border-radius:8px;border:1px solid #e4e7ed;margin-bottom:16px}
</style>

<template>
  <div class="trace-result"><div class="section-content">
    <el-breadcrumb separator="/" style="padding:20px 0">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/trace' }">溯源查询</el-breadcrumb-item>
      <el-breadcrumb-item>溯源结果</el-breadcrumb-item>
    </el-breadcrumb>

    <div v-loading="loading">
      <div v-if="data.traceability" class="result-main">
        <div class="product-summary">
          <div class="summary-left">
            <h1>{{ data.traceability.productName }}</h1>
            <div class="summary-meta">
              <span>批次号：{{ data.traceability.batchNo }}</span>
              <span>溯源码：{{ data.traceability.traceCode }}</span>
              <span>产地：{{ data.traceability.originPlace }}</span>
            </div>
          </div>
          <div class="summary-right"><el-tag type="success" size="large" effect="dark">已认证</el-tag></div>
        </div>

        <!-- 溯源地图 -->
        <div class="trace-map-section" v-if="locations.length">
          <h2 class="section-heading">产地地图</h2>
          <div ref="mapContainer" class="map-container"></div>
        </div>

        <div class="trace-timeline">
          <h2 class="section-heading">溯源记录（种植 → 收获）</h2>
          <el-timeline>
            <!-- 种植 -->
            <el-timeline-item v-if="data.planting" :timestamp="data.planting.plantingDate" placement="top" color="#1a8c3a" size="large">
              <el-card shadow="hover">
                <template #header><div class="card-header"><el-icon color="#1a8c3a"><Sunny /></el-icon> 种植信息</div></template>
                <el-descriptions :column="3" size="small" border>
                  <el-descriptions-item label="种植日期">{{ data.planting.plantingDate }}</el-descriptions-item>
                  <el-descriptions-item label="品种">{{ data.planting.seedVariety || '-' }}</el-descriptions-item>
                  <el-descriptions-item label="种植方式">{{ data.planting.plantingMethod || '-' }}</el-descriptions-item>
                  <el-descriptions-item label="土壤类型">{{ data.planting.soilType || '-' }}</el-descriptions-item>
                  <el-descriptions-item label="操作人">{{ data.planting.operator || '-' }}</el-descriptions-item>
                  <el-descriptions-item label="说明" :span="1">{{ data.planting.description || '-' }}</el-descriptions-item>
                </el-descriptions>
                <div v-if="getImages('种植').length" class="stage-images">
                  <el-image v-for="img in getImages('种植')" :key="img.id" :src="img.imageUrl" style="width:100px;height:80px;border-radius:6px;margin-right:8px" fit="cover" :preview-src-list="getImages('种植').map(i=>i.imageUrl)" />
                </div>
              </el-card>
            </el-timeline-item>

            <!-- 施肥（合并） -->
            <el-timeline-item v-if="data.fertilizerList?.length" :timestamp="data.fertilizerList[0].fertilizeDate" placement="top" color="#67c23a">
              <el-card shadow="hover">
                <template #header><div class="card-header"><el-icon color="#67c23a"><Pouring /></el-icon> 施肥记录（{{ data.fertilizerList.length }}次）</div></template>
                <el-table :data="data.fertilizerList" border size="small" style="width:100%">
                  <el-table-column prop="fertilizeDate" label="日期" width="110" />
                  <el-table-column prop="fertilizerName" label="肥料名称" width="120" />
                  <el-table-column label="用量" width="80"><template #default="{row}">{{ row.dosage }}kg</template></el-table-column>
                  <el-table-column prop="fertilizeMethod" label="方式" width="100" />
                  <el-table-column prop="description" label="说明" />
                </el-table>
                <div v-if="getImages('施肥').length" class="stage-images">
                  <el-image v-for="img in getImages('施肥')" :key="img.id" :src="img.imageUrl" style="width:100px;height:80px;border-radius:6px;margin-right:8px" fit="cover" :preview-src-list="getImages('施肥').map(i=>i.imageUrl)" />
                </div>
              </el-card>
            </el-timeline-item>

            <!-- 农药（合并） -->
            <el-timeline-item v-if="data.pesticideList?.length" :timestamp="data.pesticideList[0].useDate" placement="top" color="#e6a23c">
              <el-card shadow="hover">
                <template #header><div class="card-header"><el-icon color="#e6a23c"><Warning /></el-icon> 农药使用（{{ data.pesticideList.length }}次）</div></template>
                <el-table :data="data.pesticideList" border size="small" style="width:100%">
                  <el-table-column prop="useDate" label="日期" width="110" />
                  <el-table-column prop="pesticideName" label="农药名称" width="140" />
                  <el-table-column label="用量" width="80"><template #default="{row}">{{ row.dosage }}g</template></el-table-column>
                  <el-table-column prop="dilutionRatio" label="稀释比" width="80" />
                  <el-table-column prop="safetyInterval" label="安全间隔" width="80"><template #default="{row}">{{ row.safetyInterval }}天</template></el-table-column>
                  <el-table-column prop="description" label="说明" />
                </el-table>
                <div v-if="getImages('农药').length" class="stage-images">
                  <el-image v-for="img in getImages('农药')" :key="img.id" :src="img.imageUrl" style="width:100px;height:80px;border-radius:6px;margin-right:8px" fit="cover" :preview-src-list="getImages('农药').map(i=>i.imageUrl)" />
                </div>
              </el-card>
            </el-timeline-item>

            <!-- 灌溉（合并） -->
            <el-timeline-item v-if="data.irrigationList?.length" :timestamp="data.irrigationList[0].irrigationDate" placement="top" color="#409eff">
              <el-card shadow="hover">
                <template #header><div class="card-header"><el-icon color="#409eff"><Pouring /></el-icon> 灌溉记录（{{ data.irrigationList.length }}次）</div></template>
                <el-table :data="data.irrigationList" border size="small" style="width:100%">
                  <el-table-column prop="irrigationDate" label="日期" width="110" />
                  <el-table-column prop="waterSource" label="水源" width="80" />
                  <el-table-column label="水量" width="80"><template #default="{row}">{{ row.waterVolume }}m³</template></el-table-column>
                  <el-table-column prop="irrigationType" label="方式" width="80" />
                  <el-table-column prop="duration" label="时长(分)" width="80" />
                  <el-table-column prop="description" label="说明" />
                </el-table>
                <div v-if="getImages('灌溉').length" class="stage-images">
                  <el-image v-for="img in getImages('灌溉')" :key="img.id" :src="img.imageUrl" style="width:100px;height:80px;border-radius:6px;margin-right:8px" fit="cover" :preview-src-list="getImages('灌溉').map(i=>i.imageUrl)" />
                </div>
              </el-card>
            </el-timeline-item>

            <!-- 收获 -->
            <el-timeline-item v-if="data.harvest" :timestamp="data.harvest.harvestDate" placement="top" color="#f56c6c" size="large">
              <el-card shadow="hover">
                <template #header><div class="card-header"><el-icon color="#f56c6c"><Present /></el-icon> 收获信息</div></template>
                <el-descriptions :column="3" size="small" border>
                  <el-descriptions-item label="收获日期">{{ data.harvest.harvestDate }}</el-descriptions-item>
                  <el-descriptions-item label="收获数量">{{ data.harvest.harvestQuantity }}{{ data.harvest.harvestUnit }}</el-descriptions-item>
                  <el-descriptions-item label="收获方式">{{ data.harvest.harvestMethod || '-' }}</el-descriptions-item>
                  <el-descriptions-item label="操作人">{{ data.harvest.operator || '-' }}</el-descriptions-item>
                  <el-descriptions-item label="说明" :span="2">{{ data.harvest.description || '-' }}</el-descriptions-item>
                </el-descriptions>
                <div v-if="getImages('收获').length" class="stage-images">
                  <el-image v-for="img in getImages('收获')" :key="img.id" :src="img.imageUrl" style="width:100px;height:80px;border-radius:6px;margin-right:8px" fit="cover" :preview-src-list="getImages('收获').map(i=>i.imageUrl)" />
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>

      <el-empty v-else description="未找到溯源信息，请确认批次号是否正确" :image-size="120" />
    </div>
  </div></div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { scanTrace } from '../../api/trace'
import axios from 'axios'

const route = useRoute()
const data = ref({})
const images = ref([])
const locations = ref([])
const mapContainer = ref(null)
const loading = ref(false)

const typeEn = { '种植': 'planting', '施肥': 'fertilizer', '农药': 'pesticide', '灌溉': 'irrigation', '收获': 'harvest' }
function getImages(type) {
  const en = typeEn[type] || type
  return images.value.filter(i => {
    const t = (i.imageType || '').toLowerCase()
    return t === en || t === type
  })
}

async function loadData() {
  loading.value = true
  try {
    const batchNo = route.params.batchNo
    const res = await scanTrace(batchNo)
    data.value = res.data
    // 从 scan 返回的 imageList 直接拿图片
    images.value = data.value.imageList || []
    // 如果 scan 没带图片，单独拉取
    const traceId = data.value.traceability?.id
    if (images.value.length === 0 && traceId) {
      try {
        const imgRes = await axios.get(`/api/trace/image/list-by-trace/${traceId}`)
        images.value = imgRes.data.data || []
      } catch {}
    }
    // 加载溯源位置
    if (traceId) {
      try {
        const locRes = await axios.get(`/api/trace/locations/${traceId}`)
        locations.value = locRes.data.data || []
        if (locations.value.length) {
          setTimeout(renderMap, 300)
        }
      } catch {}
    }
  } finally { loading.value = false }
}

function renderMap() {
  if (!mapContainer.value || !window.AMap) {
    const script = document.createElement('script')
    script.src = 'https://webapi.amap.com/maps?v=2.0&key=202efb721c5ede9efc4f1b7343cd0cdd'
    script.onload = () => renderMap()
    document.head.appendChild(script)
    return
  }
  const map = new window.AMap.Map(mapContainer.value, {
    zoom: 12,
    center: [locations.value[0].longitude, locations.value[0].latitude],
    mapStyle: 'amap://styles/normal'
  })
  const typeColors = { farm: '#52c41a', warehouse: '#1890ff', delivery: '#fa8c16', processing: '#722ed1' }
  const markers = locations.value.map(loc => {
    return new window.AMap.Marker({
      position: [loc.longitude, loc.latitude],
      title: loc.locationName,
      label: {
        content: `<div style="background:${typeColors[loc.locationType]||'#666'};color:#fff;padding:2px 6px;border-radius:4px;font-size:12px;white-space:nowrap">${loc.locationName}</div>`,
        offset: new window.AMap.Pixel(0, -30)
      }
    })
  })
  map.add(markers)
  if (markers.length > 1) map.setFitView(markers)
}

onMounted(loadData)
</script>

<style scoped>
.trace-result { background: #f5f6f7; min-height: 80vh; }
.section-content { max-width: 960px; margin: 0 auto; padding: 0 20px 40px; }
.product-summary { background: linear-gradient(135deg, #1a8c3a 0%, #2e7d32 100%); border-radius: 12px; padding: 30px; color: #fff; display: flex; justify-content: space-between; align-items: center; }
.summary-left h1 { font-size: 28px; margin-bottom: 10px; }
.summary-meta { display: flex; gap: 20px; font-size: 14px; opacity: .9; }
.trace-timeline { margin-top: 30px; }
.section-heading { font-size: 20px; font-weight: 600; color: #333; margin-bottom: 20px; padding-left: 12px; border-left: 4px solid #1a8c3a; }
.card-header { display: flex; align-items: center; gap: 8px; font-weight: 600; }
.stage-images { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 12px; padding-top: 10px; border-top: 1px solid #eee; }
.trace-map-section { margin-top: 30px; }
.map-container { width: 100%; height: 400px; border-radius: 12px; border: 1px solid #e4e7ed; overflow: hidden; }
</style>

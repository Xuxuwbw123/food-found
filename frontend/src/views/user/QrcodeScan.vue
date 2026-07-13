<template>
  <div class="scan-page">
    <!-- 顶部 Logo -->
    <header class="scan-header">
      <img src="/images/logo/logo-color.png" alt="农臻溯源" class="logo" />
      <span class="brand">农臻溯源</span>
    </header>

    <!-- 正品认证 -->
    <div class="auth-banner">
      <div class="auth-icon">✓</div>
      <h2>正品认证</h2>
      <p>该商品已通过溯源验证，信息真实可靠</p>
    </div>

    <!-- 商品信息 -->
    <div class="product-card" v-if="trace">
      <h1>{{ trace.productName }}</h1>
      <div class="meta-grid">
        <div class="meta-item"><label>批次号</label><span>{{ trace.batchNo }}</span></div>
        <div class="meta-item"><label>溯源码</label><span>{{ trace.traceCode }}</span></div>
        <div class="meta-item"><label>产地</label><span>{{ trace.originPlace }}</span></div>
      </div>
    </div>

    <!-- 溯源时间轴 -->
    <div class="timeline-section" v-if="trace">
      <h2 class="section-title">溯源记录</h2>

      <!-- 种植 -->
      <div class="timeline-card" v-if="planting">
        <div class="card-dot" style="background:#52c41a"></div>
        <div class="card-icon" style="background:#52c41a">🌱</div>
        <div class="card-body">
          <h3>种植信息</h3>
          <span class="card-date">{{ planting.plantingDate }}</span>
          <div class="card-info">
            <div class="info-row"><label>品种</label><span>{{ planting.seedVariety || '-' }}</span></div>
            <div class="info-row"><label>种植方式</label><span>{{ planting.plantingMethod || '-' }}</span></div>
            <div class="info-row"><label>土壤类型</label><span>{{ planting.soilType || '-' }}</span></div>
            <div class="info-row"><label>操作人</label><span>{{ planting.operator || '-' }}</span></div>
          </div>
          <p v-if="planting.description" class="card-desc">{{ planting.description }}</p>
          <div class="card-images" v-if="getImages('planting').length">
            <img v-for="img in getImages('planting')" :key="img.id" :src="img.imageUrl" @click="previewImage(img.imageUrl)" />
          </div>
        </div>
      </div>

      <!-- 施肥 -->
      <div class="timeline-card" v-if="fertilizerList.length">
        <div class="card-dot" style="background:#67c23a"></div>
        <div class="card-icon" style="background:#67c23a">💧</div>
        <div class="card-body">
          <h3>施肥记录（{{ fertilizerList.length }}次）</h3>
          <span class="card-date">{{ fertilizerList[0].fertilizeDate }}</span>
          <div class="record-list">
            <div class="record-item" v-for="f in fertilizerList" :key="f.id">
              <span class="record-date">{{ f.fertilizeDate }}</span>
              <span class="record-name">{{ f.fertilizerName }}</span>
              <span class="record-dose">{{ f.dosage }}kg</span>
            </div>
          </div>
          <div class="card-images" v-if="getImages('fertilizer').length">
            <img v-for="img in getImages('fertilizer')" :key="img.id" :src="img.imageUrl" @click="previewImage(img.imageUrl)" />
          </div>
        </div>
      </div>

      <!-- 农药 -->
      <div class="timeline-card" v-if="pesticideList.length">
        <div class="card-dot" style="background:#e6a23c"></div>
        <div class="card-icon" style="background:#e6a23c">🧪</div>
        <div class="card-body">
          <h3>农药使用（{{ pesticideList.length }}次）</h3>
          <span class="card-date">{{ pesticideList[0].useDate }}</span>
          <div class="record-list">
            <div class="record-item" v-for="p in pesticideList" :key="p.id">
              <span class="record-date">{{ p.useDate }}</span>
              <span class="record-name">{{ p.pesticideName }}</span>
              <span class="record-dose">{{ p.dosage }}g / 间隔{{ p.safetyInterval }}天</span>
            </div>
          </div>
          <div class="card-images" v-if="getImages('pesticide').length">
            <img v-for="img in getImages('pesticide')" :key="img.id" :src="img.imageUrl" @click="previewImage(img.imageUrl)" />
          </div>
        </div>
      </div>

      <!-- 灌溉 -->
      <div class="timeline-card" v-if="irrigationList.length">
        <div class="card-dot" style="background:#409eff"></div>
        <div class="card-icon" style="background:#409eff">🚿</div>
        <div class="card-body">
          <h3>灌溉记录（{{ irrigationList.length }}次）</h3>
          <span class="card-date">{{ irrigationList[0].irrigationDate }}</span>
          <div class="record-list">
            <div class="record-item" v-for="ir in irrigationList" :key="ir.id">
              <span class="record-date">{{ ir.irrigationDate }}</span>
              <span class="record-name">{{ ir.waterSource || '-' }}</span>
              <span class="record-dose">{{ ir.waterVolume }}m³</span>
            </div>
          </div>
          <div class="card-images" v-if="getImages('irrigation').length">
            <img v-for="img in getImages('irrigation')" :key="img.id" :src="img.imageUrl" @click="previewImage(img.imageUrl)" />
          </div>
        </div>
      </div>

      <!-- 收获 -->
      <div class="timeline-card" v-if="harvest">
        <div class="card-dot" style="background:#f56c6c"></div>
        <div class="card-icon" style="background:#f56c6c">📦</div>
        <div class="card-body">
          <h3>收获信息</h3>
          <span class="card-date">{{ harvest.harvestDate }}</span>
          <div class="card-info">
            <div class="info-row"><label>收获数量</label><span>{{ harvest.harvestQuantity }}{{ harvest.harvestUnit }}</span></div>
            <div class="info-row"><label>收获方式</label><span>{{ harvest.harvestMethod || '-' }}</span></div>
            <div class="info-row"><label>操作人</label><span>{{ harvest.operator || '-' }}</span></div>
          </div>
          <p v-if="harvest.description" class="card-desc">{{ harvest.description }}</p>
          <div class="card-images" v-if="getImages('harvest').length">
            <img v-for="img in getImages('harvest')" :key="img.id" :src="img.imageUrl" @click="previewImage(img.imageUrl)" />
          </div>
        </div>
      </div>
    </div>

    <!-- 地图 -->
    <div class="map-section" v-if="locations.length">
      <h2 class="section-title">产地地图</h2>
      <div ref="mapContainer" class="map-container"></div>
    </div>

    <!-- 底部 -->
    <footer class="scan-footer">
      <p>由 农臻溯源 提供技术支持</p>
    </footer>

    <!-- 图片预览 -->
    <div class="image-preview" v-if="previewUrl" @click="previewUrl = ''">
      <img :src="previewUrl" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const trace = ref(null)
const planting = ref(null)
const fertilizerList = ref([])
const pesticideList = ref([])
const irrigationList = ref([])
const harvest = ref(null)
const images = ref([])
const locations = ref([])
const mapContainer = ref(null)
const previewUrl = ref('')

function getImages(type) {
  return images.value.filter(i => (i.imageType || '').toLowerCase() === type)
}

function previewImage(url) {
  previewUrl.value = url
}

async function loadData() {
  const code = route.params.code
  if (!code) return
  try {
    const r = await axios.get(`/api/trace/qrcode/detail/${code}`)
    const d = r.data.data
    trace.value = d.trace || null
    planting.value = d.planting || null
    fertilizerList.value = d.fertilizerList || []
    pesticideList.value = d.pesticideList || []
    irrigationList.value = d.irrigationList || []
    harvest.value = d.harvest || null
    images.value = d.imageList || []
    locations.value = d.locationList || []
    if (locations.value.length) setTimeout(renderMap, 500)
  } catch (e) {
    console.error('Failed to load', e)
  }
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
  const typeColors = { farm: '#52c41a', warehouse: '#1890ff', delivery: '#faad14', processing: '#722ed1' }
  const markers = locations.value.map(loc => new window.AMap.Marker({
    position: [loc.longitude, loc.latitude],
    label: {
      content: `<div style="background:${typeColors[loc.locationType]||'#666'};color:#fff;padding:2px 6px;border-radius:4px;font-size:12px">${loc.locationName}</div>`,
      offset: new window.AMap.Pixel(0, -30)
    }
  }))
  map.add(markers)
  if (markers.length > 1) map.setFitView(markers)
}

onMounted(loadData)
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }
.scan-page { max-width: 480px; margin: 0 auto; background: #f5f5f5; min-height: 100vh; padding-bottom: 20px; }

.scan-header { background: #fff; padding: 14px 16px; display: flex; align-items: center; gap: 10px; border-bottom: 1px solid #eee; position: sticky; top: 0; z-index: 10; }
.logo { height: 32px; }
.brand { font-size: 18px; font-weight: 700; color: #1a8c3a; }

.auth-banner { background: linear-gradient(135deg, #52c41a, #73d13d); padding: 28px 16px; text-align: center; color: #fff; }
.auth-icon { width: 56px; height: 56px; border-radius: 50%; background: rgba(255,255,255,0.3); display: flex; align-items: center; justify-content: center; font-size: 28px; margin: 0 auto 10px; }
.auth-banner h2 { font-size: 20px; margin-bottom: 4px; }
.auth-banner p { font-size: 13px; opacity: 0.9; }

.product-card { background: #fff; margin: 12px; border-radius: 12px; padding: 16px; }
.product-card h1 { font-size: 18px; color: #333; margin-bottom: 12px; }
.meta-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
.meta-item { display: flex; flex-direction: column; gap: 2px; }
.meta-item label { font-size: 12px; color: #999; }
.meta-item span { font-size: 13px; color: #333; }

.section-title { font-size: 16px; font-weight: 600; color: #333; padding: 16px 12px 8px; padding-left: 24px; border-left: 3px solid #1a8c3a; }

.timeline-section { position: relative; }
.timeline-card { display: flex; gap: 12px; margin: 0 12px 12px; background: #fff; border-radius: 12px; padding: 14px; position: relative; }
.card-dot { position: absolute; left: -6px; top: 20px; width: 10px; height: 10px; border-radius: 50%; border: 2px solid #fff; box-shadow: 0 0 0 2px #e8e8e8; display: none; }
.card-icon { width: 40px; height: 40px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 20px; flex-shrink: 0; }
.card-body { flex: 1; min-width: 0; }
.card-body h3 { font-size: 15px; color: #333; margin-bottom: 2px; }
.card-date { font-size: 12px; color: #999; }
.card-info { margin-top: 8px; }
.info-row { display: flex; gap: 8px; font-size: 13px; padding: 3px 0; }
.info-row label { color: #999; white-space: nowrap; min-width: 56px; }
.info-row span { color: #333; }
.card-desc { font-size: 12px; color: #666; margin-top: 6px; line-height: 1.6; }

.record-list { margin-top: 8px; }
.record-item { display: flex; align-items: center; gap: 8px; font-size: 12px; padding: 5px 0; border-bottom: 1px solid #f5f5f5; }
.record-item:last-child { border-bottom: none; }
.record-date { color: #999; min-width: 70px; }
.record-name { color: #333; flex: 1; }
.record-dose { color: #666; font-size: 11px; }

.card-images { display: flex; gap: 6px; margin-top: 8px; overflow-x: auto; }
.card-images img { width: 70px; height: 52px; object-fit: cover; border-radius: 6px; cursor: pointer; flex-shrink: 0; }

.map-section { margin: 0 12px; }
.map-container { width: 100%; height: 250px; border-radius: 12px; overflow: hidden; }

.scan-footer { text-align: center; padding: 20px 16px 10px; font-size: 11px; color: #ccc; }

.image-preview { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.9); display: flex; align-items: center; justify-content: center; z-index: 999; }
.image-preview img { max-width: 95%; max-height: 95%; object-fit: contain; }
</style>

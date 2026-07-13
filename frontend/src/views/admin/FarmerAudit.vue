<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header"><span class="page-title">农户入驻审核</span></div>
      <el-form :model="query" inline class="search-form">
        <el-form-item label="状态"><el-select v-model="query.status" placeholder="全部" clearable @change="loadData"><el-option label="待审核" :value="0" /><el-option label="已通过" :value="1" /><el-option label="已驳回" :value="2" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">搜索</el-button></el-form-item>
      </el-form>
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;padding:8px 12px;background:#f5f7fa;border-radius:6px">
        <span style="font-size:13px;color:#666">共 {{ total }} 条记录，第 {{ query.pageNum }} 页</span>
        <div>
          <el-button size="small" :disabled="query.pageNum<=1" @click="query.pageNum--;loadData()">上一页</el-button>
          <el-button size="small" :disabled="query.pageNum*query.pageSize>=total" @click="query.pageNum++;loadData()">下一页</el-button>
          <el-select v-model="query.pageSize" size="small" style="width:90px;margin-left:8px" @change="query.pageNum=1;loadData()">
            <el-option :value="10" label="10条/页" />
            <el-option :value="20" label="20条/页" />
            <el-option :value="50" label="50条/页" />
          </el-select>
        </div>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="160" />
        <el-table-column prop="farmerName" label="农场名称" width="150" />
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="province" label="省" width="80" />
        <el-table-column prop="city" label="市" width="80" />
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.auditStatus===1?'success':row.auditStatus===2?'danger':'warning'" size="small">{{row.auditStatus===1?'已通过':row.auditStatus===2?'已驳回':'待审核'}}</el-tag></template></el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{row}">
            <el-button v-if="row.auditStatus===0" link type="success" size="small" @click="audit(row,true)">通过</el-button>
            <el-button v-if="row.auditStatus===0" link type="danger" size="small" @click="audit(row,false)">驳回</el-button>
            <el-button v-if="row.auditStatus===1" link type="warning" size="small" @click="expireQualification(row)">资质过期</el-button>
            <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="农户资质详情" width="650px">
      <el-descriptions :column="2" border v-if="detail.id">
        <el-descriptions-item label="农场名称">{{detail.farmerName}}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{detail.contactPerson}}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{detail.contactPhone}}</el-descriptions-item>
        <el-descriptions-item label="地址">{{detail.province}}{{detail.city}}{{detail.district}} {{detail.address}}</el-descriptions-item>
        <el-descriptions-item label="农场面积">{{detail.farmArea}} 亩</el-descriptions-item>
        <el-descriptions-item label="主营产品" :span="2">{{detail.mainProducts}}</el-descriptions-item>
        <el-descriptions-item label="审核状态" :span="2"><el-tag :type="detail.auditStatus===1?'success':detail.auditStatus===2?'danger':'warning'">{{detail.auditStatus===1?'已通过':detail.auditStatus===2?'已驳回':'待审核'}}</el-tag></el-descriptions-item>
        <el-descriptions-item v-if="detail.auditRemark" label="审核意见" :span="2">{{detail.auditRemark}}</el-descriptions-item>
      </el-descriptions>
      <div v-if="detail.longitude && detail.latitude" style="margin-top:16px">
        <h4 style="margin-bottom:8px">农场位置</h4>
        <div ref="detailMapContainer" style="width:100%;height:300px;border-radius:8px;overflow:hidden"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
const query = reactive({ pageNum:1, pageSize:10, status:null })
const tableData = ref([]); const total = ref(0); const loading = ref(false)
const detailVisible = ref(false); const detail = ref({}); const detailMapContainer = ref(null)

async function loadData() {
  loading.value = true
  try { const params={...query}; Object.keys(params).forEach(k=>(params[k]===''||params[k]===null)&&delete params[k]); const r=await axios.get('/api/admin/farmer/audit/list',{params}); tableData.value=r.data.data?.records||[]; total.value=r.data.data?.total||0 } finally { loading.value = false }
}
async function viewDetail(row) {
  const r = await axios.get(`/api/admin/farmer/audit/detail/${row.id}`)
  detail.value = r.data.data || {}
  detailVisible.value = true
  if (detail.value.longitude && detail.value.latitude) {
    setTimeout(() => renderDetailMap(), 300)
  }
}

function renderDetailMap() {
  if (!detailMapContainer.value || !window.AMap) {
    const script = document.createElement('script')
    script.src = 'https://webapi.amap.com/maps?v=2.0&key=202efb721c5ede9efc4f1b7343cd0cdd'
    script.onload = () => renderDetailMap()
    document.head.appendChild(script)
    return
  }
  const map = new window.AMap.Map(detailMapContainer.value, {
    zoom: 15,
    center: [detail.value.longitude, detail.value.latitude],
    mapStyle: 'amap://styles/normal'
  })
  map.add(new window.AMap.Marker({
    position: [detail.value.longitude, detail.value.latitude],
    label: { content: detail.value.farmerName || '农场位置', offset: new window.AMap.Pixel(0, -30) }
  }))
}
function audit(row, approve) {
  const action = approve ? '通过' : '驳回'
  ElMessageBox.prompt(action==='驳回'?'请输入驳回原因':'确认通过该申请？', action, { confirmButtonText:'确定', cancelButtonText:'取消', inputType: action==='驳回'?'textarea':undefined }).then(async ({value})=>{
    await axios.put(`/api/admin/farmer/audit/${row.id}`, { approve, remark: value||'' })
    ElMessage.success(action+'成功')
    loadData()
  }).catch(()=>{})
}
function expireQualification(row) {
  ElMessageBox.prompt('请输入资质过期原因', '资质过期', { confirmButtonText:'确定', cancelButtonText:'取消', inputType:'textarea', inputPlaceholder:'如：资质到期未续审' }).then(async ({value})=>{
    if (!value) { ElMessage.warning('请填写过期原因'); return }
    await axios.put(`/api/admin/farmer/expire/${row.farmerId}`, { remark: value })
    ElMessage.success('已撤销农户资质')
    loadData()
  }).catch(()=>{})
}
onMounted(loadData)
</script>

<style scoped>
.page-container{animation:fadeIn .3s}@keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-title{font-size:16px;font-weight:600}
.search-form{margin:16px 0}.pagination{display:flex;justify-content:flex-end;margin-top:16px}
</style>
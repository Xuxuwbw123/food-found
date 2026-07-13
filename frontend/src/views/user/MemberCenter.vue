<template>
  <div class="page-container">
    <el-card shadow="never">
      <h2 style="margin-bottom:16px">会员中心</h2>

      <el-row :gutter="16" style="margin-bottom:20px">
        <el-col :span="8">
          <el-card shadow="hover" style="background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;text-align:center">
            <h3 style="margin:0">{{ info.levelName || '普通用户' }}</h3>
            <p style="font-size:13px;opacity:0.8">当前等级</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" style="background:linear-gradient(135deg,#f093fb,#f5576c);color:#fff;text-align:center">
            <h2 style="margin:0">¥{{ info.balance || '0.00' }}</h2>
            <p style="font-size:13px;opacity:0.8">账户余额</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" style="background:linear-gradient(135deg,#43e97b,#38f9d7);color:#fff;text-align:center">
            <h2 style="margin:0">{{ info.points || 0 }}</h2>
            <p style="font-size:13px;opacity:0.8">可用积分</p>
          </el-card>
        </el-col>
      </el-row>

      <el-card shadow="never" style="margin-bottom:16px">
        <h3>会员折扣</h3>
        <p style="color:#f56c6c;font-size:24px;font-weight:bold">{{ (info.discountRate * 10).toFixed(1) }}折</p>
        <p style="color:#999;font-size:13px">累计消费: ¥{{ info.totalSpent || '0.00' }}</p>
      </el-card>

      <h3>充值</h3>
      <el-form label-width="80px" style="margin:12px 0;max-width:400px">
        <el-form-item label="充值金额">
          <el-input-number v-model="rechargeAmount" :min="1" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-radio-group v-model="payType">
            <el-radio-button :value="1">支付宝</el-radio-button>
            <el-radio-button :value="2">微信</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doRecharge" style="width:100%">充值 ¥{{ rechargeAmount }}</el-button>
        </el-form-item>
      </el-form>

      <el-divider />
      <h3>会员等级说明</h3>
      <el-table :data="info.levels || []" border style="margin:12px 0">
        <el-table-column prop="levelName" label="等级" width="120" />
        <el-table-column label="折扣" width="100"><template #default="{row}">{{ (row.discountRate * 10).toFixed(1) }}折</template></el-table-column>
        <el-table-column label="升级门槛" width="120"><template #default="{row}">¥{{ row.upgradeAmount }}</template></el-table-column>
        <el-table-column label="积分倍率" width="100"><template #default="{row}">{{ row.pointsRate }}x</template></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const info = ref({}); const rechargeAmount = ref(100); const payType = ref(1)

async function loadData() {
  try { const r = await axios.get('/api/member/info'); info.value = r.data.data || {} } catch {}
}

async function doRecharge() {
  try {
    await axios.post('/api/member/recharge', { amount: rechargeAmount.value, payType: payType.value })
    ElMessage.success('充值成功'); loadData()
  } catch (e) { ElMessage.error(e.response?.data?.msg || '充值失败') }
}

onMounted(loadData)
</script>
<style scoped>.page-container{max-width:1200px;margin:0 auto;padding:20px}</style>

<template>
  <div class="report-list">
    <el-card>
      <template #header>
        <span>财务报表</span>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>利润表</span>
            </template>
            <el-table :data="profitData" style="width: 100%">
              <el-table-column prop="name" label="项目" />
              <el-table-column prop="amount" label="金额">
                <template #default="{ row }">
                  ¥{{ row.amount }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>资产负债表</span>
            </template>
            <el-table :data="balanceData" style="width: 100%">
              <el-table-column prop="name" label="项目" />
              <el-table-column prop="amount" label="金额">
                <template #default="{ row }">
                  ¥{{ row.amount }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
      
      <el-card style="margin-top: 20px">
        <template #header>
          <span>现金流分析</span>
        </template>
        <div id="cashflow-chart" style="height: 300px;"></div>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { accountApi } from '../../api/account'
import { entryApi } from '../../api/entry'

const profitData = ref([])
const balanceData = ref([])

const loadReports = async () => {
  try {
    const accounts = await accountApi.getAll()
    const entries = await entryApi.getAll()
    
    // 计算利润表数据（简化版）
    const revenue = accounts
      .filter(a => a.category === 'REVENUE')
      .reduce((sum, a) => sum + parseFloat(a.creditAmount || 0), 0)
    
    const expense = accounts
      .filter(a => a.category === 'EXPENSE')
      .reduce((sum, a) => sum + parseFloat(a.debitAmount || 0), 0)
    
    profitData.value = [
      { name: '营业收入', amount: revenue },
      { name: '营业支出', amount: expense },
      { name: '净利润', amount: revenue - expense }
    ]
    
    // 计算资产负债表数据（简化版）
    const asset = accounts
      .filter(a => a.category === 'ASSET')
      .reduce((sum, a) => sum + parseFloat(a.debitAmount || 0), 0)
    
    const liability = accounts
      .filter(a => a.category === 'LIABILITY')
      .reduce((sum, a) => sum + parseFloat(a.creditAmount || 0), 0)
    
    const equity = accounts
      .filter(a => a.category === 'EQUITY')
      .reduce((sum, a) => sum + parseFloat(a.creditAmount || 0), 0)
    
    balanceData.value = [
      { name: '资产', amount: asset },
      { name: '负债', amount: liability },
      { name: '权益', amount: equity },
      { name: '负债+权益', amount: liability + equity }
    ]
  } catch (error) {
    console.error('加载报表数据失败', error)
  }
}

onMounted(() => {
  loadReports()
})
</script>

<style scoped>
.report-list {
  padding: 20px;
}
</style>



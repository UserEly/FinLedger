<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <el-card class="welcome-banner" shadow="never">
      <div class="banner-content">
        <div>
          <h2>欢迎回来，{{ user?.username }}！</h2>
          <p class="banner-desc">今天是 {{ currentDate }}，以下是您的财务概览</p>
        </div>
        <el-icon class="banner-icon" :size="60"><DataAnalysis /></el-icon>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card stat-card-1" shadow="hover" @click="$router.push('/transactions')">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingTransactions }}</div>
              <div class="stat-label">待过账交易</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card stat-card-2" shadow="hover" @click="$router.push('/entries')">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Edit /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.submittedEntries }}</div>
              <div class="stat-label">待审核分录</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card stat-card-3" shadow="hover" @click="$router.push('/payments')">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingPayments }}</div>
              <div class="stat-label">待审批支付</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card stat-card-4" shadow="hover" @click="$router.push('/accounts')">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Wallet /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalAccounts }}</div>
              <div class="stat-label">账户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-row :gutter="20" class="data-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span><el-icon><Document /></el-icon> 最近交易</span>
              <el-button text type="primary" @click="$router.push('/transactions')">查看更多</el-button>
            </div>
          </template>
          <el-table :data="recentTransactions" style="width: 100%" v-loading="loading" :show-header="true">
            <el-table-column prop="supplierClient" label="供应商/客户" min-width="120" />
            <el-table-column prop="totalAmount" label="金额" width="100">
              <template #default="{ row }">
                <span class="amount-text">¥{{ formatAmount(row.totalAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="recentTransactions.length === 0" class="empty-state">
            <el-empty description="暂无交易记录" :image-size="80" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span><el-icon><List /></el-icon> 待处理事项</span>
            </div>
          </template>
          <el-scrollbar height="300px">
            <el-timeline v-if="pendingItems.length > 0">
              <el-timeline-item
                v-for="item in pendingItems"
                :key="item.id"
                :timestamp="item.time"
                placement="top"
                :type="item.type"
              >
                <el-card shadow="hover" class="timeline-card">
                  <h4>{{ item.title }}</h4>
                  <p>{{ item.description }}</p>
                  <el-button
                    v-if="item.action"
                    size="small"
                    type="primary"
                    text
                    @click="handleAction(item)"
                  >
                    {{ item.actionText }}
                  </el-button>
                </el-card>
              </el-timeline-item>
            </el-timeline>
            <div v-else class="empty-state">
              <el-empty description="暂无待处理事项" :image-size="80" />
            </div>
          </el-scrollbar>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { transactionApi } from '../api/transaction'
import { entryApi } from '../api/entry'
import { paymentApi } from '../api/payment'
import { accountApi } from '../api/account'
import { Document, Edit, Money, Wallet, DataAnalysis, List } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const user = computed(() => authStore.user)

const stats = ref({
  pendingTransactions: 0,
  submittedEntries: 0,
  pendingPayments: 0,
  totalAccounts: 0
})

const recentTransactions = ref([])
const pendingItems = ref([])
const loading = ref(false)

const currentDate = computed(() => {
  const date = new Date()
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return parseFloat(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'POSTED': 'success',
    'PAID': 'info'
  }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待过账',
    'POSTED': '已过账',
    'PAID': '已支付'
  }
  return map[status] || status
}

const handleAction = (item) => {
  if (item.route) {
    router.push(item.route)
  }
}

const loadDashboardData = async () => {
  loading.value = true
  try {
    const [transactions, entries, payments, accounts] = await Promise.all([
      transactionApi.getPending().catch(() => []),
      entryApi.getSubmitted().catch(() => []),
      paymentApi.getPending().catch(() => []),
      accountApi.getAll().catch(() => [])
    ])

    stats.value = {
      pendingTransactions: transactions.length || 0,
      submittedEntries: entries.length || 0,
      pendingPayments: payments.length || 0,
      totalAccounts: accounts.length || 0
    }

    // 获取最近交易
    const allTransactions = await transactionApi.getAll().catch(() => [])
    recentTransactions.value = allTransactions
      .sort((a, b) => new Date(b.date) - new Date(a.date))
      .slice(0, 5)

    // 构建待处理事项
    pendingItems.value = []
    
    if (transactions.length > 0) {
      pendingItems.value.push({
        id: 'pending-transactions',
        title: `有 ${transactions.length} 笔交易待过账`,
        description: '请及时处理待过账的交易记录',
        time: '刚刚',
        type: 'warning',
        action: true,
        actionText: '去处理',
        route: '/transactions'
      })
    }

    if (entries.length > 0) {
      pendingItems.value.push({
        id: 'pending-entries',
        title: `有 ${entries.length} 条分录待审核`,
        description: '请审核待处理的分录',
        time: '刚刚',
        type: 'primary',
        action: true,
        actionText: '去审核',
        route: '/entries'
      })
    }

    if (payments.length > 0) {
      pendingItems.value.push({
        id: 'pending-payments',
        title: `有 ${payments.length} 笔支付待审批`,
        description: '请审批待处理的支付申请',
        time: '刚刚',
        type: 'success',
        action: true,
        actionText: '去审批',
        route: '/payments'
      })
    }
  } catch (error) {
    console.error('加载数据失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.welcome-banner {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.banner-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.banner-content h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.banner-desc {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.banner-icon {
  opacity: 0.3;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-card-1 .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card-2 .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card-3 .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card-4 .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.data-row {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.amount-text {
  font-weight: 600;
  color: #409eff;
}

.empty-state {
  padding: 40px 0;
}

.timeline-card {
  margin-bottom: 8px;
}

.timeline-card h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
}

.timeline-card p {
  margin: 0 0 8px 0;
  font-size: 12px;
  color: #909399;
}

@media (max-width: 768px) {
  .dashboard {
    padding: 12px;
  }

  .banner-content {
    flex-direction: column;
    text-align: center;
  }

  .banner-icon {
    margin-top: 16px;
  }

  .stat-content {
    flex-direction: column;
    text-align: center;
  }
}
</style>

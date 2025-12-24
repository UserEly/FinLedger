<template>
  <div class="payment-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>支付管理</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部" name="all">
          <el-table :data="allPayments" style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="支付ID" width="100" />
            <el-table-column prop="amount" label="支付金额" width="120">
              <template #default="{ row }">
                ¥{{ row.amount }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="paymentDate" label="支付日期" width="180">
              <template #default="{ row }">
                {{ formatDate(row.paymentDate) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="250" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === 'PENDING' && canApprovePayment(user)"
                  size="small"
                  type="success"
                  @click="handleApprove(row)"
                >
                  审批
                </el-button>
                <el-button
                  v-if="row.status === 'PENDING' && canApprovePayment(user)"
                  size="small"
                  type="danger"
                  @click="handleReject(row)"
                >
                  拒绝
                </el-button>
                <el-button
                  v-if="row.status === 'APPROVED' && canCompletePayment(user)"
                  size="small"
                  type="primary"
                  @click="handleComplete(row)"
                >
                  完成支付
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="待审批" name="pending">
          <el-table :data="pendingPayments" style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="支付ID" width="100" />
            <el-table-column prop="amount" label="支付金额" width="120">
              <template #default="{ row }">
                ¥{{ row.amount }}
              </template>
            </el-table-column>
            <el-table-column prop="paymentDate" label="支付日期" width="180">
              <template #default="{ row }">
                {{ formatDate(row.paymentDate) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="250" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="canApprovePayment(user)"
                  size="small"
                  type="success"
                  @click="handleApprove(row)"
                >
                  审批
                </el-button>
                <el-button
                  v-if="canApprovePayment(user)"
                  size="small"
                  type="danger"
                  @click="handleReject(row)"
                >
                  拒绝
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { paymentApi } from '../../api/payment'
import { canApprovePayment, canCompletePayment, isBoss, isManagerOrBoss } from '../../utils/permission'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const user = computed(() => authStore.user)
const userRole = computed(() => authStore.user?.role)

const allPayments = ref([])
const pendingPayments = ref([])
const loading = ref(false)
const activeTab = ref('all')

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'APPROVED': 'info',
    'REJECTED': 'danger',
    'PAID': 'success'
  }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待审批',
    'APPROVED': '已审批',
    'REJECTED': '已拒绝',
    'PAID': '已支付'
  }
  return map[status] || status
}

const loadPayments = async () => {
  loading.value = true
  try {
    const [all, pending] = await Promise.all([
      paymentApi.getAll(),
      paymentApi.getPending()
    ])
    allPayments.value = all
    pendingPayments.value = pending
  } catch (error) {
    ElMessage.error('加载支付列表失败')
  } finally {
    loading.value = false
  }
}

const handleApprove = async (row) => {
  try {
    await paymentApi.approve(row.id, authStore.user.userId)
    ElMessage.success('审批通过')
    loadPayments()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleReject = async (row) => {
  try {
    await paymentApi.reject(row.id, authStore.user.userId)
    ElMessage.success('已拒绝')
    loadPayments()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleComplete = async (row) => {
  try {
    await paymentApi.complete(row.id)
    ElMessage.success('支付完成')
    loadPayments()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadPayments()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>


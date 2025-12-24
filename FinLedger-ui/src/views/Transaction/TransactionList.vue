<template>
  <div class="transaction-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h3><el-icon><Document /></el-icon> 交易管理</h3>
          </div>
          <el-button 
            type="primary" 
            @click="handleCreate"
            v-if="canCreateTransaction"
          >
            <el-icon><Plus /></el-icon>
            新增交易
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchForm.supplierClient"
          placeholder="搜索供应商/客户"
          clearable
          style="width: 200px"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="searchForm.status"
          placeholder="状态筛选"
          clearable
          style="width: 150px"
          @change="handleSearch"
        >
          <el-option label="全部" value="" />
          <el-option label="待过账" value="PENDING" />
          <el-option label="已过账" value="POSTED" />
          <el-option label="已支付" value="PAID" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 240px"
          @change="handleDateChange"
        />
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        :data="filteredTransactions"
        style="width: 100%"
        v-loading="loading"
        stripe
        :default-sort="{ prop: 'date', order: 'descending' }"
      >
        <el-table-column prop="id" label="ID" width="80" sortable />
        <el-table-column prop="date" label="交易日期" width="180" sortable>
          <template #default="{ row }">
            {{ formatDate(row.date) }}
          </template>
        </el-table-column>
        <el-table-column prop="supplierClient" label="供应商/客户" min-width="150" show-overflow-tooltip />
        <el-table-column prop="project" label="项目" min-width="120" show-overflow-tooltip />
        <el-table-column prop="productService" label="产品/服务" min-width="120" show-overflow-tooltip />
        <el-table-column prop="totalAmount" label="总金额" width="120" sortable>
          <template #default="{ row }">
            <span class="amount-text">¥{{ formatAmount(row.totalAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="taxRate" label="税率" width="80">
          <template #default="{ row }">
            <span v-if="row.taxRate">{{ row.taxRate }}%</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button
              size="small"
              text
              type="success"
              @click="handlePost(row)"
              v-if="row.status === 'PENDING' && isAccountant(user)"
            >
              <el-icon><Edit /></el-icon>
              过账
            </el-button>
            <el-button
              size="small"
              text
              type="danger"
              @click="handleDelete(row)"
              v-if="isAccountant(user)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <div v-if="!loading && filteredTransactions.length === 0" class="empty-state">
        <el-empty description="暂无交易记录" :image-size="120">
          <el-button type="primary" @click="handleCreate">创建第一条交易</el-button>
        </el-empty>
      </div>

      <!-- 分页 -->
      <div class="pagination-container" v-if="filteredTransactions.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredTransactions.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="交易详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border v-if="currentTransaction">
        <el-descriptions-item label="交易ID">{{ currentTransaction.id }}</el-descriptions-item>
        <el-descriptions-item label="交易日期">
          {{ formatDate(currentTransaction.date) }}
        </el-descriptions-item>
        <el-descriptions-item label="供应商/客户" :span="2">
          {{ currentTransaction.supplierClient }}
        </el-descriptions-item>
        <el-descriptions-item label="项目">{{ currentTransaction.project || '-' }}</el-descriptions-item>
        <el-descriptions-item label="账期">
          {{ currentTransaction.dueDate ? formatDate(currentTransaction.dueDate) : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="产品/服务">{{ currentTransaction.productService || '-' }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ currentTransaction.quantity || '-' }}</el-descriptions-item>
        <el-descriptions-item label="单价">
          {{ currentTransaction.unitPrice ? '¥' + formatAmount(currentTransaction.unitPrice) : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="总金额">
          <span class="amount-text-large">¥{{ formatAmount(currentTransaction.totalAmount) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="税率">
          {{ currentTransaction.taxRate ? currentTransaction.taxRate + '%' : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentTransaction.status)">
            {{ getStatusText(currentTransaction.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          type="primary"
          @click="handlePost(currentTransaction)"
          v-if="currentTransaction && currentTransaction.status === 'PENDING' && isAccountant(user)"
        >
          立即过账
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { transactionApi } from '../../api/transaction'
import { canCreateTransaction, isAccountant } from '../../utils/permission'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Search,
  Refresh,
  Document,
  View,
  Edit,
  Delete
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const user = computed(() => authStore.user)
const transactions = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const dateRange = ref(null)

const searchForm = reactive({
  supplierClient: '',
  status: ''
})

const detailDialogVisible = ref(false)
const currentTransaction = ref(null)

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

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

// 过滤后的交易列表
const filteredTransactions = computed(() => {
  let result = [...transactions.value]

  // 供应商/客户筛选
  if (searchForm.supplierClient) {
    result = result.filter(item =>
      item.supplierClient?.toLowerCase().includes(searchForm.supplierClient.toLowerCase())
    )
  }

  // 状态筛选
  if (searchForm.status) {
    result = result.filter(item => item.status === searchForm.status)
  }

  // 日期范围筛选
  if (dateRange.value && dateRange.value.length === 2) {
    const [start, end] = dateRange.value
    result = result.filter(item => {
      const itemDate = new Date(item.date)
      return itemDate >= start && itemDate <= end
    })
  }

  // 分页
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return result.slice(start, end)
})

const loadTransactions = async () => {
  loading.value = true
  try {
    transactions.value = await transactionApi.getAll()
  } catch (error) {
    ElMessage.error('加载交易列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  router.push('/transactions/create')
}

const handleView = (row) => {
  currentTransaction.value = row
  detailDialogVisible.value = true
}

const handlePost = (row) => {
  router.push(`/entries/create/${row.id}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除交易记录 "${row.supplierClient}" 吗？`,
      '删除确认',
      {
        type: 'warning',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消'
      }
    )
    await transactionApi.delete(row.id)
    ElMessage.success('删除成功')
    loadTransactions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.response?.data?.message || error.message))
    }
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleReset = () => {
  searchForm.supplierClient = ''
  searchForm.status = ''
  dateRange.value = null
  currentPage.value = 1
}

const handleDateChange = () => {
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

onMounted(() => {
  loadTransactions()
})
</script>

<style scoped>
.transaction-list {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left h3 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.amount-text {
  font-weight: 600;
  color: #409eff;
}

.amount-text-large {
  font-size: 18px;
  font-weight: 700;
  color: #409eff;
}

.empty-state {
  padding: 60px 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .transaction-list {
    padding: 12px;
  }

  .search-bar {
    flex-direction: column;
  }

  .search-bar > * {
    width: 100% !important;
  }

  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>

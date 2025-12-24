<template>
  <div class="entry-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分录管理</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部" name="all">
          <el-table :data="allEntries" style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="分录ID" width="100" />
            <el-table-column prop="summary" label="摘要" />
            <el-table-column prop="totalAmount" label="总金额" width="120">
              <template #default="{ row }">
                ¥{{ row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdDate" label="创建日期" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createdDate) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click="handleView(row)">查看</el-button>
                <el-button
                  v-if="row.status === 'SUBMITTED' && canReviewEntry(user)"
                  size="small"
                  type="success"
                  @click="handleApprove(row)"
                >
                  审核通过
                </el-button>
                <el-button
                  v-if="row.status === 'SUBMITTED' && canReviewEntry(user)"
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
        <el-tab-pane label="待审核" name="submitted">
          <el-table :data="submittedEntries" style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="分录ID" width="100" />
            <el-table-column prop="summary" label="摘要" />
            <el-table-column prop="totalAmount" label="总金额" width="120">
              <template #default="{ row }">
                ¥{{ row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="createdDate" label="创建日期" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createdDate) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click="handleView(row)">查看</el-button>
                <el-button
                  v-if="canReviewEntry(user)"
                  size="small"
                  type="success"
                  @click="handleApprove(row)"
                >
                  审核通过
                </el-button>
                <el-button
                  v-if="canReviewEntry(user)"
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
    
    <el-dialog v-model="viewDialogVisible" title="分录详情" width="800px">
      <div v-if="currentEntry">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="分录ID">{{ currentEntry.id }}</el-descriptions-item>
          <el-descriptions-item label="摘要">{{ currentEntry.summary }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ currentEntry.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentEntry.status)">
              {{ getStatusText(currentEntry.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <el-divider />
        <h4>分录拆分</h4>
        <el-table :data="splits" style="width: 100%">
          <el-table-column prop="accountID" label="科目ID" />
          <el-table-column prop="debitAmount" label="借方金额" />
          <el-table-column prop="creditAmount" label="贷方金额" />
          <el-table-column prop="taxAmount" label="税额" />
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { entryApi } from '../../api/entry'
import { canReviewEntry, isManager } from '../../utils/permission'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const user = computed(() => authStore.user)
const userRole = computed(() => authStore.user?.role)

const allEntries = ref([])
const submittedEntries = ref([])
const loading = ref(false)
const activeTab = ref('all')
const viewDialogVisible = ref(false)
const currentEntry = ref(null)
const splits = ref([])

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const getStatusType = (status) => {
  const map = {
    'DRAFT': 'info',
    'SUBMITTED': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = {
    'DRAFT': '草稿',
    'SUBMITTED': '待审核',
    'APPROVED': '已审核',
    'REJECTED': '已拒绝'
  }
  return map[status] || status
}

const loadEntries = async () => {
  loading.value = true
  try {
    const [all, submitted] = await Promise.all([
      entryApi.getAll(),
      entryApi.getSubmitted()
    ])
    allEntries.value = all
    submittedEntries.value = submitted
  } catch (error) {
    ElMessage.error('加载分录列表失败')
  } finally {
    loading.value = false
  }
}

const handleView = async (row) => {
  currentEntry.value = row
  try {
    splits.value = await entryApi.getSplits(row.id)
    viewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载分录详情失败')
  }
}

const handleApprove = async (row) => {
  try {
    await entryApi.updateStatus(row.id, 'APPROVED')
    ElMessage.success('审核通过')
    loadEntries()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleReject = async (row) => {
  try {
    await entryApi.updateStatus(row.id, 'REJECTED')
    ElMessage.success('已拒绝')
    loadEntries()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadEntries()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>


<template>
  <div class="create-entry">
    <el-card>
      <template #header>
        <span>创建分录 - 交易ID: {{ transactionId }}</span>
      </template>
      
      <div v-if="transaction" class="transaction-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="供应商/客户">{{ transaction.supplierClient }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ transaction.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="税率">{{ transaction.taxRate }}%</el-descriptions-item>
          <el-descriptions-item label="项目">{{ transaction.project }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        style="margin-top: 20px"
      >
        <el-form-item label="摘要" prop="summary">
          <el-input v-model="form.summary" placeholder="请输入摘要" />
        </el-form-item>
        
        <el-divider>分录拆分</el-divider>
        
        <div v-for="(split, index) in form.splits" :key="index" class="split-item">
          <el-card>
            <el-form-item :label="`拆分 ${index + 1}`" :prop="`splits.${index}.accountID`">
              <el-select v-model="split.accountID" placeholder="选择科目" style="width: 100%">
                <el-option
                  v-for="account in accounts"
                  :key="account.id"
                  :label="`${account.code} - ${account.name}`"
                  :value="account.id"
                />
              </el-select>
            </el-form-item>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="借方金额">
                  <el-input-number v-model="split.debitAmount" :min="0" :precision="2" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="贷方金额">
                  <el-input-number v-model="split.creditAmount" :min="0" :precision="2" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="税额">
                  <el-input-number v-model="split.taxAmount" :min="0" :precision="2" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-button type="danger" size="small" @click="removeSplit(index)" v-if="form.splits.length > 1">
              删除
            </el-button>
          </el-card>
        </div>
        
        <el-button type="primary" @click="addSplit" style="margin-bottom: 20px">
          添加拆分
        </el-button>
        
        <el-alert
          :title="`借方总额: ¥${totalDebit.toFixed(2)} | 贷方总额: ¥${totalCredit.toFixed(2)}`"
          :type="isBalanced ? 'success' : 'error'"
          :closable="false"
          style="margin-bottom: 20px"
        />
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading" :disabled="!isBalanced">
            提交
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { transactionApi } from '../../api/transaction'
import { accountApi } from '../../api/account'
import { entryApi } from '../../api/entry'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const transactionId = parseInt(route.params.transactionId)
const transaction = ref(null)
const accounts = ref([])

const form = reactive({
  summary: '',
  totalAmount: 0,
  status: 'DRAFT',
  transactionID: transactionId,
  userID: authStore.user?.userId,
  splits: [
    {
      accountID: null,
      debitAmount: 0,
      creditAmount: 0,
      taxAmount: 0
    }
  ]
})

const rules = {
  summary: [{ required: true, message: '请输入摘要', trigger: 'blur' }]
}

const totalDebit = computed(() => {
  return form.splits.reduce((sum, split) => sum + (parseFloat(split.debitAmount) || 0), 0)
})

const totalCredit = computed(() => {
  return form.splits.reduce((sum, split) => sum + (parseFloat(split.creditAmount) || 0), 0)
})

const isBalanced = computed(() => {
  return Math.abs(totalDebit.value - totalCredit.value) < 0.01
})

const loadTransaction = async () => {
  try {
    transaction.value = await transactionApi.getById(transactionId)
    form.totalAmount = transaction.value.totalAmount
  } catch (error) {
    ElMessage.error('加载交易信息失败')
    router.back()
  }
}

const loadAccounts = async () => {
  try {
    accounts.value = await accountApi.getAll()
  } catch (error) {
    ElMessage.error('加载科目列表失败')
  }
}

const addSplit = () => {
  form.splits.push({
    accountID: null,
    debitAmount: 0,
    creditAmount: 0,
    taxAmount: 0
  })
}

const removeSplit = (index) => {
  form.splits.splice(index, 1)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  if (!isBalanced.value) {
    ElMessage.error('借贷金额不平衡，请检查')
    return
  }
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await entryApi.create({
          entry: {
            summary: form.summary,
            totalAmount: form.totalAmount,
            status: form.status,
            transactionID: form.transactionID,
            userID: form.userID
          },
          splits: form.splits
        })
        ElMessage.success('创建成功')
        router.push('/entries')
      } catch (error) {
        ElMessage.error('创建失败：' + (error.response?.data?.message || error.message))
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  loadTransaction()
  loadAccounts()
})
</script>

<style scoped>
.create-entry {
  padding: 20px;
}

.transaction-info {
  margin-bottom: 20px;
}

.split-item {
  margin-bottom: 20px;
}
</style>



<template>
  <div class="create-transaction">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><Plus /></el-icon>
            <span>新增交易</span>
          </div>
          <el-button text @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="140px"
        label-position="left"
      >
        <el-row :gutter="20">
          <el-col :xs="24" :md="12">
            <el-form-item label="交易日期" prop="date">
              <el-date-picker
                v-model="form.date"
                type="datetime"
                placeholder="选择交易日期"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DDTHH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="账期" prop="dueDate">
              <el-date-picker
                v-model="form.dueDate"
                type="datetime"
                placeholder="选择账期（可选）"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DDTHH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">基本信息</el-divider>

        <el-form-item label="供应商/客户" prop="supplierClient">
          <el-input
            v-model="form.supplierClient"
            placeholder="请输入供应商或客户名称"
            clearable
            maxlength="100"
            show-word-limit
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :xs="24" :md="12">
            <el-form-item label="项目" prop="project">
              <el-input
                v-model="form.project"
                placeholder="请输入项目名称（可选）"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="产品/服务" prop="productService">
              <el-input
                v-model="form.productService"
                placeholder="请输入产品或服务名称（可选）"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">金额信息</el-divider>

        <el-row :gutter="20">
          <el-col :xs="24" :md="8">
            <el-form-item label="数量" prop="quantity">
              <el-input-number
                v-model="form.quantity"
                :min="1"
                :precision="0"
                style="width: 100%"
                placeholder="数量"
                @change="calculateTotal"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="8">
            <el-form-item label="单价" prop="unitPrice">
              <el-input-number
                v-model="form.unitPrice"
                :min="0"
                :precision="2"
                style="width: 100%"
                placeholder="单价"
                @change="calculateTotal"
              >
                <template #prefix>¥</template>
              </el-input-number>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="8">
            <el-form-item label="总金额" prop="totalAmount">
              <el-input-number
                v-model="form.totalAmount"
                :min="0"
                :precision="2"
                style="width: 100%"
                placeholder="总金额"
              >
                <template #prefix>¥</template>
              </el-input-number>
              <div class="form-tip">
                <el-icon><InfoFilled /></el-icon>
                可手动输入或由数量×单价自动计算
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :xs="24" :md="12">
            <el-form-item label="税率" prop="taxRate">
              <el-input-number
                v-model="form.taxRate"
                :min="0"
                :max="100"
                :precision="2"
                style="width: 100%"
                placeholder="税率（%）"
              >
                <template #suffix>%</template>
              </el-input-number>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="税额">
              <el-input
                :value="formatAmount(taxAmount)"
                readonly
                style="width: 100%"
              >
                <template #prefix>¥</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-alert
          v-if="form.totalAmount > 0"
          :title="`合计金额：¥${formatAmount(form.totalAmount)}${taxAmount > 0 ? `（含税：¥${formatAmount(form.totalAmount + taxAmount)}）` : ''}`"
          type="info"
          :closable="false"
          style="margin-bottom: 20px"
        />

        <el-divider />

        <el-form-item>
          <el-button type="primary" size="large" @click="handleSubmit" :loading="loading">
            <el-icon><Check /></el-icon>
            提交
          </el-button>
          <el-button size="large" @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button size="large" @click="$router.back()">
            <el-icon><Close /></el-icon>
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { transactionApi } from '../../api/transaction'
import { ElMessage } from 'element-plus'
import {
  Plus,
  ArrowLeft,
  User,
  InfoFilled,
  Check,
  Refresh,
  Close
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  date: new Date().toISOString().slice(0, 19),
  supplierClient: '',
  project: '',
  dueDate: null,
  productService: '',
  quantity: 1,
  unitPrice: 0,
  totalAmount: 0,
  taxRate: 0,
  status: 'PENDING',
  userID: authStore.user?.userId
})

const rules = {
  date: [{ required: true, message: '请选择交易日期', trigger: 'change' }],
  supplierClient: [
    { required: true, message: '请输入供应商/客户', trigger: 'blur' },
    { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  totalAmount: [
    { required: true, message: '请输入总金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '总金额必须大于0', trigger: 'blur' }
  ]
}

// 计算税额
const taxAmount = computed(() => {
  if (!form.totalAmount || !form.taxRate) return 0
  return parseFloat((form.totalAmount * form.taxRate / 100).toFixed(2))
})

// 自动计算总金额
const calculateTotal = () => {
  if (form.quantity && form.unitPrice) {
    form.totalAmount = parseFloat((form.quantity * form.unitPrice).toFixed(2))
  }
}

const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return parseFloat(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 转换日期格式
        const submitData = {
          ...form,
          date: form.date,
          dueDate: form.dueDate || null
        }
        await transactionApi.create(submitData)
        ElMessage.success('交易创建成功！')
        router.push('/transactions')
      } catch (error) {
        const errorMsg = error.response?.data?.message || error.message || '创建失败'
        ElMessage.error(errorMsg)
        console.error('创建交易失败:', error)
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.warning('请完善必填信息')
    }
  })
}

const handleReset = () => {
  formRef.value?.resetFields()
  form.date = new Date().toISOString().slice(0, 19)
  form.quantity = 1
  form.unitPrice = 0
  form.totalAmount = 0
  form.taxRate = 0
  form.dueDate = null
}
</script>

<style scoped>
.create-transaction {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
}

.header-icon {
  font-size: 20px;
  color: #409eff;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

:deep(.el-divider__text) {
  font-weight: 600;
  color: #606266;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

@media (max-width: 768px) {
  .create-transaction {
    padding: 12px;
  }

  :deep(.el-form) {
    padding: 0;
  }
}
</style>

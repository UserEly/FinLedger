<template>
  <div class="account-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>账户管理</span>
          <el-button 
            type="primary" 
            @click="handleCreate"
            v-if="canManageAccount"
          >
            <el-icon><Plus /></el-icon>
            新增科目
          </el-button>
        </div>
      </template>
      
      <el-table :data="accounts" style="width: 100%" v-loading="loading" row-key="id">
        <el-table-column prop="code" label="科目编码" width="120" />
        <el-table-column prop="name" label="科目名称" />
        <el-table-column prop="category" label="科目类别" width="120">
          <template #default="{ row }">
            <el-tag>{{ getCategoryText(row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="debitAmount" label="借方金额" width="120">
          <template #default="{ row }">
            ¥{{ row.debitAmount || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="creditAmount" label="贷方金额" width="120">
          <template #default="{ row }">
            ¥{{ row.creditAmount || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="currency" label="货币" width="100" />
        <el-table-column label="操作" width="200" fixed="right" v-if="canManageAccount">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="科目编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入科目编码" />
        </el-form-item>
        <el-form-item label="科目名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入科目名称" />
        </el-form-item>
        <el-form-item label="科目类别" prop="category">
          <el-select v-model="form.category" placeholder="请选择类别" style="width: 100%">
            <el-option label="资产" value="ASSET" />
            <el-option label="负债" value="LIABILITY" />
            <el-option label="权益" value="EQUITY" />
            <el-option label="收入" value="REVENUE" />
            <el-option label="费用" value="EXPENSE" />
          </el-select>
        </el-form-item>
        <el-form-item label="货币类型" prop="currency">
          <el-input v-model="form.currency" placeholder="默认CNY" />
        </el-form-item>
        <el-form-item label="父科目ID" prop="parentID">
          <el-input-number v-model="form.parentID" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { accountApi } from '../../api/account'
import { canManageAccount } from '../../utils/permission'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const authStore = useAuthStore()
const user = computed(() => authStore.user)

const accounts = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增科目')
const formRef = ref()
const editingId = ref(null)

const form = reactive({
  code: '',
  name: '',
  category: '',
  currency: 'CNY',
  parentID: null
})

const rules = {
  code: [{ required: true, message: '请输入科目编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入科目名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择科目类别', trigger: 'change' }]
}

const getCategoryText = (category) => {
  const map = {
    'ASSET': '资产',
    'LIABILITY': '负债',
    'EQUITY': '权益',
    'REVENUE': '收入',
    'EXPENSE': '费用'
  }
  return map[category] || category
}

const loadAccounts = async () => {
  loading.value = true
  try {
    accounts.value = await accountApi.getAll()
  } catch (error) {
    ElMessage.error('加载账户列表失败')
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  dialogTitle.value = '新增科目'
  editingId.value = null
  Object.assign(form, {
    code: '',
    name: '',
    category: '',
    currency: 'CNY',
    parentID: null
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑科目'
  editingId.value = row.id
  Object.assign(form, {
    code: row.code,
    name: row.name,
    category: row.category,
    currency: row.currency,
    parentID: row.parentID
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (editingId.value) {
          await accountApi.update(editingId.value, form)
          ElMessage.success('更新成功')
        } else {
          await accountApi.create(form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadAccounts()
      } catch (error) {
        ElMessage.error(editingId.value ? '更新失败' : '创建失败')
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个科目吗？', '提示', {
      type: 'warning'
    })
    await accountApi.delete(row.id)
    ElMessage.success('删除成功')
    loadAccounts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadAccounts()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>


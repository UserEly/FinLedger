<template>
  <div class="profile">
    <el-card>
      <template #header>
        <span>个人中心</span>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        label-width="120px"
        style="max-width: 600px"
      >
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-input v-model="roleText" disabled />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-input v-model="form.avatar" placeholder="请输入头像URL" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  avatar: ''
})

const roleText = computed(() => {
  const map = {
    'ACCOUNTANT': '会计',
    'MANAGER': '财务经理',
    'BOSS': '老板'
  }
  return map[authStore.user?.role] || authStore.user?.role
})

const loadProfile = () => {
  if (authStore.user) {
    form.username = authStore.user.username
    // 这里应该从API加载完整的用户信息
    form.email = ''
    form.avatar = ''
  }
}

const handleSubmit = async () => {
  // 这里应该调用API更新用户信息
  ElMessage.success('保存成功')
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile {
  padding: 20px;
}
</style>



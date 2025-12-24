<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <h2>FinLedger</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <!-- 交易管理：会计可以创建，所有角色可以查看 -->
        <el-menu-item index="/transactions" v-if="canViewTransactions">
          <el-icon><Document /></el-icon>
          <span>交易管理</span>
        </el-menu-item>
        
        <!-- 账户管理：仅财务经理可以管理 -->
        <el-menu-item index="/accounts" v-if="canManageAccount">
          <el-icon><Wallet /></el-icon>
          <span>账户管理</span>
        </el-menu-item>
        
        <!-- 分录管理：会计可以创建，财务经理可以审核 -->
        <el-menu-item index="/entries" v-if="canViewEntries">
          <el-icon><Edit /></el-icon>
          <span>分录管理</span>
        </el-menu-item>
        
        <!-- 支付管理：财务经理和老板可以审批 -->
        <el-menu-item index="/payments" v-if="canViewPayments">
          <el-icon><Money /></el-icon>
          <span>支付管理</span>
        </el-menu-item>
        
        <!-- 报表管理：所有角色可以查看 -->
        <el-menu-item index="/reports" v-if="canViewReport">
          <el-icon><DataAnalysis /></el-icon>
          <span>报表管理</span>
        </el-menu-item>
        
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-right">
          <el-tag :type="getRoleTagType(user?.role)" size="large" style="margin-right: 12px">
            {{ getRoleText(user?.role) }}
          </el-tag>
          <span class="username">{{ user?.username }}</span>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  isAccountant,
  isManager,
  isBoss,
  isManagerOrBoss,
  canManageAccount,
  canViewReport
} from '../utils/permission'

const canViewTransactions = computed(() => {
  return user.value && (isAccountant(user.value) || isManagerOrBoss(user.value))
})

const canViewEntries = computed(() => {
  return user.value && (isAccountant(user.value) || isManager(user.value))
})

const canViewPayments = computed(() => {
  return user.value && isManagerOrBoss(user.value)
})
import {
  House, Document, Wallet, Edit, Money, DataAnalysis, User, ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)
const user = computed(() => authStore.user)

// 权限计算
const canViewTransactions = computed(() => {
  return user.value && (isAccountant(user.value) || isManagerOrBoss(user.value))
})

const canViewEntries = computed(() => {
  return user.value && (isAccountant(user.value) || isManager(user.value))
})

const canViewPayments = computed(() => {
  return user.value && isManagerOrBoss(user.value)
})

const getRoleText = (role) => {
  const map = {
    'ACCOUNTANT': '会计',
    'MANAGER': '财务经理',
    'BOSS': '老板'
  }
  return map[role] || role
}

const getRoleTagType = (role) => {
  const map = {
    'ACCOUNTANT': 'success',
    'MANAGER': 'warning',
    'BOSS': 'danger'
  }
  return map[role] || ''
}

const handleCommand = (command) => {
  if (command === 'logout') {
    authStore.clearAuth()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  overflow-y: auto;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background-color: #2b3a4a;
}

.sidebar-menu {
  border-right: none;
}

.header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  color: #333;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>


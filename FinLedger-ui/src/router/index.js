import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: 'transactions',
        name: 'Transactions',
        component: () => import('../views/Transaction/TransactionList.vue')
      },
      {
        path: 'transactions/create',
        name: 'CreateTransaction',
        component: () => import('../views/Transaction/CreateTransaction.vue')
      },
      {
        path: 'accounts',
        name: 'Accounts',
        component: () => import('../views/Account/AccountList.vue'),
        meta: { requiredRole: 'MANAGER' } // 仅财务经理可以访问
      },
      {
        path: 'entries',
        name: 'Entries',
        component: () => import('../views/Entry/EntryList.vue')
      },
      {
        path: 'entries/create/:transactionId',
        name: 'CreateEntry',
        component: () => import('../views/Entry/CreateEntry.vue')
      },
      {
        path: 'payments',
        name: 'Payments',
        component: () => import('../views/Payment/PaymentList.vue')
      },
      {
        path: 'reports',
        name: 'Reports',
        component: () => import('../views/Report/ReportList.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const user = authStore.user
  
  // 检查是否需要认证
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }
  
  // 已登录用户访问登录/注册页，重定向到首页
  if ((to.path === '/login' || to.path === '/register') && authStore.isAuthenticated) {
    next('/')
    return
  }
  
  // 角色权限检查
  if (to.meta.requiredRole && user) {
    const userRole = user.role
    const requiredRoles = Array.isArray(to.meta.requiredRole) 
      ? to.meta.requiredRole 
      : [to.meta.requiredRole]
    
    if (!requiredRoles.includes(userRole)) {
      // 权限不足，重定向到首页并提示
      next('/')
      return
    }
  }
  
  next()
})

export default router


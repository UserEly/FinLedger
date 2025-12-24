import axios from 'axios'
import { useAuthStore } from '../stores/auth'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 从 localStorage 直接获取 token（避免 Pinia store 初始化问题）
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => response.data,
  error => {
    // 处理 401 未授权错误
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.clearAuth()
      window.location.href = '/login'
      return Promise.reject(error)
    }
    
    // 处理 403 禁止访问错误
    if (error.response?.status === 403) {
      console.error('403 错误 - 可能是 token 无效或过期，请重新登录')
      const authStore = useAuthStore()
      authStore.clearAuth()
      window.location.href = '/login'
      return Promise.reject(error)
    }
    
    // 处理业务错误，确保错误信息正确传递
    if (error.response?.data) {
      // 如果后端返回了错误信息，保持原样
      return Promise.reject(error)
    }
    
    // 网络错误或其他错误
    return Promise.reject(error)
  }
)

export default api


import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'
import { getToken, removeToken } from './auth'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || '/api', // API的基础URL
  timeout: 30000, // 请求超时时间（30秒）
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在请求发送之前做一些处理
    
    // 从store或localStorage获取token
    const token = getToken()
    if (token) {
      // 如果token存在，将其添加到请求头中
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    return config
  },
  error => {
    // 请求错误时做的处理
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 对响应数据做处理
    
    const res = response.data
    
    // 如果响应状态码不是200，说明请求出错
    if (res.code !== 200) {
      // 401：未授权/登录过期
      if (res.code === 401) {
        ElMessageBox.confirm(
          '登录状态已过期，您可以继续留在该页面，或者重新登录',
          '系统提示',
          {
            confirmButtonText: '重新登录',
            cancelButtonText: '取消',
            type: 'warning'
          }
        ).then(() => {
          // 清除token和用户信息
          removeToken()
          const userStore = useUserStore()
          userStore.logout()
          // 跳转到登录页
          router.push('/login')
        }).catch(() => {
          // 用户取消，不跳转
        })
        return Promise.reject(new Error(res.msg || '认证失败'))
      }
      
      // 403：权限不足（不是登录过期）
      if (res.code === 403) {
        ElMessage.warning(res.msg || '您没有权限访问该资源')
        return Promise.reject(new Error(res.msg || '权限不足'))
      }
      
      // 其他错误，显示错误消息
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    } else {
      // 请求成功，直接返回数据
      return res
    }
  },
  error => {
    // 响应错误时做的处理
    
    let message = '请求失败'
    
    if (error.response) {
      // 服务器响应了错误状态码
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          message = data?.msg || '请求参数错误'
          break
        case 401:
          message = '未授权，请重新登录'
          // 清除token并跳转到登录页
          removeToken()
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
          break
        case 403:
          message = data?.msg || '您没有权限访问该资源'
          // 403是权限不足，不是登录过期，不需要跳转登录页
          ElMessage.warning(message)
          return Promise.reject(error)
        case 404:
          message = '请求资源不存在'
          break
        case 500:
          message = data?.msg || '服务器内部错误'
          break
        case 502:
          message = '网关错误'
          break
        case 503:
          message = '服务不可用'
          break
        case 504:
          message = '网关超时'
          break
        default:
          message = data?.msg || `请求失败 (${status})`
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      message = '网络连接失败，请检查网络'
    } else {
      // 发送请求时出了点问题
      message = error.message || '请求失败'
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service


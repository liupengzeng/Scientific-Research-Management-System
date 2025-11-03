import { defineStore } from 'pinia'
import { getToken, setToken, removeToken, getUserInfo, setUserInfo, removeUserInfo } from '@/utils/auth'
import { login, logout, getUserInfo as getUserInfoApi } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: getUserInfo() || null,
    roles: [],
    permissions: []
  }),

  getters: {
    // 是否已登录
    isLoggedIn: (state) => {
      return !!state.token && !!state.userInfo
    },
    // 获取用户名
    username: (state) => {
      return state.userInfo?.username || ''
    },
    // 获取用户ID
    userId: (state) => {
      return state.userInfo?.userId || null
    },
    // 获取真实姓名
    realName: (state) => {
      return state.userInfo?.realName || ''
    },
    // 获取头像
    avatar: (state) => {
      return state.userInfo?.avatar || ''
    }
  },

  actions: {
    // 登录
    async login(loginForm) {
      try {
        const response = await login(loginForm)
        if (response.code === 200) {
          const { token, userInfo } = response.data
          this.token = token
          this.userInfo = userInfo
          this.roles = userInfo.roles || []
          this.permissions = userInfo.permissions || []
          
          // 持久化存储
          setToken(token)
          setUserInfo(userInfo)
          
          return Promise.resolve(response)
        } else {
          return Promise.reject(new Error(response.msg || '登录失败'))
        }
      } catch (error) {
        return Promise.reject(error)
      }
    },

    // 登出
    async logout() {
      try {
        // 调用后端登出接口（可选）
        // await logout()
      } catch (error) {
        // 登出失败也继续清除本地数据
      } finally {
        // 清除本地数据
        this.token = ''
        this.userInfo = null
        this.roles = []
        this.permissions = []
        
        // 清除持久化数据
        removeToken()
        removeUserInfo()
        
        // 跳转到登录页
        router.push('/login')
      }
    },

    // 获取用户信息
    async getUserInfo() {
      try {
        const response = await getUserInfoApi()
        if (response.code === 200) {
          const userInfo = response.data
          this.userInfo = userInfo
          this.roles = userInfo.roles || []
          this.permissions = userInfo.permissions || []
          
          // 更新持久化数据
          setUserInfo(userInfo)
          
          return Promise.resolve(userInfo)
        } else {
          return Promise.reject(new Error(response.msg || '获取用户信息失败'))
        }
      } catch (error) {
        return Promise.reject(error)
      }
    },

    // 重置用户信息
    resetUserInfo() {
      this.token = ''
      this.userInfo = null
      this.roles = []
      this.permissions = []
    },

    // 设置用户信息
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      this.roles = userInfo?.roles || []
      this.permissions = userInfo?.permissions || []
      setUserInfo(userInfo)
    }
  }
})


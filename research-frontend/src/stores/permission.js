import { defineStore } from 'pinia'
import { getMenuList } from '@/api/menu'
import router, { constantRoutes } from '@/router'
import { transformMenusToRoutes } from '@/utils/permission'

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    routes: [],
    addRoutes: [],
    menuList: []
  }),

  getters: {
    // 获取所有路由
    allRoutes: (state) => {
      return constantRoutes.concat(state.addRoutes)
    }
  },

  actions: {
    // 生成路由
    generateRoutes(roles) {
      return new Promise((resolve, reject) => {
        // 获取菜单列表
        getMenuList()
          .then(response => {
            if (response.code === 200) {
              const menuList = response.data || []
              this.menuList = menuList
              
              // 将菜单转换为可注册的前端路由；忽略未知组件，避免错误
              const accessedRoutes = transformMenusToRoutes(menuList)
              this.addRoutes = accessedRoutes
              
              // 动态添加路由
              accessedRoutes.forEach(route => {
                router.addRoute(route)
              })
              
              resolve(accessedRoutes)
            } else {
              reject(new Error('获取菜单列表失败'))
            }
          })
          .catch(error => {
            reject(error)
          })
      })
    },

    // 重置路由
    resetRoutes() {
      this.addRoutes = []
      this.menuList = []
    }
  }
})


import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'
import { getToken, isTokenExpired } from '@/utils/auth'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 配置NProgress
NProgress.configure({ showSpinner: false })

// 白名单路由（不需要登录即可访问）
const whiteList = ['/login', '/404', '/401', '/redirect']

// 常量路由（固定路由，不需要权限）
export const constantRoutes = [
  {
    path: '/redirect',
    component: () => import('@/layout/index.vue'),
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index.vue')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index.vue'),
    hidden: true,
    meta: { title: '登录' }
  },
  {
    path: '/404',
    component: () => import('@/views/error/404.vue'),
    hidden: true,
    meta: { title: '404' }
  },
  {
    path: '/401',
    component: () => import('@/views/error/401.vue'),
    hidden: true,
    meta: { title: '401' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'House', affix: true }
      },
      {
        path: 'system/user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'UserFilled' }
      }
    ]
  }
]

// 创建路由
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRoutes,
  scrollBehavior: () => ({ top: 0 })
})

// 路由守卫 - 前置守卫
router.beforeEach(async (to, from, next) => {
  // 开始进度条
  NProgress.start()

  // 获取token
  const hasToken = getToken()

  if (hasToken) {
    // token 本地过期直接清理并跳转登录
    if (isTokenExpired(hasToken)) {
      const userStore = useUserStore()
      await userStore.logout()
      next(`/login?redirect=${to.path}`)
      NProgress.done()
      return
    }

    // 已登录
    if (to.path === '/login') {
      // 如果已登录，跳转到首页
      next({ path: '/' })
      NProgress.done()
    } else {
      // 获取用户信息
      const userStore = useUserStore()
      const hasRoles = userStore.roles && userStore.roles.length > 0

      if (hasRoles) {
        // 已有角色信息，直接通过
        next()
      } else {
        try {
          // 获取用户信息
          await userStore.getUserInfo()
          const roles = userStore.roles

          // 生成可访问的路由
          const permissionStore = usePermissionStore()
          const accessRoutes = await permissionStore.generateRoutes(roles)

          // 设置replace: true，这样导航就不会留下历史记录
          next({ ...to, replace: true })
        } catch (error) {
          // 获取用户信息失败，清除token并跳转到登录页
          await userStore.logout()
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    }
  } else {
    // 未登录
    if (whiteList.indexOf(to.path) !== -1) {
      // 在白名单中，直接通过
      next()
    } else {
      // 不在白名单中，跳转到登录页
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

// 路由守卫 - 后置守卫
router.afterEach(() => {
  // 结束进度条
  NProgress.done()
})

export default router


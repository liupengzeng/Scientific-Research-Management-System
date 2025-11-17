import { constantRoutes } from '@/router'

/**
 * 将后端菜单转为前端路由
 * 仅注册已存在的页面，未知 component 将被忽略以避免报错
 * @param {Array} menus 后端菜单树
 */
export function transformMenusToRoutes(menus) {
  const routes = []
  const componentMap = {
    Layout: () => import('@/layout/index.vue'),
    'system/user/index': () => import('@/views/system/user/index.vue'),
    'system/dept/index': () => import('@/views/system/dept/index.vue'),
    'system/role/index': () => import('@/views/system/role/index.vue'),
    'system/menu/index': () => import('@/views/system/menu/index.vue'),
    'dashboard/index': () => import('@/views/dashboard/index.vue'),
    'profile/index': () => import('@/views/profile/index.vue'),
  }

  const walk = (nodes) => {
    if (!Array.isArray(nodes)) return []
    return nodes.map(node => {
      const route = {
        path: normalizePath(node),
        name: toRouteName(node.path),
        meta: { title: node.menuName, icon: node.icon || undefined },
      }
      // 组件映射：未知组件则跳过，避免打包时动态 import 报错
      const comp = componentMap[node.component]
      if (node.component === 'Layout') {
        route.component = componentMap['Layout']
        route.redirect = node.children && node.children.length > 0 ? childFirstPath(node.children) : undefined
      } else if (comp) {
        route.component = comp
      } else {
        // 没有可用组件时不返回此节点（但仍处理其子节点作为扁平化注册）
        if (node.children && node.children.length > 0) {
          return walk(node.children)
        }
        return null
      }

      if (node.children && node.children.length > 0) {
        const children = walk(node.children).flat().filter(Boolean)
        if (children.length > 0) route.children = children
      }
      return route
    }).flat().filter(Boolean)
  }

  const dynamicRoutes = walk(menus)
  return dynamicRoutes
}

function normalizePath(node) {
  // 后端 path 已含 /system/user 之类，保持原样；确保以 / 开头
  if (!node?.path) return '/'
  return node.path.startsWith('/') ? node.path : `/${node.path}`
}

function toRouteName(path) {
  return (path || '').replace(/^\//, '').replace(/[\\/]/g, '-').replace(/[^a-zA-Z0-9_-]/g, '')
}

function childFirstPath(children) {
  const first = children && children.length > 0 ? children[0] : null
  if (!first) return undefined
  return normalizePath(first)
}

/**
 * 过滤异步路由
 * @param {Array} routes 路由列表
 * @param {Array} roles 角色列表
 */
export function filterAsyncRoutes(routes, roles) {
  const res = []
  
  routes.forEach(route => {
    const tmp = { ...route }
    
    // 检查路由权限
    if (hasPermission(roles, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, roles)
      }
      res.push(tmp)
    }
  })
  
  return res
}

/**
 * 检查是否有权限
 * @param {Array} roles 角色列表
 * @param {Object} route 路由对象
 */
function hasPermission(roles, route) {
  if (route.meta && route.meta.roles) {
    return roles.some(role => route.meta.roles.includes(role))
  } else {
    return true
  }
}

/**
 * 检查路由权限
 * @param {Array} roles 角色列表
 * @param {String} permission 权限标识
 */
export function checkPermission(roles, permission) {
  if (!permission) {
    return true
  }
  // 这里可以根据实际需求实现权限检查逻辑
  return true
}


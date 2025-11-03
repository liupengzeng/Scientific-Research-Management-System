import { constantRoutes } from '@/router'

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


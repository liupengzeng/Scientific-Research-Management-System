import request from '@/utils/request'

/**
 * 获取当前用户菜单列表（动态路由）
 */
export function getMenuList() {
  return request({
    url: '/system/menu/list',
    method: 'get'
  })
}

/**
 * 菜单管理：查询菜单列表
 */
export function getManageMenuList(params) {
  return request({
    url: '/system/menu/manage/list',
    method: 'get',
    params
  })
}

/**
 * 菜单管理：查询树形结构
 */
export function getManageMenuTree() {
  return request({
    url: '/system/menu/manage/tree',
    method: 'get'
  })
}

export function getMenu(menuId) {
  return request({
    url: `/system/menu/${menuId}`,
    method: 'get'
  })
}

export function addMenu(data) {
  return request({
    url: '/system/menu',
    method: 'post',
    data
  })
}

export function updateMenu(data) {
  return request({
    url: '/system/menu',
    method: 'put',
    data
  })
}

export function deleteMenu(menuId) {
  return request({
    url: `/system/menu/${menuId}`,
    method: 'delete'
  })
}

export function changeMenuStatus(data) {
  return request({
    url: '/system/menu/changeStatus',
    method: 'put',
    data
  })
}

export function getRoleMenuIds(roleId) {
  return request({
    url: `/system/menu/roleMenuIds/${roleId}`,
    method: 'get'
  })
}

export function assignRoleMenu(data) {
  return request({
    url: '/system/menu/assignRoleMenu',
    method: 'post',
    data
  })
}

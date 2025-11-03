import request from '@/utils/request'

/**
 * 获取菜单列表
 */
export function getMenuList() {
  return request({
    url: '/system/menu/list',
    method: 'get'
  })
}

/**
 * 根据角色获取菜单
 * @param {Number} roleId 角色ID
 */
export function getMenuListByRole(roleId) {
  return request({
    url: `/system/menu/role/${roleId}`,
    method: 'get'
  })
}


import request from '@/utils/request'

/**
 * 获取角色列表
 * @param {Object} params 查询条件
 */
export function getRoleList(params) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params
  })
}

/**
 * 获取角色详情
 */
export function getRole(roleId) {
  return request({
    url: `/system/role/${roleId}`,
    method: 'get'
  })
}

/**
 * 新增角色
 */
export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

/**
 * 修改角色
 */
export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

/**
 * 删除角色
 */
export function deleteRole(roleId) {
  return request({
    url: `/system/role/${roleId}`,
    method: 'delete'
  })
}

/**
 * 修改角色状态
 */
export function changeRoleStatus(data) {
  return request({
    url: '/system/role/changeStatus',
    method: 'put',
    data
  })
}


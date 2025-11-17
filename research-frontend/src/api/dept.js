import request from '@/utils/request'

/**
 * 获取部门列表（平铺）
 */
export function getDeptList() {
  return request({
    url: '/system/dept/list',
    method: 'get'
  })
}

/**
 * 获取部门树
 */
export function getDeptTree() {
  return request({
    url: '/system/dept/tree',
    method: 'get'
  })
}

/**
 * 获取部门详情
 */
export function getDept(deptId) {
  return request({
    url: `/system/dept/${deptId}`,
    method: 'get'
  })
}

/**
 * 新增部门
 */
export function addDept(data) {
  return request({
    url: '/system/dept',
    method: 'post',
    data
  })
}

/**
 * 修改部门
 */
export function updateDept(data) {
  return request({
    url: '/system/dept',
    method: 'put',
    data
  })
}

/**
 * 删除部门
 */
export function deleteDept(deptId) {
  return request({
    url: `/system/dept/${deptId}`,
    method: 'delete'
  })
}

/**
 * 修改部门状态
 */
export function changeDeptStatus(data) {
  return request({
    url: '/system/dept/changeStatus',
    method: 'put',
    data
  })
}


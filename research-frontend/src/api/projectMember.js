import request from '@/utils/request'

/**
 * 查询项目成员列表
 * @param {Number} projectId - 项目ID
 */
export function getMemberList(projectId) {
  return request({
    url: '/research/project/member/list',
    method: 'get',
    params: { projectId }
  })
}

/**
 * 添加项目成员
 * @param {Object} data - 成员数据
 */
export function addMember(data) {
  return request({
    url: '/research/project/member',
    method: 'post',
    data
  })
}

/**
 * 更新项目成员
 * @param {Object} data - 成员数据
 */
export function updateMember(data) {
  return request({
    url: '/research/project/member',
    method: 'put',
    data
  })
}

/**
 * 删除项目成员
 * @param {Number} id - 成员ID
 */
export function removeMember(id) {
  return request({
    url: `/research/project/member/${id}`,
    method: 'delete'
  })
}


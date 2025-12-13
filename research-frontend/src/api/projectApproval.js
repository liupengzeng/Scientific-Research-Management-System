import request from '@/utils/request'

/**
 * 审批项目
 * @param {Object} data - { projectId, decision, comment }
 */
export function approveProject(data) {
  return request({
    url: '/research/project/approve',
    method: 'post',
    data
  })
}

/**
 * 查询审批记录
 * @param {Number} projectId - 项目ID
 */
export function getApprovalRecords(projectId) {
  return request({
    url: '/research/project/approve/records',
    method: 'get',
    params: { projectId }
  })
}


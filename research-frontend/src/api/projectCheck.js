import request from '@/utils/request'

/**
 * 提交中检
 * @param {Object} data - 中检数据
 */
export function submitMidCheck(data) {
  return request({
    url: '/research/project/midCheck',
    method: 'post',
    data
  })
}

/**
 * 查询中检记录
 * @param {Number} projectId - 项目ID
 * @param {Object} params - 分页参数
 */
export function getMidCheckList(projectId, params) {
  return request({
    url: '/research/project/midCheck/list',
    method: 'get',
    params: { projectId, ...params }
  })
}


import request from '@/utils/request'

/**
 * 提交结题
 * @param {Object} data - 结题数据
 */
export function submitFinal(data) {
  return request({
    url: '/research/project/finalize',
    method: 'post',
    data
  })
}

/**
 * 查询结题记录
 * @param {Number} projectId - 项目ID
 * @param {Object} params - 分页参数
 */
export function getFinalList(projectId, params) {
  return request({
    url: '/research/project/finalize/list',
    method: 'get',
    params: { projectId, ...params }
  })
}

/**
 * 结题验收
 * @param {Object} data - { projectId, decision, comment }
 */
export function acceptFinal(data) {
  return request({
    url: '/research/project/finalize/accept',
    method: 'post',
    data
  })
}


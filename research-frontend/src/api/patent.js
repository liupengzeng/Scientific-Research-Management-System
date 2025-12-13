import request from '@/utils/request'

/**
 * 分页查询专利列表
 * @param {Object} params - 查询参数
 */
export function listPatents(params) {
  return request({
    url: '/research/patent/list',
    method: 'get',
    params
  })
}

/**
 * 查询专利详情
 * @param {Number} patentId - 专利ID
 */
export function getPatent(patentId) {
  return request({
    url: `/research/patent/${patentId}`,
    method: 'get'
  })
}

/**
 * 新增专利（草稿）
 * @param {Object} data - 专利数据
 */
export function addPatent(data) {
  return request({
    url: '/research/patent',
    method: 'post',
    data
  })
}

/**
 * 更新专利
 * @param {Object} data - 专利数据
 */
export function updatePatent(data) {
  return request({
    url: '/research/patent',
    method: 'put',
    data
  })
}

/**
 * 提交专利
 * @param {Number} patentId - 专利ID
 */
export function submitPatent(patentId) {
  return request({
    url: `/research/patent/submit/${patentId}`,
    method: 'post'
  })
}

/**
 * 专利审核
 * @param {Object} data - 审核数据 { patentId, decision, comment, finalFlag }
 */
export function approvePatent(data) {
  return request({
    url: '/research/patent/approve',
    method: 'post',
    data
  })
}

/**
 * 查询审核记录
 * @param {Number} patentId - 专利ID
 */
export function getApprovalRecords(patentId) {
  return request({
    url: '/research/patent/approve/records',
    method: 'get',
    params: { patentId }
  })
}

/**
 * 删除专利
 * @param {String} patentIds - 专利ID，多个用逗号分隔
 */
export function deletePatents(patentIds) {
  return request({
    url: `/research/patent/${patentIds}`,
    method: 'delete'
  })
}


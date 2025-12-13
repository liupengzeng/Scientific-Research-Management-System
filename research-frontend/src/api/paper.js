import request from '@/utils/request'

/**
 * 分页查询论文列表
 * @param {Object} params - 查询参数
 */
export function listPapers(params) {
  return request({
    url: '/research/paper/list',
    method: 'get',
    params
  })
}

/**
 * 查询论文详情
 * @param {Number} paperId - 论文ID
 */
export function getPaper(paperId) {
  return request({
    url: `/research/paper/${paperId}`,
    method: 'get'
  })
}

/**
 * 新增论文（草稿）
 * @param {Object} data - 论文数据
 */
export function addPaper(data) {
  return request({
    url: '/research/paper',
    method: 'post',
    data
  })
}

/**
 * 更新论文
 * @param {Object} data - 论文数据
 */
export function updatePaper(data) {
  return request({
    url: '/research/paper',
    method: 'put',
    data
  })
}

/**
 * 提交论文
 * @param {Number} paperId - 论文ID
 */
export function submitPaper(paperId) {
  return request({
    url: `/research/paper/submit/${paperId}`,
    method: 'post'
  })
}

/**
 * 论文审核
 * @param {Object} data - 审核数据 { paperId, decision, comment, finalFlag }
 */
export function approvePaper(data) {
  return request({
    url: '/research/paper/approve',
    method: 'post',
    data
  })
}

/**
 * 查询审核记录
 * @param {Number} paperId - 论文ID
 */
export function getApprovalRecords(paperId) {
  return request({
    url: '/research/paper/approve/records',
    method: 'get',
    params: { paperId }
  })
}

/**
 * 删除论文
 * @param {String} paperIds - 论文ID，多个用逗号分隔
 */
export function deletePapers(paperIds) {
  return request({
    url: `/research/paper/${paperIds}`,
    method: 'delete'
  })
}


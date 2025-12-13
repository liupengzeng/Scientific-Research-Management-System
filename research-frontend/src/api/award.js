import request from '@/utils/request'

/**
 * 分页查询获奖列表
 * @param {Object} params - 查询参数
 */
export function listAwards(params) {
  return request({
    url: '/research/award/list',
    method: 'get',
    params
  })
}

/**
 * 查询获奖详情
 * @param {Number} awardId - 获奖ID
 */
export function getAward(awardId) {
  return request({
    url: `/research/award/${awardId}`,
    method: 'get'
  })
}

/**
 * 新增获奖
 * @param {Object} data - 获奖数据
 */
export function addAward(data) {
  return request({
    url: '/research/award',
    method: 'post',
    data
  })
}

/**
 * 更新获奖
 * @param {Object} data - 获奖数据
 */
export function updateAward(data) {
  return request({
    url: '/research/award',
    method: 'put',
    data
  })
}

/**
 * 删除获奖
 * @param {String} awardIds - 获奖ID，多个用逗号分隔
 */
export function deleteAwards(awardIds) {
  return request({
    url: `/research/award/${awardIds}`,
    method: 'delete'
  })
}


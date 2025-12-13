import request from '@/utils/request'

/**
 * 分页查询著作列表
 * @param {Object} params - 查询参数
 */
export function listBooks(params) {
  return request({
    url: '/research/book/list',
    method: 'get',
    params
  })
}

/**
 * 查询著作详情
 * @param {Number} bookId - 著作ID
 */
export function getBook(bookId) {
  return request({
    url: `/research/book/${bookId}`,
    method: 'get'
  })
}

/**
 * 新增著作
 * @param {Object} data - 著作数据
 */
export function addBook(data) {
  return request({
    url: '/research/book',
    method: 'post',
    data
  })
}

/**
 * 更新著作
 * @param {Object} data - 著作数据
 */
export function updateBook(data) {
  return request({
    url: '/research/book',
    method: 'put',
    data
  })
}

/**
 * 删除著作
 * @param {String} bookIds - 著作ID，多个用逗号分隔
 */
export function deleteBooks(bookIds) {
  return request({
    url: `/research/book/${bookIds}`,
    method: 'delete'
  })
}


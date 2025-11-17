import request from '@/utils/request'

/**
 * 获取用户列表（分页查询）
 * @param {Object} params 查询参数 { pageNum, pageSize, username, realName, status }
 */
export function getUserList(params) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params
  })
}

/**
 * 获取用户详情
 * @param {Number} userId 用户ID
 */
export function getUser(userId) {
  return request({
    url: `/system/user/${userId}`,
    method: 'get'
  })
}

/**
 * 新增用户
 * @param {Object} data 用户数据
 */
export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

/**
 * 修改用户
 * @param {Object} data 用户数据
 */
export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

/**
 * 删除用户（支持批量删除）
 * @param {String} userIds 用户ID，多个用逗号分隔，如 "1,2,3"
 */
export function deleteUser(userIds) {
  return request({
    url: `/system/user/${userIds}`,
    method: 'delete'
  })
}

/**
 * 重置用户密码
 * @param {Object} data { userId, password }
 */
export function resetUserPassword(data) {
  return request({
    url: '/system/user/resetPwd',
    method: 'put',
    data
  })
}

/**
 * 修改用户状态
 * @param {Object} data { userId, status }
 */
export function changeUserStatus(data) {
  return request({
    url: '/system/user/changeStatus',
    method: 'put',
    data
  })
}

/**
 * 检查用户名是否唯一
 * @param {String} username 用户名
 * @param {Number} userId 用户ID（编辑时传递，新增时不传）
 */
export function checkUsernameUnique(username, userId) {
  return request({
    url: '/system/user/checkUsernameUnique',
    method: 'get',
    params: {
      username,
      userId: userId || ''
    }
  })
}

/**
 * 检查邮箱是否唯一
 * @param {String} email 邮箱
 * @param {Number} userId 用户ID（编辑时传递，新增时不传）
 */
export function checkEmailUnique(email, userId) {
  return request({
    url: '/system/user/checkEmailUnique',
    method: 'get',
    params: {
      email,
      userId: userId || ''
    }
  })
}

/**
 * 检查手机号是否唯一
 * @param {String} phone 手机号
 * @param {Number} userId 用户ID（编辑时传递，新增时不传）
 */
export function checkPhoneUnique(phone, userId) {
  return request({
    url: '/system/user/checkPhoneUnique',
    method: 'get',
    params: {
      phone,
      userId: userId || ''
    }
  })
}

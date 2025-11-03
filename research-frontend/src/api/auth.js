import request from '@/utils/request'

/**
 * 登录接口
 * @param {Object} data 登录表单数据 { username, password, code, uuid }
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 登出接口
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return request({
    url: '/auth/userInfo',
    method: 'get'
  })
}


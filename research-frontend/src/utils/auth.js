import Cookies from 'js-cookie'

const TokenKey = 'research-token'
const UserInfoKey = 'research-user-info'

/**
 * 获取Token
 */
export function getToken() {
  return Cookies.get(TokenKey) || localStorage.getItem(TokenKey)
}

/**
 * 设置Token
 * @param {string} token Token字符串
 */
export function setToken(token) {
  // 同时存储在Cookie和localStorage中
  Cookies.set(TokenKey, token, { expires: 1 }) // Cookie保存1天
  localStorage.setItem(TokenKey, token)
}

/**
 * 删除Token
 */
export function removeToken() {
  Cookies.remove(TokenKey)
  localStorage.removeItem(TokenKey)
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  const userInfoStr = localStorage.getItem(UserInfoKey)
  if (userInfoStr) {
    try {
      return JSON.parse(userInfoStr)
    } catch (e) {
      // 解析失败，清除无效数据
      localStorage.removeItem(UserInfoKey)
      return null
    }
  }
  return null
}

/**
 * 设置用户信息
 * @param {Object} userInfo 用户信息对象
 */
export function setUserInfo(userInfo) {
  if (userInfo) {
    localStorage.setItem(UserInfoKey, JSON.stringify(userInfo))
  } else {
    localStorage.removeItem(UserInfoKey)
  }
}

/**
 * 删除用户信息
 */
export function removeUserInfo() {
  localStorage.removeItem(UserInfoKey)
}

/**
 * 清除所有认证信息
 */
export function clearAuth() {
  removeToken()
  removeUserInfo()
}

function b64UrlDecode(input) {
  let base64 = input.replace(/-/g, '+').replace(/_/g, '/')
  const pad = base64.length % 4
  if (pad) base64 += '='.repeat(4 - pad)
  return atob(base64)
}

function parseJwt(token) {
  if (!token) return null
  const parts = token.split('.')
  if (parts.length !== 3) return null
  try {
    const payload = JSON.parse(b64UrlDecode(parts[1]))
    return payload
  } catch (e) {
    return null
  }
}

export function isTokenExpired(token) {
  const payload = parseJwt(token)
  if (!payload || !payload.exp) return false
  const now = Math.floor(Date.now() / 1000)
  return now >= payload.exp
}


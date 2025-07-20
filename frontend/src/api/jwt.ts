/**
 * JWT处理工具
 * 用于管理认证令牌，提供获取、设置和清除功能
 */

const TOKEN_KEY = 'access_token'

/**
 * 从localStorage获取token，如果不存在则从sessionStorage获取
 */
export const getToken = (): string | null => {
  const localToken = localStorage.getItem(TOKEN_KEY)
  if (localToken) {
    // 显示令牌前10个字符，用于调试，避免泄露完整令牌
    console.log(`[JWT] 从localStorage成功获取令牌: ${localToken.substring(0, 10)}...`)
    return localToken
  }

  // 从sessionStorage获取备用token
  const sessionToken = sessionStorage.getItem(TOKEN_KEY)
  if (sessionToken) {
    console.log(`[JWT] 从sessionStorage成功获取令牌: ${sessionToken.substring(0, 10)}...`)
    return sessionToken
  }

  console.log('[JWT] 未找到有效令牌')
  return null
}

/**
 * 设置token到localStorage
 */
export const setToken = (token: string): void => {
  localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 设置token到sessionStorage (仅会话期间有效)
 */
export const setSessionToken = (token: string): void => {
  sessionStorage.setItem(TOKEN_KEY, token)
}

/**
 * 清除token
 */
export const removeToken = (): void => {
  localStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(TOKEN_KEY)
}

/**
 * 检查是否有token
 */
export const hasToken = (): boolean => {
  return !!getToken()
}

/**
 * 获取带Authorization的请求头
 */
export const getAuthHeaders = (): Record<string, string> => {
  const token = getToken()
  return token ? { 'Authorization': `Bearer ${token}` } : {}
}

export default {
  getToken,
  setToken,
  setSessionToken,
  removeToken,
  hasToken,
  getAuthHeaders
}

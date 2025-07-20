import axios from 'axios'
import { removeToken } from './jwt'

// 创建API实例，设置基础URL
const api = axios.create({
  baseURL: 'http://localhost:8080'
})

// 注册接口响应类型
export interface RegisterResponse {
  success: boolean
  message?: string
}

// 检查可用性响应类型
export interface AvailabilityResponse {
  available: boolean
}

// 登录请求类型
export interface LoginRequest {
  email: string
  password: string
}

// 登录响应类型
export interface LoginResponse {
  token: string
  userId: number
  username: string
  role: string
}

/**
 * 用户登录
 * @param email 用户邮箱
 * @param password 密码
 * @returns 登录结果
 */
export const login = async (
  email: string,
  password: string
): Promise<{ success: boolean; data?: LoginResponse; message?: string }> => {
  try {
    const response = await api.post('/login', {
      email,
      password
    })

    // 登录成功，存储token
    if (response.data && response.data.token) {
      localStorage.setItem('access_token', response.data.token)
      sessionStorage.setItem('access_token', response.data.token)

      // 保存用户ID
      if (response.data.userId) {
        localStorage.setItem('userId', response.data.userId.toString())
        sessionStorage.setItem('userId', response.data.userId.toString())
      }

      return {
        success: true,
        data: response.data
      }
    }

    return {
      success: true,
      data: response.data
    }
  } catch (error: unknown) {
    if (axios.isAxiosError(error) && error.response) {
      return {
        success: false,
        message: typeof error.response.data === 'string'
          ? error.response.data
          : '登录失败'
      }
    }
    return {
      success: false,
      message: '登录请求失败'
    }
  }
}

/**
 * 用户注册
 * @param username 用户名
 * @param password 密码
 * @param email 邮箱
 * @param role 角色，默认为teacher
 * @returns 注册结果
 */
export const register = async (
  username: string,
  password: string,
  email: string,
  role: string = 'teacher'
): Promise<RegisterResponse> => {
  try {
    const response = await api.post('/api/signup/register', {
      username,
      password,
      email,
      role
    })
    return response.data
  } catch (error: unknown) {
    if (axios.isAxiosError(error) && error.response) {
      return error.response.data
    }
    return { success: false, message: '注册请求失败' }
  }
}

/**
 * 检查用户名是否可用
 * @param username 用户名
 * @returns 是否可用
 */
export const checkUsernameAvailable = async (username: string): Promise<boolean> => {
  try {
    const response = await api.get<AvailabilityResponse>(`/api/signup/check-username?username=${encodeURIComponent(username)}`)
    return response.data.available
  } catch (error: unknown) {
    console.error('检查用户名可用性失败:', error)
    return false
  }
}

/**
 * 检查邮箱是否可用
 * @param email 邮箱
 * @returns 是否可用
 */
export const checkEmailAvailable = async (email: string): Promise<boolean> => {
  try {
    const response = await api.get<AvailabilityResponse>(`/api/signup/check-email?email=${encodeURIComponent(email)}`)
    return response.data.available
  } catch (error: unknown) {
    console.error('检查邮箱可用性失败:', error)
    return false
  }
}

/**
 * 用户退出登录
 * 清除localStorage和sessionStorage中的token
 * @returns 退出结果
 */
export const logout = (): { success: boolean; message: string } => {
  try {
    removeToken()
    // 清除用户ID
    localStorage.removeItem('userId')
    sessionStorage.removeItem('userId')
    return {
      success: true,
      message: '退出登录成功'
    }
  } catch (error) {
    console.error('退出登录失败:', error)
    return {
      success: false,
      message: '退出登录失败'
    }
  }
}

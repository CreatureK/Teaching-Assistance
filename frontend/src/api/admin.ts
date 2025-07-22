import axios from 'axios'
import { setToken, setSessionToken, getAuthHeaders } from './jwt'

// 基础URL配置
const API_BASE_URL = 'http://localhost:8080'

// 数据类型定义
export interface AdminLoginDTO {
  username: string
  password: string
}

export interface User {
  id?: number
  username: string
  password?: string
  email: string
  avatarUrl?: string
  role?: string
  status?: string
  isDeleted?: number
  createdAt?: Date
  updatedAt?: Date
}

export interface LoginResponse {
  token: string
  user: User
}

export interface DashboardData {
  userStats: {
    totalUsers: number
    teacherCount: number
    adminCount: number
  }
  courseStats: {
    totalCourses: number
    pendingCount: number
    approvedCount: number
    rejectedCount: number
  }
}

// 系统统计数据
export const getDashboardData = async (): Promise<any> => {
  try {
    const response = await axios.get(`${API_BASE_URL}/admin/statistics/dashboard`, {
      headers: getAuthHeaders()
    })
    return response
  } catch (error) {
    console.error('获取统计数据失败:', error)
    throw new Error('获取统计数据失败')
  }
}

// 管理员API接口
export const adminApi = {
  // 管理员登录
  login: async (loginData: AdminLoginDTO): Promise<LoginResponse> => {
    try {
      const response = await axios.post(`${API_BASE_URL}/admin/login`, loginData)
      const { token, user } = response.data

      // 存储token到localStorage和sessionStorage
      setToken(token)
      setSessionToken(token)

      // 存储用户ID到localStorage和sessionStorage
      if (user.id) {
        localStorage.setItem('user_id', user.id.toString())
        sessionStorage.setItem('user_id', user.id.toString())
      }

      // 存储完整用户信息
      localStorage.setItem('user_info', JSON.stringify(user))
      sessionStorage.setItem('user_info', JSON.stringify(user))

      console.log('[Admin] 登录成功，已存储认证信息')
      return response.data
    } catch (error) {
      if (axios.isAxiosError(error) && error.response) {
        throw new Error(error.response.data || '登录失败')
      }
      throw new Error('网络错误')
    }
  },

  // 获取用户列表
  getUserList: async (): Promise<User[]> => {
    try {
      const response = await axios.get(`${API_BASE_URL}/admin/list`, {
        headers: getAuthHeaders()
      })
      return response.data
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error('获取用户列表失败')
      }
      throw new Error('网络错误')
    }
  },

  // 添加用户
  addUser: async (userData: User): Promise<string> => {
    try {
      const response = await axios.post(`${API_BASE_URL}/admin/user/add`, userData, {
        headers: getAuthHeaders()
      })
      return response.data
    } catch (error) {
      if (axios.isAxiosError(error) && error.response) {
        const errorMessage = typeof error.response.data === 'string'
          ? error.response.data
          : error.response.data?.message || '添加用户失败'
        throw new Error(errorMessage)
      }
      throw new Error('网络错误')
    }
  },

  // 删除用户
  deleteUser: async (id: number): Promise<string> => {
    try {
      const response = await axios.delete(`${API_BASE_URL}/admin/delete/${id}`, {
        headers: getAuthHeaders()
      })
      return response.data
    } catch (error) {
      if (axios.isAxiosError(error) && error.response) {
        throw new Error(error.response.data || '删除用户失败')
      }
      throw new Error('网络错误')
    }
  },

  // 获取单个用户信息
  getUserInfo: async (id: number): Promise<User> => {
    try {
      const response = await axios.get(`${API_BASE_URL}/admin/${id}`, {
        headers: getAuthHeaders()
      })
      return response.data
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error('获取用户信息失败')
      }
      throw new Error('网络错误')
    }
  },

  // 更新用户名
  updateUsername: async (id: number, username: string): Promise<string> => {
    try {
      const response = await axios.put(`${API_BASE_URL}/admin/user/${id}/updateName`, 
        { username },
        { headers: getAuthHeaders() }
      )
      return response.data
    } catch (error) {
      if (axios.isAxiosError(error) && error.response) {
        throw new Error(error.response.data || '更新用户名失败')
      }
      throw new Error('网络错误')
    }
  },

  // 更新用户邮箱
  updateEmail: async (id: number, email: string): Promise<string> => {
    try {
      const response = await axios.put(`${API_BASE_URL}/admin/user/${id}/updateEmail`, 
        { email },
        { headers: getAuthHeaders() }
      )
      return response.data
    } catch (error) {
      if (axios.isAxiosError(error) && error.response) {
        throw new Error(error.response.data || '更新邮箱失败')
      }
      throw new Error('网络错误')
    }
  },

  // 获取当前登录用户信息
  getCurrentUser: (): User | null => {
    try {
      const userInfo = localStorage.getItem('user_info') || sessionStorage.getItem('user_info')
      return userInfo ? JSON.parse(userInfo) : null
    } catch (error) {
      console.error('[Admin] 获取当前用户信息失败:', error)
      return null
    }
  },

  // 获取当前用户ID
  getCurrentUserId: (): number | null => {
    const id = localStorage.getItem('user_id') || sessionStorage.getItem('user_id')
    return id ? parseInt(id, 10) : null
  }
}

export default adminApi

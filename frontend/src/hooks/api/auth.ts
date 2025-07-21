// 模拟认证相关API
import { ref } from 'vue'

// 存储登录状态
const isAuthenticated = ref(false)
const currentUser = ref<string | null>(null)

// 默认用户
const DEFAULT_USERNAME = 'teaching'
const DEFAULT_PASSWORD = 'teaching123'

// 模拟登录API
export const getToken = async (credentials: { username: string; password: string }) => {
  // 模拟网络延迟
  await new Promise(resolve => setTimeout(resolve, 800))

  // 验证默认账号
  if (credentials.username === DEFAULT_USERNAME && credentials.password === DEFAULT_PASSWORD) {
    const token = `mock_token_${Date.now()}`

    // 存储token
    localStorage.setItem('access_token', token)
    sessionStorage.setItem('access_token', token)

    // 更新认证状态
    isAuthenticated.value = true
    currentUser.value = credentials.username

    return {
      code: 200,
      message: '登录成功',
      data: {
        data: {
          access_token: token,
          token_type: 'bearer',
          username: credentials.username
        }
      }
    }
  }

  // 登录失败
  return {
    code: 401,
    message: '用户名或密码错误',
    data: null
  }
}

// 模拟注册API
export const signin = async (userData: { username: string; password: string; email: string; role?: string }) => {
  // 模拟网络延迟
  await new Promise(resolve => setTimeout(resolve, 1000))

  console.log('注册用户:', userData.username, '邮箱:', userData.email, '角色:', userData.role || 'teacher')

  // 简单模拟注册成功
  return {
    code: 200,
    message: '注册成功',
    data: null
  }
}

// 检查是否已登录
export const checkAuthentication = () => {
  const token = localStorage.getItem('access_token') || sessionStorage.getItem('access_token')
  return !!token
}

// 退出登录
export const logout = () => {
  // 清除token
  localStorage.removeItem('access_token')
  sessionStorage.removeItem('access_token')
  
  // 清除管理员标记
  localStorage.removeItem('isAdmin')
  
  // 清除用户信息
  localStorage.removeItem('user_info')
  
  // 更新状态
  isAuthenticated.value = false
  currentUser.value = null

  return {
    code: 200,
    message: '已退出登录',
    data: null
  }
}

// 导出认证状态，方便在组件中使用
export { isAuthenticated, currentUser } 
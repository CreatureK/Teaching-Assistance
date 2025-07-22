<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import UserManage from '@/components/UserManage.vue'
import Statistics from '@/components/Statistics.vue'

const router = useRouter()
const isLoggedIn = ref(false)
const isAdmin = ref(false)

onMounted(() => {
  // 检查是否已登录且是管理员
  checkLoginStatus()
  
  // 监听登录状态变化
  window.addEventListener('login-state-changed', checkLoginStatus)
  
  return () => {
    window.removeEventListener('login-state-changed', checkLoginStatus)
  }
})

// 检查登录状态
const checkLoginStatus = () => {
  const token = sessionStorage.getItem('access_token') || localStorage.getItem('access_token')
  isLoggedIn.value = !!token
  isAdmin.value = localStorage.getItem('isAdmin') === 'true'
  
  console.log('[Admin] 检查登录状态:', { isLoggedIn: isLoggedIn.value, isAdmin: isAdmin.value })
  
  if (!isLoggedIn.value) {
    // 如果未登录，跳转到登录页
    console.log('[Admin] 未登录，跳转到登录页')
    router.push('/login')
  } else if (!isAdmin.value) {
    // 如果已登录但不是管理员，跳转到首页
    console.log('[Admin] 不是管理员，跳转到首页')
    router.push('/')
  }
}

// 处理用户选择事件
const handleUserSelected = (user: { id: number, username: string }) => {
  console.log('选中的用户:', user)
  // 这里可以添加更多的用户选择处理逻辑
}
</script>

<template>
  <div class="admin-container">
    <Header />
    
    <div v-if="isLoggedIn" class="content-area">
      <Statistics />
      <UserManage @user-selected="handleUserSelected" />
      
    </div>
  </div>
</template>

<style scoped>
.admin-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: relative;
}

.content-area {
  flex: 1;
  position: relative;
  margin-top: 50px; /* 添加与header高度相等的上边距 */
  min-height: calc(100vh - 50px);
  padding: 0 20px;
}

/* 添加组件之间的间距 */
.content-area > * + * {
  margin-top: -40px;
}
</style>

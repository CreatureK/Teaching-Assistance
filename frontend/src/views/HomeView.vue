<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as authApi from '@/hooks/api/auth'
import Header from '@/components/Header.vue'
import CourseManage from '@/components/CourseManage.vue'
import FunctionSelect from '@/components/FunctionSelect.vue'
import CourseInfo from '@/components/CourseInfo.vue'
import CourseDescription from '@/components/CourseDescription.vue'

const router = useRouter()
const username = ref('')
const isLoggedIn = ref(false)
const showFunctionSelect = ref(false)
const showCourseInfo = ref(false)
const showCourseDescription = ref(false)
const selectedCourseTitle = ref('')
const selectedModuleId = ref('')

// 从本地存储恢复状态
const restoreState = () => {
  const storedShowFunctionSelect = localStorage.getItem('showFunctionSelect')
  const storedShowCourseInfo = localStorage.getItem('showCourseInfo')
  const storedShowCourseDescription = localStorage.getItem('showCourseDescription')
  const storedCourseTitle = localStorage.getItem('selectedCourseTitle')
  const storedModuleId = localStorage.getItem('selectedModuleId')
  
  if (storedShowFunctionSelect === 'true' && storedCourseTitle) {
    showFunctionSelect.value = true
    selectedCourseTitle.value = storedCourseTitle
  }
  
  if (storedShowCourseInfo === 'true') {
    showCourseInfo.value = true
  }

  if (storedShowCourseDescription === 'true' && storedModuleId) {
    showCourseDescription.value = true
    selectedModuleId.value = storedModuleId
  }
}

// 保存状态到本地存储
watch([showFunctionSelect, showCourseInfo, showCourseDescription, selectedCourseTitle, selectedModuleId], () => {
  localStorage.setItem('showFunctionSelect', showFunctionSelect.value.toString())
  localStorage.setItem('showCourseInfo', showCourseInfo.value.toString())
  localStorage.setItem('showCourseDescription', showCourseDescription.value.toString())
  localStorage.setItem('selectedCourseTitle', selectedCourseTitle.value)
  localStorage.setItem('selectedModuleId', selectedModuleId.value)
})

onMounted(() => {
  // 检查是否已登录
  checkLoginStatus()
  
  // 恢复状态
  restoreState()

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
  
  if (!isLoggedIn.value) {
    // 如果未登录，跳转到登录页
    router.push('/login')
  } else {
    // 显示默认用户名
    username.value = authApi.currentUser.value || '教学用户'
  }
}

// 退出登录
const logout = async () => {
  await authApi.logout()
  // 清除状态
  localStorage.removeItem('showFunctionSelect')
  localStorage.removeItem('showCourseInfo')
  localStorage.removeItem('selectedCourseTitle')
  router.push('/login')
}

// 打开课程功能选择界面
const openFunctionSelect = (courseTitle: string) => {
  selectedCourseTitle.value = courseTitle
  showFunctionSelect.value = true
  showCourseInfo.value = false
}

// 返回课程管理界面
const backToCourseManage = () => {
  showFunctionSelect.value = false
  showCourseInfo.value = false
}

// 显示课程信息
const showCourseInfoPanel = () => {
  showCourseInfo.value = true
  showCourseDescription.value = false
}

// 隐藏课程信息
const hideCourseInfoPanel = () => {
  showCourseInfo.value = false
}

// 显示课程模块内容
const showModule = (moduleId: string) => {
  selectedModuleId.value = moduleId
  if (moduleId === 'basic') {
    showCourseDescription.value = true
    showCourseInfo.value = false
  }
  // 其他模块的处理可以在这里添加
}

// 返回功能选择
const backToFunctionSelect = () => {
  showCourseDescription.value = false
}
</script>

<template>
  <div class="home-container">
    <Header />
    
    <div v-if="isLoggedIn" class="content-area">
      <!-- 根据当前状态显示课程管理、功能选择或课程信息 -->
      <CourseManage v-if="!showFunctionSelect && !showCourseInfo && !showCourseDescription" @course-selected="openFunctionSelect" />
      <FunctionSelect 
        v-else-if="showFunctionSelect && !showCourseInfo && !showCourseDescription" 
        :courseTitle="selectedCourseTitle" 
        @back="backToCourseManage"
        @show-course-info="showCourseInfoPanel"
        @show-module="showModule"
      />
      <CourseInfo 
        v-else-if="showCourseInfo" 
        @back="hideCourseInfoPanel" 
      />
      <CourseDescription
        v-else-if="showCourseDescription"
        @back="backToFunctionSelect"
      />
    </div>
  </div>
</template>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: relative;
}

.content-area {
  flex: 1;
  position: relative;
  margin-top: 50px; /* 添加与header高度相等的上边距 */
}
</style>

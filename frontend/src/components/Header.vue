<template>
  <header class="header-container">
    <el-row :gutter="0" type="flex" justify="space-between" align="middle">
      <!-- Logo -->
      <el-col :span="4">
        <div class="logo-container">
          <img src="https://res.cloudinary.com/dm3rouwgn/image/upload/t_media_lib_thumb/rfm1y1en2sqea4rd9ggy" alt="智教未来" class="logo" />
          <span class="logo-text">智教未来</span>
        </div>
      </el-col>
      
      <!-- 搜索框 -->
      <!-- <el-col :span="12" class="search-container">
        <el-input
          v-model="searchQuery"
          placeholder="请输入内容"
          class="search-input"
          :prefix-icon="Search"
          clearable
        />
      </el-col> -->
      
      <!-- 用户信息 -->
      <el-col :span="8">
        <div class="user-actions">
          <el-avatar :size="32" :icon="User" class="user-avatar" />
          <span class="username">用户</span>
          <el-divider direction="vertical" />
          <el-button text type="primary" :icon="SwitchButton" @click="handleLogout" class="logout-btn">
            <span>退出</span>
          </el-button>
          <el-divider direction="vertical" />
          <el-button text type="primary" :icon="InfoFilled">关于</el-button>
        </div>
      </el-col>
    </el-row>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { Search, User, SwitchButton, InfoFilled } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { logout } from '@/api/auth';

const router = useRouter();
const searchQuery = ref('');

// 退出登录功能
const handleLogout = () => {
  // 使用API进行登出
  const result = logout();
  
  localStorage.removeItem('showFunctionSelect')
  localStorage.removeItem('showCourseInfo')
  localStorage.removeItem('selectedCourseTitle')
  
  // 显示退出成功提示
  if (result.success) {
    ElMessage({
      message: result.message,
      type: 'success',
    });
  } else {
    ElMessage({
      message: result.message,
      type: 'error',
    });
  }
  
  // 跳转到登录页
  router.push('/login');
};
</script>

<style scoped>
.header-container {
  padding: 0 20px;
  height: 50px; /* 减小高度从60px到50px */
  background-color: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex; /* 添加flex布局 */
  align-items: center; /* 垂直居中 */
}

/* 添加发光边缘效果 */
.header-container::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0),
    rgba(255, 255, 255, 0.5),
    rgba(52, 152, 219, 0.3),
    rgba(255, 255, 255, 0.5),
    rgba(255, 255, 255, 0)
  );
  z-index: 1;
}

.logo-container {
  display: flex;
  align-items: center;
  height: 100%;
}

.logo {
  height: 28px; /* 稍微减小logo尺寸 */
  width: auto;
  filter: drop-shadow(0 0 3px rgba(255, 255, 255, 0.5));
}

.logo-text {
  margin-left: 8px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

.search-container {
  display: flex;
  justify-content: center;
}

.search-input {
  max-width: 400px;
}

/* 修改搜索框样式使其融入背景 */
:deep(.el-input__wrapper) {
  border-radius: 20px;
  background-color: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  box-shadow: 0 2px 8px rgba(31, 38, 135, 0.1), 
              inset 0 1px 2px rgba(255, 255, 255, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 12px rgba(31, 38, 135, 0.15),
              inset 0 1px 2px rgba(255, 255, 255, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 2px 12px rgba(52, 152, 219, 0.2),
              inset 0 1px 2px rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(52, 152, 219, 0.5);
  background-color: rgba(255, 255, 255, 0.25);
}

:deep(.el-input__inner) {
  color: rgba(0, 0, 0, 0.8);
}

:deep(.el-input__inner::placeholder) {
  color: rgba(0, 0, 0, 0.5);
}

.user-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 100%;
}

.user-avatar {
  background-color: rgba(52, 152, 219, 0.1);
  color: #3498db;
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.2);
}

.username {
  margin: 0 12px 0 8px;
  font-size: 14px;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.8);
}

:deep(.el-divider--vertical) {
  height: 1em;
  margin: 0 12px;
  border-color: rgba(255, 255, 255, 0.5);
}

/* 按钮样式 */
:deep(.el-button) {
  color: rgba(0, 0, 0, 0.7);
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  color: rgba(52, 152, 219, 0.9);
  transform: translateY(-1px);
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.3);
}

/* 退出按钮样式增强 */
.logout-btn {
  position: relative;
  overflow: hidden;
}

.logout-btn:hover {
  color: #e74c3c !important;
}

:deep(.logout-btn:hover .el-icon) {
  color: #e74c3c !important;
}

/* 确保el-row也垂直居中 */
:deep(.el-row) {
  height: 100%;
  width: 100%;
  align-items: center;
}
</style>

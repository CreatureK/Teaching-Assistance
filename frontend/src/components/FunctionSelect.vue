<script setup lang="ts">
import { ref, watch } from 'vue'

// 定义props和emits
interface Props {
  courseTitle?: string
}
const props = defineProps<Props>()
const emit = defineEmits(['back', 'show-course-info'])

// 课程标题
const courseTitle = ref(props.courseTitle || '课程标题')
const isEditingTitle = ref(false)

// 监听props变化更新标题
watch(() => props.courseTitle, (newTitle) => {
  if (newTitle) {
    courseTitle.value = newTitle
  }
})

// 模块状态枚举
type ModuleStatus = 'empty' | 'pending' | 'completed' | 'warning'

// 模块配置
interface ModuleConfig {
  id: string
  title: string
  icon: string
  status: ModuleStatus
}

// 四个模块的状态
const modules = ref<ModuleConfig[]>([
  { id: 'basic', title: '课程介绍', icon: 'info', status: 'warning' },
  { id: 'outline', title: '课程大纲', icon: 'list', status: 'completed' },
  { id: 'lecture', title: '教学讲义', icon: 'book', status: 'pending' },
  { id: 'courseware', title: '教学课件', icon: 'presentation', status: 'empty' }
])

// 切换标题编辑状态
const toggleTitleEdit = () => {
  isEditingTitle.value = !isEditingTitle.value
}

// 返回到课程管理
const goBack = () => {
  emit('back')
}

// 显示课程信息
const showCourseInfo = () => {
  emit('show-course-info')
}

// 获取状态图标
const getStatusIcon = (status: ModuleStatus) => {
  switch (status) {
    case 'empty':
      return '○' // 空心圆图标
    case 'pending':
      return '⏱' // 时钟图标
    case 'completed':
      return '✓' // 对勾图标
    case 'warning':
      return '⚠' // 警告三角形图标
    default:
      return '○'
  }
}

// 获取模块图标SVG
const getModuleIcon = (iconName: string) => {
  switch (iconName) {
    case 'info':
      return `<svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="16" x2="12" y2="12"></line>
                <line x1="12" y1="8" x2="12.01" y2="8"></line>
              </svg>`
    case 'list':
      return `<svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="8" y1="6" x2="21" y2="6"></line>
                <line x1="8" y1="12" x2="21" y2="12"></line>
                <line x1="8" y1="18" x2="21" y2="18"></line>
                <line x1="3" y1="6" x2="3.01" y2="6"></line>
                <line x1="3" y1="12" x2="3.01" y2="12"></line>
                <line x1="3" y1="18" x2="3.01" y2="18"></line>
              </svg>`
    case 'book':
      return `<svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
              </svg>`
    case 'presentation':
      return `<svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="2" y="3" width="20" height="14" rx="2" ry="2"></rect>
                <line x1="8" y1="21" x2="16" y2="21"></line>
                <line x1="12" y1="17" x2="12" y2="21"></line>
              </svg>`
    default:
      return ''
  }
}
</script>

<template>
  <div class="function-select-container">
    <!-- 课程标题 -->
    <div class="title-container">
      <button class="back-button" @click="goBack">←</button>
      <div v-if="isEditingTitle" class="editing-title">
        <input
          v-model="courseTitle"
          type="text"
          class="title-input"
          @keyup.enter="toggleTitleEdit"
        />
      </div>
      <h1 v-else class="course-title">{{ courseTitle }}</h1>
      <button class="edit-button" @click="toggleTitleEdit">
        {{ isEditingTitle ? '保存' : '修改' }}
      </button>
      <button class="course-info-button" @click="showCourseInfo">课程信息</button>
    </div>

    <!-- 模块列表 -->
    <div class="modules-container">
      <template v-for="(module, index) in modules" :key="module.id">
        <!-- 模块 -->
        <div class="module-card">
          <div class="status-icon" :class="`status-${module.status}`">
            {{ getStatusIcon(module.status) }}
          </div>
          <div class="module-icon">
            <div class="icon-circle" v-html="getModuleIcon(module.icon)">
            </div>
          </div>
          <div class="module-title">{{ module.title }}</div>
        </div>

        <!-- 箭头 (除了最后一个模块) -->
        <div v-if="index < modules.length - 1" class="arrow">→</div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.function-select-container {
  padding: 20px;
  width: 100%;
}

.title-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
  position: relative;
}

.back-button {
  background-color: transparent;
  border: none;
  color: #2196f3;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  position: absolute;
  left: 0;
  transition: background-color 0.2s;
}

.back-button:hover {
  background-color: rgba(33, 150, 243, 0.1);
}

.course-title {
  font-size: 28px;
  font-weight: bold;
  margin: 0 15px;
  color: #333;
  transition: all 0.3s ease;
}

.editing-title {
  margin: 0 10px;
}

.title-input {
  font-size: 28px;
  font-weight: bold;
  border: none;
  border-bottom: 2px solid #2196f3;
  padding: 5px 10px;
  border-radius: 4px;
  width: 400px;
  background-color: rgba(33, 150, 243, 0.05);
  transition: all 0.3s ease;
  outline: none;
  box-shadow: 0 2px 10px rgba(33, 150, 243, 0.1);
}

.title-input:focus {
  background-color: rgba(33, 150, 243, 0.1);
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.2);
}

.course-info-button {
  padding: 8px 16px;
  background-color: rgba(244, 67, 54, 0.2);
  color: rgba(244, 67, 54, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 4px 12px rgba(244, 67, 54, 0.2);
  position: absolute;
  right: 0;
}

.course-info-button:hover {
  background-color: rgba(244, 67, 54, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(244, 67, 54, 0.3);
}

.edit-button {
  padding: 5px 15px;
  background-color: rgba(33, 150, 243, 0.2);
  color: rgba(33, 150, 243, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.2);
}

.edit-button:hover {
  background-color: rgba(33, 150, 243, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(89, 178, 250, 0.3);
}

.modules-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 30px;
  padding: 20px;
  width: 100%;
  max-width: 1400px;
  margin: 30px auto;
  flex-wrap: nowrap;
  overflow-x: auto;
  padding-bottom: 20px; /* 为滚动条预留空间 */
}

/* 自定义滚动条样式 */
.modules-container::-webkit-scrollbar {
  height: 8px;
}

.modules-container::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 10px;
}

.modules-container::-webkit-scrollbar-thumb {
  background: rgba(33, 150, 243, 0.3);
  border-radius: 10px;
}

.modules-container::-webkit-scrollbar-thumb:hover {
  background: rgba(33, 150, 243, 0.5);
}

@media (max-width: 1200px) {
  .modules-container {
    padding: 10px;
    justify-content: flex-start;
  }
  
  .module-card {
    min-width: 200px; /* 确保卡片不会太小 */
  }
}

@media (max-width: 768px) {
  .module-card {
    min-width: 180px;
  }
  
  .arrow {
    margin: 0 15px;
    font-size: 36px;
  }
}

.module-card {
  width: 220px;
  height: 250px;
  border: none;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  background: rgba(255, 255, 255, 0.25);
  box-shadow: 
    0 4px 10px rgba(0, 0, 0, 0.05),
    0 10px 30px rgba(31, 38, 135, 0.1);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  padding: 25px;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  z-index: 1;
  overflow: hidden;
}

.module-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: -1;
  background: linear-gradient(
    135deg, 
    rgba(255, 255, 255, 0.3) 0%, 
    rgba(255, 255, 255, 0.1) 100%
  );
  border-radius: 20px;
  opacity: 0;
  transition: opacity 0.4s ease;
}

.module-card:hover {
  transform: translateY(-15px) scale(1.03);
  box-shadow: 
    0 10px 20px rgba(0, 0, 0, 0.08),
    0 20px 40px rgba(31, 38, 135, 0.15),
    0 0 15px rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.4);
  z-index: 2;
}

.module-card:hover::before {
  opacity: 1;
}

.status-icon {
  position: absolute;
  top: 15px;
  right: 15px;
  font-size: 28px;
  z-index: 2;
  filter: drop-shadow(0 0 5px rgba(0, 0, 0, 0.2));
}

.status-empty {
  color: #9e9e9e;
  text-shadow: 0 0 10px rgba(158, 158, 158, 0.3);
}

.status-pending {
  color: #ff9800;
  text-shadow: 0 0 10px rgba(255, 152, 0, 0.3);
}

.status-completed {
  color: #4caf50;
  text-shadow: 0 0 10px rgba(76, 175, 80, 0.3);
}

.status-warning {
  color: #f44336;
  text-shadow: 0 0 10px rgba(244, 67, 54, 0.3);
}

.module-icon {
  margin-bottom: 20px;
}

.icon-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: rgba(227, 242, 253, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2196f3;
  box-shadow: 0 4px 20px rgba(33, 150, 243, 0.2);
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.icon-circle::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    to bottom right,
    rgba(255, 255, 255, 0) 0%,
    rgba(255, 255, 255, 0.3) 50%,
    rgba(255, 255, 255, 0) 100%
  );
  transform: rotate(45deg);
  z-index: 1;
  transition: all 0.6s ease;
  opacity: 0;
}

.module-card:hover .icon-circle {
  transform: scale(1.1);
  box-shadow: 0 8px 25px rgba(33, 150, 243, 0.3);
}

.module-card:hover .icon-circle::after {
  animation: shine 1.5s forwards;
}

@keyframes shine {
  0% {
    opacity: 0;
    transform: rotate(45deg) translateX(-100%);
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0;
    transform: rotate(45deg) translateX(100%);
  }
}

.module-title {
  font-size: 22px;
  font-weight: 600;
  margin-top: 20px;
  text-align: center;
}

.arrow {
  margin: 0 25px;
  font-size: 48px;
  color: #2196f3;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  opacity: 0.8;
  animation: pulse 1.5s infinite;
  text-shadow: 0 0 15px rgba(33, 150, 243, 0.5);
  z-index: 0;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

.back-button, .edit-button, .course-info-button {
  outline: none;
}

.back-button:focus, .edit-button:focus, .course-info-button:focus {
  outline: none;
  box-shadow: none;
}

/* 防止点击时产生黑色边框 */
.back-button:active, .edit-button:active, .course-info-button:active {
  outline: none;
  border-style: none;
  -webkit-tap-highlight-color: transparent;
}
</style>

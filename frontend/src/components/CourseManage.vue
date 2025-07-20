<script setup lang="ts">
import { ref, defineEmits, onMounted } from 'vue';
import { getTeacherCourses, createCourse, deleteCourse, updateCourseName } from '../api/courseManger';

interface Course {
  id: number;
  name: string;
  imageUrl?: string;
  isEditing?: boolean;
  isSelected?: boolean;
}

// 定义事件
const emit = defineEmits(['course-selected']);

// 课程数据
const courses = ref<Course[]>([]);

// 是否处于删除模式
const isDeleteMode = ref(false);

// 加载状态
const loading = ref(false);

// 错误信息
const errorMessage = ref('');

// 获取课程列表
const fetchCourses = async () => {
  loading.value = true;
  errorMessage.value = '';
  try {
    const data = await getTeacherCourses();
    // 转换后端数据格式为组件所需格式，并按ID降序排序（ID大的排在前面）
    courses.value = data.map((course: any) => ({
      id: course.id,
      name: course.name,
      imageUrl: 'https://res.cloudinary.com/dm3rouwgn/image/upload/t_media_lib_thumb/zuxomrowewwe5spaci7w',
      isEditing: false,
      isSelected: false
    })).sort((a: Course, b: Course) => b.id - a.id);
  } catch (error) {
    console.error('获取课程列表失败:', error);
    errorMessage.value = '获取课程列表失败，请稍后重试';
  } finally {
    loading.value = false;
  }
};

// 组件挂载时获取课程列表
onMounted(fetchCourses);

// 添加新课程
const addNewCourse = async () => {
  try {
    const courseName = '新课程';
    const response = await createCourse(courseName);
    
    // 添加新创建的课程到列表中
    courses.value.push({
      id: response.id,
      name: response.name,
      imageUrl: 'https://res.cloudinary.com/dm3rouwgn/image/upload/t_media_lib_thumb/zuxomrowewwe5spaci7w',
      isEditing: true, // 创建后立即进入编辑模式
      isSelected: false
    });
    
    // 按ID降序排序，确保ID大的课程显示在前面
    courses.value.sort((a: Course, b: Course) => b.id - a.id);
  } catch (error) {
    console.error('创建课程失败:', error);
    errorMessage.value = '创建课程失败，请稍后重试';
  }
};

// 编辑课程名称
const editTitle = (course: Course) => {
  if (!isDeleteMode.value) {
    course.isEditing = true;
  } else {
    toggleCourseSelection(course);
  }
};

// 完成编辑
const finishEdit = async (course: Course) => {
  try {
    // 调用更新课程名称的API
    const updatedCourse = await updateCourseName(course.id, course.name);
    
    // 更新本地数据
    course.name = updatedCourse.name;
    course.isEditing = false;
  } catch (error) {
    console.error('更新课程名称失败:', error);
    errorMessage.value = '更新课程名称失败，请稍后重试';
  }
};

// 切换删除模式
const toggleDeleteMode = () => {
  isDeleteMode.value = !isDeleteMode.value;
  
  // 退出删除模式时，取消所有选择
  if (!isDeleteMode.value) {
    courses.value.forEach(course => {
      course.isSelected = false;
    });
  }
};

// 切换课程选择状态
const toggleCourseSelection = (course: Course) => {
  if (isDeleteMode.value) {
    course.isSelected = !course.isSelected;
  }
};

// 删除选中的课程
const deleteSelectedCourses = async () => {
  const selectedCourses = courses.value.filter(course => course.isSelected);
  const deletePromises = selectedCourses.map(course => deleteCourse(course.id));
  
  try {
    await Promise.all(deletePromises);
    courses.value = courses.value.filter(course => !course.isSelected);
    isDeleteMode.value = false;
  } catch (error) {
    console.error('删除课程失败:', error);
    errorMessage.value = '删除课程失败，请稍后重试';
  }
};

// 点击课程
const handleCourseClick = (course: Course) => {
  if (isDeleteMode.value) {
    toggleCourseSelection(course);
  } else {
    emit('course-selected', { id: course.id, name: course.name });
  }
};

// 自定义指令：自动聚焦并全选
const vFocus = {
  mounted(el: HTMLInputElement) {
    el.focus();
    el.select();
  }
};
</script>

<template>
  <div class="course-manage">
    <div class="course-header">
      <h2 class="course-title">您已有的课程 {{ courses.length }}</h2>
      <div class="course-actions">
        <button 
          @click="toggleDeleteMode" 
          class="delete-button"
          :class="{ 'active': isDeleteMode }"
        >
          {{ isDeleteMode ? '取消' : '删除' }}
        </button>
        <button 
          v-if="isDeleteMode" 
          @click="deleteSelectedCourses" 
          class="confirm-delete-button"
        >
          确认删除
        </button>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      正在加载课程，请稍候...
    </div>
    
    <!-- 错误信息 -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    
    <div class="course-grid" v-if="!loading">
      <!-- 添加按钮卡片 (放在第一位) -->
      <div class="course-card add-card" @click="addNewCourse">
        <div class="add-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
        </div>
      </div>
      
      <!-- 课程卡片 -->
      <div 
        v-for="course in courses" 
        :key="course.id" 
        class="course-card" 
        :class="{ 'selected': course.isSelected }"
        @click="handleCourseClick(course)"
      >
        <div class="course-image">
          <img :src="course.imageUrl" alt="课程封面" />
          <div v-if="isDeleteMode" class="selection-indicator">
            <svg v-if="course.isSelected" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="20 6 9 17 4 12"></polyline>
            </svg>
          </div>
        </div>
        <div class="course-info">
          <h3 v-if="!course.isEditing" @click.stop="editTitle(course)">{{ course.name }}</h3>
          <input 
            v-else 
            type="text" 
            v-model="course.name" 
            @blur="finishEdit(course)"
            @keyup.enter="finishEdit(course)"
            @click.stop
            ref="titleInput"
            class="title-input"
            v-focus
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.course-manage {
  width: 100%;
  max-width: 100%;
  margin: 0 auto;
  padding: 20px;
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.course-title {
  font-size: 24px;
  margin: 0;
  color: #333;
}

.course-actions {
  display: flex;
  gap: 10px;
}

.delete-button {
  padding: 6px 12px;
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
  border: 1px solid rgba(231, 76, 60, 0.2);
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.delete-button:hover {
  background: rgba(231, 76, 60, 0.2);
}

.delete-button.active {
  background: #e74c3c;
  color: white;
}

.confirm-delete-button {
  padding: 6px 12px;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.confirm-delete-button:hover {
  background: #c0392b;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  width: 100%;
}

.course-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  position: relative;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.course-card.selected {
  box-shadow: 0 0 0 2px #e74c3c, 0 8px 16px rgba(0, 0, 0, 0.15);
}

.course-image {
  width: 100%;
  height: 160px;
  overflow: hidden;
  position: relative;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.selection-indicator {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(231, 76, 60, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.course-info {
  padding: 16px;
  flex-grow: 1;
}

.course-info h3 {
  font-size: 18px;
  margin: 0;
  color: #333;
  text-align: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.course-info h3:hover {
  background: rgba(104, 112, 250, 0.1);
}

.title-input {
  width: 100%;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  text-align: center;
  border: none;
  border-bottom: 1px solid #6870fa;
  background: transparent;
  padding: 4px 8px;
  margin: 0;
  outline: none;
  box-shadow: 0 2px 8px rgba(104, 112, 250, 0.2);
  border-radius: 4px;
  transition: all 0.2s ease;
}

.title-input:focus {
  box-shadow: 0 4px 12px rgba(104, 112, 250, 0.3);
  background: rgba(255, 255, 255, 0.9);
}

.add-card {
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(240, 240, 250, 0.5);
  color: #6870fa;
  min-height: 226px; /* 匹配课程卡片高度: 图片高度160px + 内边距和标题 */
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
}

.add-card:hover {
  background: rgba(240, 240, 250, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.25);
}

.add-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 添加样式 */
.loading-state {
  text-align: center;
  padding: 20px;
  font-size: 16px;
  color: #6870fa;
}

.error-message {
  text-align: center;
  padding: 10px;
  margin-bottom: 20px;
  background-color: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
  border: 1px solid rgba(231, 76, 60, 0.2);
  border-radius: 6px;
}
</style>

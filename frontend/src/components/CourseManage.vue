<script setup lang="ts">
import { ref, defineEmits } from 'vue';

interface Course {
  id: number;
  title: string;
  imageUrl: string;
  isEditing?: boolean;
  isSelected?: boolean;
}

// 定义事件
const emit = defineEmits(['course-selected']);

// 课程数据
const courses = ref<Course[]>([
  {
    id: 1,
    title: '人工智能导论',
    imageUrl: 'https://res.cloudinary.com/dm3rouwgn/image/upload/t_media_lib_thumb/zuxomrowewwe5spaci7w',
    isEditing: false,
    isSelected: false
  },
  {
    id: 2,
    title: '移动应用开发技术',
    imageUrl: 'https://res.cloudinary.com/dm3rouwgn/image/upload/t_media_lib_thumb/zuxomrowewwe5spaci7w',
    isEditing: false,
    isSelected: false
  },
  {
    id: 3,
    title: 'Web前端开发实战',
    imageUrl: 'https://res.cloudinary.com/dm3rouwgn/image/upload/t_media_lib_thumb/zuxomrowewwe5spaci7w',
    isEditing: false,
    isSelected: false
  }
]);

// 是否处于删除模式
const isDeleteMode = ref(false);

// 添加新课程
const addNewCourse = () => {
  const newId = courses.value.length > 0 ? Math.max(...courses.value.map(c => c.id)) + 1 : 1;
  courses.value.unshift({
    id: newId,
    title: '新课程',
    imageUrl: 'https://res.cloudinary.com/dm3rouwgn/image/upload/t_media_lib_thumb/zuxomrowewwe5spaci7w',
    isEditing: false,
    isSelected: false
  });
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
const finishEdit = (course: Course) => {
  course.isEditing = false;
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
const deleteSelectedCourses = () => {
  courses.value = courses.value.filter(course => !course.isSelected);
  isDeleteMode.value = false;
};

// 点击课程
const handleCourseClick = (course: Course) => {
  if (isDeleteMode.value) {
    toggleCourseSelection(course);
  } else {
    emit('course-selected', course.title);
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
    
    <div class="course-grid">
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
          <h3 v-if="!course.isEditing" @click.stop="editTitle(course)">{{ course.title }}</h3>
          <input 
            v-else 
            type="text" 
            v-model="course.title" 
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
</style>

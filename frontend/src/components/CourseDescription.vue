<template>
  <div class="course-description-container">
    <div class="header">
      <button class="back-button" @click="$emit('back')">←</button>
      <div class="header-right">
        <button class="ai-btn" @click="showPrompt = true" :disabled="isGenerating || isLoading || isSaving">
          <span class="ai-icon">✨</span>
          AI生成
        </button>
        <!-- <button class="btn btn-secondary" @click="handleTempSave" :disabled="isGenerating || isLoading || isSaving">暂存</button> -->
        <button class="btn btn-primary" @click="handleSave" :disabled="isGenerating || isLoading || isSaving">保存</button>
      </div>
    </div>

    <div v-if="isLoading" class="status-message loading">
      <div class="spinner"></div>
      <span>{{ loadingMessage }}</span>
    </div>

    <div v-if="isGenerating" class="status-message generating">
      <div class="spinner"></div>
      <span>{{ generatingStatus }}</span>
    </div>

    <div v-if="isSaving" class="status-message saving">
      <div class="spinner"></div>
      <span>正在保存...</span>
    </div>

    <!-- 新增成功消息提示 -->
    <div v-if="showSuccessMessage" class="status-message success">
      <span class="success-icon">✓</span>
      <span>{{ successMessage }}</span>
    </div>

    <div v-if="!props.courseId || props.courseId <= 0" class="error-message">
      <p>课程ID无效，请返回重新选择课程</p>
    </div>

    <div v-else>
      <div class="section">
        <h2 class="section-title">课程介绍</h2>
        <Markdown 
          ref="introductionMdRef"
          :initial-value="courseIntroduction" 
          height="250px" 
          preview-style="tab"
          :editable="true" 
        />
      </div>

      <div class="section">
        <h2 class="section-title">教学目标</h2>
        <Markdown 
          ref="contentMdRef"
          :initial-value="courseContent" 
          height="250px" 
          preview-style="tab"
          :editable="true" 
        />
      </div>
    </div>

    <Prompt
      :is-visible="showPrompt"
      title="AI生成"
      description="请输入您想要AI生成的内容描述或指令"
      placeholder="例如：生成一个关于人工智能基础的课程介绍"
      @close="showPrompt = false"
      @confirm="handlePromptConfirm"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, defineProps } from 'vue';
import Markdown from './markdown.vue';
import Prompt from './Prompt.vue';
import { generateCourseObjective, getCourseObjective, saveCourseObjective } from '../api/functions';

// 定义Markdown组件实例的类型
interface MarkdownInstance {
  setMarkdown: (content: string) => void;
  getMarkdown: () => string;
  [key: string]: any;
}

const props = defineProps({
  courseId: {
    type: Number,
    required: false,
    default: 0
  }
});

defineEmits(['back']);

// Markdown组件引用
const introductionMdRef = ref<MarkdownInstance | null>(null);
const contentMdRef = ref<MarkdownInstance | null>(null);

// 控制Prompt组件显示
const showPrompt = ref(false);

// 加载状态
const isLoading = ref(false);
const loadingMessage = ref('');

// 生成状态
const isGenerating = ref(false);
const generatingStatus = ref('正在生成中...');
let cancelPoll: (() => void) | null = null;

// 课程内容
const courseIntroduction = ref('');
const courseContent = ref('');

// 保存状态
const isSaving = ref(false);

// 暂存的内容
let tempIntroduction = '';
let tempContent = '';

// 成功消息状态
const showSuccessMessage = ref(false);
const successMessage = ref('');

// 获取课程目标
const fetchCourseObjective = async () => {
  // 检查courseId是否有效
  if (!props.courseId || props.courseId <= 0) {
    console.warn('课程ID无效，无法获取课程目标');
    return;
  }
  
  try {
    isLoading.value = true;
    loadingMessage.value = '正在加载课程内容...';
    
    const data = await getCourseObjective(props.courseId);
    console.log('获取到的课程目标:', data);
    
    if (data && data.courseContent) {
      courseIntroduction.value = data.courseContent;
      // 使用组件实例的setMarkdown方法更新内容
      if (introductionMdRef.value && introductionMdRef.value.setMarkdown) {
        introductionMdRef.value.setMarkdown(data.courseContent);
      }
    }
    
    if (data && data.teachingTarget) {
      courseContent.value = data.teachingTarget;
      // 使用组件实例的setMarkdown方法更新内容
      if (contentMdRef.value && contentMdRef.value.setMarkdown) {
        contentMdRef.value.setMarkdown(data.teachingTarget);
      }
    }
    
  } catch (error) {
    console.error('获取课程目标失败:', error);
    // 如果获取失败，使用默认值
    courseIntroduction.value = '暂无课程介绍，请添加或使用AI生成';
    courseContent.value = '暂无教学目标，请添加或使用AI生成';
    
    // 更新Markdown组件
    if (introductionMdRef.value && introductionMdRef.value.setMarkdown) {
      introductionMdRef.value.setMarkdown('暂无课程介绍，请添加或使用AI生成');
    }
    if (contentMdRef.value && contentMdRef.value.setMarkdown) {
      contentMdRef.value.setMarkdown('暂无教学目标，请添加或使用AI生成');
    }
  } finally {
    isLoading.value = false;
  }
};

// 组件挂载时获取课程目标
onMounted(() => {
  console.log('CourseDescription组件已挂载，courseId:', props.courseId);
  if (props.courseId && props.courseId > 0) {
    fetchCourseObjective();
  } else {
    console.warn('无效的courseId，跳过获取课程目标');
  }
});

// 处理Prompt提交事件
const handlePromptConfirm = async (content: string) => {
  console.log('用户提交的内容:', content);
  showPrompt.value = false;
  
  try {
    // 检查courseId是否有效
    if (!props.courseId || props.courseId <= 0) {
      generatingStatus.value = '课程ID无效，请返回重试';
      isGenerating.value = true;
      setTimeout(() => {
        isGenerating.value = false;
      }, 3000);
      return;
    }
    
    isGenerating.value = true;
    generatingStatus.value = '正在生成课程介绍和教学目标...';
    
    // 检查localStorage中是否有userId
    const userId = localStorage.getItem('userId') || sessionStorage.getItem('userId');
    if (!userId) {
      console.warn('未找到userId，可能会导致请求失败');
    }
    
    // 调用生成接口
    const response = await generateCourseObjective(props.courseId, content);
    console.log('生成接口返回数据:', response);
    
    // 直接使用返回的结果，不再需要轮询状态
    if (response) {
      if (response.courseContent) {
        courseIntroduction.value = response.courseContent;
        if (introductionMdRef.value && introductionMdRef.value.setMarkdown) {
          introductionMdRef.value.setMarkdown(response.courseContent);
        }
      }
      
      if (response.teachingTarget) {
        courseContent.value = response.teachingTarget;
        if (contentMdRef.value && contentMdRef.value.setMarkdown) {
          contentMdRef.value.setMarkdown(response.teachingTarget);
        }
      }
    }
    
    generatingStatus.value = '生成完成!';
    setTimeout(() => {
      isGenerating.value = false;
    }, 1000);
    
  } catch (error: any) {
    console.error('生成失败:', error);
    // 增强错误处理，显示更详细的错误信息
    if (error.response && error.response.status === 400) {
      generatingStatus.value = '生成失败: 请求参数错误，可能缺少用户ID，请重新登录';
    } else if (error.message) {
      generatingStatus.value = `生成失败: ${error.message}`;
    } else {
      generatingStatus.value = '生成失败，请重试';
    }
    
    setTimeout(() => {
      isGenerating.value = false;
    }, 3000);
  }
};

// 暂存功能
const handleTempSave = () => {
  tempIntroduction = courseIntroduction.value;
  tempContent = courseContent.value;
  alert('内容已暂存');
};

// 保存功能
const handleSave = async () => {
  if (!props.courseId || props.courseId <= 0) {
    alert('课程ID无效，无法保存');
    return;
  }
  
  try {
    isSaving.value = true;
    
    // 从Markdown组件获取最新内容
    if (introductionMdRef.value) {
      const introContent = introductionMdRef.value.getMarkdown ? 
        introductionMdRef.value.getMarkdown() : courseIntroduction.value;
      courseIntroduction.value = introContent;
    }
    
    if (contentMdRef.value) {
      const teachingContent = contentMdRef.value.getMarkdown ? 
        contentMdRef.value.getMarkdown() : courseContent.value;
      courseContent.value = teachingContent;
    }
    
    const objectiveData = {
      courseContent: courseIntroduction.value,
      teachingTarget: courseContent.value
    };
    
    console.log('保存课程目标:', objectiveData);
    await saveCourseObjective(props.courseId, objectiveData);
    
    // 显示成功消息
    successMessage.value = '保存成功';
    showSuccessMessage.value = true;
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
    
    // 保存成功后重新获取数据
    await fetchCourseObjective();
    
  } catch (error) {
    console.error('保存失败:', error);
    successMessage.value = '保存失败，请重试';
    showSuccessMessage.value = true;
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
  } finally {
    isSaving.value = false;
  }
};

// 组件销毁时不再需要取消轮询
onUnmounted(() => {
  // 取消可能存在的其他资源
});
</script>

<style scoped>
.course-description-container {
  max-width: 1500px;
  margin: 0 auto;
  padding: 20px;
  overflow: hidden; /* 隐藏滚动条 */
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
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
  transition: background-color 0.2s;
}

.back-button:hover {
  background-color: rgba(33, 150, 243, 0.1);
}

.status-message {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.loading {
  background-color: rgba(33, 150, 243, 0.1);
}

.generating {
  background-color: rgba(255, 193, 7, 0.1);
}

.saving {
  background-color: rgba(76, 175, 80, 0.1);
}

/* 新增成功消息样式 */
.success {
  background-color: rgba(76, 175, 80, 0.2);
  animation: fadeOut 3s forwards;
}

.success-icon {
  color: #4caf50;
  font-weight: bold;
  font-size: 18px;
  margin-right: 5px;
}

@keyframes fadeOut {
  0% { opacity: 1; }
  70% { opacity: 1; }
  100% { opacity: 0; }
}

.spinner {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #2196f3;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.ai-btn {
  display: flex;
  align-items: center;
  background-color: rgba(76, 175, 80, 0.7);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 14px;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.ai-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.ai-btn:hover:not(:disabled) {
  background-color: rgba(76, 175, 80, 0.85);
}

.ai-icon {
  margin-right: 8px;
}

.section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 18px;
  font-weight: 500;
  color: #333;
  margin-bottom: 10px;
}

.btn {
  padding: 10px 20px;
  border-radius: 4px;
  border: none;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background-color: rgba(76, 175, 80, 0.7);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: rgba(76, 175, 80, 0.85);
}

.btn-secondary {
  background-color: rgba(245, 245, 245, 0.7);
  color: #333;
}

.btn-secondary:hover:not(:disabled) {
  background-color: rgba(245, 245, 245, 0.85);
}

.btn:hover:not(:disabled) {
  opacity: 0.9;
}

.error-message {
  background-color: rgba(255, 0, 0, 0.1);
  color: #ff0000;
  padding: 15px;
  border-radius: 8px;
  text-align: center;
  margin-bottom: 20px;
  border: 1px solid rgba(255, 0, 0, 0.2);
}
</style>

<style>
/* 隐藏全局滚动条 */
body {
  overflow: hidden;
}

::-webkit-scrollbar {
  display: none;
}

* {
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}
</style>


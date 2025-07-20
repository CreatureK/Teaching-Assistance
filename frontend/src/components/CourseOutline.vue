<template>
  <div class="course-description-container">
    <div class="header">
      <button class="back-button" @click="$emit('back')">←</button>
      <h3 class="section-title">课程大纲</h3>
      <div class="header-right">
        <button class="ai-btn" @click="openPrompt" :disabled="isGenerating || isLoading || isSaving">
          <span class="ai-icon">✨</span>
          AI生成
        </button>
        <!-- <button class="btn btn-secondary">暂存</button> -->
        <button class="btn btn-primary" @click="saveSyllabus" :disabled="isGenerating || isLoading || isSaving">保存</button>
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

    <div v-if="showSuccessMessage" class="status-message success">
      <span class="success-icon">✓</span>
      <span>{{ successMessage }}</span>
    </div>

    <div v-if="error" class="status-message error">
      <span class="error-icon">❌</span>
      <span>{{ error }}</span>
    </div>

    <div class="section">
      <Markdown 
        ref="markdownRef"
        v-model="courseOutline"
        :initial-value="courseOutline" 
        :height="editorHeight" 
        preview-style="tab"
        :editable="true" 
      />
    </div>
    
    <Prompt
      :is-visible="showPrompt"
      title="AI生成课程大纲"
      description="请输入您想要生成的课程大纲的相关描述或关键词"
      placeholder="例如：计算机网络基础、面向对象程序设计、人工智能导论等"
      @close="showPrompt = false"
      @confirm="handlePromptConfirm"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, defineEmits, onMounted, onUnmounted, defineProps } from 'vue';
import Markdown from './markdown.vue';
import Prompt from './Prompt.vue';
import { getCourseSyllabus, saveCourseSyllabus, generateCourseSyllabus } from '../api/functions';

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
    default: undefined
  }
});

defineEmits(['back']);

// Markdown组件引用
const markdownRef = ref<MarkdownInstance | null>(null);

// 控制Prompt组件显示
const showPrompt = ref(false);

// 课程大纲内容
const courseOutline = ref('');
const loading = ref(false);
const error = ref('');

// 状态变量
const isLoading = ref(false);
const loadingMessage = ref('正在加载课程大纲...');
const isGenerating = ref(false);
const generatingStatus = ref('正在生成中...');
const isSaving = ref(false);
const showSuccessMessage = ref(false);
const successMessage = ref('');

// 编辑器高度响应式处理
const editorHeight = ref('calc(100vh - 200px)');

// 获取课程大纲
const fetchCourseSyllabus = async () => {
  if (!props.courseId || isNaN(props.courseId)) {
    error.value = '课程ID无效，无法获取大纲';
    console.error('无效的课程ID:', props.courseId);
    return;
  }
  
  isLoading.value = true;
  loadingMessage.value = '正在加载课程大纲...';
  error.value = '';
  
  try {
    const response = await getCourseSyllabus(props.courseId);
    courseOutline.value = response.content || '';
    console.log('成功获取课程大纲');
    
    // 使用ref直接更新Markdown组件
    if (markdownRef.value && markdownRef.value.setMarkdown) {
      markdownRef.value.setMarkdown(courseOutline.value);
    }
  } catch (err) {
    console.error('获取课程教学大纲失败', err);
    error.value = '获取课程大纲失败，请稍后重试';
  } finally {
    isLoading.value = false;
  }
};

// 保存课程大纲
const saveSyllabus = async () => {
  if (!props.courseId || isNaN(props.courseId)) {
    alert('课程ID无效，无法保存大纲');
    console.error('无效的课程ID:', props.courseId);
    return;
  }
  
  isSaving.value = true;
  try {
    await saveCourseSyllabus(props.courseId, {
      content: courseOutline.value
    });
    showSuccessMessage.value = true;
    successMessage.value = '保存成功！';
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
  } catch (err) {
    console.error('保存课程大纲失败', err);
    alert('保存失败，请稍后重试');
  } finally {
    isSaving.value = false;
  }
};

// 更新编辑器高度
const updateEditorHeight = () => {
  const calculatedHeight = Math.min(window.innerHeight - 200, 1800);
  editorHeight.value = `${calculatedHeight}px`;
};

// 处理Prompt提交事件
const handlePromptConfirm = async (content: string) => {
  if (!props.courseId || isNaN(props.courseId)) {
    alert('课程ID无效，无法生成大纲');
    console.error('无效的课程ID:', props.courseId);
    showPrompt.value = false;
    return;
  }
  
  console.log('用户提交的大纲生成内容:', content);
  if (!content.trim()) {
    alert('请输入有效的描述内容');
    return;
  }
  
  showPrompt.value = false;
  isGenerating.value = true;
  generatingStatus.value = '正在生成课程大纲...';
  error.value = '';
  
  // 打印请求信息，确认userId
  const userId = localStorage.getItem('userId') || sessionStorage.getItem('userId') || '未获取到userId';
  console.log('生成大纲请求信息:', {
    courseId: props.courseId,
    userId: userId,
    contentLength: content.length
  });
  
  try {
    const response = await generateCourseSyllabus(props.courseId, content);
    console.log('收到API响应:', response);
    
    // 处理新的响应格式
    if (response && response.choices && response.choices[0] && response.choices[0].message) {
      // 从新的响应格式中提取content
      courseOutline.value = response.choices[0].message.content;
      console.log('成功生成教学大纲');
      
      // 使用ref直接更新Markdown组件
      if (markdownRef.value && markdownRef.value.setMarkdown) {
        markdownRef.value.setMarkdown(courseOutline.value);
      }
      
      showSuccessMessage.value = true;
      successMessage.value = '生成成功！';
      setTimeout(() => {
        showSuccessMessage.value = false;
      }, 3000);
    } else if (response && response.content) {
      // 兼容原有响应格式
      courseOutline.value = response.content;
      console.log('成功生成教学大纲');
      
      // 使用ref直接更新Markdown组件
      if (markdownRef.value && markdownRef.value.setMarkdown) {
        markdownRef.value.setMarkdown(courseOutline.value);
      }
      
      showSuccessMessage.value = true;
      successMessage.value = '生成成功！';
      setTimeout(() => {
        showSuccessMessage.value = false;
      }, 3000);
    } else {
      error.value = '生成的大纲内容为空，请尝试提供更详细的描述';
      console.error('生成的大纲内容为空', response);
    }
  } catch (err) {
    console.error('生成课程大纲失败', err);
    error.value = '生成课程大纲失败，请稍后重试';
  } finally {
    isGenerating.value = false;
  }
};

// 打开Prompt对话框
const openPrompt = () => {
  if (isGenerating.value || isLoading.value || isSaving.value) {
    return;
  }
  console.log('打开AI生成对话框');
  showPrompt.value = true;
};

onMounted(() => {
  updateEditorHeight();
  window.addEventListener('resize', updateEditorHeight);
  fetchCourseSyllabus();
});

onUnmounted(() => {
  window.removeEventListener('resize', updateEditorHeight);
});
</script>

<style scoped>
.course-description-container {
  max-width: 1500px;
  margin: 0 auto;
  padding: 20px;
  overflow: hidden; /* 隐藏滚动条 */
  height: 100%;
  display: flex;
  flex-direction: column;
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

.loading {
  text-align: center;
  padding: 20px;
  font-size: 16px;
  color: #666;
}

.error {
  text-align: center;
  padding: 20px;
  font-size: 16px;
  color: #f44336;
}

.error {
  background-color: rgba(244, 67, 54, 0.1);
}

.error-icon {
  color: #f44336;
  font-weight: bold;
  font-size: 18px;
}

.status-message {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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

.success {
  background-color: rgba(76, 175, 80, 0.2);
  animation: fadeOut 3s forwards;
}

.success-icon {
  color: #4caf50;
  font-weight: bold;
  font-size: 18px;
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

.btn-primary {
  background-color: rgba(76, 175, 80, 0.7);
  color: white;
}

.btn-primary:hover {
  background-color: rgba(76, 175, 80, 0.85);
}

.btn-secondary {
  background-color: rgba(245, 245, 245, 0.7);
  color: #333;
}

.btn-secondary:hover {
  background-color: rgba(245, 245, 245, 0.85);
}

.btn:hover {
  opacity: 0.9;
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

.ai-btn:hover {
  background-color: rgba(76, 175, 80, 0.85);
}

.ai-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.ai-icon {
  margin-right: 8px;
}

.section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 24px;
  font-weight: 500;
  color: #333;
  margin-bottom: 10px;
  text-align: center;
  flex-grow: 1;
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

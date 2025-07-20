<template>
  <div class="teaching-lecture-container">
    <div class="header">
      <button class="back-button" @click="$emit('back')">←</button>
      <h3 class="section-title">教学讲义</h3>
      <div class="header-right">
        <button class="ai-btn" @click="showPrompt = true">
          <span class="ai-icon">✨</span>
          AI生成
        </button>
        <!-- <button class="btn btn-secondary" @click="handleSaveDraft">暂存</button> -->
        <button class="btn btn-primary" @click="handleSave">保存</button>
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

    <div class="content-container">
      <!-- 左侧目录 -->
      <div class="catalog-panel" :class="{ 'collapsed': !catalogExpanded }">
        <Catalog 
          :content="markdownContent" 
          :activeHeading="activeHeading" 
          @navigate="scrollToHeading"
          @toggle="handleCatalogToggle"
        />
      </div>
      
      <!-- 右侧编辑器 -->
      <div class="editor-panel" :class="{ 'expanded': !catalogExpanded }">
        <Markdown 
          ref="markdownEditor"
          :initial-value="markdownContent" 
          :height="editorHeight" 
          preview-style="tab"
          @update:content="updateContent"
        />
      </div>
    </div>
    
    <Prompt
      :is-visible="showPrompt"
      title="AI生成讲义内容"
      description="请输入您想要AI生成的讲义内容描述或指令"
      placeholder="例如：生成关于分布式系统CAP理论的讲解，包括概念和应用案例"
      @close="showPrompt = false"
      @confirm="handlePromptConfirm"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, defineProps } from 'vue';
import Markdown from './markdown.vue';
import Catalog from './Catalog.vue';
import Prompt from './Prompt.vue';
import { getCourseMaterial, saveCourseMaterial, generateCourseMaterial } from '../api/functions';

// 定义讲义单元的接口
interface LectureUnit {
  unit_number: string;
  unit_title: string;
  lecture_content: string;
  ideological_target?: string;
  time_allocation?: string;
}

// 定义API响应的接口
interface MaterialResponse {
  content?: string;
  units?: LectureUnit[];
  message?: string;
  status?: string;
}

const props = defineProps({
  courseId: {
    type: Number,
    required: false,
    default: undefined
  }
});

const emit = defineEmits(['back', 'save', 'save-draft']);

// 状态变量
const isLoading = ref(false);
const loadingMessage = ref('正在加载讲义内容...');
const isGenerating = ref(false);
const generatingStatus = ref('正在生成讲义...');
const isSaving = ref(false);
const showSuccessMessage = ref(false);
const successMessage = ref('');
const error = ref('');

// 控制Prompt组件显示
const showPrompt = ref(false);

// 目录展开状态
const catalogExpanded = ref(true);

// 处理目录折叠/展开
const handleCatalogToggle = (expanded: boolean) => {
  catalogExpanded.value = expanded;
};

// 处理Prompt提交事件
const handlePromptConfirm = async (content: string) => {
  if (!props.courseId || isNaN(props.courseId)) {
    error.value = '课程ID无效，无法生成讲义';
    console.error('无效的课程ID:', props.courseId);
    showPrompt.value = false;
    return;
  }
  
  if (!content.trim()) {
    error.value = '请输入有效的描述内容';
    return;
  }
  
  showPrompt.value = false;
  isGenerating.value = true;
  generatingStatus.value = '正在生成讲义内容...';
  error.value = '';
  
  try {
    // 假设我们使用课程标题作为第二个参数，这里可以修改为实际需求
    const courseTitle = '课程讲义'; // 可以从props或其他地方获取实际标题
    const response: MaterialResponse = await generateCourseMaterial(props.courseId, courseTitle, content);
    
    if (response && response.content) {
      markdownContent.value = response.content;
    } else if (response && response.units) {
      // 处理新的响应格式，拼接所有单元的讲义内容
      let combinedContent = '';
      response.units.forEach((unit: LectureUnit, index: number) => {
        if (index > 0) {
          combinedContent += '\n\n---\n\n'; // 单元之间添加分隔线
        }
        
        combinedContent += unit.lecture_content;
      });
      
      console.log('生成的内容长度:', combinedContent.length);
      markdownContent.value = combinedContent;
    } else {
      error.value = '生成的讲义内容为空，请尝试提供更详细的描述';
      isGenerating.value = false;
      return;
    }
    
    // 使用ref更新Markdown组件
    if (markdownEditor.value && markdownEditor.value.setMarkdown) {
      console.log('准备更新生成的内容到编辑器');
      setTimeout(() => {
        markdownEditor.value.setMarkdown(markdownContent.value);
        console.log('编辑器内容已更新');
      }, 100); // 添加短暂延时确保渲染正确
    }
    
    showSuccessMessage.value = true;
    successMessage.value = '生成成功！';
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
  } catch (err) {
    console.error('生成讲义失败', err);
    error.value = '生成讲义失败，请稍后重试';
  } finally {
    isGenerating.value = false;
  }
};

// 示例讲义内容
const markdownContent = ref('');

// 当前活跃的标题锚点
const activeHeading = ref('');

// 编辑器引用
const markdownEditor = ref<any>(null);

// 编辑器高度响应式处理
const editorHeight = ref('calc(100vh - 200px)');

// 更新编辑器高度
const updateEditorHeight = () => {
  const calculatedHeight = Math.min(window.innerHeight - 200, 1800);
  editorHeight.value = `${calculatedHeight}px`;
};

// 监听内容变化
const updateContent = (content: string) => {
  markdownContent.value = content;
};

// 切换编辑模式
const toggleEditMode = () => {
  // 实现编辑模式切换逻辑
  if (markdownEditor.value) {
    // 可以实现一些编辑模式的切换，比如聚焦编辑器等
    const editor = markdownEditor.value.editor();
    if (editor) {
      editor.focus();
    }
  }
};

// 获取课程讲义
const fetchCourseMaterial = async () => {
  if (!props.courseId || isNaN(props.courseId)) {
    error.value = '课程ID无效，无法获取讲义';
    console.error('无效的课程ID:', props.courseId);
    return;
  }
  
  isLoading.value = true;
  loadingMessage.value = '正在加载讲义内容...';
  error.value = '';
  
  try {
    const response: MaterialResponse = await getCourseMaterial(props.courseId);
    if (response && response.content) {
      markdownContent.value = response.content;
    } else if (response && response.units) {
      // 处理新的响应格式，拼接所有单元的讲义内容
      let combinedContent = '';
      response.units.forEach((unit: LectureUnit, index: number) => {
        if (index > 0) {
          combinedContent += '\n\n---\n\n'; // 单元之间添加分隔线
        }
        combinedContent += `# 单元${unit.unit_number}: ${unit.unit_title}\n\n`;
        combinedContent += unit.lecture_content;
      });
      
      // 添加日志，查看拼接后的内容长度
      console.log('拼接后的内容长度:', combinedContent.length);
      markdownContent.value = combinedContent;
    } else {
      // 如果没有内容，使用空字符串
      markdownContent.value = '';
      console.log('获取的讲义内容为空');
    }
    
    // 使用ref更新Markdown组件
    if (markdownEditor.value && markdownEditor.value.setMarkdown) {
      console.log('准备更新Markdown组件');
      setTimeout(() => {
        markdownEditor.value.setMarkdown(markdownContent.value);
        console.log('Markdown组件已更新');
      }, 100); // 添加短暂延时确保渲染正确
    }
  } catch (err) {
    console.error('获取课程讲义失败', err);
    error.value = '获取课程讲义失败，请稍后重试';
  } finally {
    isLoading.value = false;
  }
};

// 滚动到指定标题位置
const scrollToHeading = (anchor: string) => {
  activeHeading.value = anchor;
  
  if (markdownEditor.value) {
    const editor = markdownEditor.value.editor();
    if (!editor) return;
    
    // 使用正则表达式搜索对应的标题文本
    const content = markdownContent.value;
    const lines = content.split('\n');
    const anchorText = anchor.replace(/-/g, ' ');
    const targetTextRegex = new RegExp(`^(#+)\\s+${anchorText}`, 'i');
    
    // 查找对应的行号
    let lineNumber = -1;
    for (let i = 0; i < lines.length; i++) {
      if (targetTextRegex.test(lines[i]) || lines[i].toLowerCase().includes(anchorText)) {
        lineNumber = i;
        break;
      }
    }
    
    if (lineNumber >= 0) {
      // 检查编辑器当前模式
      const isWysiwygMode = editor.isWysiwygMode();
      
      if (isWysiwygMode) {
        // 在所见即所得模式下，尝试查找标题元素并滚动
        try {
          const wysiwygEl = editor.getEditorElements().wysiwyg;
          if (wysiwygEl) {
            const headers = wysiwygEl.querySelectorAll('h1, h2, h3, h4, h5, h6');
            for (let i = 0; i < headers.length; i++) {
              if (headers[i].textContent?.toLowerCase().includes(anchorText)) {
                headers[i].scrollIntoView({ behavior: 'smooth' });
                break;
              }
            }
          }
        } catch (e) {
          console.log('无法在所见即所得模式下滚动到标题', e);
        }
      } else {
        // 在Markdown模式下，使用行号滚动
        try {
          // 滚动到对应行
          editor.setScrollTop(lineNumber * 21); // 假设每行约21px高
          
          // 尝试将光标定位到该行以突出显示
          setTimeout(() => {
            try {
              editor.setSelection({
                line: lineNumber,
                ch: 0
              }, {
                line: lineNumber,
                ch: lines[lineNumber].length
              });
            } catch (e) {
              console.log('无法设置选择区域', e);
            }
          }, 100);
        } catch (e) {
          console.log('无法滚动到标题', e);
        }
      }
    }
  }
};

// 保存草稿
const handleSaveDraft = () => {
  const content = markdownContent.value;
  emit('save-draft', content);
};

// 保存讲义
const handleSave = async () => {
  if (!props.courseId || isNaN(props.courseId)) {
    error.value = '课程ID无效，无法保存讲义';
    console.error('无效的课程ID:', props.courseId);
    return;
  }
  
  isSaving.value = true;
  error.value = '';
  
  try {
    await saveCourseMaterial(props.courseId, {
      content: markdownContent.value
    });
    
    showSuccessMessage.value = true;
    successMessage.value = '保存成功！';
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
  } catch (err) {
    console.error('保存讲义失败', err);
    error.value = '保存讲义失败，请稍后重试';
  } finally {
    isSaving.value = false;
  }
};

onMounted(() => {
  updateEditorHeight();
  window.addEventListener('resize', updateEditorHeight);
  fetchCourseMaterial(); // 加载讲义内容
});

onUnmounted(() => {
  window.removeEventListener('resize', updateEditorHeight);
});
</script>

<style scoped>
.teaching-lecture-container {
  max-width: 1500px;
  margin: 0 auto;
  padding: 20px;
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

.section-title {
  font-size: 24px;
  font-weight: 500;
  color: #333;
  margin-bottom: 10px;
  text-align: center;
  flex-grow: 1;
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

.error {
  background-color: rgba(244, 67, 54, 0.1);
}

.success-icon {
  color: #4caf50;
  font-weight: bold;
  font-size: 18px;
}

.error-icon {
  color: #f44336;
  font-weight: bold;
  font-size: 18px;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #2196f3;
  animation: spin 1s ease-in-out infinite;
}

@keyframes fadeOut {
  0% { opacity: 1; }
  70% { opacity: 1; }
  100% { opacity: 0; }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.content-container {
  display: flex;
  gap: 20px;
  height: calc(100vh - 120px);
  overflow: hidden;
}

.catalog-panel {
  width: 300px;
  flex-shrink: 0;
  overflow-y: auto;
  transition: width 0.3s ease;
}

.catalog-panel.collapsed {
  width: 40px;
}

.editor-panel {
  flex-grow: 1;
  overflow-y: auto;
  transition: width 0.3s ease;
}

.editor-panel.expanded {
  width: calc(100% - 60px);
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

.ai-icon {
  margin-right: 8px;
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

@media (max-width: 768px) {
  .content-container {
    flex-direction: column;
  }
  
  .catalog-panel {
    width: 100%;
    height: auto;
    max-height: 300px;
  }
}
</style>

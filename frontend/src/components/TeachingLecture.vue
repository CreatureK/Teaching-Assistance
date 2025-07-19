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
        <button class="btn btn-secondary" @click="handleSaveDraft">暂存</button>
        <button class="btn btn-primary" @click="handleSave">保存</button>
      </div>
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
import { ref, onMounted, onUnmounted, watch } from 'vue';
import Markdown from './markdown.vue';
import Catalog from './Catalog.vue';
import Prompt from './Prompt.vue';

const emit = defineEmits(['back', 'save', 'save-draft']);

// 控制Prompt组件显示
const showPrompt = ref(false);

// 目录展开状态
const catalogExpanded = ref(true);

// 处理目录折叠/展开
const handleCatalogToggle = (expanded: boolean) => {
  catalogExpanded.value = expanded;
};

// 处理Prompt提交事件
const handlePromptConfirm = (content: string) => {
  console.log('用户提交的讲义生成内容:', content);
  // 这里可以添加处理AI生成的逻辑，比如发送请求到后端
  showPrompt.value = false;
};

// 示例讲义内容
const markdownContent = ref(`# 第一章 课程介绍

## 1.1 教学目标

本课程旨在帮助学生掌握基本理论知识和实践技能，培养学生的独立思考能力和创新精神。

### 1.1.1 课程意义

通过本课程的学习，学生将能够理解该领域的核心概念，并能够应用这些知识解决实际问题。

### 1.1.2 学习内容

本课程包括理论学习和实践操作两部分，涵盖基础理论、核心技术和前沿应用。

## 1.2 教学安排

课程共16周，包括课堂讲授、实验实践和项目研讨。

### 1.2.1 课程进度

第1-8周主要学习基础理论，第9-16周进行实践项目和综合应用。

# 第二章 基础知识

## 2.1 关键概念

本章介绍该领域的核心概念和基本原理。

### 2.1.1 定义

对关键术语和基本概念进行定义和解释。

### 2.1.2 实例

通过具体实例帮助理解抽象概念。`);

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

// 保存
const handleSave = () => {
  const content = markdownContent.value;
  emit('save', content);
};

onMounted(() => {
  updateEditorHeight();
  window.addEventListener('resize', updateEditorHeight);
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

<!-- markdown 编辑器 -->
<template>
  <div class="markdown-editor-container">
    <div ref="editorEl" class="editor"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, defineProps, defineEmits } from 'vue';
import '@toast-ui/editor/dist/toastui-editor.css';
// @ts-ignore
import Editor from '@toast-ui/editor';
// 导入中文语言包
import '@toast-ui/editor/dist/i18n/zh-cn';

const props = defineProps({
  initialValue: {
    type: String,
    default: ''
  },
  height: {
    type: String,
    default: '500px'
  },
  previewStyle: {
    type: String,
    default: 'tab' // 'tab' 或 'vertical'
  },
  placeholder: {
    type: String,
    default: '请输入内容...'
  }
});

const emit = defineEmits(['update:content', 'change']);

const editorEl = ref<HTMLElement | null>(null);
let editor: any = null;

onMounted(() => {
  if (editorEl.value) {
    // 初始化编辑器
    editor = new Editor({
      el: editorEl.value,
      height: props.height,
      initialValue: props.initialValue,
      previewStyle: props.previewStyle,
      placeholder: props.placeholder,
      language: 'zh-CN', // 设置语言为中文
      hideModeSwitch: true, // 隐藏Markdown和所见即所得切换按钮
      initialEditType: 'markdown', // 始终使用Markdown模式
      toolbarItems: [
        ['heading', 'bold', 'italic', 'strike'],
        ['hr', 'quote'],
        ['ul', 'ol', 'task', 'indent', 'outdent'],
        ['table', 'link', 'code', 'codeblock']
      ],
      events: {
        change: () => {
          if (editor) {
            const content = editor.getMarkdown();
            emit('update:content', content);
            emit('change', content);
          }
        }
      }
    });
  }
});

// 提供获取编辑器内容的方法
const getMarkdown = () => {
  return editor ? editor.getMarkdown() : '';
};

// 提供设置编辑器内容的方法
const setMarkdown = (content: string) => {
  if (editor) {
    editor.setMarkdown(content);
  }
};

// 清理资源
onBeforeUnmount(() => {
  if (editor) {
    editor.destroy();
    editor = null;
  }
});

// 暴露方法给父组件
defineExpose({
  getMarkdown,
  setMarkdown,
  editor: () => editor
});
</script>

<style scoped>
.markdown-editor-container {
  width: 100%;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}

.editor {
  width: 100%;
}
</style>

<style>
/* 毛玻璃效果 - 工具栏 */
.toastui-editor-toolbar {
  backdrop-filter: blur(8px) !important;
  -webkit-backdrop-filter: blur(8px) !important;
  background-color: rgba(247, 249, 252, 0.7) !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3) !important;
  display: flex !important;
  align-items: center !important;
}

.toastui-editor-defaultUI-toolbar {
  background-color: transparent !important;
  align-items: center !important;
}

.toastui-editor-md-tab-container {
  background-color: transparent !important;
  display: flex !important;
  align-items: center !important;
}

/* 毛玻璃效果 - 按钮 */
.toastui-editor-tabs {
  display: flex !important;
  align-items: center !important;
}

.toastui-editor-tabs .tab-item {
  backdrop-filter: blur(5px) !important;
  -webkit-backdrop-filter: blur(5px) !important;
  background-color: rgba(234, 237, 241, 0.6) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 4px !important;
  margin-right: 4px !important;
  transition: all 0.3s ease !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  height: 28px !important;
}

.toastui-editor-tabs .tab-item.active {
  background-color: rgba(255, 255, 255, 0.7) !important;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1) !important;
}

/* 工具栏按钮 */
.toastui-editor-toolbar-icons {
  backdrop-filter: blur(5px) !important;
  -webkit-backdrop-filter: blur(5px) !important;
  background-color: rgba(255, 255, 255, 0.6) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 4px !important;
  margin: 0 2px !important;
  transition: all 0.2s ease !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.toastui-editor-toolbar-icons:hover {
  background-color: rgba(255, 255, 255, 0.8) !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08) !important;
}

.toastui-editor-toolbar-divider {
  background-color: rgba(0, 0, 0, 0.1) !important;
  align-self: center !important;
}

/* 工具栏组件组 */
.toastui-editor-toolbar-group {
  display: flex !important;
  align-items: center !important;
}

/* 隐藏"更多"按钮 */
button.more.toastui-editor-toolbar-icons {
  display: none !important;
}
</style>
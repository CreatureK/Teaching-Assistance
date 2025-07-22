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
// 导入UML插件
// @ts-ignore
import uml from '@toast-ui/editor-plugin-uml';

// 为TypeScript声明全局MathJax对象
declare global {
  interface Window {
    MathJax: any;
  }
}

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

// 创建自定义MathJax插件函数
function createMathPlugin() {
  return function() {
    return {
      toHTMLRenderers: {
        // 行内数学公式渲染器
        inlineMath(node: any) {
          const content = node.literal || '';
          return {
            type: 'html',
            content: `<span class="math-inline">$${content}$</span>`
          };
        },
        // 块级数学公式渲染器
        math(node: any) {
          const content = node.literal || '';
          return {
            type: 'html',
            content: `<div class="math-block">$$${content}$$</div>`
          };
        }
      },
      // 扩展Markdown语法分析器，识别$和$$包围的数学公式
      extendMarkdownIt(markdownit: any) {
        if (!markdownit) return markdownit;
  
        // 行内数学公式: $...$
        markdownit.inline.ruler.before('escape', 'inlineMath', (state: any, silent: boolean) => {
          const openMarker = '$';
          const closeMarker = '$';
          
          // 不以$开头，直接返回
          if (state.src.charAt(state.pos) !== openMarker) {
            return false;
          }
          
          // 尝试寻找结束标记
          const start = state.pos + 1;
          const max = state.posMax;
          let found = false;
          let end = -1;
          
          for (let i = start; i < max; i++) {
            if (state.src.charAt(i) === closeMarker && state.src.charAt(i - 1) !== '\\') {
              found = true;
              end = i;
              break;
            }
          }
          
          if (!found || start === end) {
            return false;
          }
          
          // 如果只是为了检测是否存在匹配，不进行标记操作
          if (silent) {
            return true;
          }
          
          // 提取数学公式内容
          const content = state.src.slice(start, end);
          
          // 创建标记
          const token = state.push('inlineMath', '', 0);
          token.content = content;
          token.markup = openMarker;
          token.literal = content;
          
          // 更新解析位置
          state.pos = end + 1;
          return true;
        });
        
        // 块级数学公式: $$...$$
        markdownit.block.ruler.before('fence', 'math', (state: any, startLine: number, endLine: number, silent: boolean) => {
          const openMarker = '$$';
          const closeMarker = '$$';
          
          // 检查开始标记
          let start = state.bMarks[startLine] + state.tShift[startLine];
          let max = state.eMarks[startLine];
          
          if (max - start < 2 || state.src.slice(start, start + 2) !== openMarker) {
            return false;
          }
          
          // 如果只是为了检测是否存在匹配，不进行标记操作
          if (silent) {
            return true;
          }
          
          // 查找结束标记
          let nextLine = startLine;
          let found = false;
          
          while (nextLine < endLine) {
            nextLine++;
            start = state.bMarks[nextLine] + state.tShift[nextLine];
            max = state.eMarks[nextLine];
            
            if (nextLine >= endLine || start >= max) {
              continue;
            }
            
            if (state.src.slice(start, max).trim().endsWith(closeMarker)) {
              found = true;
              break;
            }
          }
          
          if (!found) {
            return false;
          }
          
          // 提取数学公式内容
          let content = '';
          for (let i = startLine; i <= nextLine; i++) {
            let lineStart = state.bMarks[i] + state.tShift[i];
            let lineEnd = state.eMarks[i];
            
            if (i === startLine) {
              // 第一行，去除开始标记
              lineStart += 2;
            }
            
            if (i === nextLine) {
              // 最后一行，去除结束标记
              lineEnd -= 2;
            }
            
            if (content.length > 0) {
              content += '\n';
            }
            
            content += state.src.slice(lineStart, lineEnd);
          }
          
          // 创建标记
          let token = state.push('math', '', 0);
          token.content = content.trim();
          token.markup = openMarker;
          token.literal = content;
          
          // 更新解析位置
          state.line = nextLine + 1;
          return true;
        });
        
        return markdownit;
      }
    };
  };
}

onMounted(() => {
  if (editorEl.value) {
    // 初始化MathJax
    if (window.MathJax) {
      window.MathJax = {
        tex: {
          inlineMath: [['$', '$']],
          displayMath: [['$$', '$$']]
        },
        options: {
          processHtmlClass: 'math-inline|math-block'
        },
        startup: {
          typeset: false
        }
      };
    }

    // 加载MathJax脚本
    const loadMathJax = () => {
      const script = document.createElement('script');
      script.src = '/mathjax/tex-mml-chtml.js';
      
      script.async = true;
      script.id = 'MathJax-script';
      document.head.appendChild(script);
    };

    // 如果MathJax尚未加载，则加载它
    if (!document.getElementById('MathJax-script')) {
      loadMathJax();
    }

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
      plugins: [createMathPlugin(), uml],
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
        },
        load: () => {
          // 编辑器内容加载完成后滚动到顶部
          setTimeout(scrollToTop, 100);
        }
      },
      // 自定义渲染器钩子，处理渲染后的内容
      hooks: {
        addImageBlobHook: (blob: any, callback: any) => {
          // 图片上传处理（如果需要）
          callback('data:image/png;base64,');
          return false;
        }
      }
    });

    // 使用延时确保内容渲染后再滚动到顶部
    setTimeout(scrollToTop, 200);

    // 为编辑器的预览区域添加MathJax渲染
    // 在编辑器初始化后添加监听
    const handlePreviewChange = () => {
      setTimeout(() => {
        if (window.MathJax && window.MathJax.typesetPromise) {
          window.MathJax.typesetPromise().catch((err: any) => console.error('MathJax typeset error:', err));
        }
      }, 100);
    };

    // 监听编辑器tab切换事件
    const editorEl2 = editorEl.value;
    const tabsElement = editorEl2.querySelector('.toastui-editor-tabs');
    if (tabsElement) {
      const tabs = tabsElement.querySelectorAll('.tab-item');
      tabs.forEach(tab => {
        tab.addEventListener('click', () => {
          if (tab.textContent?.includes('预览')) {
            handlePreviewChange();
          }
        });
      });
    }

    // 初次加载也处理一次
    handlePreviewChange();
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
    // 设置内容后滚动到顶部，使用延时确保内容渲染完成
    setTimeout(scrollToTop, 100);
  }
};

// 滚动编辑器到顶部
const scrollToTop = () => {
  if (editor) {
    try {
      // 尝试获取所有可能的编辑器内容区域
      const mdEditor = editorEl.value?.querySelector('.toastui-editor-md-container .ProseMirror');
      if (mdEditor) {
        mdEditor.scrollTop = 0;
      }
      
      // 处理预览区域
      const previewArea = editorEl.value?.querySelector('.toastui-editor-md-preview .toastui-editor-contents');
      if (previewArea) {
        previewArea.scrollTop = 0;
      }
      
      // 直接通过editor API尝试滚动
      const editorInst = editor.getEditorElements();
      if (editorInst && editorInst.mdEditor) {
        editorInst.mdEditor.scrollTop = 0;
      }
      
      // 处理整个容器
      const editorContainer = editorEl.value?.querySelector('.toastui-editor-md-container');
      if (editorContainer) {
        editorContainer.scrollTop = 0;
      }
      
      // 使用编辑器实例的scrollTop方法(如果存在)
      if (editor.scrollTop) {
        editor.scrollTop(0);
      }
    } catch (e) {
      console.error('Failed to scroll editor to top:', e);
    }
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

/* 设置编辑器字体大小为16px */
.toastui-editor .ProseMirror {
  font-size: 16px !important;
}

.toastui-editor-contents {
  font-size: 16px !important;
}

.toastui-editor .toastui-editor-md-preview {
  font-size: 16px !important;
}

.toastui-editor-md-preview .toastui-editor-contents {
  font-size: 16px !important;
}

/* 数学公式样式 */
.math-inline {
  display: inline-block;
}

.math-block {
  display: block;
  text-align: center;
  margin: 1em 0;
}

/* 修复弹出菜单层叠顺序问题 */
.toastui-editor-popup {
  z-index: 20 !important;
  position: absolute !important;
}

.toastui-editor-dropdown-toolbar {
  z-index: 20 !important;
}

/* 确保编辑区内容不会覆盖弹出菜单 */
.toastui-editor-defaultUI {
  position: relative;
}

.toastui-editor .ProseMirror {
  z-index: 1;
}

/* 强化编辑区域和弹出菜单的层叠关系 */
.toastui-editor-popup, 
.toastui-editor-dropdown-toolbar, 
.toastui-editor-dropdown-menu,
.toastui-editor-context-menu {
  z-index: 50 !important; 
  position: absolute !important;
}

.toastui-editor-toolbar {
  z-index: 30 !important;
  position: relative !important;
}

.toastui-editor-md-container,
.toastui-editor-ww-container {
  z-index: 10 !important;
  position: relative !important;
}

.toastui-editor-md-container .ProseMirror,
.toastui-editor-ww-container .ProseMirror,
.toastui-editor .ProseMirror {
  z-index: 3 !important;
  position: relative !important;
}
</style>
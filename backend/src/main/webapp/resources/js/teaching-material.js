// 全局变量
let editor_materialEditor = null;  // 编辑器实例
let toc = [];  // 目录数组
let currentHeading = '';  // 当前标题
let observer = null;  // 观察者实例
let isLoading = false;  // 加载状态
let isAILoading = false;  // AI生成加载状态

// DOM元素
let sidebar = null;  // 侧边栏元素
let showSidebar = true;  // 是否显示侧边栏

// CSRF令牌和用户ID
const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
const csrfToken = document.querySelector('meta[name="_csrf"]')?.content || '';
const userId = document.getElementById('userId')?.value || '';
const courseId = document.getElementById('courseId')?.value || '';


// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    // 初始化UI组件
    initUI();
    
    // 初始化编辑器
    initEditor();
    
    // 加载教学材料数据
    fetchMaterial().then(() => {
        // 内容加载完成后设置滚动监听
        setupScrollSpy();
    });
    
    // 设置事件监听器
    setupEventListeners();
});

/**
 * 初始化UI组件
 */
function initUI() {
    // 获取侧边栏DOM元素
    sidebar = document.querySelector('.sidebar');
    
    // 显示加载中遮罩
    showLoading();
}

/**
 * 初始化Markdown编辑器
 */
function initEditor() {
    editor_materialEditor = editormd("materialEditor", {
        width: "100%",
        height: "100%",
        path: "${pageContext.request.contextPath}/resources/lib/editor.md/lib/",
        saveHTMLToTextarea: true,
        placeholder: "请输入教学讲义内容...",
        imageUpload: true,
        imageFormats: ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
        imageUploadURL: "${pageContext.request.contextPath}/upload/image",
        onload: function() {
            // 编辑器加载完成后隐藏加载中遮罩
            hideLoading();
            
            // 监听内容变化，更新目录
            this.cm.on("change", function() {
                // 添加小延迟，确保预览已更新
                setTimeout(() => {
                    generateToc();
                }, 100);
            });
            
            // 初始生成目录
            setTimeout(() => {
                generateToc();
                setupScrollSpy();
            }, 500);
        },
        onpreviewscroll: function() {
            setupScrollSpy();
        },
        toolbarIcons: function() {
            return [
                "undo", "redo", "|",
                "bold", "del", "italic", "quote", "|",
                "h1", "h2", "h3", "h4", "h5", "h6", "|",
                "list-ul", "list-ol", "hr", "|",
                "link", "image", "code", "preformatted-text", "code-block", "table", "|",
                "watch", "preview", "fullscreen", "search"
            ];
        }
    });
    
    // 将编辑器实例存储在全局变量中
    window.editor_materialEditor = editor_materialEditor;
}

/**
 * 为UI元素设置事件监听器
 */
function setupEventListeners() {
    // 返回按钮
    const backButton = document.querySelector('.back-button');
    if (backButton) {
        backButton.addEventListener('click', goBack);
    }
    
    // 切换侧边栏按钮
    const toggleButton = document.querySelector('.toggle-sidebar');
    if (toggleButton) {
        toggleButton.addEventListener('click', toggleSidebar);
    }
    
    // 保存按钮
    const saveMaterialBtn = document.getElementById('saveMaterialBtn');
    if (saveMaterialBtn) {
        saveMaterialBtn.addEventListener('click', saveMaterial);
    }
    
    // AI生成按钮
    const aiGenerateBtn = document.getElementById('aiGenerateBtn');
    if (aiGenerateBtn) {
        aiGenerateBtn.addEventListener('click', showAIDialog);
    }
    
    // AI对话框按钮
    const aiDialogClose = document.getElementById('aiDialogClose');
    if (aiDialogClose) {
        aiDialogClose.addEventListener('click', hideAIDialog);
    }
    
    const aiForm = document.getElementById('aiForm');
    if (aiForm) {
        aiForm.addEventListener('submit', function(e) {
            e.preventDefault();
            generateWithAI();
        });
    }
    
    // 点击模态框外部关闭模态框
    window.addEventListener('click', function(event) {
        const modal = document.getElementById('aiDialog');
        if (event.target === modal) {
            hideAIDialog();
        }
    });
    
    // 处理窗口大小变化
    window.addEventListener('resize', handleResize);
}

/**
 * 处理窗口大小变化
 */
function handleResize() {
    if (editor_materialEditor) {
        editor_materialEditor.resize();
    }
}

/**
 * 返回上一页
 */
function goBack() {
    window.history.back();
}

/**
 * 切换侧边栏显示/隐藏
 */
function toggleSidebar() {
    showSidebar = !showSidebar;
    if (sidebar) {
        if (showSidebar) {
            sidebar.classList.remove('hidden');
            // 添加小延迟确保过渡效果正常
            setTimeout(() => {
                if (editor_materialEditor) {
                    editor_materialEditor.resize();
                }
            }, 300);
        } else {
            sidebar.classList.add('hidden');
            // 添加小延迟确保过渡效果正常
            setTimeout(() => {
                if (editor_materialEditor) {
                    editor_materialEditor.resize();
                }
            }, 300);
        }
    }
}

/**
 * 显示加载中遮罩
 */
function showLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.style.display = 'flex';
        isLoading = true;
    }
}

/**
 * 隐藏加载中遮罩
 */
function hideLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.style.display = 'none';
        isLoading = false;
    }
}

/**
 * 显示消息提示
 * @param {string} message - 要显示的消息内容
 * @param {string} type - 消息类型 (success, error, warning, info)
 * @param {number} duration - 消息显示时长(毫秒)，默认为3000毫秒
 */
function showMessage(message, type = 'info', duration = 3000) {
    const messageContainer = document.getElementById('messageContainer');
    if (!messageContainer) return;
    
    const messageEl = document.createElement('div');
    messageEl.className = `message ${type}`;
    messageEl.textContent = message;
    
    messageContainer.appendChild(messageEl);
    
    // 在指定时间后自动移除消息
    setTimeout(() => {
        messageEl.classList.add('fade-out');
        setTimeout(() => {
            messageEl.remove();
        }, 300);
    }, duration);
}

/**
 * 显示AI生成对话框
 */
function showAIDialog() {
    const aiDialog = document.getElementById('aiDialog');
    if (aiDialog) {
        aiDialog.style.display = 'block';
        // 对话框显示后自动聚焦到输入框
        setTimeout(() => {
            const firstInput = aiDialog.querySelector('input, textarea');
            if (firstInput) {
                firstInput.focus();
            }
        }, 100);
    }
}

/**
 * 隐藏AI生成对话框
 */
function hideAIDialog() {
    const aiDialog = document.getElementById('aiDialog');
    if (aiDialog) {
        aiDialog.style.display = 'none';
        // 对话框隐藏时清空输入
        const promptInput = document.getElementById('aiPrompt');
        if (promptInput) {
            promptInput.value = '';
        }
    }
}

/**
 * 使用AI生成内容
 */
function generateWithAI() {
    const promptInput = document.getElementById('aiPrompt');
    const generateBtn = document.getElementById('generateBtn');
    
    if (!promptInput || !generateBtn) return;
    
    const prompt = promptInput.value.trim();
    
    if (!prompt) {
        showMessage('请输入生成要求', 'warning');
        return;
    }
    
    // Disable the button and show loading state
    generateBtn.disabled = true;
    generateBtn.innerHTML = '<span class="loading-spinner"></span> 生成中...';
    isAILoading = true;
    
    // Prepare the request data
    const requestData = {
        courseId: courseId,
        prompt: prompt
    };
    
    // Make the API call
    fetch(`/teacher/material/${courseId}/generate`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest',
            [csrfHeaderName]: csrfToken,
            'userId': userId
        },
        body: JSON.stringify(requestData)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                try {
                    const errorData = JSON.parse(text);
                    throw new Error(errorData.message || '生成失败');
                } catch (e) {
                    throw new Error(text || '生成失败');
                }
            });
        }
        return response.json();
    })
    .then(data => {
        if (data && data.content) {
            // Set the generated content in the editor
            if (editor_materialEditor) {
                editor_materialEditor.setMarkdown(data.content);
                showMessage('生成成功！', 'success');
                hideAIDialog();
                
                // Clear the prompt
                if (promptInput) {
                    promptInput.value = '';
                }
                
                // Update TOC after a short delay to allow the preview to update
                setTimeout(() => {
                    generateToc();
                    setupScrollSpy();
                }, 500);
            }
        } else {
            throw new Error('生成失败：返回数据格式错误');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showMessage(error.message || '生成过程中出错', 'error');
    })
    .finally(() => {
        // Re-enable the button and reset loading state
        if (generateBtn) {
            generateBtn.disabled = false;
            generateBtn.textContent = '生 成';
        }
        isAILoading = false;
    });
}

/**
 * 保存教学讲义
 */
function saveMaterial() {
    if (isLoading) return;
    
    const content = editor_materialEditor ? editor_materialEditor.getMarkdown() : '';
    
    // 显示加载中
    showLoading();
    
    // 准备请求头
    const headers = {
        'Content-Type': 'application/json',
        [csrfHeaderName]: csrfToken
    };
    
    // 如果用户ID存在，添加到请求头
    if (userId) {
        headers['userId'] = userId;
    }
    
    // 发送API请求
    fetch('/teacher/teaching-material/' + courseId + '/save', {
        method: 'POST',
        headers: headers,
        body: JSON.stringify({
            courseId: courseId,
            content: content
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('HTTP错误，状态码: ' + response.status);
        }
        return response.json();
    })
    .then(data => {
        if (data && data.success) {
            showMessage('教学讲义保存成功！', 'success');
        } else {
            const errorMsg = data && data.message ? data.message : '保存失败';
            showMessage(errorMsg, 'error');
        }
    })
    .catch(error => {
        console.error('保存教学讲义时出错:', error);
        showMessage('保存过程中出错：' + (error.message || '未知错误'), 'error');
    })
    .finally(() => {
        hideLoading();
    });
}

/**
 * 从服务器获取教学讲义数据
 */
async function fetchMaterial() {
    if (isLoading) return;
    
    showLoading();
    
    try {
        const response = await fetch(`/teacher/teaching-material/${courseId}`, {
            headers: {
                [csrfHeaderName]: csrfToken,
                'userId': userId
            }
        });
        
        if (!response.ok) {
            throw new Error('获取教学讲义失败');
        }
        
        const result = await response.json();
        if (result && result.success) {
            const material = result.data || {};
            // 如果存在内容，设置到编辑器中
            if (material.content && editor_materialEditor) {
                editor_materialEditor.setMarkdown(material.content);
            }
        }
    } catch (error) {
        console.error('获取教学讲义时出错:', error);
        showMessage('加载教学讲义失败: ' + (error.message || '未知错误'), 'error');
    } finally {
        hideLoading();
    }
}

/**
 * 从预览内容生成目录
 */
function generateToc() {
    const preview = document.querySelector('.editormd-preview');
    if (!preview) return;
    
    const headings = preview.querySelectorAll('h1, h2, h3, h4, h5, h6');
    toc = [];
    
    headings.forEach((heading, index) => {
        const level = parseInt(heading.tagName.substring(1));
        const text = heading.textContent;
        const id = `heading-${index}`;
        
        // 如果标题没有ID，则设置一个
        if (!heading.id) {
            heading.id = id;
        }
        
        toc.push({
            id: heading.id,
            text: text,
            level: level,
            element: heading
        });
    });
    
    // 更新UI中的目录
    updateTocUI();
}

/**
 * 更新UI中的目录
 */
function updateTocUI() {
    const tocContainer = document.querySelector('.toc');
    const emptyTip = document.querySelector('.empty-tip');
    
    if (!tocContainer || !emptyTip) return;
    
    if (toc.length === 0) {
        emptyTip.style.display = 'block';
        tocContainer.style.display = 'none';
        return;
    }
    
    // 清空现有目录
    tocContainer.innerHTML = '';
    
    // 构建目录HTML
    toc.forEach(item => {
        const li = document.createElement('li');
        li.className = `toc-item toc-level-${item.level}`;
        li.innerHTML = `<a href="#${item.id}" data-id="${item.id}">${escapeHtml(item.text)}</a>`;
        tocContainer.appendChild(li);
    });
    
    emptyTip.style.display = 'none';
    tocContainer.style.display = 'block';
}

/**
 * 滚动到指定标题
 * @param {string} id - 要滚动到的标题ID
 */
function scrollToHeading(id) {
    const heading = document.getElementById(id);
    if (heading) {
        // 更新活动状态
        document.querySelectorAll('.toc a').forEach(link => {
            link.classList.remove('active');
            if (link.getAttribute('data-id') === id) {
                link.classList.add('active');
                // 滚动目录以使活动链接可见
                link.scrollIntoView({ block: 'nearest', behavior: 'smooth' });
            }
        });
        
        // 滚动到标题
        heading.scrollIntoView({ behavior: 'smooth', block: 'start' });
        
        // 更新当前标题
        currentHeading = id;
    }
}

/**
 * 设置交叉观察器以高亮显示TOC中的当前章节
 */
function setupScrollSpy() {
    // 移除现有的观察器（如果有）
    if (observer) {
        observer.disconnect();
    }
    
    const headings = document.querySelectorAll('.editormd-preview h1, .editormd-preview h2, .editormd-preview h3, .editormd-preview h4, .editormd-preview h5, .editormd-preview h6');
    
    if (headings.length === 0) return;
    
    // 创建新的观察器
    observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const id = entry.target.id;
                if (id && id !== currentHeading) {
                    // 更新TOC中的活动状态
                    document.querySelectorAll('.toc a').forEach(link => {
                        link.classList.remove('active');
                        if (link.getAttribute('data-id') === id) {
                            link.classList.add('active');
                            currentHeading = id;
                        }
                    });
                }
            }
        });
    }, {
        root: null,
        rootMargin: '0px 0px -80% 0px',
        threshold: 0.1
    });
    
    // 观察所有标题
    headings.forEach(heading => {
        observer.observe(heading);
    });
}

/**
 * 转义HTML以防止XSS
 */
function escapeHtml(unsafe) {
    if (!unsafe) return '';
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// 使函数在全局可用
window.scrollToHeading = scrollToHeading;

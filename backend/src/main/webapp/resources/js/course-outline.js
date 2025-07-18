// 全局变量
let editor_outlineEditor = null;
let chapters = [];

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    // 初始化Editor.md
    if (typeof editormd !== 'undefined') {
        editor_outlineEditor = editormd('outlineEditor', {
            width: '100%',
            height: '100%',
            path: '${pageContext.request.contextPath}/resources/lib/editor.md/lib/',
            theme: 'default',
            previewTheme: 'default',
            editorTheme: 'base16-light',
            markdown: '', // 内容将在JSP中设置
            codeFold: true,
            saveHTMLToTextarea: true,
            searchReplace: true,
            htmlDecode: 'style,script,iframe|on*',
            toolbarIcons: 'full',
            emoji: true,
            taskList: true,
            tocm: true,
            tex: true,
            flowChart: true,
            sequenceDiagram: true,
            imageUpload: true,
            imageFormats: ['jpg', 'jpeg', 'gif', 'png', 'bmp', 'webp'],
            imageUploadURL: '${pageContext.request.contextPath}/upload/image'
        });
        
        // 设置编辑器的onload和onchange事件
        if (editor_outlineEditor) {
            editor_outlineEditor.on('load', function() {
                console.log('Editor.md is loaded!');
                // 初始解析章节
                const content = editor_outlineEditor.getMarkdown();
                chapters = parseChapters(content);
                updateChapterList();
            });
            
            editor_outlineEditor.on('change', function() {
                const content = editor_outlineEditor.getMarkdown();
                chapters = parseChapters(content);
                updateChapterList();
            });
        }
    }
    
    // 绑定事件
    bindEvents();
});

// 绑定事件
function bindEvents() {
    // 返回按钮
    const backBtn = document.querySelector('.back-button');
    if (backBtn) {
        backBtn.addEventListener('click', goBack);
    }
    
    // 保存草稿按钮
    const saveDraftBtn = document.getElementById('saveDraftBtn');
    if (saveDraftBtn) {
        saveDraftBtn.addEventListener('click', function() {
            saveSyllabus(true);
        });
    }
    
    // 保存并发布按钮
    const savePublishBtn = document.getElementById('savePublishBtn');
    if (savePublishBtn) {
        savePublishBtn.addEventListener('click', function() {
            saveSyllabus(false);
        });
    }
    
    // AI生成按钮
    const generateAIBtn = document.getElementById('generateAIBtn');
    if (generateAIBtn) {
        generateAIBtn.addEventListener('click', showAIDialog);
    }
    
    // AI对话框关闭按钮
    const closeAIDialogBtn = document.querySelector('.ai-dialog-close');
    if (closeAIDialogBtn) {
        closeAIDialogBtn.addEventListener('click', hideAIDialog);
    }
    
    // AI生成表单提交
    const aiForm = document.getElementById('aiForm');
    if (aiForm) {
        aiForm.addEventListener('submit', generateWithAI);
    }
}

// 返回上一页
function goBack() {
    window.history.back();
}

// 切换章节展开/收起
function toggleChapter(chapterIndex) {
    if (chapters[chapterIndex]) {
        chapters[chapterIndex].expanded = !chapters[chapterIndex].expanded;
        updateChapterList();
    }
}

// 解析Markdown内容为章节
function parseChapters(content) {
    if (!content) return [];
    
    const lines = content.split('\n');
    const resultChapters = [];
    let currentChapter = null;
    
    lines.forEach(line => {
        // 匹配章节标题 (## 或 ###)
        const chapterMatch = line.match(/^#{2,3}\s+(.+?)(?:\s*\((.+?)\))?\s*$/);
        if (chapterMatch) {
            if (currentChapter) {
                resultChapters.push(currentChapter);
            }
            currentChapter = {
                title: chapterMatch[1],
                duration: chapterMatch[2] || '',
                objectives: [],
                expanded: false
            };
        } 
        // 匹配学习目标
        else if (currentChapter) {
            const objectiveMatch = line.match(/^-\s*(.+)/);
            if (objectiveMatch) {
                currentChapter.objectives.push(objectiveMatch[1]);
            }
        }
    });
    
    if (currentChapter) {
        resultChapters.push(currentChapter);
    }
    
    return resultChapters;
}

// 更新章节列表显示
function updateChapterList() {
    const chapterList = document.getElementById('chapterList');
    if (!chapterList) return;
    
    if (chapters.length === 0) {
        chapterList.innerHTML = '<div class="empty-tip"><p>暂无章节数据，请先编辑大纲内容</p></div>';
        return;
    }
    
    let html = '';
    chapters.forEach((chapter, index) => {
        html += `
            <div class="chapter-item">
                <div class="chapter-header" onclick="toggleChapter(${index})">
                    <span class="chapter-title">${escapeHtml(chapter.title)}</span>
                    ${chapter.duration ? `<span class="chapter-duration">${escapeHtml(chapter.duration)}</span>` : ''}
                </div>
                <div class="chapter-content" ${chapter.expanded ? '' : 'style="display:none;"'}>
                    ${chapter.objectives && chapter.objectives.length > 0 ? 
                        `<div class="learning-objectives">
                            ${chapter.objectives.map(obj => 
                                `<div class="objective-item">${escapeHtml(obj)}</div>`
                            ).join('')}
                        </div>` 
                        : '<div class="no-objectives">暂无学习目标</div>'
                    }
                </div>
            </div>
        `;
    });
    
    chapterList.innerHTML = html;
}

// HTML转义
function escapeHtml(unsafe) {
    if (!unsafe) return '';
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// 保存大纲
function saveSyllabus(isDraft = false) {
    const content = editor_outlineEditor ? editor_outlineEditor.getMarkdown() : '';
    // 更新章节数据
    chapters = parseChapters(content);
    updateChapterList();
    
    const headers = {
        'Content-Type': 'application/json',
        '${_csrf.headerName}': '${_csrf.token}'
    };
    
    const userId = '${sessionScope.userId}';
    if (userId) {
        headers['userId'] = userId;
    }
    
    showLoading();
    
    fetch('/teacher/syllabus/${param.courseId}/save', {
        method: 'POST',
        headers: headers,
        body: JSON.stringify({
            courseId: '${param.courseId}',
            content: content,
            status: isDraft ? 0 : 1 // 0表示草稿，1表示已发布
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('HTTP error, status = ' + response.status);
        }
        return response.json();
    })
    .then(data => {
        if (data && data.success) {
            showMessage(isDraft ? '草稿保存成功！' : '课程大纲保存成功！', 'success');
        } else {
            const errorMsg = data && data.message ? data.message : '保存失败';
            showMessage(errorMsg, 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showMessage('保存过程中出错：' + (error.message || '未知错误'), 'error');
    })
    .finally(() => {
        hideLoading();
    });
}

// 显示AI对话框
function showAIDialog() {
    const aiDialog = document.getElementById('aiDialog');
    if (aiDialog) {
        // 设置课程名称
        const courseTitleInput = document.getElementById('aiCourseTitle');
        if (courseTitleInput) {
            const courseTitle = document.getElementById('courseTitle');
            if (courseTitle) {
                courseTitleInput.value = courseTitle.textContent || '';
            }
        }
        
        aiDialog.style.display = 'flex';
    }
}

// 隐藏AI对话框
function hideAIDialog() {
    const aiDialog = document.getElementById('aiDialog');
    if (aiDialog) {
        aiDialog.style.display = 'none';
    }
}

// 使用AI生成大纲
function generateWithAI(event) {
    event.preventDefault();
    
    const form = document.getElementById('aiForm');
    const formData = new FormData(form);
    const aiData = {
        courseId: '${param.courseId}',
        courseTitle: formData.get('courseTitle') || '',
        courseIntroduction: formData.get('courseIntroduction') || '',
        teachingTarget: formData.get('teachingTarget') || '',
        request: formData.get('request') || ''
    };
    
    const headers = {
        'Content-Type': 'application/json',
        '${_csrf.headerName}': '${_csrf.token}'
    };
    
    const userId = '${sessionScope.userId}';
    if (userId) {
        headers['userId'] = userId;
    }
    
    const generateBtn = document.getElementById('generateBtn');
    const originalText = generateBtn ? generateBtn.textContent : '开始生成';
    
    if (generateBtn) {
        generateBtn.disabled = true;
        generateBtn.textContent = '生成中...';
    }
    
    showLoading();
    
    fetch('/teacher/syllabus/generate', {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(aiData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('HTTP error, status = ' + response.status);
        }
        return response.json();
    })
    .then(data => {
        if (data && data.success) {
            if (editor_outlineEditor) {
                const content = data.data || '';
                editor_outlineEditor.setMarkdown(content);
                chapters = parseChapters(content);
                updateChapterList();
                hideAIDialog();
                showMessage('AI生成成功！', 'success');
            }
        } else {
            const errorMsg = data && data.message ? data.message : '生成失败';
            showMessage(errorMsg, 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showMessage('生成过程中出错：' + (error.message || '未知错误'), 'error');
    })
    .finally(() => {
        if (generateBtn) {
            generateBtn.disabled = false;
            generateBtn.textContent = originalText;
        }
        hideLoading();
    });
}

// 显示加载中遮罩
function showLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.style.display = 'flex';
    }
}

// 隐藏加载中遮罩
function hideLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.style.display = 'none';
    }
}

// 显示消息提示
function showMessage(message, type = 'info') {
    const messageContainer = document.getElementById('messageContainer');
    if (!messageContainer) return;
    
    const messageEl = document.createElement('div');
    messageEl.className = `message ${type}`;
    messageEl.textContent = message;
    
    messageContainer.appendChild(messageEl);
    
    // 3秒后自动消失
    setTimeout(() => {
        messageEl.style.opacity = '0';
        setTimeout(() => {
            if (messageContainer.contains(messageEl)) {
                messageContainer.removeChild(messageEl);
            }
        }, 300);
    }, 3000);
}

// 点击模态框外部关闭
window.onclick = function(event) {
    const modal = document.getElementById('aiDialog');
    if (event.target == modal) {
        modal.style.display = 'none';
    }
};

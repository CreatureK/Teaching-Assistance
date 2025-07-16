// 课程数据
let courses = [
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
];

// 是否处于删除模式
let isDeleteMode = false;

// DOM 元素引用
const courseGrid = document.getElementById('courseGrid');
const courseCount = document.getElementById('courseCount');
const deleteBtn = document.getElementById('deleteBtn');
const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
const addCourseBtn = document.getElementById('addCourseBtn');

// 初始化页面
function initPage() {
  renderCourses();
  updateCourseCount();
  setupEventListeners();
}

// 渲染课程列表
function renderCourses() {
  // 清除现有的课程卡片（除了添加按钮）
  const existingCourseCards = document.querySelectorAll('.course-card:not(.add-card)');
  existingCourseCards.forEach(card => card.remove());

  // 渲染课程列表
  courses.forEach(course => {
    const courseCard = createCourseCard(course);
    courseGrid.insertBefore(courseCard, addCourseBtn.nextSibling);
  });
}

// 创建课程卡片元素
function createCourseCard(course) {
  const courseCard = document.createElement('div');
  courseCard.className = 'course-card';
  courseCard.dataset.id = course.id;

  if (course.isSelected) {
    courseCard.classList.add('selected');
  }

  // 构建卡片内容
  courseCard.innerHTML = `
    <div class="course-image">
      <img src="${course.imageUrl}" alt="课程封面" />
      ${isDeleteMode ? `
        <div class="selection-indicator" style="${course.isSelected ? '' : 'display: none;'}">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="20 6 9 17 4 12"></polyline>
          </svg>
        </div>
      ` : ''}
    </div>
    <div class="course-info">
      ${course.isEditing ?
      `<input type="text" value="${course.title}" class="title-input" />` :
      `<h3>${course.title}</h3>`}
    </div>
  `;

  // 添加点击事件
  courseCard.addEventListener('click', () => handleCourseClick(course));

  // 如果正在编辑，给输入框添加事件
  if (course.isEditing) {
    const titleInput = courseCard.querySelector('.title-input');
    titleInput.focus();
    titleInput.select();

    titleInput.addEventListener('blur', () => finishEdit(course, titleInput.value));
    titleInput.addEventListener('keyup', (e) => {
      if (e.key === 'Enter') finishEdit(course, titleInput.value);
    });
    titleInput.addEventListener('click', (e) => e.stopPropagation());
  } else {
    const titleElement = courseCard.querySelector('h3');
    titleElement.addEventListener('click', (e) => {
      e.stopPropagation();
      editTitle(course);
    });
  }

  return courseCard;
}

// 更新课程数量
function updateCourseCount() {
  courseCount.textContent = courses.length;
}

// 设置事件监听器
function setupEventListeners() {
  // 添加课程按钮点击事件
  addCourseBtn.addEventListener('click', addNewCourse);

  // 删除按钮点击事件
  deleteBtn.addEventListener('click', toggleDeleteMode);

  // 确认删除按钮点击事件
  confirmDeleteBtn.addEventListener('click', deleteSelectedCourses);
}

// 添加新课程
function addNewCourse() {
  const newId = courses.length > 0 ? Math.max(...courses.map(c => c.id)) + 1 : 1;
  const newCourse = {
    id: newId,
    title: '新课程',
    imageUrl: 'https://res.cloudinary.com/dm3rouwgn/image/upload/t_media_lib_thumb/zuxomrowewwe5spaci7w',
    isEditing: false,
    isSelected: false
  };

  // 添加到课程数组的开头
  courses.unshift(newCourse);

  // 重新渲染课程并更新计数
  renderCourses();
  updateCourseCount();

  // 进入编辑模式
  setTimeout(() => {
    editTitle(newCourse);
  }, 100);
}

// 编辑课程标题
function editTitle(course) {
  if (isDeleteMode) {
    toggleCourseSelection(course);
    return;
  }

  // 设置编辑状态
  course.isEditing = true;
  renderCourses();
}

// 完成编辑
function finishEdit(course, newTitle) {
  course.isEditing = false;
  course.title = newTitle.trim() || course.title; // 如果为空，保留原标题
  renderCourses();
}

// 切换删除模式
function toggleDeleteMode() {
  isDeleteMode = !isDeleteMode;

  // 更新UI
  deleteBtn.classList.toggle('active', isDeleteMode);
  confirmDeleteBtn.style.display = isDeleteMode ? 'block' : 'none';

  // 退出删除模式时，取消所有选择
  if (!isDeleteMode) {
    courses.forEach(course => course.isSelected = false);
  }

  renderCourses();
}

// 切换课程选择状态
function toggleCourseSelection(course) {
  if (!isDeleteMode) return;

  course.isSelected = !course.isSelected;
  renderCourses();
}

// 删除选中的课程
function deleteSelectedCourses() {
  courses = courses.filter(course => !course.isSelected);
  isDeleteMode = false;

  // 更新UI
  deleteBtn.classList.remove('active');
  confirmDeleteBtn.style.display = 'none';

  renderCourses();
  updateCourseCount();
}

// 处理课程点击
function handleCourseClick(course) {
  if (isDeleteMode) {
    toggleCourseSelection(course);
  }
  // 这里可以添加课程选择逻辑，例如跳转到课程详情页
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', initPage); 
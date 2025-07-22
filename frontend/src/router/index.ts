import { createRouter, createWebHistory } from 'vue-router'
import * as authApi from '../hooks/api/auth'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('../views/HomeView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/AuthForm.vue')
  },
  {
    path: '/admin',
    name: 'admin',
    component: () => import('../views/Admin.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  // 如果访问不存在的路由，重定向到首页
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  // 检查路由是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 检查用户是否已登录
    if (!authApi.checkAuthentication()) {
      // 未登录，重定向到登录页
      console.log('[路由守卫] 用户未登录，重定向到登录页')
      next({ name: 'login' })
    } else {
      // 已登录，检查用户角色
      const isAdmin = localStorage.getItem('isAdmin') === 'true'
      console.log('[路由守卫] 用户已登录，isAdmin =', isAdmin, '请求路径:', to.path)
      
      if (to.path === '/' && isAdmin) {
        // 管理员访问首页，重定向到管理页
        console.log('[路由守卫] 管理员访问首页，重定向到管理页')
        next('/admin')
      } else if (to.matched.some(record => record.meta.requiresAdmin) && !isAdmin) {
        // 非管理员访问需要管理员权限的页面，重定向到首页
        console.log('[路由守卫] 非管理员访问管理页面，重定向到首页')
        next('/')
      } else {
        // 允许访问，但确保清除所有本地存储的视图状态
        // 这样无论从哪来，都会回到课程管理页面
        if (to.path === '/' && !isAdmin && from.path === '/login') {
          console.log('[路由守卫] 从登录页进入，清除所有视图状态')
          localStorage.removeItem('showFunctionSelect');
          localStorage.removeItem('showCourseInfo');
          localStorage.removeItem('showCourseDescription');
          localStorage.removeItem('showCourseOutline');
          localStorage.removeItem('showTeachingLecture');
          localStorage.removeItem('selectedCourseTitle');
          localStorage.removeItem('selectedCourseId');
          localStorage.removeItem('selectedModuleId');
        }
        console.log('[路由守卫] 允许访问:', to.path)
        next()
      }
    }
  } else {
    // 不需要认证的路由，直接访问
    console.log('[路由守卫] 不需要认证的路由，允许访问:', to.path)
    next()
  }
})

export default router 
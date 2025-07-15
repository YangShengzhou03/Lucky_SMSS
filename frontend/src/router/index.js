import { createRouter, createWebHistory } from 'vue-router';
import IndexLayout from '@/components/IndexLayout.vue';
import StudentLayout from '@/components/StudentLayout.vue';
import LoginPage from '@/views/LoginPage.vue';
import RegisterPage from '@/views/RegisterPage.vue';
import HomePage from '@/views/Student/HomePage.vue';

const routes = [
  // 未登录布局（首页 + 登录/注册）
  {
    path: '/',
    component: IndexLayout,
    children: [
      { path: '', name: 'Home', meta: { title: '首页' } },
      { path: 'login', name: 'Login', component: LoginPage, meta: { title: '登录' } },
      { path: 'register', name: 'Register', component: RegisterPage, meta: { title: '注册' } }
    ]
  },

  // 登录后布局（学生系统）
  {
    path: '/stulayout',
    component: StudentLayout,
    children: [
      { path: '', name: 'StudentHome', component: HomePage, meta: { title: '学生首页' } }
    ]
  },

  // 错误路径重定向（仅保留这一条）
  { path: '/:pathMatch(.*)*', redirect: '/' }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫
router.beforeEach((to) => {
  // 设置页面标题
  document.title = to.meta.title || 'Lucky SMS';
});

export default router;
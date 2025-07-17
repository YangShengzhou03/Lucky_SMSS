import { createRouter, createWebHistory } from 'vue-router';
import IndexLayout from '@/components/IndexLayout.vue';
import StudentLayout from '@/components/StudentLayout.vue';
import LoginPage from '@/views/LoginPage.vue';
import RegisterPage from '@/views/RegisterPage.vue';
import StudentHome from '@/views/Student/HomePage.vue';
import StudentStatus from '@/views/Student/StatusPage.vue';
import StudentGrades from '@/views/Student/GradesPage.vue';
import StudentSchedule from '@/views/Student/SchedulePage.vue';
import StudentLibrary from '@/views/Student/LibraryPage.vue';
import StudentCourse from '@/views/Student/CoursePage.vue';
import StudentSettings from '@/views/Student/SettingsPage.vue';
import TeacherLayout from '@/components/TeacherLayout.vue';
import TeacherHome from '@/views/Teacher/HomePage.vue'

const routes = [
  // 未登录布局（首页 + 登录/注册）
  {
    path: '/',
    component: IndexLayout,
    children: [
      { path: '', name: 'Home', meta: { title: 'Lucky-SMS' } },
      { path: '/login', name: 'Login', component: LoginPage, meta: { title: '登录' } },
      { path: '/register', name: 'Register', component: RegisterPage, meta: { title: '注册' } }
    ]
  },

  // 登录后布局（学生系统）
  {
    path: '/student',
    component: StudentLayout,
    children: [
      { path: '', name: 'StudentHome', component: StudentHome, meta: { title: '学生首页' } },
      { path: 'status', name: 'StudentStatus', component: StudentStatus, meta: { title: '学籍信息' } },
      { path: 'grades', name: 'StudentGrades', component: StudentGrades, meta: { title: '成绩查询' } },
      { path: 'schedule', name: 'StudentSchedule', component: StudentSchedule, meta: { title: '课表查询' } },
      { path: 'library', name: 'StudentLibrary', component: StudentLibrary, meta: { title: '图书借阅' } },
      { path: 'course', name: 'StudentCourse', component: StudentCourse, meta: { title: '选课系统' } },
      { path: 'settings', name: 'StudentSettings', component: StudentSettings, meta: { title: '系统设置' } },

    ]
  },

  // 登录后布局（教师页面）
  {
    path: '/teacher',
    component: TeacherLayout,
    children: [
      { path: '', name: 'TeacherHome', component: TeacherHome, meta: { title: '教师首页' } }
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
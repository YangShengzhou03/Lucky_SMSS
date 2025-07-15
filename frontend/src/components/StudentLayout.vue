<template>
  <div class="student-layout bg-gray-50 min-h-screen flex flex-col">
    <!-- 顶部导航栏 -->
    <header class="top-nav bg-gradient-to-r from-indigo-600 to-blue-600 text-white shadow-lg fixed w-full z-50">
      <div class="container mx-auto px-4 h-16 flex items-center justify-between">
        <!-- 左侧 Logo 和菜单按钮 -->
        <div class="flex items-center space-x-4">
          <button class="lg:hidden text-white p-2 rounded-full hover:bg-white/10 transition-colors duration-200"
            @click="toggleSidebar">
            <el-icon size="24">
              <Menu />
            </el-icon>
          </button>

          <div class="flex items-center space-x-2">
            <el-icon class="text-2xl text-yellow-400">
              <School />
            </el-icon>
            <span class="font-bold text-xl tracking-tight">Lucky SMS</span>
            <span class="text-xs bg-yellow-400 text-indigo-800 px-2 py-0.5 rounded-full font-medium ml-2">STUDENT</span>
          </div>
        </div>

        <!-- 右侧工具栏 -->
        <div class="flex items-center space-x-4">
          <!-- 搜索框 -->
          <div class="hidden md:flex items-center bg-white/10 rounded-full px-3 py-1.5">
            <el-icon class="text-gray-300 mr-2">
              <Search />
            </el-icon>
            <input type="text" placeholder="搜索课程、作业..."
              class="bg-transparent border-none text-white placeholder-gray-300 focus:outline-none w-48 lg:w-64" />
          </div>

          <!-- 通知中心 -->
          <el-dropdown trigger="click" placement="bottom-end">
            <template #default>
              <button class="text-white p-2 rounded-full hover:bg-white/10 transition-colors duration-200 relative">
                <el-icon class="text-xl">
                  <Bell />
                </el-icon>
                <span
                  class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-4 w-4 flex items-center justify-center">3</span>
              </button>
            </template>
            <template #dropdown>
              <div class="w-80 bg-white rounded-lg shadow-xl overflow-hidden">
                <div class="p-3 border-b border-gray-100 bg-gray-50 flex justify-between items-center">
                  <h3 class="font-semibold text-gray-800">通知中心</h3>
                  <span class="text-xs text-blue-600 cursor-pointer">全部标为已读</span>
                </div>

                <div class="max-h-96 overflow-y-auto">
                  <div class="p-4 border-b border-gray-100 hover:bg-gray-50 transition-colors duration-150">
                    <div class="flex items-start">
                      <el-avatar size="36" icon="Document" class="bg-blue-100 text-blue-600" />
                      <div class="ml-3">
                        <p class="text-sm font-medium text-gray-900">新作业发布</p>
                        <p class="text-xs text-gray-500 mt-1">数学分析第三章习题已发布，截止日期：下周五</p>
                        <p class="text-xs text-gray-400 mt-1">10分钟前</p>
                      </div>
                    </div>
                  </div>

                  <div class="p-4 border-b border-gray-100 hover:bg-gray-50 transition-colors duration-150">
                    <div class="flex items-start">
                      <el-avatar size="36" icon="DocumentChecked" class="bg-green-100 text-green-600" />
                      <div class="ml-3">
                        <p class="text-sm font-medium text-gray-900">考试成绩公布</p>
                        <p class="text-xs text-gray-500 mt-1">线性代数期中考试成绩已公布，点击查看详情</p>
                        <p class="text-xs text-gray-400 mt-1">昨天</p>
                      </div>
                    </div>
                  </div>

                  <div class="p-4 hover:bg-gray-50 transition-colors duration-150">
                    <div class="flex items-start">
                      <el-avatar size="36" icon="ChatDotRound" class="bg-purple-100 text-purple-600" />
                      <div class="ml-3">
                        <p class="text-sm font-medium text-gray-900">新消息</p>
                        <p class="text-xs text-gray-500 mt-1">李老师：请准备下周的实验报告</p>
                        <p class="text-xs text-gray-400 mt-1">2天前</p>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="p-3 border-t border-gray-100 bg-gray-50 text-center">
                  <a href="#" class="text-sm text-blue-600 hover:underline">查看全部通知</a>
                </div>
              </div>
            </template>
          </el-dropdown>

          <!-- 用户信息 -->
          <el-dropdown trigger="click" placement="bottom-end">
            <template #default>
              <div class="flex items-center space-x-2 cursor-pointer group">
                <el-avatar size="36" :src="userAvatar" class="ring-2 ring-white/20" />
                <div class="hidden md:block">
                  <p class="text-sm font-medium">{{ username }}</p>
                  <p class="text-xs text-white/70">学号: {{ studentId }}</p>
                </div>
                <el-icon class="text-xs group-hover:rotate-180 transition-transform duration-300">
                  <CaretDown />
                </el-icon>
              </div>
            </template>
            <template #dropdown>
              <el-dropdown-menu class="w-48">
                <el-dropdown-item @click="goToProfile">
                  <el-icon class="text-gray-500 mr-2">
                    <User />
                  </el-icon>
                  <span>个人资料</span>
                </el-dropdown-item>
                <el-dropdown-item @click="goToSettings">
                  <el-icon class="text-gray-500 mr-2">
                    <Setting />
                  </el-icon>
                  <span>设置</span>
                </el-dropdown-item>
                <el-dropdown-item @click="showHelp">
                  <el-icon class="text-gray-500 mr-2">
                    <HelpCircle />
                  </el-icon>
                  <span>帮助中心</span>
                </el-dropdown-item>
                <el-dropdown-item divided @click="logout" class="text-red-500">
                  <el-icon class="mr-2">
                    <Logout />
                  </el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <div class="flex flex-1 pt-16">
      <!-- 侧边栏 -->
      <aside
        class="sidebar w-64 bg-white shadow-lg fixed h-full pt-16 z-40 transform transition-transform duration-300 ease-in-out"
        :class="{ '-translate-x-full lg:translate-x-0': !sidebarOpen }">
        <div class="p-4 border-b border-gray-100">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-semibold text-gray-800">导航菜单</h2>
            <button class="lg:hidden text-gray-400 hover:text-gray-600" @click="sidebarOpen = false">
              <el-icon>
                <Close />
              </el-icon>
            </button>
          </div>
        </div>

        <nav class="py-4">
          <el-menu class="w-full" router mode="vertical" :default-active="activeMenu" background-color="#ffffff"
            text-color="#333333" active-text-color="#3b82f6">
            <el-menu-item index="/stulayout/home">
              <template #title>
                <el-icon class="text-lg">
                  <House />
                </el-icon>
                <span>首页</span>
              </template>
            </el-menu-item>

            <el-sub-menu index="student">
              <template #title>
                <el-icon class="text-lg">
                  <User />
                </el-icon>
                <span>个人中心</span>
              </template>
              <el-menu-item index="/stulayout/profile">个人资料</el-menu-item>
              <el-menu-item index="/stulayout/settings">设置</el-menu-item>
              <el-menu-item index="/stulayout/account">账户安全</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="course">
              <template #title>
                <el-icon class="text-lg">
                  <Notebook />
                </el-icon>
                <span>课程管理</span>
              </template>
              <el-menu-item index="/stulayout/courses">我的课程</el-menu-item>
              <el-menu-item index="/stulayout/schedule">课程表</el-menu-item>
              <el-menu-item index="/stulayout/assignments">作业</el-menu-item>
              <el-menu-item index="/stulayout/attendance">考勤记录</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="exam">
              <template #title>
                <el-icon class="text-lg">
                  <Document />
                </el-icon>
                <span>考试管理</span>
              </template>
              <el-menu-item index="/stulayout/exams">考试安排</el-menu-item>
              <el-menu-item index="/stulayout/results">成绩查询</el-menu-item>
              <el-menu-item index="/stulayout/transcript">成绩单</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="resource">
              <template #title>
                <el-icon class="text-lg">
                  <Collection />
                </el-icon>
                <span>学习资源</span>
              </template>
              <el-menu-item index="/stulayout/materials">学习资料</el-menu-item>
              <el-menu-item index="/stulayout/videos">教学视频</el-menu-item>
              <el-menu-item index="/stulayout/library">图书馆</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="communication">
              <template #title>
                <el-icon class="text-lg">
                  <ChatRound />
                </el-icon>
                <span>交流互动</span>
              </template>
              <el-menu-item index="/stulayout/messages">消息中心</el-menu-item>
              <el-menu-item index="/stulayout/forums">讨论区</el-menu-item>
              <el-menu-item index="/stulayout/feedback">意见反馈</el-menu-item>
            </el-sub-menu>
          </el-menu>
        </nav>

        <!-- 快捷工具 -->
        <div class="p-4 border-t border-gray-100 mt-4">
          <h3 class="text-sm font-medium text-gray-500 mb-3">快捷工具</h3>
          <div class="grid grid-cols-2 gap-2">
            <button class="bg-gray-50 hover:bg-gray-100 p-2 rounded-lg transition-colors duration-150 text-center">
              <el-icon class="text-blue-500 text-lg">
                <Calendar />
              </el-icon>
              <p class="text-xs mt-1">校历</p>
            </button>
            <button class="bg-gray-50 hover:bg-gray-100 p-2 rounded-lg transition-colors duration-150 text-center">
              <el-icon class="text-green-500 text-lg">
                <Bus />
              </el-icon>
              <p class="text-xs mt-1">校车查询</p>
            </button>
            <button class="bg-gray-50 hover:bg-gray-100 p-2 rounded-lg transition-colors duration-150 text-center">
              <el-icon class="text-purple-500 text-lg">
                <Restaurant />
              </el-icon>
              <p class="text-xs mt-1">食堂菜单</p>
            </button>
            <button class="bg-gray-50 hover:bg-gray-100 p-2 rounded-lg transition-colors duration-150 text-center">
              <el-icon class="text-orange-500 text-lg">
                <Phone />
              </el-icon>
              <p class="text-xs mt-1">联系我们</p>
            </button>
          </div>
        </div>
      </aside>

      <!-- 遮罩层（移动端侧边栏打开时显示） -->
      <div class="fixed inset-0 bg-black bg-opacity-50 z-30 lg:hidden" v-show="sidebarOpen"
        @click="sidebarOpen = false">
      </div>

      <!-- 主要内容区 -->
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  Menu, School, Search, Bell, CaretDown, User, House,
  Setting, HelpCircle, Logout, Notebook, Document,
  Collection, ChatRound, Calendar, Bus, Restaurant,
  Phone, Close
} from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage } from 'element-plus';

const route = useRoute();
const router = useRouter();

// 侧边栏状态
const sidebarOpen = ref(true);

// 用户信息
const username = ref(localStorage.getItem('username') || '张三');
const studentId = ref('20250001');
const userAvatar = ref('https://picsum.photos/id/1005/200/200'); // 示例头像

// 计算当前激活的菜单
const activeMenu = computed(() => {
  return route.path || '/stulayout/home';
});

// 移动端菜单切换
const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value;
};

// 导航方法
const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '退出登录', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    router.push('/login');
    ElMessage.success('已成功退出登录');
  }).catch(() => {
    ElMessage.info('已取消退出操作');
  });
};

const goToProfile = () => {
  router.push('/stulayout/profile');
  sidebarOpen.value = false; // 移动端导航后关闭侧边栏
};

const goToSettings = () => {
  router.push('/stulayout/settings');
  sidebarOpen.value = false;
};

const showHelp = () => {
  ElMessage.info('帮助中心正在建设中，敬请期待！');
};

// 页面加载时根据屏幕宽度初始化侧边栏状态
onMounted(() => {
  const handleResize = () => {
    sidebarOpen.value = window.innerWidth >= 1024;
  };

  handleResize();
  window.addEventListener('resize', handleResize);

  return () => {
    window.removeEventListener('resize', handleResize);
  };
});
</script>

<style>
/* 基础样式变量 */
:root {
  --primary-gradient: linear-gradient(90deg, #6366f1, #3b82f6);
  --sidebar-width: 260px;
}

/* 平滑滚动 */
html {
  scroll-behavior: smooth;
}

/* 侧边栏 */
.sidebar {
  position: fixed;
  top: 64px;
  left: 0;
  height: calc(100vh - 64px);
  overflow-y: auto;
}

/* 动画效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
</style>
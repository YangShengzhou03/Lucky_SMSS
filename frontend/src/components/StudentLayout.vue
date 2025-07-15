<template>
    <!-- 顶部导航栏 -->
    <header 
     
      <div class="con
        <!-- 左侧 Logo 和菜单按钮 -->

            <el-icon size="24">
              <Menu />
            </el-icon>
          </button>

        <div class="flex items-center space-x-4">
          <!-- 搜索框 -->
          <div class="hidden md:flex items-center bg-white/10 rounded-full px-3 py-1.5 transition-all duration-300 hover:bg-white/20 w-48 lg:w-64">
            <el-icon class="text-gray-300 mr-2">
              <Search />
          <!-- 通知中心 -->
              >
                <el-icon class="text-xl">
                  <Bell />
                </el-icon>
                <span ray-800">通知中心</h3>
                  <span class="text-xs text-blue-600 cursor-pointer hover:text-blue-800 transition-colors">全部标为已读</span>
                </div>

                <div class="max-h-96 overflow-y-auto">
                  <div class="p-4 border-b border-gray-100 hover:bg-gray-50 transition-colors duration-150 bg-blue-50/50">
                    <div class="flex items-start">
                      <el-avatar size="36" icon="Do
                    查看全部通知
                    <el-icon size="12" class="ml-1">
                      <ArrowRight />
                    </el-icon>
                  </a>
                </div>
              </div>
            </template>
          </el-dropdown>

          <!-- 用户信息 -->
          <el-dropdown trigger="click" placement="bottom-end">
            <template #default>
              <div class="flex items-center space-x-2 cursor-pointer group">
                <el-avatar 
            </template>
            <template #dropdown>
              <el-dropdown-menu class="w-48 shadow-lg border-none rounded-xl overflow-hidden">
                <el-dropdown-item @click="goToProfile" class="cursor-pointer">
                  <el-icon class="text-gray-500 mr-2">
                    <User />
                  </el-icon>
                  <span>个人资料</span>
                </el-dropdown-item>
                <el-dropdown-item @click="goToSettings" class="cursor-pointer">
                  <el-icon class="text-gray-500 mr-2">
                    <Setting />
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <div class="flex flex-1 pt-16">
      <!-- 侧边栏 -->
      <aside
        class="sidebar w-64 bg-white shadow-lg fixed h-full pt-16 z-40 transition-transform duration-300 ease-in-out"
        :class="{ 
          '-translate-x-full lg:translate-x-0': !sidebarOpen,
          'translate-x-0': sidebarOpen
          </div>
        </div>

        <nav class="py-4">
          <el-menu 
            class="w-full" 
            router 
            mode="vertical" 
            :default-active="activeMenu" 
            background-color="#ffffff"
            text-color="#333333" 
            active-text-color="#3b82f6"
            menu-trigger="click"ttings">设置</el-menu-item>
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
                <span>学习资源</span>
              </template>
              <el-menu-item index="/stulayout/materials">学习资料</el-menu-item>
              <el-menu-item index="/stulayout/videos">教学视频</el-menu-item>
              <el-menu-item index="/stulayout/library">图书馆</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="communication">
              <template #title>
                <el-icon class="text-lg">
                  <ChatRound />mt-4 bg-gradient-to-b from-white to-gray-50">
          <h3 class="text-sm font-medium text-gray-500 mb-3 flex items-center">
            <el-icon size="16" class="mr-1">
              <Tools />
            </el-icon>
            快捷工具
          </h3>
          <div class="grid grid-cols-2 gap-3">
            <button class="bg-white hover:bg-gray-50 p-3 rounded-xl shadow-sm hover:shadow transition-all duration-200 text-center border border-gray-100 group">
              <el-icon class="text-blue-500 text-lg mb-1 group-hover:scale-110 transition-transform">
                <Calendar />0 text-lg mb-1 group-hover:scale-110 transition-transform">
                <Restaurant />
              </el-icon>
              <p class="text-xs mt-1">食堂菜单</p>
            </button>
            <button class="bg-white hover:bg-gray-50 p-3 rounded-xl shadow-sm hover:shadow transition-all duration-200 text-center border border-gray-100 group">
              <el-icon class="text-orange-500 text-lg mb-1 group-hover:scale-110 transition-transform">
                <Phone />
              </el-icon>
              <p class="text-xs mt-1">联系我们</p>
            </button>
      <!-- 主要内容区 -->
      <main class="flex-1 lg:ml-64 transition-all duration-300 p-0">
        <!-- 面包屑导航 -->
        <div class="bg-white border-b border-gray-100 px-6 py-3 shadow-sm">
          <el-breadcrumb separator=">" class="text-sm">
            <el-breadcrumb-item :to="{ path: '/stulayout/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="(crumb, index) in breadcrumbs" :key="index" :to="crumb.path">
              {{ crumb.name }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <!-- 页面内容 -->
        <div class="p-6">
          <router-view />
        </div>
      </main>
    </div>

    <!-- 移动端底部快捷导航 -->
    <div class="lg:hidden fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 z-40 px-4 py-2">
      <div class="flex justify-around items-center">
        <button 
          class="flex flex-col items-center py-1 px-3 text-blue-600"
          @click="$router.push('/stulayout/home')"
        >
          <el-icon size="20">
            <House />
          </el-icon>
          <span class="text-xs mt-1">首页</span>
        </button>
        <button 
          class="flex flex-col items-center py-1 px-3 text-gray-500"
          @click="$router.push('/stulayout/courses')"
        >
          <el-icon size="20">
            <Notebook />
          </el-icon>
          <span class="text-xs mt-1">课程</span>
        </button>
        <button 
          class="flex flex-col items-center py-1 px-3 text-gray-500"
          @click="$router.push('/stulayout/messages')"
        >
          <el-icon size="20">
            <Message />
          </el-icon>
          <span class="text-xs mt-1">消息</span>
        </button>
        <button 
          class="flex flex-col items-center py-1 px-3 text-gray-500"
          @click="goToProfile"
        >
          <el-icon size="20">
            <User />
          </el-icon>
          <span class="text-xs mt-1">我的</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  Menu, School, Search, Bell, CaretDown, User, House,
  Setting, HelpCircle, Logout, Notebook, Document,
  Collection, ChatRound, Calendar, Bus, Restaurant,
  Phone, Close, ArrowRight, Tools, Message
} from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage, ElNotification } from 'element-plus';

const route = useRoute();
const router = useRouter();

// 侧边栏状态
const sidebarOpen = ref(true);
// 滚动状态
const isScrolled = ref(false);
// 未读通知数量
const unreadCount = ref(3);

// 用户信息
const username = ref(localStorage.getItem('username') || '张三');
const studentId = ref('20250001');
const userAvatar = ref('https://picsum.photos/id/1005/200/200');

// 计算当前激活的菜单
const activeMenu = computed(() => {
  return route.path || '/stulayout/home';
});

// 面包屑导航数据
const breadcrumbs = computed(() => {
  const matched = route.matched.slice(1);
  return matched.map(record => ({
    name: record.meta.title || record.name,
    path: record.path
  }));
});

// 移动端菜单切换
const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value;
  document.body.style.overflow = sidebarOpen.value ? 'hidden' : '';
};

// 导航方法
const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '退出登录', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
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
  if (window.innerWidth < 1024) {
    sidebarOpen.value = false;
    document.body.style.overflow = '';
  }
};

const goToSettings = () => {
  router.push('/stulayout/settings');
  if (window.innerWidth < 1024) {
    sidebarOpen.value = false;
    document.body.style.overflow = '';
  }
};

const showHelp = () => {
  ElNotification({
    title: '帮助中心',
    message: '帮助中心正在建设中，敬请期待！',
    duration: 3000,
    icon: HelpCircle,
    position: 'bottom-right'
  });
};

// 监听滚动事件，改变导航栏样式
const handleScroll = () => {
  isScrolled.value = window.scrollY > 10;
};

// 页面加载时根据屏幕宽度初始化侧边栏状态
onMounted(() => {
  const handleResize = () => {
    const isMobile = window.innerWidth < 1024;
    sidebarOpen.value = !isMobile;
    document.body.style.overflow = '';
  };

  handleResize();
  window.addEventListener('resize', handleResize);
  window.addEventListener('scroll', handleScroll);

  // 页面加载时检查是否有未读通知
  if (unreadCount.value > 0) {
    setTimeout(() => {
      ElNotification({
        title: '新通知',
        message: `您有 ${unreadCount.value} 条未读通知`,
        duration: 4000,
        icon: Bell,
        position: 'bottom-right'
      });
    }, 2000);
  }

  return () => {
    window.removeEventListener('resize', handleResize);
    window.removeEventListener('scroll', handleScroll);
  };
});

// 监听路由变化，移动端自动关闭侧边栏
watch(route, () => {
  if (window.innerWidth < 1024) {
    sidebarOpen.value = false;
    document.body.style.overflow = '';
  }
});
</script>

<style scoped>
/* 基础样式 */
body {
  font-family: 'Inter', sans-serif;
  margin: 0;
  padding: 0;
}

/* 平滑滚动 */
html {
  scroll-behavior: smooth;
}

/* 顶部导航栏 */
.top-nav {
  top: 0;
  left: 0;
  right: 0;
}

/* 侧边栏 */
.sidebar {
  top: 64px;
  left: 0;
  height: calc(100vh - 64px);
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(156, 163, 175, 0.2) transparent;
}

.sidebar::-webkit-scrollbar {
  width: 4px;
}

.sidebar::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar::-webkit-scrollbar-thumb {
  background-color: rgba(156, 163, 175, 0.2);
  border-radius: 2px;
}

/* 下拉菜单动画 */
@keyframes dropdownFadeIn {
  from {
    opacity: 0;
    transform: translateY(10px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 通知下拉动画 */
.enterNotification {
  animation: dropdownFadeIn 0.25s ease-out forwards;
}

.leaveNotification {
  animation: dropdownFadeIn 0.2s reverse forwards;
}

/* 菜单激活项样式 */
::v-deep .el-menu-item.is-active {
  background-color: #eff6ff !important;
  border-right: 3px solid #3b82f6 !important;
}

/* 子菜单标题悬停样式 */
::v-deep .el-sub-menu__title:hover {
  background-color: #f9fafb !important;
}

/* 面包屑样式优化 */
::v-deep .el-breadcrumb__item:last-child .el-breadcrumb__inner {
  color: #64748b !important;
  font-weight: 500;
}

/* 自定义滚动条 */
.scrollbar-thin {
  scrollbar-width: thin;
}

.scrollbar-thumb-gray-200::-webkit-scrollbar-thumb {
  background-color: rgba(209, 213, 219, 0.5);
}

.scrollbar-thumb-gray-300::-webkit-scrollbar-thumb {
  background-color: rgba(156, 163, 175, 0.5);
}

.scrollbar-track-transparent::-webkit-scrollbar-track {
  background-color: transparent;
}

/* 响应式布局 */
@media (max-width: 1023px) {
  .lg\:hidden {
    display: none;
  }
  
  .lg\:ml-64 {
    margin-left: 0;
  }
}
</style>
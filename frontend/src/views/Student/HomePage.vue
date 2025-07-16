<template>
  <div class="home-page">
    <!-- 页面标题和欢迎信息 -->
    <div class="page-header">
      <div class="welcome-message">
        <h1 class="page-title">欢迎回来，<span class="user-name">张三</span></h1>
        <p class="page-subtitle">这是您的个性化学习中心</p>
      </div>
      <div class="date-time-widget">
        <div class="current-date">{{ currentDate }}</div>
        <div class="current-time">{{ currentTime }}</div>
      </div>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-cards grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <!-- GPA统计卡片 -->
      <div class="stat-card bg-white dark:bg-gray-800 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 p-6 border border-gray-100 dark:border-gray-700">
        <div class="card-header flex justify-between items-start mb-4">
          <div>
            <h3 class="card-title text-gray-500 dark:text-gray-400 font-medium">GPA</h3>
            <p class="card-subtitle text-gray-400 dark:text-gray-500 text-sm">本学期平均绩点</p>
          </div>
          <div class="stat-icon bg-primary/10 text-primary p-2 rounded-lg">
            <el-icon><Rank /></el-icon>
          </div>
        </div>
        <div class="card-body">
          <div class="gpa-value text-4xl font-bold text-gray-800 dark:text-white">3.82</div>
          <div class="gpa-trend flex items-center mt-2 text-success">
            <el-icon><ArrowUp /></el-icon>
            <span class="ml-1 text-sm font-medium">0.21 提升</span>
          </div>
        </div>
        <div class="card-footer mt-4 pt-4 border-t border-gray-100 dark:border-gray-700">
          <div class="progress-bar h-2 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
            <div class="progress-fill h-full bg-primary rounded-full" style="width: 85%"></div>
          </div>
          <div class="flex justify-between mt-2 text-xs text-gray-500 dark:text-gray-400">
            <span>班级排名: 前 15%</span>
            <span>学分完成: 24/30</span>
          </div>
        </div>
      </div>

      <!-- 课程统计卡片 -->
      <div class="stat-card bg-white dark:bg-gray-800 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 p-6 border border-gray-100 dark:border-gray-700">
        <div class="card-header flex justify-between items-start mb-4">
          <div>
            <h3 class="card-title text-gray-500 dark:text-gray-400 font-medium">进行中课程</h3>
            <p class="card-subtitle text-gray-400 dark:text-gray-500 text-sm">本学期课程进度</p>
          </div>
          <div class="stat-icon bg-info/10 text-info p-2 rounded-lg">
            <el-icon><Notebook /></el-icon>
          </div>
        </div>
        <div class="card-body">
          <div class="course-count text-4xl font-bold text-gray-800 dark:text-white">8/12</div>
          <div class="course-progress flex items-center mt-2">
            <div class="flex-1 h-2 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
              <div class="h-full bg-info rounded-full" style="width: 67%"></div>
            </div>
            <span class="ml-2 text-sm font-medium text-gray-600 dark:text-gray-300">67%</span>
          </div>
        </div>
        <div class="card-footer mt-4 pt-4 border-t border-gray-100 dark:border-gray-700">
          <div class="flex justify-between text-sm">
            <span class="text-gray-600 dark:text-gray-300">
              <el-icon class="text-success mr-1"><CircleCheck /></el-icon>
              通过: 5门
            </span>
            <span class="text-gray-600 dark:text-gray-300">
              <el-icon class="text-warning mr-1"><CircleMinus /></el-icon>
              进行中: 3门
            </span>
          </div>
        </div>
      </div>

      <!-- 作业统计卡片 -->
      <div class="stat-card bg-white dark:bg-gray-800 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 p-6 border border-gray-100 dark:border-gray-700">
        <div class="card-header flex justify-between items-start mb-4">
          <div>
            <h3 class="card-title text-gray-500 dark:text-gray-400 font-medium">作业</h3>
            <p class="card-subtitle text-gray-400 dark:text-gray-500 text-sm">待完成作业</p>
          </div>
          <div class="stat-icon bg-warning/10 text-warning p-2 rounded-lg">
            <el-icon><EditPen /></el-icon>
          </div>
        </div>
        <div class="card-body">
          <div class="task-count text-4xl font-bold text-gray-800 dark:text-white">3</div>
          <div class="task-deadline text-warning mt-2 text-sm">
            <el-icon><Time /></el-icon>
            <span class="ml-1">最近截止: 明天</span>
          </div>
        </div>
        <div class="card-footer mt-4 pt-4 border-t border-gray-100 dark:border-gray-700">
          <div class="flex justify-between text-sm">
            <span class="text-gray-600 dark:text-gray-300">
              <el-icon class="text-success mr-1"><CircleCheck /></el-icon>
              已完成: 12/15
            </span>
            <span class="text-danger dark:text-gray-300">
              <el-icon class="text-danger mr-1"><CircleClose /></el-icon>
              逾期: 1
            </span>
          </div>
        </div>
      </div>

      <!-- 考勤统计卡片 -->
      <div class="stat-card bg-white dark:bg-gray-800 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 p-6 border border-gray-100 dark:border-gray-700">
        <div class="card-header flex justify-between items-start mb-4">
          <div>
            <h3 class="card-title text-gray-500 dark:text-gray-400 font-medium">考勤</h3>
            <p class="card-subtitle text-gray-400 dark:text-gray-500 text-sm">本学期出勤率</p>
          </div>
          <div class="stat-icon bg-success/10 text-success p-2 rounded-lg">
            <el-icon><Calendar /></el-icon>
          </div>
        </div>
        <div class="card-body">
          <div class="attendance-rate text-4xl font-bold text-gray-800 dark:text-white">95%</div>
          <div class="attendance-status flex items-center mt-2 text-success">
            <el-icon><CircleCheck /></el-icon>
            <span class="ml-1 text-sm font-medium">优秀</span>
          </div>
        </div>
        <div class="card-footer mt-4 pt-4 border-t border-gray-100 dark:border-gray-700">
          <div class="flex justify-between text-sm">
            <span class="text-gray-600 dark:text-gray-300">
              <el-icon class="text-success mr-1"><Check /></el-icon>
              出勤: 38
            </span>
            <span class="text-gray-600 dark:text-gray-300">
              <el-icon class="text-danger mr-1"><Close /></el-icon>
              缺勤: 2
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- 左侧区域 -->
      <div class="lg:col-span-2 space-y-8">
        <!-- 课程进度图表 -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 p-6 border border-gray-100 dark:border-gray-700">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-xl font-bold text-gray-800 dark:text-white">课程进度</h2>
            <div class="flex space-x-2">
              <el-button size="small" type="text" class="text-primary" @click="toggleChartView">
                <el-icon><Refresh /></el-icon>
                <span>刷新</span>
              </el-button>
              <el-dropdown trigger="click">
                <el-button size="small" type="text" class="text-gray-600 dark:text-gray-300">
                  本学期 <el-icon><ArrowDown /></el-icon>
                </el-button>
                <template #content>
                  <el-dropdown-menu>
                    <el-dropdown-item>本学期</el-dropdown-item>
                    <el-dropdown-item>上学期</el-dropdown-item>
                    <el-dropdown-item>全年</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
          <div class="h-[300px]">
            <canvas id="courseProgressChart"></canvas>
          </div>
        </div>

        <!-- 最近作业 -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 p-6 border border-gray-100 dark:border-gray-700">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-xl font-bold text-gray-800 dark:text-white">最近作业</h2>
            <el-button size="small" type="text" class="text-primary" @click="viewAllAssignments">
              查看全部 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          <el-table
            :data="assignments"
            stripe
            border
            fit
            highlight-current-row
            class="mb-4"
          >
            <el-table-column prop="course" label="课程" width="120"></el-table-column>
            <el-table-column prop="title" label="作业名称"></el-table-column>
            <el-table-column prop="deadline" label="截止日期" width="140"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === '已完成' ? 'success' : scope.row.status === '进行中' ? 'info' : 'warning'">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button size="mini" type="primary" @click="viewAssignment(scope.row)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- 右侧区域 -->
      <div class="space-y-8">
        <!-- 即将到来的课程 -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 p-6 border border-gray-100 dark:border-gray-700">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-xl font-bold text-gray-800 dark:text-white">今日课程</h2>
            <el-button size="small" type="text" class="text-primary" @click="viewSchedule">
              查看课表 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          <div class="space-y-4">
            <div class="upcoming-class bg-gray-50 dark:bg-gray-700/50 rounded-lg p-4 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors cursor-pointer">
              <div class="flex justify-between items-start">
                <div>
                  <h3 class="font-medium text-gray-800 dark:text-white">高等数学</h3>
                  <p class="text-sm text-gray-500 dark:text-gray-400">陈教授 | 理科楼 302</p>
                </div>
                <el-tag type="info">9:00-10:30</el-tag>
              </div>
              <div class="mt-2 flex items-center text-xs text-gray-500 dark:text-gray-400">
                <el-icon><Location /></el-icon>
                <span class="ml-1">理科楼 302 教室</span>
              </div>
            </div>
            
            <div class="upcoming-class bg-gray-50 dark:bg-gray-700/50 rounded-lg p-4 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors cursor-pointer">
              <div class="flex justify-between items-start">
                <div>
                  <h3 class="font-medium text-gray-800 dark:text-white">大学英语</h3>
                  <p class="text-sm text-gray-500 dark:text-gray-400">李老师 | 文科楼 201</p>
                </div>
                <el-tag type="info">13:30-15:00</el-tag>
              </div>
              <div class="mt-2 flex items-center text-xs text-gray-500 dark:text-gray-400">
                <el-icon><Location /></el-icon>
                <span class="ml-1">文科楼 201 教室</span>
              </div>
            </div>
            
            <div class="upcoming-class bg-gray-50 dark:bg-gray-700/50 rounded-lg p-4 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors cursor-pointer">
              <div class="flex justify-between items-start">
                <div>
                  <h3 class="font-medium text-gray-800 dark:text-white">数据结构</h3>
                  <p class="text-sm text-gray-500 dark:text-gray-400">王教授 | 计算机楼 405</p>
                </div>
                <el-tag type="info">16:00-17:30</el-tag>
              </div>
              <div class="mt-2 flex items-center text-xs text-gray-500 dark:text-gray-400">
                <el-icon><Location /></el-icon>
                <span class="ml-1">计算机楼 405 实验室</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 考试安排 -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 p-6 border border-gray-100 dark:border-gray-700">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-xl font-bold text-gray-800 dark:text-white">近期考试</h2>
            <el-button size="small" type="text" class="text-primary" @click="viewExams">
              查看全部 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          <div class="space-y-4">
            <div class="exam-item flex p-3 rounded-lg border border-gray-100 dark:border-gray-700 hover:border-primary/50 transition-colors">
              <div class="date-block bg-primary/10 text-primary rounded-lg p-2 text-center mr-3">
                <div class="day text-lg font-bold">15</div>
                <div class="month text-xs">6月</div>
              </div>
              <div class="flex-1">
                <h3 class="font-medium text-gray-800 dark:text-white">高等数学期中考</h3>
                <div class="flex items-center mt-1 text-sm text-gray-500 dark:text-gray-400">
                  <el-icon><Time /></el-icon>
                  <span class="ml-1">06-15 09:00-11:00</span>
                </div>
                <div class="flex items-center mt-1 text-sm text-gray-500 dark:text-gray-400">
                  <el-icon><Location /></el-icon>
                  <span class="ml-1">理科楼 101</span>
                </div>
              </div>
            </div>
            
            <div class="exam-item flex p-3 rounded-lg border border-gray-100 dark:border-gray-700 hover:border-primary/50 transition-colors">
              <div class="date-block bg-primary/10 text-primary rounded-lg p-2 text-center mr-3">
                <div class="day text-lg font-bold">22</div>
                <div class="month text-xs">6月</div>
              </div>
              <div class="flex-1">
                <h3 class="font-medium text-gray-800 dark:text-white">大学英语期中考</h3>
                <div class="flex items-center mt-1 text-sm text-gray-500 dark:text-gray-400">
                  <el-icon><Time /></el-icon>
                  <span class="ml-1">06-22 14:00-16:00</span>
                </div>
                <div class="flex items-center mt-1 text-sm text-gray-500 dark:text-gray-400">
                  <el-icon><Location /></el-icon>
                  <span class="ml-1">文科楼 303</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 学习资源推荐 -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 p-6 border border-gray-100 dark:border-gray-700">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-xl font-bold text-gray-800 dark:text-white">学习资源推荐</h2>
            <el-button size="small" type="text" class="text-primary" @click="viewResources">
              更多资源 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          <div class="space-y-4">
            <div class="resource-item flex items-center p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors cursor-pointer">
              <div class="resource-icon bg-blue-100 text-blue-600 dark:bg-blue-900/50 dark:text-blue-300 p-2 rounded-lg mr-3">
                <el-icon><Document /></el-icon>
              </div>
              <div class="flex-1">
                <h3 class="font-medium text-gray-800 dark:text-white">《数据结构与算法分析》</h3>
                <p class="text-sm text-gray-500 dark:text-gray-400">适合初学者的经典教材</p>
              </div>
              <el-icon class="text-gray-400 dark:text-gray-500"><ArrowRight /></el-icon>
            </div>
            
            <div class="resource-item flex items-center p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors cursor-pointer">
              <div class="resource-icon bg-green-100 text-green-600 dark:bg-green-900/50 dark:text-green-300 p-2 rounded-lg mr-3">
                <el-icon><VideoCamera /></el-icon>
              </div>
              <div class="flex-1">
                <h3 class="font-medium text-gray-800 dark:text-white">微积分教学视频</h3>
                <p class="text-sm text-gray-500 dark:text-gray-400">陈教授主讲 | 12讲</p>
              </div>
              <el-icon class="text-gray-400 dark:text-gray-500"><ArrowRight /></el-icon>
            </div>
            
            <div class="resource-item flex items-center p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors cursor-pointer">
              <div class="resource-icon bg-purple-100 text-purple-600 dark:bg-purple-900/50 dark:text-purple-300 p-2 rounded-lg mr-3">
                <el-icon><Reading /></el-icon>
              </div>
              <div class="flex-1">
                <h3 class="font-medium text-gray-800 dark:text-white">英语写作技巧指南</h3>
                <p class="text-sm text-gray-500 dark:text-gray-400">提升学术写作能力</p>
              </div>
              <el-icon class="text-gray-400 dark:text-gray-500"><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Calendar, Document, VideoCamera, Reading, 
  ArrowRight, ArrowUp, Time, Location, 
  CircleCheck, CircleMinus, CircleClose,
  Check, Close, Refresh, Rank, Notebook, 
  EditPen } from '@element-plus/icons-vue'
import Chart from 'chart.js/auto'

const router = useRouter()

// 页面状态
const assignments = ref([
  { id: 1, course: '高等数学', title: '第三章课后习题', deadline: '2025-06-18', status: '进行中' },
  { id: 2, course: '大学英语', title: '阅读理解作业', deadline: '2025-06-19', status: '进行中' },
  { id: 3, course: '数据结构', title: '链表实现', deadline: '2025-06-20', status: '已完成' },
  { id: 4, course: '计算机网络', title: 'TCP/IP协议分析', deadline: '2025-06-17', status: '已完成' },
  { id: 5, course: '操作系统', title: '进程调度实验', deadline: '2025-06-22', status: '待提交' }
])

// 当前日期时间
const currentDate = ref('')
const currentTime = ref('')

// 更新日期时间
const updateDateTime = () => {
  const now = new Date()
  const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
  currentDate.value = now.toLocaleDateString('zh-CN', options)
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

// 课程进度图表
let courseProgressChart = null

const initChart = () => {
  const ctx = document.getElementById('courseProgressChart')
  if (courseProgressChart) {
    courseProgressChart.destroy()
  }
  
  courseProgressChart = new Chart(ctx, {
    type: 'radar',
    data: {
      labels: ['高等数学', '大学英语', '数据结构', '计算机网络', '操作系统', '线性代数'],
      datasets: [{
        label: '课程进度',
        data: [85, 75, 90, 65, 70, 80],
        backgroundColor: 'rgba(67, 97, 238, 0.2)',
        borderColor: 'rgba(67, 97, 238, 1)',
        pointBackgroundColor: 'rgba(67, 97, 238, 1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(67, 97, 238, 1)'
      }]
    },
    options: {
      scales: {
        r: {
          angleLines: {
            display: true,
            color: 'rgba(0, 0, 0, 0.1)'
          },
          suggestedMin: 0,
          suggestedMax: 100,
          ticks: {
            stepSize: 20,
            backdropColor: 'transparent'
          }
        }
      },
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          callbacks: {
            label: function(context) {
              return context.dataset.label + ': ' + context.raw + '%';
            }
          }
        }
      },
      elements: {
        line: {
          tension: 0.1
        }
      },
      responsive: true,
      maintainAspectRatio: false,
      animation: {
        duration: 2000,
        easing: 'easeOutQuart'
      }
    }
  })
}

// 页面方法
const toggleChartView = () => {
  // 模拟刷新图表
  if (courseProgressChart) {
    courseProgressChart.data.datasets[0].data = courseProgressChart.data.datasets[0].data.map(
      val => Math.max(0, Math.min(100, val + Math.floor(Math.random() * 11) - 5))
    )
    courseProgressChart.update()
  }
}

const viewAllAssignments = () => {
  router.push({ name: 'Assignments' })
}

const viewAssignment = (assignment) => {
  router.push({ name: 'AssignmentDetail', params: { id: assignment.id } })
}

const viewSchedule = () => {
  router.push({ name: 'Schedule' })
}

const viewExams = () => {
  router.push({ name: 'Exams' })
}

const viewResources = () => {
  router.push({ name: 'Resources' })
}

// 生命周期钩子
onMounted(() => {
  // 初始化日期时间
  updateDateTime()
  setInterval(updateDateTime, 1000)
  
  // 初始化图表
  initChart()
  
  // 为卡片添加交互动画
  const cards = document.querySelectorAll('.stat-card, .upcoming-class, .exam-item, .resource-item')
  cards.forEach(card => {
    card.addEventListener('mouseenter', () => {
      card.style.transform = 'translateY(-5px)'
    })
    card.addEventListener('mouseleave', () => {
      card.style.transform = 'translateY(0)'
    })
  })
})
</script>

<style scoped>
.home-page {
  height: 100%;
  width: 100%;
  background: transparent;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.welcome-message {
  h1 {
    font-size: 1.75rem;
    font-weight: 700;
    color: var(--gray-800);
    margin-bottom: 0.5rem;
  }
  
  .user-name {
    color: var(--primary-color);
  }
  
  p {
    color: var(--gray-500);
  }
}

.date-time-widget {
  text-align: right;
  
  .current-date {
    font-size: 1rem;
    color: var(--gray-600);
  }
  
  .current-time {
    font-size: 1.25rem;
    font-weight: 500;
    color: var(--primary-color);
  }
}

.stat-card {
  transition: all 0.3s ease;
  
  .card-title {
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }
  
  .stat-icon {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .progress-bar, .course-progress > div {
    position: relative;
  }
  
  .progress-fill {
    transition: width 1s ease-in-out;
  }
}

.upcoming-class, .exam-item, .resource-item {
  transition: all 0.2s ease;
}

.exam-item .date-block {
  min-width: 50px;
}

.resource-item .resource-icon {
  min-width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-container {
  position: relative;
  height: 300px;
}

/* 深色模式适配 */
.dark-mode {
  .welcome-message h1 {
    color: var(--gray-200);
  }
  
  .welcome-message p, .date-time-widget .current-date {
    color: var(--gray-400);
  }
  
  .date-time-widget .current-time {
    color: var(--primary-light);
  }
  
  .stat-card .card-title, .stat-card .card-subtitle {
    color: var(--gray-400);
  }
  
  .stat-card .gpa-value, .stat-card .course-count, .stat-card .task-count, .stat-card .attendance-rate {
    color: var(--gray-200);
  }
  
  .upcoming-class h3, .exam-item h3, .resource-item h3 {
    color: var(--gray-200);
  }
  
  .upcoming-class p, .exam-item div, .resource-item p {
    color: var(--gray-400);
  }
}
</style>  
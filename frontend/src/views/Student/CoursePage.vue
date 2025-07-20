<template>
  <div class="course-selection-container" @mousemove="handleMouseMove">
    <!-- 顶部统计卡片 -->
    <div class="modern-card">
      <div class="panel-header">
        <h3>
          <el-icon>
            <DataAnalysis />
          </el-icon>
          课程概览
        </h3>
        <div class="semester-selector">
          <el-select v-model="currentSemester" placeholder="选择学期" size="small" class="modern-select">
            <el-option v-for="item in semesters" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
      </div>

      <div class="stats-grid">
        <div class="stat-card" v-for="(stat, index) in stats" :key="index" :style="{ '--card-color': stat.color }"
          @mouseenter="hoverStat(index)" @mouseleave="unhoverStat(index)">
          <div class="stat-icon">
            <div class="icon-bg"></div>
            <el-icon :size="24">
              <component :is="stat.icon" />
            </el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
          <div class="stat-trend" v-if="stat.trend">
            <el-icon :color="stat.trend > 0 ? '#f56c6c' : '#67c23a'">
              <CaretTop v-if="stat.trend > 0" />
              <CaretBottom v-else />
            </el-icon>
            <span :style="{ color: stat.trend > 0 ? '#f56c6c' : '#67c23a' }">
              {{ Math.abs(stat.trend) }}%
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 左侧：选课指南和课程列表 -->
      <div class="left-panel">
        <!-- 选课指南风琴面板 -->
        <div class="selection-guide modern-card">
          <el-collapse v-model="activeGuide" accordion>
            <el-collapse-item title="选课指南" name="guide">
              <div class="guide-content">
                <h3>选课流程</h3>
                <ol>
                  <li>在课程列表中浏览或搜索您感兴趣的课程</li>
                  <li>点击"选课"按钮选择课程，系统会自动检查时间冲突</li>
                  <li>已选课程会显示在已选列表中</li>
                  <li>通过课程表预览功能查看您的时间安排</li>
                  <li>确认无误后点击"确认选课"完成选课</li>
                </ol>

                <h3>注意事项</h3>
                <ul>
                  <li>每学期最多可选修{{ maxCredits }}学分的课程</li>
                  <li>所选课程时间不能冲突</li>
                  <li>选课截止日期: {{ selectionDeadline }}</li>
                  <li>选课截止后将无法修改选课</li>
                </ul>
              </div>
            </el-collapse-item>

            <el-collapse-item title="常见问题" name="faq">
              <div class="guide-content">
                <div class="faq-item">
                  <h4>Q: 如何解决选课时间冲突问题？</h4>
                  <p>A: 可以调整课程选择，避免选择同一时间段的课程，或使用课程表预览功能查看时间安排。</p>
                </div>

                <div class="faq-item">
                  <h4>Q: 选课成功后可以退选吗？</h4>
                  <p>A: 可以，在选课截止日期前，您可以在已选课程列表中退选课程。</p>
                </div>

                <div class="faq-item">
                  <h4>Q: 课程容量已满怎么办？</h4>
                  <p>A: 您可以选择其他课程，或定期查看课程容量，部分学生会在截止前退选。</p>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>

        <!-- 课程列表 -->
        <div class="course-list modern-card">
          <div class="panel-header">
            <h3>
              <el-icon>
                <Collection />
              </el-icon>
              可选课程
            </h3>
            <div class="search-filter">
              <el-input v-model="searchQuery" placeholder="搜索课程" size="small" clearable class="modern-input">
                <template #suffix>
                  <el-icon>
                    <Search />
                  </el-icon>
                </template>
              </el-input>
              <el-select v-model="filterCategory" placeholder="筛选分类" size="small" class="modern-select">
                <el-option v-for="category in categories" :key="category.value" :label="category.label"
                  :value="category.value" />
              </el-select>
            </div>
          </div>

          <div class="courses-container">
            <el-card v-for="course in filteredCourses" :key="course.id" class="course-card" shadow="hover">
              <div class="course-header">
                <h4>{{ course.name }}</h4>
                <span class="course-code">{{ course.code }}</span>
              </div>
              <div class="course-info">
                <div class="info-item">
                  <el-icon>
                    <User />
                  </el-icon>
                  <span>{{ course.teacher }}</span>
                </div>
                <div class="info-item">
                  <el-icon>
                    <Clock />
                  </el-icon>
                  <span>{{ course.time }}</span>
                </div>
                <div class="info-item">
                  <el-icon>
                    <Location />
                  </el-icon>
                  <span>{{ course.location }}</span>
                </div>
                <div class="info-item">
                  <el-icon>
                    <CreditCard />
                  </el-icon>
                  <span>{{ course.credits }}学分</span>
                </div>
                <div class="info-item capacity">
                  <el-progress :percentage="course.capacityUsed" :color="capacityColor(course.capacityUsed)"
                    :stroke-width="10" text-inside status="success" />
                  <span>{{ course.capacityUsed }}%</span>
                </div>
              </div>
              <div class="course-footer">
                <el-button @click="selectCourse(course)" :disabled="isCourseSelected(course.id)" type="primary"
                  size="small">
                  {{ isCourseSelected(course.id) ? '已选' : '选课' }}
                </el-button>
              </div>
            </el-card>

            <!-- 暂无课程提示 -->
            <div v-if="!filteredCourses.length && !searchQuery" class="no-courses">
              <el-empty description="暂无可选课程" />
            </div>

            <!-- 搜索无结果提示 -->
            <div v-if="!filteredCourses.length && searchQuery" class="no-courses">
              <el-empty image="search" description="没有找到匹配的课程" />
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
              :current-page="currentPage" :page-sizes="[5, 10, 15]" :page-size="pageSize"
              layout="total, sizes, prev, pager, next, jumper" :total="allCourses.length">
            </el-pagination>
          </div>
        </div>
      </div>

      <!-- 右侧：已选课程和课程表 -->
      <div class="right-panel">
        <!-- 已选课程 -->
        <div class="selected-courses modern-card">
          <div class="panel-header">
            <h3>
              <el-icon>
                <Check />
              </el-icon>
              已选课程
              <span class="credit-summary">({{ selectedCredits }}/{{ maxCredits }}学分)</span>
            </h3>
          </div>

          <div class="selected-courses-list">
            <el-card v-for="course in selectedCourses" :key="course.id" class="selected-course-card" shadow="hover">
              <div class="course-header">
                <h4>{{ course.name }}</h4>
                <span class="course-code">{{ course.code }}</span>
              </div>
              <div class="course-info">
                <div class="info-item">
                  <el-icon>
                    <User />
                  </el-icon>
                  <span>{{ course.teacher }}</span>
                </div>
                <div class="info-item">
                  <el-icon>
                    <Clock />
                  </el-icon>
                  <span>{{ course.time }}</span>
                </div>
                <div class="info-item">
                  <el-icon>
                    <Location />
                  </el-icon>
                  <span>{{ course.location }}</span>
                </div>
                <div class="info-item">
                  <el-icon>
                    <CreditCard />
                  </el-icon>
                  <span>{{ course.credits }}学分</span>
                </div>
              </div>
              <div class="course-footer">
                <el-button @click="deselectCourse(course.id)" type="danger" size="small">
                  退选
                </el-button>
              </div>
            </el-card>

            <!-- 暂无已选课程提示 -->
            <div v-if="!selectedCourses.length" class="no-selected-courses">
              <el-empty description="您还没有选择任何课程" />
            </div>
          </div>

          <div class="selection-actions" v-if="selectedCourses.length">
            <el-button @click="confirmSelection" type="primary" size="small" :loading="selectionLoading">
              确认选课
            </el-button>
          </div>
        </div>

        <!-- 课程表预览 -->
        <div class="timetable-preview modern-card">
          <div class="panel-header">
            <h3>
              <el-icon>
                <Calendar />
              </el-icon>
              课程表预览
            </h3>
          </div>

          <div class="timetable-container">
            <!-- 课程表头部 -->
            <div class="timetable-header">
              <div class="time-slot-header"></div>
              <div class="day-header" v-for="day in daysOfWeek" :key="day">{{ day }}</div>
            </div>

            <!-- 课程表内容 -->
            <div class="timetable-grid">
              <!-- 时间段行 -->
              <div class="time-row" v-for="(timeSlot, timeIndex) in timeSlots" :key="timeIndex">
                <div class="time-label">{{ timeSlot }}</div>
                <!-- 每天的时间段 -->
                <div class="day-column" v-for="(day, dayIndex) in daysOfWeek" :key="dayIndex">
                  <!-- 课程卡片 -->
                  <div class="course-block" v-for="course in selectedCourses"
                    :key="course.id + '-' + dayIndex + '-' + timeIndex">
                    <div class="course-block-inner"
                      :style="{ backgroundColor: courseColors[course.id % courseColors.length] }">
                      <div class="course-name">{{ course.name }}</div>
                      <div class="course-location">{{ course.location }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 暂无课程提示 -->
            <div v-if="!selectedCourses.length" class="no-timetable">
              <el-empty description="暂无课程安排" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  DataAnalysis,
  Collection,
  Check,
  CaretTop,
  CaretBottom,
  Calendar,
  CreditCard,
  User,
  Clock,
  Location,
  Search
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 学期数据
const currentSemester = ref('2023-2024-2')
const semesters = ref([
  { value: '2023-2024-2', label: '2023-2024学年第二学期' },
  { value: '2023-2024-1', label: '2023-2024学年第一学期' }
])

// 选课指南
const activeGuide = ref(['guide'])

// 悬停状态
const hoveredStat = ref(null)

// 配置参数
const maxCredits = 25
const selectionDeadline = '2023年7月30日'

// 搜索和筛选
const searchQuery = ref('')
const filterCategory = ref('all')
const categories = ref([
  { value: 'all', label: '全部' },
  { value: 'compulsory', label: '必修课' },
  { value: 'elective', label: '选修课' },
  { value: 'professional', label: '专业课' },
  { value: 'general', label: '通识课' }
])

// 分页
const currentPage = ref(1)
const pageSize = ref(5)

// 统计卡片数据
const stats = computed(() => [
  {
    value: allCourses.value.length,
    label: '可选课程',
    icon: Collection,
    color: '#6366f1',
    trend: 5.2
  },
  {
    value: selectedCourses.value.length,
    label: '已选课程',
    icon: Check,
    color: '#10b981',
    trend: selectedCourses.value.length > 0 ? 12.8 : 0
  },
  {
    value: `${selectedCredits.value}/${maxCredits}`,
    label: '已选学分',
    icon: CreditCard,
    color: '#f59e0b',
    trend: selectedCourses.value.length > 0 ? -3.5 : 0
  },
  {
    value: '7天',
    label: '选课截止',
    icon: Calendar,
    color: '#ef4444',
    trend: 2.1
  }
])

// 课程数据
const allCourses = ref([
  {
    id: 1,
    name: '数据结构与算法',
    code: 'CS101',
    teacher: '李教授',
    time: '周一 1-2节',
    location: '科技楼A302',
    credits: 4,
    capacityUsed: 75,
    category: 'compulsory',
    schedule: [
      { day: 1, timeSlot: 0 } // 周一第1时间段
    ]
  },
  {
    id: 2,
    name: '计算机网络',
    code: 'CS102',
    teacher: '王教授',
    time: '周二 3-4节',
    location: '科技楼B401',
    credits: 3,
    capacityUsed: 60,
    category: 'compulsory',
    schedule: [
      { day: 2, timeSlot: 1 } // 周二第2时间段
    ]
  },
  {
    id: 3,
    name: '人工智能导论',
    code: 'CS103',
    teacher: '张教授',
    time: '周三 5-6节',
    location: '科技楼C503',
    credits: 4,
    capacityUsed: 85,
    category: 'elective',
    schedule: [
      { day: 3, timeSlot: 2 } // 周三第3时间段
    ]
  },
  {
    id: 4,
    name: '操作系统',
    code: 'CS104',
    teacher: '刘教授',
    time: '周四 1-2节',
    location: '科技楼A301',
    credits: 4,
    capacityUsed: 90,
    category: 'compulsory',
    schedule: [
      { day: 4, timeSlot: 0 } // 周四第1时间段
    ]
  },
  {
    id: 5,
    name: '数据库系统',
    code: 'CS105',
    teacher: '陈教授',
    time: '周五 3-4节',
    location: '科技楼B402',
    credits: 3,
    capacityUsed: 50,
    category: 'compulsory',
    schedule: [
      { day: 5, timeSlot: 1 } // 周五第2时间段
    ]
  },
  {
    id: 6,
    name: '机器学习',
    code: 'CS106',
    teacher: '赵教授',
    time: '周二 5-6节',
    location: '科技楼C501',
    credits: 4,
    capacityUsed: 70,
    category: 'elective',
    schedule: [
      { day: 2, timeSlot: 2 } // 周二第3时间段
    ]
  },
  {
    id: 7,
    name: '软件工程',
    code: 'CS107',
    teacher: '孙教授',
    time: '周四 5-6节',
    location: '科技楼C502',
    credits: 3,
    capacityUsed: 65,
    category: 'compulsory',
    schedule: [
      { day: 4, timeSlot: 2 } // 周四第3时间段
    ]
  },
  {
    id: 8,
    name: '计算机组成原理',
    code: 'CS108',
    teacher: '周教授',
    time: '周一 3-4节',
    location: '科技楼B403',
    credits: 4,
    capacityUsed: 80,
    category: 'compulsory',
    schedule: [
      { day: 1, timeSlot: 1 } // 周一第2时间段
    ]
  }
])

// 已选课程
const selectedCourses = ref([])

// 课程表数据
const daysOfWeek = ref(['周一', '周二', '周三', '周四', '周五'])
const timeSlots = ref(['08:00-09:40', '10:00-11:40', '14:00-15:40'])
const courseColors = ref([
  'rgba(99, 102, 241, 0.8)', // indigo
  'rgba(16, 185, 129, 0.8)', // emerald
  'rgba(245, 158, 11, 0.8)', // amber
  'rgba(239, 68, 68, 0.8)',  // red
  'rgba(139, 92, 246, 0.8)', // violet
  'rgba(236, 72, 153, 0.8)'  // pink
])

// 计算属性
const filteredCourses = computed(() => {
  let filtered = allCourses.value

  // 按分类筛选
  if (filterCategory.value !== 'all') {
    filtered = filtered.filter(course => course.category === filterCategory.value)
  }

  // 按关键词搜索
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(course =>
      course.name.toLowerCase().includes(query) ||
      course.code.toLowerCase().includes(query) ||
      course.teacher.toLowerCase().includes(query)
    )
  }

  // 分页
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filtered.slice(start, end)
})

const selectedCredits = computed(() => {
  return selectedCourses.value.reduce((total, course) => total + course.credits, 0)
})

// 鼠标位置
const handleMouseMove = (e) => {
  document.documentElement.style.setProperty('--mouse-x', `${e.clientX}px`)
  document.documentElement.style.setProperty('--mouse-y', `${e.clientY}px`)
}

// 悬停效果
const hoverStat = (index) => {
  hoveredStat.value = index
}

const unhoverStat = (index) => {
  if (hoveredStat.value === index) {
    hoveredStat.value = null
  }
}

// 选课功能
const selectCourse = (course) => {
  // 检查学分限制
  if (selectedCredits.value + course.credits > maxCredits) {
    ElMessage.warning(`已达到学分上限(${maxCredits}学分)，无法选择更多课程`)
    return
  }

  // 检查时间冲突
  const hasConflict = selectedCourses.value.some(selectedCourse => {
    return selectedCourse.schedule.some(s =>
      course.schedule.some(cs => s.day === cs.day && s.timeSlot === cs.timeSlot)
    )
  })

  if (hasConflict) {
    ElMessage.warning('所选课程与已选课程时间冲突')
    return
  }

  // 添加到已选课程
  selectedCourses.value.push(course)
  ElMessage.success(`已成功选择课程：${course.name}`)
}

const deselectCourse = (courseId) => {
  const courseIndex = selectedCourses.value.findIndex(c => c.id === courseId)
  if (courseIndex !== -1) {
    const course = selectedCourses.value[courseIndex]
    selectedCourses.value.splice(courseIndex, 1)
    ElMessage.success(`已成功退选课程：${course.name}`)
  }
}

const isCourseSelected = (courseId) => {
  return selectedCourses.value.some(course => course.id === courseId)
}

// 容量颜色
const capacityColor = (capacity) => {
  if (capacity >= 90) return '#f56c6c' // 红色
  if (capacity >= 70) return '#f59e0b' // 橙色
  return '#67c23a' // 绿色
}

// 分页处理
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
}

// 确认选课
const selectionLoading = ref(false)
const confirmSelection = () => {
  if (selectedCourses.value.length === 0) {
    ElMessage.warning('请先选择课程')
    return
  }

  selectionLoading.value = true

  // 模拟API调用
  setTimeout(() => {
    selectionLoading.value = false
    ElMessage.success('选课成功！')

    // 模拟选课成功后更新课程状态
    selectedCourses.value.forEach(course => {
      const index = allCourses.value.findIndex(c => c.id === course.id)
      if (index !== -1) {
        allCourses.value[index].capacityUsed += 5
      }
    })

    // 清空已选课程
    selectedCourses.value = []
  }, 1500)
}

// 页面加载完成后初始化
onMounted(() => {
  // 模拟从服务器获取数据的延迟
  setTimeout(() => {
    // 可以在这里添加初始化逻辑
  }, 300)
})
</script>

<style scoped lang="scss">
.course-selection-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  --mouse-x: 0;
  --mouse-y: 0;
}

.modern-card {
  position: relative;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(226, 232, 240, 0.5);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  overflow: hidden;
  z-index: 1;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(600px circle at var(--mouse-x) var(--mouse-y),
        rgba(99, 102, 241, 0.08) 0%,
        transparent 70%);
    opacity: 0;
    transition: opacity 0.3s ease;
    z-index: -1;
    pointer-events: none;
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
    border-color: rgba(199, 210, 254, 0.8);

    &::before {
      opacity: 1;
    }
  }

  .dark & {
    background: rgba(30, 35, 45, 0.95);
    border-color: rgba(74, 85, 104, 0.3);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);

    &::before {
      background: radial-gradient(800px circle at var(--mouse-x) var(--mouse-y),
          rgba(99, 102, 241, 0.15) 0%,
          transparent 80%);
    }

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 12px 40px rgba(0, 0, 0, 0.4);
      border-color: rgba(99, 102, 241, 0.5);

      &::before {
        opacity: 1;
      }
    }
  }
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  h3 {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    color: #2c3e50;
    display: flex;
    align-items: center;

    .el-icon {
      margin-right: 12px;
      font-size: 22px;
      color: #409eff;
    }
  }

  .credit-summary {
    margin-left: 8px;
    font-size: 14px;
    color: #64748b;
  }
}

.search-filter {
  display: flex;
  gap: 12px;

  .modern-input {
    width: 200px;

    .el-input__inner {
      background-color: rgba(255, 255, 255, 0.7);
      border: 1px solid rgba(226, 232, 240, 0.8);
      border-radius: 12px;
      padding: 0 12px;
      height: 36px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
      transition: all 0.2s ease;

      &:hover {
        border-color: #c7d2fe;
      }

      &:focus {
        border-color: #6366f1;
        box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
      }
    }
  }

  .modern-select {
    width: 150px;

    .el-input__inner {
      background-color: rgba(255, 255, 255, 0.7);
      border: 1px solid rgba(226, 232, 240, 0.8);
      border-radius: 12px;
      padding: 0 12px;
      height: 36px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
      transition: all 0.2s ease;

      &:hover {
        border-color: #c7d2fe;
      }

      &:focus {
        border-color: #6366f1;
        box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
      }
    }
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.5rem;

  @media (max-width: 768px) {
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
  }

  @media (max-width: 480px) {
    grid-template-columns: 1fr;
  }

  .stat-card {
    display: flex;
    align-items: center;
    padding: 1.5rem;
    border-radius: 14px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(8px);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
    border: 1px solid rgba(226, 232, 240, 0.6);
    cursor: pointer;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
      border-color: rgba(199, 210, 254, 0.8);
    }

    .stat-icon {
      position: relative;
      margin-right: 1.25rem;
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      z-index: 1;

      .icon-bg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        border-radius: 12px;
        background-color: var(--card-color);
        opacity: 0.1;
        transition: all 0.3s ease;
      }

      .el-icon {
        color: var(--card-color);
        z-index: 1;
      }
    }

    .stat-content {
      flex: 1;
      min-width: 0;
      z-index: 1;

      .stat-value {
        font-size: 1.75rem;
        font-weight: 700;
        color: #1e293b;
        line-height: 1.2;
        margin-bottom: 0.25rem;
        font-feature-settings: "tnum";
      }

      .stat-label {
        font-size: 0.875rem;
        font-weight: 500;
        color: #64748b;
        line-height: 1.4;
      }
    }

    .stat-trend {
      display: flex;
      align-items: center;
      margin-left: 0.75rem;
      font-size: 0.75rem;
      font-weight: 600;

      .el-icon {
        margin-right: 0.25rem;
        font-size: 0.875rem;
      }
    }
  }
}

.selection-guide {
  .guide-content {
    padding: 0 16px;

    h3 {
      font-size: 15px;
      margin: 16px 0 8px 0;
      color: #1e293b;
    }

    ol,
    ul {
      padding-left: 20px;
      margin: 8px 0;
      font-size: 14px;
      color: #4b5563;

      li {
        margin-bottom: 6px;
        line-height: 1.6;
      }
    }

    .faq-item {
      margin-bottom: 16px;

      h4 {
        font-size: 14px;
        margin: 0 0 8px 0;
        color: #1e293b;
      }

      p {
        font-size: 14px;
        color: #4b5563;
        margin: 0;
        line-height: 1.6;
      }
    }
  }
}

:deep(.el-collapse-item__header) {
  font-weight: 600;
  font-size: 16px;
  padding: 0 16px;
  height: 56px;
  border-bottom: none;

  .el-icon {
    margin-right: 12px;
    color: #409eff;
  }
}

:deep(.el-collapse-item__content) {
  padding-bottom: 16px;
}

.courses-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.course-card {
  border-radius: 14px;
  padding: 16px;
  transition: all 0.3s ease;

  .course-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    h4 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: #1e293b;
    }

    .course-code {
      font-size: 12px;
      color: #64748b;
      background-color: rgba(226, 232, 240, 0.5);
      padding: 2px 8px;
      border-radius: 4px;
    }
  }

  .course-info {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
    margin-bottom: 16px;

    .info-item {
      display: flex;
      align-items: center;
      font-size: 13px;
      color: #4b5563;

      .el-icon {
        margin-right: 6px;
        color: #64748b;
      }
    }

    .capacity {
      grid-column: 1 / span 2;
      margin-top: 4px;

      .el-progress__text {
        font-size: 11px !important;
      }
    }
  }

  .course-footer {
    display: flex;
    justify-content: flex-end;
  }
}

.selected-courses-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.selected-course-card {
  border-radius: 14px;
  padding: 16px;
  transition: all 0.3s ease;

  .course-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    h4 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: #1e293b;
    }

    .course-code {
      font-size: 12px;
      color: #64748b;
      background-color: rgba(226, 232, 240, 0.5);
      padding: 2px 8px;
      border-radius: 4px;
    }
  }

  .course-info {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
    margin-bottom: 16px;

    .info-item {
      display: flex;
      align-items: center;
      font-size: 13px;
      color: #4b5563;

      .el-icon {
        margin-right: 6px;
        color: #64748b;
      }
    }
  }

  .course-footer {
    display: flex;
    justify-content: flex-end;
  }
}

.selection-actions {
  display: flex;
  justify-content: flex-end;
}

.no-courses,
.no-selected-courses,
.no-timetable {
  padding: 40px 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.timetable-container {
  overflow-x: auto;
}

.timetable-header {
  display: grid;
  grid-template-columns: 120px repeat(5, 1fr);
  gap: 1px;
  background-color: rgba(226, 232, 240, 0.5);
  border-radius: 8px 8px 0 0;
  padding: 8px;
}

.time-slot-header {
  font-weight: 600;
  color: #64748b;
  text-align: center;
}

.day-header {
  font-weight: 600;
  color: #64748b;
  text-align: center;
}

.timetable-grid {
  display: grid;
  grid-template-columns: 120px repeat(5, 1fr);
  gap: 1px;
  background-color: rgba(226, 232, 240, 0.5);
}

.time-row {
  display: contents;
}

.time-label {
  background-color: white;
  padding: 12px;
  text-align: center;
  font-weight: 500;
  color: #4b5563;
}

.day-column {
  position: relative;
  min-height: 80px;
  background-color: white;
  padding: 4px;
}

.course-block {
  position: absolute;
  width: calc(100% - 8px);
  margin: 4px;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
  transition: all 0.2s ease;
}

.course-block-inner {
  padding: 6px 8px;
  color: white;
  font-size: 12px;
  min-height: 72px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.course-name {
  font-weight: 600;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.course-location {
  font-size: 11px;
  opacity: 0.8;
}
</style>
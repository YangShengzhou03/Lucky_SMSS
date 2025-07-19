<template>
  <div class="grades-dashboard" :class="{ 'dark': isDarkMode }" @mousemove="handleMouseMove">
    <!-- 成绩概览卡片 -->
    <div class="grades-overview modern-card">
      <div class="card-header">
        <h2>成绩概览</h2>
        <div class="semester-selector">
          <el-select v-model="currentSemester" placeholder="选择学期" size="small">
            <el-option v-for="item in semesters" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
      </div>

      <div class="stats-container">
        <!-- GPA卡片 -->
        <div class="stats-card">
          <div class="stat-header">
            <h3>平均绩点</h3>
            <div class="stat-value">{{ gpa || '0.00' }}</div>
          </div>
          <div class="stat-meta">
            <div class="meta-item">
              <el-icon>
                <User />
              </el-icon>
              <span>班级排名: {{ rank || '--' }}/{{ classSize || '--' }}</span>
            </div>
            <div class="stat-progress">
              <el-progress :percentage="gpaPercentage" :stroke-width="6" :color="progressColor" />
              <div class="progress-text">超过班级 {{ gpaPercentage }}% 的同学</div>
            </div>
          </div>
        </div>

        <!-- 平均分卡片 -->
        <div class="stats-card">
          <div class="stat-header">
            <h3>平均分数</h3>
            <div class="stat-value">{{ avgScore || '0' }}</div>
          </div>
          <div class="stat-meta">
            <div class="score-distribution">
              <div class="distribution-item" v-for="(item, index) in scoreDistribution" :key="index">
                <div class="distribution-bar" :style="{ height: `${item.percentage}%`, backgroundColor: item.color }">
                </div>
                <div class="distribution-label">{{ item.label }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 学分卡片 -->
        <div class="stats-card">
          <div class="stat-header">
            <h3>学分完成情况</h3>
            <div class="stat-value">{{ completedCredits || '0' }}/{{ totalCredits || '0' }}</div>
          </div>
          <div class="stat-meta">
            <el-progress :percentage="creditPercentage" :stroke-width="6" :color="creditColor" />
            <div class="progress-text">已完成 {{ creditPercentage }}% 的毕业学分要求</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 详细成绩表格卡片 -->
    <div class="grades-table modern-card">
      <div class="card-header">
        <h2>详细成绩</h2>
        <div class="table-actions">
          <el-input v-model="searchKeyword" placeholder="搜索课程名称" prefix-icon="Search" size="small"
            class="search-input" />
          <el-select v-model="filterType" placeholder="筛选类型" size="small" class="filter-select">
            <el-option label="全部课程" value="all" />
            <el-option label="必修课" value="required" />
            <el-option label="选修课" value="elective" />
            <el-option label="实践课" value="practical" />
          </el-select>
        </div>
      </div>

      <el-table :data="filteredGrades" border stripe class="grades-data-table" style="width: 100%">
        <el-table-column prop="courseName" label="课程名称" min-width="200" />
        <el-table-column prop="courseCode" label="课程代码" width="120" />
        <el-table-column prop="courseType" label="课程类型" width="100" />
        <el-table-column prop="credits" label="学分" width="80" />
        <el-table-column prop="score" label="成绩" width="100" :formatter="formatScore" />
        <el-table-column prop="gpa" label="绩点" width="80" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button type="text" size="small" @click="viewDetails(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination" v-if="filteredGrades.length > 0">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
          :page-sizes="[5, 10, 20]" :page-size="pageSize" :total="filteredGrades.length"
          layout="total, sizes, prev, pager, next" small />
      </div>
    </div>

    <!-- 成绩趋势与分析卡片 -->
    <div class="grades-analysis modern-card">
      <div class="card-header">
        <h2>成绩趋势分析</h2>
        <div class="chart-type-selector">
          <el-radio-group v-model="chartType">
            <el-radio-button label="gpa">绩点趋势</el-radio-button>
            <el-radio-button label="score">分数趋势</el-radio-button>
            <el-radio-button label="credit">学分趋势</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <div class="chart-container">
        <div class="trend-chart">
          <canvas ref="trendChartCanvas"></canvas>
        </div>
      </div>
    </div>

    <!-- 绩点计算器卡片 -->
    <div class="gpa-calculator modern-card">
      <div class="card-header">
        <h2>绩点计算器</h2>
        <el-button type="primary" size="small" @click="addCourse" class="add-course-btn">
          <el-icon>
            <Plus />
          </el-icon> 添加课程
        </el-button>
      </div>

      <div class="calculator-content">
        <el-table :data="calculatorCourses" border stripe class="calculator-table">
          <el-table-column label="课程名称" width="200">
            <template #default="scope">
              <el-input v-model="scope.row.name" size="small" placeholder="输入课程名称" />
            </template>
          </el-table-column>
          <el-table-column label="学分" width="100">
            <template #default="scope">
              <el-input-number v-model="scope.row.credits" size="small" :min="1" :max="5" :step="0.5" />
            </template>
          </el-table-column>
          <el-table-column label="成绩" width="100">
            <template #default="scope">
              <el-input-number v-model="scope.row.score" size="small" :min="0" :max="100" :step="1" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <el-button type="text" size="small" @click="removeCourse(scope.$index)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="calculator-result">
          <div class="result-label">计算结果:</div>
          <div class="result-value">预计绩点: <span class="gpa-result">{{ calculatedGPA || '0.00' }}</span></div>
          <el-button type="primary" size="small" @click="calculateGPA">
            计算绩点
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, inject, watch } from 'vue'
import { Plus, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Chart from 'chart.js/auto'

// 暗色模式状态
const isDarkMode = inject('isDarkMode', ref(false))

// 学期数据
const currentSemester = ref('2023-2024-2')
const semesters = ref([
  { value: '2023-2024-2', label: '2023-2024学年第二学期' },
  { value: '2023-2024-1', label: '2023-2024学年第一学期' },
  { value: '2022-2023-2', label: '2022-2023学年第二学期' },
  { value: '2022-2023-1', label: '2022-2023学年第一学期' }
])

// 成绩数据
const allGrades = ref([
  {
    id: 1,
    courseName: '数据结构与算法',
    courseCode: 'CS101',
    courseType: '必修课',
    credits: 4,
    score: 85,
    gpa: 3.7,
    semester: '2023-2024-2'
  },
  {
    id: 2,
    courseName: '计算机网络',
    courseCode: 'CS102',
    courseType: '必修课',
    credits: 3,
    score: 78,
    gpa: 2.8,
    semester: '2023-2024-2'
  },
  {
    id: 3,
    courseName: '操作系统',
    courseCode: 'CS103',
    courseType: '必修课',
    credits: 4,
    score: 92,
    gpa: 4.0,
    semester: '2023-2024-2'
  },
  {
    id: 4,
    courseName: '人工智能导论',
    courseCode: 'CS104',
    courseType: '选修课',
    credits: 3,
    score: 88,
    gpa: 3.9,
    semester: '2023-2024-2'
  },
  {
    id: 5,
    courseName: '高等数学（下）',
    courseCode: 'MATH102',
    courseType: '必修课',
    credits: 5,
    score: 76,
    gpa: 2.6,
    semester: '2023-2024-1'
  },
  {
    id: 6,
    courseName: '线性代数',
    courseCode: 'MATH103',
    courseType: '必修课',
    credits: 4,
    score: 82,
    gpa: 3.3,
    semester: '2023-2024-1'
  }
])

// 图表引用
const trendChartCanvas = ref(null)
let trendChart = null

// 搜索与筛选
const searchKeyword = ref('')
const filterType = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)

// 鼠标位置
const mouseX = ref(0)
const mouseY = ref(0)

// 计算属性
const filteredGrades = computed(() => {
  return allGrades.value
    .filter(grade =>
      grade.courseName.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
    .filter(grade =>
      filterType.value === 'all' || grade.courseType === filterType.value
    )
})

const currentSemesterGrades = computed(() => {
  return allGrades.value.filter(grade => grade.semester === currentSemester.value)
})

const gpa = computed(() => {
  if (currentSemesterGrades.value.length === 0) return 0
  const totalCredits = currentSemesterGrades.value.reduce((sum, grade) => sum + grade.credits, 0)
  const totalPoints = currentSemesterGrades.value.reduce((sum, grade) => sum + (grade.gpa * grade.credits), 0)
  return (totalPoints / totalCredits).toFixed(2)
})

const avgScore = computed(() => {
  if (currentSemesterGrades.value.length === 0) return 0
  const totalScore = currentSemesterGrades.value.reduce((sum, grade) => sum + grade.score, 0)
  return Math.round(totalScore / currentSemesterGrades.value.length)
})

const rank = ref(12)
const classSize = ref(45)
const gpaPercentage = computed(() => {
  return Math.round((1 - (rank.value / classSize.value)) * 100)
})

const completedCredits = ref(68)
const totalCredits = ref(140)
const creditPercentage = computed(() => {
  return Math.round((completedCredits.value / totalCredits.value) * 100)
})

const progressColor = computed(() => {
  const progress = gpaPercentage.value
  if (progress < 30) return '#ef4444'
  if (progress < 60) return '#f59e0b'
  return '#10b981'
})

const creditColor = computed(() => {
  const progress = creditPercentage.value
  if (progress < 30) return '#ef4444'
  if (progress < 60) return '#f59e0b'
  return '#10b981'
})

const scoreDistribution = ref([
  { label: '90-100', percentage: 25, color: '#67c23a' },
  { label: '80-89', percentage: 35, color: '#409eff' },
  { label: '70-79', percentage: 25, color: '#e6a23c' },
  { label: '60-69', percentage: 10, color: '#f56c6c' },
  { label: '60以下', percentage: 5, color: '#909399' }
])

// 初始化趋势图表
const initTrendChart = () => {
  if (trendChartCanvas.value) {
    const ctx = trendChartCanvas.value.getContext('2d')

    const data = [
      { semester: '2022-2023-1', gpa: 3.2, score: 80, credits: 28 },
      { semester: '2022-2023-2', gpa: 3.5, score: 84, credits: 32 },
      { semester: '2023-2024-1', gpa: 3.4, score: 82, credits: 30 },
      { semester: '2023-2024-2', gpa: 3.6, score: 86, credits: 28 }
    ]

    trendChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: data.map(item => item.semester),
        datasets: [{
          label: '绩点趋势',
          data: data.map(item => item.gpa),
          borderColor: '#409eff',
          backgroundColor: isDarkMode.value ? 'rgba(64, 158, 255, 0.1)' : 'rgba(64, 158, 255, 0.2)',
          tension: 0.4,
          borderWidth: 2,
          pointBackgroundColor: '#fff',
          pointBorderColor: '#409eff',
          pointRadius: 4,
          pointHoverRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: false,
            min: 2.5,
            max: 4.0,
            ticks: {
              stepSize: 0.5
            },
            grid: {
              color: isDarkMode.value ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.05)'
            }
          },
          x: {
            grid: {
              display: false
            }
          }
        },
        plugins: {
          legend: {
            display: false
          }
        }
      }
    })
  }
}

// 监听暗色模式变化
watch(isDarkMode, (newVal) => {
  if (trendChart) {
    trendChart.options.scales.y.grid.color = newVal
      ? 'rgba(255, 255, 255, 0.1)'
      : 'rgba(0, 0, 0, 0.05)'
    trendChart.data.datasets[0].backgroundColor = newVal
      ? 'rgba(64, 158, 255, 0.1)'
      : 'rgba(64, 158, 255, 0.2)'
    trendChart.update()
  }
})

// 绩点计算器
const calculatorCourses = ref([
  { name: '', credits: 3, score: 80 }
])
const calculatedGPA = ref('0.00')

// 方法
const formatScore = (row) => {
  if (row.score >= 90) return `${row.score} (优秀)`
  if (row.score >= 80) return `${row.score} (良好)`
  if (row.score >= 70) return `${row.score} (中等)`
  if (row.score >= 60) return `${row.score} (及格)`
  return `${row.score} (不及格)`
}

const viewDetails = (row) => {
  ElMessage.info(`查看课程详情: ${row.courseName}`)
}

const handleSizeChange = (size) => {
  pageSize.value = size
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

const addCourse = () => {
  calculatorCourses.value.push({ name: '', credits: 3, score: 80 })
}

const removeCourse = (index) => {
  if (calculatorCourses.value.length > 1) {
    calculatorCourses.value.splice(index, 1)
  } else {
    ElMessage.warning('至少保留一门课程')
  }
}

const calculateGPA = () => {
  const validCourses = calculatorCourses.value.filter(course => course.score && course.credits)
  if (validCourses.length === 0) return 0

  const totalCredits = validCourses.reduce((sum, course) => sum + course.credits, 0)
  const totalPoints = validCourses.reduce((sum, course) => {
    // 百分制转绩点计算
    let gpa = 0
    if (course.score >= 90) gpa = 4.0
    else if (course.score >= 85) gpa = 3.7
    else if (course.score >= 82) gpa = 3.3
    else if (course.score >= 78) gpa = 3.0
    else if (course.score >= 75) gpa = 2.7
    else if (course.score >= 72) gpa = 2.3
    else if (course.score >= 68) gpa = 2.0
    else if (course.score >= 64) gpa = 1.5
    else if (course.score >= 60) gpa = 1.0
    return sum + (gpa * course.credits)
  }, 0)

  calculatedGPA.value = (totalPoints / totalCredits).toFixed(2)
  return calculatedGPA.value
}

// 处理鼠标移动
const handleMouseMove = (e) => {
  mouseX.value = e.clientX
  mouseY.value = e.clientY
}

// 初始化
onMounted(() => {
  initTrendChart()
})

onUnmounted(() => {
  if (trendChart) trendChart.destroy()
})
</script>

<style scoped lang="scss">
.grades-dashboard {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  transition: background-color 0.3s ease;
  padding: 0 15px;
}

// 主内容区域 - 增加了 gap 值，调整了 max-width
.dashboard-content {
  flex: 1;
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
  padding: 40px 0;
  display: grid;
  grid-template-columns: 1fr;
  gap: 60px; // 显著增加了卡片之间的间距
}

// 现代化卡片样式
.modern-card {
  position: relative;
  border-radius: 16px;
  padding: 30px;
  transition: all 0.3s ease;
  overflow: hidden;
  z-index: 1;
  margin-bottom: 15px; // 额外增加底部间距

  // 浅色模式
  background: white;
  border: 1px solid rgba(0, 0, 0, 0.1);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.05);

  // 深色模式样式
  .dark & {
    background: rgba(30, 41, 59, 0.8);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  }

  // 卡片悬停效果 - 减少悬浮高度，增强阴影清晰度
  &:hover {
    transform: translateY(-2px); // 减少悬浮高度，避免覆盖相邻卡片
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12); // 调整阴影，保持足够的视觉反馈
  }

  // 卡片头部
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
      color: var(--text-primary);
    }
  }
}

// 成绩概览卡片
.grades-overview {
  .stats-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 30px; // 增加了小卡片之间的间距

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }

  .stats-card {
    background-color: rgba(0, 0, 0, 0.03);
    border-radius: 12px;
    padding: 20px;
    transition: all 0.3s ease;

    .dark & {
      background-color: rgba(255, 255, 255, 0.05);
    }

    // 调整小卡片的悬停效果
    &:hover {
      transform: translateY(-2px); // 减少悬浮高度
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08); // 调整阴影
    }

    .stat-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 500;
        color: var(--text-secondary);
      }

      .stat-value {
        font-size: 28px;
        font-weight: 700;
        color: var(--text-primary);
      }
    }

    .stat-meta {
      .meta-item {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 12px;
        color: var(--text-secondary);
        font-size: 14px;

        .el-icon {
          color: #409eff;
        }
      }

      .stat-progress {
        margin-top: 16px;

        .progress-text {
          margin-top: 8px;
          font-size: 13px;
          color: var(--text-secondary);
          text-align: center;
        }
      }

      .score-distribution {
        display: flex;
        justify-content: space-around;
        align-items: flex-end;
        height: 120px;
        margin-top: 16px;

        .distribution-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          width: 20px;

          .distribution-bar {
            width: 100%;
            border-radius: 4px 4px 0 0;
            transition: height 0.5s ease;
          }

          .distribution-label {
            margin-top: 8px;
            font-size: 12px;
            color: var(--text-secondary);
          }
        }
      }
    }
  }
}

// 详细成绩表格卡片
.grades-table {
  .table-actions {
    display: flex;
    gap: 12px;

    .search-input {
      width: 240px;
    }

    .filter-select {
      width: 150px;
    }
  }

  .grades-data-table {
    margin-top: 20px;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

// 成绩趋势分析卡片
.grades-analysis {
  .chart-type-selector {
    margin-bottom: 20px;
  }

  .chart-container {
    height: 350px;

    .trend-chart {
      width: 100%;
      height: 100%;
    }
  }
}

// 绩点计算器卡片
.gpa-calculator {
  .add-course-btn {
    margin-left: auto;
  }

  .calculator-table {
    margin-top: 20px;
  }

  .calculator-result {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-top: 20px;

    .result-label {
      font-size: 16px;
      font-weight: 500;
      color: var(--text-secondary);
    }

    .result-value {
      font-size: 16px;
      color: var(--text-primary);

      .gpa-result {
        font-size: 20px;
        font-weight: 700;
        color: #409eff;
      }
    }
  }
}

// 颜色变量
:root {
  --text-primary: #303133;
  --text-secondary: #606266;
}

.dark {
  --text-primary: #ffffff;
  --text-secondary: rgba(255, 255, 255, 0.7);
}
</style>
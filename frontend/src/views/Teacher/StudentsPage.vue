<template>
  <div class="grades-dashboard" :class="{ 'dark': isDarkMode }" @mousemove="handleMouseMove">
    <!-- 第一张卡片：成绩统计概览 -->
    <div class="grades-stats modern-card">
      <div class="card-header">
        <h2>成绩统计概览</h2>
      </div>
      <div class="stats-container">
        <div class="stats-card" :class="{ 'high-light': selectedCourse && selectedClass }">
          <div class="stat-header">
            <h3>当前课程平均分</h3>
            <div class="stat-value">{{ courseAverageScore || '0' }}</div>
          </div>
          <div class="stat-trend">
            <span v-if="courseAverageScoreTrend > 0" class="trend-up">
              <el-icon>
                <ArrowUp />
              </el-icon>
              {{ courseAverageScoreTrend.toFixed(1) }}%
            </span>
            <span v-else-if="courseAverageScoreTrend < 0" class="trend-down">
              <el-icon>
                <ArrowDown />
              </el-icon>
              {{ Math.abs(courseAverageScoreTrend).toFixed(1) }}%
            </span>
            <span v-else>持平</span>
            <span class="text-sm ml-2">vs 上学期</span>
          </div>
        </div>

        <div class="stats-card" :class="{ 'high-light': selectedCourse && selectedClass }">
          <div class="stat-header">
            <h3>及格率</h3>
            <div class="stat-value">{{ coursePassRate || '0%' }}</div>
          </div>
          <div class="stat-progress">
            <el-progress :percentage="coursePassRateValue" :stroke-width="6" :color="coursePassRateColor" />
          </div>
        </div>

        <div class="stats-card" :class="{ 'high-light': selectedCourse && selectedClass }">
          <div class="stat-header">
            <h3>最高分/最低分</h3>
            <div class="stat-value">{{ courseMaxScore || '0' }}/{{ courseMinScore || '0' }}</div>
          </div>
          <div class="stat-meta">
            <div class="text-sm text-secondary">
              {{ courseMaxStudent || '暂无数据' }} / {{ courseMinStudent || '暂无数据' }}
            </div>
          </div>
        </div>

        <div class="stats-card" :class="{ 'high-light': selectedCourse && selectedClass }">
          <div class="stat-header">
            <h3>待提交成绩</h3>
            <div class="stat-value">{{ pendingGradesCount || '0' }}</div>
          </div>
          <div class="stat-meta">
            <el-button @click="checkPendingGrades" type="text" size="small" class="mt-2">
              查看详情
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 第二张卡片：成绩管理（重新设计布局） -->
    <div class="grades-management modern-card">
      <!-- 卡片头部 -->
      <div class="card-header">
        <h2>成绩管理</h2>
      </div>

      <!-- 筛选与操作区 -->
      <div class="management-controls">
        <!-- 顶部筛选区 -->
        <div class="filters-section">
          <div class="filter-group">
            <el-select v-model="selectedCourse" placeholder="选择课程" size="small">
              <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
            </el-select>
            <el-select v-model="selectedClass" placeholder="选择班级" size="small">
              <el-option v-for="classItem in classes" :key="classItem.id" :label="classItem.name"
                :value="classItem.id" />
            </el-select>
            <el-select v-model="currentSemester" placeholder="选择学期" size="small">
              <el-option v-for="item in semesters" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </div>

          <div class="action-buttons">
            <el-button @click="importGrades" type="primary" size="small" class="btn-import">
              <el-icon>
                <Upload />
              </el-icon> 导入成绩
            </el-button>
            <el-button @click="exportGrades" type="success" size="small" class="btn-export">
              <el-icon>
                <Download />
              </el-icon> 导出成绩
            </el-button>
            <el-button @click="submitGrades" type="danger" size="small" class="btn-submit" :loading="isSubmitting">
              提交成绩
            </el-button>
          </div>
        </div>

        <!-- 搜索与筛选区 -->
        <div class="search-section">
          <el-input v-model="searchKeyword" placeholder="搜索学生姓名" prefix-icon="Search" size="small"
            class="search-input" />
          <el-select v-model="filterType" placeholder="筛选类型" size="small" class="filter-select">
            <el-option label="全部学生" value="all" />
            <el-option label="及格" value="pass" />
            <el-option label="不及格" value="fail" />
            <el-option label="待提交" value="pending" />
          </el-select>
        </div>
      </div>

      <!-- 表格内容区 -->
      <div class="table-container">
        <el-table :data="filteredGrades" border stripe class="grades-data-table" style="width: 100%">
          <el-table-column prop="studentId" label="学号" width="120" />
          <el-table-column prop="studentName" label="学生姓名" width="100" />
          <el-table-column prop="courseName" label="课程名称" min-width="150" />
          <el-table-column prop="courseType" label="课程类型" width="100" />
          <el-table-column prop="credits" label="学分" width="80" />
          <el-table-column label="成绩" width="150">
            <template #default="scope">
              <el-input-number v-model="scope.row.score" :min="0" :max="100" size="small"
                @change="handleScoreChange(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="绩点" width="80" :formatter="formatGPA" />
          <el-table-column label="状态" width="100" :formatter="formatStatus" />
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button type="text" size="small" @click="viewDetails(scope.row)">
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页控件 -->
      <div class="pagination" v-if="filteredGrades.length > 0">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
          :page-sizes="[5, 10, 20]" :page-size="pageSize" :total="filteredGrades.length"
          layout="total, sizes, prev, pager, next" small />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject, watch } from 'vue'
import { Upload, Download, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const isDarkMode = inject('isDarkMode', ref(false))

const selectedCourse = ref(null)
const selectedClass = ref(null)
const currentSemester = ref('2023-2024-2')
const semesters = ref([
  { value: '2023-2024-2', label: '2023-2024学年第二学期' },
  { value: '2023-2024-1', label: '2023-2024学年第一学期' },
  { value: '2022-2023-2', label: '2022-2023学年第二学期' },
  { value: '2022-2023-1', label: '2022-2023学年第一学期' }
])

const courses = ref([
  { id: 1, name: '数据结构与算法' },
  { id: 2, name: '计算机网络' },
  { id: 3, name: '操作系统' },
  { id: 4, name: '人工智能导论' },
  { id: 5, name: '高等数学（下）' },
  { id: 6, name: '线性代数' }
])

const classes = ref([
  { id: 1, name: '计算机科学与技术1班' },
  { id: 2, name: '计算机科学与技术2班' },
  { id: 3, name: '软件工程1班' },
  { id: 4, name: '软件工程2班' }
])

const allGrades = ref([
  {
    id: 1,
    studentId: '2023001',
    studentName: '张三',
    courseId: 1,
    courseName: '数据结构与算法',
    courseType: '必修课',
    credits: 4,
    score: 85,
    semester: '2023-2024-2',
    classId: 1,
    submitted: true
  },
  {
    id: 2,
    studentId: '2023002',
    studentName: '李四',
    courseId: 1,
    courseName: '数据结构与算法',
    courseType: '必修课',
    credits: 4,
    score: 78,
    semester: '2023-2024-2',
    classId: 1,
    submitted: true
  },
  {
    id: 3,
    studentId: '2023003',
    studentName: '王五',
    courseId: 1,
    courseName: '数据结构与算法',
    courseType: '必修课',
    credits: 4,
    score: null,
    semester: '2023-2024-2',
    classId: 1,
    submitted: false
  },
  {
    id: 4,
    studentId: '2023004',
    studentName: '赵六',
    courseId: 1,
    courseName: '数据结构与算法',
    courseType: '必修课',
    credits: 4,
    score: 92,
    semester: '2023-2024-2',
    classId: 2,
    submitted: true
  },
  {
    id: 5,
    studentId: '2023005',
    studentName: '钱七',
    courseId: 1,
    courseName: '数据结构与算法',
    courseType: '必修课',
    credits: 4,
    score: 88,
    semester: '2023-2024-2',
    classId: 2,
    submitted: true
  },
  {
    id: 6,
    studentId: '2023006',
    studentName: '孙八',
    courseId: 1,
    courseName: '数据结构与算法',
    courseType: '必修课',
    credits: 4,
    score: 59,
    semester: '2023-2024-2',
    classId: 2,
    submitted: true
  }
])

const searchKeyword = ref('')
const filterType = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const isSubmitting = ref(false)

const filteredGrades = computed(() => {
  let grades = allGrades.value

  // 筛选课程
  if (selectedCourse.value) {
    grades = grades.filter(grade => grade.courseId === selectedCourse.value)
  }

  // 筛选班级
  if (selectedClass.value) {
    grades = grades.filter(grade => grade.classId === selectedClass.value)
  }

  // 筛选学期
  if (currentSemester.value) {
    grades = grades.filter(grade => grade.semester === currentSemester.value)
  }

  // 搜索学生姓名
  if (searchKeyword.value) {
    grades = grades.filter(grade =>
      grade.studentName.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  // 筛选及格/不及格/待提交
  if (filterType.value === 'pass') {
    grades = grades.filter(grade => grade.score >= 60)
  } else if (filterType.value === 'fail') {
    grades = grades.filter(grade => grade.score < 60 && grade.score !== null)
  } else if (filterType.value === 'pending') {
    grades = grades.filter(grade => grade.score === null || !grade.submitted)
  }

  return grades
})

// 当前课程统计
const courseGrades = computed(() => {
  if (!selectedCourse.value || !selectedClass.value) return []

  return allGrades.value.filter(grade =>
    grade.courseId === selectedCourse.value &&
    grade.classId === selectedClass.value &&
    grade.semester === currentSemester.value
  )
})

const courseAverageScore = computed(() => {
  if (courseGrades.value.length === 0) return 0
  const validScores = courseGrades.value.filter(g => g.score !== null)
  if (validScores.length === 0) return 0

  const sum = validScores.reduce((total, grade) => total + grade.score, 0)
  return (sum / validScores.length).toFixed(1)
})

// 模拟与上学期对比数据
const courseAverageScoreTrend = computed(() => {
  if (courseGrades.value.length === 0) return 0
  // 随机生成-5到5之间的趋势值
  return (Math.random() * 10 - 5).toFixed(1) * 1
})

const coursePassRate = computed(() => {
  if (courseGrades.value.length === 0) return '0%'
  const validScores = courseGrades.value.filter(g => g.score !== null)
  if (validScores.length === 0) return '0%'

  const passCount = validScores.filter(grade => grade.score >= 60).length
  return ((passCount / validScores.length) * 100).toFixed(1) + '%'
})

const coursePassRateValue = computed(() => {
  if (courseGrades.value.length === 0) return 0
  const validScores = courseGrades.value.filter(g => g.score !== null)
  if (validScores.length === 0) return 0

  const passCount = validScores.filter(grade => grade.score >= 60).length
  return Math.round((passCount / validScores.length) * 100)
})

const coursePassRateColor = computed(() => {
  const rate = coursePassRateValue.value
  if (rate < 60) return '#ef4444'
  if (rate < 85) return '#f59e0b'
  return '#10b981'
})

const courseMaxScore = computed(() => {
  const validScores = courseGrades.value.filter(g => g.score !== null)
  if (validScores.length === 0) return 0
  return Math.max(...validScores.map(grade => grade.score))
})

const courseMinScore = computed(() => {
  const validScores = courseGrades.value.filter(g => g.score !== null)
  if (validScores.length === 0) return 0
  return Math.min(...validScores.map(grade => grade.score))
})

const courseMaxStudent = computed(() => {
  const validScores = courseGrades.value.filter(g => g.score !== null)
  if (validScores.length === 0) return ''

  const maxScore = Math.max(...validScores.map(grade => grade.score))
  const maxStudent = validScores.find(grade => grade.score === maxScore)
  return maxStudent ? maxStudent.studentName : ''
})

const courseMinStudent = computed(() => {
  const validScores = courseGrades.value.filter(g => g.score !== null)
  if (validScores.length === 0) return ''

  const minScore = Math.min(...validScores.map(grade => grade.score))
  const minStudent = validScores.find(grade => grade.score === minScore)
  return minStudent ? minStudent.studentName : ''
})

const pendingGradesCount = computed(() => {
  if (courseGrades.value.length === 0) return 0
  return courseGrades.value.filter(grade => grade.score === null || !grade.submitted).length
})

const formatGPA = (row) => {
  if (row.score === null || row.score === undefined) return '-'
  if (row.score >= 90) return 4.0
  if (row.score >= 85) return 3.7
  if (row.score >= 80) return 3.3
  if (row.score >= 75) return 3.0
  if (row.score >= 70) return 2.7
  if (row.score >= 65) return 2.3
  if (row.score >= 60) return 1.0
  return 0
}

const formatStatus = (row) => {
  if (row.score === null || row.score === undefined) return '未录入'
  if (row.score >= 60) return '及格'
  return '不及格'
}

const handleScoreChange = (row) => {
  // 更新成绩后可以做一些验证或其他操作
  console.log(`成绩更新: ${row.studentName} - ${row.courseName}: ${row.score}`)
  row.submitted = false // 成绩修改后重置提交状态
}

const viewDetails = (row) => {
  ElMessage.info(`查看 ${row.studentName} 的 ${row.courseName} 成绩详情`)
  // 这里可以添加查看详情的逻辑
}

const importGrades = () => {
  ElMessage.info('导入成绩功能')
  // 这里可以添加导入成绩的逻辑
}

const exportGrades = () => {
  if (!selectedCourse.value || !selectedClass.value) {
    ElMessage.warning('请先选择课程和班级')
    return
  }

  ElMessage.success(`导出 ${currentSemester.value} 学期 ${selectedCourse.value} 课程 ${selectedClass.value} 班级的成绩数据`)
  // 这里可以添加导出成绩的逻辑
}

const submitGrades = () => {
  if (!selectedCourse.value || !selectedClass.value) {
    ElMessage.warning('请先选择课程和班级')
    return
  }

  const unsubmittedGrades = courseGrades.value.filter(grade => grade.score === null || !grade.submitted)

  if (unsubmittedGrades.length > 0) {
    ElMessageBox.confirm(
      `还有 ${unsubmittedGrades.length} 名学生的成绩未提交，确定要提交吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      proceedSubmit()
    }).catch(() => {
      // 用户取消操作
    })
  } else {
    ElMessage.info('所有成绩已提交')
  }
}

const proceedSubmit = () => {
  isSubmitting.value = true

  // 模拟提交成绩到服务器
  setTimeout(() => {
    isSubmitting.value = false

    // 更新提交状态
    courseGrades.value.forEach(grade => {
      if (grade.score !== null) {
        grade.submitted = true
      }
    })

    ElMessage.success('成绩提交成功！')
  }, 1500)
}

const checkPendingGrades = () => {
  if (!selectedCourse.value || !selectedClass.value) {
    ElMessage.warning('请先选择课程和班级')
    return
  }

  filterType.value = 'pending'

}

const handleSizeChange = (size) => {
  pageSize.value = size
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

const handleMouseMove = (e) => {
  document.documentElement.style.setProperty('--mouse-x', `${e.clientX}px`)
  document.documentElement.style.setProperty('--mouse-y', `${e.clientY}px`)
}

onMounted(() => {
  // 初始化时默认选择第一个课程和班级
  if (courses.value.length > 0) {
    selectedCourse.value = courses.value[0].id
  }

  if (classes.value.length > 0) {
    selectedClass.value = classes.value[0].id
  }
})

watch(isDarkMode, (newVal) => {
  // 深色模式切换时的处理
  console.log('深色模式切换:', newVal)
})
</script>

<style scoped lang="scss">
:root {
  --text-primary: #303133;
  --text-secondary: #606266;
  --text-tertiary: #909399;
  --text-highlight: #409eff;
  --border-color: #ebeef5;
  --card-bg: white;
  --card-hover-bg: #f5f7fa;
  --trend-up: #10b981;
  --trend-down: #ef4444;
  --shadow-light: 0 4px 12px rgba(0, 0, 0, 0.05);
  --shadow-medium: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.dark {
  --text-primary: #ffffff;
  --text-secondary: rgba(255, 255, 255, 0.7);
  --text-tertiary: rgba(255, 255, 255, 0.5);
  --text-highlight: #64748b;
  --border-color: rgba(255, 255, 255, 0.1);
  --card-bg: rgba(30, 41, 59, 0.8);
  --card-hover-bg: rgba(51, 65, 85, 0.8);
  --trend-up: #22c55e;
  --trend-down: #ef4444;
  --shadow-light: 0 4px 12px rgba(0, 0, 0, 0.15);
  --shadow-medium: 0 6px 16px rgba(0, 0, 0, 0.2);
}

.base-card {
  position: relative;
  border-radius: 16px;
  padding: 24px;
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
        rgba(99, 102, 241, 0.05) 0%,
        transparent 80%);
    opacity: 0;
    transition: opacity 0.3s ease;
    z-index: -1;
    pointer-events: none;
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-medium);
  }

  &:hover::before {
    opacity: 1;
  }

  .dark & {
    &::before {
      background: radial-gradient(600px circle at var(--mouse-x) var(--mouse-y),
          rgba(99, 102, 241, 0.08) 0%,
          transparent 80%);
    }
  }
}

.grades-dashboard {
  display: flex;
  flex-direction: column;
  transition: background-color 0.3s ease;
  gap: 30px;
  padding: 0 15px;
  --mouse-x: 0;
  --mouse-y: 0;

  .dark & {
    background-color: #1e293b;
  }
}


.modern-card {
  position: relative;
  border-radius: 16px;
  padding: 30px;
  transition: all 0.3s ease;
  overflow: hidden;
  z-index: 1;

  .card-content {
    position: relative;
    z-index: 2;
  }

  background: white;
  border: 1px solid rgba(0, 0, 0, 0.1);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.05);

  .dark & {
    background: rgba(30, 41, 59, 0.9);
    border-color: rgba(74, 85, 104, 0.4);
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
    border-color: rgba(199, 210, 254, 0.8);
  }

  .dark &:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.3);
    border-color: rgba(99, 102, 241, 0.5);
  }

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
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);

    &::before {
      opacity: 1;
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h3 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
      color: var(--text-primary);
    }

    .progress-indicator {
      background-color: rgba(64, 158, 255, 0.1);
      color: #409eff;
      padding: 6px 12px;
      border-radius: 999px;
      font-size: 14px;
    }
  }
}


.grades-stats {
  .stats-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 24px;

    .stats-card {
      @extend .base-card;
      background: rgba(255, 255, 255, 0.9);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(226, 232, 240, 0.7);
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.06);

      .dark & {
        background: rgba(30, 41, 59, 0.9);
        border-color: rgba(74, 85, 104, 0.4);
      }

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
        border-color: rgba(199, 210, 254, 0.8);
      }

      .dark &:hover {
        transform: translateY(-4px);
        box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.3);
        border-color: rgba(99, 102, 241, 0.5);
      }

      .stat-header {
        display: flex;
        flex-direction: column;
        margin-bottom: 16px;

        h3 {
          margin: 0;
          font-size: 16px;
          font-weight: 500;
          color: var(--text-secondary);
        }

        .stat-value {
          font-size: 32px;
          font-weight: 700;
          color: var(--text-primary);
          margin-top: 8px;
        }
      }

      .stat-trend {
        display: flex;
        align-items: center;
        font-size: 14px;

        .trend-up {
          color: var(--trend-up);
          display: flex;
          align-items: center;
        }

        .trend-down {
          color: var(--trend-down);
          display: flex;
          align-items: center;
        }

        .text-sm {
          font-size: 12px;
          color: var(--text-tertiary);
        }
      }

      .stat-progress {
        margin-top: 16px;
      }

      .stat-meta {
        margin-top: 8px;

        .text-sm {
          font-size: 14px;
          color: var(--text-tertiary);
        }
      }
    }
  }
}

/* 重新设计的成绩管理卡片样式 */
.grades-management {
  .management-controls {
    display: flex;
    flex-direction: column;
    gap: 16px;
    margin-bottom: 20px;
  }

  .filters-section {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
    padding: 16px;
    background: transparent;
    border-radius: 12px;
    border: 1px solid var(--border-color);

    .filter-group {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
    }

    .action-buttons {
      display: flex;
      gap: 10px;
    }

    .el-select {
      min-width: 180px;
      transition: all 0.2s ease;

      &:hover {
        .el-input__wrapper {
          border-color: #6366f1;
        }
      }
    }

    .el-button {
      transition: all 0.2s ease;

      &:hover {
        transform: translateY(-2px);
      }
    }
  }

  .search-section {
    display: flex;
    gap: 12px;
    align-items: center;

    .search-input {
      flex: 1;
      max-width: 320px;
      transition: all 0.2s ease;

      .el-input__wrapper {
        transition: all 0.2s ease;
      }

      &:hover .el-input__wrapper {
        border-color: #6366f1;
      }
    }

    .filter-select {
      width: 150px;
    }
  }

  .table-container {
    position: relative;
    border-radius: 12px;
    overflow: hidden;
    border: 1px solid var(--border-color);
    transition: all 0.3s ease;

    &:hover {
      border-color: rgba(99, 102, 241, 0.3);
    }
  }

  .grades-data-table {
    margin: 0;
    border-radius: 12px;
    border-collapse: separate;

    .el-table__header-wrapper {
      background-color: rgba(245, 247, 250, 0.8);

      .dark & {
        background-color: rgba(42, 53, 71, 0.8);
      }
    }

    .el-table__body tr {
      transition: all 0.2s ease;

      &:hover>td {
        background-color: rgba(99, 102, 241, 0.05);
      }
    }

    .el-table__body tr.el-table__row--current>td {
      background-color: rgba(99, 102, 241, 0.1);
    }

    .el-input-number {
      width: 100%;
    }
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    padding: 10px 0;
  }
}
</style>
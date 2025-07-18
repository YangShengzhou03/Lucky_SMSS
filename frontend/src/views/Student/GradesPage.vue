<template>
  <div class="grades-page">
    <!-- 成绩概览卡片 -->
    <div class="grades-overview">
      <div class="overview-header">
        <h2>成绩概览</h2>
        <div class="semester-selector">
          <el-select v-model="currentSemester" placeholder="选择学期" size="small">
            <el-option
              v-for="item in semesters"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
      </div>
      
      <div class="stats-container">
        <div class="stats-card gpa-card">
          <div class="card-header">
            <h3>平均绩点</h3>
            <div class="gpa-value">{{ gpa || '0.00' }}</div>
          </div>
          <div class="card-body">
            <div class="gpa-rank">
              <span>班级排名:</span>
              <span class="rank-value">{{ rank || '--' }}/{{ classSize || '--' }}</span>
            </div>
            <div class="gpa-percentage">
              <el-progress 
                :percentage="gpaPercentage" 
                :stroke-width="12"
                color="#409eff"
              />
              <div class="progress-text">超过班级 {{ gpaPercentage }}% 的同学</div>
            </div>
          </div>
        </div>
        
        <div class="stats-card score-card">
          <div class="card-header">
            <h3>平均分数</h3>
            <div class="score-value">{{ avgScore || '0' }}</div>
          </div>
          <div class="card-body">
            <div class="score-distribution">
              <div class="distribution-item" v-for="(item, index) in scoreDistribution" :key="index">
                <div class="distribution-bar" :style="{ height: `${item.percentage}%`, backgroundColor: item.color }"></div>
                <div class="distribution-label">{{ item.label }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="stats-card credit-card">
          <div class="card-header">
            <h3>学分完成情况</h3>
            <div class="credit-value">{{ completedCredits || '0' }}/{{ totalCredits || '0' }}</div>
          </div>
          <div class="card-body">
            <el-progress 
              :percentage="creditPercentage" 
              :stroke-width="12"
              color="#67c23a"
            />
            <div class="credit-text">已完成 {{ creditPercentage }}% 的毕业学分要求</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 详细成绩表格 -->
    <div class="grades-table">
      <div class="table-header">
        <h3>详细成绩</h3>
        <div class="search-and-filter">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索课程名称"
            prefix-icon="Search"
            size="small"
            class="search-input"
          />
          <el-select 
            v-model="filterType" 
            placeholder="筛选类型" 
            size="small"
            class="filter-select"
          >
            <el-option label="全部课程" value="all" />
            <el-option label="必修课" value="required" />
            <el-option label="选修课" value="elective" />
            <el-option label="实践课" value="practical" />
          </el-select>
        </div>
      </div>
      
      <el-table 
        :data="filteredGrades" 
        border 
        stripe
        class="grades-data-table"
      >
        <el-table-column prop="courseName" label="课程名称" min-width="200" />
        <el-table-column prop="courseCode" label="课程代码" width="120" />
        <el-table-column prop="courseType" label="课程类型" width="100" />
        <el-table-column prop="credits" label="学分" width="80" />
        <el-table-column prop="score" label="成绩" width="100" 
          :formatter="formatScore" />
        <el-table-column prop="gpa" label="绩点" width="80" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button 
              type="text" 
              size="small" 
              @click="viewDetails(scope.row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination" v-if="filteredGrades.length > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          :total="filteredGrades.length"
          layout="total, sizes, prev, pager, next"
          small
        />
      </div>
    </div>

    <!-- 成绩趋势与分析 -->
    <div class="grades-analysis">
      <div class="analysis-header">
        <h3>成绩趋势分析</h3>
        <div class="chart-type-selector">
          <el-radio-group v-model="chartType">
            <el-radio-button label="gpa">绩点趋势</el-radio-button>
            <el-radio-button label="score">分数趋势</el-radio-button>
            <el-radio-button label="credit">学分趋势</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      
      <div class="chart-container">
        <el-chart :option="chartOption" class="trend-chart" />
      </div>
    </div>

    <!-- 绩点计算器 -->
    <div class="gpa-calculator">
      <div class="calculator-header">
        <h3>绩点计算器</h3>
        <el-button 
          type="primary" 
          size="small" 
          @click="addCourse"
        >
          <el-icon><Plus /></el-icon> 添加课程
        </el-button>
      </div>
      
      <div class="calculator-content">
        <el-table 
          :data="calculatorCourses" 
          border 
          stripe
          class="calculator-table"
        >
          <el-table-column label="课程名称" width="200">
            <template #default="scope">
              <el-input 
                v-model="scope.row.name" 
                size="small"
                placeholder="输入课程名称"
              />
            </template>
          </el-table-column>
          <el-table-column label="学分" width="100">
            <template #default="scope">
              <el-input-number 
                v-model="scope.row.credits" 
                size="small"
                :min="1" 
                :max="5" 
                :step="0.5"
              />
            </template>
          </el-table-column>
          <el-table-column label="成绩" width="100">
            <template #default="scope">
              <el-input-number 
                v-model="scope.row.score" 
                size="small"
                :min="0" 
                :max="100" 
                :step="1"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <el-button 
                type="text" 
                size="small" 
                @click="removeCourse(scope.$index)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="calculator-result">
          <div class="result-label">计算结果:</div>
          <div class="result-value">预计绩点: <span class="gpa-result">{{ calculateGPA() || '0.00' }}</span></div>
          <el-button 
            type="primary" 
            size="small" 
            @click="calculateGPA"
          >
            计算绩点
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

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

// 搜索与筛选
const searchKeyword = ref('')
const filterType = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)

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

const scoreDistribution = ref([
  { label: '90-100', percentage: 25, color: '#67c23a' },
  { label: '80-89', percentage: 35, color: '#409eff' },
  { label: '70-79', percentage: 25, color: '#e6a23c' },
  { label: '60-69', percentage: 10, color: '#f56c6c' },
  { label: '60以下', percentage: 5, color: '#909399' }
])

// 图表数据
const chartType = ref('gpa')
const chartOption = computed(() => {
  const data = [
    { semester: '2022-2023-1', gpa: 3.2, score: 80, credits: 28 },
    { semester: '2022-2023-2', gpa: 3.5, score: 84, credits: 32 },
    { semester: '2023-2024-1', gpa: 3.4, score: 82, credits: 30 },
    { semester: '2023-2024-2', gpa: 3.6, score: 86, credits: 28 }
  ]
  
  let series = []
  let yAxis = { type: 'value' }
  let title = ''
  
  if (chartType.value === 'gpa') {
    series = [{
      data: data.map(item => item.gpa),
      type: 'line',
      smooth: true,
      lineStyle: { width: 3 },
      itemStyle: { radius: 6 },
      color: '#409eff'
    }]
    title = '学期绩点趋势'
    yAxis = { type: 'value', min: 0, max: 4.0, interval: 0.5 }
  } else if (chartType.value === 'score') {
    series = [{
      data: data.map(item => item.score),
      type: 'line',
      smooth: true,
      lineStyle: { width: 3 },
      itemStyle: { radius: 6 },
      color: '#67c23a'
    }]
    title = '学期平均分趋势'
    yAxis = { type: 'value', min: 0, max: 100, interval: 20 }
  } else {
    series = [{
      data: data.map(item => item.credits),
      type: 'line',
      smooth: true,
      lineStyle: { width: 3 },
      itemStyle: { radius: 6 },
      color: '#e6a23c'
    }]
    title = '学期获得学分'
    yAxis = { type: 'value', min: 0, max: 40, interval: 10 }
  }
  
  return {
    title: {
      text: title,
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'normal'
      }
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.semester)
    },
    yAxis: yAxis,
    series: series,
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    }
  }
})

// 绩点计算器
const calculatorCourses = ref([
  { name: '', credits: 3, score: 80 }
])

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
  // 实际应用中这里可以跳转到课程详情页
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
    // 简单的百分制转绩点计算
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
  
  return (totalPoints / totalCredits).toFixed(2)
}

// 初始化数据
onMounted(() => {
  // 模拟从API获取成绩数据
})
</script>

<style scoped lang="scss">
.grades-page {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

// 成绩概览卡片样式
.grades-overview {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.4);
  
  .overview-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #333;
    }
    
    .semester-selector {
      width: 220px;
    }
  }
  
  .stats-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
    
    .stats-card {
      background: rgba(245, 247, 250, 0.7);
      border-radius: 12px;
      padding: 20px;
      
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        
        h3 {
          margin: 0;
          font-size: 16px;
          font-weight: 500;
          color: #666;
        }
        
        .gpa-value, .score-value, .credit-value {
          font-size: 28px;
          font-weight: 700;
          color: #409eff;
        }
        
        .score-value {
          color: #67c23a;
        }
        
        .credit-value {
          color: #e6a23c;
        }
      }
      
      .card-body {
        .gpa-rank {
          display: flex;
          align-items: center;
          margin-bottom: 12px;
          
          span {
            font-size: 14px;
            color: #666;
          }
          
          .rank-value {
            margin-left: 8px;
            font-weight: 500;
            color: #333;
          }
        }
        
        .gpa-percentage, .credit-text {
          margin-top: 12px;
          font-size: 14px;
          color: #666;
          text-align: center;
        }
        
        .score-distribution {
          display: flex;
          justify-content: space-around;
          align-items: flex-end;
          height: 80px;
          
          .distribution-item {
            display: flex;
            flex-direction: column;
            align-items: center;
            
            .distribution-bar {
              width: 20px;
              border-radius: 4px 4px 0 0;
            }
            
            .distribution-label {
              margin-top: 8px;
              font-size: 12px;
              color: #666;
            }
          }
        }
      }
    }
  }
}

// 详细成绩表格样式
.grades-table {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.4);
  
  .table-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    
    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
    
    .search-and-filter {
      display: flex;
      gap: 12px;
      
      .search-input {
        width: 240px;
      }
      
      .filter-select {
        width: 150px;
      }
    }
  }
  
  .grades-data-table {
    width: 100%;
    margin-bottom: 16px;
  }
  
  .pagination {
    text-align: right;
  }
}

// 成绩趋势分析样式
.grades-analysis {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.4);
  
  .analysis-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    
    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
    
    .chart-type-selector {
      .el-radio-button__inner {
        padding: 8px 16px;
      }
    }
  }
  
  .chart-container {
    height: 300px;
  }
}

// 绩点计算器样式
.gpa-calculator {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.4);
  
  .calculator-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    
    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
  }
  
  .calculator-table {
    width: 100%;
    margin-bottom: 20px;
  }
  
  .calculator-result {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .result-label {
      font-size: 16px;
      font-weight: 500;
      color: #666;
    }
    
    .result-value {
      font-size: 16px;
      color: #333;
      
      .gpa-result {
        font-size: 20px;
        font-weight: 700;
        color: #409eff;
      }
    }
  }
}
</style>
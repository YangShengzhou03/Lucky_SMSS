<template>
  <div class="status-dashboard dark">
    <!-- 主要内容区域 -->
    <main class="dashboard-content">
      <!-- 学籍状态卡片 -->
      <div class="status-card modern-card">
        <div class="card-header">
          <h2>学籍状态概览</h2>
          <el-button type="primary" size="small" @click="handleRefresh" class="refresh-btn">
            <el-icon>
              <Refresh />
            </el-icon> 刷新数据
          </el-button>
        </div>

        <div class="status-main">
          <div class="status-indicator">
            <div class="status-badge">
              <el-tag :type="statusConfig[status].type" size="large" effect="dark" class="status-tag">
                {{ statusConfig[status].label }}
              </el-tag>
            </div>
            <div class="status-meta">
              <p class="meta-item">
                <el-icon class="meta-icon">
                  <Calendar />
                </el-icon>
                <span>状态生效日期: {{ formatDate(effectiveDate) }}</span>
              </p>
              <p class="meta-item">
                <el-icon class="meta-icon">
                  <Graduation />
                </el-icon>
                <span>预计毕业日期: {{ formatDate(graduationDate) }}</span>
              </p>
            </div>
          </div>

          <div class="status-stats">
            <div class="stat-card">
              <div class="stat-value counter">{{ credits }}</div>
              <div class="stat-label">已修学分</div>
              <div class="stat-progress">
                <el-progress :percentage="creditProgress" :stroke-width="6" :color="progressColor" />
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-value counter">{{ attendanceRate }}%</div>
              <div class="stat-label">出勤率</div>
              <div class="stat-progress">
                <el-progress :percentage="attendanceRate" :stroke-width="6" :color="attendanceColor" />
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ performanceLevel }}</div>
              <div class="stat-label">学业等级</div>
              <div class="stat-rating">
                <el-rate :value="levelToRating(performanceLevel)" disabled :show-text="false" :allow-half="true"
                  :colors="['#999', '#f7ba1e', '#f7ba1e', '#f7ba1e', '#10b981']" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 图表与数据区域 -->
      <div class="charts-container">
        <!-- 学分进度图表 -->
        <div class="chart-card modern-card">
          <div class="card-header">
            <h3>学分完成进度</h3>
            <div class="progress-indicator">
              <span class="progress-text">{{ creditProgress }}%</span>
            </div>
          </div>
          <div class="chart-wrapper">
            <div class="credit-chart">
              <div class="doughnut-chart">
                <canvas ref="creditDoughnutCanvas"></canvas>
              </div>
              <div class="credit-info">
                <div class="credit-detail">
                  <span class="detail-label">已修:</span>
                  <span class="detail-value">{{ credits }}/{{ totalCredits }} 学分</span>
                </div>
                <div class="credit-detail">
                  <span class="detail-label">剩余:</span>
                  <span class="detail-value">{{ totalCredits - credits }} 学分</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 学籍状态趋势图 -->
        <div class="chart-card modern-card">
          <div class="card-header">
            <h3>学业表现趋势</h3>
            <div class="chart-actions">
              <el-select v-model="trendTimeRange" size="small" @change="updateTrendChart">
                <el-option label="本学期" value="semester"></el-option>
                <el-option label="本学年" value="year"></el-option>
                <el-option label="全部" value="all"></el-option>
              </el-select>
            </div>
          </div>
          <div class="chart-wrapper">
            <div class="trend-chart">
              <canvas ref="performanceTrendCanvas"></canvas>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>

import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { Refresh, Calendar, Graduation } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Chart from 'chart.js/auto'

const creditDoughnutCanvas = ref(null)
const performanceTrendCanvas = ref(null)

// 状态配置映射
const statusConfig = {
  normal: { label: '正常', type: 'success', color: '#10b981' },
  suspended: { label: '休学', type: 'warning', color: '#f59e0b' },
  probation: { label: '试读', type: 'info', color: '#3b82f6' },
  graduated: { label: '已毕业', type: 'primary', color: '#6366f1' },
  dropped: { label: '退学', type: 'danger', color: '#ef4444' }
}

// 响应式数据
const isDarkMode = ref(true) // 强制使用暗色模式
const status = ref('normal')
const effectiveDate = ref('2023-09-01')
const graduationDate = ref('2027-06-30')
const credits = ref(68)
const totalCredits = ref(140)
const attendanceRate = ref(96)
const performanceLevel = ref('良好')
const trendTimeRange = ref('year')
let creditChart = null
let trendChart = null

// 计算属性
const creditProgress = computed(() => {
  return Math.round((credits.value / totalCredits.value) * 100)
})

const progressColor = computed(() => {
  const progress = creditProgress.value
  if (progress < 30) return '#ef4444'
  if (progress < 60) return '#f59e0b'
  return '#10b981'
})

const attendanceColor = computed(() => {
  const rate = attendanceRate.value
  if (rate < 70) return '#ef4444'
  if (rate < 90) return '#f59e0b'
  return '#10b981'
})

// 将学业等级转换为评分
const levelToRating = (level) => {
  const levelMap = {
    '优秀': 5,
    '良好': 4,
    '中等': 3,
    '及格': 2,
    '不及格': 1
  }
  return levelMap[level] || 3
}

// 方法
const formatDate = (dateStr) => {
  if (!dateStr) return '--'
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).replace(/\//g, '-')
}

const handleRefresh = () => {
  // 模拟数据刷新
  const loading = ElMessage.loading({
    message: '正在刷新数据...',
    duration: 0
  })

  setTimeout(() => {
    // 随机更新部分数据
    credits.value = Math.min(credits.value + Math.floor(Math.random() * 5), totalCredits.value)
    attendanceRate.value = Math.min(attendanceRate.value + Math.floor(Math.random() * 3), 100)

    loading.close()
    ElMessage.success('数据刷新成功')

    // 更新图表
    updateCharts()
  }, 800)
}

const updateTrendChart = () => {
  if (!trendChart) return

  // 根据时间范围更新图表数据
  let labels, data
  if (trendTimeRange.value === 'semester') {
    labels = ['第1周', '第2周', '第3周', '第4周', '第5周', '第6周', '当前']
    data = [85, 88, 90, 89, 92, 94, attendanceRate.value]
  } else if (trendTimeRange.value === 'year') {
    labels = ['大一上', '大一下', '大二上', '大二下', '大三上', '大三下', '当前']
    data = [80, 85, 88, 90, 92, 94, attendanceRate.value]
  } else {
    labels = ['大一上', '大一下', '大二上', '大二下', '大三上', '大三下', '大四上', '大四下', '当前']
    data = [75, 80, 85, 88, 90, 92, 94, 95, attendanceRate.value]
  }

  trendChart.data.labels = labels
  trendChart.data.datasets[0].data = data
  trendChart.update()
}

const updateCharts = () => {
  // 更新学分环形图
  if (creditChart) {
    creditChart.data.datasets[0].data = [credits.value, totalCredits.value - credits.value]
    creditChart.update()
  }

  // 更新趋势图
  updateTrendChart()
}

// 初始化图表
const initCharts = () => {
  nextTick(() => {
    // 学分环形图
    const creditCtx = creditDoughnutCanvas.value.getContext('2d')
    creditChart = new Chart(creditCtx, {
      type: 'doughnut',
      data: {
        labels: ['已修', '剩余'],
        datasets: [{
          data: [credits.value, totalCredits.value - credits.value],
          backgroundColor: [
            '#409eff',
            'rgba(255, 255, 255, 0.1)'
          ],
          borderWidth: 0,
          cutout: '70%'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          },
          tooltip: {
            callbacks: {
              label: function (context) {
                const label = context.label || ''
                const value = context.raw || 0
                const total = context.dataset.data.reduce((a, b) => a + b, 0)
                const percentage = Math.round((value / total) * 100)
                return `${label}: ${value} 学分 (${percentage}%)`
              }
            }
          }
        },
        animation: {
          animateScale: true,
          animateRotate: true
        }
      }
    })

    // 学业表现趋势图
    const trendCtx = performanceTrendCanvas.value.getContext('2d')
    trendChart = new Chart(trendCtx, {
      type: 'line',
      data: {
        labels: ['大一上', '大一下', '大二上', '大二下', '大三上', '大三下', '当前'],
        datasets: [{
          label: '学业表现',
          data: [80, 85, 88, 90, 92, 94, attendanceRate.value],
          borderColor: '#409eff',
          backgroundColor: 'rgba(64, 158, 255, 0.1)',
          tension: 0.4,
          fill: true,
          pointBackgroundColor: '#fff',
          pointBorderColor: '#409eff',
          pointBorderWidth: 2,
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
            min: 50,
            max: 100,
            ticks: {
              stepSize: 10
            },
            grid: {
              color: 'rgba(255, 255, 255, 0.1)'
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
          },
          tooltip: {
            mode: 'index',
            intersect: false
          }
        },
        interaction: {
          mode: 'nearest',
          axis: 'x',
          intersect: false
        }
      }
    })
  })
}

// 监听暗色模式变化
watch(isDarkMode, (newVal) => {
  if (creditChart) {
    creditChart.data.datasets[0].backgroundColor[1] = newVal
      ? 'rgba(255, 255, 255, 0.1)'
      : 'rgba(0, 0, 0, 0.05)'
    creditChart.update()
  }

  if (trendChart) {
    trendChart.data.datasets[0].backgroundColor = newVal
      ? 'rgba(64, 158, 255, 0.1)'
      : 'rgba(64, 158, 255, 0.1)'

    trendChart.options.scales.y.grid.color = newVal
      ? 'rgba(255, 255, 255, 0.1)'
      : 'rgba(0, 0, 0, 0.05)'

    trendChart.update()
  }
})

// 初始化数据
onMounted(() => {
  // 强制使用暗色模式
  isDarkMode.value = true
  document.documentElement.classList.add('dark')

  // 初始化图表
  initCharts()

  // 初始化数字计数器动画
  const counters = document.querySelectorAll('.counter')
  counters.forEach(counter => {
    const target = parseInt(counter.innerText)
    counter.innerText = '0'

    const updateCounter = () => {
      const current = parseInt(counter.innerText)
      const increment = Math.ceil(target / 20)

      if (current < target) {
        counter.innerText = current + increment
        setTimeout(updateCounter, 50)
      } else {
        counter.innerText = target
      }
    }

    updateCounter()
  })

  // 添加鼠标移动效果
  const cardElements = document.querySelectorAll('.modern-card')
  cardElements.forEach(card => {
    card.addEventListener('mousemove', (e) => {
      const rect = card.getBoundingClientRect()
      const x = e.clientX - rect.left
      const y = e.clientY - rect.top

      card.style.setProperty('--mouse-x', `${x}px`)
      card.style.setProperty('--mouse-y', `${y}px`)
    })
  })
})

// 清理
onUnmounted(() => {
  if (creditChart) creditChart.destroy()
  if (trendChart) trendChart.destroy()
})
</script>

<style scoped lang="scss">
// 仪表盘整体样式
.status-dashboard {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  transition: background-color 0.3s ease;
  background: transparent; // 深色背景
}

// 主内容区域
.dashboard-content {
  flex: 1;
  width: 100%; // 增加最大宽度
  margin: 0 auto;
  padding: 30px; // 增加内边距
  display: grid;
  grid-template-columns: 1fr; // 单列布局
  gap: 30px; // 增加卡片间距
}

// 现代化卡片样式
.modern-card {
  position: relative;
  border-radius: 16px;
  padding: 30px; // 增加内边距
  transition: all 0.3s ease;
  overflow: hidden;
  z-index: 1;
  width: 100%; // 确保卡片宽度为100%

  // 深色背景
  background: rgba(30, 41, 59, 0.8);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);

  // 卡片光影效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(600px circle at var(--mouse-x) var(--mouse-y),
        rgba(64, 158, 255, 0.1) 0%,
        transparent 80%);
    opacity: 0;
    transition: opacity 0.3s ease;
    z-index: -1;
    pointer-events: none;
  }

  // 卡片悬停效果
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.25);

    &::before {
      opacity: 1;
    }
  }

  // 卡片头部
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px; // 增加底部间距

    h2,
    h3 {
      margin: 0;
      font-size: 20px; // 增加字体大小
      font-weight: 600;
      color: #fff; // 文字颜色改为白色
    }

    .progress-indicator {
      background-color: rgba(64, 158, 255, 0.2);
      color: #69b1ff;
      padding: 6px 12px; // 增加内边距
      border-radius: 999px;
      font-size: 14px; // 增加字体大小
    }

    .chart-actions {
      display: flex;
      gap: 12px; // 增加间距
    }
  }
}

// 学籍状态卡片
.status-card {
  .card-header {
    .refresh-btn {
      display: flex;
      align-items: center;
      transition: all 0.3s ease;
    }
  }

  .status-main {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 30px; // 增加间距

    @media (width: 100%) {
      flex-direction: column;
      align-items: flex-start;
    }
  }

  .status-indicator {
    .status-badge {
      margin-bottom: 16px; // 增加底部间距

      .status-tag {
        font-size: 18px; // 增加字体大小
        padding: 8px 20px; // 增加内边距
        border-radius: 999px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      }
    }

    .status-meta {
      .meta-item {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 8px; // 增加底部间距
        color: rgba(255, 255, 255, 0.7); // 文字颜色改为白色
        font-size: 16px; // 增加字体大小

        .meta-icon {
          color: #69b1ff;
          font-size: 18px; // 增加图标大小
        }
      }
    }
  }

  .status-stats {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px; // 增加间距
    width: 100%;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }

    .stat-card {
      background-color: rgba(255, 255, 255, 0.05);
      border-radius: 12px; // 增加圆角
      padding: 20px; // 增加内边距
      text-align: center;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;

      // 卡片悬停效果
      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
      }

      // 卡片背景装饰
      &::before {
        content: '';
        position: absolute;
        top: -50%;
        left: -50%;
        width: 200%;
        height: 200%;
        background: radial-gradient(circle at center, rgba(64, 158, 255, 0.1) 0%, transparent 70%);
        transform: scale(0);
        opacity: 0;
        transition: transform 0.5s ease, opacity 0.5s ease;
        z-index: -1;
      }

      &:hover::before {
        transform: scale(1);
        opacity: 1;
      }

      // 统计值样式
      .stat-value {
        font-size: 32px; // 增加字体大小
        font-weight: 700;
        color: #fff; // 文字颜色改为白色
        margin-bottom: 8px; // 增加底部间距
        position: relative;
        z-index: 1;

        // 数字计数器样式
        &.counter {
          display: block;
          transition: all 0.2s ease;
        }
      }

      // 统计标签样式
      .stat-label {
        font-size: 15px; // 增加字体大小
        color: rgba(255, 255, 255, 0.7); // 文字颜色改为白色
        margin-bottom: 12px; // 增加底部间距
        position: relative;
        z-index: 1;
      }

      // 进度条样式
      .stat-progress {
        margin-top: 12px; // 增加顶部间距
        position: relative;
        z-index: 1;
      }

      // 评分样式
      .stat-rating {
        margin-top: 12px; // 增加顶部间距
        position: relative;
        z-index: 1;
      }
    }
  }
}

// 图表卡片
.chart-card {
  .chart-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 300px; // 增加高度
    position: relative;
  }

  .credit-chart {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 30px; // 增加间距
    width: 100%;

    .doughnut-chart {
      width: 200px; // 增加宽度
      height: 200px; // 增加高度
    }

    .credit-info {
      display: flex;
      flex-direction: column;
      gap: 12px; // 增加间距

      .credit-detail {
        display: flex;
        align-items: center;
        gap: 12px; // 增加间距

        .detail-label {
          color: rgba(255, 255, 255, 0.7); // 文字颜色改为白色
          font-size: 15px; // 增加字体大小
        }

        .detail-value {
          color: #fff; // 文字颜色改为白色
          font-size: 18px; // 增加字体大小
          font-weight: 500;
        }
      }
    }
  }
}
</style>
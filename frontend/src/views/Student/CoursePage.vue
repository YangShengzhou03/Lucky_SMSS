<template>
  <div class="course-selection-container" @mousemove="handleMouseMove">
    <!-- 课程概览卡片 -->
    <div class="course-overview-card modern-card" :class="{ 'panel-hover': isHoveringOverview }">
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

    <!-- 选课指南风琴面板 -->
    <div class="selection-guide modern-card" :class="{ 'panel-hover': isHoveringGuide }">
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
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import {
  DataAnalysis,
  Collection,
  Check,
  CaretTop,
  CaretBottom,
  Calendar,
  CreditCard  // 添加缺失的CreditCard图标导入
} from '@element-plus/icons-vue'

// 学期数据
const currentSemester = ref('2023-2024-2')
const semesters = ref([
  { value: '2023-2024-2', label: '2023-2024学年第二学期' },
  { value: '2023-2024-1', label: '2023-2024学年第一学期' }
])

// 选课指南
const activeGuide = ref(['guide'])

// 悬停状态
const isHoveringOverview = ref(false)
const isHoveringGuide = ref(false)
const hoveredStat = ref(null)

// 配置参数
const maxCredits = 25
const selectionDeadline = '2023年7月30日'

// 统计卡片数据
const stats = computed(() => [
  {
    value: 8,
    label: '可选课程',
    icon: Collection,
    color: '#6366f1',
    trend: 5.2
  },
  {
    value: 3,
    label: '已选课程',
    icon: Check,
    color: '#10b981',
    trend: 12.8
  },
  {
    value: '12/25',
    label: '已选学分',
    icon: CreditCard,
    color: '#f59e0b',
    trend: -3.5
  },
  {
    value: '7天',
    label: '选课截止',
    icon: Calendar,
    color: '#ef4444',
    trend: 2.1
  }
])

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
</script>

<style scoped lang="scss">
.course-selection-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  --mouse-x: 0;
  --mouse-y: 0;
}

.modern-card {
  position: relative;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 24px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(226, 232, 240, 0.6);
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
}

.modern-select {
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

// 悬停效果类
.panel-hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.12);
  border-color: rgba(64, 158, 255, 0.3);
}

@media (max-width: 768px) {
  .course-selection-container {
    padding: 15px;
  }

  .modern-card {
    padding: 20px;
  }

  .panel-header h3 {
    font-size: 18px;
  }
}
</style>
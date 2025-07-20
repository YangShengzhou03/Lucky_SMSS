<template>
  <div class="personal-center-page" @mousemove="handleMouseMove">
    <!-- 个人中心内容区域 -->
    <div class="personal-center-content">
      <!-- 个人信息卡片 -->
      <div class="personal-info-card modern-card" :class="{ 'panel-hover': isHoveringPanel }">
        <div class="card-header">
          <div class="avatar-section">
            <el-upload class="avatar-uploader" action="#" :show-file-list="false" :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload">
              <img v-if="userProfile.avatarUrl" :src="userProfile.avatarUrl" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon">
                <Plus />
              </el-icon>
            </el-upload>
            <div class="user-info">
              <h2 class="username">{{ userProfile.name }}</h2>
              <p class="student-id">{{ userProfile.studentId }}</p>
            </div>
          </div>
          <el-button type="primary" size="small" @click="editProfile" class="edit-button">
            <el-icon>
              <Edit />
            </el-icon> 编辑信息
          </el-button>
        </div>

        <div class="card-body">
          <div class="info-row">
            <div class="info-item">
              <el-icon class="item-icon">
                <User />
              </el-icon>
              <div class="item-content">
                <p class="item-label">性别</p>
                <p class="item-value">{{ getGenderText(userProfile.gender) }}</p>
              </div>
            </div>
            <div class="info-item">
              <el-icon class="item-icon">
                <Calendar />
              </el-icon>
              <div class="item-content">
                <p class="item-label">出生日期</p>
                <p class="item-value">{{ userProfile.birthdate }}</p>
              </div>
            </div>
          </div>

          <div class="info-row">
            <div class="info-item">
              <el-icon class="item-icon">
                <Notification />
              </el-icon>
              <div class="item-content">
                <p class="item-label">邮箱</p>
                <p class="item-value">{{ userProfile.email }}</p>
              </div>
            </div>
            <div class="info-item">
              <el-icon class="item-icon">
                <Phone />
              </el-icon>
              <div class="item-content">
                <p class="item-label">手机号码</p>
                <p class="item-value">{{ userProfile.phone }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷操作卡片 -->
      <div class="quick-actions-card modern-card">
        <div class="card-title">
          <el-icon class="title-icon">
            <Setting />
          </el-icon>
          <span>账户设置</span>
        </div>
        <div class="actions-grid">
          <el-button type="text" @click="changePassword" class="action-button">
            <div class="action-content">
              <el-icon class="action-icon">
                <Lock />
              </el-icon>
              <span>修改密码</span>
            </div>
            <el-icon class="arrow-icon">
              <ArrowRight />
            </el-icon>
          </el-button>
          <el-button type="text" @click="notificationSettings" class="action-button">
            <div class="action-content">
              <el-icon class="action-icon">
                <Bell />
              </el-icon>
              <span>通知设置</span>
            </div>
            <el-icon class="arrow-icon">
              <ArrowRight />
            </el-icon>
          </el-button>
          <el-button type="text" @click="securitySettings" class="action-button">
            <div class="action-content">
              <el-icon class="action-icon">
                <FirstAidKit />
              </el-icon>
              <span>安全设置</span>
            </div>
            <el-icon class="arrow-icon">
              <ArrowRight />
            </el-icon>
          </el-button>
          <el-button type="text" @click="helpCenter" class="action-button">
            <div class="action-content">
              <el-icon class="action-icon">
                <QuestionFilled />
              </el-icon>
              <span>帮助中心</span>
            </div>
            <el-icon class="arrow-icon">
              <ArrowRight />
            </el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import {
  Plus, User, Edit, ArrowRight, Lock,
  Calendar, Notification, Phone, Setting, Bell,
  FirstAidKit, QuestionFilled
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 个人信息
const userProfile = reactive({
  studentId: '20230001',
  name: '张三',
  gender: 'male',
  birthdate: '2000-01-15',
  email: 'zhangsan@example.com',
  phone: '13800138000',
  avatarUrl: 'https://picsum.photos/id/1005/200/200'
})

// 卡片悬停状态
const isHoveringPanel = ref(false)

// 鼠标位置
const handleMouseMove = (e) => {
  document.documentElement.style.setProperty('--mouse-x', `${e.clientX}px`)
  document.documentElement.style.setProperty('--mouse-y', `${e.clientY}px`)
}

// 获取性别的文本表示
const getGenderText = (gender) => {
  const genderMap = {
    'male': '男',
    'female': '女',
    'other': '其他'
  }
  return genderMap[gender] || '未知'
}

// 方法
const editProfile = () => ElMessage.info('编辑个人信息功能将在未来版本中实现')
const changePassword = () => ElMessage.info('修改密码功能将在未来版本中实现')
const notificationSettings = () => ElMessage.info('通知设置功能将在未来版本中实现')
const securitySettings = () => ElMessage.info('安全设置功能将在未来版本中实现')
const helpCenter = () => ElMessage.info('帮助中心功能将在未来版本中实现')

// 头像上传相关方法
const handleAvatarSuccess = (res, file) => {
  userProfile.avatarUrl = URL.createObjectURL(file.raw)
  ElMessage.success('头像更新成功')
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) ElMessage.error('上传头像图片只能是 JPG 或 PNG 格式!')
  if (!isLt2M) ElMessage.error('上传头像图片大小不能超过 2MB!')
  return isJPG && isLt2M
}
</script>

<style scoped lang="scss">
// 定义深色和浅色主题变量
:root {
  --bg-color: #ffffff;
  --card-bg: rgba(255, 255, 255, 0.95);
  // 增强基础边框，使其在默认状态下更清晰
  --card-border: 1px solid rgba(226, 232, 240, 0.7);
  --card-border-hover: 1px solid rgba(199, 210, 254, 0.8);
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --text-tertiary: #94a3b8;
  --accent-color: #6366f1;
  --hover-bg: #f1f5f9;
  --item-bg: #f8fafc;
  // 增强基础阴影，使卡片更突出
  --shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  --shadow-hover: 0 12px 40px rgba(0, 0, 0, 0.12);
  --avatar-border: 3px solid #fff;
  --uploader-bg: #f8fafc;
  --uploader-border: 2px dashed #d9d9d9;
}

// 深色主题变量
@media (prefers-color-scheme: dark) {
  :root {
    --bg-color: #1a1c23;
    --card-bg: rgba(30, 35, 45, 0.95);
    // 深色模式下增强边框可见性
    --card-border: 1px solid rgba(74, 85, 104, 0.5);
    --card-border-hover: 1px solid rgba(199, 210, 254, 0.8);
    --text-primary: #f1f5f9;
    --text-secondary: #94a3b8;
    --text-tertiary: #94a3b8;
    --accent-color: #8b5cf6;
    --hover-bg: #2d313a;
    --item-bg: #252932;
    // 深色模式下的阴影调整
    --shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    --shadow-hover: 0 12px 40px rgba(0, 0, 0, 0.25);
    --avatar-border: 3px solid #252932;
    --uploader-bg: #252932;
    --uploader-border: 2px dashed #3a3f4b;
  }
}

.personal-center-page {
  background: var(--bg-color);
  --mouse-x: 0;
  --mouse-y: 0;
  min-height: 100vh;
}

.personal-center-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;

  .personal-info-card,
  .quick-actions-card {
    margin-bottom: 24px;
  }
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

/* 以下样式保持不变 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .avatar-section {
    display: flex;
    align-items: center;
    gap: 20px;

    .user-info {
      .username {
        margin: 0;
        font-size: 1.5rem;
        font-weight: 600;
        color: var(--text-primary);
      }

      .student-id {
        margin: 4px 0 0;
        font-size: 0.9rem;
        color: var(--text-secondary);
      }
    }
  }

  .edit-button {
    padding: 8px 16px;
    font-weight: 500;

    .el-icon {
      margin-right: 6px;
    }
  }
}

.avatar-uploader {
  .avatar {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    object-fit: cover;
    border: var(--avatar-border);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    cursor: pointer;

    &:hover {
      transform: scale(1.05);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
    }
  }

  .avatar-uploader-icon {
    width: 100px;
    height: 100px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: var(--uploader-border);
    border-radius: 50%;
    background-color: var(--uploader-bg);
    color: var(--text-tertiary);
    font-size: 24px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      border-color: var(--accent-color);
      background-color: #f0f2ff;
      color: var(--accent-color);
    }
  }
}

.card-body {
  .info-row {
    display: flex;
    gap: 16px;
    margin-bottom: 16px;

    @media (max-width: 768px) {
      flex-direction: column;
      gap: 12px;
    }

    .info-item {
      flex: 1;
      display: flex;
      align-items: center;
      padding: 16px;
      border-radius: 12px;
      background: var(--item-bg);
      transition: all 0.2s ease;

      &:hover {
        background: var(--hover-bg);
        transform: translateY(-2px);
      }

      .item-icon {
        font-size: 20px;
        color: var(--accent-color);
        margin-right: 16px;
      }

      .item-content {
        .item-label {
          font-size: 0.85rem;
          color: var(--text-secondary);
          margin-bottom: 4px;
        }

        .item-value {
          font-size: 1rem;
          font-weight: 500;
          color: var(--text-primary);
        }
      }
    }
  }
}

.quick-actions-card {
  .card-title {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    font-size: 1.25rem;
    font-weight: 600;
    color: var(--text-primary);

    .title-icon {
      margin-right: 12px;
      color: var(--accent-color);
      font-size: 1.2em;
    }
  }

  .actions-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }

  .action-button {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 16px;
    border-radius: 12px;
    transition: all 0.2s ease;
    color: var(--text-primary);

    &:hover {
      background: var(--hover-bg);
      color: var(--accent-color);
    }

    .action-content {
      display: flex;
      align-items: center;

      .action-icon {
        margin-right: 12px;
        color: var(--accent-color);
        font-size: 1.1em;
      }

      span {
        font-weight: 500;
      }
    }

    .arrow-icon {
      color: var(--text-tertiary);
    }
  }
}
</style>
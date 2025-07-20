<template>
  <div class="settings-page" @mousemove="handleMouseMove">
    <!-- 设置内容区域 -->
    <div class="settings-content">
      <!-- 个人信息设置 -->
      <div class="setting-panel modern-card" :class="{'panel-hover': isHoveringPanel}">
        <div class="panel-header">
          <h3><el-icon><User /></el-icon> 个人信息</h3>
          <div class="action-buttons">
            <el-button 
              type="primary" 
              size="small" 
              @click="saveProfile"
            >
              <el-icon><Check /></el-icon> 保存修改
            </el-button>
          </div>
        </div>
        
        <el-form :model="userProfile" label-width="120px" class="profile-form">
          <el-form-item label="学号">
            <el-input v-model="userProfile.studentId" disabled />
          </el-form-item>
          
          <el-form-item label="姓名">
            <el-input v-model="userProfile.name" />
          </el-form-item>
          
          <el-form-item label="性别">
            <el-radio-group v-model="userProfile.gender">
              <el-radio label="male">男</el-radio>
              <el-radio label="female">女</el-radio>
              <el-radio label="other">其他</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="出生日期">
            <el-date-picker
              v-model="userProfile.birthdate"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          
          <el-form-item label="邮箱">
            <el-input v-model="userProfile.email" />
          </el-form-item>
          
          <el-form-item label="手机号码">
            <el-input v-model="userProfile.phone" />
          </el-form-item>
          
          <el-form-item label="头像">
            <el-upload
              class="avatar-uploader"
              action="#"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="userProfile.avatarUrl" :src="userProfile.avatarUrl" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon">
                <Plus />
              </el-icon>
            </el-upload>
          </el-form-item>
        </el-form>
      </div>

      <!-- 密码修改设置 -->
      <div class="setting-panel modern-card" :class="{'panel-hover': isHoveringPassword}">
        <div class="panel-header">
          <h3><el-icon><Lock /></el-icon> 密码修改</h3>
        </div>
        
        <el-form 
          :model="passwordForm" 
          label-width="150px" 
          class="password-form"
          :rules="passwordRules"
          ref="passwordFormRef"
        >
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input 
              type="password" 
              v-model="passwordForm.oldPassword" 
              autocomplete="off"
            />
          </el-form-item>
          
          <el-form-item label="新密码" prop="newPassword">
            <el-input 
              type="password" 
              v-model="passwordForm.newPassword" 
              autocomplete="off"
            />
          </el-form-item>
          
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input 
              type="password" 
              v-model="passwordForm.confirmPassword" 
              autocomplete="off"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="changePassword"
            >
              <el-icon><RefreshRight /></el-icon> 修改密码
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { 
  Plus, User, Lock, Check, RefreshRight
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

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 卡片悬停状态
const isHoveringPanel = ref(false)
const isHoveringPassword = ref(false)

// 鼠标位置
const handleMouseMove = (e) => {
  document.documentElement.style.setProperty('--mouse-x', `${e.clientX}px`)
  document.documentElement.style.setProperty('--mouse-y', `${e.clientY}px`)
}

// 密码验证规则
const passwordRules = reactive({
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { 
      validator: (rule, value) => {
        if (value !== passwordForm.newPassword) {
          return Promise.reject('两次输入的密码不一致')
        } else {
          return Promise.resolve()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 方法
// 个人信息相关方法
const saveProfile = () => {
  // 模拟保存个人信息
  ElMessage.success('个人信息保存成功')
}

const handleAvatarSuccess = (res, file) => {
  userProfile.avatarUrl = URL.createObjectURL(file.raw)
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG 或 PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 密码修改相关方法
const changePassword = () => {
  // 模拟修改密码
  ElMessage.success('密码修改成功，请使用新密码登录')
}
</script>

<style scoped lang="scss">
.settings-page {
  display: flex;
  min-height: calc(100vh - 60px);
  padding: 20px;
  background: transparent;
  --mouse-x: 0;
  --mouse-y: 0;
}

.settings-content {
  flex: 1;
  max-width: 100%;
  
  .setting-panel {
    margin-bottom: 24px;
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

    .action-buttons {
      display: flex;
      gap: 10px;
    }
  }
}

.profile-form, .password-form {
  margin-top: 24px;
  
  .el-form-item {
    margin-bottom: 18px;
  }
  
  .el-input, .el-date-picker, .el-radio-group {
    transition: all 0.2s ease;
    
    &:focus-within {
      transform: translateY(-1px);
    }
  }
}

.avatar-uploader {
  .avatar {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    object-fit: cover;
    cursor: pointer;
    border: 3px solid #fff;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    
    &:hover {
      transform: scale(1.05);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
    }
  }
  
  .avatar-uploader-icon {
    width: 120px;
    height: 120px;
    line-height: 120px;
    text-align: center;
    border: 2px dashed #d9d9d9;
    border-radius: 50%;
    background-color: #fafafa;
    color: #8c8c8c;
    font-size: 28px;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      border-color: #409eff;
      background-color: #f5faff;
      color: #409eff;
      transform: scale(1.03);
    }
  }
}

// 悬停效果类
.panel-hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.12);
  border-color: rgba(64, 158, 255, 0.3);
}

@media (max-width: 768px) {
  .settings-content {
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
    
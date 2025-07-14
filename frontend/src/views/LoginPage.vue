<template>
  <div class="login-page">
    <!-- 背景粒子动画 -->
    <div class="particles" id="login-particles"></div>
    
    <!-- 登录容器 -->
    <div class="login-container">
      
      <!-- 登录卡片 -->
      <div class="login-card">
        <!-- 直角切角二维码入口 -->
        <div 
          class="qr-corner"
          @click="toggleQRCode"
          :class="{ active: showQRCode }"
        >
          <svg viewBox="0 0 1024 1024" width="24" height="24">
            <path d="M999.131429 930.03581V999.619048h-174.031239v-69.583238h174.05562z m-313.295239-208.944762v208.944762h-28.769523l-40.813715-40.716191v-168.228571h69.558858z m139.264 139.264v69.680762h-69.583238v-69.680762h69.583238z m174.05562-139.166477v69.583239h-34.79162v69.583238H894.780952v-69.583238h34.791619V721.188571h69.583239z m-452.608 69.583239v29.110857l-29.086477-29.110857h29.086477z m278.55238-69.680762v69.558857h-69.583238v-69.558857h69.583238zM546.54781 616.594286v104.496762h-69.583239V616.594286h69.583239z m208.944761 0v69.583238H685.83619V616.594286h69.705143z m243.663239 0v69.583238h-174.08V616.594286h174.08z m-696.246858-40.17981l-29.159619-29.379047h29.159619v-69.583239h174.08v-104.496761h69.558858v104.496761h139.264v139.142096h-69.558858v-69.558857H302.908952v29.379047z m696.246858-98.986666v69.607619h-69.583239v-69.583239h69.583239z m-104.350477 0v69.607619h-139.288381v-69.583239H894.780952z m-661.455238 0v28.281904l-28.842666-28.281904H233.325714zM407.28381 24.868571v383.024762H163.523048v29.622857L24.380952 297.447619V24.868571h382.902858z m591.969523 0v383.024762h-383.024762V24.868571h383.024762zM337.700571 94.427429H93.96419v243.760761h243.736381V94.427429z m591.872 0h-243.760761v243.760761h243.760761V94.427429z m-383.024761 139.264v69.583238h-69.583239V233.691429h69.583239z m-278.430477-69.680762v104.472381H163.669333V164.010667h104.496762z m591.872 0v104.472381h-104.472381V164.010667h104.472381z m-313.441523-104.350477v104.472381h-69.583239V59.63581h69.583239z" fill="currentColor"></path>
          </svg>
        </div>

        <!-- 表单内容 -->
        <transition name="fade">
          <div class="form-content" v-if="!showQRCode">
            <div class="card-header">
              <h2 class="welcome-text">欢迎回来</h2>
              <p class="login-subtitle">登录Lucky SMS学生管理系统</p>
            </div>
            
            <el-form 
              ref="loginFormRef" 
              :model="loginForm" 
              :rules="loginRules" 
              class="login-form"
              @keyup.enter="handleLogin"
            >
              <!-- 用户名输入 -->
              <el-form-item prop="username" class="form-item">
                <el-input 
                  v-model="loginForm.username" 
                  placeholder="输入用户名" 
                  size="large"
                  :prefix-icon="User"
                  clearable
                  class="custom-input"
                />
              </el-form-item>
              
              <!-- 密码输入 -->
              <el-form-item prop="password" class="form-item">
                <el-input 
                  v-model="loginForm.password" 
                  placeholder="输入密码" 
                  size="large"
                  :prefix-icon="Lock"
                  clearable
                  show-password
                  class="custom-input"
                />
              </el-form-item>
              
              <!-- 记住我 & 忘记密码 -->
              <div class="login-options">
                <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
                <el-link 
                  type="primary" 
                  :underline="false"
                  class="forgot-password"
                >
                  忘记密码?
                </el-link>
              </div>
              
              <!-- 登录按钮 -->
              <el-button 
                type="primary" 
                size="large" 
                class="login-btn" 
                @click="handleLogin"
                :loading="loginLoading"
              >
                <span v-if="!loginLoading">登录</span>
                <span v-else>登录中...</span>
              </el-button>
              
              <!-- 没有账号 -->
              <div class="register-link">
                没有账号?
                <el-link 
                  type="primary" 
                  @click="$router.push('/register')"
                  :underline="false"
                  class="register-text"
                >
                  立即注册
                </el-link>
              </div>
            </el-form>
          </div>
        </transition>

        <!-- 二维码内容 -->
        <transition name="fade">
          <div class="qr-content" v-if="showQRCode">
            <div class="qr-card">
              <div class="qr-code-placeholder">
                <!-- 这里放实际二维码图片 -->
                <img src="@/assets/images/login-qr-code.png" alt="扫码登录" class="qr-image">
              </div>
              <h3>扫码登录</h3>
              <p>使用Lucky SMS APP扫描二维码</p>
              <el-button 
                type="text" 
                class="back-btn"
                @click="toggleQRCode"
              >
                <el-icon><ArrowLeft /></el-icon>
                返回账号登录
              </el-button>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { 
  User, Lock, ArrowLeft 
} from '@element-plus/icons-vue';

const showQRCode = ref(false);
const loginLoading = ref(false);

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
});

const loginRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ]
});

const toggleQRCode = () => {
  showQRCode.value = !showQRCode.value;
};

const handleLogin = async () => {
  loginLoading.value = true;
  try {
    // 模拟登录请求
    await new Promise(resolve => setTimeout(resolve, 1000));
    ElMessage.success('登录成功！');
  } catch (error) {
    ElMessage.error('登录失败: ' + (error.message || '用户名或密码错误'));
  } finally {
    loginLoading.value = false;
  }
};

// 初始化粒子动画
onMounted(() => {
  if (window.particlesJS) {
    window.particlesJS('login-particles', {
      particles: {
        number: { value: 60, density: { enable: true, value_area: 800 } },
        color: { value: "#3b82f6" },
        shape: { type: "circle" },
        opacity: { value: 0.5, random: true },
        size: { value: 3, random: true },
        line_linked: { 
          enable: true, 
          distance: 150, 
          color: "#3b82f6", 
          opacity: 0.3, 
          width: 1 
        },
        move: { 
          enable: true, 
          speed: 2, 
          direction: "none", 
          random: true, 
          straight: false, 
          out_mode: "out" 
        }
      },
      interactivity: {
        detect_on: "canvas",
        events: {
          onhover: { enable: true, mode: "grab" },
          onclick: { enable: true, mode: "push" }
        }
      }
    });
  }
});
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.login-container {
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 1;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
  min-height: 400px; /* 新增：设置最小高度防止收缩 */
}

.login-card:hover {
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

/* 直角切角二维码入口 */
.qr-corner {
  position: absolute;
  top: 0;
  right: 0;
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, transparent 0%, transparent 50%, #3b82f6 50%, #3b82f6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.qr-corner svg {
  position: absolute;
  top: 5px;
  right: 5px;
  color: white;
}

.qr-corner.active {
  background: linear-gradient(135deg, transparent 0%, transparent 50%, #10b981 50%, #10b981 100%);
}

.card-header {
  text-align: center;
  margin-bottom: 28px;
}

.welcome-text {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
  background: linear-gradient(90deg, #3b82f6, #10b981);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.login-subtitle {
  font-size: 14px;
  color: #64748b;
}

.login-form {
  margin-top: 10px;
}

.form-item {
  margin-bottom: 20px;
}

.custom-input {
  border-radius: 10px;
}

.custom-input :deep(.el-input__wrapper) {
  padding: 0 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  border-radius: 10px;
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 8px rgba(59, 130, 246, 0.2);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #3b82f6 inset, 0 4px 8px rgba(59, 130, 246, 0.2);
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.forgot-password {
  font-size: 14px;
}

/* 登录按钮文字颜色修改为白色 */
.login-btn {
  width: 100%;
  padding: 14px 0;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 1px;
  background: linear-gradient(90deg, #3b82f6, #10b981);
  border: none;
  transition: all 0.3s ease;
  margin-top: 10px;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
  color: white; /* 新增：设置按钮文字颜色为白色 */
}

.login-btn:hover {
  background: linear-gradient(90deg, #2563eb, #0d9488);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.4);
}

.login-btn:active {
  transform: translateY(0);
}

.register-link {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #64748b;
}

.register-text {
  font-weight: 500;
  margin-left: 4px;
  transition: all 0.2s ease;
}

.register-text:hover {
  transform: translateX(2px);
}

/* 二维码内容样式 */
.qr-content {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  padding: 40px;
}

.qr-card {
  text-align: center;
  width: 100%;
}

.qr-code-placeholder {
  width: 200px;
  height: 200px; /* 修复：设置正确的高度 */
  margin: 0 auto 20px;
  background: #f8fafc;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
}

.qr-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.qr-card h3 {
  font-size: 18px;
  margin-bottom: 8px;
  color: #1e293b;
}

.qr-card p {
  color: #64748b;
  margin-bottom: 25px;
  font-size: 14px;
}

.back-btn {
  color: #64748b;
}

.back-btn:hover {
  color: #3b82f6;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
    border-radius: 12px;
  }
  
  .qr-code-placeholder {
    width: 180px;
    height: 180px;
  }
  
  .qr-corner {
    width: 40px;
    height: 40px;
  }
}
</style>
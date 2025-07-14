<template>
  <div class="login-page">
    <!-- 背景粒子动画 -->
    <div class="particles" id="login-particles"></div>
    
    <!-- 登录容器 -->
    <div class="login-container">
      
      <!-- 登录卡片 -->
      <div class="login-card">
        <!-- 卡片头部 -->
        <div class="card-header">
          <h2 class="welcome-text">欢迎使用</h2>
          <p class="login-subtitle">请登录您的账户继续</p>
        </div>
        
        <!-- 登录表单 -->
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
              placeholder="用户名/邮箱" 
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
              :type="showPassword ? 'text' : 'password'" 
              placeholder="请输入密码" 
              size="large"
              :prefix-icon="Lock"
              clearable
              class="custom-input"
            >
              <template #suffix>
                <el-icon 
                  @click="showPassword = !showPassword"
                  class="password-toggle"
                >
                  <component :is="showPassword ? Eye : EyeOff" />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <!-- 记住密码 & 忘记密码 -->
          <div class="form-actions">
            <el-checkbox v-model="rememberMe" class="remember-me">
              记住我
            </el-checkbox>
            <el-link 
              type="primary" 
              class="forgot-password"
              :underline="false"
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
            round
          >
            <span v-if="!loginLoading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
          
          <!-- 社交登录 -->
          <div class="social-login">
            <div class="divider">
              <span class="divider-text">或通过以下方式登录</span>
            </div>
            <div class="social-icons">
              <el-icon :size="24" class="social-icon wechat">
                <ChatDotRound />
              </el-icon>
              <el-icon :size="24" class="social-icon qq">
                <Iphone />
              </el-icon>
              <el-icon :size="24" class="social-icon weibo">
                <Opportunity />
              </el-icon>
            </div>
          </div>
          
          <!-- 注册链接 -->
          <div class="register-link">
            还没有账号?
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
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { 
  User, Lock, Eye, EyeOff, 
  ChatDotRound, Iphone, Opportunity 
} from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 表单数据
const loginForm = reactive({
  username: '',
  password: ''
});

// 表单验证规则
const loginRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3-20 个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ]
});

// 状态管理
const loginFormRef = ref(null);
const showPassword = ref(false);
const rememberMe = ref(false);
const loginLoading = ref(false);

// 登录处理
const handleLogin = async () => {
  const valid = await loginFormRef.value.validate();
  if (!valid) return;

  loginLoading.value = true;

  try {
    // 模拟登录请求
    await new Promise(resolve => setTimeout(resolve, 800));
    
    // 模拟登录成功
    ElMessage.success('登录成功！');
    router.push('/');
    
    // 记住密码逻辑
    if (rememberMe.value) {
      localStorage.setItem('rememberedUser', loginForm.username);
    }
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
  
  // 检查记住的用户名
  const rememberedUser = localStorage.getItem('rememberedUser');
  if (rememberedUser) {
    loginForm.username = rememberedUser;
    rememberMe.value = true;
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
  padding: 0px;
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
}

.login-card:hover {
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.card-header {
  text-align: center;
  margin-bottom: 32px;
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
  margin-bottom: 24px;
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

.password-toggle {
  color: #94a3b8;
  cursor: pointer;
  transition: all 0.2s ease;
}

.password-toggle:hover {
  color: #3b82f6;
  transform: scale(1.1);
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.remember-me :deep(.el-checkbox__label) {
  color: #64748b;
  font-size: 14px;
}

.forgot-password {
  font-size: 14px;
  color: #64748b;
  transition: all 0.2s ease;
}

.forgot-password:hover {
  color: #3b82f6;
  transform: translateX(2px);
}

.login-btn {
  width: 100%;
  padding: 14px 0;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 1px;
  background: linear-gradient(90deg, #3b82f6, #2563eb);
  border: none;
  transition: all 0.3s ease;
  margin-top: 10px;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.login-btn:hover {
  background: linear-gradient(90deg, #2563eb, #1d4ed8);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.4);
}

.login-btn:active {
  transform: translateY(0);
}

.social-login {
  margin: 30px 0 20px;
}

.divider {
  display: flex;
  align-items: center;
  margin: 20px 0;
  color: #94a3b8;
  font-size: 12px;
}

.divider::before, .divider::after {
  content: "";
  flex: 1;
  border-bottom: 1px solid #e2e8f0;
}

.divider-text {
  padding: 0 12px;
}

.social-icons {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 20px;
}

.social-icon {
  padding: 10px;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #64748b;
  background: #f1f5f9;
}

.social-icon:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.social-icon.wechat:hover {
  color: #07C160;
  background: rgba(7, 193, 96, 0.1);
}

.social-icon.qq:hover {
  color: #12B7F5;
  background: rgba(18, 183, 245, 0.1);
}

.social-icon.weibo:hover {
  color: #E6162D;
  background: rgba(230, 22, 45, 0.1);
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

@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
  }
  
  .login-container {
    padding: 0 15px;
  }
  
  .welcome-text {
    font-size: 22px;
  }
}
</style>
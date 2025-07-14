<template>
  <div class="register-page">
    <!-- 背景粒子动画 -->
    <div class="particles" id="register-particles"></div>
    
    <!-- 注册容器 -->
    <div class="register-container">
      
      <!-- 注册卡片 -->
      <div class="register-card">
        <!-- 卡片头部 -->
        <div class="card-header">
          <h2 class="welcome-text">创建账户</h2>
          <p class="register-subtitle">加入Lucky SMS学生管理系统</p>
        </div>
        
        <!-- 注册表单 -->
        <el-form 
          ref="registerFormRef" 
          :model="registerForm" 
          :rules="registerRules" 
          class="register-form"
          @keyup.enter="handleRegister"
        >
          <!-- 用户名输入 -->
          <el-form-item prop="username" class="form-item">
            <el-input 
              v-model="registerForm.username" 
              placeholder="设置用户名" 
              size="large"
              :prefix-icon="User"
              clearable
              class="custom-input"
            />
          </el-form-item>
          
          <!-- 邮箱输入 -->
          <el-form-item prop="email" class="form-item">
            <el-input 
              v-model="registerForm.email" 
              placeholder="输入邮箱地址" 
              size="large"
              :prefix-icon="Message"
              clearable
              class="custom-input"
            />
          </el-form-item>
          
          <!-- 密码输入 -->
          <el-form-item prop="password" class="form-item">
            <el-input 
              v-model="registerForm.password" 
              :type="showPassword ? 'text' : 'password'" 
              placeholder="设置密码" 
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
            <div class="password-strength">
              <div 
                class="strength-bar" 
                :class="{
                  'weak': passwordStrength === 1,
                  'medium': passwordStrength === 2,
                  'strong': passwordStrength === 3
                }"
              ></div>
              <span class="strength-text">{{ passwordStrengthText }}</span>
            </div>
          </el-form-item>
          
          <!-- 确认密码 -->
          <el-form-item prop="confirmPassword" class="form-item">
            <el-input 
              v-model="registerForm.confirmPassword" 
              :type="showConfirmPassword ? 'text' : 'password'" 
              placeholder="确认密码" 
              size="large"
              :prefix-icon="Lock"
              clearable
              class="custom-input"
            >
              <template #suffix>
                <el-icon 
                  @click="showConfirmPassword = !showConfirmPassword"
                  class="password-toggle"
                >
                  <component :is="showConfirmPassword ? Eye : EyeOff" />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <!-- 验证码 -->
          <el-form-item prop="captcha" class="form-item">
            <div class="captcha-container">
              <el-input 
                v-model="registerForm.captcha" 
                placeholder="输入验证码" 
                size="large"
                :prefix-icon="Key"
                class="custom-input captcha-input"
              />
              <el-button 
                type="primary" 
                size="large" 
                class="captcha-btn"
                :disabled="captchaCooldown > 0"
                @click="sendCaptcha"
              >
                {{ captchaBtnText }}
              </el-button>
            </div>
          </el-form-item>
          
          <!-- 用户协议 -->
          <el-form-item prop="agreement" class="form-item agreement-item">
            <el-checkbox v-model="registerForm.agreement">
              我已阅读并同意
              <el-link type="primary" :underline="false">《用户协议》</el-link>
              和
              <el-link type="primary" :underline="false">《隐私政策》</el-link>
            </el-checkbox>
          </el-form-item>
          
          <!-- 注册按钮 -->
          <el-button 
            type="primary" 
            size="large" 
            class="register-btn" 
            @click="handleRegister"
            :loading="registerLoading"
            round
          >
            <span v-if="!registerLoading">立即注册</span>
            <span v-else>注册中...</span>
          </el-button>
          
          <!-- 已有账号 -->
          <div class="login-link">
            已有账号?
            <el-link 
              type="primary" 
              @click="$router.push('/login')"
              :underline="false"
              class="login-text"
            >
              立即登录
            </el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { 
  User, Message, Lock, Key, Eye, EyeOff 
} from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 表单数据
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  captcha: '',
  agreement: false
});

// 表单验证规则
const registerRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符之间', trigger: 'blur' },
    { 
      pattern: /^[a-zA-Z0-9_]+$/,
      message: '用户名只能包含字母、数字和下划线',
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { 
      type: 'email', 
      message: '请输入正确的邮箱格式', 
      trigger: ['blur', 'change'] 
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '密码长度不能少于8个字符', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
          callback(new Error('密码需包含大小写字母和数字'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ],
  agreement: [
    { 
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请同意用户协议和隐私政策'));
        } else {
          callback();
        }
      },
      trigger: 'change'
    }
  ]
});

// 状态管理
const registerFormRef = ref(null);
const showPassword = ref(false);
const showConfirmPassword = ref(false);
const registerLoading = ref(false);
const captchaCooldown = ref(0);
let captchaTimer = null;

// 密码强度计算
const passwordStrength = computed(() => {
  if (!registerForm.password) return 0;
  
  let strength = 0;
  if (registerForm.password.length >= 8) strength++;
  if (/[A-Z]/.test(registerForm.password)) strength++;
  if (/[0-9]/.test(registerForm.password)) strength++;
  if (/[^A-Za-z0-9]/.test(registerForm.password)) strength++;
  
  return Math.min(3, Math.floor(strength / 1.5));
});

const passwordStrengthText = computed(() => {
  const texts = ['', '弱', '中', '强'];
  return texts[passwordStrength.value];
});

// 验证码按钮文本
const captchaBtnText = computed(() => {
  return captchaCooldown.value > 0 
    ? `${captchaCooldown.value}秒后重试` 
    : '获取验证码';
});

// 发送验证码
const sendCaptcha = () => {
  if (!registerForm.email) {
    ElMessage.warning('请输入邮箱地址');
    return;
  }
  
  // 模拟发送验证码
  captchaCooldown.value = 60;
  captchaTimer = setInterval(() => {
    captchaCooldown.value--;
    if (captchaCooldown.value <= 0) {
      clearInterval(captchaTimer);
    }
  }, 1000);
  
  ElMessage.success(`验证码已发送至 ${registerForm.email}`);
};

// 注册处理
const handleRegister = async () => {
  const valid = await registerFormRef.value.validate();
  if (!valid) return;

  registerLoading.value = true;

  try {
    // 模拟注册请求
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // 模拟注册成功
    ElMessage.success('注册成功！');
    router.push('/login');
  } catch (error) {
    ElMessage.error('注册失败: ' + (error.message || '请稍后再试'));
  } finally {
    registerLoading.value = false;
  }
};

// 初始化粒子动画
onMounted(() => {
  if (window.particlesJS) {
    window.particlesJS('register-particles', {
      particles: {
        number: { value: 60, density: { enable: true, value_area: 800 } },
        color: { value: "#10b981" },
        shape: { type: "circle" },
        opacity: { value: 0.5, random: true },
        size: { value: 3, random: true },
        line_linked: { 
          enable: true, 
          distance: 150, 
          color: "#10b981", 
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

// // 清理定时器
// onUnmounted(() => {
//   if (captchaTimer) clearInterval(captchaTimer);
// });
</script>

<style scoped>
.register-page {
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

.register-container {
  width: 100%;
  max-width: 480px;
  position: relative;
  z-index: 1;
}

.register-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.register-card:hover {
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
  background: linear-gradient(90deg, #10b981, #3b82f6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.register-subtitle {
  font-size: 14px;
  color: #64748b;
}

.register-form {
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
  box-shadow: 0 4px 8px rgba(16, 185, 129, 0.2);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #10b981 inset, 0 4px 8px rgba(16, 185, 129, 0.2);
}

.password-toggle {
  color: #94a3b8;
  cursor: pointer;
  transition: all 0.2s ease;
}

.password-toggle:hover {
  color: #10b981;
  transform: scale(1.1);
}

.password-strength {
  display: flex;
  align-items: center;
  margin-top: 8px;
  height: 6px;
}

.strength-bar {
  flex: 1;
  height: 100%;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
  position: relative;
}

.strength-bar::after {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 0;
  transition: width 0.3s ease;
}

.strength-bar.weak::after {
  width: 33%;
  background: #ef4444;
}

.strength-bar.medium::after {
  width: 66%;
  background: #f59e0b;
}

.strength-bar.strong::after {
  width: 100%;
  background: #10b981;
}

.strength-text {
  font-size: 12px;
  color: #64748b;
  margin-left: 8px;
}

.captcha-container {
  display: flex;
  gap: 10px;
}

.captcha-input {
  flex: 1;
}

.captcha-btn {
  flex-shrink: 0;
  width: 120px;
  padding: 0;
}

.captcha-btn:disabled {
  background: #e2e8f0 !important;
  color: #94a3b8 !important;
  cursor: not-allowed;
}

.agreement-item :deep(.el-checkbox__label) {
  font-size: 14px;
  color: #64748b;
}

.register-btn {
  width: 100%;
  padding: 14px 0;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 1px;
  background: linear-gradient(90deg, #10b981, #3b82f6);
  border: none;
  transition: all 0.3s ease;
  margin-top: 10px;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.register-btn:hover {
  background: linear-gradient(90deg, #0d9488, #2563eb);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(16, 185, 129, 0.4);
}

.register-btn:active {
  transform: translateY(0);
}

.login-link {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #64748b;
}

.login-text {
  font-weight: 500;
  margin-left: 4px;
  transition: all 0.2s ease;
}

.login-text:hover {
  transform: translateX(2px);
}

@media (max-width: 480px) {
  .register-card {
    padding: 30px 20px;
  }
  
  .register-container {
    padding: 0 15px;
  }
  
  .welcome-text {
    font-size: 22px;
  }
  
  .captcha-container {
    flex-direction: column;
  }
  
  .captcha-btn {
    width: 100%;
  }
}
</style>
<template>
  <div class="website-container">
    <!-- 导航栏 -->
    <el-menu ref="menuRef" default-active="1" class="el-menu-demo" mode="horizontal" background-color="#ffffff"
      text-color="#333333" active-text-color="#409EFF">
      <el-menu-item index="1" data-id="home" @click="scrollTo('home')">首页</el-menu-item>
      <el-menu-item index="2" data-id="system" @click="scrollTo('system')">系统介绍</el-menu-item>
      <el-menu-item index="3" data-id="solution" @click="scrollTo('solution')">解决方案</el-menu-item>
      <el-menu-item index="4" data-id="about" @click="scrollTo('about')">关于我们</el-menu-item>
    </el-menu>

    <!-- 轮播图 -->
    <div id="home" class="banner">
      <el-carousel height="500px" indicator-position="bottom" autoplay arrow="always">
        <el-carousel-item v-for="(item, index) in banners" :key="index">
          <div class="banner-content">
            <h1 class="banner-title animate-fade-in">{{ item.title }}</h1>
            <p class="banner-desc animate-slide-up">{{ item.desc }}</p>
            <el-button type="primary" class="banner-btn animate-scale" @click="scrollTo(item.btnTarget)">{{ item.btnText
            }}</el-button>
          </div>
          <div class="banner-bg" :style="{ backgroundImage: `url(${item.bgImage})` }"></div>
          <div class="banner-overlay"></div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 系统介绍 -->
    <div id="system" class="system-section section-with-bg">
      <h2 class="section-title animate-float">系统介绍</h2>
      <div class="feature-list">
        <div class="feature-item animate-on-scroll" v-for="(feature, index) in features" :key="index">
          <el-icon class="feature-icon">
            <component :is="feature.icon" />
          </el-icon>
          <h3 class="feature-title">{{ feature.title }}</h3>
          <p class="feature-desc">{{ feature.desc }}</p>
        </div>
      </div>
    </div>

    <!-- 解决方案 -->
    <div id="solution" class="solution-section">
      <h2 class="section-title animate-float">解决方案</h2>
      <div class="solution-list">
        <div class="solution-item animate-on-scroll" v-for="(solution, index) in solutions" :key="index">
          <el-icon class="solution-icon">
            <component :is="solution.icon" />
          </el-icon>
          <h3 class="solution-title">{{ solution.title }}</h3>
          <p class="solution-desc">{{ solution.desc }}</p>
        </div>
      </div>
    </div>

    <!-- 关于我们 -->
    <div id="about" class="about-section section-with-bg">
      <h2 class="section-title animate-float">关于我们</h2>
      <div class="about-content animate-on-scroll">
        <p>我们是一家专注于企业数字化转型的技术服务提供商，致力于为客户提供全方位的信息化解决方案。</p>
        <p>凭借专业的技术团队和丰富的行业经验，我们已成功服务超过100家企业客户，涵盖学生等多个领域。</p>
      </div>
    </div>

    <!-- 联系我们 -->
    <div class="footer">
      <p><el-icon>
          <Location />
        </el-icon> 联系地址：北京市朝阳区XX大厦</p>
      <p><el-icon>
          <Phone />
        </el-icon> 联系电话：010-12345678</p>
      <p><el-icon>
          <Mail />
        </el-icon> 电子邮箱：contact@example.com</p>
      <p class="footer-text">©2025 你的公司名称 版权所有</p>
    </div>

    <!-- 返回顶部按钮 -->
    <el-button v-show="showBackToTop" type="primary" class="back-to-top" @click="scrollToTop">
      <el-icon>
        <ArrowUp />
      </el-icon>
    </el-button>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import {
  Monitor, Database, Shield,
  Star, Clock, Wallet,
  Location, Phone, Mail,
  ArrowUp
} from '@element-plus/icons-vue';
import { ElCarousel, ElCarouselItem, ElButton } from 'element-plus';

// 数据
const menuRef = ref(null);
const showBackToTop = ref(false);

const banners = [
  {
    title: "专注创新技术，引领行业未来",
    desc: "为您提供专业、高效、定制化的解决方案",
    btnText: "立即了解",
    btnTarget: "system",
    bgImage: "https://picsum.photos/id/1/1920/1080"
  },
  {
    title: "智能系统解决方案",
    desc: "集成前沿技术，打造全方位智能管理平台",
    btnText: "查看方案",
    btnTarget: "solution",
    bgImage: "https://picsum.photos/id/20/1920/1080"
  },
  {
    title: "7×24小时专业服务",
    desc: "随时响应您的需求，保障业务持续运行",
    btnText: "联系我们",
    btnTarget: "contact",
    bgImage: "https://picsum.photos/id/48/1920/1080"
  }
];

const features = [
  {
    icon: Monitor,
    title: "智能监控",
    desc: "实时监控系统运行状态，自动预警异常情况，保障业务稳定运行。"
  },
  {
    icon: Database,
    title: "数据管理",
    desc: "高效管理海量数据，提供快速查询和分析能力，助力业务决策。"
  },
  {
    icon: Shield,
    title: "安全防护",
    desc: "多重安全防护机制，全方位保障数据安全，防止信息泄露。"
  }
];

const solutions = [
  {
    icon: Star,
    title: "专业团队",
    desc: "拥有多年行业经验的专业团队，提供一流的技术支持和服务。"
  },
  {
    icon: Clock,
    title: "高效响应",
    desc: "7×24小时快速响应机制，及时解决用户问题，保障业务连续性。"
  },
  {
    icon: Wallet,
    title: "性价比高",
    desc: "合理的价格体系，提供高性价比的产品和服务，降低企业成本。"
  }
];

// 方法
const scrollTo = (id) => {
  const element = document.getElementById(id);
  if (element) {
    // 平滑滚动到目标位置
    element.scrollIntoView({
      behavior: 'smooth',
      block: 'start'
    });

    // 更新菜单激活状态
    updateActiveMenuItem(id);
  }
};

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  });
};

const updateActiveMenuItem = (id) => {
  const menuItems = document.querySelectorAll('.el-menu-item[data-id]');
  menuItems.forEach(item => item.classList.remove('is-active'));
  const activeItem = document.querySelector(`.el-menu-item[data-id="${id}"]`);
  if (activeItem) {
    activeItem.classList.add('is-active');
  }
};

// 监听滚动事件
const handleScroll = () => {
  // 显示/隐藏返回顶部按钮
  showBackToTop.value = window.scrollY > 300;

  // 根据滚动位置更新菜单激活状态
  const sections = document.querySelectorAll('div[id^="home"], div[id^="system"], div[id^="solution"], div[id^="about"], div[id^="contact"]');
  let currentSection = '';

  sections.forEach(section => {
    const sectionTop = section.offsetTop - 100;
    const sectionHeight = section.clientHeight;
    if (window.scrollY >= sectionTop && window.scrollY < sectionTop + sectionHeight) {
      currentSection = section.getAttribute('id');
    }
  });

  if (currentSection) {
    updateActiveMenuItem(currentSection);
  }

  // 处理滚动动画
  const animateElements = document.querySelectorAll('.animate-on-scroll');
  animateElements.forEach(element => {
    const elementTop = element.getBoundingClientRect().top;
    const windowHeight = window.innerHeight;

    if (elementTop < windowHeight * 0.85) {
      element.classList.add('animated');
    }
  });
};

// 初始化
onMounted(() => {
  window.addEventListener('scroll', handleScroll);

  // 初始检查可见元素
  nextTick(() => {
    handleScroll();
  });
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<style scoped>
/* 基础样式 */
.website-container {
  width: 100%;
  margin: 0 auto;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  overflow-x: hidden;
}

/* 导航栏样式 */
.el-menu-demo {
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 100;
  transition: all 0.3s ease;
}

.el-menu-demo.is-scrolled {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

/* 轮播图样式 */
.banner {
  width: 100%;
  height: 500px;
  position: relative;
}

.el-carousel-item {
  position: relative;
  height: 100%;
}

.banner-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  z-index: 1;
  transition: transform 8s ease-in-out;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to right, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.4));
  z-index: 2;
}

.banner-content {
  position: relative;
  max-width: 800px;
  padding: 0 20px;
  margin: 0 auto;
  text-align: center;
  color: #ffffff;
  z-index: 3;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.banner-title {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 20px;
  line-height: 1.2;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.8s ease;
}

.banner-desc {
  font-size: 20px;
  margin-bottom: 40px;
  opacity: 0.9;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.8s ease 0.3s;
}

.banner-btn {
  font-size: 18px;
  padding: 12px 30px;
  opacity: 0;
  transform: scale(0.9);
  transition: all 0.8s ease 0.6s;
}

/* 轮播图动画 */
.el-carousel-item.is-active .banner-bg {
  transform: scale(1.05);
}

.el-carousel-item.is-active .banner-title {
  opacity: 1;
  transform: translateY(0);
}

.el-carousel-item.is-active .banner-desc {
  opacity: 0.9;
  transform: translateY(0);
}

.el-carousel-item.is-active .banner-btn {
  opacity: 1;
  transform: scale(1);
}

/* 通用部分样式 */
.system-section,
.solution-section,
.about-section,
.contact-section {
  padding: 80px 20px;
  text-align: center;
}

.section-with-bg {
  background-color: #f8f9fa;
}

.section-title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 60px;
  color: #333333;
  position: relative;
  display: inline-block;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 4px;
  background-color: #409EFF;
  border-radius: 2px;
  transition: all 0.5s ease;
}

.section-title:hover::after {
  width: 120px;
}

/* 特性列表样式 */
.feature-list,
.solution-list {
  display: flex;
  justify-content: center;
  width: 90%;
  margin: 0 auto;
  gap: 30px;
  flex-wrap: wrap;
}

.feature-item,
.solution-item {
  flex: 1 1 300px;
  max-width: 350px;
  padding: 40px 30px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  opacity: 0;
  transform: translateY(20px);
}

.feature-item::before,
.solution-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background-color: #409EFF;
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.feature-item:hover,
.solution-item:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.12);
}

.feature-item:hover::before,
.solution-item:hover::before {
  transform: scaleX(1);
}

.feature-item.animated,
.solution-item.animated {
  opacity: 1;
  transform: translateY(0);
}

.feature-icon,
.solution-icon {
  font-size: 48px;
  color: #409EFF;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.feature-item:hover .feature-icon,
.solution-item:hover .solution-icon {
  transform: scale(1.1) rotate(5deg);
}

.feature-title,
.solution-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333333;
}

.feature-desc,
.solution-desc {
  font-size: 16px;
  color: #666666;
  line-height: 1.6;
}

/* 关于我们部分 */
.about-content {
  max-width: 800px;
  margin: 0 auto;
  line-height: 1.8;
  margin-bottom: 40px;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.8s ease;
}

.about-content.animated {
  opacity: 1;
  transform: translateY(0);
}

/* 联系我们部分 */
.contact-info {
  max-width: 600px;
  margin: 0 auto;
  line-height: 2;
  font-size: 18px;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.8s ease;
}

.contact-info.animated {
  opacity: 1;
  transform: translateY(0);
}

.contact-info p {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.contact-info .el-icon {
  margin-right: 10px;
  color: #409EFF;
}

.social-media {
  margin-top: 40px;
  display: flex;
  justify-content: center;
  gap: 20px;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.8s ease 0.3s;
}

.social-media.animated {
  opacity: 1;
  transform: translateY(0);
}

.social-icon {
  font-size: 24px;
  color: #409EFF;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #ffffff;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.social-icon:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.15);
  background-color: #409EFF;
  color: #ffffff;
}

/* 页脚 */
.footer {
  background-color: #333;
  text-align: center;
  padding: 40px 20px;
  color: #999999;
  margin-top: 50px;
}

.footer-text {
  margin: 10px 0;
  line-height: 1.6;
}

/* 返回顶部按钮 */
.back-to-top {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 100;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.back-to-top.visible {
  opacity: 1;
  visibility: visible;
}

/* 按钮悬停效果 */
.btn-hover-effect {
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.btn-hover-effect::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: all 0.6s ease;
}

.btn-hover-effect:hover::before {
  left: 100%;
}

/* 动画类 */
.animate-fade-in {
  animation: fadeIn 1s ease forwards;
}

.animate-slide-up {
  animation: slideUp 1s ease forwards;
}

.animate-scale {
  animation: scale 1s ease forwards;
}

.animate-float {
  position: relative;
  animation: float 3s ease-in-out infinite;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes scale {
  from {
    opacity: 0;
    transform: scale(0.9);
  }

  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes float {
  0% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-10px);
  }

  100% {
    transform: translateY(0px);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-title {
    font-size: 32px;
  }

  .banner-desc {
    font-size: 16px;
  }

  .feature-list,
  .solution-list {
    flex-direction: column;
    align-items: center;
  }

  .feature-item,
  .solution-item {
    width: 90%;
    margin-bottom: 30px;
  }

  .el-menu-demo {
    padding: 0 10px;
  }
}
</style>
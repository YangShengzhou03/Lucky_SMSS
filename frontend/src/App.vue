<template>
  <div id="app">
    <!-- 导航栏 -->
    <NavigationBar />
    
    <!-- 英雄区域（轮播图） -->
    <HeroCarousel />
    
    <!-- 合作伙伴 -->
    <PartnersSection />
    
    <!-- 功能介绍 -->
    <FeaturesSection />
    
    <!-- 数据统计 -->
    <StatsSection />
    
    <!-- 用户评价 -->
    <TestimonialsSection />
    
    <!-- 价格方案 -->
    <PricingSection />
    
    <!-- 联系我们 -->
    <ContactSection />
    
    <!-- 页脚 -->
    <FooterSection />
    
    <!-- 登录模态框 -->
    <LoginModal />
    
    <!-- 注册模态框 -->
    <RegisterModal />
  </div>
</template>

<script>
// 导入组件，使用符合规范的命名
import NavigationBar from './components/NavigationBar.vue'
import HeroCarousel from './components/HeroCarousel.vue'
import PartnersSection from './components/PartnersSection.vue'
import FeaturesSection from './components/FeaturesSection.vue'
import StatsSection from './components/StatsSection.vue'
import TestimonialsSection from './components/TestimonialsSection.vue'
import PricingSection from './components/PricingSection.vue'
import ContactSection from './components/ContactSection.vue'
import FooterSection from './components/FooterSection.vue'
import LoginModal from './components/LoginModal.vue'
import RegisterModal from './components/RegisterModal.vue'

export default {
  name: 'App',
  components: {
    NavigationBar,
    HeroCarousel,
    PartnersSection,
    FeaturesSection,
    StatsSection,
    TestimonialsSection,
    PricingSection,
    ContactSection,
    FooterSection,
    LoginModal,
    RegisterModal
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

body {
  font-family: 'Inter', sans-serif;
  background-color: #f9fafb;
  color: #1e293b;
}
</style>
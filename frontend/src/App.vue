<template>
  <!-- 根容器，所有页面都在这里渲染 -->
  <div id="app">
    <!-- 唯一的路由出口：所有路由页面（登录/首页等）都在这里显示 -->
    <router-view />
  </div>
</template>

<script setup>
// 无需任何逻辑，仅作为路由容器
</script>

<style>
/* 基础样式，确保页面无默认边距 */
#app {
  min-height: 100vh;
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}
</style>
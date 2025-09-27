<template>
  <a-layout class="basic-layout">
    <!-- 顶部导航栏 -->
    <GlobalHeader />

    <!-- 中间内容区域 -->
    <a-layout-content class="layout-content">
      <div class="content-wrapper">
        <RouterView />
      </div>
    </a-layout-content>

    <!-- 底部页脚 -->
    <GlobalFooter />
  </a-layout>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'
</script>

<style scoped>
.basic-layout {
  min-height: 100vh;
  background: #f5f5f5;
}

.layout-content {
  margin-top: 64px; /* 为固定头部留出空间 */
  margin-bottom: 60px; /* 为固定底部留出空间 */
  min-height: calc(100vh - 124px); /* 减去头部和底部的高度 */
  background: #fff;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  min-height: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-wrapper {
    padding: 16px;
  }

  .layout-content {
    margin-top: 64px;
    margin-bottom: 60px;
  }
}

@media (max-width: 480px) {
  .content-wrapper {
    padding: 12px;
  }

  .layout-content {
    margin-top: 64px;
    margin-bottom: 60px;
  }
}

/* 确保页面内容不被固定元素遮挡 */
:deep(.ant-layout-header) {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

:deep(.ant-layout-footer) {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}
</style>

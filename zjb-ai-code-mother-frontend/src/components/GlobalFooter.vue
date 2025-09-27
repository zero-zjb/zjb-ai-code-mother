<template>
  <a-layout-footer class="global-footer">
    <div class="footer-content">
      <p class="copyright">智能AI代码生成平台项目 by 程序员zjb</p>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts">
// 页脚组件，无需额外逻辑
</script>

<style scoped>
.global-footer {
  background: #f0f2f5;
  text-align: center;
  padding: 16px 0;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  border-top: 1px solid #d9d9d9;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.copyright {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-content {
    padding: 0 16px;
  }

  .copyright {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .footer-content {
    padding: 0 12px;
  }

  .copyright {
    font-size: 11px;
  }
}
</style>

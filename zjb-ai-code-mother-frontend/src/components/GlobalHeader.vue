<template>
  <a-layout-header class="global-header">
    <div class="header-content">
      <!-- 左侧 Logo 和标题 -->
      <div class="header-left">
        <img src="@/assets/logo.png" alt="Logo" class="logo" />
        <span class="site-title">智能AI代码生成平台</span>
      </div>

      <!-- 中间菜单 -->
      <div class="header-menu">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="menuItems"
          class="menu"
          @click="handleMenuClick"
        />
      </div>

      <!-- 右侧用户信息 -->
      <div class="header-right">
        <a-button type="primary" @click="handleLogin"> 登录 </a-button>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const selectedKeys = ref<string[]>(['home'])

// 菜单配置
const menuItems = reactive([
  {
    key: 'home',
    label: '首页',
    path: '/',
  },
  {
    key: 'about',
    label: '关于',
    path: '/about',
  },
  {
    key: 'tools',
    label: '工具',
    children: [
      {
        key: 'code-generator',
        label: '代码生成器',
        path: '/tools/code-generator',
      },
      {
        key: 'api-docs',
        label: 'API文档',
        path: '/tools/api-docs',
      },
    ],
  },
  {
    key: 'resources',
    label: '资源',
    children: [
      {
        key: 'tutorials',
        label: '教程',
        path: '/resources/tutorials',
      },
      {
        key: 'examples',
        label: '示例',
        path: '/resources/examples',
      },
    ],
  },
])

// 处理菜单点击
const handleMenuClick = ({ key }: { key: string }) => {
  // 查找菜单项路径
  const findPath = (items: any[], targetKey: string): string | null => {
    for (const item of items) {
      if (item.key === targetKey && item.path) {
        return item.path
      }
      if (item.children) {
        const found = findPath(item.children, targetKey)
        if (found) return found
      }
    }
    return null
  }

  const path = findPath(menuItems, key)
  if (path) {
    router.push(path)
  }
}

// 处理登录
const handleLogin = () => {
  console.log('登录功能待实现')
}
</script>

<style scoped>
.global-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 32px;
  width: 32px;
}

.site-title {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
}

.header-menu {
  flex: 1;
  display: flex;
  justify-content: center;
}

.menu {
  border-bottom: none;
  background: transparent;
}

.header-right {
  display: flex;
  align-items: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }

  .site-title {
    font-size: 16px;
  }

  .header-menu {
    display: none;
  }
}

@media (max-width: 480px) {
  .header-content {
    padding: 0 12px;
  }

  .site-title {
    display: none;
  }
}
</style>

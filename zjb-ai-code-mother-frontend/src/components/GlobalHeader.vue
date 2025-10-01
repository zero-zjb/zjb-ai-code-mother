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
<!--      <div class="header-right">-->
<!--        <a-button type="primary" @click="handleLogin"> 登录 </a-button>-->
<!--      </div>-->

      <div class="user-login-status">
        <div v-if="loginUserStore.loginUser.id">
          <a-dropdown>
            <a-space>
              <a-avatar :src="loginUserStore.loginUser.userAvatar" />
              {{ loginUserStore.loginUser.userName ?? '无名' }}
            </a-space>
            <template #overlay>
              <a-menu>
                <a-menu-item key="logout" @click="doLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
        <div v-else>
          <a-button type="primary" href="/user/login">登录</a-button>
        </div>
      </div>

    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { ref, reactive, h } from 'vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import {userLogout} from "@/api/userController.ts";
import {MenuProps, message} from "ant-design-vue";
import {LogoutOutlined, HomeOutlined} from "@ant-design/icons-vue";

const loginUserStore = useLoginUserStore()
loginUserStore.fetchLoginUser()


const router = useRouter()
const selectedKeys = ref<string[]>(['home'])

// 菜单配置
// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://www.codefather.cn', target: '_blank' }, '编程导航'),
    title: '编程导航',
  },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))


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
  router.push('/user/login');
}

// 处理登出
const doLogout = async () => {
  const res = await userLogout();
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: "未登录",
    })
    await router.push('/user/login')
    message.success('退出成功')
  }else {
    message.error("退出失败：" + res.data.message)
  }
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

.user-login-status {
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

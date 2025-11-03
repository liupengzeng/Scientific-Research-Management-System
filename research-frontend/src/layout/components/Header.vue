<template>
  <div class="header-container">
    <div class="header-left">
      <el-icon class="sidebar-toggle" @click="toggleSidebar">
        <Fold v-if="!appStore.sidebarCollapsed" />
        <Expand v-else />
      </el-icon>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item
          v-for="(item, index) in breadcrumbList"
          :key="index"
          :to="{ path: item.path }"
        >
          {{ item.meta?.title || item.name }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="header-right">
      <!-- 全屏按钮 -->
      <el-tooltip content="全屏" placement="bottom">
        <el-icon class="header-icon" @click="toggleFullscreen">
          <FullScreen />
        </el-icon>
      </el-tooltip>
      <!-- 用户信息下拉菜单 -->
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <el-avatar :src="userStore.avatar" :size="32">
            {{ userStore.realName?.charAt(0) || 'U' }}
          </el-avatar>
          <span class="username">{{ userStore.realName || userStore.username }}</span>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import { ElMessage, ElMessageBox } from 'element-plus'
import screenfull from 'screenfull'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

// 面包屑导航
const breadcrumbList = computed(() => {
  return route.matched.filter(item => item.meta && item.meta.title)
})

// 切换侧边栏
const toggleSidebar = () => {
  appStore.toggleSidebar()
}

// 切换全屏
const toggleFullscreen = () => {
  if (!screenfull.isEnabled) {
    ElMessage.warning('浏览器不支持全屏功能')
    return
  }
  screenfull.toggle()
}

// 处理下拉菜单命令
const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await userStore.logout()
        ElMessage.success('退出登录成功')
      }).catch(() => {
        // 取消
      })
      break
  }
}
</script>

<style lang="scss" scoped>
.header-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;

    .sidebar-toggle {
      font-size: 20px;
      cursor: pointer;
      color: #666;
      transition: color 0.3s;

      &:hover {
        color: #409EFF;
      }
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 20px;

    .header-icon {
      font-size: 20px;
      cursor: pointer;
      color: #666;
      transition: color 0.3s;

      &:hover {
        color: #409EFF;
      }
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 4px 12px;
      border-radius: 4px;
      transition: background 0.3s;

      &:hover {
        background: #f5f5f5;
      }

      .username {
        font-size: 14px;
        color: #333;
      }
    }
  }
}
</style>


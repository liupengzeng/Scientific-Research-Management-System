<template>
  <div class="sidebar-container" :class="{ collapsed: appStore.sidebarCollapsed }">
    <div class="logo-container">
      <h1 class="logo-title">科研管理系统</h1>
    </div>
    <el-menu
      :default-active="activeMenu"
      :collapse="appStore.sidebarCollapsed"
      :collapse-transition="false"
      :unique-opened="false"
      :router="true"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
    >
      <sidebar-item
        v-for="route in menuList"
        :key="route.path"
        :item="route"
        :base-path="route.path"
      />
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { usePermissionStore } from '@/stores/permission'
import SidebarItem from './SidebarItem.vue'

const route = useRoute()
const appStore = useAppStore()
const permissionStore = usePermissionStore()

// 菜单列表
const menuList = computed(() => {
  return permissionStore.menuList
})

// 当前激活的菜单
const activeMenu = computed(() => {
  const { path } = route
  if (path.startsWith('/redirect')) {
    return route.query.redirect
  }
  return path
})
</script>

<style lang="scss" scoped>
.sidebar-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 200px;
  height: 100vh;
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
  z-index: 1000;

  &.collapsed {
    width: 64px;
  }

  .logo-container {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #2b3a4a;

    .logo-title {
      color: #fff;
      font-size: 18px;
      font-weight: 600;
      white-space: nowrap;
      transition: opacity 0.3s;

      .sidebar-container.collapsed & {
        opacity: 0;
        width: 0;
        overflow: hidden;
      }
    }
  }

  :deep(.el-menu) {
    border-right: none;
    height: calc(100vh - 60px);
    overflow-y: auto;

    &.el-menu--collapse {
      width: 64px;
    }
  }
}
</style>


<template>
  <div v-if="!item.hidden">
    <el-menu-item
      v-if="hasOneShowingChild(item.children, item) && !onlyOneChild.children && !item.alwaysShow"
      :index="resolvePath(onlyOneChild.path)"
    >
      <el-icon v-if="onlyOneChild.meta?.icon">
        <component :is="onlyOneChild.meta.icon" />
      </el-icon>
      <template #title>
        <span>{{ onlyOneChild.meta?.title || item.name }}</span>
      </template>
    </el-menu-item>

    <el-sub-menu
      v-else
      :index="resolvePath(item.path)"
    >
      <template #title>
        <el-icon v-if="item.meta?.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <span>{{ item.meta?.title || item.name }}</span>
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :base-path="resolvePath(child.path)"
      />
    </el-sub-menu>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  basePath: {
    type: String,
    default: ''
  }
})

const onlyOneChild = ref(null)

// 判断是否只有一个显示的子路由
function hasOneShowingChild(children = [], parent) {
  const showingChildren = children.filter(item => {
    if (item.hidden) {
      return false
    } else {
      onlyOneChild.value = item
      return true
    }
  })

  if (showingChildren.length === 1) {
    return true
  }

  if (showingChildren.length === 0) {
    onlyOneChild.value = { ...parent, path: '', noShowingChildren: true }
    return true
  }

  return false
}

// 解析路径
function resolvePath(routePath) {
  if (isExternal(routePath)) {
    return routePath
  }
  if (isExternal(props.basePath)) {
    return props.basePath
  }
  // 简单的路径拼接
  if (props.basePath && !routePath.startsWith('/')) {
    return props.basePath + '/' + routePath
  }
  return routePath || props.basePath
}

// 判断是否为外部链接
function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}
</script>


<template>
  <div></div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

// 重定向到目标路径
const { params, query } = route
const { path } = params
router.replace({ path: '/' + path, query })
</script>


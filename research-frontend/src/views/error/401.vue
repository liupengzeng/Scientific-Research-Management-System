<template>
  <div class="error-container">
    <div class="error-content">
      <div class="error-code">401</div>
      <div class="error-desc">抱歉，您没有权限访问该页面</div>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style lang="scss" scoped>
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;

  .error-content {
    text-align: center;

    .error-code {
      font-size: 120px;
      font-weight: bold;
      color: #F56C6C;
      line-height: 1;
      margin-bottom: 20px;
    }

    .error-desc {
      font-size: 18px;
      color: #666;
      margin-bottom: 30px;
    }
  }
}
</style>


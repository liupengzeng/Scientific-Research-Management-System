<template>
  <div class="profile-container">
    <el-card class="box-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ userStore.username }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ userStore.realName }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ userStore.userId }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ (userStore.roles || []).join(', ') }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ userStore.userInfo?.deptName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userStore.userInfo?.email || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div class="actions">
        <el-button type="primary" @click="goDashboard">返回首页</el-button>
        <el-button @click="goUserManage">用户管理</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const goDashboard = () => {
  router.push('/dashboard')
}

const goUserManage = () => {
  router.push('/system/user')
}
</script>

<style lang="scss" scoped>
.profile-container {
  padding: 20px;
  .box-card {
    max-width: 900px;
    margin: 0 auto;
  }
  .actions {
    margin-top: 20px;
    display: flex;
    gap: 12px;
  }
}
</style>

<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="(stat, index) in statList" :key="index">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="24">
                <Document v-if="stat.icon === 'Document'" />
                <DataLine v-else-if="stat.icon === 'DataLine'" />
                <Folder v-else-if="stat.icon === 'Folder'" />
                <Money v-else-if="stat.icon === 'Money'" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>欢迎使用高校科研管理系统</span>
            </div>
          </template>
          <div class="welcome-content">
            <p>系统功能正在开发中，敬请期待...</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Document, DataLine, Folder, Money } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const statList = ref([
  {
    label: '科研项目',
    value: 0,
    icon: 'Document',
    color: '#409EFF'
  },
  {
    label: '科研成果',
    value: 0,
    icon: 'DataLine',
    color: '#67C23A'
  },
  {
    label: '待办事项',
    value: 0,
    icon: 'Folder',
    color: '#E6A23C'
  },
  {
    label: '科研经费',
    value: 0,
    icon: 'Money',
    color: '#F56C6C'
  }
])
</script>

<style lang="scss" scoped>
.dashboard-container {
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      gap: 20px;

      .stat-icon {
        width: 60px;
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 8px;
        color: #fff;
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #333;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 14px;
          color: #999;
        }
      }
    }
  }

  .card-header {
    font-size: 16px;
    font-weight: 600;
  }

  .welcome-content {
    padding: 20px;
    text-align: center;
    color: #666;
  }
}
</style>


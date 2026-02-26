<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="stat in statList" :key="stat.key">
        <el-card class="stat-card" shadow="hover" v-loading="loading && stat.loading">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="24">
                <Document v-if="stat.icon === 'Document'" />
                <DataLine v-else-if="stat.icon === 'DataLine'" />
                <Folder v-else-if="stat.icon === 'Folder'" />
                <CircleCheck v-else-if="stat.icon === 'CircleCheck'" />
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
              <el-button link type="primary" @click="loadDashboardData">刷新数据</el-button>
            </div>
          </template>
          <div class="welcome-content">
            <p>看板数据来自真实业务接口（项目 / 论文 / 专利 / 著作 / 获奖），不包含经费统计。</p>
            <p class="update-time">最近更新时间：{{ updateTime || '-' }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, DataLine, Folder, CircleCheck } from '@element-plus/icons-vue'
import { listProjects } from '@/api/project'
import { listPapers } from '@/api/paper'
import { listPatents } from '@/api/patent'
import { listAwards } from '@/api/award'
import { listBooks } from '@/api/book'

const loading = ref(false)
const updateTime = ref('')

const statList = ref([
  {
    key: 'projectTotal',
    label: '科研项目总数',
    value: '--',
    icon: 'Document',
    color: '#409EFF',
    loading: true
  },
  {
    key: 'outcomeTotal',
    label: '科研成果总数',
    value: '--',
    icon: 'DataLine',
    color: '#67C23A',
    loading: true
  },
  {
    key: 'todoApproval',
    label: '待审批项目',
    value: '--',
    icon: 'Folder',
    color: '#E6A23C',
    loading: true
  },
  {
    key: 'approvedProject',
    label: '已立项项目',
    value: '--',
    icon: 'CircleCheck',
    color: '#909399',
    loading: true
  }
])

const setStatValue = (key, value) => {
  const target = statList.value.find(item => item.key === key)
  if (target) {
    target.value = value
  }
}

const fetchTotal = async (api, extraParams = {}) => {
  const res = await api({ pageNum: 1, pageSize: 1, ...extraParams })
  if (res.code !== 200 || !res.data) {
    throw new Error(res.msg || '数据获取失败')
  }
  return Number(res.data.total || 0)
}

const loadDashboardData = async () => {
  loading.value = true
  try {
    const [projectTotal, paperTotal, patentTotal, awardTotal, bookTotal, todoApproval, approvedProject] = await Promise.all([
      fetchTotal(listProjects),
      fetchTotal(listPapers),
      fetchTotal(listPatents),
      fetchTotal(listAwards),
      fetchTotal(listBooks),
      fetchTotal(listProjects, { projectStatus: 'submitted' }),
      fetchTotal(listProjects, { projectStatus: 'approved' })
    ])

    setStatValue('projectTotal', projectTotal)
    setStatValue('outcomeTotal', paperTotal + patentTotal + awardTotal + bookTotal)
    setStatValue('todoApproval', todoApproval)
    setStatValue('approvedProject', approvedProject)

    updateTime.value = new Date().toLocaleString('zh-CN', { hour12: false })
  } catch (error) {
    ElMessage.error(error.message || '看板数据加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboardData()
})
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
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .welcome-content {
    padding: 20px;
    text-align: center;
    color: #666;

    .update-time {
      margin-top: 10px;
      font-size: 13px;
      color: #909399;
    }
  }
}
</style>

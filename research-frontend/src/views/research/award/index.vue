<template>
  <div class="award-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>获奖管理</span>
          <el-button type="primary" @click="goAdd" v-permission="'research:award:add'">新增获奖</el-button>
        </div>
      </template>

      <el-form :model="query" inline class="query-form">
        <el-form-item label="获奖名称">
          <el-input v-model="query.awardName" placeholder="请输入获奖名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="获奖人">
          <el-input v-model="query.awardees" placeholder="请输入获奖人" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="获奖类型">
          <el-select v-model="query.awardType" placeholder="请选择" clearable style="width: 150px">
            <el-option v-for="type in typeOptions" :key="type" :label="type" :value="type" />
          </el-select>
        </el-form-item>
        <el-form-item label="获奖等级">
          <el-select v-model="query.awardLevel" placeholder="请选择" clearable style="width: 150px">
            <el-option v-for="level in levelOptions" :key="level" :label="level" :value="level" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="有效" value="active" />
            <el-option label="无效" value="inactive" />
          </el-select>
        </el-form-item>
        <el-form-item label="获奖日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="awardName" label="获奖名称" min-width="250" show-overflow-tooltip />
        <el-table-column prop="awardees" label="获奖人/团队" min-width="150" show-overflow-tooltip />
        <el-table-column prop="awardType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.awardType)">{{ row.awardType || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="awardLevel" label="等级" width="120">
          <template #default="{ row }">
            <el-tag :type="levelTag(row.awardLevel)">{{ row.awardLevel || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="awardDate" label="获奖日期" width="120">
          <template #default="{ row }">{{ formatDate(row.awardDate) }}</template>
        </el-table-column>
        <el-table-column prop="awardingUnit" label="颁奖单位" min-width="180" show-overflow-tooltip />
        <el-table-column prop="certificateNo" label="证书编号" width="150" show-overflow-tooltip />
        <el-table-column prop="bonus" label="奖金(万元)" width="120">
          <template #default="{ row }">{{ row.bonus || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '有效' : '无效' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="goEdit(row)" v-permission="'research:award:edit'">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="'research:award:remove'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <Pagination :total="total" :page-num="pageNum" :page-size="pageSize" @pagination-change="handlePage" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import Pagination from '@/components/Pagination/index.vue'
import { listAwards, deleteAwards } from '@/api/award'

const router = useRouter()

const loading = ref(false)
const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const query = reactive({
  awardName: '',
  awardees: '',
  awardType: '',
  awardLevel: '',
  status: '',
  beginTime: '',
  endTime: ''
})

const dateRange = ref(null)

const typeOptions = ['国家级', '省部级', '校级']
const levelOptions = ['一等奖', '二等奖', '三等奖']

const handleDateRangeChange = (dates) => {
  if (dates && dates.length === 2) {
    query.beginTime = dates[0]
    query.endTime = dates[1]
  } else {
    query.beginTime = ''
    query.endTime = ''
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await listAwards({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...query
    })
    if (res.code === 200) {
      list.value = res.data.records || []
      total.value = res.data.total || 0
      pageNum.value = res.data.pageNum || pageNum.value
      pageSize.value = res.data.pageSize || pageSize.value
    } else {
      ElMessage.error(res.msg || '查询失败')
    }
  } catch (e) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  pageNum.value = 1
  fetchList()
}

const handleReset = () => {
  query.awardName = ''
  query.awardees = ''
  query.awardType = ''
  query.awardLevel = ''
  query.status = ''
  query.beginTime = ''
  query.endTime = ''
  dateRange.value = null
  pageNum.value = 1
  fetchList()
}

const handlePage = ({ pageNum: p, pageSize: s }) => {
  pageNum.value = p
  pageSize.value = s
  fetchList()
}

const goAdd = () => {
  router.push('/research/award/form')
}

const goEdit = (row) => {
  router.push(`/research/award/form/${row.awardId}`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除获奖「${row.awardName}」？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deleteAwards(row.awardId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  }).catch(() => {})
}

const typeTag = (type) => {
  const map = { '国家级': 'danger', '省部级': 'warning', '校级': 'success' }
  return map[type] || 'info'
}

const levelTag = (level) => {
  const map = { '一等奖': 'danger', '二等奖': 'warning', '三等奖': 'success' }
  return map[level] || 'info'
}

const formatDate = (date) => {
  if (!date) return '-'
  return date
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.award-container {
  padding: 20px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.query-form {
  margin-bottom: 16px;
}
</style>


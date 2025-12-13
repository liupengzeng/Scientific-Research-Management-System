<template>
  <div class="patent-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>专利管理</span>
          <el-button type="primary" @click="goAdd" v-permission="'research:patent:add'">新增专利</el-button>
        </div>
      </template>

      <el-form :model="query" inline class="query-form">
        <el-form-item label="专利名称">
          <el-input v-model="query.patentName" placeholder="请输入专利名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="发明人">
          <el-input v-model="query.inventors" placeholder="请输入发明人" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="专利类型">
          <el-select v-model="query.patentType" placeholder="请选择" clearable style="width: 150px">
            <el-option v-for="type in typeOptions" :key="type" :label="type" :value="type" />
          </el-select>
        </el-form-item>
        <el-form-item label="专利状态">
          <el-select v-model="query.patentStatus" placeholder="请选择" clearable style="width: 120px">
            <el-option v-for="status in statusOptions" :key="status" :label="status" :value="status" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="query.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="草稿" value="draft" />
            <el-option label="已提交" value="submitted" />
            <el-option label="已审核" value="approved" />
            <el-option label="已驳回" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联项目">
          <el-input-number v-model="query.projectId" :min="1" placeholder="项目ID" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="申请日期">
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
        <el-table-column prop="patentName" label="专利名称" min-width="250" show-overflow-tooltip />
        <el-table-column prop="inventors" label="发明人" min-width="150" show-overflow-tooltip />
        <el-table-column prop="patentType" label="类型" width="140">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.patentType)">{{ row.patentType || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="patentNo" label="专利号" width="150" show-overflow-tooltip />
        <el-table-column prop="patentStatus" label="专利状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.patentStatus)">{{ row.patentStatus || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicationDate" label="申请日期" width="120">
          <template #default="{ row }">{{ formatDate(row.applicationDate) }}</template>
        </el-table-column>
        <el-table-column prop="authorizationDate" label="授权日期" width="120">
          <template #default="{ row }">{{ formatDate(row.authorizationDate) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="approvalStatusTag(row.status)">{{ approvalStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="goEdit(row)" v-permission="'research:patent:edit'">编辑</el-button>
            <el-button link type="success" size="small" @click="handleSubmit(row)" :disabled="row.status !== 'draft'" v-permission="'research:patent:submit'">提交</el-button>
            <el-button link type="warning" size="small" @click="goApproval(row)" v-if="row.status === 'submitted'" v-permission="'research:patent:approve'">审核</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="'research:patent:remove'">删除</el-button>
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
import { listPatents, submitPatent, deletePatents } from '@/api/patent'

const router = useRouter()

const loading = ref(false)
const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const query = reactive({
  patentName: '',
  inventors: '',
  patentType: '',
  patentStatus: '',
  status: '',
  projectId: null,
  beginTime: '',
  endTime: ''
})

const dateRange = ref(null)

const typeOptions = ['发明专利', '实用新型专利', '外观设计专利', '其他']
const statusOptions = ['申请中', '已授权', '已转化', '已失效', '其他']

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
    const res = await listPatents({
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
  query.patentName = ''
  query.inventors = ''
  query.patentType = ''
  query.patentStatus = ''
  query.status = ''
  query.projectId = null
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
  router.push('/research/patent/form')
}

const goEdit = (row) => {
  router.push(`/research/patent/form/${row.patentId}`)
}

const goApproval = (row) => {
  router.push(`/research/patent/approval/${row.patentId}`)
}

const handleSubmit = (row) => {
  ElMessageBox.confirm(`确认提交专利「${row.patentName}」？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await submitPatent(row.patentId)
    if (res.code === 200) {
      ElMessage.success('提交成功')
      fetchList()
    } else {
      ElMessage.error(res.msg || '提交失败')
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除专利「${row.patentName}」？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deletePatents(row.patentId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  }).catch(() => {})
}

const typeTag = (type) => {
  const map = { '发明专利': 'danger', '实用新型专利': 'warning', '外观设计专利': 'success', '其他': 'info' }
  return map[type] || 'info'
}

const statusTag = (status) => {
  const map = { '申请中': 'info', '已授权': 'success', '已转化': 'warning', '已失效': 'danger', '其他': 'info' }
  return map[status] || 'info'
}

const approvalStatusTag = (status) => {
  const map = { draft: 'info', submitted: 'warning', approved: 'success', rejected: 'danger' }
  return map[status] || 'info'
}

const approvalStatusLabel = (status) => {
  const map = { draft: '草稿', submitted: '已提交', approved: '已审核', rejected: '已驳回' }
  return map[status] || status
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
.patent-container {
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


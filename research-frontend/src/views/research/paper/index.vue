<template>
  <div class="paper-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>论文管理</span>
          <el-button type="primary" @click="goAdd" v-permission="'research:paper:add'">新增论文</el-button>
        </div>
      </template>

      <el-form :model="query" inline class="query-form">
        <el-form-item label="论文标题">
          <el-input v-model="query.paperTitle" placeholder="请输入论文标题" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="query.authors" placeholder="请输入作者" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="期刊/会议">
          <el-input v-model="query.journalName" placeholder="请输入期刊/会议名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="论文级别">
          <el-select v-model="query.paperLevel" placeholder="请选择" clearable style="width: 150px">
            <el-option v-for="level in levelOptions" :key="level" :label="level" :value="level" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="草稿" value="draft" />
            <el-option label="已提交" value="submitted" />
            <el-option label="已审核" value="approved" />
            <el-option label="已驳回" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="发表时间">
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
        <el-table-column prop="paperTitle" label="论文标题" min-width="250" show-overflow-tooltip />
        <el-table-column prop="authors" label="作者" min-width="150" show-overflow-tooltip />
        <el-table-column prop="journalName" label="期刊/会议" min-width="180" show-overflow-tooltip />
        <el-table-column prop="paperLevel" label="级别" width="120">
          <template #default="{ row }">
            <el-tag :type="levelTag(row.paperLevel)">{{ row.paperLevel || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishDate" label="发表时间" width="120">
          <template #default="{ row }">{{ formatDate(row.publishDate) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="impactFactor" label="影响因子" width="100" />
        <el-table-column prop="citationCount" label="引用次数" width="100" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="goEdit(row)" v-permission="'research:paper:edit'">编辑</el-button>
            <el-button link type="success" size="small" @click="handleSubmit(row)" :disabled="row.status !== 'draft'" v-permission="'research:paper:submit'">提交</el-button>
            <el-button link type="warning" size="small" @click="goApproval(row)" v-if="row.status === 'submitted'" v-permission="'research:paper:approve'">审核</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="'research:paper:remove'">删除</el-button>
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
import { listPapers, submitPaper, deletePapers } from '@/api/paper'

const router = useRouter()

const loading = ref(false)
const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const query = reactive({
  paperTitle: '',
  authors: '',
  journalName: '',
  paperLevel: '',
  status: '',
  beginTime: '',
  endTime: ''
})

const dateRange = ref(null)

const levelOptions = ['SCI', 'EI', '核心期刊', '普通期刊', '会议论文', '其他']

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
    const res = await listPapers({
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
  query.paperTitle = ''
  query.authors = ''
  query.journalName = ''
  query.paperLevel = ''
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
  router.push('/research/paper/form')
}

const goEdit = (row) => {
  router.push(`/research/paper/form/${row.paperId}`)
}

const goApproval = (row) => {
  router.push(`/research/paper/approval/${row.paperId}`)
}

const handleSubmit = (row) => {
  ElMessageBox.confirm(`确认提交论文「${row.paperTitle}」？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await submitPaper(row.paperId)
    if (res.code === 200) {
      ElMessage.success('提交成功')
      fetchList()
    } else {
      ElMessage.error(res.msg || '提交失败')
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除论文「${row.paperTitle}」？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deletePapers(row.paperId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  }).catch(() => {})
}

const levelTag = (level) => {
  const map = { SCI: 'danger', EI: 'warning', '核心期刊': 'success', '普通期刊': 'info', '会议论文': '', '其他': 'info' }
  return map[level] || 'info'
}

const statusTag = (status) => {
  const map = { draft: 'info', submitted: 'warning', approved: 'success', rejected: 'danger' }
  return map[status] || 'info'
}

const statusLabel = (status) => {
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
.paper-container {
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


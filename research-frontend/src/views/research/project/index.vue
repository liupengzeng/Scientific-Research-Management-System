<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>项目查询</span>
        </div>
      </template>
      <el-form :model="query" label-width="90px" class="query-form" @keyup.enter.native="handleQuery">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="项目名称">
              <el-input v-model="query.projectName" placeholder="请输入项目名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="类型">
              <el-select v-model="query.projectTypeId" placeholder="请选择" clearable filterable>
                <el-option v-for="t in typeOptions" :key="t.typeId" :label="t.typeName" :value="t.typeId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态">
              <el-select v-model="query.projectStatus" placeholder="请选择" clearable>
                <el-option label="草稿" value="draft" />
                <el-option label="已提交" value="submitted" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6" class="query-actions">
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button type="primary" @click="goAdd" v-permission="'research:project:add'">新增</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card class="mt-16">
      <template #header>
        <div class="card-header">
          <span>项目列表</span>
          <div class="actions-bar">
            <el-button type="danger" :disabled="!selected.length" @click="handleBatchDelete" v-permission="'research:project:remove'">批量删除</el-button>
            <el-button @click="handleQuery">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="projectNo" label="项目编号" min-width="140" />
        <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="projectTypeId" label="类型" width="120">
          <template #default="{ row }">{{ formatType(row.projectTypeId) }}</template>
        </el-table-column>
        <el-table-column prop="leaderId" label="负责人ID" width="110" />
        <el-table-column prop="projectStatus" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.projectStatus)">{{ statusLabel(row.projectStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalBudget" label="总预算" width="120" />
        <el-table-column prop="approvedAmount" label="批准经费" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="goDetail(row)">详情</el-button>
            <el-button link type="primary" size="small" @click="goEdit(row)" v-permission="'research:project:edit'">编辑</el-button>
            <el-button link type="success" size="small" @click="handleSubmit(row)" :disabled="row.projectStatus !== 'draft'" v-permission="'research:project:submit'">提交</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="'research:project:remove'">删除</el-button>
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
import Pagination from '@/components/Pagination/index.vue'
import { listProjects, submitProject, deleteProjects } from '@/api/project'
import { listProjectTypes } from '@/api/projectType'
import { useRouter } from 'vue-router'

const router = useRouter()
const query = reactive({ projectName: '', projectTypeId: null, projectStatus: '' })
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const list = ref([])
const loading = ref(false)
const selected = ref([])
const typeOptions = ref([])

const fetchTypes = async () => {
  const res = await listProjectTypes({ pageNum: 1, pageSize: 200 })
  if (res.code === 200) {
    typeOptions.value = res.data.records || []
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await listProjects({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...query
    })
    if (res.code === 200) {
      list.value = res.data.records
      total.value = res.data.total
      pageNum.value = res.data.pageNum
      pageSize.value = res.data.pageSize
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
  query.projectName = ''
  query.projectTypeId = null
  query.projectStatus = ''
  pageNum.value = 1
  fetchList()
}

const handlePage = ({ pageNum: p, pageSize: s }) => {
  pageNum.value = p
  pageSize.value = s
  fetchList()
}

const handleSelectionChange = (sel) => {
  selected.value = sel
}

const goAdd = () => {
  router.push('/research/project/form')
}

const goDetail = (row) => {
  router.push(`/research/project/detail/${row.projectId}`)
}

const goEdit = (row) => {
  router.push({ path: '/research/project/form', query: { id: row.projectId } })
}

const handleSubmit = (row) => {
  ElMessageBox.confirm(`确认提交项目「${row.projectName}」？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await submitProject(row.projectId)
    if (res.code === 200) {
      ElMessage.success('提交成功')
      fetchList()
    } else {
      ElMessage.error(res.msg || '提交失败')
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除项目「${row.projectName}」？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deleteProjects(row.projectId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  if (!selected.value.length) return
  const ids = selected.value.map(i => i.projectId).join(',')
  ElMessageBox.confirm(`确认删除选中的 ${selected.value.length} 个项目？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deleteProjects(ids)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  }).catch(() => {})
}

const statusLabel = (s) => {
  if (s === 'submitted') return '已提交'
  return '草稿'
}

const statusTag = (s) => {
  if (s === 'submitted') return 'success'
  return 'info'
}

const formatType = (id) => {
  const t = typeOptions.value.find(i => i.typeId === id)
  return t ? t.typeName : '-'
}

onMounted(() => {
  fetchTypes()
  fetchList()
})
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.query-form { margin-top: 4px; }
.query-actions { display: flex; align-items: center; gap: 10px; margin-top: 4px; }
.actions-bar { display: flex; gap: 12px; align-items: center; flex-wrap: wrap; }
.mt-16 { margin-top: 16px; }
</style>


<template>
  <div class="oplog-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>操作日志查询</span>
        </div>
      </template>
      <el-form :model="query" label-width="100px" class="query-form">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="标题">
              <el-input v-model="query.title" placeholder="请输入标题" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="业务类型">
              <el-select v-model="query.businessType" placeholder="请选择" clearable>
                <el-option label="新增" :value="1" />
                <el-option label="修改" :value="2" />
                <el-option label="删除" :value="3" />
                <el-option label="授权" :value="4" />
                <el-option label="重置密码" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态">
              <el-select v-model="query.status" placeholder="请选择" clearable>
                <el-option label="成功" :value="0" />
                <el-option label="失败" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item>
              <el-button type="primary" @click="handleQuery">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card class="box-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>操作日志列表</span>
          <div class="actions-bar">
            <el-button type="danger" :disabled="selected.length===0" @click="handleBatchDelete">批量删除</el-button>
            <el-button @click="handleQuery">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe style="width:100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="logId" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="180" />
        <el-table-column prop="businessType" label="业务类型" width="120" />
        <el-table-column prop="method" label="方法" width="220" />
        <el-table-column prop="requestMethod" label="HTTP" width="90" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="requestUrl" label="请求地址" min-width="240" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{row}">
            <el-tag :type="row.status===0?'success':'danger'">{{ row.status===0?'成功':'失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operationTime" label="时间" width="180">
          <template #default="{row}">{{ formatTime(row.operationTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="handleView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <Pagination :total="total" :page-num="pageNum" :page-size="pageSize" @pagination-change="handlePage" />
    </el-card>

    <el-dialog v-model="detailVisible" title="日志详情" width="700px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="标题">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ detail.businessType }}</el-descriptions-item>
        <el-descriptions-item label="方法">{{ detail.method }}</el-descriptions-item>
        <el-descriptions-item label="HTTP">{{ detail.requestMethod }}</el-descriptions-item>
        <el-descriptions-item label="请求地址">{{ detail.requestUrl }}</el-descriptions-item>
        <el-descriptions-item label="请求参数"><pre class="json-block">{{ detail.requestParam }}</pre></el-descriptions-item>
        <el-descriptions-item label="响应结果"><pre class="json-block">{{ detail.responseResult }}</pre></el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status===0?'成功':'失败' }}</el-descriptions-item>
        <el-descriptions-item label="错误信息">{{ detail.errorMsg || '-' }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ formatTime(detail.operationTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/Pagination/index.vue'
import { getOperationLogList, getOperationLogDetail, deleteOperationLogs } from '@/api/operationLog'

const query = reactive({ title: '', businessType: null, status: null })
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const list = ref([])
const loading = ref(false)
const selected = ref([])

const detailVisible = ref(false)
const detail = reactive({})

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getOperationLogList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      title: query.title,
      businessType: query.businessType,
      status: query.status
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

const handleReset = () => {
  query.title = ''
  query.businessType = null
  query.status = null
  pageNum.value = 1
  handleQuery()
}

const handlePage = ({ pageNum: p, pageSize: s }) => {
  pageNum.value = p
  pageSize.value = s
  handleQuery()
}

const handleSelectionChange = (sel) => {
  selected.value = sel
}

const handleView = async (row) => {
  const res = await getOperationLogDetail(row.logId)
  if (res.code === 200) {
    Object.assign(detail, res.data)
    detailVisible.value = true
  }
}

const handleBatchDelete = () => {
  if (selected.value.length === 0) {
    ElMessage.warning('请先选择要删除的日志')
    return
  }
  const ids = selected.value.map(i => i.logId).join(',')
  ElMessageBox.confirm(`确定删除选中的 ${selected.value.length} 条日志吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deleteOperationLogs(ids)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      handleQuery()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  }).catch(() => {})
}

const formatTime = (t) => {
  if (!t) return '-'
  const d = new Date(t)
  return d.toLocaleString('zh-CN')
}

handleQuery()
</script>

<style scoped>
.oplog-container { padding: 20px; }
.box-card { border-radius: 8px; }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.actions-bar { display: flex; flex-wrap: wrap; gap: 12px; }
.query-form .el-col { margin-bottom: 10px; }
.json-block { white-space: pre-wrap; word-break: break-all; background: #f7f8fa; padding: 8px; border-radius: 6px; }
</style>
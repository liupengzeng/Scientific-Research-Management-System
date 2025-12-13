<template>
  <div class="book-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>著作管理</span>
          <el-button type="primary" @click="goAdd" v-permission="'research:book:add'">新增著作</el-button>
        </div>
      </template>

      <el-form :model="query" inline class="query-form">
        <el-form-item label="书名">
          <el-input v-model="query.bookTitle" placeholder="请输入书名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="query.authors" placeholder="请输入作者" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="query.publisher" placeholder="请输入出版社" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="著作类型">
          <el-select v-model="query.bookType" placeholder="请选择" clearable style="width: 150px">
            <el-option v-for="type in typeOptions" :key="type" :label="type" :value="type" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="有效" value="active" />
            <el-option label="无效" value="inactive" />
          </el-select>
        </el-form-item>
        <el-form-item label="出版日期">
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
        <el-table-column prop="bookTitle" label="书名" min-width="250" show-overflow-tooltip />
        <el-table-column prop="authors" label="作者" min-width="150" show-overflow-tooltip />
        <el-table-column prop="publisher" label="出版社" min-width="180" show-overflow-tooltip />
        <el-table-column prop="bookType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.bookType)">{{ row.bookType || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishDate" label="出版日期" width="120">
          <template #default="{ row }">{{ formatDate(row.publishDate) }}</template>
        </el-table-column>
        <el-table-column prop="isbn" label="ISBN" width="150" show-overflow-tooltip />
        <el-table-column prop="pageCount" label="页数" width="100" />
        <el-table-column prop="wordCount" label="字数(万字)" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '有效' : '无效' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="goEdit(row)" v-permission="'research:book:edit'">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="'research:book:remove'">删除</el-button>
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
import { listBooks, deleteBooks } from '@/api/book'

const router = useRouter()

const loading = ref(false)
const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const query = reactive({
  bookTitle: '',
  authors: '',
  publisher: '',
  bookType: '',
  status: '',
  beginTime: '',
  endTime: ''
})

const dateRange = ref(null)

const typeOptions = ['专著', '译著', '教材', '工具书', '其他']

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
    const res = await listBooks({
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
  query.bookTitle = ''
  query.authors = ''
  query.publisher = ''
  query.bookType = ''
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
  router.push('/research/book/form')
}

const goEdit = (row) => {
  router.push(`/research/book/form/${row.bookId}`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除著作「${row.bookTitle}」？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deleteBooks(row.bookId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  }).catch(() => {})
}

const typeTag = (type) => {
  const map = { 专著: 'danger', 译著: 'warning', 教材: 'success', 工具书: 'info', 其他: 'info' }
  return map[type] || 'info'
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
.book-container {
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


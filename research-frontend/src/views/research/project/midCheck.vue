<template>
  <div class="midcheck-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>项目中检</span>
          <el-button type="primary" @click="handleAdd" v-permission="'research:project:midCheck'">提交中检</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="checkTitle" label="中检标题" min-width="200" />
        <el-table-column prop="checkDate" label="中检日期" width="120">
          <template #default="{ row }">{{ formatDate(row.checkDate) }}</template>
        </el-table-column>
        <el-table-column prop="resultStatus" label="审核状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.resultStatus)">{{ statusLabel(row.resultStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="审核意见" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="提交时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <Pagination :total="total" :page-num="pageNum" :page-size="pageSize" @pagination-change="handlePage" />
    </el-card>

    <!-- 提交中检对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="项目名称">
          <el-input v-model="projectName" disabled />
        </el-form-item>
        <el-form-item label="中检标题" prop="checkTitle">
          <el-input v-model="form.checkTitle" placeholder="请输入中检标题" />
        </el-form-item>
        <el-form-item label="中检日期" prop="checkDate">
          <el-date-picker v-model="form.checkDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="中检内容" prop="checkContent">
          <el-input v-model="form.checkContent" type="textarea" :rows="6" placeholder="请输入中检内容" />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            :limit="5"
            :file-list="fileList"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">支持上传多个文件，单个文件不超过10MB</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewVisible" title="中检详情" width="700px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="中检标题">{{ detail.checkTitle }}</el-descriptions-item>
        <el-descriptions-item label="中检日期">{{ formatDate(detail.checkDate) }}</el-descriptions-item>
        <el-descriptions-item label="中检内容">
          <pre class="content-text">{{ detail.checkContent }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="附件">
          <div v-if="detail.attachment">
            <a :href="detail.attachment" target="_blank" v-for="(url, idx) in attachmentList" :key="idx">
              {{ url.split('/').pop() }}
            </a>
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="statusTag(detail.resultStatus)">{{ statusLabel(detail.resultStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核意见">{{ detail.comment || '-' }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ formatTime(detail.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import Pagination from '@/components/Pagination/index.vue'
import { getMidCheckList, submitMidCheck } from '@/api/projectCheck'
import { getProject } from '@/api/project'
import { uploadFile } from '@/api/project'
import { getToken } from '@/utils/auth'

const route = useRoute()
const projectId = computed(() => Number(route.query.projectId) || route.params.projectId)

const loading = ref(false)
const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const dialogTitle = ref('提交中检')
const form = reactive({
  projectId: null,
  checkTitle: '',
  checkDate: '',
  checkContent: '',
  attachment: ''
})
const formRef = ref(null)
const submitting = ref(false)
const projectName = ref('')
const fileList = ref([])

const viewVisible = ref(false)
const detail = ref({})

const uploadUrl = computed(() => {
  return import.meta.env.VITE_APP_BASE_API + '/research/project/upload'
})

const uploadHeaders = computed(() => {
  return {
    Authorization: 'Bearer ' + getToken()
  }
})

const attachmentList = computed(() => {
  if (!detail.value.attachment) return []
  return detail.value.attachment.split(',').filter(Boolean)
})

const rules = {
  checkTitle: [{ required: true, message: '请输入中检标题', trigger: 'blur' }],
  checkDate: [{ required: true, message: '请选择中检日期', trigger: 'change' }],
  checkContent: [{ required: true, message: '请输入中检内容', trigger: 'blur' }]
}

const handleQuery = async () => {
  if (!projectId.value) {
    ElMessage.warning('缺少项目ID')
    return
  }
  loading.value = true
  try {
    const res = await getMidCheckList(projectId.value, {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    if (res.code === 200) {
      list.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '查询失败')
    }
  } catch (e) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handlePage = ({ pageNum: p, pageSize: s }) => {
  pageNum.value = p
  pageSize.value = s
  handleQuery()
}

const handleAdd = async () => {
  if (!projectId.value) {
    ElMessage.warning('缺少项目ID')
    return
  }
  // 获取项目信息
  try {
    const res = await getProject(projectId.value)
    if (res.code === 200) {
      projectName.value = res.data.projectName || ''
    }
  } catch (e) {
    console.error('获取项目信息失败', e)
  }
  form.projectId = projectId.value
  form.checkTitle = ''
  form.checkDate = ''
  form.checkContent = ''
  form.attachment = ''
  fileList.value = []
  dialogVisible.value = true
}

const beforeUpload = (file) => {
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB')
    return false
  }
  return true
}

const handleUploadSuccess = (response, file) => {
  if (response.code === 200) {
    const url = response.data || response.data.url
    if (form.attachment) {
      form.attachment += ',' + url
    } else {
      form.attachment = url
    }
    ElMessage.success('文件上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await submitMidCheck(form)
        if (res.code === 200) {
          ElMessage.success('提交成功')
          dialogVisible.value = false
          handleQuery()
        } else {
          ElMessage.error(res.msg || '提交失败')
        }
      } catch (e) {
        ElMessage.error('提交失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleView = (row) => {
  detail.value = { ...row }
  viewVisible.value = true
}

const statusTag = (status) => {
  const map = { pending: 'info', passed: 'success', rejected: 'danger' }
  return map[status] || 'info'
}

const statusLabel = (status) => {
  const map = { pending: '待审核', passed: '通过', rejected: '不通过' }
  return map[status] || status
}

const formatDate = (date) => {
  if (!date) return '-'
  return date
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  if (projectId.value) {
    handleQuery()
  }
})
</script>

<style scoped>
.midcheck-container {
  padding: 20px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.content-text {
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
}
</style>


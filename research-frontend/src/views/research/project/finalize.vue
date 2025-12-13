<template>
  <div class="finalize-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>项目结题</span>
          <el-button type="primary" @click="handleAdd" v-permission="'research:project:finalize'">提交结题</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="finalTitle" label="结题标题" min-width="200" />
        <el-table-column prop="submitDate" label="提交日期" width="120">
          <template #default="{ row }">{{ formatDate(row.submitDate) }}</template>
        </el-table-column>
        <el-table-column prop="acceptStatus" label="验收状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.acceptStatus)">{{ statusLabel(row.acceptStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="acceptorName" label="验收人" width="120" />
        <el-table-column prop="comment" label="验收意见" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="提交时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button
              link
              type="success"
              size="small"
              @click="handleAccept(row)"
              v-if="row.acceptStatus === 'pending'"
              v-permission="'research:project:finalize:accept'"
            >
              验收
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <Pagination :total="total" :page-num="pageNum" :page-size="pageSize" @pagination-change="handlePage" />
    </el-card>

    <!-- 提交结题对话框 -->
    <el-dialog v-model="dialogVisible" title="提交结题" width="800px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="项目名称">
          <el-input v-model="projectName" disabled />
        </el-form-item>
        <el-form-item label="结题标题" prop="finalTitle">
          <el-input v-model="form.finalTitle" placeholder="请输入结题标题" />
        </el-form-item>
        <el-form-item label="提交日期" prop="submitDate">
          <el-date-picker v-model="form.submitDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="结题内容" prop="finalContent">
          <el-input v-model="form.finalContent" type="textarea" :rows="6" placeholder="请输入结题内容" />
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
    <el-dialog v-model="viewVisible" title="结题详情" width="700px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="结题标题">{{ detail.finalTitle }}</el-descriptions-item>
        <el-descriptions-item label="提交日期">{{ formatDate(detail.submitDate) }}</el-descriptions-item>
        <el-descriptions-item label="结题内容">
          <pre class="content-text">{{ detail.finalContent }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="附件">
          <div v-if="detail.attachment">
            <a :href="detail.attachment" target="_blank" v-for="(url, idx) in attachmentList" :key="idx">
              {{ url.split('/').pop() }}
            </a>
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="验收状态">
          <el-tag :type="statusTag(detail.acceptStatus)">{{ statusLabel(detail.acceptStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="验收人">{{ detail.acceptorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="验收意见">{{ detail.comment || '-' }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ formatTime(detail.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 验收对话框 -->
    <el-dialog v-model="acceptVisible" title="结题验收" width="600px">
      <el-form :model="acceptForm" :rules="acceptRules" ref="acceptFormRef" label-width="100px">
        <el-form-item label="项目名称">
          <el-input v-model="currentProject.projectName" disabled />
        </el-form-item>
        <el-form-item label="验收决定" prop="decision">
          <el-radio-group v-model="acceptForm.decision">
            <el-radio label="passed">通过</el-radio>
            <el-radio label="rejected">不通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="验收意见" prop="comment">
          <el-input
            v-model="acceptForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入验收意见（必填）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="acceptVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAccept" :loading="accepting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import Pagination from '@/components/Pagination/index.vue'
import { getFinalList, submitFinal, acceptFinal } from '@/api/projectFinal'
import { getProject } from '@/api/project'
import { getToken } from '@/utils/auth'

const route = useRoute()
const projectId = computed(() => Number(route.query.projectId) || route.params.projectId)

const loading = ref(false)
const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const form = reactive({
  projectId: null,
  finalTitle: '',
  submitDate: '',
  finalContent: '',
  attachment: ''
})
const formRef = ref(null)
const submitting = ref(false)
const projectName = ref('')
const fileList = ref([])

const viewVisible = ref(false)
const detail = ref({})

const acceptVisible = ref(false)
const acceptForm = reactive({ projectId: null, decision: 'passed', comment: '' })
const acceptFormRef = ref(null)
const accepting = ref(false)
const currentProject = ref({})

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
  finalTitle: [{ required: true, message: '请输入结题标题', trigger: 'blur' }],
  submitDate: [{ required: true, message: '请选择提交日期', trigger: 'change' }],
  finalContent: [{ required: true, message: '请输入结题内容', trigger: 'blur' }]
}

const acceptRules = {
  decision: [{ required: true, message: '请选择验收决定', trigger: 'change' }],
  comment: [{ required: true, message: '请输入验收意见', trigger: 'blur' }]
}

const handleQuery = async () => {
  if (!projectId.value) {
    ElMessage.warning('缺少项目ID')
    return
  }
  loading.value = true
  try {
    const res = await getFinalList(projectId.value, {
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
  try {
    const res = await getProject(projectId.value)
    if (res.code === 200) {
      projectName.value = res.data.projectName || ''
    }
  } catch (e) {
    console.error('获取项目信息失败', e)
  }
  form.projectId = projectId.value
  form.finalTitle = ''
  form.submitDate = ''
  form.finalContent = ''
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
        const res = await submitFinal(form)
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

const handleAccept = async (row) => {
  try {
    const res = await getProject(row.projectId)
    if (res.code === 200) {
      currentProject.value = res.data
    }
  } catch (e) {
    console.error('获取项目信息失败', e)
  }
  acceptForm.projectId = row.projectId
  acceptForm.decision = 'passed'
  acceptForm.comment = ''
  acceptVisible.value = true
}

const submitAccept = async () => {
  if (!acceptFormRef.value) return
  await acceptFormRef.value.validate(async (valid) => {
    if (valid) {
      accepting.value = true
      try {
        const res = await acceptFinal(acceptForm)
        if (res.code === 200) {
          ElMessage.success('验收成功')
          acceptVisible.value = false
          handleQuery()
        } else {
          ElMessage.error(res.msg || '验收失败')
        }
      } catch (e) {
        ElMessage.error('验收失败')
      } finally {
        accepting.value = false
      }
    }
  })
}

const statusTag = (status) => {
  const map = { pending: 'info', passed: 'success', rejected: 'danger' }
  return map[status] || 'info'
}

const statusLabel = (status) => {
  const map = { pending: '待验收', passed: '通过', rejected: '不通过' }
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
.finalize-container {
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


<template>
  <div class="approval-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>论文审核</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="待审核论文" name="pending">
          <el-table v-loading="loading" :data="pendingList" stripe>
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
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleApprove(row)" v-permission="'research:paper:approve'">审核</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane :label="`审核记录 (${paperId ? '当前论文' : ''})`" name="records">
          <el-table v-loading="recordLoading" :data="approvalRecords" stripe>
            <el-table-column prop="approverName" label="审批人" width="120" />
            <el-table-column prop="decision" label="审批决定" width="120">
              <template #default="{ row }">
                <el-tag :type="row.decision === 'approve' ? 'success' : 'danger'">
                  {{ row.decision === 'approve' ? '通过' : '驳回' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="comment" label="审批意见" min-width="200" show-overflow-tooltip />
            <el-table-column prop="finalFlag" label="是否终审" width="100">
              <template #default="{ row }">
                <el-tag :type="row.finalFlag === 1 ? 'warning' : 'info'">
                  {{ row.finalFlag === 1 ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="审批时间" width="180">
              <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog v-model="approvalVisible" title="论文审核" width="600px">
      <el-form :model="approvalForm" :rules="approvalRules" ref="approvalFormRef" label-width="100px">
        <el-form-item label="论文标题">
          <el-input v-model="currentPaper.paperTitle" disabled />
        </el-form-item>
        <el-form-item label="审批决定" prop="decision">
          <el-radio-group v-model="approvalForm.decision">
            <el-radio label="approve">通过</el-radio>
            <el-radio label="reject">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否终审" prop="finalFlag">
          <el-radio-group v-model="approvalForm.finalFlag">
            <el-radio :label="0">否</el-radio>
            <el-radio :label="1">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见" prop="comment">
          <el-input
            v-model="approvalForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入审批意见（必填）"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approvalVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApproval" :loading="approving">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { listPapers, approvePaper, getApprovalRecords } from '@/api/paper'

const route = useRoute()
const router = useRouter()
const paperId = computed(() => route.params.paperId ? Number(route.params.paperId) : null)

const activeTab = ref('pending')
const loading = ref(false)
const pendingList = ref([])

const recordLoading = ref(false)
const approvalRecords = ref([])

const approvalVisible = ref(false)
const approvalForm = reactive({
  paperId: null,
  decision: 'approve',
  comment: '',
  finalFlag: 0
})
const approvalFormRef = ref(null)
const approving = ref(false)
const currentPaper = ref({})

const approvalRules = {
  decision: [{ required: true, message: '请选择审批决定', trigger: 'change' }],
  comment: [{ required: true, message: '请输入审批意见', trigger: 'blur' }]
}

const levelTag = (level) => {
  const map = { SCI: 'danger', EI: 'warning', '核心期刊': 'success', '普通期刊': 'info', '会议论文': '', '其他': 'info' }
  return map[level] || 'info'
}

const formatDate = (date) => {
  if (!date) return '-'
  return date
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const fetchPendingList = async () => {
  loading.value = true
  try {
    const res = await listPapers({ status: 'submitted', pageNum: 1, pageSize: 1000 })
    if (res.code === 200) {
      pendingList.value = res.data.records || []
    } else {
      ElMessage.error(res.msg || '查询失败')
    }
  } catch (e) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const fetchApprovalRecords = async () => {
  if (!paperId.value) return
  recordLoading.value = true
  try {
    const res = await getApprovalRecords(paperId.value)
    if (res.code === 200) {
      approvalRecords.value = res.data || []
    } else {
      ElMessage.error(res.msg || '查询失败')
    }
  } catch (e) {
    ElMessage.error('查询失败')
  } finally {
    recordLoading.value = false
  }
}

const handleApprove = (row) => {
  currentPaper.value = { ...row }
  approvalForm.paperId = row.paperId
  approvalForm.decision = 'approve'
  approvalForm.comment = ''
  approvalForm.finalFlag = 0
  approvalVisible.value = true
}

const submitApproval = async () => {
  if (!approvalFormRef.value) return
  await approvalFormRef.value.validate(async (valid) => {
    if (valid) {
      approving.value = true
      try {
        const res = await approvePaper(approvalForm)
        if (res.code === 200) {
          ElMessage.success('审核成功')
          approvalVisible.value = false
          fetchPendingList()
          if (paperId.value) {
            fetchApprovalRecords()
          }
        } else {
          ElMessage.error(res.msg || '审核失败')
        }
      } catch (e) {
        ElMessage.error('审核失败')
      } finally {
        approving.value = false
      }
    }
  })
}

const goBack = () => {
  router.push('/research/paper')
}

const handleTabChange = (name) => {
  if (name === 'records' && paperId.value) {
    fetchApprovalRecords()
  }
}

onMounted(() => {
  fetchPendingList()
  if (paperId.value) {
    activeTab.value = 'records'
    fetchApprovalRecords()
  }
})
</script>

<style scoped>
.approval-container {
  padding: 20px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>


<template>
  <div class="approval-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>专利审核</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="待审核专利" name="pending">
          <el-table v-loading="loading" :data="pendingList" stripe>
            <el-table-column prop="patentName" label="专利名称" min-width="250" show-overflow-tooltip />
            <el-table-column prop="inventors" label="发明人" min-width="150" show-overflow-tooltip />
            <el-table-column prop="patentType" label="类型" width="140">
              <template #default="{ row }">
                <el-tag :type="typeTag(row.patentType)">{{ row.patentType || '-' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="patentNo" label="专利号" width="150" show-overflow-tooltip />
            <el-table-column prop="applicationDate" label="申请日期" width="120">
              <template #default="{ row }">{{ formatDate(row.applicationDate) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleApprove(row)" v-permission="'research:patent:approve'">审核</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane :label="`审核记录 (${patentId ? '当前专利' : ''})`" name="records">
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
    <el-dialog v-model="approvalVisible" title="专利审核" width="600px">
      <el-form :model="approvalForm" :rules="approvalRules" ref="approvalFormRef" label-width="100px">
        <el-form-item label="专利名称">
          <el-input v-model="currentPatent.patentName" disabled />
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
import { listPatents, approvePatent, getApprovalRecords } from '@/api/patent'

const route = useRoute()
const router = useRouter()
const patentId = computed(() => route.params.patentId ? Number(route.params.patentId) : null)

const activeTab = ref('pending')
const loading = ref(false)
const pendingList = ref([])

const recordLoading = ref(false)
const approvalRecords = ref([])

const approvalVisible = ref(false)
const approvalForm = reactive({
  patentId: null,
  decision: 'approve',
  comment: '',
  finalFlag: 0
})
const approvalFormRef = ref(null)
const approving = ref(false)
const currentPatent = ref({})

const approvalRules = {
  decision: [{ required: true, message: '请选择审批决定', trigger: 'change' }],
  comment: [{ required: true, message: '请输入审批意见', trigger: 'blur' }]
}

const typeTag = (type) => {
  const map = { '发明专利': 'danger', '实用新型专利': 'warning', '外观设计专利': 'success', '其他': 'info' }
  return map[type] || 'info'
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
    const res = await listPatents({ status: 'submitted', pageNum: 1, pageSize: 1000 })
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
  if (!patentId.value) return
  recordLoading.value = true
  try {
    const res = await getApprovalRecords(patentId.value)
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
  currentPatent.value = { ...row }
  approvalForm.patentId = row.patentId
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
        const res = await approvePatent(approvalForm)
        if (res.code === 200) {
          ElMessage.success('审核成功')
          approvalVisible.value = false
          fetchPendingList()
          if (patentId.value) {
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
  router.push('/research/patent')
}

onMounted(() => {
  fetchPendingList()
  if (patentId.value) {
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


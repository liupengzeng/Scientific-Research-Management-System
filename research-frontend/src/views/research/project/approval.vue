<template>
  <div class="approval-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>待审批项目</span>
          <el-button @click="handleQuery">刷新</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="projectNo" label="项目编号" min-width="140" />
        <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="projectTypeId" label="类型" width="120">
          <template #default="{ row }">{{ formatType(row.projectTypeId) }}</template>
        </el-table-column>
        <el-table-column prop="leaderId" label="负责人ID" width="110" />
        <el-table-column prop="totalBudget" label="总预算" width="120" />
        <el-table-column prop="approvedAmount" label="批准经费" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleApprove(row)" v-permission="'research:project:approve'">审批</el-button>
            <el-button link type="info" size="small" @click="handleViewRecords(row)">审批记录</el-button>
          </template>
        </el-table-column>
      </el-table>

      <Pagination :total="total" :page-num="pageNum" :page-size="pageSize" @pagination-change="handlePage" />
    </el-card>

    <!-- 审批对话框 -->
    <el-dialog v-model="approvalVisible" title="项目审批" width="600px">
      <el-form :model="approvalForm" :rules="approvalRules" ref="approvalFormRef" label-width="100px">
        <el-form-item label="项目名称">
          <el-input v-model="currentProject.projectName" disabled />
        </el-form-item>
        <el-form-item label="审批决定" prop="decision">
          <el-radio-group v-model="approvalForm.decision">
            <el-radio label="approve">通过</el-radio>
            <el-radio label="reject">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见" prop="comment">
          <el-input
            v-model="approvalForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入审批意见（必填）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approvalVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApproval" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 审批记录对话框 -->
    <el-dialog v-model="recordsVisible" title="审批记录" width="800px">
      <el-table :data="approvalRecords" stripe>
        <el-table-column prop="approverName" label="审批人" width="120" />
        <el-table-column prop="decision" label="决定" width="100">
          <template #default="{ row }">
            <el-tag :type="row.decision === 'approve' ? 'success' : 'danger'">
              {{ row.decision === 'approve' ? '通过' : '驳回' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="审批意见" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="审批时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import Pagination from '@/components/Pagination/index.vue'
import { listProjects } from '@/api/project'
import { approveProject, getApprovalRecords } from '@/api/projectApproval'
import { listProjectTypes } from '@/api/projectType'

const query = reactive({ projectStatus: 'submitted' })
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const list = ref([])
const loading = ref(false)
const typeOptions = ref([])

const approvalVisible = ref(false)
const approvalForm = reactive({ projectId: null, decision: 'approve', comment: '' })
const approvalFormRef = ref(null)
const submitting = ref(false)
const currentProject = ref({})

const recordsVisible = ref(false)
const approvalRecords = ref([])

const approvalRules = {
  decision: [{ required: true, message: '请选择审批决定', trigger: 'change' }],
  comment: [{ required: true, message: '请输入审批意见', trigger: 'blur' }]
}

const fetchTypes = async () => {
  try {
    const res = await listProjectTypes({ pageNum: 1, pageSize: 1000 })
    if (res.code === 200) {
      typeOptions.value = res.data.records || []
    }
  } catch (e) {
    console.error('加载项目类型失败', e)
  }
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await listProjects({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      projectStatus: query.projectStatus
    })
    if (res.code === 200) {
      list.value = res.data.records || []
      total.value = res.data.total || 0
      pageNum.value = res.data.pageNum || 1
      pageSize.value = res.data.pageSize || 10
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

const formatType = (typeId) => {
  const type = typeOptions.value.find(t => t.typeId === typeId)
  return type ? type.typeName : '-'
}

const handleApprove = (row) => {
  currentProject.value = row
  approvalForm.projectId = row.projectId
  approvalForm.decision = 'approve'
  approvalForm.comment = ''
  approvalVisible.value = true
}

const submitApproval = async () => {
  if (!approvalFormRef.value) return
  await approvalFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await approveProject(approvalForm)
        if (res.code === 200) {
          ElMessage.success('审批成功')
          approvalVisible.value = false
          handleQuery()
        } else {
          ElMessage.error(res.msg || '审批失败')
        }
      } catch (e) {
        ElMessage.error('审批失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleViewRecords = async (row) => {
  try {
    const res = await getApprovalRecords(row.projectId)
    if (res.code === 200) {
      approvalRecords.value = res.data || []
      recordsVisible.value = true
    } else {
      ElMessage.error(res.msg || '查询失败')
    }
  } catch (e) {
    ElMessage.error('查询失败')
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchTypes()
  handleQuery()
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


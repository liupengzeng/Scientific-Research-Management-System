<template>
  <div class="detail-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>项目详情</span>
          <div class="actions-bar">
            <el-button @click="goBack">返回</el-button>
            <el-button type="primary" @click="goEdit" v-permission="'research:project:edit'">编辑</el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="基本信息" name="info">
          <el-descriptions :column="2" border v-if="project">
            <el-descriptions-item label="项目编号">{{ project.projectNo }}</el-descriptions-item>
            <el-descriptions-item label="项目名称">{{ project.projectName }}</el-descriptions-item>
            <el-descriptions-item label="项目类型">{{ formatType(project.projectTypeId) }}</el-descriptions-item>
            <el-descriptions-item label="负责人ID">{{ project.leaderId }}</el-descriptions-item>
            <el-descriptions-item label="部门ID">{{ project.deptId }}</el-descriptions-item>
            <el-descriptions-item label="项目状态">
              <el-tag :type="statusTag(project.projectStatus)">{{ statusLabel(project.projectStatus) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="总预算(万)">{{ project.totalBudget }}</el-descriptions-item>
            <el-descriptions-item label="批准经费(万)">{{ project.approvedAmount }}</el-descriptions-item>
            <el-descriptions-item label="开始日期">{{ formatDate(project.startDate) }}</el-descriptions-item>
            <el-descriptions-item label="结束日期">{{ formatDate(project.endDate) }}</el-descriptions-item>
            <el-descriptions-item label="项目级别">{{ project.projectLevel || '-' }}</el-descriptions-item>
            <el-descriptions-item label="来源单位">{{ project.sourceUnit || '-' }}</el-descriptions-item>
            <el-descriptions-item label="关键词" :span="2">{{ project.keywords || '-' }}</el-descriptions-item>
            <el-descriptions-item label="研究内容" :span="2">
              <pre class="content-text">{{ project.researchContent || '-' }}</pre>
            </el-descriptions-item>
            <el-descriptions-item label="预期成果" :span="2">
              <pre class="content-text">{{ project.expectedResult || '-' }}</pre>
            </el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ project.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <el-tab-pane label="成员管理" name="member">
          <div class="member-section">
            <div class="member-header">
              <el-button type="primary" @click="handleAddMember" v-permission="'research:project:member:add'">添加成员</el-button>
              <el-button @click="handleQueryMembers">刷新</el-button>
            </div>

            <el-table v-loading="memberLoading" :data="memberList" stripe>
              <el-table-column prop="realName" label="姓名" width="120" />
              <el-table-column prop="username" label="用户名" width="120" />
              <el-table-column prop="deptName" label="部门" width="150" />
              <el-table-column prop="memberRole" label="角色" width="150">
                <template #default="{ row }">
                  <el-select
                    v-model="row.memberRole"
                    @change="handleRoleChange(row)"
                    v-permission="'research:project:member:edit'"
                    style="width: 100%"
                  >
                    <el-option v-for="role in roleOptions" :key="role" :label="role" :value="role" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column prop="workloadRatio" label="工作量(%)" width="120">
                <template #default="{ row }">
                  <el-input-number
                    v-model="row.workloadRatio"
                    :min="0"
                    :max="100"
                    :precision="2"
                    @change="handleWorkloadChange(row)"
                    v-permission="'research:project:member:edit'"
                    style="width: 100%"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="responsibility" label="承担任务" min-width="200" show-overflow-tooltip>
                <template #default="{ row }">
                  <el-input
                    v-model="row.responsibility"
                    @blur="handleResponsibilityChange(row)"
                    v-permission="'research:project:member:edit'"
                    placeholder="请输入承担任务"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="joinDate" label="加入日期" width="120">
                <template #default="{ row }">{{ formatDate(row.joinDate) }}</template>
              </el-table-column>
              <el-table-column label="操作" width="100" fixed="right">
                <template #default="{ row }">
                  <el-button link type="danger" size="small" @click="handleRemoveMember(row)" v-permission="'research:project:member:remove'">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 添加成员对话框 -->
    <el-dialog v-model="memberDialogVisible" title="添加成员" width="600px">
      <el-form :model="memberForm" :rules="memberRules" ref="memberFormRef" label-width="100px">
        <el-form-item label="项目名称">
          <el-input v-model="project?.projectName" disabled />
        </el-form-item>
        <el-form-item label="选择用户" prop="userId">
          <el-select v-model="memberForm.userId" placeholder="请选择用户" filterable clearable style="width: 100%">
            <el-option
              v-for="user in teacherOptions"
              :key="user.userId"
              :label="`${user.realName} (${user.username})`"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="成员角色" prop="memberRole">
          <el-select v-model="memberForm.memberRole" placeholder="请选择角色" style="width: 100%">
            <el-option v-for="role in roleOptions" :key="role" :label="role" :value="role" />
          </el-select>
        </el-form-item>
        <el-form-item label="工作量(%)" prop="workloadRatio">
          <el-input-number v-model="memberForm.workloadRatio" :min="0" :max="100" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="承担任务" prop="responsibility">
          <el-input v-model="memberForm.responsibility" type="textarea" :rows="3" placeholder="请输入承担任务" />
        </el-form-item>
        <el-form-item label="加入日期" prop="joinDate">
          <el-date-picker v-model="memberForm.joinDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="memberDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitMember" :loading="memberSubmitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { getProject } from '@/api/project'
import { getMemberList, addMember, updateMember, removeMember } from '@/api/projectMember'
import { getUserList } from '@/api/user'
import { listProjectTypes } from '@/api/projectType'

const route = useRoute()
const router = useRouter()
const projectId = computed(() => Number(route.params.projectId))

const activeTab = ref('info')
const project = ref(null)
const loading = ref(false)
const typeOptions = ref([])

const memberLoading = ref(false)
const memberList = ref([])
const memberDialogVisible = ref(false)
const memberForm = reactive({
  projectId: null,
  userId: null,
  memberRole: '普通参与人',
  workloadRatio: 0,
  responsibility: '',
  joinDate: ''
})
const memberFormRef = ref(null)
const memberSubmitting = ref(false)
const teacherOptions = ref([])

// 角色选项（可配置，这里先写死，后续可以从配置或字典表获取）
const roleOptions = ref(['主持人', '主要参与人', '普通参与人', '其他'])

const memberRules = {
  userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
  memberRole: [{ required: true, message: '请选择角色', trigger: 'change' }],
  workloadRatio: [{ required: true, message: '请输入工作量比例', trigger: 'blur' }]
}

const handleTabChange = (name) => {
  if (name === 'member' && projectId.value) {
    handleQueryMembers()
  }
}

const handleQuery = async () => {
  if (!projectId.value) {
    ElMessage.warning('缺少项目ID')
    return
  }
  loading.value = true
  try {
    const res = await getProject(projectId.value)
    if (res.code === 200) {
      project.value = res.data
    } else {
      ElMessage.error(res.msg || '查询失败')
    }
  } catch (e) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleQueryMembers = async () => {
  if (!projectId.value) {
    return
  }
  memberLoading.value = true
  try {
    const res = await getMemberList(projectId.value)
    if (res.code === 200) {
      memberList.value = res.data || []
    } else {
      ElMessage.error(res.msg || '查询失败')
    }
  } catch (e) {
    ElMessage.error('查询失败')
  } finally {
    memberLoading.value = false
  }
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

const fetchTeachers = async () => {
  try {
    // 查询所有启用状态的用户，前端过滤教师类型
    const res = await getUserList({ pageNum: 1, pageSize: 1000, status: '0' })
    if (res.code === 200) {
      // 过滤出教师类型（userType='01'）
      teacherOptions.value = (res.data.records || []).filter(u => u.userType === '01')
    }
  } catch (e) {
    console.error('加载教师列表失败', e)
  }
}

const handleAddMember = async () => {
  if (!projectId.value) {
    ElMessage.warning('缺少项目ID')
    return
  }
  memberForm.projectId = projectId.value
  memberForm.userId = null
  memberForm.memberRole = '普通参与人'
  memberForm.workloadRatio = 0
  memberForm.responsibility = ''
  memberForm.joinDate = ''
  memberDialogVisible.value = true
}

const submitMember = async () => {
  if (!memberFormRef.value) return
  await memberFormRef.value.validate(async (valid) => {
    if (valid) {
      memberSubmitting.value = true
      try {
        const res = await addMember(memberForm)
        if (res.code === 200) {
          ElMessage.success('添加成功')
          memberDialogVisible.value = false
          handleQueryMembers()
        } else {
          ElMessage.error(res.msg || '添加失败')
        }
      } catch (e) {
        ElMessage.error('添加失败')
      } finally {
        memberSubmitting.value = false
      }
    }
  })
}

const handleRoleChange = async (row) => {
  try {
    const res = await updateMember({
      id: row.id,
      memberRole: row.memberRole
    })
    if (res.code === 200) {
      ElMessage.success('更新成功')
    } else {
      ElMessage.error(res.msg || '更新失败')
      // 恢复原值
      handleQueryMembers()
    }
  } catch (e) {
    ElMessage.error('更新失败')
    handleQueryMembers()
  }
}

const handleWorkloadChange = async (row) => {
  try {
    const res = await updateMember({
      id: row.id,
      workloadRatio: row.workloadRatio
    })
    if (res.code === 200) {
      ElMessage.success('更新成功')
    } else {
      ElMessage.error(res.msg || '更新失败')
      handleQueryMembers()
    }
  } catch (e) {
    ElMessage.error('更新失败')
    handleQueryMembers()
  }
}

const handleResponsibilityChange = async (row) => {
  try {
    const res = await updateMember({
      id: row.id,
      responsibility: row.responsibility
    })
    if (res.code === 200) {
      ElMessage.success('更新成功')
    } else {
      ElMessage.error(res.msg || '更新失败')
      handleQueryMembers()
    }
  } catch (e) {
    ElMessage.error('更新失败')
    handleQueryMembers()
  }
}

const handleRemoveMember = (row) => {
  ElMessageBox.confirm(`确定删除成员 "${row.realName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await removeMember(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        handleQueryMembers()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const formatType = (typeId) => {
  const type = typeOptions.value.find(t => t.typeId === typeId)
  return type ? type.typeName : '-'
}

const statusTag = (status) => {
  const map = {
    draft: 'info',
    submitted: 'warning',
    approved: 'success',
    in_progress: '',
    mid_check: 'warning',
    completed: 'success',
    closed: 'success',
    rejected: 'danger',
    cancelled: 'info'
  }
  return map[status] || 'info'
}

const statusLabel = (status) => {
  const map = {
    draft: '草稿',
    submitted: '已提交',
    approved: '已立项',
    in_progress: '进行中',
    mid_check: '中期检查',
    completed: '已完成',
    closed: '已结题',
    rejected: '已驳回',
    cancelled: '已取消'
  }
  return map[status] || status
}

const formatDate = (date) => {
  if (!date) return '-'
  return date
}

const goBack = () => {
  router.back()
}

const goEdit = () => {
  router.push(`/research/project/form/${projectId.value}`)
}

onMounted(() => {
  fetchTypes()
  fetchTeachers()
  if (projectId.value) {
    handleQuery()
  }
})
</script>

<style scoped>
.detail-container {
  padding: 20px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.actions-bar {
  display: flex;
  gap: 12px;
}
.content-text {
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
}
.member-section {
  padding: 20px 0;
}
.member-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
</style>


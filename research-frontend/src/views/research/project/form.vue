<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑项目' : '新增项目' }}</span>
          <div class="actions-bar">
            <el-button @click="goBack">返回</el-button>
          </div>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" class="form-body">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="form.projectName" maxlength="200" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目类型" prop="projectTypeId">
              <el-select v-model="form.projectTypeId" placeholder="请选择" filterable clearable>
                <el-option v-for="t in typeOptions" :key="t.typeId" :label="t.typeName" :value="t.typeId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人ID" prop="leaderId">
              <el-input-number v-model="form.leaderId" :min="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门ID" prop="deptId">
              <el-input-number v-model="form.deptId" :min="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总预算(万)" prop="totalBudget">
              <el-input-number v-model="form.totalBudget" :min="0" :precision="2" :step="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批准经费(万)" prop="approvedAmount">
              <el-input-number v-model="form.approvedAmount" :min="0" :precision="2" :step="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker v-model="form.startDate" type="date" placeholder="选择日期" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker v-model="form.endDate" type="date" placeholder="选择日期" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目级别" prop="projectLevel">
              <el-input v-model="form.projectLevel" maxlength="50" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源单位" prop="sourceUnit">
              <el-input v-model="form.sourceUnit" maxlength="200" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批准文号" prop="approvalDocNo">
              <el-input v-model="form.approvalDocNo" maxlength="100" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批准日期" prop="approvalDate">
              <el-date-picker v-model="form.approvalDate" type="date" placeholder="选择日期" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关键词" prop="keywords">
              <el-input v-model="form.keywords" maxlength="500" show-word-limit type="textarea" rows="2" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="研究内容" prop="researchContent">
              <el-input v-model="form.researchContent" type="textarea" rows="3" maxlength="2000" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="预期成果" prop="expectedResult">
              <el-input v-model="form.expectedResult" type="textarea" rows="3" maxlength="2000" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="当前进展" prop="currentProgress">
              <el-input v-model="form.currentProgress" type="textarea" rows="3" maxlength="2000" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" rows="2" maxlength="500" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div class="form-actions">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存草稿</el-button>
        <el-button type="success" @click="handleSubmit(true)" v-if="isEdit" :disabled="form.projectStatus !== 'draft'">提交</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addProject, updateProject, getProject, submitProject } from '@/api/project'
import { listProjectTypes } from '@/api/projectType'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const form = reactive({
  projectId: null,
  projectName: '',
  projectTypeId: null,
  leaderId: null,
  deptId: null,
  totalBudget: 0,
  approvedAmount: 0,
  startDate: '',
  endDate: '',
  projectLevel: '',
  sourceUnit: '',
  keywords: '',
  researchContent: '',
  expectedResult: '',
  currentProgress: '',
  approvalDocNo: '',
  approvalDate: '',
  remark: '',
  projectStatus: 'draft'
})

const rules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  projectTypeId: [{ required: true, message: '请选择项目类型', trigger: 'change' }],
  leaderId: [{ required: true, message: '请输入负责人ID', trigger: 'change' }],
  totalBudget: [{ type: 'number', min: 0, message: '预算需为非负数', trigger: 'change' }],
  approvedAmount: [{ type: 'number', min: 0, message: '批准经费需为非负数', trigger: 'change' }]
}

const typeOptions = ref([])
const isEdit = computed(() => !!form.projectId)

const fetchTypes = async () => {
  const res = await listProjectTypes({ pageNum: 1, pageSize: 200 })
  if (res.code === 200) {
    typeOptions.value = res.data.records || []
  }
}

const loadDetail = async () => {
  const id = route.query.id
  if (!id) return
  const res = await getProject(id)
  if (res.code === 200) {
    Object.assign(form, res.data)
  }
}

const handleSubmit = async (isSubmit = false) => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    const payload = { ...form }
    const api = isEdit.value ? updateProject : addProject
    const res = await api(payload)
    if (res.code === 200) {
      if (isSubmit && form.projectId) {
        const submitRes = await submitProject(form.projectId)
        if (submitRes.code !== 200) {
          ElMessage.error(submitRes.msg || '提交失败')
          return
        }
      }
      ElMessage.success(isSubmit ? '提交成功' : '保存成功')
      router.push('/research/project')
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  })
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchTypes()
  loadDetail()
})
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.form-body { margin-top: 12px; }
.actions-bar { display: flex; gap: 12px; align-items: center; }
.form-actions { margin-top: 16px; display: flex; gap: 12px; }
</style>


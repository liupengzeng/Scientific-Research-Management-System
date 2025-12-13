<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑专利' : '新增专利' }}</span>
          <div class="actions-bar">
            <el-button @click="goBack">返回</el-button>
          </div>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="form-body">
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="专利名称" prop="patentName">
              <el-input v-model="form.patentName" maxlength="500" show-word-limit placeholder="请输入专利名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发明人列表" prop="inventors">
              <el-input v-model="form.inventors" maxlength="1000" show-word-limit placeholder="按顺序输入发明人，用逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专利类型" prop="patentType">
              <el-select v-model="form.patentType" placeholder="请选择" clearable style="width: 100%">
                <el-option v-for="type in typeOptions" :key="type" :label="type" :value="type" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专利号" prop="patentNo">
              <el-input v-model="form.patentNo" maxlength="100" placeholder="请输入专利号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专利状态" prop="patentStatus">
              <el-select v-model="form.patentStatus" placeholder="请选择" clearable style="width: 100%">
                <el-option v-for="status in statusOptions" :key="status" :label="status" :value="status" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="申请日期" prop="applicationDate">
              <el-date-picker v-model="form.applicationDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="授权日期" prop="authorizationDate">
              <el-date-picker v-model="form.authorizationDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="有效期至" prop="validUntil">
              <el-date-picker v-model="form.validUntil" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联项目ID" prop="projectId">
              <el-input-number v-model="form.projectId" :min="1" style="width: 100%;" placeholder="请输入关联项目ID（可选）" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
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
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" rows="2" maxlength="500" show-word-limit placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="form-footer">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存草稿</el-button>
        <el-button type="success" @click="handleSubmit" :loading="submitting" :disabled="isEdit && form.status !== 'draft'">提交审核</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { getPatent, addPatent, updatePatent, submitPatent } from '@/api/patent'
import { getToken } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const patentId = computed(() => route.params.patentId ? Number(route.params.patentId) : null)
const isEdit = computed(() => !!patentId.value)

const formRef = ref(null)
const saving = ref(false)
const submitting = ref(false)
const fileList = ref([])

const form = reactive({
  patentName: '',
  inventors: '',
  patentType: '',
  patentNo: '',
  applicationDate: '',
  authorizationDate: '',
  validUntil: '',
  patentStatus: '',
  attachmentPath: '',
  projectId: null,
  status: 'draft',
  remark: ''
})

const typeOptions = ['发明专利', '实用新型专利', '外观设计专利', '其他']
const statusOptions = ['申请中', '已授权', '已转化', '已失效', '其他']

const uploadUrl = computed(() => {
  return import.meta.env.VITE_APP_BASE_API + '/research/project/upload'
})

const uploadHeaders = computed(() => {
  return {
    Authorization: 'Bearer ' + getToken()
  }
})

const rules = {
  patentName: [{ required: true, message: '请输入专利名称', trigger: 'blur' }],
  inventors: [{ required: true, message: '请输入发明人列表', trigger: 'blur' }]
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
    if (form.attachmentPath) {
      form.attachmentPath += ',' + url
    } else {
      form.attachmentPath = url
    }
    ElMessage.success('文件上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

const loadData = async () => {
  if (!patentId.value) return
  try {
    const res = await getPatent(patentId.value)
    if (res.code === 200) {
      Object.assign(form, res.data)
      if (res.data.attachmentPath) {
        const attachments = res.data.attachmentPath.split(',').filter(Boolean)
        fileList.value = attachments.map((url, idx) => ({
          name: url.split('/').pop(),
          url: url,
          uid: idx
        }))
      }
    } else {
      ElMessage.error(res.msg || '加载失败')
    }
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

const handleSave = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        const res = isEdit.value ? await updatePatent(form) : await addPatent(form)
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
          if (!isEdit.value) {
            router.push('/research/patent')
          }
        } else {
          ElMessage.error(res.msg || '操作失败')
        }
      } catch (e) {
        ElMessage.error('操作失败')
      } finally {
        saving.value = false
      }
    }
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      await handleSave()
      if (isEdit.value && form.patentId) {
        submitting.value = true
        try {
          const res = await submitPatent(form.patentId)
          if (res.code === 200) {
            ElMessage.success('提交成功')
            router.push('/research/patent')
          } else {
            ElMessage.error(res.msg || '提交失败')
          }
        } catch (e) {
          ElMessage.error('提交失败')
        } finally {
          submitting.value = false
        }
      } else {
        ElMessage.warning('请先保存草稿')
      }
    }
  })
}

const goBack = () => {
  router.push('/research/patent')
}

onMounted(() => {
  if (isEdit.value) {
    loadData()
  }
})
</script>

<style scoped>
.page-container {
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
.form-body {
  padding: 20px 0;
}
.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
</style>


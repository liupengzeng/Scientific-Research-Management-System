<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑论文' : '新增论文' }}</span>
          <div class="actions-bar">
            <el-button @click="goBack">返回</el-button>
          </div>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="form-body">
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="论文标题" prop="paperTitle">
              <el-input v-model="form.paperTitle" maxlength="500" show-word-limit placeholder="请输入论文标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者列表" prop="authors">
              <el-input v-model="form.authors" maxlength="1000" show-word-limit placeholder="按顺序输入作者，用逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="通讯作者ID" prop="correspondingAuthorId">
              <el-input-number v-model="form.correspondingAuthorId" :min="1" style="width: 100%;" placeholder="请输入通讯作者ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="期刊/会议名称" prop="journalName">
              <el-input v-model="form.journalName" maxlength="300" show-word-limit placeholder="请输入期刊/会议名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发表时间" prop="publishDate">
              <el-date-picker v-model="form.publishDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="卷" prop="volume">
              <el-input v-model="form.volume" maxlength="50" placeholder="请输入卷" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="期" prop="issue">
              <el-input v-model="form.issue" maxlength="50" placeholder="请输入期" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="页码范围" prop="pageRange">
              <el-input v-model="form.pageRange" maxlength="100" placeholder="如：1-10" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="DOI号" prop="doi">
              <el-input v-model="form.doi" maxlength="200" placeholder="请输入DOI号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="论文级别" prop="paperLevel">
              <el-select v-model="form.paperLevel" placeholder="请选择" clearable style="width: 100%">
                <el-option v-for="level in levelOptions" :key="level" :label="level" :value="level" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="影响因子" prop="impactFactor">
              <el-input-number v-model="form.impactFactor" :min="0" :precision="3" :step="0.1" style="width: 100%;" placeholder="请输入影响因子" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="引用次数" prop="citationCount">
              <el-input-number v-model="form.citationCount" :min="0" style="width: 100%;" placeholder="请输入引用次数" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联项目ID" prop="projectId">
              <el-input-number v-model="form.projectId" :min="1" style="width: 100%;" placeholder="请输入关联项目ID（可选）" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关键词" prop="keywords">
              <el-input v-model="form.keywords" maxlength="500" show-word-limit type="textarea" rows="2" placeholder="请输入关键词，用逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="摘要" prop="abstractText">
              <el-input v-model="form.abstractText" type="textarea" rows="6" maxlength="5000" show-word-limit placeholder="请输入摘要" />
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
import { getPaper, addPaper, updatePaper, submitPaper } from '@/api/paper'
import { uploadFile } from '@/api/project'
import { getToken } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const paperId = computed(() => route.params.paperId ? Number(route.params.paperId) : null)
const isEdit = computed(() => !!paperId.value)

const formRef = ref(null)
const saving = ref(false)
const submitting = ref(false)
const fileList = ref([])

const form = reactive({
  paperTitle: '',
  authors: '',
  correspondingAuthorId: null,
  journalName: '',
  publishDate: '',
  volume: '',
  issue: '',
  pageRange: '',
  doi: '',
  paperLevel: '',
  impactFactor: null,
  citationCount: 0,
  keywords: '',
  abstractText: '',
  attachmentPath: '',
  projectId: null,
  status: 'draft',
  remark: ''
})

const levelOptions = ['SCI', 'EI', '核心期刊', '普通期刊', '会议论文', '其他']

const uploadUrl = computed(() => {
  return import.meta.env.VITE_APP_BASE_API + '/research/project/upload'
})

const uploadHeaders = computed(() => {
  return {
    Authorization: 'Bearer ' + getToken()
  }
})

const rules = {
  paperTitle: [{ required: true, message: '请输入论文标题', trigger: 'blur' }],
  authors: [{ required: true, message: '请输入作者列表', trigger: 'blur' }],
  journalName: [{ required: true, message: '请输入期刊/会议名称', trigger: 'blur' }]
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
  if (!paperId.value) return
  try {
    const res = await getPaper(paperId.value)
    if (res.code === 200) {
      Object.assign(form, res.data)
      if (res.data.attachmentPath) {
        // 处理附件列表显示
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
        const res = isEdit.value ? await updatePaper(form) : await addPaper(form)
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
          if (!isEdit.value) {
            router.push('/research/paper')
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
      // 先保存
      await handleSave()
      if (isEdit.value && form.paperId) {
        submitting.value = true
        try {
          const res = await submitPaper(form.paperId)
          if (res.code === 200) {
            ElMessage.success('提交成功')
            router.push('/research/paper')
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
  router.push('/research/paper')
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


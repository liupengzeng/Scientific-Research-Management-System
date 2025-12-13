<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑获奖' : '新增获奖' }}</span>
          <div class="actions-bar">
            <el-button @click="goBack">返回</el-button>
          </div>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="form-body">
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="获奖名称" prop="awardName">
              <el-input v-model="form.awardName" maxlength="500" show-word-limit placeholder="请输入获奖名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="获奖人/团队" prop="awardees">
              <el-input v-model="form.awardees" maxlength="1000" show-word-limit placeholder="按顺序输入获奖人，用逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="获奖类型" prop="awardType">
              <el-select v-model="form.awardType" placeholder="请选择" clearable style="width: 100%">
                <el-option v-for="type in typeOptions" :key="type" :label="type" :value="type" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="获奖等级" prop="awardLevel">
              <el-select v-model="form.awardLevel" placeholder="请选择" clearable style="width: 100%">
                <el-option v-for="level in levelOptions" :key="level" :label="level" :value="level" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="获奖日期" prop="awardDate">
              <el-date-picker v-model="form.awardDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="颁奖单位" prop="awardingUnit">
              <el-input v-model="form.awardingUnit" maxlength="300" show-word-limit placeholder="请输入颁奖单位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="证书编号" prop="certificateNo">
              <el-input v-model="form.certificateNo" maxlength="100" placeholder="请输入证书编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="奖金(万元)" prop="bonus">
              <el-input-number v-model="form.bonus" :min="0" :precision="2" :step="0.1" style="width: 100%;" placeholder="请输入奖金" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
                <el-option label="有效" value="active" />
                <el-option label="无效" value="inactive" />
              </el-select>
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
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { getAward, addAward, updateAward } from '@/api/award'
import { getToken } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const awardId = computed(() => route.params.awardId ? Number(route.params.awardId) : null)
const isEdit = computed(() => !!awardId.value)

const formRef = ref(null)
const saving = ref(false)
const fileList = ref([])

const form = reactive({
  awardName: '',
  awardees: '',
  awardLevel: '',
  awardType: '',
  awardDate: '',
  awardingUnit: '',
  certificateNo: '',
  bonus: null,
  attachmentPath: '',
  status: 'active',
  remark: ''
})

const typeOptions = ['国家级', '省部级', '校级']
const levelOptions = ['一等奖', '二等奖', '三等奖']

const uploadUrl = computed(() => {
  return import.meta.env.VITE_APP_BASE_API + '/research/project/upload'
})

const uploadHeaders = computed(() => {
  return {
    Authorization: 'Bearer ' + getToken()
  }
})

const rules = {
  awardName: [{ required: true, message: '请输入获奖名称', trigger: 'blur' }],
  awardees: [{ required: true, message: '请输入获奖人/团队', trigger: 'blur' }]
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
  if (!awardId.value) return
  try {
    const res = await getAward(awardId.value)
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
        const res = isEdit.value ? await updateAward(form) : await addAward(form)
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
          router.push('/research/award')
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

const goBack = () => {
  router.push('/research/award')
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


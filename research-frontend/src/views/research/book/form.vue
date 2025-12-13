<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑著作' : '新增著作' }}</span>
          <div class="actions-bar">
            <el-button @click="goBack">返回</el-button>
          </div>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="form-body">
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="书名" prop="bookTitle">
              <el-input v-model="form.bookTitle" maxlength="500" show-word-limit placeholder="请输入书名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者列表" prop="authors">
              <el-input v-model="form.authors" maxlength="1000" show-word-limit placeholder="按顺序输入作者，用逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出版社" prop="publisher">
              <el-input v-model="form.publisher" maxlength="300" show-word-limit placeholder="请输入出版社" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出版日期" prop="publishDate">
              <el-date-picker v-model="form.publishDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="ISBN号" prop="isbn">
              <el-input v-model="form.isbn" maxlength="50" placeholder="请输入ISBN号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="页数" prop="pageCount">
              <el-input-number v-model="form.pageCount" :min="0" style="width: 100%;" placeholder="请输入页数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="字数(万字)" prop="wordCount">
              <el-input-number v-model="form.wordCount" :min="0" style="width: 100%;" placeholder="请输入字数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="著作类型" prop="bookType">
              <el-select v-model="form.bookType" placeholder="请选择" clearable style="width: 100%">
                <el-option v-for="type in typeOptions" :key="type" :label="type" :value="type" />
              </el-select>
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
import { getBook, addBook, updateBook } from '@/api/book'

const route = useRoute()
const router = useRouter()
const bookId = computed(() => route.params.bookId ? Number(route.params.bookId) : null)
const isEdit = computed(() => !!bookId.value)

const formRef = ref(null)
const saving = ref(false)

const form = reactive({
  bookTitle: '',
  authors: '',
  publisher: '',
  publishDate: '',
  isbn: '',
  pageCount: null,
  wordCount: null,
  bookType: '',
  status: 'active',
  remark: ''
})

const typeOptions = ['专著', '译著', '教材', '工具书', '其他']

const rules = {
  bookTitle: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  authors: [{ required: true, message: '请输入作者列表', trigger: 'blur' }],
  publisher: [{ required: true, message: '请输入出版社', trigger: 'blur' }]
}

const loadData = async () => {
  if (!bookId.value) return
  try {
    const res = await getBook(bookId.value)
    if (res.code === 200) {
      Object.assign(form, res.data)
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
        const res = isEdit.value ? await updateBook(form) : await addBook(form)
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
          router.push('/research/book')
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
  router.push('/research/book')
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


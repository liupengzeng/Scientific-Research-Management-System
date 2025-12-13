<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>项目类型查询</span>
        </div>
      </template>
      <el-form :model="query" label-width="90px" class="query-form" @keyup.enter.native="handleQuery">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="类型名称">
              <el-input v-model="query.typeName" placeholder="请输入类型名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态">
              <el-select v-model="query.status" placeholder="请选择" clearable>
                <el-option label="正常" value="0" />
                <el-option label="停用" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="编码">
              <el-input v-model="query.typeCode" placeholder="请输入编码" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="6" class="query-actions">
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card class="mt-16">
      <template #header>
        <div class="card-header">
          <span>项目类型列表</span>
          <div class="actions-bar">
            <el-button type="primary" @click="openDialog()" v-permission="'research:projectType:add'">新增</el-button>
            <el-button type="danger" :disabled="!selected.length" @click="handleBatchDelete" v-permission="'research:projectType:remove'">批量删除</el-button>
            <el-button @click="handleQuery">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="typeId" label="ID" width="90" />
        <el-table-column prop="typeName" label="类型名称" min-width="180" />
        <el-table-column prop="typeCode" label="编码" min-width="140" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'info'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="90" />
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDialog(row)" v-permission="'research:projectType:edit'">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="'research:projectType:remove'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <Pagination :total="total" :page-num="pageNum" :page-size="pageSize" @pagination-change="handlePage" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="form.typeName" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="编码" prop="typeCode">
          <el-input v-model="form.typeCode" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" rows="3" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/Pagination/index.vue'
import { listProjectTypes, getProjectType, addProjectType, updateProjectType, deleteProjectTypes } from '@/api/projectType'

const query = reactive({ typeName: '', status: '', typeCode: '' })
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const list = ref([])
const loading = ref(false)
const selected = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('新增项目类型')
const formRef = ref(null)
const form = reactive({
  typeId: null,
  typeName: '',
  typeCode: '',
  status: '0',
  orderNum: 0,
  remark: ''
})

const rules = {
  typeName: [{ required: true, message: '请输入类型名称', trigger: 'blur' }, { min: 1, max: 100, message: '1-100字符', trigger: 'blur' }],
  typeCode: [{ required: true, message: '请输入编码', trigger: 'blur' }, { min: 1, max: 50, message: '1-50字符', trigger: 'blur' }]
}

const formatTime = (t) => {
  if (!t) return '-'
  const d = new Date(t)
  return d.toLocaleString('zh-CN')
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await listProjectTypes({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...query
    })
    if (res.code === 200) {
      list.value = res.data.records
      total.value = res.data.total
      pageNum.value = res.data.pageNum
      pageSize.value = res.data.pageSize
    } else {
      ElMessage.error(res.msg || '查询失败')
    }
  } catch (e) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  pageNum.value = 1
  fetchList()
}

const handleReset = () => {
  query.typeName = ''
  query.typeCode = ''
  query.status = ''
  pageNum.value = 1
  fetchList()
}

const handlePage = ({ pageNum: p, pageSize: s }) => {
  pageNum.value = p
  pageSize.value = s
  fetchList()
}

const handleSelectionChange = (sel) => {
  selected.value = sel
}

const openDialog = async (row) => {
  if (row && row.typeId) {
    dialogTitle.value = '编辑项目类型'
    const res = await getProjectType(row.typeId)
    if (res.code === 200) {
      Object.assign(form, res.data)
    }
  } else {
    dialogTitle.value = '新增项目类型'
    Object.assign(form, {
      typeId: null,
      typeName: '',
      typeCode: '',
      status: '0',
      orderNum: 0,
      remark: ''
    })
  }
  dialogVisible.value = true
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    const api = form.typeId ? updateProjectType : addProjectType
    const payload = { ...form }
    const res = await api(payload)
    if (res.code === 200) {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      fetchList()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除类型 "${row.typeName}"?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deleteProjectTypes(row.typeId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  if (!selected.value.length) return
  const ids = selected.value.map(i => i.typeId).join(',')
  ElMessageBox.confirm(`确认删除选中的 ${selected.value.length} 条记录?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deleteProjectTypes(ids)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.query-form { margin-top: 4px; }
.query-actions { display: flex; align-items: center; gap: 10px; margin-top: 4px; }
.actions-bar { display: flex; gap: 12px; align-items: center; flex-wrap: wrap; }
.mt-16 { margin-top: 16px; }
</style>


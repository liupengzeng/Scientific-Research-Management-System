<template>
  <div class="dept-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>部门管理</span>
          <div class="actions">
            <el-button type="primary" @click="handleAddRoot" v-permission="'system:dept:add'">新增顶级部门</el-button>
            <el-button @click="loadData" v-permission="'system:dept:list'">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="tableData"
        v-loading="loading"
        row-key="deptId"
        border
        default-expand-all
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="deptName" label="部门名称" min-width="220" />
        <el-table-column prop="deptCode" label="部门编码" min-width="140" />
        <el-table-column prop="leader" label="负责人" min-width="120" />
        <el-table-column prop="phone" label="联系电话" min-width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="orderNum" label="排序" width="90" align="center" />
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status"
              active-value="0"
              inactive-value="1"
              @change="(val) => handleStatusChange(row, val)"
              v-permission="'system:dept:edit'"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleAddChild(row)" v-permission="'system:dept:add'">新增下级</el-button>
            <el-button type="primary" link @click="handleEdit(row)" v-permission="'system:dept:edit'">编辑</el按钮>
            <el-button type="danger" link @click="handleDelete(row)" v-permission="'system:dept:remove'">删除</el按钮>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetDialog">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="treeSelectData"
            :props="treeSelectProps"
            check-strictly
            default-expand-all
            placeholder="请选择上级部门"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="formData.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门编码" prop="deptCode">
          <el-input v-model="formData.deptCode" placeholder="请输入部门编码" />
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="formData.leader" placeholder="请输入负责人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="formData.orderNum" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getDeptTree,
  getDept,
  addDept,
  updateDept,
  deleteDept,
  changeDeptStatus
} from '@/api/dept'

const loading = ref(false)
const tableData = ref([])
const originalTree = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const disabledDeptIds = ref([])

const initialForm = () => ({
  deptId: null,
  parentId: 0,
  deptName: '',
  deptCode: '',
  leader: '',
  phone: '',
  email: '',
  orderNum: 0,
  status: '0',
  remark: ''
})

const formData = reactive(initialForm())

const formRules = {
  parentId: [
    { required: true, message: '请选择上级部门', trigger: 'change' }
  ],
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度应为2-50个字符', trigger: 'blur' }
  ],
  deptCode: [
    { required: true, message: '请输入部门编码', trigger: 'blur' }
  ],
  phone: [
    {
      pattern: /^$|^1[3-9]\d{9}$/,
      message: '请输入正确的手机号',
      trigger: 'blur'
    }
  ],
  email: [
    {
      type: 'email',
      message: '请输入正确的邮箱',
      trigger: 'blur'
    }
  ]
}

const dialogTitle = computed(() => (isEdit.value ? '编辑部门' : '新增部门'))

const treeSelectData = computed(() => [
  {
    deptId: 0,
    deptName: '顶级部门',
    children: originalTree.value
  }
])

const treeSelectProps = computed(() => ({
  value: 'deptId',
  label: 'deptName',
  children: 'children',
  disabled: (node) => disabledDeptIds.value.includes(node.deptId)
}))

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDeptTree()
    if (res.code === 200) {
      tableData.value = res.data || []
      originalTree.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('获取部门列表失败')
  } finally {
    loading.value = false
  }
}

const handleAddRoot = () => {
  isEdit.value = false
  Object.assign(formData, initialForm())
  disabledDeptIds.value = []
  dialogVisible.value = true
}

const collectDescendantIds = (node, ids) => {
  ids.push(node.deptId)
  if (node.children && node.children.length > 0) {
    node.children.forEach(child => collectDescendantIds(child, ids))
  }
}

const handleAddChild = (row) => {
  isEdit.value = false
  Object.assign(formData, initialForm(), { parentId: row.deptId })
  disabledDeptIds.value = []
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  isEdit.value = true
  try {
    const res = await getDept(row.deptId)
    if (res.code === 200) {
      Object.assign(formData, initialForm(), res.data)
      const ids = []
      collectDescendantIds(row, ids)
      disabledDeptIds.value = ids
      dialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取部门信息失败')
    }
  } catch (error) {
    ElMessage.error('获取部门信息失败')
  }
}

const handleSubmit = () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const api = isEdit.value ? updateDept : addDept
      const res = await api(formData)
      if (res.code === 200) {
        ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
        dialogVisible.value = false
        loadData()
      } else {
        ElMessage.error(res.msg || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定删除部门「${row.deptName}」吗？`,
    '提示',
    {
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteDept(row.deptId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleStatusChange = async (row, value) => {
  const oldStatus = row.status
  row.status = value
  try {
    const res = await changeDeptStatus({ deptId: row.deptId, status: value })
    if (res.code === 200) {
      ElMessage.success(value === '0' ? '已启用' : '已停用')
    } else {
      row.status = oldStatus
      ElMessage.error(res.msg || '状态修改失败')
    }
  } catch (error) {
    row.status = oldStatus
    ElMessage.error('状态修改失败')
  }
}

const resetDialog = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(formData, initialForm())
  disabledDeptIds.value = []
}

loadData()
</script>

<style lang="scss" scoped>
.dept-container {
  padding: 20px;

  .box-card {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    align-items: center;
    gap: 50px;

    .actions {
      display: flex;
      gap: 20px;
    }
  }

  .dialog-footer {
    text-align: right;
  }
}
</style>

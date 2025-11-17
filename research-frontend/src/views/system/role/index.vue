<template>
  <div class="role-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>角色查询</span>
        </div>
      </template>
      <el-form :model="queryForm" label-width="90px" class="query-form">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="角色名称">
              <el-input v-model="queryForm.roleName" placeholder="请输入角色名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="权限字符">
              <el-input v-model="queryForm.roleKey" placeholder="请输入权限字符" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态">
              <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
                <el-option label="正常" value="0" />
                <el-option label="停用" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item>
              <el-button type="primary" @click="handleQuery">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card class="box-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>角色列表</span>
        </div>
      </template>
      <div class="actions-bar">
        <el-button type="primary" @click="handleAdd" v-permission="'system:role:add'">新增角色</el-button>
        <el-button @click="handleQuery" v-permission="'system:role:list'">刷新</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="roleList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="roleId" label="角色ID" width="90" />
        <el-table-column prop="roleName" label="角色名称" width="160" />
        <el-table-column prop="roleKey" label="权限字符" width="160" />
        <el-table-column prop="roleSort" label="显示顺序" width="120" align="center" />
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status"
              active-value="0"
              inactive-value="1"
              @change="(val) => handleStatusChange(row, val)"
              v-permission="'system:role:edit'"
            />
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)" v-permission="'system:role:edit'">编辑</el-button>
            <el-button type="primary" link @click="handleAssignMenu(row)" v-permission="'system:menu:edit'">分配菜单</el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-permission="'system:role:remove'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" @close="resetDialog">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleKey">
          <el-input v-model="formData.roleKey" placeholder="请输入权限字符，例如 system:user:list" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="roleSort">
          <el-input-number v-model="formData.roleSort" :min="0" />
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

    <el-dialog v-model="assignDialogVisible" title="分配菜单" width="520px" @close="handleAssignClose">
      <el-tree
        ref="menuTreeRef"
        v-loading="menuTreeLoading"
        :data="menuTreeData"
        node-key="menuId"
        show-checkbox
        default-expand-all
        :props="menuTreeProps"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="assignDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAssignSubmit">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getRoleList,
  getRole,
  addRole,
  updateRole,
  deleteRole,
  changeRoleStatus
} from '@/api/role'
import {
  getManageMenuTree,
  getRoleMenuIds,
  assignRoleMenu
} from '@/api/menu'

const loading = ref(false)
const roleList = ref([])

const queryForm = reactive({
  roleName: '',
  roleKey: '',
  status: ''
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const assignDialogVisible = ref(false)
const currentRoleId = ref(null)
const menuTreeLoading = ref(false)
const menuTreeData = ref([])
const menuTreeRef = ref(null)

const initialForm = () => ({
  roleId: null,
  roleName: '',
  roleKey: '',
  roleSort: 0,
  status: '0',
  remark: ''
})

const formData = reactive(initialForm())

const formRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度应为2-50个字符', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '请输入权限字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_:]+$/, message: '只能包含字母、数字、下划线、冒号', trigger: 'blur' }
  ]
}

const dialogTitle = computed(() => (isEdit.value ? '编辑角色' : '新增角色'))

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoleList(queryForm)
    if (res.code === 200) {
      roleList.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  loadData()
}

const handleReset = () => {
  queryForm.roleName = ''
  queryForm.roleKey = ''
  queryForm.status = ''
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, initialForm())
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  isEdit.value = true
  try {
    const res = await getRole(row.roleId)
    if (res.code === 200) {
      Object.assign(formData, initialForm(), res.data)
      dialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取角色信息失败')
    }
  } catch (error) {
    ElMessage.error('获取角色信息失败')
  }
}

const handleSubmit = () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const api = isEdit.value ? updateRole : addRole
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
    `确定删除角色「${row.roleName}」吗？`,
    '提示',
    { type: 'warning' }
  ).then(async () => {
    try {
      const res = await deleteRole(row.roleId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败，角色可能已分配用户')
    }
  }).catch(() => {})
}

const handleStatusChange = async (row, value) => {
  const oldStatus = row.status
  row.status = value
  try {
    const res = await changeRoleStatus({ roleId: row.roleId, status: value })
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
}

const menuTreeProps = {
  value: 'menuId',
  label: 'menuName',
  children: 'children'
}

const loadMenuTreeData = async () => {
  if (menuTreeData.value.length > 0) return
  menuTreeLoading.value = true
  try {
    const res = await getManageMenuTree()
    if (res.code === 200) {
      menuTreeData.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('获取菜单树失败')
  } finally {
    menuTreeLoading.value = false
  }
}

const loadRoleMenuIdsData = async (roleId) => {
  if (!roleId) return
  menuTreeLoading.value = true
  try {
    const res = await getRoleMenuIds(roleId)
    if (res.code === 200) {
      await nextTick()
      menuTreeRef.value?.setCheckedKeys(res.data || [])
    }
  } catch (error) {
    ElMessage.error('获取角色菜单失败')
  } finally {
    menuTreeLoading.value = false
  }
}

const handleAssignMenu = async (row) => {
  currentRoleId.value = row.roleId
  assignDialogVisible.value = true
  await loadMenuTreeData()
  await loadRoleMenuIdsData(row.roleId)
}

const handleAssignSubmit = async () => {
  if (!currentRoleId.value) {
    ElMessage.warning('请先选择角色')
    return
  }
  const checked = menuTreeRef.value?.getCheckedKeys() || []
  const halfChecked = menuTreeRef.value?.getHalfCheckedKeys() || []
  const menuIds = [...new Set([...checked, ...halfChecked])]
  try {
    const res = await assignRoleMenu({ roleId: currentRoleId.value, menuIds })
    if (res.code === 200) {
      ElMessage.success('菜单分配成功')
      assignDialogVisible.value = false
    } else {
      ElMessage.error(res.msg || '菜单分配失败')
    }
  } catch (error) {
    ElMessage.error('菜单分配失败')
  }
}

const handleAssignClose = () => {
  currentRoleId.value = null
  menuTreeRef.value?.setCheckedKeys([])
}

loadData()
</script>

<style lang="scss" scoped>
.role-container {
  padding: 20px;

  .box-card {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .actions-bar {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    padding: 8px 0 16px;
  }

  .dialog-footer {
    text-align: right;
  }
}
</style>


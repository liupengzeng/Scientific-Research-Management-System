<template>
  <div class="menu-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>菜单查询</span>
        </div>
      </template>
      <el-form :model="queryForm" label-width="90px" class="query-form">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="菜单名称">
              <el-input v-model="queryForm.menuName" placeholder="请输入菜单名称" clearable />
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
          <span>菜单列表</span>
        </div>
      </template>
      <div class="actions-bar">
        <el-button type="primary" @click="handleAddRoot">新增顶级菜单</el-button>
        <el-button @click="loadData">刷新</el-button>
      </div>

      <el-table
        :data="menuData"
        v-loading="loading"
        row-key="menuId"
        border
        default-expand-all
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="200" />
        <el-table-column prop="menuType" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.menuType === 'M'">目录</el-tag>
            <el-tag type="success" v-else-if="row.menuType === 'C'">菜单</el-tag>
            <el-tag type="warning" v-else>按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由地址" min-width="180" />
        <el-table-column prop="component" label="组件路径" min-width="180" />
        <el-table-column prop="perms" label="权限字符" min-width="180" />
        <el-table-column prop="icon" label="图标" width="120" />
        <el-table-column prop="orderNum" label="排序" width="100" align="center" />
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status"
              active-value="0"
              inactive-value="1"
              @change="(val) => handleStatusChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleAddChild(row)">新增下级</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="box-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>角色菜单分配</span>
        </div>
      </template>
      <div class="assign-panel">
        <el-select
          v-model="selectedRoleId"
          placeholder="请选择角色"
          style="width: 260px"
          @change="loadRoleMenus"
        >
          <el-option
            v-for="role in roleOptions"
            :key="role.roleId"
            :label="role.roleName"
            :value="role.roleId"
          />
        </el-select>
        <el-button type="primary" :disabled="!selectedRoleId" @click="handleSaveRoleMenus">保存菜单权限</el-button>
      </div>
      <el-tree
        ref="roleMenuTreeRef"
        v-loading="roleMenuLoading"
        :data="menuTreeData"
        node-key="menuId"
        show-checkbox
        :default-checked-keys="checkedMenuIds"
        default-expand-all
        :props="treeProps"
        style="margin-top: 16px"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="580px" @close="resetDialog">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="treeSelectData"
            :props="treeProps"
            check-strictly
            default-expand-all
            placeholder="请选择上级菜单"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="formData.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="formData.menuType">
            <el-radio label="M">目录</el-radio>
            <el-radio label="C">菜单</el-radio>
            <el-radio label="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="路由地址" v-if="formData.menuType !== 'F'">
          <el-input v-model="formData.path" placeholder="请输入路由地址" />
        </el-form-item>
        <el-form-item label="组件路径" v-if="formData.menuType === 'C'">
          <el-input v-model="formData.component" placeholder="例如 system/user/index" />
        </el-form-item>
        <el-form-item label="权限字符" v-if="formData.menuType !== 'M'">
          <el-input v-model="formData.perms" placeholder="例如 system:user:list" />
        </el-form-item>
        <el-form-item label="图标" v-if="formData.menuType !== 'F'">
          <el-input v-model="formData.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="显示排序" prop="orderNum">
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
  getManageMenuList,
  getManageMenuTree,
  getMenu,
  addMenu,
  updateMenu,
  deleteMenu,
  changeMenuStatus,
  getRoleMenuIds,
  assignRoleMenu
} from '@/api/menu'
import { getRoleList } from '@/api/role'

const loading = ref(false)
const menuData = ref([])
const menuTreeData = ref([])
const roleOptions = ref([])
const selectedRoleId = ref(null)
const roleMenuLoading = ref(false)
const checkedMenuIds = ref([])
const roleMenuTreeRef = ref(null)

const queryForm = reactive({
  menuName: '',
  status: ''
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const initialForm = () => ({
  menuId: null,
  parentId: 0,
  menuName: '',
  menuType: 'M',
  path: '',
  component: '',
  perms: '',
  icon: '',
  orderNum: 0,
  status: '0',
  remark: ''
})

const formData = reactive(initialForm())

const formRules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  menuType: [
    { required: true, message: '请选择菜单类型', trigger: 'change' }
  ]
}

const treeProps = {
  value: 'menuId',
  label: 'menuName',
  children: 'children'
}

const treeSelectData = computed(() => [
  {
    menuId: 0,
    menuName: '顶级菜单',
    children: menuTreeData.value
  }
])

const loadData = async () => {
  loading.value = true
  try {
    const [listRes, treeRes] = await Promise.all([
      getManageMenuList(queryForm),
      getManageMenuTree()
    ])
    if (listRes.code === 200) {
      menuData.value = listRes.data || []
    }
    if (treeRes.code === 200) {
      menuTreeData.value = treeRes.data || []
    }
  } catch (error) {
    ElMessage.error('获取菜单列表失败')
  } finally {
    loading.value = false
  }
}

const loadRoles = async () => {
  try {
    const res = await getRoleList({})
    if (res.code === 200) {
      roleOptions.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  }
}

const loadRoleMenus = async () => {
  if (!selectedRoleId.value) return
  roleMenuLoading.value = true
  try {
    const res = await getRoleMenuIds(selectedRoleId.value)
    if (res.code === 200) {
      checkedMenuIds.value = res.data || []
      roleMenuTreeRef.value?.setCheckedKeys(checkedMenuIds.value)
    }
  } catch (error) {
    ElMessage.error('获取角色菜单失败')
  } finally {
    roleMenuLoading.value = false
  }
}

const handleQuery = () => {
  loadData()
}

const handleReset = () => {
  queryForm.menuName = ''
  queryForm.status = ''
  loadData()
}

const handleAddRoot = () => {
  isEdit.value = false
  Object.assign(formData, initialForm())
  dialogVisible.value = true
}

const handleAddChild = (row) => {
  isEdit.value = false
  Object.assign(formData, initialForm(), {
    parentId: row.menuId,
    menuType: row.menuType === 'F' ? 'F' : 'C'
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  isEdit.value = true
  try {
    const res = await getMenu(row.menuId)
    if (res.code === 200) {
      Object.assign(formData, initialForm(), res.data)
      dialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取菜单信息失败')
    }
  } catch (error) {
    ElMessage.error('获取菜单信息失败')
  }
}

const handleSubmit = () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const api = isEdit.value ? updateMenu : addMenu
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
  ElMessageBox.confirm(`确定删除菜单「${row.menuName}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        const res = await deleteMenu(row.menuId)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          loadData()
        } else {
          ElMessage.error(res.msg || '删除失败')
        }
      } catch (error) {
        ElMessage.error('删除失败，可能存在子菜单')
      }
    })
    .catch(() => {})
}

const handleStatusChange = async (row, value) => {
  const oldStatus = row.status
  row.status = value
  try {
    const res = await changeMenuStatus({ menuId: row.menuId, status: value })
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

const handleSaveRoleMenus = async () => {
  if (!selectedRoleId.value) {
    ElMessage.warning('请先选择角色')
    return
  }
  const checked = roleMenuTreeRef.value?.getCheckedKeys() || []
  const halfChecked = roleMenuTreeRef.value?.getHalfCheckedKeys() || []
  const menuIds = [...new Set([...checked, ...halfChecked])]
  try {
    const res = await assignRoleMenu({ roleId: selectedRoleId.value, menuIds })
    if (res.code === 200) {
      ElMessage.success('菜单权限保存成功')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const resetDialog = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(formData, initialForm())
}

loadData()
loadRoles()
</script>

<style lang="scss" scoped>
.menu-container {
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

  .assign-panel {
    display: flex;
    flex-wrap: wrap;
    gap: 12px


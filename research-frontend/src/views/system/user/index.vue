<template>
  <div class="user-container">
    <!-- 查询表单 -->
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>用户查询</span>
        </div>
      </template>
      <el-form :model="queryForm" label-width="100px" class="query-form">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="用户名">
              <el-input
                v-model="queryForm.username"
                placeholder="请输入用户名"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="真实姓名">
              <el-input
                v-model="queryForm.realName"
                placeholder="请输入真实姓名"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态">
              <el-select
                v-model="queryForm.status"
                placeholder="请选择状态"
                clearable
              >
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

    <!-- 操作按钮 -->
    <el-card class="box-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
        </div>
      </template>

      <div class="actions-bar">
        <el-button type="primary" @click="handleAdd" v-permission="'system:user:add'">新增用户</el-button>
        <el-button
          type="danger"
          :disabled="selectedUsers.length === 0"
          @click="handleBatchDelete"
          v-permission="'system:user:remove'"
        >
          批量删除
        </el-button>
        <el-button @click="handleQuery" v-permission="'system:user:list'">刷新</el-button>
      </div>

      <!-- 用户列表表格 -->
      <el-table
        v-loading="loading"
        :data="userList"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column label="部门" width="150">
          <template #default="{ row }">
            {{ row.dept?.deptName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="角色" width="150">
          <template #default="{ row }">
            <el-tag
              v-for="role in row.roles"
              :key="role.roleId"
              style="margin-right: 5px"
            >
              {{ role.roleName }}
            </el-tag>
            <span v-if="!row.roles || row.roles.length === 0">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后登录" width="180">
          <template #default="{ row }">
            {{ row.lastLoginTime ? formatTime(row.lastLoginTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleEdit(row)"
              v-permission="'system:user:edit'"
            >
              编辑
            </el-button>
            <el-button
              link
              type="warning"
              size="small"
              @click="handleResetPassword(row)"
              v-permission="'system:user:resetPwd'"
            >
              重置密码
            </el-button>
            <el-button
              link
              type="success"
              size="small"
              @click="handleAssignRole(row)"
              v-permission="'system:user:edit'"
            >
              分配角色
            </el-button>
            <el-button
              link
              :type="row.status === '0' ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
              v-permission="'system:user:edit'"
            >
              {{ row.status === '0' ? '停用' : '启用' }}
            </el-button>
            <el-button
              link
              type="danger"
              size="small"
              @click="handleDelete(row)"
              v-permission="'system:user:remove'"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <Pagination
        :total="total"
        :page-num="pageNum"
        :page-size="pageSize"
        @pagination-change="handlePaginationChange"
      />
    </el-card>

    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="formData.username"
            placeholder="请输入用户名"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
          <span v-if="isEdit" class="el-form-item__error">
            编辑时可不填密码，留空则不修改
          </span>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input
            v-model="formData.realName"
            placeholder="请输入真实姓名"
          />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="formData.gender" placeholder="请选择性别">
            <el-option label="男" value="0" />
            <el-option label="女" value="1" />
            <el-option label="未知" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="formData.deptId" placeholder="请选择部门">
            <el-option
              v-for="dept in deptList"
              :key="dept.deptId"
              :label="dept.deptName"
              :value="dept.deptId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="formData.userType" placeholder="请选择用户类型">
            <el-option label="系统用户" value="00" />
            <el-option label="教师" value="01" />
          </el-select>
        </el-form-item>
        <el-form-item label="职称">
          <el-input v-model="formData.title" placeholder="请输入职称" />
        </el-form-item>
        <el-form-item label="研究方向">
          <el-input
            v-model="formData.researchDirection"
            type="textarea"
            placeholder="请输入研究方向"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitForm">提交</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog v-model="resetPwdDialogVisible" title="重置密码" width="400px">
      <el-form
        ref="resetPwdFormRef"
        :model="resetPwdForm"
        :rules="resetPwdRules"
        label-width="100px"
      >
        <el-form-item label="用户">
          <span>{{ resetPwdForm.username }}</span>
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input
            v-model="resetPwdForm.password"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="resetPwdForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resetPwdDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitResetPwd">
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 角色分配对话框 -->
    <el-dialog v-model="assignRoleDialogVisible" title="分配角色" width="400px">
      <div>
        <p>用户：{{ assignRoleForm.realName }}</p>
        <el-checkbox-group v-model="assignRoleForm.roleIds">
          <el-checkbox
            v-for="role in roleList"
            :key="role.roleId"
            :label="role.roleId"
          >
            {{ role.roleName }}
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="assignRoleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitAssignRole">
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/Pagination/index.vue'
import {
  getUserList,
  getUser,
  addUser,
  updateUser,
  deleteUser,
  resetUserPassword,
  changeUserStatus,
  checkUsernameUnique,
  checkEmailUnique,
  checkPhoneUnique
} from '@/api/user'
import { getDeptList } from '@/api/dept'
import { getRoleList } from '@/api/role'

// 查询表单
const queryForm = reactive({
  username: '',
  realName: '',
  status: ''
})

// 分页参数
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 列表数据
const userList = ref([])
const loading = ref(false)
const selectedUsers = ref([])

// 部门和角色数据
const deptList = ref([])
const roleList = ref([])

// 对话框状态
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

// 表单数据
const formData = reactive({
  userId: null,
  username: '',
  password: '',
  realName: '',
  gender: '0',
  email: '',
  phone: '',
  deptId: null,
  userType: '00',
  title: '',
  researchDirection: '',
  status: '0',
  remark: '',
  roleIds: []
})

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    {
      min: 3,
      max: 20,
      message: '用户名长度应为3-20个字符',
      trigger: 'blur'
    },
    {
      asyncValidator: async (rule, value) => {
        if (!value) return
        if (isEdit.value && formData.userId) {
          const result = await checkUsernameUnique(value, formData.userId)
          if (!result.data) {
            return Promise.reject(new Error('用户名已存在'))
          }
        } else {
          const result = await checkUsernameUnique(value)
          if (!result.data) {
            return Promise.reject(new Error('用户名已存在'))
          }
        }
      },
      trigger: 'blur'
    }
  ],
  password: [
    {
      validator: (rule, value) => {
        if (!isEdit.value && !value) {
          return false
        }
        if (value && (value.length < 6 || value.length > 20)) {
          return false
        }
        return true
      },
      message: '新增时密码必填，长度6-20个字符',
      trigger: 'blur'
    }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '真实姓名长度应为2-50个字符', trigger: 'blur' }
  ],
  email: [
    {
      type: 'email',
      message: '请输入正确的邮箱',
      trigger: 'blur'
    },
    {
      asyncValidator: async (rule, value) => {
        if (!value) return
        if (isEdit.value && formData.userId) {
          const result = await checkEmailUnique(value, formData.userId)
          if (!result.data) {
            return Promise.reject(new Error('邮箱已存在'))
          }
        } else if (value) {
          const result = await checkEmailUnique(value)
          if (!result.data) {
            return Promise.reject(new Error('邮箱已存在'))
          }
        }
      },
      trigger: 'blur'
    }
  ],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号',
      trigger: 'blur'
    },
    {
      asyncValidator: async (rule, value) => {
        if (!value) return
        if (isEdit.value && formData.userId) {
          const result = await checkPhoneUnique(value, formData.userId)
          if (!result.data) {
            return Promise.reject(new Error('手机号已存在'))
          }
        } else if (value) {
          const result = await checkPhoneUnique(value)
          if (!result.data) {
            return Promise.reject(new Error('手机号已存在'))
          }
        }
      },
      trigger: 'blur'
    }
  ]
}

// 重置密码对话框
const resetPwdDialogVisible = ref(false)
const resetPwdFormRef = ref(null)
const resetPwdForm = reactive({
  userId: null,
  username: '',
  password: '',
  confirmPassword: ''
})

const resetPwdRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    {
      min: 6,
      max: 20,
      message: '密码长度应为6-20个字符',
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value) => {
        if (value !== resetPwdForm.password) {
          return false
        }
        return true
      },
      message: '两次输入密码不一致',
      trigger: 'blur'
    }
  ]
}

// 角色分配对话框
const assignRoleDialogVisible = ref(false)
const assignRoleForm = reactive({
  userId: null,
  realName: '',
  roleIds: []
})

// 对话框标题
const dialogTitle = computed(() => {
  return isEdit.value ? '编辑用户' : '新增用户'
})

// 查询用户列表
const handleQuery = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      username: queryForm.username,
      realName: queryForm.realName,
      status: queryForm.status
    }
    const response = await getUserList(params)
    if (response.code === 200) {
      userList.value = response.data.records
      total.value = response.data.total
      pageNum.value = response.data.pageNum
      pageSize.value = response.data.pageSize
    } else {
      ElMessage.error(response.msg || '查询失败')
    }
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置查询表单
const handleReset = () => {
  queryForm.username = ''
  queryForm.realName = ''
  queryForm.status = ''
  pageNum.value = 1
  handleQuery()
}

// 分页变化
const handlePaginationChange = ({ pageNum: newPageNum, pageSize: newPageSize }) => {
  pageNum.value = newPageNum
  pageSize.value = newPageSize
  handleQuery()
}

// 选择行
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 新增用户
const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    userId: null,
    username: '',
    password: '',
    realName: '',
    gender: '0',
    email: '',
    phone: '',
    deptId: null,
    userType: '00',
    title: '',
    researchDirection: '',
    status: '0',
    remark: '',
    roleIds: []
  })
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = async (row) => {
  isEdit.value = true
  try {
    const response = await getUser(row.userId)
    if (response.code === 200) {
      const user = response.data
      Object.assign(formData, {
        userId: user.userId,
        username: user.username,
        password: '',
        realName: user.realName,
        gender: user.gender || '0',
        email: user.email || '',
        phone: user.phone || '',
        deptId: user.deptId,
        userType: user.userType || '00',
        title: user.title || '',
        researchDirection: user.researchDirection || '',
        status: user.status,
        remark: user.remark || '',
        roleIds: user.roles ? user.roles.map(r => r.roleId) : []
      })
      dialogVisible.value = true
    } else {
      ElMessage.error(response.msg || '获取用户信息失败')
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

// 提交表单
const handleSubmitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    const submitData = {
      ...formData
    }

    // 编辑时如果没有修改密码，则不传递密码
    if (isEdit.value && !submitData.password) {
      delete submitData.password
    }

    let response
    if (isEdit.value) {
      response = await updateUser(submitData)
    } else {
      response = await addUser(submitData)
    }

    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      handleQuery()
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    ElMessage.error('表单验证失败')
  }
}

// 对话框关闭
const handleDialogClose = () => {
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 重置密码
const handleResetPassword = (row) => {
  resetPwdForm.userId = row.userId
  resetPwdForm.username = row.username
  resetPwdForm.password = ''
  resetPwdForm.confirmPassword = ''
  resetPwdDialogVisible.value = true
}

// 提交重置密码
const handleSubmitResetPwd = async () => {
  if (!resetPwdFormRef.value) return

  try {
    await resetPwdFormRef.value.validate()
    const response = await resetUserPassword({
      userId: resetPwdForm.userId,
      password: resetPwdForm.password
    })

    if (response.code === 200) {
      ElMessage.success('密码重置成功')
      resetPwdDialogVisible.value = false
      handleQuery()
    } else {
      ElMessage.error(response.msg || '密码重置失败')
    }
  } catch (error) {
    ElMessage.error('表单验证失败')
  }
}

// 分配角色
const handleAssignRole = (row) => {
  assignRoleForm.userId = row.userId
  assignRoleForm.realName = row.realName
  assignRoleForm.roleIds = row.roles ? row.roles.map(r => r.roleId) : []
  assignRoleDialogVisible.value = true
}

// 提交角色分配
const handleSubmitAssignRole = async () => {
  try {
    const response = await updateUser({
      userId: assignRoleForm.userId,
      roleIds: assignRoleForm.roleIds
    })

    if (response.code === 200) {
      ElMessage.success('角色分配成功')
      assignRoleDialogVisible.value = false
      handleQuery()
    } else {
      ElMessage.error(response.msg || '角色分配失败')
    }
  } catch (error) {
    ElMessage.error('角色分配失败')
  }
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除用户 "${row.username}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await deleteUser(row.userId.toString())
      if (response.code === 200) {
        ElMessage.success('删除成功')
        handleQuery()
      } else {
        ElMessage.error(response.msg || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要删除的用户')
    return
  }

  const usernames = selectedUsers.value.map(u => u.username).join('、')
  ElMessageBox.confirm(
    `确定删除选中的 ${selectedUsers.value.length} 个用户吗？(${usernames})`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const userIds = selectedUsers.value.map(u => u.userId).join(',')
      const response = await deleteUser(userIds)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        handleQuery()
      } else {
        ElMessage.error(response.msg || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 切换用户状态
const handleToggleStatus = async (row) => {
  const newStatus = row.status === '0' ? '1' : '0'
  const statusText = newStatus === '0' ? '启用' : '停用'

  ElMessageBox.confirm(
    `确定要${statusText}用户 "${row.username}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await changeUserStatus({
        userId: row.userId,
        status: newStatus
      })
      if (response.code === 200) {
        ElMessage.success(`${statusText}成功`)
        handleQuery()
      } else {
        ElMessage.error(response.msg || `${statusText}失败`)
      }
    } catch (error) {
      ElMessage.error(`${statusText}失败`)
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 加载部门和角色列表
const loadDeptList = async () => {
  try {
    const response = await getDeptList()
    if (response.code === 200) {
      deptList.value = response.data || []
    }
  } catch (error) {
    console.error('加载部门列表失败:', error)
  }
}

const loadRoleList = async () => {
  try {
    const response = await getRoleList()
    if (response.code === 200) {
      roleList.value = response.data || []
    }
  } catch (error) {
    console.error('加载角色列表失败:', error)
  }
}

// 初始化加载
handleQuery()
loadDeptList()
loadRoleList()
</script>

<style lang="scss" scoped>



.user-container {
  padding: 20px;

  .box-card {
    margin-top: 20px;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
    background: linear-gradient(135deg, #f5f7fa 0%, #eef2f6 100%);
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



  .query-form {
    .el-col {
      margin-bottom: 10px;
    }
  }

  .dialog-footer {
    text-align: right;
  }


}
</style>


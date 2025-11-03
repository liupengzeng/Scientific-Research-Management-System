<template>
  <div class="upload-container">
    <el-upload
      ref="uploadRef"
      :action="uploadUrl"
      :headers="uploadHeaders"
      :data="uploadData"
      :file-list="fileList"
      :limit="limit"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-exceed="handleExceed"
      :before-upload="beforeUpload"
      :on-remove="handleRemove"
      :disabled="disabled"
    >
      <el-button type="primary" :disabled="disabled">
        <el-icon><Upload /></el-icon>
        选择文件
      </el-button>
      <template #tip>
        <div class="el-upload__tip" v-if="tip">
          {{ tip }}
        </div>
      </template>
    </el-upload>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getToken } from '@/utils/auth'

const props = defineProps({
  modelValue: {
    type: [String, Array],
    default: ''
  },
  limit: {
    type: Number,
    default: 1
  },
  fileSize: {
    type: Number,
    default: 10 // MB
  },
  fileType: {
    type: Array,
    default: () => []
  },
  tip: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const uploadRef = ref(null)
const uploadUrl = computed(() => {
  return import.meta.env.VITE_APP_BASE_API + '/common/upload'
})

const uploadHeaders = computed(() => {
  return {
    Authorization: 'Bearer ' + getToken()
  }
})

const uploadData = computed(() => {
  return {}
})

const fileList = ref([])

// 上传成功回调
const handleSuccess = (response, file, fileList) => {
  if (response.code === 200) {
    const fileUrl = response.data.url || response.data
    if (props.limit === 1) {
      emit('update:modelValue', fileUrl)
    } else {
      const urls = fileList.map(item => item.response?.data?.url || item.url).filter(Boolean)
      emit('update:modelValue', urls)
    }
    emit('change', fileUrl, file, fileList)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

// 上传失败回调
const handleError = (error, file, fileList) => {
  ElMessage.error('上传失败: ' + (error.message || '未知错误'))
}

// 文件超出限制
const handleExceed = () => {
  ElMessage.warning(`最多只能上传${props.limit}个文件`)
}

// 上传前验证
const beforeUpload = (file) => {
  // 验证文件大小
  const isLtSize = file.size / 1024 / 1024 < props.fileSize
  if (!isLtSize) {
    ElMessage.error(`文件大小不能超过${props.fileSize}MB`)
    return false
  }

  // 验证文件类型
  if (props.fileType.length > 0) {
    const fileName = file.name
    const fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()
    if (!props.fileType.includes(fileExtension)) {
      ElMessage.error(`只允许上传${props.fileType.join(', ')}格式的文件`)
      return false
    }
  }

  return true
}

// 移除文件
const handleRemove = (file, fileList) => {
  if (props.limit === 1) {
    emit('update:modelValue', '')
  } else {
    const urls = fileList.map(item => item.response?.data?.url || item.url).filter(Boolean)
    emit('update:modelValue', urls)
  }
  emit('change', '', file, fileList)
}
</script>

<style lang="scss" scoped>
.upload-container {
  .el-upload__tip {
    color: #909399;
    font-size: 12px;
    margin-top: 8px;
  }
}
</style>


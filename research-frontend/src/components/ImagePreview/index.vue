<template>
  <div class="image-preview-container">
    <el-image
      v-if="src"
      :src="src"
      :preview-src-list="previewList"
      :initial-index="initialIndex"
      fit="cover"
      :preview-teleported="true"
      class="preview-image"
    >
      <template #error>
        <div class="image-slot">
          <el-icon><Picture /></el-icon>
        </div>
      </template>
    </el-image>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  src: {
    type: String,
    default: ''
  },
  previewList: {
    type: Array,
    default: () => []
  },
  initialIndex: {
    type: Number,
    default: 0
  }
})

const previewList = computed(() => {
  if (props.previewList && props.previewList.length > 0) {
    return props.previewList
  }
  if (props.src) {
    return [props.src]
  }
  return []
})
</script>

<style lang="scss" scoped>
.image-preview-container {
  .preview-image {
    width: 100%;
    height: 100%;
    cursor: pointer;
  }

  .image-slot {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    background: #f5f7fa;
    color: #909399;
    font-size: 30px;
  }
}
</style>


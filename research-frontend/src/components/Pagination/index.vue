<template>
  <div class="pagination-container" v-if="total > 0">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      :layout="layout"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  total: {
    type: Number,
    default: 0
  },
  pageNum: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  },
  layout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  }
})

const emit = defineEmits(['update:pageNum', 'update:pageSize', 'pagination-change'])

const currentPage = ref(props.pageNum)
const pageSize = ref(props.pageSize)

watch(() => props.pageNum, (val) => {
  currentPage.value = val
})

watch(() => props.pageSize, (val) => {
  pageSize.value = val
})

const handleSizeChange = (val) => {
  pageSize.value = val
  emit('update:pageSize', val)
  emit('pagination-change', { pageNum: currentPage.value, pageSize: val })
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  emit('update:pageNum', val)
  emit('pagination-change', { pageNum: val, pageSize: pageSize.value })
}
</script>

<style lang="scss" scoped>
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>


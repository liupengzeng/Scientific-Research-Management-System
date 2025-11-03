import { useUserStore } from '@/stores/user'

/**
 * 权限指令
 * 使用方式: v-permission="'system:user:add'"
 */
export default {
  mounted(el, binding) {
    const { value } = binding
    const userStore = useUserStore()
    const permissions = userStore.permissions || []

    if (value) {
      const hasPermission = permissions.includes(value)
      if (!hasPermission) {
        // 没有权限，移除元素
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error('需要设置权限标识，如: v-permission="\'system:user:add\'"')
    }
  },
  updated(el, binding) {
    const { value } = binding
    const userStore = useUserStore()
    const permissions = userStore.permissions || []

    if (value) {
      const hasPermission = permissions.includes(value)
      if (!hasPermission) {
        // 没有权限，移除元素
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  }
}


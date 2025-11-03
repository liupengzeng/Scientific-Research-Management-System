import pluginVue from 'eslint-plugin-vue'

// ESLint配置文件 - JavaScript版本
// 项目使用纯JavaScript，不涉及TypeScript

export default [
  // 忽略文件
  {
    ignores: ['dist/**', 'dist-ssr/**', 'coverage/**', 'node_modules/**']
  },
  // Vue文件配置
  ...pluginVue.configs['flat/essential'],
  // 全局配置
  {
    files: ['**/*.{js,mjs,cjs,vue}'],
    languageOptions: {
      ecmaVersion: 2022,
      sourceType: 'module',
      globals: {
        // Vue全局变量
        defineProps: 'readonly',
        defineEmits: 'readonly',
        defineExpose: 'readonly',
        withDefaults: 'readonly',
        // 浏览器全局变量
        console: 'readonly',
        window: 'readonly',
        document: 'readonly'
      }
    },
    rules: {
      // 覆盖Vue插件规则
      'vue/multi-word-component-names': 'off',
      'no-console': 'warn',
      'no-debugger': 'warn'
    }
  }
]


import pluginVue from 'eslint-plugin-vue'

// ESLint配置文件 - JavaScript版本
// 项目使用纯JavaScript，不涉及TypeScript

export default [
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
      },
    },
    plugins: {
      vue: pluginVue,
    },
    rules: {
      ...pluginVue.configs['flat/essential'].rules,
      // 自定义规则
      'vue/multi-word-component-names': 'off', // 允许单单词组件名
      'no-console': 'warn', // 警告console
      'no-debugger': 'warn', // 警告debugger
    },
    ignores: [
      '**/dist/**',
      '**/dist-ssr/**',
      '**/coverage/**',
      '**/node_modules/**',
    ],
  },
]


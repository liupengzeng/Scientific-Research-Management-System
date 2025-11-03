import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import directives from './directives'
import './styles/index.scss'

// 创建应用实例
const app = createApp(App)

// 注册Pinia
const pinia = createPinia()
app.use(pinia)

// 注册Vue Router
app.use(router)

// 注册Element Plus
app.use(ElementPlus)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册自定义指令
Object.keys(directives).forEach(key => {
  app.directive(key, directives[key])
})

// 挂载应用
app.mount('#app')

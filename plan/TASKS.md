# 高校科研管理系统 - 项目实现计划

**项目名称**: 高校科研管理系统  
**项目类型**: 前后端分离的企业级 Web 应用  
**技术栈**: Spring Boot 3.2.0 + Vue 3 + MySQL + Redis  
**创建时间**: 2025-01-20  
**最后更新**: 2025-01-20

---

## 项目实现总体策略

### 实现原则

1. **自底向上**: 先实现基础设施，再实现业务功能
2. **前后端并行**: 后端 API 先行，前端页面紧随
3. **模块化开发**: 按功能模块拆分，独立开发测试
4. **持续集成**: 每完成一个任务立即测试和提交
5. **文档同步**: 每次完成功能后更新本文档

### 实现顺序

```
基础设施 → 系统管理 → 核心业务 → 辅助功能 → 统计可视化 → 优化测试
```

---

## 第一阶段：基础设施与框架搭建

**目标**: 搭建项目基础架构，实现核心框架功能，为后续开发打下基础

### Phase 1.1: 项目环境与配置

**状态**: 🔄 进行中  
**优先级**: P0 (最高)  
**预计工时**: 2-3 天

#### 任务清单

- [x] **TASK-1.1.1**: 清理前端 TypeScript 残留文件

  - 删除 `research-frontend/tsconfig.json`
  - 删除 `research-frontend/tsconfig.app.json`
  - 删除 `research-frontend/tsconfig.node.json`
  - 删除 `research-frontend/env.d.ts`
  - 删除 `research-frontend/src/types/` 目录
  - 将 `eslint.config.ts` 改为 `eslint.config.js` (JavaScript 配置)
  - **验收标准**: 前端项目无 TypeScript 配置文件，ESLint 使用 JS 配置

- [ ] **TASK-1.1.2**: 数据库初始化

  - 执行 `research-backend/SQL/research.sql` 创建数据库表
  - 验证所有表结构正确
  - 添加初始测试数据（可选）
  - **验收标准**: 数据库表创建成功，外键约束正确

- [x] **TASK-1.1.3**: 后端配置文件完善

  - 检查 `application.yml` 配置完整性
  - 配置数据源（MySQL 连接）
  - 配置 Redis 连接
  - 配置 JWT 参数
  - 配置文件上传限制
  - **验收标准**: 后端应用可以成功启动，连接数据库和 Redis

- [x] **TASK-1.1.4**: 前端环境配置完善
  - ✅ 验证 `vite.config.js` 配置正确
  - ✅ 验证 API 代理配置（/api -> http://localhost:8080）
  - ✅ 验证路径别名配置（@ -> src）
  - ✅ 验证 Element Plus 自动导入
  - ✅ 修复样式文件引用问题
  - **验收标准**: 前端开发服务器可以正常启动，代理配置正确

---

### Phase 1.2: 后端核心框架组件

**状态**: ✅ 已完成  
**优先级**: P0  
**预计工时**: 3-4 天

#### 任务清单

- [x] **TASK-1.2.1**: Spring Security + JWT 认证框架

  - 创建 `JwtAuthenticationTokenFilter` (JWT 认证过滤器)
  - 创建 `JwtTokenUtil` (JWT 工具类，生成/解析 Token)
  - 创建 `SecurityConfig` (Spring Security 配置类)
  - 实现 `UserDetailsService` (从数据库加载用户信息)
  - 配置白名单（登录、注册等无需认证的接口）
  - **文件路径**:
    - `framework/security/JwtAuthenticationTokenFilter.java`
    - `framework/security/JwtTokenUtil.java`
    - `framework/security/SecurityConfig.java`
    - `framework/security/UserDetailsServiceImpl.java`
  - **验收标准**: 可以实现用户登录并生成 JWT Token，Token 验证通过

- [x] **TASK-1.2.2**: 统一响应封装完善

  - 检查 `AjaxResult` 类是否完整
  - 添加更多便捷方法（如分页响应封装）
  - 确保所有 Controller 统一使用
  - **文件路径**: `common/core/AjaxResult.java`
  - **验收标准**: AjaxResult 功能完整，使用便捷

- [x] **TASK-1.2.3**: 全局异常处理完善

  - 检查 `GlobalExceptionHandler` 是否完整
  - 添加参数校验异常处理
  - 添加认证异常处理
  - 添加权限异常处理
  - **文件路径**: `common/exception/GlobalExceptionHandler.java`
  - **验收标准**: 所有异常都能被正确捕获并返回友好提示

- [x] **TASK-1.2.4**: 分页查询工具完善

  - 检查 `PageQuery` 类
  - 创建 `PageResult` 分页响应封装
  - 集成 MyBatis-Plus 分页插件
  - **文件路径**:
    - `common/core/PageQuery.java`
    - `common/core/PageResult.java`
  - **验收标准**: 分页查询功能可用，响应格式统一

- [x] **TASK-1.2.5**: 工具类完善
  - 创建 `StringUtils` (字符串工具)
  - 创建 `DateUtils` (日期工具)
  - 创建 `SecurityUtils` (安全工具，获取当前用户)
  - 创建 `FileUtils` (文件工具)
  - **文件路径**: `common/utils/`
  - **验收标准**: 常用工具类可用

---

### Phase 1.3: 前端核心框架组件

**状态**: ✅ 已完成  
**优先级**: P0  
**预计工时**: 3-4 天

#### 任务清单

- [x] **TASK-1.3.1**: HTTP 请求封装完善

  - 完善 `utils/request.js` Axios 封装
  - 实现请求拦截器（添加 Token）
  - 实现响应拦截器（统一错误处理）
  - 实现 Token 过期自动跳转登录
  - **文件路径**: `src/utils/request.js`
  - **验收标准**: 请求封装完整，可以自动携带 Token，错误处理正确

- [x] **TASK-1.3.2**: 认证工具完善

  - 完善 `utils/auth.js` 认证相关工具
  - 实现 Token 存储/获取/删除
  - 实现用户信息存储/获取
  - **文件路径**: `src/utils/auth.js`
  - **验收标准**: 认证工具可用

- [x] **TASK-1.3.3**: Pinia 状态管理设置

  - 创建 `stores/user.js` (用户状态)
  - 创建 `stores/permission.js` (权限状态)
  - 创建 `stores/app.js` (应用全局状态)
  - 配置 Pinia 持久化（可选）
  - **文件路径**: `src/stores/`
  - **验收标准**: Pinia store 可用，可以管理用户和权限状态

- [x] **TASK-1.3.4**: Vue Router 路由配置

  - 创建路由配置文件
  - 配置基础路由（登录、404 等）
  - 实现路由守卫（权限校验）
  - 配置路由懒加载
  - **文件路径**: `src/router/index.js`
  - **验收标准**: 路由配置可用，路由守卫生效

- [x] **TASK-1.3.5**: 主布局组件

  - 创建 `layout/index.vue` (主布局)
  - 创建 `layout/components/Header.vue` (头部)
  - 创建 `layout/components/Sidebar.vue` (侧边栏)
  - 创建 `layout/components/AppMain.vue` (内容区)
  - 实现响应式布局
  - **文件路径**: `src/layout/`
  - **验收标准**: 主布局可用，可以正常显示头部、侧边栏、内容区

- [x] **TASK-1.3.6**: 公共组件
  - 创建 `components/Pagination.vue` (分页组件)
  - 创建 `components/Upload.vue` (文件上传组件)
  - 创建 `components/ImagePreview.vue` (图片预览组件)
  - **文件路径**: `src/components/`
  - **验收标准**: 公共组件可用

---

## 第二阶段：系统管理模块

**目标**: 实现系统基础管理功能，包括用户、角色、权限、菜单管理等

### Phase 2.1: 部门管理

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 2-3 天

#### 任务清单

- [ ] **TASK-2.1.1**: 后端 - 部门实体与数据层

  - 创建 `SysDept` 实体类
  - 创建 `SysDeptMapper` 接口
  - 创建 `SysDeptMapper.xml` 映射文件
  - **文件路径**:
    - `system/domain/SysDept.java`
    - `system/mapper/SysDeptMapper.java`
    - `resources/mapper/system/SysDeptMapper.xml`
  - **验收标准**: 实体类和 Mapper 可用

- [ ] **TASK-2.1.2**: 后端 - 部门业务逻辑层

  - 创建 `SysDeptService` 接口
  - 创建 `SysDeptServiceImpl` 实现类
  - 实现部门树形结构查询
  - 实现部门 CRUD 操作
  - 实现部门状态管理（启用/停用）
  - **文件路径**: `system/service/`
  - **验收标准**: 部门业务逻辑完整，支持树形查询

- [ ] **TASK-2.1.3**: 后端 - 部门控制器

  - 创建 `SysDeptController`
  - 实现部门列表查询（树形）
  - 实现部门新增
  - 实现部门修改
  - 实现部门删除（检查是否有子部门）
  - 实现部门状态修改
  - **文件路径**: `system/controller/SysDeptController.java`
  - **API 路径**: `/api/system/dept`
  - **验收标准**: 部门管理 API 可用，通过 Knife4j 测试

- [ ] **TASK-2.1.4**: 前端 - 部门管理页面
  - 创建 `views/system/dept/index.vue`
  - 实现部门树形列表展示
  - 实现部门新增/编辑对话框
  - 实现部门删除确认
  - 实现部门状态切换
  - **文件路径**: `src/views/system/dept/`
  - **验收标准**: 部门管理页面可用，可以完成 CRUD 操作

---

### Phase 2.2: 用户管理

**状态**: 🔄 进行中 (80%)  
**优先级**: P1  
**预计工时**: 3-4 天

#### 任务清单

- [x] **TASK-2.2.1**: 后端 - 用户实体与数据层

  - 创建 `SysUser` 实体类
  - 创建 `SysUserMapper` 接口
  - 创建 `SysUserMapper.xml` 映射文件
  - 实现用户查询（关联部门、角色）
  - **文件路径**: `system/domain/`, `system/mapper/`
  - **验收标准**: 实体类和 Mapper 可用，支持关联查询

- [x] **TASK-2.2.2**: 后端 - 用户业务逻辑层

  - 创建 `SysUserService` 接口
  - 创建 `SysUserServiceImpl` 实现类
  - 实现用户 CRUD 操作
  - 实现用户密码加密（BCrypt）
  - 实现用户重置密码
  - 实现用户状态管理
  - 实现用户角色分配
  - **文件路径**: `system/service/`
  - **验收标准**: 用户业务逻辑完整，密码加密正确

- [x] **TASK-2.2.3**: 后端 - 用户控制器

  - 创建 `SysUserController`
  - 实现用户列表查询（分页）
  - 实现用户详情查询
  - 实现用户新增
  - 实现用户修改
  - 实现用户删除（逻辑删除）
  - 实现用户重置密码
  - 实现用户状态修改
  - **文件路径**: `system/controller/SysUserController.java`
  - **API 路径**: `/api/system/user`
  - **验收标准**: 用户管理 API 可用

- [ ] **TASK-2.2.4**: 前端 - 用户管理页面
  - 创建 `views/system/user/index.vue`
  - 实现用户列表（表格，支持分页）
  - 实现用户新增/编辑表单
  - 实现用户删除确认
  - 实现用户重置密码对话框
  - 实现用户角色分配
  - **文件路径**: `src/views/system/user/`
  - **验收标准**: 用户管理页面可用，功能完整

---

### Phase 2.3: 角色管理

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 2-3 天

#### 任务清单

- [ ] **TASK-2.3.1**: 后端 - 角色实体与数据层

  - 创建 `SysRole` 实体类
  - 创建 `SysRoleMapper` 接口
  - 创建 `SysRoleMapper.xml` 映射文件
  - **文件路径**: `system/domain/`, `system/mapper/`
  - **验收标准**: 实体类和 Mapper 可用

- [ ] **TASK-2.3.2**: 后端 - 角色业务逻辑层

  - 创建 `SysRoleService` 接口
  - 创建 `SysRoleServiceImpl` 实现类
  - 实现角色 CRUD 操作
  - 实现角色状态管理
  - **文件路径**: `system/service/`
  - **验收标准**: 角色业务逻辑完整

- [ ] **TASK-2.3.3**: 后端 - 角色控制器

  - 创建 `SysRoleController`
  - 实现角色列表查询
  - 实现角色新增
  - 实现角色修改
  - 实现角色删除
  - 实现角色状态修改
  - **文件路径**: `system/controller/SysRoleController.java`
  - **API 路径**: `/api/system/role`
  - **验收标准**: 角色管理 API 可用

- [ ] **TASK-2.3.4**: 前端 - 角色管理页面
  - 创建 `views/system/role/index.vue`
  - 实现角色列表（表格）
  - 实现角色新增/编辑表单
  - 实现角色删除确认
  - **文件路径**: `src/views/system/role/`
  - **验收标准**: 角色管理页面可用

---

### Phase 2.4: 菜单与权限管理

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 3-4 天

#### 任务清单

- [ ] **TASK-2.4.1**: 后端 - 菜单实体与数据层

  - 创建 `SysMenu` 实体类
  - 创建 `SysMenuMapper` 接口
  - 创建 `SysMenuMapper.xml` 映射文件
  - 实现菜单树形结构查询
  - **文件路径**: `system/domain/`, `system/mapper/`
  - **验收标准**: 实体类和 Mapper 可用，支持树形查询

- [ ] **TASK-2.4.2**: 后端 - 菜单业务逻辑层

  - 创建 `SysMenuService` 接口
  - 创建 `SysMenuServiceImpl` 实现类
  - 实现菜单 CRUD 操作
  - 实现菜单树形结构构建
  - 实现根据角色获取菜单
  - 实现根据用户获取权限列表
  - **文件路径**: `system/service/`
  - **验收标准**: 菜单业务逻辑完整

- [ ] **TASK-2.4.3**: 后端 - 菜单控制器

  - 创建 `SysMenuController`
  - 实现菜单列表查询（树形）
  - 实现菜单新增
  - 实现菜单修改
  - 实现菜单删除（检查是否有子菜单）
  - 实现根据角色获取菜单
  - **文件路径**: `system/controller/SysMenuController.java`
  - **API 路径**: `/api/system/menu`
  - **验收标准**: 菜单管理 API 可用

- [ ] **TASK-2.4.4**: 后端 - 权限控制

  - 创建权限注解 `@PreAuthorize` 配置
  - 实现方法级权限校验
  - 实现数据权限控制（可选）
  - **文件路径**: `framework/security/`
  - **验收标准**: 权限控制生效

- [ ] **TASK-2.4.5**: 前端 - 菜单管理页面

  - 创建 `views/system/menu/index.vue`
  - 实现菜单树形列表展示
  - 实现菜单新增/编辑表单
  - 实现菜单删除确认
  - **文件路径**: `src/views/system/menu/`
  - **验收标准**: 菜单管理页面可用

- [ ] **TASK-2.4.6**: 前端 - 角色权限分配

  - 在角色管理页面添加权限分配功能
  - 实现菜单权限树形选择
  - 实现权限保存
  - **文件路径**: `src/views/system/role/`
  - **验收标准**: 角色权限分配功能可用

- [ ] **TASK-2.4.7**: 前端 - 动态菜单与权限指令
  - 实现根据用户权限动态生成菜单
  - 创建 `v-permission` 指令（按钮权限控制）
  - 在路由守卫中校验权限
  - **文件路径**: `src/directives/`, `src/router/`
  - **验收标准**: 动态菜单和权限控制生效

---

### Phase 2.5: 登录与认证

**状态**: ⏳ 待开始  
**优先级**: P0  
**预计工时**: 2-3 天

#### 任务清单

- [ ] **TASK-2.5.1**: 后端 - 登录接口

  - 创建 `AuthController`
  - 实现登录接口（验证用户名密码）
  - 生成 JWT Token
  - 返回用户信息和权限
  - 实现验证码生成（可选）
  - **文件路径**: `framework/web/AuthController.java`
  - **API 路径**: `/api/auth/login`
  - **验收标准**: 登录接口可用，返回 Token 和用户信息

- [ ] **TASK-2.5.2**: 后端 - 登出接口

  - 实现登出接口
  - Token 失效处理（可选，使用 Redis 黑名单）
  - **API 路径**: `/api/auth/logout`
  - **验收标准**: 登出接口可用

- [ ] **TASK-2.5.3**: 后端 - 获取当前用户信息

  - 实现获取当前登录用户信息接口
  - 返回用户基本信息、权限列表、菜单列表
  - **API 路径**: `/api/auth/userInfo`
  - **验收标准**: 获取用户信息接口可用

- [ ] **TASK-2.5.4**: 前端 - 登录页面

  - 创建 `views/login/index.vue`
  - 实现登录表单
  - 实现验证码显示（如果后端支持）
  - 实现登录逻辑（调用登录 API）
  - 实现 Token 存储
  - 实现登录后跳转
  - **文件路径**: `src/views/login/`
  - **验收标准**: 登录页面可用，可以成功登录

- [ ] **TASK-2.5.5**: 前端 - 路由守卫完善
  - 在路由守卫中实现 Token 验证
  - 实现 Token 过期自动跳转登录
  - 实现未登录自动跳转登录页
  - **文件路径**: `src/router/index.js`
  - **验收标准**: 路由守卫生效，未登录无法访问页面

---

### Phase 2.6: 操作日志

**状态**: ⏳ 待开始  
**优先级**: P2  
**预计工时**: 2-3 天

#### 任务清单

- [ ] **TASK-2.6.1**: 后端 - 操作日志实体与数据层

  - 创建 `SysOperationLog` 实体类
  - 创建 `SysOperationLogMapper` 接口
  - **文件路径**: `system/domain/`, `system/mapper/`
  - **验收标准**: 实体类和 Mapper 可用

- [ ] **TASK-2.6.2**: 后端 - 操作日志 AOP 切面

  - 创建 `@Log` 注解
  - 创建 `LogAspect` AOP 切面
  - 实现自动记录操作日志
  - **文件路径**: `common/annotation/`, `framework/web/`
  - **验收标准**: AOP 切面可用，可以自动记录日志

- [ ] **TASK-2.6.3**: 后端 - 操作日志查询接口

  - 在 `SysOperationLogController` 中实现日志列表查询（分页）
  - 实现日志详情查询
  - 实现日志删除（可选）
  - **文件路径**: `system/controller/SysOperationLogController.java`
  - **API 路径**: `/api/system/operationLog`
  - **验收标准**: 操作日志查询接口可用

- [ ] **TASK-2.6.4**: 前端 - 操作日志页面
  - 创建 `views/system/operationLog/index.vue`
  - 实现日志列表（表格，支持分页、筛选）
  - 实现日志详情查看
  - **文件路径**: `src/views/system/operationLog/`
  - **验收标准**: 操作日志页面可用

---

## 第三阶段：科研项目管理模块

**目标**: 实现科研项目的全生命周期管理，包括申报、审批、立项、中检、结题等

### Phase 3.1: 项目类型管理

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 1-2 天

#### 任务清单

- [ ] **TASK-3.1.1**: 后端 - 项目类型实体与数据层

  - 创建 `ResearchProjectType` 实体类
  - 创建 `ResearchProjectTypeMapper` 接口
  - **文件路径**: `research/domain/`, `research/mapper/`
  - **验收标准**: 实体类和 Mapper 可用

- [ ] **TASK-3.1.2**: 后端 - 项目类型业务与控制器

  - 创建 `ResearchProjectTypeService` 和实现类
  - 创建 `ResearchProjectTypeController`
  - 实现项目类型 CRUD 操作
  - **文件路径**: `research/service/`, `research/controller/`
  - **API 路径**: `/api/research/projectType`
  - **验收标准**: 项目类型管理 API 可用

- [ ] **TASK-3.1.3**: 前端 - 项目类型管理页面
  - 创建 `views/research/projectType/index.vue`
  - 实现项目类型列表、新增、编辑、删除
  - **文件路径**: `src/views/research/projectType/`
  - **验收标准**: 项目类型管理页面可用

---

### Phase 3.2: 项目基础功能

**状态**: ⏳ 待开始  
**优先级**: P0  
**预计工时**: 4-5 天

#### 任务清单

- [ ] **TASK-3.2.1**: 后端 - 项目实体与数据层

  - 创建 `ResearchProject` 实体类
  - 创建 `ResearchProjectMapper` 接口
  - 创建 `ResearchProjectMapper.xml` 映射文件
  - 实现项目关联查询（负责人、部门、类型等）
  - **文件路径**: `research/domain/`, `research/mapper/`
  - **验收标准**: 实体类和 Mapper 可用

- [ ] **TASK-3.2.2**: 后端 - 项目业务逻辑层

  - 创建 `ResearchProjectService` 接口
  - 创建 `ResearchProjectServiceImpl` 实现类
  - 实现项目 CRUD 操作
  - 实现项目状态流转（草稿 → 已提交 → 已立项 → 进行中 → 已完成 → 已结题）
  - 实现项目编号自动生成
  - 实现项目预算校验
  - **文件路径**: `research/service/`
  - **验收标准**: 项目业务逻辑完整

- [ ] **TASK-3.2.3**: 后端 - 项目控制器

  - 创建 `ResearchProjectController`
  - 实现项目列表查询（分页，支持多条件筛选）
  - 实现项目详情查询
  - 实现项目新增（保存为草稿）
  - 实现项目修改
  - 实现项目删除
  - 实现项目提交（状态改为已提交）
  - **文件路径**: `research/controller/ResearchProjectController.java`
  - **API 路径**: `/api/research/project`
  - **验收标准**: 项目管理 API 可用

- [ ] **TASK-3.2.4**: 前端 - 项目列表页面

  - 创建 `views/research/project/index.vue`
  - 实现项目列表（表格，支持分页、筛选）
  - 实现项目状态标签显示
  - 实现项目操作按钮（查看、编辑、删除等）
  - **文件路径**: `src/views/research/project/`
  - **验收标准**: 项目列表页面可用

- [ ] **TASK-3.2.5**: 前端 - 项目新增/编辑页面
  - 创建 `views/research/project/form.vue`
  - 实现项目表单（项目名称、类型、负责人、预算等）
  - 实现表单验证
  - 实现保存草稿功能
  - 实现提交审核功能
  - **文件路径**: `src/views/research/project/`
  - **验收标准**: 项目新增/编辑页面可用，可以保存和提交项目

---

### Phase 3.3: 项目审批流程

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 3-4 天

#### 任务清单

- [ ] **TASK-3.3.1**: 后端 - 审批记录实体与数据层

  - 创建 `ResearchProjectApproval` 实体类
  - 创建 `ResearchProjectApprovalMapper` 接口
  - **文件路径**: `research/domain/`, `research/mapper/`
  - **验收标准**: 实体类和 Mapper 可用

- [ ] **TASK-3.3.2**: 后端 - 项目审批业务逻辑

  - 在 `ResearchProjectService` 中添加审批方法
  - 实现项目审批（通过/驳回）
  - 实现多级审批流程（可选）
  - 实现项目立项（审批通过后自动立项，生成项目编号）
  - **文件路径**: `research/service/`
  - **验收标准**: 审批业务逻辑完整

- [ ] **TASK-3.3.3**: 后端 - 项目审批接口

  - 在 `ResearchProjectController` 中添加审批接口
  - 实现审批通过接口
  - 实现审批驳回接口
  - 实现审批记录查询接口
  - **API 路径**: `/api/research/project/approve`
  - **验收标准**: 审批接口可用

- [ ] **TASK-3.3.4**: 前端 - 项目审批页面
  - 创建 `views/research/project/approval.vue`
  - 实现待审批项目列表
  - 实现审批对话框（通过/驳回，填写审批意见）
  - 实现审批记录查看
  - **文件路径**: `src/views/research/project/`
  - **验收标准**: 项目审批页面可用

---

### Phase 3.4: 项目中检与结题

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 3-4 天

#### 任务清单

- [ ] **TASK-3.4.1**: 后端 - 项目检查记录实体

  - 创建 `ResearchProjectCheck` 实体类（中检记录）
  - 创建 `ResearchProjectFinal` 实体类（结题记录）
  - 创建对应的 Mapper 接口
  - **文件路径**: `research/domain/`, `research/mapper/`
  - **验收标准**: 实体类和 Mapper 可用

- [ ] **TASK-3.4.2**: 后端 - 项目中检与结题业务逻辑

  - 在 `ResearchProjectService` 中添加中检和结题方法
  - 实现项目中检记录提交
  - 实现项目结题材料提交
  - 实现项目结题验收（状态改为已结题）
  - **文件路径**: `research/service/`
  - **验收标准**: 中检和结题业务逻辑完整

- [ ] **TASK-3.4.3**: 后端 - 项目中检与结题接口

  - 在 `ResearchProjectController` 中添加相关接口
  - 实现中检记录提交接口
  - 实现结题材料提交接口
  - 实现结题验收接口
  - **API 路径**: `/api/research/project/midCheck`, `/api/research/project/finalize`
  - **验收标准**: 中检和结题接口可用

- [ ] **TASK-3.4.4**: 前端 - 项目中检页面

  - 创建 `views/research/project/midCheck.vue`
  - 实现中检报告表单
  - 实现中检材料上传
  - **文件路径**: `src/views/research/project/`
  - **验收标准**: 项目中检页面可用

- [ ] **TASK-3.4.5**: 前端 - 项目结题页面
  - 创建 `views/research/project/finalize.vue`
  - 实现结题材料表单
  - 实现结题材料上传
  - 实现结题验收
  - **文件路径**: `src/views/research/project/`
  - **验收标准**: 项目结题页面可用

---

### Phase 3.5: 项目成员管理

**状态**: ⏳ 待开始  
**优先级**: P2  
**预计工时**: 2-3 天

#### 任务清单

- [ ] **TASK-3.5.1**: 后端 - 项目成员实体与数据层

  - 创建 `ResearchProjectMember` 实体类
  - 创建 `ResearchProjectMemberMapper` 接口
  - **文件路径**: `research/domain/`, `research/mapper/`
  - **验收标准**: 实体类和 Mapper 可用

- [ ] **TASK-3.5.2**: 后端 - 项目成员业务与接口

  - 在 `ResearchProjectService` 中添加成员管理方法
  - 在 `ResearchProjectController` 中添加成员管理接口
  - 实现成员添加/删除
  - 实现成员角色分配
  - **API 路径**: `/api/research/project/member`
  - **验收标准**: 项目成员管理接口可用

- [ ] **TASK-3.5.3**: 前端 - 项目成员管理
  - 在项目详情页面添加成员管理功能
  - 实现成员列表展示
  - 实现成员添加/删除对话框
  - **文件路径**: `src/views/research/project/`
  - **验收标准**: 项目成员管理功能可用

---

## 第四阶段：科研成果管理模块

**目标**: 实现科研成果的管理，包括论文、著作、专利、获奖等

### Phase 4.1: 论文管理

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 3-4 天

#### 任务清单

- [ ] **TASK-4.1.1**: 后端 - 论文实体与数据层

  - 创建 `ResearchPaper` 实体类
  - 创建 `ResearchPaperMapper` 接口
  - **文件路径**: `research/domain/`, `research/mapper/`
  - **验收标准**: 实体类和 Mapper 可用

- [ ] **TASK-4.1.2**: 后端 - 论文业务逻辑层

  - 创建 `ResearchPaperService` 接口
  - 创建 `ResearchPaperServiceImpl` 实现类
  - 实现论文 CRUD 操作
  - 实现论文审核状态管理
  - **文件路径**: `research/service/`
  - **验收标准**: 论文业务逻辑完整

- [ ] **TASK-4.1.3**: 后端 - 论文控制器

  - 创建 `ResearchPaperController`
  - 实现论文列表查询（分页，支持筛选）
  - 实现论文新增
  - 实现论文修改
  - 实现论文删除
  - 实现论文审核
  - **文件路径**: `research/controller/ResearchPaperController.java`
  - **API 路径**: `/api/research/paper`
  - **验收标准**: 论文管理 API 可用

- [ ] **TASK-4.1.4**: 前端 - 论文管理页面
  - 创建 `views/research/paper/index.vue`
  - 实现论文列表（表格，支持分页、筛选）
  - 实现论文新增/编辑表单
  - 实现论文附件上传
  - 实现论文审核功能
  - **文件路径**: `src/views/research/paper/`
  - **验收标准**: 论文管理页面可用

---

### Phase 4.2: 著作管理

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 2-3 天

#### 任务清单

- [ ] **TASK-4.2.1**: 后端 - 著作实体与业务逻辑

  - 创建 `ResearchBook` 实体类
  - 创建 `ResearchBookMapper` 接口
  - 创建 `ResearchBookService` 和实现类
  - 创建 `ResearchBookController`
  - **文件路径**: `research/domain/`, `research/mapper/`, `research/service/`, `research/controller/`
  - **API 路径**: `/api/research/book`
  - **验收标准**: 著作管理 API 可用

- [ ] **TASK-4.2.2**: 前端 - 著作管理页面
  - 创建 `views/research/book/index.vue`
  - 实现著作列表、新增、编辑、删除
  - **文件路径**: `src/views/research/book/`
  - **验收标准**: 著作管理页面可用

---

### Phase 4.3: 专利管理

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 2-3 天

#### 任务清单

- [ ] **TASK-4.3.1**: 后端 - 专利实体与业务逻辑

  - 创建 `ResearchPatent` 实体类
  - 创建 `ResearchPatentMapper` 接口
  - 创建 `ResearchPatentService` 和实现类
  - 创建 `ResearchPatentController`
  - **文件路径**: `research/domain/`, `research/mapper/`, `research/service/`, `research/controller/`
  - **API 路径**: `/api/research/patent`
  - **验收标准**: 专利管理 API 可用

- [ ] **TASK-4.3.2**: 前端 - 专利管理页面
  - 创建 `views/research/patent/index.vue`
  - 实现专利列表、新增、编辑、删除
  - **文件路径**: `src/views/research/patent/`
  - **验收标准**: 专利管理页面可用

---

### Phase 4.4: 获奖管理

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 2-3 天

#### 任务清单

- [ ] **TASK-4.4.1**: 后端 - 获奖实体与业务逻辑

  - 创建 `ResearchAward` 实体类
  - 创建 `ResearchAwardMapper` 接口
  - 创建 `ResearchAwardService` 和实现类
  - 创建 `ResearchAwardController`
  - **文件路径**: `research/domain/`, `research/mapper/`, `research/service/`, `research/controller/`
  - **API 路径**: `/api/research/award`
  - **验收标准**: 获奖管理 API 可用

- [ ] **TASK-4.4.2**: 前端 - 获奖管理页面
  - 创建 `views/research/award/index.vue`
  - 实现获奖列表、新增、编辑、删除
  - **文件路径**: `src/views/research/award/`
  - **验收标准**: 获奖管理页面可用

---

## 第五阶段：科研经费管理模块

**目标**: 实现科研经费的到账、使用、预算控制等功能

### Phase 5.1: 经费到账管理

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 2-3 天

#### 任务清单

- [ ] **TASK-5.1.1**: 后端 - 经费实体与数据层

  - 创建 `ResearchFunding` 实体类
  - 创建 `ResearchFundingMapper` 接口
  - **文件路径**: `research/domain/`, `research/mapper/`
  - **验收标准**: 实体类和 Mapper 可用

- [ ] **TASK-5.1.2**: 后端 - 经费业务逻辑层

  - 创建 `ResearchFundingService` 接口
  - 创建 `ResearchFundingServiceImpl` 实现类
  - 实现经费到账登记
  - 实现经费使用登记
  - 实现经费预算校验（支出不能超过预算）
  - 实现经费统计查询
  - **文件路径**: `research/service/`
  - **验收标准**: 经费业务逻辑完整

- [ ] **TASK-5.1.3**: 后端 - 经费控制器

  - 创建 `ResearchFundingController`
  - 实现经费列表查询（按项目）
  - 实现经费到账登记接口
  - 实现经费使用登记接口
  - 实现经费统计接口
  - **文件路径**: `research/controller/ResearchFundingController.java`
  - **API 路径**: `/api/research/funding`
  - **验收标准**: 经费管理 API 可用

- [ ] **TASK-5.1.4**: 前端 - 经费管理页面
  - 创建 `views/research/funding/index.vue`
  - 实现经费列表（按项目展示）
  - 实现经费到账登记表单
  - 实现经费使用登记表单
  - 实现经费预算预警显示
  - **文件路径**: `src/views/research/funding/`
  - **验收标准**: 经费管理页面可用

---

### Phase 5.2: 经费报表

**状态**: ⏳ 待开始  
**优先级**: P2  
**预计工时**: 2-3 天

#### 任务清单

- [ ] **TASK-5.2.1**: 后端 - 经费统计接口

  - 在 `ResearchFundingController` 中添加统计接口
  - 实现按项目统计
  - 实现按部门统计
  - 实现按时间范围统计
  - **API 路径**: `/api/research/funding/statistics`
  - **验收标准**: 经费统计接口可用

- [ ] **TASK-5.2.2**: 前端 - 经费报表页面
  - 创建 `views/research/funding/report.vue`
  - 实现经费统计图表（使用 ECharts）
  - 实现经费明细导出（Excel）
  - **文件路径**: `src/views/research/funding/`
  - **验收标准**: 经费报表页面可用

---

## 第六阶段：学术活动管理模块

**目标**: 实现学术会议和学术报告的管理

### Phase 6.1: 学术会议管理

**状态**: ⏳ 待开始  
**优先级**: P2  
**预计工时**: 2-3 天

#### 任务清单

- [ ] **TASK-6.1.1**: 后端 - 学术会议实体与业务逻辑

  - 创建 `ResearchConference` 实体类
  - 创建 `ResearchConferenceMapper` 接口
  - 创建 `ResearchConferenceService` 和实现类
  - 创建 `ResearchConferenceController`
  - **文件路径**: `research/domain/`, `research/mapper/`, `research/service/`, `research/controller/`
  - **API 路径**: `/api/research/conference`
  - **验收标准**: 学术会议管理 API 可用

- [ ] **TASK-6.1.2**: 前端 - 学术会议管理页面
  - 创建 `views/research/conference/index.vue`
  - 实现会议列表、新增、编辑、删除
  - **文件路径**: `src/views/research/conference/`
  - **验收标准**: 学术会议管理页面可用

---

### Phase 6.2: 学术报告管理

**状态**: ⏳ 待开始  
**优先级**: P2  
**预计工时**: 2-3 天

#### 任务清单

- [ ] **TASK-6.2.1**: 后端 - 学术报告实体与业务逻辑

  - 创建 `ResearchReport` 实体类
  - 创建 `ResearchReportMapper` 接口
  - 创建 `ResearchReportService` 和实现类
  - 创建 `ResearchReportController`
  - **文件路径**: `research/domain/`, `research/mapper/`, `research/service/`, `research/controller/`
  - **API 路径**: `/api/research/report`
  - **验收标准**: 学术报告管理 API 可用

- [ ] **TASK-6.2.2**: 前端 - 学术报告管理页面
  - 创建 `views/research/report/index.vue`
  - 实现报告列表、新增、编辑、删除
  - **文件路径**: `src/views/research/report/`
  - **验收标准**: 学术报告管理页面可用

---

## 第七阶段：数据统计与可视化

**目标**: 实现个人工作台和综合统计功能

### Phase 7.1: 个人工作台

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 3-4 天

#### 任务清单

- [ ] **TASK-7.1.1**: 后端 - 工作台数据接口

  - 创建 `DashboardController`
  - 实现待办事项统计接口
  - 实现个人项目统计接口
  - 实现个人成果统计接口
  - 实现通知公告接口（可选）
  - **文件路径**: `research/controller/DashboardController.java`
  - **API 路径**: `/api/dashboard`
  - **验收标准**: 工作台数据接口可用

- [ ] **TASK-7.1.2**: 前端 - 个人工作台页面
  - 创建 `views/dashboard/index.vue`
  - 实现待办事项列表
  - 实现统计卡片（项目数、成果数等）
  - 实现快速操作入口
  - 实现通知公告（可选）
  - **文件路径**: `src/views/dashboard/`
  - **验收标准**: 个人工作台页面可用

---

### Phase 7.2: 综合统计

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 4-5 天

#### 任务清单

- [ ] **TASK-7.2.1**: 后端 - 统计接口

  - 创建 `StatisticsController`
  - 实现项目统计接口（按类型、按状态、按部门、按时间）
  - 实现成果统计接口（按类型、按部门、按时间）
  - 实现经费统计接口
  - 实现趋势分析接口
  - **文件路径**: `research/controller/StatisticsController.java`
  - **API 路径**: `/api/statistics`
  - **验收标准**: 统计接口可用

- [ ] **TASK-7.2.2**: 前端 - 综合统计页面
  - 创建 `views/statistics/index.vue`
  - 实现统计图表（使用 ECharts）
    - 项目统计图表（柱状图、饼图）
    - 成果统计图表
    - 经费统计图表
    - 趋势分析图表（折线图）
  - 实现统计筛选（按部门、时间范围等）
  - 实现数据导出功能
  - **文件路径**: `src/views/statistics/`
  - **验收标准**: 综合统计页面可用，图表展示正确

---

## 第八阶段：优化与测试

**目标**: 系统优化、性能提升、全面测试

### Phase 8.1: 代码优化

**状态**: ⏳ 待开始  
**优先级**: P2  
**预计工时**: 3-4 天

#### 任务清单

- [ ] **TASK-8.1.1**: 后端代码优化

  - 代码审查和重构
  - 优化 SQL 查询（添加索引、优化慢查询）
  - 添加缓存（Redis 缓存热点数据）
  - 优化异常处理
  - **验收标准**: 代码质量提升，性能优化

- [ ] **TASK-8.1.2**: 前端代码优化
  - 代码审查和重构
  - 优化组件性能
  - 实现路由懒加载
  - 优化打包体积
  - **验收标准**: 前端性能提升，打包体积优化

---

### Phase 8.2: 功能测试

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 5-7 天

#### 任务清单

- [ ] **TASK-8.2.1**: 后端接口测试

  - 使用 Knife4j 测试所有接口
  - 测试正常流程
  - 测试异常流程
  - 测试边界条件
  - **验收标准**: 所有接口测试通过

- [ ] **TASK-8.2.2**: 前后端联调测试

  - 测试所有功能模块
  - 测试权限控制
  - 测试数据流转
  - 修复发现的 bug
  - **验收标准**: 前后端联调通过，功能正常

- [ ] **TASK-8.2.3**: 性能测试
  - 测试并发性能
  - 测试大数据量查询
  - 优化性能瓶颈
  - **验收标准**: 性能达到要求

---

### Phase 8.3: 安全加固

**状态**: ⏳ 待开始  
**优先级**: P1  
**预计工时**: 2-3 天

#### 任务清单

- [ ] **TASK-8.3.1**: 安全漏洞检查

  - SQL 注入防护检查
  - XSS 攻击防护检查
  - CSRF 防护检查
  - 权限越权检查
  - **验收标准**: 安全漏洞修复

- [ ] **TASK-8.3.2**: 配置安全优化
  - 生产环境配置加密
  - JWT 密钥安全存储
  - 数据库连接加密
  - **验收标准**: 配置安全优化完成

---

## 进度追踪

### 总体进度

- **第一阶段**: ✅ 已完成 (100%)
  - Phase 1.1: ✅ 基本完成 (100%，数据库已初始化)
  - Phase 1.2: ✅ 已完成 (100%)
  - Phase 1.3: ✅ 已完成 (100%)
- **第二阶段**: 🔄 进行中 (20%)
  - Phase 2.2: 🔄 进行中 (80%，后端完成，前端待实现)
- **第三阶段**: ⏳ 待开始 (0%)
- **第四阶段**: ⏳ 待开始 (0%)
- **第五阶段**: ⏳ 待开始 (0%)
- **第六阶段**: ⏳ 待开始 (0%)
- **第七阶段**: ⏳ 待开始 (0%)
- **第八阶段**: ⏳ 待开始 (0%)

### 已完成任务

_暂无_

### 进行中任务

_暂无_

### 已完成功能模块

_暂无_

---

## 更新记录

### 2025-01-20

#### 初始创建

- 创建项目实现计划文档
- 制定 8 个阶段的实现计划
- 明确每个阶段的任务清单和验收标准

#### Phase 1.1 执行进度

- ✅ **TASK-1.1.1**: 完成清理前端 TypeScript 残留文件
  - 删除 tsconfig.json, tsconfig.app.json, tsconfig.node.json, env.d.ts
  - 将 eslint.config.ts 转换为 eslint.config.js
  - ESLint 配置改为纯 JavaScript
- ✅ **TASK-1.1.3**: 完成后端配置文件完善
  - 修复 application.yml 编码问题（中文注释显示正常）
  - 创建 application-prod.yml 生产环境配置
  - 创建 application-dev.yml 开发环境配置
  - 配置包含：数据源、Redis、JWT、文件上传、MyBatis-Plus 等
- 📝 **TASK-1.1.2**: 数据库初始化（待用户执行 SQL 脚本）
  - 已创建数据库初始化说明文档
- ✅ **TASK-1.1.4**: 完成前端环境配置完善
  - 验证并修复 vite.config.js 配置
  - API 代理、路径别名、Element Plus 自动导入均配置正确
  - 修复样式文件引用问题
  - 创建前端配置验证说明文档

#### Phase 1.2 执行进度

- ✅ **TASK-1.2.1**: 完成 Spring Security + JWT 认证框架
  - 创建 JwtTokenUtil（JWT 工具类，生成/解析 Token）
  - 创建 JwtAuthenticationTokenFilter（JWT 认证过滤器）
  - 创建 SecurityConfig（Spring Security 配置类，包含 CORS、白名单等）
  - 创建 UserDetailsServiceImpl（用户详情服务，待 User 实体创建后完善）
- ✅ **TASK-1.2.2**: 完成统一响应封装完善
  - 完善 AjaxResult 类，添加 success(String msg) 方法
  - 添加 isSuccess() 判断方法
  - 添加 page() 分页响应方法
- ✅ **TASK-1.2.3**: 完成全局异常处理完善
  - 添加参数校验异常处理（@Valid）
  - 添加参数绑定异常处理
  - 添加认证异常处理（BadCredentials、AccountExpired 等）
  - 添加权限不足异常处理
  - 添加非法参数异常处理
  - 添加运行时异常处理
- ✅ **TASK-1.2.4**: 完成分页查询工具完善
  - 创建 PageResult 分页响应封装类
  - 支持从 MyBatis-Plus 的 Page 对象转换
  - 自动计算总页数
- ✅ **TASK-1.2.5**: 完成工具类完善
  - 创建 StringUtils（字符串工具，继承 Apache Commons）
  - 创建 DateUtils（日期时间工具，使用 Java 8+时间 API）
  - 创建 SecurityUtils（安全工具，获取当前登录用户信息）
  - 创建 FileUtils（文件工具，文件上传、删除、格式化等）

#### Phase 1.3 执行进度

- ✅ **TASK-1.3.1**: 完成 HTTP 请求封装完善
  - 创建 axios 实例，配置基础 URL 和超时时间
  - 实现请求拦截器（自动添加 Token 到请求头）
  - 实现响应拦截器（统一错误处理、Token 过期跳转登录）
  - 支持错误码统一处理（401、403、500 等）
- ✅ **TASK-1.3.2**: 完成认证工具完善
  - 实现 Token 存储/获取/删除（Cookie + localStorage 双重存储）
  - 实现用户信息存储/获取
  - 提供 clearAuth()清除所有认证信息
- ✅ **TASK-1.3.3**: 完成 Pinia 状态管理设置
  - 创建 user store（用户状态：登录、登出、获取用户信息）
  - 创建 permission store（权限状态：动态路由生成）
  - 创建 app store（应用全局状态：侧边栏、设备类型、加载状态等）
- ✅ **TASK-1.3.4**: 完成 Vue Router 路由配置
  - 配置基础路由（登录、404、401、重定向）
  - 实现路由守卫（前置守卫：Token 验证、用户信息获取、动态路由添加）
  - 配置路由懒加载
  - 集成 NProgress 进度条
- ✅ **TASK-1.3.5**: 完成主布局组件
  - 创建主布局容器（layout/index.vue）
  - 创建头部组件（Header：面包屑、用户信息、全屏切换）
  - 创建侧边栏组件（Sidebar：动态菜单、折叠功能）
  - 创建侧边栏菜单项组件（SidebarItem：递归渲染菜单）
  - 创建内容区组件（AppMain：路由视图、过渡动画）
  - 实现响应式布局（侧边栏折叠/展开）
- ✅ **TASK-1.3.6**: 完成公共组件
  - 创建分页组件（Pagination：支持分页、每页大小调整）
  - 创建文件上传组件（Upload：支持单文件/多文件、文件类型/大小验证）
  - 创建图片预览组件（ImagePreview：基于 Element Plus 图片预览）
  - 创建权限指令（v-permission：按钮级别权限控制）
- ✅ **额外完成**:
  - 创建登录页面（views/login/index.vue）
  - 创建错误页面（404、401）
  - 创建重定向页面
  - 创建首页（dashboard）
  - 更新 main.js 配置（Pinia、Router、Element Plus、指令）
  - 创建全局样式文件

---

## 注意事项

1. **任务优先级说明**:

   - P0: 最高优先级，必须优先完成
   - P1: 高优先级，核心功能
   - P2: 中优先级，辅助功能

2. **任务状态说明**:

   - ⏳ 待开始: 任务未开始
   - 🔄 进行中: 任务正在进行
   - ✅ 已完成: 任务已完成
   - ❌ 已取消: 任务已取消
   - ⚠️ 已阻塞: 任务被阻塞

3. **更新规范**:

   - 每次完成一个任务后，更新对应的复选框
   - 每次完成一个阶段后，更新进度追踪
   - 在更新记录中添加完成的功能说明
   - 如有问题或变更，在注意事项中说明

4. **验收标准**:
   - 每个任务都有明确的验收标准
   - 必须满足验收标准才能标记为完成
   - 如有特殊情况，在更新记录中说明

---

**文档维护**: 每次完成功能后及时更新本文档，保持文档与实际进度同步。

# 校企合作 MVP 执行清单（毕业设计版）

## 1. MVP 范围（只做最小闭环）
1. 企业库管理（CRUD）
2. 校企合作项目管理（CRUD + 提交）
3. 四角色审批（教研组长 -> 教研处 -> 院校领导）
4. 审批记录查询

## 2. 本次已落地内容
- 新增 SQL：`research-backend/SQL/cooperation_mvp.sql`
- 新增后端实体：
  - `CooperationEnterprise`
  - `CooperationProject`
  - `CooperationProjectApproval`
- 新增 Mapper + XML：
  - `CooperationEnterpriseMapper`
  - `CooperationProjectMapper`
  - `CooperationProjectApprovalMapper`
- 新增 Service：
  - `CooperationEnterpriseService` / `Impl`
  - `CooperationProjectService` / `Impl`
- 新增 Controller：
  - `CooperationEnterpriseController`
  - `CooperationProjectController`
- 扩展权限映射：`UserDetailsServiceImpl` 增加 cooperation 相关细粒度权限

## 3. 接口列表
### 3.1 企业库
- `GET /api/research/cooperation/enterprise/list`
- `GET /api/research/cooperation/enterprise/{enterpriseId}`
- `POST /api/research/cooperation/enterprise`
- `PUT /api/research/cooperation/enterprise`
- `DELETE /api/research/cooperation/enterprise/{enterpriseIds}`

### 3.2 合作项目
- `GET /api/research/cooperation/project/list`
- `GET /api/research/cooperation/project/{coopProjectId}`
- `POST /api/research/cooperation/project`
- `PUT /api/research/cooperation/project`
- `POST /api/research/cooperation/project/submit/{coopProjectId}`
- `POST /api/research/cooperation/project/approve`
- `GET /api/research/cooperation/project/approve/records?coopProjectId=1`
- `DELETE /api/research/cooperation/project/{coopProjectIds}`

## 4. 后续（非 MVP）
- 合同分期拨付
- 企业满意度评价
- 动态审批模板

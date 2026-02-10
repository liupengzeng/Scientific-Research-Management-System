-- 校企合作 MVP 初始化 SQL（控制为 7-9 字段）
USE research_management;

CREATE TABLE IF NOT EXISTS cooperation_enterprise (
  enterprise_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  enterprise_name VARCHAR(200) NOT NULL COMMENT '企业名称',
  credit_code VARCHAR(64) NOT NULL COMMENT '统一社会信用代码',
  industry VARCHAR(100) NOT NULL COMMENT '行业',
  contact_name VARCHAR(100) NOT NULL COMMENT '联系人',
  contact_phone VARCHAR(30) NOT NULL COMMENT '联系人电话',
  status VARCHAR(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='校企合作-企业表';

CREATE TABLE IF NOT EXISTS cooperation_project (
  coop_project_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  project_no VARCHAR(64) NOT NULL COMMENT '项目编号',
  project_name VARCHAR(200) NOT NULL COMMENT '项目名称',
  enterprise_id BIGINT NOT NULL COMMENT '企业ID',
  teacher_id BIGINT NOT NULL COMMENT '教师负责人ID',
  budget DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '预算金额',
  status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态(draft/submitted/approved)',
  current_step TINYINT NOT NULL DEFAULT 1 COMMENT '审批节点(1-3)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  CONSTRAINT fk_coop_project_enterprise FOREIGN KEY (enterprise_id) REFERENCES cooperation_enterprise(enterprise_id)
) COMMENT='校企合作-项目表';

CREATE TABLE IF NOT EXISTS cooperation_project_approval (
  approval_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  coop_project_id BIGINT NOT NULL COMMENT '项目ID',
  step_no TINYINT NOT NULL COMMENT '步骤(1组长/2教研处/3院校领导)',
  approver_id BIGINT NOT NULL COMMENT '审批人ID',
  approver_role VARCHAR(64) NOT NULL COMMENT '审批人角色',
  decision VARCHAR(20) NOT NULL COMMENT '审批结果(approve/reject)',
  comment VARCHAR(500) NOT NULL COMMENT '审批意见',
  final_flag TINYINT NOT NULL DEFAULT 0 COMMENT '是否终审(0否1是)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  CONSTRAINT fk_coop_project_approval FOREIGN KEY (coop_project_id) REFERENCES cooperation_project(coop_project_id)
) COMMENT='校企合作-审批表';

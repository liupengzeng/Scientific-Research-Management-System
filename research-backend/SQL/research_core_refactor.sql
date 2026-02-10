-- 精简重构版核心表（7-9字段）
USE research_management;

DROP TABLE IF EXISTS research_project_approval_core;
DROP TABLE IF EXISTS research_project_core;

CREATE TABLE research_project_core (
  project_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  project_no VARCHAR(64) NOT NULL COMMENT '项目编号',
  project_name VARCHAR(200) NOT NULL COMMENT '项目名称',
  applicant_id BIGINT NOT NULL COMMENT '申请人ID(教师)',
  dept_id BIGINT NOT NULL COMMENT '所属部门ID',
  total_budget DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '申请经费',
  project_status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态',
  current_step TINYINT NOT NULL DEFAULT 1 COMMENT '当前审批节点(1-3)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='科研项目核心表(精简版)';

CREATE TABLE research_project_approval_core (
  approval_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  project_id BIGINT NOT NULL COMMENT '项目ID',
  step_no TINYINT NOT NULL COMMENT '审批步骤(1组长/2教研处/3院校领导)',
  approver_id BIGINT NOT NULL COMMENT '审批人ID',
  approver_role VARCHAR(64) NOT NULL COMMENT '审批人角色',
  decision VARCHAR(20) NOT NULL COMMENT '审批结果(approve/reject)',
  comment VARCHAR(500) NOT NULL COMMENT '审批意见',
  final_flag TINYINT NOT NULL DEFAULT 0 COMMENT '是否终审(0否1是)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
  CONSTRAINT fk_core_project_approval FOREIGN KEY (project_id) REFERENCES research_project_core(project_id)
) COMMENT='科研项目审批核心表(精简版)';

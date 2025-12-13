-- 创建数据库
CREATE DATABASE IF NOT EXISTS `research_management`
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `research_management`;

-- 部门表（学院、系部）
CREATE TABLE `sys_dept` (
                            `dept_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
                            `parent_id` BIGINT DEFAULT 0 COMMENT '父部门ID',
                            `dept_name` VARCHAR(100) NOT NULL COMMENT '部门名称',
                            `dept_code` VARCHAR(50) COMMENT '部门编码',
                            `leader` VARCHAR(50) COMMENT '部门负责人',
                            `phone` VARCHAR(20) COMMENT '联系电话',
                            `email` VARCHAR(100) COMMENT '邮箱',
                            `order_num` INT DEFAULT 0 COMMENT '显示顺序',
                            `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
                            `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                            PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB COMMENT='部门表';

-- 用户表
CREATE TABLE `sys_user` (
                            `user_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                            `dept_id` BIGINT COMMENT '部门ID',
                            `username` VARCHAR(50) NOT NULL COMMENT '用户名',
                            `password` VARCHAR(100) NOT NULL COMMENT '密码',
                            `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
                            `user_type` VARCHAR(2) DEFAULT '00' COMMENT '用户类型（00系统用户 01教师）',
                            `email` VARCHAR(100) DEFAULT '' COMMENT '邮箱',
                            `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号码',
                            `gender` CHAR(1) DEFAULT '0' COMMENT '性别（0男 1女 2未知）',
                            `avatar` VARCHAR(200) DEFAULT '' COMMENT '头像',
                            `title` VARCHAR(50) COMMENT '职称',
                            `research_direction` VARCHAR(200) COMMENT '研究方向',
                            `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
                            `last_login_ip` VARCHAR(128) DEFAULT '' COMMENT '最后登录IP',
                            `last_login_time` DATETIME COMMENT '最后登录时间',
                            `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                            PRIMARY KEY (`user_id`),
                            UNIQUE KEY `idx_username` (`username`),
                            KEY `idx_dept_id` (`dept_id`),
                            CONSTRAINT `fk_user_dept` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`)
) ENGINE=InnoDB COMMENT='用户表';

-- 角色表
CREATE TABLE `sys_role` (
                            `role_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                            `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
                            `role_key` VARCHAR(100) NOT NULL COMMENT '角色权限字符串',
                            `role_sort` INT NOT NULL COMMENT '显示顺序',
                            `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
                            `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                            PRIMARY KEY (`role_id`),
                            UNIQUE KEY `idx_role_key` (`role_key`)
) ENGINE=InnoDB COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE `sys_user_role` (
                                 `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                 `role_id` BIGINT NOT NULL COMMENT '角色ID',
                                 PRIMARY KEY (`user_id`, `role_id`),
                                 KEY `idx_role_id` (`role_id`),
                                 CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`),
                                 CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`)
) ENGINE=InnoDB COMMENT='用户和角色关联表';

-- 菜单表
CREATE TABLE `sys_menu` (
                            `menu_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
                            `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
                            `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单ID',
                            `path` VARCHAR(200) DEFAULT '' COMMENT '路由地址',
                            `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
                            `perms` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
                            `icon` VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
                            `menu_type` CHAR(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
                            `order_num` INT DEFAULT 0 COMMENT '显示顺序',
                            `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
                            `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `remark` VARCHAR(500) DEFAULT '' COMMENT '备注',
                            PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB COMMENT='菜单权限表';

-- 角色菜单关联表
CREATE TABLE `sys_role_menu` (
                                 `role_id` BIGINT NOT NULL COMMENT '角色ID',
                                 `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
                                 PRIMARY KEY (`role_id`, `menu_id`),
                                 KEY `idx_menu_id` (`menu_id`),
                                 CONSTRAINT `fk_role_menu_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`),
                                 CONSTRAINT `fk_role_menu_menu` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`menu_id`)
) ENGINE=InnoDB COMMENT='角色和菜单关联表';

-- 操作日志表
CREATE TABLE `sys_operation_log` (
                                     `log_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志主键',
                                     `title` VARCHAR(100) DEFAULT '' COMMENT '模块标题',
                                     `business_type` VARCHAR(50) DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
                                     `method` VARCHAR(100) DEFAULT '' COMMENT '方法名称',
                                     `request_method` VARCHAR(10) DEFAULT '' COMMENT '请求方式',
                                     `operator_type` VARCHAR(50) DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
                                     `user_id` BIGINT COMMENT '操作人员ID',
                                     `real_name` VARCHAR(50) DEFAULT '' COMMENT '操作人员',
                                     `dept_name` VARCHAR(100) DEFAULT '' COMMENT '部门名称',
                                     `request_url` VARCHAR(500) DEFAULT '' COMMENT '请求URL',
                                     `request_ip` VARCHAR(128) DEFAULT '' COMMENT '主机地址',
                                     `request_location` VARCHAR(255) DEFAULT '' COMMENT '操作地点',
                                     `request_param` TEXT COMMENT '请求参数',
                                     `response_result` TEXT COMMENT '返回参数',
                                     `status` CHAR(1) DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
                                     `error_msg` TEXT COMMENT '错误消息',
                                     `operation_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                                     PRIMARY KEY (`log_id`),
                                     KEY `idx_user_id` (`user_id`),
                                     KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB COMMENT='操作日志记录';

-- 项目类型字典表
CREATE TABLE `research_project_type` (
                                         `type_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '类型ID',
                                         `type_name` VARCHAR(100) NOT NULL COMMENT '类型名称',
                                         `type_code` VARCHAR(50) NOT NULL COMMENT '类型编码',
                                         `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                         `order_num` INT DEFAULT 0 COMMENT '显示顺序',
                                         `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                                         `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                                         `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                                         PRIMARY KEY (`type_id`),
                                         UNIQUE KEY `idx_type_code` (`type_code`),
                                         UNIQUE KEY `idx_type_name` (`type_name`)
) ENGINE=InnoDB COMMENT='项目类型字典表';

-- 科研项目表
CREATE TABLE `research_project` (
                                    `project_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
                                    `project_no` VARCHAR(100) NOT NULL COMMENT '项目编号',
                                    `project_name` VARCHAR(500) NOT NULL COMMENT '项目名称',
                                    `project_type_id` BIGINT NOT NULL COMMENT '项目类型ID',
                                    `leader_id` BIGINT NOT NULL COMMENT '项目负责人ID',
                                    `dept_id` BIGINT COMMENT '负责部门ID',
                                    `source_unit` VARCHAR(200) COMMENT '项目来源单位',
                                    `project_level` VARCHAR(50) COMMENT '项目级别（国家级、省部级等）',
                                    `total_budget` DECIMAL(15,2) DEFAULT 0.00 COMMENT '总经费（万元）',
                                    `approved_amount` DECIMAL(15,2) DEFAULT 0.00 COMMENT '批准经费（万元）',
                                    `start_date` DATE COMMENT '开始日期',
                                    `end_date` DATE COMMENT '结束日期',
                                    `duration` INT COMMENT '研究期限（月）',
                                    `project_status` VARCHAR(50) DEFAULT 'draft' COMMENT '项目状态（draft:草稿 submitted:已提交 approved:已立项 in_progress:进行中 mid_check:中期检查 completed:已完成 closed:已结题 cancelled:已取消 rejected:已驳回）',
                                    `keywords` VARCHAR(500) COMMENT '关键词',
                                    `research_content` TEXT COMMENT '研究内容',
                                    `expected_result` TEXT COMMENT '预期成果',
                                    `current_progress` TEXT COMMENT '当前进展',
                                    `approval_doc_no` VARCHAR(100) COMMENT '批准文号',
                                    `approval_date` DATE COMMENT '批准日期',
                                    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                                    PRIMARY KEY (`project_id`),
                                    UNIQUE KEY `idx_project_no` (`project_no`),
                                    KEY `idx_leader_id` (`leader_id`),
                                    KEY `idx_dept_id` (`dept_id`),
                                    KEY `idx_project_type` (`project_type_id`),
                                    KEY `idx_project_status` (`project_status`),
                                    CONSTRAINT `fk_project_leader` FOREIGN KEY (`leader_id`) REFERENCES `sys_user` (`user_id`),
                                    CONSTRAINT `fk_project_dept` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`),
                                    CONSTRAINT `fk_project_type` FOREIGN KEY (`project_type_id`) REFERENCES `research_project_type` (`type_id`)
) ENGINE=InnoDB COMMENT='科研项目表';

-- 项目审批记录表
CREATE TABLE `research_project_approval` (
                                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                             `project_id` BIGINT NOT NULL COMMENT '项目ID',
                                             `decision` VARCHAR(20) NOT NULL COMMENT '审批结果(approve/reject)',
                                             `comment` VARCHAR(1000) NOT NULL COMMENT '审批意见',
                                             `approver_id` BIGINT NOT NULL COMMENT '审批人ID',
                                             `approver_name` VARCHAR(100) DEFAULT '' COMMENT '审批人名称',
                                             `final_flag` TINYINT DEFAULT 0 COMMENT '是否终审(1是 0否)',
                                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             PRIMARY KEY (`id`),
                                             KEY `idx_project_id` (`project_id`),
                                             CONSTRAINT `fk_project_approval` FOREIGN KEY (`project_id`) REFERENCES `research_project` (`project_id`)
) ENGINE=InnoDB COMMENT='项目审批记录表';

-- 项目中检记录表
CREATE TABLE `research_project_check` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `project_id` BIGINT NOT NULL COMMENT '项目ID',
                                          `check_title` VARCHAR(200) NOT NULL COMMENT '中检标题',
                                          `check_content` TEXT COMMENT '中检内容',
                                          `attachment` VARCHAR(500) DEFAULT '' COMMENT '附件路径(MinIO)',
                                          `check_date` DATE COMMENT '中检日期',
                                          `result_status` VARCHAR(20) DEFAULT 'pending' COMMENT '中检结果状态(pending:待审核 passed:通过 rejected:不通过)',
                                          `comment` VARCHAR(1000) DEFAULT '' COMMENT '审核意见',
                                          `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                                          `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`id`),
                                          KEY `idx_project_id` (`project_id`),
                                          KEY `idx_check_date` (`check_date`),
                                          CONSTRAINT `fk_check_project` FOREIGN KEY (`project_id`) REFERENCES `research_project` (`project_id`)
) ENGINE=InnoDB COMMENT='项目中检记录表';

-- 项目结题记录表
CREATE TABLE `research_project_final` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `project_id` BIGINT NOT NULL COMMENT '项目ID',
                                          `final_title` VARCHAR(200) NOT NULL COMMENT '结题标题',
                                          `final_content` TEXT COMMENT '结题内容',
                                          `attachment` VARCHAR(500) DEFAULT '' COMMENT '附件路径(MinIO)',
                                          `submit_date` DATE COMMENT '提交日期',
                                          `accept_status` VARCHAR(20) DEFAULT 'pending' COMMENT '验收状态(pending:待验收 passed:通过 rejected:不通过)',
                                          `comment` VARCHAR(1000) DEFAULT '' COMMENT '验收意见',
                                          `acceptor_id` BIGINT COMMENT '验收人ID',
                                          `acceptor_name` VARCHAR(100) DEFAULT '' COMMENT '验收人名称',
                                          `accept_date` DATE COMMENT '验收日期',
                                          `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                                          `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`id`),
                                          KEY `idx_project_id` (`project_id`),
                                          KEY `idx_submit_date` (`submit_date`),
                                          CONSTRAINT `fk_final_project` FOREIGN KEY (`project_id`) REFERENCES `research_project` (`project_id`)
) ENGINE=InnoDB COMMENT='项目结题记录表';

-- 项目成员表
CREATE TABLE `research_project_member` (
                                           `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                           `project_id` BIGINT NOT NULL COMMENT '项目ID',
                                           `user_id` BIGINT NOT NULL COMMENT '成员用户ID',
                                           `member_role` VARCHAR(50) NOT NULL COMMENT '成员角色（主持人、主要参与人、普通参与人等）',
                                           `workload_ratio` DECIMAL(5,2) DEFAULT 0.00 COMMENT '工作量比例(%)',
                                           `responsibility` VARCHAR(500) COMMENT '承担任务',
                                           `join_date` DATE COMMENT '加入日期',
                                           `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `idx_project_user` (`project_id`, `user_id`),
                                           KEY `idx_user_id` (`user_id`),
                                           CONSTRAINT `fk_member_project` FOREIGN KEY (`project_id`) REFERENCES `research_project` (`project_id`),
                                           CONSTRAINT `fk_member_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`)
) ENGINE=InnoDB COMMENT='项目成员表';

-- 论文成果表
CREATE TABLE `research_paper` (
                                  `paper_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '论文ID',
                                  `paper_title` VARCHAR(500) NOT NULL COMMENT '论文标题',
                                  `authors` VARCHAR(1000) NOT NULL COMMENT '作者列表（按顺序，逗号分隔）',
                                  `corresponding_author_id` BIGINT COMMENT '通讯作者ID',
                                  `journal_name` VARCHAR(300) NOT NULL COMMENT '期刊/会议名称',
                                  `publish_date` DATE COMMENT '发表时间',
                                  `volume` VARCHAR(50) COMMENT '卷',
                                  `issue` VARCHAR(50) COMMENT '期',
                                  `page_range` VARCHAR(100) COMMENT '页码范围',
                                  `doi` VARCHAR(200) COMMENT 'DOI号',
                                  `paper_level` VARCHAR(50) COMMENT '论文级别（SCI、EI、核心期刊等）',
                                  `impact_factor` DECIMAL(5,3) COMMENT '影响因子',
                                  `citation_count` INT DEFAULT 0 COMMENT '引用次数',
                                  `keywords` VARCHAR(500) COMMENT '关键词',
                                  `abstract_text` TEXT COMMENT '摘要',
                                  `attachment_path` VARCHAR(500) COMMENT '附件路径',
                                  `project_id` BIGINT COMMENT '关联项目ID',
                                  `status` VARCHAR(50) DEFAULT 'draft' COMMENT '状态（draft:草稿 submitted:已提交 approved:已审核 rejected:已驳回）',
                                  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                                  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                                  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                                  PRIMARY KEY (`paper_id`),
                                  KEY `idx_corresponding_author` (`corresponding_author_id`),
                                  KEY `idx_project_id` (`project_id`),
                                  KEY `idx_publish_date` (`publish_date`),
                                  CONSTRAINT `fk_paper_author` FOREIGN KEY (`corresponding_author_id`) REFERENCES `sys_user` (`user_id`),
                                  CONSTRAINT `fk_paper_project` FOREIGN KEY (`project_id`) REFERENCES `research_project` (`project_id`)
) ENGINE=InnoDB COMMENT='论文成果表';

-- 专利成果表
CREATE TABLE `research_patent` (
                                   `patent_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '专利ID',
                                   `patent_name` VARCHAR(500) NOT NULL COMMENT '专利名称',
                                   `inventors` VARCHAR(1000) NOT NULL COMMENT '发明人列表',
                                   `patent_type` VARCHAR(50) COMMENT '专利类型（发明专利、实用新型等）',
                                   `patent_no` VARCHAR(100) COMMENT '专利号',
                                   `application_date` DATE COMMENT '申请日期',
                                   `authorization_date` DATE COMMENT '授权日期',
                                   `valid_until` DATE COMMENT '有效期至',
                                   `patent_status` VARCHAR(50) COMMENT '专利状态（申请中、已授权、已转化等）',
                                   `attachment_path` VARCHAR(500) COMMENT '附件路径',
                                   `project_id` BIGINT COMMENT '关联项目ID',
                                   `status` VARCHAR(50) DEFAULT 'draft' COMMENT '状态（draft:草稿 submitted:已提交 approved:已审核）',
                                   `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                                   `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                                   `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                                   PRIMARY KEY (`patent_id`),
                                   UNIQUE KEY `idx_patent_no` (`patent_no`),
                                   KEY `idx_project_id` (`project_id`),
                                   CONSTRAINT `fk_patent_project` FOREIGN KEY (`project_id`) REFERENCES `research_project` (`project_id`)
) ENGINE=InnoDB COMMENT='专利成果表';

-- 科研经费表
CREATE TABLE `research_funding` (
                                    `funding_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '经费ID',
                                    `project_id` BIGINT NOT NULL COMMENT '项目ID',
                                    `funding_type` VARCHAR(50) NOT NULL COMMENT '经费类型（到账经费、支出经费）',
                                    `amount` DECIMAL(15,2) NOT NULL COMMENT '金额（万元）',
                                    `transaction_date` DATE NOT NULL COMMENT '交易日期',
                                    `description` VARCHAR(500) COMMENT '描述',
                                    `category` VARCHAR(100) COMMENT '经费类别（设备费、材料费、差旅费等）',
                                    `payer_payee` VARCHAR(200) COMMENT '付款方/收款方',
                                    `voucher_no` VARCHAR(100) COMMENT '凭证号',
                                    `attachment_path` VARCHAR(500) COMMENT '附件路径',
                                    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                                    PRIMARY KEY (`funding_id`),
                                    KEY `idx_project_id` (`project_id`),
                                    KEY `idx_funding_type` (`funding_type`),
                                    KEY `idx_transaction_date` (`transaction_date`),
                                    CONSTRAINT `fk_funding_project` FOREIGN KEY (`project_id`) REFERENCES `research_project` (`project_id`)
) ENGINE=InnoDB COMMENT='科研经费表';

-- 学术活动表
CREATE TABLE `academic_activity` (
                                     `activity_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动ID',
                                     `activity_name` VARCHAR(500) NOT NULL COMMENT '活动名称',
                                     `activity_type` VARCHAR(50) NOT NULL COMMENT '活动类型（学术会议、学术报告等）',
                                     `organizer` VARCHAR(200) COMMENT '主办单位',
                                     `location` VARCHAR(200) COMMENT '地点',
                                     `start_time` DATETIME COMMENT '开始时间',
                                     `end_time` DATETIME COMMENT '结束时间',
                                     `participant_id` BIGINT COMMENT '参与人ID',
                                     `participant_role` VARCHAR(100) COMMENT '参与角色（主讲人、参会人等）',
                                     `report_title` VARCHAR(500) COMMENT '报告题目',
                                     `attachment_path` VARCHAR(500) COMMENT '附件路径',
                                     `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                                     `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                                     `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                                     PRIMARY KEY (`activity_id`),
                                     KEY `idx_participant_id` (`participant_id`),
                                     KEY `idx_activity_type` (`activity_type`),
                                     KEY `idx_start_time` (`start_time`),
                                     CONSTRAINT `fk_activity_participant` FOREIGN KEY (`participant_id`) REFERENCES `sys_user` (`user_id`)
) ENGINE=InnoDB COMMENT='学术活动表';

-- 项目审批流程表
CREATE TABLE `research_approval` (
                                     `approval_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '审批ID',
                                     `project_id` BIGINT NOT NULL COMMENT '项目ID',
                                     `approval_step` VARCHAR(50) NOT NULL COMMENT '审批步骤',
                                     `approver_id` BIGINT NOT NULL COMMENT '审批人ID',
                                     `approval_status` VARCHAR(50) NOT NULL COMMENT '审批状态（pending:待审批 approved:通过 rejected:驳回）',
                                     `approval_opinion` TEXT COMMENT '审批意见',
                                     `approval_time` DATETIME COMMENT '审批时间',
                                     `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     PRIMARY KEY (`approval_id`),
                                     KEY `idx_project_id` (`project_id`),
                                     KEY `idx_approver_id` (`approver_id`),
                                     KEY `idx_approval_status` (`approval_status`),
                                     CONSTRAINT `fk_approval_project` FOREIGN KEY (`project_id`) REFERENCES `research_project` (`project_id`),
                                     CONSTRAINT `fk_approval_approver` FOREIGN KEY (`approver_id`) REFERENCES `sys_user` (`user_id`)
) ENGINE=InnoDB COMMENT='项目审批流程表';

-- 通知公告表
CREATE TABLE `sys_notice` (
                              `notice_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
                              `notice_title` VARCHAR(200) NOT NULL COMMENT '公告标题',
                              `notice_type` VARCHAR(50) NOT NULL COMMENT '公告类型（1通知 2公告）',
                              `notice_content` TEXT COMMENT '公告内容',
                              `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1关闭）',
                              `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                              `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                              PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB COMMENT='通知公告表';
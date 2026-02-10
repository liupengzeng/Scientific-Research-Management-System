-- 测试数据SQL脚本
-- 用于创建测试用户、角色、部门等基础数据

USE research_management;

-- 清空测试数据（可选，谨慎使用）
-- DELETE FROM sys_user_role;
-- DELETE FROM sys_user;
-- DELETE FROM sys_role;
-- DELETE FROM sys_dept;

-- 插入部门数据
INSERT INTO sys_dept (dept_id, parent_id, dept_name, dept_code, leader, phone, email, order_num, status, create_by, remark) VALUES
(1, 0, '计算机学院', 'CS', '张三', '010-12345678', 'cs@university.edu.cn', 1, '0', 'admin', '计算机科学与技术学院'),
(2, 0, '机械工程学院', 'ME', '李四', '010-12345679', 'me@university.edu.cn', 2, '0', 'admin', '机械工程学院'),
(3, 1, '软件工程系', 'CS-SE', '王五', '010-12345680', 'se@university.edu.cn', 1, '0', 'admin', '软件工程系'),
(4, 1, '网络工程系', 'CS-NE', '赵六', '010-12345681', 'ne@university.edu.cn', 2, '0', 'admin', '网络工程系');

-- 插入角色数据
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, status, create_by, remark) VALUES
(1, '教师', 'teacher', 1, '0', 'admin', '教师角色，负责项目申报与维护'),
(2, '教研组长', 'research_group_leader', 2, '0', 'admin', '教研组长，负责一级审批'),
(3, '教研处', 'research_office', 3, '0', 'admin', '教研处，负责二级审批'),
(4, '院校领导', 'college_leader', 4, '0', 'admin', '院校领导，负责终审');

-- 插入用户数据
-- 注意：密码使用BCrypt加密，默认密码为123456
-- BCrypt加密后的密码：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ0O
-- 可以使用在线工具生成：https://bcrypt-generator.com/
-- 或者使用Java代码生成：BCryptPasswordEncoder().encode("123456")
INSERT INTO sys_user (user_id, dept_id, username, password, real_name, user_type, email, phone, gender, avatar, title, research_direction, status, create_by, remark) VALUES
(1, 1, 'teacher01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ0O', '张老师', '01', 'teacher01@university.edu.cn', '13800138001', '0', '', '副教授', '软件工程', '0', 'admin', '教师账号'),
(2, 1, 'groupleader01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ0O', '王组长', '00', 'groupleader01@university.edu.cn', '13800138002', '0', '', '教授', '软件工程管理', '0', 'admin', '教研组长账号'),
(3, 1, 'office01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ0O', '赵处长', '00', 'office01@university.edu.cn', '13800138003', '0', '', '研究员', '科研管理', '0', 'admin', '教研处账号'),
(4, 1, 'leader01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ0O', '钱校长', '00', 'leader01@university.edu.cn', '13800138004', '0', '', '教授', '院校治理', '0', 'admin', '院校领导账号');

-- 插入用户角色关联数据
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1), -- teacher01用户拥有教师角色
(2, 2), -- groupleader01用户拥有教研组长角色
(3, 3), -- office01用户拥有教研处角色
(4, 4); -- leader01用户拥有院校领导角色

-- 插入菜单数据（基础菜单，用于测试）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, path, component, perms, icon, menu_type, order_num, status, create_by, remark) VALUES
(1, '系统管理', 0, '/system', 'Layout', NULL, 'system', 'M', 1, '0', 'admin', '系统管理目录'),
(2, '用户管理', 1, '/system/user', 'system/user/index', 'system:user:list', 'user', 'C', 1, '0', 'admin', '用户管理菜单'),
(3, '部门管理', 1, '/system/dept', 'system/dept/index', 'system:dept:list', 'tree', 'C', 2, '0', 'admin', '部门管理菜单'),
(4, '角色管理', 1, '/system/role', 'system/role/index', 'system:role:list', 'peoples', 'C', 3, '0', 'admin', '角色管理菜单'),
(5, '菜单管理', 1, '/system/menu', 'system/menu/index', 'system:menu:list', 'tree-table', 'C', 4, '0', 'admin', '菜单管理菜单');

-- 插入角色菜单关联数据（四类角色都需要访问基础菜单）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5),
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5),
(4, 1), (4, 2), (4, 3), (4, 4), (4, 5);


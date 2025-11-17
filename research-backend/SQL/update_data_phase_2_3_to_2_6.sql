-- 项目进度数据更新（Phase 2.3 ~ Phase 2.6）
-- 包含：角色/菜单新增权限与操作日志菜单

USE research_management;

-- 防重复插入：如果已存在对应菜单ID，则先删除同ID再插入（根据需要可注释）
DELETE FROM sys_menu WHERE menu_id IN (6,7,8,9,10,11,12,13);

-- 操作日志菜单（目录下功能）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, path, component, perms, icon, menu_type, order_num, status, create_by, remark) VALUES
-- 操作日志主菜单（C）
(6, '操作日志', 1, '/system/operationLog', 'system/operationLog/index', 'system:operationLog:list', 'document', 'C', 5, '0', 'admin', '操作日志列表页面'),
-- 按钮权限（F）
(7, '日志列表', 6, '', NULL, 'system:operationLog:list', '#', 'F', 1, '0', 'admin', '列表权限'),
(8, '日志详情', 6, '', NULL, 'system:operationLog:query', '#', 'F', 2, '0', 'admin', '详情权限'),
(9, '日志删除', 6, '', NULL, 'system:operationLog:remove', '#', 'F', 3, '0', 'admin', '删除权限');

-- 用户管理按钮权限补充（F）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, path, component, perms, icon, menu_type, order_num, status, create_by, remark) VALUES
(10, '用户新增', 2, '', NULL, 'system:user:add', '#', 'F', 11, '0', 'admin', '新增用户权限'),
(11, '用户修改', 2, '', NULL, 'system:user:edit', '#', 'F', 12, '0', 'admin', '修改用户权限'),
(12, '用户删除', 2, '', NULL, 'system:user:remove', '#', 'F', 13, '0', 'admin', '删除用户权限'),
(13, '重置密码', 2, '', NULL, 'system:user:resetPwd', '#', 'F', 14, '0', 'admin', '重置密码权限');

-- 角色菜单关联（为超级管理员授权新菜单与按钮权限）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 6), (1, 7), (1, 8), (1, 9),
(1, 10), (1, 11), (1, 12), (1, 13);

-- 可选：为普通管理员开放查看日志列表与详情（按需）
-- INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 6), (2, 7), (2, 8);

-- 验证查询（可在客户端执行）
-- SELECT m.menu_id, m.menu_name, m.perms FROM sys_menu m WHERE m.menu_id IN (6,7,8,9,10,11,12,13);
-- SELECT * FROM sys_role_menu WHERE role_id=1 AND menu_id IN (6,7,8,9,10,11,12,13);
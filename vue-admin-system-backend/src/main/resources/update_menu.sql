-- 更新菜单数据，添加仪表盘菜单
USE admin_system;

-- 清空原有菜单
DELETE FROM sys_role_menu;
DELETE FROM sys_menu;

-- 插入新菜单
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, icon, perms, sort, status) VALUES
(1, 0, '仪表盘', '/dashboard', 'Layout', 'odometer', NULL, 0, 1),
(2, 1, '仪表盘', 'dashboard', 'dashboard/index', 'odometer', NULL, 0, 1),
(3, 0, '系统管理', '/system', 'Layout', 'setting', NULL, 1, 1),
(4, 3, '用户管理', 'user', 'system/user/index', 'user', 'system:user:view', 1, 1),
(5, 3, '角色管理', 'role', 'system/role/index', 'user-filled', 'system:role:view', 2, 1),
(6, 3, '菜单管理', 'menu', 'system/menu/index', 'menu', 'system:menu:view', 3, 1),
(7, 3, '商品管理', 'product', 'system/product/index', 'goods', 'system:product:view', 4, 1),
(8, 3, '供应商管理', 'provider', 'system/provider/index', 'office-building', 'system:provider:view', 5, 1),
(9, 3, '合同管理', 'contract', 'system/contract/index', 'document', 'system:contract:view', 6, 1),
(10, 3, '许可证管理', 'license', 'system/license/index', 'tickets', 'system:license:view', 7, 1),
(11, 0, '日志管理', '/log', 'Layout', 'document', NULL, 2, 1),
(12, 11, '操作日志', 'oper', 'system/log/index', 'document', 'system:log:view', 1, 1);

-- 关联管理员所有菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12);

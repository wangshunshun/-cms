-- 添加测试用户
USE admin_system;

-- 清除现有的用户角色关联
DELETE FROM sys_user_role WHERE user_id > 1;

-- 添加editor用户 (密码: password)
INSERT INTO sys_user (username, password, nickname, email, phone, status, create_time) VALUES
('editor', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '编辑员', 'editor@example.com', '13800138001', 1, NOW());

-- 为editor用户分配角色 (角色ID 2 = 普通用户/编辑员)
INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 2);

-- 更新角色权限关联
DELETE FROM sys_role_menu;

-- admin角色(1) - 所有菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8);

-- editor角色(2) - 只有查看权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, 1), (2, 2), (2, 3), (2, 6), (2, 7), (2, 8);

-- 查看结果
SELECT u.id, u.username, u.nickname, r.role_name
FROM sys_user u
LEFT JOIN sys_user_role ur ON u.id = ur.user_id
LEFT JOIN sys_role r ON ur.role_id = r.id;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS admin_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE admin_system;

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态 0停用 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL COMMENT '角色权限字符串',
    status TINYINT DEFAULT 1 COMMENT '状态 0停用 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 菜单表
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID 0为根菜单',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    path VARCHAR(200) COMMENT '路由地址',
    component VARCHAR(200) COMMENT '组件路径',
    icon VARCHAR(100) COMMENT '菜单图标',
    perms VARCHAR(100) COMMENT '权限标识',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0停用 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色菜单关联表
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 操作日志表
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '用户ID',
    oper_name VARCHAR(50) COMMENT '操作人',
    oper_url VARCHAR(255) COMMENT '请求URL',
    oper_method VARCHAR(10) COMMENT '请求方式',
    oper_ip VARCHAR(50) COMMENT '操作IP',
    oper_params TEXT COMMENT '请求参数',
    oper_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    oper_result TINYINT DEFAULT 0 COMMENT '操作结果 0成功 1失败'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 商品表
DROP TABLE IF EXISTS product;
CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    description VARCHAR(500) COMMENT '商品描述',
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品价格',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0下架 1上架',
    image VARCHAR(255) DEFAULT NULL COMMENT '商品图片',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 供应商表
DROP TABLE IF EXISTS provider;
CREATE TABLE provider (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider_name VARCHAR(120) NOT NULL COMMENT '供应商名称',
    country VARCHAR(80) COMMENT '国家',
    notes VARCHAR(1000) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 合同表
DROP TABLE IF EXISTS `contract`;
CREATE TABLE `contract` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_name VARCHAR(160) NOT NULL COMMENT '合同名称',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    provider_id BIGINT NOT NULL COMMENT '所属供应商ID',
    commercial_rights TEXT COMMENT '商业权益(JSON)',
    notes VARCHAR(1000) COMMENT '备注',
    attachment_name VARCHAR(255) COMMENT '附件名',
    attachment_url VARCHAR(255) COMMENT '附件地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同表';

-- 许可证表
DROP TABLE IF EXISTS `license`;
CREATE TABLE `license` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_name VARCHAR(160) NOT NULL COMMENT '许可证名称',
    contract_id BIGINT NOT NULL COMMENT '所属合同ID',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    service_type VARCHAR(50) COMMENT '服务类型',
    regions TEXT COMMENT '区域(JSON)',
    platforms TEXT COMMENT '平台(JSON)',
    advertorial_rights TEXT COMMENT '广告权益(JSON)',
    downloadable_for_mobile TINYINT DEFAULT 0 COMMENT '移动端可下载',
    download_duration INT COMMENT '下载有效天数',
    preview_for_mobile TINYINT DEFAULT 0 COMMENT '移动端可预览',
    preview_begin_time VARCHAR(10) COMMENT '预览开始时间',
    preview_end_time VARCHAR(10) COMMENT '预览结束时间',
    notes VARCHAR(1000) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='许可证表';

-- 业务修改记录表
DROP TABLE IF EXISTS biz_change_log;
CREATE TABLE biz_change_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    biz_type VARCHAR(50) NOT NULL COMMENT '业务类型',
    biz_id BIGINT NOT NULL COMMENT '业务ID',
    action VARCHAR(50) COMMENT '操作类型',
    content VARCHAR(500) COMMENT '操作内容',
    operator VARCHAR(50) COMMENT '操作人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务修改记录表';

-- 初始化数据

-- 插入默认管理员用户 admin/123456 (BCrypt加密后的密码)
INSERT INTO sys_user (username, password, nickname, email, phone, avatar, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Administrator', 'admin@example.com', '13800138000', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 1);

-- 插入角色
INSERT INTO sys_role (role_name, role_key, status) VALUES
('管理员', 'admin', 1),
('普通用户', 'user', 1);

-- 插入菜单
INSERT INTO sys_menu (parent_id, menu_name, path, component, icon, perms, sort, status) VALUES
(0, '系统管理', '/system', 'Layout', 'setting', NULL, 1, 1),
(1, '用户管理', '/system/user', 'system/user/index', 'user', 'system:user:view', 1, 1),
(1, '角色管理', '/system/role', 'system/role/index', 'user-filled', 'system:role:view', 2, 1),
(1, '菜单管理', '/system/menu', 'system/menu/index', 'menu', 'system:menu:view', 3, 1),
(1, '商品管理', '/system/product', 'system/product/index', 'goods', 'system:product:view', 4, 1),
(1, '供应商管理', '/system/provider', 'system/provider/index', 'office-building', 'system:provider:view', 5, 1),
(1, '合同管理', '/system/contract', 'system/contract/index', 'document', 'system:contract:view', 6, 1),
(1, '许可证管理', '/system/license', 'system/license/index', 'tickets', 'system:license:view', 7, 1),
(0, '日志管理', '/log', 'Layout', 'document', NULL, 2, 1),
(9, '操作日志', '/log/oper', 'log/oper/index', 'document', 'system:log:view', 1, 1);

-- 关联管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 关联管理员所有菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10);

-- 商品初始化数据
INSERT INTO product (name, description, price, stock, category_id, status, image) VALUES
('无线蓝牙耳机', '主动降噪，40小时续航', 399.00, 120, NULL, 1, NULL),
('机械键盘', '87键，热插拔，RGB背光', 299.00, 80, NULL, 1, NULL),
('便携显示器', '15.6英寸，1080P，Type-C供电', 899.00, 35, NULL, 0, NULL);

-- 供应商初始化数据
INSERT INTO provider (provider_name, country, notes) VALUES
('Global Media Group', 'USA', '北美区域核心内容供应商'),
('Asia Vision Studio', 'Singapore', '覆盖东南亚内容版权');

-- 合同初始化数据
INSERT INTO `contract` (contract_name, start_date, end_date, provider_id, commercial_rights, notes, attachment_name, attachment_url) VALUES
('GMG-2026-Main', '2026-01-01', '2026-12-31', 1, '{"IPTV":true,"Mobile":true,"SmartTV":false,"Web":true}', '包含体育与综艺内容', 'GMG-main.pdf', '/mock-files/GMG-main.pdf'),
('AVS-2026-Regional', '2026-03-01', '2027-02-28', 2, '{"IPTV":true,"Mobile":false,"SmartTV":true,"Web":true}', '区域独家发行', '', '');

-- 许可证初始化数据
INSERT INTO `license` (license_name, contract_id, start_date, end_date, service_type, regions, platforms, advertorial_rights, downloadable_for_mobile, download_duration, preview_for_mobile, preview_begin_time, preview_end_time, notes) VALUES
('GMG Sports Pack', 1, '2026-01-15', '2026-10-15', 'SVOD', '["US","CA"]', '["IPTV","Mobile"]', '{"IPTV":true,"Mobile":false}', 1, 7, 1, '08:00', '22:00', '移动端限部分片源下载'),
('AVS Drama Basic', 2, '2026-04-01', '2026-12-31', 'TVOD', '["SG","MY"]', '["SmartTV","Web"]', '{"SmartTV":true,"Web":true}', 0, NULL, 0, '', '', '');

-- 业务变更记录初始化
INSERT INTO biz_change_log (biz_type, biz_id, action, content, operator) VALUES
('provider', 1, '创建', '创建供应商', 'admin'),
('provider', 2, '创建', '创建供应商', 'admin'),
('contract', 1, '创建', '创建合同', 'admin'),
('contract', 2, '创建', '创建合同', 'admin'),
('license', 1, '创建', '创建许可证', 'admin'),
('license', 2, '创建', '创建许可证', 'admin');

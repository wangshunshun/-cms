package com.admin.service;

import com.admin.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface SysMenuService extends IService<SysMenu> {
    List<Map<String, Object>> getMenuList();
    List<Map<String, Object>> getMenuTree();
    void saveMenu(SysMenu menu);
    void updateMenu(SysMenu menu);
    void deleteMenu(Long id);

    /**
     * 根据用户获取路由菜单（前端动态路由格式）
     */
    List<Map<String, Object>> getRoutesByUser(String username);
}

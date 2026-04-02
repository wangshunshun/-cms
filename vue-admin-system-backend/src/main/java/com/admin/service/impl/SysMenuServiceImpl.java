package com.admin.service.impl;

import com.admin.entity.SysMenu;
import com.admin.mapper.SysMenuMapper;
import com.admin.service.SysMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    public SysMenuServiceImpl(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    @Override
    public List<Map<String, Object>> getMenuList() {
        List<SysMenu> menus = sysMenuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSort)
        );

        List<Map<String, Object>> result = new ArrayList<>();
        for (SysMenu menu : menus) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", menu.getId());
            map.put("parentId", menu.getParentId());
            map.put("menuName", menu.getMenuName());
            map.put("path", menu.getPath());
            map.put("component", menu.getComponent());
            map.put("icon", menu.getIcon());
            map.put("perms", menu.getPerms());
            map.put("sort", menu.getSort());
            map.put("status", menu.getStatus());
            map.put("createTime", menu.getCreateTime());
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getMenuTree() {
        List<Map<String, Object>> allMenus = getMenuList();
        return buildMenuTree(allMenus, 0L);
    }

    private List<Map<String, Object>> buildMenuTree(List<Map<String, Object>> menus, Long parentId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> menu : menus) {
            Long pid = (Long) menu.get("parentId");
            if (Objects.equals(pid, parentId)) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = buildMenuTree(menus, (Long) menu.get("id"));
                if (!children.isEmpty()) {
                    menu.put("children", children);
                }
                result.add(menu);
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getRoutesByUser(String username) {
        // 查询用户的所有菜单（通过角色关联）
        List<SysMenu> menus;

        if ("admin".equals(username)) {
            menus = sysMenuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getStatus, 1)
                    .orderByAsc(SysMenu::getSort)
            );
        } else {
            // 非admin用户，可以通过用户角色关联查询
            menus = new ArrayList<>();
        }

        // 转换为前端路由格式
        return convertToRoutes(menus, 0L);
    }

    /**
     * 将菜单转换为前端路由格式
     */
    private List<Map<String, Object>> convertToRoutes(List<SysMenu> menus, Long parentId) {
        List<Map<String, Object>> routes = new ArrayList<>();

        for (SysMenu menu : menus) {
            if (!Objects.equals(menu.getParentId(), parentId)) {
                continue;
            }

            Map<String, Object> route = new LinkedHashMap<>();
            route.put("path", menu.getPath());

            // 处理组件路径
            String component = menu.getComponent();
            if ("Layout".equals(component)) {
                route.put("component", "Layout");
            } else if (component != null && !component.isEmpty()) {
                route.put("component", component);
            }

            // 处理 meta 信息
            Map<String, Object> meta = new HashMap<>();
            meta.put("title", menu.getMenuName());
            if (menu.getIcon() != null && !menu.getIcon().isEmpty()) {
                meta.put("icon", toPascalCase(menu.getIcon()));
            }
            if (menu.getPerms() != null && !menu.getPerms().isEmpty()) {
                meta.put("roles", Arrays.asList("admin"));
            }
            route.put("meta", meta);

            // name
            route.put("name", toCamelCase(menu.getMenuName()));

            // 递归处理子菜单
            List<Map<String, Object>> children = convertToRoutes(menus, menu.getId());
            if (!children.isEmpty()) {
                // 获取第一个子路由路径作为 redirect
                String firstChildPath = (String) children.get(0).get("path");
                route.put("redirect", firstChildPath);
                route.put("children", children);
            }

            routes.add(route);
        }

        return routes;
    }

    /**
     * 转换为 PascalCase（图标名）
     */
    private String toPascalCase(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        String[] parts = str.split("-");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)))
                       .append(part.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    /**
     * 转换为 camelCase（路由名）
     */
    private String toCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        String pascal = toPascalCase(str);
        if (pascal.isEmpty()) {
            return "";
        }
        return Character.toLowerCase(pascal.charAt(0)) + pascal.substring(1);
    }

    @Override
    public void saveMenu(SysMenu menu) {
        menu.setCreateTime(LocalDateTime.now());
        sysMenuMapper.insert(menu);
    }

    @Override
    public void updateMenu(SysMenu menu) {
        sysMenuMapper.updateById(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        sysMenuMapper.deleteById(id);
    }
}

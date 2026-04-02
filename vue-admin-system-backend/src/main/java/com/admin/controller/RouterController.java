package com.admin.controller;

import com.admin.service.SysMenuService;
import com.admin.utils.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RouterController {

    private final SysMenuService sysMenuService;

    public RouterController(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    /**
     * 获取用户路由菜单（前端动态路由）
     */
    @GetMapping("/routes")
    public Result<List<Map<String, Object>>> getRoutes() {
        // 从 SecurityContext 获取当前用户名
        String username = SecurityContextHolder.getContext().getAuthentication() != null
            ? SecurityContextHolder.getContext().getAuthentication().getName()
            : null;

        if (username == null) {
            // 未登录用户返回空路由
            return Result.success(java.util.Collections.emptyList());
        }

        List<Map<String, Object>> routes = sysMenuService.getRoutesByUser(username);
        return Result.success(routes);
    }
}

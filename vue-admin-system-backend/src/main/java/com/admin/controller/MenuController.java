package com.admin.controller;

import com.admin.entity.SysMenu;
import com.admin.service.SysMenuService;
import com.admin.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final SysMenuService sysMenuService;

    public MenuController(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getMenuList() {
        List<Map<String, Object>> list = sysMenuService.getMenuList();
        return Result.success(list);
    }

    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> getMenuTree() {
        List<Map<String, Object>> tree = sysMenuService.getMenuTree();
        return Result.success(tree);
    }

    @PostMapping
    public Result<Void> createMenu(@RequestBody SysMenu menu) {
        sysMenuService.saveMenu(menu);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateMenu(@RequestBody SysMenu menu) {
        sysMenuService.updateMenu(menu);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        sysMenuService.deleteMenu(id);
        return Result.success();
    }
}

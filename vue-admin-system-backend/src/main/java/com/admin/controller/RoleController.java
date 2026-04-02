package com.admin.controller;

import com.admin.dto.RoleDTO;
import com.admin.entity.SysRole;
import com.admin.service.SysRoleService;
import com.admin.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final SysRoleService sysRoleService;

    public RoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> getRoleList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Map<String, Object>> result = sysRoleService.getRolePage(page, pageSize, keyword);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<SysRole>> getAllRoles() {
        List<SysRole> roles = sysRoleService.getAllRoles();
        return Result.success(roles);
    }

    @PostMapping
    public Result<Void> createRole(@RequestBody RoleDTO roleDTO) {
        sysRoleService.saveRole(roleDTO);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateRole(@RequestBody RoleDTO roleDTO) {
        sysRoleService.updateRole(roleDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Result.success();
    }

    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getRolePermissions(@PathVariable Long id) {
        List<Long> permissions = sysRoleService.getRolePermissions(id);
        return Result.success(permissions);
    }

    @PutMapping("/{id}/permissions")
    public Result<Void> updateRolePermissions(
            @PathVariable Long id,
            @RequestBody Map<String, List<Long>> params) {
        List<Long> permissions = params.get("permissions");
        sysRoleService.updateRolePermissions(id, permissions);
        return Result.success();
    }
}

package com.admin.service;

import com.admin.dto.RoleDTO;
import com.admin.entity.SysRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface SysRoleService extends IService<SysRole> {
    Page<Map<String, Object>> getRolePage(Integer page, Integer pageSize, String keyword);
    List<SysRole> getAllRoles();
    void saveRole(RoleDTO roleDTO);
    void updateRole(RoleDTO roleDTO);
    void deleteRole(Long id);
    List<Long> getRolePermissions(Long roleId);
    void updateRolePermissions(Long roleId, List<Long> menuIds);
}

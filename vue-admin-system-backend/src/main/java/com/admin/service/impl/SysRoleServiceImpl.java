package com.admin.service.impl;

import com.admin.dto.RoleDTO;
import com.admin.entity.SysRole;
import com.admin.mapper.SysRoleMapper;
import com.admin.mapper.SysRoleMenuMapper;
import com.admin.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper, SysRoleMenuMapper sysRoleMenuMapper) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysRoleMenuMapper = sysRoleMenuMapper;
    }

    @Override
    public Page<Map<String, Object>> getRolePage(Integer page, Integer pageSize, String keyword) {
        Page<SysRole> rolePage = new Page<>(page, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysRole::getRoleName, keyword)
                    .or().like(SysRole::getRoleKey, keyword);
        }

        Page<SysRole> result = sysRoleMapper.selectPage(rolePage, wrapper);

        Page<Map<String, Object>> mapPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<Map<String, Object>> records = new ArrayList<>();

        for (SysRole role : result.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", role.getId());
            map.put("roleName", role.getRoleName());
            map.put("roleKey", role.getRoleKey());
            map.put("status", role.getStatus());
            map.put("createTime", role.getCreateTime());
            records.add(map);
        }

        mapPage.setRecords(records);
        return mapPage;
    }

    @Override
    public List<SysRole> getAllRoles() {
        return sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, 1)
        );
    }

    @Override
    @Transactional
    public void saveRole(RoleDTO roleDTO) {
        SysRole role = new SysRole();
        role.setRoleName(roleDTO.getRoleName());
        role.setRoleKey(roleDTO.getRoleKey());
        role.setStatus(roleDTO.getStatus());
        role.setCreateTime(LocalDateTime.now());

        sysRoleMapper.insert(role);

        if (roleDTO.getMenuIds() != null && !roleDTO.getMenuIds().isEmpty()) {
            sysRoleMenuMapper.insertBatch(role.getId(), roleDTO.getMenuIds());
        }
    }

    @Override
    @Transactional
    public void updateRole(RoleDTO roleDTO) {
        SysRole role = sysRoleMapper.selectById(roleDTO.getId());
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        role.setRoleName(roleDTO.getRoleName());
        role.setRoleKey(roleDTO.getRoleKey());
        role.setStatus(roleDTO.getStatus());

        sysRoleMapper.updateById(role);

        sysRoleMenuMapper.deleteByRoleId(role.getId());
        if (roleDTO.getMenuIds() != null && !roleDTO.getMenuIds().isEmpty()) {
            sysRoleMenuMapper.insertBatch(role.getId(), roleDTO.getMenuIds());
        }
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        sysRoleMapper.deleteById(id);
        sysRoleMenuMapper.deleteByRoleId(id);
    }

    @Override
    public List<Long> getRolePermissions(Long roleId) {
        return sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional
    public void updateRolePermissions(Long roleId, List<Long> menuIds) {
        sysRoleMenuMapper.deleteByRoleId(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            sysRoleMenuMapper.insertBatch(roleId, menuIds);
        }
    }
}

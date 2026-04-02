package com.admin.service.impl;

import com.admin.dto.LoginDTO;
import com.admin.dto.UserDTO;
import com.admin.entity.SysUser;
import com.admin.mapper.SysUserMapper;
import com.admin.mapper.SysUserRoleMapper;
import com.admin.service.SysUserService;
import com.admin.utils.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public SysUserServiceImpl(SysUserMapper sysUserMapper, SysUserRoleMapper sysUserRoleMapper,
                              JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
    }

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        SysUser user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被停用");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        return result;
    }

    @Override
    public Page<Map<String, Object>> getUserPage(Integer page, Integer pageSize, String keyword) {
        Page<SysUser> userPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getNickname, keyword)
                    .or().like(SysUser::getEmail, keyword);
        }

        Page<SysUser> result = sysUserMapper.selectPage(userPage, wrapper);

        Page<Map<String, Object>> mapPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<Map<String, Object>> records = new ArrayList<>();

        for (SysUser user : result.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("username", user.getUsername());
            map.put("nickname", user.getNickname());
            map.put("email", user.getEmail());
            map.put("phone", user.getPhone());
            map.put("avatar", user.getAvatar());
            map.put("status", user.getStatus());
            map.put("createTime", user.getCreateTime());

            List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(user.getId());
            map.put("roleIds", roleIds);

            records.add(map);
        }

        mapPage.setRecords(records);
        return mapPage;
    }

    @Override
    @Transactional
    public void saveUser(UserDTO userDTO) {
        SysUser user = new SysUser();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setNickname(userDTO.getNickname());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAvatar(userDTO.getAvatar());
        user.setStatus(userDTO.getStatus());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        sysUserMapper.insert(user);

        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            sysUserRoleMapper.insertBatch(user.getId(), userDTO.getRoleIds());
        }
    }

    @Override
    @Transactional
    public void updateUser(UserDTO userDTO) {
        SysUser user = sysUserMapper.selectById(userDTO.getId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setNickname(userDTO.getNickname());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAvatar(userDTO.getAvatar());
        user.setStatus(userDTO.getStatus());
        user.setUpdateTime(LocalDateTime.now());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        sysUserMapper.updateById(user);

        sysUserRoleMapper.deleteByUserId(user.getId());
        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            sysUserRoleMapper.insertBatch(user.getId(), userDTO.getRoleIds());
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        sysUserMapper.deleteById(id);
        sysUserRoleMapper.deleteByUserId(id);
    }

    @Override
    public Map<String, Object> getUserInfo(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("nickname", user.getNickname());
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        map.put("avatar", user.getAvatar());
        map.put("status", user.getStatus());

        // 获取角色ID列表
        List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(user.getId());

        // 根据角色ID返回角色名称列表和权限列表
        List<String> roleNames = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                if (roleId == 1L) {
                    // admin 角色 - 所有权限
                    roleNames.add("admin");
                    permissions.add("*");
                } else if (roleId == 2L) {
                    // editor 角色 - 部分权限
                    roleNames.add("editor");
                    permissions.add("system:user:view");
                    permissions.add("system:role:view");
                    permissions.add("system:menu:view");
                    permissions.add("system:log:view");
                } else {
                    // 普通用户 - 只读权限
                    roleNames.add("user");
                    permissions.add("system:user:view");
                }
            }
        }

        // 默认给admin角色
        if (roleNames.isEmpty()) {
            roleNames.add("admin");
            permissions.add("*");
        }

        // 去重
        permissions = new ArrayList<>(new LinkedHashSet<>(permissions));

        map.put("roles", roleNames);
        map.put("permissions", permissions);

        return map;
    }
}

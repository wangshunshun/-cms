package com.admin.service;

import com.admin.dto.LoginDTO;
import com.admin.dto.UserDTO;
import com.admin.entity.SysUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface SysUserService extends IService<SysUser> {
    SysUser getByUsername(String username);
    Map<String, Object> login(LoginDTO loginDTO);
    Page<Map<String, Object>> getUserPage(Integer page, Integer pageSize, String keyword);
    void saveUser(UserDTO userDTO);
    void updateUser(UserDTO userDTO);
    void deleteUser(Long id);
    Map<String, Object> getUserInfo(Long id);
}

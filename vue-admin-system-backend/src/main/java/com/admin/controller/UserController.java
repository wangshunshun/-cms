package com.admin.controller;

import com.admin.dto.UserDTO;
import com.admin.entity.SysUser;
import com.admin.service.SysUserService;
import com.admin.utils.JwtUtil;
import com.admin.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;

    public UserController(SysUserService sysUserService, JwtUtil jwtUtil) {
        this.sysUserService = sysUserService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || !token.startsWith("Bearer ") || token.length() <= 7) {
                return Result.error(401, "未登录或登录已过期");
            }
            String username = jwtUtil.getUsernameFromToken(token.substring(7));
            SysUser user = sysUserService.getByUsername(username);
            if (user != null) {
                Map<String, Object> userInfo = sysUserService.getUserInfo(user.getId());
                return Result.success(userInfo);
            }
            return Result.error("用户不存在");
        } catch (Exception ex) {
            return Result.error(401, "登录状态无效，请重新登录");
        }
    }

    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Map<String, Object>> result = sysUserService.getUserPage(page, pageSize, keyword);
        return Result.success(result);
    }

    @PostMapping
    public Result<Void> createUser(@RequestBody UserDTO userDTO) {
        sysUserService.saveUser(userDTO);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateUser(@RequestBody UserDTO userDTO) {
        sysUserService.updateUser(userDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> params) {
        return Result.success();
    }
}

package com.admin.controller;

import com.admin.dto.LoginDTO;
import com.admin.entity.SysUser;
import com.admin.service.SysUserService;
import com.admin.utils.JwtUtil;
import com.admin.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;

    public AuthController(SysUserService sysUserService, JwtUtil jwtUtil) {
        this.sysUserService = sysUserService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        try {
            Map<String, Object> data = sysUserService.login(loginDTO);
            return Result.success(data);
        } catch (RuntimeException ex) {
            return Result.error(401, ex.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}

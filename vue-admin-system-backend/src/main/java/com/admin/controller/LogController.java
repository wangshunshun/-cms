package com.admin.controller;

import com.admin.service.SysOperLogService;
import com.admin.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/log")
public class LogController {

    private final SysOperLogService sysOperLogService;

    public LogController(SysOperLogService sysOperLogService) {
        this.sysOperLogService = sysOperLogService;
    }

    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> getLogList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Map<String, Object>> result = sysOperLogService.getLogPage(page, pageSize, keyword);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getLogDetail(@PathVariable Long id) {
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteLog(@PathVariable Long id) {
        return Result.success();
    }

    @DeleteMapping("/clear")
    public Result<Void> clearLogs() {
        return Result.success();
    }

    @GetMapping("/export")
    public Result<Void> exportLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success();
    }
}

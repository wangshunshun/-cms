package com.admin.service;

import com.admin.entity.SysOperLog;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface SysOperLogService extends IService<SysOperLog> {
    Page<Map<String, Object>> getLogPage(Integer page, Integer pageSize, String keyword);
    void saveLog(SysOperLog log);
}

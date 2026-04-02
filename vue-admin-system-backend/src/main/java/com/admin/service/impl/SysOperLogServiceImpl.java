package com.admin.service.impl;

import com.admin.entity.SysOperLog;
import com.admin.mapper.SysOperLogMapper;
import com.admin.service.SysOperLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {

    private final SysOperLogMapper sysOperLogMapper;

    public SysOperLogServiceImpl(SysOperLogMapper sysOperLogMapper) {
        this.sysOperLogMapper = sysOperLogMapper;
    }

    @Override
    public Page<Map<String, Object>> getLogPage(Integer page, Integer pageSize, String keyword) {
        Page<SysOperLog> logPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysOperLog::getOperTime);

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysOperLog::getOperName, keyword)
                    .or().like(SysOperLog::getOperUrl, keyword);
        }

        Page<SysOperLog> result = sysOperLogMapper.selectPage(logPage, wrapper);

        Page<Map<String, Object>> mapPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<Map<String, Object>> records = new ArrayList<>();

        for (SysOperLog log : result.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", log.getId());
            map.put("userId", log.getUserId());
            map.put("operName", log.getOperName());
            map.put("operUrl", log.getOperUrl());
            map.put("operMethod", log.getOperMethod());
            map.put("operIp", log.getOperIp());
            map.put("operTime", log.getOperTime());
            map.put("operResult", log.getOperResult());
            records.add(map);
        }

        mapPage.setRecords(records);
        return mapPage;
    }

    @Override
    public void saveLog(SysOperLog log) {
        sysOperLogMapper.insert(log);
    }
}

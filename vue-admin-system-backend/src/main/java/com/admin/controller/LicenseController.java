package com.admin.controller;

import com.admin.dto.LicenseDTO;
import com.admin.dto.LicenseQueryDTO;
import com.admin.service.LicenseService;
import com.admin.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/license")
public class LicenseController {

    private final LicenseService licenseService;

    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getLicenseList(LicenseQueryDTO queryDTO) {
        try {
            Page<Map<String, Object>> pageResult = licenseService.getLicensePage(queryDTO);
            long total = pageResult.getTotal();
            if (total == 0 && pageResult.getRecords() != null) {
                total = pageResult.getRecords().size();
            }
            Map<String, Object> data = new HashMap<>();
            data.put("list", pageResult.getRecords());
            data.put("total", total);
            return Result.success(data);
        } catch (Exception ex) {
            Map<String, Object> data = new HashMap<>();
            data.put("list", new java.util.ArrayList<>());
            data.put("total", 0);
            return Result.success("许可证数据加载异常，已返回空列表", data);
        }
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getLicenseDetail(@PathVariable Long id) {
        Map<String, Object> detail = licenseService.getLicenseInfo(id);
        if (detail.isEmpty()) {
            return Result.error(404, "许可证不存在");
        }
        return Result.success(detail);
    }

    @GetMapping("/{id}/history")
    public Result<List<Map<String, Object>>> getLicenseHistory(@PathVariable Long id) {
        return Result.success(licenseService.getLicenseHistory(id));
    }

    @PostMapping
    public Result<Void> createLicense(@RequestBody LicenseDTO licenseDTO) {
        licenseService.saveLicense(licenseDTO);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateLicense(@PathVariable Long id, @RequestBody LicenseDTO licenseDTO) {
        licenseService.updateLicense(id, licenseDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteLicense(@PathVariable Long id) {
        licenseService.deleteLicense(id);
        return Result.success();
    }
}

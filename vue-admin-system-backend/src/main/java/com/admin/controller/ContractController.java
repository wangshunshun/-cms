package com.admin.controller;

import com.admin.dto.ContractDTO;
import com.admin.dto.ContractQueryDTO;
import com.admin.entity.License;
import com.admin.mapper.LicenseMapper;
import com.admin.service.ContractService;
import com.admin.utils.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contract")
public class ContractController {

    private final ContractService contractService;
    private final LicenseMapper licenseMapper;

    public ContractController(ContractService contractService, LicenseMapper licenseMapper) {
        this.contractService = contractService;
        this.licenseMapper = licenseMapper;
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getContractList(ContractQueryDTO queryDTO) {
        try {
            Page<Map<String, Object>> pageResult = contractService.getContractPage(queryDTO);
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
            return Result.success("合同数据加载异常，已返回空列表", data);
        }
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getContractDetail(@PathVariable Long id) {
        Map<String, Object> detail = contractService.getContractInfo(id);
        if (detail.isEmpty()) {
            return Result.error(404, "合同不存在");
        }
        List<License> licenses = licenseMapper.selectList(
            new LambdaQueryWrapper<License>().eq(License::getContractId, id).orderByDesc(License::getId)
        );
        java.util.List<Map<String, Object>> licenseList = new java.util.ArrayList<>();
        for (License license : licenses) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", license.getId());
            item.put("licenseName", license.getLicenseName());
            item.put("serviceType", license.getServiceType());
            item.put("startDate", license.getStartDate());
            item.put("endDate", license.getEndDate());
            item.put("contractId", license.getContractId());
            licenseList.add(item);
        }
        detail.put("licenses", licenseList);
        return Result.success(detail);
    }

    @GetMapping("/{id}/history")
    public Result<List<Map<String, Object>>> getContractHistory(@PathVariable Long id) {
        return Result.success(contractService.getContractHistory(id));
    }

    @PostMapping
    public Result<Void> createContract(@RequestBody ContractDTO contractDTO) {
        contractService.saveContract(contractDTO);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateContract(@PathVariable Long id, @RequestBody ContractDTO contractDTO) {
        contractService.updateContract(id, contractDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return Result.success();
    }
}

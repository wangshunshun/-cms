package com.admin.controller;

import com.admin.dto.ProviderDTO;
import com.admin.dto.ProviderQueryDTO;
import com.admin.entity.Contract;
import com.admin.mapper.ContractMapper;
import com.admin.service.ProviderService;
import com.admin.utils.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/provider")
public class ProviderController {

    private final ProviderService providerService;
    private final ContractMapper contractMapper;

    public ProviderController(ProviderService providerService, ContractMapper contractMapper) {
        this.providerService = providerService;
        this.contractMapper = contractMapper;
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getProviderList(ProviderQueryDTO queryDTO) {
        Page<Map<String, Object>> pageResult = providerService.getProviderPage(queryDTO);
        long total = pageResult.getTotal();
        if (total == 0 && pageResult.getRecords() != null) {
            total = pageResult.getRecords().size();
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", pageResult.getRecords());
        data.put("total", total);
        return Result.success(data);
    }

    @GetMapping("/all")
    public Result<List<Map<String, Object>>> getAllProviders() {
        return Result.success(providerService.getAllProviders());
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getProviderDetail(@PathVariable Long id) {
        Map<String, Object> detail = providerService.getProviderInfo(id);
        if (detail.isEmpty()) {
            return Result.error(404, "供应商不存在");
        }
        List<Contract> contracts = contractMapper.selectList(
            new LambdaQueryWrapper<Contract>().eq(Contract::getProviderId, id).orderByDesc(Contract::getId)
        );
        java.util.List<Map<String, Object>> contractList = new java.util.ArrayList<>();
        for (Contract contract : contracts) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", contract.getId());
            item.put("contractName", contract.getContractName());
            item.put("startDate", contract.getStartDate());
            item.put("endDate", contract.getEndDate());
            item.put("providerId", contract.getProviderId());
            item.put("providerName", detail.get("providerName"));
            contractList.add(item);
        }
        detail.put("contracts", contractList);
        return Result.success(detail);
    }

    @GetMapping("/{id}/history")
    public Result<List<Map<String, Object>>> getProviderHistory(@PathVariable Long id) {
        return Result.success(providerService.getProviderHistory(id));
    }

    @PostMapping
    public Result<Void> createProvider(@RequestBody ProviderDTO providerDTO) {
        providerService.saveProvider(providerDTO);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateProvider(@PathVariable Long id, @RequestBody ProviderDTO providerDTO) {
        providerService.updateProvider(id, providerDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProvider(@PathVariable Long id) {
        providerService.deleteProvider(id);
        return Result.success();
    }
}

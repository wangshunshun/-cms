package com.admin.service.impl;

import com.admin.dto.ContractDTO;
import com.admin.dto.ContractQueryDTO;
import com.admin.entity.BizChangeLog;
import com.admin.entity.Contract;
import com.admin.entity.License;
import com.admin.entity.Provider;
import com.admin.mapper.BizChangeLogMapper;
import com.admin.mapper.ContractMapper;
import com.admin.mapper.LicenseMapper;
import com.admin.mapper.ProviderMapper;
import com.admin.service.ContractService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements ContractService {

    private final ProviderMapper providerMapper;
    private final LicenseMapper licenseMapper;
    private final BizChangeLogMapper bizChangeLogMapper;

    public ContractServiceImpl(ProviderMapper providerMapper, LicenseMapper licenseMapper, BizChangeLogMapper bizChangeLogMapper) {
        this.providerMapper = providerMapper;
        this.licenseMapper = licenseMapper;
        this.bizChangeLogMapper = bizChangeLogMapper;
    }

    @Override
    public Page<Map<String, Object>> getContractPage(ContractQueryDTO queryDTO) {
        Page<Contract> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getContractName() != null && !queryDTO.getContractName().isEmpty()) {
            wrapper.like(Contract::getContractName, queryDTO.getContractName());
        }
        if (queryDTO.getProviderId() != null) {
            wrapper.eq(Contract::getProviderId, queryDTO.getProviderId());
        }
        wrapper.orderByDesc(Contract::getId);
        page(page, wrapper);

        Page<Map<String, Object>> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<Map<String, Object>> records = new ArrayList<>();
        for (Contract contract : page.getRecords()) {
            records.add(toMap(contract));
        }
        result.setRecords(records);
        return result;
    }

    @Override
    public Map<String, Object> getContractInfo(Long id) {
        Contract contract = getById(id);
        return contract == null ? new HashMap<>() : toMap(contract);
    }

    @Override
    public List<Map<String, Object>> getContractHistory(Long id) {
        List<BizChangeLog> logs = bizChangeLogMapper.selectList(
            new LambdaQueryWrapper<BizChangeLog>()
                .eq(BizChangeLog::getBizType, "contract")
                .eq(BizChangeLog::getBizId, id)
                .orderByDesc(BizChangeLog::getId)
        );
        List<Map<String, Object>> result = new ArrayList<>();
        for (BizChangeLog log : logs) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", log.getId());
            item.put("action", log.getAction());
            item.put("content", log.getContent());
            item.put("operator", log.getOperator());
            item.put("time", log.getCreateTime());
            result.add(item);
        }
        return result;
    }

    @Override
    @Transactional
    public void saveContract(ContractDTO contractDTO) {
        Contract contract = new Contract();
        fillEntity(contract, contractDTO);
        save(contract);
        appendLog("contract", contract.getId(), "创建", "创建合同");
    }

    @Override
    @Transactional
    public void updateContract(Long id, ContractDTO contractDTO) {
        Contract contract = getById(id);
        if (contract == null) {
            return;
        }
        fillEntity(contract, contractDTO);
        updateById(contract);
        appendLog("contract", id, "修改", "更新合同基本信息");
    }

    @Override
    @Transactional
    public void deleteContract(Long id) {
        removeById(id);
        licenseMapper.delete(new LambdaQueryWrapper<License>().eq(License::getContractId, id));
    }

    private void fillEntity(Contract contract, ContractDTO dto) {
        contract.setContractName(dto.getContractName());
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        if (dto.getProviderId() != null) {
            contract.setProviderId(dto.getProviderId());
        }
        contract.setCommercialRights(dto.getCommercialRights() == null ? "{}" : JSON.toJSONString(dto.getCommercialRights()));
        contract.setNotes(dto.getNotes());
        contract.setAttachmentName(dto.getAttachmentName());
        contract.setAttachmentUrl(dto.getAttachmentUrl());
    }

    private Map<String, Object> toMap(Contract contract) {
        Provider provider = providerMapper.selectById(contract.getProviderId());
        Map<String, Object> map = new HashMap<>();
        map.put("id", contract.getId());
        map.put("contractName", contract.getContractName());
        map.put("startDate", contract.getStartDate());
        map.put("endDate", contract.getEndDate());
        map.put("providerId", contract.getProviderId());
        map.put("providerName", provider == null ? "" : provider.getProviderName());
        map.put("commercialRights", parseRights(contract.getCommercialRights()));
        map.put("notes", contract.getNotes());
        map.put("attachmentName", contract.getAttachmentName());
        map.put("attachmentUrl", contract.getAttachmentUrl());
        map.put("createTime", contract.getCreateTime());
        map.put("updateTime", contract.getUpdateTime());
        return map;
    }

    private Map<String, Boolean> parseRights(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return JSON.parseObject(json, new TypeReference<Map<String, Boolean>>() {});
        } catch (Exception ex) {
            return new HashMap<>();
        }
    }

    private void appendLog(String bizType, Long bizId, String action, String content) {
        BizChangeLog log = new BizChangeLog();
        log.setBizType(bizType);
        log.setBizId(bizId);
        log.setAction(action);
        log.setContent(content);
        log.setOperator("admin");
        bizChangeLogMapper.insert(log);
    }
}

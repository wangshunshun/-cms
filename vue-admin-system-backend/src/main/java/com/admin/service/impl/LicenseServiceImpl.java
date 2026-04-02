package com.admin.service.impl;

import com.admin.dto.LicenseDTO;
import com.admin.dto.LicenseQueryDTO;
import com.admin.entity.BizChangeLog;
import com.admin.entity.Contract;
import com.admin.entity.License;
import com.admin.mapper.BizChangeLogMapper;
import com.admin.mapper.ContractMapper;
import com.admin.mapper.LicenseMapper;
import com.admin.service.LicenseService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LicenseServiceImpl extends ServiceImpl<LicenseMapper, License> implements LicenseService {

    private final ContractMapper contractMapper;
    private final BizChangeLogMapper bizChangeLogMapper;

    public LicenseServiceImpl(ContractMapper contractMapper, BizChangeLogMapper bizChangeLogMapper) {
        this.contractMapper = contractMapper;
        this.bizChangeLogMapper = bizChangeLogMapper;
    }

    @Override
    public Page<Map<String, Object>> getLicensePage(LicenseQueryDTO queryDTO) {
        Page<License> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        LambdaQueryWrapper<License> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getLicenseName() != null && !queryDTO.getLicenseName().isEmpty()) {
            wrapper.like(License::getLicenseName, queryDTO.getLicenseName());
        }
        if (queryDTO.getContractId() != null) {
            wrapper.eq(License::getContractId, queryDTO.getContractId());
        }
        wrapper.orderByDesc(License::getId);
        page(page, wrapper);

        Page<Map<String, Object>> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<Map<String, Object>> records = new ArrayList<>();
        for (License license : page.getRecords()) {
            records.add(toMap(license));
        }
        result.setRecords(records);
        return result;
    }

    @Override
    public Map<String, Object> getLicenseInfo(Long id) {
        License license = getById(id);
        return license == null ? new HashMap<>() : toMap(license);
    }

    @Override
    public List<Map<String, Object>> getLicenseHistory(Long id) {
        List<BizChangeLog> logs = bizChangeLogMapper.selectList(
            new LambdaQueryWrapper<BizChangeLog>()
                .eq(BizChangeLog::getBizType, "license")
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
    public void saveLicense(LicenseDTO licenseDTO) {
        License license = new License();
        fillEntity(license, licenseDTO);
        save(license);
        appendLog("license", license.getId(), "创建", "创建许可证");
    }

    @Override
    @Transactional
    public void updateLicense(Long id, LicenseDTO licenseDTO) {
        License license = getById(id);
        if (license == null) {
            return;
        }
        fillEntity(license, licenseDTO);
        updateById(license);
        appendLog("license", id, "修改", "更新许可证基本信息");
    }

    @Override
    @Transactional
    public void deleteLicense(Long id) {
        removeById(id);
    }

    private void fillEntity(License license, LicenseDTO dto) {
        license.setLicenseName(dto.getLicenseName());
        license.setContractId(dto.getContractId());
        license.setStartDate(dto.getStartDate());
        license.setEndDate(dto.getEndDate());
        license.setServiceType(dto.getServiceType());
        license.setRegions(JSON.toJSONString(dto.getRegions() == null ? Collections.emptyList() : dto.getRegions()));
        license.setPlatforms(JSON.toJSONString(dto.getPlatforms() == null ? Collections.emptyList() : dto.getPlatforms()));
        license.setAdvertorialRights(JSON.toJSONString(dto.getAdvertorialRights() == null ? Collections.emptyMap() : dto.getAdvertorialRights()));
        license.setDownloadableForMobile(Boolean.TRUE.equals(dto.getDownloadableForMobile()) ? 1 : 0);
        license.setDownloadDuration(Boolean.TRUE.equals(dto.getDownloadableForMobile()) ? dto.getDownloadDuration() : null);
        license.setPreviewForMobile(Boolean.TRUE.equals(dto.getPreviewForMobile()) ? 1 : 0);
        license.setPreviewBeginTime(Boolean.TRUE.equals(dto.getPreviewForMobile()) ? dto.getPreviewBeginTime() : "");
        license.setPreviewEndTime(Boolean.TRUE.equals(dto.getPreviewForMobile()) ? dto.getPreviewEndTime() : "");
        license.setNotes(dto.getNotes());
    }

    private Map<String, Object> toMap(License license) {
        Contract contract = contractMapper.selectById(license.getContractId());
        Map<String, Object> map = new HashMap<>();
        map.put("id", license.getId());
        map.put("licenseName", license.getLicenseName());
        map.put("contractId", license.getContractId());
        map.put("contractName", contract == null ? "" : contract.getContractName());
        map.put("startDate", license.getStartDate());
        map.put("endDate", license.getEndDate());
        map.put("serviceType", license.getServiceType());
        map.put("regions", parseList(license.getRegions()));
        map.put("platforms", parseList(license.getPlatforms()));
        map.put("advertorialRights", parseRights(license.getAdvertorialRights()));
        map.put("downloadableForMobile", Objects.equals(license.getDownloadableForMobile(), 1));
        map.put("downloadDuration", license.getDownloadDuration());
        map.put("previewForMobile", Objects.equals(license.getPreviewForMobile(), 1));
        map.put("previewBeginTime", license.getPreviewBeginTime());
        map.put("previewEndTime", license.getPreviewEndTime());
        map.put("notes", license.getNotes());
        map.put("createTime", license.getCreateTime());
        map.put("updateTime", license.getUpdateTime());
        return map;
    }

    private List<String> parseList(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return JSON.parseArray(json, String.class);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
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

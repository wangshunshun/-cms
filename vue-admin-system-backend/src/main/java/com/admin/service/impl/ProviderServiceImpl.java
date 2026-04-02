package com.admin.service.impl;

import com.admin.dto.ProviderDTO;
import com.admin.dto.ProviderQueryDTO;
import com.admin.entity.BizChangeLog;
import com.admin.entity.Contract;
import com.admin.entity.Provider;
import com.admin.mapper.BizChangeLogMapper;
import com.admin.mapper.ContractMapper;
import com.admin.mapper.ProviderMapper;
import com.admin.service.ProviderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProviderServiceImpl extends ServiceImpl<ProviderMapper, Provider> implements ProviderService {

    private final ContractMapper contractMapper;
    private final BizChangeLogMapper bizChangeLogMapper;

    public ProviderServiceImpl(ContractMapper contractMapper, BizChangeLogMapper bizChangeLogMapper) {
        this.contractMapper = contractMapper;
        this.bizChangeLogMapper = bizChangeLogMapper;
    }

    @Override
    public Page<Map<String, Object>> getProviderPage(ProviderQueryDTO queryDTO) {
        Page<Provider> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        LambdaQueryWrapper<Provider> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getProviderName() != null && !queryDTO.getProviderName().isEmpty()) {
            wrapper.like(Provider::getProviderName, queryDTO.getProviderName());
        }
        if (queryDTO.getCountry() != null && !queryDTO.getCountry().isEmpty()) {
            wrapper.like(Provider::getCountry, queryDTO.getCountry());
        }
        wrapper.orderByDesc(Provider::getId);
        page(page, wrapper);

        Page<Map<String, Object>> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<Map<String, Object>> records = new ArrayList<>();
        for (Provider provider : page.getRecords()) {
            Map<String, Object> map = toMap(provider);
            Long count = contractMapper.selectCount(
                new LambdaQueryWrapper<Contract>().eq(Contract::getProviderId, provider.getId())
            );
            map.put("contractCount", count);
            records.add(map);
        }
        result.setRecords(records);
        return result;
    }

    @Override
    public List<Map<String, Object>> getAllProviders() {
        List<Provider> providers = list(new LambdaQueryWrapper<Provider>().orderByAsc(Provider::getProviderName));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Provider provider : providers) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", provider.getId());
            map.put("providerName", provider.getProviderName());
            result.add(map);
        }
        return result;
    }

    @Override
    public Map<String, Object> getProviderInfo(Long id) {
        Provider provider = getById(id);
        return provider == null ? new HashMap<>() : toMap(provider);
    }

    @Override
    public List<Map<String, Object>> getProviderHistory(Long id) {
        List<BizChangeLog> logs = bizChangeLogMapper.selectList(
            new LambdaQueryWrapper<BizChangeLog>()
                .eq(BizChangeLog::getBizType, "provider")
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
    public void saveProvider(ProviderDTO providerDTO) {
        Provider provider = new Provider();
        provider.setProviderName(providerDTO.getProviderName());
        provider.setCountry(providerDTO.getCountry());
        provider.setNotes(providerDTO.getNotes());
        save(provider);
        appendLog("provider", provider.getId(), "创建", "创建供应商");
    }

    @Override
    @Transactional
    public void updateProvider(Long id, ProviderDTO providerDTO) {
        Provider provider = getById(id);
        if (provider == null) {
            return;
        }
        provider.setProviderName(providerDTO.getProviderName());
        provider.setCountry(providerDTO.getCountry());
        provider.setNotes(providerDTO.getNotes());
        updateById(provider);
        appendLog("provider", id, "修改", "更新供应商基本信息");
    }

    @Override
    @Transactional
    public void deleteProvider(Long id) {
        removeById(id);
    }

    private Map<String, Object> toMap(Provider provider) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", provider.getId());
        map.put("providerName", provider.getProviderName());
        map.put("country", provider.getCountry());
        map.put("notes", provider.getNotes());
        map.put("createTime", provider.getCreateTime());
        map.put("updateTime", provider.getUpdateTime());
        return map;
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

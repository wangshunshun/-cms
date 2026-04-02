package com.admin.service;

import com.admin.dto.ProviderDTO;
import com.admin.dto.ProviderQueryDTO;
import com.admin.entity.Provider;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface ProviderService extends IService<Provider> {
    Page<Map<String, Object>> getProviderPage(ProviderQueryDTO queryDTO);
    List<Map<String, Object>> getAllProviders();
    Map<String, Object> getProviderInfo(Long id);
    List<Map<String, Object>> getProviderHistory(Long id);
    void saveProvider(ProviderDTO providerDTO);
    void updateProvider(Long id, ProviderDTO providerDTO);
    void deleteProvider(Long id);
}

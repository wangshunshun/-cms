package com.admin.service;

import com.admin.dto.LicenseDTO;
import com.admin.dto.LicenseQueryDTO;
import com.admin.entity.License;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface LicenseService extends IService<License> {
    Page<Map<String, Object>> getLicensePage(LicenseQueryDTO queryDTO);
    Map<String, Object> getLicenseInfo(Long id);
    List<Map<String, Object>> getLicenseHistory(Long id);
    void saveLicense(LicenseDTO licenseDTO);
    void updateLicense(Long id, LicenseDTO licenseDTO);
    void deleteLicense(Long id);
}

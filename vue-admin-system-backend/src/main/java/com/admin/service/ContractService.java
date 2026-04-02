package com.admin.service;

import com.admin.dto.ContractDTO;
import com.admin.dto.ContractQueryDTO;
import com.admin.entity.Contract;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface ContractService extends IService<Contract> {
    Page<Map<String, Object>> getContractPage(ContractQueryDTO queryDTO);
    Map<String, Object> getContractInfo(Long id);
    List<Map<String, Object>> getContractHistory(Long id);
    void saveContract(ContractDTO contractDTO);
    void updateContract(Long id, ContractDTO contractDTO);
    void deleteContract(Long id);
}

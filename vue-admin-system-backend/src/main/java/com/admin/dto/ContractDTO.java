package com.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

@Data
public class ContractDTO implements Serializable {
    private Long id;
    private String contractName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long providerId;
    private Map<String, Boolean> commercialRights;
    private String notes;
    private String attachmentName;
    private String attachmentUrl;
}

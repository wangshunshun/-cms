package com.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class LicenseDTO implements Serializable {
    private Long id;
    private String licenseName;
    private Long contractId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String serviceType;
    private List<String> regions;
    private List<String> platforms;
    private Map<String, Boolean> advertorialRights;
    private Boolean downloadableForMobile;
    private Integer downloadDuration;
    private Boolean previewForMobile;
    private String previewBeginTime;
    private String previewEndTime;
    private String notes;
}

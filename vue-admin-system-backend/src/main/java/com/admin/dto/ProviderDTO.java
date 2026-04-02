package com.admin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProviderDTO implements Serializable {
    private Long id;
    private String providerName;
    private String country;
    private String notes;
}

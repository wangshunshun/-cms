package com.admin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProviderQueryDTO implements Serializable {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String providerName;
    private String country;
}

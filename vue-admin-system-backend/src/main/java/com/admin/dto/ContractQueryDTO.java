package com.admin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ContractQueryDTO implements Serializable {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String contractName;
    private Long providerId;
}

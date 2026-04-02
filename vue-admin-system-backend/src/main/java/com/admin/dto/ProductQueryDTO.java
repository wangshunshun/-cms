package com.admin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductQueryDTO implements Serializable {
    private String name;
    private Long categoryId;
    private Integer status;
    private Integer page = 1;
    private Integer pageSize = 10;
}
package com.admin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageDTO implements Serializable {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String keyword;
}

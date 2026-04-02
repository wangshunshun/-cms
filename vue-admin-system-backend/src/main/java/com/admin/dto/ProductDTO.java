package com.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDTO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Long categoryId;
    private Integer status;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
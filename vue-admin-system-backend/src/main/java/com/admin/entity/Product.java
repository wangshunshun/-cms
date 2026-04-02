package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private Long categoryId;

    private Integer status;

    private String image;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
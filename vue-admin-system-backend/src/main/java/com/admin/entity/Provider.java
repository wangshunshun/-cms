package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("provider")
public class Provider implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String providerName;

    private String country;

    private String notes;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

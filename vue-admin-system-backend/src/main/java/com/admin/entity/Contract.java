package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("contract")
public class Contract implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String contractName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long providerId;

    private String commercialRights;

    private String notes;

    private String attachmentName;

    private String attachmentUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

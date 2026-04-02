package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("license")
public class License implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String licenseName;

    private Long contractId;

    private LocalDate startDate;

    private LocalDate endDate;

    private String serviceType;

    private String regions;

    private String platforms;

    private String advertorialRights;

    private Integer downloadableForMobile;

    private Integer downloadDuration;

    private Integer previewForMobile;

    private String previewBeginTime;

    private String previewEndTime;

    private String notes;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

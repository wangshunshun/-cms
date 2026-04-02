package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("biz_change_log")
public class BizChangeLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String bizType;

    private Long bizId;

    private String action;

    private String content;

    private String operator;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

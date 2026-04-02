package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String operName;

    private String operUrl;

    private String operMethod;

    private String operIp;

    private String operParams;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime operTime;

    private Integer operResult;
}

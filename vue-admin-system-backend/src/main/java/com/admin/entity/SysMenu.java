package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class SysMenu implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String menuName;

    private String path;

    private String component;

    private String icon;

    private String perms;

    private Integer sort;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

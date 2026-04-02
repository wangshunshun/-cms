package com.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RoleDTO implements Serializable {
    private Long id;
    private String roleName;
    private String roleKey;
    private Integer status;
    private List<Long> menuIds;
}

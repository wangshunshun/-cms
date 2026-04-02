package com.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserDTO implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private List<Long> roleIds;
}

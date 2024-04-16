package com.d2d.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private Integer roleId;
    private String roleJson;
}

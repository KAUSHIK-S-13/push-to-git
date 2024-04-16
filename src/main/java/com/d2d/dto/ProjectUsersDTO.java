package com.d2d.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ProjectUsersDTO {
    private Integer id;
    private ProjectDTO projectId;
    private UsersDTO userId;
}

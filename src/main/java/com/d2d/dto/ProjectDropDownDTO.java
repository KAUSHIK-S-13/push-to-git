package com.d2d.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDropDownDTO {
    private Integer projectId;
    private Integer projectTypeId;
    private String projectType;
    private String projectName;
    private Integer dbEngineId;
    private Long userId;
}

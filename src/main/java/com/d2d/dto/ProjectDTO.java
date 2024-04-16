package com.d2d.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ProjectDTO {
    private Integer id;
    private String projectType;
    private String projectName;
    private DataBaseEngineDTO dbEngineIdFk;
    private FileDetailsDTO fileIdFk;
    private Boolean isActive;
    private Boolean deletedFlag;
    private Timestamp createdAt;
    private Integer createdBy;
    private Timestamp modifiedAt;
    private Integer modifiedBy;
}

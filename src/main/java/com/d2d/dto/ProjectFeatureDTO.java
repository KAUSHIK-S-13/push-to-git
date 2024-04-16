package com.d2d.dto;

import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;
@Getter
@Setter
public class ProjectFeatureDTO {
    private Integer id;
    private String featureType;
    private String featureCategory;
    private String featureDescription;
    private Boolean isDefault;
    private String componentCode;
    private String featureLevel;
    private Boolean isActive;
    private Boolean deletedFlag;
    private Timestamp createdAt;
    private Integer createdBy;
    private Timestamp modifiedAt;
    private Integer modifiedBy;
}

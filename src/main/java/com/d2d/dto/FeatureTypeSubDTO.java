package com.d2d.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeatureTypeSubDTO {
    private String featureCode;
    private String featureDescription;
    private Boolean isDefault;
    private String componentCode;
    private String featureLevel;
    private Boolean isActive;
    private Boolean deletedFlag;
}

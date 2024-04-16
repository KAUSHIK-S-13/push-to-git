package com.d2d.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FeatureCategoryDTO {
    private String featureCategory;

    private List<FeatureTypeSubDTO> featureTypeSub;

}

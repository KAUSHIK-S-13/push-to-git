package com.d2d.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FeatureTypeDTO {

    private String featureType;
    private List<FeatureCategoryDTO> featureCategoryDetails;
}

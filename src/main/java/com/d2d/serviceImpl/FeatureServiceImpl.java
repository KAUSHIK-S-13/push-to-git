package com.d2d.serviceImpl;

import com.d2d.dto.FeatureCategoryDTO;
import com.d2d.dto.FeatureTypeDTO;
import com.d2d.dto.FeatureTypeSubDTO;
import com.d2d.dto.ProjectDropDownDTO;
import com.d2d.entity.Project;
import com.d2d.entity.ProjectFeatures;
import com.d2d.entity.Users;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ErrorCode;
import com.d2d.exception.ModelNotFoundException;
import com.d2d.repository.ProjectFeaturesRepository;
import com.d2d.response.SuccessResponse;
import com.d2d.service.FeatureService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeatureServiceImpl implements FeatureService {

    @Autowired
    ProjectFeaturesRepository projectFeaturesRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public SuccessResponse<List<FeatureTypeDTO>> getAllFeatures() throws ModelNotFoundException {
        List<ProjectFeatures> projectFeatures = projectFeaturesRepository.findAllByDeletedFlag(false);

        if (!projectFeatures.isEmpty()) {
            Map<String, Map<String, List<ProjectFeatures>>> groupedFeatures = projectFeatures.stream()
                    .collect(Collectors.groupingBy(ProjectFeatures::getFeatureType,
                            Collectors.groupingBy(ProjectFeatures::getFeatureCategory)));

            List<FeatureTypeDTO> featureTypeDTOS = new ArrayList<>();

            groupedFeatures.forEach((featureType, categoryMap) -> {
                FeatureTypeDTO featureTypeDTO = new FeatureTypeDTO();
                featureTypeDTO.setFeatureType(featureType);

                List<FeatureCategoryDTO> featureCategoryDTOs = new ArrayList<>();

                categoryMap.forEach((category, features) -> {
                    FeatureCategoryDTO featureCategoryDTO = new FeatureCategoryDTO(); // Create a new instance for each category
                    featureCategoryDTO.setFeatureCategory(category);

                    List<FeatureTypeSubDTO> featureTypeSubDTOs = features.stream()
                            .map(data -> modelMapper.map(data, FeatureTypeSubDTO.class))
                            .collect(Collectors.toList());

                    featureCategoryDTO.setFeatureTypeSub(featureTypeSubDTOs);
                    featureCategoryDTOs.add(featureCategoryDTO);
                });

                featureTypeDTO.setFeatureCategoryDetails(featureCategoryDTOs);
                featureTypeDTOS.add(featureTypeDTO);
            });

            SuccessResponse<List<FeatureTypeDTO>> successResponse = new SuccessResponse<>();
            successResponse.setData(featureTypeDTOS);
            return successResponse;
        } else {
            throw new ModelNotFoundException(ErrorCode.D2D_1);
        }
    }
}

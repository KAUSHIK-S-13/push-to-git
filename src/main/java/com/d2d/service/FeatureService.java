package com.d2d.service;

import com.d2d.dto.FeatureTypeDTO;
import com.d2d.exception.ModelNotFoundException;
import com.d2d.response.SuccessResponse;

import java.util.List;

public interface FeatureService {
    SuccessResponse<List<FeatureTypeDTO>> getAllFeatures() throws ModelNotFoundException;
}

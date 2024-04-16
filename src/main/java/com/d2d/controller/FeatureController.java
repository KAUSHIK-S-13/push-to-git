package com.d2d.controller;

import com.d2d.dto.FeatureTypeDTO;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ModelNotFoundException;
import com.d2d.response.SuccessResponse;
import com.d2d.service.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class FeatureController {

    @Autowired
    FeatureService featureService;
    @GetMapping("/get/AllFeatures")

    public SuccessResponse<List<FeatureTypeDTO>> getAllFeatures()
            throws CustomValidationException, ModelNotFoundException {
        return featureService.getAllFeatures();
    }
}

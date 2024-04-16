package com.d2d.service;

import com.d2d.dto.DbDesignTables;
import com.d2d.dto.ProjectDropDownDTO;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ModelNotFoundException;
import com.d2d.response.SuccessResponse;

import java.io.IOException;
import java.util.List;

public interface ProjectInterface {

    SuccessResponse<List<ProjectDropDownDTO>> getAllProjects(Long userId,Integer engineId) throws ModelNotFoundException, CustomValidationException;

    DbDesignTables getJsonDataFromProject(ProjectDropDownDTO projectDTO) throws CustomValidationException, IOException;
}

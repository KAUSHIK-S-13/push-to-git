package com.d2d.controller;

import com.d2d.dto.DbDesignTables;
import com.d2d.dto.ProjectDropDownDTO;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ModelNotFoundException;
import com.d2d.response.SuccessResponse;
import com.d2d.service.ProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    ProjectInterface projectInterface;
    @GetMapping("/get/All")
    public SuccessResponse<List<ProjectDropDownDTO>> getAllProjects(@RequestParam Long userId,
                                                                        @RequestParam Integer engineId)
            throws CustomValidationException, ModelNotFoundException {
        return projectInterface.getAllProjects(userId,engineId);
    }


    @PostMapping("/get/jsonData")
    public DbDesignTables getJsonDataFromProject(@RequestBody ProjectDropDownDTO projectDTO)
            throws CustomValidationException, ModelNotFoundException, IOException {
        return projectInterface.getJsonDataFromProject(projectDTO);
    }

}

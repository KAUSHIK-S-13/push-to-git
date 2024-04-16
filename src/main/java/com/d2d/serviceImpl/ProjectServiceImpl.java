package com.d2d.serviceImpl;

import com.d2d.config.MinioFileUploader;
import com.d2d.dto.DbDesignTables;
import com.d2d.dto.ProjectDropDownDTO;
import com.d2d.entity.FileDetails;
import com.d2d.entity.Project;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ErrorCode;
import com.d2d.exception.ModelNotFoundException;
import com.d2d.repository.FileDetailsRepository;
import com.d2d.repository.ProjectRepository;
import com.d2d.response.SuccessResponse;
import com.d2d.service.ProjectInterface;
import com.d2d.util.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectInterface {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    FileDetailsRepository fileDetailsRepository;

    @Autowired
    MinioFileUploader minioFileUploader;

    @Autowired
    D2DHistoryServiceImpl d2DHistoryService;

    @Autowired
    ProjectUtil projectUtil;

    @Override
    public SuccessResponse<List<ProjectDropDownDTO>> getAllProjects(Long userId, Integer engineId) throws ModelNotFoundException, CustomValidationException {
        if (userId != null) {
            List<Project> projects = projectRepository.findAllByUserAndEngineId(userId, engineId);
            List<ProjectDropDownDTO> projectDropDownDTOS = new ArrayList<>();
            SuccessResponse<List<ProjectDropDownDTO>> successResponse = new SuccessResponse<>();
            if (!projects.isEmpty()) {
                projects.forEach(data -> {
                    ProjectDropDownDTO projectDropDownDTO = new ProjectDropDownDTO();
                    projectDropDownDTO.setProjectId(data.getId());
                    projectDropDownDTO.setProjectName(data.getProjectName());
                    projectDropDownDTO.setDbEngineId(data.getDbEngine().getId());
                    projectDropDownDTO.setProjectTypeId(data.getProjectType().getId());
                    projectDropDownDTO.setUserId(userId);
                    projectDropDownDTO.setProjectType(data.getProjectType().getMasterName());
                    projectDropDownDTOS.add(projectDropDownDTO);
                });
                successResponse.setData(projectDropDownDTOS);
                return successResponse;
            }
            successResponse.setData(projectDropDownDTOS);
            return successResponse;
        } else {
            throw new CustomValidationException(ErrorCode.D2D_2);
        }
    }

    @Override
    public DbDesignTables getJsonDataFromProject(ProjectDropDownDTO projectDTO) throws CustomValidationException, IOException {
        Optional<FileDetails> fileDetails = fileDetailsRepository.findAllByFilters(projectDTO.getProjectId(),
                projectDTO.getProjectName());
        if (fileDetails.isPresent()) {
            InputStream inputStream = minioFileUploader.downloadFile(fileDetails.get().getGeneratedFileName());
            byte[] encryptedBytes = inputStream.readAllBytes();
            MultipartFile multipartFile=projectUtil.getFile(encryptedBytes);
            ResponseEntity<DbDesignTables> tablesDetails = d2DHistoryService.uploadEncFileAndDecryptFile(multipartFile);
            return tablesDetails.getBody();
        }
        return null;
    }

}

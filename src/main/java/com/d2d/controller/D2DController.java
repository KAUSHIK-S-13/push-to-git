package com.d2d.controller;

import com.d2d.dto.ConnectionDetailsDTO;
import com.d2d.dto.DataBaseEngineDTO;
import com.d2d.dto.DataTypeDTO;
import com.d2d.dto.DataTypeMappingDTO;
import com.d2d.dto.DataTypeSaveDTO;
import com.d2d.dto.DbDesignTables;
import com.d2d.dto.DbMigrationColumsTablesDTO;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ErrorCode;
import com.d2d.exception.ModelNotFoundException;
import com.d2d.generator.DbDesignExcelGenerator;
import com.d2d.response.SuccessResponse;
import com.d2d.service.D2DServiceInterface;
import com.d2d.util.CommonUtil;
import com.d2d.util.EmailUtil;
import com.d2d.util.ProjectUtil;
import com.d2d.util.SpringBootBasicUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.errors.MinioException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Sekhar
 */
@RestController
public class D2DController {

    @Autowired
    D2DServiceInterface d2DServiceInterface;

    @Autowired
    CommonUtil commonUtil;

    @Autowired
    EmailUtil emailUtil;

    @Autowired
    ProjectUtil projectUtil;

    @Autowired
    SpringBootBasicUtil springBootBasicUtil;

    private static final Logger logger = LoggerFactory.getLogger(D2DController.class);

    @PostMapping("/d2d/export/db/design/excel")
    public ResponseEntity<Resource> downloadDBDesign(HttpServletResponse response, @RequestBody DbDesignTables tables) throws IOException, CustomValidationException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        HttpHeaders headers = new HttpHeaders();
        ByteArrayResource downloadExcelFile = DbDesignExcelGenerator.downloadDBDesign(tables.getTableList());
        ByteArrayResource downloadTextFile = null;
        if (tables.getDatabaseEngineId() == 1) {
            downloadTextFile = DbDesignExcelGenerator.queryGenerator(tables.getTableList(), tables);
        } else if (tables.getDatabaseEngineId() == 4) {
            downloadTextFile = DbDesignExcelGenerator.postGreSQLQueryGenerator(tables.getTableList(), tables);
        } else if (tables.getDatabaseEngineId() == 3) {
            downloadTextFile = DbDesignExcelGenerator.oracleQueryGenerator(tables.getTableList(), tables);
        } else if (tables.getDatabaseEngineId() == 5) {
            downloadTextFile = DbDesignExcelGenerator.msSqlQueryGenerator(tables.getTableList(), tables);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

        ZipEntry excelEntry = new ZipEntry(String.format("%s_DbDesign.xlsx", tables.getDbDesignName()));
        zipOutputStream.putNextEntry(excelEntry);
        IOUtils.copy(downloadExcelFile.getInputStream(), zipOutputStream);
        zipOutputStream.closeEntry();

        ZipEntry textEntry = new ZipEntry(String.format("%s_SqlScript.sql", tables.getDbDesignName()));
        zipOutputStream.putNextEntry(textEntry);
        if (downloadTextFile != null) {
            IOUtils.copy(downloadTextFile.getInputStream(), zipOutputStream);
        } else {
            throw new CustomValidationException(ErrorCode.D2D_11);
        }
        zipOutputStream.closeEntry();

        zipOutputStream.finish();
        zipOutputStream.close();

        ByteArrayResource zipResource = new ByteArrayResource(outputStream.toByteArray());
        projectUtil.encryptJson(tables);

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=documents.zip");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        return new ResponseEntity<>(zipResource, headers, HttpStatus.OK);

    }

    @PostMapping("/save/data-base-engine-name")
    public SuccessResponse<String> saveDataBaseEngine(@RequestParam String dataBaseEngineName)
            throws CustomValidationException {
        return d2DServiceInterface.saveDataBaseEngine(dataBaseEngineName);
    }

    @PostMapping("/save/data-type")
    public SuccessResponse<String> saveDataType(@RequestBody DataTypeSaveDTO dataTypeSaveDTO)
            throws CustomValidationException, ModelNotFoundException {
        return d2DServiceInterface.saveDataType(dataTypeSaveDTO);
    }

    @GetMapping("/get/data-types")
    public SuccessResponse<List<DataTypeDTO>> getDataTypeDetails(@RequestParam Integer dataBaseEngineId)
            throws CustomValidationException, ModelNotFoundException {
        return d2DServiceInterface.getAllDataTypeDetails(dataBaseEngineId);
    }

    @GetMapping("/get/data-base-engine")
    public SuccessResponse<List<DataBaseEngineDTO>> getDataBaseEngineDetails()
            throws ModelNotFoundException {
        return d2DServiceInterface.getAllDataBaseEngineDetails();
    }

    @PostMapping("/generateBasicSpringBootSetUp")
    public ResponseEntity<Resource> zipFileCreation(@RequestBody DbDesignTables tables) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonValueForDbDesignTables = objectMapper.writeValueAsString(tables);
        try {
            if (tables.getProjectTypeId()!=2){
            ByteArrayResource downloadExcelFile = DbDesignExcelGenerator.downloadDBDesign(tables.getTableList());
            ByteArrayResource downloadTextFile = null;
            if (tables.getDatabaseEngineId() == 1) {
                downloadTextFile = DbDesignExcelGenerator.queryGenerator(tables.getTableList(), tables);
            } else if (tables.getDatabaseEngineId() == 4) {
                downloadTextFile = DbDesignExcelGenerator.postGreSQLQueryGenerator(tables.getTableList(), tables);
            } else if (tables.getDatabaseEngineId() == 3) {
                downloadTextFile = DbDesignExcelGenerator.oracleQueryGenerator(tables.getTableList(), tables);
            } else if (tables.getDatabaseEngineId() == 5) {
                downloadTextFile = DbDesignExcelGenerator.msSqlQueryGenerator(tables.getTableList(), tables);
            }
            Path dbDesignAndScriptZip = Files.createTempFile("documents", ".zip");

            writeToZip(dbDesignAndScriptZip, tables, downloadTextFile, downloadExcelFile);

            Path projectBaseSetUpZip = Files.createTempFile("projectBaseSetUp", ".zip");
            ZipOutputStream updatedZipFile = new ZipOutputStream(new FileOutputStream(projectBaseSetUpZip.toFile()));
            d2DServiceInterface.zipFileCreation(tables, updatedZipFile);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

            ZipEntry dbDesignAndScriptZipEntry = new ZipEntry("documents.zip");
            zipOutputStream.putNextEntry(dbDesignAndScriptZipEntry);
            zipOutputStream.write(Files.readAllBytes(dbDesignAndScriptZip.toAbsolutePath()));
            zipOutputStream.closeEntry();

            ZipEntry projectBaseSetUpZipEntry = new ZipEntry(tables.getSpringBootBasicDTO().getArtifactId() + ".zip");
            zipOutputStream.putNextEntry(projectBaseSetUpZipEntry);
            zipOutputStream.write(Files.readAllBytes(projectBaseSetUpZip.toAbsolutePath()));
            zipOutputStream.closeEntry();

            zipOutputStream.close();

            Files.deleteIfExists(projectBaseSetUpZip);
            Files.deleteIfExists(dbDesignAndScriptZip);

            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MMM_yyyy_HH.mm");
            String formattedDateTime = currentDateTime.format(formatter);
            String zipName = tables.getSpringBootBasicDTO().getArtifactId() + "_" + formattedDateTime + ".zip";

            projectUtil.encryptJson(tables);

            HttpHeaders headers = new HttpHeaders();
            ByteArrayResource zipResource = new ByteArrayResource(outputStream.toByteArray());
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipName);
            headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
            return new ResponseEntity<>(zipResource, headers, HttpStatus.OK);
            }else {
                return springBootBasicUtil.getResourceResponseEntity(tables);
            }
        } catch (CustomValidationException customValidationException) {
            emailUtil.sendMail(customValidationException.toString(), jsonValueForDbDesignTables);
            throw new CustomValidationException(customValidationException.getErrorCode());
        }
    }
    public void writeToZip(Path dbDesignAndScriptZip,DbDesignTables tables,ByteArrayResource downloadTextFile,ByteArrayResource downloadExcelFile) throws IOException {
        try (ZipOutputStream dbDesignAndScript = new ZipOutputStream(new FileOutputStream(dbDesignAndScriptZip.toFile()))) {
            ZipEntry excelEntry = new ZipEntry(tables.getSpringBootBasicDTO().getArtifactId() + "_DbDesign.xlsx");
            dbDesignAndScript.putNextEntry(excelEntry);
            IOUtils.copy(downloadExcelFile.getInputStream(), dbDesignAndScript);
            dbDesignAndScript.closeEntry();
            ZipEntry textEntry = new ZipEntry(tables.getSpringBootBasicDTO().getArtifactId() + "_SqlScript.sql");
            dbDesignAndScript.putNextEntry(textEntry);
            if (downloadTextFile != null) {
                IOUtils.copy(downloadTextFile.getInputStream(), dbDesignAndScript);
            } else {
                throw new CustomValidationException(ErrorCode.D2D_11);
            }
            dbDesignAndScript.closeEntry();

        } catch (Exception e) {
            logger.info("********generateBasicSpringBootSetUp*****", e);
        }
    }
    @GetMapping("/getAllMetaData")
    public Object getAllMetaData() {
        return d2DServiceInterface.getAllMetaData();
    }

    @GetMapping("/get/data-types-with-mapping")
    public SuccessResponse<List<DataTypeMappingDTO>> getDataTypeMappingDetails(@RequestParam Integer dataBaseEngineId)
            throws CustomValidationException, ModelNotFoundException {
        return d2DServiceInterface.getAllDataTypeMappingDetails(dataBaseEngineId);
    }

    @PostMapping("/dbMigration")
    public ResponseEntity<DbMigrationColumsTablesDTO> dbMigration(@RequestBody Map<String, ConnectionDetailsDTO> connectionDetailsMap) {
        return d2DServiceInterface.getDbMigrationJsonData(connectionDetailsMap);
    }

    @PostMapping("/getDbMigrationQueryGenerator")
    public ResponseEntity<Resource> dbMigrationQueryGenerator(@RequestBody DbDesignTables tables) throws CustomValidationException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonValueForDbDesignTables = objectMapper.writeValueAsString(tables);
        try {
            if (tables.getDatabaseEngineId() == 1) {
                DbDesignExcelGenerator.queryGenerator(tables.getTableList(), tables);
            } else if (tables.getDatabaseEngineId() == 4) {
                DbDesignExcelGenerator.postGreSQLQueryGenerator(tables.getTableList(), tables);
            } else if (tables.getDatabaseEngineId() == 3) {
                DbDesignExcelGenerator.oracleQueryGenerator(tables.getTableList(), tables);
            } else if (tables.getDatabaseEngineId() == 5) {
                DbDesignExcelGenerator.msSqlQueryGenerator(tables.getTableList(), tables);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomValidationException customValidationException) {
            emailUtil.sendMail(customValidationException.toString(), jsonValueForDbDesignTables);
            throw new CustomValidationException(customValidationException.getErrorCode());
        }
    }
    @GetMapping("/getSwaggerJsonData")
    public String getSwaggerJsonData() throws IOException, InterruptedException {
        return d2DServiceInterface.fetchDataFromApi();
    }

}

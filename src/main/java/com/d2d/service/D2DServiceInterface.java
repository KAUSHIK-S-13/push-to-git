package com.d2d.service;

import com.d2d.dto.*;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ModelNotFoundException;
import com.d2d.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

@Service
public interface D2DServiceInterface {

    SuccessResponse<List<DataTypeDTO>> getAllDataTypeDetails(Integer dataBaseEngineId)
            throws CustomValidationException, ModelNotFoundException;

    SuccessResponse<String> saveDataBaseEngine(String dataBaseEngineName) throws CustomValidationException;

    SuccessResponse<String> saveDataType(DataTypeSaveDTO dataTypeSaveDTO) throws ModelNotFoundException,
            CustomValidationException;

    SuccessResponse<List<DataBaseEngineDTO>> getAllDataBaseEngineDetails() throws ModelNotFoundException;

    void zipFileCreation(DbDesignTables tables, ZipOutputStream updatedZipFile) throws Exception;

    Object getAllMetaData();

    SuccessResponse<List<DataTypeMappingDTO>> getAllDataTypeMappingDetails(Integer dataBaseEngineId) throws ModelNotFoundException, CustomValidationException;

    ResponseEntity<DbMigrationColumsTablesDTO> getDbMigrationJsonData(Map<String, ConnectionDetailsDTO> connectionDetailsMap);
    String fetchDataFromApi() throws IOException;
}

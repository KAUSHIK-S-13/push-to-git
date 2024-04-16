package com.d2d.service;

import com.d2d.exception.CustomValidationException;
import com.d2d.dto.DbDesignTables;
import com.d2d.dto.TableList;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface D2DHistoryServiceInterface {

    ResponseEntity<Resource> downloadEncFile(DbDesignTables tables) throws CustomValidationException;

    ResponseEntity<DbDesignTables> uploadEncFileAndDecryptFile(MultipartFile file) throws CustomValidationException;

    byte[]  excelTemplateDownload() throws IOException;

    ResponseEntity<TableList> uploadExcelFile(MultipartFile file) throws IOException, CustomValidationException;

    DbDesignTables saveTableDetails(DbDesignTables tables) throws CustomValidationException;
}

package com.d2d.controller;

import com.d2d.exception.CustomValidationException;
import com.d2d.service.D2DHistoryServiceInterface;
import com.d2d.dto.DbDesignTables;
import com.d2d.dto.TableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping(value = "/file")
public class D2DHistoryController {

    @Autowired
    D2DHistoryServiceInterface d2DHistoryServiceInterface;

    @PostMapping("/export")
    public ResponseEntity<Resource> downloadEncFile(@RequestBody DbDesignTables tables) throws CustomValidationException {
        return d2DHistoryServiceInterface.downloadEncFile(tables);
    }

    @PostMapping("/saveTableDetails")
    public DbDesignTables saveTableDetails(@RequestBody DbDesignTables tables) throws CustomValidationException {
        return d2DHistoryServiceInterface.saveTableDetails(tables);
    }

    @PostMapping("/import")
    public ResponseEntity<DbDesignTables> uploadEncFileAndDecryptFile(@RequestParam("file") MultipartFile file) throws CustomValidationException {
        return d2DHistoryServiceInterface.uploadEncFileAndDecryptFile(file);
    }

    @GetMapping("/excel/template")
    public HttpEntity<byte[]> excelTemplateDownload() throws IOException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
        String currentDateTime = dateFormatter.format(new Date());
        byte[] excelContent = d2DHistoryServiceInterface.excelTemplateDownload();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= D2D_" + currentDateTime  + ".xlsx");
        header.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        header.setContentLength(excelContent.length);
        return new HttpEntity<>(excelContent, header);
    }

    @PostMapping("/excel/import")
    public ResponseEntity<TableList> uploadExcelFile(@RequestParam("file") MultipartFile file) throws IOException, CustomValidationException {
        return d2DHistoryServiceInterface.uploadExcelFile(file);
    }

}

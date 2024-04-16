package com.d2d.serviceImpl;

import com.d2d.constant.Constant;
import com.d2d.dto.Column;
import com.d2d.dto.DbDesignTables;
import com.d2d.dto.TableList;
import com.d2d.dto.TableOfExcel;
import com.d2d.entity.DataBaseEngine;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ErrorCode;
import com.d2d.repository.DataBaseEngineRepository;
import com.d2d.service.D2DHistoryServiceInterface;
import com.d2d.util.ProjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@Service
public class D2DHistoryServiceImpl implements D2DHistoryServiceInterface {
    private static final Logger logger = LoggerFactory.getLogger(D2DHistoryServiceImpl.class);

    @Value("${app.SECRET_KEY}")
    private String SECRET_KEY;
    @Value("${app.ALGORITHM}")
    private String ALGORITHM;
    @Value("${app.CIPHER_TRANSFORMATION}")
    private String CIPHER_TRANSFORMATION;

    @Autowired
    ProjectUtil projectUtil;

    @Autowired
    DataBaseEngineRepository dataBaseEngineRepository;

    @Override
    public ResponseEntity<Resource> downloadEncFile(DbDesignTables tables) throws CustomValidationException {

        try {

            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);

            Cipher aesCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);

            ObjectMapper objectMapper = new ObjectMapper();
            if (tables.getDatabaseEngineId()!=null && tables.getDatabaseEngineId()!=0) {
                Optional<DataBaseEngine> dataBaseEngine =
                        dataBaseEngineRepository.findByIdAndIsActive(tables.getDatabaseEngineId(), 1);
                tables.setDbName(dataBaseEngine.get().getDataBaseEngineName());
            }
            //DbDesignTables tablesList= projectUtil.exportEncryptJson(tables);

            String jsonValue = objectMapper.writeValueAsString(tables);
            byte[] encryptedBytes = aesCipher.doFinal(jsonValue.getBytes());

            ByteArrayResource resource = new ByteArrayResource(encryptedBytes);
            if (tables.getProjectName() == null || tables.getProjectName().isEmpty()) {
                return ResponseEntity.ok()
                        .contentLength(resource.contentLength())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=D2DFile.dtd")
                        .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentLength(resource.contentLength())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + tables.getProjectName() + ".dtd")
                        .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                        .body(resource);
            }
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException | JsonProcessingException e) {
            throw new CustomValidationException(ErrorCode.valueOf("File Not Exist"));
        }
    }

    @Override
    @Transactional
    public DbDesignTables saveTableDetails(DbDesignTables tables) throws CustomValidationException {

        return projectUtil.exportEncryptJson(tables);
    }

    @Override
    public ResponseEntity<DbDesignTables> uploadEncFileAndDecryptFile(MultipartFile file) throws CustomValidationException {
        try {
            if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith("dtd")) {
                throw new CustomValidationException(ErrorCode.D2D_14);
            }

            byte[] encryptedBytes = file.getBytes();

            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);

            Cipher aesCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            aesCipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decryptedBytes = aesCipher.doFinal(encryptedBytes);

            DbDesignTables jsonValue = new ObjectMapper().readValue(decryptedBytes, DbDesignTables.class);

            return ResponseEntity.ok(jsonValue);
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw new CustomValidationException(e.getErrorCode());
        } catch (NoSuchPaddingException | IllegalBlockSizeException | IOException | NoSuchAlgorithmException |
                 BadPaddingException | InvalidKeyException e) {
            throw new CustomValidationException(ErrorCode.valueOf("File Not Exist"));
        }
    }

    @Override
    public byte[] excelTemplateDownload() throws IOException {
        ByteArrayOutputStream outputStream = null;
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("D2D");

            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.BLACK.index);
            headerFont.setFontHeight((short) 250);
            headerFont.setFontHeightInPoints((short) 15);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);

            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            int rowSize = 0;

            Row headerRow = sheet.createRow(rowSize);

            String[] excelColumns = {"Table Name ", "Column ", "DataType ", "Default Value", "key Type", "Reference Table", "Null Field", "Auto Increment", "Sample Data", "Column Comments", "Table Comments"};

            for (int column = 0; column < excelColumns.length; column++) {
                Cell cell = headerRow.createCell(column);
                cell.setCellValue(excelColumns[column]);
                cell.setCellStyle(headerCellStyle);
                sheet.autoSizeColumn(column);
            }

            sheet.autoSizeColumn(0);

            List<List<String>> dummyData = Arrays.asList(
                    Arrays.asList(Constant.SAMPLE, "id", "NUMBER", "", "PK", "", Constant.NOTNULL, "yes", "", "", "Sample Template"),
                    Arrays.asList(Constant.SAMPLE, "email", "CHAR VARYING", "", "", "", Constant.NOTNULL, "no", "", "", ""),
                    Arrays.asList(Constant.SAMPLE, "fullname", Constant.VARCHAR, "", "", "", "null", "no", "", "", ""),
                    Arrays.asList(Constant.SAMPLE, "otp", Constant.VARCHAR, "", "", "", Constant.NOTNULL, "no", "", "", ""),
                    Arrays.asList(Constant.SAMPLE, "password", Constant.VARCHAR, "", "", "", Constant.NOTNULL, "no", "", "", "")
            );

            int m = 1, k = 1;
            for (int j = 1; j <= 1; j++) {
                int size = 5;
                int lastRow = size + m - 1;
                sheet.addMergedRegion(new CellRangeAddress(m, lastRow, 0, 0));

                m = size + m + 2;
                sheet.addMergedRegion(new CellRangeAddress(m - 2, m - 2, 0, 10));
                k++;

            }


            int m1 = 1;
            for (int j = 1; j <= 1; j++) {
                int size = 5;
                int lastRow = size + m1 - 1;
                sheet.addMergedRegion(new CellRangeAddress(m1, lastRow, 10, 10));
                m1 = size + m1 + 2;

            }

            sheet.autoSizeColumn(0);
            for (int i = 1; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
                sheet.setColumnWidth(i, 20 * 255);

            }

            int rowIndex = 1;
            for (List<String> rowData : dummyData) {
                Row dataRow = sheet.createRow(rowIndex);
                for (int i = 0; i < rowData.size(); i++) {
                    Cell cell = dataRow.createCell(i);
                    cell.setCellValue(rowData.get(i));
                    CellStyle cellStyles = workbook.createCellStyle();
                    cellStyles.setAlignment(HorizontalAlignment.GENERAL);
                    cellStyles.setVerticalAlignment(VerticalAlignment.CENTER);
                    cellStyles.getWrapText();
                    cell.setCellStyle(cellStyles);
                }
                rowIndex++;
            }

            outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
        } catch (IOException e) {
            logger.info("*******excelTemplateDownload********", e);
        }
        assert outputStream != null;
        return outputStream.toByteArray();
    }

    @Override
    public ResponseEntity<TableList> uploadExcelFile(MultipartFile file) throws IOException, CustomValidationException {
        byte[] excelData = file.getBytes();
        Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData));
        Sheet sheet = workbook.getSheetAt(0);
        List<TableOfExcel> tables = new ArrayList<>();

        Map<String, Map<String, Column>> tableValues = new LinkedHashMap<>();


        int rowCount = sheet.getPhysicalNumberOfRows();
        int colCount = sheet.getRow(0).getPhysicalNumberOfCells();

        if (rowCount <= 1) {
            throw new CustomValidationException(ErrorCode.D2D_18);
        }

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            Cell tableNameCell = row.getCell(0);
            Cell columnNameCell = row.getCell(1);
            if ((tableNameCell == null || tableNameCell.getStringCellValue().isEmpty()) &&
                    (columnNameCell != null && !columnNameCell.getStringCellValue().isEmpty())) {
                Row previousRow = sheet.getRow(rowIndex - 1);
                String previousRowTableName = previousRow.getCell(0).getStringCellValue();

                if (previousRowTableName.equalsIgnoreCase("Table Name ")) {
                    throw new CustomValidationException(ErrorCode.D2D_15);
                } else {
                    assert tableNameCell != null;
                    tableNameCell.setCellValue(previousRowTableName);
                }

            }
        }
        for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
            Row row = sheet.getRow(rowIndex);

            if (row == null || isRowEmpty(row)) {
                continue;
            }

            String tableName = getStringCellValue(row.getCell(0));
            String tableComments = getStringCellValue(row.getCell(colCount - 1));

            if (row.getCell(0) == null || row.getCell(0).getStringCellValue().isEmpty()) {
                throw new CustomValidationException(ErrorCode.D2D_15);
            }
            if (row.getCell(1) == null || row.getCell(1).getStringCellValue().isEmpty()) {
                throw new CustomValidationException(ErrorCode.D2D_16);
            }
            if (row.getCell(2) == null || row.getCell(2).getStringCellValue().isEmpty()) {
                throw new CustomValidationException(ErrorCode.D2D_17);
            }

            if (tableValues.get(tableName) != null) {
                Column column = new Column();
                column.setName(getStringCellValue(row.getCell(1)));
                column.setDatatype(getStringCellValue(row.getCell(2)));
                column.setDefaultValue(getStringCellValue(row.getCell(3)));
                column.setKeytype(getStringCellValue(row.getCell(4)));
                column.setTableReference(getStringCellValue(row.getCell(5)));
                if (row.getCell(6) == null || row.getCell(6).getStringCellValue().equalsIgnoreCase("not null") || row.getCell(6).getStringCellValue().isEmpty() || row.getCell(6).getStringCellValue().equalsIgnoreCase("notnull")) {
                    column.setIsNull(Constant.FALSE);
                } else {
                    column.setIsNull(Constant.TRUE);
                }
                if (row.getCell(7) == null || row.getCell(7).getStringCellValue().isEmpty() || row.getCell(7).getStringCellValue().equalsIgnoreCase("No")) {
                    column.setIsAutoIncrement(Constant.FALSE);
                } else {
                    column.setIsAutoIncrement(Constant.TRUE);
                }
                column.setSampleData(getStringCellValue(row.getCell(8)));
                column.setColumnComments(getStringCellValue(row.getCell(9)));


                column.setId(colCount++);
                tableValues.get(tableName).put(getStringCellValue(row.getCell(1)), column);
            } else {
                if (getStringCellValue(row.getCell(1)) != null && !getStringCellValue(row.getCell(1)).equals("Column ")) {
                    LinkedHashMap<String, Column> columnMap = new LinkedHashMap<>();
                    Column column = new Column();
                    column.setName(getStringCellValue(row.getCell(1)));
                    column.setDatatype(getStringCellValue(row.getCell(2)));
                    column.setDefaultValue(getStringCellValue(row.getCell(3)));
                    column.setKeytype(getStringCellValue(row.getCell(4)));
                    column.setTableReference(getStringCellValue(row.getCell(5)));
                    if (row.getCell(6) == null || row.getCell(6).getStringCellValue().equalsIgnoreCase("not null") || row.getCell(6).getStringCellValue().isEmpty() || row.getCell(6).getStringCellValue().equalsIgnoreCase("notnull")) {
                        column.setIsNull("false");
                    } else {
                        column.setIsNull("true");
                    }
                    if (row.getCell(7) == null || row.getCell(7).getStringCellValue().isEmpty() || row.getCell(7).getStringCellValue().equalsIgnoreCase("No")) {
                        column.setIsAutoIncrement("false");
                    } else {
                        column.setIsAutoIncrement("true");
                    }
                    column.setSampleData(getStringCellValue(row.getCell(8)));
                    column.setColumnComments(getStringCellValue(row.getCell(9)));
                    TableOfExcel tableObj = new TableOfExcel();
                    tableObj.setName(tableName);
                    tableObj.setTableComments(tableComments);
                    columnMap.put(getStringCellValue(row.getCell(1)), column);
                    tableValues.put(tableName, columnMap);
                    tables.add(tableObj);
                }
            }
        }


        for (String tableName : tableValues.keySet()) {
            List<Column> columnList = new ArrayList<>(tableValues.get(tableName).values());

            for (TableOfExcel table : tables) {
                if (table.getName().equals(tableName)) {
                    table.setColumns(columnList);
                }
            }
        }

        workbook.close();

        TableList tableList = new TableList();
        tableList.setTableLists(tables);
        return ResponseEntity.ok(tableList);
    }


    private String getStringCellValue(Cell cell) {
        if (cell != null) {
            if (cell.getCellType().name().equals("NUMERIC")) {
                return String.valueOf(cell.getNumericCellValue());
            } else {
                return cell.getStringCellValue();
            }
        }

        return null;
    }

    private static boolean isRowEmpty(Row row) {
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

}

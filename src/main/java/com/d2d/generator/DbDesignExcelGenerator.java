package com.d2d.generator;

import com.d2d.constant.Constant;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ErrorCode;
import com.d2d.dto.Column;
import com.d2d.dto.DbDesignTables;
import com.d2d.dto.Table;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.d2d.util.CommonUtil.isColumnTypeValid;
import static com.d2d.util.CommonUtil.isColumnTypeValidDefaultValue;

public class DbDesignExcelGenerator {
    private static final Logger logger = LoggerFactory.getLogger(DbDesignExcelGenerator.class);

    private DbDesignExcelGenerator() {
    }

    public static ByteArrayResource downloadDBDesign(List<Table> tables) throws IOException, CustomValidationException {

        findDuplicateTableName(tables);

        String[] columns = {"Table Name ", "Column ", "DataType ", "Default Value", "key Type", "Reference Table", "Null Field", "Auto Increment", "Sample Data", "Column Comments", "Table Comments"};
        ByteArrayOutputStream out=null;
        try (Workbook workbook = new XSSFWorkbook()) {
             out = new ByteArrayOutputStream();
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


            int m = 1;
            int k = 1;

            for (int j = 0; j < tables.size(); j++) {
                int size = tables.get(j).getColumns().size();
                int lastRow = size + m - 1;
                if (size > 1)
                    sheet.addMergedRegion(new CellRangeAddress(m, lastRow, 0, 0));

                m = size + m + 2;
                if (tables.size() != k)
                    sheet.addMergedRegion(new CellRangeAddress(m - 2, m - 2, 0, 10));
                k++;

            }


            int m1 = 1;
            for (int j = 0; j < tables.size(); j++) {
                int size = tables.get(j).getColumns().size();
                int lastRow = size + m1 - 1;
                if (size > 1)
                    sheet.addMergedRegion(new CellRangeAddress(m1, lastRow, 10, 10));
                m1 = size + m1 + 2;

            }


            AtomicInteger rowIdx = new AtomicInteger(1);

            int g = 0;
            for (Table table : tables) {
                Row headerRow1 = sheet.createRow(g);

                for (int col = 0; col < columns.length; col++) {
                    Cell cell = headerRow1.createCell(col);
                    cell.setCellValue(columns[col]);
                    cell.setCellStyle(headerCellStyle);
                    sheet.autoSizeColumn(col);

                }
                sheet.autoSizeColumn(0);
                for (int i = 1; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
                    sheet.setColumnWidth(i, 20 * 255);

                }

                validateColumns(table.getColumns());

                for (Column column : table.getColumns()) {

                    Row row2 = sheet.createRow(rowIdx.getAndIncrement());
                    row2.createCell(1).setCellValue(column.getName());
                    row2.createCell(2).setCellValue(column.getDatatype());
                    row2.createCell(3).setCellValue(column.getDefaultValue());
                    row2.createCell(4).setCellValue(column.getKeytype());
                    if (column.getKeytype() != null && !column.getKeytype().isEmpty()) {
                        if (column.getKeytype().equalsIgnoreCase("FK")) {
                            row2.createCell(5).setCellValue(column.getTableReference());
                        } else {
                            row2.createCell(5).setCellValue("");
                        }
                    }
                    if (column.getIsNull() != null && !column.getIsNull().isEmpty()) {

                        if (column.getIsNull().equals("true")) {
                            row2.createCell(6).setCellValue("null");
                        } else {
                            row2.createCell(6).setCellValue("not null");
                        }
                    }


                    if (column.getIsAutoIncrement() != null && !column.getIsAutoIncrement().isEmpty()) {
                        if (column.getIsAutoIncrement().equals("true")) {
                            row2.createCell(7).setCellValue("yes");

                        } else {
                            row2.createCell(7).setCellValue("");

                        }
                    }


                    row2.createCell(8).setCellValue(column.getSampleData());
                    row2.createCell(9).setCellValue(column.getColumnComments());
                    CellStyle cellStyle2 = workbook.createCellStyle();
                    cellStyle2.setAlignment(HorizontalAlignment.GENERAL);
                    cellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
                    cellStyle2.getWrapText();

                    Cell cell = row2.createCell(10);

                    cell.setCellValue(table.getTableComments());
                    cell.setCellStyle(cellStyle2);

                    Cell cell1 = row2.createCell(0);

                    cell1.setCellValue(table.getName());
                    cell1.setCellStyle(cellStyle2);
                }
                 sheet.createRow(rowIdx.getAndIncrement());
                g = rowIdx.getAndIncrement();

            }

            workbook.write(out);

        } catch (IOException e) {
            logger.info("******DbDesignExcelGenerator*****",e);
        }
        return new ByteArrayResource(out.toByteArray());

    }



    public static ByteArrayResource queryGenerator(List<Table> tables, DbDesignTables dbDesignTables) throws CustomValidationException {
        StringBuilder sqlBuilder = new StringBuilder();
        String keyType = null;
        String columnName = null;
        ByteArrayResource textResource = null;
        String tableName = null;
        for (int i = 0; i < tables.size(); i++) {
            tableName = tables.get(i).getName();
            sqlBuilder.append(String.format(Constant.STRING_FORMAT, tableName));

            sqlBuilder.append(String.format(Constant.CREATE_TABLE_FORMAT, tableName));
            for (int j = 0; j < tables.get(i).getColumns( ).size( ); j++) {
                columnName = tables.get(i).getColumns( ).get(j).getName( );
                String columnType = tables.get(i).getColumns( ).get(j).getDatatype( );
                keyType = tables.get(i).getColumns( ).get(j).getKeytype( );
                String tableReference = tables.get(i).getColumns( ).get(j).getTableReference( );
                String defaultValue = tables.get(i).getColumns( ).get(j).getDefaultValue( );
                String tableReferenceId = tables.get(i).getColumns( ).get(j).getTableReferenceId( );
                String isAutoIncrement = tables.get(i).getColumns( ).get(j).getIsAutoIncrement( );
                sqlBuilder.append(String.format(Constant.COLUMN_FORMAT, columnName, columnType));

                if (keyType != null && !keyType.isEmpty() && keyType.equals("PK")) {
                        if (isAutoIncrement != null && !isAutoIncrement.isEmpty()) {
                            if (isAutoIncrement.equals("true")) {
                                sqlBuilder.append(String.format("  %s", Constant.PRIMARY_KEY));
                                sqlBuilder.append(String.format("  %s", Constant.AUTO_INCREMENT));
                            } else {
                                sqlBuilder.append(String.format("  %s", ""));
                            }
                        } else {
                            throw new CustomValidationException(ErrorCode.D2D_4);
                        }


                if (defaultValue != null && !defaultValue.isEmpty() && columnType != null && !columnType.isEmpty()) {
                        if (columnType.startsWith("INT") || columnType.startsWith("SMALLINT") || columnType.startsWith("MEDIUMINT")
                                || columnType.startsWith("BIGINT") || columnType.startsWith("TINYINT") || columnType.startsWith("BIT")) {
                            sqlBuilder.append(String.format(Constant.DEFAULT_FORMAT, defaultValue));
                        } else {
                            sqlBuilder.append(String.format(Constant.DEFAULT_FORMAT, "'" + defaultValue + "'"));
                        }
                    }

                if (keyType != null && !keyType.isEmpty() && keyType.equals("FK")) {
                        if (tableReference != null && tableReferenceId != null && !tableReference.isEmpty() && !tableReferenceId.isEmpty()) {
                            sqlBuilder.append(",");
                            sqlBuilder.append(String.format(" %s", "CONSTRAINT" + ' ' + columnName + ' ' + "FOREIGN KEY" + ' ' + "(" + columnName + ")" + ' ' + "REFERENCES" + ' ' + tableReference + "(" + tableReferenceId + ")"));

                        } else {
                            throw new CustomValidationException(ErrorCode.D2D_5);

                        }

                    }
                }

                if (j < tables.get(i).getColumns( ).size( ) - 1) {
                    sqlBuilder.append(Constant.COMMA_NEWLINE);
                }

            }

            sqlBuilder.append(");\n");

            sqlBuilder.append("\n");

            textResource = new ByteArrayResource(sqlBuilder.toString().getBytes());


        }
        connectionEstablished(sqlBuilder, dbDesignTables, tableName);

        return textResource;
    }


    private static void findDuplicateTableName(List<Table> tables) throws CustomValidationException {
        Set<String> duplicateTableNames = new HashSet<>();
        Set<String> uniqueTableNames = new HashSet<>();
        tables.forEach(table -> {
            String tableName = table.getName();
            if (!uniqueTableNames.add(tableName)) {
                duplicateTableNames.add(tableName);
            }
        });
        if (!duplicateTableNames.isEmpty()) {
            throw new CustomValidationException(ErrorCode.D2D_8);
        }
    }

    private static void validateColumns(List<Column> columnsList) throws CustomValidationException {
        Set<String> duplicateColumnNames = columnsList.stream()
                .map(Column::getName)
                .collect(Collectors.toSet());

        if (duplicateColumnNames.size() != columnsList.size()) {
            throw new CustomValidationException(ErrorCode.D2D_9);
        }

        Set<String> duplicatePrimaryKeys = columnsList.stream()
                .filter(column -> "PK".equalsIgnoreCase(column.getKeytype()))
                .map(Column::getKeytype)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (!duplicatePrimaryKeys.isEmpty()) {
            throw new CustomValidationException(ErrorCode.D2D_10);
        }
    }

    public static ByteArrayResource postGreSQLQueryGenerator(List<Table> tables, DbDesignTables dbDesignTables) throws CustomValidationException {
        StringBuilder sqlBuilder = new StringBuilder();
        ByteArrayResource textResource = null;
        String tableName = null;
        for (int i = 0; i < tables.size(); i++) {
            tableName = tables.get(i).getName();
            sqlBuilder.append(String.format(Constant.STRING_FORMAT, tableName));

            sqlBuilder.append(String.format(Constant.CREATE_TABLE_IF_NOT_EXISTS, tableName));
            for (int j = 0; j < tables.get(i).getColumns( ).size( ); j++) {
                Column column = tables.get(i).getColumns( ).get(j);
                String columnName = column.getName( );
                String columnType = column.getDatatype( );
                String keyType = column.getKeytype( );
                String tableReference = column.getTableReference( );
                String defaultValue = column.getDefaultValue( );
                String tableReferenceId = column.getTableReferenceId( );
                String isAutoIncrement = column.getIsAutoIncrement( );
                String[] dataTypeSplit = column.getDatatype( ).split("\\(");

                boolean isValid = isColumnTypeValid(columnType);
                if (isValid) {
                    sqlBuilder.append(String.format(Constant.COLUMN_FORMAT, columnName, column.getDatatype( )));
                } else {
                    sqlBuilder.append(String.format(Constant.COLUMN_FORMAT, columnName, dataTypeSplit[0]));
                }

                if (keyType != null && !keyType.isEmpty() && (keyType.equals("PK"))) {
                        if (isAutoIncrement != null && !isAutoIncrement.isEmpty()) {
                            if (isAutoIncrement.equals("true")) {
                                if (columnType.toLowerCase().startsWith("int2") || columnType.toLowerCase().startsWith("smallint") || columnType.toLowerCase().startsWith("smallserial")) {
                                    int lastDatatypeIndex = sqlBuilder.lastIndexOf(" ");
                                    if (lastDatatypeIndex != -1) {
                                        sqlBuilder.setLength(lastDatatypeIndex);
                                    }
                                    sqlBuilder.append(" smallserial");
                                } else if (columnType.toLowerCase().startsWith("int4") || columnType.toLowerCase().startsWith("int") || columnType.toLowerCase().startsWith("integer") || columnType.toLowerCase().startsWith("serial")) {
                                    int lastDatatypeIndex = sqlBuilder.lastIndexOf(" ");
                                    if (lastDatatypeIndex != -1) {
                                        sqlBuilder.setLength(lastDatatypeIndex);
                                    }
                                    sqlBuilder.append(" serial");
                                } else if (columnType.toLowerCase().startsWith("int8") || columnType.toLowerCase().startsWith("bigint") || columnType.toLowerCase().startsWith("bigserial")) {
                                    int lastDatatypeIndex = sqlBuilder.lastIndexOf(" ");
                                    if (lastDatatypeIndex != -1) {
                                        sqlBuilder.setLength(lastDatatypeIndex);
                                    }
                                    sqlBuilder.append(" bigserial");
                                }
                            }
                        } else {
                            throw new CustomValidationException(ErrorCode.D2D_4);
                        }
                        sqlBuilder.append(Constant.PRIMARY_KEY);
                    }


                if (defaultValue != null && !defaultValue.isEmpty()) {
                    boolean isValidDefaultValue = isColumnTypeValidDefaultValue(columnType);
                    if (isValidDefaultValue) {
                        sqlBuilder.append(String.format(Constant.DEFAULT, defaultValue));
                    } else {
                        sqlBuilder.append(String.format(Constant.DEFAULT_FORMAT, defaultValue));
                    }
                }

                if (keyType != null && !keyType.isEmpty() && keyType.equals("FK")) {
                        if (tableReference != null && tableReferenceId != null && !tableReference.isEmpty( ) && !tableReferenceId.isEmpty( )) {
                            sqlBuilder.append(String.format(Constant.COMMA_NEWLINE));
                            sqlBuilder.append(String.format(Constant.CONSTRAINT_FK_FORMAT, columnName, columnName, tableReference, tableReferenceId));
                        } else {
                            throw new CustomValidationException(ErrorCode.D2D_5);
                        }
                    }


                if (j < tables.get(i).getColumns( ).size( ) - 1) {
                    sqlBuilder.append(Constant.COMMA_NEWLINE);
                }
            }

            sqlBuilder.append(Constant.END_OF_STATEMENT);
            sqlBuilder.append(sequenceMsSqlAndPostgreSQL(tableName));
        }
        connectionEstablished(sqlBuilder, dbDesignTables, tableName);

        textResource = new ByteArrayResource(sqlBuilder.toString().getBytes());
        return textResource;
    }


    public static ByteArrayResource oracleQueryGenerator(List<Table> tables, DbDesignTables dbDesignTables) throws CustomValidationException {
        StringBuilder sqlBuilder = new StringBuilder();
        ByteArrayResource textResource = null;
        String tableName = null;
        for (int i = 0; i < tables.size(); i++) {
            Table table = tables.get(i);
            tableName = table.getName();


            sqlBuilder.append(String.format(Constant.CREATE_TABLE_FORMAT, tableName));
            List<Column> columns = table.getColumns( );
            for (int j = 0; j < columns.size( ); j++) {
                Column column = columns.get(j);
                String columnName = column.getName();
                String columnType = column.getDatatype();
                String keyType = column.getKeytype();
                String tableReference = column.getTableReference();
                String defaultValue = column.getDefaultValue();
                String tableReferenceId = column.getTableReferenceId();
                String isAutoIncrement = column.getIsAutoIncrement();

                sqlBuilder.append(String.format(Constant.COLUMN_FORMAT, columnName, columnType));

                if (keyType != null && !keyType.isEmpty() && keyType.equals("PK")) {
                        if (isAutoIncrement != null && !isAutoIncrement.isEmpty()) {
                            if (isAutoIncrement.equals("true")) {
                                sqlBuilder.append(" GENERATED BY DEFAULT AS IDENTITY");
                            }
                        } else {
                            throw new CustomValidationException(ErrorCode.D2D_4);
                        }
                        sqlBuilder.append(Constant.PRIMARY_KEY);
                    }


                if (defaultValue != null && !defaultValue.isEmpty()) {
                    if (columnType.startsWith("VARCHAR") || columnType.startsWith("VARCHAR2") ||
                            columnType.startsWith("CHAR") || columnType.startsWith("CHAR VARYING") ||
                            columnType.startsWith("CHARACTER") || columnType.startsWith("CHARACTER VARYING") ||
                            columnType.startsWith("NATIONAL CHAR") || columnType.startsWith("NATIONAL CHAR VARYING") ||
                            columnType.startsWith("NATIONAL CHARACTER") || columnType.startsWith("NATIONAL CHARACTER VARYING") ||
                            columnType.startsWith("NCHAR") || columnType.startsWith("NCHAR VARYING") ||
                            columnType.startsWith("NVARCHAR2") || columnType.startsWith("NCLOB")) {
                        sqlBuilder.append(String.format(Constant.DEFAULT, defaultValue));
                    } else {
                        sqlBuilder.append(String.format(Constant.DEFAULT_FORMAT, defaultValue));
                    }
                }

                if (keyType != null && !keyType.isEmpty() && keyType.equals("FK")) {
                        if (tableReference != null && tableReferenceId != null && !tableReference.isEmpty( ) && !tableReferenceId.isEmpty( )) {
                            sqlBuilder.append(Constant.COMMA_NEWLINE);
                            sqlBuilder.append(String.format(Constant.CONSTRAINT_FK_FORMAT, columnName, columnName, tableReference, tableReferenceId));
                        } else {
                            throw new CustomValidationException(ErrorCode.D2D_5);
                        }
                    }

                if (j < columns.size( ) - 1) {
                    sqlBuilder.append(Constant.COMMA_NEWLINE);
                }
            }

            sqlBuilder.append(Constant.END_OF_STATEMENT);
            sqlBuilder.append(sequenceOracle(tableName));
        }

        connectionEstablished(sqlBuilder, dbDesignTables, tableName);
        textResource = new ByteArrayResource(sqlBuilder.toString().getBytes());
        return textResource;
    }
    public static StringBuilder sequenceOracle(String tableName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("/* %s */%n%n", tableName.toUpperCase()+" SEQUENCE "));
        stringBuilder.append("CREATE SEQUENCE ").append(tableName.toUpperCase()).append("_").append("SEQ").append(" START WITH 1 INCREMENT BY 1;").append("\n\n");
        return stringBuilder;
    }

    public static ByteArrayResource msSqlQueryGenerator(List<Table> tables, DbDesignTables dbDesignTables) throws CustomValidationException {
        StringBuilder sqlBuilder = new StringBuilder();
        ByteArrayResource textResource = null;
        String tableName = null;
        for (int i = 0; i < tables.size(); i++) {
            Table table = tables.get(i);
            tableName = table.getName();
            sqlBuilder.append(String.format(Constant.STRING_FORMAT, tableName));

            sqlBuilder.append(String.format(Constant.CREATE_TABLE_FORMAT, tableName));
            List<Column> columns = table.getColumns( );
            for (int j = 0; j < columns.size( ); j++) {
                Column column = columns.get(j);
                String columnName = column.getName();
                String columnType = column.getDatatype();
                String keyType = column.getKeytype();
                String tableReference = column.getTableReference();
                String defaultValue = column.getDefaultValue();
                String tableReferenceId = column.getTableReferenceId();
                String isAutoIncrement = column.getIsAutoIncrement();

                sqlBuilder.append(String.format(Constant.COLUMN_FORMAT, columnName, columnType));

                if (keyType != null && !keyType.isEmpty() && keyType.equals("PK")) {
                    if (isAutoIncrement != null && !isAutoIncrement.isEmpty()) {
                        if (isAutoIncrement.equals("true")) {
                            sqlBuilder.append(" IDENTITY(1,1)");
                        }
                    } else {
                        throw new CustomValidationException(ErrorCode.D2D_4);
                    }
                    sqlBuilder.append(Constant.PRIMARY_KEY);
                }

                if (defaultValue != null && !defaultValue.isEmpty()) {
                    if (columnType.startsWith("varchar") || columnType.startsWith("nvarchar") ||
                            columnType.startsWith("char") || columnType.startsWith("nchar")) {
                        sqlBuilder.append(String.format(Constant.DEFAULT, defaultValue));
                    } else {
                        sqlBuilder.append(String.format(Constant.DEFAULT_FORMAT, defaultValue));
                    }
                }

                if (keyType != null && !keyType.isEmpty() && keyType.equals("FK")) {
                        if (tableReference != null && tableReferenceId != null && !tableReference.isEmpty( ) && !tableReferenceId.isEmpty( )) {
                            sqlBuilder.append(Constant.COMMA_NEWLINE);
                            sqlBuilder.append(String.format(Constant.CONSTRAINT_FK_FORMAT, columnName, columnName, tableReference, tableReferenceId));
                        } else {
                            throw new CustomValidationException(ErrorCode.D2D_5);
                        }
                    }

                if (j < columns.size( ) - 1) {
                    sqlBuilder.append(Constant.COMMA_NEWLINE);
                }
            }

            sqlBuilder.append("\n)\n\n");
            sqlBuilder.append(sequenceMsSqlAndPostgreSQL(tableName));
        }
        connectionEstablished(sqlBuilder, dbDesignTables, tableName);

        textResource = new ByteArrayResource(sqlBuilder.toString().getBytes());
        return textResource;
    }

    public static StringBuilder sequenceMsSqlAndPostgreSQL(String tableName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("/* %s */%n%n", tableName.toUpperCase()+" SEQUENCE "));
        stringBuilder.append("CREATE SEQUENCE ").append(tableName.toUpperCase()).append("_").append("SEQ").append(" START WITH 1 INCREMENT BY 1 ").append(" MINVALUE 1 MAXVALUE 1000 CYCLE;").append("\n\n");
        return stringBuilder;
    }
    public static void connectionEstablished(StringBuilder sqlBuilder, DbDesignTables dbDesignTables, String tableName) {
        String url1 = null;
        Statement stmt = null;
        String query = null;
        String createTableSQL = null;

        List<StringBuilder> stringBuilders = new ArrayList<>();
        stringBuilders.add(sqlBuilder);

        for (StringBuilder stringBuilder : stringBuilders) {
            createTableSQL = String.valueOf(stringBuilder);
        }

        if (dbDesignTables.getDestination( ) != null) {
            if (dbDesignTables.getDestination( ).getDatabaseEngines( ).equalsIgnoreCase("Oracle")) {
                url1 = "jdbc:oracle:thin:@//" + dbDesignTables.getDestination( ).getHostAddress( ) + ":" + dbDesignTables.getDestination( ).getPort( ) + "/XE" + Constant.URL;
            } else if (dbDesignTables.getDestination( ).getDatabaseEngines( ).equalsIgnoreCase("PostgreSQL")) {
                url1 = "jdbc:postgresql://" + dbDesignTables.getDestination( ).getHostAddress( ) + ":" + dbDesignTables.getDestination( ).getPort( ) + "/" + Constant.URL;
            } else if (dbDesignTables.getDestination( ).getDatabaseEngines( ).equalsIgnoreCase(" MicrosoftSQL")) {
                url1 = "dbc:sqlserver://" + dbDesignTables.getDestination( ).getHostAddress( ) + ":" + dbDesignTables.getDestination( ).getPort( ) + "/" + Constant.URL;
            } else if (dbDesignTables.getDestination( ).getDatabaseEngines( ).equalsIgnoreCase(" MicrosoftSQL")) {
                url1 = "jdbc:mysql://" + dbDesignTables.getDestination( ).getHostAddress( ) + ":" + dbDesignTables.getDestination( ).getPort( ) + "/" + dbDesignTables.getDestination( ).getDbName( ) + Constant.URL;
            }
        }
        try {
            query = createTableSQL.replace("\n", "");
            if (dbDesignTables.getDestination() != null) {
               try(Connection conn = DriverManager.getConnection(url1, dbDesignTables.getDestination().getUsername(), dbDesignTables.getDestination().getPassword())){
                   if (conn != null) {
                       stmt = conn.createStatement();
                   }
               }catch (Exception e){
                   logger.info("******ConnectionEstablished******",e);
               }
            }

            String[] arrOfStr = query.split(";");
            for (String querys : arrOfStr) {

                if (stmt != null) {
                    String query2 = "SELECT * FROM " + tableName + "";
                    ResultSet resultSet = stmt.executeQuery(query2);
                    while (resultSet.next()) {
                        resultSet.getInt("student_id");
                        resultSet.getString("first_name");
                        resultSet.getString("last_name");
                        resultSet.getInt("age");
                        resultSet.getString("major");
                    }
                    stmt.addBatch(querys);
                    stmt.executeBatch();
                }
            }

            logger.info("Table created successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}



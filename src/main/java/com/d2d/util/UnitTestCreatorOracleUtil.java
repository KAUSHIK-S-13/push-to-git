package com.d2d.util;

import com.d2d.constant.Constant;
import com.d2d.dto.Column;
import com.d2d.dto.CrudDTO;
import com.d2d.dto.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Component
public class UnitTestCreatorOracleUtil {
    @Autowired
    CommonUtil commonUtil;


    public String mockitoClassCreation(String tableClassName) {
        StringBuilder serviceImplClassCreation = new StringBuilder();
        serviceImplClassCreation.append("\t@InjectMocks\n");
        serviceImplClassCreation.append("\t").append(tableClassName).append("ServiceImpl").append(" ").append(commonUtil.toCamelCase(tableClassName)).append(Constant.SERVICE_NAME).append(";\n");
        serviceImplClassCreation.append("\t@Mock\n");

        serviceImplClassCreation.append("\t").append(tableClassName).append("Repository").append(" ").append(commonUtil.toCamelCase(tableClassName)).append(Constant.REPOSITORY_SMALL).append(";\n");

        return serviceImplClassCreation.toString();
    }

    public String commanModel() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\t").append("@Mock\n");
        stringBuilder.append("\t").append("private ModelMapper modelMapper;\n");
        return stringBuilder.toString();
    }

    public String uniTestImportsCreation(String packageName, String tableClassName1, CrudDTO crudDTO) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constant.IMPORT).append(packageName).append(".").append(Constant.REPOSITORY_SMALL).append(".").append(tableClassName1).append("Repository;\n");
        stringBuilder.append(Constant.IMPORT).append(packageName).append(".").append("serviceimpl").append(".").append(tableClassName1).append("ServiceImpl;\n");

        stringBuilder.append(Constant.IMPORT).append(packageName).append(".").append("dto.").append(commonUtil.capitalize(tableClassName1)).append("DTO;").append("\n");
        stringBuilder.append(Constant.IMPORT).append(packageName).append(".").append("model.").append(commonUtil.capitalize(tableClassName1)).append(";\n");


        if (crudDTO.getCreate()) {
            stringBuilder.append(Constant.IMPORT).append(packageName).append(".").append("dto.").append(commonUtil.capitalize(tableClassName1)).append("RequestDTO;\n");
        }
        return stringBuilder.toString();
    }

    public String commanImport() {

        StringBuilder commanImports = new StringBuilder();
        commanImports.append("import org.modelmapper.ModelMapper;\n");
        commanImports.append("import static org.junit.jupiter.api.Assertions.assertNotNull;\n");
        commanImports.append("import static org.mockito.Mockito.when;\n");
        commanImports.append("import java.util.Optional;\n");
        commanImports.append("import static org.mockito.ArgumentMatchers.any;\n");
        commanImports.append("import java.util.ArrayList;\n");
        commanImports.append("import java.util.List;\n");
        commanImports.append("import java.sql.Timestamp;\n");
        return commanImports.toString();
    }

    public String curdUnitTestCaseCreation(Table table, String tableClassName1, List<Column> columns) {

        String types = null;
        String values = null;
        for (Column column : columns) {

            if (column.getDatatype().equalsIgnoreCase("NUMBER") || column.getDatatype().equalsIgnoreCase("SMALLINT") ||
                    column.getDatatype().equalsIgnoreCase("TINYINT") || column.getDatatype().equalsIgnoreCase("INT") ||
                    column.getDatatype().equalsIgnoreCase("INTEGER") || column.getDatatype().equalsIgnoreCase("NUMERIC")) {
                types = "int";
                values = "1";
            } else if (column.getDatatype().equalsIgnoreCase("LONG") || column.getDatatype().equalsIgnoreCase("LONG RAW")) {
                types = "long";
                values = "1L";
            } else if (column.getDatatype().equalsIgnoreCase("SHORT")) {
                types = "short";
                values = "1";
            } else if (column.getDatatype().equalsIgnoreCase("VARCHAR") || column.getDatatype().equalsIgnoreCase("VARCHAR2") || column.getDatatype().equalsIgnoreCase("NVARCHAR2")) {
                types = "String";
                values = "";
            } else if (column.getDatatype().equalsIgnoreCase("DOUBLE")) {
                types = "double";
                values = "0.0";
            } else if (column.getDatatype().equalsIgnoreCase("Boolean")) {
                types = "boolean";
                values = "false";
            } else if (column.getDatatype().equalsIgnoreCase("TINYINT")) {
                types = "boolean";
                values = String.valueOf(Boolean.valueOf("false"));
            }

        }

        StringBuilder curdCreationOfUnitTest = new StringBuilder();
        if (table.getCrudDTO().getSingleRecord()) {
            curdCreationOfUnitTest.append(Constant.TEST);
            curdCreationOfUnitTest.append(Constant.GET).append(commonUtil.capitalize(tableClassName1)).append("ById").append(Constant.FUNCTION_NEWLINE);
            curdCreationOfUnitTest.append("\t\t").append(Constant.WHEN).append(commonUtil.toCamelCase(tableClassName1)).append(Constant.REPOSITORY_SMALL).append(Constant.OPTIONAL_NULLABLE).append("(").append(commonUtil.toCamelCase(tableClassName1)).append(Constant.LIST_DATA);
            curdCreationOfUnitTest.append("\t\twhen(modelMapper.map(any( ), any( ))).thenReturn(").append(commonUtil.capitalize(tableClassName1)).append("MasterDto( ));\n");
            curdCreationOfUnitTest.append("\t\t").append(types).append(" id").append(" = ").append(values).append(";\n");
            for (Column column : columns) {
                if (column.getKeytype() != null && column.getKeytype().equalsIgnoreCase("FK")) {
                    curdCreationOfUnitTest.append("\t\t").append(Constant.WHEN).append(commonUtil.data(column.getTableReference())).append(Constant.REPOSITORY_SMALL).append(Constant.OPTIONAL_NULLABLE).append("(").append(commonUtil.data(column.getTableReference())).append(Constant.LIST_DATA);
                }
            }
            curdCreationOfUnitTest.append("\t\t").append(Constant.ASSERT_NOTNULL).append(commonUtil.toCamelCase(tableClassName1)).append(Constant.SERVICE_NAME).append(".").append("getById(id));}\n\n");


        }
        if (table.getCrudDTO().getDelete().getIsChecked()) {
            curdCreationOfUnitTest.append(Constant.TEST);
            curdCreationOfUnitTest.append(Constant.GET).append(commonUtil.capitalize(tableClassName1)).append("DeletedById").append(Constant.FUNCTION_NEWLINE);
            curdCreationOfUnitTest.append("\t\t").append(Constant.WHEN).append(commonUtil.toCamelCase(tableClassName1)).append(Constant.REPOSITORY_SMALL).append(Constant.OPTIONAL_NULLABLE).append("(").append(commonUtil.toCamelCase(tableClassName1)).append(Constant.LIST_DATA);
            curdCreationOfUnitTest.append("\t\t").append(types).append(" id").append(" = ").append(values).append(";\n");
            for (Column column : columns) {
                if (column.getKeytype() != null && column.getKeytype().equalsIgnoreCase("FK")) {
                    curdCreationOfUnitTest.append("\t\t").append(Constant.WHEN).append(commonUtil.data(column.getTableReference())).append(Constant.REPOSITORY_SMALL).append(Constant.OPTIONAL_NULLABLE).append("(").append(commonUtil.data(column.getTableReference())).append(Constant.LIST_DATA);
                }
            }
            curdCreationOfUnitTest.append("\t\t").append(Constant.ASSERT_NOTNULL).append(commonUtil.toCamelCase(tableClassName1)).append(Constant.SERVICE_NAME).append(".").append("deleteById(id));}\n\n");

        }

        if (table.getCrudDTO().getCreate()) {
            curdCreationOfUnitTest.append(Constant.TEST);
            curdCreationOfUnitTest.append(Constant.GET).append(commonUtil.capitalize(tableClassName1)).append("SaveData").append(Constant.FUNCTION_NEWLINE);
            curdCreationOfUnitTest.append("\t\t").append(Constant.WHEN).append(commonUtil.toCamelCase(tableClassName1)).append(Constant.REPOSITORY_SMALL).append(Constant.OPTIONAL_NULLABLE).append("(").append(commonUtil.toCamelCase(tableClassName1)).append(Constant.LIST_DATA);
            curdCreationOfUnitTest.append("\t\t").append(Constant.WHEN).append(commonUtil.toCamelCase(tableClassName1)).append(Constant.REPOSITORY_SMALL).append(".save(any( ))).thenReturn((").append(commonUtil.toCamelCase(tableClassName1)).append(Constant.LIST_DATA);
            for (Column column : columns) {
                if (column.getKeytype() != null && column.getKeytype().equalsIgnoreCase("FK")) {
                    curdCreationOfUnitTest.append("\t\t").append(Constant.WHEN).append(commonUtil.data(column.getTableReference())).append(Constant.REPOSITORY_SMALL).append(Constant.OPTIONAL_NULLABLE).append("(").append(commonUtil.data(column.getTableReference())).append(Constant.LIST_DATA);
                }
            }
            curdCreationOfUnitTest.append("\t\t").append(Constant.ASSERT_NOTNULL).append(commonUtil.toCamelCase(tableClassName1)).append(Constant.SERVICE_NAME).append(".").append("save(").append(commonUtil.toCamelCase(tableClassName1)).append("RequestDTO()").append("));\n");
            curdCreationOfUnitTest.append(Constant.CLOSE);
        }


        if (table.getCrudDTO().getMultipleRecord().getIsChecked() && !table.getCrudDTO().getMultipleRecord().getPagination() && !table.getCrudDTO().getMultipleRecord().getIsSearch()) {
            curdCreationOfUnitTest.append(Constant.TEST);
            curdCreationOfUnitTest.append(Constant.GET).append(commonUtil.capitalize(tableClassName1)).append("AllListOfDataS").append(Constant.FUNCTION_NEWLINE);
            curdCreationOfUnitTest.append("\t\t").append(Constant.WHEN).append(commonUtil.toCamelCase(tableClassName1)).append(Constant.REPOSITORY_SMALL).append(".findAll()).thenReturn((").append("get").append(commonUtil.capitalize(tableClassName1)).append("AllListOfData()));\n");
            for (Column column : columns) {
                if (column.getKeytype() != null && column.getKeytype().equalsIgnoreCase("FK")) {
                    curdCreationOfUnitTest.append("\t\t").append(Constant.WHEN).append(commonUtil.data(column.getTableReference())).append(Constant.REPOSITORY_SMALL).append(Constant.OPTIONAL_NULLABLE).append("(").append(commonUtil.data(column.getTableReference())).append(Constant.LIST_DATA);
                }
            }
            curdCreationOfUnitTest.append("\t\t").append(Constant.ASSERT_NOTNULL).append(commonUtil.toCamelCase(tableClassName1)).append(Constant.SERVICE_NAME).append(".").append("getAllList(").append("));\n");
            curdCreationOfUnitTest.append(Constant.CLOSE);
        }

        return curdCreationOfUnitTest.toString();
    }

    public String curdUnitTestCommonData(Table table, String tableClassName1, List<Column> columns) {

        StringBuilder curdCreationOfUnitTest = new StringBuilder();

        curdCreationOfUnitTest.append("\tpublic ").append(commonUtil.capitalize(tableClassName1)).append("DTO ").append(commonUtil.capitalize(tableClassName1)).append("MasterDto(){\n");
        curdCreationOfUnitTest.append(Constant.T_LIST).append(commonUtil.capitalize(tableClassName1)).append("DTO").append("> ").append(commonUtil.toCamelCase(tableClassName1)).append("DtoList ").append("= ").append("new ").append(Constant.ARRAY_LIST).append("\n");
        curdCreationOfUnitTest.append("\t\t").append(commonUtil.capitalize(tableClassName1)).append("DTO ").append(commonUtil.toCamelCase(tableClassName1)).append("DTO ").append(" = ").append("new ").append(commonUtil.capitalize(tableClassName1)).append("DTO();").append("\n");
        for (Column column : columns) {
            String values = "";
            if (column.getKeytype() == null || column.getKeytype().equalsIgnoreCase("PK")) {
                if (column.getDatatype().equalsIgnoreCase("NUMBER")  || column.getDatatype().equalsIgnoreCase("SMALLINT") ||
                        column.getDatatype().equalsIgnoreCase("TINYINT")|| column.getDatatype().equalsIgnoreCase("INT")||
                        column.getDatatype().equalsIgnoreCase("INTEGER") || column.getDatatype().equalsIgnoreCase("NUMERIC")) {
                    values = "1";
                } else if (column.getDatatype().equalsIgnoreCase("LONG")
                        || column.getDatatype().equalsIgnoreCase("NUMBER")) {
                    values = "1L";
                } else if (column.getDatatype().equalsIgnoreCase("SHORT")) {
                    values = "1";
                } else if (column.getDatatype().equalsIgnoreCase("VARCHAR")||column.getDatatype().equalsIgnoreCase("VARCHAR2") ||column.getDatatype().equalsIgnoreCase("NVARCHAR2")) {
                    values = "";
                } else if (column.getDatatype().equalsIgnoreCase("DOUBLE")) {
                    values = "0.0";
                } else if (column.getDatatype().equalsIgnoreCase("Boolean")) {
                    values = "false";
                } else if (column.getDatatype().equalsIgnoreCase("TIMESTAMP")) {
                    Date date = new Date();
                    Timestamp ts = new Timestamp(date.getTime());
                    values = String.valueOf(ts);
                } else if (column.getDatatype().startsWith("TINYINT")) {
                    values = String.valueOf(Boolean.valueOf("false"));
                }

                if (column.getDatatype().equalsIgnoreCase("INTEGER")||column.getDatatype().equalsIgnoreCase(Constant.SMALLINT) ||column.getDatatype().equalsIgnoreCase("NUMBER") ||column.getDatatype().equalsIgnoreCase("NUMERIC") || column.getDatatype().startsWith("TINYINT") || column.getDatatype().equalsIgnoreCase("int") || column.getDatatype().equalsIgnoreCase("Long") || column.getDatatype().equalsIgnoreCase(Constant.SHORT_SMALL) || column.getDatatype().equalsIgnoreCase("Boolean")) {
                    curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(tableClassName1)).append("DTO").append(".").append("set").append(commonUtil.CapitalizeClassName(column.getName())).append("(").append(values).append(");\n");

                } else if (column.getDatatype().equalsIgnoreCase("TIMESTAMP")) {
                    curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(tableClassName1)).append("DTO").append(".").append("set").append(commonUtil.CapitalizeClassName(column.getName())).append("(Timestamp.valueOf(").append('"' + values + '"').append("));\n");
                } else {
                    curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(tableClassName1)).append("DTO").append(".").append("set").append(commonUtil.CapitalizeClassName(column.getName())).append("(").append('"').append('"').append(");\n");

                }
            }
        }
        for (Column column : columns) {
            String values = "";
            if (column.getKeytype() != null && column.getKeytype().equalsIgnoreCase("FK")) {
                if (column.getTableReference().contains("_")) {
                    curdCreationOfUnitTest.append("\t\t").append(commonUtil.removeUnderScore(column.getTableReference())).append("DTO").append(" ").append(commonUtil.toCamelCase(column.getTableReference())).append(" = ").append("new ").append(commonUtil.removeUnderScore(column.getTableReference())).append("DTO").append("( );").append("\n");

                } else {
                    curdCreationOfUnitTest.append("\t\t").append(commonUtil.capitalize(column.getTableReference())).append("DTO").append(" ").append(commonUtil.toCamelCase(column.getTableReference())).append(" = ").append("new ").append(commonUtil.capitalize(column.getTableReference())).append("DTO").append("( );").append("\n");
                }

                if (column.getDatatype().equalsIgnoreCase("NUMBER")  || column.getDatatype().equalsIgnoreCase("SMALLINT") ||
                        column.getDatatype().equalsIgnoreCase("TINYINT")|| column.getDatatype().equalsIgnoreCase("INT")||
                        column.getDatatype().equalsIgnoreCase("INTEGER") || column.getDatatype().equalsIgnoreCase("NUMERIC")) {
                    values = "1";
                } else if (column.getDatatype().equalsIgnoreCase("LONG")
                        || column.getDatatype().equalsIgnoreCase("NUMBER")) {
                    values = "1L";
                } else if (column.getDatatype().equalsIgnoreCase("SHORT")) {
                    values = "1";
                } else if (column.getDatatype().equalsIgnoreCase("VARCHAR")||column.getDatatype().equalsIgnoreCase("VARCHAR2") ||column.getDatatype().equalsIgnoreCase("NVARCHAR2")) {
                    values = "";
                } else if (column.getDatatype().equalsIgnoreCase("DOUBLE")) {
                    values = "0.0";
                } else if (column.getDatatype().equalsIgnoreCase("Boolean")) {
                    values = "false";
                } else if (column.getDatatype().equalsIgnoreCase("TIMESTAMP")) {
                    Date date = new Date();
                    Timestamp ts = new Timestamp(date.getTime());
                    values = String.valueOf(ts);
                } else if (column.getDatatype().startsWith("TINYINT")) {
                    values = String.valueOf(Boolean.valueOf("false"));
                }
                if (column.getDatatype().equalsIgnoreCase("INTEGER")||column.getDatatype().equalsIgnoreCase(Constant.SMALLINT) ||column.getDatatype().equalsIgnoreCase("NUMBER") ||column.getDatatype().equalsIgnoreCase("NUMERIC") || column.getDatatype().startsWith("TINYINT") || column.getDatatype().equalsIgnoreCase("int") || column.getDatatype().equalsIgnoreCase("Long") || column.getDatatype().equalsIgnoreCase(Constant.SHORT_SMALL) || column.getDatatype().equalsIgnoreCase("Boolean")) {
                    curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(column.getTableReference())).append(".").append("set").append(commonUtil.CapitalizeClassName(column.getTableReferenceId())).append("(").append(values).append(");\n");

                } else if (column.getDatatype().equalsIgnoreCase("TIMESTAMP")) {
                    curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(column.getTableReference())).append("DTO").append(".").append("set").append(commonUtil.CapitalizeClassName(column.getTableReferenceId())).append("(Timestamp.valueOf(").append('"' + values + '"').append("));\n");
                } else {
                    curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(column.getTableReference())).append("DTO").append(".").append("set").append(commonUtil.CapitalizeClassName(column.getTableReferenceId())).append("(").append('"').append('"').append(");\n");

                }
                curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(tableClassName1)).append("DTO").append(".").append("set").append(commonUtil.CapitalizeClassName(column.getTableReference())).append("(").append(commonUtil.toCamelCase(column.getTableReference())).append(");\n");

            }

        }
        curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(tableClassName1)).append("DtoList").append(".").append("add(").append(commonUtil.toCamelCase(tableClassName1)).append("DTO").append(");\n");
        curdCreationOfUnitTest.append("\t\t").append(Constant.RETURN_SMALL).append(commonUtil.toCamelCase(tableClassName1)).append("DTO;").append("\n");
        curdCreationOfUnitTest.append(Constant.CLOSE);

        curdCreationOfUnitTest.append("\t\t").append("public ").append(commonUtil.capitalize(tableClassName1) + " ").append(commonUtil.toCamelCase(tableClassName1)).append("ListOfData()").append("{\n");
        curdCreationOfUnitTest.append("\t\t").append(commonUtil.capitalize(tableClassName1) + " ").append(commonUtil.toCamelCase(tableClassName1) + " ").append(" = ").append("new ").append(commonUtil.capitalize(tableClassName1)).append("( );\n");
        for (Column column : columns) {
            String values = "";
            if (column.getKeytype() == null || column.getKeytype().equalsIgnoreCase("PK")) {
                if (column.getDatatype().equalsIgnoreCase("NUMBER")  || column.getDatatype().equalsIgnoreCase("SMALLINT") ||
                        column.getDatatype().equalsIgnoreCase("TINYINT")|| column.getDatatype().equalsIgnoreCase("INT")||
                        column.getDatatype().equalsIgnoreCase("INTEGER") || column.getDatatype().equalsIgnoreCase("NUMERIC")) {
                    values = "1";
                } else if (column.getDatatype().equalsIgnoreCase("LONG")
                        || column.getDatatype().equalsIgnoreCase("NUMBER")) {
                    values = "1L";
                } else if (column.getDatatype().equalsIgnoreCase("SHORT")) {
                    values = "1";
                } else if (column.getDatatype().equalsIgnoreCase("VARCHAR")||column.getDatatype().equalsIgnoreCase("VARCHAR2") ||column.getDatatype().equalsIgnoreCase("NVARCHAR2")) {
                    values = "";
                } else if (column.getDatatype().equalsIgnoreCase("DOUBLE")) {
                    values = "0.0";
                } else if (column.getDatatype().equalsIgnoreCase("Boolean")) {
                    values = "false";
                } else if (column.getDatatype().equalsIgnoreCase("TIMESTAMP")) {
                    Date date = new Date();
                    Timestamp ts = new Timestamp(date.getTime());

                    values = String.valueOf(ts);
                } else if (column.getDatatype().startsWith("TINYINT")) {
                    values = String.valueOf(Boolean.valueOf("false"));
                }
                if (column.getDatatype().equalsIgnoreCase("INTEGER")||column.getDatatype().equalsIgnoreCase(Constant.SMALLINT) ||column.getDatatype().equalsIgnoreCase("NUMBER") ||column.getDatatype().equalsIgnoreCase("NUMERIC") || column.getDatatype().startsWith("TINYINT") || column.getDatatype().equalsIgnoreCase("int") || column.getDatatype().equalsIgnoreCase("Long") || column.getDatatype().equalsIgnoreCase(Constant.SHORT_SMALL) || column.getDatatype().equalsIgnoreCase("Boolean")) {
                    curdCreationOfUnitTest.append("\t\t").append(
                            commonUtil.toCamelCase(tableClassName1)).append(".").append("set").append(
                            commonUtil.CapitalizeClassName(column.getName())).append("(").append(values).append(");\n");

                } else if (column.getDatatype().equalsIgnoreCase("TIMESTAMP")) {
                    curdCreationOfUnitTest.append("\t\t").append(
                            commonUtil.toCamelCase(tableClassName1)).append(".").append("set").append(
                            commonUtil.CapitalizeClassName(column.getName())).append("(Timestamp.valueOf(").append('"' + values + '"').append("));\n");
                } else {
                    curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(tableClassName1)).
                            append(".").append("set").append(commonUtil.CapitalizeClassName(column.getName()))
                            .append("(").append('"').append('"').append(");\n");

                }
            }
        }
        for (Column column : columns) {
            String values = "";
            if (column.getKeytype() != null && column.getKeytype().equalsIgnoreCase("FK")) {
                if (column.getTableReference().contains("_")) {
                    curdCreationOfUnitTest.append("\t\t").append(commonUtil.removeUnderScore(column.getTableReference())).append(" ").append(commonUtil.toCamelCase(column.getTableReference())).append(" = ").append("new ").append(commonUtil.removeUnderScore(column.getTableReference())).append("( );").append("\n");
                } else {
                    curdCreationOfUnitTest.append("\t\t").append(commonUtil.capitalize(column.getTableReference())).append(" ").append(commonUtil.toCamelCase(column.getTableReference())).append(" = ").append("new ").append(commonUtil.capitalize(column.getTableReference())).append("( );").append("\n");
                }
                if (column.getDatatype().equalsIgnoreCase("NUMBER")  || column.getDatatype().equalsIgnoreCase("SMALLINT") ||
                        column.getDatatype().equalsIgnoreCase("TINYINT")|| column.getDatatype().equalsIgnoreCase("INT")||
                        column.getDatatype().equalsIgnoreCase("INTEGER") || column.getDatatype().equalsIgnoreCase("NUMERIC")) {
                    values = "1";
                } else if (column.getDatatype().equalsIgnoreCase("LONG")
                        || column.getDatatype().equalsIgnoreCase("NUMBER")) {
                    values = "1L";
                } else if (column.getDatatype().equalsIgnoreCase("SHORT")) {
                    values = "1";
                } else if (column.getDatatype().equalsIgnoreCase("VARCHAR")||column.getDatatype().equalsIgnoreCase("VARCHAR2") ||column.getDatatype().equalsIgnoreCase("NVARCHAR2")) {
                    values = "";
                } else if (column.getDatatype().equalsIgnoreCase("DOUBLE")) {
                    values = "0.0";
                } else if (column.getDatatype().equalsIgnoreCase("Boolean")) {
                    values = "false";
                } else if (column.getDatatype().equalsIgnoreCase("TIMESTAMP")) {
                    Date date = new Date();
                    Timestamp ts = new Timestamp(date.getTime());
                    values = String.valueOf(ts);
                } else if (column.getDatatype().startsWith("TINYINT")) {
                    values = String.valueOf(Boolean.valueOf("false"));
                }
                if (column.getDatatype().equalsIgnoreCase("INTEGER")||column.getDatatype().equalsIgnoreCase(Constant.SMALLINT) ||column.getDatatype().equalsIgnoreCase("NUMBER") ||column.getDatatype().equalsIgnoreCase("NUMERIC") || column.getDatatype().startsWith("TINYINT") || column.getDatatype().equalsIgnoreCase("int") || column.getDatatype().equalsIgnoreCase("Long") || column.getDatatype().equalsIgnoreCase(Constant.SHORT_SMALL) || column.getDatatype().equalsIgnoreCase("Boolean")) {
                    curdCreationOfUnitTest.append("\t\t").append(
                            commonUtil.toCamelCase(column.getTableReference())).append(".").append("set").append(
                            commonUtil.CapitalizeClassName(column.getTableReferenceId())).append("(").append(values).append(");\n");

                } else if (column.getDatatype().equalsIgnoreCase("TIMESTAMP")) {
                    curdCreationOfUnitTest.append("\t\t").append(
                            commonUtil.toCamelCase(column.getTableReference())).append(".").append("set").append(
                            commonUtil.CapitalizeClassName(column.getTableReferenceId())).append("(Timestamp.valueOf(").append('"' + values + '"').append("));\n");
                } else {
                    curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(column.getTableReference())).
                            append(".").append("set").append(commonUtil.CapitalizeClassName(column.getTableReferenceId()))
                            .append("(").append('"').append('"').append(");\n");

                }
                curdCreationOfUnitTest.append("\t\t").append(
                        commonUtil.toCamelCase(tableClassName1)).append(".").append("set").append(
                        commonUtil.CapitalizeClassName(column.getTableReference())).append("(").append(commonUtil.toCamelCase(column.getTableReference())).append(");\n");
            }

        }


        curdCreationOfUnitTest.append("\t\t").append(Constant.RETURN_SMALL).append(commonUtil.
                toCamelCase(tableClassName1)).append(";}\n\n");


        if (table.getCrudDTO().getCreate()) {
            curdCreationOfUnitTest.append("\tpublic ").append(commonUtil.capitalize(tableClassName1)).append(Constant.REQUEST_DTO_SPACE).append(commonUtil.toCamelCase(tableClassName1)).append("RequestDTO(){\n");
            curdCreationOfUnitTest.append(Constant.T_LIST).append(commonUtil.capitalize(tableClassName1)).append(Constant.REQUEST_DTO).append("> ").append(commonUtil.toCamelCase(tableClassName1)).append("RequestDTOList ").append("= ").append("new ").append(Constant.ARRAY_LIST).append("\n");
            curdCreationOfUnitTest.append("\t\t").append(commonUtil.capitalize(tableClassName1)).append(Constant.REQUEST_DTO_SPACE).append(commonUtil.toCamelCase(tableClassName1)).append(Constant.REQUEST_DTO_SPACE).append(" = ").append("new ").append(commonUtil.capitalize(tableClassName1)).append("RequestDTO();").append("\n");
            for (Column column : columns) {
                String values = "";
                if (column.getKeytype() == null || column.getKeytype().equalsIgnoreCase("PK")) {
                    if (column.getDatatype().equalsIgnoreCase("NUMBER")  || column.getDatatype().equalsIgnoreCase("SMALLINT") ||
                            column.getDatatype().equalsIgnoreCase("TINYINT")|| column.getDatatype().equalsIgnoreCase("INT")||
                            column.getDatatype().equalsIgnoreCase("INTEGER") || column.getDatatype().equalsIgnoreCase("NUMERIC")) {
                        values = "1";
                    } else if (column.getDatatype().equalsIgnoreCase("LONG")
                            || column.getDatatype().equalsIgnoreCase("NUMBER")) {
                        values = "1L";
                    } else if (column.getDatatype().equalsIgnoreCase("SHORT")) {
                        values = "1";
                    } else if (column.getDatatype().equalsIgnoreCase("VARCHAR")||column.getDatatype().equalsIgnoreCase("VARCHAR2") ||column.getDatatype().equalsIgnoreCase("NVARCHAR2")) {
                        values = "";
                    } else if (column.getDatatype().equalsIgnoreCase("DOUBLE")) {
                        values = "0.0";
                    } else if (column.getDatatype().equalsIgnoreCase("Boolean")) {
                        values = "false";
                    } else if (column.getDatatype().equalsIgnoreCase("TIMESTAMP")) {
                        Date date = new Date();
                        Timestamp ts = new Timestamp(date.getTime());

                        values = String.valueOf(ts);
                    } else if (column.getDatatype().startsWith("TINYINT")) {
                        values = String.valueOf(Boolean.valueOf("false"));
                    }
                    if (column.getDatatype().equalsIgnoreCase("INTEGER")||column.getDatatype().equalsIgnoreCase(Constant.SMALLINT) ||column.getDatatype().equalsIgnoreCase("NUMBER") ||column.getDatatype().equalsIgnoreCase("NUMERIC") || column.getDatatype().startsWith("TINYINT") || column.getDatatype().equalsIgnoreCase("int") || column.getDatatype().equalsIgnoreCase("Long") || column.getDatatype().equalsIgnoreCase(Constant.SHORT_SMALL) || column.getDatatype().equalsIgnoreCase("Boolean")) {
                        curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(tableClassName1)).append(Constant.REQUEST_DTO).append(".").append("set").append(commonUtil.CapitalizeClassName(column.getName())).append("(").append(values).append(");\n");
                    } else if (column.getDatatype().equalsIgnoreCase("TIMESTAMP")) {
                        curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(tableClassName1)).append(Constant.REQUEST_DTO).append(".").append("set").append(commonUtil.CapitalizeClassName(column.getName())).append("(Timestamp.valueOf(").append('"' + values + '"').append("));\n");
                    } else {
                        curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(tableClassName1)).append(Constant.REQUEST_DTO).append(".").append("set").append(commonUtil.CapitalizeClassName(column.getName())).append("(").append('"').append('"').append(");\n");
                    }
                }
            }

            curdCreationOfUnitTest.append("\t").append(commonUtil.toCamelCase(tableClassName1)).append("RequestDTOList").append(".").append("add(").append(commonUtil.toCamelCase(tableClassName1)).append(Constant.REQUEST_DTO).append(");\n");
            curdCreationOfUnitTest.append("\t").append(Constant.RETURN_SMALL).append(commonUtil.toCamelCase(tableClassName1)).append("RequestDTO;").append("\n");
            curdCreationOfUnitTest.append(Constant.CLOSE);

        }


        if (table.getCrudDTO().getMultipleRecord().getIsChecked() && !table.getCrudDTO().getMultipleRecord().getPagination() && !table.getCrudDTO().getMultipleRecord().getIsSearch()) {
            curdCreationOfUnitTest.append("\tpublic List< ").append(commonUtil.capitalize(tableClassName1)).append("> ").append("get").append(commonUtil.capitalize(tableClassName1)).append("AllListOfData(){\n");
            curdCreationOfUnitTest.append(Constant.T_LIST).append(commonUtil.capitalize(tableClassName1)).append("> ").append(commonUtil.toCamelCase(tableClassName1)).append("List").append("= ").append("new ").append(Constant.ARRAY_LIST).append("\n");
            curdCreationOfUnitTest.append("\t\t").append(commonUtil.capitalize(tableClassName1)).append(" ").append(commonUtil.toCamelCase(tableClassName1)).append(" = ").append("new ").append(commonUtil.capitalize(tableClassName1)).append("( );").append("\n");
            for (Column column : columns) {
                String values = "";
                if (column.getKeytype() == null || column.getKeytype().equalsIgnoreCase("PK")) {
                    if (column.getDatatype().equalsIgnoreCase("NUMBER")  || column.getDatatype().equalsIgnoreCase("SMALLINT") ||
                            column.getDatatype().equalsIgnoreCase("TINYINT")|| column.getDatatype().equalsIgnoreCase("INT")||
                            column.getDatatype().equalsIgnoreCase("INTEGER") || column.getDatatype().equalsIgnoreCase("NUMERIC")) {
                        values = "1";
                    } else if (column.getDatatype().equalsIgnoreCase("LONG")
                            || column.getDatatype().equalsIgnoreCase("NUMBER")) {
                        values = "1L";
                    } else if (column.getDatatype().equalsIgnoreCase("SHORT")) {
                        values = "1";
                    } else if (column.getDatatype().equalsIgnoreCase("VARCHAR")||column.getDatatype().equalsIgnoreCase("VARCHAR2") ||column.getDatatype().equalsIgnoreCase("NVARCHAR2")) {
                        values = "";
                    } else if (column.getDatatype().equalsIgnoreCase("DOUBLE")) {
                        values = "0.0";
                    } else if (column.getDatatype().equalsIgnoreCase("Boolean")) {
                        values = "false";
                    } else if (column.getDatatype().equalsIgnoreCase("TIMESTAMP")) {
                        Date date = new Date();
                        Timestamp ts = new Timestamp(date.getTime());
                        values = String.valueOf(ts);
                    } else if (column.getDatatype().startsWith("TINYINT")) {
                        values = String.valueOf(Boolean.valueOf("false"));
                    }
                    if (column.getDatatype().equalsIgnoreCase("INTEGER")||column.getDatatype().equalsIgnoreCase(Constant.SMALLINT) ||column.getDatatype().equalsIgnoreCase("NUMBER") ||column.getDatatype().equalsIgnoreCase("NUMERIC") || column.getDatatype().startsWith("TINYINT") || column.getDatatype().equalsIgnoreCase("int") || column.getDatatype().equalsIgnoreCase("Long") || column.getDatatype().equalsIgnoreCase(Constant.SHORT_SMALL) || column.getDatatype().equalsIgnoreCase("Boolean")) {
                        curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(tableClassName1)).append(".").append("set").append(commonUtil.CapitalizeClassName(column.getName())).append("(").append(values).append(");\n");
                    } else if (column.getDatatype().equalsIgnoreCase("TIMESTAMP")) {
                        curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(tableClassName1)).append(".").append("set").append(commonUtil.CapitalizeClassName(column.getName())).append("(Timestamp.valueOf(").append('"' + values + '"').append("));\n");
                    } else {
                        curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(tableClassName1)).append(".").append("set").append(commonUtil.CapitalizeClassName(column.getName())).append("(").append('"').append('"').append(");\n");
                    }
                }
            }
            for (Column column : columns) {
                String values = "";
                if (column.getKeytype() != null && column.getKeytype().equalsIgnoreCase("FK")) {
                    if (column.getTableReference().contains("_")) {
                        curdCreationOfUnitTest.append("\t\t").append(commonUtil.removeUnderScore(column.getTableReference())).append(" ").append(commonUtil.toCamelCase(column.getTableReference())).append(" = ").append("new ").append(commonUtil.removeUnderScore(column.getTableReference())).append("( );").append("\n");

                    } else {
                        curdCreationOfUnitTest.append("\t\t").append(commonUtil.capitalize(column.getTableReference())).append(" ").append(commonUtil.toCamelCase(column.getTableReference())).append(" = ").append("new ").append(commonUtil.capitalize(column.getTableReference())).append("( );").append("\n");
                    }

                    if (column.getDatatype().equalsIgnoreCase("INTEGER")||column.getDatatype().equalsIgnoreCase(Constant.SMALLINT) ||column.getDatatype().equalsIgnoreCase("NUMBER") ||column.getDatatype().equalsIgnoreCase("NUMERIC") || column.getDatatype().startsWith("TINYINT") || column.getDatatype().equalsIgnoreCase("int") || column.getDatatype().equalsIgnoreCase("Long") || column.getDatatype().equalsIgnoreCase(Constant.SHORT_SMALL) || column.getDatatype().equalsIgnoreCase("Boolean")) {
                        values = "1";
                    } else if (column.getDatatype().equalsIgnoreCase("LONG")
                            || column.getDatatype().equalsIgnoreCase("NUMBER")) {
                        values = "1L";
                    } else if (column.getDatatype().equalsIgnoreCase("SHORT")) {
                        values = "1";
                    } else if (column.getDatatype().equalsIgnoreCase("VARCHAR")||column.getDatatype().equalsIgnoreCase("VARCHAR2") ||column.getDatatype().equalsIgnoreCase("NVARCHAR2")) {
                        values = "";
                    } else if (column.getDatatype().equalsIgnoreCase("DOUBLE")) {
                        values = "0.0";
                    } else if (column.getDatatype().equalsIgnoreCase("Boolean")) {
                        values = "false";
                    } else if (column.getDatatype().equalsIgnoreCase("TIMESTAMP")) {
                        Date date = new Date();
                        Timestamp ts = new Timestamp(date.getTime());
                        values = String.valueOf(ts);
                    } else if (column.getDatatype().startsWith("TINYINT")) {
                        values = String.valueOf(Boolean.valueOf("false"));
                    }
                    if (column.getDatatype().equalsIgnoreCase("INTEGER")||column.getDatatype().equalsIgnoreCase(Constant.SMALLINT) ||column.getDatatype().equalsIgnoreCase("NUMBER") ||column.getDatatype().equalsIgnoreCase("NUMERIC") || column.getDatatype().startsWith("TINYINT") || column.getDatatype().equalsIgnoreCase("int") || column.getDatatype().equalsIgnoreCase("Long") || column.getDatatype().equalsIgnoreCase(Constant.SHORT_SMALL) || column.getDatatype().equalsIgnoreCase("Boolean")) {
                        curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(column.getTableReference())).append(".").append("set").append(commonUtil.CapitalizeClassName(column.getTableReferenceId())).append("(").append(values).append(");\n");
                    } else if (column.getDatatype().equalsIgnoreCase("TIMESTAMP")) {
                        curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(column.getTableReference())).append(".").append("set").append(commonUtil.CapitalizeClassName(column.getTableReferenceId())).append("(Timestamp.valueOf(").append('"' + values + '"').append("));\n");
                    } else {
                        curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(column.getTableReference())).append(".").append("set").append(commonUtil.CapitalizeClassName(column.getTableReferenceId())).append("(").append('"').append('"').append(");\n");
                    }
                    curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(tableClassName1)).append(".").append("set").append(commonUtil.CapitalizeClassName(column.getTableReference())).append("(").append(commonUtil.toCamelCase(column.getTableReference())).append(");\n");

                }


            }
            curdCreationOfUnitTest.append("\t\t").append(commonUtil.toCamelCase(tableClassName1)).append("List").append(".").append("add(").append(commonUtil.toCamelCase(tableClassName1)).append(");\n");
            curdCreationOfUnitTest.append("\t\t").append(Constant.RETURN_SMALL).append(commonUtil.toCamelCase(tableClassName1)).append("List;").append("\n");
            curdCreationOfUnitTest.append(Constant.CLOSE);
        }
        return curdCreationOfUnitTest.toString();
    }
}

package com.d2d.util;


import com.d2d.constant.Constant;
import com.d2d.exception.CustomValidationException;
import com.d2d.dto.Column;
import com.d2d.dto.DbDesignTables;
import com.d2d.dto.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CrudUtil {

    @Autowired
    CommonUtil commonUtil;

    public String createControllerClass(Table table, String tableClassName, String packageName, String primaryKeyJavaType, DbDesignTables tables, Boolean isDeletedPresent) throws CustomValidationException {

        String tableClassNameInObject = tableClassName.substring(0, 1).toLowerCase( ) + tableClassName.substring(1);
        StringBuilder controllerCreation = new StringBuilder( );
        controllerCreation.append(Constant.PACKAGE_SPACE).append(packageName).append(".controller;\n\n");
        controllerCreation.append(Constant.IMPORT).append(packageName).append(Constant.SUCCESS_RESPONSE);
        if(table.getCrudDTO().getMultipleRecord().getIsChecked() && table.getCrudDTO().getMultipleRecord().getPagination()) {
            controllerCreation.append(Constant.IMPORT).append(packageName).append(Constant.PAGE_RESPONSE);
        }
        controllerCreation.append(Constant.IMPORT).append(packageName).append(".").append("service.")
                .append(tableClassName).append("Service;\n");
        if (table.getCrudDTO( ).getCreate( )) {
            controllerCreation.append(Constant.IMPORT).append(packageName).append(Constant.DTO).append(tableClassName).append(Constant.REQUEST_DTO_NEWLINE);
        }

        if (table.getCrudDTO( ).getMultipleRecord( ).getIsChecked( ) && (table.getCrudDTO( ).getMultipleRecord( ).getPagination( )
                || table.getCrudDTO( ).getMultipleRecord( ).getIsSearch( ) || table.getCrudDTO( ).getMultipleRecord( ).getFilter( )
                || table.getCrudDTO( ).getMultipleRecord( ).getIsSearch( ))) {
            controllerCreation.append(Constant.IMPORT).append(packageName).append(Constant.DTO).append(tableClassName).append(Constant.GET_ALLREQUEST_DTO);
        }

        if (tables.getEnableSecurityDTO( ).getIsSecurityRequired( ) && tables.getEnableSecurityDTO( ).getSecurityMethod( ) == 3) {
            if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("2")) {
                controllerCreation.append("import javax.annotation.security.RolesAllowed;\n");
            } else if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("3")) {
                controllerCreation.append("import org.springframework.security.access.prepost.PreAuthorize;\n");
            }

        }
        if (tables.getEnableSecurityDTO( ).getIsSecurityRequired( ) && tables.getEnableSecurityDTO( ).getSecurityMethod( ) == 2) {
            if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("2")) {
                controllerCreation.append("import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;\n");

            } else if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("3")) {
                controllerCreation.append("import org.springframework.security.access.prepost.PreAuthorize;\n");

            }
        }
        controllerCreation.append("import org.springframework.beans.factory.annotation.Autowired;\n")
                .append("import org.springframework.web.bind.annotation.*;\n\n");
        controllerCreation.append("@RestController\n").append("@RequestMapping(\"/");

        char firstLetter = tableClassName.charAt(0);
        String requestMappingName = String.valueOf(firstLetter).toLowerCase( ) + tableClassName.substring(1);
        controllerCreation.append(requestMappingName).append("\")\n");
        if (tables.getEnableSecurityDTO( ).getIsSecurityRequired( ) && tables.getEnableSecurityDTO( ).getSecurityMethod( ) == 2) {
            if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("2")) {
                controllerCreation.append("@EnableResourceServer\n");
            }
        }
        controllerCreation.append("public class ").append(tableClassName).append("Controller").append(" { \n\n");
        controllerCreation.append(Constant.AUTOWIRED).append(Constant.PRIVATE_SPACE).append(tableClassName)
                .append("Service ");

        String serviceFileObjectName = requestMappingName + "service";
        controllerCreation.append(serviceFileObjectName).append(Constant.SEMI_SPACE);

        if (table.getCrudDTO().getCreate()) {
            controllerCreation.append("    @PostMapping(\"/save\")\n");
            controllerCreation.append("    public SuccessResponse<Object> save(@RequestBody ").append(tableClassName).append("RequestDTO ")
                    .append(requestMappingName).append(Constant.REQUEST_DTO).append(") {\n");
            controllerCreation.append(Constant.RETURN).append(serviceFileObjectName).append(".save(").append(requestMappingName).
                    append(Constant.REQUEST_DTO).append(");\n");
            controllerCreation.append(Constant.SPACE_NEWLINE);
        }
        if (table.getCrudDTO( ).getMultipleRecord( ).getIsChecked( ) && !table.getCrudDTO( ).getMultipleRecord( ).getPagination( ) && !table.getCrudDTO( ).getMultipleRecord( ).getIsSearch( )) {
            controllerCreation.append("    @GetMapping(\"/getAll\")\n");
            controllerCreation.append("    public SuccessResponse<Object> getAllList() {\n");
            controllerCreation.append(Constant.RETURN).append(serviceFileObjectName).append(".getAllList();\n");
            controllerCreation.append(Constant.SPACE_NEWLINE);
        } else if (table.getCrudDTO( ).getMultipleRecord( ).getIsChecked( ) && (table.getCrudDTO( ).getMultipleRecord( ).getPagination( )
                || table.getCrudDTO( ).getMultipleRecord( ).getIsSearch( ) || table.getCrudDTO( ).getMultipleRecord( ).getFilter( )
                || table.getCrudDTO( ).getMultipleRecord( ).getSort( ))) {
            controllerCreation.append("    @PostMapping(\"/getAllWithPagination\")\n");
            controllerCreation.append("    public PageResponse<Object> getAllListWithPagination(@RequestBody ").append(tableClassName)
                    .append(Constant.GETALL_REQUEST_DTO).append(tableClassNameInObject).append("GetAllRequestDTO) {\n");
            controllerCreation.append(Constant.RETURN).append(serviceFileObjectName).append(".getAllListWithPagination(").append(tableClassNameInObject)
                    .append("GetAllRequestDTO);\n");
            controllerCreation.append(Constant.SPACE_NEWLINE);
        }

        if (table.getCrudDTO( ).getSingleRecord( )) {
            controllerCreation.append("    @GetMapping(\"/getById\")\n");
            controllerCreation.append("    public SuccessResponse<Object> getById(@RequestParam(\"id\") ").append(primaryKeyJavaType).append(Constant.ID_NEWLINE);
            controllerCreation.append(Constant.RETURN).append(serviceFileObjectName).append(".getById(id);\n");
            controllerCreation.append(Constant.SPACE_NEWLINE);
        }
        if (tables.getEnableSecurityDTO( ).getIsSecurityRequired( ) && tables.getEnableSecurityDTO( ).getSecurityMethod( ) == 3) {
            if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("2")) {
                controllerCreation.append("    @RolesAllowed(\"\")\n");
                controllerCreation.append(Constant.ROLES_ALLOWED);
                controllerCreation.append("\n");
                controllerCreation.append(Constant.RETURN_NEWLINE);
                controllerCreation.append(Constant.SPACE_NEWLINE);
            } else if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("3")) {
                controllerCreation.append("    @PreAuthorize(\"hasRole('')\")\n");
                controllerCreation.append(Constant.ROLES_ALLOWED);
                controllerCreation.append(Constant.RETURN_NEWLINE);
                controllerCreation.append(Constant.SPACE_NEWLINE);
            }

        }
        if (tables.getEnableSecurityDTO( ).getIsSecurityRequired( ) && tables.getEnableSecurityDTO( ).getSecurityMethod( ) == 2) {
            if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("3")) {
                controllerCreation.append("    @PreAuthorize(\"hasRole('')\")\n");
                controllerCreation.append(Constant.ROLES_ALLOWED);
                controllerCreation.append(Constant.RETURN_NEWLINE);
                controllerCreation.append(Constant.SPACE_NEWLINE);
            }
        }
        if (table.getCrudDTO( ).getDelete( ).getIsChecked( )) {
            if (isDeletedPresent && table.getCrudDTO( ).getDelete( ).getSoftDelete( ) && table.getCrudDTO( ).getDelete( ).getHardDelete( )) {
                controllerCreation.append("    @PostMapping(\"/deleteById\")\n");
                controllerCreation.append("    public SuccessResponse<Object> deleteById(@RequestParam(\"id\") ").append(primaryKeyJavaType).append(" id, @RequestParam(\"softDelete\") Boolean softDelete) {\n");
                controllerCreation.append(Constant.RETURN).append(serviceFileObjectName).append(".deleteById(id,softDelete);\n");
            } else {
                controllerCreation.append("    @DeleteMapping(\"/deleteById\")\n");
                controllerCreation.append("    public SuccessResponse<Object> deleteById(@RequestParam(\"id\") ").append(primaryKeyJavaType).append(Constant.ID_NEWLINE);
                controllerCreation.append(Constant.RETURN).append(serviceFileObjectName).append(".deleteById(id);\n");
            }
            controllerCreation.append(Constant.SPACE_NEWLINE);
        }
        controllerCreation.append("}");
        return controllerCreation.toString( );
    }

    public String createServiceClass(Table table, String tableClassName, String packageName, String primaryKeyJavaType, Boolean isDeletedPresent) {
        StringBuilder serviceClassCreation = new StringBuilder( );
        serviceClassCreation.append(Constant.PACKAGE_SPACE).append(packageName).append(".service;\n\n");
        if (table.getCrudDTO( ).getCreate( )) {
            serviceClassCreation.append(Constant.IMPORT).append(packageName).append(Constant.DTO).append(tableClassName).append(Constant.REQUEST_DTO_NEWLINE);
        }

        if (table.getCrudDTO( ).getMultipleRecord( ).getIsChecked( ) && (table.getCrudDTO( ).getMultipleRecord( ).getPagination( )
                || table.getCrudDTO( ).getMultipleRecord( ).getIsSearch( ) || table.getCrudDTO( ).getMultipleRecord( ).getFilter( )
                || table.getCrudDTO( ).getMultipleRecord( ).getIsSearch( ))) {
            serviceClassCreation.append(Constant.IMPORT).append(packageName).append(Constant.DTO).append(tableClassName).append(Constant.GET_ALLREQUEST_DTO);
        }

        serviceClassCreation.append(Constant.IMPORT).append(packageName).append(Constant.SUCCESS_RESPONSE);
        if(table.getCrudDTO().getMultipleRecord().getIsChecked() && table.getCrudDTO().getMultipleRecord().getPagination()) {
            serviceClassCreation.append(Constant.IMPORT).append(packageName).append(Constant.PAGE_RESPONSE);
        }
        serviceClassCreation.append("import org.springframework.stereotype.Service;\n\n");
        serviceClassCreation.append("@Service\n");

        serviceClassCreation.append("public interface ").append(tableClassName).append("Service {\n\n");

        if (table.getCrudDTO( ).getCreate( )) {
            serviceClassCreation.append("    SuccessResponse<Object> save(").append(tableClassName).append("RequestDTO ").
                    append(tableClassName.substring(0, 1).toLowerCase( )).append(tableClassName.substring(1)).append(Constant.REQUEST_DTO).append(");\n\n");
        }
        if (table.getCrudDTO( ).getMultipleRecord( ).getIsChecked( ) && !table.getCrudDTO( ).getMultipleRecord( ).getPagination( ) && !table.getCrudDTO( ).getMultipleRecord( ).getIsSearch( )) {
            serviceClassCreation.append("    SuccessResponse<Object> getAllList();\n\n");
        } else if (table.getCrudDTO( ).getMultipleRecord( ).getIsChecked( ) && (table.getCrudDTO( ).getMultipleRecord( ).getPagination( )
                || table.getCrudDTO( ).getMultipleRecord( ).getIsSearch( ) || table.getCrudDTO( ).getMultipleRecord( ).getFilter( )
                || table.getCrudDTO( ).getMultipleRecord( ).getSort( ))) {
            serviceClassCreation.append("    PageResponse<Object> getAllListWithPagination(").append(tableClassName).append(Constant.GETALL_REQUEST_DTO)
                    .append(tableClassName.substring(0, 1).toLowerCase( )).append(tableClassName.substring(1)).append("GetAllRequestDTO);\n\n");
        }

        if (table.getCrudDTO( ).getSingleRecord( )) {
            serviceClassCreation.append("    SuccessResponse<Object> getById(").append(primaryKeyJavaType).append(" id);\n\n");
        }
        if (table.getCrudDTO( ).getDelete( ).getIsChecked( )) {
            if (isDeletedPresent && table.getCrudDTO( ).getDelete( ).getSoftDelete( ) && table.getCrudDTO( ).getDelete( ).getHardDelete( )) {
                serviceClassCreation.append("    SuccessResponse<Object> deleteById(").append(primaryKeyJavaType).append(" id, Boolean softDelete);\n\n");
            } else {
                serviceClassCreation.append("    SuccessResponse<Object> deleteById(").append(primaryKeyJavaType).append(" id);\n\n");
            }
        }
        serviceClassCreation.append("}");
        return serviceClassCreation.toString( );
    }

    public String createServiceImplClass(Table table, String tableClassName, String packageName, String primaryKeyJavaType,
                                         String primaryKeyFieldName, Boolean isDeletedPresent) throws CustomValidationException {

        String tableClassNameInObject = tableClassName.substring(0, 1).toLowerCase( ) + tableClassName.substring(1);
        Map<Integer, Map<String, String>> map = commonUtil.getJavaType( );
        List<Column> foreignKeyColumn = table.getColumns( ).stream( ).filter(column -> column.getKeytype( ) != null && column.getKeytype( ).equalsIgnoreCase("FK")).collect(Collectors.toList( ));
        StringBuilder serviceImplClassCreation = new StringBuilder( );
        serviceImplClassCreation.append(Constant.PACKAGE_SPACE).append(packageName).append(".serviceimpl;\n\n");
        serviceImplClassCreation.append(Constant.IMPORT).append(packageName).append(".model.").append(tableClassName).append(";\n");
        serviceImplClassCreation.append(Constant.IMPORT).append(packageName).append(".repository.").append(tableClassName).append("Repository;\n");
        serviceImplClassCreation.append(Constant.IMPORT).append(packageName).append(".exception.").append("CustomValidationException;").append("\n");
        serviceImplClassCreation.append(Constant.IMPORT).append(packageName).append(".service.")
                .append(tableClassName).append("Service;\n");

        if (table.getCrudDTO( ).getMultipleRecord( ).getIsChecked( ) && (table.getCrudDTO( ).getMultipleRecord( ).getPagination( )
                || table.getCrudDTO( ).getMultipleRecord( ).getIsSearch( ) || table.getCrudDTO( ).getMultipleRecord( ).getFilter( )
                || table.getCrudDTO( ).getMultipleRecord( ).getIsSearch( ))) {
            serviceImplClassCreation.append(Constant.IMPORT).append(packageName).append(Constant.DTO).append(tableClassName).append(Constant.GET_ALLREQUEST_DTO);
        }

        if (table.getCrudDTO( ).getCreate( )) {
            for (int i = 0; i < foreignKeyColumn.size( ); i++) {
                serviceImplClassCreation.append(Constant.IMPORT).append(packageName).append(".model.").append(commonUtil
                        .CapitalizeClassName(foreignKeyColumn.get(i).getTableReference( ))).append(";\n");
                serviceImplClassCreation.append(Constant.IMPORT).append(packageName).append(".repository.").append(commonUtil
                        .CapitalizeClassName(foreignKeyColumn.get(i).getTableReference( ))).append("Repository").append(";\n");
            }
            serviceImplClassCreation.append(Constant.IMPORT).append(packageName).append(Constant.DTO).append(tableClassName).append(Constant.REQUEST_DTO_NEWLINE);
        }
        serviceImplClassCreation.append(Constant.IMPORT).append(packageName).append(Constant.SUCCESS_RESPONSE);
        if(table.getCrudDTO().getMultipleRecord().getIsChecked() && table.getCrudDTO().getMultipleRecord().getPagination()){
        serviceImplClassCreation.append(Constant.IMPORT).append(packageName).append(Constant.PAGE_RESPONSE);
        }
        serviceImplClassCreation.append(Constant.IMPORT).append(packageName).append(Constant.DTO).append(tableClassName).append("DTO;\n");
        serviceImplClassCreation.append("import org.modelmapper.ModelMapper;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Service;\n\n");
        if (table.getCrudDTO( ).getMultipleRecord( ).getIsChecked( )) {
            serviceImplClassCreation.append("import java.util.ArrayList;\n" +
                    "import java.util.List;\n");
            if (table.getCrudDTO().getMultipleRecord().getPagination()) {
                serviceImplClassCreation.append("import org.springframework.data.domain.Page;\n");
                serviceImplClassCreation.append("import org.springframework.data.domain.PageRequest;\n");
                serviceImplClassCreation.append("import org.springframework.data.domain.Pageable;\n");
                if (table.getCrudDTO().getMultipleRecord().getSort()) {
                    serviceImplClassCreation.append("import org.springframework.data.domain.Sort;\n\n");
                }
            }
        }
        if (table.getCrudDTO( ).getSingleRecord( ) || table.getCrudDTO( ).getDelete( ).getIsChecked( ) || table.getCrudDTO( ).getCreate( )) {
            serviceImplClassCreation.append("import java.util.Optional;\n");
        }
        serviceImplClassCreation.append("\n");
        serviceImplClassCreation.append("@Service\n");
        serviceImplClassCreation.append("public class ").append(tableClassName).append("ServiceImpl implements ")
                .append(tableClassName).append("Service { \n\n");
        serviceImplClassCreation.append(Constant.AUTOWIRED +
                "    private ModelMapper modelMapper;\n\n");
        serviceImplClassCreation.append(Constant.AUTOWIRED +
                        Constant.PRIVATE_SPACE).append(tableClassName).append("Repository ")
                .append(tableClassNameInObject)
                .append("Repository").append(Constant.SEMI_SPACE);
        if (table.getCrudDTO( ).getCreate( )) {
            for (int i = 0; i < foreignKeyColumn.size( ); i++) {
                String foreignTableClassName = commonUtil.CapitalizeClassName(foreignKeyColumn.get(i).getTableReference( ));
                String foreignTableInObject = foreignTableClassName.substring(0, 1).toLowerCase( ) + foreignTableClassName.substring(1);
                serviceImplClassCreation.append(Constant.AUTOWIRED +
                                Constant.PRIVATE_SPACE).append(foreignTableClassName).append("Repository ")
                        .append(foreignTableInObject)
                        .append("Repository").append(Constant.SEMI_SPACE);
            }
        }
        if (table.getCrudDTO( ).getMultipleRecord( ).getIsChecked( )) {
            // Without Pagination
            if (!table.getCrudDTO( ).getMultipleRecord( ).getPagination( )) {
                serviceImplClassCreation.append(Constant.OVERRIDE +
                        "    public SuccessResponse<Object> ");
                serviceImplClassCreation.append("getAllList() { \n");
                serviceImplClassCreation.append(Constant.SUCCESSRESPONSE_CREATION);
                serviceImplClassCreation.append(Constant.LIST).append(tableClassName).append("DTO> list = new ArrayList<>();\n");
                serviceImplClassCreation.append(Constant.LIST).append(tableClassName).append("> ").append(tableClassNameInObject)
                        .append("List = ").append(tableClassNameInObject).append("Repository.findAll();\n");
                serviceImplClassCreation.append("        if(").append(tableClassNameInObject).append("List.size() > 0) {\n");
                serviceImplClassCreation.append(Constant.SPACE).append(tableClassNameInObject).append("List.forEach(")
                        .append(tableClassNameInObject).append(" -> {\n" +
                                "                list.add(modelMapper.map(").append(tableClassNameInObject).append(",").append(tableClassName)
                        .append("DTO.class").append("));\n" +
                                "            });\n" +
                                Constant.NEW_LINE);
                serviceImplClassCreation.append("        successResponse.setData(list);\n" +
                        Constant.RETURN_SUCCESS +
                        Constant.SPACE_NEWLINE);
            }
            // With Pagination
            else {
                String getAllRequestDTO = "GetAllRequestDTO";
                String tableClassNameInObjectWithGetAllRequestDTO = tableClassNameInObject + getAllRequestDTO;

                serviceImplClassCreation.append(Constant.OVERRIDE +
                        "    public PageResponse<Object> ");
                serviceImplClassCreation.append("getAllListWithPagination(").append(tableClassName)
                        .append(Constant.GETALL_REQUEST_DTO).append(tableClassNameInObjectWithGetAllRequestDTO).append(") { \n");
                serviceImplClassCreation.append(Constant.LIST).append(tableClassName).append("DTO> list = new ArrayList<>();\n");
                serviceImplClassCreation.append("        Integer pageSize = ").append(tableClassNameInObjectWithGetAllRequestDTO).append(".getPageSize();\n");
                serviceImplClassCreation.append("        Integer pageNo = 0;\n");
                serviceImplClassCreation.append("        PageResponse<Object> pageResponse = new PageResponse<Object>();\n");
                if (table.getCrudDTO( ).getMultipleRecord( ).getSort( ) && table.getCrudDTO( ).getMultipleRecord( ).getSortField( ) != null && !table.getCrudDTO( ).getMultipleRecord( ).getSortField( ).isEmpty( )) {
                    serviceImplClassCreation.append("        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(\"").append(table.getCrudDTO( ).getMultipleRecord( ).getSortField( )).append("\").ascending());\n");
                } else {
                    serviceImplClassCreation.append("        Pageable paging = PageRequest.of(pageNo, pageSize);\n");
                }
                serviceImplClassCreation.append("        Page<").append(tableClassName).append("> ").append(tableClassNameInObject).append(Constant.SEMI_SPACE);


                serviceImplClassCreation.append(Constant.SPACE).append(tableClassNameInObject).append(" = ").append(tableClassNameInObject).append("Repository.findAllListWithPagination(");

                StringBuilder parameterString = new StringBuilder( );

                if (table.getCrudDTO( ).getMultipleRecord( ).getIsSearch( ) && table.getCrudDTO( ).getMultipleRecord( ).getSearchFieldList( ) != null
                        && table.getCrudDTO( ).getMultipleRecord( ).getSearchFieldList( ).size( ) > 0) {
                    parameterString.append(tableClassNameInObjectWithGetAllRequestDTO).append(".getSearch(),");
                }

                if (table.getCrudDTO( ).getMultipleRecord( ).getFilter( ) && table.getCrudDTO( ).getMultipleRecord( ).getFilteredFieldList( ).size( ) > 0) {
                    List<Column> foreignKeyVariablesOnly = table.getColumns( ).stream( ).filter(column -> column.getKeytype( ) != null
                            && column.getKeytype( ).equalsIgnoreCase("FK")).collect(Collectors.toList( ));
                    for (int i = 0; i < table.getCrudDTO( ).getMultipleRecord( ).getFilteredFieldList( ).size( ); i++) {
                        int finalI = i;
                        foreignKeyVariablesOnly.stream( ).forEach(column -> {
                            if (column.getName( ).equalsIgnoreCase(table.getCrudDTO( ).getMultipleRecord( ).getFilteredFieldList( ).get(finalI))) {
                                String columnNameInCaps = commonUtil.CapitalizeClassName(column.getTableReference( ));
                                parameterString.append(tableClassNameInObjectWithGetAllRequestDTO).append(".get").append(columnNameInCaps).append("(),");
                            }
                        });
                    }
                }


                serviceImplClassCreation.append(parameterString.toString( ));
                serviceImplClassCreation.append("paging );\n");
                serviceImplClassCreation.append("             if(!").append(tableClassNameInObject).append(".isEmpty()) {\n");
                serviceImplClassCreation.append(Constant.SPACE).append(tableClassNameInObject).append(".forEach(").append(tableClassNameInObject).append("s").append(" -> {\n");
                serviceImplClassCreation.append(Constant.SECOND_SPACE).append("list").append(".add(modelMapper.map(").append(tableClassNameInObject).append("s").append(",").append(tableClassName).append("DTO.class));\n");
                serviceImplClassCreation.append("            });\n");
                serviceImplClassCreation.append("            pageResponse.setTotalRecordCount(").append(tableClassNameInObject).append(".getTotalElements());\n");
                serviceImplClassCreation.append("            pageResponse.setHasNext(").append(tableClassNameInObject).append(".hasNext());\n");
                serviceImplClassCreation.append("            pageResponse.setHasPrevious(").append(tableClassNameInObject).append(".hasPrevious());\n");
                serviceImplClassCreation.append("            pageResponse.setData(list);\n");
                serviceImplClassCreation.append(Constant.NEW_LINE);
                serviceImplClassCreation.append("        return pageResponse;\n    }\n");
            }
        }

        if (table.getCrudDTO( ).getSingleRecord( )) {
            serviceImplClassCreation.append(Constant.OVERRIDE +
                    "    public SuccessResponse<Object> getById(").append(primaryKeyJavaType).append(Constant.ID_NEWLINE);
            serviceImplClassCreation.append(Constant.SUCCESSRESPONSE_CREATION);
            serviceImplClassCreation.append(Constant.OPTIONAL).append(tableClassName).append("> ")
                    .append(tableClassNameInObject).append(Constant.OPTIONAL_EQUAL).append(tableClassNameInObject).append("Repository.findById(id);\n");
            serviceImplClassCreation.append(Constant.IF).append(tableClassNameInObject)
                    .append(Constant.OPTIONAL_PRESENT);

            if (isDeletedPresent) {
                serviceImplClassCreation.append("            if (!").append(tableClassNameInObject).append("Optional.get().getDeletedFlag()) { \n");
                serviceImplClassCreation.append(Constant.SECOND_SPACE).append(tableClassName).append("DTO ").append(tableClassNameInObject).append("DTO = modelMapper.map(")
                        .append(tableClassNameInObject).append("Optional.get(), ").append(tableClassName).append("DTO.class);\n");
                serviceImplClassCreation.append("                successResponse.setData(").append(tableClassNameInObject).append("DTO);\n");

                serviceImplClassCreation.append(Constant.ELSE_NEWLINE +
                        Constant.THROW_NEW);

                    serviceImplClassCreation.append(Constant.CUSTOM_VALIDATION_EXCEPTION);

                serviceImplClassCreation.append(Constant.ID_NOT_FOUND_EXCEPTION);
                serviceImplClassCreation.append(Constant.CURLY_NEWLINE);

            } else {
                serviceImplClassCreation.append(Constant.SPACE).append(tableClassName).append("DTO ").append(tableClassNameInObject).append("DTO = modelMapper.map(")
                        .append(tableClassNameInObject).append("Optional.get(), ").append(tableClassName).append("DTO.class);\n");
                serviceImplClassCreation.append("            successResponse.setData(").append(tableClassNameInObject).append("DTO);\n");
            }
            serviceImplClassCreation.append(Constant.CURLY_ELSE_NEWLINE +
                    Constant.THROW_NEW_TWO);

                serviceImplClassCreation.append(Constant.CUSTOM_VALIDATION_EXCEPTION);

            serviceImplClassCreation.append(Constant.ID_NOT_FOUND_EXCEPTION);
            serviceImplClassCreation.append(Constant.NEW_LINE +
                    Constant.RETURN_SUCCESS +
                    Constant.CURLY_NEWLINE  +
                    "\n");
        }
        if (table.getCrudDTO( ).getDelete( ).getIsChecked( )) {
            Boolean softDelete = table.getCrudDTO( ).getDelete( ).getSoftDelete( );
            Boolean hardDelete = table.getCrudDTO( ).getDelete( ).getHardDelete( );
            if (isDeletedPresent && hardDelete && softDelete) {
                serviceImplClassCreation.append(Constant.OVERRIDE +
                        "    public SuccessResponse<Object> deleteById(").append(primaryKeyJavaType).append(" id, Boolean softDelete) {\n");
            } else {
                serviceImplClassCreation.append(Constant.OVERRIDE +
                        "    public SuccessResponse<Object> deleteById(").append(primaryKeyJavaType).append(Constant.ID_NEWLINE);
            }
            serviceImplClassCreation.append(Constant.SUCCESSRESPONSE_CREATION);
            serviceImplClassCreation.append(Constant.OPTIONAL).append(tableClassName).append("> ")
                    .append(tableClassNameInObject).append(Constant.OPTIONAL_EQUAL).append(tableClassNameInObject).append("Repository.findById(id);\n");
            serviceImplClassCreation.append(Constant.IF).append(tableClassNameInObject)
                    .append("Optional.isPresent()) {\n            ");

            if (isDeletedPresent) {
                serviceImplClassCreation.append("if (!").append(tableClassNameInObject).append("Optional.get().getDeletedFlag()) { \n");

                if (hardDelete && softDelete) {
                    serviceImplClassCreation.append("                if (softDelete) {\n");
                    serviceImplClassCreation.append(Constant.SPACE_TWO).append(tableClassNameInObject).append("Optional.get().setDeletedFlag(true);\n");
                    serviceImplClassCreation.append(Constant.SPACE_TWO).append(tableClassNameInObject).append("Repository.save(").append(tableClassNameInObject).append("Optional.get());\n");
                    serviceImplClassCreation.append("                } else {\n");
                    serviceImplClassCreation.append(Constant.SPACE_TWO).append(tableClassNameInObject).append(Constant.REPO_DELETEBYID);
                    serviceImplClassCreation.append("                }\n");
                    serviceImplClassCreation.append("                successResponse.setData(\"Record Deleted Successfully\");\n");
                } else {
                    serviceImplClassCreation.append("                 ").append(tableClassNameInObject).append(Constant.REPO_DELETEBYID);
                    serviceImplClassCreation.append("                 successResponse.setData(\"Record Deleted Successfully\");\n");
                }

                serviceImplClassCreation.append(Constant.ELSE_NEWLINE +
                        Constant.THROW_NEW);

                    serviceImplClassCreation.append(Constant.CUSTOM_VALIDATION_EXCEPTION);

                serviceImplClassCreation.append(Constant.ID_NOT_FOUND_EXCEPTION);
                serviceImplClassCreation.append(Constant.CURLY_NEWLINE);

            } else {
                serviceImplClassCreation.append(tableClassNameInObject).append(Constant.REPO_DELETEBYID);
                serviceImplClassCreation.append("            successResponse.setData(\"Record Deleted Successfully\");\n");
            }
            serviceImplClassCreation.append(
                    Constant.CURLY_ELSE_NEWLINE +
                            Constant.THROW_NEW_TWO);

                serviceImplClassCreation.append(Constant.CUSTOM_VALIDATION_EXCEPTION);

            serviceImplClassCreation.append(Constant.ID_NOT_FOUND_EXCEPTION);
            serviceImplClassCreation.append(Constant.NEW_LINE +
                    Constant.RETURN_SUCCESS +
                    Constant.CURLY_NEWLINE  +
                    "\n");
        }
        if (table.getCrudDTO( ).getCreate( )) {

            String RequestDTOObjectName = tableClassNameInObject + Constant.REQUEST_DTO;
            String RequestDTOClassName = tableClassName + Constant.REQUEST_DTO;
            String primaryKeyFieldNameForGetOff = commonUtil.CapitalizeClassName(primaryKeyFieldName);

            serviceImplClassCreation.append(Constant.OVERRIDE +
                    "    public SuccessResponse<Object> save(").append(RequestDTOClassName).append(" ").append(RequestDTOObjectName).append(") {\n");
            serviceImplClassCreation.append(Constant.SUCCESSRESPONSE_CREATION);

            serviceImplClassCreation.append("        ").append(tableClassName).append(" ").append(tableClassNameInObject).append(" = null ;\n");
            serviceImplClassCreation.append(Constant.IF).append(RequestDTOObjectName).append(".get").append(primaryKeyFieldNameForGetOff).append("() == null) {\n");
            serviceImplClassCreation.append(Constant.SPACE).append(tableClassNameInObject).append(" = new ").append(tableClassName).append("();\n");
            serviceImplClassCreation.append(Constant.CURLY_ELSE_NEWLINE);
            serviceImplClassCreation.append("            Optional<").append(tableClassName).append("> ").append(tableClassNameInObject)
                    .append(Constant.OPTIONAL_EQUAL).append(tableClassNameInObject).append("Repository.findById(").append(RequestDTOObjectName)
                    .append(".get").append(primaryKeyFieldNameForGetOff).append("());\n");
            serviceImplClassCreation.append("            if (").append(tableClassNameInObject).append(Constant.OPTIONAL_PRESENT);
            serviceImplClassCreation.append(Constant.SECOND_SPACE).append(tableClassNameInObject).append(" = ").append(tableClassNameInObject).append("Optional.get();\n");

            serviceImplClassCreation.append(Constant.ELSE_NEWLINE);
            serviceImplClassCreation.append(Constant.THROW_NEW);

                serviceImplClassCreation.append(Constant.CUSTOM_VALIDATION_EXCEPTION);

            serviceImplClassCreation.append(Constant.ID_NOT_FOUND_EXCEPTION);
            serviceImplClassCreation.append(Constant.CURLY_NEWLINE);


            serviceImplClassCreation.append(Constant.NEW_LINE);

            for (int i = 0; i < foreignKeyColumn.size( ); i++) {
                String foreignKeyTableNameInClassName = commonUtil.CapitalizeClassName(foreignKeyColumn.get(i).getTableReference( ));
                String foreignKeyTableNameInObjectName = foreignKeyTableNameInClassName.substring(0, 1).toLowerCase( ) + foreignKeyTableNameInClassName.substring(1);

                serviceImplClassCreation.append(Constant.OPTIONAL).append(foreignKeyTableNameInClassName).append("> ").append(foreignKeyTableNameInObjectName)
                        .append(Constant.OPTIONAL_EQUAL).append(foreignKeyTableNameInObjectName).append("Repository.findById(").append(RequestDTOObjectName)
                        .append(".get").append(foreignKeyTableNameInClassName).append("Id());\n");
                serviceImplClassCreation.append(Constant.IF).append(foreignKeyTableNameInObjectName).append(Constant.OPTIONAL_PRESENT);
                serviceImplClassCreation.append(Constant.SPACE).append(tableClassNameInObject).append(".set").append(foreignKeyTableNameInClassName)
                        .append("(").append(foreignKeyTableNameInObjectName).append("Optional.get());\n");

                serviceImplClassCreation.append(Constant.CURLY_ELSE_NEWLINE);
                serviceImplClassCreation.append(Constant.THROW_NEW_TWO);

                    serviceImplClassCreation.append(Constant.CUSTOM_VALIDATION_EXCEPTION);

                serviceImplClassCreation.append(Constant.ID_NOT_FOUND_EXCEPTION);
                serviceImplClassCreation.append("        }\n\n");
            }
            serviceImplClassCreation.append("        modelMapper.map(").append(RequestDTOObjectName).append(",")
                    .append(tableClassNameInObject).append(");\n");


            serviceImplClassCreation.append("        ").append(tableClassNameInObject).append("Repository.save(").append(tableClassNameInObject).append(");\n");
            serviceImplClassCreation.append("        successResponse.setData(\"Data Saved Successfully\");\n");
            serviceImplClassCreation.append(Constant.RETURN_SUCCESS).append(Constant.CURLY_NEWLINE ).append("\n");
        }
        serviceImplClassCreation.append("\n}");

        return serviceImplClassCreation.toString( );
    }


    public String successResponseClassCreation(String packageName) {
        StringBuilder responseFile = new StringBuilder( );
        responseFile.append(Constant.PACKAGE_SPACE).append(packageName).append(".response;\n");
        responseFile.append("import lombok.Getter;\n" + "import lombok.Setter;\n\n");
        responseFile.append("import java.io.Serializable;\n");

        responseFile.append("@Getter\n" + "@Setter\n");
        responseFile.append("public class SuccessResponse<T> implements Serializable { \n\n");
        responseFile.append("    private int statusCode = 200;\n" +
                "    private String statusMessage = \"Success\";\n" +
                "    private transient T data;\n\n");
        responseFile.append("}\n");
        return responseFile.toString( );
    }

    public String pageResponseClassCreation(String packageName) {
        StringBuilder responseFile = new StringBuilder( );
        responseFile.append(Constant.PACKAGE_SPACE).append(packageName).append(".response;\n\n");
        responseFile.append("import lombok.Getter;\n" + "import lombok.Setter;\n\n");
        responseFile.append("import java.io.Serializable;\n\n");
        responseFile.append("@Getter\n" + "@Setter\n");
        responseFile.append("public class PageResponse<T> implements Serializable {\n");
        responseFile.append("\n");
        responseFile.append("    private static final long serialVersionUID = 1L;\n");
        responseFile.append("    private long totalRecordCount;\n");
        responseFile.append("    boolean hasNext;\n");
        responseFile.append("    boolean hasPrevious;\n");
        responseFile.append("    private transient T data;\n\n");
        responseFile.append("}\n");
        return responseFile.toString( );
    }
    public String swaggerConstant (String packageName, String artifactName){
        StringBuilder responseFile = new StringBuilder();
        responseFile.append(Constant.PACKAGE_SPACE).append(packageName).append(".response;\n\n");
        responseFile.append("public class SwaggerConstant  {\n");
        responseFile.append("\n");
        responseFile.append("\t\tpublic static final String AUTHENTICATION = ").append('"').append(artifactName).append(" Authentication"+'"').append(";\n");
        responseFile.append("}\n");
        return responseFile.toString();
    }


}

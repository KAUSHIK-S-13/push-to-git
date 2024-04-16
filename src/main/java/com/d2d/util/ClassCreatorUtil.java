package com.d2d.util;

import com.d2d.constant.Constant;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ErrorCode;
import com.d2d.dto.Column;
import com.d2d.dto.DbDesignTables;
import com.d2d.dto.EnableSecurityDTO;
import com.d2d.dto.MultipleRecordDTO;
import com.d2d.dto.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ClassCreatorUtil {

    @Autowired
    CommonUtil commonUtil;

    public String modelClassFileZipEntryString(Integer databaseEngineId, Table table, String packageName, String bootVersion, Boolean isDeletedPresent) throws CustomValidationException {

        StringBuilder stringBuilder = new StringBuilder( );
        String className = commonUtil.CapitalizeClassName(table.getName( ));
        stringBuilder.append(Constant.PACKAGE).append(packageName).append(".model;\n\n");
        if (bootVersion.startsWith("2")) {
            stringBuilder.append("import javax.persistence.*;\n");
        } else {
            stringBuilder.append("import jakarta.persistence.*;\n");
        }

        stringBuilder.append(Constant.IMPORT_LOMBOK_GETTER + Constant.IMPORT_LOMBOK_SETTER);
        if (table.getCrudDTO( ).getDelete( ).getIsChecked( ) && isDeletedPresent && !table.getCrudDTO( ).getDelete( ).getHardDelete( )
                && table.getCrudDTO( ).getDelete( ).getSoftDelete( )) {
            stringBuilder.append("import org.hibernate.annotations.SQLDelete;\n");
        }
        boolean flag = table.getColumns( ).stream( )
                .anyMatch(column -> column.getDatatype( ).startsWith(Constant.TIMESTAMP) || column.getDatatype( ).startsWith(Constant.DATE_TIME) ||
                        column.getDatatype( ).startsWith("YEAR") || column.getDatatype( ).startsWith("DATE") || column.getDatatype( ).startsWith("TIME") ||
                        column.getDatatype( ).startsWith("date") || column.getDatatype( ).startsWith("time") || column.getDatatype( ).startsWith(Constant.TIMETZ)
                        || column.getDatatype( ).startsWith(Constant.TIMES_STAMP) || column.getDatatype( ).startsWith(Constant.TIMES_TAMPTZ)
                        || column.getDatatype( ).startsWith("datetime") || column.getDatatype( ).startsWith("datetime2")
                        || column.getDatatype( ).startsWith(Constant.DATE_TIME_OFF_SET) || column.getDatatype( ).startsWith("smalldatetime"));
        if (flag) {
            stringBuilder.append("import java.sql.Timestamp;\n\n");
            stringBuilder.append("import org.hibernate.annotations.CreationTimestamp;\n" + "import org.hibernate.annotations.UpdateTimestamp;\n\n");
        }

        boolean flagBlob = table.getColumns( ).stream( )
                .anyMatch(column -> column.getDatatype( ).startsWith("BLOB") || column.getDatatype( ).startsWith("TINYBLOB")
                        || column.getDatatype( ).startsWith("LONGBLOB") || column.getDatatype( ).startsWith("MEDIUMBLOB")
                        || column.getDatatype( ).startsWith("LONG RAW"));
        if (flagBlob) {
            stringBuilder.append("import java.sql.Blob;\n\n");
        }

        boolean flagClob = table.getColumns( ).stream( )
                .anyMatch(column -> column.getDatatype( ).startsWith("CLOB") || column.getDatatype( ).startsWith("LONG")
                        || column.getDatatype( ).startsWith("NCLOB"));
        if (flagClob) {
            stringBuilder.append("import java.sql.Clob;\n\n");
        }

        stringBuilder.append(Constant.GETTER + Constant.SETTER);
        stringBuilder.append("@Entity\n");
        stringBuilder.append("@Table(name = \"").append(table.getName( )).append(Constant.CLOSE_PARENTHESIS);
        if (table.getCrudDTO( ).getDelete( ).getIsChecked( ) && isDeletedPresent && !table.getCrudDTO( ).getDelete( ).getHardDelete( )
                && table.getCrudDTO( ).getDelete( ).getSoftDelete( )) {
            stringBuilder.append("@SQLDelete(sql = \"UPDATE ").append(table.getName( )).append(" SET deleted_flag =  true WHERE id=?\")\n");
        }
        stringBuilder.append(Constant.PUBLIC_CLASS).append(className).append(Constant.OPEN_PARENTHESIS);

        for (Column column : table.getColumns( )) {
            if (column.getName( ) == null || column.getName( ).isEmpty( ) || column.getDatatype( ) == null || column.getDatatype( ).isEmpty( )) {
                throw new CustomValidationException(ErrorCode.D2D_2);
            }
            String fieldName = commonUtil.toCamelCase(column.getName( ));
            String[] dataTypeSplit = column.getDatatype( ).split("\\(");
            Map<Integer, Map<String, String>> map = commonUtil.getJavaType( );
            String javaType = map.get(databaseEngineId).get(dataTypeSplit[0]);
            if (column.getKeytype( ) != null && column.getKeytype( ).equals("PK")) {
                stringBuilder.append("  @Id\n");
                stringBuilder.append("  @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
                stringBuilder.append(Constant.COLUMN_NAME).append(column.getName( )).append(Constant.CLOSE_PARENTHESIS);
                stringBuilder.append(Constant.PRIVATE).append(javaType).append(" ").append(fieldName).append(Constant.SINGLE_SLASH);
            } else if (column.getKeytype( ) != null && !column.getKeytype( ).isEmpty( ) &&
                    column.getKeytype( ).equals("FK") && column.getMapping( ) != null &&
                    !column.getMapping( ).isEmpty( ) && column.getMapping( ).equalsIgnoreCase("OneToOne")) {
                String tableReference = column.getTableReference( );
                String refLower = tableReference.substring(0, 1).toLowerCase( ) + tableReference.substring(1);
                stringBuilder.append("  @OneToOne\n");
                stringBuilder.append("  @JoinColumn(name = \"").append(column.getName( )).append(Constant.CLOSE_PARENTHESIS);
                stringBuilder.append(Constant.PRIVATE).append(commonUtil.capitalize(commonUtil.toCamelCase(tableReference))).append(" ").append(commonUtil.toCamelCase(refLower)).append(Constant.SINGLE_SLASH);
            } else if (column.getKeytype( ) != null && !column.getKeytype( ).isEmpty( ) &&
                    column.getKeytype( ).equals("FK") && column.getMapping( ) != null &&
                    !column.getMapping( ).isEmpty( ) && column.getMapping( ).equalsIgnoreCase("ManyToOne")) {
                String tableReference = column.getTableReference( );
                String refLower = tableReference.substring(0, 1).toLowerCase( ) + tableReference.substring(1);
                stringBuilder.append("  @ManyToOne\n");
                stringBuilder.append("  @JoinColumn(name = \"").append(column.getName( )).append(Constant.CLOSE_PARENTHESIS);
                stringBuilder.append(Constant.PRIVATE).append(commonUtil.capitalize(commonUtil.toCamelCase(tableReference))).append(" ").append(commonUtil.toCamelCase(refLower)).append(Constant.SINGLE_SLASH);
            } else if (column.getName( ) != null && column.getName( ).equals("created_at") && (column.getDatatype( ).startsWith(Constant.TIMESTAMP) ||
                    column.getDatatype( ).startsWith(Constant.DATE_TIME) || column.getDatatype( ).startsWith("YEAR") || column.getDatatype( ).startsWith("DATE") ||
                    column.getDatatype( ).startsWith("TIME") || column.getDatatype( ).startsWith("date") || column.getDatatype( ).startsWith("time") ||
                    column.getDatatype( ).startsWith(Constant.TIMETZ) || column.getDatatype( ).startsWith(Constant.TIMES_STAMP) || column.getDatatype( ).startsWith(Constant.TIMES_TAMPTZ)
                    || column.getDatatype( ).startsWith(Constant.DATE_TIMES) || column.getDatatype( ).startsWith(Constant.DATE_TIME2)
                    || column.getDatatype( ).startsWith(Constant.DATE_TIME_OFF_SET) || column.getDatatype( ).startsWith(Constant.SMALL_DATE_TIME))) {
                stringBuilder.append(Constant.COLUMN_NAME).append(column.getName( )).append(Constant.CLOSE_PARENTHESIS);
                stringBuilder.append("  @CreationTimestamp\n");
                stringBuilder.append(Constant.PRIVATE).append(javaType).append(" ").append(fieldName).append(Constant.SINGLE_SLASH);
            } else if (column.getName( ) != null && column.getName( ).equals("modified_at") && (column.getDatatype( ).startsWith(Constant.TIMESTAMP) ||
                    column.getDatatype( ).startsWith(Constant.DATE_TIME) || column.getDatatype( ).startsWith("YEAR") || column.getDatatype( ).startsWith("DATE") ||
                    column.getDatatype( ).startsWith("TIME") || column.getDatatype( ).startsWith("date") || column.getDatatype( ).startsWith("time") ||
                    column.getDatatype( ).startsWith(Constant.TIMETZ) || column.getDatatype( ).startsWith(Constant.TIMES_STAMP) || column.getDatatype( ).startsWith(Constant.TIMES_TAMPTZ)
                    || column.getDatatype( ).startsWith(Constant.DATE_TIMES) || column.getDatatype( ).startsWith(Constant.DATE_TIME2)
                    || column.getDatatype( ).startsWith(Constant.DATE_TIME_OFF_SET) || column.getDatatype( ).startsWith(Constant.SMALL_DATE_TIME))) {
                stringBuilder.append(Constant.COLUMN_NAME).append(column.getName( )).append(Constant.CLOSE_PARENTHESIS);
                stringBuilder.append("  @UpdateTimestamp\n");
                stringBuilder.append(Constant.PRIVATE).append(javaType).append(" ").append(fieldName).append(Constant.SINGLE_SLASH);
            } else {
                stringBuilder.append(Constant.COLUMN_NAME).append(column.getName( )).append(Constant.CLOSE_PARENTHESIS);
                stringBuilder.append(Constant.PRIVATE).append(javaType).append(" ").append(fieldName).append(Constant.SINGLE_SLASH);
            }
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString( );
    }

    public String repositoryClassFileZipEntryString(EnableSecurityDTO enableSecurityDTO, Integer databaseEngineId, Table table, String packageName) throws CustomValidationException {

        String className = commonUtil.CapitalizeClassName(table.getName( ));
        MultipleRecordDTO multipleRecordDTO = table.getCrudDTO( ).getMultipleRecord( );
        Boolean pagination = multipleRecordDTO.getPagination( );
        Boolean isSearch = multipleRecordDTO.getIsSearch( );
        Boolean filter = multipleRecordDTO.getFilter( );
        Boolean sort = multipleRecordDTO.getSort( );
        Map<Integer, Map<String, String>> map = commonUtil.getJavaType( );
        StringBuilder stringBuilder = new StringBuilder( );
        stringBuilder.append(Constant.PACKAGE).append(packageName).append(".repository;\n\n");
        stringBuilder.append("import org.springframework.data.jpa.repository.JpaRepository;\n");
        stringBuilder.append("import org.springframework.stereotype.Repository;\n");
        stringBuilder.append("import org.springframework.data.jpa.repository.Query;\n");
        stringBuilder.append("import ").append(packageName).append(".model.").append(className).append(Constant.SINGLE_SLASH);
        if (enableSecurityDTO.getIsSecurityRequired( ) && enableSecurityDTO.getSecurityMethod( ) == 1) {
            if (Objects.equals(table.getName( ), enableSecurityDTO.getAuthTableName( ))) {
                stringBuilder.append("import java.util.Optional;\n\n");
            }
        }
        if (table.getCrudDTO( ).getMultipleRecord( ).getIsChecked( ) && pagination) {
            stringBuilder.append("import org.springframework.data.domain.Page;\n" +
                    "import org.springframework.data.domain.Pageable;\n");
        }
        if (table.getCrudDTO( ).getMultipleRecord( ).getIsChecked( )) {
            stringBuilder.append(Constant.IMPORT_JAVA_UTIL_LIST);
        }

        stringBuilder.append("@Repository\n");
        for (Column column : table.getColumns( )) {
            if (column.getKeytype( ) != null && !column.getKeytype( ).isEmpty( ) && column.getKeytype( ).equals("PK")) {
                stringBuilder.append("public interface ").append(className).append("Repository extends JpaRepository<").append(className).append(", ");
                String[] dataTypeSplit = column.getDatatype( ).split("\\(");
                String javaType = map.get(databaseEngineId).get(dataTypeSplit[0]);
                stringBuilder.append(javaType).append("> {\n\n");
            }
            if (enableSecurityDTO.getIsSecurityRequired( ) && enableSecurityDTO.getSecurityMethod( ) == 1) {
                if (Objects.equals(table.getName( ), enableSecurityDTO.getAuthTableName( )) && Objects.equals(column.getName( ), enableSecurityDTO.getFieldOne( ))) {
                    String fieldOne = commonUtil.CapitalizeClassName(enableSecurityDTO.getFieldOne( ));
                    String capitalizeTableName = commonUtil.CapitalizeClassName(enableSecurityDTO.getAuthTableName( ));
                    stringBuilder.append("Optional<").append(capitalizeTableName).append("> findBy").append(fieldOne).append("(String email);\n");
                }
            }
        }

        if (table.getCrudDTO( ).getMultipleRecord( ).getIsChecked( )) {
            StringBuilder queryBuilderString = new StringBuilder( );
            StringBuilder multipleRecordString = new StringBuilder( );
            List<Column> foreignKeyVariablesOnly = table.getColumns( ).stream( ).filter(column -> column.getKeytype( ) != null
                    && column.getKeytype( ).equalsIgnoreCase("FK")).collect(Collectors.toList( ));
            queryBuilderString.append("\t@Query(\"SELECT o FROM ").append(className).append(" o ");
            // With Pagination
            if (pagination) {
                multipleRecordString.append("\tPage<").append(className).append("> findAllListWithPagination(");
                if (isSearch) {
                    queryBuilderString.append(" WHERE ((:search IS NULL) OR (:search IS NOT NULL");
                    queryBuilderString.append(" AND (");
                    for (int i = 0; i < multipleRecordDTO.getSearchFieldList( ).size( ); i++) {
                        String searchFieldName = multipleRecordDTO.getSearchFieldList( ).get(i);
                        List<Column> searchColumn = table.getColumns( ).stream( ).filter(column -> column.getName( ) != null &&
                                column.getName( ).equalsIgnoreCase(searchFieldName)).collect(Collectors.toList( ));
                        if (!(searchColumn.size( ) == 1)) {
                            throw new CustomValidationException(ErrorCode.D2D_13);
                        }
                        String[] StringSplit = searchColumn.get(0).getDatatype( ).split("\\(");
                        String javaDataType = map.get(databaseEngineId).get(StringSplit[0]);
                        String columnName = commonUtil.toCamelCase(searchColumn.get(0).getName( ));
                        String columnNameInObject = columnName.substring(0, 1).toLowerCase( ) + columnName.substring(1);
                        if (i == 0) {
                            queryBuilderString.append(" o.").append(columnNameInObject).append(" ");
                        } else {
                            queryBuilderString.append(" OR o.").append(columnNameInObject).append(" ");
                        }
                        // here it may be change to like Query which is depends on the Java DataType
                        if (javaDataType.equalsIgnoreCase("STRING")) {
                            queryBuilderString.append("like %:search% ");
                        } else {
                            queryBuilderString.append("= :search ");
                        }

                    }
                    queryBuilderString.append(")");
                    queryBuilderString.append("))");
                    if (filter) {
                        queryBuilderString.append(Constant.AND);
                    }
                    multipleRecordString.append("String search,");
                }
                if (filter) {
                    for (int i = 0; i < multipleRecordDTO.getFilteredFieldList( ).size( ); i++) {
                        int finalI = i;
                        foreignKeyVariablesOnly.stream( ).forEach(column -> {
                            if (column.getName( ).equalsIgnoreCase(table.getCrudDTO( ).getMultipleRecord( ).getFilteredFieldList( ).get(finalI))) {
                                String tableReferenceClassType = commonUtil.CapitalizeClassName(column.getTableReference( ));
                                String tableReferenceClassTypeInObject = tableReferenceClassType.substring(0, 1).toLowerCase( ) + tableReferenceClassType.substring(1);
                                String[] dataTypeSplit = column.getDatatype( ).split("\\(");
                                String javaType = map.get(databaseEngineId).get(dataTypeSplit[0]);
                                queryBuilderString.append(" (( :").append(tableReferenceClassTypeInObject).append(" IS NULL) OR ( :").append(tableReferenceClassTypeInObject)
                                        .append(" IS NOT NULL AND o.").append(tableReferenceClassTypeInObject).append(".id = :")
                                        .append(tableReferenceClassTypeInObject).append("))");
                                if (!(multipleRecordDTO.getFilteredFieldList( ).size( ) - finalI == 1)) {
                                    queryBuilderString.append(Constant.AND);
                                }
                                multipleRecordString.append(javaType).append(" ").append(tableReferenceClassTypeInObject).append(",");
                            }
                        });
                    }
                }
                if (sort) {
                    String sortField = commonUtil.capitalize(multipleRecordDTO.getSortField( ));
                    queryBuilderString.append(" ORDER BY o.").append(sortField.substring(0, 1).toLowerCase( )).append(sortField.substring(1));
                }
                queryBuilderString.append("\")");
                multipleRecordString.append("Pageable paging);\n");
            }
            // WIthout Pagination
            else {
                multipleRecordString.append("\tList<").append(className).append("> findAllList(");
                if (isSearch) {
                    queryBuilderString.append(" WHERE ((:search IS NULL) OR (:search IS NOT NULL");
                    queryBuilderString.append(" AND (");
                    for (int i = 0; i < multipleRecordDTO.getSearchFieldList( ).size( ); i++) {
                        String searchFieldName = multipleRecordDTO.getSearchFieldList( ).get(i);
                        List<Column> searchColumn = table.getColumns( ).stream( ).filter(column -> column.getName( ) != null &&
                                column.getName( ).equalsIgnoreCase(searchFieldName)).collect(Collectors.toList( ));
                        if (!(searchColumn.size( ) == 1)) {
                            throw new CustomValidationException(ErrorCode.D2D_13);
                        }
                        String[] StringSplit = searchColumn.get(0).getDatatype( ).split("\\(");
                        String javaDataType = map.get(databaseEngineId).get(StringSplit[0]);
                        String columnName = commonUtil.toCamelCase(searchColumn.get(0).getName( ));
                        String columnNameInObject = columnName.substring(0, 1).toLowerCase( ) + columnName.substring(1);
                        if (i == 0) {
                            queryBuilderString.append(" o.").append(columnNameInObject).append(" ");
                        } else {
                            queryBuilderString.append(" OR o.").append(columnNameInObject).append(" ");
                        }
                        // here it may be change to like Query which is depends on the Java DataType
                        if (javaDataType.equalsIgnoreCase("STRING")) {
                            queryBuilderString.append("like %:search% ");
                        } else {
                            queryBuilderString.append("= :search ");
                        }

                    }
                    queryBuilderString.append(")");
                    queryBuilderString.append("))");
                    if (filter) {
                        queryBuilderString.append(Constant.AND);
                    }
                    multipleRecordString.append("String search");

                }
                if (filter) {
                    queryBuilderString.append(" (");
                    if (isSearch) {
                        multipleRecordString.append(",");
                    }
                    for (int i = 0; i < multipleRecordDTO.getFilteredFieldList( ).size( ); i++) {
                        int finalI = i;
                        foreignKeyVariablesOnly.stream( ).forEach(column -> {
                            if (column.getName( ).equalsIgnoreCase(table.getCrudDTO( ).getMultipleRecord( ).getFilteredFieldList( ).get(finalI))) {
                                String tableReferenceClassType = commonUtil.CapitalizeClassName(column.getTableReference( ));
                                String tableReferenceClassTypeInObject = tableReferenceClassType.substring(0, 1).toLowerCase( ) + tableReferenceClassType.substring(1);
                                String[] dataTypeSplit = column.getDatatype( ).split("\\(");
                                String javaType = map.get(databaseEngineId).get(dataTypeSplit[0]);
                                queryBuilderString.append(" (( :").append(tableReferenceClassTypeInObject).append(" IS NULL) OR ( :").append(tableReferenceClassTypeInObject)
                                        .append(" IS NOT NULL AND o.").append(tableReferenceClassTypeInObject).append(".id = :")
                                        .append(tableReferenceClassTypeInObject).append("))");
                                if (!(multipleRecordDTO.getFilteredFieldList( ).size( ) - finalI == 1)) {
                                    queryBuilderString.append(Constant.AND);
                                }
                                multipleRecordString.append(javaType).append(" ").append(tableReferenceClassTypeInObject).append(",");
                            }
                        });
                    }
                }
                if (sort) {
                    String sortField = commonUtil.capitalize(multipleRecordDTO.getSortField( ));
                    queryBuilderString.append(" ORDER BY o.").append(sortField.substring(0, 1).toLowerCase( )).append(sortField.substring(1));
                }
                multipleRecordString.append(");\n");
                queryBuilderString.append("\")");
            }
            stringBuilder.append(queryBuilderString.toString( )).append("\n");
            stringBuilder.append(multipleRecordString);
        }

        stringBuilder.append(Constant.CLOSE);
        return stringBuilder.toString( );
    }

    public String dtoClassFileZipEntryString(Integer databaseEngineId, Table table, String packageName, String dtoType) {
        StringBuilder dtoBuilder = new StringBuilder( );
        String className = commonUtil.CapitalizeClassName(table.getName( ));

        dtoBuilder.append(Constant.PACKAGE).append(packageName).append(".dto;\n\n");
        dtoBuilder.append(Constant.IMPORT_LOMBOK_GETTER +  Constant.IMPORT_LOMBOK_SETTER);
        boolean flag = table.getColumns( ).stream( )
                .anyMatch(column -> column.getDatatype( ).startsWith(Constant.TIMESTAMP) || column.getDatatype( ).startsWith(Constant.DATE_TIME) ||
                        column.getDatatype( ).startsWith("YEAR") || column.getDatatype( ).startsWith("DATE") || column.getDatatype( ).startsWith("TIME") ||
                        column.getDatatype( ).startsWith("date") || column.getDatatype( ).startsWith("time") || column.getDatatype( ).startsWith(Constant.TIMETZ)
                        || column.getDatatype( ).startsWith(Constant.TIMES_STAMP) || column.getDatatype( ).startsWith(Constant.TIMES_TAMPTZ)
                        || column.getDatatype( ).startsWith(Constant.DATE_TIMES) || column.getDatatype( ).startsWith(Constant.DATE_TIME2)
                        || column.getDatatype( ).startsWith(Constant.DATE_TIME_OFF_SET) || column.getDatatype( ).startsWith(Constant.SMALL_DATE_TIME));
        if (flag) {
            dtoBuilder.append("import java.sql.Timestamp;\n\n");
        }

        boolean flagBlob = table.getColumns( ).stream( )
                .anyMatch(column -> column.getDatatype( ).startsWith("BLOB") || column.getDatatype( ).startsWith("TINYBLOB")
                        || column.getDatatype( ).startsWith("LONGBLOB") || column.getDatatype( ).startsWith("MEDIUMBLOB")
                        || column.getDatatype( ).startsWith("LONG RAW"));
        if (flagBlob) {
            dtoBuilder.append("import java.sql.Blob;\n\n");
        }

        boolean flagClob = table.getColumns( ).stream( )
                .anyMatch(column -> column.getDatatype( ).startsWith("CLOB") || column.getDatatype( ).startsWith("LONG")
                        || column.getDatatype( ).startsWith("NCLOB"));
        if (flagClob) {
            dtoBuilder.append("import java.sql.Clob;\n\n");
        }

        dtoBuilder.append(Constant.GETTER + Constant.SETTER);
        dtoBuilder.append(Constant.PUBLIC_CLASS).append(className);
        if (dtoType.equalsIgnoreCase("RequestDTO")) {
            dtoBuilder.append("Request");
        }
        dtoBuilder.append("DTO {\n");
        for (Column column : table.getColumns( )) {
            String fieldName = commonUtil.toCamelCase(column.getName( ));
            String[] dataTypeSplit = column.getDatatype( ).split("\\(");
            Map<Integer, Map<String, String>> map = commonUtil.getJavaType( );
            String javaType = map.get(databaseEngineId).get(dataTypeSplit[0]);
            if (column.getKeytype( ) != null && !column.getKeytype( ).isEmpty( ) && column.getKeytype( ).equals("FK")) {
                String tableReference = column.getTableReference( );
                if (dtoType.equalsIgnoreCase("RequestDTO")) {
                    dtoBuilder.append(Constant.SLASH_PRIVATE).append(javaType)
                            .append(" ").append(commonUtil.toCamelCase(tableReference)).append("Id").append(Constant.SINGLE_SLASH);
                } else {
                    dtoBuilder.append(Constant.SLASH_PRIVATE).append(commonUtil.CapitalizeClassName(tableReference)).append("DTO").append(" ").append(commonUtil.toCamelCase(tableReference)).append(Constant.SINGLE_SLASH);
                }
            } else {
                dtoBuilder.append(Constant.SLASH_PRIVATE).append(javaType).append(" ").append(fieldName).append(Constant.SINGLE_SLASH);
            }
        }
        dtoBuilder.append(Constant.CLOSE);


        return dtoBuilder.toString( );
    }

    public boolean isValidSpringDetails(DbDesignTables tables) {
        if (tables.getSpringBootBasicDTO( ).getType( ) == null || tables.getSpringBootBasicDTO( ).getType( ).isEmpty( )) {
            return true;
        }

        if (tables.getSpringBootBasicDTO( ).getLanguage( ) == null || tables.getSpringBootBasicDTO( ).getLanguage( ).isEmpty( )) {
            return true;
        }

        if (tables.getSpringBootBasicDTO( ).getBootVersion( ) == null || tables.getSpringBootBasicDTO( ).getBootVersion( ).isEmpty( )) {
            return true;
        }

        if (tables.getSpringBootBasicDTO( ).getBaseDir( ) == null || tables.getSpringBootBasicDTO( ).getBaseDir( ).isEmpty( )) {
            return true;
        }

        if (tables.getSpringBootBasicDTO( ).getGroupId( ) == null || tables.getSpringBootBasicDTO( ).getGroupId( ).isEmpty( )) {
            return true;
        }

        if (tables.getSpringBootBasicDTO( ).getArtifactId( ) == null || tables.getSpringBootBasicDTO( ).getArtifactId( ).isEmpty( )) {
            return true;
        }
        if (tables.getSpringBootBasicDTO( ).getName( ) == null || tables.getSpringBootBasicDTO( ).getName( ).isEmpty( )) {
            return true;
        }

        if (tables.getSpringBootBasicDTO( ).getDescription( ) == null || tables.getSpringBootBasicDTO( ).getDescription( ).isEmpty( )) {
            return true;
        }
        if (tables.getSpringBootBasicDTO( ).getPackageName( ) == null || tables.getSpringBootBasicDTO( ).getPackageName( ).isEmpty( )) {
            return true;
        }

        if (tables.getSpringBootBasicDTO( ).getPackaging( ) == null || tables.getSpringBootBasicDTO( ).getPackaging( ).isEmpty( )) {
            return true;
        }
        if (tables.getSpringBootBasicDTO( ).getJavaVersion( ) == null || tables.getSpringBootBasicDTO( ).getJavaVersion( ).isEmpty( )) {
            return true;
        }

        if (tables.getSpringBootBasicDTO( ).getDependencies( ) == null || tables.getSpringBootBasicDTO( ).getDependencies( ).isEmpty( )) {
            return true;
        }

        return false;
    }

    public String applicationPropertiesString(DbDesignTables tables) {
        Integer databaseName = tables.getDatabaseEngineId( );
        String[] splitDependency = tables.getSpringBootBasicDTO( ).getDependencies( ).split(",");
        Boolean dependencyCheck = false;
        if (databaseName == 1) {
            for (String s : splitDependency) {
                if (s.equalsIgnoreCase("mysql")) {
                    dependencyCheck = true;
                }
            }
        } else if (databaseName == 4) {
            for (String s : splitDependency) {
                if (s.equalsIgnoreCase("postgresql")) {
                    dependencyCheck = true;
                }
            }
        } else if (databaseName == 3) {
            for (String s : splitDependency) {
                if (s.equalsIgnoreCase("oracle")) {
                    dependencyCheck = true;
                }
            }
        } else if (databaseName == 5) {
            for (String s : splitDependency) {
                if (s.equalsIgnoreCase("sqlserver")) {
                    dependencyCheck = true;
                }
            }
        }

        String mainPackageName = "spring.datasource.";
        StringBuilder propertyFileCreation = new StringBuilder( );

        propertyFileCreation.append("spring.application.name = ");
        propertyFileCreation.append(tables.getSpringBootBasicDTO( ).getArtifactId( )).append("-application").append("\n\n");
        if (tables.getEnableSecurityDTO( ).getIsSecurityRequired( ) && tables.getEnableSecurityDTO( ).getSecurityMethod( ) == 2) {
            if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("2")) {
                propertyFileCreation.append(mainPackageName).append("spring.main.allow-bean-definition-overriding=true\n");
                propertyFileCreation.append(mainPackageName).append("security.oauth2.resource.filter-order=3 \n");
                propertyFileCreation.append("app.clientId= \n");
                propertyFileCreation.append("app.clientSecret= \n");
            } else if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("3")) {
                propertyFileCreation.append("spring.security.oauth2.resourceserver.jwt.issuer-uri = \n");
                propertyFileCreation.append("spring.security.oauth2.resourceserver.jwt.jwk-set-uri = \n");
                propertyFileCreation.append("jwt.auth.converter.resource-id = \n");
                propertyFileCreation.append("jwt.auth.converter.principle-attribute = \n");
            }

        } else if (tables.getEnableSecurityDTO( ).getIsSecurityRequired( ) && tables.getEnableSecurityDTO( ).getSecurityMethod( ) == 3) {
            if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("2")) {
                propertyFileCreation.append("keycloak.realm = \n");
                propertyFileCreation.append("keycloak.auth-server-url = \n");
                propertyFileCreation.append("keycloak.resource = \n");
                propertyFileCreation.append("keycloak.public-client = true\n");
                propertyFileCreation.append("keycloak.bearer-only = true\n");

            } else if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("3")) {
                propertyFileCreation.append("spring.security.oauth2.resourceserver.jwt.issuer-uri = \n");
                propertyFileCreation.append("spring.security.oauth2.resourceserver.jwt.jwk-set-uri = \n");
                propertyFileCreation.append("jwt.auth.converter.resource-id = \n");
                propertyFileCreation.append("jwt.auth.converter.principle-attribute = \n");
            }

        }
        if (dependencyCheck) {
            propertyFileCreation.append(mainPackageName).append("driver-class-name = ");
            if (databaseName == 1) {
                propertyFileCreation.append("com.mysql.cj.jdbc.Driver\n");
                propertyFileCreation.append(mainPackageName).append("url = jdbc:mysql://\n");
            } else if (databaseName == 4) {
                propertyFileCreation.append("org.postgresql.Driver\n");
                propertyFileCreation.append(mainPackageName).append("url = jdbc:postgresql://\n");
            } else if (databaseName == 3) {
                propertyFileCreation.append("oracle.jdbc.driver.OracleDriver\n");
                propertyFileCreation.append(mainPackageName).append("url = jdbc:oracle:\n");
            } else if (databaseName == 5) {
                propertyFileCreation.append("com.microsoft.sqlserver.jdbc.SQLServerDriver\n");
                propertyFileCreation.append(mainPackageName).append("url = jdbc:sqlserver://\n");
            }

            propertyFileCreation.append(mainPackageName).append("username = \n");
            propertyFileCreation.append(mainPackageName).append("password = \n");
            propertyFileCreation.append("spring.main.allow-circular-references=true \n");

            propertyFileCreation.append("spring.jpa.properties.hibernate.dialect = ").append("org.hibernate.dialect.");

            if (databaseName == 1) {
                propertyFileCreation.append("MySQL5InnoDBDialect\n");
            } else if (databaseName == 4) {
                propertyFileCreation.append("PostgreSQLDialect\n");
            } else if (databaseName == 3) {
                propertyFileCreation.append("Oracle10gDialect\n");
            } else if (databaseName == 5) {
                propertyFileCreation.append("SQLServer2008Dialect\n");
            }

            propertyFileCreation.append("spring.jpa.hibernate.ddl-auto = none\n");

            if (tables.getEnableSecurityDTO( ).getIsSecurityRequired( ) && tables.getEnableSecurityDTO( ).getSecurityMethod( ) == 1) {
                propertyFileCreation.append("jwt.secret=Update with your secret key\n");
            }
        }

        return propertyFileCreation.toString( );
    }

    public String addDependencysString(DbDesignTables tables) {
        StringBuilder propertyFileCreation = new StringBuilder( );
        if (tables.isSwagger( )) {
            if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("3")) {
                propertyFileCreation = new StringBuilder( );
                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append("\t\t\t<groupId>org.springdoc</groupId>\n");
                propertyFileCreation.append("\t\t\t<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>\n");
                propertyFileCreation.append("\t\t\t<version>2.1.0</version>\n");
                propertyFileCreation.append(Constant.CLOSE_DEPENDENCY);
            } else if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("2")) {
                propertyFileCreation = new StringBuilder( );
                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append("\t\t\t<groupId>org.springdoc</groupId>\n");
                propertyFileCreation.append("\t\t\t<artifactId>springdoc-openapi-ui</artifactId>\n");
                propertyFileCreation.append("\t\t\t<version>1.6.11</version>\n");
                propertyFileCreation.append(Constant.CLOSE_DEPENDENCY);
            }
        }
        if (tables.getEnableSecurityDTO( ).getIsSecurityRequired( ) && tables.getEnableSecurityDTO( ).getSecurityMethod( ) == 1) {
            if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("3")) {
                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append(Constant.JASON_WEB_TOKEN);
                propertyFileCreation.append("\t\t\t<artifactId>jjwt-api</artifactId>\n");
                propertyFileCreation.append(Constant.JACKSON_VERSION);
                propertyFileCreation.append(Constant.CLOSE_DEPENDENCY);
                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append(Constant.JASON_WEB_TOKEN);
                propertyFileCreation.append("\t\t\t<artifactId>jjwt-impl</artifactId>\n");
                propertyFileCreation.append(Constant.JACKSON_VERSION);
                propertyFileCreation.append(Constant.CLOSE_DEPENDENCY);
                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append(Constant.JASON_WEB_TOKEN);
                propertyFileCreation.append("\t\t\t<artifactId>jjwt-jackson</artifactId>\n");
                propertyFileCreation.append(Constant.JACKSON_VERSION);
                propertyFileCreation.append(Constant.CLOSE_DEPENDENCY);
                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append(Constant.SPRING_FRAMEWORK);
                propertyFileCreation.append(Constant.SPRING_STARTER_SECURITY);
                propertyFileCreation.append(Constant.CLOSE_DEPENDENCY);
            } else {
                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append(Constant.JASON_WEB_TOKEN);
                propertyFileCreation.append("\t\t\t<artifactId>jjwt</artifactId>\n");
                propertyFileCreation.append("\t\t\t<version>0.9.1</version>\n");
                propertyFileCreation.append(Constant.CLOSE_DEPENDENCY);
                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append(Constant.SPRING_FRAMEWORK);
                propertyFileCreation.append(Constant.SPRING_STARTER_SECURITY);
                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
            }

        }
        if (tables.getEnableSecurityDTO( ).getIsSecurityRequired( ) && tables.getEnableSecurityDTO( ).getSecurityMethod( ) == 2) {
            if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("2")) {
                propertyFileCreation.append("\t\t<dependency>\n");
                propertyFileCreation.append("\t\t\t<groupId>jakarta.xml.bind</groupId>\n");
                propertyFileCreation.append("\t\t\t<artifactId>jakarta.xml.bind-api</artifactId>\n");
                propertyFileCreation.append("\t\t\t<version>2.3.2</version>\n");
                propertyFileCreation.append(Constant.CLOSE_DEPENDENCY);

                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append("\t\t\t<groupId>org.glassfish.jaxb</groupId>\n");
                propertyFileCreation.append("\t\t\t<artifactId>jaxb-runtime</artifactId>\n");
                propertyFileCreation.append("\t\t\t<version>2.3.2</version>\n");
                propertyFileCreation.append(Constant.DEPENDENCY);

                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append("\t\t\t<groupId>org.springframework.security.oauth</groupId>\n");
                propertyFileCreation.append("\t\t\t<artifactId>spring-security-oauth2</artifactId>\n");
                propertyFileCreation.append("\t\t\t<version>2.3.0.RELEASE</version>\n");
                propertyFileCreation.append(Constant.DEPENDENCY);

                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append("\t\t\t<groupId>org.springframework.security</groupId>\n");
                propertyFileCreation.append("\t\t\t<artifactId>spring-security-jwt</artifactId>\n");
                propertyFileCreation.append("\t\t\t<version>1.0.9.RELEASE</version>\n");
                propertyFileCreation.append(Constant.DEPENDENCY);
            } else if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("3")) {
                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append(Constant.SPRING_FRAMEWORK);
                propertyFileCreation.append("\t\t\t<artifactId>spring-boot-starter-oauth2-resource-server</artifactId>\n");
                propertyFileCreation.append(Constant.DEPENDENCY);

                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append(Constant.SPRING_FRAMEWORK);
                propertyFileCreation.append(Constant.SPRING_STARTER_SECURITY);
                propertyFileCreation.append(Constant.DEPENDENCY);

                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append("\t\t\t<groupId>org.projectlombok</groupId>\n");
                propertyFileCreation.append("\t\t\t<artifactId>lombok</artifactId>\n");
                propertyFileCreation.append("\t\t\t<optional>true</optional>\n");
                propertyFileCreation.append(Constant.DEPENDENCY);

            }

        }
        if (tables.getEnableSecurityDTO( ).getIsSecurityRequired( ) && tables.getEnableSecurityDTO( ).getSecurityMethod( ) == 3) {
            if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("2")) {
                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append("\t\t\t<groupId>org.keycloak</groupId>\n");
                propertyFileCreation.append("\t\t\t<artifactId>keycloak-spring-boot-starter</artifactId>\n");
                propertyFileCreation.append(Constant.DEPENDENCY);

                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append(Constant.SPRING_FRAMEWORK);
                propertyFileCreation.append(Constant.SPRING_STARTER_SECURITY);
                propertyFileCreation.append(Constant.DEPENDENCY);
            } else if (tables.getSpringBootBasicDTO( ).getBootVersion( ).startsWith("3")) {
                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append(Constant.SPRING_FRAMEWORK);
                propertyFileCreation.append("\t\t\t<artifactId>spring-boot-starter-oauth2-resource-server</artifactId>\n");
                propertyFileCreation.append(Constant.DEPENDENCY);

                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append(Constant.SPRING_FRAMEWORK);
                propertyFileCreation.append(Constant.SPRING_STARTER_SECURITY);
                propertyFileCreation.append(Constant.DEPENDENCY);

                propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
                propertyFileCreation.append("\t\t\t<groupId>org.projectlombok</groupId>\n");
                propertyFileCreation.append("\t\t\t<artifactId>lombok</artifactId>\n");
                propertyFileCreation.append("\t\t\t<optional>true</optional>\n");
                propertyFileCreation.append(Constant.DEPENDENCY);

            }


        }
        propertyFileCreation.append(Constant.OPEN_DEPENDENCY);
        propertyFileCreation.append("\t\t\t<groupId>org.modelmapper</groupId>\n");
        propertyFileCreation.append("\t\t\t<artifactId>modelmapper</artifactId>\n");
        propertyFileCreation.append("\t\t\t<version>2.4.4</version>\n");
        propertyFileCreation.append(Constant.DEPENDENCY);


        return propertyFileCreation.toString( );
    }

    public String addDependencysKeyCloakManagementString(DbDesignTables tables) {
        StringBuilder propertyFileCreation = new StringBuilder( );
        propertyFileCreation.append("    <dependencyManagement>\n");
        propertyFileCreation.append("        <dependencies>\n");
        propertyFileCreation.append("            <dependency>\n");
        propertyFileCreation.append("                <groupId>org.keycloak.bom</groupId>\n");
        propertyFileCreation.append("                <artifactId>keycloak-adapter-bom</artifactId>\n");
        propertyFileCreation.append("                <version>21.1.1</version>\n");
        propertyFileCreation.append("                <type>pom</type>\n");
        propertyFileCreation.append("                <scope>import</scope>\n");
        propertyFileCreation.append("            </dependency>\n");
        propertyFileCreation.append("        </dependencies>\n");
        propertyFileCreation.append("    </dependencyManagement>\n");
        return propertyFileCreation.toString( );
    }

    public String apiErrorClass(String packageName) {

        StringBuilder stringBuilder = new StringBuilder( );
        stringBuilder.append(Constant.PACKAGE).append(packageName).append(Constant.EXCEPTION);
        stringBuilder.append("import com.fasterxml.jackson.annotation.JsonFormat;\n");
        stringBuilder.append("import lombok.AllArgsConstructor;\n").append(Constant.IMPORT_LOMBOK_GETTER);
        stringBuilder.append("import lombok.Setter;\n");
        stringBuilder.append("import org.springframework.http.HttpStatus;\n");
        stringBuilder.append("import java.time.LocalDateTime;\n");
        stringBuilder.append(Constant.IMPORT_JAVA_UTIL_LIST);
        stringBuilder.append(Constant.GETTER);
        stringBuilder.append(Constant.SETTER).append("@AllArgsConstructor\n");
        stringBuilder.append(Constant.PUBLIC_CLASS).append("ApiError ").append(Constant.DOUBLE_NEWLINE);
        stringBuilder.append("\t@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = \"dd-MM-yyyy hh:mm:ss\")\n");
        stringBuilder.append("\tprivate LocalDateTime timestamp;\n");
        stringBuilder.append("\tprivate HttpStatus status;\n");
        stringBuilder.append("\tprivate String message;\n");
        stringBuilder.append("\tprivate List errors;\n");
        stringBuilder.append(Constant.CLOSE);
        return stringBuilder.toString( );
    }

    public String ResponseEntityBuilder(String packageName) {

        StringBuilder stringBuilder = new StringBuilder( );
        stringBuilder.append(Constant.PACKAGE).append(packageName).append(Constant.EXCEPTION);
        stringBuilder.append("import org.springframework.http.ResponseEntity;\n");
        stringBuilder.append(Constant.PUBLIC_CLASS).append("ResponseEntityBuilder").append(Constant.OPEN_PARENTHESIS);
        stringBuilder.append("\tpublic static ResponseEntity<Object> build(ApiError apiError)").append(Constant.DOUBLE_NEWLINE);
        stringBuilder.append("\t\treturn new ResponseEntity<>( apiError, apiError.getStatus( ) );\n");
        stringBuilder.append(Constant.TAB_NEWLINE);
        stringBuilder.append(Constant.CLOSE);
        return stringBuilder.toString( );
    }
    public String customValidationException(String packageName) {

        StringBuilder stringBuilder = new StringBuilder( );
        stringBuilder.append(Constant.PACKAGE).append(packageName).append(Constant.EXCEPTION);
        stringBuilder.append("import java.io.Serial;\n");
        stringBuilder.append(Constant.PUBLIC_CLASS).append("CustomValidationException  extends RuntimeException").append(Constant.DOUBLE_NEWLINE);
        stringBuilder.append( "@Serial\n");
        stringBuilder.append("private static final long serialVersionUID = 1L;\n");
        stringBuilder.append("public CustomValidationException(String msg) {\n" );
        stringBuilder.append("super(msg);\n");
        stringBuilder.append("}\n\n");

        stringBuilder.append(Constant.CLOSE);
        return stringBuilder.toString( );
    }

    public String CustomExceptionHandler(String packageName) {

        StringBuilder stringBuilder = new StringBuilder( );
        stringBuilder.append(Constant.PACKAGE).append(packageName).append(Constant.EXCEPTION);
        stringBuilder.append("import org.springframework.http.HttpStatus;\n");
        stringBuilder.append("import org.springframework.http.ResponseEntity;\n");
        stringBuilder.append("import org.springframework.web.bind.annotation.ControllerAdvice;\n");
        stringBuilder.append("import org.springframework.web.bind.annotation.ExceptionHandler;\n");
        stringBuilder.append("import org.springframework.web.context.request.WebRequest;\n");
        stringBuilder.append("import java.time.LocalDateTime;\n");
        stringBuilder.append("import java.util.ArrayList;\n");
        stringBuilder.append(Constant.IMPORT_JAVA_UTIL_LIST);
        stringBuilder.append("@ControllerAdvice\n");
        stringBuilder.append(Constant.PUBLIC_CLASS).append("CustomExceptionHandler").append(Constant.DOUBLE_NEWLINE);
        stringBuilder.append("\t/**\n" + " * Deal with ALL Other Exceptions\n" + " */\n");
        stringBuilder.append("\t@ExceptionHandler({Exception.class})\n");
        stringBuilder.append("\tpublic ResponseEntity<Object> handleAll(Exception ex,WebRequest request)").append(Constant.DOUBLE_NEWLINE);
        stringBuilder.append("\t\tList<String> details = new ArrayList<String>();\n");
        stringBuilder.append("\t\tdetails.add(ex.getLocalizedMessage());\n");
        stringBuilder.append("\t\tApiError err = new ApiError(LocalDateTime.now(),HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage(),details);\n");
        stringBuilder.append("\t\treturn ResponseEntityBuilder.build(err);\n");
        stringBuilder.append(Constant.TAB_NEWLINE);

        stringBuilder.append("\t@ExceptionHandler({CustomValidationException.class})\n");
        stringBuilder.append("\tpublic ResponseEntity<Object> customValidationException(CustomValidationException ex)").append(Constant.DOUBLE_NEWLINE);
        stringBuilder.append("\t\tList<String> details = new ArrayList<String>();\n");
        stringBuilder.append("\t\tdetails.add(ex.getLocalizedMessage());\n");
        stringBuilder.append("\t\tApiError err = new ApiError(LocalDateTime.now(),HttpStatus.BAD_REQUEST,ex.getMessage(),details);\n");
        stringBuilder.append("\t\treturn ResponseEntityBuilder.build(err);\n");
        stringBuilder.append(Constant.TAB_NEWLINE);

        stringBuilder.append(Constant.CLOSE);


        return stringBuilder.toString( );
    }

    public String SwaggerConfig(String packageName, String artifactId) {

        StringBuilder stringBuilder = new StringBuilder( );
        stringBuilder.append(Constant.PACKAGE).append(packageName).append(".config;\n\n");
        stringBuilder.append(" import io.swagger.v3.oas.models.Components;\n");
        stringBuilder.append(" import io.swagger.v3.oas.models.OpenAPI;\n");
        stringBuilder.append(" import io.swagger.v3.oas.models.info.Info;\n");
        stringBuilder.append(" import io.swagger.v3.oas.models.info.License;\n");
        stringBuilder.append(" import io.swagger.v3.oas.models.security.SecurityRequirement;\n");
        stringBuilder.append(" import io.swagger.v3.oas.models.security.SecurityScheme;\n");
        stringBuilder.append(" import ").append(packageName).append(".response.SwaggerConstant;\n");
        stringBuilder.append(" import org.springframework.context.annotation.Bean;\n");
        stringBuilder.append(" import org.springframework.context.annotation.Configuration;\n");
        stringBuilder.append(" @Configuration\n");

        stringBuilder.append(Constant.PUBLICCLASS).append("SwaggerConfig").append(Constant.OPEN_PARENTHESIS);
        stringBuilder.append(" \t@Bean\n");
        stringBuilder.append(" \tpublic OpenAPI customOpenAPI()").append(Constant.DOUBLE_NEWLINE);
        stringBuilder.append(" \t\tLicense mitLicense = new License()").append(".name(\"Apache 2.0\")").append(".url(\"http://springdoc.org\");\n");

        stringBuilder.append(" \t\tInfo info = new Info().title(\"" + artifactId + "\").version(\"1.0\")\n");
        stringBuilder.append(" \t\t\t\t.description(\"" + artifactId + " Backend service End points\")\n");
        stringBuilder.append(" \t\t\t\t.termsOfService(\"http://swagger.io/terms/\")\n");
        stringBuilder.append("\t\t\t\t.license(mitLicense);\n");
        stringBuilder.append(" \t\treturn new OpenAPI().info(info)\n");
        stringBuilder.append(" \t\t\t.addSecurityItem(new SecurityRequirement()\n");
        stringBuilder.append(" \t\t\t\t.addList(SwaggerConstant.AUTHENTICATION)).components(new Components().addSecuritySchemes(SwaggerConstant.AUTHENTICATION, new SecurityScheme()\n");
        stringBuilder.append(" \t\t\t\t\t.name(SwaggerConstant.AUTHENTICATION)\n");
        stringBuilder.append(" \t\t\t\t\t.type(SecurityScheme.Type.HTTP)\n");
        stringBuilder.append("\t\t\t\t\t.scheme(\"bearer\").bearerFormat(\"JWT\")));\n");
        stringBuilder.append(Constant.TAB_NEWLINE);
        stringBuilder.append(Constant.SPACE_CHARACTER);
        return stringBuilder.toString( );
    }


    public String jwtAuthenticationEntryPoint(String packageName) {

        StringBuilder stringBuilder = new StringBuilder( );
        stringBuilder.append(Constant.PACKAGE).append(packageName).append(".jwt;\n\n");
        stringBuilder.append(" import org.springframework.security.core.AuthenticationException;\n");
        stringBuilder.append(" import org.springframework.security.web.AuthenticationEntryPoint;\n");
        stringBuilder.append(" import org.springframework.stereotype.Component;\n");
        stringBuilder.append(" import io.swagger.v3.oas.models.info.License;\n");
        stringBuilder.append(" import javax.servlet.ServletException;\n");
        stringBuilder.append(" import javax.servlet.http.HttpServletRequest;\n");
        stringBuilder.append(" import javax.servlet.http.HttpServletResponse;\n");
        stringBuilder.append(" import java.io.IOException;\n");
        stringBuilder.append(" import java.io.Serializable;\n");
        stringBuilder.append(" @Component\n");

        stringBuilder.append(Constant.PUBLICCLASS).append("JwtAuthenticationEntryPoint").append("implements").append("AuthenticationEntryPoint , Serializable").append(Constant.DOUBLE_NEWLINE);
        stringBuilder.append(" \t\tprivate static final long serialVersionUID = -521401304750789166L;\n");
        stringBuilder.append(" \t\t@Override");
        stringBuilder.append("\t\t public void commence(HttpServletRequest request, HttpServletResponse response,");
        stringBuilder.append("\t\t AuthenticationException authException) throws IOException, ServletException").append(Constant.DOUBLE_NEWLINE);
        stringBuilder.append("\t\t response.sendError(HttpServletResponse.SC_UNAUTHORIZED, \"Unauthorized\");\n");
        stringBuilder.append(" \t\t}\n\n");
        stringBuilder.append(Constant.SPACE_CHARACTER);
        return stringBuilder.toString( );
    }

    public String jwtRequestFilter(String packageName) {

        StringBuilder stringBuilder = new StringBuilder( );
        stringBuilder.append(Constant.PACKAGE).append(packageName).append(".jwt;\n\n");
        stringBuilder.append(" import io.jsonwebtoken.ExpiredJwtException;\n");
        stringBuilder.append(" import org.springframework.beans.factory.annotation.Autowired;\n");
        stringBuilder.append(" import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;\n");
        stringBuilder.append(" import org.springframework.security.core.context.SecurityContextHolder;\n");
        stringBuilder.append(" import org.springframework.security.core.userdetails.UserDetails;\n");
        stringBuilder.append(" import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;\n");
        stringBuilder.append(" import org.springframework.stereotype.Component;\n");
        stringBuilder.append(" import org.springframework.web.filter.OncePerRequestFilter;\n");
        stringBuilder.append(" com").append(packageName).append("Constant\n");
        stringBuilder.append("com").append(packageName).append("utils").append("JWTUtils\n");
        stringBuilder.append("import javax.servlet.FilterChain;\n");
        stringBuilder.append("import javax.servlet.ServletException;\n");
        stringBuilder.append("import javax.servlet.http.HttpServletRequest;\n");
        stringBuilder.append("import javax.servlet.http.HttpServletResponse;\n");
        stringBuilder.append("import java.io.IOException;\n");

        stringBuilder.append(" @Component\n");

        stringBuilder.append(Constant.PUBLICCLASS).append("JwtRequestFilter").append("extends").append("OncePerRequestFilter").append(Constant.DOUBLE_NEWLINE);
        stringBuilder.append("\t\t@Autowired\n");
        stringBuilder.append("\t\tprivate JwtUserDetailsService jwtUserDetailsService;\n");
        stringBuilder.append("\t\t@Autowired");
        stringBuilder.append("\t\tprivate JWTUtils jwtTokenUtil;\n");
        stringBuilder.append("\t\t@Override\n");
        stringBuilder.append("\t\tprotected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)\n");
        stringBuilder.append("\t\t throws ServletException, IOException").append(Constant.DOUBLE_NEWLINE);
        stringBuilder.append("\t\tfinal String requestTokenHeader = request.getHeader(Constant.HEADER);\n");

        stringBuilder.append(" \t\t}\n\n");
        stringBuilder.append(Constant.SPACE_CHARACTER);
        return stringBuilder.toString( );
    }

    public String modelMapperBeanClassCreation() {
        StringBuilder modelMapperBeanClassCreation = new StringBuilder( );
        modelMapperBeanClassCreation.append("\t@Bean\n" +
                "\tModelMapper modelMapper() {\n" +
                "\t\treturn new ModelMapper();\n" +
                "\t}");
        return modelMapperBeanClassCreation.toString( );
    }

    public String getAllDTOClassFileZipEntryString(Integer databaseEngineId, Table table, String packageName) throws CustomValidationException {
        StringBuilder getAllRequestDTO = new StringBuilder( );
        String className = commonUtil.CapitalizeClassName(table.getName( ));
        Map<Integer, Map<String, String>> map = commonUtil.getJavaType( );
        getAllRequestDTO.append(Constant.PACKAGE).append(packageName).append(".dto;\n\n");
        getAllRequestDTO.append(Constant.IMPORT_LOMBOK_GETTER +  Constant.IMPORT_LOMBOK_SETTER);
        getAllRequestDTO.append(Constant.GETTER + Constant.SETTER);
        getAllRequestDTO.append(Constant.PUBLIC_CLASS).append(className).append("GetAllRequestDTO {\n\n");
        if (table.getCrudDTO( ).getMultipleRecord( ).getPagination( )) {
            getAllRequestDTO.append("    private Integer pageNo;\n\n");
            getAllRequestDTO.append("    private Integer pageSize;\n\n");
        }
        if (table.getCrudDTO( ).getMultipleRecord( ).getSort( )) {
            getAllRequestDTO.append("    private String sortBy;\n\n");
        }
        if (table.getCrudDTO( ).getMultipleRecord( ).getIsSearch( )) {
            getAllRequestDTO.append("    private String search; \n\n");
        }
        if (table.getCrudDTO( ).getMultipleRecord( ).getFilter( )) {
            List<Column> foreignKeyVariablesOnly = table.getColumns( ).stream( ).filter(column -> column.getKeytype( ) != null
                    && column.getKeytype( ).equalsIgnoreCase("FK")).collect(Collectors.toList( ));
            for (int i = 0; i < table.getCrudDTO( ).getMultipleRecord( ).getFilteredFieldList( ).size( ); i++) {
                int finalI = i;
                foreignKeyVariablesOnly.stream( ).forEach(column -> {
                    if (column.getName( ).equalsIgnoreCase(table.getCrudDTO( ).getMultipleRecord( ).getFilteredFieldList( ).get(finalI))) {
                        String tableReference = column.getTableReference( );
                        String javaType = map.get(databaseEngineId).get(column.getDatatype( ));
                        getAllRequestDTO.append(Constant.SLASH_PRIVATE).append(javaType)
                                .append(" ").append(commonUtil.toCamelCase(tableReference)).append(Constant.SINGLE_SLASH);
                    }
                });
            }
        }
        getAllRequestDTO.append(Constant.CLOSE);
        return getAllRequestDTO.toString( );
    }

}

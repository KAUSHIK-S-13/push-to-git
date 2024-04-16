package com.d2d.dto;

/**
 * @author Sekhar
 */
public class Column {

    private int id;
    private String name;

    private String datatype;

    private String keytype;

    private String tableReference;

    private String tableReferenceId;

    private String defaultValue;

    private String isNull;

    private String isAutoIncrement;

    private String sampleData;
    private  String columnComments;
    private String tableComments;
    private String mapping;

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getKeytype() {
        return keytype;
    }

    public void setKeytype(String keytype) {
        this.keytype = keytype;
    }

    public String getTableReferenceId() {
        return tableReferenceId;
    }

    public void setTableReferenceId(String tableReferenceId) {
        this.tableReferenceId = tableReferenceId;
    }

    public String getTableReference() {
        return tableReference;
    }

    public void setTableReference(String tableReference) {
        this.tableReference = tableReference;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getIsNull() {
        return isNull;
    }

    public void setIsNull(String isNull) {
        this.isNull = isNull;
    }

    public String getIsAutoIncrement() {
        return isAutoIncrement;
    }

    public void setIsAutoIncrement(String isAutoIncrement) {
        this.isAutoIncrement = isAutoIncrement;
    }

    public String getSampleData() {
        return sampleData;
    }

    public void setSampleData(String sampleData) {
        this.sampleData = sampleData;
    }

    public String getColumnComments() {
        return columnComments;
    }

    public void setColumnComments(String columnComments) {
        this.columnComments = columnComments;
    }

    public String getTableComments() {
        return tableComments;
    }

    public void setTableComments(String tableComments) {
        this.tableComments = tableComments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

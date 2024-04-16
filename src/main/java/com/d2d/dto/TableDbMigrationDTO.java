package com.d2d.dto;

import java.util.List;

public class TableDbMigrationDTO {
    private String tableName;
    private List<ColumnDbMigrationDTO> columns;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnDbMigrationDTO> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnDbMigrationDTO> columns) {
        this.columns = columns;
    }


}

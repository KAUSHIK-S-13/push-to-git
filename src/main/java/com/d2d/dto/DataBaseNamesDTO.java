package com.d2d.dto;

import java.util.List;

public class DataBaseNamesDTO {

    private String dbName;
    private List<TableDbMigrationDTO> tableColumnsList;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public List<TableDbMigrationDTO> getTableColumnsList() {
        return tableColumnsList;
    }

    public void setTableColumnsList(List<TableDbMigrationDTO> tableColumnsList) {
        this.tableColumnsList = tableColumnsList;
    }
}

package com.d2d.dto;

import java.util.List;

public class DbMigrationColumsTablesDTO {


    private List<DataBaseNamesDTO> dataBaseNamesTablesColumns;


    public List<DataBaseNamesDTO> getDataBaseNamesTablesColumns() {
        return dataBaseNamesTablesColumns;
    }

    public void setDataBaseNamesTablesColumns(List<DataBaseNamesDTO> dataBaseNamesTablesColumns) {
        this.dataBaseNamesTablesColumns = dataBaseNamesTablesColumns;
    }
}

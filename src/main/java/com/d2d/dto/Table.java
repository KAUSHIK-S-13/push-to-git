package com.d2d.dto;

import java.util.List;

/**
 * @author Sekhar
 */
public class Table {

    private String name;
    private List<Column> columns;

    private String tableComments;

    private CrudDTO crudDTO;

    public CrudDTO getCrudDTO() {
        if (crudDTO==null){
            crudDTO=new CrudDTO();
        }
        return crudDTO;
    }

    public void setCrudDTO(CrudDTO crudDTO) {
        this.crudDTO = crudDTO;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public String getTableComments() {
        return tableComments;
    }

    public void setTableComments(String tableComments) {
        this.tableComments = tableComments;
    }
}

package com.d2d.dto;

import java.util.List;

/**
 * @author Sekhar
 */

public class DbDesignTables {

    private String projectName;

    private String dbDesignName;

    private String dbName;


    private List<Table> tableList;
    private SpringBootBasicDTO springBootBasicDTO;

    private Integer databaseEngineId;
    private boolean customException = false;
    private boolean swagger = false;

    private EnableSecurityDTO enableSecurityDTO;

    private ConnectionDetailsDTO destination;

    private Integer projectTypeId;

    private Integer projectId;

    public SpringBootBasicDTO getSpringBootBasicDTO() {
        return springBootBasicDTO;
    }

    public void setSpringBootBasicDTO(SpringBootBasicDTO springBootBasicDTO) {
        this.springBootBasicDTO = springBootBasicDTO;
    }

    public List<Table> getTableList() {
        return tableList;
    }

    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
    }

    public Integer getDatabaseEngineId() {
        return databaseEngineId;
    }

    public void setDatabaseEngineId(Integer databaseEngineId) {
        this.databaseEngineId = databaseEngineId;
    }

    public boolean isCustomException() {
        return customException;
    }

    public void setCustomException(boolean customException) {
        this.customException = customException;
    }

    public boolean isSwagger() {
        return swagger;
    }

    public void setSwagger(boolean swagger) {
        this.swagger = swagger;
    }

    public EnableSecurityDTO getEnableSecurityDTO() {
        if (enableSecurityDTO ==null){
            enableSecurityDTO =new EnableSecurityDTO();
        }
        return enableSecurityDTO;
    }

    public void setEnableSecurityDTO(EnableSecurityDTO enableSecurityDTO) {
        this.enableSecurityDTO = enableSecurityDTO;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public ConnectionDetailsDTO getDestination() {
        return destination;
    }

    public void setDestination(ConnectionDetailsDTO destination) {
        this.destination = destination;
    }

    public String getDbDesignName() {
        return dbDesignName;
    }

    public void setDbDesignName(String dbDesignName) {
        this.dbDesignName = dbDesignName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Integer getProjectTypeId() {
        return projectTypeId;
    }

    public void setProjectTypeId(Integer projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
}

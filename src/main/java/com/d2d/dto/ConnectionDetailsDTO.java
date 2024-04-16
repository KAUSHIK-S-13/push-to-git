package com.d2d.dto;

public class ConnectionDetailsDTO {

    private String hostAddress;
    private String port;
    private  String dbName;
    private  String username;
    private  String password;
    private String databaseEngines;

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseEngines() {
        return databaseEngines;
    }

    public void setDatabaseEngines(String databaseEngines) {
        this.databaseEngines = databaseEngines;
    }
}

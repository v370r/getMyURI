package com.getmyuri.backend.model;

public class DatabaseConnectionConfig {
    private String host;
    private String username;
    private String databaseName;
    private String password;
    private int port;

    public DatabaseConnectionConfig() {}

    public DatabaseConnectionConfig(String host, String username, String databaseName, String password, int port) {
        this.host = host;
        this.username = username;
        this.databaseName = databaseName;
        this.password = password;
        this.port = port;
    }

    // Getters and Setters
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
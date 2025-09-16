package com.getmyuri.backend.model;

public class DatabaseDeleteResponse {
    private String command;
    private String databaseType;
    private String warning;
    private boolean success;

    public DatabaseDeleteResponse() {}

    public DatabaseDeleteResponse(String command, String databaseType, String warning, boolean success) {
        this.command = command;
        this.databaseType = databaseType;
        this.warning = warning;
        this.success = success;
    }

    // Getters and Setters
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
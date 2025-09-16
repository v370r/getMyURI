package com.getmyuri.backend.service;

import com.getmyuri.backend.model.DatabaseConnectionConfig;
import com.getmyuri.backend.model.DatabaseDeleteResponse;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    private static final String WARNING_MESSAGE = "⚠️ WARNING: This action is irreversible and will permanently delete all data in the production database. Double-check your credentials and database name before running the command.";

    public DatabaseDeleteResponse generateMySQLDeleteCommand(DatabaseConnectionConfig config) {
        if (config.getHost() == null || config.getUsername() == null || config.getDatabaseName() == null) {
            return new DatabaseDeleteResponse("", "MySQL", "Missing required parameters: host, username, or databaseName", false);
        }

        String command;
        if (config.getPort() > 0) {
            command = String.format("mysql -u %s -p -h %s -P %d -e \"DROP DATABASE %s;\"", 
                config.getUsername(), 
                config.getHost(), 
                config.getPort(),
                config.getDatabaseName());
        } else {
            command = String.format("mysql -u %s -p -h %s -e \"DROP DATABASE %s;\"", 
                config.getUsername(), 
                config.getHost(), 
                config.getDatabaseName());
        }

        return new DatabaseDeleteResponse(command, "MySQL", WARNING_MESSAGE, true);
    }

    public DatabaseDeleteResponse generatePostgreSQLDeleteCommand(DatabaseConnectionConfig config) {
        if (config.getHost() == null || config.getUsername() == null || config.getDatabaseName() == null) {
            return new DatabaseDeleteResponse("", "PostgreSQL", "Missing required parameters: host, username, or databaseName", false);
        }

        String command;
        if (config.getPort() > 0) {
            command = String.format("psql -U %s -h %s -p %d -c \"DROP DATABASE %s;\"", 
                config.getUsername(), 
                config.getHost(), 
                config.getPort(),
                config.getDatabaseName());
        } else {
            command = String.format("psql -U %s -h %s -c \"DROP DATABASE %s;\"", 
                config.getUsername(), 
                config.getHost(), 
                config.getDatabaseName());
        }

        return new DatabaseDeleteResponse(command, "PostgreSQL", WARNING_MESSAGE, true);
    }

    public DatabaseDeleteResponse generateMongoDBDeleteCommand(DatabaseConnectionConfig config) {
        if (config.getHost() == null || config.getUsername() == null || config.getDatabaseName() == null) {
            return new DatabaseDeleteResponse("", "MongoDB", "Missing required parameters: host, username, or databaseName", false);
        }

        String command;
        if (config.getPort() > 0) {
            command = String.format("mongo --host %s:%d -u %s -p --authenticationDatabase admin --eval \"db.getSiblingDB('%s').dropDatabase()\"", 
                config.getHost(), 
                config.getPort(),
                config.getUsername(), 
                config.getDatabaseName());
        } else {
            command = String.format("mongo --host %s -u %s -p --authenticationDatabase admin --eval \"db.getSiblingDB('%s').dropDatabase()\"", 
                config.getHost(), 
                config.getUsername(), 
                config.getDatabaseName());
        }

        return new DatabaseDeleteResponse(command, "MongoDB", WARNING_MESSAGE, true);
    }
}
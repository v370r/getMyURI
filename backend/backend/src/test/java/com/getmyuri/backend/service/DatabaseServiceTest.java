package com.getmyuri.backend.service;

import com.getmyuri.backend.model.DatabaseConnectionConfig;
import com.getmyuri.backend.model.DatabaseDeleteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseServiceTest {

    private DatabaseService databaseService;

    @BeforeEach
    void setUp() {
        databaseService = new DatabaseService();
    }

    @Test
    void testGenerateMySQLDeleteCommand_Success() {
        DatabaseConnectionConfig config = new DatabaseConnectionConfig();
        config.setHost("localhost");
        config.setUsername("admin");
        config.setDatabaseName("prod_db");

        DatabaseDeleteResponse response = databaseService.generateMySQLDeleteCommand(config);

        assertTrue(response.isSuccess());
        assertEquals("MySQL", response.getDatabaseType());
        assertEquals("mysql -u admin -p -h localhost -e \"DROP DATABASE prod_db;\"", response.getCommand());
        assertNotNull(response.getWarning());
        assertTrue(response.getWarning().contains("WARNING"));
    }

    @Test
    void testGenerateMySQLDeleteCommand_WithPort() {
        DatabaseConnectionConfig config = new DatabaseConnectionConfig();
        config.setHost("localhost");
        config.setUsername("admin");
        config.setDatabaseName("prod_db");
        config.setPort(3306);

        DatabaseDeleteResponse response = databaseService.generateMySQLDeleteCommand(config);

        assertTrue(response.isSuccess());
        assertEquals("mysql -u admin -p -h localhost -P 3306 -e \"DROP DATABASE prod_db;\"", response.getCommand());
    }

    @Test
    void testGenerateMySQLDeleteCommand_MissingParameters() {
        DatabaseConnectionConfig config = new DatabaseConnectionConfig();
        config.setHost("localhost");
        // Missing username and databaseName

        DatabaseDeleteResponse response = databaseService.generateMySQLDeleteCommand(config);

        assertFalse(response.isSuccess());
        assertEquals("MySQL", response.getDatabaseType());
        assertTrue(response.getWarning().contains("Missing required parameters"));
    }

    @Test
    void testGeneratePostgreSQLDeleteCommand_Success() {
        DatabaseConnectionConfig config = new DatabaseConnectionConfig();
        config.setHost("localhost");
        config.setUsername("postgres");
        config.setDatabaseName("prod_db");

        DatabaseDeleteResponse response = databaseService.generatePostgreSQLDeleteCommand(config);

        assertTrue(response.isSuccess());
        assertEquals("PostgreSQL", response.getDatabaseType());
        assertEquals("psql -U postgres -h localhost -c \"DROP DATABASE prod_db;\"", response.getCommand());
        assertNotNull(response.getWarning());
        assertTrue(response.getWarning().contains("WARNING"));
    }

    @Test
    void testGeneratePostgreSQLDeleteCommand_WithPort() {
        DatabaseConnectionConfig config = new DatabaseConnectionConfig();
        config.setHost("localhost");
        config.setUsername("postgres");
        config.setDatabaseName("prod_db");
        config.setPort(5432);

        DatabaseDeleteResponse response = databaseService.generatePostgreSQLDeleteCommand(config);

        assertTrue(response.isSuccess());
        assertEquals("psql -U postgres -h localhost -p 5432 -c \"DROP DATABASE prod_db;\"", response.getCommand());
    }

    @Test
    void testGenerateMongoDBDeleteCommand_Success() {
        DatabaseConnectionConfig config = new DatabaseConnectionConfig();
        config.setHost("localhost");
        config.setUsername("admin");
        config.setDatabaseName("prod_db");

        DatabaseDeleteResponse response = databaseService.generateMongoDBDeleteCommand(config);

        assertTrue(response.isSuccess());
        assertEquals("MongoDB", response.getDatabaseType());
        assertEquals("mongo --host localhost -u admin -p --authenticationDatabase admin --eval \"db.getSiblingDB('prod_db').dropDatabase()\"", response.getCommand());
        assertNotNull(response.getWarning());
        assertTrue(response.getWarning().contains("WARNING"));
    }

    @Test
    void testGenerateMongoDBDeleteCommand_WithPort() {
        DatabaseConnectionConfig config = new DatabaseConnectionConfig();
        config.setHost("localhost");
        config.setUsername("admin");
        config.setDatabaseName("prod_db");
        config.setPort(27017);

        DatabaseDeleteResponse response = databaseService.generateMongoDBDeleteCommand(config);

        assertTrue(response.isSuccess());
        assertEquals("mongo --host localhost:27017 -u admin -p --authenticationDatabase admin --eval \"db.getSiblingDB('prod_db').dropDatabase()\"", response.getCommand());
    }

    @Test
    void testGenerateMongoDBDeleteCommand_MissingParameters() {
        DatabaseConnectionConfig config = new DatabaseConnectionConfig();
        config.setHost("localhost");
        // Missing username and databaseName

        DatabaseDeleteResponse response = databaseService.generateMongoDBDeleteCommand(config);

        assertFalse(response.isSuccess());
        assertEquals("MongoDB", response.getDatabaseType());
        assertTrue(response.getWarning().contains("Missing required parameters"));
    }
}
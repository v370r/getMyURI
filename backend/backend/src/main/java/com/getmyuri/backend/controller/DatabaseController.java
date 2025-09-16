package com.getmyuri.backend.controller;

import com.getmyuri.backend.model.DatabaseConnectionConfig;
import com.getmyuri.backend.model.DatabaseDeleteResponse;
import com.getmyuri.backend.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/database")
@CrossOrigin(origins = "*")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @PostMapping("/delete/mysql")
    public ResponseEntity<DatabaseDeleteResponse> getMySQLDeleteCommand(@RequestBody DatabaseConnectionConfig config) {
        DatabaseDeleteResponse response = databaseService.generateMySQLDeleteCommand(config);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/delete/postgresql")
    public ResponseEntity<DatabaseDeleteResponse> getPostgreSQLDeleteCommand(@RequestBody DatabaseConnectionConfig config) {
        DatabaseDeleteResponse response = databaseService.generatePostgreSQLDeleteCommand(config);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/delete/mongodb")
    public ResponseEntity<DatabaseDeleteResponse> getMongoDBDeleteCommand(@RequestBody DatabaseConnectionConfig config) {
        DatabaseDeleteResponse response = databaseService.generateMongoDBDeleteCommand(config);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/hello")
    public String hello() {
        return "Database Management API is running!";
    }
}
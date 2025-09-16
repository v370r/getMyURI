# Database Management API

This API provides secure database deletion commands for production databases. It supports MySQL, PostgreSQL, and MongoDB databases.

## Endpoints

### Health Check
- **GET** `/api/database/hello`
  - Returns: "Database Management API is running!"

### Database Deletion Commands

#### MySQL Database Deletion
- **POST** `/api/database/delete/mysql`
- **Request Body:**
  ```json
  {
    "host": "localhost",
    "username": "admin",
    "databaseName": "prod_db",
    "port": 3306  // optional
  }
  ```
- **Response:**
  ```json
  {
    "command": "mysql -u admin -p -h localhost -e \"DROP DATABASE prod_db;\"",
    "databaseType": "MySQL",
    "warning": "⚠️ WARNING: This action is irreversible and will permanently delete all data in the production database. Double-check your credentials and database name before running the command.",
    "success": true
  }
  ```

#### PostgreSQL Database Deletion
- **POST** `/api/database/delete/postgresql`
- **Request Body:**
  ```json
  {
    "host": "localhost",
    "username": "postgres",
    "databaseName": "prod_db",
    "port": 5432  // optional
  }
  ```
- **Response:**
  ```json
  {
    "command": "psql -U postgres -h localhost -c \"DROP DATABASE prod_db;\"",
    "databaseType": "PostgreSQL",
    "warning": "⚠️ WARNING: This action is irreversible and will permanently delete all data in the production database. Double-check your credentials and database name before running the command.",
    "success": true
  }
  ```

#### MongoDB Database Deletion
- **POST** `/api/database/delete/mongodb`
- **Request Body:**
  ```json
  {
    "host": "localhost",
    "username": "admin",
    "databaseName": "prod_db",
    "port": 27017  // optional
  }
  ```
- **Response:**
  ```json
  {
    "command": "mongo --host localhost -u admin -p --authenticationDatabase admin --eval \"db.getSiblingDB('prod_db').dropDatabase()\"",
    "databaseType": "MongoDB",
    "warning": "⚠️ WARNING: This action is irreversible and will permanently delete all data in the production database. Double-check your credentials and database name before running the command.",
    "success": true
  }
  ```

## Error Handling

If required parameters are missing, the API returns a 400 Bad Request response:

```json
{
  "command": "",
  "databaseType": "MySQL",
  "warning": "Missing required parameters: host, username, or databaseName",
  "success": false
}
```

## Safety Features

1. **No Direct Execution**: The API only generates commands - it does not execute them directly.
2. **Warning Messages**: All successful responses include safety warnings.
3. **Parameter Validation**: Required parameters are validated before command generation.
4. **Explicit Commands**: Generated commands are human-readable and can be reviewed before execution.

## Usage Example

```bash
# Generate MySQL deletion command
curl -X POST http://localhost:9090/api/database/delete/mysql \
  -H "Content-Type: application/json" \
  -d '{"host": "localhost", "username": "admin", "databaseName": "prod_db"}'

# Generate PostgreSQL deletion command with custom port
curl -X POST http://localhost:9090/api/database/delete/postgresql \
  -H "Content-Type: application/json" \
  -d '{"host": "localhost", "username": "postgres", "databaseName": "prod_db", "port": 5432}'

# Generate MongoDB deletion command
curl -X POST http://localhost:9090/api/database/delete/mongodb \
  -H "Content-Type: application/json" \
  -d '{"host": "localhost", "username": "admin", "databaseName": "prod_db"}'
```

## Security Considerations

- The API does not store or log database passwords
- Commands are generated but not executed automatically
- Users must manually run the generated commands
- Always verify database details before executing commands
- Consider using database backups before deletion
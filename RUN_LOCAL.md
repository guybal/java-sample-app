# Running Locally with VS Code

This guide explains how to run the application in development and production modes locally using VS Code.

## Prerequisites

### For Development (H2)
- Java 21
- Maven
- No additional setup needed - H2 is embedded

### For Production (PostgreSQL)
- Java 21
- Maven
- PostgreSQL 18.0 running (via Docker or local installation)

## VS Code Run Configurations

### Option 1: Using VS Code Launch Configurations

Two launch configurations are available in `.vscode/launch.json`:

#### 1. **Spring Boot (Dev Profile - H2)**
- **Profile**: `dev`
- **Database**: H2 (file-based at `./data/drinks-db`)
- **How to run**: 
  1. Press `F5` or go to Run & Debug
  2. Select "Spring Boot (Dev Profile - H2)"
  3. Click Run

#### 2. **Spring Boot (Prod Profile - PostgreSQL)**
- **Profile**: `prod`
- **Database**: PostgreSQL
- **How to run**:
  1. Set environment variables (see below)
  2. Press `F5` or go to Run & Debug
  3. Select "Spring Boot (Prod Profile - PostgreSQL)"
  4. Click Run

### Setting Environment Variables for Production

Before running with prod profile, you need to set environment variables. You can do this in VS Code:

**Method 1: VS Code settings.json**
Add to `.vscode/settings.json`:
```json
{
    "java.debug.settings.env": {
        "DATABASE_URL": "jdbc:postgresql://localhost:5432/drinksdb",
        "DATABASE_USER": "postgres",
        "DATABASE_PASSWORD": "postgres"
    }
}
```

**Method 2: System Environment Variables**
In Windows:
```cmd
set DATABASE_URL=jdbc:postgresql://localhost:5432/drinksdb
set DATABASE_USER=postgres
set DATABASE_PASSWORD=postgres
```

In WSL:
```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/drinksdb
export DATABASE_USER=postgres
export DATABASE_PASSWORD=postgres
```

**Method 3: Update launch.json directly**
You can hardcode values in `.vscode/launch.json` under the `vmArgs` section (not recommended for passwords).

## Running PostgreSQL Locally

### Using Docker

```bash
# Start PostgreSQL container
docker run --name postgres-drinks \
  -e POSTGRES_DB=drinksdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:18.0

# Verify it's running
docker ps

# Check logs
docker logs postgres-drinks
```

### Using Local PostgreSQL Installation

1. Ensure PostgreSQL is installed and running
2. Create database:
```sql
CREATE DATABASE drinksdb;
```
3. Update `DATABASE_URL` to point to your PostgreSQL instance

## Alternative: Command Line

### Development (H2)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev
```

### Production (PostgreSQL)
```bash
# Set environment variables
export DATABASE_URL=jdbc:postgresql://localhost:5432/drinksdb
export DATABASE_USER=postgres
export DATABASE_PASSWORD=postgres

# Run
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=prod
```

## Verifying the Configuration

After starting the application:

1. **Check logs** - Look for the active profile:
   - Dev: `The following profiles are active: dev`
   - Prod: `The following profiles are active: prod`

2. **Check database**:
   - **H2 Console** (dev only): Navigate to `http://localhost:8080/h2-console`
   - **PostgreSQL**: Connect using any PostgreSQL client

3. **Test endpoints**:
   - Landing page: `http://localhost:8080/`
   - Registry: `http://localhost:8080/registry`
   - API docs: `http://localhost:8080/docs`

## Troubleshooting

### "Could not connect to database"
- **Dev**: Ensure H2 file permissions in `./data/` directory
- **Prod**: Verify PostgreSQL is running and connection string is correct

### "Profile not found"
- Check that profile name matches exactly: `dev` or `prod`
- Verify `application-dev.yaml` and `application-prod.yaml` exist

### "Port already in use"
- Change port in `application.yaml`: `server.port: 8081`
- Or stop other running instances

### Environment variables not working
- Restart VS Code after setting environment variables
- Use `vmArgs` in launch.json instead
- Check variable names match exactly: `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD`


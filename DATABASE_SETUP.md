# Database Configuration Guide

This application uses Spring Data JPA with different database configurations for development and production environments.

## Profiles

### Development Profile (`dev`)
- **Database**: H2 (embedded, file-based)
- **Location**: `./data/drinks-db.mv.db`
- **Console**: Available at `/h2-console`
- **Auto-schema**: Tables are automatically created/updated

### Production Profile (`prod`)
- **Database**: PostgreSQL
- **Configuration**: Via environment variables
- **Auto-schema**: Schema validation only (no auto-create)

## Running the Application

### Development (H2 Database)

**Option 1: Default (automatically uses 'dev')**
```bash
mvn spring-boot:run
```

**Option 2: Explicitly set dev profile**
```bash
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

Or in Windows:
```cmd
set SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

**Option 3: Via application argument**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev
```

### Production (PostgreSQL)

**Option 1: Via Environment Variables**
```bash
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://your-postgres-host:5432/drinksdb
export DATABASE_USER=your_username
export DATABASE_PASSWORD=your_password
mvn spring-boot:run
```

**Option 2: Via JVM Arguments**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod --spring.datasource.url=jdbc:postgresql://localhost:5432/drinksdb --spring.datasource.username=postgres --spring.datasource.password=password"
```

## H2 Console (Development Only)

When running with `dev` profile, you can access the H2 console at:
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:file:./data/drinks-db`
- **Username**: `sa`
- **Password**: (leave empty)

## PostgreSQL Setup (Production)

### Using Docker (Local Testing)

1. **Start PostgreSQL container:**
```bash
docker run --name postgres-drinks \
  -e POSTGRES_DB=drinksdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=yourpassword \
  -p 5432:5432 \
  -d postgres:18.0
```

2. **Set environment variables:**
```bash
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://localhost:5432/drinksdb
export DATABASE_USER=postgres
export DATABASE_PASSWORD=yourpassword
```

3. **Run application:**
```bash
mvn spring-boot:run
```

### Azure Database for PostgreSQL Setup

1. **Create Azure Database for PostgreSQL** (via Azure Portal or CLI)

2. **Set Azure App Service Configuration:**
   - Go to your Azure Web App → Configuration → Application Settings
   - Add these environment variables:
     ```
     SPRING_PROFILES_ACTIVE=prod
     DATABASE_URL=jdbc:postgresql://your-azure-postgres-host:5432/drinksdb?sslmode=require
     DATABASE_USER=your_azure_username
     DATABASE_PASSWORD=your_azure_password
     ```

3. **Update Azure Firewall Rules** to allow your Web App IP

## Database Schema

The application automatically creates the following table:

**`transactions` table:**
- `id` (BIGSERIAL PRIMARY KEY)
- `name` (VARCHAR NOT NULL)
- `drink` (VARCHAR NOT NULL)
- `price` (DOUBLE PRECISION NOT NULL)
- `timestamp` (TIMESTAMP NOT NULL)

## Migration Notes

When migrating from file-based storage to JPA:
- Existing transactions in JSON files will NOT be automatically migrated
- New transactions will be stored in the database
- You may need to manually migrate historical data if needed

## Troubleshooting

### H2 Database Issues
- Check that the `data/` directory exists and is writable
- Delete `data/drinks-db.mv.db` to reset the database

### PostgreSQL Connection Issues
- Verify PostgreSQL is running: `docker ps` (if using Docker)
- Check connection string format
- Verify firewall rules allow connections
- For Azure, ensure SSL mode is set: `?sslmode=require`

### Profile Not Active
- Check logs for: `The following profiles are active: dev` or `The following profiles are active: prod`
- Verify `SPRING_PROFILES_ACTIVE` environment variable is set correctly


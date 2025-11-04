# Drinks App - Spring Boot Application

A modern Spring Boot web application for ordering and tracking drinks (coffee, tea, water) with a beautiful UI, transaction management, and full GitOps deployment capabilities.

## 🎯 Features

- **Interactive Drink Ordering**: Users can enter their name and order coffee ($4.99), tea ($2.99), or water ($0.99)
- **Personalized Experience**: Customized greeting messages for each customer
- **Transaction Registry**: Tracks all orders with timestamps, customer names, drink types, and prices
- **Analytics Dashboard**: View transaction statistics including total revenue, order counts by drink type
- **Modern UI**: Glassmorphism design with styled drink pages matching each beverage's theme
- **API Documentation**: Built-in OpenAPI/Swagger UI at `/docs`
- **Health Monitoring**: Spring Boot Actuator endpoints for health checks and metrics
- **Database Persistence**: Spring Data JPA with H2 (development) and PostgreSQL (production)

## 🏗️ Architecture

- **Framework**: Spring Boot 3.3.4
- **Java Version**: 21
- **Build Tool**: Maven
- **Database**:
  - H2 (embedded, development)
  - PostgreSQL (production)
- **Persistence**: Spring Data JPA
- **API Documentation**: Springdoc OpenAPI
- **Monitoring**: Spring Boot Actuator

## 🚀 Quick Start

### Prerequisites

- Java 21
- Maven 3.9+
- (Optional) PostgreSQL for production

### Local Development (H2 Database)

```bash
# Build the application
mvn clean package

# Run with dev profile (uses H2 database)
java -jar target/app-*.jar --spring.profiles.active=dev

# Or use Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will be available at `http://localhost:8080`

### Production (PostgreSQL)

```bash
# Set environment variables
export DATABASE_URL=jdbc:postgresql://localhost:5432/drinksdb
export DATABASE_USER=postgres
export DATABASE_PASSWORD=yourpassword
export SPRING_PROFILES_ACTIVE=prod

# Run the application
java -jar target/app-*.jar
```

## 📍 API Endpoints

- **`GET /`** - Landing page with drink selection
- **`GET/POST /coffee`** - Order coffee (with optional `name` parameter)
- **`GET/POST /tea`** - Order tea (with optional `name` parameter)
- **`GET/POST /water`** - Order water (with optional `name` parameter)
- **`GET /registry`** - View all transactions and statistics
- **`POST /registry/record`** - Record a transaction (API endpoint)
- **`GET /welcome`** - Returns greeting message from `GREETING_MSG` environment variable
- **`GET /docs`** - Swagger UI for API documentation
- **`GET /api-docs`** - OpenAPI JSON specification
- **`GET /actuator/health`** - Health check endpoint
- **`GET /actuator/metrics`** - Application metrics

## 🐳 Docker

Build and run with Docker:

```bash
# Build the image
docker build -t drinks-app:latest .

# Run the container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  drinks-app:latest
```

## ☸️ Kubernetes Deployment

### Using Helm Chart

The application includes a Helm chart for Kubernetes deployment:

```bash
# Install with Helm
helm install drinks-app ./config/helm/drinks-app \
  -f ./config/helm/drinks-app/values-production.yaml \
  --namespace production

# Or for development
helm install drinks-app ./config/helm/drinks-app \
  -f ./config/helm/drinks-app/values-dev.yaml \
  --namespace dev
```

See [config/helm/README.md](config/helm/README.md) for detailed deployment instructions.

### Using Flux GitOps

The application is configured for Flux GitOps deployment. See [config/flux/README.md](config/flux/README.md) for setup instructions.

## 🔄 CI/CD

The repository includes GitHub Actions workflows for:

- **Automated Testing**: Runs Maven tests on every push
- **Docker Image Building**: Builds and pushes images to GitHub Container Registry (GHCR)
- **Helm Chart Publishing**: Automatically packages and pushes Helm charts to GHCR
- **Version Management**: Automatic semantic versioning (major/minor/patch)
- **Git Tagging**: Creates tags for app versions and Helm chart versions

See [.github/workflows/README.md](.github/workflows/README.md) for workflow details.

## 📚 Documentation

- **[RUN_LOCAL.md](RUN_LOCAL.md)** - Detailed instructions for running locally
- **[DATABASE_SETUP.md](DATABASE_SETUP.md)** - Database configuration guide
- **[WSL_SETUP.md](WSL_SETUP.md)** - WSL environment setup for Windows
- **[config/helm/README.md](config/helm/README.md)** - Helm chart documentation
- **[config/flux/README.md](config/flux/README.md)** - Flux GitOps configuration
- **[.github/workflows/README.md](.github/workflows/README.md)** - CI/CD pipeline documentation

## 🛠️ Development

### Project Structure

```plaintext
java-sample-app/
├── src/main/java/org/example/app/
│   ├── main.java              # Application entry point
│   ├── HelloController.java    # Drink ordering endpoints
│   ├── RegistryController.java # Transaction registry
│   ├── TransactionService.java # Business logic
│   └── Transaction*.java      # Data models and repositories
├── src/main/resources/
│   ├── application.yaml       # Base configuration
│   ├── application-dev.yaml   # Development profile (H2)
│   ├── application-prod.yaml  # Production profile (PostgreSQL)
│   ├── landing-page.html      # Main landing page
│   ├── drink-template.html    # Drink display template
│   └── registry-template.html # Transaction registry template
├── config/
│   ├── helm/drinks-app/       # Helm chart for Kubernetes
│   └── flux/                  # Flux GitOps manifests
└── .github/workflows/          # CI/CD pipelines
```

### Running Tests

```bash
mvn test
```

### Building

```bash
mvn clean package
```

## 📦 Deployment Options

1. **Local JAR**: Run the built JAR file directly
2. **Docker**: Containerized deployment
3. **Kubernetes (Helm)**: Deploy using the Helm chart
4. **Kubernetes (Flux)**: GitOps deployment with Flux

## 🔐 Configuration

### Environment Variables

- `SPRING_PROFILES_ACTIVE` - Active profile (dev/prod)
- `DATABASE_URL` - PostgreSQL connection URL
- `DATABASE_USER` - Database username
- `DATABASE_PASSWORD` - Database password
- `GREETING_MSG` - Custom greeting message (displayed at `/welcome`)

### Application Properties

Drink images are configured via base64 strings in `application.yaml`:

- `drink.image.coffee`
- `drink.image.tea`
- `drink.image.water`

## 📊 Monitoring

- **Health Checks**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`

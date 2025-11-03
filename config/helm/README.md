# Drinks App Helm Chart

Helm chart for deploying the Drinks App Spring Boot application to Kubernetes.

## Prerequisites

- Kubernetes 1.19+
- Helm 3.0+
- PostgreSQL database (external or embedded)
- Container registry with application image

## Installation

### Quick Start (Development with Embedded PostgreSQL)

```bash
# Install with development values
helm install drinks-app ./drinks-app -f drinks-app/values-dev.yaml

# Or install with default values
helm install drinks-app ./drinks-app
```

### Production Installation

```bash
# 1. Create database secret first
kubectl create secret generic drinks-app-db-secret \
  --from-literal=password='your-secure-password' \
  --namespace production

# 2. Update values-production.yaml with your database host
# Edit: database.external.host

# 3. Install with production values
helm install drinks-app ./drinks-app \
  -f drinks-app/values-production.yaml \
  --namespace production \
  --create-namespace
```

## Configuration

### Key Configuration Options

| Parameter | Description | Default |
|-----------|-------------|---------|
| `replicaCount` | Number of replicas | `2` |
| `image.repository` | Container image repository | `your-registry/drinks-app` |
| `image.tag` | Container image tag | `2.0.1` |
| `spring.profiles.active` | Spring profile | `prod` |
| `database.external.enabled` | Use external PostgreSQL | `true` |
| `database.external.host` | PostgreSQL host | `""` |
| `database.embedded.enabled` | Use embedded PostgreSQL | `false` |
| `resources.limits.cpu` | CPU limit | `1000m` |
| `resources.limits.memory` | Memory limit | `1Gi` |
| `autoscaling.enabled` | Enable HPA | `false` |
| `ingress.enabled` | Enable Ingress | `false` |

### Database Configuration

#### External PostgreSQL (Recommended for Production)

```yaml
database:
  external:
    enabled: true
    host: "postgres.example.com"
    port: 5432
    database: drinksdb
    username: postgres
    secretName: drinks-app-db-secret
```

Create the secret:
```bash
kubectl create secret generic drinks-app-db-secret \
  --from-literal=password='your-password'
```

#### Embedded PostgreSQL (For Development)

```yaml
database:
  embedded:
    enabled: true
    image: postgres:18.0
    persistence:
      enabled: true
      size: 10Gi
```

### Spring Boot Configuration

The chart supports all Spring Boot configuration via environment variables:

- `SPRING_PROFILES_ACTIVE`: Active Spring profile (default: `prod`)
- `SPRING_JPA_HIBERNATE_DDL_AUTO`: Schema management (`update`, `validate`, `none`)
- `SPRING_JPA_SHOW_SQL`: Show SQL queries (default: `false`)

### Ingress Configuration

```yaml
ingress:
  enabled: true
  className: "nginx"
  hosts:
    - host: drinks-app.example.com
      paths:
        - path: /
          pathType: Prefix
  tls:
    - secretName: drinks-app-tls
      hosts:
        - drinks-app.example.com
```

### Autoscaling (HPA)

```yaml
autoscaling:
  enabled: true
  minReplicas: 2
  maxReplicas: 10
  targetCPUUtilizationPercentage: 80
  targetMemoryUtilizationPercentage: 80
```

## Building and Pushing Docker Image

Before deploying, build and push your Docker image:

```bash
# Build the application JAR
mvn clean package -DskipTests

# Build Docker image
docker build -t your-registry/drinks-app:2.0.1 .

# Push to registry
docker push your-registry/drinks-app:2.0.1
```

## Deployment

### Development

```bash
helm install drinks-app ./drinks-app \
  -f drinks-app/values-dev.yaml \
  --namespace development \
  --create-namespace
```

### Production

```bash
# 1. Create namespace
kubectl create namespace production

# 2. Create database secret
kubectl create secret generic drinks-app-db-secret \
  --from-literal=password='your-secure-password' \
  --namespace production

# 3. Update values-production.yaml with your settings
# 4. Deploy
helm install drinks-app ./drinks-app \
  -f drinks-app/values-production.yaml \
  --namespace production
```

## Upgrading

```bash
# Update image tag in values file, then:
helm upgrade drinks-app ./drinks-app \
  -f drinks-app/values-production.yaml \
  --namespace production
```

## Uninstallation

```bash
helm uninstall drinks-app --namespace production
```

## Troubleshooting

### Check Pod Status

```bash
kubectl get pods -l app.kubernetes.io/name=drinks-app
```

### View Logs

```bash
kubectl logs -l app.kubernetes.io/name=drinks-app -f
```

### Check Database Connection

```bash
# For embedded PostgreSQL
kubectl exec -it drinks-app-postgresql-0 -- psql -U postgres -d drinksdb

# Check connection from app pod
kubectl exec -it <app-pod-name> -- env | grep SPRING_DATASOURCE
```

### Port Forward for Local Testing

```bash
kubectl port-forward svc/drinks-app 8080:8080
# Then access: http://localhost:8080
```

## Values Files

- `values.yaml`: Default values
- `values-dev.yaml`: Development environment
- `values-production.yaml`: Production environment

## Customization

You can override any values:

```bash
helm install drinks-app ./drinks-app \
  --set replicaCount=3 \
  --set image.tag=2.0.2 \
  --set database.external.host=postgres.example.com
```

Or use a custom values file:

```bash
helm install drinks-app ./drinks-app -f my-custom-values.yaml
```


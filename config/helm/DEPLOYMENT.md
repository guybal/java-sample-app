# Helm Chart Deployment Guide

Complete guide for deploying the Drinks App using Helm charts to Kubernetes.

## Quick Start

### 1. Build and Push Docker Image

```bash
# Build JAR
mvn clean package -DskipTests

# Build Docker image
docker build -t your-registry/drinks-app:2.0.1 .

# Push to registry
docker push your-registry/drinks-app:2.0.1
```

### 2. Update Image Repository

Edit `config/helm/drinks-app/values.yaml`:
```yaml
image:
  repository: your-registry/drinks-app
  tag: "2.0.1"
```

### 3. Deploy

#### Development (with Embedded PostgreSQL)

```bash
helm install drinks-app ./config/helm/drinks-app \
  -f ./config/helm/drinks-app/values-dev.yaml \
  --namespace development \
  --create-namespace
```

#### Production (with External PostgreSQL)

```bash
# 1. Create namespace
kubectl create namespace production

# 2. Create database secret
kubectl create secret generic drinks-app-db-secret \
  --from-literal=password='your-secure-password' \
  --namespace production

# 3. Update values-production.yaml with your database host
# Edit: database.external.host

# 4. Deploy
helm install drinks-app ./config/helm/drinks-app \
  -f ./config/helm/drinks-app/values-production.yaml \
  --namespace production
```

## Configuration Options

### Database Configuration

#### External PostgreSQL (Production)

```yaml
database:
  external:
    enabled: true
    host: "postgres.example.com"
    port: 5432
    database: drinksdb
    username: postgres
    secretName: drinks-app-db-secret
    secretKey: password
```

#### Embedded PostgreSQL (Development)

```yaml
database:
  embedded:
    enabled: true
    image: postgres:18.0
    persistence:
      enabled: true
      size: 10Gi
```

### Scaling

#### Horizontal Pod Autoscaler (HPA)

```yaml
autoscaling:
  enabled: true
  minReplicas: 3
  maxReplicas: 10
  targetCPUUtilizationPercentage: 80
  targetMemoryUtilizationPercentage: 80
```

### Ingress

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

## Environment-Specific Deployments

### Development

```bash
helm install drinks-app ./config/helm/drinks-app \
  -f ./config/helm/drinks-app/values-dev.yaml \
  --namespace dev
```

Features:
- Single replica
- Embedded PostgreSQL
- Auto-schema creation
- Debug logging

### Staging

```bash
helm install drinks-app ./config/helm/drinks-app \
  -f ./config/helm/drinks-app/values.yaml \
  --set database.external.host=staging-postgres.example.com \
  --namespace staging
```

### Production

```bash
helm install drinks-app ./config/helm/drinks-app \
  -f ./config/helm/drinks-app/values-production.yaml \
  --namespace production
```

Features:
- Multiple replicas
- External PostgreSQL
- Schema validation
- HPA enabled
- Ingress with TLS

## Upgrading

```bash
# Update image tag in values file
# Then upgrade:
helm upgrade drinks-app ./config/helm/drinks-app \
  -f ./config/helm/drinks-app/values-production.yaml \
  --namespace production
```

## Rollback

```bash
# List revisions
helm history drinks-app --namespace production

# Rollback to previous revision
helm rollback drinks-app --namespace production

# Rollback to specific revision
helm rollback drinks-app 2 --namespace production
```

## Troubleshooting

### Check Pod Status

```bash
kubectl get pods -l app.kubernetes.io/name=drinks-app -n production
```

### View Logs

```bash
kubectl logs -l app.kubernetes.io/name=drinks-app -n production -f
```

### Check Database Connection

```bash
# Get pod name
POD_NAME=$(kubectl get pods -l app.kubernetes.io/name=drinks-app -n production -o jsonpath='{.items[0].metadata.name}')

# Check environment variables
kubectl exec $POD_NAME -n production -- env | grep SPRING_DATASOURCE
```

### Port Forward for Testing

```bash
kubectl port-forward svc/drinks-app 8080:8080 -n production
# Access: http://localhost:8080
```

### Test Database Connection

```bash
# For embedded PostgreSQL
kubectl exec -it drinks-app-postgresql-0 -n production -- psql -U postgres -d drinksdb

# Check connection from app pod
kubectl exec -it $POD_NAME -n production -- sh
# Inside pod: psql -h $SPRING_DATASOURCE_URL -U $SPRING_DATASOURCE_USERNAME -d drinksdb
```

## Custom Values

Create your own values file:

```yaml
# my-values.yaml
replicaCount: 5
image:
  tag: "2.0.2"
database:
  external:
    host: "my-postgres.example.com"
```

Deploy with custom values:

```bash
helm install drinks-app ./config/helm/drinks-app \
  -f ./config/helm/drinks-app/values.yaml \
  -f my-values.yaml \
  --namespace production
```

## Uninstallation

```bash
helm uninstall drinks-app --namespace production
```

## Chart Structure

```
config/helm/drinks-app/
├── Chart.yaml              # Chart metadata
├── values.yaml             # Default values
├── values-dev.yaml         # Development values
├── values-production.yaml  # Production values
└── templates/
    ├── deployment.yaml     # Deployment manifest
    ├── service.yaml        # Service manifest
    ├── ingress.yaml        # Ingress manifest
    ├── configmap.yaml      # ConfigMap manifest
    ├── hpa.yaml           # HPA manifest
    ├── postgresql.yaml     # Embedded PostgreSQL (optional)
    ├── serviceaccount.yaml # ServiceAccount manifest
    ├── NOTES.txt          # Post-install notes
    └── _helpers.tpl       # Template helpers
```


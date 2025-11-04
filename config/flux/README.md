# Flux GitOps Manifests for Drinks App

These are the manifests you need to put in your GitOps repository (https://github.com/guybal/app-gitops.git).

## Files to Copy

Copy these files to your GitOps repository's watched path (e.g., `apps/drinks-app/` or wherever your Flux watches):

1. **`gitrepository.yaml`** - Points to private source repository (java-sample-app) with authentication
2. **`helmrelease.yaml`** - Deploys the Helm chart from Git repository path `config/helm/drinks-app`
3. **`image-repository.yaml`** - Watches GHCR for new images
4. **`image-policy.yaml`** - Defines update policy
5. **`image-automation.yaml`** - Auto-updates HelmRelease

## How It Works

The `HelmRelease` references a `GitRepository` source and points to the chart at `config/helm/drinks-app`. According to [Flux HelmRelease documentation](https://fluxcd.io/flux/guides/helmreleases/), when using a GitRepository source:

1. **GitRepository** clones the private repository using the credentials secret
2. **HelmRelease** references the GitRepository and specifies the chart path: `config/helm/drinks-app`
3. Flux automatically creates a `HelmChart` resource that packages the chart from the Git source
4. The Helm chart is deployed using the values specified in `HelmRelease`

## Prerequisites

1. **GHCR Credentials Secret** in your cluster:
   ```bash
   kubectl create secret generic ghcr-credentials \
     --namespace=flux-system \
     --from-literal=username=guybal \
     --from-literal=password=YOUR_GITHUB_TOKEN
   ```

2. **Database Secret**:
   ```bash
   kubectl create secret generic drinks-app-db-secret \
     --namespace=default \
     --from-literal=password=YOUR_DB_PASSWORD
   ```

## Configuration

### Update Image Repository
In `helmrelease.yaml`, update if your image is different:
```yaml
image:
  repository: ghcr.io/guybal/drinks-app
```

### Update Database Host
In `helmrelease.yaml`, set your database host:
```yaml
database:
  external:
    host: your-postgres-host.postgres.database.azure.com
```

### Update Image Automation Path
In `image-automation.yaml`, update the path to match your GitOps structure:
```yaml
update:
  setters:
    - path: ./apps/drinks-app/helmrelease.yaml  # Adjust this path
```

## Deployment

Just commit these files to your GitOps repository and Flux will automatically:
1. Detect the GitRepository
2. Deploy the HelmRelease
3. Watch for new images
4. Auto-update when new images are available

## Verify

```bash
# Check HelmRelease
flux get helmreleases

# Check ImageRepository
flux get images repository

# Check ImagePolicy
flux get images policy

# Check ImageUpdateAutomation
flux get images automation
```

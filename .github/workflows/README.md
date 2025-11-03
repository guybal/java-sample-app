# GitHub Actions Workflows

This directory contains CI/CD workflows for the Drinks App.

## Workflows

### 1. `ci-cd.yml` - Complete CI/CD Pipeline
**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop`
- Tags starting with `v*` (e.g., `v2.0.1`)
- Manual workflow dispatch

**Jobs:**
1. **Test**: Runs Maven tests
2. **Build**: Builds JAR artifact
3. **Docker Build & Push**: Builds and pushes Docker images to:
   - GitHub Container Registry: `ghcr.io/your-username/java-sample-app`

### 2. `docker-build-push.yml` - Docker Build Only
**Triggers:**
- Push to branches/tags
- Pull requests
- Manual workflow dispatch

**Features:**
- Builds Docker image
- Pushes to GitHub Container Registry
- Multi-platform support (amd64, arm64)
- Image caching for faster builds

### 3. `main_az204-webapp-guyb.yml` - Azure Deployment
**Triggers:**
- Push to `main` branch
- Manual workflow dispatch

**Jobs:**
1. **Build**: Builds JAR with Maven
2. **Deploy**: Deploys to Azure Web App

## Required Secrets

No secrets required! GitHub Container Registry uses the automatically provided `GITHUB_TOKEN` which has the necessary permissions.

**Note:** Make sure the workflow has `packages: write` permission (which it does by default).

### Azure (for existing workflow)
- `AZUREAPPSERVICE_CLIENTID_F7E54BBB9628470FB4A3A278D49590A7`
- `AZUREAPPSERVICE_TENANTID_661792E2F2BA4BBB9BD6604B0B4D8D8F`
- `AZUREAPPSERVICE_SUBSCRIPTIONID_B3EFD0635BAD4BF187766C94DDEF3B08`

## Image Tags

The workflows automatically generate tags based on:

- **Branch names**: `main`, `develop`, etc.
- **Semantic versions**: `v2.0.1` → `2.0.1`, `2.0`, `2`, `latest`
- **Commit SHA**: `main-abc123def`
- **Pull requests**: `pr-123`
- **Latest**: Tagged as `latest` on default branch

## Example Image URLs

After deployment, images will be available at:

### GitHub Container Registry
```
docker pull ghcr.io/your-username/java-sample-app:latest
docker pull ghcr.io/your-username/java-sample-app:2.0.1
docker pull ghcr.io/your-username/java-sample-app:main
```

**Note:** You may need to authenticate first:
```bash
echo $GITHUB_TOKEN | docker login ghcr.io -u YOUR_USERNAME --password-stdin
```

## Using Images in Helm Chart

Update `config/helm/drinks-app/values.yaml`:

```yaml
image:
  repository: ghcr.io/your-username/java-sample-app
  pullPolicy: IfNotPresent
  tag: "latest"  # or specific version like "2.0.1"
```

## Manual Trigger

To manually trigger the workflow:

1. Go to `Actions` tab in GitHub
2. Select the workflow
3. Click `Run workflow`
4. Choose branch and options
5. Click `Run workflow`

## Troubleshooting

### Build fails with "GitHub Container Registry login failed"
- Verify workflow has `packages: write` permission (check workflow file)
- Ensure `GITHUB_TOKEN` is available (automatically provided)

### Image not found after push
- Wait a few minutes for image to be available
- Check GitHub Packages page: `https://github.com/YOUR_USERNAME/java-sample-app/pkgs/container/java-sample-app`
- Verify tags match what you expect
- Ensure package visibility is set correctly (Settings → Package visibility)

### Maven build fails
- Check Java version matches (21)
- Verify all dependencies are available
- Check Maven cache

### Permission denied on GitHub Container Registry
- Ensure `packages: write` permission is set in workflow
- Check repository settings allow package creation


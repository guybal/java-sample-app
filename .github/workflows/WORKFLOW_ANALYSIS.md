# CI/CD Workflow Analysis

## Current Jobs (5 total)

### 1. **`versioning`** ⭐
**Purpose**: Calculates and manages semantic versions for both Docker image and Helm chart

**What it does**:
- Reads current versions from `Chart.yaml`
- Calculates new image version (explicit input OR auto-bump: major/minor/patch)
- Calculates new chart version (auto-bump patch) **ONLY if Helm files changed**
- Updates `Chart.yaml` with new versions
- Commits and pushes version changes back to repo

**When it runs**: Always runs first (required by other jobs)

**Dependencies**: None

**Keep?**: ✅ **YES** - Essential for version management

---

### 2. **`test`** ⭐
**Purpose**: Run unit tests to ensure code quality

**What it does**:
- Sets up JDK 21
- Runs `mvn clean test`
- Uploads test results as artifacts

**When it runs**: Always (before build)

**Dependencies**: None

**Keep?**: ✅ **YES** - Essential for quality assurance

---

### 3. **`build`** ⚠️
**Purpose**: Build JAR artifact

**What it does**:
- Sets up JDK 21 (again)
- Runs `mvn clean package -DskipTests` (recompiles everything)
- Uploads JAR artifact

**When it runs**: After `test` and `versioning` pass

**Dependencies**: `test`, `versioning`

**Keep?**: ⚠️ **POTENTIALLY REDUNDANT** 
- **Issue**: Both `test` and `build` compile the code. `test` runs `mvn clean test` (compiles + tests), then `build` runs `mvn clean package -DskipTests` (recompiles everything again).
- **Optimization option**: Could combine into one job that runs `mvn clean package` (which includes tests)

---

### 4. **`docker-build-push`** ⭐
**Purpose**: Build and push Docker image to GHCR

**What it does**:
- Builds Docker image using the Dockerfile
- Tags with version from `versioning` job
- Pushes to `ghcr.io/guybal/java-sample-app`

**When it runs**: After `build` and `versioning` pass

**Dependencies**: `build`, `versioning`

**Keep?**: ✅ **YES** - Essential for container deployment

---

### 5. **`helm-chart-build-push`** ⭐
**Purpose**: Build and push Helm chart to GHCR

**What it does**:
- Checks if Helm files changed
- Packages Helm chart
- Pushes to `ghcr.io/guybal/helm-charts/drinks-app`

**When it runs**: 
- Manual trigger with `helm_only: true`
- Commit message contains `[helm]` or `[chart]`
- On push (but only processes if Helm files changed)

**Dependencies**: `versioning`

**Keep?**: ✅ **YES** - Essential for Helm deployments

---

## Recommendations

### Option 1: Combine `test` + `build` (Recommended)
**Merge into single `build` job**:
```yaml
build:
  steps:
    - name: Build and test
      run: mvn clean package  # This includes tests
    - name: Upload JAR
      ...
    - name: Upload test results
      ...
```

**Benefits**:
- ✅ Eliminates redundant compilation
- ✅ Faster pipeline (single Maven execution)
- ✅ Simpler workflow

**Trade-offs**:
- ⚠️ Less granular visibility (can't see test vs build separately)
- ⚠️ If tests fail, no JAR artifact (but this might be desired)

### Option 2: Keep separate but optimize
**Keep `test` and `build` separate** but make `build` reuse compiled classes:
```yaml
build:
  steps:
    - name: Reuse compiled classes from test
      run: mvn package -DskipTests  # No clean, reuse classes
```

**Benefits**:
- ✅ Better visibility (separate test vs build status)
- ✅ Faster build (no recompilation)

---

## Current Workflow Flow

```
versioning (always)
    ↓
test (always) ────┐
    ↓             │
build (always) ───┴──→ docker-build-push (always)
    ↓
helm-chart-build-push (conditional: helm files changed OR manual trigger)
```

## Suggested Optimized Flow

```
versioning (always)
    ↓
build-and-test (combined) ────→ docker-build-push (always)
    ↓
helm-chart-build-push (conditional: helm files changed OR manual trigger)
```

**Result**: 4 jobs instead of 5, faster execution, same functionality


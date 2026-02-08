# Feature: Django Project Setup & Docker Infrastructure

## Summary

Set up the foundational Django REST Framework project with Docker Compose orchestration, replacing the existing Spring Boot infrastructure. This includes the Django project skeleton with all 12 domain apps, environment variable management via django-environ, PostgreSQL and MinIO services in Docker Compose, gunicorn for production serving, ruff for code quality, and a health check endpoint to verify the system is running.

## User Story

As a backend developer migrating from Spring Boot to Django
I want a fully configured Django project running in Docker with PostgreSQL and MinIO
So that I have a solid foundation to build all API modules on top of

## Problem Statement

The existing Spring Boot project requires Java/Maven expertise the developer lacks. A Django project skeleton with Docker infrastructure must be created that mirrors the existing service topology (PostgreSQL, MinIO) while following Python/Django best practices and conventions.

## Solution Statement

Create a new Django 5.1 project in a `backend/` directory at the repository root (preserving the existing Spring Boot code for reference). The project uses a `config/` package for settings, a dedicated `apps/` directory for all 12 domain apps, Docker Compose for orchestration, and gunicorn for production serving. Environment variables mirror the existing `.env.example` format where possible.

## Metadata

| Field | Value |
|-------|-------|
| Type | NEW_CAPABILITY |
| Complexity | MEDIUM |
| Systems Affected | Docker, Django project structure, settings, environment configuration |
| Dependencies | Django 5.1.x, djangorestframework 3.15.x, django-environ, psycopg2-binary, gunicorn, drf-spectacular, django-cors-headers, django-filter, ruff, minio, WeasyPrint |
| Estimated Tasks | 12 |

---

## UX Design

### Before State

```
╔══════════════════════════════════════════════════════════════════════════╗
║                           BEFORE STATE                                  ║
╠══════════════════════════════════════════════════════════════════════════╣
║                                                                          ║
║   ┌─────────────┐      ┌──────────────┐      ┌─────────────┐           ║
║   │  Developer   │ ──── │ mvn spring-  │ ──── │ Java App    │           ║
║   │  runs build  │      │ boot:run     │      │ on :8080    │           ║
║   └─────────────┘      └──────────────┘      └─────────────┘           ║
║                                │                                         ║
║                    ┌───────────┼───────────┐                            ║
║                    ▼           ▼           ▼                            ║
║              ┌──────────┐ ┌────────┐ ┌──────────┐                      ║
║              │PostgreSQL│ │ MinIO  │ │ RabbitMQ │                      ║
║              │  :5432   │ │ :9000  │ │  :5672   │                      ║
║              └──────────┘ └────────┘ └──────────┘                      ║
║                                                                          ║
║   PAIN_POINT: Developer must use Maven, Java 21, Spring Boot            ║
║   ecosystem they are unfamiliar with                                     ║
║                                                                          ║
╚══════════════════════════════════════════════════════════════════════════╝
```

### After State

```
╔══════════════════════════════════════════════════════════════════════════╗
║                            AFTER STATE                                   ║
╠══════════════════════════════════════════════════════════════════════════╣
║                                                                          ║
║   ┌─────────────┐      ┌──────────────┐      ┌─────────────┐           ║
║   │  Developer   │ ──── │ docker-      │ ──── │ Django App  │           ║
║   │  runs start  │      │ compose up   │      │ on :8000    │           ║
║   └─────────────┘      └──────────────┘      └─────────────┘           ║
║                                │                                         ║
║                    ┌───────────┼───────────┐                            ║
║                    ▼           ▼           ▼                            ║
║              ┌──────────┐ ┌────────┐ ┌───────────────┐                 ║
║              │PostgreSQL│ │ MinIO  │ │ Auto-bucket   │                 ║
║              │  :5432   │ │ :9000  │ │ creation (mc) │                 ║
║              └──────────┘ └────────┘ └───────────────┘                 ║
║                                                                          ║
║   VALUE_ADD: Developer works in familiar Python/Django stack            ║
║   Single command `docker-compose up` starts everything                   ║
║   Health check at GET /api/health/ confirms system ready                 ║
║                                                                          ║
╚══════════════════════════════════════════════════════════════════════════╝
```

### Interaction Changes

| Location | Before | After | User Impact |
|----------|--------|-------|-------------|
| Build command | `mvn clean install` | `docker-compose up --build` | Familiar Python tooling |
| Dev server | Spring Boot on :8080 | Django/gunicorn on :8000 | Same workflow, different port |
| Health check | None explicit | `GET /api/health/` → 200 | Can verify system is running |
| Code quality | Checkstyle (Java) | ruff (Python) | `ruff check .` and `ruff format .` |
| API docs | `/swagger-ui.html` | `/api/schema/swagger-ui/` | Same Swagger UI experience |
| Env config | `application.properties` + spring-dotenv | `.env` + django-environ | Same `.env` file pattern |

---

## Mandatory Reading

**CRITICAL: Implementation agent MUST read these files before starting any task:**

| Priority | File | Lines | Why Read This |
|----------|------|-------|---------------|
| P0 | `compose.yaml` | all | Docker Compose pattern to REPLICATE for Django |
| P0 | `compose-prod.yaml` | all | Production compose pattern for reference |
| P0 | `src/main/resources/application.properties` | all | All env vars that Django settings must support |
| P0 | `.env.example` | all | Environment variable names to keep compatible |
| P1 | `Dockerfile` | all | Multi-stage build pattern to adapt |
| P1 | `nginx.conf` | all | Reverse proxy routes to adapt |
| P1 | `src/main/java/com/sigesi/sigesi/config/MinioConfig.java` | all | MinIO config to replicate |
| P1 | `src/main/java/com/sigesi/sigesi/config/WebConfig.java` | all | CORS origins to replicate |
| P2 | `checkstyle.xml` | all | Code quality rules to map to ruff |
| P2 | `.gitignore` | all | Patterns to extend for Python |

**External Documentation:**

| Source | Section | Why Needed |
|--------|---------|------------|
| [django-environ docs](https://django-environ.readthedocs.io/) | read_env() behavior | Understand .env loading precedence with Docker |
| [drf-spectacular docs](https://drf-spectacular.readthedocs.io/en/latest/readme.html) | Quick Start | Swagger UI setup |
| [WeasyPrint installation](https://doc.courtbouillon.org/weasyprint/stable/first_steps.html) | System dependencies | Docker image system packages |
| [Ruff configuration](https://docs.astral.sh/ruff/configuration/) | Rule selection | Django-specific rules (DJ prefix) |
| [Gunicorn settings](https://docs.gunicorn.org/en/latest/settings.html) | Workers/threads | Production server config |

---

## Patterns to Mirror

**DOCKER_COMPOSE_PATTERN:**
```yaml
# SOURCE: compose.yaml:1-61
# REPLICATE this service topology:
services:
  db:
    image: postgres:17
    environment:
      POSTGRES_DB: sigesi
      POSTGRES_USER: ${DATABASE_USER:-postgres}
      POSTGRES_PASSWORD: ${DATABASE_PASSWORD:-postgres}
    volumes:
      - sigesi_data:/var/lib/postgresql/data
    ports:
      - 5432:5432
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DATABASE_USER:-postgres} -d ${DB_NAME:-sigesi}"]
      interval: 10s
      timeout: 5s
      retries: 5

  minio:
    image: minio/minio:latest
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: ${MINIO_ACCESS_KEY:-minioadmin}
      MINIO_ROOT_PASSWORD: ${MINIO_SECRET_KEY:-minioadmin}
    volumes:
      - minio_data:/data
    command: server /data --console-address ":9001"
```

**ENV_VAR_PATTERN:**
```properties
# SOURCE: .env.example:1-23
# KEEP these variable names compatible:
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
DATABASE_URL=postgres://user:password@db:5432/sigesi
DATABASE_USER=user
DATABASE_PASSWORD=password
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
ADMIN_EMAIL=admin@example.com
```

**CORS_ORIGINS_PATTERN:**
```java
// SOURCE: src/main/java/com/sigesi/sigesi/config/WebConfig.java:15-20
// REPLICATE these allowed origins:
.allowedOrigins("http://ezioalves.space", "http://localhost:3000")
.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
.allowCredentials(true);
```

**ERROR_RESPONSE_PATTERN:**
```java
// SOURCE: src/main/java/com/sigesi/sigesi/config/GlobalExceptionHandler.java:20-25
// REPLICATE this response structure in DRF exception handler:
Map.of(
    "status", HttpStatus.NOT_FOUND.value(),
    "error", "Not Found",
    "message", ex.getMessage())
```

---

## Files to Change

| File | Action | Justification |
|------|--------|---------------|
| `backend/manage.py` | CREATE | Django management script |
| `backend/config/__init__.py` | CREATE | Config package |
| `backend/config/settings/__init__.py` | CREATE | Settings package |
| `backend/config/settings/base.py` | CREATE | Shared settings (DB, apps, middleware, REST framework, CORS, MinIO) |
| `backend/config/settings/local.py` | CREATE | Development overrides (DEBUG=True) |
| `backend/config/settings/production.py` | CREATE | Production security settings |
| `backend/config/urls.py` | CREATE | Root URL configuration with health check and Swagger |
| `backend/config/wsgi.py` | CREATE | WSGI entry point |
| `backend/config/asgi.py` | CREATE | ASGI entry point |
| `backend/apps/__init__.py` | CREATE | Apps package |
| `backend/apps/usuarios/__init__.py` | CREATE | Usuarios app init |
| `backend/apps/usuarios/apps.py` | CREATE | Usuarios app config |
| `backend/apps/usuarios/models.py` | CREATE | Empty placeholder (models come in Phase 2) |
| `backend/apps/solicitacoes/__init__.py` | CREATE | Solicitacoes app init |
| `backend/apps/solicitacoes/apps.py` | CREATE | Solicitacoes app config |
| `backend/apps/solicitacoes/models.py` | CREATE | Empty placeholder |
| `backend/apps/demandas/__init__.py` | CREATE | Demandas app init |
| `backend/apps/demandas/apps.py` | CREATE | Demandas app config |
| `backend/apps/demandas/models.py` | CREATE | Empty placeholder |
| `backend/apps/materiais/__init__.py` | CREATE | Materiais app init |
| `backend/apps/materiais/apps.py` | CREATE | Materiais app config |
| `backend/apps/materiais/models.py` | CREATE | Empty placeholder |
| `backend/apps/comentarios/__init__.py` | CREATE | Comentarios app init |
| `backend/apps/comentarios/apps.py` | CREATE | Comentarios app config |
| `backend/apps/comentarios/models.py` | CREATE | Empty placeholder |
| `backend/apps/documentos/__init__.py` | CREATE | Documentos app init |
| `backend/apps/documentos/apps.py` | CREATE | Documentos app config |
| `backend/apps/documentos/models.py` | CREATE | Empty placeholder |
| `backend/apps/arquivos/__init__.py` | CREATE | Arquivos app init |
| `backend/apps/arquivos/apps.py` | CREATE | Arquivos app config |
| `backend/apps/arquivos/models.py` | CREATE | Empty placeholder |
| `backend/apps/enderecos/__init__.py` | CREATE | Enderecos app init |
| `backend/apps/enderecos/apps.py` | CREATE | Enderecos app config |
| `backend/apps/enderecos/models.py` | CREATE | Empty placeholder |
| `backend/apps/cemiterios/__init__.py` | CREATE | Cemiterios app init |
| `backend/apps/cemiterios/apps.py` | CREATE | Cemiterios app config |
| `backend/apps/cemiterios/models.py` | CREATE | Empty placeholder |
| `backend/apps/jazigos/__init__.py` | CREATE | Jazigos app init |
| `backend/apps/jazigos/apps.py` | CREATE | Jazigos app config |
| `backend/apps/jazigos/models.py` | CREATE | Empty placeholder |
| `backend/apps/gavetas/__init__.py` | CREATE | Gavetas app init |
| `backend/apps/gavetas/apps.py` | CREATE | Gavetas app config |
| `backend/apps/gavetas/models.py` | CREATE | Empty placeholder |
| `backend/apps/pessoas/__init__.py` | CREATE | Pessoas app init |
| `backend/apps/pessoas/apps.py` | CREATE | Pessoas app config |
| `backend/apps/pessoas/models.py` | CREATE | Empty placeholder |
| `backend/requirements.txt` | CREATE | Python dependencies |
| `backend/Dockerfile` | CREATE | Multi-stage Docker build for Django |
| `backend/docker-compose.yml` | CREATE | Development compose with PostgreSQL + MinIO |
| `backend/gunicorn.conf.py` | CREATE | Gunicorn production config |
| `backend/.env.example` | CREATE | Environment variable template |
| `backend/pyproject.toml` | CREATE | Ruff config and project metadata |
| `backend/.gitignore` | CREATE | Python-specific gitignore |
| `backend/.dockerignore` | CREATE | Docker build exclusions |

---

## NOT Building (Scope Limits)

- **Django models with fields** - Only empty `models.py` placeholders; actual models are Phase 2
- **Authentication/OAuth2** - No django-allauth setup; that's Phase 3
- **Any API endpoints** - Only the health check; all module endpoints come in Phase 4+
- **Serializers/ViewSets** - No DRF serializers; those come with their respective modules
- **Tests** - Explicitly deprioritized per PRD
- **RabbitMQ/Celery** - Dropped from migration scope per PRD
- **Audit logging** - Dropped from migration scope per PRD
- **nginx.conf** - Production reverse proxy stays as-is for now, only dev compose needed
- **Production docker-compose** - Only dev compose; production compose deferred to Phase 8
- **Admin interface** - django-admin left at default; customization out of scope per PRD open questions

---

## Step-by-Step Tasks

Execute in order. Each task is atomic and independently verifiable.

### Task 1: CREATE `backend/requirements.txt`

- **ACTION**: Create Python dependencies file
- **IMPLEMENT**:
  ```
  Django==5.1.6
  djangorestframework==3.15.2
  django-environ==0.12.0
  django-cors-headers==4.6.0
  django-filter==24.3
  drf-spectacular==0.28.0
  psycopg2-binary==2.9.10
  gunicorn==23.0.0
  minio==7.2.15
  WeasyPrint==63.1
  ruff==0.9.6
  ```
- **GOTCHA**: Use `psycopg2-binary` (not `psycopg2`) to avoid needing libpq-dev at runtime. Pin all versions for reproducible builds.
- **VALIDATE**: File exists and is valid text

### Task 2: CREATE `backend/pyproject.toml`

- **ACTION**: Create project config with ruff settings
- **IMPLEMENT**: Ruff configuration targeting Python 3.12, line-length 120, Django-specific rules (E, W, F, I, B, C4, UP, DJ, SIM, N), exclude migrations, per-file ignores for tests and settings
- **MIRROR**: Code quality expectations from `checkstyle.xml` (max line length 140 → 120 in Python convention)
- **VALIDATE**: `cd backend && ruff check --config pyproject.toml` runs without config errors

### Task 3: CREATE Django project skeleton (`backend/config/` and `backend/manage.py`)

- **ACTION**: Create Django project configuration package
- **IMPLEMENT**:
  - `backend/manage.py` - Standard Django management script, default settings module = `config.settings.local`
  - `backend/config/__init__.py` - Empty
  - `backend/config/wsgi.py` - WSGI app, `DJANGO_SETTINGS_MODULE` = `config.settings.production`
  - `backend/config/asgi.py` - ASGI app, `DJANGO_SETTINGS_MODULE` = `config.settings.production`
- **GOTCHA**: `manage.py` should default to `config.settings.local` (dev), but `wsgi.py`/`asgi.py` should default to `config.settings.production` (production via gunicorn)
- **VALIDATE**: Files exist with correct `DJANGO_SETTINGS_MODULE` references

### Task 4: CREATE `backend/config/settings/base.py`

- **ACTION**: Create shared settings file
- **IMPLEMENT**:
  - `django-environ` setup with `Env.read_env()` from `BASE_DIR / '.env'`
  - `SECRET_KEY` from env
  - `DEBUG` from env (default False)
  - `ALLOWED_HOSTS` from env (default [])
  - `INSTALLED_APPS`: django defaults + `rest_framework`, `drf_spectacular`, `django_filters`, `corsheaders`, all 12 domain apps (`apps.usuarios`, `apps.solicitacoes`, etc.)
  - `MIDDLEWARE`: security, cors (before CommonMiddleware), session, common, csrf, auth, messages, clickjacking
  - `ROOT_URLCONF` = `config.urls`
  - `TEMPLATES` config
  - `DATABASES` using `env.db('DATABASE_URL')` with default `postgres://postgres:postgres@db:5432/sigesi`
  - `AUTH_USER_MODEL` = `usuarios.Usuario`
  - `REST_FRAMEWORK` with `DEFAULT_SCHEMA_CLASS` = `drf_spectacular.openapi.AutoSchema`, default filter backend, pagination
  - `SPECTACULAR_SETTINGS`: title=SIGESI API, version=1.0.0
  - `CORS_ALLOWED_ORIGINS`: `http://localhost:3000`, `http://ezioalves.space`
  - `CORS_ALLOW_CREDENTIALS` = True
  - `CORS_ALLOW_METHODS`: GET, POST, PUT, PATCH, DELETE, OPTIONS
  - MinIO settings from env: `MINIO_ENDPOINT`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY`, `MINIO_BUCKET_NAME`
  - `ADMIN_EMAIL` from env
  - File upload limits: `DATA_UPLOAD_MAX_MEMORY_SIZE` = 50MB, `FILE_UPLOAD_MAX_MEMORY_SIZE` = 10MB
  - `STATIC_URL`, `STATIC_ROOT`
  - `DEFAULT_AUTO_FIELD` = `django.db.models.BigAutoField`
- **MIRROR**: Environment variables from `src/main/resources/application.properties` and `.env.example`
- **MIRROR**: CORS origins from `src/main/java/com/sigesi/sigesi/config/WebConfig.java:15-20`
- **GOTCHA**: `corsheaders.middleware.CorsMiddleware` MUST be placed before `CommonMiddleware` in MIDDLEWARE list. `AUTH_USER_MODEL` must be set BEFORE first migration.
- **VALIDATE**: Python syntax is valid: `python -c "import ast; ast.parse(open('backend/config/settings/base.py').read())"`

### Task 5: CREATE `backend/config/settings/local.py` and `backend/config/settings/production.py`

- **ACTION**: Create environment-specific settings overrides
- **IMPLEMENT**:
  - `local.py`: imports from base, `DEBUG=True`, `ALLOWED_HOSTS=['*']`
  - `production.py`: imports from base, `DEBUG=False`, security headers (`SECURE_PROXY_SSL_HEADER`, `SESSION_COOKIE_SECURE`, `CSRF_COOKIE_SECURE`)
  - `__init__.py`: empty file
- **MIRROR**: Session cookie config from `application.properties:16-17` (SameSite=Lax, Secure=false for dev)
- **VALIDATE**: Python syntax valid for both files

### Task 6: CREATE `backend/config/urls.py`

- **ACTION**: Create root URL configuration with health check and Swagger
- **IMPLEMENT**:
  ```python
  from django.contrib import admin
  from django.urls import path, include
  from django.http import JsonResponse
  from drf_spectacular.views import (
      SpectacularAPIView,
      SpectacularSwaggerView,
      SpectacularRedocView,
  )

  def health_check(request):
      return JsonResponse({"status": "ok"})

  urlpatterns = [
      path("admin/", admin.site.urls),
      path("api/health/", health_check, name="health-check"),
      path("api/schema/", SpectacularAPIView.as_view(), name="schema"),
      path("api/schema/swagger-ui/", SpectacularSwaggerView.as_view(url_name="schema"), name="swagger-ui"),
      path("api/schema/redoc/", SpectacularRedocView.as_view(url_name="schema"), name="redoc"),
  ]
  ```
- **GOTCHA**: Health check is a simple Django view, not a DRF view - no authentication required
- **VALIDATE**: Python syntax valid

### Task 7: CREATE all 12 app skeletons under `backend/apps/`

- **ACTION**: Create empty Django app structures for all domain modules
- **IMPLEMENT**: For each of: `usuarios`, `solicitacoes`, `demandas`, `materiais`, `comentarios`, `documentos`, `arquivos`, `enderecos`, `cemiterios`, `jazigos`, `gavetas`, `pessoas`:
  - `__init__.py` - empty
  - `apps.py` - AppConfig with `name = "apps.<app_name>"` and Portuguese `verbose_name`
  - `models.py` - empty file with docstring only (models come in Phase 2)
  - `migrations/__init__.py` - empty (enables migrations directory)
- **GOTCHA**: `apps.py` MUST set `name = "apps.<app_name>"` (not just `<app_name>`) because apps live inside the `apps/` directory. Also create `backend/apps/__init__.py`.
- **VALIDATE**: Each app has `__init__.py`, `apps.py`, `models.py`, `migrations/__init__.py`

### Task 8: CREATE `backend/.env.example`

- **ACTION**: Create environment variable template matching existing Spring Boot vars
- **IMPLEMENT**:
  ```bash
  # Django
  SECRET_KEY=change-me-to-a-random-string
  DEBUG=True
  ALLOWED_HOSTS=localhost,127.0.0.1
  DJANGO_SETTINGS_MODULE=config.settings.local

  # Database
  DATABASE_URL=postgres://postgres:postgres@db:5432/sigesi

  # Google OAuth2
  GOOGLE_CLIENT_ID=your-google-client-id
  GOOGLE_CLIENT_SECRET=your-google-client-secret

  # OAuth2 Redirects
  OAUTH2_SUCCESS_REDIRECT=http://localhost:3000
  OAUTH2_FAILURE_REDIRECT=http://localhost:3000/login?error=true
  OAUTH2_LOGOUT_REDIRECT=http://localhost:3000

  # MinIO
  MINIO_ENDPOINT=http://minio:9000
  MINIO_ACCESS_KEY=minioadmin
  MINIO_SECRET_KEY=minioadmin
  MINIO_BUCKET_NAME=sigesi-files

  # Admin
  ADMIN_EMAIL=admin@example.com
  ```
- **MIRROR**: Variable names from `.env.example` at project root
- **GOTCHA**: `DATABASE_URL` uses `postgres://` format (django-environ converts it). Dropped `RABBITMQ_*` vars (not in scope).
- **VALIDATE**: File exists

### Task 9: CREATE `backend/Dockerfile`

- **ACTION**: Create multi-stage Dockerfile for Django + WeasyPrint
- **IMPLEMENT**:
  - **Builder stage** (`python:3.12-slim`):
    - Install `gcc`, `libpq-dev` for compiling psycopg2
    - `pip wheel` all requirements into `/app/wheels`
  - **Final stage** (`python:3.12-slim`):
    - Install WeasyPrint system deps: `libpq5`, `libpango-1.0-0`, `libpangoft2-1.0-0`, `libharfbuzz-subset0`, `libffi8`, `libjpeg62-turbo`, `libopenjp2-7`, `fonts-liberation`
    - Install Python wheels from builder
    - Copy app code
    - `collectstatic --noinput`
    - Create non-root user `app`
    - `CMD gunicorn config.wsgi:application --config gunicorn.conf.py`
    - `EXPOSE 8000`
  - Set `PYTHONDONTWRITEBYTECODE=1` and `PYTHONUNBUFFERED=1`
- **MIRROR**: Multi-stage pattern from existing `Dockerfile` (builder → final)
- **GOTCHA**: Use `python:3.12-slim` NOT alpine (WeasyPrint system deps are painful on Alpine). `collectstatic` may fail if Django can't import settings without DB - use `|| true` fallback.
- **VALIDATE**: `docker build -t sigesi-django-test backend/` builds successfully

### Task 10: CREATE `backend/docker-compose.yml`

- **ACTION**: Create development Docker Compose with PostgreSQL, MinIO, and Django
- **IMPLEMENT**:
  - `db` service: `postgres:17`, health check with `pg_isready`, volume `sigesi_data`, port 5432
  - `minio` service: `minio/minio:latest`, ports 9000 + 9001, health check, volume `minio_data`
  - `createbuckets` service: `minio/mc:latest`, depends on minio healthy, creates `sigesi-files` bucket, `restart: "no"`
  - `web` service: build from `.`, `gunicorn config.wsgi:application --config gunicorn.conf.py`, depends on db healthy and minio healthy, `env_file: .env`, port 8000, runs `python manage.py migrate && gunicorn ...` as entrypoint
  - Named volumes: `sigesi_data`, `minio_data`
  - Network: `sigesi-network`
- **MIRROR**: Service names and configuration from `compose.yaml` at project root
- **GOTCHA**: The `web` service should run migrations before starting gunicorn. Use a shell entrypoint: `sh -c "python manage.py migrate --noinput && gunicorn config.wsgi:application --config gunicorn.conf.py"`. The `createbuckets` service uses `restart: "no"` since it's a one-shot task.
- **VALIDATE**: `cd backend && docker-compose config` validates the compose file

### Task 11: CREATE `backend/gunicorn.conf.py`

- **ACTION**: Create gunicorn production configuration
- **IMPLEMENT**:
  ```python
  """Gunicorn configuration for SIGESI."""
  bind = "0.0.0.0:8000"
  workers = 4
  worker_class = "gthread"
  threads = 2
  timeout = 120
  graceful_timeout = 30
  keepalive = 5
  max_requests = 1000
  max_requests_jitter = 50
  accesslog = "-"
  errorlog = "-"
  loglevel = "info"
  proc_name = "sigesi"
  ```
- **GOTCHA**: `timeout = 120` is intentionally high for PDF generation and file uploads. `workers = 4` hardcoded (better than `cpu_count() * 2 + 1` in Docker where CPU is often limited). Logging to `-` (stdout) is required for Docker.
- **VALIDATE**: Python syntax valid

### Task 12: CREATE `backend/.gitignore` and `backend/.dockerignore`

- **ACTION**: Create Python-specific ignore files
- **IMPLEMENT**:
  - `.gitignore`:
    ```
    __pycache__/
    *.py[cod]
    *$py.class
    *.so
    .env
    .venv/
    venv/
    db.sqlite3
    staticfiles/
    mediafiles/
    *.egg-info/
    dist/
    build/
    .ruff_cache/
    .pytest_cache/
    *.log
    .DS_Store
    ```
  - `.dockerignore`:
    ```
    .git
    .gitignore
    .env
    .venv
    venv
    __pycache__
    *.pyc
    .ruff_cache
    .pytest_cache
    *.md
    docker-compose.yml
    .dockerignore
    ```
- **VALIDATE**: Files exist

---

## Testing Strategy

### Verification Tests (No unit tests per PRD)

| Check | What It Validates | Command |
|-------|-------------------|---------|
| Docker build | Dockerfile is valid | `docker build -t sigesi-test backend/` |
| Compose up | All services start | `cd backend && docker-compose up -d` |
| Health check | Django responds | `curl http://localhost:8000/api/health/` → `{"status": "ok"}` |
| PostgreSQL | DB accessible | `docker-compose exec db pg_isready -U postgres` |
| MinIO | Storage accessible | `curl http://localhost:9000/minio/health/live` |
| Swagger UI | API docs render | `curl -s http://localhost:8000/api/schema/swagger-ui/` → 200 |
| Migrations | Django migrate works | `docker-compose exec web python manage.py migrate --check` |
| Ruff | Code passes lint | `cd backend && ruff check .` |

### Edge Cases Checklist

- [ ] Missing `.env` file (django-environ silently ignores, Docker env_file may error)
- [ ] PostgreSQL not ready when Django starts (health check + depends_on handles this)
- [ ] MinIO bucket doesn't exist (createbuckets service handles this)
- [ ] Invalid DATABASE_URL format (django-environ raises clear error)

---

## Validation Commands

### Level 1: STATIC_ANALYSIS

```bash
cd backend && ruff check . && ruff format --check .
```

**EXPECT**: Exit 0, no errors or warnings

### Level 2: DJANGO_CHECKS

```bash
cd backend && python manage.py check --deploy 2>&1 || true
# Note: some deploy checks will warn without HTTPS, that's expected in dev
```

**EXPECT**: No critical errors (warnings about HTTPS in dev are OK)

### Level 3: DOCKER_BUILD

```bash
cd backend && docker build -t sigesi-django-test .
```

**EXPECT**: Build completes successfully

### Level 4: FULL_STACK

```bash
cd backend && docker-compose up -d && sleep 10 && curl -f http://localhost:8000/api/health/
```

**EXPECT**: Health check returns `{"status": "ok"}`

### Level 5: SWAGGER_UI

```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8000/api/schema/swagger-ui/
```

**EXPECT**: HTTP 200

---

## Acceptance Criteria

- [ ] `docker-compose up` starts Django, PostgreSQL, and MinIO successfully
- [ ] `GET /api/health/` returns `{"status": "ok"}` with HTTP 200
- [ ] `GET /api/schema/swagger-ui/` renders Swagger UI
- [ ] `python manage.py migrate` runs without errors
- [ ] `ruff check .` passes with no violations
- [ ] All 12 domain apps registered in INSTALLED_APPS
- [ ] MinIO bucket `sigesi-files` auto-created
- [ ] `.env.example` contains all required environment variables
- [ ] AUTH_USER_MODEL set to `usuarios.Usuario` before any migrations

---

## Completion Checklist

- [ ] All 12 tasks completed in dependency order
- [ ] Each task validated immediately after completion
- [ ] Level 1: ruff passes
- [ ] Level 3: Docker build succeeds
- [ ] Level 4: Full stack starts and health check passes
- [ ] Level 5: Swagger UI accessible
- [ ] All acceptance criteria met

---

## Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| WeasyPrint system deps missing in Docker | LOW | HIGH | Explicitly list all Debian packages; test build early |
| AUTH_USER_MODEL set after initial migration | LOW | HIGH | Set in base.py before ANY migration run; create usuarios app skeleton first |
| django-environ DATABASE_URL parsing | LOW | MED | Use `postgres://` scheme (not `postgresql://`); test with default value |
| Docker Compose service ordering | LOW | MED | Use `depends_on` with `condition: service_healthy` |
| Port conflict with existing Spring Boot compose | MED | LOW | Django uses port 8000 (Spring uses 8080); different compose files |

---

## Notes

- The `backend/` directory is created alongside the existing Spring Boot project, not replacing it. This allows reference during migration.
- `AUTH_USER_MODEL = 'usuarios.Usuario'` is set now even though the model is empty. This MUST be set before the first migration is ever run, or Django will create the default User model and switching later requires a complex migration.
- RabbitMQ is intentionally excluded from docker-compose per PRD decision to drop notifications.
- The `createbuckets` sidecar service ensures the MinIO bucket exists without needing application-level init code (simpler than the Spring Boot `CommandLineRunner` approach in `SigesiApplication.java`).
- Production compose and nginx config adaptation are deferred to Phase 8.
- All app `models.py` files are empty placeholders - Phase 2 will populate them with actual Django models.

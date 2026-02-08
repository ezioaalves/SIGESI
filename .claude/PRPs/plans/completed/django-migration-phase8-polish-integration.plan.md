# Feature: Phase 8 - Polish & Integration

## Summary

Finalize the SIGESI Django backend for production readiness by enhancing OpenAPI documentation with `@extend_schema` decorators on all 10 custom `@action` endpoints, improving `SPECTACULAR_SETTINGS` for a polished Swagger UI experience, adding a Docker health check for the web service, and performing final endpoint verification against the Spring Boot feature list. Most Phase 8 infrastructure (exception handler, CORS, drf-spectacular base config, permissions) is already implemented from prior phases.

## User Story

As a solo backend developer
I want a fully documented, production-ready Django API with Swagger UI, proper error responses, and Docker health checks
So that I can confidently deploy, explore, and maintain all 66+ endpoints

## Problem Statement

The Django backend has all functional modules implemented but lacks:
- `@extend_schema` decorators on custom actions (file upload, PDF download, presigned URL, etc.) causing poor Swagger UI documentation for these endpoints
- Enhanced SPECTACULAR_SETTINGS for production-quality Swagger UI (tag descriptions, server URLs, component splitting for file uploads)
- Docker health check for the web service container
- Final verification that all Spring Boot endpoints have Django equivalents

## Solution Statement

Add targeted `@extend_schema` and `@extend_schema_view` decorators to all ViewSets with custom actions, enhance SPECTACULAR_SETTINGS, add a web service health check to docker-compose.yml, and verify endpoint parity. No structural changes needed - this is a documentation and polish phase.

## Metadata

| Field            | Value                                                        |
| ---------------- | ------------------------------------------------------------ |
| Type             | ENHANCEMENT                                                  |
| Complexity       | LOW                                                          |
| Systems Affected | config/settings, config/urls, all 12 ViewSets, docker-compose |
| Dependencies     | drf-spectacular>=0.28.0 (already installed)                  |
| Estimated Tasks  | 7                                                            |

---

## UX Design

### Before State

```
+----------------------------------------------------+
|  Swagger UI at /api/schema/swagger-ui/             |
|                                                    |
|  [All 66 endpoints visible]                        |
|  BUT:                                              |
|  - Custom @action endpoints have NO descriptions   |
|  - File upload shows wrong request body schema     |
|  - PDF download shows JSON response instead of     |
|    binary                                          |
|  - No tag descriptions in sidebar                  |
|  - No server URLs configured                       |
|  - Authorize button present but minimal guidance   |
|                                                    |
|  Docker: web service has NO healthcheck            |
|  - Other services cannot depend on web:healthy     |
+----------------------------------------------------+
```

### After State

```
+----------------------------------------------------+
|  Swagger UI at /api/schema/swagger-ui/             |
|                                                    |
|  [All 66 endpoints with full documentation]        |
|  - Custom actions have summaries & descriptions    |
|  - File upload shows multipart/form-data schema    |
|  - PDF download shows binary response type         |
|  - Tags have descriptions in sidebar               |
|  - Server URLs (local dev + production)            |
|  - persistAuthorization for session cookie          |
|  - Search/filter bar in Swagger UI                 |
|                                                    |
|  Docker: web service has healthcheck               |
|  - curl to /api/health/ with start_period          |
+----------------------------------------------------+
```

### Interaction Changes

| Location | Before | After | User Impact |
|----------|--------|-------|-------------|
| Swagger UI sidebar | Tags with no descriptions | Tags with Portuguese descriptions | Easier navigation |
| File upload endpoint | Wrong schema display | Correct multipart/form-data with file field | Can test uploads from Swagger |
| PDF download endpoint | Shows JSON schema | Shows binary/PDF response | Correct documentation |
| Presigned URL endpoint | Generic response | Typed response with field descriptions | Clear API contract |
| Docker health | No web healthcheck | Python urllib check with start_period | Reliable container orchestration |

---

## Mandatory Reading

**CRITICAL: Implementation agent MUST read these files before starting any task:**

| Priority | File | Lines | Why Read This |
|----------|------|-------|---------------|
| P0 | `backend/config/settings/base.py` | 132-155 | Current REST_FRAMEWORK and SPECTACULAR_SETTINGS to UPDATE |
| P0 | `backend/config/urls.py` | 1-41 | Current URL routing structure |
| P0 | `backend/apps/core/exceptions.py` | 1-102 | Exception handler format to document in schema |
| P1 | `backend/apps/arquivos/views.py` | 1-105 | File upload/download/presigned URL actions to annotate |
| P1 | `backend/apps/documentos/views.py` | 1-79 | PDF download action to annotate |
| P1 | `backend/apps/demandas/views.py` | 1-111 | by_solicitacao and by_responsavel actions to annotate |
| P1 | `backend/apps/usuarios/views.py` | 1-77 | /me and /toggle-ativo actions to annotate |
| P1 | `backend/apps/pessoas/views.py` | 1-86 | /cpf lookup action to annotate |
| P1 | `backend/apps/comentarios/views.py` | 37-51 | by_demanda action to annotate |
| P2 | `backend/apps/solicitacoes/views.py` | 1-57 | Role-filtered list to document |
| P2 | `backend/apps/gavetas/views.py` | 1-62 | Filtered ViewSet to document |
| P2 | `backend/docker-compose.yml` | 54-69 | Web service to add healthcheck |
| P2 | `backend/pyproject.toml` | 1-67 | Dependencies for version verification |

**External Documentation:**

| Source | Section | Why Needed |
|--------|---------|------------|
| [drf-spectacular Settings](https://drf-spectacular.readthedocs.io/en/latest/settings.html) | SPECTACULAR_SETTINGS reference | All available settings for Swagger UI customization |
| [drf-spectacular Customization](https://drf-spectacular.readthedocs.io/en/latest/customization.html) | @extend_schema usage | Decorator patterns for custom actions |
| [drf-spectacular FAQ](https://drf-spectacular.readthedocs.io/en/latest/faq.html) | File upload, binary responses | Gotchas for file endpoints |

---

## Patterns to Mirror

**VIEWSET_PATTERN:**
```python
# SOURCE: backend/apps/materiais/views.py:1-20
# COPY THIS PATTERN for simple ViewSets:
class MaterialViewSet(ModelViewSet):
    """ViewSet for Material CRUD operations."""

    queryset = Material.objects.all().order_by("id")
    permission_classes = [IsAllRoles]

    def get_serializer_class(self):
        """Return the appropriate serializer per action."""
        if self.action == "create":
            return MaterialCreateSerializer
        if self.action in ("update", "partial_update"):
            return MaterialUpdateSerializer
        return MaterialResponseSerializer
```

**CUSTOM_ACTION_PATTERN:**
```python
# SOURCE: backend/apps/arquivos/views.py:44-71
# COPY THIS PATTERN for @action with @extend_schema:
@extend_schema(
    summary="Upload a file",
    description="Upload a file to MinIO storage.",
    request={"multipart/form-data": {...}},
    responses={201: ArquivoResponseSerializer},
    tags=["arquivos"],
)
@action(
    detail=False,
    methods=["post"],
    url_path="upload",
    url_name="upload",
    parser_classes=[MultiPartParser, FormParser],
)
def upload(self, request):
    ...
```

**EXCEPTION_HANDLER_PATTERN:**
```python
# SOURCE: backend/apps/core/exceptions.py:76-101
# Error response format to document: {"status": code, "error": "type", "message": "detail"}
```

---

## Files to Change

| File | Action | Justification |
|------|--------|---------------|
| `backend/config/settings/base.py` | UPDATE | Enhance SPECTACULAR_SETTINGS with tags, servers, UI config, component splitting |
| `backend/apps/arquivos/views.py` | UPDATE | Add @extend_schema to upload, presigned_url, download actions |
| `backend/apps/documentos/views.py` | UPDATE | Add @extend_schema to pdf action |
| `backend/apps/demandas/views.py` | UPDATE | Add @extend_schema to by_solicitacao, by_responsavel actions |
| `backend/apps/usuarios/views.py` | UPDATE | Add @extend_schema to me, toggle_ativo actions |
| `backend/apps/pessoas/views.py` | UPDATE | Add @extend_schema to by_cpf action |
| `backend/apps/comentarios/views.py` | UPDATE | Add @extend_schema to by_demanda action |
| `backend/docker-compose.yml` | UPDATE | Add healthcheck to web service |

---

## NOT Building (Scope Limits)

- **No `@extend_schema_view` on standard CRUD actions** - drf-spectacular auto-detects serializer classes from `get_serializer_class()` for standard list/create/retrieve/update/destroy. Only custom `@action` methods need explicit decoration.
- **No error response decorators on every endpoint** - The custom exception handler already provides consistent error format. Adding `responses={400: ..., 401: ..., 403: ...}` to every endpoint would be verbose and low-value.
- **No `drf-spectacular[sidecar]` installation** - CDN-served Swagger UI assets are fine for this project. Sidecar adds complexity without meaningful benefit.
- **No custom AutoSchema subclass** - The built-in AutoSchema with `SCHEMA_PATH_PREFIX` handles tag extraction automatically.
- **No new dependencies** - Everything needed is already installed.
- **No test suite** - Explicitly deprioritized per PRD.

---

## Step-by-Step Tasks

Execute in order. Each task is atomic and independently verifiable.

### Task 1: UPDATE `backend/config/settings/base.py` - Enhance SPECTACULAR_SETTINGS

- **ACTION**: Replace the existing `SPECTACULAR_SETTINGS` block (lines 149-155) with enhanced configuration
- **IMPLEMENT**: Add tag descriptions, server URLs, Swagger UI settings, component splitting for file uploads, enum handling
- **MIRROR**: Keep existing TITLE, DESCRIPTION, VERSION, SERVE_INCLUDE_SCHEMA values
- **SPECIFIC CHANGES**:
  ```python
  SPECTACULAR_SETTINGS = {
      "TITLE": "SIGESI API",
      "DESCRIPTION": "Sistema de Gerenciamento da Secretaria de Infraestrutura",
      "VERSION": "1.0.0",
      "SERVE_INCLUDE_SCHEMA": False,
      "SCHEMA_PATH_PREFIX": r"/api/",
      "COMPONENT_SPLIT_REQUEST": True,
      "SWAGGER_UI_SETTINGS": {
          "deepLinking": True,
          "persistAuthorization": True,
          "displayOperationId": False,
          "filter": True,
      },
      "TAGS": [
          {"name": "usuarios", "description": "Gerenciamento de usuarios"},
          {"name": "solicitacoes", "description": "Solicitacoes de servico dos cidadaos"},
          {"name": "demandas", "description": "Demandas de trabalho"},
          {"name": "comentarios", "description": "Comentarios em demandas"},
          {"name": "materiais", "description": "Catalogo de materiais"},
          {"name": "documentos", "description": "Documentos oficiais (oficios e memorandos)"},
          {"name": "arquivos", "description": "Upload e armazenamento de arquivos"},
          {"name": "enderecos", "description": "Gerenciamento de enderecos"},
          {"name": "cemiterios", "description": "Gerenciamento de cemiterios"},
          {"name": "jazigos", "description": "Gerenciamento de jazigos"},
          {"name": "gavetas", "description": "Gerenciamento de gavetas"},
          {"name": "pessoas", "description": "Gerenciamento de pessoas"},
      ],
  }
  ```
- **GOTCHA**: `COMPONENT_SPLIT_REQUEST = True` is critical - without it, file upload endpoints show the wrong request body schema because FileField behaves differently for requests vs responses. `SCHEMA_PATH_PREFIX` enables automatic tag extraction from URL paths like `/api/solicitacoes/` -> tag `solicitacoes`.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check config/settings/base.py`

### Task 2: UPDATE `backend/apps/arquivos/views.py` - Add @extend_schema to file actions

- **ACTION**: Add `@extend_schema` decorators to the 3 custom actions: upload, presigned_url, download
- **IMPLEMENT**:
  - `upload`: Document multipart/form-data request with `file` (binary) and `categoria` (string) fields, response 201 with ArquivoResponseSerializer
  - `presigned_url`: Document response with FileUrlResponseSerializer
  - `download`: Document binary response with `(200, "application/octet-stream")` tuple syntax
- **IMPORTS TO ADD**: `from drf_spectacular.utils import extend_schema, OpenApiTypes`
- **SPECIFIC CHANGES** (decorators to add before each @action):
  ```python
  @extend_schema(
      summary="Upload de arquivo",
      description="Faz upload de um arquivo para o MinIO. Maximo 10MB por arquivo.",
      request={
          "multipart/form-data": {
              "type": "object",
              "properties": {
                  "file": {"type": "string", "format": "binary"},
                  "categoria": {"type": "string", "description": "Categoria do arquivo"},
              },
              "required": ["file"],
          },
      },
      responses={201: ArquivoResponseSerializer},
  )
  # before upload action

  @extend_schema(
      summary="URL de download temporaria",
      description="Gera URL pre-assinada para download direto do MinIO (60 min).",
      responses={200: FileUrlResponseSerializer},
  )
  # before presigned_url action

  @extend_schema(
      summary="Download de arquivo",
      description="Download do arquivo via proxy do servidor.",
      responses={(200, "application/octet-stream"): OpenApiTypes.BINARY},
  )
  # before download action
  ```
- **GOTCHA**: The `@extend_schema` decorator MUST go ABOVE the `@action` decorator. Order matters for drf-spectacular to pick it up correctly.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/arquivos/views.py`

### Task 3: UPDATE `backend/apps/documentos/views.py` - Add @extend_schema to pdf action

- **ACTION**: Add `@extend_schema` decorator to the pdf download action
- **IMPORTS TO ADD**: `from drf_spectacular.utils import extend_schema, OpenApiTypes`
- **SPECIFIC CHANGES**:
  ```python
  @extend_schema(
      summary="Download do documento em PDF",
      description="Gera e faz download do documento oficial em formato PDF.",
      responses={(200, "application/pdf"): OpenApiTypes.BINARY},
  )
  # before @action(detail=True, methods=["get"], url_path="pdf", url_name="pdf")
  ```
- **GOTCHA**: Use `(200, "application/pdf")` tuple syntax for the response to correctly document the content type as PDF, not JSON.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/documentos/views.py`

### Task 4: UPDATE `backend/apps/demandas/views.py` - Add @extend_schema to custom actions

- **ACTION**: Add `@extend_schema` to by_solicitacao and by_responsavel actions
- **IMPORTS TO ADD**: `from drf_spectacular.utils import extend_schema, OpenApiParameter`
- **SPECIFIC CHANGES**:
  ```python
  @extend_schema(
      summary="Demandas por solicitacao",
      description="Retorna demandas vinculadas a uma solicitacao, ordenadas por prazo.",
      responses={200: DemandaResponseSerializer(many=True)},
  )
  # before by_solicitacao action

  @extend_schema(
      summary="Demandas por responsavel",
      description="Retorna demandas atribuidas a um responsavel, ordenadas por prazo.",
      parameters=[
          OpenApiParameter(
              name="responsavel_id",
              type=int,
              location=OpenApiParameter.QUERY,
              description="ID do usuario responsavel",
              required=True,
          ),
      ],
      responses={200: DemandaResponseSerializer(many=True)},
  )
  # before by_responsavel action
  ```
- **GOTCHA**: The `by_responsavel` action uses `request.query_params.get("responsavel_id")` - this must be documented with `OpenApiParameter` since drf-spectacular cannot auto-detect query params from manual `request.query_params` access.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/demandas/views.py`

### Task 5: UPDATE `backend/apps/usuarios/views.py` - Add @extend_schema to custom actions

- **ACTION**: Add `@extend_schema` to me and toggle_ativo actions
- **IMPORTS TO ADD**: `from drf_spectacular.utils import extend_schema`
- **SPECIFIC CHANGES**:
  ```python
  @extend_schema(
      summary="Dados do usuario autenticado",
      description="Retorna informacoes do usuario atualmente autenticado.",
      responses={200: UsuarioMeSerializer},
  )
  # before me action

  @extend_schema(
      summary="Alternar status ativo do usuario",
      description="Inverte o status ativo/inativo de um usuario. Usuario pk=1 e protegido.",
      request=None,
      responses={200: UsuarioResponseSerializer},
  )
  # before toggle_ativo action
  ```
- **GOTCHA**: `toggle_ativo` accepts PATCH but doesn't need a request body - use `request=None` to suppress the request body in the schema. The `me` action's permission differs from the rest of the ViewSet (IsActiveAuthenticated vs IsAdmin) but drf-spectacular cannot document per-action permissions automatically.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/usuarios/views.py`

### Task 6: UPDATE `backend/apps/pessoas/views.py` and `backend/apps/comentarios/views.py` - Add @extend_schema

- **ACTION**: Add `@extend_schema` to by_cpf (pessoas) and by_demanda (comentarios) actions
- **IMPORTS TO ADD**:
  - `pessoas/views.py`: `from drf_spectacular.utils import extend_schema, OpenApiParameter`
  - `comentarios/views.py`: `from drf_spectacular.utils import extend_schema`
- **SPECIFIC CHANGES for pessoas/views.py**:
  ```python
  @extend_schema(
      summary="Buscar pessoa por CPF",
      description="Retorna uma pessoa pelo numero do CPF.",
      parameters=[
          OpenApiParameter(
              name="cpf",
              type=str,
              location=OpenApiParameter.QUERY,
              description="Numero do CPF",
              required=True,
          ),
      ],
      responses={200: PessoaResponseSerializer},
  )
  # before by_cpf action
  ```
- **SPECIFIC CHANGES for comentarios/views.py** (read the file first to confirm action signature):
  ```python
  @extend_schema(
      summary="Comentarios por demanda",
      description="Retorna todos os comentarios de uma demanda.",
      responses={200: ComentarioResponseSerializer(many=True)},
  )
  # before by_demanda action
  ```
- **GOTCHA**: The `by_cpf` action uses `request.query_params.get("cpf")` which must be documented with `OpenApiParameter`. The `by_demanda` action takes `demanda_id` as a URL path parameter which drf-spectacular should auto-detect from the url_path pattern.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/pessoas/views.py apps/comentarios/views.py`

### Task 7: UPDATE `backend/docker-compose.yml` - Add web service health check

- **ACTION**: Add healthcheck to the web service (lines 54-69)
- **IMPLEMENT**: Add Python urllib-based health check (no curl needed in slim image)
- **SPECIFIC CHANGES** (add after `networks:` in web service, before closing):
  ```yaml
  web:
    build: .
    depends_on:
      db:
        condition: service_healthy
      minio:
        condition: service_healthy
    env_file:
      - .env
    ports:
      - "8000:8000"
    entrypoint: >
      sh -c "python manage.py migrate --noinput &&
             gunicorn config.wsgi:application --config gunicorn.conf.py"
    healthcheck:
      test: ["CMD", "python", "-c", "import urllib.request; urllib.request.urlopen('http://localhost:8000/api/health/')"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    networks:
      - sigesi-network
  ```
- **GOTCHA**: Use `start_period: 40s` to give Django time to run migrations and start gunicorn. Using Python's `urllib.request` avoids needing `curl` in the slim Docker image. The health check endpoint (`/api/health/`) is already implemented in `config/urls.py:13-15` and returns `{"status": "ok"}`.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && docker compose config --quiet` (validates docker-compose syntax)

---

## Testing Strategy

### Validation Approach

Since the PRD explicitly deprioritizes tests, validation focuses on linting, schema generation, and manual verification.

### Edge Cases Checklist

- [ ] Swagger UI loads at `/api/schema/swagger-ui/` without errors
- [ ] All 12 app tags appear in sidebar with descriptions
- [ ] File upload endpoint shows multipart/form-data request body
- [ ] PDF download endpoint shows binary/PDF response type
- [ ] Presigned URL endpoint shows typed response fields
- [ ] Custom query parameters (responsavel_id, cpf) appear in Swagger UI
- [ ] Schema generation succeeds without warnings: `python manage.py spectacular --validate`
- [ ] Docker health check passes after container startup

---

## Validation Commands

### Level 1: STATIC_ANALYSIS

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check .
```

**EXPECT**: Exit 0, no errors

### Level 2: SCHEMA_VALIDATION

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py spectacular --validate --fail-on-warn
```

**EXPECT**: Exit 0, schema validates without warnings

Note: This requires database access (for model introspection). If running locally without Docker, use:
```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && DJANGO_SETTINGS_MODULE=config.settings.local uv run python manage.py spectacular --validate
```

### Level 3: DOCKER_VALIDATION

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && docker compose config --quiet
```

**EXPECT**: Exit 0, valid docker-compose syntax

### Level 4: FULL_BUILD (if Docker is available)

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && docker compose build web
```

**EXPECT**: Image builds successfully with static files collected

### Level 5: MANUAL_VALIDATION

1. Start services: `docker-compose up`
2. Navigate to `http://localhost:8000/api/schema/swagger-ui/`
3. Verify:
   - All 12 tags visible in sidebar with descriptions
   - Click "Upload de arquivo" - verify multipart/form-data request body
   - Click "Download do documento em PDF" - verify binary response
   - Click "Demandas por responsavel" - verify responsavel_id query parameter
   - Click "Buscar pessoa por CPF" - verify cpf query parameter
   - Docker health check: `docker inspect --format='{{.State.Health.Status}}' <container>` shows "healthy"

---

## Acceptance Criteria

- [ ] All @extend_schema decorators added to 10 custom @action methods
- [ ] SPECTACULAR_SETTINGS enhanced with tags, component splitting, UI config
- [ ] Docker web service has healthcheck configuration
- [ ] Level 1 (ruff check) passes with exit 0
- [ ] Level 2 (spectacular --validate) passes without errors
- [ ] Level 3 (docker compose config) validates
- [ ] No regressions - existing functionality unchanged

---

## Completion Checklist

- [ ] Task 1: SPECTACULAR_SETTINGS enhanced
- [ ] Task 2: arquivos/views.py - 3 @extend_schema decorators added
- [ ] Task 3: documentos/views.py - 1 @extend_schema decorator added
- [ ] Task 4: demandas/views.py - 2 @extend_schema decorators added
- [ ] Task 5: usuarios/views.py - 2 @extend_schema decorators added
- [ ] Task 6: pessoas/views.py + comentarios/views.py - 2 @extend_schema decorators added
- [ ] Task 7: docker-compose.yml - web healthcheck added
- [ ] Level 1: ruff check passes
- [ ] Level 2: spectacular --validate passes (if DB available)
- [ ] Level 3: docker compose config validates

---

## Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Schema generation warnings from complex serializers | LOW | LOW | Run `spectacular --validate` after each task; fix warnings immediately |
| File upload schema not rendering correctly in Swagger UI | LOW | MED | `COMPONENT_SPLIT_REQUEST=True` fixes this; use dict-based request schema |
| Docker health check failing during long migrations | LOW | LOW | `start_period: 40s` provides grace period; increase if needed |
| ruff formatting conflicts with @extend_schema decorators | LOW | LOW | Run `ruff format` after adding decorators |

---

## Notes

- **10 custom @action methods** need @extend_schema decorators across 6 ViewSet files
- **Standard CRUD actions** (list, create, retrieve, update, destroy) do NOT need @extend_schema - drf-spectacular auto-detects serializers from `get_serializer_class()`
- **Tag extraction** is automatic via `SCHEMA_PATH_PREFIX = r"/api/"` - URL `/api/solicitacoes/` yields tag `solicitacoes`
- **Error response documentation** is NOT added per-endpoint - the custom exception handler provides consistent format and documenting it on every endpoint would be verbose without proportional value
- Phase 8 is the final migration phase. After this, the Django backend is production-ready.

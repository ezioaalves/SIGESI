# Feature: Base Modules API (Phase 4)

## Summary

Implement DRF ViewSets, serializers, and URL routing for the three foundation modules: Enderecos (addresses), Usuarios (users), and Materiais (materials). This establishes the API patterns that all subsequent phases (5-8) will follow. Each module gets serializers (Create, Update, Response), a ViewSet with appropriate permission classes, and app-level URL routing connected to the root URL configuration via a DRF `DefaultRouter`. A custom DRF exception handler is also created to standardize error responses matching the Spring Boot format `{status, error, message}`.

## User Story

As a backend developer migrating from Spring Boot to Django
I want the Enderecos, Usuarios, and Materiais API endpoints working with DRF
So that the foundation modules are functional and establish patterns for all remaining modules

## Problem Statement

The Django project has models (Phase 2) and authentication (Phase 3) but zero API endpoints. No serializers, no views, no app-level URL routing exists. The Spring Boot app has full CRUD for Enderecos and Materiais, plus user management endpoints (list, get, /me, toggle-ativo, update role) for Usuarios. All of this must be replicated in Django using DRF conventions.

## Solution Statement

Use DRF `ModelViewSet` for standard CRUD on Enderecos and Materiais, and a mixed approach for Usuarios (ViewSet with custom `@action` decorators for `/me` and `/toggle-ativo`). Create separate serializers per module (Create, Update, Response) to control input/output. Use `DefaultRouter` for URL generation. Apply existing permission classes from `apps.core.permissions`. Add a custom DRF exception handler to standardize error responses.

## Metadata

| Field            | Value                                              |
| ---------------- | -------------------------------------------------- |
| Type             | NEW_CAPABILITY                                     |
| Complexity       | MEDIUM                                             |
| Systems Affected | apps/enderecos, apps/usuarios, apps/materiais, apps/core, config/urls, config/settings |
| Dependencies     | djangorestframework 3.15.x, django-filter 24.3+ (already installed) |
| Estimated Tasks  | 9                                                  |

---

## UX Design

### Before State
```
╔═══════════════════════════════════════════════════════════════════════════════╗
║                              BEFORE STATE                                   ║
╠═══════════════════════════════════════════════════════════════════════════════╣
║                                                                             ║
║   ┌──────────────┐         ┌──────────────┐         ┌──────────────┐        ║
║   │ React SPA    │ ──GET──►│ Django API   │ ──404──►│ "Not Found"  │        ║
║   │ /api/enderecos│         │ No views!    │         │ No endpoints │        ║
║   └──────────────┘         └──────────────┘         └──────────────┘        ║
║                                                                             ║
║   USER_FLOW: Any API request to /api/enderecos, /api/usuarios,              ║
║   /api/materiais returns 404 because no views or URL routes exist           ║
║   PAIN_POINT: Models exist but no API layer - frontend cannot interact      ║
║   DATA_FLOW: Request → Django → 404 (no URL match)                         ║
║                                                                             ║
╚═══════════════════════════════════════════════════════════════════════════════╝
```

### After State
```
╔═══════════════════════════════════════════════════════════════════════════════╗
║                               AFTER STATE                                   ║
╠═══════════════════════════════════════════════════════════════════════════════╣
║                                                                             ║
║   ┌──────────────┐  Auth  ┌──────────────┐  CRUD  ┌──────────────┐         ║
║   │ React SPA    │───────►│ Django DRF   │───────►│ PostgreSQL   │         ║
║   │ Frontend     │        │ ViewSets     │        │ Database     │         ║
║   └──────────────┘        └──────┬───────┘        └──────────────┘         ║
║                                  │                                          ║
║           ┌──────────────────────┼──────────────────────┐                   ║
║           ▼                      ▼                      ▼                   ║
║   ┌──────────────┐     ┌──────────────┐      ┌──────────────┐              ║
║   │ /api/enderecos│     │ /api/usuarios│      │ /api/materiais│             ║
║   │ CRUD (all)   │     │ /me, list,   │      │ CRUD (all)   │              ║
║   │ IsAllRoles   │     │ toggle, role │      │ IsAllRoles   │              ║
║   └──────────────┘     │ IsAdmin+     │      └──────────────┘              ║
║                         └──────────────┘                                    ║
║                                                                             ║
║   USER_FLOW: Authenticated requests hit DRF ViewSets → serialization →     ║
║   permission check → database query → serialized response                   ║
║   VALUE_ADD: Full CRUD on base modules, /me endpoint, user management      ║
║   DATA_FLOW: Request → Session Auth → Permission → ViewSet → ORM →         ║
║   Serializer → JSON Response                                                ║
║                                                                             ║
╚═══════════════════════════════════════════════════════════════════════════════╝
```

### Interaction Changes
| Location | Before | After | User Impact |
|----------|--------|-------|-------------|
| `GET /api/enderecos/` | 404 | Paginated list of addresses | Can view all addresses |
| `POST /api/enderecos/` | 404 | Creates address, returns 201 | Can create addresses |
| `PATCH /api/enderecos/{id}/` | 404 | Updates address fields | Can edit addresses |
| `DELETE /api/enderecos/{id}/` | 404 | Deletes address, returns 204 | Can remove addresses |
| `GET /api/usuarios/me/` | 404 | Current user info (id, name, email, role, picture) | Can check own identity |
| `GET /api/usuarios/` | 404 | User list (ADMIN only, excludes id=1) | Admin can manage users |
| `PATCH /api/usuarios/{id}/toggle-ativo/` | 404 | Toggles user active status | Admin can enable/disable users |
| `PATCH /api/usuarios/{id}/` | 404 | Updates user role | Admin can change roles |
| `GET /api/materiais/` | 404 | Paginated list of materials | Can view material catalog |
| `POST /api/materiais/` | 404 | Creates material, returns 201 | Can add materials |

---

## Mandatory Reading

**CRITICAL: Implementation agent MUST read these files before starting any task:**

| Priority | File | Lines | Why Read This |
|----------|------|-------|---------------|
| P0 | `backend/config/settings/base.py` | all | REST_FRAMEWORK config, INSTALLED_APPS to verify |
| P0 | `backend/config/urls.py` | all | Root URL config to UPDATE with router includes |
| P0 | `backend/apps/usuarios/models.py` | all | Usuario model fields and Role enum |
| P0 | `backend/apps/enderecos/models.py` | all | Endereco model fields |
| P0 | `backend/apps/materiais/models.py` | all | Material model fields |
| P0 | `backend/apps/core/permissions.py` | all | Permission classes to USE in ViewSets |
| P1 | `src/main/java/com/sigesi/sigesi/usuarios/UsuarioController.java` | all | Endpoint structure to REPLICATE |
| P1 | `src/main/java/com/sigesi/sigesi/usuarios/UsuarioService.java` | all | Business logic to REPLICATE |
| P1 | `src/main/java/com/sigesi/sigesi/config/GlobalExceptionHandler.java` | all | Error format to REPLICATE |
| P2 | `backend/pyproject.toml` | all | Ruff config for code style |
| P2 | `backend/apps/usuarios/adapters.py` | all | Context on how user fields get populated |

**External Documentation:**
| Source | Section | Why Needed |
|--------|---------|------------|
| [DRF ViewSets](https://www.django-rest-framework.org/api-guide/viewsets/) | ModelViewSet, @action | ViewSet patterns |
| [DRF Serializers](https://www.django-rest-framework.org/api-guide/serializers/) | ModelSerializer | Serializer patterns |
| [DRF Routers](https://www.django-rest-framework.org/api-guide/routers/) | DefaultRouter | URL routing |
| [DRF Exceptions](https://www.django-rest-framework.org/api-guide/exceptions/) | Custom handler | Error standardization |
| [DRF Permissions](https://www.django-rest-framework.org/api-guide/permissions/) | Custom permissions | Per-action permissions |

---

## Patterns to Mirror

**DJANGO MODEL PATTERN:**
```python
# SOURCE: backend/apps/enderecos/models.py:1-23
# All models follow this pattern:
class Endereco(models.Model):
    """Address entity used by solicitacoes, cemiterios, and pessoas."""
    logradouro = models.CharField(max_length=255)
    numero = models.CharField(max_length=20)
    bairro = models.CharField(max_length=255)
    referencia = models.CharField(max_length=255, blank=True, default="")

    class Meta:
        verbose_name = "endereco"
        verbose_name_plural = "enderecos"
```

**DJANGO PERMISSION PATTERN:**
```python
# SOURCE: backend/apps/core/permissions.py:6-13
# Permission classes check user.is_authenticated, user.ativo, and user.role:
class IsActiveAuthenticated(BasePermission):
    message = "Autenticacao necessaria ou usuario inativo."
    def has_permission(self, request, view):
        return request.user and request.user.is_authenticated and request.user.ativo
```

**SPRING BOOT ERROR RESPONSE FORMAT:**
```java
// SOURCE: src/main/java/com/sigesi/sigesi/config/GlobalExceptionHandler.java:19-25
// REPLICATE this exact response format in Django:
return ResponseEntity.status(HttpStatus.NOT_FOUND)
    .body(Map.of(
        "status", HttpStatus.NOT_FOUND.value(),
        "error", "Not Found",
        "message", ex.getMessage()));
```

**SPRING BOOT USUARIO CONTROLLER /me:**
```java
// SOURCE: src/main/java/com/sigesi/sigesi/usuarios/UsuarioController.java:31-39
// Returns these exact fields: id, name, email, role, picture
@GetMapping("/me")
public Object me(Authentication auth) {
    CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal();
    return Map.of(
        "id", user.getUser().getId(),
        "name", user.getUser().getName(),
        "email", user.getUser().getEmail(),
        "role", user.getUser().getRole(),
        "picture", user.getUser().getPictureUrl());
}
```

**SPRING BOOT USUARIO PROTECTION:**
```java
// SOURCE: src/main/java/com/sigesi/sigesi/usuarios/UsuarioService.java:24-30
// User with id=1 cannot be modified. getAll() excludes id=1.
private void validarUsuarioEditavel(Long id) {
    if (id == 1) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "Não é permitido realizar essa ação para este usuário");
    }
}
public List<Usuario> getAll() {
    return usuarioRepository.findByIdNot(1L);
}
```

**DJANGO SETTINGS PATTERN:**
```python
# SOURCE: backend/config/settings/base.py:132-146
# REST_FRAMEWORK already configured with pagination, authentication, permissions:
REST_FRAMEWORK = {
    "DEFAULT_SCHEMA_CLASS": "drf_spectacular.openapi.AutoSchema",
    "DEFAULT_FILTER_BACKENDS": ["django_filters.rest_framework.DjangoFilterBackend"],
    "DEFAULT_PAGINATION_CLASS": "rest_framework.pagination.PageNumberPagination",
    "PAGE_SIZE": 20,
    "DEFAULT_AUTHENTICATION_CLASSES": ["rest_framework.authentication.SessionAuthentication"],
    "DEFAULT_PERMISSION_CLASSES": ["apps.core.permissions.IsActiveAuthenticated"],
}
```

---

## Files to Change

| File | Action | Justification |
|------|--------|---------------|
| `backend/apps/core/exceptions.py` | CREATE | Custom DRF exception handler + exception classes |
| `backend/config/settings/base.py` | UPDATE | Add EXCEPTION_HANDLER to REST_FRAMEWORK config |
| `backend/apps/enderecos/serializers.py` | CREATE | Create, Update, Response serializers for Endereco |
| `backend/apps/enderecos/views.py` | CREATE | ModelViewSet for Endereco CRUD |
| `backend/apps/enderecos/urls.py` | CREATE | Router-based URL routing |
| `backend/apps/materiais/serializers.py` | CREATE | Create, Update, Response serializers for Material |
| `backend/apps/materiais/views.py` | CREATE | ModelViewSet for Material CRUD |
| `backend/apps/materiais/urls.py` | CREATE | Router-based URL routing |
| `backend/apps/usuarios/serializers.py` | CREATE | Me, Response, Update serializers for Usuario |
| `backend/apps/usuarios/views.py` | CREATE | ViewSet with custom actions (/me, /toggle-ativo) |
| `backend/apps/usuarios/urls.py` | CREATE | Router-based URL routing |
| `backend/config/urls.py` | UPDATE | Include app-level URL patterns |

---

## NOT Building (Scope Limits)

- **Audit logging** - Deferred per PRD (can add django-auditlog later)
- **File upload/download** - Phase 7
- **Solicitacoes/Demandas endpoints** - Phase 5
- **Cemetery endpoints** - Phase 6
- **Comentarios endpoints** - Phase 5
- **Documentos/PDF endpoints** - Phase 7
- **Unit tests** - Explicitly deprioritized by developer per PRD
- **Search/ordering filters** - Not in Spring Boot base modules, only simple queryset ordering
- **User creation via API** - Users are only created via OAuth2 login
- **User deletion** - Not in Spring Boot; users can only be toggled active/inactive

---

## Step-by-Step Tasks

Execute in order. Each task is atomic and independently verifiable.

### Task 1: CREATE `backend/apps/core/exceptions.py` - Custom exception handler

- **ACTION**: Create DRF exception handler and custom exception classes matching Spring Boot's error response format
- **IMPLEMENT**:
  - `custom_exception_handler(exc, context)` function that returns `{"status": code, "error": "type", "message": "detail"}` for all errors
  - `NotFoundException` class (maps to 404)
  - `ConflictException` class (maps to 409)
  - Handle DRF's `ValidationError` (extract first message like Spring Boot does)
  - Handle DRF's `NotFound`, `PermissionDenied`, `NotAuthenticated`
  - Handle Django's `Http404`
- **MIRROR**: `src/main/java/com/sigesi/sigesi/config/GlobalExceptionHandler.java:1-94` - same response structure
- **GOTCHA**: Spring Boot's `GlobalExceptionHandler` extracts only the FIRST validation error message from `MethodArgumentNotValidException`. Replicate this behavior by taking the first error from DRF's `ValidationError.detail`.
- **GOTCHA**: DRF `ValidationError.detail` can be a dict (field errors), list, or string. Handle all cases.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/core/exceptions.py && uv run ruff format --check apps/core/exceptions.py`

### Task 2: UPDATE `backend/config/settings/base.py` - Register exception handler

- **ACTION**: Add `EXCEPTION_HANDLER` to the `REST_FRAMEWORK` dict
- **IMPLEMENT**: Add `"EXCEPTION_HANDLER": "apps.core.exceptions.custom_exception_handler"` to the existing `REST_FRAMEWORK` dict in base.py
- **MIRROR**: `backend/config/settings/base.py:132-146` - add to existing dict
- **GOTCHA**: Only add the one key; do not modify other REST_FRAMEWORK settings
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py check`

### Task 3: CREATE `backend/apps/enderecos/serializers.py` - Endereco serializers

- **ACTION**: Create DRF serializers for Endereco module
- **IMPLEMENT**:
  - `EnderecoCreateSerializer(ModelSerializer)`:
    - Fields: `logradouro` (required), `numero` (required), `bairro` (required), `referencia` (optional, allow_blank=True)
    - Validation messages in Portuguese matching Spring Boot
  - `EnderecoUpdateSerializer(ModelSerializer)`:
    - All fields optional for PATCH support (all fields have `required=False`)
    - Note: Spring Boot's EnderecoUpdateDTO makes ALL fields @NotBlank, but since we're using Python conventions per PRD, we allow true partial updates
  - `EnderecoResponseSerializer(ModelSerializer)`:
    - Fields: `id`, `logradouro`, `numero`, `bairro`, `referencia`
    - All read-only
- **MIRROR**: Spring Boot `enderecos/dtos/EnderecoCreateDTO.java`, `EnderecoUpdateDTO.java`, `EnderecoResponseDTO.java`
- **GOTCHA**: `referencia` is optional (blank=True, default="") in the model. The CreateSerializer should allow blank for this field.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/enderecos/serializers.py && uv run ruff format --check apps/enderecos/serializers.py`

### Task 4: CREATE `backend/apps/enderecos/views.py` - Endereco ViewSet

- **ACTION**: Create ModelViewSet for Endereco CRUD
- **IMPLEMENT**:
  - `EnderecoViewSet(ModelViewSet)`:
    - `queryset = Endereco.objects.all().order_by("id")` (matches Spring Boot's `findAllByOrderByIdAsc`)
    - `permission_classes = [IsAllRoles]` (CIDADAO, AGENTE, OPERADOR, ADMIN)
    - Override `get_serializer_class()` to return the correct serializer per action:
      - `create` → `EnderecoCreateSerializer`
      - `update`, `partial_update` → `EnderecoUpdateSerializer`
      - Default → `EnderecoResponseSerializer`
- **MIRROR**: `src/main/java/com/sigesi/sigesi/enderecos/EnderecoController.java:1-50` - same endpoints
- **GOTCHA**: `ModelViewSet` provides list, create, retrieve, update, partial_update, destroy by default. This matches the Spring Boot controller exactly (GET /, GET /{id}, POST /, PATCH /{id}, DELETE /{id}).
- **GOTCHA**: Spring Boot returns 204 on delete. DRF's `DestroyModelMixin` already returns 204 by default.
- **GOTCHA**: Spring Boot returns 201 on create. DRF's `CreateModelMixin` already returns 201 by default.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/enderecos/views.py && uv run ruff format --check apps/enderecos/views.py`

### Task 5: CREATE `backend/apps/enderecos/urls.py` - Endereco URL routing

- **ACTION**: Create app-level URL routing using DRF router
- **IMPLEMENT**:
  - Use `DefaultRouter()` to register `EnderecoViewSet` at `""` (empty prefix since `/api/enderecos/` prefix is added in root urls.py)
  - Export `urlpatterns = router.urls`
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/enderecos/urls.py`

### Task 6: CREATE `backend/apps/materiais/serializers.py` - Material serializers

- **ACTION**: Create DRF serializers for Material module
- **IMPLEMENT**:
  - `MaterialCreateSerializer(ModelSerializer)`:
    - Fields: `nome` (required), `preco` (required)
    - Validation messages in Portuguese
  - `MaterialUpdateSerializer(ModelSerializer)`:
    - Fields: `nome` (optional), `preco` (optional)
    - Both `required=False` for true PATCH semantics (matches Spring Boot's `MaterialUpdateDTO` where both fields are optional)
  - `MaterialResponseSerializer(ModelSerializer)`:
    - Fields: `id`, `nome`, `preco`
- **MIRROR**: Spring Boot `materiais/dtos/MaterialCreateDTO.java`, `MaterialUpdateDTO.java`, `MaterialResponseDTO.java`
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/materiais/serializers.py && uv run ruff format --check apps/materiais/serializers.py`

### Task 7: CREATE `backend/apps/materiais/views.py` and `backend/apps/materiais/urls.py` - Material ViewSet + URLs

- **ACTION**: Create ModelViewSet and URL routing for Material CRUD
- **IMPLEMENT**:
  - `MaterialViewSet(ModelViewSet)`:
    - `queryset = Material.objects.all().order_by("id")` (matches Spring Boot's `findAllByOrderByIdAsc`)
    - `permission_classes = [IsAllRoles]`
    - Override `get_serializer_class()` per action (same pattern as Endereco)
  - `urls.py`: Same router pattern as Enderecos
- **MIRROR**: `src/main/java/com/sigesi/sigesi/materiais/MaterialController.java` - same endpoints
- **GOTCHA**: Material model has `ordering = ["nome"]` in Meta. Override with `order_by("id")` in queryset to match Spring Boot.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/materiais/views.py apps/materiais/urls.py && uv run ruff format --check apps/materiais/views.py apps/materiais/urls.py`

### Task 8: CREATE `backend/apps/usuarios/serializers.py` and `backend/apps/usuarios/views.py` and `backend/apps/usuarios/urls.py` - Usuario module

- **ACTION**: Create serializers, ViewSet with custom actions, and URL routing for Usuarios
- **IMPLEMENT**:

  **Serializers (`serializers.py`):**
  - `UsuarioMeSerializer(ModelSerializer)`:
    - Fields: `id`, `name`, `email`, `role`, `picture`
    - `name` is a `SerializerMethodField` returning `get_full_name()` or `first_name + last_name`
    - `picture` is a `CharField(source="picture_url")` to match Spring Boot's `/me` response field name
    - Note: Spring Boot's `/me` returns `name` (single field) but Django's `AbstractUser` uses `first_name`/`last_name`. The adapter sets these from Google's `name` field. We need to concatenate them back.
  - `UsuarioResponseSerializer(ModelSerializer)`:
    - Fields: `id`, `email`, `first_name`, `last_name`, `picture_url`, `provider`, `ativo`, `role`
    - Used for list/retrieve (ADMIN endpoints)
  - `UsuarioUpdateSerializer(Serializer)`:
    - Field: `role` (required, ChoiceField with Usuario.Role.choices)
    - Validation message: "A role de usuario e obrigatoria."
    - Only allows updating the `role` field (matches Spring Boot's UsuarioUpdateDTO)

  **ViewSet (`views.py`):**
  - `UsuarioViewSet(ModelViewSet)`:
    - `queryset = Usuario.objects.exclude(pk=1).order_by("id")` (excludes admin seed user, matches Spring Boot's `findByIdNot(1L)`)
    - Override `get_permissions()`:
      - `me` action → `[IsActiveAuthenticated()]`
      - All other actions → `[IsAdmin()]`
    - Override `get_serializer_class()`:
      - `me` → `UsuarioMeSerializer`
      - `partial_update` → `UsuarioUpdateSerializer`
      - `toggle_ativo` → `UsuarioResponseSerializer` (for response only)
      - Default → `UsuarioResponseSerializer`
    - Disable `create` and `destroy` (users only created via OAuth2, never deleted):
      - Set `http_method_names = ["get", "patch", "head", "options"]`
    - Custom actions:
      - `@action(detail=False, methods=["get"])` → `me(request)`: Return current user with `UsuarioMeSerializer`
      - `@action(detail=True, methods=["patch"], url_path="toggle-ativo")` → `toggle_ativo(request, pk)`:
        - Validate `pk != 1` (raise PermissionDenied if so)
        - Toggle `ativo` field
        - Return updated user
    - Override `partial_update()`:
      - Validate `pk != 1` (raise PermissionDenied if so)
      - Only update `role` field from request body
      - Return updated user with `UsuarioResponseSerializer`
    - Override `get_object()` for `retrieve` to allow ADMIN to view any user including pk=1 (only mutations are blocked)

  **URLs (`urls.py`):**
  - Same router pattern as other modules

- **MIRROR**:
  - `src/main/java/com/sigesi/sigesi/usuarios/UsuarioController.java:1-67` - endpoint structure
  - `src/main/java/com/sigesi/sigesi/usuarios/UsuarioService.java:24-57` - business logic
- **GOTCHA**: Spring Boot's `getAll()` uses `findByIdNot(1L)` to exclude user id=1. In Django, use `queryset = Usuario.objects.exclude(pk=1)`. But `retrieve` should still work for pk=1 (admin can view any user).
- **GOTCHA**: Spring Boot's `/me` endpoint returns a flat Map with `picture` (not `pictureUrl`). Match this exact field name.
- **GOTCHA**: Spring Boot's `validarUsuarioEditavel` throws 403 for id=1. In Django, raise `rest_framework.exceptions.PermissionDenied` with message "Nao e permitido realizar essa acao para este usuario".
- **GOTCHA**: Django's `AbstractUser` stores name as `first_name` + `last_name`, not a single `name` field. The adapter splits Google's `name` into these fields. For `/me`, concatenate them back: `f"{user.first_name} {user.last_name}".strip()`.
- **GOTCHA**: The Spring Boot update endpoint is `PATCH /{id}/role` (separate path for role update). In Django, we simplify to `PATCH /{id}/` since the `UsuarioUpdateDTO` only allows `role` anyway. This is functionally equivalent.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/usuarios/serializers.py apps/usuarios/views.py apps/usuarios/urls.py && uv run ruff format --check apps/usuarios/serializers.py apps/usuarios/views.py apps/usuarios/urls.py`

### Task 9: UPDATE `backend/config/urls.py` - Connect app URLs to root

- **ACTION**: Add `include()` calls for all three app URL modules
- **IMPLEMENT**: Add these paths to the `urlpatterns` list in `config/urls.py`:
  ```python
  path("api/enderecos/", include("apps.enderecos.urls")),
  path("api/materiais/", include("apps.materiais.urls")),
  path("api/usuarios/", include("apps.usuarios.urls")),
  ```
- **MIRROR**: `backend/config/urls.py:18-28` - add after existing patterns, before allauth patterns
- **GOTCHA**: The prefix `api/enderecos/` is set here, so the app-level router registers at `""` (empty prefix). This gives us URLs like `/api/enderecos/`, `/api/enderecos/{id}/`.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py check && uv run python -c "from django.urls import reverse; print(reverse('endereco-list')); print(reverse('material-list')); print(reverse('usuario-list'))"`

---

## Testing Strategy

### Verification Tests (No unit tests per PRD)

| Check | What It Validates | Command |
|-------|-------------------|---------|
| Django check | All settings and imports valid | `uv run python manage.py check` |
| Ruff lint | Code style for all new files | `uv run ruff check apps/enderecos/ apps/materiais/ apps/usuarios/ apps/core/` |
| Ruff format | Code formatting | `uv run ruff format --check apps/enderecos/ apps/materiais/ apps/usuarios/ apps/core/` |
| Import serializers | Serializer classes loadable | `uv run python -c "from apps.enderecos.serializers import EnderecoCreateSerializer, EnderecoUpdateSerializer, EnderecoResponseSerializer; print('Endereco serializers OK')"` |
| Import views | ViewSet classes loadable | `uv run python -c "from apps.enderecos.views import EnderecoViewSet; from apps.materiais.views import MaterialViewSet; from apps.usuarios.views import UsuarioViewSet; print('ViewSets OK')"` |
| URL resolution | URLs registered correctly | `uv run python -c "from django.urls import reverse; print(reverse('endereco-list')); print(reverse('material-list')); print(reverse('usuario-list')); print(reverse('usuario-me'))"` |
| Exception handler | Custom handler importable | `uv run python -c "from apps.core.exceptions import custom_exception_handler, NotFoundException; print('Exceptions OK')"` |

### Edge Cases Checklist

- [ ] PATCH with empty body should not crash (partial=True handles this)
- [ ] DELETE on non-existent ID returns 404 with standardized error format
- [ ] GET /api/usuarios/me/ with unauthenticated user returns 403
- [ ] PATCH /api/usuarios/1/toggle-ativo/ returns 403 (protected user)
- [ ] PATCH /api/usuarios/1/ (update role) returns 403 (protected user)
- [ ] GET /api/usuarios/ excludes user with pk=1
- [ ] GET /api/usuarios/1/ works (retrieve is allowed, only mutations blocked)
- [ ] POST /api/enderecos/ with missing required fields returns 400 with Portuguese message
- [ ] POST /api/materiais/ with missing `preco` returns 400
- [ ] Invalid role value in PATCH /api/usuarios/{id}/ returns 400

---

## Validation Commands

### Level 1: STATIC_ANALYSIS

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/core/ apps/enderecos/ apps/materiais/ apps/usuarios/ config/ && uv run ruff format --check apps/core/ apps/enderecos/ apps/materiais/ apps/usuarios/ config/
```

**EXPECT**: Exit 0, no errors

### Level 2: DJANGO_CHECK

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py check
```

**EXPECT**: System check identified no issues

### Level 3: COMPONENT_VERIFICATION

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python -c "
from apps.core.exceptions import custom_exception_handler, NotFoundException, ConflictException
from apps.enderecos.serializers import EnderecoCreateSerializer, EnderecoUpdateSerializer, EnderecoResponseSerializer
from apps.enderecos.views import EnderecoViewSet
from apps.materiais.serializers import MaterialCreateSerializer, MaterialUpdateSerializer, MaterialResponseSerializer
from apps.materiais.views import MaterialViewSet
from apps.usuarios.serializers import UsuarioMeSerializer, UsuarioResponseSerializer, UsuarioUpdateSerializer
from apps.usuarios.views import UsuarioViewSet
from django.urls import reverse
print('Enderecos:', reverse('endereco-list'))
print('Materiais:', reverse('material-list'))
print('Usuarios:', reverse('usuario-list'))
print('Usuario Me:', reverse('usuario-me'))
print('ALL CHECKS PASSED')
"
```

**EXPECT**: All imports succeed, all URLs resolve, prints "ALL CHECKS PASSED"

### Level 4: FULL_VALIDATION

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/ config/ && uv run ruff format --check apps/ config/ && uv run python manage.py check
```

**EXPECT**: All pass with exit 0

---

## Acceptance Criteria

- [ ] `GET /api/enderecos/` returns paginated list of addresses (ordered by id)
- [ ] `GET /api/enderecos/{id}/` returns single address
- [ ] `POST /api/enderecos/` creates address (201) with validation
- [ ] `PATCH /api/enderecos/{id}/` updates address fields (partial update)
- [ ] `DELETE /api/enderecos/{id}/` deletes address (204)
- [ ] `GET /api/materiais/` returns paginated list of materials (ordered by id)
- [ ] `GET /api/materiais/{id}/` returns single material
- [ ] `POST /api/materiais/` creates material (201) with validation
- [ ] `PATCH /api/materiais/{id}/` updates material fields (partial update)
- [ ] `DELETE /api/materiais/{id}/` deletes material (204)
- [ ] `GET /api/usuarios/me/` returns current user (id, name, email, role, picture)
- [ ] `GET /api/usuarios/` returns user list excluding pk=1 (ADMIN only)
- [ ] `GET /api/usuarios/{id}/` returns user detail (ADMIN only)
- [ ] `PATCH /api/usuarios/{id}/` updates user role (ADMIN only)
- [ ] `PATCH /api/usuarios/{id}/toggle-ativo/` toggles user active status (ADMIN only)
- [ ] User pk=1 is protected from toggle-ativo and role update (403)
- [ ] Error responses follow format: `{"status": code, "error": "type", "message": "detail"}`
- [ ] Validation errors return first error message (matching Spring Boot behavior)
- [ ] `ruff check` and `ruff format --check` pass on all files
- [ ] `python manage.py check` passes
- [ ] All endpoints appear in Swagger UI at `/api/schema/swagger-ui/`
- [ ] Enderecos and Materiais accessible to all roles (CIDADAO, AGENTE, OPERADOR, ADMIN)
- [ ] Usuarios endpoints (except /me) restricted to ADMIN only

---

## Completion Checklist

- [ ] Task 1: Custom exception handler and exception classes created
- [ ] Task 2: Exception handler registered in settings
- [ ] Task 3: Endereco serializers created (Create, Update, Response)
- [ ] Task 4: Endereco ViewSet created with permissions
- [ ] Task 5: Endereco URL routing created
- [ ] Task 6: Material serializers created (Create, Update, Response)
- [ ] Task 7: Material ViewSet and URL routing created
- [ ] Task 8: Usuario serializers, ViewSet (with /me, /toggle-ativo), and URLs created
- [ ] Task 9: Root urls.py updated to include all app URLs
- [ ] Level 1: Static analysis passes
- [ ] Level 2: Django check passes
- [ ] Level 3: Component verification passes
- [ ] Level 4: Full validation passes
- [ ] All acceptance criteria met

---

## Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Django AbstractUser name split (first_name/last_name vs single name) | LOW | MEDIUM | UsuarioMeSerializer uses get_full_name() or manual concatenation to return single `name` field |
| DRF pagination wraps list responses differently than Spring Boot | LOW | LOW | Spring Boot returned bare arrays; DRF returns `{count, next, previous, results}`. Frontend may need to adapt. This is acceptable per PRD. |
| PATCH /usuarios/{id}/ URL differs from Spring Boot's /usuarios/{id}/role | LOW | LOW | Functionally equivalent since only `role` field is accepted. Document in API docs. |
| DRF ValidationError format differs from Spring Boot | MEDIUM | LOW | Custom exception handler normalizes to same `{status, error, message}` format |
| Queryset `exclude(pk=1)` for usuario list affects pagination counts | LOW | LOW | Acceptable - matches Spring Boot's `findByIdNot(1L)` behavior |
| DefaultRouter generates API root view at /api/ which may conflict | LOW | LOW | Not an issue since each app router is included under its own prefix |

---

## Notes

**Pagination Difference from Spring Boot:**
Spring Boot returns bare JSON arrays for list endpoints. DRF's `PageNumberPagination` wraps responses in `{"count": N, "next": "url", "previous": "url", "results": [...]}`. This is a known and accepted difference per PRD ("responses will follow Python/DRF conventions").

**Pattern Established by This Phase:**
All subsequent phases (5-8) should follow the exact same pattern:
1. `serializers.py` with Create, Update, Response serializers
2. `views.py` with ModelViewSet and `get_serializer_class()` override
3. `urls.py` with DefaultRouter registration
4. Root `urls.py` include with `api/{module}/` prefix
5. Permission classes from `apps.core.permissions`

**Django REST Framework URL Convention:**
DRF's DefaultRouter generates URLs with trailing slashes by default. This matches the Spring Boot convention where all endpoints end with `/`. The generated URL patterns are:
- `GET /api/enderecos/` (list)
- `POST /api/enderecos/` (create)
- `GET /api/enderecos/{id}/` (retrieve)
- `PATCH /api/enderecos/{id}/` (partial_update)
- `PUT /api/enderecos/{id}/` (update)
- `DELETE /api/enderecos/{id}/` (destroy)

**Usuario /me Endpoint:**
The Spring Boot `/me` endpoint is a simple GET that returns a map with `id`, `name`, `email`, `role`, `picture`. In DRF, this is implemented as a `@action(detail=False)` on the ViewSet, which generates the URL `/api/usuarios/me/`. The serializer maps `picture_url` to `picture` for backward compatibility.

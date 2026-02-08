# Feature: Solicitacoes & Demandas API (Phase 5)

## Summary

Implement DRF ViewSets, serializers, and URL routing for the core infrastructure request workflow: Solicitacoes (service requests with role-based filtering), Demandas (work demands with material tracking), and Comentarios (comments on demands). Solicitacoes includes role-based list filtering (CIDADAO/AGENTE see only their own, ADMIN/OPERADOR see all). Demandas includes nested DemandaMaterial management (create/update materials inline, no separate REST endpoint) and custom query endpoints (by solicitacao, by responsavel). Comentarios are immutable after creation (no update endpoint) and scoped to demandas. All three modules follow the established Phase 4 patterns.

## User Story

As a backend developer migrating from Spring Boot to Django
I want the Solicitacoes, Demandas, and Comentarios API endpoints working with DRF
So that the core infrastructure request workflow is functional and matches the Spring Boot feature set

## Problem Statement

The Django project has models (Phase 2), authentication (Phase 3), and base module APIs (Phase 4) but the core business domain - service requests, work demands, and comments - has zero API endpoints. The Spring Boot app has full CRUD for Solicitacoes (with role-based filtering), Demandas (with inline material management and custom query endpoints), and Comentarios (with per-demand listing). All of this must be replicated in Django using the DRF patterns established in Phase 4.

## Solution Statement

Use DRF `ModelViewSet` for all three modules with custom behavior: Solicitacoes overrides `get_queryset()` for role-based filtering; Demandas uses writable nested serializers for DemandaMaterial and `@action` decorators for custom query endpoints; Comentarios restricts HTTP methods to exclude update. All serializers use nested representations for response DTOs (full Usuario, Endereco, Material objects, not just IDs). DemandaMaterial is managed entirely through Demanda serializers - no standalone endpoint.

## Metadata

| Field            | Value                                              |
| ---------------- | -------------------------------------------------- |
| Type             | NEW_CAPABILITY                                     |
| Complexity       | HIGH                                               |
| Systems Affected | apps/solicitacoes, apps/demandas, apps/comentarios, config/urls |
| Dependencies     | djangorestframework 3.15.x (already installed)     |
| Estimated Tasks  | 10                                                 |

---

## UX Design

### Before State
```
+===============================================================================+
|                              BEFORE STATE                                     |
+===============================================================================+
|                                                                               |
|   +---------------+         +---------------+         +---------------+       |
|   | React SPA     | --GET-->| Django API    | --404-->| "Not Found"   |       |
|   | /api/         |         | No views for  |         | No endpoints  |       |
|   | solicitacoes  |         | core domain!  |         | for workflow  |       |
|   +---------------+         +---------------+         +---------------+       |
|                                                                               |
|   USER_FLOW: Any API request to /api/solicitacoes/, /api/demandas/,           |
|   /api/comentarios/ returns 404 because no views or URL routes exist          |
|   PAIN_POINT: Base modules work (enderecos, materiais, usuarios) but          |
|   the core business workflow has no API - citizens can't file requests,       |
|   agents can't manage demands, nobody can comment                             |
|   DATA_FLOW: Request -> Django -> 404 (no URL match)                          |
|                                                                               |
+===============================================================================+
```

### After State
```
+===============================================================================+
|                               AFTER STATE                                     |
+===============================================================================+
|                                                                               |
|   +---------------+  Auth  +---------------+  CRUD  +---------------+         |
|   | React SPA     |------->| Django DRF    |------->| PostgreSQL    |         |
|   | Frontend      |        | ViewSets      |        | Database      |         |
|   +---------------+        +-------+-------+        +---------------+         |
|                                     |                                         |
|          +--------------------------+---------------------------+             |
|          v                          v                           v             |
|   +---------------+      +-------------------+       +---------------+        |
|   | /api/         |      | /api/demandas/    |       | /api/         |        |
|   | solicitacoes/ |      | CRUD + materials  |       | comentarios/  |        |
|   | CRUD + role   |      | + by solicitacao  |       | create, list, |        |
|   | filtering     |      | + by responsavel  |       | by demanda,   |        |
|   | IsAllRoles    |      | IsAllRoles        |       | delete (no    |        |
|   +---------------+      +-------------------+       | update)       |        |
|          |                          |                 | IsAllRoles    |        |
|          v                          v                 +---------------+        |
|   CIDADAO: own only        Materials managed                                  |
|   ADMIN: see all           inline via Demanda                                 |
|                                                                               |
|   USER_FLOW: Citizen creates solicitacao -> Admin creates demanda with        |
|   materials -> Agent comments on demanda -> Admin updates status              |
|   VALUE_ADD: Full request-to-demand workflow operational                       |
|   DATA_FLOW: Request -> Session Auth -> Permission -> ViewSet ->              |
|   Role filter -> ORM -> Nested Serializer -> JSON Response                    |
|                                                                               |
+===============================================================================+
```

### Interaction Changes
| Location | Before | After | User Impact |
|----------|--------|-------|-------------|
| `GET /api/solicitacoes/` | 404 | Role-filtered list (CIDADAO sees own, ADMIN sees all) | Citizens can view their requests |
| `POST /api/solicitacoes/` | 404 | Creates request with autor, local, anexos | Citizens can file service requests |
| `PATCH /api/solicitacoes/{id}/` | 404 | Updates status only | Admin can change request status |
| `DELETE /api/solicitacoes/{id}/` | 404 | Deletes request (204) | Admin can remove requests |
| `GET /api/demandas/` | 404 | List all demands with nested materials | Can view all work demands |
| `POST /api/demandas/` | 404 | Creates demand with inline materials | Can create demands from requests |
| `PATCH /api/demandas/{id}/` | 404 | Updates demand (status, prazo, responsavel, materials) | Can manage demand lifecycle |
| `GET /api/demandas/solicitacao/{id}/` | 404 | Demands for a specific request | Can see demands per request |
| `GET /api/demandas/responsavel/?responsavel_id={id}` | 404 | Demands assigned to a user | Can see agent workload |
| `GET /api/comentarios/` | 404 | List all comments | Can view all comments |
| `POST /api/comentarios/` | 404 | Creates comment on demand | Can discuss demands |
| `GET /api/comentarios/demanda/{id}/` | 404 | Comments for a specific demand | Can view demand discussion |
| `DELETE /api/comentarios/{id}/` | 404 | Deletes comment (204) | Can remove comments |

---

## Mandatory Reading

**CRITICAL: Implementation agent MUST read these files before starting any task:**

| Priority | File | Lines | Why Read This |
|----------|------|-------|---------------|
| P0 | `backend/apps/enderecos/serializers.py` | all | Serializer pattern to MIRROR exactly |
| P0 | `backend/apps/enderecos/views.py` | all | ViewSet pattern to MIRROR exactly |
| P0 | `backend/apps/enderecos/urls.py` | all | URL routing pattern to MIRROR exactly |
| P0 | `backend/apps/usuarios/views.py` | all | Custom @action pattern to MIRROR for Demandas/Comentarios |
| P0 | `backend/apps/usuarios/serializers.py` | all | Nested serializer patterns (SerializerMethodField, source=) |
| P0 | `backend/apps/solicitacoes/models.py` | all | Solicitacao model, enums, relationships |
| P0 | `backend/apps/demandas/models.py` | all | Demanda + DemandaMaterial models, relationships |
| P0 | `backend/apps/comentarios/models.py` | all | Comentario model, relationships |
| P0 | `backend/apps/core/permissions.py` | all | Permission classes to USE |
| P0 | `backend/apps/core/exceptions.py` | all | NotFoundException to USE |
| P1 | `backend/apps/materiais/serializers.py` | all | MaterialResponseSerializer to IMPORT for nesting |
| P1 | `backend/apps/arquivos/models.py` | all | Arquivo model for ManyToMany in Solicitacao |
| P1 | `backend/apps/usuarios/models.py` | all | Usuario model with Role enum |
| P1 | `backend/config/urls.py` | all | Root URL config to UPDATE |

**External Documentation:**
| Source | Section | Why Needed |
|--------|---------|------------|
| [DRF Writable Nested Serializers](https://www.django-rest-framework.org/api-guide/relations/#writable-nested-serializers) | WritableNestedSerializer | DemandaMaterial inline create/update |
| [DRF ViewSets - Custom Actions](https://www.django-rest-framework.org/api-guide/viewsets/#marking-extra-actions-for-routing) | @action decorator | Custom endpoints (by_solicitacao, by_responsavel, by_demanda) |
| [DRF Serializers - Nested](https://www.django-rest-framework.org/api-guide/serializers/#dealing-with-nested-objects) | Nested representations | Full object responses (Usuario, Endereco in responses) |

---

## Patterns to Mirror

**VIEWSET PATTERN (standard CRUD):**
```python
# SOURCE: backend/apps/enderecos/views.py:14-26
# COPY THIS PATTERN for Solicitacoes and Comentarios:
class EnderecoViewSet(ModelViewSet):
    """ViewSet for Endereco CRUD operations."""

    queryset = Endereco.objects.all().order_by("id")
    permission_classes = [IsAllRoles]

    def get_serializer_class(self):
        """Return the appropriate serializer per action."""
        if self.action == "create":
            return EnderecoCreateSerializer
        if self.action in ("update", "partial_update"):
            return EnderecoUpdateSerializer
        return EnderecoResponseSerializer
```

**CUSTOM ACTION PATTERN:**
```python
# SOURCE: backend/apps/usuarios/views.py:63-76
# COPY THIS PATTERN for Demanda custom endpoints:
@action(detail=False, methods=["get"])
def me(self, request):
    """Return current authenticated user info."""
    serializer = UsuarioMeSerializer(request.user)
    return Response(serializer.data)

@action(detail=True, methods=["patch"], url_path="toggle-ativo")
def toggle_ativo(self, request, pk=None):
    """Toggle user active status."""
    # ...
```

**SERIALIZER PATTERN (ModelSerializer):**
```python
# SOURCE: backend/apps/enderecos/serializers.py:8-21
# COPY THIS PATTERN for create serializers:
class EnderecoCreateSerializer(serializers.ModelSerializer):
    """Serializer for creating an Endereco."""

    class Meta:
        """Meta options."""
        model = Endereco
        fields = ["logradouro", "numero", "bairro", "referencia"]
        extra_kwargs = {
            "logradouro": {"error_messages": {"blank": "O logradouro e obrigatorio."}},
        }
```

**RESPONSE SERIALIZER PATTERN:**
```python
# SOURCE: backend/apps/enderecos/serializers.py:40-48
# COPY THIS PATTERN for response serializers:
class EnderecoResponseSerializer(serializers.ModelSerializer):
    """Serializer for Endereco responses."""

    class Meta:
        """Meta options."""
        model = Endereco
        fields = ["id", "logradouro", "numero", "bairro", "referencia"]
        read_only_fields = fields
```

**URL ROUTING PATTERN:**
```python
# SOURCE: backend/apps/enderecos/urls.py:1-10
# COPY THIS PATTERN for all modules:
from rest_framework.routers import DefaultRouter
from apps.enderecos.views import EnderecoViewSet

router = DefaultRouter()
router.register("", EnderecoViewSet, basename="endereco")

urlpatterns = router.urls
```

---

## Files to Change

| File | Action | Justification |
|------|--------|---------------|
| `backend/apps/solicitacoes/serializers.py` | CREATE | Create, Update, Response serializers for Solicitacao |
| `backend/apps/solicitacoes/views.py` | CREATE | ViewSet with role-based queryset filtering |
| `backend/apps/solicitacoes/urls.py` | CREATE | Router-based URL routing |
| `backend/apps/demandas/serializers.py` | CREATE | Create, Update, Response serializers + DemandaMaterial nested serializers |
| `backend/apps/demandas/views.py` | CREATE | ViewSet with @action for by_solicitacao and by_responsavel |
| `backend/apps/demandas/urls.py` | CREATE | Router-based URL routing |
| `backend/apps/comentarios/serializers.py` | CREATE | Create and Response serializers (no Update) |
| `backend/apps/comentarios/views.py` | CREATE | ViewSet with @action for by_demanda, restricted HTTP methods |
| `backend/apps/comentarios/urls.py` | CREATE | Router-based URL routing |
| `backend/config/urls.py` | UPDATE | Include app-level URL patterns for 3 modules |

---

## NOT Building (Scope Limits)

- **RabbitMQ notifications** - Deferred per PRD (Spring Boot publishes DemandAssigned/DemandStatusChanged events; we skip this entirely)
- **Audit logging** - Deferred per PRD (Spring Boot uses @Audited; we skip this entirely)
- **Unit tests** - Explicitly deprioritized by developer per PRD
- **DemandaMaterial standalone CRUD endpoints** - Not in Spring Boot either; materials are managed inline through Demanda create/update
- **File upload/download** - Phase 7 (Arquivo model exists but ArquivoController/MinIO service is Phase 7)
- **Solicitacao file attachments upload** - Phase 7 (the `anexo_ids` field accepts existing Arquivo IDs, but upload is Phase 7)
- **Documentos endpoints** - Phase 7
- **Cemetery endpoints** - Phase 6
- **django-filter FilterSets** - Not needed for Phase 5; custom query endpoints handle the specific filtering needs

---

## Step-by-Step Tasks

Execute in order. Each task is atomic and independently verifiable.

### Task 1: CREATE `backend/apps/solicitacoes/serializers.py` - Solicitacao serializers

- **ACTION**: Create DRF serializers for Solicitacao module with nested response representations
- **IMPLEMENT**:
  - `ArquivoSimpleResponseSerializer(ModelSerializer)`:
    - Fields: `id`, `nome_original`, `storage_key`, `content_type`, `tamanho`, `categoria`, `uploaded_at`, `ativo`
    - All read-only. This is a simple nested serializer for Solicitacao's anexos list.
    - Note: A full ArquivoResponseSerializer will be created in Phase 7; this is a temporary nested representation.
  - `AutorResponseSerializer(ModelSerializer)`:
    - Fields: `id`, `email`, `first_name`, `last_name`, `picture_url`, `role`
    - All read-only. Used to nest full Usuario info in Solicitacao/Demanda/Comentario responses.
    - Note: This is different from `UsuarioMeSerializer` (which has `name` and `picture` field mappings). This returns the raw model fields.
  - `SolicitacaoCreateSerializer(Serializer)`:
    - Fields: `assunto` (ChoiceField, required), `body` (CharField, required), `autor_id` (IntegerField, required), `local_id` (IntegerField, required), `anexo_ids` (ListField of IntegerField, required=False, default=[])
    - Validation: Verify autor_id references existing Usuario, local_id references existing Endereco, all anexo_ids reference existing Arquivo records
    - `create()` method: Build Solicitacao with status=ABERTA (default in model), set autor, local, and add anexos via `.set()`
    - Error messages in Portuguese matching Spring Boot: "Assunto e obrigatorio", "Corpo e obrigatorio", "Autor e obrigatorio", "Local e obrigatorio"
  - `SolicitacaoUpdateSerializer(Serializer)`:
    - Field: `status` (ChoiceField with SolicitacaoStatus.choices, required)
    - This is the ONLY field that can be updated (matches Spring Boot behavior)
    - Error message: "Status nao pode ser vazio"
  - `SolicitacaoResponseSerializer(ModelSerializer)`:
    - Fields: `id`, `data`, `assunto`, `status`, `body`, `anexos` (nested ArquivoSimpleResponseSerializer, many=True), `autor` (nested AutorResponseSerializer), `local` (nested EnderecoResponseSerializer)
    - Import `EnderecoResponseSerializer` from `apps.enderecos.serializers`
    - All read-only
- **MIRROR**: `backend/apps/enderecos/serializers.py:8-48` - same Create/Update/Response pattern
- **GOTCHA**: `SolicitacaoCreateSerializer` uses plain `Serializer` (not `ModelSerializer`) because it accepts `autor_id`, `local_id`, `anexo_ids` as IDs and manually creates the object. This is similar to how Spring Boot's `SolicitacaoService.createSolicitacao()` resolves IDs to entities.
- **GOTCHA**: Spring Boot's response returns full `Usuario autor` and `Endereco local` objects, not just IDs. Use nested serializers.
- **GOTCHA**: `SolicitacaoUpdateSerializer` uses `required=True` for status because Spring Boot's DTO has `@NotNull` on status. Only status can be updated.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/solicitacoes/serializers.py && uv run ruff format --check apps/solicitacoes/serializers.py`

### Task 2: CREATE `backend/apps/solicitacoes/views.py` - Solicitacao ViewSet with role-based filtering

- **ACTION**: Create ModelViewSet with `get_queryset()` override for role-based filtering
- **IMPLEMENT**:
  - `SolicitacaoViewSet(ModelViewSet)`:
    - `permission_classes = [IsAllRoles]`
    - Override `get_queryset()`:
      - If `request.user.role` in (`ADMIN`, `OPERADOR`): return `Solicitacao.objects.all().order_by("id")`
      - Else (CIDADAO, AGENTE): return `Solicitacao.objects.filter(autor=request.user).order_by("-data")`
    - Override `get_serializer_class()`:
      - `create` -> `SolicitacaoCreateSerializer`
      - `partial_update` -> `SolicitacaoUpdateSerializer`
      - Default -> `SolicitacaoResponseSerializer`
    - Override `partial_update()`:
      - Get object, validate serializer, update status field only, save, return response with `SolicitacaoResponseSerializer`
    - Override `create()`:
      - Validate serializer, call `serializer.save()`, return response with `SolicitacaoResponseSerializer` and status 201
- **MIRROR**: `backend/apps/enderecos/views.py:14-26` - same ViewSet pattern
- **MIRROR**: Spring Boot `SolicitacaoService.getAll(Usuario)` lines 39-53 - role-based filtering logic
- **GOTCHA**: Spring Boot uses `findAllByOrderByIdAsc()` for ADMIN/OPERADOR and `findByAutorIdOrderByDataDesc(autorId)` for others. Match these ordering behaviors exactly.
- **GOTCHA**: For `create`, the `SolicitacaoCreateSerializer.create()` returns the Solicitacao instance. Re-serialize it with `SolicitacaoResponseSerializer` for the response (to include nested objects).
- **GOTCHA**: For `partial_update`, manually get the object, update status from validated data, save, and return with response serializer. Don't use `ModelViewSet`'s default `partial_update` since we're using a plain Serializer for update.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/solicitacoes/views.py && uv run ruff format --check apps/solicitacoes/views.py`

### Task 3: CREATE `backend/apps/solicitacoes/urls.py` - Solicitacao URL routing

- **ACTION**: Create app-level URL routing using DRF router
- **IMPLEMENT**:
  - Use `DefaultRouter()` to register `SolicitacaoViewSet` at `""` with basename `"solicitacao"`
  - Export `urlpatterns = router.urls`
- **MIRROR**: `backend/apps/enderecos/urls.py:1-10`
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/solicitacoes/urls.py`

### Task 4: CREATE `backend/apps/demandas/serializers.py` - Demanda + DemandaMaterial serializers

- **ACTION**: Create DRF serializers for Demanda module with nested writable DemandaMaterial serializers
- **IMPLEMENT**:
  - `DemandaMaterialCreateSerializer(Serializer)`:
    - Fields: `material_id` (IntegerField, required), `quantidade` (IntegerField, required, min_value=1)
    - Validation: Verify material_id references existing Material
    - Error messages: "Material e obrigatorio", "Quantidade e obrigatoria", "Quantidade deve ser maior que zero"
  - `DemandaMaterialResponseSerializer(ModelSerializer)`:
    - Fields: `id`, `material` (nested `MaterialResponseSerializer`), `quantidade`
    - All read-only
    - Import `MaterialResponseSerializer` from `apps.materiais.serializers`
  - `DemandaCreateSerializer(Serializer)`:
    - Fields: `solicitacao_id` (IntegerField, required), `responsavel_id` (IntegerField, required=False, allow_null=True), `prazo` (DateField, required), `materiais` (DemandaMaterialCreateSerializer, many=True, required=False, default=[])
    - Validation: Verify solicitacao_id references existing Solicitacao, responsavel_id (if provided) references existing Usuario
    - `create()` method:
      1. Fetch Solicitacao by ID
      2. Fetch Usuario by responsavel_id (if provided)
      3. Create Demanda with solicitacao, responsavel, prazo, status=PENDENTE (model default)
      4. For each material in materiais: Fetch Material by ID, create DemandaMaterial with demanda, material, quantidade
      5. Return demanda
    - Error messages: "Solicitacao e obrigatoria", "Prazo e obrigatorio"
  - `DemandaUpdateSerializer(Serializer)`:
    - Fields: `responsavel_id` (IntegerField, required=False, allow_null=True), `prazo` (DateField, required=False), `status` (ChoiceField with DemandaStatus.choices, required=False), `materiais` (DemandaMaterialCreateSerializer, many=True, required=False, allow_null=True)
    - All fields optional for PATCH semantics
    - Validation: responsavel_id (if provided) references existing Usuario
  - `DemandaResponseSerializer(ModelSerializer)`:
    - Fields: `id`, `solicitacao` (nested `SolicitacaoResponseSerializer`), `responsavel` (nested `AutorResponseSerializer`, allow_null=True), `prazo`, `status`, `materiais` (nested `DemandaMaterialResponseSerializer`, many=True, source="demanda_materiais")
    - Import `SolicitacaoResponseSerializer` from `apps.solicitacoes.serializers`
    - Import `AutorResponseSerializer` from `apps.solicitacoes.serializers`
    - All read-only
- **MIRROR**: Spring Boot `DemandaCreateDTO`, `DemandaUpdateDTO`, `DemandaResponseDTO`, `DemandaMaterialCreateDTO`, `DemandaMaterialResponseDTO`
- **GOTCHA**: DemandaMaterial is NOT a ModelSerializer create - it's managed manually in the `DemandaCreateSerializer.create()` method. This matches Spring Boot's `DemandaService.resolveMateriais()` pattern.
- **GOTCHA**: For `DemandaResponseSerializer`, materials are accessed via `source="demanda_materiais"` (the `related_name` on `DemandaMaterial.demanda` ForeignKey), NOT `source="materiais"` (the M2M through field).
- **GOTCHA**: `responsavel` is nullable in both model and response. Use `allow_null=True` on the nested serializer field.
- **GOTCHA**: On update, if `materiais` is provided, the entire materials set is replaced (Spring Boot clears then repopulates). This is handled in the ViewSet's `partial_update()`.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/demandas/serializers.py && uv run ruff format --check apps/demandas/serializers.py`

### Task 5: CREATE `backend/apps/demandas/views.py` - Demanda ViewSet with custom actions

- **ACTION**: Create ModelViewSet with custom actions for by_solicitacao and by_responsavel endpoints
- **IMPLEMENT**:
  - `DemandaViewSet(ModelViewSet)`:
    - `queryset = Demanda.objects.all().order_by("id")`
    - `permission_classes = [IsAllRoles]`
    - Override `get_serializer_class()`:
      - `create` -> `DemandaCreateSerializer`
      - `partial_update` -> `DemandaUpdateSerializer`
      - Default -> `DemandaResponseSerializer`
    - Override `create()`:
      - Validate serializer, call `serializer.save()`, re-serialize with `DemandaResponseSerializer`, return 201
    - Override `partial_update()`:
      - Get existing demanda object
      - Validate update serializer
      - Update non-None fields: `prazo`, `status`
      - If `responsavel_id` in validated_data: fetch Usuario and set (or set None if null)
      - If `materiais` in validated_data:
        - Delete all existing DemandaMaterial for this demanda (`demanda.demanda_materiais.all().delete()`)
        - Create new DemandaMaterial entries from list
      - Save demanda
      - Return response with `DemandaResponseSerializer`
    - `@action(detail=False, methods=["get"], url_path="solicitacao/(?P<solicitacao_id>[^/.]+)")`:
      - `by_solicitacao(request, solicitacao_id)`: Filter demandas by solicitacao_id, order by prazo ascending
      - Return paginated list with `DemandaResponseSerializer`
    - `@action(detail=False, methods=["get"])`:
      - `by_responsavel(request)`: Get `responsavel_id` from `request.query_params`
      - If not provided, return 400 error
      - Filter demandas by responsavel_id, order by prazo ascending
      - Return paginated list with `DemandaResponseSerializer`
- **MIRROR**: `backend/apps/usuarios/views.py:63-76` - custom @action pattern
- **MIRROR**: Spring Boot `DemandaService.updateDemanda()` lines 116-148 - update logic (clear materials, resolve new ones)
- **GOTCHA**: Spring Boot's by_solicitacao uses path param `/solicitacao/{solicitacaoId}`, by_responsavel uses query param `?responsavelId={id}`. Replicate this: use URL path capture for solicitacao, query param for responsavel.
- **GOTCHA**: For by_solicitacao and by_responsavel, use `self.paginate_queryset()` and `self.get_paginated_response()` to maintain pagination consistency.
- **GOTCHA**: When updating materials, use `transaction.atomic()` to ensure atomicity (matches Spring Boot's `@Transactional`).
- **GOTCHA**: Null check for `materiais` in update: if `materiais` key is present in validated_data (even as empty list), replace all. If key is absent, don't touch materials.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/demandas/views.py && uv run ruff format --check apps/demandas/views.py`

### Task 6: CREATE `backend/apps/demandas/urls.py` - Demanda URL routing

- **ACTION**: Create app-level URL routing using DRF router
- **IMPLEMENT**:
  - Use `DefaultRouter()` to register `DemandaViewSet` at `""` with basename `"demanda"`
  - Export `urlpatterns = router.urls`
- **MIRROR**: `backend/apps/enderecos/urls.py:1-10`
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/demandas/urls.py`

### Task 7: CREATE `backend/apps/comentarios/serializers.py` - Comentario serializers

- **ACTION**: Create DRF serializers for Comentario module (no Update serializer - comments are immutable)
- **IMPLEMENT**:
  - `ComentarioCreateSerializer(Serializer)`:
    - Fields: `demanda_id` (IntegerField, required), `autor_id` (IntegerField, required), `texto` (CharField, required)
    - Validation: Verify demanda_id references existing Demanda, autor_id references existing Usuario
    - `create()` method: Fetch Demanda and Usuario, create Comentario with demanda, autor, texto. `criado_em` is auto-set by `auto_now_add=True`.
    - Error messages: "Demanda e obrigatoria", "Autor e obrigatorio", "Texto e obrigatorio"
  - `ComentarioResponseSerializer(ModelSerializer)`:
    - Fields: `id`, `demanda_id` (IntegerField, source="demanda.id"), `autor` (nested `AutorResponseSerializer`), `texto`, `criado_em`
    - Import `AutorResponseSerializer` from `apps.solicitacoes.serializers`
    - All read-only
    - Note: Spring Boot's ComentarioResponseDTO returns `demandaId` (just the ID, not the full Demanda object), and `autor` as full Usuario. Match this.
- **MIRROR**: Spring Boot `ComentarioCreateDTO` and `ComentarioResponseDTO`
- **GOTCHA**: No `ComentarioUpdateSerializer` - Spring Boot has no update endpoint for comments. They are immutable after creation.
- **GOTCHA**: Response uses `demanda_id` (mapped from `demanda.id` via `source`), NOT the full Demanda object. This matches Spring Boot's `@Mapping(target = "demandaId", source = "demanda.id")`.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/comentarios/serializers.py && uv run ruff format --check apps/comentarios/serializers.py`

### Task 8: CREATE `backend/apps/comentarios/views.py` - Comentario ViewSet with by_demanda action

- **ACTION**: Create ModelViewSet for Comentario with restricted HTTP methods and custom action
- **IMPLEMENT**:
  - `ComentarioViewSet(ModelViewSet)`:
    - `queryset = Comentario.objects.all().order_by("id")`
    - `permission_classes = [IsAllRoles]`
    - `http_method_names = ["get", "post", "delete", "head", "options"]` (NO patch/put - comments immutable)
    - Override `get_serializer_class()`:
      - `create` -> `ComentarioCreateSerializer`
      - Default -> `ComentarioResponseSerializer`
    - Override `create()`:
      - Validate serializer, call `serializer.save()`, re-serialize with `ComentarioResponseSerializer`, return 201
    - `@action(detail=False, methods=["get"], url_path="demanda/(?P<demanda_id>[^/.]+)")`:
      - `by_demanda(request, demanda_id)`: Filter comentarios by demanda_id, order by criado_em ascending
      - Return paginated list with `ComentarioResponseSerializer`
      - Note: Spring Boot orders by criado_em ASC for per-demand listing
- **MIRROR**: `backend/apps/usuarios/views.py:27-76` - ViewSet with custom actions and restricted methods
- **GOTCHA**: Spring Boot's `getAll()` orders by ID ascending, but `getComentariosByDemanda()` orders by `criadoEmAsc`. Match both orderings.
- **GOTCHA**: `http_method_names` excludes `patch` and `put` to prevent updates. This is equivalent to Spring Boot not having an update endpoint.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/comentarios/views.py && uv run ruff format --check apps/comentarios/views.py`

### Task 9: CREATE `backend/apps/comentarios/urls.py` - Comentario URL routing

- **ACTION**: Create app-level URL routing using DRF router
- **IMPLEMENT**:
  - Use `DefaultRouter()` to register `ComentarioViewSet` at `""` with basename `"comentario"`
  - Export `urlpatterns = router.urls`
- **MIRROR**: `backend/apps/enderecos/urls.py:1-10`
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/comentarios/urls.py`

### Task 10: UPDATE `backend/config/urls.py` - Connect app URLs to root

- **ACTION**: Add `include()` calls for all three app URL modules
- **IMPLEMENT**: Add these paths to the `urlpatterns` list after the existing app endpoints (line 27):
  ```python
  path("api/solicitacoes/", include("apps.solicitacoes.urls")),
  path("api/demandas/", include("apps.demandas.urls")),
  path("api/comentarios/", include("apps.comentarios.urls")),
  ```
- **MIRROR**: `backend/config/urls.py:25-27` - add after existing app URL patterns
- **GOTCHA**: The prefix `api/solicitacoes/` is set here, so the app-level router registers at `""`. This gives URLs like `/api/solicitacoes/`, `/api/solicitacoes/{id}/`.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py check && uv run python -c "from django.urls import reverse; print(reverse('solicitacao-list')); print(reverse('demanda-list')); print(reverse('comentario-list'))"`

---

## Testing Strategy

### Verification Checks (No unit tests per PRD)

| Check | What It Validates | Command |
|-------|-------------------|---------|
| Django check | All settings and imports valid | `uv run python manage.py check` |
| Ruff lint | Code style for all new files | `uv run ruff check apps/solicitacoes/ apps/demandas/ apps/comentarios/` |
| Ruff format | Code formatting | `uv run ruff format --check apps/solicitacoes/ apps/demandas/ apps/comentarios/` |
| Import serializers | All serializer classes loadable | `uv run python -c "from apps.solicitacoes.serializers import SolicitacaoCreateSerializer, SolicitacaoUpdateSerializer, SolicitacaoResponseSerializer; print('Solicitacao serializers OK')"` |
| Import views | All ViewSet classes loadable | `uv run python -c "from apps.solicitacoes.views import SolicitacaoViewSet; from apps.demandas.views import DemandaViewSet; from apps.comentarios.views import ComentarioViewSet; print('ViewSets OK')"` |
| URL resolution | URLs registered correctly | `uv run python -c "from django.urls import reverse; print(reverse('solicitacao-list')); print(reverse('demanda-list')); print(reverse('demanda-by-solicitacao', kwargs={'solicitacao_id': 1})); print(reverse('demanda-by-responsavel')); print(reverse('comentario-list')); print(reverse('comentario-by-demanda', kwargs={'demanda_id': 1}))"` |
| Nested serializers | Nested serializer imports work | `uv run python -c "from apps.demandas.serializers import DemandaResponseSerializer, DemandaMaterialResponseSerializer; print('Nested serializers OK')"` |

### Edge Cases Checklist

- [ ] `GET /api/solicitacoes/` with CIDADAO role returns only user's own solicitacoes
- [ ] `GET /api/solicitacoes/` with ADMIN role returns all solicitacoes
- [ ] `POST /api/solicitacoes/` with missing required fields returns 400
- [ ] `POST /api/solicitacoes/` with invalid assunto enum value returns 400
- [ ] `POST /api/solicitacoes/` with non-existent autor_id returns 400
- [ ] `POST /api/solicitacoes/` with non-existent local_id returns 400
- [ ] `PATCH /api/solicitacoes/{id}/` only updates status field (other fields ignored)
- [ ] `POST /api/demandas/` with empty materiais list creates demand without materials
- [ ] `POST /api/demandas/` with materiais creates demand with DemandaMaterial entries
- [ ] `PATCH /api/demandas/{id}/` with materiais replaces entire materials set
- [ ] `PATCH /api/demandas/{id}/` without materiais key does not modify existing materials
- [ ] `GET /api/demandas/solicitacao/{id}/` returns demands for that solicitacao
- [ ] `GET /api/demandas/responsavel/?responsavel_id={id}` returns demands for that user
- [ ] `GET /api/demandas/responsavel/` without query param returns 400
- [ ] `POST /api/comentarios/` creates comment with auto-timestamp
- [ ] `PATCH /api/comentarios/{id}/` returns 405 Method Not Allowed
- [ ] `PUT /api/comentarios/{id}/` returns 405 Method Not Allowed
- [ ] `GET /api/comentarios/demanda/{id}/` returns comments ordered by criado_em ASC
- [ ] All responses include nested objects (full Usuario, Endereco, Material - not just IDs)
- [ ] DELETE on non-existent ID returns 404 with standardized error format

---

## Validation Commands

### Level 1: STATIC_ANALYSIS

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/solicitacoes/ apps/demandas/ apps/comentarios/ && uv run ruff format --check apps/solicitacoes/ apps/demandas/ apps/comentarios/
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
from apps.solicitacoes.serializers import SolicitacaoCreateSerializer, SolicitacaoUpdateSerializer, SolicitacaoResponseSerializer, AutorResponseSerializer, ArquivoSimpleResponseSerializer
from apps.solicitacoes.views import SolicitacaoViewSet
from apps.demandas.serializers import DemandaCreateSerializer, DemandaUpdateSerializer, DemandaResponseSerializer, DemandaMaterialCreateSerializer, DemandaMaterialResponseSerializer
from apps.demandas.views import DemandaViewSet
from apps.comentarios.serializers import ComentarioCreateSerializer, ComentarioResponseSerializer
from apps.comentarios.views import ComentarioViewSet
from django.urls import reverse
print('Solicitacoes:', reverse('solicitacao-list'))
print('Demandas:', reverse('demanda-list'))
print('Demandas by solicitacao:', reverse('demanda-by-solicitacao', kwargs={'solicitacao_id': 1}))
print('Demandas by responsavel:', reverse('demanda-by-responsavel'))
print('Comentarios:', reverse('comentario-list'))
print('Comentarios by demanda:', reverse('comentario-by-demanda', kwargs={'demanda_id': 1}))
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

- [ ] `GET /api/solicitacoes/` returns role-filtered, paginated list (CIDADAO sees own only, ordered by -data; ADMIN sees all, ordered by id)
- [ ] `GET /api/solicitacoes/{id}/` returns single solicitacao with nested autor, local, anexos
- [ ] `POST /api/solicitacoes/` creates solicitacao (201) with validation, accepts autor_id, local_id, anexo_ids
- [ ] `PATCH /api/solicitacoes/{id}/` updates status field only
- [ ] `DELETE /api/solicitacoes/{id}/` deletes solicitacao (204)
- [ ] `GET /api/demandas/` returns paginated list of demands with nested solicitacao, responsavel, materiais
- [ ] `GET /api/demandas/{id}/` returns single demand with full nested data
- [ ] `POST /api/demandas/` creates demand (201) with inline DemandaMaterial creation
- [ ] `PATCH /api/demandas/{id}/` updates demand (prazo, status, responsavel, materials replacement)
- [ ] `DELETE /api/demandas/{id}/` deletes demand and cascades to DemandaMaterial (204)
- [ ] `GET /api/demandas/solicitacao/{id}/` returns demands for that solicitacao, ordered by prazo ASC
- [ ] `GET /api/demandas/responsavel/?responsavel_id={id}` returns demands for that user, ordered by prazo ASC
- [ ] `GET /api/comentarios/` returns paginated list of comments ordered by id ASC
- [ ] `GET /api/comentarios/{id}/` returns single comment with nested autor and demanda_id
- [ ] `POST /api/comentarios/` creates comment (201) with auto-timestamp
- [ ] `DELETE /api/comentarios/{id}/` deletes comment (204)
- [ ] `GET /api/comentarios/demanda/{id}/` returns comments for that demand, ordered by criado_em ASC
- [ ] PATCH/PUT on comentarios returns 405
- [ ] Error responses follow format: `{"status": code, "error": "type", "message": "detail"}`
- [ ] `ruff check` and `ruff format --check` pass on all new files
- [ ] `python manage.py check` passes
- [ ] All endpoints appear in Swagger UI at `/api/schema/swagger-ui/`
- [ ] All endpoints accessible to all roles (CIDADAO, AGENTE, OPERADOR, ADMIN)

---

## Completion Checklist

- [ ] Task 1: Solicitacao serializers created (Create, Update, Response + AutorResponse + ArquivoSimpleResponse)
- [ ] Task 2: Solicitacao ViewSet created with role-based queryset filtering
- [ ] Task 3: Solicitacao URL routing created
- [ ] Task 4: Demanda serializers created (Create, Update, Response + DemandaMaterialCreate + DemandaMaterialResponse)
- [ ] Task 5: Demanda ViewSet created with by_solicitacao and by_responsavel custom actions
- [ ] Task 6: Demanda URL routing created
- [ ] Task 7: Comentario serializers created (Create, Response - no Update)
- [ ] Task 8: Comentario ViewSet created with by_demanda action and restricted HTTP methods
- [ ] Task 9: Comentario URL routing created
- [ ] Task 10: Root urls.py updated to include all 3 app URLs
- [ ] Level 1: Static analysis passes
- [ ] Level 2: Django check passes
- [ ] Level 3: Component verification passes
- [ ] Level 4: Full validation passes
- [ ] All acceptance criteria met

---

## Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Nested serializer circular import (Solicitacao imports Endereco, Demanda imports Solicitacao) | MEDIUM | HIGH | Keep AutorResponseSerializer and ArquivoSimpleResponseSerializer in solicitacoes.serializers; import in demandas. Avoid circular chains. |
| DemandaMaterial replacement race condition on concurrent updates | LOW | MEDIUM | Use `transaction.atomic()` in DemandaViewSet.partial_update() to ensure atomicity |
| DRF pagination wraps responses differently than Spring Boot | LOW | LOW | Spring Boot returned bare arrays; DRF returns `{count, next, previous, results}`. Acceptable per PRD. |
| Solicitacao role filtering bypassed on retrieve/delete | LOW | MEDIUM | `get_queryset()` affects list only; retrieve/delete use `get_object()` which by default uses the queryset. CIDADAO could potentially access/delete others' solicitacoes via direct ID. Consider if this is acceptable (Spring Boot doesn't restrict this either). |
| DemandaMaterial `demanda_materiais` related_name vs `materiais` M2M field | MEDIUM | HIGH | Response serializer MUST use `source="demanda_materiais"` for the through model, not `source="materiais"`. The M2M field doesn't include quantity. |
| AutorResponseSerializer returning raw fields vs UsuarioMeSerializer | LOW | LOW | AutorResponseSerializer returns `first_name`, `last_name` separately (matching Spring Boot's full Usuario object). UsuarioMeSerializer concatenates them. Both are correct for their contexts. |

---

## Notes

**Serializer Architecture Decision:**
`AutorResponseSerializer` and `ArquivoSimpleResponseSerializer` are defined in `apps/solicitacoes/serializers.py` rather than in a shared location. This avoids creating new shared modules and follows the pattern of defining serializers where they're first needed. Demandas and Comentarios import them from solicitacoes. If Phase 6+ needs them, they can be moved to `apps/core/serializers.py` later, but that's a future concern.

**DemandaMaterial Management:**
Spring Boot manages DemandaMaterial entirely through Demanda endpoints (no standalone CRUD). The Django implementation matches this: `DemandaCreateSerializer.create()` creates DemandaMaterial entries, and `DemandaViewSet.partial_update()` replaces them. The `DemandaMaterial` model has `on_delete=CASCADE` from Demanda, so deleting a Demanda cascades to its materials.

**Role-Based Filtering for Solicitacoes:**
Spring Boot's `SolicitacaoService.getAll(Usuario)` checks `ADMIN` or `OPERADOR` for full access, else filters by `autor_id`. The Django implementation uses `get_queryset()` which is called for list, retrieve, update, and delete. This means CIDADAO users can only access their own solicitacoes even for retrieve/delete, which is slightly more restrictive than Spring Boot (which only filters on list). This is actually more secure and acceptable.

**Pattern Established by This Phase:**
- Writable nested serializers for join tables (DemandaMaterial pattern)
- Role-based queryset filtering in ViewSet (Solicitacao pattern)
- Custom `@action` endpoints with path params and query params (Demanda pattern)
- Immutable entities with restricted HTTP methods (Comentario pattern)
- Shared nested response serializers imported across modules (AutorResponseSerializer)

**Ordering Differences:**
- Solicitacoes list: ADMIN/OPERADOR by `id` ASC; CIDADAO/AGENTE by `data` DESC
- Demandas list: by `id` ASC
- Demandas by solicitacao/responsavel: by `prazo` ASC (nulls may appear at end)
- Comentarios list: by `id` ASC
- Comentarios by demanda: by `criado_em` ASC

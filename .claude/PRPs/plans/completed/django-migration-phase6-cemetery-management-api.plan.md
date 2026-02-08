# Feature: Cemetery Management API (Phase 6)

## Summary

Implement DRF ViewSets, serializers, URL routing, and django-filter FilterSets for the cemetery management module: Cemiterios (CRUD with nested Endereco), Jazigos (CRUD linked to Cemiterio), Gavetas (CRUD with filtering by jazigo and ocupante), and Pessoas (CRUD with advanced filtering by nome, cpf, sexo, endereco_id and a dedicated by-CPF lookup endpoint). Cemiterios, Jazigos, and Gavetas are restricted to OPERADOR/ADMIN roles. Pessoas is accessible to all authenticated roles. All modules follow the established DRF patterns from Phases 4-5.

## User Story

As a backend developer migrating from Spring Boot to Django
I want the Cemiterios, Jazigos, Gavetas, and Pessoas API endpoints working with DRF
So that the cemetery management workflow is functional and matches the Spring Boot feature set

## Problem Statement

The Django project has models and migrations for Cemiterio, Jazigo, Gaveta, and Pessoa (Phase 2) but zero API endpoints. The Spring Boot app provides full CRUD for all four entities with filtering on Gavetas (by jazigoId, ocupanteId) and Pessoas (by nome, cpf, sexo, enderecoId), plus a dedicated CPF lookup endpoint for Pessoas. All of this must be replicated in Django using the DRF patterns from Phase 4-5.

## Solution Statement

Use DRF `ModelViewSet` for all four modules. Cemiterios accepts `endereco_id` in create/update serializers and returns nested `EnderecoResponseSerializer` in responses. Jazigos accepts `cemiterio_id` and returns nested cemiterio data. Gavetas accepts `jazigo_id` and `ocupante_id`, returns nested data, and uses `django-filter` `FilterSet` for list filtering by `jazigo` and `ocupante`. Pessoas uses `django-filter` for list filtering (nome with case-insensitive contains, cpf exact, sexo exact, endereco exact) and adds a custom `@action` for CPF lookup. Pessoas also checks for CPF uniqueness on create (raises `ConflictException`).

## Metadata

| Field            | Value                                              |
| ---------------- | -------------------------------------------------- |
| Type             | NEW_CAPABILITY                                     |
| Complexity       | MEDIUM                                             |
| Systems Affected | apps/cemiterios, apps/jazigos, apps/gavetas, apps/pessoas, config/urls |
| Dependencies     | djangorestframework 3.15.x, django-filter 24.3 (already installed) |
| Estimated Tasks  | 13                                                 |

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
|   | cemiterios    |         | cemetery mgmt |         | for cemetery  |       |
|   +---------------+         +---------------+         +---------------+       |
|                                                                               |
|   USER_FLOW: Any API request to /api/cemiterios/, /api/jazigos/,              |
|   /api/gavetas/, /api/pessoas/ returns 404 - no views or URL routes exist     |
|   PAIN_POINT: Models exist in DB (Phase 2) but operators and admins           |
|   cannot manage cemeteries, burial plots, burial spaces, or people            |
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
|      +----------------+-------------+-----------+------------------+          |
|      v                v                         v                  v          |
| +-------------+ +-------------+         +-------------+    +-------------+    |
| | /api/       | | /api/       |         | /api/       |    | /api/       |    |
| | cemiterios/ | | jazigos/    |         | gavetas/    |    | pessoas/    |    |
| | CRUD        | | CRUD        |         | CRUD +      |    | CRUD +      |    |
| | nested addr | | nested cem  |         | filter by   |    | filter by   |    |
| | OPERADOR+   | | OPERADOR+   |         | jazigo &    |    | nome, cpf,  |    |
| | ADMIN       | | ADMIN       |         | ocupante    |    | sexo, end.  |    |
| +-------------+ +-------------+         | OPERADOR+   |    | + /cpf      |    |
|                                         | ADMIN       |    | IsAllRoles  |    |
|                                         +-------------+    +-------------+    |
|                                                                               |
|   USER_FLOW: OPERADOR/ADMIN manages cemeteries end-to-end;                    |
|   any role can manage people; django-filter provides query param filtering    |
|   DATA_FLOW: Request -> Auth -> Permission -> ViewSet -> DB -> Response       |
|                                                                               |
+===============================================================================+
```

### Interaction Changes
| Location | Before | After | User Impact |
|----------|--------|-------|-------------|
| `/api/cemiterios/` | 404 Not Found | Full CRUD with nested Endereco | Operators can manage cemeteries |
| `/api/jazigos/` | 404 Not Found | Full CRUD linked to Cemiterio | Operators can manage burial plots |
| `/api/gavetas/` | 404 Not Found | CRUD + filter by jazigo/ocupante | Operators can find burial spaces |
| `/api/pessoas/` | 404 Not Found | CRUD + filter + CPF lookup | All roles can manage people records |

---

## Mandatory Reading

**CRITICAL: Implementation agent MUST read these files before starting any task:**

| Priority | File | Lines | Why Read This |
|----------|------|-------|---------------|
| P0 | `backend/apps/enderecos/serializers.py` | all | Simple CRUD serializer pattern to MIRROR exactly |
| P0 | `backend/apps/enderecos/views.py` | all | Simple ViewSet pattern to MIRROR exactly |
| P0 | `backend/apps/enderecos/urls.py` | all | URL routing pattern to MIRROR |
| P0 | `backend/apps/demandas/serializers.py` | all | FK relationship serializer pattern (Serializer with `_id` fields) |
| P0 | `backend/apps/demandas/views.py` | all | Custom create/update + @action pattern |
| P1 | `backend/apps/core/permissions.py` | all | Permission classes: IsOperadorOrAdmin, IsAllRoles |
| P1 | `backend/apps/core/exceptions.py` | all | NotFoundException, ConflictException |
| P1 | `backend/config/urls.py` | all | Where to register new routes |
| P2 | `backend/apps/cemiterios/models.py` | all | Existing model to build serializers for |
| P2 | `backend/apps/jazigos/models.py` | all | Existing model to build serializers for |
| P2 | `backend/apps/gavetas/models.py` | all | Existing model to build serializers for |
| P2 | `backend/apps/pessoas/models.py` | all | Existing model to build serializers for |
| P2 | `backend/apps/solicitacoes/serializers.py` | 36-44 | AutorResponseSerializer and nested response pattern |

---

## Patterns to Mirror

**SIMPLE_CRUD_SERIALIZER_PATTERN:**
```python
# SOURCE: backend/apps/enderecos/serializers.py:8-48
# COPY THIS PATTERN for modules without FK relationships:
class EnderecoCreateSerializer(serializers.ModelSerializer):
    """Serializer for creating an Endereco."""
    class Meta:
        model = Endereco
        fields = ["logradouro", "numero", "bairro", "referencia"]
        extra_kwargs = {
            "logradouro": {"error_messages": {"blank": "O logradouro e obrigatorio."}},
            "referencia": {"required": False, "allow_blank": True},
        }

class EnderecoUpdateSerializer(serializers.ModelSerializer):
    """Serializer for updating an Endereco (partial update)."""
    class Meta:
        model = Endereco
        fields = ["logradouro", "numero", "bairro", "referencia"]
        extra_kwargs = {
            "logradouro": {"required": False},
            "referencia": {"required": False, "allow_blank": True},
        }

class EnderecoResponseSerializer(serializers.ModelSerializer):
    """Serializer for Endereco responses."""
    class Meta:
        model = Endereco
        fields = ["id", "logradouro", "numero", "bairro", "referencia"]
        read_only_fields = fields
```

**FK_RELATIONSHIP_CREATE_SERIALIZER_PATTERN:**
```python
# SOURCE: backend/apps/demandas/serializers.py:50-104
# COPY THIS PATTERN for modules with FK relationships:
class DemandaCreateSerializer(serializers.Serializer):
    """Uses serializers.Serializer (not ModelSerializer) for FK fields."""
    solicitacao_id = serializers.IntegerField(
        error_messages={"required": "Solicitacao e obrigatoria."},
    )
    def validate_solicitacao_id(self, value):
        if not Solicitacao.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Solicitacao nao encontrada.")
        return value
    def create(self, validated_data):
        solicitacao = Solicitacao.objects.get(pk=validated_data["solicitacao_id"])
        demanda = Demanda.objects.create(solicitacao=solicitacao, ...)
        return demanda
```

**NESTED_RESPONSE_SERIALIZER_PATTERN:**
```python
# SOURCE: backend/apps/demandas/serializers.py:133-149
# COPY THIS PATTERN for response DTOs with nested related objects:
class DemandaResponseSerializer(serializers.ModelSerializer):
    solicitacao = SolicitacaoResponseSerializer(read_only=True)
    responsavel = AutorResponseSerializer(read_only=True, allow_null=True)
    class Meta:
        model = Demanda
        fields = ["id", "solicitacao", "responsavel", "prazo", "status", "materiais"]
        read_only_fields = fields
```

**SIMPLE_VIEWSET_PATTERN:**
```python
# SOURCE: backend/apps/enderecos/views.py:1-27
# COPY THIS PATTERN for simple CRUD ViewSets:
class EnderecoViewSet(ModelViewSet):
    queryset = Endereco.objects.all().order_by("id")
    permission_classes = [IsAllRoles]
    def get_serializer_class(self):
        if self.action == "create":
            return EnderecoCreateSerializer
        if self.action in ("update", "partial_update"):
            return EnderecoUpdateSerializer
        return EnderecoResponseSerializer
```

**VIEWSET_WITH_CUSTOM_CREATE_PATTERN:**
```python
# SOURCE: backend/apps/demandas/views.py:34-40
# COPY THIS PATTERN when create serializer differs from response serializer:
def create(self, request, *args, **kwargs):
    serializer = self.get_serializer(data=request.data)
    serializer.is_valid(raise_exception=True)
    instance = serializer.save()
    response_serializer = ResponseSerializer(instance)
    return Response(response_serializer.data, status=status.HTTP_201_CREATED)
```

**CUSTOM_ACTION_PATTERN:**
```python
# SOURCE: backend/apps/demandas/views.py:75-89
# COPY THIS PATTERN for custom query endpoints:
@action(
    detail=False,
    methods=["get"],
    url_path="solicitacao/(?P<solicitacao_id>[^/.]+)",
    url_name="by-solicitacao",
)
def by_solicitacao(self, request, solicitacao_id=None):
    queryset = Demanda.objects.filter(solicitacao_id=solicitacao_id).order_by("prazo")
    page = self.paginate_queryset(queryset)
    if page is not None:
        serializer = ResponseSerializer(page, many=True)
        return self.get_paginated_response(serializer.data)
    serializer = ResponseSerializer(queryset, many=True)
    return Response(serializer.data)
```

**URL_ROUTING_PATTERN:**
```python
# SOURCE: backend/apps/enderecos/urls.py:1-10
# COPY THIS PATTERN for all URL configurations:
from rest_framework.routers import DefaultRouter
from apps.module.views import ModuleViewSet
router = DefaultRouter()
router.register("", ModuleViewSet, basename="module")
urlpatterns = router.urls
```

---

## Files to Change

| File | Action | Justification |
| ---- | ------ | ------------- |
| `backend/apps/pessoas/serializers.py` | CREATE | Create/Update/Response serializers for Pessoa |
| `backend/apps/pessoas/filters.py` | CREATE | django-filter FilterSet for nome, cpf, sexo, endereco |
| `backend/apps/pessoas/views.py` | CREATE | ViewSet with filtering and CPF lookup action |
| `backend/apps/pessoas/urls.py` | CREATE | URL routing for pessoas |
| `backend/apps/cemiterios/serializers.py` | CREATE | Create/Update/Response serializers for Cemiterio |
| `backend/apps/cemiterios/views.py` | CREATE | ViewSet for cemiterios CRUD |
| `backend/apps/cemiterios/urls.py` | CREATE | URL routing for cemiterios |
| `backend/apps/jazigos/serializers.py` | CREATE | Create/Update/Response serializers for Jazigo |
| `backend/apps/jazigos/views.py` | CREATE | ViewSet for jazigos CRUD |
| `backend/apps/jazigos/urls.py` | CREATE | URL routing for jazigos |
| `backend/apps/gavetas/serializers.py` | CREATE | Create/Update/Response serializers for Gaveta |
| `backend/apps/gavetas/filters.py` | CREATE | django-filter FilterSet for jazigo and ocupante |
| `backend/apps/gavetas/views.py` | CREATE | ViewSet with filtering |
| `backend/apps/gavetas/urls.py` | CREATE | URL routing for gavetas |
| `backend/config/urls.py` | UPDATE | Register all 4 new module routes |

---

## NOT Building (Scope Limits)

- **No test suite** - Explicitly deprioritized in PRD; manual testing via Swagger UI
- **No audit logging** - Deferred per PRD decisions
- **No pagination override** - Uses global PageNumberPagination (PAGE_SIZE=20) from settings
- **No admin panel registration** - Not required for API-only usage
- **No model changes** - Models already exist from Phase 2 with migrations applied

---

## Step-by-Step Tasks

Execute in order. Each task is atomic and independently verifiable.

### Task 1: CREATE `backend/apps/pessoas/serializers.py`

- **ACTION**: CREATE serializers for Pessoa (has FK to Endereco, CPF uniqueness)
- **IMPLEMENT**:
  - `PessoaCreateSerializer(serializers.Serializer)` with fields: `nome` (CharField, required), `cpf` (CharField, required), `sexo` (ChoiceField with SexoEnum.choices), `endereco_id` (IntegerField, required). Include `validate_endereco_id()` to check Endereco exists.
  - `PessoaUpdateSerializer(serializers.Serializer)` with all fields optional: `nome` (CharField, required=False), `cpf` (CharField, required=False), `sexo` (ChoiceField, required=False), `endereco_id` (IntegerField, required=False, allow_null=True). Include `validate_endereco_id()`.
  - `PessoaResponseSerializer(serializers.ModelSerializer)` with `endereco = EnderecoResponseSerializer(read_only=True, allow_null=True)`. Fields: `["id", "nome", "cpf", "sexo", "endereco"]`.
  - `PessoaCreateSerializer.create()` must check CPF uniqueness via `Pessoa.objects.filter(cpf=cpf).exists()` and raise `ConflictException("CPF ja cadastrado.")` if duplicate.
- **MIRROR**: `backend/apps/demandas/serializers.py:50-104` for Serializer pattern with `_id` fields; `backend/apps/enderecos/serializers.py:40-48` for ResponseSerializer pattern
- **ERROR_MESSAGES**: Use Portuguese: "Nome e obrigatorio.", "CPF e obrigatorio.", "Sexo e obrigatorio.", "Endereco e obrigatorio.", "Endereco nao encontrado.", "CPF ja cadastrado.", "Sexo invalido."
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/pessoas/serializers.py`

### Task 2: CREATE `backend/apps/pessoas/filters.py`

- **ACTION**: CREATE django-filter FilterSet for Pessoa
- **IMPLEMENT**:
  - `PessoaFilter(django_filters.FilterSet)` with:
    - `nome` = `CharFilter(lookup_expr="icontains")` (case-insensitive contains, matching Spring Boot `LIKE %nome%`)
    - `cpf` = `CharFilter(lookup_expr="exact")`
    - `sexo` = `ChoiceFilter(choices=SexoEnum.choices)`
    - `endereco_id` = `NumberFilter(field_name="endereco__id")` (join query matching Spring Boot `endereco.id`)
  - `class Meta`: model = Pessoa, fields = ["nome", "cpf", "sexo", "endereco_id"]
- **MIRROR**: This is the first django-filter usage. Follow django-filter 24.3 conventions. DjangoFilterBackend is already globally configured in `REST_FRAMEWORK["DEFAULT_FILTER_BACKENDS"]` at `config/settings/base.py:135-137`.
- **IMPORTS**: `import django_filters` and `from apps.pessoas.models import Pessoa, SexoEnum`
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/pessoas/filters.py`

### Task 3: CREATE `backend/apps/pessoas/views.py`

- **ACTION**: CREATE ViewSet for Pessoa with filtering and CPF lookup
- **IMPLEMENT**:
  - `PessoaViewSet(ModelViewSet)` with:
    - `queryset = Pessoa.objects.all().order_by("id")`
    - `permission_classes = [IsAllRoles]` (all authenticated roles can access, matching Spring Boot)
    - `filterset_class = PessoaFilter` (enables `?nome=x&cpf=y&sexo=z&endereco_id=n` query params automatically)
    - `get_serializer_class()` routing: create -> PessoaCreateSerializer, partial_update -> PessoaUpdateSerializer, default -> PessoaResponseSerializer
    - Custom `create()` method: validate, save via serializer, return PessoaResponseSerializer with status 201
    - Custom `partial_update()` method: get_object, validate, apply field-by-field updates (nome, cpf, sexo, endereco_id), save, return PessoaResponseSerializer
    - `@action(detail=False, methods=["get"], url_path="cpf", url_name="by-cpf")` for `by_cpf()`: reads `cpf` from `request.query_params`, returns 400 if missing, looks up with `Pessoa.objects.get(cpf=cpf)` or raises NotFoundException
- **MIRROR**: `backend/apps/demandas/views.py:20-110` for ViewSet with custom create/update and @action; `backend/apps/enderecos/views.py:14-27` for simple get_serializer_class
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/pessoas/views.py`

### Task 4: CREATE `backend/apps/pessoas/urls.py`

- **ACTION**: CREATE URL routing for pessoas
- **IMPLEMENT**: Standard router pattern - `router.register("", PessoaViewSet, basename="pessoa")`
- **MIRROR**: `backend/apps/enderecos/urls.py:1-10` exactly
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/pessoas/urls.py`

### Task 5: CREATE `backend/apps/cemiterios/serializers.py`

- **ACTION**: CREATE serializers for Cemiterio (has OneToOne FK to Endereco)
- **IMPLEMENT**:
  - `CemiterioCreateSerializer(serializers.Serializer)` with fields: `nome` (CharField, required), `endereco_id` (IntegerField, required). Include `validate_endereco_id()`. Include `create()` that fetches Endereco and creates Cemiterio.
  - `CemiterioUpdateSerializer(serializers.Serializer)` with all fields optional: `nome` (CharField, required=False), `endereco_id` (IntegerField, required=False). Include `validate_endereco_id()`.
  - `CemiterioResponseSerializer(serializers.ModelSerializer)` with `endereco = EnderecoResponseSerializer(read_only=True)`. Fields: `["id", "nome", "endereco"]`.
- **MIRROR**: `backend/apps/demandas/serializers.py:50-104` for Serializer with _id fields and create()
- **ERROR_MESSAGES**: "Nome e obrigatorio.", "Endereco e obrigatorio.", "Endereco nao encontrado."
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/cemiterios/serializers.py`

### Task 6: CREATE `backend/apps/cemiterios/views.py`

- **ACTION**: CREATE ViewSet for Cemiterio CRUD
- **IMPLEMENT**:
  - `CemiterioViewSet(ModelViewSet)` with:
    - `queryset = Cemiterio.objects.all().order_by("id")`
    - `permission_classes = [IsOperadorOrAdmin]` (only operators and admins)
    - `get_serializer_class()` routing as per pattern
    - Custom `create()`: validate, save, return CemiterioResponseSerializer with 201
    - Custom `partial_update()`: get_object, validate, apply field-by-field (nome, endereco_id), save, return CemiterioResponseSerializer
- **MIRROR**: `backend/apps/demandas/views.py:34-73` for custom create/partial_update with FK fields
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/cemiterios/views.py`

### Task 7: CREATE `backend/apps/cemiterios/urls.py`

- **ACTION**: CREATE URL routing for cemiterios
- **IMPLEMENT**: `router.register("", CemiterioViewSet, basename="cemiterio")`
- **MIRROR**: `backend/apps/enderecos/urls.py:1-10`
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/cemiterios/urls.py`

### Task 8: CREATE `backend/apps/jazigos/serializers.py`

- **ACTION**: CREATE serializers for Jazigo (has FK to Cemiterio)
- **IMPLEMENT**:
  - `JazigoCreateSerializer(serializers.Serializer)` with fields: `cemiterio_id` (IntegerField, required), `largura` (FloatField, required=False, allow_null=True), `comprimento` (FloatField, required=False, allow_null=True), `quadra` (IntegerField, required), `rua` (CharField, required), `lote` (CharField, required). Include `validate_cemiterio_id()`. Include `create()`.
  - `JazigoUpdateSerializer(serializers.Serializer)` with all fields optional. Include `validate_cemiterio_id()`.
  - `JazigoResponseSerializer(serializers.ModelSerializer)` with `cemiterio = CemiterioResponseSerializer(read_only=True)`. Fields: `["id", "cemiterio", "largura", "comprimento", "quadra", "rua", "lote"]`.
- **MIRROR**: `backend/apps/demandas/serializers.py:50-104` for FK create pattern
- **ERROR_MESSAGES**: "Cemiterio e obrigatorio.", "Cemiterio nao encontrado.", "Quadra e obrigatoria.", "Rua e obrigatoria.", "Lote e obrigatorio."
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/jazigos/serializers.py`

### Task 9: CREATE `backend/apps/jazigos/views.py`

- **ACTION**: CREATE ViewSet for Jazigo CRUD
- **IMPLEMENT**:
  - `JazigoViewSet(ModelViewSet)` with:
    - `queryset = Jazigo.objects.all().order_by("id")`
    - `permission_classes = [IsOperadorOrAdmin]`
    - `get_serializer_class()` routing
    - Custom `create()` and `partial_update()` with FK handling for cemiterio_id
- **MIRROR**: `backend/apps/cemiterios/views.py` (same pattern, different FK)
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/jazigos/views.py`

### Task 10: CREATE `backend/apps/jazigos/urls.py`

- **ACTION**: CREATE URL routing for jazigos
- **IMPLEMENT**: `router.register("", JazigoViewSet, basename="jazigo")`
- **MIRROR**: `backend/apps/enderecos/urls.py:1-10`
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/jazigos/urls.py`

### Task 11: CREATE `backend/apps/gavetas/serializers.py`

- **ACTION**: CREATE serializers for Gaveta (has FK to Jazigo and optional FK to Pessoa)
- **IMPLEMENT**:
  - `GavetaCreateSerializer(serializers.Serializer)` with fields: `jazigo_id` (IntegerField, required), `numero` (IntegerField, required), `ocupante_id` (IntegerField, required). Include `validate_jazigo_id()` and `validate_ocupante_id()`. Include `create()`.
  - `GavetaUpdateSerializer(serializers.Serializer)` with all fields optional: `jazigo_id`, `numero`, `ocupante_id` (allow_null=True for ocupante_id). Include validators.
  - `GavetaResponseSerializer(serializers.ModelSerializer)` with `jazigo = JazigoResponseSerializer(read_only=True)` and `ocupante = PessoaResponseSerializer(read_only=True, allow_null=True)`. Fields: `["id", "jazigo", "numero", "ocupante"]`.
- **MIRROR**: `backend/apps/demandas/serializers.py:50-131` for multiple FK fields
- **ERROR_MESSAGES**: "Jazigo e obrigatorio.", "Jazigo nao encontrado.", "Numero e obrigatorio.", "Ocupante e obrigatorio.", "Pessoa nao encontrada."
- **GOTCHA**: GavetaCreateDTO in Spring Boot requires `numero` and `ocupante` as @NotNull. The Django model has `numero` and `ocupante` as nullable, but the create serializer should enforce them as required (matching Spring Boot behavior).
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/gavetas/serializers.py`

### Task 12: CREATE `backend/apps/gavetas/filters.py`

- **ACTION**: CREATE django-filter FilterSet for Gaveta
- **IMPLEMENT**:
  - `GavetaFilter(django_filters.FilterSet)` with:
    - `jazigo_id` = `NumberFilter(field_name="jazigo__id")` (matches Spring Boot `?jazigoId=`)
    - `ocupante_id` = `NumberFilter(field_name="ocupante__id")` (matches Spring Boot `?ocupanteId=`)
  - Naming note: Spring Boot uses camelCase `jazigoId` query params; Django will use snake_case `jazigo_id` (per PRD decision to use Python conventions).
- **MIRROR**: `backend/apps/pessoas/filters.py` (Task 2)
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/gavetas/filters.py`

### Task 13: CREATE `backend/apps/gavetas/views.py` and `backend/apps/gavetas/urls.py`

- **ACTION**: CREATE ViewSet and URL routing for Gaveta
- **IMPLEMENT**:
  - `GavetaViewSet(ModelViewSet)` with:
    - `queryset = Gaveta.objects.all().order_by("id")`
    - `permission_classes = [IsOperadorOrAdmin]`
    - `filterset_class = GavetaFilter` (enables `?jazigo_id=X&ocupante_id=Y`)
    - `get_serializer_class()` routing
    - Custom `create()` and `partial_update()` with FK handling for jazigo_id and ocupante_id
  - `urls.py`: `router.register("", GavetaViewSet, basename="gaveta")`
- **MIRROR**: `backend/apps/pessoas/views.py` (Task 3) for filterset_class usage; `backend/apps/demandas/views.py:42-73` for partial_update with multiple FK fields
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/gavetas/views.py apps/gavetas/urls.py`

### Task 14: UPDATE `backend/config/urls.py`

- **ACTION**: Register all 4 new module routes
- **IMPLEMENT**: Add after the existing `path("api/comentarios/", ...)` line:
  ```python
  path("api/cemiterios/", include("apps.cemiterios.urls")),
  path("api/jazigos/", include("apps.jazigos.urls")),
  path("api/gavetas/", include("apps.gavetas.urls")),
  path("api/pessoas/", include("apps.pessoas.urls")),
  ```
- **MIRROR**: `backend/config/urls.py:25-30` existing pattern
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check config/urls.py`

---

## Testing Strategy

### Manual Endpoint Testing (via Swagger UI or curl)

| Module | Test Cases | Validates |
| ------ | ---------- | --------- |
| Cemiterios | Create with endereco_id, list, get by ID, update nome, delete | CRUD with FK |
| Jazigos | Create with cemiterio_id, list, get by ID, update quadra, delete | CRUD with FK |
| Gavetas | Create, list, filter by jazigo_id, filter by ocupante_id, update, delete | CRUD + filtering |
| Pessoas | Create, list, filter by nome, filter by cpf, filter by sexo, CPF lookup, update, delete, duplicate CPF check | CRUD + filtering + conflict |

### Edge Cases Checklist

- [ ] Create Cemiterio with non-existent endereco_id -> 400
- [ ] Create Jazigo with non-existent cemiterio_id -> 400
- [ ] Create Gaveta with non-existent jazigo_id -> 400
- [ ] Create Gaveta with non-existent ocupante_id -> 400
- [ ] Create Pessoa with duplicate CPF -> 409 Conflict
- [ ] Get Pessoa by CPF that doesn't exist -> 404
- [ ] Filter Pessoas by nome (case-insensitive partial match)
- [ ] Filter Gavetas by jazigo_id only, ocupante_id only, and both
- [ ] CIDADAO role accessing /api/cemiterios/ -> 403 Forbidden
- [ ] OPERADOR role accessing /api/cemiterios/ -> 200 OK
- [ ] Partial update with only one field (others unchanged)
- [ ] Update Cemiterio endereco_id to new address

---

## Validation Commands

### Level 1: STATIC_ANALYSIS

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/cemiterios/ apps/jazigos/ apps/gavetas/ apps/pessoas/ config/urls.py
```

**EXPECT**: Exit 0, no errors or warnings

### Level 2: MIGRATION_CHECK

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py makemigrations --check --dry-run
```

**EXPECT**: "No changes detected" (no model changes in this phase)

### Level 3: IMPORT_CHECK

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python -c "
from apps.cemiterios.serializers import CemiterioCreateSerializer, CemiterioUpdateSerializer, CemiterioResponseSerializer
from apps.cemiterios.views import CemiterioViewSet
from apps.jazigos.serializers import JazigoCreateSerializer, JazigoUpdateSerializer, JazigoResponseSerializer
from apps.jazigos.views import JazigoViewSet
from apps.gavetas.serializers import GavetaCreateSerializer, GavetaUpdateSerializer, GavetaResponseSerializer
from apps.gavetas.filters import GavetaFilter
from apps.gavetas.views import GavetaViewSet
from apps.pessoas.serializers import PessoaCreateSerializer, PessoaUpdateSerializer, PessoaResponseSerializer
from apps.pessoas.filters import PessoaFilter
from apps.pessoas.views import PessoaViewSet
print('All imports successful')
"
```

**EXPECT**: "All imports successful" with exit 0

### Level 4: SERVER_START_CHECK

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && timeout 10 uv run python manage.py check --deploy 2>&1 || true
```

**EXPECT**: No critical errors related to new modules

### Level 5: BROWSER_VALIDATION (if Docker available)

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && docker-compose up -d && sleep 5
# Then test via Swagger UI at http://localhost:8000/api/schema/swagger-ui/
```

---

## Acceptance Criteria

- [ ] All 4 modules (cemiterios, jazigos, gavetas, pessoas) have working CRUD endpoints
- [ ] Cemiterios, Jazigos, Gavetas restricted to OPERADOR/ADMIN roles
- [ ] Pessoas accessible to all authenticated roles
- [ ] Gavetas supports filtering by jazigo_id and ocupante_id via query params
- [ ] Pessoas supports filtering by nome (icontains), cpf (exact), sexo (exact), endereco_id (exact)
- [ ] Pessoas has /cpf endpoint for CPF lookup
- [ ] Pessoa creation with duplicate CPF returns 409 Conflict
- [ ] All response serializers return nested related objects (not just IDs)
- [ ] Level 1-3 validation commands pass with exit 0
- [ ] Error messages are in Portuguese, matching existing codebase style

---

## Completion Checklist

- [ ] All 14 tasks completed in dependency order
- [ ] Each task validated immediately after completion
- [ ] Level 1: Ruff linting passes
- [ ] Level 2: No unexpected migrations
- [ ] Level 3: All imports successful
- [ ] All acceptance criteria met

---

## Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
| ---- | ---------- | ------ | ---------- |
| Circular import between serializers (Gaveta imports Jazigo which imports Cemiterio) | LOW | MED | Import response serializers directly from each module; Django handles lazy loading |
| django-filter query param naming (snake_case vs Spring Boot camelCase) | LOW | LOW | PRD explicitly chose Python conventions; frontend will adapt |
| Pessoa CPF uniqueness check race condition | LOW | LOW | Model already has `unique=True` constraint as DB-level backup |
| Large nested response payloads (Gaveta -> Jazigo -> Cemiterio -> Endereco) | MED | LOW | Acceptable for admin-facing API; can add depth control later if needed |

---

## Notes

- This is the first phase to use `django-filter` FilterSets. The `DjangoFilterBackend` is already configured globally in `REST_FRAMEWORK["DEFAULT_FILTER_BACKENDS"]` at `config/settings/base.py:135-137`, so adding `filterset_class` to a ViewSet is all that's needed.
- Pessoas uses `IsAllRoles` (not `IsOperadorOrAdmin`) because the Spring Boot app allows all authenticated users to access /api/pessoas/. This makes sense since Pessoas is shared across cemetery and other modules.
- The `PessoaCreateSerializer.create()` performs CPF uniqueness check at the serializer level (raising ConflictException), not relying on the DB constraint alone, to provide a user-friendly Portuguese error message.
- Nested serializer depth: GavetaResponseSerializer nests JazigoResponseSerializer which nests CemiterioResponseSerializer which nests EnderecoResponseSerializer. This matches Spring Boot behavior where the full entity graph is returned.

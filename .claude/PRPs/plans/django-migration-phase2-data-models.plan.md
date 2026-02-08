# Feature: Django Data Models (Phase 2)

## Summary

Define all Django models for SIGESI's 12 apps, translating every Spring Boot JPA entity into Django ORM models with proper relationships, enums (TextChoices), validation constraints, and cross-app references. This phase creates the complete data layer: 13 models, 6 enums, and generates initial migrations for all apps. No serializers, views, or URLs - just models and migrations.

## User Story

As a solo backend developer migrating from Spring Boot to Django
I want all entity models defined with proper relationships and migrations applied
So that the database schema is ready for building API endpoints in subsequent phases

## Problem Statement

The Django project has 12 empty app scaffolds from Phase 1. The Spring Boot codebase has 13 entities (excluding UsuarioRevisionEntity, which is audit-only) with complex relationships (ForeignKey, OneToOne, ManyToMany with through tables). These must be translated to Django ORM models following Python conventions while preserving the exact same data structure and constraints.

## Solution Statement

Implement Django models across all 12 apps, defining TextChoices enums, model fields with proper types and constraints, cross-app relationships using string references, and a ManyToMany through table for DemandaMaterial. Generate and apply migrations in a single pass after all models are defined.

## Metadata

| Field            | Value                                                                   |
| ---------------- | ----------------------------------------------------------------------- |
| Type             | NEW_CAPABILITY                                                          |
| Complexity       | MEDIUM                                                                  |
| Systems Affected | All 12 Django apps (usuarios, enderecos, solicitacoes, demandas, materiais, comentarios, documentos, arquivos, cemiterios, jazigos, gavetas, pessoas) |
| Dependencies     | Django 5.2, PostgreSQL 17 (via Docker)                                  |
| Estimated Tasks  | 15                                                                      |

---

## UX Design

### Before State
```
╔═══════════════════════════════════════════════════════════════════════════════╗
║                              BEFORE STATE                                   ║
╠═══════════════════════════════════════════════════════════════════════════════╣
║                                                                             ║
║   ┌─────────────────┐     ┌─────────────────┐     ┌──────────────────┐      ║
║   │  12 Django Apps │────►│  Empty models.py│────►│  No DB tables    │      ║
║   │  (scaffolds)    │     │  (no models)    │     │  (no migrations) │      ║
║   └─────────────────┘     └─────────────────┘     └──────────────────┘      ║
║                                                                             ║
║   DEVELOPER: Cannot build any API endpoints                                 ║
║   DATABASE: Empty except Django system tables                               ║
║                                                                             ║
╚═══════════════════════════════════════════════════════════════════════════════╝
```

### After State
```
╔═══════════════════════════════════════════════════════════════════════════════╗
║                               AFTER STATE                                   ║
╠═══════════════════════════════════════════════════════════════════════════════╣
║                                                                             ║
║   ┌─────────────────┐     ┌─────────────────┐     ┌──────────────────┐      ║
║   │  12 Django Apps │────►│  13 Models +    │────►│  All DB tables   │      ║
║   │  (complete)     │     │  6 Enums defined│     │  created via     │      ║
║   └─────────────────┘     └─────────────────┘     │  migrations      │      ║
║                                                   └──────────────────┘      ║
║                                                                             ║
║   DEVELOPER: Ready to build serializers & viewsets in Phase 3+              ║
║   DATABASE: All 13 tables + join tables created with constraints            ║
║                                                                             ║
║   RELATIONSHIPS:                                                            ║
║   Usuario ──► Solicitacao ──► Demanda ──► DemandaMaterial ──► Material      ║
║                                    └──► Comentario                          ║
║   Endereco ──► Cemiterio ──► Jazigo ──► Gaveta ──► Pessoa                   ║
║   Arquivo ◄──► Solicitacao, Documento (ManyToMany)                          ║
║                                                                             ║
╚═══════════════════════════════════════════════════════════════════════════════╝
```

### Interaction Changes
| Location | Before | After | Developer Impact |
|----------|--------|-------|------------------|
| `apps/*/models.py` | Empty docstring | Full model definitions | Can import models in serializers/views |
| `apps/*/migrations/` | Only `__init__.py` | `0001_initial.py` generated | `migrate` creates all tables |
| Database | No app tables | 13+ tables with constraints | Ready for data operations |
| Django Admin | No models registered | Models available for admin | Can inspect data via admin |

---

## Mandatory Reading

**CRITICAL: Implementation agent MUST read these files before starting any task:**

| Priority | File | Lines | Why Read This |
|----------|------|-------|---------------|
| P0 | `backend/config/settings/base.py` | all | AUTH_USER_MODEL, DEFAULT_AUTO_FIELD, INSTALLED_APPS |
| P0 | `backend/apps/usuarios/models.py` | all | Current Usuario scaffold to expand |
| P0 | `backend/apps/usuarios/apps.py` | all | App config pattern to follow |
| P1 | `src/main/java/com/sigesi/sigesi/usuarios/Usuario.java` | all | Source entity fields |
| P1 | `src/main/java/com/sigesi/sigesi/solicitacoes/Solicitacao.java` | all | Source entity with complex relationships |
| P1 | `src/main/java/com/sigesi/sigesi/demandas/Demanda.java` | all | Through table pattern source |
| P1 | `backend/pyproject.toml` | all | Ruff config - code style rules |

**External Documentation:**
| Source | Section | Why Needed |
|--------|---------|------------|
| [Django 5.2 Custom User Model](https://docs.djangoproject.com/en/5.2/topics/auth/customizing/) | Extending AbstractUser | Pattern for Usuario model |
| [Django 5.2 Model Fields](https://docs.djangoproject.com/en/5.2/ref/models/fields/) | Enumeration types, Relationship fields | TextChoices and FK patterns |
| [Django 5.2 Extra Fields on M2M](https://docs.djangoproject.com/en/5.2/topics/db/models/#extra-fields-on-many-to-many-relationships) | Through tables | DemandaMaterial pattern |
| [Django 5.2 Migrations](https://docs.djangoproject.com/en/5.2/topics/migrations/) | Cross-app dependencies | Multi-app migration strategy |

---

## Patterns to Mirror

**DJANGO APP CONFIG:**
```python
# SOURCE: backend/apps/usuarios/apps.py
# COPY THIS PATTERN for all apps:
"""Usuarios app configuration."""

from django.apps import AppConfig


class UsuariosConfig(AppConfig):
    """Configuration for the Usuarios app."""

    default_auto_field = "django.db.models.BigAutoField"
    name = "apps.usuarios"
    verbose_name = "Usuarios"
```

**MODEL DOCSTRING PATTERN:**
```python
# All models.py files start with:
"""<AppName> models."""
# All classes have docstrings:
class MyModel(models.Model):
    """Description of the model."""
```

**SETTINGS REFERENCES:**
```python
# SOURCE: backend/config/settings/base.py:94
# AUTH_USER_MODEL = "usuarios.Usuario"
# Always reference user model via settings:
from django.conf import settings
autor = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.PROTECT)
```

**RUFF CONFIG:**
```python
# SOURCE: backend/pyproject.toml:26-65
# line-length = 120
# double quotes, space indent
# Migrations excluded from linting
# __init__.py allows F401 (unused imports)
```

---

## Files to Change

| File | Action | Justification |
| ---- | ------ | ------------- |
| `backend/apps/usuarios/models.py` | UPDATE | Expand Usuario with role, picture_url, provider, ativo fields |
| `backend/apps/enderecos/models.py` | UPDATE | Define Endereco model (shared dependency) |
| `backend/apps/arquivos/models.py` | UPDATE | Define Arquivo model (shared dependency for M2M) |
| `backend/apps/materiais/models.py` | UPDATE | Define Material model (leaf node) |
| `backend/apps/solicitacoes/models.py` | UPDATE | Define Solicitacao model with enums and relationships |
| `backend/apps/demandas/models.py` | UPDATE | Define Demanda + DemandaMaterial models |
| `backend/apps/comentarios/models.py` | UPDATE | Define Comentario model |
| `backend/apps/documentos/models.py` | UPDATE | Define Documento model with enum |
| `backend/apps/cemiterios/models.py` | UPDATE | Define Cemiterio model |
| `backend/apps/jazigos/models.py` | UPDATE | Define Jazigo model |
| `backend/apps/pessoas/models.py` | UPDATE | Define Pessoa model with enum |
| `backend/apps/gavetas/models.py` | UPDATE | Define Gaveta model |

---

## NOT Building (Scope Limits)

- **Serializers/DTOs** - Phase 4+ scope, not needed for data model definition
- **Views/ViewSets** - Phase 4+ scope
- **URL routing** - Phase 4+ scope
- **Admin registration** - Not required by the PRD, deferred
- **Audit logging** - Explicitly excluded from migration per PRD
- **Custom managers/querysets** - Simple ORM queries suffice, add only if needed in API phases
- **Model methods beyond `__str__`** - Business logic belongs in views/services
- **Data migrations (initial data)** - No seed data needed
- **Tests** - Explicitly deprioritized in the PRD

---

## Step-by-Step Tasks

Execute in order. Each task is atomic and independently verifiable.

### Task 1: UPDATE `backend/apps/usuarios/models.py` - Usuario model

- **ACTION**: Expand the existing Usuario scaffold with all fields from Spring Boot entity
- **IMPLEMENT**:
  ```python
  """Usuarios models."""

  from django.contrib.auth.models import AbstractUser
  from django.db import models


  class Usuario(AbstractUser):
      """Custom user model for SIGESI with OAuth2 and role support."""

      class Role(models.TextChoices):
          """User roles for access control."""

          CIDADAO = "CIDADAO", "Cidadao"
          OPERADOR = "OPERADOR", "Operador"
          AGENTE = "AGENTE", "Agente"
          ADMIN = "ADMIN", "Admin"

      picture_url = models.URLField(max_length=500, blank=True, default="")
      provider = models.CharField(max_length=50, blank=True, default="")
      ativo = models.BooleanField(default=True)
      role = models.CharField(max_length=20, choices=Role, default=Role.CIDADAO)

      class Meta:
          """Meta options for Usuario."""

          verbose_name = "usuario"
          verbose_name_plural = "usuarios"

      def __str__(self):
          """Return user email as string representation."""
          return self.email
  ```
- **MAPPING from Spring Boot**:
  - `id` (Long) -> BigAutoField (auto via DEFAULT_AUTO_FIELD)
  - `email` (String) -> inherited from AbstractUser
  - `name` (String) -> inherited from AbstractUser (first_name + last_name, or use username)
  - `pictureUrl` (String) -> `picture_url` URLField
  - `provider` (String) -> `provider` CharField
  - `ativo` (Boolean) -> `ativo` BooleanField
  - `role` (Role enum) -> `role` CharField with TextChoices
- **GOTCHA**: `AbstractUser` already provides `email`, `username`, `first_name`, `last_name`, `is_active`, `is_staff`, `is_superuser`. The Spring Boot `name` field maps to Django's existing name fields. The Spring Boot `ativo` is separate from Django's `is_active` - keep both since `ativo` is domain-specific.
- **GOTCHA**: Role enum defined as inner class of Usuario since it's only used by this model.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/usuarios/models.py`

### Task 2: UPDATE `backend/apps/enderecos/models.py` - Endereco model

- **ACTION**: Define Endereco model (shared dependency - used by Solicitacao, Cemiterio, Pessoa)
- **IMPLEMENT**:
  ```python
  """Enderecos models."""

  from django.db import models


  class Endereco(models.Model):
      """Address entity used by solicitacoes, cemiterios, and pessoas."""

      logradouro = models.CharField(max_length=255)
      numero = models.CharField(max_length=20)
      bairro = models.CharField(max_length=255)
      referencia = models.CharField(max_length=255, blank=True, default="")

      class Meta:
          """Meta options for Endereco."""

          verbose_name = "endereco"
          verbose_name_plural = "enderecos"

      def __str__(self):
          """Return formatted address string."""
          return f"{self.logradouro}, {self.numero} - {self.bairro}"
  ```
- **MAPPING**: All fields from Spring Boot Endereco. `@NotBlank` -> required by default (no `blank=True`). `referencia` is optional -> `blank=True, default=""`.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/enderecos/models.py`

### Task 3: UPDATE `backend/apps/arquivos/models.py` - Arquivo model

- **ACTION**: Define Arquivo model (used as M2M target by Solicitacao and Documento)
- **IMPLEMENT**:
  ```python
  """Arquivos models."""

  from django.db import models


  class Arquivo(models.Model):
      """File metadata entity stored with MinIO."""

      nome_original = models.CharField(max_length=255)
      storage_key = models.CharField(max_length=500, unique=True)
      content_type = models.CharField(max_length=100)
      tamanho = models.BigIntegerField()
      categoria = models.CharField(max_length=100, blank=True, default="")
      uploaded_at = models.DateTimeField(auto_now_add=True)
      ativo = models.BooleanField(default=True)

      class Meta:
          """Meta options for Arquivo."""

          verbose_name = "arquivo"
          verbose_name_plural = "arquivos"
          ordering = ["-uploaded_at"]

      def __str__(self):
          """Return original filename."""
          return self.nome_original
  ```
- **MAPPING**: `nomeOriginal` -> `nome_original`, `storageKey` -> `storage_key` (unique), `contentType` -> `content_type`, `tamanho` -> BigIntegerField (Long in Java), `uploadedAt` -> `uploaded_at` with `auto_now_add=True`, `ativo` -> BooleanField default True.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/arquivos/models.py`

### Task 4: UPDATE `backend/apps/materiais/models.py` - Material model

- **ACTION**: Define Material model (leaf node, used by DemandaMaterial)
- **IMPLEMENT**:
  ```python
  """Materiais models."""

  from django.db import models


  class Material(models.Model):
      """Materials catalog with name and price."""

      nome = models.CharField(max_length=255)
      preco = models.DecimalField(max_digits=10, decimal_places=2)

      class Meta:
          """Meta options for Material."""

          verbose_name = "material"
          verbose_name_plural = "materiais"
          ordering = ["nome"]

      def __str__(self):
          """Return material name."""
          return self.nome
  ```
- **MAPPING**: `nome` -> CharField (required), `preco` -> DecimalField (better than FloatField for money). Spring Boot uses `Double` but `DecimalField` is the Python convention for monetary values.
- **GOTCHA**: Using `DecimalField` instead of `FloatField` to avoid floating-point precision issues with prices.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/materiais/models.py`

### Task 5: UPDATE `backend/apps/solicitacoes/models.py` - Solicitacao model with enums

- **ACTION**: Define SolicitacaoAssunto enum, SolicitacaoStatus enum, and Solicitacao model
- **IMPLEMENT**:
  ```python
  """Solicitacoes models."""

  from django.conf import settings
  from django.db import models


  class SolicitacaoAssunto(models.TextChoices):
      """Subject categories for service requests."""

      BURACO = "BURACO", "Buraco"
      ESGOTO = "ESGOTO", "Esgoto"
      ILUMINACAO = "ILUMINACAO", "Iluminacao"
      LIMPEZA = "LIMPEZA", "Limpeza"
      OUTROS = "OUTROS", "Outros"


  class SolicitacaoStatus(models.TextChoices):
      """Status options for service requests."""

      ABERTA = "ABERTA", "Aberta"
      EM_ANDAMENTO = "EM_ANDAMENTO", "Em Andamento"
      CONCLUIDA = "CONCLUIDA", "Concluida"
      ENCERRADA = "ENCERRADA", "Encerrada"
      REJEITADA = "REJEITADA", "Rejeitada"


  class Solicitacao(models.Model):
      """Service request entity - citizen-reported issues."""

      data = models.DateField(auto_now_add=True)
      assunto = models.CharField(max_length=20, choices=SolicitacaoAssunto)
      body = models.TextField()
      autor = models.ForeignKey(
          settings.AUTH_USER_MODEL,
          on_delete=models.PROTECT,
          related_name="solicitacoes",
      )
      local = models.ForeignKey(
          "enderecos.Endereco",
          on_delete=models.PROTECT,
          related_name="solicitacoes",
      )
      status = models.CharField(
          max_length=20,
          choices=SolicitacaoStatus,
          default=SolicitacaoStatus.ABERTA,
      )
      anexos = models.ManyToManyField("arquivos.Arquivo", blank=True, related_name="solicitacoes")

      class Meta:
          """Meta options for Solicitacao."""

          verbose_name = "solicitacao"
          verbose_name_plural = "solicitacoes"
          ordering = ["-data"]

      def __str__(self):
          """Return solicitacao summary."""
          return f"Solicitacao #{self.pk} - {self.assunto}"
  ```
- **MAPPING**:
  - `data` -> DateField with `auto_now_add=True` (matches `@PrePersist LocalDate.now()`)
  - `assunto` -> CharField with SolicitacaoAssunto TextChoices
  - `body` -> TextField (maps to `@Column(columnDefinition = "TEXT")`)
  - `autor` -> ForeignKey to AUTH_USER_MODEL with PROTECT
  - `local` -> ForeignKey to Endereco with PROTECT
  - `status` -> CharField with SolicitacaoStatus, default ABERTA
  - `anexos` -> ManyToManyField to Arquivo (simple M2M, no through table needed)
- **GOTCHA**: Use `settings.AUTH_USER_MODEL` string reference for ForeignKey to custom user model.
- **GOTCHA**: `on_delete=PROTECT` prevents deleting a user/endereco that has solicitacoes referencing it.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/solicitacoes/models.py`

### Task 6: UPDATE `backend/apps/demandas/models.py` - Demanda + DemandaMaterial models

- **ACTION**: Define DemandaStatus enum, Demanda model, and DemandaMaterial through table
- **IMPLEMENT**:
  ```python
  """Demandas models."""

  from django.conf import settings
  from django.db import models


  class DemandaStatus(models.TextChoices):
      """Status options for work demands."""

      PENDENTE = "PENDENTE", "Pendente"
      EM_ANDAMENTO = "EM_ANDAMENTO", "Em Andamento"
      CONCLUIDA = "CONCLUIDA", "Concluida"
      CANCELADA = "CANCELADA", "Cancelada"


  class Demanda(models.Model):
      """Work demand entity derived from service requests."""

      solicitacao = models.ForeignKey(
          "solicitacoes.Solicitacao",
          on_delete=models.CASCADE,
          related_name="demandas",
      )
      responsavel = models.ForeignKey(
          settings.AUTH_USER_MODEL,
          on_delete=models.PROTECT,
          related_name="demandas",
          null=True,
          blank=True,
      )
      prazo = models.DateField(null=True, blank=True)
      status = models.CharField(
          max_length=20,
          choices=DemandaStatus,
          default=DemandaStatus.PENDENTE,
      )
      materiais = models.ManyToManyField(
          "materiais.Material",
          through="DemandaMaterial",
          blank=True,
          related_name="demandas",
      )

      class Meta:
          """Meta options for Demanda."""

          verbose_name = "demanda"
          verbose_name_plural = "demandas"

      def __str__(self):
          """Return demanda summary."""
          return f"Demanda #{self.pk} - {self.status}"


  class DemandaMaterial(models.Model):
      """Join entity linking demands to materials with quantity."""

      demanda = models.ForeignKey(
          Demanda,
          on_delete=models.CASCADE,
          related_name="demanda_materiais",
      )
      material = models.ForeignKey(
          "materiais.Material",
          on_delete=models.PROTECT,
          related_name="demanda_materiais",
      )
      quantidade = models.PositiveIntegerField()

      class Meta:
          """Meta options for DemandaMaterial."""

          verbose_name = "demanda material"
          verbose_name_plural = "demanda materiais"
          constraints = [
              models.UniqueConstraint(
                  fields=["demanda", "material"],
                  name="unique_demanda_material",
              )
          ]

      def __str__(self):
          """Return demanda-material summary."""
          return f"{self.material.nome} x{self.quantidade}"
  ```
- **MAPPING**:
  - `solicitacao` -> ForeignKey with CASCADE (delete demands when solicitacao deleted)
  - `responsavel` -> ForeignKey to user, nullable (Spring Boot has `@JoinColumn(nullable)` implying optional)
  - `prazo` -> DateField nullable (optional in Spring Boot: `@Column(nullable=false)` but PRD says Optional)
  - `status` -> CharField with DemandaStatus, default PENDENTE
  - `materiais` -> ManyToManyField with through=DemandaMaterial
  - DemandaMaterial: `demanda` FK, `material` FK, `quantidade` PositiveIntegerField
- **GOTCHA**: ManyToMany with `through` table means you CANNOT use `.add()`, `.create()`, `.set()` on the M2M manager. Must create DemandaMaterial instances directly.
- **GOTCHA**: UniqueConstraint prevents duplicate material entries on the same demand.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/demandas/models.py`

### Task 7: UPDATE `backend/apps/comentarios/models.py` - Comentario model

- **ACTION**: Define Comentario model
- **IMPLEMENT**:
  ```python
  """Comentarios models."""

  from django.conf import settings
  from django.db import models


  class Comentario(models.Model):
      """Comment on a work demand."""

      demanda = models.ForeignKey(
          "demandas.Demanda",
          on_delete=models.CASCADE,
          related_name="comentarios",
      )
      autor = models.ForeignKey(
          settings.AUTH_USER_MODEL,
          on_delete=models.PROTECT,
          related_name="comentarios",
      )
      texto = models.TextField()
      criado_em = models.DateTimeField(auto_now_add=True)

      class Meta:
          """Meta options for Comentario."""

          verbose_name = "comentario"
          verbose_name_plural = "comentarios"
          ordering = ["-criado_em"]

      def __str__(self):
          """Return comment summary."""
          return f"Comentario #{self.pk} por {self.autor}"
  ```
- **MAPPING**: `demanda` FK with CASCADE, `autor` FK with PROTECT, `texto` TextField, `criadoEm` -> `criado_em` with `auto_now_add=True`.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/comentarios/models.py`

### Task 8: UPDATE `backend/apps/documentos/models.py` - Documento model with enum

- **ACTION**: Define DocumentoTipo enum and Documento model
- **IMPLEMENT**:
  ```python
  """Documentos models."""

  from django.db import models


  class DocumentoTipo(models.TextChoices):
      """Types of official documents."""

      OFICIO = "OFICIO", "Oficio"
      MEMORANDO = "MEMORANDO", "Memorando"


  class Documento(models.Model):
      """Official document entity (oficios, memorandos)."""

      numero = models.CharField(max_length=50, blank=True, default="")
      data = models.DateField(auto_now_add=True)
      subject = models.CharField(max_length=255)
      honorifico = models.CharField(max_length=255, blank=True, default="")
      body = models.TextField()
      tipo = models.CharField(max_length=20, choices=DocumentoTipo)
      portaria = models.CharField(max_length=255, blank=True, default="")
      assinante = models.CharField(max_length=255)
      interessado = models.CharField(max_length=255)
      destino = models.CharField(max_length=255, blank=True, default="")
      anexos = models.ManyToManyField("arquivos.Arquivo", blank=True, related_name="documentos")

      class Meta:
          """Meta options for Documento."""

          verbose_name = "documento"
          verbose_name_plural = "documentos"
          ordering = ["-data"]

      def __str__(self):
          """Return document summary."""
          return f"{self.tipo} {self.numero} - {self.subject}"
  ```
- **MAPPING**:
  - `numero` -> CharField optional (blank in Spring Boot, but has no @NotBlank - optional)
  - `data` -> DateField `auto_now_add=True` (matches @PrePersist)
  - `subject`, `body` -> required (Spring Boot @NotBlank)
  - `assinante`, `interessado` -> required (Spring Boot @NotBlank)
  - `honorifico`, `portaria`, `destino` -> optional
  - `tipo` -> CharField with DocumentoTipo TextChoices
  - `anexos` -> ManyToManyField to Arquivo
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/documentos/models.py`

### Task 9: UPDATE `backend/apps/cemiterios/models.py` - Cemiterio model

- **ACTION**: Define Cemiterio model with OneToOne to Endereco
- **IMPLEMENT**:
  ```python
  """Cemiterios models."""

  from django.db import models


  class Cemiterio(models.Model):
      """Cemetery entity with a location address."""

      nome = models.CharField(max_length=255)
      endereco = models.OneToOneField(
          "enderecos.Endereco",
          on_delete=models.CASCADE,
          related_name="cemiterio",
      )

      class Meta:
          """Meta options for Cemiterio."""

          verbose_name = "cemiterio"
          verbose_name_plural = "cemiterios"
          ordering = ["nome"]

      def __str__(self):
          """Return cemetery name."""
          return self.nome
  ```
- **MAPPING**: `nome` required, `endereco` OneToOneField with CASCADE (matches Spring Boot @OneToOne).
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/cemiterios/models.py`

### Task 10: UPDATE `backend/apps/jazigos/models.py` - Jazigo model

- **ACTION**: Define Jazigo model
- **IMPLEMENT**:
  ```python
  """Jazigos models."""

  from django.db import models


  class Jazigo(models.Model):
      """Burial plot entity located within a cemetery."""

      cemiterio = models.ForeignKey(
          "cemiterios.Cemiterio",
          on_delete=models.CASCADE,
          related_name="jazigos",
      )
      largura = models.FloatField(null=True, blank=True)
      comprimento = models.FloatField(null=True, blank=True)
      quadra = models.IntegerField()
      rua = models.CharField(max_length=50)
      lote = models.CharField(max_length=50)

      class Meta:
          """Meta options for Jazigo."""

          verbose_name = "jazigo"
          verbose_name_plural = "jazigos"
          ordering = ["quadra", "rua", "lote"]

      def __str__(self):
          """Return jazigo location identifier."""
          return f"Quadra {self.quadra}, Rua {self.rua}, Lote {self.lote}"
  ```
- **MAPPING**: `cemiterio` FK with CASCADE, `largura`/`comprimento` optional FloatField (Double in Java), `quadra` required IntegerField, `rua`/`lote` required CharField.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/jazigos/models.py`

### Task 11: UPDATE `backend/apps/pessoas/models.py` - Pessoa model with SexoEnum

- **ACTION**: Define SexoEnum and Pessoa model
- **IMPLEMENT**:
  ```python
  """Pessoas models."""

  from django.db import models


  class SexoEnum(models.TextChoices):
      """Gender options for persons."""

      MASCULINO = "MASCULINO", "Masculino"
      FEMININO = "FEMININO", "Feminino"
      OUTRO = "OUTRO", "Outro"


  class Pessoa(models.Model):
      """Person entity with personal information."""

      nome = models.CharField(max_length=255)
      cpf = models.CharField(max_length=14, unique=True)
      sexo = models.CharField(max_length=10, choices=SexoEnum)
      endereco = models.ForeignKey(
          "enderecos.Endereco",
          on_delete=models.SET_NULL,
          null=True,
          blank=True,
          related_name="pessoas",
      )

      class Meta:
          """Meta options for Pessoa."""

          verbose_name = "pessoa"
          verbose_name_plural = "pessoas"
          ordering = ["nome"]

      def __str__(self):
          """Return person name."""
          return self.nome
  ```
- **MAPPING**: `nome` required (@NotBlank), `cpf` required with unique constraint (existsByCpf in repo), `sexo` required with SexoEnum TextChoices, `endereco` optional FK with SET_NULL.
- **GOTCHA**: CPF max_length=14 to accommodate formatted CPFs (XXX.XXX.XXX-XX). `unique=True` replaces the `existsByCpf` repository check.
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/pessoas/models.py`

### Task 12: UPDATE `backend/apps/gavetas/models.py` - Gaveta model

- **ACTION**: Define Gaveta model
- **IMPLEMENT**:
  ```python
  """Gavetas models."""

  from django.db import models


  class Gaveta(models.Model):
      """Burial space entity within a jazigo."""

      jazigo = models.ForeignKey(
          "jazigos.Jazigo",
          on_delete=models.CASCADE,
          related_name="gavetas",
      )
      numero = models.IntegerField(null=True, blank=True)
      ocupante = models.ForeignKey(
          "pessoas.Pessoa",
          on_delete=models.SET_NULL,
          null=True,
          blank=True,
          related_name="gavetas",
      )

      class Meta:
          """Meta options for Gaveta."""

          verbose_name = "gaveta"
          verbose_name_plural = "gavetas"
          ordering = ["numero"]

      def __str__(self):
          """Return gaveta identifier."""
          return f"Gaveta {self.numero or 'S/N'}"
  ```
- **MAPPING**: `jazigo` FK with CASCADE, `numero` optional IntegerField, `ocupante` optional FK with SET_NULL (removing person doesn't delete gaveta, just empties it).
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/gavetas/models.py`

### Task 13: Run ruff check on all models

- **ACTION**: Lint all model files at once
- **VALIDATE**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/*/models.py`
- **EXPECT**: Exit 0, no warnings or errors
- **FIX**: If any linting errors, fix them before proceeding to migrations

### Task 14: Generate migrations for all apps

- **ACTION**: Run `makemigrations` for all apps at once
- **COMMAND**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py makemigrations`
- **EXPECT**: `0001_initial.py` created for each app. Django auto-resolves cross-app dependencies.
- **GOTCHA**: If CircularDependencyError occurs (unlikely given our DAG), split the problematic relationship into a separate migration using `makemigrations --empty <app>` and manual `AddField`.
- **VALIDATE**: Check that each app in `backend/apps/*/migrations/` has an `0001_initial.py`

### Task 15: Apply migrations and verify

- **ACTION**: Apply all migrations to the database
- **COMMAND**: `cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py migrate`
- **EXPECT**: All migrations apply successfully with exit 0
- **VALIDATE**:
  1. `uv run python manage.py showmigrations` - all apps show `[X]` (applied)
  2. `uv run python manage.py check --deploy` - no critical issues (warnings about SECRET_KEY/DEBUG are expected in dev)

---

## Testing Strategy

### Validation Approach (no unit tests per PRD decision)

Since tests are explicitly deprioritized in the PRD, validation is done via:

| Validation | Command | Validates |
|------------|---------|-----------|
| Linting | `uv run ruff check apps/*/models.py` | Code style, imports, naming |
| Migration generation | `uv run python manage.py makemigrations --check` | Models are valid |
| Migration apply | `uv run python manage.py migrate` | Schema creation succeeds |
| System check | `uv run python manage.py check` | Django config consistency |
| Shell verification | `uv run python manage.py shell -c "from apps.X.models import Y; print(Y._meta.fields)"` | Model introspection |

### Edge Cases Checklist

- [ ] Usuario model extends AbstractUser correctly (no field conflicts)
- [ ] Cross-app ForeignKeys resolve properly (string references)
- [ ] ManyToMany through table (DemandaMaterial) generates correct join table
- [ ] TextChoices max_length accommodates longest enum value
- [ ] Nullable/blank fields match Spring Boot optional fields
- [ ] `auto_now_add` fields cannot be set manually (by design)
- [ ] `unique=True` on Arquivo.storage_key and Pessoa.cpf generates DB constraints
- [ ] OneToOneField on Cemiterio.endereco enforces single cemetery per address

---

## Validation Commands

### Level 1: STATIC_ANALYSIS

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/*/models.py && uv run ruff format --check apps/*/models.py
```

**EXPECT**: Exit 0, no errors or formatting issues

### Level 2: DJANGO_CHECK

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py check
```

**EXPECT**: System check identified no issues (or only expected warnings)

### Level 3: MIGRATION_GENERATION

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py makemigrations --check --dry-run
```

**EXPECT**: No pending migrations (all already generated)

### Level 4: DATABASE_VALIDATION

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py migrate
```

**EXPECT**: All migrations applied successfully

### Level 5: MODEL_INTROSPECTION

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py shell -c "
from django.apps import apps
for app_config in apps.get_app_configs():
    if app_config.name.startswith('apps.'):
        for model in app_config.get_models():
            fields = [f.name for f in model._meta.get_fields()]
            print(f'{model.__name__}: {fields}')
"
```

**EXPECT**: All 13 models listed with correct fields

---

## Acceptance Criteria

- [ ] All 12 `models.py` files have complete model definitions
- [ ] 6 TextChoices enums defined (Role, SolicitacaoAssunto, SolicitacaoStatus, DemandaStatus, DocumentoTipo, SexoEnum)
- [ ] 13 models defined (Usuario, Endereco, Arquivo, Material, Solicitacao, Demanda, DemandaMaterial, Comentario, Documento, Cemiterio, Jazigo, Pessoa, Gaveta)
- [ ] All cross-app relationships use string references or `settings.AUTH_USER_MODEL`
- [ ] DemandaMaterial through table has UniqueConstraint
- [ ] `makemigrations` generates 0001_initial.py for each app
- [ ] `migrate` runs cleanly with exit 0
- [ ] `ruff check` and `ruff format --check` pass on all model files
- [ ] All field types match Spring Boot entity types (see mapping in each task)

---

## Completion Checklist

- [ ] Tasks 1-12 completed (all models defined)
- [ ] Task 13 passed (ruff linting clean)
- [ ] Task 14 completed (migrations generated)
- [ ] Task 15 completed (migrations applied, verified)
- [ ] Level 1: Static analysis passes
- [ ] Level 2: Django check passes
- [ ] Level 3: No pending migrations
- [ ] Level 4: Database tables created
- [ ] Level 5: Model introspection confirms all fields
- [ ] All acceptance criteria met

---

## Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
| ---- | ---------- | ------ | ---------- |
| CircularDependencyError in migrations | LOW | MEDIUM | All cross-app refs use string format; dependency graph is a DAG with no cycles |
| AbstractUser field conflicts | LOW | HIGH | Only adding new fields (picture_url, provider, ativo, role), not overriding AbstractUser fields |
| auto_now_add prevents manual date setting | LOW | LOW | Acceptable for data/uploaded_at fields; use `default=date.today` if manual setting needed later |
| DecimalField vs Double mismatch in API | LOW | LOW | DRF serializers handle Decimal serialization automatically in Phase 4 |
| CPF unique constraint too strict | MEDIUM | LOW | Matches Spring Boot behavior (existsByCpf check); can relax later if needed |

---

## Notes

**Spring Boot to Django Field Type Mapping:**

| Java/Spring Boot | Django | Notes |
|------------------|--------|-------|
| Long (id) | BigAutoField (auto) | Via DEFAULT_AUTO_FIELD |
| String | CharField / TextField | TextField for `@Column(TEXT)` |
| Boolean | BooleanField | Always specify default |
| Double | FloatField / DecimalField | DecimalField for money |
| Integer | IntegerField / PositiveIntegerField | PositiveIntegerField for quantities |
| LocalDate | DateField | |
| LocalDateTime | DateTimeField | |
| @Enumerated(STRING) | CharField + TextChoices | |
| @ManyToOne | ForeignKey | |
| @OneToOne | OneToOneField | |
| @ManyToMany | ManyToManyField | |
| @ManyToMany (with join entity) | ManyToManyField + through | |
| @PrePersist (date) | auto_now_add=True | |
| @NotBlank | Default (no blank=True) | |
| @NotNull (with optional) | null=True, blank=True | |

**on_delete Strategy:**
- `CASCADE` for child entities that shouldn't survive parent deletion (Demanda->Solicitacao, Comentario->Demanda, Jazigo->Cemiterio, Gaveta->Jazigo)
- `PROTECT` for references that should prevent parent deletion (Solicitacao->Usuario, Solicitacao->Endereco, Demanda->Usuario, DemandaMaterial->Material)
- `SET_NULL` for optional references where unlinking is preferred (Gaveta->Pessoa, Pessoa->Endereco)

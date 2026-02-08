# Feature: File Storage (MinIO) & Document Management with PDF Generation

## Summary

Implement the Arquivos and Documentos Django REST Framework modules, providing file upload/download via MinIO object storage and official document CRUD with PDF generation via WeasyPrint. Both Django apps already have models and migrations from Phase 2; this phase adds serializers, views, URL routing, a MinIO service layer, file validation, and a PDF generation service — all mirroring existing codebase patterns exactly.

## User Story

As a city hall employee (OPERADOR/ADMIN)
I want to upload files, manage official documents (oficios/memorandos), and generate PDFs
So that I can maintain digital records of infrastructure operations and produce official correspondence

## Problem Statement

The Arquivos and Documentos Django apps have only models defined (from Phase 2). There are no API endpoints, no MinIO integration, no file validation, and no PDF generation. Without these, the system cannot handle file attachments for solicitacoes/documentos or produce official PDF documents.

## Solution Statement

Create the full API layer for both modules: a MinIO service class for object storage operations, file validation utilities, DRF serializers/viewsets for Arquivos (upload, download, presigned URLs, metadata CRUD), DRF serializers/viewsets for Documentos (CRUD + PDF download), and a WeasyPrint-based PDF generation service with HTML templates for oficios and memorandos.

## Metadata

| Field            | Value                                                 |
| ---------------- | ----------------------------------------------------- |
| Type             | NEW_CAPABILITY                                        |
| Complexity       | HIGH                                                  |
| Systems Affected | arquivos, documentos, config/urls, static assets      |
| Dependencies     | minio>=7.2.15, weasyprint>=63.1 (already in pyproject.toml) |
| Estimated Tasks  | 11                                                    |

---

## UX Design

### Before State
```
╔═══════════════════════════════════════════════════════════════════╗
║                         BEFORE STATE                             ║
╠═══════════════════════════════════════════════════════════════════╣
║                                                                   ║
║  Arquivo model exists in DB but NO endpoints:                     ║
║  ┌──────────────┐                                                 ║
║  │  POST /upload │ ──> 404 Not Found                              ║
║  │  GET /        │ ──> 404 Not Found                              ║
║  │  GET /{id}    │ ──> 404 Not Found                              ║
║  └──────────────┘                                                 ║
║                                                                   ║
║  Documento model exists in DB but NO endpoints:                   ║
║  ┌──────────────┐                                                 ║
║  │  POST /       │ ──> 404 Not Found                              ║
║  │  GET /{id}/pdf│ ──> 404 Not Found                              ║
║  └──────────────┘                                                 ║
║                                                                   ║
║  MinIO is running (Docker) but Django has no client integration   ║
║  No file validation, no storage key generation, no PDF service    ║
║                                                                   ║
║  PAIN_POINT: Cannot upload files or generate documents            ║
╚═══════════════════════════════════════════════════════════════════╝
```

### After State
```
╔═══════════════════════════════════════════════════════════════════╗
║                          AFTER STATE                              ║
╠═══════════════════════════════════════════════════════════════════╣
║                                                                   ║
║  ┌────────────────┐   ┌──────────────┐   ┌──────────────┐        ║
║  │  User uploads  │──>│ FileValidator │──>│ MinioService │        ║
║  │  multipart file│   │ (type/size)  │   │ (put_object) │        ║
║  └────────────────┘   └──────────────┘   └──────┬───────┘        ║
║                                                  ▼                ║
║                                          ┌──────────────┐        ║
║                                          │  Arquivo DB  │        ║
║                                          │  (metadata)  │        ║
║                                          └──────────────┘        ║
║                                                                   ║
║  Arquivos Endpoints (/api/arquivos/):                             ║
║  • POST /upload      → Upload file to MinIO + save metadata      ║
║  • GET /             → List active files (paginated)              ║
║  • GET /{id}/        → Get file metadata                          ║
║  • GET /{id}/url/    → Get presigned download URL (1hr)           ║
║  • GET /{id}/download/ → Proxy download from MinIO               ║
║  • DELETE /{id}/     → Delete from MinIO + DB                     ║
║                                                                   ║
║  ┌────────────────┐   ┌──────────────┐   ┌──────────────┐        ║
║  │  User creates  │──>│  Documento   │──>│  PDF Service │        ║
║  │  document      │   │  ViewSet     │   │ (WeasyPrint) │        ║
║  └────────────────┘   └──────────────┘   └──────────────┘        ║
║                                                                   ║
║  Documentos Endpoints (/api/documentos/):                         ║
║  • GET /             → List all documents (OPERADOR/ADMIN)        ║
║  • GET /{id}/        → Get document detail with nested anexos     ║
║  • POST /            → Create document with anexo_ids             ║
║  • PATCH /{id}/      → Update document fields + anexos            ║
║  • DELETE /{id}/     → Delete document                            ║
║  • GET /{id}/pdf/    → Download document as PDF (A4, background)  ║
║                                                                   ║
║  VALUE_ADD: Full file storage and official document workflow       ║
╚═══════════════════════════════════════════════════════════════════╝
```

### Interaction Changes
| Location | Before | After | User Impact |
|----------|--------|-------|-------------|
| `/api/arquivos/` | Not registered (404) | Full file CRUD + upload/download | Can upload, list, download, delete files |
| `/api/arquivos/upload/` | N/A | Multipart upload with validation | Files stored in MinIO with metadata |
| `/api/arquivos/{id}/url/` | N/A | Presigned URL (1hr expiry) | Direct MinIO download without proxy |
| `/api/arquivos/{id}/download/` | N/A | Streaming proxy download | Download files through Django |
| `/api/documentos/` | Not registered (404) | Full document CRUD | Can create/manage oficios and memorandos |
| `/api/documentos/{id}/pdf/` | N/A | PDF download | Generate and download official PDF documents |

---

## Mandatory Reading

**CRITICAL: Implementation agent MUST read these files before starting any task:**

| Priority | File | Lines | Why Read This |
|----------|------|-------|---------------|
| P0 | `backend/apps/enderecos/serializers.py` | all | Serializer pattern to MIRROR exactly |
| P0 | `backend/apps/enderecos/views.py` | all | Simple ViewSet pattern to MIRROR |
| P0 | `backend/apps/enderecos/urls.py` | all | URL routing pattern to MIRROR |
| P0 | `backend/apps/demandas/views.py` | all | Complex ViewSet with @action pattern |
| P0 | `backend/apps/solicitacoes/serializers.py` | all | Existing ArquivoSimpleResponseSerializer + ManyToMany pattern |
| P0 | `backend/apps/arquivos/models.py` | all | Existing Arquivo model - DO NOT MODIFY |
| P0 | `backend/apps/documentos/models.py` | all | Existing Documento model - DO NOT MODIFY |
| P1 | `backend/apps/core/exceptions.py` | all | Exception classes and custom handler |
| P1 | `backend/apps/core/permissions.py` | all | Permission classes (IsAllRoles, IsOperadorOrAdmin) |
| P1 | `backend/config/settings/base.py` | 169-189 | MinIO settings and file upload limits |
| P1 | `backend/config/urls.py` | all | URL registration pattern |

**External Documentation:**
| Source | Section | Why Needed |
|--------|---------|------------|
| [minio-py API](https://min.io/docs/minio/linux/developers/python/API.html) | put_object, get_object, presigned_get_object, remove_object | MinIO operations |
| [WeasyPrint API](https://doc.courtbouillon.org/weasyprint/stable/api_reference.html) | HTML.write_pdf() | PDF generation |
| [DRF Parsers](https://www.django-rest-framework.org/api-guide/parsers/) | MultiPartParser | File upload handling |

---

## Patterns to Mirror

**SERIALIZER_CREATE (with FK validation):**
```python
# SOURCE: backend/apps/solicitacoes/serializers.py:47-106
class SolicitacaoCreateSerializer(serializers.Serializer):
    anexo_ids = serializers.ListField(
        child=serializers.IntegerField(),
        required=False,
        default=[],
    )
    def validate_anexo_ids(self, value):
        if value:
            existing = set(Arquivo.objects.filter(pk__in=value).values_list("pk", flat=True))
            missing = set(value) - existing
            if missing:
                raise serializers.ValidationError(f"Arquivos nao encontrados: {missing}")
        return value
```

**SERIALIZER_RESPONSE (ModelSerializer):**
```python
# SOURCE: backend/apps/enderecos/serializers.py:40-48
class EnderecoResponseSerializer(serializers.ModelSerializer):
    class Meta:
        model = Endereco
        fields = ["id", "logradouro", "numero", "bairro", "referencia"]
        read_only_fields = fields
```

**NESTED_SERIALIZER (for Arquivo in responses):**
```python
# SOURCE: backend/apps/solicitacoes/serializers.py:16-33
class ArquivoSimpleResponseSerializer(serializers.ModelSerializer):
    class Meta:
        model = Arquivo
        fields = ["id", "nome_original", "storage_key", "content_type",
                  "tamanho", "categoria", "uploaded_at", "ativo"]
        read_only_fields = fields
```

**VIEWSET_PATTERN (simple CRUD):**
```python
# SOURCE: backend/apps/enderecos/views.py:14-26
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

**VIEWSET_CUSTOM_ACTION (with pagination):**
```python
# SOURCE: backend/apps/demandas/views.py:75-89
@action(detail=False, methods=["get"],
        url_path="solicitacao/(?P<solicitacao_id>[^/.]+)",
        url_name="by-solicitacao")
def by_solicitacao(self, request, solicitacao_id=None):
    queryset = Demanda.objects.filter(solicitacao_id=solicitacao_id).order_by("prazo")
    page = self.paginate_queryset(queryset)
    if page is not None:
        serializer = DemandaResponseSerializer(page, many=True)
        return self.get_paginated_response(serializer.data)
    serializer = DemandaResponseSerializer(queryset, many=True)
    return Response(serializer.data)
```

**URL_ROUTING_PATTERN:**
```python
# SOURCE: backend/apps/enderecos/urls.py:1-10
from rest_framework.routers import DefaultRouter
from apps.enderecos.views import EnderecoViewSet
router = DefaultRouter()
router.register("", EnderecoViewSet, basename="endereco")
urlpatterns = router.urls
```

**EXCEPTION_PATTERN:**
```python
# SOURCE: backend/apps/core/exceptions.py:11-16
class NotFoundException(APIException):
    status_code = status.HTTP_404_NOT_FOUND
    default_detail = "Recurso nao encontrado."
    default_code = "not_found"
```

**ERROR_RESPONSE_FORMAT:**
```python
# SOURCE: backend/apps/core/exceptions.py:78-82
response.data = {
    "status": code,
    "error": _error_label(code),
    "message": message,
}
```

---

## Files to Change

| File | Action | Justification |
|------|--------|---------------|
| `backend/apps/core/exceptions.py` | UPDATE | Add InvalidFileException, StorageException |
| `backend/apps/arquivos/services.py` | CREATE | MinIO client wrapper (upload, download, presigned URLs, delete) |
| `backend/apps/arquivos/validators.py` | CREATE | File type/size/extension validation |
| `backend/apps/arquivos/serializers.py` | CREATE | ArquivoResponseSerializer, FileUrlResponseSerializer |
| `backend/apps/arquivos/views.py` | CREATE | ArquivoViewSet with upload, download, url, delete actions |
| `backend/apps/arquivos/urls.py` | CREATE | DefaultRouter registration |
| `backend/apps/documentos/serializers.py` | CREATE | Create/Update/Response serializers |
| `backend/apps/documentos/services.py` | CREATE | PDF generation with WeasyPrint |
| `backend/apps/documentos/views.py` | CREATE | DocumentoViewSet with pdf action |
| `backend/apps/documentos/urls.py` | CREATE | DefaultRouter registration |
| `backend/config/urls.py` | UPDATE | Register arquivos and documentos URL includes |
| `backend/apps/documentos/templates/documentos/oficio.html` | CREATE | HTML template for oficio PDF |
| `backend/apps/documentos/templates/documentos/memorando.html` | CREATE | HTML template for memorando PDF |
| `backend/static/images/backgroundimage.jpeg` | CREATE | Copy background image from Spring Boot |

---

## NOT Building (Scope Limits)

- **Admin registration (admin.py)** — No existing module has admin.py; not part of the established pattern
- **File category filtering endpoint** — Spring Boot repository had `findByCategoria` but no controller endpoint exposed it
- **Audit logging** — Explicitly excluded in PRD (deferred to post-migration)
- **RabbitMQ notifications on file upload** — Excluded in PRD
- **Test suite** — Explicitly deprioritized by developer in PRD
- **Solicitacao-Arquivo attachment endpoint** — Already handled in solicitacoes module (Phase 5) via `anexo_ids`

---

## Step-by-Step Tasks

Execute in order. Each task is atomic and independently verifiable.

### Task 1: UPDATE `backend/apps/core/exceptions.py` — Add file/storage exceptions

- **ACTION**: ADD `InvalidFileException` (400) and `StorageException` (500) classes; add 413 to error labels
- **IMPLEMENT**:
  ```python
  class InvalidFileException(APIException):
      status_code = status.HTTP_400_BAD_REQUEST
      default_detail = "Arquivo invalido."
      default_code = "invalid_file"

  class StorageException(APIException):
      status_code = status.HTTP_500_INTERNAL_SERVER_ERROR
      default_detail = "Erro no armazenamento."
      default_code = "storage_error"
  ```
  Also add `413: "Payload Too Large"` to `_error_label` dict.
- **MIRROR**: `backend/apps/core/exceptions.py:11-24` — follow existing NotFoundException/ConflictException pattern
- **VALIDATE**: `cd backend && uv run ruff check apps/core/exceptions.py`

### Task 2: CREATE `backend/apps/arquivos/validators.py` — File validation

- **ACTION**: CREATE file validation utility
- **IMPLEMENT**:
  - Constants: `ALLOWED_CONTENT_TYPES` (image/jpeg, image/png, image/gif, application/pdf, application/msword, application/vnd.openxmlformats-officedocument.wordprocessingml.document), `ALLOWED_EXTENSIONS` (jpg, jpeg, png, gif, pdf, doc, docx), `MAX_FILE_SIZE` (10 * 1024 * 1024)
  - `validate_file(uploaded_file)` function that checks:
    1. File is not empty
    2. Filename is not empty, no path traversal (`..`, `/`, `\`, `\0`)
    3. File size <= 10MB
    4. Content type in allowed list
    5. Extension in allowed list (case-insensitive)
  - Raises `InvalidFileException` on any failure (import from `apps.core.exceptions`)
- **MIRROR**: Spring Boot `FileValidator.java` (see exploration report) but as plain functions, not a class
- **GOTCHA**: Django's `UploadedFile.name` may be None for programmatic uploads; handle gracefully
- **VALIDATE**: `cd backend && uv run ruff check apps/arquivos/validators.py`

### Task 3: CREATE `backend/apps/arquivos/services.py` — MinIO service layer

- **ACTION**: CREATE MinIO client service
- **IMPLEMENT**:
  - `get_minio_client()` function that parses `settings.MINIO_ENDPOINT` (strip `http://`/`https://`, detect secure), returns `Minio(endpoint, access_key, secret_key, secure=False/True)`
  - `ensure_bucket()` — check/create bucket using `settings.MINIO_BUCKET_NAME`
  - `generate_storage_key(filename, categoria=None)` — pattern: `[categoria/]yyyy/MM/dd/uuid.ext` (lowercase ext)
  - `upload_file(uploaded_file, storage_key)` — `uploaded_file.seek(0)`, then `client.put_object(bucket, storage_key, uploaded_file, uploaded_file.size, content_type=uploaded_file.content_type)`, wrapped in try/except S3Error raising `StorageException`
  - `download_file(storage_key)` — returns bytes from `client.get_object()` with proper close/release_conn in finally
  - `get_presigned_url(storage_key, expires_minutes=60)` — `client.presigned_get_object(bucket, key, timedelta(minutes=expires_minutes))`
  - `delete_file(storage_key)` — `client.remove_object(bucket, key)`, silently catches S3Error (best effort)
- **IMPORTS**: `from minio import Minio`, `from minio.error import S3Error`, `from django.conf import settings`, `from apps.core.exceptions import StorageException`
- **GOTCHA**: `Minio()` endpoint must NOT include `http://` prefix. Parse from settings which stores full URL. Use `urllib.parse.urlparse` to extract host:port and determine secure.
- **GOTCHA**: `get_object()` response MUST be closed in a `finally` block (both `.close()` and `.release_conn()`)
- **VALIDATE**: `cd backend && uv run ruff check apps/arquivos/services.py`

### Task 4: CREATE `backend/apps/arquivos/serializers.py` — Arquivo DTOs

- **ACTION**: CREATE serializers for Arquivo responses
- **IMPLEMENT**:
  - `ArquivoResponseSerializer(ModelSerializer)` — fields: id, nome_original, storage_key, content_type, tamanho, categoria, uploaded_at, ativo; all read_only
  - `FileUrlResponseSerializer(serializers.Serializer)` — fields: file_id (IntegerField), url (CharField), expires_in_seconds (IntegerField), nome_original (CharField)
- **MIRROR**: `backend/apps/solicitacoes/serializers.py:16-33` (ArquivoSimpleResponseSerializer)
- **NOTE**: The ArquivoResponseSerializer here will be identical to ArquivoSimpleResponseSerializer in solicitacoes. This is intentional — it's the canonical serializer for the arquivos app. Future refactor can consolidate.
- **VALIDATE**: `cd backend && uv run ruff check apps/arquivos/serializers.py`

### Task 5: CREATE `backend/apps/arquivos/views.py` — Arquivo ViewSet

- **ACTION**: CREATE ViewSet with upload, list, retrieve, download URL, proxy download, delete
- **IMPLEMENT**:
  - `ArquivoViewSet(ModelViewSet)`:
    - `queryset = Arquivo.objects.filter(ativo=True).order_by("-uploaded_at")`
    - `permission_classes = [IsAllRoles]`
    - `serializer_class = ArquivoResponseSerializer`
    - `http_method_names = ["get", "post", "delete", "head", "options"]` (no PUT/PATCH for files)
  - Custom actions:
    - `@action(detail=False, methods=["post"], url_path="upload", parser_classes=[MultiPartParser, FormParser])`
      `def upload(self, request)` — get `file` from request.FILES, `categoria` from request.data, validate with `validate_file()`, generate storage key, upload to MinIO, create Arquivo record, return ArquivoResponseSerializer (201)
    - `@action(detail=True, methods=["get"], url_path="url")`
      `def presigned_url(self, request, pk=None)` — get arquivo, generate presigned URL (60 min), return FileUrlResponseSerializer
    - `@action(detail=True, methods=["get"], url_path="download")`
      `def download(self, request, pk=None)` — get arquivo, download bytes from MinIO, return HttpResponse with Content-Disposition: attachment
  - Override `destroy()` — delete from MinIO (best effort), then delete DB record
  - Override `get_queryset()` for `list` action only filter `ativo=True`; `retrieve` allows any
- **MIRROR**: `backend/apps/demandas/views.py:75-89` for @action pattern, `backend/apps/enderecos/views.py` for base pattern
- **IMPORTS**: `from rest_framework.parsers import MultiPartParser, FormParser`, `from django.http import HttpResponse`
- **GOTCHA**: If MinIO upload fails after validation, raise StorageException (no DB record created). If DB save fails after MinIO upload, attempt MinIO cleanup.
- **VALIDATE**: `cd backend && uv run ruff check apps/arquivos/views.py`

### Task 6: CREATE `backend/apps/arquivos/urls.py` — Arquivo URL routing

- **ACTION**: CREATE URL routing for ArquivoViewSet
- **IMPLEMENT**:
  ```python
  from rest_framework.routers import DefaultRouter
  from apps.arquivos.views import ArquivoViewSet
  router = DefaultRouter()
  router.register("", ArquivoViewSet, basename="arquivo")
  urlpatterns = router.urls
  ```
- **MIRROR**: `backend/apps/enderecos/urls.py:1-10` — exact same pattern
- **VALIDATE**: `cd backend && uv run ruff check apps/arquivos/urls.py`

### Task 7: CREATE `backend/apps/documentos/serializers.py` — Documento DTOs

- **ACTION**: CREATE Create, Update, and Response serializers
- **IMPLEMENT**:
  - `DocumentoCreateSerializer(serializers.Serializer)`:
    - Fields: numero (CharField, required=False, allow_blank=True), subject (CharField, required), honorifico (CharField, required=False, allow_blank=True), body (CharField, required), tipo (ChoiceField, DocumentoTipo.choices, required), portaria (CharField, required=False, allow_blank=True), assinante (CharField, required), interessado (CharField, required), destino (CharField, required=False, allow_blank=True), anexo_ids (ListField of IntegerField, required=False, default=[])
    - `validate_anexo_ids()` — same pattern as SolicitacaoCreateSerializer
    - `create()` — create Documento, then `documento.anexos.set(anexo_ids)` if provided
  - `DocumentoUpdateSerializer(serializers.Serializer)`:
    - Same fields as Create but ALL `required=False`
    - `validate_anexo_ids()` — same validation
  - `DocumentoResponseSerializer(ModelSerializer)`:
    - Nested `anexos = ArquivoResponseSerializer(many=True, read_only=True)` (import from apps.arquivos.serializers)
    - Fields: id, numero, data, subject, honorifico, body, tipo, portaria, assinante, interessado, destino, anexos
    - All read_only
- **MIRROR**: `backend/apps/solicitacoes/serializers.py:47-106` for Create pattern, `backend/apps/solicitacoes/serializers.py:121-133` for Response pattern
- **VALIDATE**: `cd backend && uv run ruff check apps/documentos/serializers.py`

### Task 8: COPY background image to Django static directory

- **ACTION**: Create `backend/static/images/` directory and copy `backgroundimage.jpeg`
- **IMPLEMENT**: `mkdir -p backend/static/images/ && cp src/main/resources/static/images/backgroundimage.jpeg backend/static/images/`
- **VALIDATE**: `ls -la backend/static/images/backgroundimage.jpeg`

### Task 9: CREATE `backend/apps/documentos/services.py` — PDF generation with WeasyPrint

- **ACTION**: CREATE PDF generation service
- **IMPLEMENT**:
  - `generate_documento_pdf(documento)` function:
    1. Determine template based on `documento.tipo` (OFICIO or MEMORANDO)
    2. Format date in Portuguese: `"Pau dos Ferros/RN, {day} de {month} de {year}"` for OFICIO, `"{day} de {month} de {year}"` for MEMORANDO
    3. Use a month name lookup dict (Portuguese month names) instead of locale for thread-safety
    4. Build HTML string with inline CSS:
       - Page: A4, margins matching Spring Boot (top ~5cm, bottom ~2.8cm, left ~2.6cm, right ~2.1cm)
       - Background image via `@page { background-image: url(file:///...backgroundimage.jpeg); background-size: 210mm 297mm; }`
       - Font: Liberation Sans (installed in Docker via fonts-liberation) or Arial/Helvetica fallback, 10pt
       - Body text: justified, ~14pt line-height
    5. **OFICIO layout**: Header "OFÍCIO Nº {numero}", date line, "Ao {interessado},", "ASSUNTO: {subject}" bold, honorifico, body, "Atenciosamente,", signature block with line, assinante bold, title, portaria
    6. **MEMORANDO layout**: Header "MEMORANDO Nº {numero}", "DESTINO: {interessado}" bold, date, "ASSUNTO: {subject}" bold, honorifico, body, "Atenciosamente,", signature block, reception table ("Recebido por:___" / "Em(data/horário):___")
    7. Call `HTML(string=html_content, base_url=str(settings.BASE_DIR)).write_pdf()` → returns bytes
    8. On exception: log error, raise StorageException with message
  - Helper: `_format_date_pt_br(date_obj)` — returns formatted date string with Portuguese month names
- **GOTCHA**: Do NOT use `locale.setlocale()` — it's not thread-safe. Use a dict mapping month numbers to Portuguese names.
- **GOTCHA**: WeasyPrint `base_url` must be set so `file:///` URLs for background image resolve correctly. Use `str(settings.BASE_DIR) + "/"` as base_url.
- **GOTCHA**: Background image path must be absolute `file:///` URL or relative to base_url.
- **VALIDATE**: `cd backend && uv run ruff check apps/documentos/services.py`

### Task 10: CREATE `backend/apps/documentos/views.py` — Documento ViewSet

- **ACTION**: CREATE ViewSet with CRUD + PDF download action
- **IMPLEMENT**:
  - `DocumentoViewSet(ModelViewSet)`:
    - `queryset = Documento.objects.all().order_by("id")`
    - `permission_classes = [IsOperadorOrAdmin]`
    - `get_serializer_class()` — Create/Update/Response per action
  - `create()`:
    - Validate, save via serializer.save(), return DocumentoResponseSerializer (201)
  - `partial_update()`:
    - Get object, validate, update fields conditionally (if key in data), handle `anexo_ids` separately (`.set()`), save, return Response
  - `@action(detail=True, methods=["get"], url_path="pdf")`
    `def pdf(self, request, pk=None)`:
    - Get documento
    - Call `generate_documento_pdf(documento)` → bytes
    - Return `HttpResponse(pdf_bytes, content_type="application/pdf")` with `Content-Disposition: attachment; filename="documento_{numero}.pdf"`
- **MIRROR**: `backend/apps/demandas/views.py:34-73` for create + partial_update pattern
- **VALIDATE**: `cd backend && uv run ruff check apps/documentos/views.py`

### Task 11: CREATE `backend/apps/documentos/urls.py` + UPDATE `backend/config/urls.py`

- **ACTION**: CREATE Documento URL routing and register both new apps in root URLs
- **IMPLEMENT**:
  - `backend/apps/documentos/urls.py`:
    ```python
    from rest_framework.routers import DefaultRouter
    from apps.documentos.views import DocumentoViewSet
    router = DefaultRouter()
    router.register("", DocumentoViewSet, basename="documento")
    urlpatterns = router.urls
    ```
  - `backend/config/urls.py`: Add two lines after the `path("api/pessoas/", ...)` line:
    ```python
    path("api/arquivos/", include("apps.arquivos.urls")),
    path("api/documentos/", include("apps.documentos.urls")),
    ```
- **MIRROR**: `backend/apps/enderecos/urls.py` for app urls, `backend/config/urls.py:25-34` for registration
- **VALIDATE**: `cd backend && uv run ruff check config/urls.py apps/documentos/urls.py`

---

## Testing Strategy

### Manual Testing (No automated tests per PRD decision)

| Test Scenario | Endpoint | Expected Result |
|--------------|----------|-----------------|
| Upload valid JPEG | POST /api/arquivos/upload/ | 201 + ArquivoResponseDTO |
| Upload oversized file (>10MB) | POST /api/arquivos/upload/ | 400 Invalid File |
| Upload disallowed type (.exe) | POST /api/arquivos/upload/ | 400 Invalid File |
| Upload with path traversal filename | POST /api/arquivos/upload/ | 400 Invalid File |
| List files | GET /api/arquivos/ | Paginated list of active files |
| Get file metadata | GET /api/arquivos/{id}/ | ArquivoResponseDTO |
| Get presigned URL | GET /api/arquivos/{id}/url/ | FileUrlResponseDTO with URL |
| Download file | GET /api/arquivos/{id}/download/ | Binary file with correct Content-Type |
| Delete file | DELETE /api/arquivos/{id}/ | 204 No Content |
| Create OFICIO document | POST /api/documentos/ | 201 + DocumentoResponseDTO |
| Create MEMORANDO document | POST /api/documentos/ | 201 + DocumentoResponseDTO |
| Update document | PATCH /api/documentos/{id}/ | 200 + updated fields |
| Download OFICIO PDF | GET /api/documentos/{id}/pdf/ | PDF binary with correct layout |
| Download MEMORANDO PDF | GET /api/documentos/{id}/pdf/ | PDF binary with reception table |
| CIDADAO access documentos | Any /api/documentos/ | 403 Forbidden |
| Create document with invalid anexo_ids | POST /api/documentos/ | 400 validation error |

### Edge Cases Checklist

- [ ] Empty file upload (0 bytes)
- [ ] File with no extension
- [ ] File with null/empty filename
- [ ] Filename with special characters (unicode)
- [ ] Upload when MinIO is unreachable
- [ ] Generate PDF for document with empty body
- [ ] Generate PDF for document with very long body text
- [ ] Delete file when MinIO object already deleted
- [ ] Presigned URL for non-existent arquivo
- [ ] Create documento with empty anexo_ids list
- [ ] PATCH documento with only one field changed
- [ ] PDF with missing background image (graceful fallback)

---

## Validation Commands

### Level 1: STATIC_ANALYSIS

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff check apps/arquivos/ apps/documentos/ apps/core/exceptions.py config/urls.py
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run ruff format --check apps/arquivos/ apps/documentos/ apps/core/exceptions.py config/urls.py
```

**EXPECT**: Exit 0, no errors or warnings

### Level 2: IMPORT_CHECK

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python -c "
from apps.arquivos.serializers import ArquivoResponseSerializer, FileUrlResponseSerializer
from apps.arquivos.views import ArquivoViewSet
from apps.arquivos.services import upload_file, download_file, get_presigned_url, delete_file, generate_storage_key
from apps.arquivos.validators import validate_file
from apps.documentos.serializers import DocumentoCreateSerializer, DocumentoUpdateSerializer, DocumentoResponseSerializer
from apps.documentos.views import DocumentoViewSet
from apps.documentos.services import generate_documento_pdf
from apps.core.exceptions import InvalidFileException, StorageException
print('All imports successful')
"
```

**EXPECT**: "All imports successful"

### Level 3: DJANGO_CHECK

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py check --settings=config.settings.local
```

**EXPECT**: System check identified no issues

### Level 4: URL_VERIFICATION

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && uv run python manage.py show_urls --settings=config.settings.local 2>/dev/null | grep -E "arquivo|documento" || uv run python -c "
import django; django.setup()
from django.urls import reverse
print(reverse('arquivo-list'))
print(reverse('arquivo-upload'))
print(reverse('documento-list'))
"
```

**EXPECT**: URLs for both modules are registered

### Level 5: DOCKER_VALIDATION (if Docker available)

```bash
cd /home/joelfmjr/ifrn/curso/integrador/sigesi/backend && docker-compose up -d && sleep 10
# Test health
curl -s http://localhost:8000/api/health/ | python -m json.tool
# Test Swagger includes new endpoints
curl -s http://localhost:8000/api/schema/ | python -c "import sys, json; schema=json.load(sys.stdin); paths=[p for p in schema['paths'] if 'arquivo' in p or 'documento' in p]; print(f'Found {len(paths)} endpoints:', paths)"
```

### Level 6: MANUAL_VALIDATION

1. Start Docker Compose: `cd backend && docker-compose up -d`
2. Access Swagger UI: `http://localhost:8000/api/schema/swagger-ui/`
3. Authenticate via Google OAuth2
4. Upload a test file via POST /api/arquivos/upload/
5. List files via GET /api/arquivos/
6. Get presigned URL via GET /api/arquivos/{id}/url/
7. Download file via GET /api/arquivos/{id}/download/
8. Create an OFICIO document via POST /api/documentos/
9. Download PDF via GET /api/documentos/{id}/pdf/
10. Verify PDF layout matches expected format (background image, margins, Portuguese date)

---

## Acceptance Criteria

- [ ] All 6 Arquivo endpoints responding correctly (upload, list, get, url, download, delete)
- [ ] All 6 Documento endpoints responding correctly (list, get, create, update, delete, pdf)
- [ ] File validation rejects invalid types, sizes, and filenames
- [ ] Files stored in MinIO with date-based hierarchical key structure
- [ ] Presigned URLs valid for 1 hour
- [ ] PDF generation produces valid A4 PDFs with background image
- [ ] OFICIO and MEMORANDO have distinct layouts
- [ ] Portuguese date formatting in PDFs (thread-safe, no locale.setlocale)
- [ ] Role-based access: Documentos restricted to OPERADOR/ADMIN; Arquivos open to all authenticated
- [ ] Error responses follow standardized format: `{"status": code, "error": "type", "message": "detail"}`
- [ ] Static analysis (ruff) passes with no errors
- [ ] Django system check passes

---

## Completion Checklist

- [ ] Task 1: Core exceptions updated (InvalidFileException, StorageException)
- [ ] Task 2: File validator created
- [ ] Task 3: MinIO service layer created
- [ ] Task 4: Arquivo serializers created
- [ ] Task 5: Arquivo ViewSet created
- [ ] Task 6: Arquivo URL routing created
- [ ] Task 7: Documento serializers created
- [ ] Task 8: Background image copied to static/
- [ ] Task 9: PDF generation service created
- [ ] Task 10: Documento ViewSet created
- [ ] Task 11: Documento URLs + root URL registration
- [ ] Level 1 validation: ruff check + format passes
- [ ] Level 2 validation: All imports successful
- [ ] Level 3 validation: Django check passes

---

## Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| MinIO endpoint parsing (http:// prefix) | HIGH | HIGH | Use `urllib.parse.urlparse` to strip scheme from settings.MINIO_ENDPOINT and detect secure |
| WeasyPrint background image not covering margins | MEDIUM | LOW | Use `background-size: 210mm 297mm` instead of `cover`; set page margin: 0 with inner container padding |
| Portuguese date formatting thread-safety | HIGH | HIGH | Use dict-based month name lookup, NOT locale.setlocale() |
| `get_object()` connection leak | HIGH | HIGH | Always close/release_conn in `finally` block |
| Large file upload memory issues | LOW | MEDIUM | Django automatically uses temp files for uploads >FILE_UPLOAD_MAX_MEMORY_SIZE (10MB threshold) |
| WeasyPrint missing system deps locally | MEDIUM | LOW | Dockerfile already installs deps; local dev may need manual install or Docker-only testing |

---

## Notes

- The `ArquivoResponseSerializer` in `apps/arquivos/serializers.py` will be identical to `ArquivoSimpleResponseSerializer` in `apps/solicitacoes/serializers.py`. This duplication is intentional — each app owns its serializers. A future refactor could consolidate, but for now follow the convention of app-local serializers.
- The `Arquivo.ordering = ["-uploaded_at"]` in the model's Meta class means default ordering is newest-first. The ViewSet queryset should respect this.
- The `Documento.data` field uses `auto_now_add=True`, so it's automatically set on creation and cannot be updated. The Spring Boot version also had `@PrePersist` to set date on creation.
- PDF templates use inline HTML/CSS strings (not Django template files) to keep the service self-contained and avoid template directory configuration. This matches the Spring Boot approach where PDF layout was entirely in Java code.
- The `MINIO_ENDPOINT` in settings includes the `http://` prefix (e.g., `http://minio:9000`). The minio-py client requires just `host:port`. The service must parse this.

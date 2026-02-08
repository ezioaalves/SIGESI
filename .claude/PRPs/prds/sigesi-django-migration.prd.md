# SIGESI: Spring Boot to Django REST Framework Migration

## Problem Statement

A solo backend developer maintaining SIGESI (infrastructure management system for a city hall) is blocked by an unfamiliar tech stack. The project was built in Spring Boot/Java as a university requirement, but the developer's expertise is in Python/Django. As the project transitions from academic to production use for the city hall, continued development in Spring Boot means slower feature delivery, lower confidence in changes, and higher maintenance burden.

## Evidence

- Developer self-reports: not a Spring Boot developer, struggles with complex problems in Java
- Project was built in Spring Boot solely due to university requirement, not technical merit
- Developer has "vast experience" in Django - the productivity delta is significant
- Project is transitioning from academic to real-world product for city hall - velocity matters now

## Proposed Solution

Rewrite the SIGESI backend in Django REST Framework, preserving all core domain functionality (service requests, work demands, cemetery management, file storage, document generation) while dropping non-essential features (audit logging, RabbitMQ notifications). The API will follow Python conventions (snake_case) rather than maintaining exact Spring Boot response formats. No frontend changes, no data migration - clean start.

## Key Hypothesis

We believe migrating to Django REST Framework will enable faster feature development and more confident maintenance for a solo developer. We'll know we're right when new features can be shipped without fighting the framework.

## What We're NOT Building

- **RabbitMQ notification system** - deferred, not needed for initial launch
- **Hibernate Envers audit logging** - deferred, can add django-auditlog later
- **Frontend** - completely out of scope, separate project
- **Data migration scripts** - fresh database, no data to migrate
- **Exact API response format parity** - responses will follow Python/DRF conventions

## Success Metrics

| Metric | Target | How Measured |
|--------|--------|--------------|
| API feature parity | All must-have modules functional | Manual testing of all endpoints |
| Developer confidence | Can modify any module without fear | Subjective - developer reports |
| Build & run simplicity | Single command to start | `docker-compose up` works |
| All endpoints accessible | 70+ endpoints responding | Swagger UI / manual verification |

## Open Questions

- [ ] Which django-allauth version best supports the current Google OAuth2 flow with the React frontend?
- [ ] Should WeasyPrint or ReportLab be used for PDF generation? (WeasyPrint recommended for template-based approach)
- [ ] Will the existing Docker Compose setup be adapted or rewritten from scratch?
- [ ] Should django-admin be configured for internal city hall use, or is it out of scope?

---

## Users & Context

**Primary User (Developer)**
- **Who**: Solo backend developer with deep Python/Django expertise
- **Current behavior**: Struggling to implement features and fixes in Spring Boot
- **Trigger**: Project transitioning from university assignment to city hall product
- **Success state**: Can develop, debug, and ship features confidently in Django

**End Users (Unchanged)**
- City hall employees (OPERADOR, AGENTE, ADMIN roles) managing infrastructure requests and cemetery operations
- Citizens (CIDADAO role) filing service requests
- End users are unaffected by the migration - they interact via the frontend

**Job to Be Done**
When we get back to development to add new features and fix/improve the current ones, I want to implement them all in Django, so I can do it faster, with more confidence.

**Non-Users**
- Frontend developers (frontend is out of scope)
- Database administrators (no data migration needed)
- End users don't interact with the backend directly

---

## Solution Detail

### Core Capabilities (MoSCoW)

| Priority | Capability | Rationale |
|----------|------------|-----------|
| Must | Django project structure with Docker | Foundation for everything else |
| Must | PostgreSQL models for all entities | Data layer is core |
| Must | OAuth2 authentication with Google | Required for all access |
| Must | Role-based access control (CIDADAO, OPERADOR, AGENTE, ADMIN) | Security requirement |
| Must | Solicitacoes module (CRUD + role filtering) | Core domain - citizen requests |
| Must | Demandas module (CRUD + material tracking) | Core domain - work management |
| Must | Cemetery modules (Cemiterios, Jazigos, Gavetas) | Core domain - cemetery ops |
| Must | Pessoas module with filtering | Supports cemetery module |
| Must | Enderecos module | Shared dependency for multiple modules |
| Must | Materiais module | Supports demandas |
| Must | Comentarios module | Supports demandas |
| Must | Usuarios module with /me endpoint | User management |
| Must | MinIO file storage (upload, download, presigned URLs) | File attachments |
| Must | Documentos module with PDF generation | Official document creation |
| Must | Global exception handling | API consistency |
| Must | OpenAPI/Swagger documentation | API discoverability |
| Should | django-filter for dynamic query filtering | Better API usability |
| Should | CORS configuration | Frontend integration |
| Won't | RabbitMQ/Celery notifications | Deferred to post-migration |
| Won't | Audit logging (django-auditlog) | Deferred to post-migration |
| Won't | Test suite | Explicitly deprioritized by developer |

### MVP Scope

All Must-have capabilities above, running in Docker with PostgreSQL and MinIO. A developer can start the system, authenticate via Google, and exercise all CRUD endpoints through Swagger UI.

### User Flow

1. Developer runs `docker-compose up`
2. System starts: Django + PostgreSQL + MinIO
3. User accesses API, redirected to Google OAuth2
4. On success, user gets session with role-based permissions
5. User interacts with endpoints based on their role

---

## Technical Approach

**Feasibility**: HIGH

Every Spring Boot component has a mature, well-documented Django equivalent. The developer has deep Django expertise.

**Architecture Notes**

| Spring Boot | Django Equivalent | Notes |
|-------------|-------------------|-------|
| Spring Data JPA | Django ORM | Built-in, models.py |
| MapStruct DTOs | DRF Serializers | Less boilerplate, built into DRF |
| Spring Security OAuth2 | django-allauth | Google provider, well-documented |
| JPA Repositories | Django Managers/QuerySets | Built-in |
| JPA Specifications | django-filter | Declarative filtering |
| @ControllerAdvice | Custom DRF exception handler | Single function in settings |
| MinIO Java client | minio-py / django-minio-backend | Direct equivalent |
| OpenPDF | WeasyPrint | HTML templates to PDF (cleaner) |
| Bean Validation | DRF Validators | Built-in to serializers |
| @Transactional | @transaction.atomic | Built-in |
| SpringDoc OpenAPI | drf-spectacular | Auto-generated schema |
| Checkstyle | ruff / black | Python linting & formatting |
| application.properties | django-environ + settings.py | Environment-driven config |
| Maven | uv | Fast Python package management, lockfile support |

**Tech Stack**

```
Python 3.12+
Python 3.12+
Django 5.2.x
djangorestframework 3.15.x
django-allauth (Google OAuth2)
django-filter (dynamic filtering)
django-cors-headers (CORS)
drf-spectacular (OpenAPI/Swagger)
django-environ (environment variables)
minio (Python MinIO client)
WeasyPrint (PDF generation)
psycopg2-binary (PostgreSQL)
gunicorn (production server)
uv (package management)
ruff (linting + formatting)
Docker + docker-compose
```

**Technical Risks**

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| OAuth2 flow differences with frontend | Medium | Test auth flow early in Phase 3, coordinate with frontend if needed |
| WeasyPrint system dependencies in Docker | Low | Use official WeasyPrint Docker base image or install deps in Dockerfile |
| MinIO presigned URL behavior differences | Low | minio-py is the official client, same API |
| PDF layout differences from OpenPDF | Medium | Accept visual differences, focus on content parity |

---

## Implementation Phases

<!--
  STATUS: pending | in-progress | complete
  PARALLEL: phases that can run concurrently
  DEPENDS: phases that must complete first
  PRP: link to generated plan file once created
-->

| # | Phase | Description | Status | Parallel | Depends | PRP Plan |
|---|-------|-------------|--------|----------|---------|----------|
| 1 | Project Setup & Docker | Django project, settings, Docker Compose, PostgreSQL, env vars | in-progress | - | - | `.claude/PRPs/plans/django-migration-phase1-project-setup.plan.md` |
| 2 | Data Models | All Django models, relationships, enums, migrations | pending | - | 1 | - |
| 3 | Authentication & Authorization | Google OAuth2 via django-allauth, role-based permissions | pending | - | 2 | - |
| 4 | Base Modules API | Enderecos, Usuarios, Materiais - shared/foundation endpoints | pending | - | 3 | - |
| 5 | Solicitacoes & Demandas API | Service requests, work demands, comments, demand-materials | pending | - | 4 | - |
| 6 | Cemetery Management API | Cemiterios, Jazigos, Gavetas, Pessoas with filtering | pending | with 5 | 4 | - |
| 7 | File Storage & Documents | MinIO integration, file upload/download, PDF generation | pending | - | 4 | - |
| 8 | Polish & Integration | Global exception handler, CORS, OpenAPI docs, final testing | pending | - | 5, 6, 7 | - |

### Phase Details

**Phase 1: Project Setup & Docker**
- **Goal**: Running Django project in Docker with PostgreSQL and MinIO
- **Scope**:
  - Django project structure (sigesi_project with apps)
  - settings.py with django-environ for env vars
  - Dockerfile for Django app
  - docker-compose.yml with PostgreSQL, MinIO, and Django services
  - pyproject.toml with uv for dependency management
  - Basic health check endpoint
- **Success signal**: `docker-compose up` starts all services, Django responds on port 8000

**Phase 2: Data Models**
- **Goal**: All entities defined as Django models with proper relationships
- **Scope**:
  - Django apps: usuarios, solicitacoes, demandas, materiais, comentarios, documentos, arquivos, enderecos, cemiterios, jazigos, gavetas, pessoas
  - All model fields matching Spring Boot entities (with Python naming conventions)
  - Enums as Django TextChoices
  - ForeignKey, OneToOneField, ManyToManyField relationships
  - Initial migrations generated and applied
- **Success signal**: `python manage.py migrate` runs cleanly, all tables created

**Phase 3: Authentication & Authorization**
- **Goal**: Google OAuth2 login working with role-based access control
- **Scope**:
  - django-allauth configuration for Google OAuth2
  - Custom user model or adapter for role assignment
  - Admin user auto-creation from ADMIN_EMAIL
  - Permission classes for role-based endpoint access
  - Login/logout endpoints
  - OAuth2 redirect handling (success/failure URLs)
- **Success signal**: Can login via Google, receive session, access role-restricted endpoints

**Phase 4: Base Modules API**
- **Goal**: Foundation endpoints that other modules depend on
- **Scope**:
  - Enderecos: full CRUD with DRF serializers and viewsets
  - Usuarios: list, get, /me, toggle-ativo, update role (ADMIN only)
  - Materiais: full CRUD
  - Establish patterns: serializers, viewsets, URL routing, permissions
- **Success signal**: All base endpoints responding correctly in Swagger UI

**Phase 5: Solicitacoes & Demandas API**
- **Goal**: Core infrastructure request workflow functional
- **Scope**:
  - Solicitacoes: CRUD with role-based filtering (CIDADAO sees own only)
  - Demandas: CRUD with material tracking (DemandaMaterial join)
  - Comentarios: CRUD scoped to demandas
  - DemandaMaterial: managed through Demanda endpoints
  - Status enum transitions
- **Success signal**: Full request-to-demand workflow exercisable through API

**Phase 6: Cemetery Management API**
- **Goal**: Cemetery operations module functional
- **Scope**:
  - Cemiterios: CRUD with address (OPERADOR, ADMIN only)
  - Jazigos: CRUD linked to cemeteries
  - Gavetas: CRUD with filtering (jazigoId, ocupanteId)
  - Pessoas: CRUD with advanced filtering (nome, cpf, sexo, enderecoId)
  - PessoaFilter using django-filter
- **Success signal**: Full cemetery management workflow through API

**Phase 7: File Storage & Documents**
- **Goal**: File upload/download and PDF document generation working
- **Scope**:
  - MinIO service: upload, download, presigned URLs, delete
  - Arquivos: multipart upload, metadata CRUD, presigned URL endpoint
  - FileValidator: type and size validation
  - Documentos: CRUD for oficios and memorandos
  - PDF generation with WeasyPrint (oficio and memorando templates)
- **Success signal**: Can upload files to MinIO, attach to entities, generate and download PDFs

**Phase 8: Polish & Integration**
- **Goal**: Production-ready API with proper error handling and documentation
- **Scope**:
  - Custom DRF exception handler (standardized error responses)
  - CORS configuration via django-cors-headers
  - drf-spectacular OpenAPI schema generation
  - Swagger UI available at /api/docs/
  - Final endpoint verification against Spring Boot feature list
  - Docker Compose health checks
- **Success signal**: All 70+ endpoints working, Swagger UI complete, Docker Compose stable

### Parallelism Notes

Phases 5 and 6 can run in parallel as they touch different domains (infrastructure requests vs cemetery management). Both depend on Phase 4 (base modules) being complete. Phase 7 (files & documents) is independent of 5 and 6 but depends on base patterns from Phase 4.

---

## Decisions Log

| Decision | Choice | Alternatives | Rationale |
|----------|--------|--------------|-----------|
| OAuth2 library | django-allauth | social-auth-app-django, custom | Most comprehensive, best Google provider support, actively maintained |
| PDF generation | WeasyPrint | ReportLab, xhtml2pdf | HTML template approach is cleaner and more maintainable than programmatic PDF |
| API response format | Python snake_case | Keep Java camelCase | Following Python conventions, frontend can adapt |
| MinIO client | minio-py | django-minio-backend, django-storages | Direct client gives more control, matches existing MinIO setup |
| Filtering | django-filter | Manual query params | Declarative, integrates with DRF, replaces JPA Specifications cleanly |
| API docs | drf-spectacular | drf-yasg | More actively maintained, better OpenAPI 3.0 support |
| Environment vars | django-environ | python-decouple, python-dotenv | Django-specific, clean .env file support |
| Drop audit logging | Yes | Migrate with django-auditlog | Reduces scope, can add later with minimal effort |
| Drop RabbitMQ | Yes | Migrate with Celery | Reduces scope and infrastructure complexity, not needed for MVP |
| Fresh database | Yes | Migrate existing data | No production data exists yet, clean start is simpler |
| Code quality | ruff | flake8 + black + isort | Single tool replaces multiple, faster, Python community standard |
| Package manager | uv | pip + pip-tools, poetry | Fastest Python package manager, lockfile by default, from Astral (ruff creators) |

---

## Research Summary

**Market Context**
- Spring Boot to Django migrations are common when teams have stronger Python expertise
- Django ecosystem provides mature equivalents for every Spring Boot feature used in SIGESI
- No established "migration guide" exists, but component-by-component mapping is well-documented
- Django's built-in admin, ORM, and DRF's serializers significantly reduce boilerplate vs Spring Boot

**Technical Context**
- Current codebase: 131 source files, 86 endpoints, 19 modules
- Django equivalent will be significantly smaller due to less boilerplate (no MapStruct, no repository interfaces, no Lombok)
- All external integrations (PostgreSQL, MinIO, Google OAuth2) have Python clients
- Docker setup can be simplified (single Dockerfile vs multi-stage Java build)
- Estimated reduction: ~40-50% fewer lines of code for equivalent functionality

---

*Generated: 2026-02-07*
*Status: DRAFT - needs validation*

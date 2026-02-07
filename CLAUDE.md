# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SIGESI (Sistema de Gerenciamento da Secretaria de Infraestrutura) is a Spring Boot 3.5.9 application using Java 21. The project is a comprehensive infrastructure management system that handles citizen service requests (solicitacoes), work demands, cemetery management, official document generation, and file storage. It implements OAuth2 authentication with Google, role-based access control, audit logging via Hibernate Envers, and is configured with strict code quality standards via Checkstyle.

## Development Commands

### Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Run with dev tools (hot reload)
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

### Code Quality
```bash
# Run Checkstyle validation (automatically runs during validate phase)
mvn checkstyle:check

# Run tests
mvn test

# Run a specific test class
mvn test -Dtest=SigesiApplicationTests

# Run a specific test method
mvn test -Dtest=ClassName#methodName
```

### Environment Setup
The application uses a `.env` file for environment variable management (loaded automatically via spring-dotenv):

```bash
# Copy the example file
cp .env.example .env

# Required environment variables:
#   GOOGLE_CLIENT_ID        - Google OAuth2 client ID
#   GOOGLE_CLIENT_SECRET    - Google OAuth2 client secret
#   DATABASE_URL            - PostgreSQL connection URL (e.g., jdbc:postgresql://localhost:5432/sigesi)
#   DATABASE_USER           - Database username
#   DATABASE_PASSWORD       - Database password
#   MINIO_ENDPOINT          - MinIO endpoint (default: http://minio:9000)
#   MINIO_ACCESS_KEY        - MinIO access key
#   MINIO_SECRET_KEY        - MinIO secret key
#   MINIO_BUCKET_NAME       - MinIO bucket name (default: sigesi-files)
#   ADMIN_EMAIL             - Email for the auto-created admin user
#   RABBITMQ_HOST           - RabbitMQ host (default: localhost)
#   RABBITMQ_PORT           - RabbitMQ port (default: 5672)
#   RABBITMQ_USERNAME       - RabbitMQ username (default: guest)
#   RABBITMQ_PASSWORD       - RabbitMQ password (default: guest)
#   OAUTH2_SUCCESS_REDIRECT - Post-login redirect URL (default: http://localhost:3000)
#   OAUTH2_FAILURE_REDIRECT - Auth failure redirect URL (default: http://localhost:3000/login?error=true)
#   OAUTH2_LOGOUT_REDIRECT  - Post-logout redirect URL (default: http://localhost:3000)
```

**Important Notes:**
- The `.env` file is already in `.gitignore` to prevent credential leaks
- Environment variables are loaded via `spring-dotenv` (replaces the old `dotenv-java` approach)
- If `.env` is missing, the application will continue (for production deployments using system environment variables)

## Architecture

### Package Structure
The application uses package-by-feature organization under `com.sigesi.sigesi`:

**Core Application:**
- `SigesiApplication.java` - Main application entry point

**Configuration (`config` package):**
- `SpringConfig.java` - Security, OAuth2, CORS, and role-based access control configuration
- `OAuth2LoginSuccessHandler.java` - Post-login handler for OAuth2 authentication
- `CustomOidcUserService.java` - Custom OIDC user service for OAuth2 integration
- `CustomOAuth2User.java` - Custom OAuth2 user principal with role support
- `AdminUserInitializer.java` - Auto-creates admin user on startup from `ADMIN_EMAIL`
- `MinioConfig.java` - MinIO client bean configuration
- `RabbitMQConfig.java` - RabbitMQ exchanges, queues, and bindings
- `GlobalExceptionHandler.java` - Centralized exception handling with `@ControllerAdvice`
- `EnumExceptionHandler.java` - Enum validation error handling

**Solicitacoes Module (`solicitacoes` package):**
- `Solicitacao.java` - Service request entity (citizen-reported issues)
- `SolicitacaoController.java` - REST controller for request management
- `SolicitacaoService.java` - Business logic with role-based filtering
- `SolicitacaoRepository.java` - JPA repository
- `SolicitacaoMapper.java` - MapStruct mapper
- `SolicitacaoAssunto.java` - Enum: BURACO, ESGOTO, ILUMINACAO, LIMPEZA, OUTROS
- `SolicitacaoStatus.java` - Enum: ABERTA, EM_ANDAMENTO, CONCLUIDA, ENCERRADA, REJEITADA
- `dtos/` - SolicitacaoCreateDTO, SolicitacaoUpdateDTO, SolicitacaoResponseDTO

**Demandas Module (`demandas` package):**
- `Demanda.java` - Work demand entity derived from requests
- `DemandaController.java` - REST controller for demand management
- `DemandaService.java` - Business logic for demand operations
- `DemandaRepository.java` - JPA repository
- `DemandaMapper.java` - MapStruct mapper
- `DemandaStatus.java` - Enum: PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA
- `dtos/` - DemandaCreateDTO, DemandaUpdateDTO, DemandaResponseDTO

**DemandaMaterial Module (`demandamaterial` package):**
- `DemandaMaterial.java` - Join entity linking demands to materials with quantity
- `DemandaMaterialController.java` - REST controller
- `DemandaMaterialService.java` - Business logic
- `DemandaMaterialRepository.java` - JPA repository

**Materiais Module (`materiais` package):**
- `Material.java` - Materials catalog entity with name and price
- `MaterialController.java` - REST controller for materials CRUD
- `MaterialService.java` - Business logic
- `MaterialRepository.java` - JPA repository
- `MaterialMapper.java` - MapStruct mapper
- `dtos/` - MaterialCreateDTO, MaterialUpdateDTO, MaterialResponseDTO

**Comentarios Module (`comentarios` package):**
- `Comentario.java` - Comments on demands
- `ComentarioController.java` - REST controller
- `ComentarioService.java` - Business logic
- `ComentarioRepository.java` - JPA repository
- `ComentarioMapper.java` - MapStruct mapper
- `dtos/` - ComentarioCreateDTO, ComentarioResponseDTO

**Documentos Module (`documentos` package):**
- `Documento.java` - Official document entity (oficios, memorandos)
- `DocumentoController.java` - REST controller with PDF download endpoint
- `DocumentoService.java` - Business logic
- `DocumentoPdfService.java` - PDF generation using OpenPDF
- `DocumentoRepository.java` - JPA repository
- `DocumentoMapper.java` - MapStruct mapper
- `DocumentoTipo.java` - Enum: OFICIO, MEMORANDO
- `dtos/` - DocumentoCreateDTO, DocumentoUpdateDTO, DocumentoResponseDTO

**Arquivos Module (`arquivos` package):**
- `Arquivo.java` - File metadata entity stored with MinIO
- `ArquivoController.java` - REST controller for upload/download
- `ArquivoService.java` - Business logic for file management
- `ArquivoRepository.java` - JPA repository
- `ArquivoMapper.java` - MapStruct mapper
- `FileValidator.java` - File type/size validation
- `dtos/` - ArquivoResponseDTO, FileUrlResponseDTO

**Storage Module (`storage` package):**
- `MinioService.java` - MinIO integration for upload, download, presigned URLs, and deletion

**Auditoria Module (`auditoria` package):**
- `GenericAuditService.java` - Generic audit service using Hibernate Envers
- `AuditoriaController.java` - REST controller for revision history
- `UsuarioRevisionEntity.java` - Custom revision entity with user info
- `UsuarioRevisionListener.java` - Captures current user on each revision

**Notifications Module (`notifications` package):**
- `NotificationPublisher.java` - RabbitMQ event publisher for demand assignments and status changes

**Usuarios Module (`usuarios` package):**
- `Usuario.java` - Entity with role-based access (CIDADAO, OPERADOR, AGENTE, ADMIN)
- `UsuarioController.java` - REST controller with `/me` endpoint
- `UsuarioService.java` - Business logic with OAuth2 post-login processing
- `UsuarioRepository.java` - JPA repository with custom queries
- `Role.java` - Enum: CIDADAO, OPERADOR, AGENTE, ADMIN
- `dtos/` - UsuarioUpdateDTO

**Enderecos Module (`enderecos` package):**
- `Endereco.java` - Address entity
- `EnderecoController.java` - REST controller with full CRUD using DTOs
- `EnderecoService.java` - Business logic
- `EnderecoRepository.java` - JPA repository
- `EnderecoMapper.java` - MapStruct mapper
- `dtos/` - EnderecoCreateDTO, EnderecoUpdateDTO, EnderecoResponseDTO

**Cemiterios Module (`cemiterios` package):**
- `Cemiterio.java` - Cemetery entity
- `CemiterioController.java` - REST controller
- `CemiterioService.java` - Business logic
- `CemiterioRepository.java` - JPA repository
- `CemiterioMapper.java` - MapStruct mapper
- `dtos/` - CemiterioCreateDTO, CemiterioUpdateDTO, CemiterioResponseDTO

**Jazigos Module (`jazigos` package):**
- `Jazigo.java` - Burial plot entity
- `JazigoController.java` - REST controller
- `JazigoService.java` - Business logic
- `JazigoRepository.java` - JPA repository
- `JazigoMapper.java` - MapStruct mapper
- `dtos/` - JazigoCreateDTO, JazigoUpdateDTO, JazigoResponseDTO

**Gavetas Module (`gavetas` package):**
- `Gaveta.java` - Burial space entity
- `GavetaController.java` - REST controller
- `GavetaService.java` - Business logic
- `GavetaRepository.java` - JPA repository
- `GavetaMapper.java` - MapStruct mapper
- `dtos/` - GavetaCreateDTO, GavetaUpdateDTO, GavetaResponseDTO

**Pessoas Module (`pessoas` package):**
- `Pessoa.java` - Person entity with SexoEnum
- `PessoaController.java` - REST controller
- `PessoaService.java` - Business logic
- `PessoaRepository.java` - JPA repository
- `PessoaMapper.java` - MapStruct mapper
- `SexoEnum.java` - Enum: MASCULINO, FEMININO, OUTRO
- `dtos/` - PessoaCreateDTO, PessoaUpdateDTO, PessoaResponseDTO

**Custom Exceptions (`exceptions` package):**
- `NotFoundException.java` - 404 resource not found
- `ConflictException.java` - 409 conflict
- `InvalidFileException.java` - File validation errors
- `StorageException.java` - MinIO/storage errors

### Security Configuration

OAuth2 with Google is configured in `SpringConfig.java` with role-based access control:

**Roles (hierarchical):**
- `CIDADAO` - Default role for new users (citizen)
- `OPERADOR` - Operator with access to cemetery and document modules
- `AGENTE` - Field agent
- `ADMIN` - Full access including user management

**Endpoint Permissions:**
- `/api/enderecos/**` - All authenticated roles
- `/api/solicitacoes/**` - All authenticated roles
- `/api/cemiterios/**`, `/api/jazigos/**`, `/api/gavetas/**` - OPERADOR, ADMIN
- `/api/documentos/**` - OPERADOR, ADMIN
- `/api/usuarios/me` - All authenticated users
- `/api/usuarios/**` - ADMIN only
- `/api/auth/logout` - All authenticated users

**Authentication Flow:**
1. User accesses any protected endpoint
2. Redirected to Google OAuth2 login
3. `CustomOidcUserService` processes the OIDC response
4. `OAuth2LoginSuccessHandler` calls `UsuarioService.processOAuthPostLogin()`:
   - Checks if user exists by email
   - Updates existing user info OR creates new user with `ativo=true` and `role=CIDADAO`
   - If email matches `ADMIN_EMAIL`, sets role to ADMIN
5. User redirected to `OAUTH2_SUCCESS_REDIRECT` URL

### Database

**PostgreSQL with JPA/Hibernate:**
- Connection details loaded from environment variables: `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD`
- Hibernate DDL auto mode: `update` (automatically updates schema)
- SQL logging enabled with formatted output
- H2 in-memory database used for tests

**Entity Relationships:**
- `Solicitacao` has a `ManyToOne` with `Usuario` (autor) and `Endereco` (local), `ManyToMany` with `Arquivo` (anexos)
- `Demanda` has a `ManyToOne` with `Solicitacao` and `Usuario` (responsavel), `OneToMany` with `DemandaMaterial`
- `DemandaMaterial` is a join entity between `Demanda` and `Material` with quantity
- `Comentario` has a `ManyToOne` with `Demanda` and `Usuario` (autor)
- `Documento` has a `ManyToMany` with `Arquivo` (anexos)
- `Cemiterio` has a `OneToOne` with `Endereco`
- `Jazigo` has a `ManyToOne` with `Cemiterio`
- `Gaveta` has a `ManyToOne` with `Jazigo` and `Pessoa` (ocupante)
- `Pessoa` has a `ManyToOne` with `Endereco`

**Hibernate Envers Auditing:**
- Entities annotated with `@Audited` are tracked: Solicitacao, Demanda, DemandaMaterial, Material, Documento, Arquivo, Endereco, Usuario
- Custom `UsuarioRevisionEntity` captures user name, email, and timestamp per revision
- Revision history accessible via `/api/audit/revisions` endpoint

### API Endpoints

All endpoints require OAuth2 authentication and follow RESTful conventions. All modules use DTOs.

**Authentication (`/api/auth`):**
- `POST /api/auth/logout` - Logout current user

**User Management (`/api/usuarios`):**
- `GET /api/usuarios/me` - Get current authenticated user
- `GET /api/usuarios/` - List all users (ADMIN only)
- `GET /api/usuarios/{id}` - Get user by ID (ADMIN only)
- `PATCH /api/usuarios/{id}` - Update user (ADMIN only)
- `PATCH /api/usuarios/{id}/toggle-ativo` - Toggle user active status (ADMIN only)

**Service Requests (`/api/solicitacoes`):**
- `GET /api/solicitacoes/` - List all (filtered by role: citizens see only their own)
- `GET /api/solicitacoes/{id}` - Get by ID
- `POST /api/solicitacoes/` - Create new request
- `PATCH /api/solicitacoes/{id}` - Update request
- `DELETE /api/solicitacoes/{id}` - Delete request

**Work Demands (`/api/demandas`):**
- `GET /api/demandas/` - List all demands
- `GET /api/demandas/{id}` - Get demand by ID
- `GET /api/demandas/solicitacao/{solicitacaoId}` - Get demands by service request
- `GET /api/demandas/responsavel?responsavelId={id}` - Get demands by assignee
- `POST /api/demandas/` - Create new demand
- `PATCH /api/demandas/{id}` - Update demand
- `DELETE /api/demandas/{id}` - Delete demand

**Comments (`/api/comentarios`):**
- `GET /api/comentarios/` - List all comments
- `GET /api/comentarios/{id}` - Get comment by ID
- `GET /api/comentarios/demanda/{demandaId}` - Get comments by demand
- `POST /api/comentarios/` - Create comment
- `DELETE /api/comentarios/{id}` - Delete comment

**Materials (`/api/materiais`):**
- `GET /api/materiais/` - List all materials
- `GET /api/materiais/{id}` - Get material by ID
- `POST /api/materiais/` - Create material
- `PATCH /api/materiais/{id}` - Update material
- `DELETE /api/materiais/{id}` - Delete material

**Official Documents (`/api/documentos`):**
- `GET /api/documentos/` - List all documents (OPERADOR, ADMIN)
- `GET /api/documentos/{id}` - Get document by ID
- `POST /api/documentos/` - Create document
- `PATCH /api/documentos/{id}` - Update document
- `DELETE /api/documentos/{id}` - Delete document
- `GET /api/documentos/{id}/pdf` - Download document as PDF

**File Management (`/api/arquivos`):**
- `POST /api/arquivos/upload` - Upload file (multipart, max 10MB per file, 50MB per request)
- `GET /api/arquivos/` - List all files
- `GET /api/arquivos/{id}` - Get file metadata
- `GET /api/arquivos/{id}/url` - Get presigned download URL
- `GET /api/arquivos/{id}/download` - Download file via proxy
- `DELETE /api/arquivos/{id}` - Delete file

**Audit History (`/api/audit`):**
- `GET /api/audit/revisions?entity={ENTITY}&id={id}` - Get revision history for an entity

**Address Management (`/api/enderecos`):**
- `GET /api/enderecos/` - List all addresses
- `GET /api/enderecos/{id}` - Get address by ID
- `POST /api/enderecos/` - Create address
- `PATCH /api/enderecos/{id}` - Update address
- `DELETE /api/enderecos/{id}` - Delete address

**Cemetery Management (`/api/cemiterios`):** (OPERADOR, ADMIN)
- `GET /api/cemiterios/` - List all cemeteries
- `GET /api/cemiterios/{id}` - Get cemetery by ID
- `POST /api/cemiterios/` - Create cemetery
- `PATCH /api/cemiterios/{id}` - Update cemetery
- `DELETE /api/cemiterios/{id}` - Delete cemetery

**Burial Plot Management (`/api/jazigos`):** (OPERADOR, ADMIN)
- `GET /api/jazigos/` - List all burial plots
- `GET /api/jazigos/{id}` - Get burial plot by ID
- `POST /api/jazigos/` - Create burial plot
- `PUT /api/jazigos/{id}` - Update burial plot
- `DELETE /api/jazigos/{id}` - Delete burial plot

**Burial Space Management (`/api/gavetas`):** (OPERADOR, ADMIN)
- `GET /api/gavetas/` - List all burial spaces
- `GET /api/gavetas/{id}` - Get burial space by ID
- `POST /api/gavetas/` - Create burial space
- `PUT /api/gavetas/{id}` - Update burial space
- `DELETE /api/gavetas/{id}` - Delete burial space

**Person Management (`/api/pessoas`):**
- `GET /api/pessoas/` - List all people
- `GET /api/pessoas/{id}` - Get person by ID
- `POST /api/pessoas/` - Create person
- `PUT /api/pessoas/{id}` - Update person
- `DELETE /api/pessoas/{id}` - Delete person

### API Documentation
SpringDoc OpenAPI (Swagger) is configured and available at `/swagger-ui.html` for interactive API exploration.

### Data Model

All entities use Lombok annotations (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`) to reduce boilerplate code and include proper validation annotations.

**Usuario Entity:**
| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key (auto-generated) |
| `email` | String | User's email from OAuth2 provider |
| `name` | String | User's full name |
| `pictureUrl` | String | URL to user's profile picture |
| `provider` | String | OAuth2 provider (e.g., "google") |
| `ativo` | Boolean | User active status flag |
| `role` | Role | User role enum (CIDADAO, OPERADOR, AGENTE, ADMIN) |

**Solicitacao Entity:**
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Primary key (auto-generated) | - |
| `data` | LocalDate | Request date | - |
| `assunto` | SolicitacaoAssunto | Subject category (enum) | @NotNull |
| `body` | String | Request description (text) | @NotBlank |
| `autor` | Usuario | Requesting user (ManyToOne) | @NotNull |
| `local` | Endereco | Location (ManyToOne) | @NotNull |
| `status` | SolicitacaoStatus | Current status (enum) | - |
| `anexos` | Set\<Arquivo\> | Attachments (ManyToMany) | Optional |

**Demanda Entity:**
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Primary key (auto-generated) | - |
| `solicitacao` | Solicitacao | Source request (ManyToOne) | @NotNull |
| `responsavel` | Usuario | Assigned user (ManyToOne) | @NotNull |
| `prazo` | LocalDate | Deadline | Optional |
| `status` | DemandaStatus | Current status (enum) | - |
| `materiais` | Set\<DemandaMaterial\> | Required materials (OneToMany) | Optional |

**DemandaMaterial Entity:**
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Primary key (auto-generated) | - |
| `demanda` | Demanda | Parent demand (ManyToOne) | @NotNull |
| `material` | Material | Material reference (ManyToOne) | @NotNull |
| `quantidade` | Integer | Required quantity | @NotNull |

**Material Entity:**
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Primary key (auto-generated) | - |
| `nome` | String | Material name | @NotBlank |
| `preco` | Double | Unit price | Optional |

**Comentario Entity:**
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Primary key (auto-generated) | - |
| `demanda` | Demanda | Parent demand (ManyToOne) | @NotNull |
| `autor` | Usuario | Comment author (ManyToOne) | @NotNull |
| `texto` | String | Comment text | @NotBlank |
| `criadoEm` | LocalDateTime | Creation timestamp | Auto-generated |

**Documento Entity:**
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Primary key (auto-generated) | - |
| `numero` | String | Document number | @NotBlank |
| `data` | LocalDate | Document date | @NotNull |
| `subject` | String | Subject line | @NotBlank |
| `honorifico` | String | Honorific/greeting | Optional |
| `body` | String | Document body (text) | @NotBlank |
| `tipo` | DocumentoTipo | Type: OFICIO or MEMORANDO | @NotNull |
| `portaria` | String | Ordinance reference | Optional |
| `assinante` | String | Signer name | Optional |
| `interessado` | String | Interested party | Optional |
| `destino` | String | Destination | Optional |
| `anexos` | Set\<Arquivo\> | Attachments (ManyToMany) | Optional |

**Arquivo Entity:**
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Primary key (auto-generated) | - |
| `nomeOriginal` | String | Original filename | @NotBlank |
| `storageKey` | String | MinIO object key (unique) | @NotBlank |
| `contentType` | String | MIME type | @NotBlank |
| `tamanho` | Long | File size in bytes | @NotNull |
| `categoria` | String | File category | Optional |
| `uploadedAt` | LocalDateTime | Upload timestamp | Auto-generated |
| `ativo` | Boolean | Active status | Default: true |

**Endereco Entity:**
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Primary key (auto-generated) | - |
| `logradouro` | String | Street name | @NotBlank, nullable=false |
| `numero` | String | Street number | @NotBlank, nullable=false |
| `bairro` | String | Neighborhood | @NotBlank, nullable=false |
| `referencia` | String | Reference point | Optional |

**Cemiterio Entity:**
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Primary key (auto-generated) | - |
| `nome` | String | Cemetery name | @NotBlank, nullable=false |
| `endereco` | Endereco | Address (OneToOne) | @NotNull, nullable=false |

**Jazigo Entity:**
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Primary key (auto-generated) | - |
| `cemiterio` | Cemiterio | Cemetery (ManyToOne) | @NotNull, nullable=false |
| `largura` | Double | Width dimension | Optional |
| `comprimento` | Double | Length dimension | Optional |
| `quadra` | Integer | Block number | @NotNull, nullable=false |
| `rua` | String | Street identifier | @NotBlank, nullable=false |
| `lote` | String | Lot identifier | @NotBlank, nullable=false |

**Gaveta Entity:**
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Primary key (auto-generated) | - |
| `jazigo` | Jazigo | Burial plot (ManyToOne) | @NotNull, nullable=false |
| `numero` | Integer | Drawer number | Optional |
| `ocupante` | Pessoa | Occupant (ManyToOne) | Optional |

**Pessoa Entity:**
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Primary key (auto-generated) | - |
| `nome` | String | Person's name | Optional |
| `cpf` | String | Brazilian ID number | Optional |
| `sexo` | SexoEnum | Gender (MASCULINO, FEMININO, OUTRO) | Optional |
| `endereco` | Endereco | Address (ManyToOne) | Optional |

## Code Quality Standards

### Checkstyle Rules (checkstyle.xml)
Strict enforcement with build failure on violations:
- Max file length: 1500 lines
- Max line length: 140 characters
- Max method length: 50 lines
- Max method parameters: 5
- Max nested if depth: 3
- Max nested for depth: 2
- Max nested try depth: 3
- Max return statements: 3 per method
- Javadoc required for type declarations
- No `System.out.println()` - use proper logging
- No unused or redundant imports

### Lombok and MapStruct
- Project uses Lombok for reducing boilerplate code (getters, setters, constructors, builders)
- MapStruct is used for DTO mapping with compile-time code generation
- Both tools are properly configured to work together via `lombok-mapstruct-binding`
- Ensure annotation processing is enabled in your IDE
- MapStruct mappers use `componentModel = "spring"` for Spring integration
- MapStruct uses `NullValuePropertyMappingStrategy.IGNORE` to skip null values during updates

## Implementation Notes

### DTO Pattern and MapStruct
All modules use DTOs with MapStruct for request/response mapping:

**DTO Types:**
- `CreateDTO` - Used for POST requests (contains only fields needed for creation)
- `UpdateDTO` - Used for PATCH/PUT requests (all fields optional for partial updates)
- `ResponseDTO` - Used for GET responses (controls what data is exposed to clients)

**MapStruct Mappers:**
- Defined as interfaces with `@Mapper` annotation
- `componentModel = "spring"` for automatic Spring bean registration
- `nullValuePropertyMappingStrategy = IGNORE` prevents null values from overwriting existing data
- Common methods:
  - `toEntity(CreateDTO)` - Convert CreateDTO to Entity
  - `toDto(Entity)` - Convert Entity to ResponseDTO
  - `toDtoList(List<Entity>)` - Convert list of entities to DTOs
  - `updateFromDto(UpdateDTO, @MappingTarget Entity)` - Update existing entity from DTO

### Error Handling
- `GlobalExceptionHandler` with `@ControllerAdvice` handles exceptions centrally
- Custom exceptions: `NotFoundException` (404), `ConflictException` (409), `InvalidFileException`, `StorageException`
- `EnumExceptionHandler` handles invalid enum value errors
- Returns standardized error responses

### File Storage (MinIO)
- Files are uploaded via multipart requests to `/api/arquivos/upload`
- Stored in MinIO object storage with unique storage keys
- `FileValidator` validates file types and sizes
- Presigned URLs available for direct client downloads
- Proxy download endpoint available as fallback

### Notification System (RabbitMQ)
- `NotificationPublisher` publishes events for:
  - Demand assignment (new demand created with assignee)
  - Demand status changes
- Uses topic exchange with routing keys
- JSON message serialization via Jackson

### Audit System (Hibernate Envers)
- Entities annotated with `@Audited` have full revision history
- `UsuarioRevisionEntity` extends `DefaultRevisionEntity` with user context
- `UsuarioRevisionListener` captures the authenticated user per revision
- Generic `GenericAuditService` provides revision queries for any audited entity
- REST endpoint: `GET /api/audit/revisions?entity={ENTITY}&id={id}`

### PDF Generation
- `DocumentoPdfService` generates PDF documents (oficios, memorandos) using OpenPDF
- Available at `GET /api/documentos/{id}/pdf`

### Repository Pattern
- All repositories extend `JpaRepository<Entity, Long>`
- Custom query methods available (e.g., `findByEmail(String email)` in UsuarioRepository)
- Provides standard CRUD operations plus custom queries

## Key Technologies

- **Java**: 21
- **Spring Boot**: 3.5.9
- **Database**: PostgreSQL with Spring Data JPA and Hibernate
- **Security**: Spring Security with OAuth2 Client (Google), role-based access control
- **ORM**: Hibernate with automatic schema management
- **Auditing**: Hibernate Envers for entity revision tracking
- **Validation**: Bean Validation API (Jakarta Validation)
- **DTO Mapping**: MapStruct 1.5.5.Final for compile-time DTO mapping
- **Documentation**: SpringDoc OpenAPI 2.8.14 (Swagger UI)
- **File Storage**: MinIO 8.6.0 for object storage
- **Messaging**: RabbitMQ via Spring AMQP
- **PDF Generation**: OpenPDF 2.0.3
- **Environment**: spring-dotenv 4.0.0 for environment variable management
- **Build**: Maven with Checkstyle plugin
- **Dev Tools**: Spring Boot DevTools for hot reload
- **Code Simplification**: Lombok with MapStruct integration
- **Testing**: Spring Boot Test, Spring Security Test, H2 in-memory database

## Git Workflow

- Main branch: `main`
- Development branch: `develop`
- Feature branches: Follow naming convention `feature/<feature-name>`
- Recent work includes:
  - Service request management (Solicitacoes) with citizen role filtering
  - Work demand system (Demandas) with material tracking and comments
  - Official document generation with PDF export (Documentos)
  - File upload/storage with MinIO (Arquivos)
  - Role-based access control (CIDADAO, OPERADOR, AGENTE, ADMIN)
  - Hibernate Envers audit logging
  - RabbitMQ notification system
  - DTO adoption across all modules
  - Custom exception handling with GlobalExceptionHandler
  - Cemetery management system (Cemiterios, Jazigos, Gavetas)
  - Address management (Enderecos)
  - Person management (Pessoas)
  - OAuth2 Google login with user persistence

## Domain Model Overview

The system manages infrastructure services and cemetery operations with two main areas:

**Infrastructure Request Workflow:**
1. **Solicitacao** (Service Request) - Citizen reports an issue (buraco, esgoto, iluminacao, etc.)
2. **Demanda** (Work Demand) - Derived from a request, assigned to a responsible agent
3. **DemandaMaterial** - Materials required for a demand with quantities
4. **Material** - Catalog of available materials with pricing
5. **Comentario** (Comment) - Discussion thread on a demand
6. **Documento** (Document) - Official documents (oficios, memorandos) with PDF generation
7. **Arquivo** (File) - Attachments for requests and documents, stored in MinIO

**Cemetery Management:**
1. **Cemiterio** (Cemetery) - Has a location (Endereco)
2. **Jazigo** (Burial Plot) - Located within a cemetery, has dimensions and location identifiers
3. **Gaveta** (Burial Space) - Individual space within a jazigo, can have an occupant

**Shared Entities:**
- **Pessoa** (Person) - Can be an occupant of a gaveta, has personal info and address
- **Endereco** (Address) - Shared entity for locations of cemeteries, people, and service requests
- **Usuario** (User) - OAuth2-authenticated user with role-based permissions

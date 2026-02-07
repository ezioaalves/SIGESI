# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SIGESI (Sistema de Gerenciamento da Secretária de Infraestrutura) is a Spring Boot 3.3.0 application using Java 21. The project is a comprehensive cemetery management system that implements OAuth2 authentication with Google and is configured with strict code quality standards via Checkstyle.

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
The application uses a `.env` file for environment variable management (loaded automatically via dotenv-java):

```bash
# Copy the example file
cp .env.example .env

# Edit .env with your Google OAuth2 credentials
# Required variables:
#   GOOGLE_CLIENT_ID - Your Google OAuth2 client ID
#   GOOGLE_CLIENT_SECRET - Your Google OAuth2 client secret
# Get these from: https://console.cloud.google.com/

# The application will automatically load the .env file on startup
# No need to manually source environment files!
```

**Important Notes:**
- The `.env` file is already in `.gitignore` to prevent credential leaks
- Environment variables from `.env` are loaded in `SigesiApplication.main()` before Spring initialization
- If `.env` is missing, the application will continue (for production deployments using system environment variables)
- Old shell scripts (`set-env.sh`, `set-env.fish`) are deprecated but still supported

## Architecture

### Package Structure
The application uses package-by-feature organization under `com.sigesi.sigesi`:

**Core Application:**
- `SigesiApplication.java` - Main application entry point with .env file loading

**Configuration (`config` package):**
- `SpringConfig.java` - Security and OAuth2 configuration
- `OAuth2LoginSuccessHandler.java` - Post-login handler for OAuth2 authentication

**Usuarios Module (`usuarios` package):**
- `Usuario.java` - Entity with JPA annotations for user management
- `UsuarioController.java` - REST controller exposing `/api/usuarios` endpoints
- `UsuarioService.java` - Business logic for user operations
- `UsuarioRepository.java` - JPA repository interface with custom queries

**Enderecos Module (`enderecos` package):**
- `Endereco.java` - Address entity with street, number, neighborhood, and reference fields
- `EnderecoController.java` - REST controller with full CRUD operations using DTOs
- `EnderecoService.java` - Business logic for address operations
- `EnderecoRepository.java` - JPA repository interface
- `EnderecoMapper.java` - MapStruct mapper for DTO conversions
- `dtos/` - Data Transfer Objects (EnderecoCreateDTO, EnderecoUpdateDTO, EnderecoResponseDTO)

**Cemiterios Module (`cemiterios` package):**
- `Cemiterio.java` - Cemetery entity with name and address relationship
- `CemiterioController.java` - REST controller for cemetery management
- `CemiterioService.java` - Business logic for cemetery operations
- `CemiterioRepository.java` - JPA repository interface

**Jazigos Module (`jazigos` package):**
- `Jazigo.java` - Burial plot entity with dimensions and location (quadra, rua, lote)
- `JazigoController.java` - REST controller for burial plot management
- `JazigoService.java` - Business logic for burial plot operations
- `JazigoRepository.java` - JPA repository interface

**Gavetas Module (`gavetas` package):**
- `Gaveta.java` - Burial space entity within a jazigo, can have an occupant
- `GavetaController.java` - REST controller for burial space management
- `GavetaService.java` - Business logic for burial space operations
- `GavetaRepository.java` - JPA repository interface

**Pessoas Module (`pessoas` package):**
- `Pessoa.java` - Person entity with name, CPF, gender, and address
- `PessoaController.java` - REST controller for person management
- `PessoaService.java` - Business logic for person operations
- `PessoaRepository.java` - JPA repository interface

### Security Configuration

OAuth2 with Google is configured in `SpringConfig.java`:
- All endpoints require authentication (CSRF disabled)
- Uses Spring Security OAuth2 client with Google provider
- Credentials loaded from environment variables: `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`
- Custom `OAuth2LoginSuccessHandler` processes successful logins:
  - Extracts user info from OAuth2 response (email, name, picture)
  - Creates or updates user in database via `UsuarioService.processOAuthPostLogin()`
  - Redirects to home page ("/") after successful authentication

### Database

**PostgreSQL with JPA/Hibernate:**
- Fully configured and integrated via Spring Data JPA
- Database connection details in `application.properties`:
  - URL: `jdbc:postgresql://localhost:5432/sigesi`
  - Default credentials: `user` / `password` (update for production)
- Hibernate configuration:
  - DDL auto mode: `update` (automatically updates schema)
  - SQL logging enabled with formatted output
- All entities managed with automatic table creation/updates

**Entity Relationships:**
- `Cemiterio` has a OneToOne relationship with `Endereco`
- `Jazigo` belongs to `Cemiterio` (ManyToOne)
- `Gaveta` belongs to `Jazigo` (ManyToOne)
- `Gaveta` can have an `ocupante` (`Pessoa`) (ManyToOne)
- `Pessoa` can have an `Endereco` (ManyToOne)

### API Endpoints

All endpoints require OAuth2 authentication and follow RESTful conventions.

**User Management (`/api/usuarios`):**
- `GET /api/usuarios/` - List all users
- `GET /api/usuarios/{id}` - Get user by ID
- `PATCH /api/usuarios/{id}/toggle-ativo` - Toggle user active status

**Address Management (`/api/enderecos`):**
- `GET /api/enderecos/` - List all addresses (returns DTOs)
- `GET /api/enderecos/{id}` - Get address by ID (returns DTO)
- `POST /api/enderecos/` - Create new address (accepts EnderecoCreateDTO)
- `PATCH /api/enderecos/{id}` - Update address (accepts EnderecoUpdateDTO)
- `DELETE /api/enderecos/{id}` - Delete address

**Cemetery Management (`/api/cemiterios`):**
- `GET /api/cemiterios/` - List all cemeteries
- `GET /api/cemiterios/{id}` - Get cemetery by ID
- `POST /api/cemiterios/` - Create new cemetery
- `PATCH /api/cemiterios/{id}` - Update cemetery (partial update)
- `DELETE /api/cemiterios/{id}` - Delete cemetery

**Burial Plot Management (`/api/jazigos`):**
- `GET /api/jazigos/` - List all burial plots
- `GET /api/jazigos/{id}` - Get burial plot by ID
- `POST /api/jazigos/` - Create new burial plot
- `PUT /api/jazigos/{id}` - Update burial plot
- `DELETE /api/jazigos/{id}` - Delete burial plot

**Burial Space Management (`/api/gavetas`):**
- `GET /api/gavetas/` - List all burial spaces
- `GET /api/gavetas/{id}` - Get burial space by ID
- `POST /api/gavetas/` - Create new burial space
- `PUT /api/gavetas/{id}` - Update burial space
- `DELETE /api/gavetas/{id}` - Delete burial space

**Person Management (`/api/pessoas`):**
- `GET /api/pessoas/` - List all people
- `GET /api/pessoas/{id}` - Get person by ID
- `POST /api/pessoas/` - Create new person
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
| `sexo` | String | Gender | Optional |
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

### OAuth2 User Flow
1. User accesses any endpoint (all require authentication)
2. Redirected to Google OAuth2 login
3. After successful authentication, `OAuth2LoginSuccessHandler` is triggered
4. Handler calls `UsuarioService.processOAuthPostLogin()` which:
   - Checks if user exists by email
   - Updates existing user info OR creates new user with `ativo=true`
   - Saves to database and returns user object
5. User redirected to home page ("/")

### Repository Pattern
- All repositories extend `JpaRepository<Entity, Long>`
- Custom query methods available (e.g., `findByEmail(String email)` in UsuarioRepository)
- Provides standard CRUD operations plus custom queries

### DTO Pattern and MapStruct
The application uses DTOs for the Enderecos module (other modules currently use entities directly):

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

**Implementation Pattern:**
```java
// In Service layer
public EnderecoResponseDTO createEndereco(EnderecoCreateDTO dto) {
    Endereco entity = enderecoMapper.toEntity(dto);
    Endereco saved = repository.save(entity);
    return enderecoMapper.toDto(saved);
}
```

### Error Handling
- Service layer throws `RuntimeException` for resource not found scenarios
- Returns standardized 404 responses when entities are not found
- Consider implementing custom exception classes and @ControllerAdvice for better error handling

## Key Technologies

- **Java**: 21
- **Spring Boot**: 3.3.0
- **Database**: PostgreSQL with Spring Data JPA and Hibernate
- **Security**: Spring Security with OAuth2 Client (Google)
- **ORM**: Hibernate with automatic schema management
- **Validation**: Bean Validation API (Jakarta Validation)
- **DTO Mapping**: MapStruct 1.5.5.Final for compile-time DTO mapping
- **Documentation**: SpringDoc OpenAPI 2.5.0 (Swagger UI)
- **Environment**: dotenv-java for environment variable management
- **Build**: Maven with Checkstyle plugin
- **Dev Tools**: Spring Boot DevTools for hot reload
- **Code Simplification**: Lombok with MapStruct integration

## Git Workflow

- Main branch: `main`
- Development branch: `develop`
- Feature branches: Follow naming convention `feature/<feature-name>`
- Recent work includes:
  - Cemetery management system (Cemitérios, Jazigos, Gavetas)
  - Address management with DTO pattern (Endereços)
  - Person management (Pessoas)
  - MapStruct integration for DTO mapping
  - Swagger UI improvements with plural entity names
  - Comprehensive test coverage for controllers
  - Standardized 404 error responses
  - User management system with CRUD operations
  - OAuth2 Google login with user persistence
  - JPA/Hibernate integration with PostgreSQL

## Domain Model Overview

The system manages cemetery infrastructure with the following hierarchy:
1. **Cemitério** (Cemetery) - Has a location (Endereco)
2. **Jazigo** (Burial Plot) - Located within a cemetery, has dimensions and location identifiers
3. **Gaveta** (Burial Space) - Individual space within a jazigo, can have an occupant
4. **Pessoa** (Person) - Can be an occupant of a gaveta, has personal info and address
5. **Endereço** (Address) - Shared entity for locations of cemeteries and people

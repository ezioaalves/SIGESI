# Feature: Improve Test Coverage to 80%+

## Feature Description

The current JaCoCo test coverage report shows only **42% instruction coverage** and **19% branch coverage** across the SIGESI project. Multiple modules have 0% coverage, and several services, entities, and validators have no tests at all. This PRP plans a systematic refactoring and expansion of the test suite to achieve at least **80% instruction coverage** and significantly improved branch coverage.

## User Story

As a development team member
I want comprehensive test coverage across all modules
So that we can confidently refactor and extend the codebase without regressions

## Problem Statement

The JaCoCo report reveals critical gaps:
- **0% coverage**: auditoria, storage packages
- **16% coverage**: demandas package
- **0% branch coverage**: demandas, auditoria, solicitacoes, documentos
- **Missing service tests**: ComentarioService, DemandaService, MaterialService, SolicitacaoService, DocumentoPdfService, MinioService, GenericAuditService
- **Missing entity tests**: Arquivo, Comentario, Demanda, DemandaMaterial, Material, Solicitacao
- **Missing utility tests**: FileValidator (security-critical)
- **Incomplete tests**: ArquivoService (only 1 happy path test)

## Solution Statement

Create comprehensive unit tests for all untested services, entities, and utilities following existing test patterns. Expand partially-tested files. Target 80%+ instruction coverage by focusing on the modules with the largest coverage gaps first.

## Feature Metadata

**Feature Type**: Refactor / Test Coverage Improvement
**Estimated Complexity**: High
**Primary Systems Affected**: All modules - services, entities, validators
**Dependencies**: JUnit 5, Mockito, MockMvc, Jakarta Validation (all already in pom.xml)

---

## CONTEXT REFERENCES

### Relevant Codebase Files (Existing Test Patterns to Mirror)

- `src/test/java/com/sigesi/sigesi/cemiterios/CemiterioServiceTest.java` - Why: Gold-standard service test pattern with complete CRUD coverage, @DisplayName, MockitoExtension
- `src/test/java/com/sigesi/sigesi/pessoas/PessoaServiceTest.java` - Why: Service test with conflict exception testing (most complex existing service test)
- `src/test/java/com/sigesi/sigesi/enderecos/EnderecoEntityTest.java` - Why: Gold-standard entity validation test pattern with Jakarta Validator
- `src/test/java/com/sigesi/sigesi/documentos/DocumentoEntityTest.java` - Why: Entity test with enum and relationship testing
- `src/test/java/com/sigesi/sigesi/cemiterios/CemiterioControllerTest.java` - Why: Controller test pattern reference

### New Files to Create

**Service Tests (7 new + 1 expand):**
1. `src/test/java/com/sigesi/sigesi/comentarios/ComentarioServiceTest.java`
2. `src/test/java/com/sigesi/sigesi/demandas/DemandaServiceTest.java`
3. `src/test/java/com/sigesi/sigesi/materiais/MaterialServiceTest.java`
4. `src/test/java/com/sigesi/sigesi/solicitacoes/SolicitacaoServiceTest.java`
5. `src/test/java/com/sigesi/sigesi/storage/MinioServiceTest.java`
6. `src/test/java/com/sigesi/sigesi/documentos/DocumentoPdfServiceTest.java`
7. `src/test/java/com/sigesi/sigesi/auditoria/GenericAuditServiceTest.java`
8. `src/test/java/com/sigesi/sigesi/arquivos/ArquivoServiceTest.java` (EXPAND existing)

**Entity Tests (6 new):**
9. `src/test/java/com/sigesi/sigesi/arquivos/ArquivoEntityTest.java`
10. `src/test/java/com/sigesi/sigesi/comentarios/ComentarioEntityTest.java`
11. `src/test/java/com/sigesi/sigesi/demandas/DemandaEntityTest.java`
12. `src/test/java/com/sigesi/sigesi/demandas/DemandaMaterialEntityTest.java`
13. `src/test/java/com/sigesi/sigesi/materiais/MaterialEntityTest.java`
14. `src/test/java/com/sigesi/sigesi/solicitacoes/SolicitacaoEntityTest.java`

**Validator Tests (1 new):**
15. `src/test/java/com/sigesi/sigesi/arquivos/validation/FileValidatorTest.java`

### Patterns to Follow

**Service Test Pattern** (from CemiterioServiceTest):
```java
@ExtendWith(MockitoExtension.class)
class XxxServiceTest {
    @Mock private XxxRepository xxxRepository;
    @Mock private XxxMapper xxxMapper;
    // ... other @Mock dependencies
    @InjectMocks private XxxService xxxService;

    private Xxx xxx;
    private XxxResponseDTO responseDTO;
    // ... other test data

    @BeforeEach
    void setUp() {
        // Initialize test data using builders
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nao ha registros")
    void testGetAllRetornaListaVazia() { ... }

    @Test
    @DisplayName("Deve retornar lista de registros")
    void testGetAllRetornaLista() { ... }

    @Test
    @DisplayName("Deve retornar registro por ID")
    void testGetByIdComSucesso() { ... }

    @Test
    @DisplayName("Deve lancar NotFoundException quando ID nao encontrado")
    void testGetByIdNaoEncontrado() { ... }

    // ... create, update, delete tests
}
```

**Entity Test Pattern** (from EnderecoEntityTest):
```java
class XxxEntityTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve criar entidade valida")
    void testCriacaoValida() {
        Xxx xxx = Xxx.builder().field("value").build();
        Set<ConstraintViolation<Xxx>> violations = validator.validate(xxx);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve falhar quando campo obrigatorio e nulo")
    void testCampoObrigatorioNulo() {
        Xxx xxx = Xxx.builder().build(); // missing required field
        Set<ConstraintViolation<Xxx>> violations = validator.validate(xxx);
        assertFalse(violations.isEmpty());
    }
}
```

**Naming Conventions:**
- Test classes: `{ClassName}Test.java`
- Test methods: `test{MethodName}{Condition}` (e.g., `testGetAllRetornaListaVazia`)
- Display names: Portuguese descriptions starting with "Deve ..."
- Javadoc: Portuguese class-level docs describing what the test validates

---

## IMPLEMENTATION PLAN

### Phase 1: Security-Critical Tests (FileValidator)

Create tests for the FileValidator utility, which validates file uploads. This is security-critical code that must be tested thoroughly.

### Phase 2: Entity Tests (6 new entity test classes)

Create entity validation tests for all untested entities following the existing pattern. These are the simplest tests and provide foundational coverage for the entity classes.

### Phase 3: Service Tests - Simple CRUD (MaterialService, ComentarioService)

Start with simpler services that follow standard CRUD patterns with fewer dependencies.

### Phase 4: Service Tests - Complex Logic (SolicitacaoService, DemandaService)

Test services with complex business logic, role-based filtering, transactions, and notifications.

### Phase 5: Service Tests - Infrastructure (MinioService, ArquivoService expansion)

Test storage and file management services with proper mocking of external dependencies.

### Phase 6: Service Tests - Specialized (DocumentoPdfService, GenericAuditService)

Test PDF generation and audit trail services which have unique testing challenges.

---

## STEP-BY-STEP TASKS

### Task 1: CREATE `FileValidatorTest.java`

- **FILE**: `src/test/java/com/sigesi/sigesi/arquivos/validation/FileValidatorTest.java`
- **IMPLEMENT**: Tests for all 5 public static methods in FileValidator
- **PATTERN**: Unit tests with MockMultipartFile from Spring
- **TESTS TO WRITE**:
  - `testValidateFile_ValidJpeg` - happy path with valid JPEG
  - `testValidateFile_ValidPdf` - happy path with valid PDF
  - `testValidateFileSize_EmptyFile` - empty file throws InvalidFileException
  - `testValidateFileSize_OversizedFile` - >10MB throws InvalidFileException
  - `testValidateContentType_InvalidType` - unsupported content type throws
  - `testValidateContentType_NullType` - null content type throws
  - `testValidateFileExtension_InvalidExtension` - .exe throws
  - `testValidateFileExtension_NoExtension` - filename without extension throws
  - `testValidateFilename_PathTraversal` - `../file.jpg` throws
  - `testValidateFilename_BackslashTraversal` - `..\\file.jpg` throws
  - `testValidateFilename_NullBytes` - filename with `\0` throws
  - `testValidateFilename_EmptyName` - empty filename throws
  - `testValidateFilename_NullName` - null filename throws
  - `testValidateFile_AllValidationsPass` - composite method succeeds
- **IMPORTS**: `org.springframework.mock.web.MockMultipartFile`, JUnit5, `InvalidFileException`
- **VALIDATE**: `mvn test -Dtest=FileValidatorTest`

### Task 2: CREATE `ArquivoEntityTest.java`

- **FILE**: `src/test/java/com/sigesi/sigesi/arquivos/ArquivoEntityTest.java`
- **IMPLEMENT**: Entity validation tests for Arquivo
- **PATTERN**: Mirror `EnderecoEntityTest.java`
- **TESTS TO WRITE**:
  - Valid entity creation with all required fields
  - `nomeOriginal` @NotBlank violations (null, empty, whitespace)
  - `storageKey` @NotBlank violations (null, empty, whitespace)
  - `contentType` @NotBlank violations (null, empty)
  - `tamanho` @NotNull violation
  - Builder pattern works correctly
  - `@PrePersist` callback sets `uploadedAt` (test `onCreate()` method directly)
  - Default `ativo` value is true
- **VALIDATE**: `mvn test -Dtest=ArquivoEntityTest`

### Task 3: CREATE `ComentarioEntityTest.java`

- **FILE**: `src/test/java/com/sigesi/sigesi/comentarios/ComentarioEntityTest.java`
- **IMPLEMENT**: Entity validation tests for Comentario
- **PATTERN**: Mirror `DocumentoEntityTest.java`
- **TESTS TO WRITE**:
  - Valid entity creation
  - `demanda` @NotNull violation
  - `autor` @NotNull violation
  - `texto` @NotBlank violations (null, empty, whitespace)
  - Builder pattern works
  - `@PrePersist` callback sets `criadoEm` when null
  - `@PrePersist` does NOT overwrite existing `criadoEm`
- **VALIDATE**: `mvn test -Dtest=ComentarioEntityTest`

### Task 4: CREATE `DemandaEntityTest.java`

- **FILE**: `src/test/java/com/sigesi/sigesi/demandas/DemandaEntityTest.java`
- **IMPLEMENT**: Entity validation tests for Demanda
- **PATTERN**: Mirror `GavetaEntityTest.java` (similar relationship testing)
- **TESTS TO WRITE**:
  - Valid entity creation with required fields
  - `solicitacao` @NotNull violation
  - Builder pattern works
  - `@PrePersist` sets default status to PENDENTE
  - `@PrePersist` does NOT overwrite existing status
  - `addDemandaMaterial()` sets bidirectional reference
  - `removeDemandaMaterial()` clears bidirectional reference
  - Default `materiais` set is initialized (empty, not null)
- **VALIDATE**: `mvn test -Dtest=DemandaEntityTest`

### Task 5: CREATE `DemandaMaterialEntityTest.java`

- **FILE**: `src/test/java/com/sigesi/sigesi/demandas/DemandaMaterialEntityTest.java`
- **IMPLEMENT**: Entity validation tests for DemandaMaterial
- **TESTS TO WRITE**:
  - Valid entity creation
  - `quantidade` @NotNull violation
  - Builder pattern works
  - Relationships can be set (demanda, material)
- **VALIDATE**: `mvn test -Dtest=DemandaMaterialEntityTest`

### Task 6: CREATE `MaterialEntityTest.java`

- **FILE**: `src/test/java/com/sigesi/sigesi/materiais/MaterialEntityTest.java`
- **IMPLEMENT**: Entity validation tests for Material
- **PATTERN**: Mirror `EnderecoEntityTest.java`
- **TESTS TO WRITE**:
  - Valid entity creation
  - `nome` @NotBlank violations (null, empty, whitespace)
  - `preco` @NotNull violation
  - Builder pattern works
  - Getters/setters work correctly
- **VALIDATE**: `mvn test -Dtest=MaterialEntityTest`

### Task 7: CREATE `SolicitacaoEntityTest.java`

- **FILE**: `src/test/java/com/sigesi/sigesi/solicitacoes/SolicitacaoEntityTest.java`
- **IMPLEMENT**: Entity validation tests for Solicitacao
- **PATTERN**: Mirror `DocumentoEntityTest.java`
- **TESTS TO WRITE**:
  - Valid entity creation with all required fields
  - `body` @NotBlank violations (null, empty, whitespace)
  - `autor` @NotNull violation
  - `local` @NotNull violation
  - Builder pattern works
  - `@PrePersist` sets `data` when null
  - `@PrePersist` does NOT overwrite existing `data`
  - Default `anexos` list behavior
- **VALIDATE**: `mvn test -Dtest=SolicitacaoEntityTest`

### Task 8: CREATE `MaterialServiceTest.java`

- **FILE**: `src/test/java/com/sigesi/sigesi/materiais/MaterialServiceTest.java`
- **IMPLEMENT**: Service tests for MaterialService (standard CRUD + findAllByIds)
- **PATTERN**: Mirror `CemiterioServiceTest.java`
- **DEPENDENCIES TO MOCK**: `MaterialRepository`, `MaterialMapper`
- **TESTS TO WRITE**:
  - `testGetAllRetornaListaVazia` - empty list
  - `testGetAllRetornaLista` - list with materials
  - `testGetMaterialByIdComSucesso` - found by ID
  - `testGetMaterialByIdNaoEncontrado` - NotFoundException
  - `testCreateMaterialComSucesso` - creates and returns DTO
  - `testUpdateMaterialComSucesso` - updates existing
  - `testUpdateMaterialNaoEncontrado` - NotFoundException on update
  - `testDeleteMaterialComSucesso` - deletes by ID
  - `testDeleteMaterialNaoEncontrado` - NotFoundException on delete
  - `testGetMaterialEntityByIdComSucesso` - returns entity
  - `testGetMaterialEntityByIdNaoEncontrado` - NotFoundException
  - `testFindAllByIdsComSucesso` - all IDs found
  - `testFindAllByIdsComIdInexistente` - NotFoundException when IDs mismatch
- **VALIDATE**: `mvn test -Dtest=MaterialServiceTest`

### Task 9: CREATE `ComentarioServiceTest.java`

- **FILE**: `src/test/java/com/sigesi/sigesi/comentarios/ComentarioServiceTest.java`
- **IMPLEMENT**: Service tests for ComentarioService
- **PATTERN**: Mirror `CemiterioServiceTest.java`
- **DEPENDENCIES TO MOCK**: `ComentarioRepository`, `ComentarioMapper`, `DemandaService`, `UsuarioService`
- **TESTS TO WRITE**:
  - `testGetAllRetornaListaVazia` - empty list
  - `testGetAllRetornaLista` - with comentarios
  - `testGetComentarioByIdComSucesso` - found
  - `testGetComentarioByIdNaoEncontrado` - NotFoundException
  - `testGetComentariosByDemandaRetornaLista` - filtered by demanda ID
  - `testGetComentariosByDemandaRetornaListaVazia` - no comments for demanda
  - `testCreateComentarioComSucesso` - resolves autor + demanda, saves, returns DTO
  - `testDeleteComentarioComSucesso` - deletes existing
  - `testDeleteComentarioNaoEncontrado` - NotFoundException
  - `testGetComentarioEntityByIdComSucesso` - returns entity
  - `testGetComentarioEntityByIdNaoEncontrado` - NotFoundException
- **VALIDATE**: `mvn test -Dtest=ComentarioServiceTest`

### Task 10: CREATE `SolicitacaoServiceTest.java`

- **FILE**: `src/test/java/com/sigesi/sigesi/solicitacoes/SolicitacaoServiceTest.java`
- **IMPLEMENT**: Service tests for SolicitacaoService with role-based logic
- **PATTERN**: Mirror `PessoaServiceTest.java` (complex service test)
- **DEPENDENCIES TO MOCK**: `SolicitacaoRepository`, `SolicitacaoMapper`, `UsuarioService`, `EnderecoService`, `ArquivoService`
- **TESTS TO WRITE**:
  - `testGetAllRetornaTodasParaAdmin` - ADMIN role sees all
  - `testGetAllRetornaTodasParaOperador` - OPERADOR role sees all
  - `testGetAllRetornaApenasPropriasParaUser` - USER role sees only own
  - `testGetSolicitacaoByIdComSucesso` - found
  - `testGetSolicitacaoByIdNaoEncontrado` - NotFoundException
  - `testCreateSolicitacaoComSucesso` - creates with autor, local, status ABERTA
  - `testCreateSolicitacaoComAnexos` - creates with file attachments
  - `testCreateSolicitacaoSemAnexos` - creates without attachments
  - `testUpdateSolicitacaoComSucesso` - updates status
  - `testUpdateSolicitacaoNaoEncontrado` - NotFoundException
  - `testDeleteSolicitacaoComSucesso` - deletes
  - `testDeleteSolicitacaoNaoEncontrado` - NotFoundException
  - `testGetSolicitacaoEntityByIdComSucesso` - returns entity
  - `testGetSolicitacaoEntityByIdNaoEncontrado` - NotFoundException
- **VALIDATE**: `mvn test -Dtest=SolicitacaoServiceTest`

### Task 11: CREATE `DemandaServiceTest.java`

- **FILE**: `src/test/java/com/sigesi/sigesi/demandas/DemandaServiceTest.java`
- **IMPLEMENT**: Service tests for DemandaService (most complex - transactions, notifications, materials)
- **PATTERN**: Mirror `PessoaServiceTest.java` + add transaction/notification testing
- **DEPENDENCIES TO MOCK**: `DemandaRepository`, `DemandaMapper`, `SolicitacaoService`, `UsuarioService`, `MaterialService`, `NotificationPublisher`
- **TESTS TO WRITE**:
  - `testGetAllRetornaListaVazia` - empty list
  - `testGetAllRetornaLista` - with demandas
  - `testGetDemandaByIdComSucesso` - found
  - `testGetDemandaByIdNaoEncontrado` - NotFoundException
  - `testGetDemandasBySolicitacao` - filtered by solicitacao
  - `testGetDemandasByResponsavel` - filtered by responsavel
  - `testCreateDemandaComSucesso` - creates with solicitacao
  - `testCreateDemandaComResponsavel` - creates and publishes notification
  - `testCreateDemandaSemResponsavel` - creates without notification
  - `testCreateDemandaComMateriais` - creates with material list
  - `testUpdateDemandaComSucesso` - updates fields
  - `testUpdateDemandaMudaStatus` - publishes status change notification
  - `testUpdateDemandaAtribuiResponsavel` - publishes assignment notification
  - `testUpdateDemandaComMateriais` - clears old and adds new materials
  - `testUpdateDemandaNaoEncontrado` - NotFoundException
  - `testDeleteDemandaComSucesso` - deletes
  - `testDeleteDemandaNaoEncontrado` - NotFoundException
  - `testGetDemandaEntityByIdComSucesso` - returns entity
  - `testGetDemandaEntityByIdNaoEncontrado` - NotFoundException
- **VALIDATE**: `mvn test -Dtest=DemandaServiceTest`

### Task 12: UPDATE `ArquivoServiceTest.java` (EXPAND)

- **FILE**: `src/test/java/com/sigesi/sigesi/arquivos/ArquivoServiceTest.java`
- **IMPLEMENT**: Add missing tests (currently only has `testUploadFileSuccess`)
- **PATTERN**: Mirror existing test + expand
- **DEPENDENCIES TO MOCK**: `ArquivoRepository`, `ArquivoMapper`, `MinioService`
- **TESTS TO ADD**:
  - `testUploadFileInvalidFile` - FileValidator rejects file
  - `testUploadFileMinioFails` - MinIO upload throws, propagates exception
  - `testUploadFileDbSaveFails` - DB save fails, rollback deletes from MinIO
  - `testGetFileMetadataComSucesso` - returns DTO
  - `testGetFileMetadataNaoEncontrado` - NotFoundException
  - `testGetAllFiles` - returns list of DTOs
  - `testGetAllFilesVazio` - returns empty list
  - `testGenerateDownloadUrlComSucesso` - returns FileUrlResponseDTO
  - `testGenerateDownloadUrlNaoEncontrado` - NotFoundException
  - `testDownloadFileComSucesso` - returns InputStream
  - `testDownloadFileNaoEncontrado` - NotFoundException
  - `testDeleteFileComSucesso` - deletes from MinIO and DB
  - `testDeleteFileNaoEncontrado` - NotFoundException
  - `testDeleteFileMinioFails` - MinIO fails but DB deletion continues
  - `testGetArquivoEntityByIdComSucesso` - returns entity
  - `testGetArquivoEntityByIdNaoEncontrado` - NotFoundException
- **VALIDATE**: `mvn test -Dtest=ArquivoServiceTest`

### Task 13: CREATE `MinioServiceTest.java`

- **FILE**: `src/test/java/com/sigesi/sigesi/storage/MinioServiceTest.java`
- **IMPLEMENT**: Service tests for MinioService with mocked MinioClient
- **DEPENDENCIES TO MOCK**: `MinioClient`
- **TESTS TO WRITE**:
  - `testInitBucketCreatesWhenNotExists` - bucket doesn't exist, creates it
  - `testInitBucketSkipsWhenExists` - bucket exists, no creation
  - `testInitBucketThrowsStorageException` - MinIO error wraps to StorageException
  - `testUploadFileComSucesso` - uploads and returns object name
  - `testUploadFileThrowsStorageException` - upload fails
  - `testDownloadFileComSucesso` - returns InputStream
  - `testDownloadFileThrowsStorageException` - download fails
  - `testGetPresignedUrlComSucesso` - returns URL string
  - `testGetPresignedUrlThrowsStorageException` - generation fails
  - `testDeleteFileComSucesso` - deletes successfully
  - `testDeleteFileThrowsStorageException` - deletion fails
  - `testFileExistsReturnsTrue` - file exists
  - `testFileExistsReturnsFalse` - file not found returns false (no exception)
- **GOTCHA**: MinioClient methods throw checked exceptions; mock with `doThrow()` or `when().thenThrow()`
- **VALIDATE**: `mvn test -Dtest=MinioServiceTest`

### Task 14: CREATE `DocumentoPdfServiceTest.java`

- **FILE**: `src/test/java/com/sigesi/sigesi/documentos/DocumentoPdfServiceTest.java`
- **IMPLEMENT**: Tests for PDF generation service
- **TESTS TO WRITE**:
  - `testGerarPdfOficio` - generates non-empty byte array for OFICIO type
  - `testGerarPdfMemorando` - generates non-empty byte array for MEMORANDO type
  - `testGerarPdfConteudoCorreto` - verify PDF content contains expected text (use PDFBox or just check bytes non-empty)
  - `testGerarPdfComCamposNulos` - handles optional null fields gracefully
- **GOTCHA**: PDF testing is complex; focus on verifying non-empty output and no exceptions. Don't try to parse PDF content deeply.
- **VALIDATE**: `mvn test -Dtest=DocumentoPdfServiceTest`

### Task 15: CREATE `GenericAuditServiceTest.java`

- **FILE**: `src/test/java/com/sigesi/sigesi/auditoria/GenericAuditServiceTest.java`
- **IMPLEMENT**: Tests for audit service with mocked EntityManager and AuditReader
- **DEPENDENCIES TO MOCK**: `EntityManager`, Envers `AuditReaderFactory` (use `MockedStatic`)
- **TESTS TO WRITE**:
  - `testGetRevisionsRetornaLista` - returns audit log entries
  - `testGetRevisionsRetornaListaVazia` - no revisions found
  - `testGetRevisionsComEntityId` - filters by specific entity ID
  - `testGetRevisionsSemEntityId` - returns all revisions for entity class
  - `testMapAction` - maps ADD/MOD/DEL correctly
- **GOTCHA**: Envers AuditReaderFactory.get() is static; needs `try(MockedStatic<AuditReaderFactory>)` pattern
- **VALIDATE**: `mvn test -Dtest=GenericAuditServiceTest`

---

## TESTING STRATEGY

### Unit Tests

All new tests are unit tests using:
- **JUnit 5** (`@ExtendWith(MockitoExtension.class)` for service tests)
- **Mockito** for mocking dependencies
- **Jakarta Validation** for entity constraint testing
- **MockMultipartFile** for file upload testing

### Test Organization

Tests mirror the source package structure:
```
src/test/java/com/sigesi/sigesi/
├── arquivos/
│   ├── ArquivoEntityTest.java (NEW)
│   ├── ArquivoServiceTest.java (EXPAND)
│   └── validation/
│       └── FileValidatorTest.java (NEW)
├── auditoria/
│   └── GenericAuditServiceTest.java (NEW)
├── comentarios/
│   ├── ComentarioEntityTest.java (NEW)
│   └── ComentarioServiceTest.java (NEW)
├── demandas/
│   ├── DemandaEntityTest.java (NEW)
│   ├── DemandaMaterialEntityTest.java (NEW)
│   └── DemandaServiceTest.java (NEW)
├── materiais/
│   ├── MaterialEntityTest.java (NEW)
│   └── MaterialServiceTest.java (NEW)
├── solicitacoes/
│   ├── SolicitacaoEntityTest.java (NEW)
│   └── SolicitacaoServiceTest.java (NEW)
├── storage/
│   └── MinioServiceTest.java (NEW)
└── documentos/
    └── DocumentoPdfServiceTest.java (NEW)
```

### Edge Cases

- NotFoundException for all get/update/delete by ID methods
- Empty lists for getAll methods
- Null handling in update DTOs (MapStruct IGNORE strategy)
- File validation edge cases (path traversal, null bytes, oversized)
- Bidirectional relationship management (Demanda <-> DemandaMaterial)
- Role-based access filtering (SolicitacaoService.getAll)
- Notification publishing conditions (DemandaService)
- Storage failure recovery (ArquivoService rollback)

---

## VALIDATION COMMANDS

### Level 1: Compile
```bash
mvn compile -q
mvn test-compile -q
```

### Level 2: Run All Tests
```bash
mvn test
```

### Level 3: Run Individual Test Classes
```bash
mvn test -Dtest=FileValidatorTest
mvn test -Dtest=ArquivoEntityTest
mvn test -Dtest=ComentarioEntityTest
mvn test -Dtest=DemandaEntityTest
mvn test -Dtest=DemandaMaterialEntityTest
mvn test -Dtest=MaterialEntityTest
mvn test -Dtest=SolicitacaoEntityTest
mvn test -Dtest=MaterialServiceTest
mvn test -Dtest=ComentarioServiceTest
mvn test -Dtest=SolicitacaoServiceTest
mvn test -Dtest=DemandaServiceTest
mvn test -Dtest=ArquivoServiceTest
mvn test -Dtest=MinioServiceTest
mvn test -Dtest=DocumentoPdfServiceTest
mvn test -Dtest=GenericAuditServiceTest
```

### Level 4: JaCoCo Coverage Report
```bash
mvn verify
# Report at: target/site/jacoco/index.html
```

### Level 5: Checkstyle
```bash
mvn checkstyle:check
```

---

## ACCEPTANCE CRITERIA

- [ ] All 14 new/expanded test files compile without errors
- [ ] All new tests pass (green)
- [ ] All existing tests continue to pass (no regressions)
- [ ] JaCoCo instruction coverage reaches 80%+ overall
- [ ] JaCoCo branch coverage improves significantly (target 50%+)
- [ ] `mvn verify` passes (JaCoCo thresholds met)
- [ ] `mvn checkstyle:check` passes
- [ ] FileValidator security validation fully tested
- [ ] All CRUD operations tested for every service
- [ ] All entity validation constraints tested
- [ ] Error scenarios (NotFoundException, StorageException) tested
- [ ] Complex business logic tested (DemandaService notifications, SolicitacaoService role filtering)

---

## COMPLETION CHECKLIST

- [ ] Task 1: FileValidatorTest created and passing
- [ ] Task 2: ArquivoEntityTest created and passing
- [ ] Task 3: ComentarioEntityTest created and passing
- [ ] Task 4: DemandaEntityTest created and passing
- [ ] Task 5: DemandaMaterialEntityTest created and passing
- [ ] Task 6: MaterialEntityTest created and passing
- [ ] Task 7: SolicitacaoEntityTest created and passing
- [ ] Task 8: MaterialServiceTest created and passing
- [ ] Task 9: ComentarioServiceTest created and passing
- [ ] Task 10: SolicitacaoServiceTest created and passing
- [ ] Task 11: DemandaServiceTest created and passing
- [ ] Task 12: ArquivoServiceTest expanded and passing
- [ ] Task 13: MinioServiceTest created and passing
- [ ] Task 14: DocumentoPdfServiceTest created and passing
- [ ] Task 15: GenericAuditServiceTest created and passing
- [ ] All validation commands pass
- [ ] JaCoCo 80%+ instruction coverage confirmed

---

## NOTES

### JaCoCo Exclusions (Already excluded from coverage)
These do NOT need tests as they are excluded from JaCoCo measurement:
- Config package (`com/sigesi/sigesi/config/**`)
- Authentication package (`com/sigesi/sigesi/authentication/**`)
- All DTOs (`com/sigesi/sigesi/**/dtos/**`)
- MapStruct generated implementations (`*MapperImpl.class`)
- All enums
- Exception classes (NotFoundException, ConflictException, StorageException, InvalidFileException)
- Notifications package
- SigesiApplication main class

### Priority Order Rationale
1. **FileValidator first** - Security-critical code, simple to test, high impact
2. **Entities second** - Simple tests with Jakarta Validation, quick wins for coverage
3. **Simple services third** - MaterialService, ComentarioService follow established patterns
4. **Complex services fourth** - DemandaService, SolicitacaoService need careful mocking
5. **Infrastructure last** - MinioService, DocumentoPdfService, GenericAuditService have external dependencies

### Risk Considerations
- **DemandaService** is the most complex service with transactions, notifications, and material resolution - highest risk of test failures during implementation
- **GenericAuditService** requires Envers mocking which is non-trivial - may need MockedStatic for AuditReaderFactory
- **DocumentoPdfService** loads classpath resources (background image) - tests need proper resource setup or mocking
- **MinioClient** has checked exceptions - Mockito doThrow/thenThrow patterns needed

### Estimated Confidence Score: 8/10
High confidence because:
- All existing test patterns are well-established and consistent
- The tests to write follow identical patterns to existing tests
- Dependencies and mocking strategies are clear
- JaCoCo exclusions reduce the surface area needing coverage

Risk factors:
- DemandaService complexity with @Transactional and NotificationPublisher
- GenericAuditService Envers mocking may be tricky
- DocumentoPdfService classpath resource loading in tests

<!-- EOF -->

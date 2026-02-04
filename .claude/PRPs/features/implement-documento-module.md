# Feature: Implementar Modulo Documento

## Feature Description

Implementar o modulo completo de Documento no SIGESI. Um documento representa um oficio ou memorando oficial emitido pelo sistema, contendo titulo, corpo, data, tipo (enum: OFICIO ou MEMORANDO), assinante (Pessoa), interessado (Pessoa), destino (Endereco) e uma lista de anexos (Arquivo). Os anexos sao modelados como uma relacao ManyToMany entre Documento e Arquivo (uma vez que um documento pode ter muitos anexos e um arquivo pode ser referenciado por varios documentos), seguindo o mesmo padrao ja usado em Solicitacao com Arquivo.

## User Story

As a usuario do sistema SIGESI
I want to criar, visualizar, atualizar e deletar documentos (oficios e memorandos)
So that possa gerenciar a documentacao oficial da Secretaria de Infraestrutura

## Problem Statement

O sistema SIGESI nao possui modulo para gerenciamento de documentos oficiais (oficios e memorandos). E necessario implementar a entidade Documento com todos os seus relacionamentos, DTOs, servico, controlador, repositorio, mapper e testes.

## Solution Statement

Criar um modulo completo `documentos` seguindo os padroes ja estabelecidos no projeto (semelhante ao modulo `solicitacoes`), com:
- Entidade `Documento` com campos: id, data, title, body, tipo (enum), assinante (Pessoa), interessado (Pessoa), destino (Endereco), anexos (ManyToMany com Arquivo)
- Enum `DocumentoTipo` com valores OFICIO e MEMORANDO
- DTOs: CreateDTO, UpdateDTO, ResponseDTO
- MapStruct Mapper
- Service com CRUD completo
- Controller REST em `/api/documentos`
- Repository JPA
- Testes unitarios: Entity, Service, Controller

## Feature Metadata

**Feature Type**: New Capability
**Estimated Complexity**: Medium
**Primary Systems Affected**: documentos (new module), arquivos (existing - referenced), pessoas (existing - referenced), enderecos (existing - referenced)
**Dependencies**: Nenhuma dependencia externa nova; usa Arquivo, Pessoa e Endereco existentes

---

## CONTEXT REFERENCES

### Relevant Codebase Files

- `src/main/java/com/sigesi/sigesi/solicitacoes/Solicitacao.java` (lines 1-77) - Why: Padrao de entidade com ManyToMany para Arquivo, ManyToOne para Usuario/Endereco, @PrePersist e @Enumerated
- `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoController.java` (lines 1-63) - Why: Padrao de controller REST com DTOs
- `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoService.java` (lines 1-87) - Why: Padrao de service com resolucao de FKs e mapper
- `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoMapper.java` (lines 1-64) - Why: Padrao de mapper com default methods para mapear IDs para entidades
- `src/main/java/com/sigesi/sigesi/solicitacoes/dtos/SolicitacaoCreateDTO.java` (lines 1-32) - Why: Padrao de CreateDTO com validacao e lista de IDs
- `src/main/java/com/sigesi/sigesi/solicitacoes/dtos/SolicitacaoResponseDTO.java` (lines 1-46) - Why: Padrao de ResponseDTO com @Builder e entidades embarcadas
- `src/main/java/com/sigesi/sigesi/solicitacoes/dtos/SolicitacaoUpdateDTO.java` (lines 1-23) - Why: Padrao de UpdateDTO com @Schema
- `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoRepository.java` (lines 1-18) - Why: Padrao de repository com findAllByOrderByIdAsc()
- `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoAssunto.java` - Why: Padrao de enum simples
- `src/main/java/com/sigesi/sigesi/demandas/DemandaStatus.java` - Why: Padrao de enum com Javadoc
- `src/main/java/com/sigesi/sigesi/pessoas/Pessoa.java` (lines 1-49) - Why: Entidade referenciada por assinante e interessado
- `src/main/java/com/sigesi/sigesi/pessoas/PessoaService.java` - Why: Service referenciado para resolver IDs de Pessoa
- `src/main/java/com/sigesi/sigesi/enderecos/Endereco.java` (lines 1-40) - Why: Entidade referenciada por destino
- `src/main/java/com/sigesi/sigesi/enderecos/EnderecoService.java` - Why: Service referenciado para resolver ID de Endereco
- `src/main/java/com/sigesi/sigesi/arquivos/Arquivo.java` (lines 1-60) - Why: Entidade para relacao ManyToMany com anexos
- `src/main/java/com/sigesi/sigesi/arquivos/ArquivoService.java` - Why: Service referenciado para resolver IDs de Arquivo
- `src/main/java/com/sigesi/sigesi/arquivos/dtos/ArquivoResponseDTO.java` - Why: DTO usado no ResponseDTO para anexos
- `src/main/java/com/sigesi/sigesi/config/SpringConfig.java` (lines 1-47) - Why: Precisa adicionar regra de acesso para /api/documentos
- `src/main/java/com/sigesi/sigesi/config/NotFoundException.java` - Why: Excecao padrao para 404
- `src/test/java/com/sigesi/sigesi/enderecos/EnderecoControllerTest.java` (lines 1-192) - Why: Padrao de teste de controller com @WebMvcTest
- `src/test/java/com/sigesi/sigesi/enderecos/EnderecoServiceTest.java` (lines 1-174) - Why: Padrao de teste de service com @ExtendWith(MockitoExtension)
- `src/test/java/com/sigesi/sigesi/enderecos/EnderecoEntityTest.java` (lines 1-220) - Why: Padrao de teste de entidade com Jakarta Validation
- `checkstyle.xml` - Why: Regras de checkstyle que devem ser seguidas

### New Files to Create

- `src/main/java/com/sigesi/sigesi/documentos/Documento.java` - Entidade JPA
- `src/main/java/com/sigesi/sigesi/documentos/DocumentoTipo.java` - Enum com OFICIO e MEMORANDO
- `src/main/java/com/sigesi/sigesi/documentos/DocumentoController.java` - Controller REST
- `src/main/java/com/sigesi/sigesi/documentos/DocumentoService.java` - Service layer
- `src/main/java/com/sigesi/sigesi/documentos/DocumentoRepository.java` - JPA Repository
- `src/main/java/com/sigesi/sigesi/documentos/DocumentoMapper.java` - MapStruct Mapper
- `src/main/java/com/sigesi/sigesi/documentos/dtos/DocumentoCreateDTO.java` - DTO de criacao
- `src/main/java/com/sigesi/sigesi/documentos/dtos/DocumentoUpdateDTO.java` - DTO de atualizacao
- `src/main/java/com/sigesi/sigesi/documentos/dtos/DocumentoResponseDTO.java` - DTO de resposta
- `src/test/java/com/sigesi/sigesi/documentos/DocumentoEntityTest.java` - Testes da entidade
- `src/test/java/com/sigesi/sigesi/documentos/DocumentoServiceTest.java` - Testes do service
- `src/test/java/com/sigesi/sigesi/documentos/DocumentoControllerTest.java` - Testes do controller

### Patterns to Follow

**Entity Pattern:**
```java
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NomeEntidade {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  // ... fields
}
```
- Javadoc obrigatorio na classe: `/** Entidade Documento. */`
- Enums com `@Enumerated(EnumType.STRING)` e `@Column(name = "...")`
- ManyToOne com `@JoinColumn(name = "..._id")`
- ManyToMany com `@JoinTable` (ver Solicitacao.java:49-55)
- @PrePersist para setar data automaticamente

**Controller Pattern:**
- `@RestController`, `@RequestMapping("/api/documentos")`, `@Tag(name = "documentos")`
- Javadoc obrigatorio: `/** Controlador REST para Documento. */`
- GET `/` e `/{id}`, POST `/`, PATCH `/{id}`, DELETE `/{id}`
- POST retorna `HttpStatus.CREATED`, DELETE retorna `noContent().build()`
- `@Valid @RequestBody` nos POST/PATCH

**Service Pattern:**
- `@Service` com `@Autowired` para Repository, Mapper e services dependentes
- Javadoc obrigatorio: `/** Servico para Documento. */`
- Metodo `getDocumentoEntityById(Long id)` publico para uso cross-service
- Usa `NotFoundException` para entidades nao encontradas
- No create: resolve FKs via services dependentes (PessoaService, EnderecoService, ArquivoService)

**Mapper Pattern:**
- `@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)`
- Javadoc obrigatorio: `/** Mapper para Documento. */`
- `toEntity()` ignora `id` e campos de relacionamento
- `updateFromDto()` com `@MappingTarget`
- Default methods para mapear Long IDs para entidades stub

**DTO Pattern:**
- CreateDTO: `@Data @AllArgsConstructor @NoArgsConstructor` com validacao `@NotBlank`/`@NotNull`
- UpdateDTO: `@Data @AllArgsConstructor @NoArgsConstructor` com `@Schema` do Swagger
- ResponseDTO: `@Data @AllArgsConstructor @NoArgsConstructor @Builder`
- Javadoc obrigatorio em todos os DTOs

**Repository Pattern:**
- `extends JpaRepository<Documento, Long>`
- Javadoc obrigatorio: `/** Repositorio para Documento. */`
- `findAllByOrderByIdAsc()` obrigatorio

**Test Pattern:**
- Controller: `@WebMvcTest`, `@AutoConfigureMockMvc(addFilters = false)`, `@MockitoBean`
- Service: `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`
- Entity: `Validator` do Jakarta Validation
- Todos com `@DisplayName` na classe e nos metodos

**Naming:**
- Imports devem ser explicitos (sem wildcards) - exceto em testes
- Mensagens de validacao em portugues: "Titulo e obrigatorio", "Corpo e obrigatorio"
- Mensagem NotFoundException: "Documento nao encontrado com id "

---

## IMPLEMENTATION PLAN

### Phase 1: Foundation - Enum e Entidade

Criar o enum `DocumentoTipo` e a entidade `Documento` com todos os campos e relacionamentos.

**Tasks:**
- Criar enum DocumentoTipo (OFICIO, MEMORANDO)
- Criar entidade Documento com todos os campos do diagrama
- Relacionamentos: assinante (ManyToOne Pessoa), interessado (ManyToOne Pessoa), destino (ManyToOne Endereco), anexos (ManyToMany Arquivo)

### Phase 2: DTOs e Mapper

Criar os DTOs e o MapStruct mapper para conversao entre entidades e DTOs.

**Tasks:**
- Criar DocumentoCreateDTO com validacao
- Criar DocumentoUpdateDTO com campos opcionais
- Criar DocumentoResponseDTO com @Builder
- Criar DocumentoMapper com toEntity, toDto, toDtoList, updateFromDto e default methods

### Phase 3: Repository, Service e Controller

Implementar a camada de persistencia, logica de negocio e endpoints REST.

**Tasks:**
- Criar DocumentoRepository
- Criar DocumentoService com CRUD completo
- Criar DocumentoController com endpoints REST
- Atualizar SpringConfig para adicionar regra de acesso /api/documentos

### Phase 4: Testing

Criar testes unitarios para entidade, service e controller.

**Tasks:**
- Criar DocumentoEntityTest
- Criar DocumentoServiceTest
- Criar DocumentoControllerTest

---

## STEP-BY-STEP TASKS

### Task 1: CREATE `src/main/java/com/sigesi/sigesi/documentos/DocumentoTipo.java`

- **IMPLEMENT**: Enum com dois valores: `OFICIO` e `MEMORANDO`
- **PATTERN**: Mirror `src/main/java/com/sigesi/sigesi/demandas/DemandaStatus.java` (enum com Javadoc)
- **GOTCHA**: Checkstyle exige Javadoc na declaracao do tipo. Adicionar: `/** Tipos possiveis para um Documento. */`
- **VALIDATE**: `mvn checkstyle:check -pl .`

### Task 2: CREATE `src/main/java/com/sigesi/sigesi/documentos/Documento.java`

- **IMPLEMENT**: Entidade JPA com os seguintes campos:
  - `id`: Long, PK, GenerationType.IDENTITY
  - `data`: LocalDate, nullable=false, com @PrePersist para setar LocalDate.now() se null
  - `title`: String, @NotBlank(message = "Titulo e obrigatorio"), @Column(nullable = false)
  - `body`: String, @NotBlank(message = "Corpo e obrigatorio"), @Column(nullable = false, columnDefinition = "TEXT")
  - `tipo`: DocumentoTipo, @Enumerated(EnumType.STRING), @Column(name = "tipo")
  - `assinante`: Pessoa, @ManyToOne, @JoinColumn(name = "assinante_id", nullable = false), @NotNull(message = "Assinante e obrigatorio")
  - `interessado`: Pessoa, @ManyToOne, @JoinColumn(name = "interessado_id", nullable = false), @NotNull(message = "Interessado e obrigatorio")
  - `destino`: Endereco, @ManyToOne, @JoinColumn(name = "destino_id", nullable = false), @NotNull(message = "Destino e obrigatorio")
  - `anexos`: List<Arquivo>, @ManyToMany com @JoinTable(name = "documento_arquivos", joinColumns = @JoinColumn(name = "documento_id"), inverseJoinColumns = @JoinColumn(name = "arquivo_id"))
- **PATTERN**: Mirror `src/main/java/com/sigesi/sigesi/solicitacoes/Solicitacao.java` para a estrutura geral, @PrePersist e @ManyToMany com @JoinTable
- **IMPORTS**: jakarta.persistence.*, jakarta.validation.constraints.*, lombok.*, java.time.LocalDate, Pessoa, Endereco, Arquivo
- **GOTCHA**: Javadoc obrigatorio: `/** Entidade Documento. */`. Usar @Entity @Data @AllArgsConstructor @NoArgsConstructor @Builder. A anotacao @ManyToMany com @JoinTable deve seguir o mesmo formato de Solicitacao.java (com @formatter:off/@formatter:on se necessario para nao exceder 140 chars).
- **VALIDATE**: `mvn checkstyle:check -pl .`

### Task 3: CREATE `src/main/java/com/sigesi/sigesi/documentos/dtos/DocumentoCreateDTO.java`

- **IMPLEMENT**: DTO para criacao com campos:
  - `title`: String, @NotBlank(message = "Titulo e obrigatorio")
  - `body`: String, @NotBlank(message = "Corpo e obrigatorio")
  - `tipo`: DocumentoTipo, @NotNull(message = "Tipo e obrigatorio")
  - `assinanteId`: Long, @NotNull(message = "Assinante e obrigatorio")
  - `interessadoId`: Long, @NotNull(message = "Interessado e obrigatorio")
  - `destinoId`: Long, @NotNull(message = "Destino e obrigatorio")
  - `anexoIds`: List<Long> (opcional, sem validacao)
- **PATTERN**: Mirror `src/main/java/com/sigesi/sigesi/solicitacoes/dtos/SolicitacaoCreateDTO.java`
- **GOTCHA**: Javadoc obrigatorio: `/** DTO para criacao de documento. */`. Usar @Data @AllArgsConstructor @NoArgsConstructor.
- **VALIDATE**: `mvn checkstyle:check -pl .`

### Task 4: CREATE `src/main/java/com/sigesi/sigesi/documentos/dtos/DocumentoUpdateDTO.java`

- **IMPLEMENT**: DTO para atualizacao com campos opcionais:
  - `title`: String (opcional)
  - `body`: String (opcional)
  - `tipo`: DocumentoTipo (opcional), @Schema(description = "Tipo do documento", example = "OFICIO")
  - `assinanteId`: Long (opcional)
  - `interessadoId`: Long (opcional)
  - `destinoId`: Long (opcional)
  - `anexoIds`: List<Long> (opcional)
- **PATTERN**: Mirror `src/main/java/com/sigesi/sigesi/solicitacoes/dtos/SolicitacaoUpdateDTO.java`
- **IMPORTS**: io.swagger.v3.oas.annotations.media.Schema, DocumentoTipo
- **GOTCHA**: Javadoc obrigatorio: `/** DTO para atualizacao de documento. */`. Campos opcionais para permitir PATCH parcial.
- **VALIDATE**: `mvn checkstyle:check -pl .`

### Task 5: CREATE `src/main/java/com/sigesi/sigesi/documentos/dtos/DocumentoResponseDTO.java`

- **IMPLEMENT**: DTO de resposta com campos:
  - `id`: long
  - `data`: LocalDate
  - `title`: String
  - `body`: String
  - `tipo`: DocumentoTipo
  - `assinante`: Pessoa (entidade embarcada diretamente, como em SolicitacaoResponseDTO com Usuario)
  - `interessado`: Pessoa (entidade embarcada)
  - `destino`: Endereco (entidade embarcada)
  - `anexos`: List<ArquivoResponseDTO>
- **PATTERN**: Mirror `src/main/java/com/sigesi/sigesi/solicitacoes/dtos/SolicitacaoResponseDTO.java`
- **GOTCHA**: Javadoc obrigatorio: `/** DTO de resposta para documento. */`. Usar @Data @AllArgsConstructor @NoArgsConstructor @Builder.
- **VALIDATE**: `mvn checkstyle:check -pl .`

### Task 6: CREATE `src/main/java/com/sigesi/sigesi/documentos/DocumentoMapper.java`

- **IMPLEMENT**: Interface MapStruct mapper com:
  - `@Mapping(target = "id", ignore = true)` + `@Mapping(target = "data", ignore = true)` + `@Mapping(target = "assinante", source = "assinanteId")` + `@Mapping(target = "interessado", source = "interessadoId")` + `@Mapping(target = "destino", source = "destinoId")` + `@Mapping(target = "anexos", source = "anexoIds")` em `toEntity(DocumentoCreateDTO dto)`
  - `toDto(Documento entity)` - mapeamento automatico
  - `toDtoList(List<Documento> entities)`
  - `updateFromDto(DocumentoUpdateDTO dto, @MappingTarget Documento documento)` com `@Mapping(target = "id", ignore = true)`, `@Mapping(target = "data", ignore = true)` e ignore para todos os campos de relacionamento (assinante, interessado, destino, anexos) - serao tratados manualmente no service
  - Default methods: `mapAssinante(Long id)`, `mapInteressado(Long id)`, `mapDestino(Long id)`, `mapAnexos(List<Long> ids)` - cada um cria entidade stub via Builder
- **PATTERN**: Mirror `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoMapper.java` (lines 1-64)
- **GOTCHA**: Javadoc obrigatorio: `/** Mapper para Documento. */`. As default methods devem verificar null antes de criar stubs.
- **VALIDATE**: `mvn checkstyle:check -pl .`

### Task 7: CREATE `src/main/java/com/sigesi/sigesi/documentos/DocumentoRepository.java`

- **IMPLEMENT**: Interface repository extendendo JpaRepository<Documento, Long> com:
  - `findAllByOrderByIdAsc()`
  - `findByAssinanteIdOrderByDataDesc(Long assinanteId)` - buscar por assinante
  - `findByInteressadoIdOrderByDataDesc(Long interessadoId)` - buscar por interessado
  - `findByTipo(DocumentoTipo tipo)` - filtrar por tipo
- **PATTERN**: Mirror `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoRepository.java`
- **GOTCHA**: Javadoc obrigatorio: `/** Repositorio para Documento. */`. Usar @Repository.
- **VALIDATE**: `mvn checkstyle:check -pl .`

### Task 8: CREATE `src/main/java/com/sigesi/sigesi/documentos/DocumentoService.java`

- **IMPLEMENT**: Service com CRUD completo:
  - `getAll()`: retorna List<DocumentoResponseDTO> usando findAllByOrderByIdAsc() e stream().map(mapper::toDto)
  - `getDocumentoById(Long id)`: retorna DocumentoResponseDTO, lanca NotFoundException
  - `createDocumento(DocumentoCreateDTO dto)`: resolve assinante via PessoaService.getPessoEntityById(), interessado via PessoaService.getPessoEntityById(), destino via EnderecoService.getEnderecoEntityById(), anexos via ArquivoService.getArquivoEntityById() em loop. Seta entidades no entity apos mapper.toEntity()
  - `updateDocumento(Long id, DocumentoUpdateDTO dto)`: busca entidade, aplica mapper.updateFromDto() para campos escalares, resolve FKs manualmente se os IDs estiverem presentes no DTO (null check), salva e retorna DTO
  - `deleteDocumento(Long id)`: busca entidade, deleta
  - `getDocumentoEntityById(Long id)`: publico, retorna Documento entity
- **PATTERN**: Mirror `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoService.java` (lines 1-87)
- **IMPORTS**: PessoaService, EnderecoService, ArquivoService, NotFoundException, Collectors
- **GOTCHA**: Javadoc obrigatorio: `/** Servico para Documento. */`. No updateDocumento, alem do mapper.updateFromDto(), tratar manualmente os campos de FK: se dto.getAssinanteId() != null, resolver via PessoaService e setar. Idem para interessadoId, destinoId e anexoIds. Cuidado com o limite de 50 linhas por metodo e 3 returns max - pode ser necessario extrair metodo auxiliar para resolucao de FKs no update.
- **VALIDATE**: `mvn checkstyle:check -pl .`

### Task 9: CREATE `src/main/java/com/sigesi/sigesi/documentos/DocumentoController.java`

- **IMPLEMENT**: Controller REST com endpoints:
  - `GET /` - listAll() retorna ResponseEntity<List<DocumentoResponseDTO>>
  - `GET /{id}` - getDocumentoById(@PathVariable Long id)
  - `POST /` - createDocumento(@Valid @RequestBody DocumentoCreateDTO dto) retorna 201
  - `PATCH /{id}` - updateDocumento(@PathVariable Long id, @Valid @RequestBody DocumentoUpdateDTO dto)
  - `DELETE /{id}` - deleteDocumento(@PathVariable Long id) retorna 204
- **PATTERN**: Mirror `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoController.java` (lines 1-63)
- **GOTCHA**: Javadoc obrigatorio: `/** Controlador REST para Documento. */`. Usar @Tag(name = "documentos") para Swagger.
- **VALIDATE**: `mvn checkstyle:check -pl .`

### Task 10: UPDATE `src/main/java/com/sigesi/sigesi/config/SpringConfig.java`

- **IMPLEMENT**: Adicionar regra de acesso para `/api/documentos/**` na cadeia de autorizacao. Colocar antes de `.anyRequest().authenticated()`. Decidir quais roles tem acesso - provavelmente OPERADOR e ADMIN como cemiterios/jazigos/gavetas, ja que documentos oficiais sao funcoes administrativas.
- **ADD**: `.requestMatchers("/api/documentos/**").hasAnyRole("OPERADOR", "ADMIN")` apos a linha de gavetas (line 33)
- **PATTERN**: Mirror linhas 31-33 de SpringConfig.java
- **GOTCHA**: A ordem dos matchers importa. Adicionar antes de `.anyRequest().authenticated()`.
- **VALIDATE**: `mvn checkstyle:check -pl .`

### Task 11: CREATE `src/test/java/com/sigesi/sigesi/documentos/DocumentoEntityTest.java`

- **IMPLEMENT**: Testes de validacao da entidade Documento:
  - Teste title nulo lanca violacao
  - Teste title vazio lanca violacao
  - Teste title apenas espacos lanca violacao
  - Teste body nulo lanca violacao
  - Teste body vazio lanca violacao
  - Teste assinante nulo lanca violacao
  - Teste interessado nulo lanca violacao
  - Teste destino nulo lanca violacao
  - Teste criacao documento valido sem violacoes
  - Teste Builder pattern
  - Teste Lombok getters/setters
- **PATTERN**: Mirror `src/test/java/com/sigesi/sigesi/enderecos/EnderecoEntityTest.java` (lines 1-220)
- **GOTCHA**: Javadoc obrigatorio: `/** Testes unitarios para a entidade Documento. */`. @DisplayName em todos os testes e na classe.
- **VALIDATE**: `mvn test -Dtest=DocumentoEntityTest`

### Task 12: CREATE `src/test/java/com/sigesi/sigesi/documentos/DocumentoServiceTest.java`

- **IMPLEMENT**: Testes do service:
  - getAll retorna lista vazia
  - getAll retorna lista com documentos
  - getDocumentoById com sucesso
  - getDocumentoById lanca NotFoundException
  - createDocumento com sucesso (mockando PessoaService, EnderecoService, ArquivoService)
  - updateDocumento com sucesso
  - deleteDocumento com sucesso
  - deleteDocumento lanca NotFoundException
  - getDocumentoEntityById com sucesso
- **PATTERN**: Mirror `src/test/java/com/sigesi/sigesi/enderecos/EnderecoServiceTest.java` (lines 1-174)
- **IMPORTS**: @Mock para DocumentoRepository, DocumentoMapper, PessoaService, EnderecoService, ArquivoService. @InjectMocks para DocumentoService.
- **GOTCHA**: Javadoc obrigatorio: `/** Testes para DocumentoService. */`. Mockar todos os services dependentes. @DisplayName em todos os testes.
- **VALIDATE**: `mvn test -Dtest=DocumentoServiceTest`

### Task 13: CREATE `src/test/java/com/sigesi/sigesi/documentos/DocumentoControllerTest.java`

- **IMPLEMENT**: Testes do controller:
  - GET / retorna 200 com lista vazia
  - GET / retorna 200 com multiplos documentos
  - GET /{id} retorna 200 quando encontrado
  - GET /{id} retorna 404 quando nao encontrado
  - POST / retorna 201 quando criado com sucesso
  - POST / retorna 400 quando dados invalidos (title vazio)
  - PATCH /{id} retorna 200 com dados atualizados
  - PATCH /{id} retorna 404 quando nao existe
  - DELETE /{id} retorna 204 quando deletado com sucesso
  - DELETE /{id} retorna 404 quando nao existe
- **PATTERN**: Mirror `src/test/java/com/sigesi/sigesi/enderecos/EnderecoControllerTest.java` (lines 1-192)
- **IMPORTS**: @WebMvcTest(controllers = DocumentoController.class), @AutoConfigureMockMvc(addFilters = false), @MockitoBean DocumentoService
- **GOTCHA**: Javadoc obrigatorio: `/** Testes para DocumentoController. */`. Helper methods para construir DTOs de teste. @DisplayName em todos os testes.
- **VALIDATE**: `mvn test -Dtest=DocumentoControllerTest`

---

## TESTING STRATEGY

### Unit Tests

**Entity Tests (DocumentoEntityTest):**
- Validacao de constraints com Jakarta Validator
- Testes para cada campo obrigatorio (nulo, vazio, espacos)
- Testes de Builder pattern e Lombok getters/setters

**Service Tests (DocumentoServiceTest):**
- Mock do repository, mapper e services dependentes
- Testes de cada operacao CRUD
- Testes de excecoes (NotFoundException)

**Controller Tests (DocumentoControllerTest):**
- @WebMvcTest com security desabilitada
- Mock do service
- Testes de cada endpoint HTTP
- Testes de validacao (400 Bad Request)
- Testes de entidade nao encontrada (404)

### Edge Cases

- Criar documento sem anexos (anexoIds null ou lista vazia)
- Criar documento com tipo invalido (tratado pelo EnumExceptionHandler existente)
- Atualizar documento com PATCH parcial (apenas titulo, apenas tipo, etc.)
- Atualizar relacionamentos individuais (apenas assinante, apenas destino)
- Assinante e interessado podem ser a mesma Pessoa

---

## VALIDATION COMMANDS

### Level 1: Syntax & Style

```bash
mvn checkstyle:check
```

### Level 2: Unit Tests

```bash
mvn test -Dtest=DocumentoEntityTest
mvn test -Dtest=DocumentoServiceTest
mvn test -Dtest=DocumentoControllerTest
```

### Level 3: Full Test Suite

```bash
mvn test
```

### Level 4: Build Completo

```bash
mvn clean install -DskipTests=false
```

---

## ACCEPTANCE CRITERIA

- [ ] Enum DocumentoTipo criado com OFICIO e MEMORANDO
- [ ] Entidade Documento criada com todos os campos do diagrama
- [ ] Relacionamentos corretos: assinante/interessado (ManyToOne Pessoa), destino (ManyToOne Endereco), anexos (ManyToMany Arquivo)
- [ ] @PrePersist seta data automaticamente
- [ ] DTOs criados (Create, Update, Response) com validacao adequada
- [ ] MapStruct Mapper funcional com toEntity, toDto, toDtoList, updateFromDto
- [ ] Repository com findAllByOrderByIdAsc e queries uteis
- [ ] Service com CRUD completo e resolucao de FKs
- [ ] Controller REST em /api/documentos com 5 endpoints
- [ ] SpringConfig atualizado com regra de acesso para /api/documentos
- [ ] Todos os testes passam (entity, service, controller)
- [ ] Checkstyle passa sem violacoes
- [ ] Build completo (mvn clean install) passa
- [ ] Swagger UI mostra os endpoints de documentos

---

## COMPLETION CHECKLIST

- [ ] All tasks completed in order (1-13)
- [ ] Each task validation passed immediately
- [ ] All validation commands executed successfully
- [ ] Full test suite passes (unit)
- [ ] No linting or Checkstyle errors
- [ ] Acceptance criteria all met

---

## NOTES

### Design Decisions

1. **Anexos como ManyToMany**: O diagrama mostra `anexo: LIST<STRING>`, mas conforme especificado pelo usuario, isso deve ser modelado como ManyToMany com Arquivo (mesmo padrao de Solicitacao), ja que um documento pode ter muitos anexos.

2. **Tipo como Enum**: Conforme especificado, tipo sera um enum `DocumentoTipo` com valores `OFICIO` e `MEMORANDO`, armazenado como STRING no banco.

3. **Roles de acesso**: Documentos oficiais (oficios e memorandos) sao funcoes administrativas, entao acesso restrito a OPERADOR e ADMIN, similar a cemiterios/jazigos/gavetas. Isso pode ser ajustado posteriormente se necessario.

4. **Update parcial (PATCH)**: O updateDocumento trata campos escalares via mapper e relacionamentos manualmente, permitindo atualizacao parcial de qualquer campo.

5. **Campo data com @PrePersist**: A data sera setada automaticamente ao criar o documento, similar ao padrao usado em Solicitacao.

### Potential Risks

- O metodo `updateDocumento` pode ficar proximo do limite de 50 linhas do Checkstyle se todos os campos de FK forem tratados inline. Solucao: extrair metodo auxiliar `resolveRelationships()` ou similar.
- O metodo no PessoaService se chama `getPessoEntityById()` (sem o 'a' no final de 'Pesso'), NAO `getPessoaEntityById()`. Usar o nome correto: `pessoaService.getPessoEntityById(id)`.

<!-- EOF -->

# Feature: Implementar Demanda, Material e Comentario

## Feature Description

Implementar as entidades `Demanda`, `Material` e `Comentario` com seus relacionamentos:
- **Demanda**: Representa uma demanda de trabalho vinculada a uma Solicitacao, com responsavel (Usuario/Agente), prazo, status e materiais necessarios
- **Material**: Representa materiais que podem ser associados a multiplas demandas (relacionamento ManyToMany)
- **Comentario**: Representa comentarios feitos em uma demanda (relacionamento ManyToOne)

## User Story

As a gestor do sistema
I want to criar demandas a partir de solicitacoes, associar materiais e adicionar comentarios
So that posso gerenciar o trabalho da equipe de infraestrutura de forma organizada

## Problem Statement

O sistema SIGESI precisa de um fluxo de trabalho onde solicitacoes dos cidadaos gerem demandas internas para a equipe. Cada demanda precisa:
- Ter um responsavel (agente) atribuido
- Ter prazo e status de acompanhamento
- Poder ter materiais associados (M:N)
- Permitir comentarios/anotacoes da equipe (1:N)

## Solution Statement

Implementar tres novas entidades seguindo os padroes estabelecidos no projeto:
1. `Material` - entidade independente com nome e preco
2. `Demanda` - entidade principal com FK para Solicitacao e Usuario, ManyToMany com Material
3. `Comentario` - entidade filha de Demanda com ManyToOne

## Feature Metadata

**Feature Type**: New Capability
**Estimated Complexity**: Medium
**Primary Systems Affected**: demandas (novo), materiais (novo), comentarios (novo), solicitacoes (update)
**Dependencies**: Solicitacao (existente), Usuario (existente)

---

## CONTEXT REFERENCES

### Relevant Codebase Files

**Entity Patterns:**
- `src/main/java/com/sigesi/sigesi/solicitacoes/Solicitacao.java` (lines 1-73) - Why: Modelo de entidade com @Enumerated, @ManyToOne, @PrePersist
- `src/main/java/com/sigesi/sigesi/jazigos/Jazigo.java` (lines 1-51) - Why: Exemplo de @ManyToOne com @NotNull
- `src/main/java/com/sigesi/sigesi/usuarios/Usuario.java` (lines 1-37) - Why: Entidade do responsavel (agente)
- `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoStatus.java` (lines 1-9) - Why: Padrao de enum para status

**DTO Patterns:**
- `src/main/java/com/sigesi/sigesi/solicitacoes/dtos/SolicitacaoCreateDTO.java` (lines 1-32) - Why: Padrao de CreateDTO com IDs para relacionamentos
- `src/main/java/com/sigesi/sigesi/solicitacoes/dtos/SolicitacaoResponseDTO.java` (lines 1-46) - Why: Padrao de ResponseDTO com entidades aninhadas
- `src/main/java/com/sigesi/sigesi/solicitacoes/dtos/SolicitacaoUpdateDTO.java` (lines 1-23) - Why: Padrao de UpdateDTO minimo

**Service Patterns:**
- `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoService.java` (lines 1-84) - Why: Servico com multiplos relacionamentos
- `src/main/java/com/sigesi/sigesi/cemiterios/CemiterioService.java` (lines 1-77) - Why: Padrao de resolucao de FK em create/update

**Mapper Patterns:**
- `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoMapper.java` (lines 1-62) - Why: Mapper com custom methods para IDs
- `src/main/java/com/sigesi/sigesi/enderecos/EnderecoMapper.java` (lines 1-26) - Why: Mapper basico

**Controller Patterns:**
- `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoController.java` (lines 1-63) - Why: Controller REST completo
- `src/main/java/com/sigesi/sigesi/enderecos/EnderecoController.java` (lines 1-63) - Why: Controller com CRUD completo

**Test Patterns:**
- `src/test/java/com/sigesi/sigesi/solicitacoes/SolicitacaoControllerTest.java` (lines 1-228) - Why: Testes de controller com MockMvc

**Exception Patterns:**
- `src/main/java/com/sigesi/sigesi/config/NotFoundException.java` (lines 1-14) - Why: Exception para 404
- `src/main/java/com/sigesi/sigesi/config/ConflictException.java` (lines 1-18) - Why: Exception para 409

### New Files to Create

**Material Module:**
- `src/main/java/com/sigesi/sigesi/materiais/Material.java` - Entidade Material
- `src/main/java/com/sigesi/sigesi/materiais/MaterialRepository.java` - Repository
- `src/main/java/com/sigesi/sigesi/materiais/MaterialService.java` - Service
- `src/main/java/com/sigesi/sigesi/materiais/MaterialController.java` - Controller REST
- `src/main/java/com/sigesi/sigesi/materiais/MaterialMapper.java` - MapStruct mapper
- `src/main/java/com/sigesi/sigesi/materiais/dtos/MaterialCreateDTO.java` - DTO criacao
- `src/main/java/com/sigesi/sigesi/materiais/dtos/MaterialUpdateDTO.java` - DTO atualizacao
- `src/main/java/com/sigesi/sigesi/materiais/dtos/MaterialResponseDTO.java` - DTO resposta
- `src/test/java/com/sigesi/sigesi/materiais/MaterialControllerTest.java` - Testes

**Demanda Module:**
- `src/main/java/com/sigesi/sigesi/demandas/Demanda.java` - Entidade Demanda
- `src/main/java/com/sigesi/sigesi/demandas/DemandaStatus.java` - Enum de status
- `src/main/java/com/sigesi/sigesi/demandas/DemandaRepository.java` - Repository
- `src/main/java/com/sigesi/sigesi/demandas/DemandaService.java` - Service
- `src/main/java/com/sigesi/sigesi/demandas/DemandaController.java` - Controller REST
- `src/main/java/com/sigesi/sigesi/demandas/DemandaMapper.java` - MapStruct mapper
- `src/main/java/com/sigesi/sigesi/demandas/dtos/DemandaCreateDTO.java` - DTO criacao
- `src/main/java/com/sigesi/sigesi/demandas/dtos/DemandaUpdateDTO.java` - DTO atualizacao
- `src/main/java/com/sigesi/sigesi/demandas/dtos/DemandaResponseDTO.java` - DTO resposta
- `src/test/java/com/sigesi/sigesi/demandas/DemandaControllerTest.java` - Testes

**Comentario Module:**
- `src/main/java/com/sigesi/sigesi/comentarios/Comentario.java` - Entidade Comentario
- `src/main/java/com/sigesi/sigesi/comentarios/ComentarioRepository.java` - Repository
- `src/main/java/com/sigesi/sigesi/comentarios/ComentarioService.java` - Service
- `src/main/java/com/sigesi/sigesi/comentarios/ComentarioController.java` - Controller REST
- `src/main/java/com/sigesi/sigesi/comentarios/ComentarioMapper.java` - MapStruct mapper
- `src/main/java/com/sigesi/sigesi/comentarios/dtos/ComentarioCreateDTO.java` - DTO criacao
- `src/main/java/com/sigesi/sigesi/comentarios/dtos/ComentarioResponseDTO.java` - DTO resposta
- `src/test/java/com/sigesi/sigesi/comentarios/ComentarioControllerTest.java` - Testes

### Relevant Documentation

- [JPA ManyToMany - Baeldung](https://www.baeldung.com/jpa-many-to-many)
  - Configuracao de @JoinTable
  - Why: Padrao para relacionamento M:N entre Demanda e Material

- [Vlad Mihalcea - Set vs List in ManyToMany](https://vladmihalcea.com/hibernate-facts-favoring-sets-vs-bags/)
  - Usar Set ao inves de List para M:N
  - Why: Evitar problemas de performance com delete/reinsert

- [JPA CascadeType Best Practices](https://www.baeldung.com/jpa-cascade-remove-vs-orphanremoval)
  - CascadeType.PERSIST e MERGE para M:N
  - Why: Nunca usar REMOVE em M:N

### Patterns to Follow

**Naming Conventions:**
- Entities: PascalCase singular (Demanda, Material, Comentario)
- Tables: lowercase plural (demandas, materiais, comentarios)
- Packages: lowercase singular (demandas, materiais, comentarios)
- DTOs: EntityCreateDTO, EntityUpdateDTO, EntityResponseDTO
- Controllers: `/api/{plural}/` (ex: `/api/demandas/`)

**Entity Pattern:**
```java
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Entity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  // fields...
}
```

**Relationship FK (ManyToOne):**
```java
@NotNull(message = "Campo é obrigatório")
@ManyToOne
@JoinColumn(name = "campo_id", nullable = false)
private Entity campo;
```

**ManyToMany (CRITICAL - usar Set):**
```java
@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
@JoinTable(
    name = "demanda_material",
    joinColumns = @JoinColumn(name = "demanda_id"),
    inverseJoinColumns = @JoinColumn(name = "material_id")
)
private Set<Material> materiais = new HashSet<>();
```

**OneToMany (parent side) com orphanRemoval:**
```java
@OneToMany(mappedBy = "demanda", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Comentario> comentarios = new ArrayList<>();
```

**DTO com IDs para relacionamentos:**
```java
@NotNull(message = "Responsavel é obrigatório")
private Long responsavelId;

private Set<Long> materiaisIds; // opcional
```

**Mapper ignorando relacionamentos:**
```java
@Mapping(target = "id", ignore = true)
@Mapping(target = "responsavel", ignore = true)
@Mapping(target = "materiais", ignore = true)
Demanda toEntity(DemandaCreateDTO dto);
```

**Service resolvendo relacionamentos:**
```java
public DemandaResponseDTO createDemanda(DemandaCreateDTO dto) {
    Usuario responsavel = usuarioService.getUsuarioById(dto.getResponsavelId());

    Demanda demanda = demandaMapper.toEntity(dto);
    demanda.setResponsavel(responsavel);

    if (dto.getMateriaisIds() != null && !dto.getMateriaisIds().isEmpty()) {
        Set<Material> materiais = materialService.findAllByIds(dto.getMateriaisIds());
        demanda.setMateriais(materiais);
    }

    return demandaMapper.toDto(demandaRepository.save(demanda));
}
```

**Error Handling:**
```java
return repository.findById(id)
    .orElseThrow(() -> new NotFoundException("Entidade não encontrada com id " + id));
```

---

## IMPLEMENTATION PLAN

### Phase 1: Foundation - Material Entity

Criar a entidade Material primeiro pois ela nao tem dependencias e sera referenciada por Demanda.

**Tasks:**
- Criar entidade Material com campos: id, nome, preco
- Criar DTOs (Create, Update, Response)
- Criar Repository, Mapper, Service, Controller
- Criar testes

### Phase 2: Core - Demanda Entity

Criar a entidade Demanda com todos os relacionamentos:
- ManyToOne para Solicitacao
- ManyToOne para Usuario (responsavel)
- ManyToMany para Material
- Enum DemandaStatus

**Tasks:**
- Criar enum DemandaStatus
- Criar entidade Demanda
- Criar DTOs com IDs para relacionamentos
- Criar Repository, Mapper, Service, Controller
- Criar testes

### Phase 3: Child Entity - Comentario

Criar a entidade Comentario como filha de Demanda.

**Tasks:**
- Criar entidade Comentario com ManyToOne para Demanda e Usuario
- Criar DTOs
- Criar Repository, Mapper, Service, Controller
- Criar testes

### Phase 4: Integration & Testing

Integrar e testar o fluxo completo.

**Tasks:**
- Verificar todos os endpoints
- Executar todos os testes
- Validar checkstyle

---

## STEP-BY-STEP TASKS

### FASE 1: MATERIAL

#### Task 1.1: CREATE Material Entity

**File:** `src/main/java/com/sigesi/sigesi/materiais/Material.java`

- **IMPLEMENT**: Entidade JPA com id (Long), nome (String, @NotBlank), preco (Double)
- **PATTERN**: `src/main/java/com/sigesi/sigesi/enderecos/Endereco.java:17-40`
- **IMPORTS**: jakarta.persistence.*, jakarta.validation.constraints.*, lombok.*
- **GOTCHA**: Usar Double para preco (nao BigDecimal) para manter consistencia com projeto
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.materiais;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa um material.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Material {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Nome é obrigatório")
  @Column(nullable = false)
  private String nome;

  @NotNull(message = "Preço é obrigatório")
  @Column(nullable = false)
  private Double preco;
}
```

#### Task 1.2: CREATE MaterialRepository

**File:** `src/main/java/com/sigesi/sigesi/materiais/MaterialRepository.java`

- **IMPLEMENT**: Interface JpaRepository com findAllByOrderByIdAsc() e findAllById(Set<Long>)
- **PATTERN**: `src/main/java/com/sigesi/sigesi/enderecos/EnderecoRepository.java:1-12`
- **IMPORTS**: org.springframework.data.jpa.repository.*, org.springframework.stereotype.Repository, java.util.*
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.materiais;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para Material.
 */
@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

  List<Material> findAllByOrderByIdAsc();

  Set<Material> findAllByIdIn(Set<Long> ids);
}
```

#### Task 1.3: CREATE MaterialCreateDTO

**File:** `src/main/java/com/sigesi/sigesi/materiais/dtos/MaterialCreateDTO.java`

- **IMPLEMENT**: DTO com nome (@NotBlank) e preco (@NotNull)
- **PATTERN**: `src/main/java/com/sigesi/sigesi/enderecos/dtos/EnderecoCreateDTO.java:1-28`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.materiais.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criacao de Material.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialCreateDTO {

  @NotBlank(message = "Nome é obrigatório")
  private String nome;

  @NotNull(message = "Preço é obrigatório")
  private Double preco;
}
```

#### Task 1.4: CREATE MaterialUpdateDTO

**File:** `src/main/java/com/sigesi/sigesi/materiais/dtos/MaterialUpdateDTO.java`

- **IMPLEMENT**: DTO com nome e preco (ambos opcionais para PATCH)
- **PATTERN**: `src/main/java/com/sigesi/sigesi/enderecos/dtos/EnderecoUpdateDTO.java:1-28`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.materiais.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualizacao de Material.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialUpdateDTO {

  @Schema(description = "Nome do material", example = "Cimento")
  private String nome;

  @Schema(description = "Preço do material", example = "50.00")
  private Double preco;
}
```

#### Task 1.5: CREATE MaterialResponseDTO

**File:** `src/main/java/com/sigesi/sigesi/materiais/dtos/MaterialResponseDTO.java`

- **IMPLEMENT**: DTO com id, nome, preco
- **PATTERN**: `src/main/java/com/sigesi/sigesi/enderecos/dtos/EnderecoResponseDTO.java:1-32`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.materiais.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para Material.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialResponseDTO {

  private Long id;
  private String nome;
  private Double preco;
}
```

#### Task 1.6: CREATE MaterialMapper

**File:** `src/main/java/com/sigesi/sigesi/materiais/MaterialMapper.java`

- **IMPLEMENT**: Interface MapStruct com toEntity, toDto, toDtoList, updateFromDto
- **PATTERN**: `src/main/java/com/sigesi/sigesi/enderecos/EnderecoMapper.java:1-26`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.materiais;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sigesi.sigesi.materiais.dtos.MaterialCreateDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialResponseDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialUpdateDTO;

/**
 * Mapper MapStruct para Material.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MaterialMapper {

  @Mapping(target = "id", ignore = true)
  Material toEntity(MaterialCreateDTO dto);

  MaterialResponseDTO toDto(Material entity);

  List<MaterialResponseDTO> toDtoList(List<Material> entities);

  Set<MaterialResponseDTO> toDtoSet(Set<Material> entities);

  @Mapping(target = "id", ignore = true)
  void updateFromDto(MaterialUpdateDTO dto, @MappingTarget Material entity);
}
```

#### Task 1.7: CREATE MaterialService

**File:** `src/main/java/com/sigesi/sigesi/materiais/MaterialService.java`

- **IMPLEMENT**: Service com CRUD completo + findAllByIds para M:N
- **PATTERN**: `src/main/java/com/sigesi/sigesi/enderecos/EnderecoService.java:1-60`
- **GOTCHA**: Adicionar metodo findAllByIds(Set<Long>) para uso em Demanda
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.materiais;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.materiais.dtos.MaterialCreateDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialResponseDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialUpdateDTO;

/**
 * Service para Material.
 */
@Service
public class MaterialService {

  @Autowired
  private MaterialRepository materialRepository;

  @Autowired
  private MaterialMapper materialMapper;

  /**
   * Lista todos os materiais ordenados por ID.
   */
  public List<MaterialResponseDTO> getAll() {
    return materialRepository.findAllByOrderByIdAsc()
        .stream()
        .map(materialMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Busca material por ID.
   */
  public MaterialResponseDTO getMaterialById(Long id) {
    Material material = this.getMaterialEntityById(id);
    return materialMapper.toDto(material);
  }

  /**
   * Cria novo material.
   */
  public MaterialResponseDTO createMaterial(MaterialCreateDTO dto) {
    Material entity = materialMapper.toEntity(dto);
    Material saved = materialRepository.save(entity);
    return materialMapper.toDto(saved);
  }

  /**
   * Atualiza material existente.
   */
  public MaterialResponseDTO updateMaterial(Long id, MaterialUpdateDTO dto) {
    Material material = this.getMaterialEntityById(id);
    materialMapper.updateFromDto(dto, material);
    Material updated = materialRepository.save(material);
    return materialMapper.toDto(updated);
  }

  /**
   * Deleta material por ID.
   */
  public void deleteMaterial(Long id) {
    Material material = this.getMaterialEntityById(id);
    materialRepository.delete(material);
  }

  /**
   * Busca entidade Material por ID (uso interno).
   */
  public Material getMaterialEntityById(Long id) {
    return materialRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Material não encontrado com id " + id));
  }

  /**
   * Busca multiplos materiais por IDs (para relacionamento M:N).
   */
  public Set<Material> findAllByIds(Set<Long> ids) {
    Set<Material> materiais = materialRepository.findAllByIdIn(ids);
    if (materiais.size() != ids.size()) {
      throw new NotFoundException("Um ou mais materiais não foram encontrados");
    }
    return materiais;
  }
}
```

#### Task 1.8: CREATE MaterialController

**File:** `src/main/java/com/sigesi/sigesi/materiais/MaterialController.java`

- **IMPLEMENT**: Controller REST com endpoints CRUD
- **PATTERN**: `src/main/java/com/sigesi/sigesi/enderecos/EnderecoController.java:1-63`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.materiais;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigesi.sigesi.materiais.dtos.MaterialCreateDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialResponseDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialUpdateDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para Material.
 */
@RestController
@RequestMapping("/api/materiais")
@Tag(name = "materiais")
public class MaterialController {

  @Autowired
  private MaterialService materialService;

  /**
   * Lista todos os materiais.
   */
  @GetMapping("/")
  public ResponseEntity<List<MaterialResponseDTO>> listAll() {
    List<MaterialResponseDTO> materiais = materialService.getAll();
    return ResponseEntity.ok(materiais);
  }

  /**
   * Busca material por ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<MaterialResponseDTO> getMaterialById(@PathVariable Long id) {
    MaterialResponseDTO material = materialService.getMaterialById(id);
    return ResponseEntity.ok(material);
  }

  /**
   * Cria novo material.
   */
  @PostMapping("/")
  public ResponseEntity<MaterialResponseDTO> createMaterial(
      @Valid @RequestBody MaterialCreateDTO dto) {
    MaterialResponseDTO novo = materialService.createMaterial(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(novo);
  }

  /**
   * Atualiza material existente.
   */
  @PatchMapping("/{id}")
  public ResponseEntity<MaterialResponseDTO> updateMaterial(
      @PathVariable Long id,
      @Valid @RequestBody MaterialUpdateDTO dto) {
    MaterialResponseDTO atualizado = materialService.updateMaterial(id, dto);
    return ResponseEntity.ok(atualizado);
  }

  /**
   * Deleta material.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMaterial(@PathVariable Long id) {
    materialService.deleteMaterial(id);
    return ResponseEntity.noContent().build();
  }
}
```

#### Task 1.9: CREATE MaterialControllerTest

**File:** `src/test/java/com/sigesi/sigesi/materiais/MaterialControllerTest.java`

- **IMPLEMENT**: Testes unitarios com MockMvc
- **PATTERN**: `src/test/java/com/sigesi/sigesi/solicitacoes/SolicitacaoControllerTest.java:1-228`
- **VALIDATE**: `mvn test -Dtest=MaterialControllerTest -q`

```java
package com.sigesi.sigesi.materiais;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.materiais.dtos.MaterialCreateDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialResponseDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialUpdateDTO;

/**
 * Testes para MaterialController.
 */
@WebMvcTest(controllers = MaterialController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MaterialController Tests")
class MaterialControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MaterialService service;

  private MaterialResponseDTO responseDto(Long id, String nome, Double preco) {
    return MaterialResponseDTO.builder()
        .id(id)
        .nome(nome)
        .preco(preco)
        .build();
  }

  @Test
  @DisplayName("GET /api/materiais/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(service.getAll()).willReturn(List.of());

    mockMvc.perform(get("/api/materiais/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("GET /api/materiais/ retorna 200 com multiplos materiais")
  void testListAllRetorna200ComMultiplosMateriais() throws Exception {
    MaterialResponseDTO dto1 = responseDto(1L, "Cimento", 50.0);
    MaterialResponseDTO dto2 = responseDto(2L, "Areia", 30.0);

    given(service.getAll()).willReturn(List.of(dto1, dto2));

    mockMvc.perform(get("/api/materiais/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].nome", is("Cimento")))
        .andExpect(jsonPath("$[1].id", is(2)));
  }

  @Test
  @DisplayName("GET /api/materiais/{id} retorna 200 quando encontrado")
  void testGetByIdRetorna200QuandoEncontrado() throws Exception {
    MaterialResponseDTO dto = responseDto(1L, "Cimento", 50.0);

    given(service.getMaterialById(1L)).willReturn(dto);

    mockMvc.perform(get("/api/materiais/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.nome", is("Cimento")))
        .andExpect(jsonPath("$.preco", is(50.0)));
  }

  @Test
  @DisplayName("GET /api/materiais/{id} retorna 404 quando nao encontrado")
  void testGetByIdRetorna404QuandoNaoEncontrado() throws Exception {
    given(service.getMaterialById(999L))
        .willThrow(new NotFoundException("Material não encontrado com id 999"));

    mockMvc.perform(get("/api/materiais/999").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("POST /api/materiais/ retorna 201 quando criado com sucesso")
  void testCreateRetorna201QuandoCriadoComSucesso() throws Exception {
    MaterialCreateDTO createDto = new MaterialCreateDTO("Cimento", 50.0);
    MaterialResponseDTO responseDto = responseDto(1L, "Cimento", 50.0);

    given(service.createMaterial(any(MaterialCreateDTO.class))).willReturn(responseDto);

    mockMvc.perform(post("/api/materiais/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.nome", is("Cimento")));
  }

  @Test
  @DisplayName("POST /api/materiais/ retorna 400 quando nome vazio")
  void testCreateRetorna400QuandoNomeVazio() throws Exception {
    MaterialCreateDTO createDto = new MaterialCreateDTO("", 50.0);

    mockMvc.perform(post("/api/materiais/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("PATCH /api/materiais/{id} retorna 200 com dados atualizados")
  void testUpdateRetorna200ComDadosAtualizados() throws Exception {
    MaterialUpdateDTO updateDto = new MaterialUpdateDTO("Cimento Portland", 55.0);
    MaterialResponseDTO responseDto = responseDto(1L, "Cimento Portland", 55.0);

    given(service.updateMaterial(eq(1L), any(MaterialUpdateDTO.class)))
        .willReturn(responseDto);

    mockMvc.perform(patch("/api/materiais/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nome", is("Cimento Portland")))
        .andExpect(jsonPath("$.preco", is(55.0)));
  }

  @Test
  @DisplayName("DELETE /api/materiais/{id} retorna 204 quando deletado")
  void testDeleteRetorna204QuandoDeletado() throws Exception {
    doNothing().when(service).deleteMaterial(1L);

    mockMvc.perform(delete("/api/materiais/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/materiais/{id} retorna 404 quando nao existe")
  void testDeleteRetorna404QuandoNaoExiste() throws Exception {
    doThrow(new NotFoundException("Material não encontrado com id 999"))
        .when(service).deleteMaterial(999L);

    mockMvc.perform(delete("/api/materiais/999"))
        .andExpect(status().isNotFound());
  }
}
```

---

### FASE 2: DEMANDA

#### Task 2.1: CREATE DemandaStatus Enum

**File:** `src/main/java/com/sigesi/sigesi/demandas/DemandaStatus.java`

- **IMPLEMENT**: Enum com status: PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoStatus.java:1-9`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.demandas;

/**
 * Status possiveis para uma Demanda.
 */
public enum DemandaStatus {
  PENDENTE,
  EM_ANDAMENTO,
  CONCLUIDA,
  CANCELADA
}
```

#### Task 2.2: CREATE Demanda Entity

**File:** `src/main/java/com/sigesi/sigesi/demandas/Demanda.java`

- **IMPLEMENT**: Entidade com relacionamentos ManyToOne (Solicitacao, Usuario) e ManyToMany (Material)
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/Solicitacao.java:1-73`
- **GOTCHA**: Usar Set<Material> para ManyToMany, NAO List
- **GOTCHA**: Usar cascade = {PERSIST, MERGE} para ManyToMany, NUNCA REMOVE
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.demandas;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.sigesi.sigesi.materiais.Material;
import com.sigesi.sigesi.solicitacoes.Solicitacao;
import com.sigesi.sigesi.usuarios.Usuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa uma demanda de trabalho.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Demanda {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull(message = "Solicitação é obrigatória")
  @ManyToOne
  @JoinColumn(name = "solicitacao_id", nullable = false)
  private Solicitacao solicitacao;

  @ManyToOne
  @JoinColumn(name = "responsavel_id")
  private Usuario responsavel;

  @Column(nullable = false)
  private LocalDate prazo;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private DemandaStatus status;

  @Builder.Default
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "demanda_material",
      joinColumns = @JoinColumn(name = "demanda_id"),
      inverseJoinColumns = @JoinColumn(name = "material_id")
  )
  private Set<Material> materiais = new HashSet<>();

  /**
   * Define status padrao antes de persistir.
   */
  @PrePersist
  protected void onCreate() {
    if (this.status == null) {
      this.status = DemandaStatus.PENDENTE;
    }
  }

  /**
   * Adiciona material a demanda.
   */
  public void addMaterial(Material material) {
    materiais.add(material);
  }

  /**
   * Remove material da demanda.
   */
  public void removeMaterial(Material material) {
    materiais.remove(material);
  }
}
```

#### Task 2.3: CREATE DemandaRepository

**File:** `src/main/java/com/sigesi/sigesi/demandas/DemandaRepository.java`

- **IMPLEMENT**: Interface JpaRepository com queries customizadas
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoRepository.java:1-18`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.demandas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para Demanda.
 */
@Repository
public interface DemandaRepository extends JpaRepository<Demanda, Long> {

  List<Demanda> findAllByOrderByIdAsc();

  List<Demanda> findBySolicitacaoIdOrderByPrazoAsc(Long solicitacaoId);

  List<Demanda> findByResponsavelIdOrderByPrazoAsc(Long responsavelId);

  List<Demanda> findByStatusOrderByPrazoAsc(DemandaStatus status);
}
```

#### Task 2.4: CREATE DemandaCreateDTO

**File:** `src/main/java/com/sigesi/sigesi/demandas/dtos/DemandaCreateDTO.java`

- **IMPLEMENT**: DTO com IDs para relacionamentos e Set<Long> para materiais
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/dtos/SolicitacaoCreateDTO.java:1-32`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.demandas.dtos;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criacao de Demanda.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandaCreateDTO {

  @NotNull(message = "Solicitação é obrigatória")
  private Long solicitacaoId;

  private Long responsavelId;

  @NotNull(message = "Prazo é obrigatório")
  private LocalDate prazo;

  private Set<Long> materiaisIds;
}
```

#### Task 2.5: CREATE DemandaUpdateDTO

**File:** `src/main/java/com/sigesi/sigesi/demandas/dtos/DemandaUpdateDTO.java`

- **IMPLEMENT**: DTO para atualizacao com status, responsavel e materiais
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/dtos/SolicitacaoUpdateDTO.java:1-23`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.demandas.dtos;

import java.time.LocalDate;
import java.util.Set;

import com.sigesi.sigesi.demandas.DemandaStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualizacao de Demanda.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandaUpdateDTO {

  @Schema(description = "ID do responsável (agente)", example = "1")
  private Long responsavelId;

  @Schema(description = "Prazo da demanda", example = "2025-12-31")
  private LocalDate prazo;

  @Schema(description = "Status da demanda", example = "EM_ANDAMENTO")
  private DemandaStatus status;

  @Schema(description = "IDs dos materiais")
  private Set<Long> materiaisIds;
}
```

#### Task 2.6: CREATE DemandaResponseDTO

**File:** `src/main/java/com/sigesi/sigesi/demandas/dtos/DemandaResponseDTO.java`

- **IMPLEMENT**: DTO de resposta com entidades aninhadas
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/dtos/SolicitacaoResponseDTO.java:1-46`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.demandas.dtos;

import java.time.LocalDate;
import java.util.Set;

import com.sigesi.sigesi.demandas.DemandaStatus;
import com.sigesi.sigesi.materiais.dtos.MaterialResponseDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoResponseDTO;
import com.sigesi.sigesi.usuarios.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para Demanda.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DemandaResponseDTO {

  private Long id;
  private SolicitacaoResponseDTO solicitacao;
  private Usuario responsavel;
  private LocalDate prazo;
  private DemandaStatus status;
  private Set<MaterialResponseDTO> materiais;
}
```

#### Task 2.7: CREATE DemandaMapper

**File:** `src/main/java/com/sigesi/sigesi/demandas/DemandaMapper.java`

- **IMPLEMENT**: Mapper ignorando relacionamentos (resolvidos no Service)
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoMapper.java:1-62`
- **GOTCHA**: Ignorar solicitacao, responsavel e materiais no toEntity
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.demandas;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sigesi.sigesi.demandas.dtos.DemandaCreateDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaResponseDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaUpdateDTO;
import com.sigesi.sigesi.materiais.MaterialMapper;
import com.sigesi.sigesi.solicitacoes.SolicitacaoMapper;

/**
 * Mapper MapStruct para Demanda.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {SolicitacaoMapper.class, MaterialMapper.class})
public interface DemandaMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "solicitacao", ignore = true)
  @Mapping(target = "responsavel", ignore = true)
  @Mapping(target = "materiais", ignore = true)
  @Mapping(target = "status", ignore = true)
  Demanda toEntity(DemandaCreateDTO dto);

  DemandaResponseDTO toDto(Demanda entity);

  List<DemandaResponseDTO> toDtoList(List<Demanda> entities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "solicitacao", ignore = true)
  @Mapping(target = "responsavel", ignore = true)
  @Mapping(target = "materiais", ignore = true)
  void updateFromDto(DemandaUpdateDTO dto, @MappingTarget Demanda entity);
}
```

#### Task 2.8: CREATE DemandaService

**File:** `src/main/java/com/sigesi/sigesi/demandas/DemandaService.java`

- **IMPLEMENT**: Service com resolucao de relacionamentos e logica ManyToMany
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoService.java:1-84`
- **GOTCHA**: Usar materialService.findAllByIds() para M:N
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.demandas;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.demandas.dtos.DemandaCreateDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaResponseDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaUpdateDTO;
import com.sigesi.sigesi.materiais.Material;
import com.sigesi.sigesi.materiais.MaterialService;
import com.sigesi.sigesi.solicitacoes.Solicitacao;
import com.sigesi.sigesi.solicitacoes.SolicitacaoService;
import com.sigesi.sigesi.usuarios.Usuario;
import com.sigesi.sigesi.usuarios.UsuarioService;

/**
 * Service para Demanda.
 */
@Service
public class DemandaService {

  @Autowired
  private DemandaRepository demandaRepository;

  @Autowired
  private DemandaMapper demandaMapper;

  @Autowired
  private SolicitacaoService solicitacaoService;

  @Autowired
  private UsuarioService usuarioService;

  @Autowired
  private MaterialService materialService;

  /**
   * Lista todas as demandas.
   */
  public List<DemandaResponseDTO> getAll() {
    return demandaRepository.findAllByOrderByIdAsc()
        .stream()
        .map(demandaMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Busca demanda por ID.
   */
  public DemandaResponseDTO getDemandaById(Long id) {
    Demanda demanda = this.getDemandaEntityById(id);
    return demandaMapper.toDto(demanda);
  }

  /**
   * Busca demandas por solicitacao.
   */
  public List<DemandaResponseDTO> getDemandasBySolicitacao(Long solicitacaoId) {
    return demandaRepository.findBySolicitacaoIdOrderByPrazoAsc(solicitacaoId)
        .stream()
        .map(demandaMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Busca demandas por responsavel.
   */
  public List<DemandaResponseDTO> getDemandasByResponsavel(Long responsavelId) {
    return demandaRepository.findByResponsavelIdOrderByPrazoAsc(responsavelId)
        .stream()
        .map(demandaMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Cria nova demanda.
   */
  @Transactional
  public DemandaResponseDTO createDemanda(DemandaCreateDTO dto) {
    Solicitacao solicitacao = solicitacaoService
        .getSolicitacaoEntityById(dto.getSolicitacaoId());

    Demanda demanda = demandaMapper.toEntity(dto);
    demanda.setSolicitacao(solicitacao);

    if (dto.getResponsavelId() != null) {
      Usuario responsavel = usuarioService.getUsuarioById(dto.getResponsavelId());
      demanda.setResponsavel(responsavel);
    }

    if (dto.getMateriaisIds() != null && !dto.getMateriaisIds().isEmpty()) {
      Set<Material> materiais = materialService.findAllByIds(dto.getMateriaisIds());
      demanda.setMateriais(materiais);
    }

    Demanda saved = demandaRepository.save(demanda);
    return demandaMapper.toDto(saved);
  }

  /**
   * Atualiza demanda existente.
   */
  @Transactional
  public DemandaResponseDTO updateDemanda(Long id, DemandaUpdateDTO dto) {
    Demanda demanda = this.getDemandaEntityById(id);

    demandaMapper.updateFromDto(dto, demanda);

    if (dto.getResponsavelId() != null) {
      Usuario responsavel = usuarioService.getUsuarioById(dto.getResponsavelId());
      demanda.setResponsavel(responsavel);
    }

    if (dto.getMateriaisIds() != null) {
      if (dto.getMateriaisIds().isEmpty()) {
        demanda.setMateriais(new HashSet<>());
      } else {
        Set<Material> materiais = materialService.findAllByIds(dto.getMateriaisIds());
        demanda.setMateriais(materiais);
      }
    }

    Demanda updated = demandaRepository.save(demanda);
    return demandaMapper.toDto(updated);
  }

  /**
   * Deleta demanda.
   */
  public void deleteDemanda(Long id) {
    Demanda demanda = this.getDemandaEntityById(id);
    demandaRepository.delete(demanda);
  }

  /**
   * Busca entidade Demanda por ID (uso interno).
   */
  public Demanda getDemandaEntityById(Long id) {
    return demandaRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Demanda não encontrada com id " + id));
  }
}
```

#### Task 2.9: CREATE DemandaController

**File:** `src/main/java/com/sigesi/sigesi/demandas/DemandaController.java`

- **IMPLEMENT**: Controller REST com endpoints CRUD e queries customizadas
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoController.java:1-63`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.demandas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sigesi.sigesi.demandas.dtos.DemandaCreateDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaResponseDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaUpdateDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para Demanda.
 */
@RestController
@RequestMapping("/api/demandas")
@Tag(name = "demandas")
public class DemandaController {

  @Autowired
  private DemandaService demandaService;

  /**
   * Lista todas as demandas.
   */
  @GetMapping("/")
  public ResponseEntity<List<DemandaResponseDTO>> listAll() {
    List<DemandaResponseDTO> demandas = demandaService.getAll();
    return ResponseEntity.ok(demandas);
  }

  /**
   * Busca demanda por ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<DemandaResponseDTO> getDemandaById(@PathVariable Long id) {
    DemandaResponseDTO demanda = demandaService.getDemandaById(id);
    return ResponseEntity.ok(demanda);
  }

  /**
   * Busca demandas por solicitacao.
   */
  @GetMapping("/solicitacao/{solicitacaoId}")
  public ResponseEntity<List<DemandaResponseDTO>> getDemandasBySolicitacao(
      @PathVariable Long solicitacaoId) {
    List<DemandaResponseDTO> demandas = demandaService
        .getDemandasBySolicitacao(solicitacaoId);
    return ResponseEntity.ok(demandas);
  }

  /**
   * Busca demandas por responsavel.
   */
  @GetMapping("/responsavel")
  public ResponseEntity<List<DemandaResponseDTO>> getDemandasByResponsavel(
      @RequestParam Long responsavelId) {
    List<DemandaResponseDTO> demandas = demandaService
        .getDemandasByResponsavel(responsavelId);
    return ResponseEntity.ok(demandas);
  }

  /**
   * Cria nova demanda.
   */
  @PostMapping("/")
  public ResponseEntity<DemandaResponseDTO> createDemanda(
      @Valid @RequestBody DemandaCreateDTO dto) {
    DemandaResponseDTO nova = demandaService.createDemanda(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(nova);
  }

  /**
   * Atualiza demanda existente.
   */
  @PatchMapping("/{id}")
  public ResponseEntity<DemandaResponseDTO> updateDemanda(
      @PathVariable Long id,
      @Valid @RequestBody DemandaUpdateDTO dto) {
    DemandaResponseDTO atualizada = demandaService.updateDemanda(id, dto);
    return ResponseEntity.ok(atualizada);
  }

  /**
   * Deleta demanda.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDemanda(@PathVariable Long id) {
    demandaService.deleteDemanda(id);
    return ResponseEntity.noContent().build();
  }
}
```

#### Task 2.10: CREATE DemandaControllerTest

**File:** `src/test/java/com/sigesi/sigesi/demandas/DemandaControllerTest.java`

- **IMPLEMENT**: Testes unitarios completos
- **PATTERN**: `src/test/java/com/sigesi/sigesi/solicitacoes/SolicitacaoControllerTest.java:1-228`
- **VALIDATE**: `mvn test -Dtest=DemandaControllerTest -q`

```java
package com.sigesi.sigesi.demandas;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.demandas.dtos.DemandaCreateDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaResponseDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaUpdateDTO;
import com.sigesi.sigesi.usuarios.Usuario;

/**
 * Testes para DemandaController.
 */
@WebMvcTest(controllers = DemandaController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DemandaController Tests")
class DemandaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private DemandaService service;

  private DemandaResponseDTO responseDto(Long id, DemandaStatus status) {
    Usuario responsavel = Usuario.builder().id(1L).email("agente@test.com").build();

    return DemandaResponseDTO.builder()
        .id(id)
        .solicitacao(null)
        .responsavel(responsavel)
        .prazo(LocalDate.of(2025, 12, 31))
        .status(status)
        .materiais(new HashSet<>())
        .build();
  }

  @Test
  @DisplayName("GET /api/demandas/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(service.getAll()).willReturn(List.of());

    mockMvc.perform(get("/api/demandas/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /api/demandas/ retorna 200 com multiplas demandas")
  void testListAllRetorna200ComMultiplasDemandas() throws Exception {
    DemandaResponseDTO dto1 = responseDto(1L, DemandaStatus.PENDENTE);
    DemandaResponseDTO dto2 = responseDto(2L, DemandaStatus.EM_ANDAMENTO);

    given(service.getAll()).willReturn(List.of(dto1, dto2));

    mockMvc.perform(get("/api/demandas/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].status", is("PENDENTE")))
        .andExpect(jsonPath("$[1].id", is(2)));
  }

  @Test
  @DisplayName("GET /api/demandas/{id} retorna 200 quando encontrada")
  void testGetByIdRetorna200QuandoEncontrada() throws Exception {
    DemandaResponseDTO dto = responseDto(1L, DemandaStatus.PENDENTE);

    given(service.getDemandaById(1L)).willReturn(dto);

    mockMvc.perform(get("/api/demandas/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.status", is("PENDENTE")));
  }

  @Test
  @DisplayName("GET /api/demandas/{id} retorna 404 quando nao encontrada")
  void testGetByIdRetorna404QuandoNaoEncontrada() throws Exception {
    given(service.getDemandaById(999L))
        .willThrow(new NotFoundException("Demanda não encontrada com id 999"));

    mockMvc.perform(get("/api/demandas/999").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /api/demandas/solicitacao/{id} retorna demandas por solicitacao")
  void testGetBySolicitacaoRetorna200() throws Exception {
    DemandaResponseDTO dto = responseDto(1L, DemandaStatus.PENDENTE);

    given(service.getDemandasBySolicitacao(1L)).willReturn(List.of(dto));

    mockMvc.perform(get("/api/demandas/solicitacao/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  @DisplayName("POST /api/demandas/ retorna 201 quando criada com sucesso")
  void testCreateRetorna201QuandoCriadaComSucesso() throws Exception {
    DemandaCreateDTO createDto = new DemandaCreateDTO();
    createDto.setSolicitacaoId(1L);
    createDto.setResponsavelId(1L);
    createDto.setPrazo(LocalDate.of(2025, 12, 31));
    createDto.setMateriaisIds(Set.of(1L, 2L));

    DemandaResponseDTO responseDto = responseDto(1L, DemandaStatus.PENDENTE);

    given(service.createDemanda(any(DemandaCreateDTO.class))).willReturn(responseDto);

    mockMvc.perform(post("/api/demandas/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)));
  }

  @Test
  @DisplayName("POST /api/demandas/ retorna 400 quando solicitacaoId nulo")
  void testCreateRetorna400QuandoSolicitacaoIdNulo() throws Exception {
    DemandaCreateDTO createDto = new DemandaCreateDTO();
    createDto.setSolicitacaoId(null);
    createDto.setPrazo(LocalDate.of(2025, 12, 31));

    mockMvc.perform(post("/api/demandas/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("PATCH /api/demandas/{id} retorna 200 com dados atualizados")
  void testUpdateRetorna200ComDadosAtualizados() throws Exception {
    DemandaUpdateDTO updateDto = new DemandaUpdateDTO();
    updateDto.setStatus(DemandaStatus.EM_ANDAMENTO);

    DemandaResponseDTO responseDto = responseDto(1L, DemandaStatus.EM_ANDAMENTO);

    given(service.updateDemanda(eq(1L), any(DemandaUpdateDTO.class)))
        .willReturn(responseDto);

    mockMvc.perform(patch("/api/demandas/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("EM_ANDAMENTO")));
  }

  @Test
  @DisplayName("DELETE /api/demandas/{id} retorna 204 quando deletada")
  void testDeleteRetorna204QuandoDeletada() throws Exception {
    doNothing().when(service).deleteDemanda(1L);

    mockMvc.perform(delete("/api/demandas/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/demandas/{id} retorna 404 quando nao existe")
  void testDeleteRetorna404QuandoNaoExiste() throws Exception {
    doThrow(new NotFoundException("Demanda não encontrada com id 999"))
        .when(service).deleteDemanda(999L);

    mockMvc.perform(delete("/api/demandas/999"))
        .andExpect(status().isNotFound());
  }
}
```

---

### FASE 3: COMENTARIO

#### Task 3.1: CREATE Comentario Entity

**File:** `src/main/java/com/sigesi/sigesi/comentarios/Comentario.java`

- **IMPLEMENT**: Entidade com ManyToOne para Demanda e Usuario, texto e data
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/Solicitacao.java:1-73` (para @PrePersist)
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.comentarios;

import java.time.LocalDateTime;

import com.sigesi.sigesi.demandas.Demanda;
import com.sigesi.sigesi.usuarios.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa um comentario em uma demanda.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comentario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull(message = "Demanda é obrigatória")
  @ManyToOne
  @JoinColumn(name = "demanda_id", nullable = false)
  private Demanda demanda;

  @NotNull(message = "Autor é obrigatório")
  @ManyToOne
  @JoinColumn(name = "autor_id", nullable = false)
  private Usuario autor;

  @NotBlank(message = "Texto é obrigatório")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String texto;

  @Column(nullable = false)
  private LocalDateTime criadoEm;

  /**
   * Define data de criacao antes de persistir.
   */
  @PrePersist
  protected void onCreate() {
    if (this.criadoEm == null) {
      this.criadoEm = LocalDateTime.now();
    }
  }
}
```

#### Task 3.2: CREATE ComentarioRepository

**File:** `src/main/java/com/sigesi/sigesi/comentarios/ComentarioRepository.java`

- **IMPLEMENT**: Interface JpaRepository com queries por demanda
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoRepository.java:1-18`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.comentarios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para Comentario.
 */
@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

  List<Comentario> findAllByOrderByIdAsc();

  List<Comentario> findByDemandaIdOrderByCriadoEmAsc(Long demandaId);

  List<Comentario> findByAutorIdOrderByCriadoEmDesc(Long autorId);
}
```

#### Task 3.3: CREATE ComentarioCreateDTO

**File:** `src/main/java/com/sigesi/sigesi/comentarios/dtos/ComentarioCreateDTO.java`

- **IMPLEMENT**: DTO com demandaId, autorId e texto
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/dtos/SolicitacaoCreateDTO.java:1-32`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.comentarios.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criacao de Comentario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComentarioCreateDTO {

  @NotNull(message = "Demanda é obrigatória")
  private Long demandaId;

  @NotNull(message = "Autor é obrigatório")
  private Long autorId;

  @NotBlank(message = "Texto é obrigatório")
  private String texto;
}
```

#### Task 3.4: CREATE ComentarioResponseDTO

**File:** `src/main/java/com/sigesi/sigesi/comentarios/dtos/ComentarioResponseDTO.java`

- **IMPLEMENT**: DTO de resposta com dados completos
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/dtos/SolicitacaoResponseDTO.java:1-46`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.comentarios.dtos;

import java.time.LocalDateTime;

import com.sigesi.sigesi.usuarios.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para Comentario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComentarioResponseDTO {

  private Long id;
  private Long demandaId;
  private Usuario autor;
  private String texto;
  private LocalDateTime criadoEm;
}
```

#### Task 3.5: CREATE ComentarioMapper

**File:** `src/main/java/com/sigesi/sigesi/comentarios/ComentarioMapper.java`

- **IMPLEMENT**: Mapper com mapeamento customizado para demandaId
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoMapper.java:1-62`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.comentarios;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sigesi.sigesi.comentarios.dtos.ComentarioCreateDTO;
import com.sigesi.sigesi.comentarios.dtos.ComentarioResponseDTO;

/**
 * Mapper MapStruct para Comentario.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ComentarioMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "demanda", ignore = true)
  @Mapping(target = "autor", ignore = true)
  @Mapping(target = "criadoEm", ignore = true)
  Comentario toEntity(ComentarioCreateDTO dto);

  @Mapping(target = "demandaId", source = "demanda.id")
  ComentarioResponseDTO toDto(Comentario entity);

  List<ComentarioResponseDTO> toDtoList(List<Comentario> entities);
}
```

#### Task 3.6: CREATE ComentarioService

**File:** `src/main/java/com/sigesi/sigesi/comentarios/ComentarioService.java`

- **IMPLEMENT**: Service com resolucao de relacionamentos
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoService.java:1-84`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.comentarios;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.comentarios.dtos.ComentarioCreateDTO;
import com.sigesi.sigesi.comentarios.dtos.ComentarioResponseDTO;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.demandas.Demanda;
import com.sigesi.sigesi.demandas.DemandaService;
import com.sigesi.sigesi.usuarios.Usuario;
import com.sigesi.sigesi.usuarios.UsuarioService;

/**
 * Service para Comentario.
 */
@Service
public class ComentarioService {

  @Autowired
  private ComentarioRepository comentarioRepository;

  @Autowired
  private ComentarioMapper comentarioMapper;

  @Autowired
  private DemandaService demandaService;

  @Autowired
  private UsuarioService usuarioService;

  /**
   * Lista todos os comentarios.
   */
  public List<ComentarioResponseDTO> getAll() {
    return comentarioRepository.findAllByOrderByIdAsc()
        .stream()
        .map(comentarioMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Busca comentario por ID.
   */
  public ComentarioResponseDTO getComentarioById(Long id) {
    Comentario comentario = this.getComentarioEntityById(id);
    return comentarioMapper.toDto(comentario);
  }

  /**
   * Busca comentarios por demanda.
   */
  public List<ComentarioResponseDTO> getComentariosByDemanda(Long demandaId) {
    return comentarioRepository.findByDemandaIdOrderByCriadoEmAsc(demandaId)
        .stream()
        .map(comentarioMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Cria novo comentario.
   */
  public ComentarioResponseDTO createComentario(ComentarioCreateDTO dto) {
    Demanda demanda = demandaService.getDemandaEntityById(dto.getDemandaId());
    Usuario autor = usuarioService.getUsuarioById(dto.getAutorId());

    Comentario comentario = comentarioMapper.toEntity(dto);
    comentario.setDemanda(demanda);
    comentario.setAutor(autor);

    Comentario saved = comentarioRepository.save(comentario);
    return comentarioMapper.toDto(saved);
  }

  /**
   * Deleta comentario.
   */
  public void deleteComentario(Long id) {
    Comentario comentario = this.getComentarioEntityById(id);
    comentarioRepository.delete(comentario);
  }

  /**
   * Busca entidade Comentario por ID (uso interno).
   */
  public Comentario getComentarioEntityById(Long id) {
    return comentarioRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Comentário não encontrado com id " + id));
  }
}
```

#### Task 3.7: CREATE ComentarioController

**File:** `src/main/java/com/sigesi/sigesi/comentarios/ComentarioController.java`

- **IMPLEMENT**: Controller REST com endpoints
- **PATTERN**: `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoController.java:1-63`
- **VALIDATE**: `mvn compile -pl . -q`

```java
package com.sigesi.sigesi.comentarios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigesi.sigesi.comentarios.dtos.ComentarioCreateDTO;
import com.sigesi.sigesi.comentarios.dtos.ComentarioResponseDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para Comentario.
 */
@RestController
@RequestMapping("/api/comentarios")
@Tag(name = "comentarios")
public class ComentarioController {

  @Autowired
  private ComentarioService comentarioService;

  /**
   * Lista todos os comentarios.
   */
  @GetMapping("/")
  public ResponseEntity<List<ComentarioResponseDTO>> listAll() {
    List<ComentarioResponseDTO> comentarios = comentarioService.getAll();
    return ResponseEntity.ok(comentarios);
  }

  /**
   * Busca comentario por ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<ComentarioResponseDTO> getComentarioById(@PathVariable Long id) {
    ComentarioResponseDTO comentario = comentarioService.getComentarioById(id);
    return ResponseEntity.ok(comentario);
  }

  /**
   * Busca comentarios por demanda.
   */
  @GetMapping("/demanda/{demandaId}")
  public ResponseEntity<List<ComentarioResponseDTO>> getComentariosByDemanda(
      @PathVariable Long demandaId) {
    List<ComentarioResponseDTO> comentarios = comentarioService
        .getComentariosByDemanda(demandaId);
    return ResponseEntity.ok(comentarios);
  }

  /**
   * Cria novo comentario.
   */
  @PostMapping("/")
  public ResponseEntity<ComentarioResponseDTO> createComentario(
      @Valid @RequestBody ComentarioCreateDTO dto) {
    ComentarioResponseDTO novo = comentarioService.createComentario(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(novo);
  }

  /**
   * Deleta comentario.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteComentario(@PathVariable Long id) {
    comentarioService.deleteComentario(id);
    return ResponseEntity.noContent().build();
  }
}
```

#### Task 3.8: CREATE ComentarioControllerTest

**File:** `src/test/java/com/sigesi/sigesi/comentarios/ComentarioControllerTest.java`

- **IMPLEMENT**: Testes unitarios completos
- **PATTERN**: `src/test/java/com/sigesi/sigesi/solicitacoes/SolicitacaoControllerTest.java:1-228`
- **VALIDATE**: `mvn test -Dtest=ComentarioControllerTest -q`

```java
package com.sigesi.sigesi.comentarios;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigesi.sigesi.comentarios.dtos.ComentarioCreateDTO;
import com.sigesi.sigesi.comentarios.dtos.ComentarioResponseDTO;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.usuarios.Usuario;

/**
 * Testes para ComentarioController.
 */
@WebMvcTest(controllers = ComentarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ComentarioController Tests")
class ComentarioControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ComentarioService service;

  private ComentarioResponseDTO responseDto(Long id, String texto) {
    Usuario autor = Usuario.builder().id(1L).email("autor@test.com").build();

    return ComentarioResponseDTO.builder()
        .id(id)
        .demandaId(1L)
        .autor(autor)
        .texto(texto)
        .criadoEm(LocalDateTime.of(2025, 12, 29, 10, 0))
        .build();
  }

  @Test
  @DisplayName("GET /api/comentarios/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(service.getAll()).willReturn(List.of());

    mockMvc.perform(get("/api/comentarios/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /api/comentarios/ retorna 200 com multiplos comentarios")
  void testListAllRetorna200ComMultiplosComentarios() throws Exception {
    ComentarioResponseDTO dto1 = responseDto(1L, "Comentario 1");
    ComentarioResponseDTO dto2 = responseDto(2L, "Comentario 2");

    given(service.getAll()).willReturn(List.of(dto1, dto2));

    mockMvc.perform(get("/api/comentarios/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].texto", is("Comentario 1")));
  }

  @Test
  @DisplayName("GET /api/comentarios/{id} retorna 200 quando encontrado")
  void testGetByIdRetorna200QuandoEncontrado() throws Exception {
    ComentarioResponseDTO dto = responseDto(1L, "Test");

    given(service.getComentarioById(1L)).willReturn(dto);

    mockMvc.perform(get("/api/comentarios/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.texto", is("Test")));
  }

  @Test
  @DisplayName("GET /api/comentarios/{id} retorna 404 quando nao encontrado")
  void testGetByIdRetorna404QuandoNaoEncontrado() throws Exception {
    given(service.getComentarioById(999L))
        .willThrow(new NotFoundException("Comentário não encontrado com id 999"));

    mockMvc.perform(get("/api/comentarios/999").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /api/comentarios/demanda/{id} retorna comentarios por demanda")
  void testGetByDemandaRetorna200() throws Exception {
    ComentarioResponseDTO dto = responseDto(1L, "Test");

    given(service.getComentariosByDemanda(1L)).willReturn(List.of(dto));

    mockMvc.perform(get("/api/comentarios/demanda/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  @DisplayName("POST /api/comentarios/ retorna 201 quando criado com sucesso")
  void testCreateRetorna201QuandoCriadoComSucesso() throws Exception {
    ComentarioCreateDTO createDto = new ComentarioCreateDTO(1L, 1L, "Novo comentario");
    ComentarioResponseDTO responseDto = responseDto(1L, "Novo comentario");

    given(service.createComentario(any(ComentarioCreateDTO.class))).willReturn(responseDto);

    mockMvc.perform(post("/api/comentarios/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)));
  }

  @Test
  @DisplayName("POST /api/comentarios/ retorna 400 quando texto vazio")
  void testCreateRetorna400QuandoTextoVazio() throws Exception {
    ComentarioCreateDTO createDto = new ComentarioCreateDTO(1L, 1L, "");

    mockMvc.perform(post("/api/comentarios/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("DELETE /api/comentarios/{id} retorna 204 quando deletado")
  void testDeleteRetorna204QuandoDeletado() throws Exception {
    doNothing().when(service).deleteComentario(1L);

    mockMvc.perform(delete("/api/comentarios/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/comentarios/{id} retorna 404 quando nao existe")
  void testDeleteRetorna404QuandoNaoExiste() throws Exception {
    doThrow(new NotFoundException("Comentário não encontrado com id 999"))
        .when(service).deleteComentario(999L);

    mockMvc.perform(delete("/api/comentarios/999"))
        .andExpect(status().isNotFound());
  }
}
```

---

## TESTING STRATEGY

### Unit Tests

Cada modulo tera testes de controller usando `@WebMvcTest` com `@MockitoBean` para o service.

**Cobertura minima**: 80% dos endpoints testados

**Cenarios obrigatorios:**
- GET / - lista vazia e com dados
- GET /{id} - encontrado e 404
- POST / - sucesso e validacao (400)
- PATCH /{id} - sucesso (quando aplicavel)
- DELETE /{id} - sucesso e 404

### Integration Tests

Nao incluidos neste PRP. Podem ser adicionados posteriormente.

### Edge Cases

1. **Material**:
   - Criar com preco negativo (deve falhar validacao)
   - Deletar material em uso por demanda (verificar comportamento)

2. **Demanda**:
   - Criar sem materiais (deve funcionar)
   - Atualizar materiais para lista vazia
   - Criar com responsavel inexistente (404)

3. **Comentario**:
   - Criar em demanda inexistente (404)
   - Criar com autor inexistente (404)

---

## VALIDATION COMMANDS

### Level 1: Syntax & Style

```bash
mvn checkstyle:check -q
```

### Level 2: Compilation

```bash
mvn compile -pl . -q
```

### Level 3: Unit Tests

```bash
# Todos os testes
mvn test -q

# Testes especificos
mvn test -Dtest=MaterialControllerTest -q
mvn test -Dtest=DemandaControllerTest -q
mvn test -Dtest=ComentarioControllerTest -q
```

### Level 4: Full Build

```bash
mvn clean install -q
```

### Level 5: Manual Validation

Apos subir a aplicacao (`mvn spring-boot:run`):

```bash
# Material
curl -X GET http://localhost:8080/api/materiais/
curl -X POST http://localhost:8080/api/materiais/ \
  -H "Content-Type: application/json" \
  -d '{"nome":"Cimento","preco":50.0}'

# Demanda (requer solicitacao existente)
curl -X GET http://localhost:8080/api/demandas/
curl -X POST http://localhost:8080/api/demandas/ \
  -H "Content-Type: application/json" \
  -d '{"solicitacaoId":1,"prazo":"2025-12-31","materiaisIds":[1]}'

# Comentario (requer demanda existente)
curl -X GET http://localhost:8080/api/comentarios/
curl -X POST http://localhost:8080/api/comentarios/ \
  -H "Content-Type: application/json" \
  -d '{"demandaId":1,"autorId":1,"texto":"Comentario teste"}'
```

---

## ACCEPTANCE CRITERIA

- [x] Entidade Material implementada com CRUD completo
- [x] Entidade Demanda implementada com relacionamentos corretos
  - [x] ManyToOne para Solicitacao
  - [x] ManyToOne para Usuario (responsavel)
  - [x] ManyToMany para Material (usando Set)
- [x] Entidade Comentario implementada com ManyToOne para Demanda e Usuario
- [x] Todos os DTOs criados (Create, Update, Response)
- [x] Todos os mappers MapStruct funcionando
- [x] Todos os controllers REST implementados
- [x] Todos os testes de controller passando
- [x] Checkstyle sem erros
- [x] Build completo sem erros

---

## COMPLETION CHECKLIST

- [ ] Fase 1: Material - todos os arquivos criados
- [ ] Fase 1: Material - testes passando
- [ ] Fase 2: Demanda - todos os arquivos criados
- [ ] Fase 2: Demanda - testes passando
- [ ] Fase 3: Comentario - todos os arquivos criados
- [ ] Fase 3: Comentario - testes passando
- [ ] `mvn checkstyle:check` passa
- [ ] `mvn test` passa
- [ ] `mvn clean install` passa
- [ ] Endpoints funcionando via Swagger UI

---

## NOTES

### Design Decisions

1. **Set vs List para ManyToMany**: Escolhido `Set<Material>` para evitar problemas de performance do Hibernate com `List` em relacionamentos M:N.

2. **Cascade Types**: Usado apenas `PERSIST` e `MERGE` no ManyToMany. NUNCA usar `REMOVE` ou `ALL` para evitar delecao em cascata indesejada.

3. **Status como Enum**: Usado `@Enumerated(EnumType.STRING)` para maior legibilidade no banco e evitar problemas com reordenacao de valores.

4. **Comentario sem Update**: Comentarios geralmente nao sao editaveis em sistemas de gestao. Se necessario, pode ser adicionado posteriormente.

5. **Relacionamento unidirecional Demanda-Material**: O Material nao precisa conhecer as demandas. Se necessario, pode-se adicionar o lado inverso com `mappedBy`.

### Trade-offs

- **Complexidade vs Simplicidade**: O PRP foca em implementacao direta seguindo padroes existentes. Funcionalidades avancadas (paginacao, filtros complexos) podem ser adicionadas posteriormente.

- **Testes de Controller vs Testes de Service**: Optou-se por testes de controller que cobrem o fluxo principal. Testes de service podem ser adicionados para logica de negocio complexa.

<!-- EOF -->

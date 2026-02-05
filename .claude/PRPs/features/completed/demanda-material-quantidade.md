# Feature: Adicionar quantidade de material na demanda (DemandaMaterial)

## Feature Description

Atualmente, o relacionamento entre `Demanda` e `Material` e implementado como um `@ManyToMany` puro via join table `demanda_material`, que armazena apenas os IDs de demanda e material. Nao existe campo para registrar a **quantidade** de cada material associado a uma demanda. A quantidade nao pode ser atributo de `Material` (pois o mesmo material pode ter quantidades diferentes em demandas diferentes), nem de `Demanda` (pois cada material tem sua propria quantidade). A solucao e criar uma **entidade intermediaria** (`DemandaMaterial`) que substitui o `@ManyToMany` por dois `@ManyToOne`, adicionando o campo `quantidade`.

## User Story

As a usuario do sistema (operador/admin)
I want to registrar a quantidade de cada material ao cadastrar materiais em uma demanda
So that o sistema saiba exatamente quantas unidades de cada material sao necessarias para a demanda

## Problem Statement

A tabela `demanda_material` atual e uma join table pura (apenas `demanda_id` + `material_id`), sem campo `quantidade`. Isso impossibilita registrar quantas unidades de cada material uma demanda precisa. O atributo `quantidade` nao pode ficar em `Material` (seria global) nem em `Demanda` (seria unico).

## Solution Statement

Substituir o relacionamento `@ManyToMany` entre `Demanda` e `Material` por uma entidade intermediaria `DemandaMaterial` com:
- FK para `Demanda` (`@ManyToOne`)
- FK para `Material` (`@ManyToOne`)
- Campo `quantidade` (`Integer`, obrigatorio, > 0)

Isso transforma o M:N em dois relacionamentos 1:N, permitindo atributos adicionais no relacionamento.

## Feature Metadata

**Feature Type**: Enhancement
**Estimated Complexity**: Medium
**Primary Systems Affected**: `demandas` module, DTOs, Mapper, Service, Controller, Tests
**Dependencies**: Nenhuma nova dependencia externa

---

## CONTEXT REFERENCES

### Relevant Codebase Files

- `src/main/java/com/sigesi/sigesi/demandas/Demanda.java` (lines 63-73) - Why: Contem o `@ManyToMany` atual com `Material` que sera substituido por `@OneToMany` com `DemandaMaterial`
- `src/main/java/com/sigesi/sigesi/demandas/DemandaService.java` (lines 90-115, 120-156) - Why: Logica de create/update que manipula `materiaisIds` e precisa ser refatorada para usar `DemandaMaterial` com quantidade
- `src/main/java/com/sigesi/sigesi/demandas/DemandaMapper.java` (lines 1-40) - Why: Mapper que ignora `materiais` e precisa ser atualizado para a nova estrutura
- `src/main/java/com/sigesi/sigesi/demandas/dtos/DemandaCreateDTO.java` (line 27) - Why: Campo `materiaisIds` (Set<Long>) que sera substituido por lista de itens com materialId + quantidade
- `src/main/java/com/sigesi/sigesi/demandas/dtos/DemandaUpdateDTO.java` (line 31) - Why: Campo `materiaisIds` que sera substituido
- `src/main/java/com/sigesi/sigesi/demandas/dtos/DemandaResponseDTO.java` (line 30) - Why: Campo `materiais` (Set<MaterialResponseDTO>) que sera substituido por Set<DemandaMaterialResponseDTO>
- `src/main/java/com/sigesi/sigesi/demandas/DemandaController.java` (lines 1-105) - Why: Controller que nao precisa mudar (opera sobre DTOs), mas deve ser verificado
- `src/main/java/com/sigesi/sigesi/materiais/Material.java` (lines 1-39) - Why: Entidade Material nao muda, apenas referenciada pela nova entidade
- `src/main/java/com/sigesi/sigesi/materiais/MaterialService.java` (lines 83-89) - Why: Metodo `findAllByIds` usado pelo DemandaService que sera mantido para validacao
- `src/test/java/com/sigesi/sigesi/demandas/DemandaControllerTest.java` (lines 1-198) - Why: Testes existentes que precisam ser atualizados para refletir a nova estrutura de DTOs

### New Files to Create

- `src/main/java/com/sigesi/sigesi/demandas/DemandaMaterial.java` - Entidade intermediaria com demanda, material e quantidade
- `src/main/java/com/sigesi/sigesi/demandas/DemandaMaterialRepository.java` - Repository para DemandaMaterial
- `src/main/java/com/sigesi/sigesi/demandas/dtos/DemandaMaterialCreateDTO.java` - DTO para criar item material com quantidade
- `src/main/java/com/sigesi/sigesi/demandas/dtos/DemandaMaterialResponseDTO.java` - DTO de resposta com material + quantidade
- `src/main/java/com/sigesi/sigesi/demandas/DemandaMaterialMapper.java` - Mapper para DemandaMaterial

### Relevant Documentation

- [JPA ManyToMany with Extra Columns](https://www.baeldung.com/jpa-many-to-many#many-to-many-with-a-new-entity)
  - Why: Padrao oficial para converter ManyToMany em entidade intermediaria
- [MapStruct Spring Integration](https://mapstruct.org/documentation/stable/reference/html/#spring)
  - Why: Configuracao de mapper com `componentModel = "spring"`

### Patterns to Follow

**Entity Pattern (from Demanda.java):**
```java
@Entity
@Data @AllArgsConstructor @NoArgsConstructor @Builder @Audited
public class EntityName {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  // fields...
}
```

**DTO Create Pattern (from DemandaCreateDTO.java):**
```java
@Data @AllArgsConstructor @NoArgsConstructor
public class EntityCreateDTO {
  @NotNull(message = "Campo é obrigatório")
  private Type field;
}
```

**DTO Response Pattern (from DemandaResponseDTO.java):**
```java
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class EntityResponseDTO {
  private Long id;
  // fields...
}
```

**Mapper Pattern (from DemandaMapper.java):**
```java
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {OtherMapper.class})
public interface EntityMapper {
  // methods...
}
```

**Service M:N Resolution Pattern (from DemandaService.java:102-105):**
```java
if (dto.getMateriaisIds() != null && !dto.getMateriaisIds().isEmpty()) {
  Set<Material> materiais = materialService.findAllByIds(dto.getMateriaisIds());
  demanda.setMateriais(materiais);
}
```

**Naming Conventions:**
- Entity: PascalCase singular (`DemandaMaterial`)
- Repository: `DemandaMaterialRepository`
- DTO: `DemandaMaterialCreateDTO`, `DemandaMaterialResponseDTO`
- Mapper: `DemandaMaterialMapper`
- Table: `demanda_material` (mesma tabela, agora com coluna `quantidade` e `id`)
- Package: Todos os arquivos novos ficam no package `com.sigesi.sigesi.demandas`

**Checkstyle Constraints:**
- Max method length: 50 lines
- Max line length: 140 characters
- Max return statements: 3 per method
- Javadoc required on type declarations
- No `System.out.println()`

---

## IMPLEMENTATION PLAN

### Phase 1: Foundation - Entidade e Repository

Criar a entidade `DemandaMaterial` e seu repository. A entidade tera sua propria PK (`id`), FKs para `Demanda` e `Material`, e o campo `quantidade`.

**Tasks:**
- Criar entidade `DemandaMaterial` com `@Entity`, `@Audited`
- Criar `DemandaMaterialRepository`

### Phase 2: DTOs e Mapper

Criar DTOs especificos para o relacionamento e o mapper correspondente. Atualizar os DTOs de Demanda para usar a nova estrutura.

**Tasks:**
- Criar `DemandaMaterialCreateDTO` (materialId + quantidade)
- Criar `DemandaMaterialResponseDTO` (material + quantidade)
- Criar `DemandaMaterialMapper`
- Atualizar `DemandaCreateDTO` para usar `List<DemandaMaterialCreateDTO>` em vez de `Set<Long>`
- Atualizar `DemandaUpdateDTO` para usar `List<DemandaMaterialCreateDTO>` em vez de `Set<Long>`
- Atualizar `DemandaResponseDTO` para usar `Set<DemandaMaterialResponseDTO>` em vez de `Set<MaterialResponseDTO>`

### Phase 3: Entidade Demanda e Service

Refatorar a entidade `Demanda` para usar `@OneToMany` com `DemandaMaterial`. Atualizar o `DemandaService` para manipular a nova estrutura.

**Tasks:**
- Atualizar `Demanda.java` - Substituir `@ManyToMany Set<Material>` por `@OneToMany Set<DemandaMaterial>`
- Atualizar `DemandaMapper.java` - Ajustar ignores
- Atualizar `DemandaService.java` - Refatorar create/update para criar `DemandaMaterial` com quantidade

### Phase 4: Testing & Validation

Atualizar testes existentes e adicionar novos para a entidade intermediaria.

**Tasks:**
- Atualizar `DemandaControllerTest.java` para refletir novos DTOs
- Criar testes para validacao de quantidade

---

## STEP-BY-STEP TASKS

IMPORTANT: Execute every task in order, top to bottom. Each task is atomic and independently testable.

### Task 1: CREATE `DemandaMaterial.java`

- **IMPLEMENT**: Criar entidade `DemandaMaterial` no package `com.sigesi.sigesi.demandas`
- **PATTERN**: Seguir padrao de `Demanda.java` (lines 35-41) para annotations
- **DETAILS**:
  ```java
  @Entity
  @Table(name = "demanda_material")
  @Data @AllArgsConstructor @NoArgsConstructor @Builder @Audited
  public class DemandaMaterial {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "demanda_id", nullable = false)
    private Demanda demanda;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @NotNull(message = "Quantidade é obrigatória")
    @Column(nullable = false)
    private Integer quantidade;
  }
  ```
- **IMPORTS**: `jakarta.persistence.*`, `jakarta.validation.constraints.NotNull`, `lombok.*`, `org.hibernate.envers.Audited`, entidades `Demanda` e `Material`
- **GOTCHA**: A tabela `demanda_material` ja existe como join table. O Hibernate `ddl-auto=update` vai tentar adicionar coluna `id` e `quantidade` a tabela existente. Pode ser necessario dropar a tabela antiga se houver conflito. Usar `@Table(name = "demanda_material")` para manter o mesmo nome de tabela.
- **GOTCHA**: Javadoc obrigatorio na declaracao da classe (checkstyle)
- **VALIDATE**: `mvn compile -pl . 2>&1 | tail -20`

### Task 2: CREATE `DemandaMaterialRepository.java`

- **IMPLEMENT**: Criar repository interface no package `com.sigesi.sigesi.demandas`
- **PATTERN**: Seguir padrao de `MaterialRepository.java`
- **DETAILS**:
  ```java
  @Repository
  public interface DemandaMaterialRepository
      extends JpaRepository<DemandaMaterial, Long> {
    Set<DemandaMaterial> findByDemandaId(Long demandaId);
    void deleteByDemandaId(Long demandaId);
  }
  ```
- **IMPORTS**: `org.springframework.data.jpa.repository.JpaRepository`, `org.springframework.stereotype.Repository`, `java.util.Set`
- **GOTCHA**: Javadoc obrigatorio na interface (checkstyle)
- **VALIDATE**: `mvn compile -pl . 2>&1 | tail -20`

### Task 3: CREATE `DemandaMaterialCreateDTO.java`

- **IMPLEMENT**: Criar DTO para receber materialId + quantidade no package `com.sigesi.sigesi.demandas.dtos`
- **PATTERN**: Seguir padrao de `DemandaCreateDTO.java`
- **DETAILS**:
  ```java
  @Data @AllArgsConstructor @NoArgsConstructor
  public class DemandaMaterialCreateDTO {
    @NotNull(message = "Material é obrigatório")
    private Long materialId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    private Integer quantidade;
  }
  ```
- **IMPORTS**: `jakarta.validation.constraints.NotNull`, `jakarta.validation.constraints.Min`, `lombok.*`
- **GOTCHA**: Javadoc obrigatorio na classe. Max line length 140 chars
- **VALIDATE**: `mvn compile -pl . 2>&1 | tail -20`

### Task 4: CREATE `DemandaMaterialResponseDTO.java`

- **IMPLEMENT**: Criar DTO de resposta com informacao do material + quantidade no package `com.sigesi.sigesi.demandas.dtos`
- **PATTERN**: Seguir padrao de `DemandaResponseDTO.java`
- **DETAILS**:
  ```java
  @Data @AllArgsConstructor @NoArgsConstructor @Builder
  public class DemandaMaterialResponseDTO {
    private Long id;
    private MaterialResponseDTO material;
    private Integer quantidade;
  }
  ```
- **IMPORTS**: `lombok.*`, `com.sigesi.sigesi.materiais.dtos.MaterialResponseDTO`
- **GOTCHA**: Javadoc obrigatorio
- **VALIDATE**: `mvn compile -pl . 2>&1 | tail -20`

### Task 5: CREATE `DemandaMaterialMapper.java`

- **IMPLEMENT**: Criar mapper MapStruct no package `com.sigesi.sigesi.demandas`
- **PATTERN**: Seguir padrao de `DemandaMapper.java` e `MaterialMapper.java`
- **DETAILS**:
  ```java
  @Mapper(componentModel = "spring",
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
      uses = {MaterialMapper.class})
  public interface DemandaMaterialMapper {

    DemandaMaterialResponseDTO toDto(DemandaMaterial entity);

    Set<DemandaMaterialResponseDTO> toDtoSet(Set<DemandaMaterial> entities);
  }
  ```
- **IMPORTS**: `org.mapstruct.*`, `java.util.Set`, `com.sigesi.sigesi.materiais.MaterialMapper`
- **GOTCHA**: Nao precisa de `toEntity` porque a criacao da entidade sera feita manualmente no Service (precisa setar demanda e material separadamente). Javadoc obrigatorio
- **VALIDATE**: `mvn compile -pl . 2>&1 | tail -20`

### Task 6: UPDATE `Demanda.java` - Substituir ManyToMany por OneToMany

- **IMPLEMENT**: Remover `@ManyToMany` com `Material` e substituir por `@OneToMany` com `DemandaMaterial`
- **PATTERN**: Relacionamento bidirecional com cascade
- **CHANGES**:
  - Remover imports de `CascadeType`, `JoinTable`, `ManyToMany` e `Material` (se nao usados)
  - Substituir o bloco `@ManyToMany` (lines 63-73) por:
    ```java
    @Builder.Default
    @OneToMany(mappedBy = "demanda",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private Set<DemandaMaterial> materiais = new HashSet<>();
    ```
  - Atualizar helper methods `addMaterial`/`removeMaterial` para usar `DemandaMaterial`:
    ```java
    public void addDemandaMaterial(DemandaMaterial item) {
      materiais.add(item);
      item.setDemanda(this);
    }

    public void removeDemandaMaterial(DemandaMaterial item) {
      materiais.remove(item);
      item.setDemanda(null);
    }
    ```
  - Remover imports nao usados: `JoinTable`, `ManyToMany`, `com.sigesi.sigesi.materiais.Material`
  - Adicionar imports: `OneToMany`
- **GOTCHA**: `orphanRemoval = true` garante que ao remover um `DemandaMaterial` do Set, ele sera deletado do banco. `CascadeType.ALL` inclui PERSIST e REMOVE. Javadoc nos novos metodos.
- **VALIDATE**: `mvn compile -pl . 2>&1 | tail -20`

### Task 7: UPDATE `DemandaCreateDTO.java` - Usar lista de itens com quantidade

- **IMPLEMENT**: Substituir `Set<Long> materiaisIds` por `List<DemandaMaterialCreateDTO> materiais`
- **CHANGES**:
  - Remover: `private Set<Long> materiaisIds;`
  - Adicionar: `private List<DemandaMaterialCreateDTO> materiais;`
  - Atualizar imports: Adicionar `java.util.List`, remover `java.util.Set`
  - Adicionar `@Valid` na lista para validar cada item
- **GOTCHA**: Usar `List` para preservar ordem no JSON. A anotacao `@Valid` na lista propaga a validacao para os itens internos (cada `DemandaMaterialCreateDTO` sera validado).
- **VALIDATE**: `mvn compile -pl . 2>&1 | tail -20`

### Task 8: UPDATE `DemandaUpdateDTO.java` - Usar lista de itens com quantidade

- **IMPLEMENT**: Substituir `Set<Long> materiaisIds` por `List<DemandaMaterialCreateDTO> materiais`
- **CHANGES**:
  - Remover: `private Set<Long> materiaisIds;`
  - Adicionar com `@Schema`:
    ```java
    @Schema(description = "Materiais com quantidade")
    private List<DemandaMaterialCreateDTO> materiais;
    ```
  - Atualizar imports: Adicionar `java.util.List`, remover `java.util.Set`
- **GOTCHA**: Nao adicionar `@Valid` aqui pois UpdateDTO nao valida campos (todos opcionais)
- **VALIDATE**: `mvn compile -pl . 2>&1 | tail -20`

### Task 9: UPDATE `DemandaResponseDTO.java` - Usar DemandaMaterialResponseDTO

- **IMPLEMENT**: Substituir `Set<MaterialResponseDTO> materiais` por `Set<DemandaMaterialResponseDTO> materiais`
- **CHANGES**:
  - Substituir: `private Set<MaterialResponseDTO> materiais;` por `private Set<DemandaMaterialResponseDTO> materiais;`
  - Atualizar imports: Remover `MaterialResponseDTO`, adicionar `DemandaMaterialResponseDTO`
- **VALIDATE**: `mvn compile -pl . 2>&1 | tail -20`

### Task 10: UPDATE `DemandaMapper.java` - Ajustar para nova estrutura

- **IMPLEMENT**: Atualizar mapper para usar `DemandaMaterialMapper` em vez de `MaterialMapper`
- **CHANGES**:
  - Alterar `uses`: de `{SolicitacaoMapper.class, MaterialMapper.class}` para `{SolicitacaoMapper.class, DemandaMaterialMapper.class}`
  - Remover import de `MaterialMapper`, adicionar import de `DemandaMaterialMapper` (se necessario, o import pode ser implicito por estar no mesmo package)
- **GOTCHA**: O `toDto` de Demanda agora precisa converter `Set<DemandaMaterial>` para `Set<DemandaMaterialResponseDTO>` - isso e feito automaticamente pelo MapStruct usando o `DemandaMaterialMapper` no `uses`
- **VALIDATE**: `mvn compile -pl . 2>&1 | tail -20`

### Task 11: UPDATE `DemandaService.java` - Refatorar create e update

- **IMPLEMENT**: Refatorar `createDemanda` e `updateDemanda` para criar entidades `DemandaMaterial` com quantidade
- **CHANGES no createDemanda (lines 90-115)**:
  - Substituir bloco de materiaisIds (lines 102-105) por logica que itera sobre `dto.getMateriais()`:
    ```java
    if (dto.getMateriais() != null && !dto.getMateriais().isEmpty()) {
      for (DemandaMaterialCreateDTO item : dto.getMateriais()) {
        Material material = materialService
            .getMaterialEntityById(item.getMaterialId());
        DemandaMaterial dm = DemandaMaterial.builder()
            .material(material)
            .quantidade(item.getQuantidade())
            .build();
        demanda.addDemandaMaterial(dm);
      }
    }
    ```
  - Adicionar imports: `DemandaMaterial`, `DemandaMaterialCreateDTO`
  - Remover imports nao usados: `HashSet` (se nao usado em outro lugar)

- **CHANGES no updateDemanda (lines 120-156)**:
  - Substituir bloco de materiaisIds (lines 134-141) por:
    ```java
    if (dto.getMateriais() != null) {
      demanda.getMateriais().clear();
      for (DemandaMaterialCreateDTO item : dto.getMateriais()) {
        Material material = materialService
            .getMaterialEntityById(item.getMaterialId());
        DemandaMaterial dm = DemandaMaterial.builder()
            .material(material)
            .quantidade(item.getQuantidade())
            .build();
        demanda.addDemandaMaterial(dm);
      }
    }
    ```
  - A estrategia `clear() + re-add` funciona com `orphanRemoval = true` - os itens antigos serao deletados automaticamente

- **GOTCHA**: O metodo `createDemanda` pode ultrapassar 50 linhas (checkstyle). Se necessario, extrair a logica de materiais para um metodo privado `resolveMateriais(List<DemandaMaterialCreateDTO>, Demanda)`. Max 3 return statements por metodo.
- **GOTCHA**: Remover `import java.util.HashSet` se nao for mais utilizado. Verificar import de `Set` se ainda necessario.
- **VALIDATE**: `mvn compile -pl . 2>&1 | tail -20`

### Task 12: UPDATE `DemandaControllerTest.java` - Ajustar para novos DTOs

- **IMPLEMENT**: Atualizar testes para refletir a nova estrutura de DTOs
- **CHANGES**:
  - No metodo `responseDto` (line 55-66): Substituir `materiais(new HashSet<>())` por `materiais(new HashSet<>())` (mesmo tipo mas agora `DemandaMaterialResponseDTO`)
  - Atualizar imports: Adicionar `DemandaMaterialResponseDTO`, `DemandaMaterialCreateDTO`. Remover imports nao usados
  - No teste `testCreateRetorna201QuandoCriadaComSucesso` (lines 131-147):
    - Substituir `createDto.setMateriaisIds(Set.of(1L, 2L))` por:
      ```java
      DemandaMaterialCreateDTO item1 = new DemandaMaterialCreateDTO(1L, 5);
      DemandaMaterialCreateDTO item2 = new DemandaMaterialCreateDTO(2L, 10);
      createDto.setMateriais(List.of(item1, item2));
      ```
    - Atualizar imports: Adicionar `java.util.List`, `DemandaMaterialCreateDTO`
- **GOTCHA**: Verificar que todos os imports removidos nao sao usados em outros testes
- **VALIDATE**: `mvn test -Dtest=DemandaControllerTest 2>&1 | tail -30`

### Task 13: RUN Full Validation

- **IMPLEMENT**: Executar suite completa de testes e checkstyle
- **VALIDATE**: `mvn checkstyle:check 2>&1 | tail -20`
- **VALIDATE**: `mvn test 2>&1 | tail -30`

---

## TESTING STRATEGY

### Unit Tests

- **DemandaControllerTest**: Atualizar testes existentes para usar novos DTOs (`DemandaMaterialCreateDTO` com materialId + quantidade). Verificar que o endpoint POST aceita a nova estrutura.
- **DemandaServiceTest** (se existir ou criar): Testar que `createDemanda` cria `DemandaMaterial` entities com quantidade correta. Testar que `updateDemanda` com `clear() + re-add` funciona.

### Integration Tests

- Validar via Swagger UI que os endpoints aceitam o novo formato de JSON:
  ```json
  {
    "solicitacaoId": 1,
    "responsavelId": 1,
    "prazo": "2025-12-31",
    "materiais": [
      { "materialId": 1, "quantidade": 5 },
      { "materialId": 2, "quantidade": 10 }
    ]
  }
  ```

### Edge Cases

- Criar demanda sem materiais (lista nula ou vazia) - deve funcionar
- Criar demanda com material que nao existe - deve retornar 404
- Criar demanda com quantidade 0 ou negativa - deve retornar 400 (validacao `@Min(1)`)
- Atualizar demanda removendo todos os materiais (lista vazia) - deve limpar materiais
- Atualizar demanda com novos materiais - deve substituir os anteriores

---

## VALIDATION COMMANDS

### Level 1: Syntax & Style

```bash
mvn checkstyle:check
```

### Level 2: Compilation

```bash
mvn compile
```

### Level 3: Unit Tests

```bash
mvn test -Dtest=DemandaControllerTest
mvn test -Dtest=MaterialControllerTest
```

### Level 4: Full Test Suite

```bash
mvn test
```

### Level 5: Manual Validation

```
POST /api/demandas/ com body:
{
  "solicitacaoId": <id_valido>,
  "prazo": "2026-06-30",
  "materiais": [
    { "materialId": <id_valido>, "quantidade": 5 }
  ]
}

GET /api/demandas/<id> - verificar que materiais retornam com quantidade
```

---

## ACCEPTANCE CRITERIA

- [ ] Entidade `DemandaMaterial` criada com campos id, demanda, material, quantidade
- [ ] Repository `DemandaMaterialRepository` criado
- [ ] DTOs `DemandaMaterialCreateDTO` e `DemandaMaterialResponseDTO` criados
- [ ] Mapper `DemandaMaterialMapper` criado
- [ ] `Demanda.java` refatorado para usar `@OneToMany` com `DemandaMaterial`
- [ ] `DemandaCreateDTO` aceita lista de materiais com quantidade
- [ ] `DemandaUpdateDTO` aceita lista de materiais com quantidade
- [ ] `DemandaResponseDTO` retorna materiais com quantidade
- [ ] `DemandaService` cria/atualiza `DemandaMaterial` corretamente
- [ ] `DemandaMapper` usa `DemandaMaterialMapper`
- [ ] Todos os testes existentes atualizados e passando
- [ ] Checkstyle sem violacoes
- [ ] Validacao de quantidade > 0 funciona
- [ ] Criar demanda sem materiais continua funcionando

---

## COMPLETION CHECKLIST

- [ ] All tasks completed in order (1-13)
- [ ] Each task validation passed immediately
- [ ] All validation commands executed successfully
- [ ] Full test suite passes (unit + integration)
- [ ] No linting or type checking errors
- [ ] Manual testing confirms feature works
- [ ] Acceptance criteria all met
- [ ] Code reviewed for quality and maintainability

---

## NOTES

### Design Decisions

1. **Entidade intermediaria vs @ManyToMany com @JoinTable extra columns**: Escolhemos entidade intermediaria porque e o padrao mais limpo e extensivel do JPA. Permite adicionar mais campos no futuro (ex: unidade de medida, observacao).

2. **`@Table(name = "demanda_material")`**: Reutilizamos o nome da tabela existente. O Hibernate `ddl-auto=update` vai adicionar as novas colunas (`id`, `quantidade`). **RISCO**: Se a tabela existente tiver dados, a migracao automatica pode falhar por falta de valores para `quantidade` (NOT NULL). Solucao: Pode ser necessario dropar a tabela antiga antes ou usar um script SQL de migracao manual.

3. **`orphanRemoval = true`**: Garante que ao fazer `clear()` no Set de materiais durante update, os registros antigos sejam deletados do banco automaticamente.

4. **`CascadeType.ALL`**: Permite que ao salvar a `Demanda`, os `DemandaMaterial` associados sejam salvos automaticamente (PERSIST + MERGE + REMOVE).

5. **`List<DemandaMaterialCreateDTO>` no CreateDTO**: Usamos `List` no DTO de entrada para preservar a ordem no JSON. No Response usamos `Set` para consistencia com o modelo.

### Riscos

- **Migracao de dados**: Se houver dados na tabela `demanda_material` atual, a migracao automatica do Hibernate pode nao funcionar perfeitamente. Recomenda-se fazer backup e, se necessario, dropar a tabela antes do primeiro deploy.
- **Performance**: `orphanRemoval` + `clear()` gera DELETE + INSERT em cada update. Para volumes grandes, considerar diff mais inteligente no futuro.

<!-- EOF -->

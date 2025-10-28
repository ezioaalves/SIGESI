# Plano de Testes Unitários - Módulo Cemitério

## Visão Geral

Este documento descreve o plano de testes unitários para o módulo Cemitério do sistema SIGESI. O objetivo é garantir a qualidade e confiabilidade de todas as camadas do módulo: entidade, repositório, serviço e controller.

## Estrutura do Módulo

### Entidade: Cemiterio
- **id**: Long (PK, auto-gerado)
- **nome**: String (obrigatório, @NotBlank)
- **endereco**: Endereco (obrigatório, @NotNull, OneToOne)

### Dependências
- **EnderecoService**: Para validação de endereços existentes
- **CemiterioRepository**: Para persistência de dados

---

## 1. CemiterioEntityTest

Testes focados na validação de constraints da entidade JPA e Bean Validation.

### 1.1 Testes de Validação de Campos Obrigatórios

#### testNomeNaoPodeSerNulo
- **Objetivo**: Validar que o campo `nome` não pode ser null
- **Entrada**: Cemiterio com nome = null
- **Resultado Esperado**: Violação de constraint @NotBlank
- **Mensagem Esperada**: "Nome é obrigatório"

#### testNomeNaoPodeSerVazio
- **Objetivo**: Validar que o campo `nome` não pode ser string vazia
- **Entrada**: Cemiterio com nome = ""
- **Resultado Esperado**: Violação de constraint @NotBlank
- **Mensagem Esperada**: "Nome é obrigatório"

#### testNomeNaoPodeSerApenasEspacos
- **Objetivo**: Validar que o campo `nome` não pode ser apenas espaços em branco
- **Entrada**: Cemiterio com nome = "   "
- **Resultado Esperado**: Violação de constraint @NotBlank
- **Mensagem Esperada**: "Nome é obrigatório"

#### testEnderecoNaoPodeSerNulo
- **Objetivo**: Validar que o campo `endereco` não pode ser null
- **Entrada**: Cemiterio com endereco = null
- **Resultado Esperado**: Violação de constraint @NotNull
- **Mensagem Esperada**: "Endereço é obrigatório"

### 1.2 Testes de Criação com Sucesso

#### testCriacaoCemiterioValido
- **Objetivo**: Validar criação de cemitério com todos os campos válidos
- **Entrada**: Cemiterio com nome válido e endereço válido
- **Resultado Esperado**: Nenhuma violação de constraint
- **Validações**: Nome preenchido, endereço não-nulo

#### testBuilderPattern
- **Objetivo**: Validar que o padrão Builder funciona corretamente
- **Entrada**: Usar Cemiterio.builder() para criar instância
- **Resultado Esperado**: Objeto criado com sucesso
- **Validações**: Todos os campos configurados via builder

#### testLombokGettersSetters
- **Objetivo**: Validar que getters e setters do Lombok funcionam
- **Entrada**: Cemiterio criado, modificando campos via setters
- **Resultado Esperado**: Getters retornam valores corretos após setters

---

## 2. CemiterioServiceTest

Testes da lógica de negócio e interação com repositório e serviços externos.

**Configuração dos Testes:**
- Mock de CemiterioRepository
- Mock de EnderecoService
- Instância real de CemiterioService (com mocks injetados)

### 2.1 Testes do método getAll()

#### testGetAllRetornaListaVazia
- **Objetivo**: Validar comportamento quando não há cemitérios no banco
- **Mock Setup**: repository.findAll() retorna lista vazia
- **Resultado Esperado**: Lista vazia (não null)
- **Validações**: Método repository.findAll() chamado 1 vez

#### testGetAllRetornaListaComCemiterios
- **Objetivo**: Validar retorno de lista com cemitérios existentes
- **Mock Setup**: repository.findAll() retorna lista com 3 cemitérios
- **Resultado Esperado**: Lista com 3 cemitérios
- **Validações**: Método repository.findAll() chamado 1 vez

### 2.2 Testes do método getCemiterioById(Long id)

#### testGetCemiterioByIdComSucesso
- **Objetivo**: Validar busca de cemitério existente por ID
- **Mock Setup**: repository.findById(1L) retorna Optional com cemitério
- **Entrada**: id = 1L
- **Resultado Esperado**: Cemiterio com id = 1L
- **Validações**:
  - Método repository.findById() chamado 1 vez com id = 1L
  - Cemitério retornado não é null

#### testGetCemiterioByIdLancaExcecaoQuandoNaoEncontrado
- **Objetivo**: Validar exception quando ID não existe
- **Mock Setup**: repository.findById(999L) retorna Optional.empty()
- **Entrada**: id = 999L
- **Resultado Esperado**: RuntimeException lançada
- **Mensagem Esperada**: "Cemitério não encontrado com id 999"
- **Validações**: Método repository.findById() chamado 1 vez

### 2.3 Testes do método createCemiterio(Cemiterio cemiterio)

#### testCreateCemiterioComSucesso
- **Objetivo**: Validar criação com endereço válido
- **Mock Setup**:
  - enderecoService.getEnderecoById(1L) retorna endereço válido
  - repository.save() retorna cemitério com id gerado
- **Entrada**: Cemiterio com endereco.id = 1L
- **Resultado Esperado**: Cemiterio salvo com id gerado
- **Validações**:
  - enderecoService.getEnderecoById() chamado 1 vez com id = 1L
  - repository.save() chamado 1 vez

#### testCreateCemiterioValidaEnderecoExistente
- **Objetivo**: Validar que EnderecoService é chamado para validar endereço
- **Mock Setup**: enderecoService.getEnderecoById() configurado
- **Entrada**: Cemiterio com endereco.id = 5L
- **Validações**:
  - enderecoService.getEnderecoById() chamado exatamente 1 vez com id = 5L
  - Validação ocorre antes do save

#### testCreateCemiterioComEnderecoNulo
- **Objetivo**: Validar comportamento quando endereço é null
- **Mock Setup**: repository.save() configurado
- **Entrada**: Cemiterio com endereco = null
- **Resultado Esperado**: Save executado sem validar endereço
- **Validações**:
  - enderecoService.getEnderecoById() NÃO deve ser chamado
  - repository.save() chamado 1 vez

#### testCreateCemiterioComEnderecoSemId
- **Objetivo**: Validar comportamento quando endereço não tem ID
- **Mock Setup**: repository.save() configurado
- **Entrada**: Cemiterio com endereco.id = null
- **Resultado Esperado**: Save executado sem validar endereço
- **Validações**:
  - enderecoService.getEnderecoById() NÃO deve ser chamado
  - repository.save() chamado 1 vez

### 2.4 Testes do método updateCemiterio(Long id, Cemiterio cemiterioAtualizado)

#### testUpdateCemiterioComSucesso
- **Objetivo**: Validar atualização de nome e endereço
- **Mock Setup**:
  - repository.findById(1L) retorna cemitério existente
  - enderecoService.getEnderecoById(2L) retorna endereço válido
  - repository.save() retorna cemitério atualizado
- **Entrada**: id = 1L, cemiterio com novo nome e endereco.id = 2L
- **Resultado Esperado**: Cemiterio atualizado com novos valores
- **Validações**:
  - Nome atualizado
  - Endereço atualizado
  - repository.save() chamado 1 vez

#### testUpdateCemiterioApenasNome
- **Objetivo**: Validar atualização apenas do nome
- **Mock Setup**: repository.findById() e save() configurados
- **Entrada**: id = 1L, cemiterio com novo nome, endereco = null
- **Resultado Esperado**: Apenas nome atualizado
- **Validações**:
  - enderecoService NÃO deve ser chamado
  - Nome foi alterado

#### testUpdateCemiterioApenasEndereco
- **Objetivo**: Validar atualização apenas do endereço
- **Mock Setup**: repository e enderecoService configurados
- **Entrada**: id = 1L, cemiterio com mesmo nome, novo endereco.id = 3L
- **Resultado Esperado**: Apenas endereço atualizado
- **Validações**:
  - enderecoService.getEnderecoById(3L) chamado 1 vez
  - Endereço foi alterado

#### testUpdateCemiterioLancaExcecaoQuandoNaoEncontrado
- **Objetivo**: Validar exception quando cemitério não existe
- **Mock Setup**: repository.findById(999L) retorna Optional.empty()
- **Entrada**: id = 999L, cemiterio com dados válidos
- **Resultado Esperado**: RuntimeException lançada
- **Mensagem Esperada**: "Cemitério não encontrado com id 999"
- **Validações**:
  - repository.save() NÃO deve ser chamado
  - enderecoService NÃO deve ser chamado

#### testUpdateCemiterioValidaEnderecoExistente
- **Objetivo**: Validar que EnderecoService é chamado na atualização
- **Mock Setup**: repository e enderecoService configurados
- **Entrada**: cemiterio com endereco.id = 10L
- **Validações**: enderecoService.getEnderecoById(10L) chamado exatamente 1 vez

### 2.5 Testes do método deleteCemiterio(Long id)

#### testDeleteCemiterioComSucesso
- **Objetivo**: Validar exclusão de cemitério existente
- **Mock Setup**: repository.findById(1L) retorna cemitério
- **Entrada**: id = 1L
- **Resultado Esperado**: Sem retorno (void)
- **Validações**:
  - repository.findById() chamado 1 vez
  - repository.delete() chamado 1 vez com cemitério correto

#### testDeleteCemiterioLancaExcecaoQuandoNaoEncontrado
- **Objetivo**: Validar exception quando cemitério não existe
- **Mock Setup**: repository.findById(999L) retorna Optional.empty()
- **Entrada**: id = 999L
- **Resultado Esperado**: RuntimeException lançada
- **Mensagem Esperada**: "Cemitério não encontrado com id 999"
- **Validações**: repository.delete() NÃO deve ser chamado

---

## 3. CemiterioControllerTest

Testes dos endpoints REST e integração HTTP.

**Configuração dos Testes:**
- MockMvc para simular requisições HTTP
- Mock de CemiterioService
- @WebMvcTest(CemiterioController.class)

### 3.1 Testes do endpoint GET /api/cemiterios/

#### testListAllRetorna200ComListaVazia
- **Objetivo**: Validar status 200 com lista vazia
- **Mock Setup**: service.getAll() retorna lista vazia
- **Request**: GET /api/cemiterios/
- **Response Esperado**:
  - Status: 200 OK
  - Body: [] (array vazio)
  - Content-Type: application/json

#### testListAllRetorna200ComCemiterios
- **Objetivo**: Validar status 200 com cemitérios
- **Mock Setup**: service.getAll() retorna lista com 2 cemitérios
- **Request**: GET /api/cemiterios/
- **Response Esperado**:
  - Status: 200 OK
  - Body: array com 2 objetos
  - Validar estrutura JSON (id, nome, endereco)

### 3.2 Testes do endpoint GET /api/cemiterios/{id}

#### testGetByIdRetorna200ComCemiterio
- **Objetivo**: Validar status 200 e cemitério encontrado
- **Mock Setup**: service.getCemiterioById(1L) retorna cemitério
- **Request**: GET /api/cemiterios/1
- **Response Esperado**:
  - Status: 200 OK
  - Body: objeto cemitério com id = 1
  - Validar campos do JSON

#### testGetByIdRetorna404QuandoNaoEncontrado
- **Objetivo**: Validar status 404 quando não existe
- **Mock Setup**: service.getCemiterioById(999L) lança RuntimeException
- **Request**: GET /api/cemiterios/999
- **Response Esperado**:
  - Status: 404 Not Found ou 500 Internal Server Error (dependendo do error handler)

### 3.3 Testes do endpoint POST /api/cemiterios/

#### testCreateRetorna201ComCemiterioValido
- **Objetivo**: Validar status 201 e criação com sucesso
- **Mock Setup**: service.createCemiterio() retorna cemitério com id = 1L
- **Request**:
  - POST /api/cemiterios/
  - Body: {"nome": "Cemitério Central", "endereco": {"id": 1}}
- **Response Esperado**:
  - Status: 201 Created
  - Body: cemitério criado com id gerado

#### testCreateRetorna400SemNome
- **Objetivo**: Validar status 400 quando nome é omitido
- **Request**:
  - POST /api/cemiterios/
  - Body: {"endereco": {"id": 1}}
- **Response Esperado**:
  - Status: 400 Bad Request
  - Validação: erro sobre campo "nome"

#### testCreateRetorna400ComNomeVazio
- **Objetivo**: Validar status 400 quando nome é vazio
- **Request**:
  - POST /api/cemiterios/
  - Body: {"nome": "", "endereco": {"id": 1}}
- **Response Esperado**:
  - Status: 400 Bad Request
  - Mensagem: "Nome é obrigatório"

#### testCreateRetorna400SemEndereco
- **Objetivo**: Validar status 400 quando endereço é omitido
- **Request**:
  - POST /api/cemiterios/
  - Body: {"nome": "Cemitério Central"}
- **Response Esperado**:
  - Status: 400 Bad Request
  - Mensagem: "Endereço é obrigatório"

#### testCreateRetorna400ComNomeApenasEspacos
- **Objetivo**: Validar status 400 quando nome é apenas espaços
- **Request**:
  - POST /api/cemiterios/
  - Body: {"nome": "   ", "endereco": {"id": 1}}
- **Response Esperado**:
  - Status: 400 Bad Request
  - Mensagem: "Nome é obrigatório"

### 3.4 Testes do endpoint PUT /api/cemiterios/{id}

#### testUpdateRetorna200ComCemiterioAtualizado
- **Objetivo**: Validar status 200 e atualização com sucesso
- **Mock Setup**: service.updateCemiterio(1L, ...) retorna cemitério atualizado
- **Request**:
  - PUT /api/cemiterios/1
  - Body: {"nome": "Novo Nome", "endereco": {"id": 2}}
- **Response Esperado**:
  - Status: 200 OK
  - Body: cemitério com dados atualizados

#### testUpdateRetorna400ComDadosInvalidos
- **Objetivo**: Validar status 400 com validação
- **Request**:
  - PUT /api/cemiterios/1
  - Body: {"nome": "", "endereco": {"id": 1}}
- **Response Esperado**:
  - Status: 400 Bad Request
  - Erro de validação

#### testUpdateRetorna404QuandoNaoEncontrado
- **Objetivo**: Validar status 404 quando cemitério não existe
- **Mock Setup**: service.updateCemiterio(999L, ...) lança RuntimeException
- **Request**: PUT /api/cemiterios/999
- **Response Esperado**: Status 404 ou 500

### 3.5 Testes do endpoint DELETE /api/cemiterios/{id}

#### testDeleteRetorna204QuandoExcluiComSucesso
- **Objetivo**: Validar status 204 na exclusão
- **Mock Setup**: service.deleteCemiterio(1L) executa sem erro
- **Request**: DELETE /api/cemiterios/1
- **Response Esperado**:
  - Status: 204 No Content
  - Body vazio

#### testDeleteRetorna404QuandoNaoEncontrado
- **Objetivo**: Validar status 404 quando não existe
- **Mock Setup**: service.deleteCemiterio(999L) lança RuntimeException
- **Request**: DELETE /api/cemiterios/999
- **Response Esperado**: Status 404 ou 500

---

## Resumo de Cobertura

### Estatísticas Esperadas
- **Total de Testes**: ~40 casos de teste
- **CemiterioEntityTest**: 7 testes (validação de entidade)
- **CemiterioServiceTest**: 13 testes (lógica de negócio)
- **CemiterioControllerTest**: 12 testes (endpoints REST)

### Cobertura de Código Esperada
- **Cemiterio (Entity)**: 100% (getters, setters, builder)
- **CemiterioRepository**: N/A (interface JPA)
- **CemiterioService**: ~95% (todos os métodos e fluxos)
- **CemiterioController**: ~95% (todos os endpoints)

### Cenários Cobertos
✅ Validação de campos obrigatórios
✅ Criação com sucesso
✅ Atualização com sucesso
✅ Exclusão com sucesso
✅ Busca por ID
✅ Listagem completa
✅ Tratamento de entidades não encontradas
✅ Validação de dependências (Endereco)
✅ Validação de entrada HTTP
✅ Status HTTP corretos

---

## Tecnologias e Frameworks

### Dependências de Teste
```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Boot Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Bean Validation Test -->
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <scope>test</scope>
</dependency>
```

### Anotações Principais
- `@ExtendWith(MockitoExtension.class)` - Para testes com Mockito
- `@Mock` - Para criar mocks
- `@InjectMocks` - Para injetar mocks
- `@WebMvcTest` - Para testes de controllers
- `@Test` - Para marcar métodos de teste
- `@DisplayName` - Para nomes descritivos de testes

---

## Estrutura de Diretórios

```
src/test/java/com/sigesi/sigesi/cemiterios/
├── CemiterioEntityTest.java         (7 testes)
├── CemiterioServiceTest.java        (13 testes)
└── CemiterioControllerTest.java     (12 testes)
```

---

## Observações Importantes

1. **Mocks vs Integração**: Estes são testes unitários com mocks. Testes de integração seriam necessários para validar a integração real com banco de dados.

2. **Bean Validation**: Os testes de validação da entidade requerem um `Validator` do Hibernate Validator.

3. **Error Handling**: O módulo atualmente lança `RuntimeException`. Considerar criar exceptions customizadas no futuro.

4. **Transações**: Testes de repository (se implementados) devem usar `@Transactional` e `@Rollback`.

5. **Checkstyle**: Todos os arquivos de teste devem seguir as mesmas regras de Checkstyle do projeto.

---

## Próximos Passos

1. ✅ Documentar plano de testes (este arquivo)
2. ⏳ Implementar CemiterioEntityTest
3. ⏳ Implementar CemiterioServiceTest
4. ⏳ Implementar CemiterioControllerTest
5. ⏳ Executar testes e validar cobertura
6. ⏳ Corrigir eventuais falhas
7. ⏳ Revisar código com Checkstyle

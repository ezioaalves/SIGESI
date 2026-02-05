# Feature: Filtro de Solicitações por Role do Usuário

## Feature Description

Implementar filtragem baseada em role na listagem de solicitações (`GET /api/solicitacoes/`). Usuários com role `CIDADAO` ou `AGENTE` devem ver apenas as solicitações que eles próprios criaram (onde são o `autor`). Usuários com role `ADMIN` ou `OPERADOR` devem ver todas as solicitações.

## User Story

Como um **cidadão ou agente**
Eu quero ver apenas as minhas solicitações na rota de listagem
Para que eu não tenha acesso a solicitações de outros usuários

Como um **admin ou operador**
Eu quero ver todas as solicitações
Para que eu possa gerenciar e acompanhar todas as demandas do sistema

## Problem Statement

Atualmente, o endpoint `GET /api/solicitacoes/` retorna **todas** as solicitações para qualquer usuário autenticado, independente da sua role. Isso é um problema de segurança e privacidade, pois cidadãos e agentes conseguem ver solicitações de outros usuários. A filtragem precisa acontecer no nível do serviço, usando a identidade do usuário logado e sua role para decidir quais registros retornar.

## Solution Statement

Modificar o `SolicitacaoController` para injetar o `Authentication` no endpoint de listagem e passar o usuário autenticado ao `SolicitacaoService`. No service, verificar a role do usuário: se for `ADMIN` ou `OPERADOR`, retornar todas as solicitações; se for `CIDADAO` ou `AGENTE`, retornar apenas as solicitações onde o `autor_id` é o ID do usuário logado. O repository já possui o método `findByAutorIdOrderByDataDesc(Long autorId)` que será reutilizado.

## Feature Metadata

**Feature Type**: Enhancement
**Estimated Complexity**: Low
**Primary Systems Affected**: SolicitacaoController, SolicitacaoService
**Dependencies**: Nenhuma nova dependência necessária

---

## CONTEXT REFERENCES

### Relevant Codebase Files

- `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoController.java` (lines 32-36) - Why: Endpoint `listAll()` que precisa receber `Authentication` e delegar ao service
- `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoService.java` (lines 38-43) - Why: Método `getAll()` que precisa da lógica de filtragem por role
- `src/main/java/com/sigesi/sigesi/solicitacoes/SolicitacaoRepository.java` (line 15) - Why: Já possui `findByAutorIdOrderByDataDesc(Long autorId)` que será reutilizado
- `src/main/java/com/sigesi/sigesi/authentication/CustomOAuth2User.java` (lines 16-27) - Why: Como obter o `Usuario` do `Authentication` principal
- `src/main/java/com/sigesi/sigesi/usuarios/enums/Role.java` (lines 13-17) - Why: Enum com roles disponíveis (CIDADAO, OPERADOR, AGENTE, ADMIN)
- `src/main/java/com/sigesi/sigesi/usuarios/UsuarioController.java` (lines 28-36) - Why: Padrão de como injetar `Authentication` e fazer cast para `CustomOAuth2User`
- `src/main/java/com/sigesi/sigesi/solicitacoes/Solicitacao.java` (lines 61-63) - Why: Relação `autor` (ManyToOne com Usuario)

### New Files to Create

Nenhum arquivo novo necessário.

### Relevant Documentation

- [Spring Security Authentication](https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html)
  - Seção: Authentication Principal
  - Why: Como acessar o usuário autenticado nos controllers
- [Spring Data JPA Query Methods](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)
  - Seção: Query Creation
  - Why: Referência para os query methods do repository

### Patterns to Follow

**Obtendo o usuário autenticado (padrão existente em `UsuarioController.java:29-35`):**
```java
@GetMapping("/me")
public Object me(Authentication auth) {
    CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal();
    // user.getUser() retorna o objeto Usuario com id, role, etc.
}
```

**Verificação de Role (padrão a seguir):**
```java
Role role = usuario.getRole();
if (role == Role.ADMIN || role == Role.OPERADOR) {
    // retorna tudo
} else {
    // retorna apenas do autor
}
```

**Naming Convention:** camelCase para métodos e variáveis, classes em PascalCase.

**Service pattern:** Métodos do service recebem parâmetros necessários e encapsulam a lógica de negócio.

---

## IMPLEMENTATION PLAN

### Phase 1: Core Implementation

Modificar o service para aceitar o `Usuario` autenticado e filtrar com base na role. Modificar o controller para passar o `Authentication` ao service.

**Tasks:**
- Alterar assinatura do `getAll()` no SolicitacaoService para receber `Usuario`
- Implementar lógica condicional: ADMIN/OPERADOR veem tudo, CIDADAO/AGENTE veem apenas as próprias
- Alterar o controller para injetar `Authentication` e extrair o `Usuario`

### Phase 2: Validation

Testar manualmente via Swagger ou API client que a filtragem funciona corretamente.

**Tasks:**
- Executar checkstyle
- Executar build
- Validar manualmente com diferentes roles

---

## STEP-BY-STEP TASKS

IMPORTANT: Execute every task in order, top to bottom. Each task is atomic and independently testable.

### Task 1: UPDATE `SolicitacaoService.java` - Alterar método `getAll()` para filtrar por role

- **IMPLEMENT**: Alterar a assinatura do método `getAll()` para receber `Usuario usuario`. Dentro do método, verificar `usuario.getRole()`: se for `Role.ADMIN` ou `Role.OPERADOR`, retornar `findAllByOrderByIdAsc()` (comportamento atual). Se for `Role.CIDADAO` ou `Role.AGENTE`, retornar `findByAutorIdOrderByDataDesc(usuario.getId())`.
- **PATTERN**: Verificação de role similar ao padrão em `UsuarioController.java:29-35` para acesso ao usuario
- **IMPORTS**: `com.sigesi.sigesi.usuarios.enums.Role` (já importado indiretamente via Usuario)
- **GOTCHA**: O repository já tem `findByAutorIdOrderByDataDesc(Long autorId)` - reutilizar, não criar novo query method. A ordenação é diferente (um por id asc, outro por data desc) - manter a semântica de cada um.
- **GOTCHA**: Checkstyle exige max 3 return statements por método e max 50 linhas por método. Manter o código simples com um if/else.
- **VALIDATE**: `mvn checkstyle:check`

**Código esperado para o método `getAll`:**
```java
public List<SolicitacaoResponseDTO> getAll(Usuario usuario) {
    List<Solicitacao> solicitacoes;

    if (usuario.getRole() == Role.ADMIN
        || usuario.getRole() == Role.OPERADOR) {
      solicitacoes = solicitacaoRepository.findAllByOrderByIdAsc();
    } else {
      solicitacoes = solicitacaoRepository
          .findByAutorIdOrderByDataDesc(usuario.getId());
    }

    return solicitacoes.stream()
        .map(solicitacaoMapper::toDto)
        .collect(Collectors.toList());
}
```

### Task 2: UPDATE `SolicitacaoService.java` - Adicionar import do Role

- **IMPLEMENT**: Adicionar `import com.sigesi.sigesi.usuarios.enums.Role;` nos imports do service.
- **GOTCHA**: Checkstyle proíbe imports não utilizados. Só adicionar se for usado.
- **VALIDATE**: `mvn checkstyle:check`

### Task 3: UPDATE `SolicitacaoController.java` - Passar Authentication para o service

- **IMPLEMENT**: Alterar o método `listAll()` para receber `Authentication auth` como parâmetro. Extrair o `Usuario` com `CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal()` e passar `user.getUser()` para `solicitacaoService.getAll()`.
- **PATTERN**: Mesmo padrão usado em `UsuarioController.java:28-35` (método `me()`)
- **IMPORTS**: Adicionar `import org.springframework.security.core.Authentication;` e `import com.sigesi.sigesi.authentication.CustomOAuth2User;`
- **GOTCHA**: O Spring Security injeta automaticamente o `Authentication` como parâmetro do método do controller. Não precisa de `@AuthenticationPrincipal` ou busca manual no SecurityContext.
- **VALIDATE**: `mvn checkstyle:check`

**Código esperado para o método `listAll`:**
```java
@GetMapping("/")
public ResponseEntity<List<SolicitacaoResponseDTO>> listAll(
    Authentication auth) {
    CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal();
    List<SolicitacaoResponseDTO> solicitacoes =
        solicitacaoService.getAll(user.getUser());
    return ResponseEntity.ok(solicitacoes);
}
```

### Task 4: BUILD - Verificar que tudo compila e passa checkstyle

- **IMPLEMENT**: Executar build completo para garantir zero erros.
- **VALIDATE**: `mvn clean install -DskipTests`

---

## TESTING STRATEGY

### Unit Tests

Os testes do SolicitacaoController estão atualmente comentados. A validação será feita via build + checkstyle + teste manual.

### Manual Testing

1. Logar com um usuário ADMIN → `GET /api/solicitacoes/` deve retornar todas as solicitações
2. Logar com um usuário OPERADOR → `GET /api/solicitacoes/` deve retornar todas as solicitações
3. Logar com um usuário CIDADAO → `GET /api/solicitacoes/` deve retornar apenas solicitações onde `autor.id == usuario.id`
4. Logar com um usuário AGENTE → `GET /api/solicitacoes/` deve retornar apenas solicitações onde `autor.id == usuario.id`

### Edge Cases

- Usuário CIDADAO sem nenhuma solicitação criada → deve retornar lista vazia
- Usuário AGENTE que criou solicitações → deve ver apenas as suas

---

## VALIDATION COMMANDS

### Level 1: Syntax & Style

```bash
mvn checkstyle:check
```

### Level 2: Build

```bash
mvn clean install -DskipTests
```

### Level 3: Full Build with Tests

```bash
mvn clean install
```

### Level 4: Manual Validation

1. Subir a aplicação: `mvn spring-boot:run`
2. Logar via OAuth2 com diferentes contas que possuem diferentes roles
3. Chamar `GET /api/solicitacoes/` e verificar:
   - ADMIN/OPERADOR veem todas
   - CIDADAO/AGENTE veem apenas as próprias

---

## ACCEPTANCE CRITERIA

- [x] O endpoint `GET /api/solicitacoes/` retorna apenas solicitações do autor para roles CIDADAO e AGENTE
- [x] O endpoint `GET /api/solicitacoes/` retorna todas as solicitações para roles ADMIN e OPERADOR
- [x] O código compila sem erros
- [x] Checkstyle passa sem violações
- [x] Nenhuma nova dependência adicionada
- [x] Padrão de obtenção do usuário autenticado segue o mesmo usado em `UsuarioController`
- [x] Repository existente (`findByAutorIdOrderByDataDesc`) é reutilizado, sem criar novos queries

---

## COMPLETION CHECKLIST

- [ ] Todos os tasks completados em ordem
- [ ] Checkstyle passou sem violações
- [ ] Build compilou sem erros
- [ ] Testes existentes continuam passando
- [ ] Teste manual confirma filtragem correta por role

---

## NOTES

**Decisão de Design:** A filtragem é feita no nível do service (não no repository nem no controller) para manter a separação de responsabilidades. O controller apenas extrai o usuário autenticado e delega ao service.

**Alternativa considerada e descartada:** Usar `@PreAuthorize` com SpEL expressions. Descartado porque o projeto não usa `@EnableMethodSecurity` e a lógica é simples o suficiente para ficar no service layer sem necessidade de annotations de segurança em nível de método.

**Nota sobre o endpoint `GET /api/solicitacoes/{id}`:** O endpoint de busca por ID individual **não** será alterado nesta feature. Um CIDADAO ainda pode acessar uma solicitação específica por ID se tiver a URL. Se for necessário restringir isso também, deve ser tratado em uma feature separada.

**Confidence Score:** 9/10 - A implementação é simples, envolve apenas 2 arquivos, reutiliza um query method existente, e segue um padrão já estabelecido no codebase.

<!-- EOF -->

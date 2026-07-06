# Arquitetura

O SIGESI e composto por backend Spring Boot, frontend React/Vite e uma infraestrutura Docker Compose por ambiente. A comunicacao publica passa pelo nginx da VPS, que termina HTTPS e roteia para a stack correta.

## Visao geral

```text
Navegador
  |
  v
nginx publico da VPS (:80/:443)
  |
  +--> frontend container (:80)
  |
  +--> backend Spring Boot (:8080)
          |
          +--> PostgreSQL
          +--> MinIO
          +--> RabbitMQ
```

Ambientes:

```text
develop -> sigesi-test.ezioalves.cloud -> stack Compose sigesi-test
main    -> sigesi.ezioalves.cloud      -> stack Compose sigesi
```

## Backend

O backend usa Spring Boot 3, Java 21, Spring Security OAuth2, Spring Data JPA, MapStruct, Hibernate Envers, MinIO e RabbitMQ.

O codigo segue `package-by-feature`: cada dominio agrupa controller, service, repository, entity, mapper e DTOs.

```text
src/main/java/com/sigesi/sigesi/
  arquivos/       uploads, metadados e integracao com storage
  auditoria/      consulta de revisoes via Hibernate Envers
  authentication/ adaptacao do usuario OAuth2/OIDC
  cemiterios/     cemiterios
  comentarios/    comentarios em demandas
  config/         seguranca, OAuth2, exceptions, beans e integracoes
  demandas/       demandas de trabalho e materiais
  documentos/     documentos oficiais e PDF
  enderecos/      enderecos compartilhados
  gavetas/        gavetas de jazigos
  jazigos/        jazigos de cemiterios
  materiais/      catalogo de materiais
  notifications/  publicacao de eventos RabbitMQ
  pessoas/        cadastro de pessoas
  solicitacoes/   solicitacoes de cidadaos
  storage/        servico MinIO
  usuarios/       usuarios e roles
```

Padrao atual de um modulo:

- `Entity`: entidade JPA e relacionamentos.
- `Controller`: endpoints REST em `/api/...`.
- `Service`: regra de negocio, buscas, validacoes e persistencia.
- `Repository`: Spring Data JPA.
- `Mapper`: MapStruct entre entity e DTO.
- `dtos/`: contratos de entrada e saida.

## Frontend

O frontend usa React 18, Vite, TypeScript, React Router, TanStack Query, shadcn/ui, Radix UI e Tailwind CSS.

Organizacao principal:

```text
src/
  pages/       telas roteadas
  layouts/     layouts de areas autenticadas e portal
  services/    chamadas HTTP por dominio
  types/       tipos compartilhados do dominio
  hooks/       hooks de usuario, demandas, responsividade e toast
  lib/api.ts   cliente HTTP comum
  components/  componentes de aplicacao e shadcn/ui
```

Padrao atual:

- Rotas e roles ficam concentradas em `src/App.tsx`.
- Protecao de telas passa por `ProtectedRoute`.
- Chamadas REST devem passar por `src/services/*` e usar `apiFetch`.
- Componentes `src/components/ui/*` seguem o padrao shadcn/ui e devem ser alterados com cuidado.

## Autenticacao e autorizacao

O login usa Google OAuth2/OIDC no backend. O backend cria ou atualiza o usuario autenticado e mantem sessao via cookie.

Fluxo resumido:

1. Frontend envia o usuario para `/oauth2/authorization/google`.
2. Backend processa callback em `/login/oauth2/code/google`.
3. `CustomOidcUserService` e `OAuth2LoginSuccessHandler` sincronizam usuario e redirecionam.
4. Frontend chama `/api/usuarios/me` para obter perfil e liberar rotas.
5. Backend aplica permissoes no `SecurityFilterChain`.

Roles atuais:

```text
CIDADAO
AGENTE
OPERADOR
ADMIN
```

## Dados, arquivos e eventos

- PostgreSQL guarda as entidades relacionais.
- MinIO guarda arquivos enviados; a tabela `Arquivo` guarda metadados e `storageKey`.
- RabbitMQ e usado para publicar eventos de demandas.
- Hibernate Envers registra historico de entidades auditaveis.
- Migracoes SQL ficam em `src/main/resources/db/migration`.

## Padroes que devem ser preservados

- Criar novos dominios como pacote proprio, nao como camadas globais.
- Manter contratos REST em DTOs, evitando expor entidades como entrada.
- Usar MapStruct para conversao entity/DTO.
- Centralizar integracoes externas em services/configs especificos.
- No frontend, manter chamadas HTTP fora das telas quando houver service do dominio.

## Pontos de atencao conhecidos

- Backend ainda usa bastante injecao por campo com `@Autowired`; o backlog recomenda migrar para construtores.
- `SpringConfig` centraliza autorizacao e tem regra duplicada de `/api/enderecos/**`.
- Alguns DTOs/tipos do frontend podem divergir do backend; exemplo: anexos de solicitacao usam `anexoIds` no backend.
- `apiFetch` assume JSON por padrao e precisa de evolucao para blob/downloads.

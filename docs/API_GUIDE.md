# Guia de API

A API do SIGESI e servida pelo backend Spring Boot em `/api`. A documentacao interativa dos endpoints fica no Swagger UI:

```text
/swagger-ui.html
```

Use Swagger como fonte de verdade para parametros, payloads e respostas. Este guia documenta convencoes e fluxos para evitar divergencia entre backend, frontend e docs.

## Autenticacao

A autenticacao usa Google OAuth2/OIDC com sessao por cookie.

Rotas relevantes:

```text
GET  /oauth2/authorization/google
GET  /login/oauth2/code/google
POST /api/auth/logout
GET  /api/usuarios/me
```

O frontend deve enviar cookies em chamadas autenticadas. O helper `apiFetch` ja usa `credentials: "include"`.

## Convencoes REST

Padrao atual de endpoints:

```text
GET    /api/<recurso>/
GET    /api/<recurso>/{id}
POST   /api/<recurso>/
PATCH  /api/<recurso>/{id}
DELETE /api/<recurso>/{id}
```

Alguns recursos tem endpoints especificos de consulta ou acao. Consulte Swagger antes de criar chamadas no frontend.

## DTOs

O backend deve tratar DTOs como contrato publico:

- `CreateDTO`: campos obrigatorios para criacao.
- `UpdateDTO`: campos alteraveis.
- `ResponseDTO`: formato retornado ao cliente.

O frontend deve espelhar esses contratos nos services ou em `src/types/index.ts`. Quando houver diferenca entre entity e DTO, o DTO do backend vence.

Exemplo importante:

- `SolicitacaoCreateDTO` do backend usa `anexoIds: List<Long>`.
- Tipos ou forms do frontend devem enviar `anexoIds`, nao um unico `anexoId`, quando anexos forem usados.

## Erros

O backend centraliza erros em handlers em `config/`, incluindo `NotFoundException` e `ConflictException`.

O frontend deve considerar que uma resposta de erro pode conter:

```json
{
  "message": "descricao do erro"
}
```

ou outro corpo retornado pelo Spring. `apiFetch` tenta extrair `message` ou `error`.

## Arquivos

Arquivos enviados sao armazenados no MinIO. A API guarda metadados na entidade `Arquivo`.

Cuidados:

- Upload deve usar `multipart/form-data`.
- Nao defina manualmente `Content-Type` quando o body for `FormData`.
- Downloads ou URLs de arquivo nao devem passar por parser JSON quando a resposta for binaria ou texto.

O backlog recomenda evoluir `apiFetch` para suportar respostas JSON, texto e blob de forma explicita.

## Autorizacao por role

Roles:

```text
CIDADAO
AGENTE
OPERADOR
ADMIN
```

A autorizacao real fica no backend, em `SecurityFilterChain`. O frontend tambem esconde telas com `ProtectedRoute`, mas isso e apenas controle de UX; nao substitui regra no backend.

Ao adicionar endpoint:

1. Defina quais roles podem acessar.
2. Atualize a configuracao de seguranca.
3. Atualize rota/tela frontend se houver.
4. Adicione teste que prove acesso permitido e/ou negado quando a mudanca for sensivel.

## Fluxos principais

Consulte:

- [Fluxo de solicitacao](FLUXO_SOLICITACAO.md)
- [DER](DER.md)

Esses documentos ajudam a entender transicoes de status, relacoes entre entidades e impacto de mudancas.

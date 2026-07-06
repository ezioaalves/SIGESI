# Backlog de refatoracao

Este backlog registra melhorias estruturais identificadas na revisao do codigo. Ele nao deve ser aplicado como uma refatoracao unica grande. Prefira resolver por pequenos PRs, sempre com testes.

## Prioridade alta

### Corrigir lint do frontend

Estado atual: `npm run lint` falha com erros de `any`, blocos vazios, interfaces vazias em componentes shadcn e `require()` em `tailwind.config.ts`.

Resultado esperado:

- `npm run lint` passa no frontend.
- Tipos substituem `any` nos pontos alterados.
- Componentes shadcn sao ajustados sem mudar API visual.
- `tailwind.config.ts` usa import compativel com ESLint atual.

### Atualizar README do frontend e docs de deploy

Estado atual encontrado: README do frontend citava fluxo antigo com Caddy, tag `latest` e deploy apenas de producao.

Resultado esperado:

- README descreve `main -> producao` e `develop -> teste`.
- Tags documentadas usam `main-latest`, `develop-latest` e SHA.
- Backend repo e indicado como hub principal de documentacao.

### Alinhar DTOs frontend/backend

Risco: tipos frontend podem divergir dos DTOs reais do backend.

Exemplo conhecido:

- Backend `SolicitacaoCreateDTO` usa `anexoIds`.
- Frontend tinha tipo com `anexoId`.

Resultado esperado:

- Services frontend espelham DTOs reais.
- Pages nao montam payloads com campos inexistentes.
- Fluxos com anexos sao testados manualmente ou por e2e.

## Prioridade media

### Evoluir `apiFetch`

Estado atual: helper assume JSON por padrao e usa casts `any` para erro.

Resultado esperado:

- Suporte explicito para `json`, `text`, `blob` e respostas vazias.
- Erro tipado com `status` e `responseBody`.
- Upload com `FormData` continua sem `Content-Type` manual.
- Downloads deixam de usar `fetch` direto em paginas quando nao for necessario.

### Extrair tabela de rotas do frontend

Estado atual: `src/App.tsx` concentra rotas e roles manualmente.

Resultado esperado:

- Rotas protegidas ficam em uma tabela tipada.
- Roles sao reutilizadas por sidebar, rotas e menus.
- `ProtectedRoute` continua aplicando autorizacao de UX.
- Backend permanece fonte real de autorizacao.

### Constructor injection no backend

Estado atual: services/controllers/configs usam bastante `@Autowired` em campos.

Resultado esperado:

- Novos componentes usam constructor injection.
- Componentes existentes sao migrados gradualmente.
- Dependencias obrigatorias ficam `final`.
- Testes existentes continuam passando.

### Centralizar politica de seguranca

Estado atual: `SpringConfig` contem matchers diretos e uma regra duplicada para `/api/enderecos/**`.

Resultado esperado:

- Remover duplicidade.
- Agrupar rotas por role de forma legivel.
- Adicionar teste de autorizacao para endpoints sensiveis.

## Prioridade baixa

### Resolver warnings de compilacao backend

Warnings conhecidos:

- MapStruct: propriedade `status` nao mapeada em `SolicitacaoMapper`.
- `GenericAuditService` usa operacoes unchecked.

Resultado esperado:

- Warnings explicados ou removidos.
- Quando uma propriedade for ignorada intencionalmente no mapper, usar anotacao explicita.

### Reduzir comentarios de incerteza

Estado atual: alguns arquivos frontend possuem comentarios narrando duvidas temporarias.

Resultado esperado:

- Comentarios documentam decisoes permanentes.
- Duvidas resolvidas viram codigo claro, teste ou item de backlog.

### Revisar imagens Docker base

Estado atual: alguns servicos usam imagens com tag `latest`, como MinIO.

Resultado esperado:

- Definir se imagens de infraestrutura devem ser fixadas por versao.
- Documentar politica de atualizacao.

## Como trabalhar neste backlog

1. Abra um PR pequeno por tema.
2. Registre no PR quais checks passaram.
3. Atualize esta lista quando um item for resolvido.
4. Nao misture refatoracao com mudanca funcional sem necessidade.

# Desenvolvimento

Este guia explica como rodar, testar e alterar o SIGESI mantendo os padroes atuais.

## Pre-requisitos

Backend:

- Java 21
- Maven Wrapper do repositorio (`./mvnw`)
- Docker e Docker Compose para dependencias locais

Frontend:

- Node.js 20
- npm

## Backend local

```bash
cd SIGESI
./mvnw test
./mvnw spring-boot:run
```

Com Docker Compose:

```bash
docker compose up -d
docker compose ps
```

Checks uteis:

```bash
./mvnw test
./mvnw checkstyle:check
./mvnw -DskipTests -Dcheckstyle.skip compile
```

## Frontend local

No repositorio `sigesi-frontend`:

```bash
npm ci
npm run dev
npm run build
npm run lint
```

Estado conhecido:

- `npm run build` passa.
- `npm run lint` possui erros conhecidos; consulte `REFACTOR_BACKLOG.md`.

## Variaveis de ambiente

Backend usa variaveis para banco, OAuth2, MinIO, RabbitMQ e usuario administrador. Os nomes esperados estao documentados em `DEPLOYMENT.md`.

Frontend usa:

```text
VITE_API_BASE
```

Quando `VITE_API_BASE` nao e definido, o frontend usa mesma origem. Isso permite que o nginx do container e o gateway da VPS encaminhem chamadas sem hardcode de host.

## Como adicionar uma funcionalidade no backend

1. Escolha ou crie o pacote de dominio em `src/main/java/com/sigesi/sigesi/<dominio>`.
2. Crie/atualize entidade JPA e repository.
3. Crie DTOs de entrada e saida em `dtos/`.
4. Crie mapper MapStruct para conversao entity/DTO.
5. Implemente regra de negocio no service.
6. Exponha endpoints no controller com base `/api/<dominio>`.
7. Ajuste autorizacao em `SpringConfig`.
8. Adicione ou atualize testes de service, controller e entity conforme risco.
9. Atualize docs quando mudar fluxo, API, roles ou deploy.

Regras praticas:

- Service nao deve depender de detalhes de HTTP.
- Controller deve validar entrada e delegar regra ao service.
- DTO de update deve representar apenas campos alteraveis.
- Nao versionar secrets nem dados reais.

## Como adicionar uma funcionalidade no frontend

1. Adicione tipos em `src/types/index.ts` quando forem compartilhados.
2. Adicione funcoes HTTP em `src/services/<dominio>.ts`.
3. Use `apiFetch` para manter cookies OAuth2 e tratamento comum de erro.
4. Crie ou altere pagina em `src/pages`.
5. Se precisar de rota protegida, atualize `src/App.tsx` e `ProtectedRoute`.
6. Reutilize componentes `src/components/ui` antes de criar novo padrao visual.
7. Rode `npm run build` e, quando possivel, reduza itens do backlog de lint.

Regras praticas:

- Evite chamadas diretas a `fetch` em paginas; prefira services.
- Evite `any`; crie tipos pequenos para a resposta usada.
- Nao duplique regras de role em varios arquivos sem necessidade.
- Comentarios devem explicar decisoes, nao incertezas temporarias.

## Branches e fluxo de trabalho

```text
develop -> ambiente de teste
main    -> producao
```

Regra para correcoes solicitadas:

- Depois de corrigir algo, rode os checks relevantes, faca commit/push da branch `develop` do repositorio afetado e deixe o GitHub Actions publicar em `https://sigesi-test.ezioalves.cloud`.
- Use deploy direto na VPS apenas quando isso for pedido explicitamente.

Fluxo recomendado:

1. Trabalhe em branch de feature.
2. Abra PR para `develop`.
3. Valide em `sigesi-test.ezioalves.cloud`.
4. Promova para `main` somente depois de validar.
5. Verifique producao apos o deploy.

## Testes esperados por tipo de mudanca

| Mudanca | Checks minimos |
|---------|----------------|
| Service backend | Teste unitario do service e `./mvnw test`. |
| Controller/API | Teste de controller, validacao de Swagger e `./mvnw test`. |
| Entity/relacionamento | Teste de entity/repository quando aplicavel e revisao do DER. |
| Frontend page/service | `npm run build`, teste manual do fluxo e, quando possivel, e2e existente. |
| Deploy/infra | Validar workflow YAML, compose renderizado e resposta HTTP do ambiente. |
| Documentacao | Links locais revisados e comandos conferidos contra o estado atual. |

## Decisoes estruturais atuais

- Manter `package-by-feature` no backend.
- Manter frontend separado em pages, services, hooks, layouts e components.
- Swagger UI e fonte de verdade dos endpoints.
- Markdown documenta convencoes, fluxos e operacao.

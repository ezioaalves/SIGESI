# SIGESI

Sistema web para gerenciamento da Secretaria de Infraestrutura, com solicitacoes de cidadaos, demandas de trabalho, materiais, documentos, arquivos e administracao de cemiterios municipais.

Este repositorio contem o backend Spring Boot e os arquivos de infraestrutura compartilhados do deploy. O frontend fica no repositorio `ezioaalves/sigesi-frontend`.

## Documentacao

A documentacao principal do projeto fica em [`docs/README.md`](docs/README.md).

Use esse indice para:

- entender a arquitetura e os padroes do codigo;
- preparar ambiente local;
- saber como alterar ou criar funcionalidades;
- consultar convencoes de API;
- operar deploy, VPS e pipelines;
- acompanhar o backlog de refatoracao recomendado.

## Inicio rapido

### Backend local

```bash
./mvnw test
./mvnw spring-boot:run
```

### Stack local com Docker

```bash
docker compose up -d
```

### Frontend local

No repositorio `sigesi-frontend`:

```bash
npm ci
npm run dev
```

## Ambientes

```text
develop -> https://sigesi-test.ezioalves.cloud
main    -> https://sigesi.ezioalves.cloud
```

O deploy usa GitHub Actions, Docker Hub, Docker Compose e o nginx existente na VPS como gateway publico HTTPS.

Para correcoes solicitadas, valide localmente, envie a branch `develop` do repositorio afetado e teste o resultado em `https://sigesi-test.ezioalves.cloud`. Esse e o fluxo padrao; deploy direto na VPS fica reservado para pedidos explicitos.

## Comandos principais

```bash
# Backend
./mvnw test
./mvnw -DskipTests -Dcheckstyle.skip compile
./mvnw checkstyle:check

# Docker
docker compose up -d
docker compose ps
```

## Estado de qualidade conhecido

- Backend: suite de testes atual passa com `396` testes.
- Backend: compilacao passa, com avisos conhecidos de MapStruct e uso unchecked em auditoria.
- Frontend: build passa, mas `npm run lint` tem erros conhecidos documentados no backlog.

Consulte [`docs/REFACTOR_BACKLOG.md`](docs/REFACTOR_BACKLOG.md) antes de iniciar mudancas estruturais.

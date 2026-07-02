# Deploy do SIGESI

Este projeto usa dois repositorios:

- `ezioaalves/SIGESI`: backend Spring Boot e arquivos de infraestrutura da VPS.
- `ezioaalves/sigesi-frontend`: frontend React/Vite.

## Ambientes

O deploy e separado por branch:

```text
develop -> https://sigesi-test.ezioalves.cloud
main    -> https://sigesi.ezioalves.cloud
```

O proxy publico da VPS e o nginx existente em `/root/SIGESI/nginx.conf`. Ele fica responsavel por HTTPS e roteia cada dominio para a stack Docker correta.

## Diretorios na VPS

```text
/root/pipeline/sigesi       producao, branch main, projeto Compose sigesi
/root/pipeline/sigesi-test  teste, branch develop, projeto Compose sigesi-test
```

O deploy do backend copia `compose-prod.yaml` e `compose-test.yaml`, recria o `.env` do ambiente da branch e atualiza backend/servicos persistentes.

O deploy do frontend assume que o compose do ambiente ja existe e atualiza apenas o servico de frontend desse ambiente.

## Secrets do GitHub

Configure estes secrets nos dois repositorios:

- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`
- `VPS_HOST`
- `VPS_USER`
- `VPS_SSH_KEY`

Configure estes secrets no repositorio do backend:

- `DATABASE_NAME`
- `DATABASE_USER`
- `DATABASE_PASSWORD`
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `MINIO_ACCESS_KEY`
- `MINIO_SECRET_KEY`
- `ADMIN_EMAIL`
- `RABBITMQ_USERNAME`
- `RABBITMQ_PASSWORD`

Nao coloque secrets em arquivos versionados.

## Imagens Docker

As imagens publicadas sao:

```text
${DOCKERHUB_USERNAME}/sigesi-backend:main-latest
${DOCKERHUB_USERNAME}/sigesi-backend:develop-latest
${DOCKERHUB_USERNAME}/sigesi-backend:<commit-sha>
${DOCKERHUB_USERNAME}/sigesi-frontend:main-latest
${DOCKERHUB_USERNAME}/sigesi-frontend:develop-latest
${DOCKERHUB_USERNAME}/sigesi-frontend:<commit-sha>
```

`IMAGE_TAG` no `.env` da VPS escolhe qual tag o Compose usa.

## Servicos persistentes

Producao usa volumes do projeto Compose `sigesi`.

Teste usa volumes separados do projeto Compose `sigesi-test`.

Nao remova volumes durante deploys normais.

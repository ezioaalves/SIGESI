# Deploy do SIGESI

Este projeto usa dois repositorios:

- `ezioaalves/SIGESI`: backend Spring Boot e arquivos de infraestrutura da VPS.
- `ezioaalves/sigesi-frontend`: frontend React/Vite.

O deploy de producao roda apenas em pushes para `main`.

Para a checklist operacional completa de secrets, DNS, OAuth, VPS e primeira publicacao, veja [`NEXT_STEPS.md`](NEXT_STEPS.md).

## VPS

A VPS deve ter Docker e Docker Compose instalados. O pipeline usa o diretorio:

```bash
~/pipeline/sigesi
```

O deploy do backend copia `compose-prod.yaml` e `Caddyfile` para esse diretorio, recria o arquivo `.env` com GitHub Secrets e sobe a stack completa.

O deploy do frontend assume que essa stack ja existe e atualiza apenas o servico `sigesi-frontend`.

## Dominio e HTTPS

O proxy publico e o Caddy. Em producao, ele usa:

```text
APP_DOMAIN=sigesi.ezioalves.cloud
APP_PUBLIC_URL=https://sigesi.ezioalves.cloud
```

Caddy emite e renova certificados TLS automaticamente. As portas `80` e `443` precisam estar liberadas no firewall da VPS e apontadas no DNS para o IP da VPS.

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

## Imagens Docker

As imagens publicadas sao:

```text
${DOCKERHUB_USERNAME}/sigesi-backend:latest
${DOCKERHUB_USERNAME}/sigesi-backend:<commit-sha>
${DOCKERHUB_USERNAME}/sigesi-frontend:latest
${DOCKERHUB_USERNAME}/sigesi-frontend:<commit-sha>
```

`compose-prod.yaml` usa `IMAGE_TAG=latest` por padrao. Para rollback manual, altere `IMAGE_TAG` no `.env` da VPS para o SHA desejado e rode:

```bash
docker compose -f compose-prod.yaml pull
docker compose -f compose-prod.yaml up -d
```

## Servicos persistentes

Os dados ficam em volumes Docker nomeados:

- `sigesi_data`: PostgreSQL principal.
- `minio_data`: arquivos do MinIO.
- `rabbitmq_data`: estado do RabbitMQ.
- `caddy_data` e `caddy_config`: certificados e configuracao interna do Caddy.

Nao remova esses volumes durante deploys normais.

## Notificacoes

O antigo `notification-service` e seu banco foram removidos da stack de producao inicial. O RabbitMQ permanece porque o backend publica eventos de demandas nele. Se o microservico de notificacoes voltar, ele deve entrar com pipeline, imagem e secrets proprios.

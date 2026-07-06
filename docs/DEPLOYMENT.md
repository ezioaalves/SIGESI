# Deploy e operacao

Este documento descreve como o SIGESI e publicado e operado na VPS.

## Repositorios

| Repositorio | Responsabilidade |
|-------------|------------------|
| `ezioaalves/SIGESI` | Backend, Compose dos ambientes, documentacao principal e deploy de servicos persistentes. |
| `ezioaalves/sigesi-frontend` | Frontend, Dockerfile do frontend, nginx interno do container e deploy isolado do frontend. |

## Ambientes

```text
develop -> https://sigesi-test.ezioalves.cloud
main    -> https://sigesi.ezioalves.cloud
```

Cada branch publica imagens Docker com tags proprias:

```text
${DOCKERHUB_USERNAME}/sigesi-backend:main-latest
${DOCKERHUB_USERNAME}/sigesi-backend:develop-latest
${DOCKERHUB_USERNAME}/sigesi-backend:<commit-sha>
${DOCKERHUB_USERNAME}/sigesi-frontend:main-latest
${DOCKERHUB_USERNAME}/sigesi-frontend:develop-latest
${DOCKERHUB_USERNAME}/sigesi-frontend:<commit-sha>
```

## VPS

Diretorios:

```text
/root/pipeline/sigesi       producao, branch main, projeto Compose sigesi
/root/pipeline/sigesi-test  teste, branch develop, projeto Compose sigesi-test
/root/SIGESI/nginx.conf     nginx publico HTTPS
```

Projetos Compose:

```text
sigesi       producao
sigesi-test  teste
```

O nginx existente na VPS e o gateway publico. Ele escuta `80` e `443`, termina HTTPS e encaminha cada dominio para o frontend/backend da stack correta.

## Secrets do GitHub

Configure nos dois repositorios:

```text
DOCKERHUB_USERNAME
DOCKERHUB_TOKEN
VPS_HOST
VPS_USER
VPS_SSH_KEY
```

Configure somente no backend:

```text
DATABASE_NAME
DATABASE_USER
DATABASE_PASSWORD
GOOGLE_CLIENT_ID
GOOGLE_CLIENT_SECRET
MINIO_ACCESS_KEY
MINIO_SECRET_KEY
ADMIN_EMAIL
RABBITMQ_USERNAME
RABBITMQ_PASSWORD
```

Nunca versionar valores reais de secrets.

## Google OAuth

Authorized JavaScript origins:

```text
https://sigesi.ezioalves.cloud
https://sigesi-test.ezioalves.cloud
```

Authorized redirect URIs:

```text
https://sigesi.ezioalves.cloud/login/oauth2/code/google
https://sigesi-test.ezioalves.cloud/login/oauth2/code/google
```

## Workflows

Backend:

- `Backend CI`: valida backend em `main` e `develop`.
- `Backend CD`: builda/pusha imagem backend, copia Compose para a VPS, recria `.env`, atualiza backend, Postgres, MinIO e RabbitMQ do ambiente.

Frontend:

- `Frontend CI`: instala dependencias, roda lint e build.
- `Frontend CD`: builda/pusha imagem frontend e atualiza somente o servico frontend do ambiente.

Observacao: o deploy do frontend depende do `compose.yaml` criado pelo deploy do backend. Se o frontend rodar antes do backend em um ambiente novo, ele espera alguns minutos e falha se o arquivo nao existir.

## Nginx

O nginx publico roda como container `sigesi-nginx-1`. Os workflows conectam esse container a rede Compose do ambiente e recarregam o nginx apos recriar containers.

Esse reload e necessario porque o nginx pode manter em memoria o IP antigo de um container recriado e responder `502` ate ser recarregado.

Comandos uteis:

```bash
docker exec sigesi-nginx-1 nginx -t
docker exec sigesi-nginx-1 nginx -s reload
docker logs --tail 100 sigesi-nginx-1
```

## Verificacao depois do deploy

Producao:

```bash
cd /root/pipeline/sigesi
docker compose -p sigesi -f compose.yaml ps
```

Teste:

```bash
cd /root/pipeline/sigesi-test
docker compose -p sigesi-test -f compose.yaml ps
```

HTTP:

```bash
curl -I https://sigesi.ezioalves.cloud
curl -I https://sigesi-test.ezioalves.cloud
```

Logs:

```bash
docker compose -p sigesi -f /root/pipeline/sigesi/compose.yaml logs -f sigesi-backend
docker compose -p sigesi-test -f /root/pipeline/sigesi-test/compose.yaml logs -f backend
docker logs -f sigesi-nginx-1
```

## Rollback

Rollback de imagem:

1. Escolha uma tag SHA publicada no Docker Hub.
2. Edite temporariamente `IMAGE_TAG` no `.env` do ambiente.
3. Rode `docker compose pull <servico>`.
4. Rode `docker compose up -d <servico>`.
5. Recarregue nginx.
6. Valide HTTP e login.

Exemplo backend producao:

```bash
cd /root/pipeline/sigesi
sed -i 's/^IMAGE_TAG=.*/IMAGE_TAG=<commit-sha>/' .env
docker compose -p sigesi -f compose.yaml pull sigesi-backend
docker compose -p sigesi -f compose.yaml up -d sigesi-backend
docker exec sigesi-nginx-1 nginx -s reload
```

Para voltar ao fluxo normal, restaure `IMAGE_TAG=main-latest` ou rode novamente o workflow de `main`.

## Cuidados operacionais

- Nao remova volumes em deploy normal.
- Nao remova `sigesi-nginx-1`; ele e o gateway publico.
- Teste primeiro em `develop`.
- Mantenha secrets somente no GitHub Actions ou na VPS.
- Antes de alterar Compose, valide em teste e confira nomes de servico usados pelo nginx.

# Proximos passos do deploy SIGESI

Estado atual esperado:

- `sigesi.ezioalves.cloud` aponta para a VPS de producao.
- `sigesi-test.ezioalves.cloud` aponta para a mesma VPS e ja tem HTTPS no nginx.
- Firewall da Hostinger libera `22`, `80` e `443`.
- SSH funciona com o usuario configurado em `VPS_USER`.
- Google OAuth contem os redirects de producao e teste.

## Branches

```text
develop -> deploy automatico de teste
main    -> deploy automatico de producao
```

URLs:

```text
Teste:    https://sigesi-test.ezioalves.cloud
Producao: https://sigesi.ezioalves.cloud
```

## Secrets necessarios

Nos dois repositorios:

```text
DOCKERHUB_USERNAME
DOCKERHUB_TOKEN
VPS_HOST
VPS_USER
VPS_SSH_KEY
```

Somente no backend:

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

Nunca salve valores reais de secrets em arquivos versionados.

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

## Fluxo de uso

1. Faça merge/push em `develop`.
2. Aguarde os workflows `Backend CD` e `Frontend CD`.
3. Teste em `https://sigesi-test.ezioalves.cloud`.
4. Depois que validar, faça merge de `develop` para `main`.
5. Aguarde os workflows em `main`.
6. Teste em `https://sigesi.ezioalves.cloud`.

## Verificacao na VPS

Teste:

```bash
cd /root/pipeline/sigesi-test
docker compose -p sigesi-test -f compose.yaml ps
```

Producao:

```bash
cd /root/pipeline/sigesi
docker compose -p sigesi -f compose.yaml ps
```

Logs:

```bash
docker logs --tail 100 sigesi-nginx-1
docker compose -p sigesi-test -f /root/pipeline/sigesi-test/compose.yaml logs -f backend
docker compose -p sigesi -f /root/pipeline/sigesi/compose.yaml logs -f sigesi-backend
```

## Observacoes

- O nginx da VPS continua sendo o gateway publico e nao deve ser removido pelo pipeline.
- O backend deve rodar ao menos uma vez no ambiente antes do frontend, porque ele cria o `compose.yaml` e `.env`.
- Se o frontend workflow rodar antes do backend, ele espera o `compose.yaml` por alguns minutos e falha se o backend nao criar a stack.

# Proximos passos para ativar o deploy automatico do SIGESI

Este documento assume a organizacao atual em dois repositorios:

- Backend e infraestrutura: `ezioaalves/SIGESI`
- Frontend: `ezioaalves/sigesi-frontend`

O deploy automatico de producao roda apenas quando houver push na branch `main`.

## 1. Conferir dominio e DNS

1. Abra o painel da Hostinger:
   - https://hpanel.hostinger.com/
2. Confirme o IP publico da VPS.
   - Normalmente fica em **VPS** > selecione o servidor > detalhes do servidor.
3. Abra a zona DNS do dominio `ezioalves.cloud`.
4. Crie ou confira este registro:

```text
Tipo: A
Nome/Host: sigesi
Valor/Aponta para: IP publico da VPS
TTL: padrao
```

5. Aguarde a propagacao e teste localmente:

```bash
nslookup sigesi.ezioalves.cloud
```

O resultado deve apontar para o IP da VPS.

## 2. Conferir portas da VPS

Caddy precisa receber trafego nas portas `80` e `443`.

1. No painel da Hostinger, abra a VPS.
2. Confira firewall/regras de rede.
3. Garanta entrada liberada para:

```text
TCP 22   SSH
TCP 80   HTTP, necessario para emissao inicial do certificado
TCP 443  HTTPS
```

Documentacao util:

- Hostinger VPS: https://support.hostinger.com/en/collections/9445323-vps
- Docker Engine: https://docs.docker.com/engine/install/
- Docker Compose: https://docs.docker.com/compose/

## 3. Conferir Docker na VPS

Acesse a VPS por SSH:

```bash
ssh SEU_USUARIO@IP_DA_VPS
```

Rode:

```bash
docker --version
docker compose version
```

Se o usuario do deploy nao puder usar Docker, adicione-o ao grupo `docker`:

```bash
sudo usermod -aG docker SEU_USUARIO
```

Depois saia e entre novamente no SSH.

Crie o diretorio usado pelo pipeline:

```bash
mkdir -p ~/pipeline/sigesi
```

## 4. Criar token do Docker Hub

O pipeline publica as imagens:

```text
DOCKERHUB_USERNAME/sigesi-backend:latest
DOCKERHUB_USERNAME/sigesi-frontend:latest
```

1. Abra Docker Hub:
   - https://hub.docker.com/
2. Entre na conta que sera dona das imagens.
3. Abra:
   - Account Settings > Personal access tokens
   - Link direto: https://app.docker.com/settings/personal-access-tokens
4. Crie um token com permissao de leitura/escrita.
5. Guarde:

```text
DOCKERHUB_USERNAME = nome do usuario/namespace do Docker Hub
DOCKERHUB_TOKEN    = token criado
```

## 5. Criar chave SSH para o GitHub Actions

Na sua maquina local ou na VPS, crie uma chave exclusiva para deploy:

```bash
ssh-keygen -t ed25519 -C "github-actions-sigesi-deploy" -f sigesi_deploy_key
```

Isso cria:

```text
sigesi_deploy_key      chave privada, vai para o GitHub Secret VPS_SSH_KEY
sigesi_deploy_key.pub  chave publica, vai para a VPS
```

Na VPS, adicione a chave publica ao usuario que fara deploy:

```bash
mkdir -p ~/.ssh
chmod 700 ~/.ssh
echo "CONTEUDO_DA_CHAVE_PUBLICA" >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

Teste a conexao usando a chave privada:

```bash
ssh -i sigesi_deploy_key SEU_USUARIO@IP_DA_VPS
```

## 6. Configurar Google OAuth

1. Abra Google Cloud Console:
   - https://console.cloud.google.com/
2. Selecione o projeto usado pelo SIGESI.
3. Abra:
   - APIs & Services > Credentials
   - Link: https://console.cloud.google.com/apis/credentials
4. Abra o OAuth Client usado pelo backend.
5. Em **Authorized redirect URIs**, adicione:

```text
https://sigesi.ezioalves.cloud/login/oauth2/code/google
```

6. Em **Authorized JavaScript origins**, adicione:

```text
https://sigesi.ezioalves.cloud
```

7. Guarde:

```text
GOOGLE_CLIENT_ID
GOOGLE_CLIENT_SECRET
```

Documentacao util:

- Google OAuth web server flow: https://developers.google.com/identity/protocols/oauth2/web-server
- Google Cloud credentials: https://console.cloud.google.com/apis/credentials

## 7. Separar os valores dos secrets

Use estes valores para os secrets dos repositorios.

### Secrets usados nos dois repositorios

Configure no backend `ezioaalves/SIGESI` e no frontend `ezioaalves/sigesi-frontend`:

```text
DOCKERHUB_USERNAME = usuario/namespace do Docker Hub
DOCKERHUB_TOKEN    = token do Docker Hub
VPS_HOST           = IP publico ou host da VPS
VPS_USER           = usuario SSH da VPS
VPS_SSH_KEY        = conteudo completo da chave privada sigesi_deploy_key
```

### Secrets usados apenas no backend

Configure somente em `ezioaalves/SIGESI`:

```text
DATABASE_NAME        = nome do banco principal, por exemplo sigesi
DATABASE_USER        = usuario do PostgreSQL
DATABASE_PASSWORD    = senha forte do PostgreSQL
GOOGLE_CLIENT_ID     = Client ID do Google OAuth
GOOGLE_CLIENT_SECRET = Client Secret do Google OAuth
MINIO_ACCESS_KEY     = usuario/chave do MinIO
MINIO_SECRET_KEY     = senha/chave secreta do MinIO
ADMIN_EMAIL          = email do primeiro admin
RABBITMQ_USERNAME    = usuario do RabbitMQ
RABBITMQ_PASSWORD    = senha do RabbitMQ
```

Sugestao para gerar senhas:

```bash
openssl rand -base64 32
```

Nao coloque aspas no valor do GitHub Secret, a menos que a aspas faca parte real do segredo.

## 8. Adicionar secrets no GitHub

Para cada repositorio:

1. Abra o repositorio no GitHub.
2. Va em:
   - Settings > Secrets and variables > Actions
3. Clique em **New repository secret**.
4. Crie cada secret com o nome exato listado acima.

Links uteis:

- GitHub Actions secrets: https://docs.github.com/en/actions/security-guides/using-secrets-in-github-actions
- Pagina do backend: https://github.com/ezioaalves/SIGESI/settings/secrets/actions
- Pagina do frontend: https://github.com/ezioaalves/sigesi-frontend/settings/secrets/actions

## 9. Publicar os commits

No backend:

```bash
cd /home/pandora/projects/SIGESI/SIGESI
git status
git push origin HEAD
```

No frontend:

```bash
cd /home/pandora/projects/SIGESI/sigesi-frontend
git status
git push origin HEAD
```

Se a branch atual nao for `main`, abra Pull Requests para `main` e faca merge quando o CI passar.

## 10. Rodar o deploy do backend primeiro

O backend cria a stack base na VPS.

1. Abra:
   - https://github.com/ezioaalves/SIGESI/actions
2. Abra o workflow **Backend CD**.
3. Se ja houve push em `main`, acompanhe a execucao.
4. Se quiser acionar manualmente, clique em **Run workflow** na branch `main`.

O workflow deve:

1. Buildar e publicar a imagem `sigesi-backend`.
2. Copiar `compose-prod.yaml` e `Caddyfile` para `~/pipeline/sigesi`.
3. Criar `~/pipeline/sigesi/.env`.
4. Rodar Docker Compose na VPS.

## 11. Rodar o deploy do frontend

Depois que o backend deploy terminar:

1. Abra:
   - https://github.com/ezioaalves/sigesi-frontend/actions
2. Abra o workflow **Frontend CD**.
3. Acompanhe a execucao ou acione manualmente em `main`.

O workflow deve:

1. Buildar e publicar a imagem `sigesi-frontend`.
2. Rodar `docker compose pull sigesi-frontend`.
3. Recriar apenas o container do frontend.

## 12. Verificar na VPS

Entre por SSH:

```bash
ssh SEU_USUARIO@IP_DA_VPS
cd ~/pipeline/sigesi
docker compose -f compose-prod.yaml ps
```

Todos os servicos principais devem estar `Up`:

```text
sigesi-caddy
sigesi-backend
sigesi-frontend
db
sigesi-minio
sigesi-rabbitmq
```

Logs uteis:

```bash
docker compose -f compose-prod.yaml logs -f caddy
docker compose -f compose-prod.yaml logs -f sigesi-backend
docker compose -f compose-prod.yaml logs -f sigesi-frontend
```

## 13. Verificar no navegador

Abra:

```text
https://sigesi.ezioalves.cloud
https://sigesi.ezioalves.cloud/swagger-ui.html
```

Teste:

1. Login com Google.
2. Redirecionamento para `/portal`.
3. Criacao de uma solicitacao.
4. Listagem de solicitacoes.
5. Upload/download de arquivo, se essa parte estiver em uso.
6. Logout.

## 14. Problemas comuns

### Caddy nao sobe

Verifique se outra coisa esta usando portas `80` ou `443`:

```bash
sudo ss -tulpn | grep -E ':80|:443'
```

Se houver container antigo de Nginx:

```bash
docker ps
docker stop NOME_OU_ID_DO_CONTAINER
```

### Certificado HTTPS nao emite

Confira:

- DNS aponta para a VPS.
- Portas `80` e `443` abertas.
- `APP_DOMAIN=sigesi.ezioalves.cloud` no `.env` da VPS.
- Logs do Caddy.

### Login Google falha

Confira:

- Redirect URI no Google Cloud esta exatamente:

```text
https://sigesi.ezioalves.cloud/login/oauth2/code/google
```

- Secrets `GOOGLE_CLIENT_ID` e `GOOGLE_CLIENT_SECRET` estao corretos.
- Logs do backend.

### Frontend deploy falha antes do backend

Rode o deploy do backend primeiro. O frontend precisa que `~/pipeline/sigesi/compose-prod.yaml` ja exista na VPS.

### Backend nao conecta no banco

Confira os secrets:

```text
DATABASE_NAME
DATABASE_USER
DATABASE_PASSWORD
```

Na VPS, confira `.env` gerado pelo workflow:

```bash
cd ~/pipeline/sigesi
sed -n '1,120p' .env
```

Nao cole o conteudo desse arquivo em chats ou issues publicas.

## 15. Rollback manual

Cada imagem tambem e publicada com a tag do commit SHA.

Na VPS:

```bash
cd ~/pipeline/sigesi
nano .env
```

Altere:

```text
IMAGE_TAG=SHA_DE_UM_COMMIT_ANTERIOR
```

Depois rode:

```bash
docker compose -f compose-prod.yaml pull
docker compose -f compose-prod.yaml up -d
docker compose -f compose-prod.yaml ps
```

Para voltar ao deploy normal:

```text
IMAGE_TAG=latest
```

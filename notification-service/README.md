# SIGESI Notification Service

Microserviço FastAPI para gerenciamento de notificações do sistema SIGESI.

## Características

- Consumo de eventos RabbitMQ do SIGESI
- Envio de notificações por email via SMTP
- API REST para consulta de notificações
- Sistema de preferências de notificação
- PostgreSQL para persistência

## Requisitos

- Python 3.12+
- PostgreSQL 16+
- RabbitMQ 3+
- SMTP Server (Gmail, SendGrid, etc.)

## Instalação

1. Instalar dependências:
```bash
pip install -r requirements.txt
```

2. Configurar variáveis de ambiente:
```bash
cp .env.example .env
# Editar .env com suas credenciais
```

3. Executar migrações:
```bash
alembic upgrade head
```

4. Iniciar o servidor:
```bash
uvicorn app.main:app --reload
```

## Docker

Para executar com Docker Compose:

```bash
docker-compose up -d
```

Isso irá iniciar:
- Notification Service (porta 8001)
- PostgreSQL (porta 5433)
- RabbitMQ (porta 5672, Management UI: 15672)

## Endpoints da API

### Health Check
- `GET /health` - Verifica status do serviço

### Notificações
- `GET /api/notifications/user/{user_id}` - Lista notificações do usuário
- `GET /api/notifications/{notification_id}` - Busca notificação por ID
- `POST /api/notifications/mark-read` - Marca notificações como lidas
- `GET /api/notifications/user/{user_id}/unread-count` - Conta notificações não lidas

### Preferências
- `GET /api/preferences/user/{user_id}` - Lista preferências do usuário
- `PUT /api/preferences/user/{user_id}` - Cria/atualiza preferência
- `DELETE /api/preferences/{preference_id}` - Remove preferência

## Testes

Executar testes unitários:

```bash
pytest tests/ -v
```

## Integração com SIGESI

O serviço consome eventos do exchange `sigesi_events` com as routing keys:
- `demand.assigned` - Nova demanda atribuída
- `demand.status_changed` - Status da demanda alterado

## Estrutura do Projeto

```
notification-service/
├── app/
│   ├── main.py                 # Aplicação FastAPI
│   ├── config.py               # Configurações
│   ├── database.py             # SQLAlchemy async
│   ├── models/                 # Modelos de dados
│   ├── schemas/                # Schemas Pydantic
│   ├── routers/                # Endpoints da API
│   ├── services/               # Lógica de negócio
│   ├── consumers/              # Consumidores RabbitMQ
│   └── templates/              # Templates de email
├── alembic/                    # Migrações de banco
├── tests/                      # Testes unitários
└── docker-compose.yml          # Orquestração Docker
```

## Configuração SMTP

Para Gmail, você precisa:
1. Ativar verificação em duas etapas
2. Gerar senha de app
3. Usar a senha de app em `NOTIFICATION_SMTP_PASSWORD`

Para outros provedores SMTP, ajuste `NOTIFICATION_SMTP_HOST` e `NOTIFICATION_SMTP_PORT`.

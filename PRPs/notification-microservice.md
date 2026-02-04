name: "Notification Microservice - FastAPI with RabbitMQ Integration"
description: |

---

## Goal

**Feature Goal**: Criar um microserviço de notificações independente usando FastAPI que recebe eventos do SIGESI via RabbitMQ e envia notificações por email para agentes quando receberem novas demandas e para operadores quando demandas mudarem de status.

**Deliverable**:
- Microserviço FastAPI completo com banco de dados PostgreSQL próprio
- Integração com RabbitMQ para consumo de eventos assíncrono
- Sistema de envio de emails via SMTP
- API REST para consulta e gerenciamento de notificações
- Sistema de preferências de notificação por canal e tipo

**Success Definition**:
- O microserviço consome eventos do RabbitMQ e envia emails automaticamente
- Notificações são registradas no banco de dados com status de envio
- Usuários podem consultar suas notificações via API
- Usuários podem configurar preferências de notificação por tipo

## User Persona

**Target User**:
- **Agentes**: Usuários responsáveis por executar demandas de trabalho
- **Operadores**: Usuários que gerenciam e supervisionam as demandas

**Use Case**:
- Agentes recebem email quando uma nova demanda é atribuída a eles
- Operadores recebem email quando o status de uma demanda muda (em andamento, concluída, cancelada)

**User Journey**:
1. Operador cria uma demanda no SIGESI e atribui a um agente
2. SIGESI publica evento `demand.assigned` no RabbitMQ
3. Microserviço consome evento e verifica preferências do agente
4. Se email habilitado, microserviço envia notificação por email
5. Notificação é registrada no banco de dados
6. Agente pode consultar histórico de notificações via API

**Pain Points Addressed**:
- Agentes não sabem quando recebem novas demandas sem verificar o sistema manualmente
- Operadores não têm visibilidade do andamento das demandas em tempo real
- Falta de registro histórico de comunicações sobre demandas

## Why

- **Valor de Negócio**: Melhora a comunicação e tempo de resposta da equipe
- **Integração**: Arquitetura desacoplada via message queue permite escalabilidade independente
- **Problema Resolvido**: Elimina a necessidade de verificação manual constante do sistema

## What

Sistema de notificações que:
1. Consome eventos do SIGESI via RabbitMQ
2. Envia notificações por email
3. Registra histórico de notificações
4. Permite configuração de preferências por usuário

### Success Criteria

- [ ] Microserviço inicializa e conecta ao RabbitMQ
- [ ] Eventos `demand.created`, `demand.assigned`, `demand.status_changed` são consumidos
- [ ] Emails são enviados com templates adequados
- [ ] Notificações são persistidas no PostgreSQL
- [ ] API REST funcional para consulta de notificações
- [ ] Preferências de notificação configuráveis por tipo
- [ ] Health check endpoint funcional
- [ ] Testes unitários passando

## All Needed Context

### Context Completeness Check

_Este PRP foi validado: um desenvolvedor sem conhecimento do codebase tem todas as informações necessárias para implementar com sucesso._

### Documentation & References

```yaml
# MUST READ - Documentação essencial para implementação

- docfile: PRPs/ai_docs/fastapi_notification_service.md
  why: Referência completa com patterns de projeto, código de exemplo, e configuração
  section: Todo o documento - contém exemplos de models, schemas, services, consumers
  critical: Seguir a estrutura de projeto e patterns de async/await documentados

- url: https://fastapi.tiangolo.com/tutorial/bigger-applications/
  why: Estrutura de projeto FastAPI com múltiplos arquivos
  critical: Usar APIRouter para separação de rotas

- url: https://fastapi.tiangolo.com/advanced/events/
  why: Lifespan events para inicialização de RabbitMQ consumer
  critical: Usar async context manager para startup/shutdown

- url: https://docs.sqlalchemy.org/en/20/orm/extensions/asyncio.html
  why: SQLAlchemy async para PostgreSQL
  critical: Usar async_sessionmaker e expire_on_commit=False

- url: https://aio-pika.readthedocs.io/en/latest/quick-start.html
  why: Cliente async para RabbitMQ
  critical: Usar connect_robust para reconexão automática

- url: https://docs.pydantic.dev/latest/concepts/pydantic_settings/
  why: Configuração via variáveis de ambiente
  critical: Usar SettingsConfigDict com env_prefix

- file: src/main/java/com/sigesi/sigesi/demandas/Demanda.java
  why: Entender estrutura da entidade Demanda para mapear eventos
  pattern: Campos id, solicitacao, responsavel, status, prazo
  gotcha: DemandaStatus é enum (PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA)

- file: src/main/java/com/sigesi/sigesi/demandas/DemandaService.java
  why: Entender operações que geram eventos de notificação
  pattern: createDemanda, updateDemanda são pontos de publicação de eventos
  gotcha: updateDemanda pode mudar status E responsável

- file: src/main/java/com/sigesi/sigesi/usuarios/Usuario.java
  why: Entender estrutura do usuário para notificações
  pattern: id, email, name, role (CIDADAO, OPERADOR, AGENTE, ADMIN)
  gotcha: Apenas AGENTE e OPERADOR recebem notificações de demandas
```

### Current SIGESI Codebase Tree (relevant parts)

```bash
src/main/java/com/sigesi/sigesi/
├── SigesiApplication.java          # Entry point - adicionar RabbitTemplate
├── config/
│   └── SpringConfig.java           # Security - não modificar
├── demandas/
│   ├── Demanda.java                # Entity - referência para eventos
│   ├── DemandaService.java         # MODIFICAR: adicionar publicação de eventos
│   ├── DemandaStatus.java          # Enum: PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA
│   └── dtos/
│       └── DemandaResponseDTO.java # Estrutura para eventos
├── usuarios/
│   ├── Usuario.java                # Entity com email, name, role
│   └── enums/Role.java             # CIDADAO, OPERADOR, AGENTE, ADMIN
└── solicitacoes/
    └── Solicitacao.java            # Parent entity de Demanda
```

### Desired Microservice Codebase Tree

```bash
notification-service/               # CRIAR: novo diretório na raiz do projeto
├── app/
│   ├── __init__.py
│   ├── main.py                     # FastAPI app com lifespan events
│   ├── config.py                   # Pydantic Settings para .env
│   ├── database.py                 # Async SQLAlchemy engine/session
│   ├── dependencies.py             # get_db dependency
│   │
│   ├── routers/
│   │   ├── __init__.py
│   │   ├── health.py               # GET /health - health check
│   │   ├── notifications.py        # CRUD de notificações
│   │   └── preferences.py          # CRUD de preferências
│   │
│   ├── models/
│   │   ├── __init__.py
│   │   ├── notification.py         # SQLAlchemy Notification model
│   │   └── preference.py           # SQLAlchemy NotificationPreference model
│   │
│   ├── schemas/
│   │   ├── __init__.py
│   │   ├── notification.py         # Pydantic schemas para notificações
│   │   ├── preference.py           # Pydantic schemas para preferências
│   │   └── events.py               # Pydantic schemas para eventos RabbitMQ
│   │
│   ├── services/
│   │   ├── __init__.py
│   │   ├── notification_service.py # Lógica de negócio de notificações
│   │   ├── email_service.py        # Envio de emails via SMTP
│   │   └── preference_service.py   # Gerenciamento de preferências
│   │
│   ├── consumers/
│   │   ├── __init__.py
│   │   └── rabbitmq_consumer.py    # Consumer de eventos RabbitMQ
│   │
│   └── templates/
│       └── email/
│           ├── demand_assigned.html      # Template: nova demanda atribuída
│           └── demand_status_changed.html # Template: status alterado
│
├── alembic/                        # Migrações de banco
│   ├── versions/
│   ├── env.py                      # Configuração async do Alembic
│   └── script.py.mako
├── alembic.ini
│
├── tests/
│   ├── __init__.py
│   ├── conftest.py                 # Fixtures pytest-asyncio
│   ├── test_notifications.py
│   ├── test_preferences.py
│   └── test_email_service.py
│
├── .env.example                    # Exemplo de variáveis de ambiente
├── Dockerfile                      # Container image
├── docker-compose.yml              # Orquestração local
├── requirements.txt                # Dependências Python
└── pyproject.toml                  # Configuração do projeto
```

### Known Gotchas & Library Quirks

```python
# CRITICAL: SQLAlchemy async REQUER expire_on_commit=False
async_session_factory = async_sessionmaker(
    engine,
    class_=AsyncSession,
    expire_on_commit=False  # SEM ISSO: erro ao acessar atributos após commit
)

# CRITICAL: aio-pika requer connect_robust para reconexão automática
connection = await connect_robust(settings.rabbitmq_url)  # NÃO usar connect()

# CRITICAL: FastAPI lifespan events para iniciar consumer
@asynccontextmanager
async def lifespan(app: FastAPI):
    # Consumer deve rodar como task em background
    consumer_task = asyncio.create_task(consumer.start_consuming())
    yield
    consumer_task.cancel()

# GOTCHA: Pydantic v2 usa ConfigDict ao invés de class Config
class NotificationResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)  # NÃO usar orm_mode=True

# GOTCHA: Imports em models/__init__.py para Alembic detectar models
# Em app/models/__init__.py:
from app.models.notification import Notification
from app.models.preference import NotificationPreference

# GOTCHA: SMTP com TLS requer start_tls=True no aiosmtplib
await aiosmtplib.send(
    message,
    hostname=settings.smtp_host,
    port=settings.smtp_port,
    start_tls=True  # OBRIGATÓRIO para porta 587
)
```

## Implementation Blueprint

### Data Models and Structure

#### Notification Model (SQLAlchemy)

```python
# app/models/notification.py
from sqlalchemy import String, Integer, DateTime, Text, Enum as SAEnum
from sqlalchemy.orm import Mapped, mapped_column
from sqlalchemy.dialects.postgresql import JSONB
from datetime import datetime
import enum

from app.database import Base


class NotificationStatus(str, enum.Enum):
    PENDING = "pending"
    SENT = "sent"
    FAILED = "failed"
    READ = "read"


class NotificationType(str, enum.Enum):
    NEW_DEMAND_ASSIGNED = "new_demand_assigned"
    DEMAND_STATUS_CHANGED = "demand_status_changed"


class NotificationChannel(str, enum.Enum):
    EMAIL = "email"


class Notification(Base):
    __tablename__ = "notifications"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    recipient_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    recipient_email: Mapped[str] = mapped_column(String(255), nullable=False)
    recipient_name: Mapped[str] = mapped_column(String(255), nullable=False)

    notification_type: Mapped[NotificationType] = mapped_column(
        SAEnum(NotificationType), nullable=False, index=True
    )
    channel: Mapped[NotificationChannel] = mapped_column(
        SAEnum(NotificationChannel), default=NotificationChannel.EMAIL
    )
    status: Mapped[NotificationStatus] = mapped_column(
        SAEnum(NotificationStatus), default=NotificationStatus.PENDING, index=True
    )

    title: Mapped[str] = mapped_column(String(255), nullable=False)
    message: Mapped[str] = mapped_column(Text, nullable=False)

    # Referência à entidade que gerou a notificação
    entity_type: Mapped[str | None] = mapped_column(String(50))  # "DEMANDA"
    entity_id: Mapped[int | None] = mapped_column(Integer)

    # Dados extras do evento
    metadata: Mapped[dict | None] = mapped_column(JSONB)

    retry_count: Mapped[int] = mapped_column(Integer, default=0)
    error_message: Mapped[str | None] = mapped_column(Text)

    read_at: Mapped[datetime | None] = mapped_column(DateTime)
    sent_at: Mapped[datetime | None] = mapped_column(DateTime)
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )
```

#### Preference Model (SQLAlchemy)

```python
# app/models/preference.py
from sqlalchemy import String, Integer, Boolean, DateTime, UniqueConstraint
from sqlalchemy.orm import Mapped, mapped_column
from datetime import datetime

from app.database import Base


class NotificationPreference(Base):
    __tablename__ = "notification_preferences"
    __table_args__ = (
        UniqueConstraint(
            'user_id', 'notification_type', 'channel',
            name='uq_user_type_channel'
        ),
    )

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    user_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    notification_type: Mapped[str] = mapped_column(String(50), nullable=False)
    channel: Mapped[str] = mapped_column(String(20), nullable=False)
    enabled: Mapped[bool] = mapped_column(Boolean, default=True)
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )
```

#### Pydantic Schemas

```python
# app/schemas/events.py - Schemas para eventos RabbitMQ
from pydantic import BaseModel
from datetime import date


class DemandEvent(BaseModel):
    """Evento recebido do SIGESI via RabbitMQ"""
    event_type: str  # "created", "assigned", "status_changed"
    demand_id: int
    solicitacao_id: int
    assunto: str  # BURACO, ESGOTO, ILUMINACAO, LIMPEZA, OUTROS

    # Responsável (agente/operador)
    responsavel_id: int
    responsavel_email: str
    responsavel_nome: str

    # Status (para status_changed)
    old_status: str | None = None
    new_status: str | None = None

    # Prazo
    prazo: date | None = None


# app/schemas/notification.py
from pydantic import BaseModel, ConfigDict
from datetime import datetime
from enum import Enum


class NotificationTypeEnum(str, Enum):
    NEW_DEMAND_ASSIGNED = "new_demand_assigned"
    DEMAND_STATUS_CHANGED = "demand_status_changed"


class NotificationStatusEnum(str, Enum):
    PENDING = "pending"
    SENT = "sent"
    FAILED = "failed"
    READ = "read"


class NotificationResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    recipient_id: int
    recipient_email: str
    recipient_name: str
    notification_type: NotificationTypeEnum
    status: NotificationStatusEnum
    title: str
    message: str
    entity_type: str | None
    entity_id: int | None
    read_at: datetime | None
    sent_at: datetime | None
    created_at: datetime


class NotificationMarkRead(BaseModel):
    notification_ids: list[int]


# app/schemas/preference.py
class PreferenceCreate(BaseModel):
    notification_type: str
    channel: str = "email"
    enabled: bool = True


class PreferenceUpdate(BaseModel):
    enabled: bool


class PreferenceResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    user_id: int
    notification_type: str
    channel: str
    enabled: bool
```

### Implementation Tasks (ordered by dependencies)

```yaml
Task 1: CREATE notification-service/requirements.txt
  - IMPLEMENT: Arquivo de dependências Python
  - CONTENT: |
      fastapi>=0.115.0
      uvicorn[standard]>=0.32.0
      pydantic>=2.10.0
      pydantic-settings>=2.6.0
      sqlalchemy[asyncio]>=2.0.36
      asyncpg>=0.30.0
      alembic>=1.14.0
      aio-pika>=9.5.0
      aiosmtplib>=3.0.0
      jinja2>=3.1.0
      python-dotenv>=1.0.0
      httpx>=0.28.0
      pytest>=8.0.0
      pytest-asyncio>=0.24.0
  - PLACEMENT: notification-service/requirements.txt

Task 2: CREATE notification-service/app/config.py
  - IMPLEMENT: Pydantic Settings para configuração via .env
  - FOLLOW pattern: PRPs/ai_docs/fastapi_notification_service.md (seção 4)
  - CONTENT: |
      - database_url: str
      - rabbitmq_url: str
      - rabbitmq_queue: str (default: "sigesi_notifications")
      - smtp_host, smtp_port, smtp_username, smtp_password
      - smtp_from_email, smtp_from_name
  - PLACEMENT: notification-service/app/config.py

Task 3: CREATE notification-service/app/database.py
  - IMPLEMENT: Async SQLAlchemy engine e session factory
  - FOLLOW pattern: PRPs/ai_docs/fastapi_notification_service.md (seção 3)
  - CRITICAL: expire_on_commit=False
  - PLACEMENT: notification-service/app/database.py

Task 4: CREATE notification-service/app/models/notification.py
  - IMPLEMENT: SQLAlchemy model Notification
  - FOLLOW pattern: Implementation Blueprint acima
  - PLACEMENT: notification-service/app/models/notification.py

Task 5: CREATE notification-service/app/models/preference.py
  - IMPLEMENT: SQLAlchemy model NotificationPreference
  - FOLLOW pattern: Implementation Blueprint acima
  - PLACEMENT: notification-service/app/models/preference.py

Task 6: CREATE notification-service/app/models/__init__.py
  - IMPLEMENT: Exports para Alembic detectar models
  - CONTENT: |
      from app.models.notification import Notification, NotificationStatus, NotificationType
      from app.models.preference import NotificationPreference
  - PLACEMENT: notification-service/app/models/__init__.py

Task 7: CREATE notification-service/app/schemas/events.py
  - IMPLEMENT: Pydantic schemas para eventos RabbitMQ
  - FOLLOW pattern: Implementation Blueprint acima
  - PLACEMENT: notification-service/app/schemas/events.py

Task 8: CREATE notification-service/app/schemas/notification.py
  - IMPLEMENT: Pydantic schemas para API de notificações
  - FOLLOW pattern: Implementation Blueprint acima
  - PLACEMENT: notification-service/app/schemas/notification.py

Task 9: CREATE notification-service/app/schemas/preference.py
  - IMPLEMENT: Pydantic schemas para API de preferências
  - FOLLOW pattern: Implementation Blueprint acima
  - PLACEMENT: notification-service/app/schemas/preference.py

Task 10: CREATE notification-service/app/dependencies.py
  - IMPLEMENT: Dependency injection para database session
  - FOLLOW pattern: PRPs/ai_docs/fastapi_notification_service.md (seção 3)
  - PLACEMENT: notification-service/app/dependencies.py

Task 11: CREATE notification-service/app/templates/email/demand_assigned.html
  - IMPLEMENT: Template Jinja2 para email de nova demanda
  - CONTENT: HTML com variáveis {{ recipient_name }}, {{ demand_id }}, {{ assunto }}, {{ prazo }}
  - PLACEMENT: notification-service/app/templates/email/demand_assigned.html

Task 12: CREATE notification-service/app/templates/email/demand_status_changed.html
  - IMPLEMENT: Template Jinja2 para email de mudança de status
  - CONTENT: HTML com variáveis {{ recipient_name }}, {{ demand_id }}, {{ old_status }}, {{ new_status }}
  - PLACEMENT: notification-service/app/templates/email/demand_status_changed.html

Task 13: CREATE notification-service/app/services/email_service.py
  - IMPLEMENT: Serviço de envio de emails via SMTP
  - FOLLOW pattern: PRPs/ai_docs/fastapi_notification_service.md (seção 7)
  - METHODS: send_email(to_email, subject, template_name, context) -> bool
  - CRITICAL: Usar aiosmtplib com start_tls=True
  - PLACEMENT: notification-service/app/services/email_service.py

Task 14: CREATE notification-service/app/services/preference_service.py
  - IMPLEMENT: Serviço para gerenciar preferências de notificação
  - METHODS:
      - get_user_preferences(db, user_id) -> list[PreferenceResponse]
      - is_notification_enabled(db, user_id, notification_type, channel) -> bool
      - upsert_preference(db, user_id, preference_data) -> PreferenceResponse
  - PLACEMENT: notification-service/app/services/preference_service.py

Task 15: CREATE notification-service/app/services/notification_service.py
  - IMPLEMENT: Serviço principal de notificações
  - DEPENDENCIES: EmailService, PreferenceService
  - METHODS:
      - handle_demand_assigned(event: DemandEvent) -> None
      - handle_demand_status_changed(event: DemandEvent) -> None
      - get_user_notifications(db, user_id, skip, limit) -> list[NotificationResponse]
      - mark_as_read(db, notification_ids) -> None
      - _create_notification(db, data) -> Notification
      - _send_notification(notification: Notification) -> bool
  - PLACEMENT: notification-service/app/services/notification_service.py

Task 16: CREATE notification-service/app/consumers/rabbitmq_consumer.py
  - IMPLEMENT: Consumer de eventos RabbitMQ
  - FOLLOW pattern: PRPs/ai_docs/fastapi_notification_service.md (seção 6)
  - ROUTING_KEYS: "demand.assigned", "demand.status_changed"
  - EXCHANGE: "sigesi_events" (topic exchange)
  - CRITICAL: Usar connect_robust, set_qos(prefetch_count=10)
  - PLACEMENT: notification-service/app/consumers/rabbitmq_consumer.py

Task 17: CREATE notification-service/app/routers/health.py
  - IMPLEMENT: Health check endpoint
  - ENDPOINTS: GET /health -> {"status": "healthy", "timestamp": "..."}
  - PLACEMENT: notification-service/app/routers/health.py

Task 18: CREATE notification-service/app/routers/notifications.py
  - IMPLEMENT: CRUD de notificações
  - ENDPOINTS:
      - GET /api/notifications/user/{user_id} -> list[NotificationResponse]
      - GET /api/notifications/{notification_id} -> NotificationResponse
      - POST /api/notifications/mark-read -> None
      - GET /api/notifications/user/{user_id}/unread-count -> int
  - PLACEMENT: notification-service/app/routers/notifications.py

Task 19: CREATE notification-service/app/routers/preferences.py
  - IMPLEMENT: CRUD de preferências
  - ENDPOINTS:
      - GET /api/preferences/user/{user_id} -> list[PreferenceResponse]
      - PUT /api/preferences/user/{user_id} -> PreferenceResponse
      - DELETE /api/preferences/{preference_id} -> None
  - PLACEMENT: notification-service/app/routers/preferences.py

Task 20: CREATE notification-service/app/main.py
  - IMPLEMENT: FastAPI app com lifespan events
  - FOLLOW pattern: PRPs/ai_docs/fastapi_notification_service.md (seção 8)
  - CRITICAL: Iniciar RabbitMQ consumer como asyncio.task no lifespan
  - ROUTERS: health, notifications, preferences
  - PLACEMENT: notification-service/app/main.py

Task 21: CREATE notification-service/alembic.ini
  - IMPLEMENT: Configuração do Alembic
  - CONTENT: Standard alembic.ini com sqlalchemy.url placeholder
  - PLACEMENT: notification-service/alembic.ini

Task 22: CREATE notification-service/alembic/env.py
  - IMPLEMENT: Configuração async do Alembic
  - FOLLOW pattern: PRPs/ai_docs/fastapi_notification_service.md (seção 11)
  - CRITICAL: Importar todos os models para metadata.create_all funcionar
  - PLACEMENT: notification-service/alembic/env.py

Task 23: CREATE notification-service/.env.example
  - IMPLEMENT: Exemplo de variáveis de ambiente
  - CONTENT: |
      NOTIFICATION_DATABASE_URL=postgresql+asyncpg://user:password@localhost:5432/notifications
      NOTIFICATION_RABBITMQ_URL=amqp://guest:guest@localhost:5672/
      NOTIFICATION_RABBITMQ_QUEUE=sigesi_notifications
      NOTIFICATION_SMTP_HOST=smtp.gmail.com
      NOTIFICATION_SMTP_PORT=587
      NOTIFICATION_SMTP_USERNAME=
      NOTIFICATION_SMTP_PASSWORD=
      NOTIFICATION_SMTP_FROM_EMAIL=noreply@sigesi.com
      NOTIFICATION_SMTP_FROM_NAME=SIGESI Notificações
  - PLACEMENT: notification-service/.env.example

Task 24: CREATE notification-service/Dockerfile
  - IMPLEMENT: Container image para o microserviço
  - FOLLOW pattern: PRPs/ai_docs/fastapi_notification_service.md (seção 10)
  - BASE: python:3.12-slim
  - CMD: alembic upgrade head && uvicorn app.main:app --host 0.0.0.0 --port 8000
  - PLACEMENT: notification-service/Dockerfile

Task 25: CREATE notification-service/docker-compose.yml
  - IMPLEMENT: Orquestração local com PostgreSQL e RabbitMQ
  - SERVICES: notification-service, db (postgres:16-alpine), rabbitmq (rabbitmq:3-management-alpine)
  - PORTS: notification-service:8001, rabbitmq:5672,15672
  - PLACEMENT: notification-service/docker-compose.yml

Task 26: CREATE notification-service/tests/conftest.py
  - IMPLEMENT: Fixtures pytest-asyncio
  - FOLLOW pattern: PRPs/ai_docs/fastapi_notification_service.md (seção 9)
  - FIXTURES: db_session, client (httpx AsyncClient)
  - PLACEMENT: notification-service/tests/conftest.py

Task 27: CREATE notification-service/tests/test_notifications.py
  - IMPLEMENT: Testes unitários para notificações
  - TESTS:
      - test_get_user_notifications
      - test_mark_notification_as_read
      - test_unread_count
  - PLACEMENT: notification-service/tests/test_notifications.py

Task 28: CREATE notification-service/tests/test_preferences.py
  - IMPLEMENT: Testes unitários para preferências
  - TESTS:
      - test_get_user_preferences
      - test_create_preference
      - test_update_preference
      - test_default_preferences_enabled
  - PLACEMENT: notification-service/tests/test_preferences.py

Task 29: MODIFY pom.xml (SIGESI principal)
  - ADD: spring-boot-starter-amqp dependency
  - CONTENT: |
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-amqp</artifactId>
      </dependency>
  - PLACEMENT: pom.xml

Task 30: CREATE src/main/java/com/sigesi/sigesi/config/RabbitMQConfig.java
  - IMPLEMENT: Configuração do RabbitMQ no SIGESI
  - CONTENT:
      - Exchange: sigesi_events (TopicExchange)
      - Queue: sigesi_notifications
      - Binding: queue -> exchange com routing keys "demand.*"
      - RabbitTemplate bean
  - PLACEMENT: src/main/java/com/sigesi/sigesi/config/RabbitMQConfig.java

Task 31: CREATE src/main/java/com/sigesi/sigesi/notifications/events/DemandEvent.java
  - IMPLEMENT: POJO para evento de demanda
  - FIELDS: eventType, demandId, solicitacaoId, assunto, responsavelId, responsavelEmail, responsavelNome, oldStatus, newStatus, prazo
  - PLACEMENT: src/main/java/com/sigesi/sigesi/notifications/events/DemandEvent.java

Task 32: CREATE src/main/java/com/sigesi/sigesi/notifications/NotificationPublisher.java
  - IMPLEMENT: Serviço para publicar eventos no RabbitMQ
  - DEPENDENCIES: RabbitTemplate
  - METHODS:
      - publishDemandAssigned(Demanda demanda)
      - publishDemandStatusChanged(Demanda demanda, DemandaStatus oldStatus)
  - ROUTING_KEYS: "demand.assigned", "demand.status_changed"
  - PLACEMENT: src/main/java/com/sigesi/sigesi/notifications/NotificationPublisher.java

Task 33: MODIFY src/main/java/com/sigesi/sigesi/demandas/DemandaService.java
  - ADD: Injeção de NotificationPublisher
  - MODIFY createDemanda: Adicionar chamada para publishDemandAssigned após save
  - MODIFY updateDemanda: Adicionar chamada para publishDemandStatusChanged se status mudou
  - PRESERVE: Toda lógica existente
```

### Implementation Patterns & Key Details

```python
# Pattern: Notification Service com verificação de preferências
async def handle_demand_assigned(self, event: DemandEvent):
    async with self.get_db_session() as db:
        # Verificar preferências do usuário
        is_enabled = await self.preference_service.is_notification_enabled(
            db,
            user_id=event.responsavel_id,
            notification_type=NotificationType.NEW_DEMAND_ASSIGNED.value,
            channel="email"
        )

        if not is_enabled:
            return  # Usuário desabilitou este tipo de notificação

        # Criar notificação no banco
        notification = await self._create_notification(db, {
            "recipient_id": event.responsavel_id,
            "recipient_email": event.responsavel_email,
            "recipient_name": event.responsavel_nome,
            "notification_type": NotificationType.NEW_DEMAND_ASSIGNED,
            "title": f"Nova demanda atribuída: #{event.demand_id}",
            "message": f"Você recebeu uma nova demanda relacionada a {event.assunto}",
            "entity_type": "DEMANDA",
            "entity_id": event.demand_id,
            "metadata": event.model_dump()
        })

        # Enviar email
        success = await self._send_notification(notification)

        if success:
            notification.status = NotificationStatus.SENT
            notification.sent_at = datetime.utcnow()
        else:
            notification.status = NotificationStatus.FAILED
            notification.retry_count += 1

        await db.commit()
```

```java
// Pattern: Publicação de evento no Spring Boot
@Service
public class NotificationPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE = "sigesi_events";

    public void publishDemandAssigned(Demanda demanda) {
        DemandEvent event = DemandEvent.builder()
            .eventType("assigned")
            .demandId(demanda.getId())
            .solicitacaoId(demanda.getSolicitacao().getId())
            .assunto(demanda.getSolicitacao().getAssunto().name())
            .responsavelId(demanda.getResponsavel().getId())
            .responsavelEmail(demanda.getResponsavel().getEmail())
            .responsavelNome(demanda.getResponsavel().getName())
            .prazo(demanda.getPrazo())
            .build();

        rabbitTemplate.convertAndSend(EXCHANGE, "demand.assigned", event);
    }
}
```

### Integration Points

```yaml
SIGESI (Spring Boot):
  - ADD dependency: spring-boot-starter-amqp em pom.xml
  - CREATE: RabbitMQConfig.java para configurar exchange/queue
  - CREATE: NotificationPublisher.java para publicar eventos
  - MODIFY: DemandaService.java para chamar publisher após operações

NOTIFICATION-SERVICE (FastAPI):
  - CONSUME: Eventos do exchange "sigesi_events"
  - ROUTING KEYS: "demand.assigned", "demand.status_changed"
  - DATABASE: PostgreSQL próprio (não compartilha com SIGESI)

ENVIRONMENT:
  - RabbitMQ deve estar acessível para ambos os serviços
  - Variáveis de ambiente para conexão em .env de cada serviço
```

## Validation Loop

### Level 1: Syntax & Style (Notification Service)

```bash
cd notification-service

# Instalar dependências
pip install -r requirements.txt

# Verificar imports e sintaxe
python -c "from app.main import app; print('OK')"

# Linting (se ruff instalado)
pip install ruff
ruff check app/ --fix
ruff format app/

# Type checking (opcional)
pip install mypy
mypy app/
```

### Level 2: Unit Tests

```bash
cd notification-service

# Instalar dependências de teste
pip install pytest pytest-asyncio httpx

# Rodar testes
pytest tests/ -v

# Expected: All tests pass
```

### Level 3: Integration Testing

```bash
# Subir infraestrutura
cd notification-service
docker-compose up -d

# Aguardar serviços
sleep 10

# Health check
curl -f http://localhost:8001/health || echo "Service health check failed"

# Verificar RabbitMQ Management
curl -f http://localhost:15672 || echo "RabbitMQ management not accessible"

# Expected: Both services responding
```

### Level 4: End-to-End Testing

```bash
# 1. Verificar que SIGESI compila com RabbitMQ
cd /home/joelfmjr/ifrn/curso/integrador/sigesi
mvn clean compile

# 2. Subir SIGESI com RabbitMQ
docker-compose up -d

# 3. Criar demanda via API e verificar que notificação é enviada
# (Requer autenticação OAuth2 - testar manualmente)

# 4. Verificar logs do notification-service para eventos recebidos
docker-compose logs -f notification-service
```

## Final Validation Checklist

### Technical Validation

- [ ] Microserviço FastAPI inicia sem erros
- [ ] Conexão com PostgreSQL estabelecida
- [ ] Consumer RabbitMQ conectado e escutando
- [ ] Health check endpoint respondendo
- [ ] Todos os testes unitários passando

### Feature Validation

- [ ] Evento `demand.assigned` gera notificação
- [ ] Evento `demand.status_changed` gera notificação
- [ ] Emails são enviados corretamente
- [ ] Notificações são persistidas no banco
- [ ] Preferências respeitadas (notificação não enviada se desabilitada)
- [ ] API de consulta de notificações funcional

### Code Quality Validation

- [ ] Estrutura de diretórios conforme especificado
- [ ] Pydantic schemas com ConfigDict (não class Config)
- [ ] SQLAlchemy async com expire_on_commit=False
- [ ] aio-pika com connect_robust
- [ ] Templates de email renderizando corretamente

### Integration Validation

- [ ] SIGESI compila com spring-boot-starter-amqp
- [ ] RabbitMQConfig.java configurado corretamente
- [ ] NotificationPublisher.java publicando eventos
- [ ] DemandaService.java chamando publisher

---

## Anti-Patterns to Avoid

- ❌ Não usar SQLAlchemy síncrono - usar apenas asyncpg
- ❌ Não usar `connect()` do aio-pika - usar `connect_robust()`
- ❌ Não usar `class Config` em Pydantic - usar `model_config = ConfigDict(...)`
- ❌ Não compartilhar banco de dados com SIGESI - usar banco próprio
- ❌ Não bloquear event loop com operações síncronas
- ❌ Não ignorar preferências de notificação - sempre verificar antes de enviar
- ❌ Não perder eventos - usar acknowledgment manual no RabbitMQ

---

## Confidence Score: 8/10

**Justificativa**:
- PRP detalhado com exemplos de código e estrutura completa
- Documentação de referência criada em ai_docs
- Padrões conhecidos e documentados (FastAPI, SQLAlchemy async, aio-pika)
- Integração com Spring Boot requer modificações em código existente (risco moderado)
- Dependência de infraestrutura externa (RabbitMQ) adiciona complexidade

**Riscos identificados**:
1. Configuração de SMTP pode variar por provedor (Gmail, SendGrid, etc.)
2. Testes E2E dependem de autenticação OAuth2 do SIGESI
3. RabbitMQ deve estar configurado corretamente em ambos os ambientes

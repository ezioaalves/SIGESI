# FastAPI Notification Microservice - Reference Documentation

This document provides essential patterns and references for building a notification microservice with FastAPI.

## 1. Project Structure (Recommended)

```
notification-service/
├── app/
│   ├── __init__.py
│   ├── main.py                 # FastAPI app entry point
│   ├── config.py               # Pydantic Settings
│   ├── database.py             # Async SQLAlchemy setup
│   ├── dependencies.py         # Shared dependencies (DB session, etc.)
│   ├── routers/
│   │   ├── __init__.py
│   │   ├── notifications.py    # Notification CRUD endpoints
│   │   ├── preferences.py      # User preferences endpoints
│   │   └── health.py           # Health check endpoint
│   ├── models/
│   │   ├── __init__.py
│   │   ├── notification.py     # SQLAlchemy Notification model
│   │   └── preference.py       # SQLAlchemy Preference model
│   ├── schemas/
│   │   ├── __init__.py
│   │   ├── notification.py     # Pydantic schemas for notifications
│   │   └── preference.py       # Pydantic schemas for preferences
│   ├── services/
│   │   ├── __init__.py
│   │   ├── notification_service.py   # Notification business logic
│   │   ├── email_service.py          # Email sending via SMTP/SendGrid
│   │   └── preference_service.py     # Preference management
│   ├── consumers/
│   │   ├── __init__.py
│   │   └── rabbitmq_consumer.py      # RabbitMQ event consumer
│   └── utils/
│       ├── __init__.py
│       └── templates.py              # Email template rendering
├── alembic/                    # Database migrations
│   ├── versions/
│   ├── env.py
│   └── alembic.ini
├── tests/
│   ├── __init__.py
│   ├── conftest.py             # Pytest fixtures
│   ├── test_notifications.py
│   └── test_preferences.py
├── .env.example
├── Dockerfile
├── docker-compose.yml
├── requirements.txt
└── pyproject.toml
```

## 2. Core Dependencies

```txt
# requirements.txt
fastapi>=0.115.0
uvicorn[standard]>=0.32.0
pydantic>=2.10.0
pydantic-settings>=2.6.0

# Database
sqlalchemy[asyncio]>=2.0.36
asyncpg>=0.30.0
alembic>=1.14.0

# Message Queue
aio-pika>=9.5.0  # Async RabbitMQ client

# Email
aiosmtplib>=3.0.0  # Async SMTP
# OR
sendgrid>=6.11.0  # SendGrid SDK

# Utilities
python-dotenv>=1.0.0
httpx>=0.28.0  # Async HTTP client
jinja2>=3.1.0  # Email templates
```

## 3. Async SQLAlchemy Setup

### database.py

```python
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession, async_sessionmaker
from sqlalchemy.orm import DeclarativeBase

DATABASE_URL = "postgresql+asyncpg://user:password@localhost:5432/notifications"

engine = create_async_engine(
    DATABASE_URL,
    echo=True,  # Set False in production
    pool_size=5,
    max_overflow=10
)

async_session_factory = async_sessionmaker(
    engine,
    class_=AsyncSession,
    expire_on_commit=False  # Important for async!
)


class Base(DeclarativeBase):
    pass


async def get_db() -> AsyncGenerator[AsyncSession, None]:
    async with async_session_factory() as session:
        try:
            yield session
            await session.commit()
        except Exception:
            await session.rollback()
            raise
```

### Key SQLAlchemy Async Documentation
- Session basics: https://docs.sqlalchemy.org/en/20/orm/session_basics.html
- Async extension: https://docs.sqlalchemy.org/en/20/orm/extensions/asyncio.html

## 4. Pydantic v2 Patterns

### Settings Configuration (config.py)

```python
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=".env",
        env_prefix="NOTIFICATION_",
        case_sensitive=False
    )

    # Database
    database_url: str

    # RabbitMQ
    rabbitmq_url: str = "amqp://guest:guest@localhost:5672/"
    rabbitmq_queue: str = "notifications"

    # Email (SMTP)
    smtp_host: str = "smtp.gmail.com"
    smtp_port: int = 587
    smtp_username: str
    smtp_password: str
    smtp_from_email: str
    smtp_from_name: str = "SIGESI Notificações"

    # OR SendGrid
    sendgrid_api_key: str | None = None


settings = Settings()
```

### Schema Patterns (schemas/notification.py)

```python
from pydantic import BaseModel, ConfigDict, Field
from datetime import datetime
from enum import Enum


class NotificationType(str, Enum):
    NEW_DEMAND_ASSIGNED = "new_demand_assigned"
    DEMAND_STATUS_CHANGED = "demand_status_changed"
    DEMAND_DEADLINE_APPROACHING = "demand_deadline_approaching"
    DEMAND_OVERDUE = "demand_overdue"


class NotificationStatus(str, Enum):
    PENDING = "pending"
    SENT = "sent"
    DELIVERED = "delivered"
    FAILED = "failed"
    READ = "read"


class NotificationChannel(str, Enum):
    EMAIL = "email"


# Create schema - used for incoming requests
class NotificationCreate(BaseModel):
    recipient_id: int
    recipient_email: str
    notification_type: NotificationType
    title: str
    message: str
    entity_type: str | None = None  # "DEMANDA", "SOLICITACAO"
    entity_id: int | None = None
    metadata: dict | None = None


# Response schema - used for API responses
class NotificationResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    recipient_id: int
    recipient_email: str
    notification_type: NotificationType
    channel: NotificationChannel
    status: NotificationStatus
    title: str
    message: str
    entity_type: str | None
    entity_id: int | None
    read_at: datetime | None
    sent_at: datetime | None
    created_at: datetime


# Event schema - received from RabbitMQ
class DemandEvent(BaseModel):
    event_type: str  # "created", "status_changed", "assigned"
    demand_id: int
    solicitacao_id: int
    responsavel_id: int
    responsavel_email: str
    responsavel_nome: str
    old_status: str | None = None
    new_status: str | None = None
    assunto: str
    prazo: str | None = None
```

### Key Pydantic v2 Documentation
- Models: https://docs.pydantic.dev/latest/concepts/models/
- Validators: https://docs.pydantic.dev/latest/concepts/validators/
- Settings: https://docs.pydantic.dev/latest/concepts/pydantic_settings/

## 5. SQLAlchemy Models

### models/notification.py

```python
from sqlalchemy import String, Integer, Boolean, DateTime, Text, Enum as SAEnum
from sqlalchemy.orm import Mapped, mapped_column
from sqlalchemy.dialects.postgresql import JSONB
from datetime import datetime
import enum

from app.database import Base


class NotificationStatus(str, enum.Enum):
    PENDING = "pending"
    SENT = "sent"
    DELIVERED = "delivered"
    FAILED = "failed"
    READ = "read"


class NotificationType(str, enum.Enum):
    NEW_DEMAND_ASSIGNED = "new_demand_assigned"
    DEMAND_STATUS_CHANGED = "demand_status_changed"
    DEMAND_DEADLINE_APPROACHING = "demand_deadline_approaching"
    DEMAND_OVERDUE = "demand_overdue"


class NotificationChannel(str, enum.Enum):
    EMAIL = "email"


class Notification(Base):
    __tablename__ = "notifications"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    recipient_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    recipient_email: Mapped[str] = mapped_column(String(255), nullable=False)

    notification_type: Mapped[NotificationType] = mapped_column(
        SAEnum(NotificationType), nullable=False
    )
    channel: Mapped[NotificationChannel] = mapped_column(
        SAEnum(NotificationChannel), default=NotificationChannel.EMAIL
    )
    status: Mapped[NotificationStatus] = mapped_column(
        SAEnum(NotificationStatus), default=NotificationStatus.PENDING, index=True
    )

    title: Mapped[str] = mapped_column(String(255), nullable=False)
    message: Mapped[str] = mapped_column(Text, nullable=False)

    entity_type: Mapped[str | None] = mapped_column(String(50))
    entity_id: Mapped[int | None] = mapped_column(Integer)
    metadata: Mapped[dict | None] = mapped_column(JSONB)

    retry_count: Mapped[int] = mapped_column(Integer, default=0)

    read_at: Mapped[datetime | None] = mapped_column(DateTime)
    sent_at: Mapped[datetime | None] = mapped_column(DateTime)
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )
```

### models/preference.py

```python
from sqlalchemy import String, Integer, Boolean, DateTime, UniqueConstraint
from sqlalchemy.orm import Mapped, mapped_column
from datetime import datetime

from app.database import Base


class NotificationPreference(Base):
    __tablename__ = "notification_preferences"
    __table_args__ = (
        UniqueConstraint('user_id', 'notification_type', 'channel', name='uq_user_type_channel'),
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

## 6. RabbitMQ Consumer Pattern

### consumers/rabbitmq_consumer.py

```python
import asyncio
import json
import logging
from aio_pika import connect_robust, IncomingMessage, ExchangeType

from app.config import settings
from app.services.notification_service import NotificationService
from app.schemas.notification import DemandEvent

logger = logging.getLogger(__name__)


class RabbitMQConsumer:
    def __init__(self, notification_service: NotificationService):
        self.notification_service = notification_service
        self.connection = None
        self.channel = None

    async def connect(self):
        self.connection = await connect_robust(settings.rabbitmq_url)
        self.channel = await self.connection.channel()
        await self.channel.set_qos(prefetch_count=10)

        # Declare exchange and queue
        exchange = await self.channel.declare_exchange(
            "sigesi_events",
            ExchangeType.TOPIC,
            durable=True
        )

        queue = await self.channel.declare_queue(
            settings.rabbitmq_queue,
            durable=True
        )

        # Bind to demand events
        await queue.bind(exchange, routing_key="demand.*")

        return queue

    async def start_consuming(self):
        queue = await self.connect()

        async with queue.iterator() as queue_iter:
            async for message in queue_iter:
                async with message.process():
                    await self.process_message(message)

    async def process_message(self, message: IncomingMessage):
        try:
            body = json.loads(message.body.decode())
            routing_key = message.routing_key

            logger.info(f"Received message: {routing_key}")

            if routing_key == "demand.created":
                event = DemandEvent(**body)
                await self.notification_service.handle_demand_created(event)
            elif routing_key == "demand.status_changed":
                event = DemandEvent(**body)
                await self.notification_service.handle_demand_status_changed(event)
            elif routing_key == "demand.assigned":
                event = DemandEvent(**body)
                await self.notification_service.handle_demand_assigned(event)

        except Exception as e:
            logger.error(f"Error processing message: {e}")
            # Message will be requeued due to exception

    async def close(self):
        if self.connection:
            await self.connection.close()
```

### Key aio-pika Documentation
- Getting started: https://aio-pika.readthedocs.io/en/latest/quick-start.html
- Patterns: https://aio-pika.readthedocs.io/en/latest/rabbitmq-tutorial/

## 7. Email Service Pattern

### services/email_service.py (SMTP)

```python
import aiosmtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from jinja2 import Environment, PackageLoader
import logging

from app.config import settings

logger = logging.getLogger(__name__)

# Jinja2 environment for email templates
env = Environment(loader=PackageLoader('app', 'templates/email'))


class EmailService:
    async def send_email(
        self,
        to_email: str,
        subject: str,
        template_name: str,
        context: dict
    ) -> bool:
        try:
            # Render template
            template = env.get_template(f"{template_name}.html")
            html_content = template.render(**context)

            # Create message
            message = MIMEMultipart("alternative")
            message["From"] = f"{settings.smtp_from_name} <{settings.smtp_from_email}>"
            message["To"] = to_email
            message["Subject"] = subject

            # Attach HTML content
            html_part = MIMEText(html_content, "html")
            message.attach(html_part)

            # Send email
            await aiosmtplib.send(
                message,
                hostname=settings.smtp_host,
                port=settings.smtp_port,
                username=settings.smtp_username,
                password=settings.smtp_password,
                start_tls=True
            )

            logger.info(f"Email sent to {to_email}")
            return True

        except Exception as e:
            logger.error(f"Failed to send email to {to_email}: {e}")
            return False
```

### services/email_service.py (SendGrid alternative)

```python
from sendgrid import SendGridAPIClient
from sendgrid.helpers.mail import Mail, Email, To, Content
import logging

from app.config import settings

logger = logging.getLogger(__name__)


class SendGridEmailService:
    def __init__(self):
        self.client = SendGridAPIClient(settings.sendgrid_api_key)

    async def send_email(
        self,
        to_email: str,
        subject: str,
        html_content: str
    ) -> bool:
        try:
            message = Mail(
                from_email=Email(settings.smtp_from_email, settings.smtp_from_name),
                to_emails=To(to_email),
                subject=subject,
                html_content=Content("text/html", html_content)
            )

            response = self.client.send(message)

            if response.status_code in [200, 201, 202]:
                logger.info(f"Email sent to {to_email}")
                return True
            else:
                logger.error(f"SendGrid error: {response.status_code}")
                return False

        except Exception as e:
            logger.error(f"Failed to send email to {to_email}: {e}")
            return False
```

## 8. FastAPI Lifespan for Background Tasks

### main.py

```python
from contextlib import asynccontextmanager
from fastapi import FastAPI
import asyncio

from app.database import engine, Base
from app.consumers.rabbitmq_consumer import RabbitMQConsumer
from app.services.notification_service import NotificationService
from app.services.email_service import EmailService
from app.routers import notifications, preferences, health


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    # Create tables
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)

    # Start RabbitMQ consumer in background
    email_service = EmailService()
    notification_service = NotificationService(email_service)
    consumer = RabbitMQConsumer(notification_service)

    consumer_task = asyncio.create_task(consumer.start_consuming())

    yield

    # Shutdown
    consumer_task.cancel()
    await consumer.close()


app = FastAPI(
    title="SIGESI Notification Service",
    version="1.0.0",
    lifespan=lifespan
)

app.include_router(health.router, tags=["health"])
app.include_router(notifications.router, prefix="/api/notifications", tags=["notifications"])
app.include_router(preferences.router, prefix="/api/preferences", tags=["preferences"])
```

### Key FastAPI Documentation
- Lifespan events: https://fastapi.tiangolo.com/advanced/events/
- Background tasks: https://fastapi.tiangolo.com/tutorial/background-tasks/
- Bigger applications: https://fastapi.tiangolo.com/tutorial/bigger-applications/

## 9. Testing Patterns

### conftest.py

```python
import pytest
import pytest_asyncio
from httpx import AsyncClient, ASGITransport
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession, async_sessionmaker

from app.main import app
from app.database import Base, get_db

TEST_DATABASE_URL = "postgresql+asyncpg://test:test@localhost:5432/test_notifications"

test_engine = create_async_engine(TEST_DATABASE_URL, echo=True)
test_session_factory = async_sessionmaker(
    test_engine, class_=AsyncSession, expire_on_commit=False
)


@pytest_asyncio.fixture
async def db_session():
    async with test_engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)

    async with test_session_factory() as session:
        yield session

    async with test_engine.begin() as conn:
        await conn.run_sync(Base.metadata.drop_all)


@pytest_asyncio.fixture
async def client(db_session):
    async def override_get_db():
        yield db_session

    app.dependency_overrides[get_db] = override_get_db

    async with AsyncClient(
        transport=ASGITransport(app=app),
        base_url="http://test"
    ) as ac:
        yield ac

    app.dependency_overrides.clear()
```

### test_notifications.py

```python
import pytest
from httpx import AsyncClient


@pytest.mark.asyncio
async def test_create_notification(client: AsyncClient):
    response = await client.post(
        "/api/notifications/",
        json={
            "recipient_id": 1,
            "recipient_email": "test@example.com",
            "notification_type": "new_demand_assigned",
            "title": "Nova demanda atribuída",
            "message": "Você recebeu uma nova demanda."
        }
    )
    assert response.status_code == 201
    data = response.json()
    assert data["status"] == "pending"


@pytest.mark.asyncio
async def test_get_user_notifications(client: AsyncClient):
    response = await client.get("/api/notifications/user/1")
    assert response.status_code == 200
    assert isinstance(response.json(), list)
```

## 10. Docker Configuration

### Dockerfile

```dockerfile
FROM python:3.12-slim

WORKDIR /app

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application
COPY app/ ./app/
COPY alembic/ ./alembic/
COPY alembic.ini .

# Run migrations and start server
CMD alembic upgrade head && uvicorn app.main:app --host 0.0.0.0 --port 8000
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  notification-service:
    build: .
    ports:
      - "8001:8000"
    environment:
      - NOTIFICATION_DATABASE_URL=postgresql+asyncpg://user:password@db:5432/notifications
      - NOTIFICATION_RABBITMQ_URL=amqp://guest:guest@rabbitmq:5672/
      - NOTIFICATION_SMTP_HOST=smtp.gmail.com
      - NOTIFICATION_SMTP_PORT=587
      - NOTIFICATION_SMTP_USERNAME=${SMTP_USERNAME}
      - NOTIFICATION_SMTP_PASSWORD=${SMTP_PASSWORD}
      - NOTIFICATION_SMTP_FROM_EMAIL=noreply@sigesi.com
    depends_on:
      - db
      - rabbitmq

  db:
    image: postgres:16-alpine
    environment:
      - POSTGRES_USER=user
      - POSTGRES_PASSWORD=password
      - POSTGRES_DB=notifications
    volumes:
      - notification_db_data:/var/lib/postgresql/data

  rabbitmq:
    image: rabbitmq:3-management-alpine
    ports:
      - "5672:5672"
      - "15672:15672"

volumes:
  notification_db_data:
```

## 11. Alembic Migration Setup

### alembic/env.py (async version)

```python
import asyncio
from logging.config import fileConfig
from sqlalchemy import pool
from sqlalchemy.ext.asyncio import async_engine_from_config
from alembic import context

from app.database import Base
from app.config import settings
from app.models import notification, preference  # Import all models

config = context.config
config.set_main_option("sqlalchemy.url", settings.database_url)

if config.config_file_name is not None:
    fileConfig(config.config_file_name)

target_metadata = Base.metadata


def run_migrations_offline() -> None:
    url = config.get_main_option("sqlalchemy.url")
    context.configure(
        url=url,
        target_metadata=target_metadata,
        literal_binds=True,
        dialect_opts={"paramstyle": "named"},
    )
    with context.begin_transaction():
        context.run_migrations()


def do_run_migrations(connection):
    context.configure(connection=connection, target_metadata=target_metadata)
    with context.begin_transaction():
        context.run_migrations()


async def run_async_migrations():
    connectable = async_engine_from_config(
        config.get_section(config.config_ini_section, {}),
        prefix="sqlalchemy.",
        poolclass=pool.NullPool,
    )
    async with connectable.connect() as connection:
        await connection.run_sync(do_run_migrations)
    await connectable.dispose()


def run_migrations_online() -> None:
    asyncio.run(run_async_migrations())


if context.is_offline_mode():
    run_migrations_offline()
else:
    run_migrations_online()
```

## Key External Resources

### FastAPI
- Official docs: https://fastapi.tiangolo.com/
- Best practices: https://github.com/zhanymkanov/fastapi-best-practices

### SQLAlchemy Async
- Async docs: https://docs.sqlalchemy.org/en/20/orm/extensions/asyncio.html
- Tutorial: https://berkkaraal.com/blog/2024/09/19/setup-fastapi-project-with-async-sqlalchemy-2-alembic-postgresql-and-docker/

### aio-pika (RabbitMQ)
- Docs: https://aio-pika.readthedocs.io/en/latest/
- Tutorial: https://aio-pika.readthedocs.io/en/latest/rabbitmq-tutorial/

### Pydantic
- v2 docs: https://docs.pydantic.dev/latest/
- Settings: https://docs.pydantic.dev/latest/concepts/pydantic_settings/

### Testing
- pytest-asyncio: https://pytest-asyncio.readthedocs.io/
- httpx testing: https://www.python-httpx.org/async/

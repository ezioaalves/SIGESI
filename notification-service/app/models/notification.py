"""Notification SQLAlchemy model."""
import enum
from datetime import datetime

from sqlalchemy import DateTime, Enum as SAEnum, Integer, String, Text
from sqlalchemy.dialects.postgresql import JSONB
from sqlalchemy.orm import Mapped, mapped_column

from app.database import Base


class NotificationStatus(str, enum.Enum):
    """Notification status enum."""

    PENDING = "pending"
    SENT = "sent"
    FAILED = "failed"
    READ = "read"


class NotificationType(str, enum.Enum):
    """Notification type enum."""

    NEW_DEMAND_ASSIGNED = "new_demand_assigned"
    DEMAND_STATUS_CHANGED = "demand_status_changed"


class NotificationChannel(str, enum.Enum):
    """Notification channel enum."""

    EMAIL = "email"


class Notification(Base):
    """Notification model for storing notification records."""

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

    # Reference to the entity that generated the notification
    entity_type: Mapped[str | None] = mapped_column(String(50))
    entity_id: Mapped[int | None] = mapped_column(Integer)

    # Extra event data
    event_metadata: Mapped[dict | None] = mapped_column(JSONB)

    retry_count: Mapped[int] = mapped_column(Integer, default=0)
    error_message: Mapped[str | None] = mapped_column(Text)

    read_at: Mapped[datetime | None] = mapped_column(DateTime)
    sent_at: Mapped[datetime | None] = mapped_column(DateTime)
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )

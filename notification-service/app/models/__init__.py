"""Models package - exports for Alembic detection."""
from app.models.notification import (
    Notification,
    NotificationChannel,
    NotificationStatus,
    NotificationType,
)
from app.models.preference import NotificationPreference

__all__ = [
    "Notification",
    "NotificationStatus",
    "NotificationType",
    "NotificationChannel",
    "NotificationPreference",
]

"""Pydantic schemas for notifications."""
from datetime import datetime
from enum import Enum

from pydantic import BaseModel, ConfigDict


class NotificationTypeEnum(str, Enum):
    """Notification type enum for API."""

    NEW_DEMAND_ASSIGNED = "new_demand_assigned"
    DEMAND_STATUS_CHANGED = "demand_status_changed"


class NotificationStatusEnum(str, Enum):
    """Notification status enum for API."""

    PENDING = "pending"
    SENT = "sent"
    FAILED = "failed"
    READ = "read"


class NotificationResponse(BaseModel):
    """Response schema for notification."""

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
    """Schema for marking notifications as read."""

    notification_ids: list[int]

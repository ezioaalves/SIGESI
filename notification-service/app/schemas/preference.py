"""Pydantic schemas for notification preferences."""
from pydantic import BaseModel, ConfigDict


class PreferenceCreate(BaseModel):
    """Schema for creating a notification preference."""

    notification_type: str
    channel: str = "email"
    enabled: bool = True


class PreferenceUpdate(BaseModel):
    """Schema for updating a notification preference."""

    enabled: bool


class PreferenceResponse(BaseModel):
    """Response schema for notification preference."""

    model_config = ConfigDict(from_attributes=True)

    id: int
    user_id: int
    notification_type: str
    channel: str
    enabled: bool

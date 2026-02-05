"""Notification API endpoints."""
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.dependencies import get_db
from app.models.notification import Notification
from app.schemas.notification import NotificationMarkRead, NotificationResponse
from app.services.notification_service import NotificationService

router = APIRouter(prefix="/api/notifications", tags=["Notifications"])
notification_service = NotificationService()


@router.get("/user/{user_id}", response_model=list[NotificationResponse])
async def get_user_notifications(
    user_id: int,
    skip: int = 0,
    limit: int = 50,
    db: AsyncSession = Depends(get_db),
):
    """
    Get all notifications for a user.

    Args:
        user_id: User ID
        skip: Number of records to skip (pagination)
        limit: Maximum number of records to return
        db: Database session

    Returns:
        List of notifications
    """
    return await notification_service.get_user_notifications(db, user_id, skip, limit)


@router.get("/{notification_id}", response_model=NotificationResponse)
async def get_notification(notification_id: int, db: AsyncSession = Depends(get_db)):
    """
    Get a specific notification by ID.

    Args:
        notification_id: Notification ID
        db: Database session

    Returns:
        Notification details

    Raises:
        HTTPException: If notification not found
    """
    result = await db.execute(
        select(Notification).where(Notification.id == notification_id)
    )
    notification = result.scalar_one_or_none()

    if not notification:
        raise HTTPException(status_code=404, detail="Notification not found")

    return NotificationResponse.model_validate(notification)


@router.post("/mark-read", status_code=204)
async def mark_notifications_as_read(
    data: NotificationMarkRead, db: AsyncSession = Depends(get_db)
):
    """
    Mark multiple notifications as read.

    Args:
        data: List of notification IDs to mark as read
        db: Database session
    """
    await notification_service.mark_as_read(db, data.notification_ids)


@router.get("/user/{user_id}/unread-count", response_model=int)
async def get_unread_count(user_id: int, db: AsyncSession = Depends(get_db)):
    """
    Get count of unread notifications for a user.

    Args:
        user_id: User ID
        db: Database session

    Returns:
        Count of unread notifications
    """
    return await notification_service.get_unread_count(db, user_id)

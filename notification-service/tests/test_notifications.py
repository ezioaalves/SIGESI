"""Tests for notification endpoints."""
import pytest
from httpx import AsyncClient
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.notification import Notification, NotificationStatus, NotificationType


@pytest.mark.asyncio
async def test_get_user_notifications(client: AsyncClient, db_session: AsyncSession):
    """Test getting user notifications."""
    # Create test notification
    notification = Notification(
        recipient_id=1,
        recipient_email="test@example.com",
        recipient_name="Test User",
        notification_type=NotificationType.NEW_DEMAND_ASSIGNED,
        status=NotificationStatus.SENT,
        title="Test Notification",
        message="Test message",
    )
    db_session.add(notification)
    await db_session.commit()

    # Get notifications
    response = await client.get("/api/notifications/user/1")
    assert response.status_code == 200
    data = response.json()
    assert len(data) == 1
    assert data[0]["recipient_id"] == 1
    assert data[0]["title"] == "Test Notification"


@pytest.mark.asyncio
async def test_get_notification_by_id(client: AsyncClient, db_session: AsyncSession):
    """Test getting a specific notification by ID."""
    # Create test notification
    notification = Notification(
        recipient_id=1,
        recipient_email="test@example.com",
        recipient_name="Test User",
        notification_type=NotificationType.NEW_DEMAND_ASSIGNED,
        status=NotificationStatus.SENT,
        title="Test Notification",
        message="Test message",
    )
    db_session.add(notification)
    await db_session.commit()
    await db_session.refresh(notification)

    # Get notification
    response = await client.get(f"/api/notifications/{notification.id}")
    assert response.status_code == 200
    data = response.json()
    assert data["id"] == notification.id
    assert data["title"] == "Test Notification"


@pytest.mark.asyncio
async def test_get_notification_not_found(client: AsyncClient):
    """Test getting a non-existent notification."""
    response = await client.get("/api/notifications/999")
    assert response.status_code == 404


@pytest.mark.asyncio
async def test_mark_notification_as_read(client: AsyncClient, db_session: AsyncSession):
    """Test marking notifications as read."""
    # Create test notifications
    notification1 = Notification(
        recipient_id=1,
        recipient_email="test@example.com",
        recipient_name="Test User",
        notification_type=NotificationType.NEW_DEMAND_ASSIGNED,
        status=NotificationStatus.SENT,
        title="Test 1",
        message="Message 1",
    )
    notification2 = Notification(
        recipient_id=1,
        recipient_email="test@example.com",
        recipient_name="Test User",
        notification_type=NotificationType.NEW_DEMAND_ASSIGNED,
        status=NotificationStatus.SENT,
        title="Test 2",
        message="Message 2",
    )
    db_session.add_all([notification1, notification2])
    await db_session.commit()
    await db_session.refresh(notification1)
    await db_session.refresh(notification2)

    # Mark as read
    response = await client.post(
        "/api/notifications/mark-read",
        json={"notification_ids": [notification1.id, notification2.id]},
    )
    assert response.status_code == 204

    # Verify they're marked as read
    await db_session.refresh(notification1)
    await db_session.refresh(notification2)
    assert notification1.read_at is not None
    assert notification2.read_at is not None


@pytest.mark.asyncio
async def test_unread_count(client: AsyncClient, db_session: AsyncSession):
    """Test getting unread notification count."""
    # Create test notifications (2 unread, 1 read)
    notification1 = Notification(
        recipient_id=1,
        recipient_email="test@example.com",
        recipient_name="Test User",
        notification_type=NotificationType.NEW_DEMAND_ASSIGNED,
        status=NotificationStatus.SENT,
        title="Test 1",
        message="Message 1",
    )
    notification2 = Notification(
        recipient_id=1,
        recipient_email="test@example.com",
        recipient_name="Test User",
        notification_type=NotificationType.NEW_DEMAND_ASSIGNED,
        status=NotificationStatus.SENT,
        title="Test 2",
        message="Message 2",
    )
    notification3 = Notification(
        recipient_id=1,
        recipient_email="test@example.com",
        recipient_name="Test User",
        notification_type=NotificationType.NEW_DEMAND_ASSIGNED,
        status=NotificationStatus.READ,
        title="Test 3",
        message="Message 3",
    )
    db_session.add_all([notification1, notification2, notification3])
    await db_session.commit()

    # Get unread count
    response = await client.get("/api/notifications/user/1/unread-count")
    assert response.status_code == 200
    assert response.json() == 2

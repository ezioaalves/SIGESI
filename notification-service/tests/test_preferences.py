"""Tests for notification preference endpoints."""
import pytest
from httpx import AsyncClient
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.preference import NotificationPreference


@pytest.mark.asyncio
async def test_get_user_preferences_empty(client: AsyncClient):
    """Test getting preferences for user with no preferences."""
    response = await client.get("/api/preferences/user/1")
    assert response.status_code == 200
    assert response.json() == []


@pytest.mark.asyncio
async def test_get_user_preferences(client: AsyncClient, db_session: AsyncSession):
    """Test getting user preferences."""
    # Create test preferences
    pref1 = NotificationPreference(
        user_id=1,
        notification_type="new_demand_assigned",
        channel="email",
        enabled=True,
    )
    pref2 = NotificationPreference(
        user_id=1,
        notification_type="demand_status_changed",
        channel="email",
        enabled=False,
    )
    db_session.add_all([pref1, pref2])
    await db_session.commit()

    # Get preferences
    response = await client.get("/api/preferences/user/1")
    assert response.status_code == 200
    data = response.json()
    assert len(data) == 2


@pytest.mark.asyncio
async def test_create_preference(client: AsyncClient, db_session: AsyncSession):
    """Test creating a new preference."""
    response = await client.put(
        "/api/preferences/user/1",
        json={
            "notification_type": "new_demand_assigned",
            "channel": "email",
            "enabled": True,
        },
    )
    assert response.status_code == 200
    data = response.json()
    assert data["user_id"] == 1
    assert data["notification_type"] == "new_demand_assigned"
    assert data["enabled"] is True


@pytest.mark.asyncio
async def test_update_preference(client: AsyncClient, db_session: AsyncSession):
    """Test updating an existing preference."""
    # Create initial preference
    pref = NotificationPreference(
        user_id=1,
        notification_type="new_demand_assigned",
        channel="email",
        enabled=True,
    )
    db_session.add(pref)
    await db_session.commit()

    # Update preference
    response = await client.put(
        "/api/preferences/user/1",
        json={
            "notification_type": "new_demand_assigned",
            "channel": "email",
            "enabled": False,
        },
    )
    assert response.status_code == 200
    data = response.json()
    assert data["enabled"] is False


@pytest.mark.asyncio
async def test_delete_preference(client: AsyncClient, db_session: AsyncSession):
    """Test deleting a preference."""
    # Create preference
    pref = NotificationPreference(
        user_id=1,
        notification_type="new_demand_assigned",
        channel="email",
        enabled=True,
    )
    db_session.add(pref)
    await db_session.commit()
    await db_session.refresh(pref)

    # Delete preference
    response = await client.delete(f"/api/preferences/{pref.id}")
    assert response.status_code == 204

    # Verify deletion
    response = await client.get("/api/preferences/user/1")
    assert response.status_code == 200
    assert len(response.json()) == 0


@pytest.mark.asyncio
async def test_delete_preference_not_found(client: AsyncClient):
    """Test deleting a non-existent preference."""
    response = await client.delete("/api/preferences/999")
    assert response.status_code == 404


@pytest.mark.asyncio
async def test_default_preferences_enabled(db_session: AsyncSession):
    """Test that notifications are enabled by default when no preference exists."""
    from app.services.preference_service import PreferenceService

    service = PreferenceService()

    # Check enabled status without creating preference
    is_enabled = await service.is_notification_enabled(
        db_session, user_id=1, notification_type="new_demand_assigned", channel="email"
    )
    assert is_enabled is True

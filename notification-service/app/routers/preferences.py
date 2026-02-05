"""Notification preference API endpoints."""
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from app.dependencies import get_db
from app.schemas.preference import PreferenceCreate, PreferenceResponse, PreferenceUpdate
from app.services.preference_service import PreferenceService

router = APIRouter(prefix="/api/preferences", tags=["Preferences"])
preference_service = PreferenceService()


@router.get("/user/{user_id}", response_model=list[PreferenceResponse])
async def get_user_preferences(user_id: int, db: AsyncSession = Depends(get_db)):
    """
    Get all notification preferences for a user.

    Args:
        user_id: User ID
        db: Database session

    Returns:
        List of user preferences
    """
    return await preference_service.get_user_preferences(db, user_id)


@router.put("/user/{user_id}", response_model=PreferenceResponse)
async def upsert_preference(
    user_id: int, preference: PreferenceCreate, db: AsyncSession = Depends(get_db)
):
    """
    Create or update a notification preference for a user.

    Args:
        user_id: User ID
        preference: Preference data
        db: Database session

    Returns:
        Created or updated preference
    """
    return await preference_service.upsert_preference(db, user_id, preference)


@router.delete("/{preference_id}", status_code=204)
async def delete_preference(preference_id: int, db: AsyncSession = Depends(get_db)):
    """
    Delete a notification preference.

    Args:
        preference_id: Preference ID
        db: Database session

    Raises:
        HTTPException: If preference not found
    """
    deleted = await preference_service.delete_preference(db, preference_id)
    if not deleted:
        raise HTTPException(status_code=404, detail="Preference not found")

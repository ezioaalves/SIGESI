"""Service for managing notification preferences."""
import logging

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.preference import NotificationPreference
from app.schemas.preference import PreferenceCreate, PreferenceResponse

logger = logging.getLogger(__name__)


class PreferenceService:
    """Service for managing user notification preferences."""

    async def get_user_preferences(
        self, db: AsyncSession, user_id: int
    ) -> list[PreferenceResponse]:
        """
        Get all notification preferences for a user.

        Args:
            db: Database session
            user_id: User ID

        Returns:
            List of user preferences
        """
        result = await db.execute(
            select(NotificationPreference).where(NotificationPreference.user_id == user_id)
        )
        preferences = result.scalars().all()
        return [PreferenceResponse.model_validate(pref) for pref in preferences]

    async def is_notification_enabled(
        self, db: AsyncSession, user_id: int, notification_type: str, channel: str = "email"
    ) -> bool:
        """
        Check if a notification type is enabled for a user.

        Args:
            db: Database session
            user_id: User ID
            notification_type: Type of notification
            channel: Notification channel (default: "email")

        Returns:
            True if notification is enabled, False otherwise
            Default is True if no preference is set
        """
        result = await db.execute(
            select(NotificationPreference).where(
                NotificationPreference.user_id == user_id,
                NotificationPreference.notification_type == notification_type,
                NotificationPreference.channel == channel,
            )
        )
        preference = result.scalar_one_or_none()

        # If no preference is set, default to enabled
        if preference is None:
            return True

        return preference.enabled

    async def upsert_preference(
        self, db: AsyncSession, user_id: int, preference_data: PreferenceCreate
    ) -> PreferenceResponse:
        """
        Create or update a notification preference.

        Args:
            db: Database session
            user_id: User ID
            preference_data: Preference data

        Returns:
            Created or updated preference
        """
        # Check if preference exists
        result = await db.execute(
            select(NotificationPreference).where(
                NotificationPreference.user_id == user_id,
                NotificationPreference.notification_type == preference_data.notification_type,
                NotificationPreference.channel == preference_data.channel,
            )
        )
        preference = result.scalar_one_or_none()

        if preference:
            # Update existing
            preference.enabled = preference_data.enabled
        else:
            # Create new
            preference = NotificationPreference(
                user_id=user_id,
                notification_type=preference_data.notification_type,
                channel=preference_data.channel,
                enabled=preference_data.enabled,
            )
            db.add(preference)

        await db.commit()
        await db.refresh(preference)

        return PreferenceResponse.model_validate(preference)

    async def delete_preference(self, db: AsyncSession, preference_id: int) -> bool:
        """
        Delete a notification preference.

        Args:
            db: Database session
            preference_id: Preference ID

        Returns:
            True if deleted, False if not found
        """
        result = await db.execute(
            select(NotificationPreference).where(NotificationPreference.id == preference_id)
        )
        preference = result.scalar_one_or_none()

        if not preference:
            return False

        await db.delete(preference)
        await db.commit()
        return True

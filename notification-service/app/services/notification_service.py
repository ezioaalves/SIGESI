"""Main notification service for handling notification logic."""
import logging
from datetime import datetime

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import async_session_factory
from app.models.notification import Notification, NotificationStatus, NotificationType
from app.schemas.events import DemandEvent
from app.schemas.notification import NotificationResponse
from app.services.email_service import EmailService
from app.services.preference_service import PreferenceService

logger = logging.getLogger(__name__)


class NotificationService:
    """Service for handling notification business logic."""

    def __init__(self):
        """Initialize notification service with dependencies."""
        self.email_service = EmailService()
        self.preference_service = PreferenceService()

    async def handle_demand_assigned(self, event: DemandEvent):
        """
        Handle demand assigned event.

        Args:
            event: Demand event data
        """
        async with async_session_factory() as db:
            try:
                # Check user preferences
                is_enabled = await self.preference_service.is_notification_enabled(
                    db,
                    user_id=event.responsavel_id,
                    notification_type=NotificationType.NEW_DEMAND_ASSIGNED.value,
                    channel="email",
                )

                if not is_enabled:
                    logger.info(
                        f"Notification disabled for user {event.responsavel_id}, "
                        f"type {NotificationType.NEW_DEMAND_ASSIGNED.value}"
                    )
                    return

                # Create notification record
                notification = await self._create_notification(
                    db,
                    {
                        "recipient_id": event.responsavel_id,
                        "recipient_email": event.responsavel_email,
                        "recipient_name": event.responsavel_nome,
                        "notification_type": NotificationType.NEW_DEMAND_ASSIGNED,
                        "title": f"Nova demanda atribuída: #{event.demand_id}",
                        "message": f"Você recebeu uma nova demanda relacionada a {event.assunto}",
                        "entity_type": "DEMANDA",
                        "entity_id": event.demand_id,
                        "event_metadata": event.model_dump(),
                    },
                )

                # Send email
                success = await self._send_notification(notification, event)

                # Update status
                if success:
                    notification.status = NotificationStatus.SENT
                    notification.sent_at = datetime.utcnow()
                else:
                    notification.status = NotificationStatus.FAILED
                    notification.retry_count += 1

                await db.commit()

            except Exception as e:
                logger.error(f"Error handling demand assigned event: {str(e)}")
                await db.rollback()

    async def handle_demand_status_changed(self, event: DemandEvent):
        """
        Handle demand status changed event.

        Args:
            event: Demand event data
        """
        async with async_session_factory() as db:
            try:
                # Check user preferences
                is_enabled = await self.preference_service.is_notification_enabled(
                    db,
                    user_id=event.responsavel_id,
                    notification_type=NotificationType.DEMAND_STATUS_CHANGED.value,
                    channel="email",
                )

                if not is_enabled:
                    logger.info(
                        f"Notification disabled for user {event.responsavel_id}, "
                        f"type {NotificationType.DEMAND_STATUS_CHANGED.value}"
                    )
                    return

                # Create notification record
                notification = await self._create_notification(
                    db,
                    {
                        "recipient_id": event.responsavel_id,
                        "recipient_email": event.responsavel_email,
                        "recipient_name": event.responsavel_nome,
                        "notification_type": NotificationType.DEMAND_STATUS_CHANGED,
                        "title": f"Status alterado: Demanda #{event.demand_id}",
                        "message": f"Status mudou de {event.old_status} para {event.new_status}",
                        "entity_type": "DEMANDA",
                        "entity_id": event.demand_id,
                        "event_metadata": event.model_dump(),
                    },
                )

                # Send email
                success = await self._send_notification(notification, event)

                # Update status
                if success:
                    notification.status = NotificationStatus.SENT
                    notification.sent_at = datetime.utcnow()
                else:
                    notification.status = NotificationStatus.FAILED
                    notification.retry_count += 1

                await db.commit()

            except Exception as e:
                logger.error(f"Error handling demand status changed event: {str(e)}")
                await db.rollback()

    async def get_user_notifications(
        self, db: AsyncSession, user_id: int, skip: int = 0, limit: int = 50
    ) -> list[NotificationResponse]:
        """
        Get notifications for a user.

        Args:
            db: Database session
            user_id: User ID
            skip: Number of records to skip
            limit: Maximum number of records to return

        Returns:
            List of notifications
        """
        result = await db.execute(
            select(Notification)
            .where(Notification.recipient_id == user_id)
            .order_by(Notification.created_at.desc())
            .offset(skip)
            .limit(limit)
        )
        notifications = result.scalars().all()
        return [NotificationResponse.model_validate(notif) for notif in notifications]

    async def get_unread_count(self, db: AsyncSession, user_id: int) -> int:
        """
        Get count of unread notifications for a user.

        Args:
            db: Database session
            user_id: User ID

        Returns:
            Count of unread notifications
        """
        result = await db.execute(
            select(Notification).where(
                Notification.recipient_id == user_id, Notification.read_at.is_(None)
            )
        )
        return len(result.scalars().all())

    async def mark_as_read(self, db: AsyncSession, notification_ids: list[int]) -> None:
        """
        Mark notifications as read.

        Args:
            db: Database session
            notification_ids: List of notification IDs
        """
        result = await db.execute(
            select(Notification).where(Notification.id.in_(notification_ids))
        )
        notifications = result.scalars().all()

        for notification in notifications:
            if notification.read_at is None:
                notification.read_at = datetime.utcnow()
                notification.status = NotificationStatus.READ

        await db.commit()

    async def _create_notification(self, db: AsyncSession, data: dict) -> Notification:
        """
        Create a notification record in the database.

        Args:
            db: Database session
            data: Notification data

        Returns:
            Created notification
        """
        notification = Notification(**data)
        db.add(notification)
        await db.flush()
        return notification

    async def _send_notification(
        self, notification: Notification, event: DemandEvent
    ) -> bool:
        """
        Send notification via email.

        Args:
            notification: Notification record
            event: Event data for template context

        Returns:
            True if sent successfully, False otherwise
        """
        try:
            if notification.notification_type == NotificationType.NEW_DEMAND_ASSIGNED:
                template_name = "demand_assigned.html"
                context = {
                    "recipient_name": notification.recipient_name,
                    "demand_id": event.demand_id,
                    "assunto": event.assunto,
                    "prazo": event.prazo.strftime("%d/%m/%Y") if event.prazo else None,
                }
            elif notification.notification_type == NotificationType.DEMAND_STATUS_CHANGED:
                template_name = "demand_status_changed.html"
                context = {
                    "recipient_name": notification.recipient_name,
                    "demand_id": event.demand_id,
                    "old_status": event.old_status,
                    "new_status": event.new_status,
                }
            else:
                logger.error(f"Unknown notification type: {notification.notification_type}")
                return False

            return await self.email_service.send_email(
                to_email=notification.recipient_email,
                subject=notification.title,
                template_name=template_name,
                context=context,
            )

        except Exception as e:
            logger.error(f"Error sending notification: {str(e)}")
            notification.error_message = str(e)
            return False

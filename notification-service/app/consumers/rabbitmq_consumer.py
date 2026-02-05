"""RabbitMQ consumer for processing notification events."""
import asyncio
import json
import logging

from aio_pika import IncomingMessage, connect_robust
from aio_pika.abc import AbstractIncomingMessage

from app.config import settings
from app.schemas.events import DemandEvent
from app.services.notification_service import NotificationService

logger = logging.getLogger(__name__)


class RabbitMQConsumer:
    """Consumer for processing events from RabbitMQ."""

    def __init__(self):
        """Initialize consumer with notification service."""
        self.notification_service = NotificationService()
        self.connection = None
        self.channel = None

    async def start_consuming(self):
        """Start consuming messages from RabbitMQ."""
        try:
            # Connect with automatic reconnection
            self.connection = await connect_robust(settings.rabbitmq_url)
            self.channel = await self.connection.channel()

            # Set QoS - process 10 messages at a time
            await self.channel.set_qos(prefetch_count=10)

            # Declare exchange (topic type for routing keys)
            exchange = await self.channel.declare_exchange(
                settings.rabbitmq_exchange, type="topic", durable=True
            )

            # Declare queue
            queue = await self.channel.declare_queue(
                settings.rabbitmq_queue, durable=True
            )

            # Bind queue to exchange with routing keys
            await queue.bind(exchange, routing_key="demand.assigned")
            await queue.bind(exchange, routing_key="demand.status_changed")

            logger.info(
                f"Started consuming from queue '{settings.rabbitmq_queue}' "
                f"on exchange '{settings.rabbitmq_exchange}'"
            )

            # Start consuming messages
            await queue.consume(self.process_message)

            # Keep consumer running
            await asyncio.Future()

        except asyncio.CancelledError:
            logger.info("Consumer cancelled, shutting down...")
            await self.stop_consuming()
        except Exception as e:
            logger.error(f"Error in consumer: {str(e)}")
            raise

    async def stop_consuming(self):
        """Stop consuming and close connections."""
        if self.channel and not self.channel.is_closed:
            await self.channel.close()
        if self.connection and not self.connection.is_closed:
            await self.connection.close()
        logger.info("Consumer stopped")

    async def process_message(self, message: AbstractIncomingMessage):
        """
        Process incoming RabbitMQ message.

        Args:
            message: Incoming message from RabbitMQ
        """
        async with message.process():
            try:
                # Parse message body
                body = json.loads(message.body.decode())
                logger.info(
                    f"Received message with routing key: {message.routing_key}"
                )

                # Parse event
                event = DemandEvent(**body)

                # Route to appropriate handler
                if message.routing_key == "demand.assigned":
                    await self.notification_service.handle_demand_assigned(event)
                elif message.routing_key == "demand.status_changed":
                    await self.notification_service.handle_demand_status_changed(event)
                else:
                    logger.warning(f"Unknown routing key: {message.routing_key}")

                logger.info(f"Successfully processed message for demand {event.demand_id}")

            except Exception as e:
                logger.error(f"Error processing message: {str(e)}")
                # Message will be rejected and not requeued due to process() context
                raise

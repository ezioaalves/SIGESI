"""FastAPI main application with lifespan events."""
import asyncio
import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI

from app.consumers.rabbitmq_consumer import RabbitMQConsumer
from app.routers import health, notifications, preferences

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
)

logger = logging.getLogger(__name__)

# Global consumer task
consumer_task = None


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Lifespan context manager for startup and shutdown events.

    Args:
        app: FastAPI application instance
    """
    global consumer_task

    # Startup
    logger.info("Starting notification service...")

    # Start RabbitMQ consumer in background
    consumer = RabbitMQConsumer()
    consumer_task = asyncio.create_task(consumer.start_consuming())
    logger.info("RabbitMQ consumer started")

    yield

    # Shutdown
    logger.info("Shutting down notification service...")
    if consumer_task:
        consumer_task.cancel()
        try:
            await consumer_task
        except asyncio.CancelledError:
            pass
    logger.info("Notification service stopped")


# Create FastAPI app
app = FastAPI(
    title="SIGESI Notification Service",
    description="Microservice for handling notifications via email",
    version="0.1.0",
    lifespan=lifespan,
)

# Include routers
app.include_router(health.router)
app.include_router(notifications.router)
app.include_router(preferences.router)


@app.get("/")
async def root():
    """Root endpoint."""
    return {
        "service": "notification-service",
        "version": "0.1.0",
        "status": "running",
    }

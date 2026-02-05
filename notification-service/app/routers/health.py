"""Health check endpoint."""
from datetime import datetime

from fastapi import APIRouter

router = APIRouter(tags=["Health"])


@router.get("/health")
async def health_check():
    """
    Health check endpoint.

    Returns:
        Health status with timestamp
    """
    return {
        "status": "healthy",
        "service": "notification-service",
        "timestamp": datetime.utcnow().isoformat(),
    }

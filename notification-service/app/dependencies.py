"""FastAPI dependency injection utilities."""
from typing import AsyncGenerator

from sqlalchemy.ext.asyncio import AsyncSession

from app.database import async_session_factory


async def get_db() -> AsyncGenerator[AsyncSession, None]:
    """
    Dependency that provides a database session.

    Yields:
        AsyncSession: Database session
    """
    async with async_session_factory() as session:
        try:
            yield session
        finally:
            await session.close()

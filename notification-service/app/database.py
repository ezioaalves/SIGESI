"""Async SQLAlchemy database configuration."""
from sqlalchemy.ext.asyncio import AsyncSession, async_sessionmaker, create_async_engine
from sqlalchemy.orm import DeclarativeBase

from app.config import settings


class Base(DeclarativeBase):
    """Base class for all SQLAlchemy models."""

    pass


# Create async engine
engine = create_async_engine(
    settings.database_url,
    echo=True,  # Set to False in production
    pool_pre_ping=True,
    pool_size=10,
    max_overflow=20,
)

# Create async session factory
# CRITICAL: expire_on_commit=False is required for async sessions
async_session_factory = async_sessionmaker(
    engine,
    class_=AsyncSession,
    expire_on_commit=False,  # Prevents DetachedInstanceError
    autoflush=False,
    autocommit=False,
)


async def get_db() -> AsyncSession:
    """Dependency to get database session."""
    async with async_session_factory() as session:
        yield session


async def init_db():
    """Initialize database tables."""
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)

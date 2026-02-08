"""Local development settings for SIGESI."""

from .base import *  # noqa: F401, F403

DEBUG = True

ALLOWED_HOSTS = ["*"]

# CSRF settings for cross-origin development
CSRF_TRUSTED_ORIGINS = ["http://localhost:3000"]

# Session settings for development
SESSION_COOKIE_SAMESITE = "Lax"
SESSION_COOKIE_HTTPONLY = True
SESSION_COOKIE_SECURE = False

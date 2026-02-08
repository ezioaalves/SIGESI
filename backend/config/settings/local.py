"""Local development settings for SIGESI."""

from .base import *  # noqa: F401, F403

DEBUG = True

ALLOWED_HOSTS = ["*"]

SESSION_COOKIE_SECURE = False

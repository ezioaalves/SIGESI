"""Signals for the Usuarios app."""

import logging

from django.conf import settings
from django.contrib.auth import get_user_model
from django.db import OperationalError, ProgrammingError
from django.db.models.signals import post_migrate
from django.dispatch import receiver

logger = logging.getLogger(__name__)


@receiver(post_migrate)
def create_admin_user(sender, **kwargs):
    """Auto-create admin user from ADMIN_EMAIL after migrations."""
    admin_email = getattr(settings, "ADMIN_EMAIL", "")
    if not admin_email:
        return

    try:
        user_model = get_user_model()
        user, created = user_model.objects.get_or_create(
            email=admin_email,
            defaults={
                "username": admin_email,
                "role": "ADMIN",
                "ativo": True,
                "is_staff": True,
                "is_superuser": True,
                "provider": "system",
            },
        )

        if created:
            logger.info("Admin user created: %s", admin_email)
        elif user.role != "ADMIN":
            user.role = "ADMIN"
            user.is_staff = True
            user.is_superuser = True
            user.save(update_fields=["role", "is_staff", "is_superuser"])
            logger.info("Existing user promoted to admin: %s", admin_email)
    except (OperationalError, ProgrammingError):
        # Table doesn't exist yet during initial migration
        pass

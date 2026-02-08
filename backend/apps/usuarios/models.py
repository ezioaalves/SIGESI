"""Usuarios models."""

from django.contrib.auth.models import AbstractUser


class Usuario(AbstractUser):
    """Custom user model for SIGESI. Fields will be expanded in Phase 2."""

    class Meta:
        """Meta options for Usuario."""

        verbose_name = "Usuario"
        verbose_name_plural = "Usuarios"

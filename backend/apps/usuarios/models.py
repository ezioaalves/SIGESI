"""Usuarios models."""

from django.contrib.auth.models import AbstractUser
from django.db import models


class Usuario(AbstractUser):
    """Custom user model for SIGESI with OAuth2 and role support."""

    class Role(models.TextChoices):
        """User roles for access control."""

        CIDADAO = "CIDADAO", "Cidadao"
        OPERADOR = "OPERADOR", "Operador"
        AGENTE = "AGENTE", "Agente"
        ADMIN = "ADMIN", "Admin"

    picture_url = models.URLField(max_length=500, blank=True, default="")
    provider = models.CharField(max_length=50, blank=True, default="")
    ativo = models.BooleanField(default=True)
    role = models.CharField(max_length=20, choices=Role, default=Role.CIDADAO)

    class Meta:
        """Meta options for Usuario."""

        verbose_name = "usuario"
        verbose_name_plural = "usuarios"

    def __str__(self):
        """Return user email as string representation."""
        return self.email

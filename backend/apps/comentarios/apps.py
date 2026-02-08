"""Comentarios app configuration."""

from django.apps import AppConfig


class ComentariosConfig(AppConfig):
    """Configuration for the Comentarios app."""

    default_auto_field = "django.db.models.BigAutoField"
    name = "apps.comentarios"
    verbose_name = "Comentarios"

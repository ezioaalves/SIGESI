"""Enderecos app configuration."""

from django.apps import AppConfig


class EnderecosConfig(AppConfig):
    """Configuration for the Enderecos app."""

    default_auto_field = "django.db.models.BigAutoField"
    name = "apps.enderecos"
    verbose_name = "Enderecos"

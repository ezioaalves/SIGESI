"""Gavetas app configuration."""

from django.apps import AppConfig


class GavetasConfig(AppConfig):
    """Configuration for the Gavetas app."""

    default_auto_field = "django.db.models.BigAutoField"
    name = "apps.gavetas"
    verbose_name = "Gavetas"

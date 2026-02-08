"""Pessoas app configuration."""

from django.apps import AppConfig


class PessoasConfig(AppConfig):
    """Configuration for the Pessoas app."""

    default_auto_field = "django.db.models.BigAutoField"
    name = "apps.pessoas"
    verbose_name = "Pessoas"

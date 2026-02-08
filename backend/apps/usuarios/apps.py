"""Usuarios app configuration."""

from django.apps import AppConfig


class UsuariosConfig(AppConfig):
    """Configuration for the Usuarios app."""

    default_auto_field = "django.db.models.BigAutoField"
    name = "apps.usuarios"
    verbose_name = "Usuarios"

    def ready(self):
        """Load signals when the app is ready."""
        import apps.usuarios.signals  # noqa: F401

"""Comentarios models."""

from django.conf import settings
from django.db import models


class Comentario(models.Model):
    """Comment on a work demand."""

    demanda = models.ForeignKey(
        "demandas.Demanda",
        on_delete=models.CASCADE,
        related_name="comentarios",
    )
    autor = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.PROTECT,
        related_name="comentarios",
    )
    texto = models.TextField()
    criado_em = models.DateTimeField(auto_now_add=True)

    class Meta:
        """Meta options for Comentario."""

        verbose_name = "comentario"
        verbose_name_plural = "comentarios"
        ordering = ["-criado_em"]

    def __str__(self):
        """Return comment summary."""
        return f"Comentario #{self.pk} por {self.autor}"

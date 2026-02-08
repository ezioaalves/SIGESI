"""Gavetas models."""

from django.db import models


class Gaveta(models.Model):
    """Burial space entity within a jazigo."""

    jazigo = models.ForeignKey(
        "jazigos.Jazigo",
        on_delete=models.CASCADE,
        related_name="gavetas",
    )
    numero = models.IntegerField(null=True, blank=True)
    ocupante = models.ForeignKey(
        "pessoas.Pessoa",
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name="gavetas",
    )

    class Meta:
        """Meta options for Gaveta."""

        verbose_name = "gaveta"
        verbose_name_plural = "gavetas"
        ordering = ["numero"]

    def __str__(self):
        """Return gaveta identifier."""
        return f"Gaveta {self.numero or 'S/N'}"

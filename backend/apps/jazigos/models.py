"""Jazigos models."""

from django.db import models


class Jazigo(models.Model):
    """Burial plot entity located within a cemetery."""

    cemiterio = models.ForeignKey(
        "cemiterios.Cemiterio",
        on_delete=models.CASCADE,
        related_name="jazigos",
    )
    largura = models.FloatField(null=True, blank=True)
    comprimento = models.FloatField(null=True, blank=True)
    quadra = models.IntegerField()
    rua = models.CharField(max_length=50)
    lote = models.CharField(max_length=50)

    class Meta:
        """Meta options for Jazigo."""

        verbose_name = "jazigo"
        verbose_name_plural = "jazigos"
        ordering = ["quadra", "rua", "lote"]

    def __str__(self):
        """Return jazigo location identifier."""
        return f"Quadra {self.quadra}, Rua {self.rua}, Lote {self.lote}"

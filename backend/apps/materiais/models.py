"""Materiais models."""

from django.db import models


class Material(models.Model):
    """Materials catalog with name and price."""

    nome = models.CharField(max_length=255)
    preco = models.DecimalField(max_digits=10, decimal_places=2)

    class Meta:
        """Meta options for Material."""

        verbose_name = "material"
        verbose_name_plural = "materiais"
        ordering = ["nome"]

    def __str__(self):
        """Return material name."""
        return self.nome

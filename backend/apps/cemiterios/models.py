"""Cemiterios models."""

from django.db import models


class Cemiterio(models.Model):
    """Cemetery entity with a location address."""

    nome = models.CharField(max_length=255)
    endereco = models.OneToOneField(
        "enderecos.Endereco",
        on_delete=models.CASCADE,
        related_name="cemiterio",
    )

    class Meta:
        """Meta options for Cemiterio."""

        verbose_name = "cemiterio"
        verbose_name_plural = "cemiterios"
        ordering = ["nome"]

    def __str__(self):
        """Return cemetery name."""
        return self.nome

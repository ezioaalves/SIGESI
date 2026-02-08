"""Enderecos models."""

from django.db import models


class Endereco(models.Model):
    """Address entity used by solicitacoes, cemiterios, and pessoas."""

    logradouro = models.CharField(max_length=255)
    numero = models.CharField(max_length=20)
    bairro = models.CharField(max_length=255)
    referencia = models.CharField(max_length=255, blank=True, default="")

    class Meta:
        """Meta options for Endereco."""

        verbose_name = "endereco"
        verbose_name_plural = "enderecos"

    def __str__(self):
        """Return formatted address string."""
        return f"{self.logradouro}, {self.numero} - {self.bairro}"

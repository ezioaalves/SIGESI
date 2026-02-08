"""Pessoas models."""

from django.db import models


class SexoEnum(models.TextChoices):
    """Gender options for persons."""

    MASCULINO = "MASCULINO", "Masculino"
    FEMININO = "FEMININO", "Feminino"
    OUTRO = "OUTRO", "Outro"


class Pessoa(models.Model):
    """Person entity with personal information."""

    nome = models.CharField(max_length=255)
    cpf = models.CharField(max_length=14, unique=True)
    sexo = models.CharField(max_length=10, choices=SexoEnum)
    endereco = models.ForeignKey(
        "enderecos.Endereco",
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name="pessoas",
    )

    class Meta:
        """Meta options for Pessoa."""

        verbose_name = "pessoa"
        verbose_name_plural = "pessoas"
        ordering = ["nome"]

    def __str__(self):
        """Return person name."""
        return self.nome

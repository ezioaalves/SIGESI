"""Demandas models."""

from django.conf import settings
from django.db import models


class DemandaStatus(models.TextChoices):
    """Status options for work demands."""

    PENDENTE = "PENDENTE", "Pendente"
    EM_ANDAMENTO = "EM_ANDAMENTO", "Em Andamento"
    CONCLUIDA = "CONCLUIDA", "Concluida"
    CANCELADA = "CANCELADA", "Cancelada"


class Demanda(models.Model):
    """Work demand entity derived from service requests."""

    solicitacao = models.ForeignKey(
        "solicitacoes.Solicitacao",
        on_delete=models.CASCADE,
        related_name="demandas",
    )
    responsavel = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.PROTECT,
        related_name="demandas",
        null=True,
        blank=True,
    )
    prazo = models.DateField(null=True, blank=True)
    status = models.CharField(
        max_length=20,
        choices=DemandaStatus,
        default=DemandaStatus.PENDENTE,
    )
    materiais = models.ManyToManyField(
        "materiais.Material",
        through="DemandaMaterial",
        blank=True,
        related_name="demandas",
    )

    class Meta:
        """Meta options for Demanda."""

        verbose_name = "demanda"
        verbose_name_plural = "demandas"

    def __str__(self):
        """Return demanda summary."""
        return f"Demanda #{self.pk} - {self.status}"


class DemandaMaterial(models.Model):
    """Join entity linking demands to materials with quantity."""

    demanda = models.ForeignKey(
        Demanda,
        on_delete=models.CASCADE,
        related_name="demanda_materiais",
    )
    material = models.ForeignKey(
        "materiais.Material",
        on_delete=models.PROTECT,
        related_name="demanda_materiais",
    )
    quantidade = models.PositiveIntegerField()

    class Meta:
        """Meta options for DemandaMaterial."""

        verbose_name = "demanda material"
        verbose_name_plural = "demanda materiais"
        constraints = [
            models.UniqueConstraint(
                fields=["demanda", "material"],
                name="unique_demanda_material",
            )
        ]

    def __str__(self):
        """Return demanda-material summary."""
        return f"{self.material.nome} x{self.quantidade}"

"""Solicitacoes models."""

from django.conf import settings
from django.db import models


class SolicitacaoAssunto(models.TextChoices):
    """Subject categories for service requests."""

    BURACO = "BURACO", "Buraco"
    ESGOTO = "ESGOTO", "Esgoto"
    ILUMINACAO = "ILUMINACAO", "Iluminacao"
    LIMPEZA = "LIMPEZA", "Limpeza"
    OUTROS = "OUTROS", "Outros"


class SolicitacaoStatus(models.TextChoices):
    """Status options for service requests."""

    ABERTA = "ABERTA", "Aberta"
    EM_ANDAMENTO = "EM_ANDAMENTO", "Em Andamento"
    CONCLUIDA = "CONCLUIDA", "Concluida"
    ENCERRADA = "ENCERRADA", "Encerrada"
    REJEITADA = "REJEITADA", "Rejeitada"


class Solicitacao(models.Model):
    """Service request entity - citizen-reported issues."""

    data = models.DateField(auto_now_add=True)
    assunto = models.CharField(max_length=20, choices=SolicitacaoAssunto)
    body = models.TextField()
    autor = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.PROTECT,
        related_name="solicitacoes",
    )
    local = models.ForeignKey(
        "enderecos.Endereco",
        on_delete=models.PROTECT,
        related_name="solicitacoes",
    )
    status = models.CharField(
        max_length=20,
        choices=SolicitacaoStatus,
        default=SolicitacaoStatus.ABERTA,
    )
    anexos = models.ManyToManyField("arquivos.Arquivo", blank=True, related_name="solicitacoes")

    class Meta:
        """Meta options for Solicitacao."""

        verbose_name = "solicitacao"
        verbose_name_plural = "solicitacoes"
        ordering = ["-data"]

    def __str__(self):
        """Return solicitacao summary."""
        return f"Solicitacao #{self.pk} - {self.assunto}"

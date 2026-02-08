"""Documentos models."""

from django.db import models


class DocumentoTipo(models.TextChoices):
    """Types of official documents."""

    OFICIO = "OFICIO", "Oficio"
    MEMORANDO = "MEMORANDO", "Memorando"


class Documento(models.Model):
    """Official document entity (oficios, memorandos)."""

    numero = models.CharField(max_length=50, blank=True, default="")
    data = models.DateField(auto_now_add=True)
    subject = models.CharField(max_length=255)
    honorifico = models.CharField(max_length=255, blank=True, default="")
    body = models.TextField()
    tipo = models.CharField(max_length=20, choices=DocumentoTipo)
    portaria = models.CharField(max_length=255, blank=True, default="")
    assinante = models.CharField(max_length=255)
    interessado = models.CharField(max_length=255)
    destino = models.CharField(max_length=255, blank=True, default="")
    anexos = models.ManyToManyField("arquivos.Arquivo", blank=True, related_name="documentos")

    class Meta:
        """Meta options for Documento."""

        verbose_name = "documento"
        verbose_name_plural = "documentos"
        ordering = ["-data"]

    def __str__(self):
        """Return document summary."""
        return f"{self.tipo} {self.numero} - {self.subject}"

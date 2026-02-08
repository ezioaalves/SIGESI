"""Arquivos models."""

from django.db import models


class Arquivo(models.Model):
    """File metadata entity stored with MinIO."""

    nome_original = models.CharField(max_length=255)
    storage_key = models.CharField(max_length=500, unique=True)
    content_type = models.CharField(max_length=100)
    tamanho = models.BigIntegerField()
    categoria = models.CharField(max_length=100, blank=True, default="")
    uploaded_at = models.DateTimeField(auto_now_add=True)
    ativo = models.BooleanField(default=True)

    class Meta:
        """Meta options for Arquivo."""

        verbose_name = "arquivo"
        verbose_name_plural = "arquivos"
        ordering = ["-uploaded_at"]

    def __str__(self):
        """Return original filename."""
        return self.nome_original

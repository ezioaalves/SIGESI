"""Arquivo serializers for response DTOs."""

from rest_framework import serializers

from apps.arquivos.models import Arquivo


class ArquivoResponseSerializer(serializers.ModelSerializer):
    """Serializer for Arquivo responses."""

    class Meta:
        """Meta options."""

        model = Arquivo
        fields = [
            "id",
            "nome_original",
            "storage_key",
            "content_type",
            "tamanho",
            "categoria",
            "uploaded_at",
            "ativo",
        ]
        read_only_fields = fields


class FileUrlResponseSerializer(serializers.Serializer):
    """Serializer for presigned URL responses."""

    file_id = serializers.IntegerField()
    url = serializers.CharField()
    expires_in_seconds = serializers.IntegerField()
    nome_original = serializers.CharField()

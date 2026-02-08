"""Documento serializers for create, update, and response."""

from rest_framework import serializers

from apps.arquivos.models import Arquivo
from apps.arquivos.serializers import ArquivoResponseSerializer
from apps.documentos.models import Documento, DocumentoTipo


class DocumentoCreateSerializer(serializers.Serializer):
    """Serializer for creating a Documento."""

    numero = serializers.CharField(required=False, allow_blank=True, default="")
    subject = serializers.CharField(
        error_messages={"required": "Assunto e obrigatorio.", "blank": "Assunto e obrigatorio."},
    )
    honorifico = serializers.CharField(required=False, allow_blank=True, default="")
    body = serializers.CharField(
        error_messages={"required": "Corpo e obrigatorio.", "blank": "Corpo e obrigatorio."},
    )
    tipo = serializers.ChoiceField(
        choices=DocumentoTipo.choices,
        error_messages={"required": "Tipo e obrigatorio.", "invalid_choice": "Tipo invalido."},
    )
    portaria = serializers.CharField(required=False, allow_blank=True, default="")
    assinante = serializers.CharField(
        error_messages={"required": "Assinante e obrigatorio.", "blank": "Assinante e obrigatorio."},
    )
    interessado = serializers.CharField(
        error_messages={"required": "Interessado e obrigatorio.", "blank": "Interessado e obrigatorio."},
    )
    destino = serializers.CharField(required=False, allow_blank=True, default="")
    anexo_ids = serializers.ListField(
        child=serializers.IntegerField(),
        required=False,
        default=[],
    )

    def validate_anexo_ids(self, value):
        """Validate all anexo_ids reference existing Arquivo records."""
        if value:
            existing = set(Arquivo.objects.filter(pk__in=value).values_list("pk", flat=True))
            missing = set(value) - existing
            if missing:
                raise serializers.ValidationError(f"Arquivos nao encontrados: {missing}")
        return value

    def create(self, validated_data):
        """Create Documento with anexos relationship."""
        anexo_ids = validated_data.pop("anexo_ids", [])

        documento = Documento.objects.create(**validated_data)

        if anexo_ids:
            documento.anexos.set(anexo_ids)

        return documento


class DocumentoUpdateSerializer(serializers.Serializer):
    """Serializer for updating a Documento (partial update)."""

    numero = serializers.CharField(required=False, allow_blank=True)
    subject = serializers.CharField(required=False)
    honorifico = serializers.CharField(required=False, allow_blank=True)
    body = serializers.CharField(required=False)
    tipo = serializers.ChoiceField(choices=DocumentoTipo.choices, required=False)
    portaria = serializers.CharField(required=False, allow_blank=True)
    assinante = serializers.CharField(required=False)
    interessado = serializers.CharField(required=False)
    destino = serializers.CharField(required=False, allow_blank=True)
    anexo_ids = serializers.ListField(
        child=serializers.IntegerField(),
        required=False,
    )

    def validate_anexo_ids(self, value):
        """Validate all anexo_ids reference existing Arquivo records."""
        if value:
            existing = set(Arquivo.objects.filter(pk__in=value).values_list("pk", flat=True))
            missing = set(value) - existing
            if missing:
                raise serializers.ValidationError(f"Arquivos nao encontrados: {missing}")
        return value


class DocumentoResponseSerializer(serializers.ModelSerializer):
    """Serializer for Documento responses with nested anexos."""

    anexos = ArquivoResponseSerializer(many=True, read_only=True)

    class Meta:
        """Meta options."""

        model = Documento
        fields = [
            "id",
            "numero",
            "data",
            "subject",
            "honorifico",
            "body",
            "tipo",
            "portaria",
            "assinante",
            "interessado",
            "destino",
            "anexos",
        ]
        read_only_fields = fields

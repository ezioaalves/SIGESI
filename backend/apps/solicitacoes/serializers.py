"""Solicitacao serializers for create, update, and response."""

from rest_framework import serializers

from apps.arquivos.models import Arquivo
from apps.enderecos.models import Endereco
from apps.enderecos.serializers import EnderecoResponseSerializer
from apps.solicitacoes.models import (
    Solicitacao,
    SolicitacaoAssunto,
    SolicitacaoStatus,
)
from apps.usuarios.models import Usuario


class ArquivoSimpleResponseSerializer(serializers.ModelSerializer):
    """Simple nested serializer for Arquivo in Solicitacao responses."""

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


class AutorResponseSerializer(serializers.ModelSerializer):
    """Nested serializer for Usuario in responses (raw model fields)."""

    class Meta:
        """Meta options."""

        model = Usuario
        fields = ["id", "email", "first_name", "last_name", "picture_url", "role"]
        read_only_fields = fields


class SolicitacaoCreateSerializer(serializers.Serializer):
    """Serializer for creating a Solicitacao."""

    assunto = serializers.ChoiceField(
        choices=SolicitacaoAssunto.choices,
        error_messages={"required": "Assunto e obrigatorio.", "invalid_choice": "Assunto invalido."},
    )
    body = serializers.CharField(
        error_messages={"required": "Corpo e obrigatorio.", "blank": "Corpo e obrigatorio."},
    )
    autor_id = serializers.IntegerField(
        error_messages={"required": "Autor e obrigatorio."},
    )
    local_id = serializers.IntegerField(
        error_messages={"required": "Local e obrigatorio."},
    )
    anexo_ids = serializers.ListField(
        child=serializers.IntegerField(),
        required=False,
        default=[],
    )

    def validate_autor_id(self, value):
        """Validate autor_id references an existing Usuario."""
        if not Usuario.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Autor nao encontrado.")
        return value

    def validate_local_id(self, value):
        """Validate local_id references an existing Endereco."""
        if not Endereco.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Local nao encontrado.")
        return value

    def validate_anexo_ids(self, value):
        """Validate all anexo_ids reference existing Arquivo records."""
        if value:
            existing = set(Arquivo.objects.filter(pk__in=value).values_list("pk", flat=True))
            missing = set(value) - existing
            if missing:
                raise serializers.ValidationError(f"Arquivos nao encontrados: {missing}")
        return value

    def create(self, validated_data):
        """Create Solicitacao with relationships."""
        autor = Usuario.objects.get(pk=validated_data["autor_id"])
        local = Endereco.objects.get(pk=validated_data["local_id"])
        anexo_ids = validated_data.pop("anexo_ids", [])

        solicitacao = Solicitacao.objects.create(
            assunto=validated_data["assunto"],
            body=validated_data["body"],
            autor=autor,
            local=local,
        )

        if anexo_ids:
            solicitacao.anexos.set(anexo_ids)

        return solicitacao


class SolicitacaoUpdateSerializer(serializers.Serializer):
    """Serializer for updating a Solicitacao (status only)."""

    status = serializers.ChoiceField(
        choices=SolicitacaoStatus.choices,
        error_messages={
            "required": "Status nao pode ser vazio.",
            "invalid_choice": "Status invalido.",
        },
    )


class SolicitacaoResponseSerializer(serializers.ModelSerializer):
    """Serializer for Solicitacao responses with nested objects."""

    autor = AutorResponseSerializer(read_only=True)
    local = EnderecoResponseSerializer(read_only=True)
    anexos = ArquivoSimpleResponseSerializer(many=True, read_only=True)

    class Meta:
        """Meta options."""

        model = Solicitacao
        fields = ["id", "data", "assunto", "status", "body", "anexos", "autor", "local"]
        read_only_fields = fields

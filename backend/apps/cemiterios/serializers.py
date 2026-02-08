"""Cemiterio serializers for create, update, and response."""

from rest_framework import serializers

from apps.cemiterios.models import Cemiterio
from apps.enderecos.models import Endereco
from apps.enderecos.serializers import EnderecoResponseSerializer


class CemiterioCreateSerializer(serializers.Serializer):
    """Serializer for creating a Cemiterio."""

    nome = serializers.CharField(
        error_messages={"required": "Nome e obrigatorio.", "blank": "Nome e obrigatorio."},
    )
    endereco_id = serializers.IntegerField(
        error_messages={"required": "Endereco e obrigatorio."},
    )

    def validate_endereco_id(self, value):
        """Validate endereco_id references an existing Endereco."""
        if not Endereco.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Endereco nao encontrado.")
        return value

    def create(self, validated_data):
        """Create Cemiterio with Endereco relationship."""
        endereco = Endereco.objects.get(pk=validated_data["endereco_id"])
        return Cemiterio.objects.create(
            nome=validated_data["nome"],
            endereco=endereco,
        )


class CemiterioUpdateSerializer(serializers.Serializer):
    """Serializer for updating a Cemiterio (partial update)."""

    nome = serializers.CharField(required=False)
    endereco_id = serializers.IntegerField(required=False)

    def validate_endereco_id(self, value):
        """Validate endereco_id references an existing Endereco."""
        if not Endereco.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Endereco nao encontrado.")
        return value


class CemiterioResponseSerializer(serializers.ModelSerializer):
    """Serializer for Cemiterio responses with nested Endereco."""

    endereco = EnderecoResponseSerializer(read_only=True)

    class Meta:
        """Meta options."""

        model = Cemiterio
        fields = ["id", "nome", "endereco"]
        read_only_fields = fields

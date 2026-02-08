"""Jazigo serializers for create, update, and response."""

from rest_framework import serializers

from apps.cemiterios.models import Cemiterio
from apps.cemiterios.serializers import CemiterioResponseSerializer
from apps.jazigos.models import Jazigo


class JazigoCreateSerializer(serializers.Serializer):
    """Serializer for creating a Jazigo."""

    cemiterio_id = serializers.IntegerField(
        error_messages={"required": "Cemiterio e obrigatorio."},
    )
    largura = serializers.FloatField(required=False, allow_null=True)
    comprimento = serializers.FloatField(required=False, allow_null=True)
    quadra = serializers.IntegerField(
        error_messages={"required": "Quadra e obrigatoria."},
    )
    rua = serializers.CharField(
        error_messages={"required": "Rua e obrigatoria.", "blank": "Rua e obrigatoria."},
    )
    lote = serializers.CharField(
        error_messages={"required": "Lote e obrigatorio.", "blank": "Lote e obrigatorio."},
    )

    def validate_cemiterio_id(self, value):
        """Validate cemiterio_id references an existing Cemiterio."""
        if not Cemiterio.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Cemiterio nao encontrado.")
        return value

    def create(self, validated_data):
        """Create Jazigo with Cemiterio relationship."""
        cemiterio = Cemiterio.objects.get(pk=validated_data["cemiterio_id"])
        return Jazigo.objects.create(
            cemiterio=cemiterio,
            largura=validated_data.get("largura"),
            comprimento=validated_data.get("comprimento"),
            quadra=validated_data["quadra"],
            rua=validated_data["rua"],
            lote=validated_data["lote"],
        )


class JazigoUpdateSerializer(serializers.Serializer):
    """Serializer for updating a Jazigo (partial update)."""

    cemiterio_id = serializers.IntegerField(required=False)
    largura = serializers.FloatField(required=False, allow_null=True)
    comprimento = serializers.FloatField(required=False, allow_null=True)
    quadra = serializers.IntegerField(required=False)
    rua = serializers.CharField(required=False)
    lote = serializers.CharField(required=False)

    def validate_cemiterio_id(self, value):
        """Validate cemiterio_id references an existing Cemiterio."""
        if not Cemiterio.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Cemiterio nao encontrado.")
        return value


class JazigoResponseSerializer(serializers.ModelSerializer):
    """Serializer for Jazigo responses with nested Cemiterio."""

    cemiterio = CemiterioResponseSerializer(read_only=True)

    class Meta:
        """Meta options."""

        model = Jazigo
        fields = ["id", "cemiterio", "largura", "comprimento", "quadra", "rua", "lote"]
        read_only_fields = fields

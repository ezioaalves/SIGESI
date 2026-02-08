"""Gaveta serializers for create, update, and response."""

from rest_framework import serializers

from apps.gavetas.models import Gaveta
from apps.jazigos.models import Jazigo
from apps.jazigos.serializers import JazigoResponseSerializer
from apps.pessoas.models import Pessoa
from apps.pessoas.serializers import PessoaResponseSerializer


class GavetaCreateSerializer(serializers.Serializer):
    """Serializer for creating a Gaveta."""

    jazigo_id = serializers.IntegerField(
        error_messages={"required": "Jazigo e obrigatorio."},
    )
    numero = serializers.IntegerField(
        error_messages={"required": "Numero e obrigatorio."},
    )
    ocupante_id = serializers.IntegerField(
        error_messages={"required": "Ocupante e obrigatorio."},
    )

    def validate_jazigo_id(self, value):
        """Validate jazigo_id references an existing Jazigo."""
        if not Jazigo.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Jazigo nao encontrado.")
        return value

    def validate_ocupante_id(self, value):
        """Validate ocupante_id references an existing Pessoa."""
        if not Pessoa.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Pessoa nao encontrada.")
        return value

    def create(self, validated_data):
        """Create Gaveta with Jazigo and Ocupante relationships."""
        jazigo = Jazigo.objects.get(pk=validated_data["jazigo_id"])
        ocupante = Pessoa.objects.get(pk=validated_data["ocupante_id"])
        return Gaveta.objects.create(
            jazigo=jazigo,
            numero=validated_data["numero"],
            ocupante=ocupante,
        )


class GavetaUpdateSerializer(serializers.Serializer):
    """Serializer for updating a Gaveta (partial update)."""

    jazigo_id = serializers.IntegerField(required=False)
    numero = serializers.IntegerField(required=False)
    ocupante_id = serializers.IntegerField(required=False, allow_null=True)

    def validate_jazigo_id(self, value):
        """Validate jazigo_id references an existing Jazigo."""
        if not Jazigo.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Jazigo nao encontrado.")
        return value

    def validate_ocupante_id(self, value):
        """Validate ocupante_id references an existing Pessoa."""
        if value is not None and not Pessoa.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Pessoa nao encontrada.")
        return value


class GavetaResponseSerializer(serializers.ModelSerializer):
    """Serializer for Gaveta responses with nested Jazigo and Ocupante."""

    jazigo = JazigoResponseSerializer(read_only=True)
    ocupante = PessoaResponseSerializer(read_only=True, allow_null=True)

    class Meta:
        """Meta options."""

        model = Gaveta
        fields = ["id", "jazigo", "numero", "ocupante"]
        read_only_fields = fields

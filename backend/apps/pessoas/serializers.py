"""Pessoa serializers for create, update, and response."""

from rest_framework import serializers

from apps.core.exceptions import ConflictException
from apps.enderecos.models import Endereco
from apps.enderecos.serializers import EnderecoResponseSerializer
from apps.pessoas.models import Pessoa, SexoEnum


class PessoaCreateSerializer(serializers.Serializer):
    """Serializer for creating a Pessoa."""

    nome = serializers.CharField(
        error_messages={"required": "Nome e obrigatorio.", "blank": "Nome e obrigatorio."},
    )
    cpf = serializers.CharField(
        error_messages={"required": "CPF e obrigatorio.", "blank": "CPF e obrigatorio."},
    )
    sexo = serializers.ChoiceField(
        choices=SexoEnum.choices,
        error_messages={"required": "Sexo e obrigatorio.", "invalid_choice": "Sexo invalido."},
    )
    endereco_id = serializers.IntegerField(
        required=False,
        allow_null=True,
    )

    def validate_endereco_id(self, value):
        """Validate endereco_id references an existing Endereco."""
        if value is not None and not Endereco.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Endereco nao encontrado.")
        return value

    def create(self, validated_data):
        """Create Pessoa with CPF uniqueness check."""
        cpf = validated_data["cpf"]
        if Pessoa.objects.filter(cpf=cpf).exists():
            raise ConflictException("CPF ja cadastrado.")

        endereco = None
        if validated_data.get("endereco_id"):
            endereco = Endereco.objects.get(pk=validated_data["endereco_id"])

        return Pessoa.objects.create(
            nome=validated_data["nome"],
            cpf=cpf,
            sexo=validated_data["sexo"],
            endereco=endereco,
        )


class PessoaUpdateSerializer(serializers.Serializer):
    """Serializer for updating a Pessoa (partial update)."""

    nome = serializers.CharField(required=False)
    cpf = serializers.CharField(required=False)
    sexo = serializers.ChoiceField(
        choices=SexoEnum.choices,
        required=False,
        error_messages={"invalid_choice": "Sexo invalido."},
    )
    endereco_id = serializers.IntegerField(required=False, allow_null=True)

    def validate_endereco_id(self, value):
        """Validate endereco_id references an existing Endereco."""
        if value is not None and not Endereco.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Endereco nao encontrado.")
        return value


class PessoaResponseSerializer(serializers.ModelSerializer):
    """Serializer for Pessoa responses with nested Endereco."""

    endereco = EnderecoResponseSerializer(read_only=True, allow_null=True)

    class Meta:
        """Meta options."""

        model = Pessoa
        fields = ["id", "nome", "cpf", "sexo", "endereco"]
        read_only_fields = fields

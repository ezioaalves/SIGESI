"""Endereco serializers for create, update, and response."""

from rest_framework import serializers

from apps.enderecos.models import Endereco


class EnderecoCreateSerializer(serializers.ModelSerializer):
    """Serializer for creating an Endereco."""

    class Meta:
        """Meta options."""

        model = Endereco
        fields = ["logradouro", "numero", "bairro", "referencia"]
        extra_kwargs = {
            "logradouro": {"error_messages": {"blank": "O logradouro e obrigatorio."}},
            "numero": {"error_messages": {"blank": "O numero e obrigatorio."}},
            "bairro": {"error_messages": {"blank": "O bairro e obrigatorio."}},
            "referencia": {"required": False, "allow_blank": True},
        }


class EnderecoUpdateSerializer(serializers.ModelSerializer):
    """Serializer for updating an Endereco (partial update)."""

    class Meta:
        """Meta options."""

        model = Endereco
        fields = ["logradouro", "numero", "bairro", "referencia"]
        extra_kwargs = {
            "logradouro": {"required": False},
            "numero": {"required": False},
            "bairro": {"required": False},
            "referencia": {"required": False, "allow_blank": True},
        }


class EnderecoResponseSerializer(serializers.ModelSerializer):
    """Serializer for Endereco responses."""

    class Meta:
        """Meta options."""

        model = Endereco
        fields = ["id", "logradouro", "numero", "bairro", "referencia"]
        read_only_fields = fields

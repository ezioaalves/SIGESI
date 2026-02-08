"""Material serializers for create, update, and response."""

from rest_framework import serializers

from apps.materiais.models import Material


class MaterialCreateSerializer(serializers.ModelSerializer):
    """Serializer for creating a Material."""

    class Meta:
        """Meta options."""

        model = Material
        fields = ["nome", "preco"]
        extra_kwargs = {
            "nome": {"error_messages": {"blank": "O nome do material e obrigatorio."}},
            "preco": {"error_messages": {"required": "O preco e obrigatorio."}},
        }


class MaterialUpdateSerializer(serializers.ModelSerializer):
    """Serializer for updating a Material (partial update)."""

    class Meta:
        """Meta options."""

        model = Material
        fields = ["nome", "preco"]
        extra_kwargs = {
            "nome": {"required": False},
            "preco": {"required": False},
        }


class MaterialResponseSerializer(serializers.ModelSerializer):
    """Serializer for Material responses."""

    class Meta:
        """Meta options."""

        model = Material
        fields = ["id", "nome", "preco"]
        read_only_fields = fields

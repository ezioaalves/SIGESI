"""Usuario serializers for /me, response, and update."""

from rest_framework import serializers

from apps.usuarios.models import Usuario


class UsuarioMeSerializer(serializers.ModelSerializer):
    """Serializer for /me endpoint - matches Spring Boot /me response."""

    name = serializers.SerializerMethodField()
    picture = serializers.CharField(source="picture_url", read_only=True)

    class Meta:
        """Meta options."""

        model = Usuario
        fields = ["id", "name", "email", "role", "picture"]
        read_only_fields = fields

    def get_name(self, obj):
        """Return full name concatenating first_name and last_name."""
        return f"{obj.first_name} {obj.last_name}".strip()


class UsuarioResponseSerializer(serializers.ModelSerializer):
    """Serializer for admin user list/detail responses."""

    class Meta:
        """Meta options."""

        model = Usuario
        fields = ["id", "email", "first_name", "last_name", "picture_url", "provider", "ativo", "role"]
        read_only_fields = fields


class UsuarioUpdateSerializer(serializers.Serializer):
    """Serializer for updating user role (ADMIN only)."""

    role = serializers.ChoiceField(
        choices=Usuario.Role.choices,
        error_messages={"required": "A role de usuario e obrigatoria."},
    )

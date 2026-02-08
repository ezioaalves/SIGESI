"""Comentario serializers for create and response (no update - immutable)."""

from rest_framework import serializers

from apps.comentarios.models import Comentario
from apps.demandas.models import Demanda
from apps.solicitacoes.serializers import AutorResponseSerializer
from apps.usuarios.models import Usuario


class ComentarioCreateSerializer(serializers.Serializer):
    """Serializer for creating a Comentario."""

    demanda_id = serializers.IntegerField(
        error_messages={"required": "Demanda e obrigatoria."},
    )
    autor_id = serializers.IntegerField(
        error_messages={"required": "Autor e obrigatorio."},
    )
    texto = serializers.CharField(
        error_messages={"required": "Texto e obrigatorio.", "blank": "Texto e obrigatorio."},
    )

    def validate_demanda_id(self, value):
        """Validate demanda_id references an existing Demanda."""
        if not Demanda.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Demanda nao encontrada.")
        return value

    def validate_autor_id(self, value):
        """Validate autor_id references an existing Usuario."""
        if not Usuario.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Autor nao encontrado.")
        return value

    def create(self, validated_data):
        """Create Comentario with relationships."""
        demanda = Demanda.objects.get(pk=validated_data["demanda_id"])
        autor = Usuario.objects.get(pk=validated_data["autor_id"])
        return Comentario.objects.create(
            demanda=demanda,
            autor=autor,
            texto=validated_data["texto"],
        )


class ComentarioResponseSerializer(serializers.ModelSerializer):
    """Serializer for Comentario responses."""

    demanda_id = serializers.IntegerField(source="demanda.id", read_only=True)
    autor = AutorResponseSerializer(read_only=True)

    class Meta:
        """Meta options."""

        model = Comentario
        fields = ["id", "demanda_id", "autor", "texto", "criado_em"]
        read_only_fields = fields

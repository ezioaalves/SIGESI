"""Demanda and DemandaMaterial serializers for create, update, and response."""

from rest_framework import serializers

from apps.demandas.models import Demanda, DemandaMaterial, DemandaStatus
from apps.materiais.models import Material
from apps.materiais.serializers import MaterialResponseSerializer
from apps.solicitacoes.models import Solicitacao
from apps.solicitacoes.serializers import (
    AutorResponseSerializer,
    SolicitacaoResponseSerializer,
)
from apps.usuarios.models import Usuario


class DemandaMaterialCreateSerializer(serializers.Serializer):
    """Serializer for creating DemandaMaterial entries inline."""

    material_id = serializers.IntegerField(
        error_messages={"required": "Material e obrigatorio."},
    )
    quantidade = serializers.IntegerField(
        min_value=1,
        error_messages={
            "required": "Quantidade e obrigatoria.",
            "min_value": "Quantidade deve ser maior que zero.",
        },
    )

    def validate_material_id(self, value):
        """Validate material_id references an existing Material."""
        if not Material.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Material nao encontrado.")
        return value


class DemandaMaterialResponseSerializer(serializers.ModelSerializer):
    """Serializer for DemandaMaterial responses with nested Material."""

    material = MaterialResponseSerializer(read_only=True)

    class Meta:
        """Meta options."""

        model = DemandaMaterial
        fields = ["id", "material", "quantidade"]
        read_only_fields = fields


class DemandaCreateSerializer(serializers.Serializer):
    """Serializer for creating a Demanda with inline materials."""

    solicitacao_id = serializers.IntegerField(
        error_messages={"required": "Solicitacao e obrigatoria."},
    )
    responsavel_id = serializers.IntegerField(
        required=False,
        allow_null=True,
    )
    prazo = serializers.DateField(
        error_messages={"required": "Prazo e obrigatorio."},
    )
    materiais = DemandaMaterialCreateSerializer(
        many=True,
        required=False,
        default=[],
    )

    def validate_solicitacao_id(self, value):
        """Validate solicitacao_id references an existing Solicitacao."""
        if not Solicitacao.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Solicitacao nao encontrada.")
        return value

    def validate_responsavel_id(self, value):
        """Validate responsavel_id references an existing Usuario."""
        if value is not None and not Usuario.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Responsavel nao encontrado.")
        return value

    def create(self, validated_data):
        """Create Demanda with inline DemandaMaterial entries."""
        solicitacao = Solicitacao.objects.get(pk=validated_data["solicitacao_id"])
        responsavel = None
        if validated_data.get("responsavel_id"):
            responsavel = Usuario.objects.get(pk=validated_data["responsavel_id"])

        materiais_data = validated_data.pop("materiais", [])

        demanda = Demanda.objects.create(
            solicitacao=solicitacao,
            responsavel=responsavel,
            prazo=validated_data["prazo"],
        )

        for mat_data in materiais_data:
            material = Material.objects.get(pk=mat_data["material_id"])
            DemandaMaterial.objects.create(
                demanda=demanda,
                material=material,
                quantidade=mat_data["quantidade"],
            )

        return demanda


class DemandaUpdateSerializer(serializers.Serializer):
    """Serializer for updating a Demanda (all fields optional for PATCH)."""

    responsavel_id = serializers.IntegerField(
        required=False,
        allow_null=True,
    )
    prazo = serializers.DateField(required=False)
    status = serializers.ChoiceField(
        choices=DemandaStatus.choices,
        required=False,
        error_messages={"invalid_choice": "Status invalido."},
    )
    materiais = DemandaMaterialCreateSerializer(
        many=True,
        required=False,
        allow_null=True,
    )

    def validate_responsavel_id(self, value):
        """Validate responsavel_id references an existing Usuario."""
        if value is not None and not Usuario.objects.filter(pk=value).exists():
            raise serializers.ValidationError("Responsavel nao encontrado.")
        return value


class DemandaResponseSerializer(serializers.ModelSerializer):
    """Serializer for Demanda responses with nested objects."""

    solicitacao = SolicitacaoResponseSerializer(read_only=True)
    responsavel = AutorResponseSerializer(read_only=True, allow_null=True)
    materiais = DemandaMaterialResponseSerializer(
        many=True,
        read_only=True,
        source="demanda_materiais",
    )

    class Meta:
        """Meta options."""

        model = Demanda
        fields = ["id", "solicitacao", "responsavel", "prazo", "status", "materiais"]
        read_only_fields = fields

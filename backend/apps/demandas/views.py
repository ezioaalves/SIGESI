"""Demanda ViewSet with custom actions for by_solicitacao and by_responsavel."""

from django.db import transaction
from rest_framework import status
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.viewsets import ModelViewSet

from apps.core.permissions import IsAllRoles
from apps.demandas.models import Demanda, DemandaMaterial
from apps.demandas.schema import demanda_schema
from apps.demandas.serializers import (
    DemandaCreateSerializer,
    DemandaResponseSerializer,
    DemandaUpdateSerializer,
)
from apps.materiais.models import Material
from apps.usuarios.models import Usuario


@demanda_schema
class DemandaViewSet(ModelViewSet):
    """ViewSet for Demanda CRUD with custom query actions."""

    queryset = Demanda.objects.all().order_by("id")
    permission_classes = [IsAllRoles]

    def get_serializer_class(self):
        """Return the appropriate serializer per action."""
        if self.action == "create":
            return DemandaCreateSerializer
        if self.action == "partial_update":
            return DemandaUpdateSerializer
        return DemandaResponseSerializer

    def create(self, request, *args, **kwargs):
        """Create a new Demanda with inline materials."""
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        demanda = serializer.save()
        response_serializer = DemandaResponseSerializer(demanda)
        return Response(response_serializer.data, status=status.HTTP_201_CREATED)

    @transaction.atomic
    def partial_update(self, request, *args, **kwargs):
        """Update Demanda fields and optionally replace materials."""
        demanda = self.get_object()
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        data = serializer.validated_data

        if "prazo" in data:
            demanda.prazo = data["prazo"]
        if "status" in data:
            demanda.status = data["status"]
        if "responsavel_id" in data:
            if data["responsavel_id"] is None:
                demanda.responsavel = None
            else:
                demanda.responsavel = Usuario.objects.get(pk=data["responsavel_id"])

        if "materiais" in data:
            demanda.demanda_materiais.all().delete()
            materiais_data = data["materiais"] or []
            for mat_data in materiais_data:
                material = Material.objects.get(pk=mat_data["material_id"])
                DemandaMaterial.objects.create(
                    demanda=demanda,
                    material=material,
                    quantidade=mat_data["quantidade"],
                )

        demanda.save()
        response_serializer = DemandaResponseSerializer(demanda)
        return Response(response_serializer.data)

    @action(
        detail=False,
        methods=["get"],
        url_path="solicitacao/(?P<solicitacao_id>[^/.]+)",
        url_name="by-solicitacao",
    )
    def by_solicitacao(self, request, solicitacao_id=None):
        """Return demands for a specific solicitacao, ordered by prazo ASC."""
        queryset = Demanda.objects.filter(solicitacao_id=solicitacao_id).order_by("prazo")
        page = self.paginate_queryset(queryset)
        if page is not None:
            serializer = DemandaResponseSerializer(page, many=True)
            return self.get_paginated_response(serializer.data)
        serializer = DemandaResponseSerializer(queryset, many=True)
        return Response(serializer.data)

    @action(
        detail=False,
        methods=["get"],
        url_name="by-responsavel",
    )
    def by_responsavel(self, request):
        """Return demands for a specific responsavel, ordered by prazo ASC."""
        responsavel_id = request.query_params.get("responsavel_id")
        if not responsavel_id:
            return Response(
                {"status": 400, "error": "Bad Request", "message": "responsavel_id e obrigatorio."},
                status=status.HTTP_400_BAD_REQUEST,
            )
        queryset = Demanda.objects.filter(responsavel_id=responsavel_id).order_by("prazo")
        page = self.paginate_queryset(queryset)
        if page is not None:
            serializer = DemandaResponseSerializer(page, many=True)
            return self.get_paginated_response(serializer.data)
        serializer = DemandaResponseSerializer(queryset, many=True)
        return Response(serializer.data)

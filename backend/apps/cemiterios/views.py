"""Cemiterio ViewSet for CRUD operations."""

from rest_framework import status
from rest_framework.response import Response
from rest_framework.viewsets import ModelViewSet

from apps.cemiterios.models import Cemiterio
from apps.cemiterios.serializers import (
    CemiterioCreateSerializer,
    CemiterioResponseSerializer,
    CemiterioUpdateSerializer,
)
from apps.core.permissions import IsOperadorOrAdmin
from apps.enderecos.models import Endereco


class CemiterioViewSet(ModelViewSet):
    """ViewSet for Cemiterio CRUD operations."""

    queryset = Cemiterio.objects.all().order_by("id")
    permission_classes = [IsOperadorOrAdmin]

    def get_serializer_class(self):
        """Return the appropriate serializer per action."""
        if self.action == "create":
            return CemiterioCreateSerializer
        if self.action in ("update", "partial_update"):
            return CemiterioUpdateSerializer
        return CemiterioResponseSerializer

    def create(self, request, *args, **kwargs):
        """Create a new Cemiterio."""
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        cemiterio = serializer.save()
        response_serializer = CemiterioResponseSerializer(cemiterio)
        return Response(response_serializer.data, status=status.HTTP_201_CREATED)

    def partial_update(self, request, *args, **kwargs):
        """Update Cemiterio fields individually."""
        cemiterio = self.get_object()
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        data = serializer.validated_data

        if "nome" in data:
            cemiterio.nome = data["nome"]
        if "endereco_id" in data:
            cemiterio.endereco = Endereco.objects.get(pk=data["endereco_id"])

        cemiterio.save()
        response_serializer = CemiterioResponseSerializer(cemiterio)
        return Response(response_serializer.data)

"""Jazigo ViewSet for CRUD operations."""

from rest_framework import status
from rest_framework.response import Response
from rest_framework.viewsets import ModelViewSet

from apps.cemiterios.models import Cemiterio
from apps.core.permissions import IsOperadorOrAdmin
from apps.jazigos.models import Jazigo
from apps.jazigos.serializers import (
    JazigoCreateSerializer,
    JazigoResponseSerializer,
    JazigoUpdateSerializer,
)


class JazigoViewSet(ModelViewSet):
    """ViewSet for Jazigo CRUD operations."""

    queryset = Jazigo.objects.all().order_by("id")
    permission_classes = [IsOperadorOrAdmin]

    def get_serializer_class(self):
        """Return the appropriate serializer per action."""
        if self.action == "create":
            return JazigoCreateSerializer
        if self.action in ("update", "partial_update"):
            return JazigoUpdateSerializer
        return JazigoResponseSerializer

    def create(self, request, *args, **kwargs):
        """Create a new Jazigo."""
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        jazigo = serializer.save()
        response_serializer = JazigoResponseSerializer(jazigo)
        return Response(response_serializer.data, status=status.HTTP_201_CREATED)

    def partial_update(self, request, *args, **kwargs):
        """Update Jazigo fields individually."""
        jazigo = self.get_object()
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        data = serializer.validated_data

        if "cemiterio_id" in data:
            jazigo.cemiterio = Cemiterio.objects.get(pk=data["cemiterio_id"])
        if "largura" in data:
            jazigo.largura = data["largura"]
        if "comprimento" in data:
            jazigo.comprimento = data["comprimento"]
        if "quadra" in data:
            jazigo.quadra = data["quadra"]
        if "rua" in data:
            jazigo.rua = data["rua"]
        if "lote" in data:
            jazigo.lote = data["lote"]

        jazigo.save()
        response_serializer = JazigoResponseSerializer(jazigo)
        return Response(response_serializer.data)

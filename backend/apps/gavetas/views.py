"""Gaveta ViewSet with filtering for CRUD operations."""

from rest_framework import status
from rest_framework.response import Response
from rest_framework.viewsets import ModelViewSet

from apps.core.permissions import IsOperadorOrAdmin
from apps.gavetas.filters import GavetaFilter
from apps.gavetas.models import Gaveta
from apps.gavetas.serializers import (
    GavetaCreateSerializer,
    GavetaResponseSerializer,
    GavetaUpdateSerializer,
)
from apps.jazigos.models import Jazigo
from apps.pessoas.models import Pessoa


class GavetaViewSet(ModelViewSet):
    """ViewSet for Gaveta CRUD with filtering."""

    queryset = Gaveta.objects.all().order_by("id")
    permission_classes = [IsOperadorOrAdmin]
    filterset_class = GavetaFilter

    def get_serializer_class(self):
        """Return the appropriate serializer per action."""
        if self.action == "create":
            return GavetaCreateSerializer
        if self.action in ("update", "partial_update"):
            return GavetaUpdateSerializer
        return GavetaResponseSerializer

    def create(self, request, *args, **kwargs):
        """Create a new Gaveta."""
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        gaveta = serializer.save()
        response_serializer = GavetaResponseSerializer(gaveta)
        return Response(response_serializer.data, status=status.HTTP_201_CREATED)

    def partial_update(self, request, *args, **kwargs):
        """Update Gaveta fields individually."""
        gaveta = self.get_object()
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        data = serializer.validated_data

        if "jazigo_id" in data:
            gaveta.jazigo = Jazigo.objects.get(pk=data["jazigo_id"])
        if "numero" in data:
            gaveta.numero = data["numero"]
        if "ocupante_id" in data:
            if data["ocupante_id"] is None:
                gaveta.ocupante = None
            else:
                gaveta.ocupante = Pessoa.objects.get(pk=data["ocupante_id"])

        gaveta.save()
        response_serializer = GavetaResponseSerializer(gaveta)
        return Response(response_serializer.data)

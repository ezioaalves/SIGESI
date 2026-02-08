"""Endereco ViewSet for CRUD operations."""

from rest_framework.viewsets import ModelViewSet

from apps.core.permissions import IsAllRoles
from apps.enderecos.models import Endereco
from apps.enderecos.serializers import (
    EnderecoCreateSerializer,
    EnderecoResponseSerializer,
    EnderecoUpdateSerializer,
)


class EnderecoViewSet(ModelViewSet):
    """ViewSet for Endereco CRUD operations."""

    queryset = Endereco.objects.all().order_by("id")
    permission_classes = [IsAllRoles]

    def get_serializer_class(self):
        """Return the appropriate serializer per action."""
        if self.action == "create":
            return EnderecoCreateSerializer
        if self.action in ("update", "partial_update"):
            return EnderecoUpdateSerializer
        return EnderecoResponseSerializer

"""Material ViewSet for CRUD operations."""

from rest_framework.viewsets import ModelViewSet

from apps.core.permissions import IsAllRoles
from apps.materiais.models import Material
from apps.materiais.serializers import (
    MaterialCreateSerializer,
    MaterialResponseSerializer,
    MaterialUpdateSerializer,
)


class MaterialViewSet(ModelViewSet):
    """ViewSet for Material CRUD operations."""

    queryset = Material.objects.all().order_by("id")
    permission_classes = [IsAllRoles]

    def get_serializer_class(self):
        """Return the appropriate serializer per action."""
        if self.action == "create":
            return MaterialCreateSerializer
        if self.action in ("update", "partial_update"):
            return MaterialUpdateSerializer
        return MaterialResponseSerializer

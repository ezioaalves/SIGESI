"""Comentario ViewSet with by_demanda action and restricted HTTP methods."""

from rest_framework import status
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.viewsets import ModelViewSet

from apps.comentarios.models import Comentario
from apps.comentarios.serializers import (
    ComentarioCreateSerializer,
    ComentarioResponseSerializer,
)
from apps.core.permissions import IsAllRoles


class ComentarioViewSet(ModelViewSet):
    """ViewSet for Comentario with no update (immutable comments)."""

    queryset = Comentario.objects.all().order_by("id")
    permission_classes = [IsAllRoles]
    http_method_names = ["get", "post", "delete", "head", "options"]

    def get_serializer_class(self):
        """Return the appropriate serializer per action."""
        if self.action == "create":
            return ComentarioCreateSerializer
        return ComentarioResponseSerializer

    def create(self, request, *args, **kwargs):
        """Create a new Comentario."""
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        comentario = serializer.save()
        response_serializer = ComentarioResponseSerializer(comentario)
        return Response(response_serializer.data, status=status.HTTP_201_CREATED)

    @action(
        detail=False,
        methods=["get"],
        url_path="demanda/(?P<demanda_id>[^/.]+)",
        url_name="by-demanda",
    )
    def by_demanda(self, request, demanda_id=None):
        """Return comments for a specific demanda, ordered by criado_em ASC."""
        queryset = Comentario.objects.filter(demanda_id=demanda_id).order_by("criado_em")
        page = self.paginate_queryset(queryset)
        if page is not None:
            serializer = ComentarioResponseSerializer(page, many=True)
            return self.get_paginated_response(serializer.data)
        serializer = ComentarioResponseSerializer(queryset, many=True)
        return Response(serializer.data)

"""Solicitacao ViewSet with role-based queryset filtering."""

from rest_framework import status
from rest_framework.response import Response
from rest_framework.viewsets import ModelViewSet

from apps.core.permissions import IsAllRoles
from apps.solicitacoes.models import Solicitacao
from apps.solicitacoes.serializers import (
    SolicitacaoCreateSerializer,
    SolicitacaoResponseSerializer,
    SolicitacaoUpdateSerializer,
)


class SolicitacaoViewSet(ModelViewSet):
    """ViewSet for Solicitacao CRUD with role-based filtering."""

    permission_classes = [IsAllRoles]

    def get_queryset(self):
        """Return role-filtered queryset.

        ADMIN/OPERADOR see all (ordered by id ASC).
        CIDADAO/AGENTE see only their own (ordered by data DESC).
        """
        user = self.request.user
        if user.role in ("ADMIN", "OPERADOR"):
            return Solicitacao.objects.all().order_by("id")
        return Solicitacao.objects.filter(autor=user).order_by("-data")

    def get_serializer_class(self):
        """Return the appropriate serializer per action."""
        if self.action == "create":
            return SolicitacaoCreateSerializer
        if self.action == "partial_update":
            return SolicitacaoUpdateSerializer
        return SolicitacaoResponseSerializer

    def create(self, request, *args, **kwargs):
        """Create a new Solicitacao."""
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        solicitacao = serializer.save()
        response_serializer = SolicitacaoResponseSerializer(solicitacao)
        return Response(response_serializer.data, status=status.HTTP_201_CREATED)

    def partial_update(self, request, *args, **kwargs):
        """Update Solicitacao status only."""
        solicitacao = self.get_object()
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        solicitacao.status = serializer.validated_data["status"]
        solicitacao.save(update_fields=["status"])
        response_serializer = SolicitacaoResponseSerializer(solicitacao)
        return Response(response_serializer.data)

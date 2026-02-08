"""Usuario ViewSet with custom actions for /me and /toggle-ativo."""

from rest_framework import status
from rest_framework.decorators import action
from rest_framework.exceptions import PermissionDenied
from rest_framework.response import Response
from rest_framework.viewsets import ModelViewSet

from apps.core.permissions import IsActiveAuthenticated, IsAdmin
from apps.usuarios.models import Usuario
from apps.usuarios.serializers import (
    UsuarioMeSerializer,
    UsuarioResponseSerializer,
    UsuarioUpdateSerializer,
)

PROTECTED_USER_PK = 1
PROTECTED_USER_MESSAGE = "Nao e permitido realizar essa acao para este usuario."


def _validate_editable(pk):
    """Raise PermissionDenied if user pk is protected."""
    if int(pk) == PROTECTED_USER_PK:
        raise PermissionDenied(PROTECTED_USER_MESSAGE)


class UsuarioViewSet(ModelViewSet):
    """ViewSet for Usuario management with /me and /toggle-ativo actions."""

    queryset = Usuario.objects.exclude(pk=PROTECTED_USER_PK).order_by("id")
    http_method_names = ["get", "patch", "head", "options"]

    def get_permissions(self):
        """Return permissions based on action."""
        if self.action == "me":
            return [IsActiveAuthenticated()]
        return [IsAdmin()]

    def get_serializer_class(self):
        """Return the appropriate serializer per action."""
        if self.action == "me":
            return UsuarioMeSerializer
        if self.action == "partial_update":
            return UsuarioUpdateSerializer
        return UsuarioResponseSerializer

    def get_object(self):
        """Allow retrieve of any user including pk=1."""
        if self.action == "retrieve":
            return Usuario.objects.get(pk=self.kwargs["pk"])
        return super().get_object()

    def partial_update(self, request, *args, **kwargs):
        """Update user role only. Protected user pk=1 cannot be modified."""
        _validate_editable(kwargs["pk"])
        usuario = Usuario.objects.get(pk=kwargs["pk"])
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        usuario.role = serializer.validated_data["role"]
        usuario.save(update_fields=["role"])
        return Response(UsuarioResponseSerializer(usuario).data)

    @action(detail=False, methods=["get"])
    def me(self, request):
        """Return current authenticated user info."""
        serializer = UsuarioMeSerializer(request.user)
        return Response(serializer.data)

    @action(detail=True, methods=["patch"], url_path="toggle-ativo")
    def toggle_ativo(self, request, pk=None):
        """Toggle user active status. Protected user pk=1 cannot be toggled."""
        _validate_editable(pk)
        usuario = Usuario.objects.get(pk=pk)
        usuario.ativo = not usuario.ativo
        usuario.save(update_fields=["ativo"])
        return Response(UsuarioResponseSerializer(usuario).data, status=status.HTTP_200_OK)

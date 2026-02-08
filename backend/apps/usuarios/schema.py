"""OpenAPI schema definitions for Usuario endpoints."""

from drf_spectacular.utils import extend_schema, extend_schema_view

from apps.usuarios.serializers import UsuarioMeSerializer, UsuarioResponseSerializer

usuario_schema = extend_schema_view(
    me=extend_schema(
        summary="Dados do usuario autenticado",
        description="Retorna informacoes do usuario atualmente autenticado.",
        responses={200: UsuarioMeSerializer},
    ),
    toggle_ativo=extend_schema(
        summary="Alternar status ativo do usuario",
        description="Inverte o status ativo/inativo de um usuario. Usuario pk=1 e protegido.",
        request=None,
        responses={200: UsuarioResponseSerializer},
    ),
)

"""OpenAPI schema definitions for Comentario endpoints."""

from drf_spectacular.utils import extend_schema, extend_schema_view

from apps.comentarios.serializers import ComentarioResponseSerializer

comentario_schema = extend_schema_view(
    by_demanda=extend_schema(
        summary="Comentarios por demanda",
        description="Retorna todos os comentarios de uma demanda.",
        responses={200: ComentarioResponseSerializer(many=True)},
    ),
)

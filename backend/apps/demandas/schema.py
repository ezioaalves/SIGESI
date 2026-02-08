"""OpenAPI schema definitions for Demanda endpoints."""

from drf_spectacular.utils import OpenApiParameter, extend_schema, extend_schema_view

from apps.demandas.serializers import DemandaResponseSerializer

demanda_schema = extend_schema_view(
    by_solicitacao=extend_schema(
        summary="Demandas por solicitacao",
        description="Retorna demandas vinculadas a uma solicitacao, ordenadas por prazo.",
        responses={200: DemandaResponseSerializer(many=True)},
    ),
    by_responsavel=extend_schema(
        summary="Demandas por responsavel",
        description="Retorna demandas atribuidas a um responsavel, ordenadas por prazo.",
        parameters=[
            OpenApiParameter(
                name="responsavel_id",
                type=int,
                location=OpenApiParameter.QUERY,
                description="ID do usuario responsavel",
                required=True,
            ),
        ],
        responses={200: DemandaResponseSerializer(many=True)},
    ),
)

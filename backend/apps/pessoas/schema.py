"""OpenAPI schema definitions for Pessoa endpoints."""

from drf_spectacular.utils import OpenApiParameter, extend_schema, extend_schema_view

from apps.pessoas.serializers import PessoaResponseSerializer

pessoa_schema = extend_schema_view(
    by_cpf=extend_schema(
        summary="Buscar pessoa por CPF",
        description="Retorna uma pessoa pelo numero do CPF.",
        parameters=[
            OpenApiParameter(
                name="cpf",
                type=str,
                location=OpenApiParameter.QUERY,
                description="Numero do CPF",
                required=True,
            ),
        ],
        responses={200: PessoaResponseSerializer},
    ),
)

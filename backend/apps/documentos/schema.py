"""OpenAPI schema definitions for Documento endpoints."""

from drf_spectacular.utils import OpenApiTypes, extend_schema, extend_schema_view

documento_schema = extend_schema_view(
    pdf=extend_schema(
        summary="Download do documento em PDF",
        description="Gera e faz download do documento oficial em formato PDF.",
        responses={(200, "application/pdf"): OpenApiTypes.BINARY},
    ),
)

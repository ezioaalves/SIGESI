"""OpenAPI schema definitions for Arquivo endpoints."""

from drf_spectacular.utils import OpenApiTypes, extend_schema, extend_schema_view

from apps.arquivos.serializers import ArquivoResponseSerializer, FileUrlResponseSerializer

arquivo_schema = extend_schema_view(
    upload=extend_schema(
        summary="Upload de arquivo",
        description="Faz upload de um arquivo para o MinIO. Maximo 10MB por arquivo.",
        request={
            "multipart/form-data": {
                "type": "object",
                "properties": {
                    "file": {"type": "string", "format": "binary"},
                    "categoria": {"type": "string", "description": "Categoria do arquivo"},
                },
                "required": ["file"],
            },
        },
        responses={201: ArquivoResponseSerializer},
    ),
    presigned_url=extend_schema(
        summary="URL de download temporaria",
        description="Gera URL pre-assinada para download direto do MinIO (60 min).",
        responses={200: FileUrlResponseSerializer},
    ),
    download=extend_schema(
        summary="Download de arquivo",
        description="Download do arquivo via proxy do servidor.",
        responses={(200, "application/octet-stream"): OpenApiTypes.BINARY},
    ),
)

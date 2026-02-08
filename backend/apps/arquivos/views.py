"""Arquivo ViewSet with upload, download, presigned URL, and delete."""

import logging

from django.http import HttpResponse
from drf_spectacular.utils import OpenApiTypes, extend_schema
from rest_framework import status
from rest_framework.decorators import action
from rest_framework.parsers import FormParser, MultiPartParser
from rest_framework.response import Response
from rest_framework.viewsets import ModelViewSet

from apps.arquivos.models import Arquivo
from apps.arquivos.serializers import (
    ArquivoResponseSerializer,
    FileUrlResponseSerializer,
)
from apps.arquivos.services import (
    delete_file,
    download_file,
    generate_storage_key,
    get_presigned_url,
    upload_file,
)
from apps.arquivos.validators import validate_file
from apps.core.permissions import IsAllRoles

logger = logging.getLogger(__name__)


class ArquivoViewSet(ModelViewSet):
    """ViewSet for Arquivo CRUD with file storage operations."""

    queryset = Arquivo.objects.filter(ativo=True).order_by("-uploaded_at")
    permission_classes = [IsAllRoles]
    serializer_class = ArquivoResponseSerializer
    http_method_names = ["get", "post", "delete", "head", "options"]

    def get_queryset(self):
        """Filter active files for list; allow any for retrieve."""
        if self.action == "list":
            return Arquivo.objects.filter(ativo=True).order_by("-uploaded_at")
        return Arquivo.objects.all().order_by("-uploaded_at")

    @extend_schema(
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
    )
    @action(
        detail=False,
        methods=["post"],
        url_path="upload",
        url_name="upload",
        parser_classes=[MultiPartParser, FormParser],
    )
    def upload(self, request):
        """Upload a file to MinIO and save metadata."""
        uploaded_file = request.FILES.get("file")
        categoria = request.data.get("categoria", "")

        validate_file(uploaded_file)

        storage_key = generate_storage_key(uploaded_file.name, categoria or None)

        upload_file(uploaded_file, storage_key)

        arquivo = Arquivo.objects.create(
            nome_original=uploaded_file.name,
            storage_key=storage_key,
            content_type=uploaded_file.content_type,
            tamanho=uploaded_file.size,
            categoria=categoria,
        )

        serializer = ArquivoResponseSerializer(arquivo)
        return Response(serializer.data, status=status.HTTP_201_CREATED)

    @extend_schema(
        summary="URL de download temporaria",
        description="Gera URL pre-assinada para download direto do MinIO (60 min).",
        responses={200: FileUrlResponseSerializer},
    )
    @action(detail=True, methods=["get"], url_path="url", url_name="presigned-url")
    def presigned_url(self, request, pk=None):
        """Generate a presigned download URL (60 min expiry)."""
        arquivo = self.get_object()
        expires_minutes = 60
        url = get_presigned_url(arquivo.storage_key, expires_minutes)

        data = {
            "file_id": arquivo.id,
            "url": url,
            "expires_in_seconds": expires_minutes * 60,
            "nome_original": arquivo.nome_original,
        }
        serializer = FileUrlResponseSerializer(data)
        return Response(serializer.data)

    @extend_schema(
        summary="Download de arquivo",
        description="Download do arquivo via proxy do servidor.",
        responses={(200, "application/octet-stream"): OpenApiTypes.BINARY},
    )
    @action(detail=True, methods=["get"], url_path="download", url_name="download")
    def download(self, request, pk=None):
        """Proxy download a file from MinIO."""
        arquivo = self.get_object()
        file_bytes = download_file(arquivo.storage_key)

        response = HttpResponse(file_bytes, content_type=arquivo.content_type)
        response["Content-Disposition"] = f'attachment; filename="{arquivo.nome_original}"'
        return response

    def destroy(self, request, *args, **kwargs):
        """Delete file from MinIO and remove DB record."""
        arquivo = self.get_object()
        delete_file(arquivo.storage_key)
        arquivo.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)

"""Documento ViewSet with CRUD and PDF generation."""

from django.http import HttpResponse
from drf_spectacular.utils import OpenApiTypes, extend_schema
from rest_framework import status
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.viewsets import ModelViewSet

from apps.core.permissions import IsOperadorOrAdmin
from apps.documentos.models import Documento
from apps.documentos.serializers import (
    DocumentoCreateSerializer,
    DocumentoResponseSerializer,
    DocumentoUpdateSerializer,
)
from apps.documentos.services import generate_documento_pdf


class DocumentoViewSet(ModelViewSet):
    """ViewSet for Documento CRUD with PDF download action."""

    queryset = Documento.objects.all().order_by("id")
    permission_classes = [IsOperadorOrAdmin]

    def get_serializer_class(self):
        """Return the appropriate serializer per action."""
        if self.action == "create":
            return DocumentoCreateSerializer
        if self.action == "partial_update":
            return DocumentoUpdateSerializer
        return DocumentoResponseSerializer

    def create(self, request, *args, **kwargs):
        """Create a new Documento."""
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        documento = serializer.save()
        response_serializer = DocumentoResponseSerializer(documento)
        return Response(response_serializer.data, status=status.HTTP_201_CREATED)

    def partial_update(self, request, *args, **kwargs):
        """Update Documento fields and optionally replace anexos."""
        documento = self.get_object()
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        data = serializer.validated_data

        for field in (
            "numero",
            "subject",
            "honorifico",
            "body",
            "tipo",
            "portaria",
            "assinante",
            "interessado",
            "destino",
        ):
            if field in data:
                setattr(documento, field, data[field])

        if "anexo_ids" in data:
            documento.anexos.set(data["anexo_ids"])

        documento.save()
        response_serializer = DocumentoResponseSerializer(documento)
        return Response(response_serializer.data)

    @extend_schema(
        summary="Download do documento em PDF",
        description="Gera e faz download do documento oficial em formato PDF.",
        responses={(200, "application/pdf"): OpenApiTypes.BINARY},
    )
    @action(detail=True, methods=["get"], url_path="pdf", url_name="pdf")
    def pdf(self, request, pk=None):
        """Generate and download PDF for a Documento."""
        documento = self.get_object()
        pdf_bytes = generate_documento_pdf(documento)

        response = HttpResponse(pdf_bytes, content_type="application/pdf")
        filename = f"documento_{documento.numero or documento.id}.pdf"
        response["Content-Disposition"] = f'attachment; filename="{filename}"'
        return response

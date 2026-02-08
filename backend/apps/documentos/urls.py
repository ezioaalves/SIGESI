"""Documento URL routing."""

from rest_framework.routers import DefaultRouter

from apps.documentos.views import DocumentoViewSet

router = DefaultRouter()
router.register("", DocumentoViewSet, basename="documento")

urlpatterns = router.urls

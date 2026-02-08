"""Arquivo URL routing."""

from rest_framework.routers import DefaultRouter

from apps.arquivos.views import ArquivoViewSet

router = DefaultRouter()
router.register("", ArquivoViewSet, basename="arquivo")

urlpatterns = router.urls

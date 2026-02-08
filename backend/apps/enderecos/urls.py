"""Endereco URL routing."""

from rest_framework.routers import DefaultRouter

from apps.enderecos.views import EnderecoViewSet

router = DefaultRouter()
router.register("", EnderecoViewSet, basename="endereco")

urlpatterns = router.urls

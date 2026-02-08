"""Pessoa URL routing."""

from rest_framework.routers import DefaultRouter

from apps.pessoas.views import PessoaViewSet

router = DefaultRouter()
router.register("", PessoaViewSet, basename="pessoa")

urlpatterns = router.urls

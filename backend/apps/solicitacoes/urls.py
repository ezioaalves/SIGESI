"""Solicitacao URL routing."""

from rest_framework.routers import DefaultRouter

from apps.solicitacoes.views import SolicitacaoViewSet

router = DefaultRouter()
router.register("", SolicitacaoViewSet, basename="solicitacao")

urlpatterns = router.urls

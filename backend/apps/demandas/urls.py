"""Demanda URL routing."""

from rest_framework.routers import DefaultRouter

from apps.demandas.views import DemandaViewSet

router = DefaultRouter()
router.register("", DemandaViewSet, basename="demanda")

urlpatterns = router.urls

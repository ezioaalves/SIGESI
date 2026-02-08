"""Gaveta URL routing."""

from rest_framework.routers import DefaultRouter

from apps.gavetas.views import GavetaViewSet

router = DefaultRouter()
router.register("", GavetaViewSet, basename="gaveta")

urlpatterns = router.urls

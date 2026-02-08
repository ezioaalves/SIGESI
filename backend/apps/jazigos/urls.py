"""Jazigo URL routing."""

from rest_framework.routers import DefaultRouter

from apps.jazigos.views import JazigoViewSet

router = DefaultRouter()
router.register("", JazigoViewSet, basename="jazigo")

urlpatterns = router.urls
